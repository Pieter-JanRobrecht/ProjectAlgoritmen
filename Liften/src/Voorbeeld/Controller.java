package Voorbeeld;

import Model.User;
import javafx.fxml.FXML;

import Model.ManagementSystem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private AnchorPane anchorPane;

    private Application application;

    private final Group root = new Group();
    private final Xform axisGroup = new Xform();
    private final Xform moleculeGroup = new Xform();
    private final Xform world = new Xform();
    private final Xform xForm = new Xform();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -1300;
    private static final double CAMERA_INITIAL_X_ANGLE = 30.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    private ManagementSystem ms = null;

    private static final int ANIMATIE_DUUR = 2000;
    private static final int AFSTAND_TUSSEN_LIFTEN = 70;
    private static final int LENGTE_GANG = 200;
    private int AANTAL_VERDIEPINGEN;
    private final int VEILIGHEIDSAFSTAND = 10;
    private int AANTAL_LIFTEN;
    private static final int LENGTE_X = 100;
    private static final int LENGTE_Y = 140;
    private static final int LENGTE_Z = 100;
    private static final int DIKTE_VERDIEP = 5;
    private static final int DIKTE_USER = 30;
    private static final int START_USER = 80;

    //    private List<Box> liften = new ArrayList<>();
//    private List<Sphere> users = new ArrayList<>();
    public SequentialTransition sequence = new SequentialTransition();

    private static final double MAX_SCALE = 2.5d;
    private static final double MIN_SCALE = .5d;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    public void buildCamera() {
        System.out.println("buildCamera()");
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    public void buildAxes() {
        System.out.println("buildAxes()");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(false);
        world.getChildren().addAll(axisGroup);
    }

    private void handleMouse(SubScene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
                } else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
                    camera.setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED * 10);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED * 10);
                }
            }
        });
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            private Node nodeToZoom = root;

            @Override
            public void handle(ScrollEvent scrollEvent) {
//                if (scrollEvent.isControlDown()) {
                final double scale = calculateScale(scrollEvent);
                nodeToZoom.setScaleX(scale);
                nodeToZoom.setScaleY(scale);
                nodeToZoom.setScaleZ(scale);
                scrollEvent.consume();
//                }
            }

            private double calculateScale(ScrollEvent scrollEvent) {
                double scale = nodeToZoom.getScaleX() + scrollEvent.getDeltaY() / 100;

                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }
                return scale;
            }
        });
    }

    private void handleKeyboard(SubScene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z:
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                    case X:
                        axisGroup.setVisible(!axisGroup.isVisible());
                        break;
                    case V:
                        moleculeGroup.setVisible(!moleculeGroup.isVisible());
                        break;
                }
            }
        });
    }

    public void buildElevatorHall() {
        Box lift;
        for (int i = 0; i < AANTAL_LIFTEN; i++) {
            lift = new Box(LENGTE_X, LENGTE_Y, LENGTE_Z);

            setBoxPlace(i, lift);

            lift.setTranslateY(LENGTE_Y / 2);
            xForm.getChildren().add(lift);
            ms.getLifts().get(i).setBox(lift);
//            liften.add(lift);
        }

        Box verdieping;
        for (int i = 0; i < AANTAL_LIFTEN; i++) {
            for (int j = 1; j < AANTAL_VERDIEPINGEN; j++) {
                verdieping = new Box(LENGTE_X, DIKTE_VERDIEP, LENGTE_Z);
                setBoxPlace(i, verdieping);
                verdieping.setTranslateY(j * (LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2));
                xForm.getChildren().add(verdieping);
            }
        }
        moleculeGroup.getChildren().add(xForm);

        world.getChildren().addAll(moleculeGroup);
    }

    private void setBoxPlace(int i, Box test) {
        if (i % 2 == 0) {
            test.setTranslateX(LENGTE_X / 2);
            test.setTranslateZ(i / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z) + LENGTE_Z / 2);
        } else {
            test.setTranslateX(-(LENGTE_GANG + LENGTE_X / 2));
            test.setTranslateZ((i - 1) / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z) + LENGTE_Z / 2);
        }
    }

    @FXML
    void startSimulatie(ActionEvent event) {
        Simulation sim = new Simulation(ms);
        sim.setGUIController(this);
        try {
            sim.startSimulationSimple();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("actie");
//
//        Sphere sphere = makeUserOnLevel(0,1);
//        sequence.getChildren().addAll(moveUserToElevator(sphere, 3));
//        sequence.getChildren().addAll(moveElevator(liften.get(3), 1));
//        //Na een move elevator moet gans dat stuk al gespeeld worden
//
//        sequence.getChildren().addAll(userEnterElevator(users.get(0), liften.get(3)));
//        sequence.getChildren().addAll(moveElevator(liften.get(3), -1));
//
//        moveUserWithAmountOf(users.get(0), -1);
//        sequence.getChildren().addAll(userExitElevator(users.get(0), 3));
//
//        sequence.play();
    }

    public TranslateTransition moveElevator(Box lift, int aantalVerdiepingen) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), lift);

        double afstandEenVerdiep = LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2;
        double afstand = afstandEenVerdiep * aantalVerdiepingen;

        tt.setByY(afstand);

        return tt;
    }

    public Sphere makeUserOnLevel(User idUser, int niveau) {
        Sphere user = new Sphere(DIKTE_USER);

        user.setTranslateX(-LENGTE_GANG / 2);
        user.setTranslateY(DIKTE_USER + niveau * (LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2));
        user.setTranslateZ(-START_USER);

        idUser.setSphere(user);
        xForm.getChildren().add(user);
        return user;
    }

    public List<TranslateTransition> moveUserToElevator(Sphere user, int elevatorId) {
        System.out.println("Move user " + user + " naar lift " + elevatorId);
        double afstandAfTeLeggenX;
        double afstandAfTeLeggenZ;

        if (elevatorId % 2 == 0) {
            afstandAfTeLeggenX = LENGTE_GANG / 2 - DIKTE_USER;
            afstandAfTeLeggenZ = START_USER + LENGTE_Z / 2 + elevatorId / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z);
        } else {
            afstandAfTeLeggenX = -LENGTE_GANG / 2 + DIKTE_USER;
            afstandAfTeLeggenZ = START_USER + LENGTE_Z / 2 + (elevatorId - 1) / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z);
        }
        TranslateTransition tx = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user);
        tx.setByX(afstandAfTeLeggenX);

        TranslateTransition tz = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user);
        tz.setByZ(afstandAfTeLeggenZ);

        List<TranslateTransition> transitions = new ArrayList<>();
        transitions.add(tz);
        transitions.add(tx);
        return transitions;
    }

    public List<TranslateTransition> userEnterElevator(Sphere user, Box lift) {
        System.out.println("User " + user + " enter lift " + lift);
        List<TranslateTransition> list = new ArrayList<>();
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user);
        if (lift.getTranslateZ() == user.getTranslateZ() &&
                lift.getTranslateY() - LENGTE_Y / 2 == user.getTranslateY() - DIKTE_USER &&
                (lift.getTranslateX() + LENGTE_X / 2 == user.getTranslateX() - DIKTE_USER ||
                        lift.getTranslateX() - LENGTE_X / 2 == user.getTranslateX() + DIKTE_USER)) {
            if (lift.getTranslateX() - LENGTE_X / 2 == user.getTranslateX() + DIKTE_USER) {
                tt.setToX(lift.getTranslateX());
            } else {
                tt.setByX(-LENGTE_X / 2 - DIKTE_USER);
            }
            tt.setOnFinished(event -> user.setVisible(false));
        } else {
            System.out.println("User " + user.getId() + " kan niet in lift " + lift.getId());
        }

        list.add(tt);
        return list;
    }

    public void moveUserWithAmountOf(Sphere user, int aantalVerdiepingen) {
        int afstandVerdiep = LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2;
        user.setTranslateY(user.getTranslateY() + afstandVerdiep * aantalVerdiepingen);
    }

    public List<TranslateTransition> userExitElevator(Sphere user, int elevatorId) {
        System.out.println("User " + user + " verlaat lift " + elevatorId);
        user.setVisible(true);

        List<TranslateTransition> list = new ArrayList<>();
        TranslateTransition tx = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user);
        TranslateTransition tz = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user);

        int afstandX;
        int afstandZ;
        if (elevatorId % 2 == 0) {
            afstandX = -(LENGTE_X / 2 + LENGTE_GANG / 2);
            afstandZ = -(START_USER + LENGTE_Z / 2 + elevatorId / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z));
        } else {
            afstandX = (LENGTE_X / 2 + LENGTE_GANG / 2);
            afstandZ = -(START_USER + LENGTE_Z / 2 + (elevatorId - 1) / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z));
        }
        tx.setByX(afstandX);
        tz.setByZ(afstandZ);

        list.add(tx);
        list.add(tz);
        return list;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void makeWorld() {
        File file = null;
        try {
            file = new File(Controller.class.getClassLoader().getResource("original.json").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ms = objectMapper.readValue(file, ManagementSystem.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initValues();

        System.out.println("start()");

        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        buildCamera();
        buildAxes();
        buildElevatorHall();

        SubScene test = new SubScene(root, 670, 630, true, SceneAntialiasing.BALANCED);
        test.setDepthTest(DepthTest.ENABLE);

        test.setFill(Color.GREY);
        handleKeyboard(test, world);
        handleMouse(test, world);

        camera.setNearClip(0.01);
        test.setCamera(camera);
        anchorPane.getChildren().add(test);
    }

    private void initValues() {
        AANTAL_VERDIEPINGEN = ms.getLevels().size();
        AANTAL_LIFTEN = ms.getLifts().size();
    }

    public ManagementSystem getMs() {
        return ms;
    }
}
