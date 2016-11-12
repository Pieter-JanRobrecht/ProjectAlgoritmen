package Voorbeeld;


import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.text.Text;
import javafx.util.Duration;

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
    private final Xform moleculeXform = new Xform();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -700;
    private static final double CAMERA_INITIAL_X_ANGLE = 30.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double HYDROGEN_ANGLE = 104.5;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    private static final int ANIMATIE_DUUR = 2000;
    private static final int AANTAL_VERDIEPINGEN = 8;
    private static final int VEILIGHEIDSAFSTAND = 10;
    private static final int AANTAL_LIFTEN = 4;
    private static final int LENGTE_X = 100;
    private static final int LENGTE_Y = 140;
    private static final int LENGTE_Z = 100;

    private List<Box> liften = new ArrayList<Box>();

    private static final int AFSTAND_TUSSEN_LIFTEN = 70;
    private static final int LENGTE_GANG = 100;

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

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);


        Xform oxygenXform = new Xform();
        Xform hydrogen1SideXform = new Xform();
        Xform hydrogen1Xform = new Xform();
        Xform hydrogen2SideXform = new Xform();
        Xform hydrogen2Xform = new Xform();

        Sphere oxygenSphere = new Sphere(40.0);
        oxygenSphere.setMaterial(redMaterial);

        Box test;
        for (int i = 0; i < AANTAL_LIFTEN; i++) {
            test = new Box(LENGTE_X, LENGTE_Y, LENGTE_Z);

            setBoxPlace(i, test);

            test.setTranslateY(LENGTE_Y / 2);
            moleculeXform.getChildren().add(test);
            liften.add(test);
        }

        Box verdieping;
        for (int i = 0; i < AANTAL_LIFTEN; i++) {
            for (int j = 1; j < AANTAL_VERDIEPINGEN; j++) {
                verdieping = new Box(LENGTE_X, 5, LENGTE_Z);
                setBoxPlace(i, verdieping);
                verdieping.setTranslateY(j * (LENGTE_Y + VEILIGHEIDSAFSTAND + 2.5));

                moleculeXform.getChildren().add(verdieping);
            }
        }


        hydrogen1Xform.setTx(100.0);
        hydrogen2Xform.setTx(100.0);
        hydrogen2SideXform.setRotateY(HYDROGEN_ANGLE);

        moleculeGroup.getChildren().add(moleculeXform);

        world.getChildren().addAll(moleculeGroup);
    }

    private void setBoxPlace(int i, Box test) {
        if (i % 2 == 0) {
            test.setTranslateX(LENGTE_X / 2);
            test.setTranslateZ(i * AFSTAND_TUSSEN_LIFTEN + LENGTE_Z / 2);
        } else {
            test.setTranslateX(-(LENGTE_GANG + LENGTE_X));
            test.setTranslateZ((i - 1) * AFSTAND_TUSSEN_LIFTEN + LENGTE_Z / 2);
        }
    }


    @FXML
    void startSimulatie(ActionEvent event) {
        System.out.println("actie");
        Sphere sphere = makeUSerOnLevel(0);
        moveUserToElevator(sphere,0);
        moveElevator(liften.get(0), 1);
    }

    private void moveElevator(Box lift, int aantalVerdiepingen) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), liften.get(0));
        double afstandEenVerdiep = LENGTE_Y + VEILIGHEIDSAFSTAND + 2.5;
        tt.setByY(afstandEenVerdiep * aantalVerdiepingen);
        tt.play();
    }

    private Sphere makeUSerOnLevel(int niveau) {
        Sphere user = new Sphere(30.0);
        user.setMaterial(new PhongMaterial(Color.WHITE));
        user.setTranslateX(-(LENGTE_GANG + LENGTE_X) / 2 + 30);
        user.setTranslateY(30 + niveau * (LENGTE_Y + VEILIGHEIDSAFSTAND + 2.5));
        user.setTranslateZ(-50);
        moleculeXform.getChildren().add(user);
        return user;
    }

    private void moveUserToElevator(Sphere user, int elevatorId) {
        //TODO timeline onderzoeken. Hiermee miss mogelijk om alles in volgorde te doen;
        TranslateTransition tx = new TranslateTransition(Duration.millis(ANIMATIE_DUUR),user);
        TranslateTransition tz = new TranslateTransition(Duration.millis(ANIMATIE_DUUR), user);
        double afstandAfTeLeggenX = 0;
        double afstandAfTeLeggenZ = 0;
        if (elevatorId % 2 == 0) {
            afstandAfTeLeggenX = -(-(LENGTE_GANG + LENGTE_X) / 2 + 30) - LENGTE_X/2;
            afstandAfTeLeggenZ = 50 + LENGTE_Z/2 + elevatorId * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z/2);
        } else {
            afstandAfTeLeggenX = +(-(LENGTE_GANG + LENGTE_X) / 2 + 30) + LENGTE_X/2;
            afstandAfTeLeggenZ = 50 + LENGTE_Z/2 + (elevatorId-1) * (AFSTAND_TUSSEN_LIFTEN + LENGTE_Z/2);
        }
        tz.setByZ(afstandAfTeLeggenZ);
        tz.play();
        tz.setByX(100);
        tz.play();
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void makeWorld() {
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
}
