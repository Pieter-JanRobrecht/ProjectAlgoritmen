package Voorbeeld;

import Model.Lift;
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
import javafx.stage.Stage;
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

    public static final int ANIMATIE_DUUR = 200;
    private static final int AFSTAND_TUSSEN_LIFTEN = 70;
    private static final int LENGTE_GANG = 200;
    private int AANTAL_VERDIEPINGEN;
    public static final int VEILIGHEIDSAFSTAND = 10;
    private int AANTAL_LIFTEN;
    private static final int LENGTE_X = 100;
    public static final int LENGTE_Y = 140;
    private static final int LENGTE_Z = 100;
    public static final int DIKTE_VERDIEP = 5;
    private static final int DIKTE_USER = 30;
    private static final int START_USER = 80;

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

            lift.setTranslateY(LENGTE_Y / 2 + ms.getLifts().get(i).getStartLevel() * (LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2));
            xForm.getChildren().add(lift);
            ms.getLifts().get(i).setBox(lift);
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
    }

    @FXML
    void resetWorld(ActionEvent event) {
        application.reset((Stage) anchorPane.getScene().getWindow());
    }

    @FXML
    void pauseAnimation(ActionEvent event) {
        sequence.pause();
    }

    @FXML
    void resume(ActionEvent event) {
        sequence.play();
    }

    public TranslateTransition moveElevator(Lift lift, int aantalVerdiepingen) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), lift.getBox());

        double afstandEenVerdiep = LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2;
        double afstand = afstandEenVerdiep * aantalVerdiepingen;

        tt.setByY(afstand);

        tt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("\t\t GUI - Moving elevator " + lift.getId() + " in direction " + aantalVerdiepingen);
            }
        });
        return tt;
    }

    public void makeUserOnLevel(User idUser, int niveau) {
        System.out.println("\t\t GUI - Creating user sphere for " + idUser + " on level " + niveau);
        Sphere user = new Sphere(DIKTE_USER);

        user.setVisible(false);

        user.setTranslateX(-LENGTE_GANG / 2);
        user.setTranslateY(DIKTE_USER + niveau * (LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2));
        user.setTranslateZ(-START_USER);

        idUser.setSphere(user);
        xForm.getChildren().add(user);
    }

    public SequentialTransition moveUserToElevator(User user, Lift elevatorId) {
        SequentialTransition sq = new SequentialTransition();
        user.getSphere().setVisible(true);
        double afstandAfTeLeggenX;
        double afstandAfTeLeggenZ;

        TranslateTransition part1 = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user.getSphere());
        afstandAfTeLeggenX = -LENGTE_GANG / 2;
        part1.setToX(afstandAfTeLeggenX);
        part1.setFromY(user.getSphere().getTranslateY());

        if (elevatorId.getId() % 2 == 0) {
            afstandAfTeLeggenZ = LENGTE_Z / 2 + elevatorId.getId() / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z);
        } else {
            afstandAfTeLeggenZ = LENGTE_Z / 2 + (elevatorId.getId() - 1) / 2 * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z);
        }

        TranslateTransition tz = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user.getSphere());
        tz.setFromY(user.getSphere().getTranslateY());
        tz.setToZ(afstandAfTeLeggenZ);

        if (elevatorId.getId() % 2 == 0) {
            afstandAfTeLeggenX = -DIKTE_USER;
        } else {
            afstandAfTeLeggenX = -LENGTE_GANG + DIKTE_USER;
        }
        TranslateTransition part2 = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user.getSphere());
        part2.setFromY(user.getSphere().getTranslateY());
        part2.setToX(afstandAfTeLeggenX);

        sq.getChildren().addAll(part1, tz, part2);

        sq.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("\t\t GUI - Moving User " + user.getId() + " to elevator " + elevatorId.getId());
            }
        });
        return sq;
    }

    public ParallelTransition userEnterElevator(User user, Lift lift) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user.getSphere());

        ColorTransition c = new ColorTransition(lift.getBox(), Color.GREEN);
        if (lift.getCapacity() == lift.getCurrentUsers()) {
            System.out.println("\t\t\t COLOR - Color is now RED for elevator " + lift.getId());
            c = new ColorTransition(lift.getBox(), Color.RED);
        }
        if (lift.getCurrentUsers() >= lift.getCapacity() / 2 && lift.getCurrentUsers() < lift.getCapacity()) {
            System.out.println("\t\t\t COLOR - Color is now ORANGE for elevator " + lift.getId());
            c = new ColorTransition(lift.getBox(), Color.ORANGE);
        }
        if (lift.getCurrentUsers() >= 0 && lift.getCurrentUsers() < lift.getCapacity() / 2) {
            System.out.println("\t\t\t COLOR - Color is now GREEN for elevator " + lift.getId());
            c = new ColorTransition(lift.getBox(), Color.GREEN);
        }

        if (lift.getId() % 2 == 0) {
            tt.setToX(LENGTE_X / 2);
        } else {
            tt.setByX(-LENGTE_X / 2 - DIKTE_USER);
        }

        tt.setOnFinished(event -> {
            System.out.println("\t\t GUI - User " + user.getId() + " joining elevator " + lift.getId());
            user.getSphere().setVisible(false);
        });

        ParallelTransition p = new ParallelTransition();
        p.getChildren().addAll(c, tt);
        return p;
    }

    public TranslateTransition moveUserToLevel(User user, int aantalVerdiepingen) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), null);
        int afstandVerdiep = LENGTE_Y + VEILIGHEIDSAFSTAND + DIKTE_VERDIEP / 2;
        user.getSphere().setTranslateY(afstandVerdiep * aantalVerdiepingen + DIKTE_USER);
        tt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("\t\t GUI - Moving User " + user.getId() + " to level " + aantalVerdiepingen);
                user.getSphere().setVisible(true);
            }
        });
        return tt;
    }

    public SequentialTransition userExitElevator(User user, Lift lift) {
        SequentialTransition sq = new SequentialTransition();

        TranslateTransition tx = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user.getSphere());
        user.getSphere().setVisible(true);

        ColorTransition c = new ColorTransition(lift.getBox(), Color.GREEN);
        if (lift.getCapacity() == lift.getCurrentUsers()) {
            System.out.println("\t\t\t COLOR - Color is now RED for elevator " + lift.getId());
            c = new ColorTransition(lift.getBox(), Color.RED);
        }
        if (lift.getCurrentUsers() >= lift.getCapacity() / 2 && lift.getCurrentUsers() < lift.getCapacity()) {
            System.out.println("\t\t\t COLOR - Color is now ORANGE for elevator " + lift.getId());
            c = new ColorTransition(lift.getBox(), Color.ORANGE);
        }
        if (lift.getCurrentUsers() >= 0 && lift.getCurrentUsers() < lift.getCapacity() / 2) {
            System.out.println("\t\t\t COLOR - Color is now GREEN for elevator " + lift.getId());
            c = new ColorTransition(lift.getBox(), Color.GREEN);
        }

        int afstandX;
        if (lift.getId() % 2 == 0) {
            afstandX = -(LENGTE_X / 2 + DIKTE_USER);
        } else {
            afstandX = (LENGTE_X / 2 + DIKTE_USER);
        }
        tx.setByX(afstandX);
        tx.setFromY(user.getSphere().getTranslateY());

        sq.getChildren().addAll(c, tx);

        tx.setOnFinished(event -> {
            System.out.println("\t\t GUI - User " + user.getId() + " left elevator " + lift.getId());
        });
        return sq;
    }

    public SequentialTransition userLeaveHall(User user) {
        SequentialTransition sq = new SequentialTransition();

        TranslateTransition tx = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user.getSphere());
        TranslateTransition tz = new TranslateTransition(Duration.millis(ANIMATIE_DUUR * 2), user.getSphere());

        tx.setFromY(user.getSphere().getTranslateY());
        tz.setFromY(user.getSphere().getTranslateY());

        tx.setToX(-LENGTE_GANG / 2);
        tz.setToZ(-START_USER * 2);

        sq.getChildren().addAll(tx,tz);

        tz.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("\t\t GUI - User "+user.getId()+ " is leaving the hall");
                user.getSphere().setVisible(false);
            }
        });

        return sq;
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
            file = new File(Controller.class.getClassLoader().getResource("testLiftHopping.json").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ms = objectMapper.readValue(file, ManagementSystem.class);
            for(User u : ms.getUsers())
                u.initialize();
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
