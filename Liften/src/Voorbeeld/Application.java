package Voorbeeld;

/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cmcastil
 */
public class Application extends javafx.application.Application {

    private final Group root = new Group();
    private final Xform axisGroup = new Xform();
    private final Xform moleculeGroup = new Xform();
    private final Xform world = new Xform();
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

    //   private void buildScene() {
    //       root.getChildren().add(world);
    //   }
    private void buildCamera() {
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

    private void buildAxes() {
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

    private void buildElevatorHall() {
        //======================================================================
        // THIS IS THE IMPORTANT MATERIAL FOR THE TUTORIAL
        //======================================================================

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);

        // Molecule Hierarchy
        // [*] moleculeXform
        //     [*] oxygenXform
        //         [*] oxygenSphere
        //     [*] hydrogen1SideXform
        //         [*] hydrogen1Xform
        //             [*] hydrogen1Sphere
        //         [*] bond1Cylinder
        //     [*] hydrogen2SideXform
        //         [*] hydrogen2Xform
        //             [*] hydrogen2Sphere
        //         [*] bond2Cylinder
        Xform moleculeXform = new Xform();
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

            if (i % 2 == 0) {
                test.setTranslateX(LENGTE_X/2);
                test.setTranslateZ(i * AFSTAND_TUSSEN_LIFTEN + LENGTE_Z/2);
            } else {
                test.setTranslateX(-(LENGTE_GANG + LENGTE_X));
                test.setTranslateZ((i - 1) * AFSTAND_TUSSEN_LIFTEN + LENGTE_Z/2);
            }

            test.setTranslateY(LENGTE_Y/2);
            moleculeXform.getChildren().add(test);
            liften.add(test);
        }

//        Sphere hydrogen1Sphere = new Sphere(30.0);
//        hydrogen1Sphere.setMaterial(whiteMaterial);
//        hydrogen1Sphere.setTranslateX(0.0);
//
//        Sphere hydrogen2Sphere = new Sphere(30.0);
//        hydrogen2Sphere.setMaterial(whiteMaterial);
//        hydrogen2Sphere.setTranslateZ(0.0);
//
//        Cylinder bond1Cylinder = new Cylinder(5, 100);
//        bond1Cylinder.setMaterial(greyMaterial);
//        bond1Cylinder.setTranslateX(50.0);
//        bond1Cylinder.setRotationAxis(Rotate.Z_AXIS);
//        bond1Cylinder.setRotate(90.0);
//
//        Cylinder bond2Cylinder = new Cylinder(5, 100);
//        bond2Cylinder.setMaterial(greyMaterial);
//        bond2Cylinder.setTranslateX(50.0);
//        bond2Cylinder.setRotationAxis(Rotate.Z_AXIS);
//        bond2Cylinder.setRotate(90.0);
//
//        moleculeXform.getChildren().add(oxygenXform);
//        moleculeXform.getChildren().add(hydrogen1SideXform);
//        moleculeXform.getChildren().add(hydrogen2SideXform);
//        oxygenXform.getChildren().add(oxygenSphere);
//        hydrogen1SideXform.getChildren().add(hydrogen1Xform);
//        hydrogen2SideXform.getChildren().add(hydrogen2Xform);
//        hydrogen1Xform.getChildren().add(hydrogen1Sphere);
//        hydrogen2Xform.getChildren().add(hydrogen2Sphere);
//        hydrogen1SideXform.getChildren().add(bond1Cylinder);
//        hydrogen2SideXform.getChildren().add(bond2Cylinder);

        hydrogen1Xform.setTx(100.0);
        hydrogen2Xform.setTx(100.0);
        hydrogen2SideXform.setRotateY(HYDROGEN_ANGLE);

        moleculeGroup.getChildren().add(moleculeXform);

        world.getChildren().addAll(moleculeGroup);
    }

    @Override
    public void start(Stage primaryStage) {

        AnchorPane anchorPane = null;
        Scene scene = null;
        try {
            //Laden van de fxml file waarin alle gui elementen zitten
            FXMLLoader loader = new FXMLLoader();
            InputStream s = null;
            try{
                s = getClass().getClassLoader().getResource("Sample.fxml").openStream();
            }catch(Exception e){

            }
            Parent root = (Parent) loader.load(s);

            //Setten van enkele elementen van het hoofdscherm
            primaryStage.setTitle("Liften");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            //Ophalen van de controller horende bij de view klasse
            Controller viewController = loader.<Controller>getController() ;
            assert(viewController != null);

            //Link tussen controller en view
            anchorPane = viewController.getAnchorPane();
            viewController.setApplication(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

//         setUserAgentStylesheet(STYLESHEET_MODENA);
        System.out.println("start()");

        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        buildAxes();
        buildElevatorHall();

        SubScene test = new SubScene(root, 670, 630, true, SceneAntialiasing.BALANCED);
        test.setDepthTest(DepthTest.ENABLE);

//        Scene extraScene = new Scene(root, 500, 400, true);

        test.setFill(Color.GREY);
        handleKeyboard(test, world);
        handleMouse(test, world);

//        extraScene.setFill(Color.GREY);
//        handleKeyboard(extraScene, world);
//        handleMouse(extraScene, world);

//        primaryStage.setTitle("Molecule Sample Application");
//        primaryStage.setScene(extraScene);
//        primaryStage.show();
//        extraScene.setCamera(camera);

        camera.setNearClip(0.01);
        test.setCamera(camera);
        anchorPane.getChildren().add(test);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public List<Box> getLiften() {
        return liften;
    }
}

