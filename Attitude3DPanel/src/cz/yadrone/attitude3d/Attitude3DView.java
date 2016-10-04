 /*
 */
package cz.yadrone.attitude3d;

import com.interactivemesh.jfx.importer.col.ColModelImporter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.openide.modules.InstalledFileLocator;

/**
 * Manages the content of the 3D scene.
 * The method {@link #setEulerAngleRotation(double, double, double)} has to be
 * called to set the rotation of the AR.Drone model.
 * @author Tomas Prochazka 22.5.2016
 */
public class Attitude3DView {

    private static final String MESH_NAME = "ardrone_centered.dae";
    private Group root;
    private Scene scene;
    private Group droneModel;

    public Scene createScene() {
        root = createSceneContent();
        scene = new Scene(root,0,0,true);
        scene.setFill(Color.SKYBLUE);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        scene.setCamera(camera);
        camera.setFarClip(200);
        camera.setFieldOfView(60);
        camera.setTranslateZ(-2);   
        return scene;
    }

    private static Node[] loadDroneMesh() {
        //See: http://wiki.netbeans.org/DevFaqInstalledFileLocator
        File file = InstalledFileLocator.getDefault().locate(
                MESH_NAME,
                "cz.yadrone.attitude3d",
                false);
        if (file == null) {
            Logger.getLogger(Attitude3DView.class.getName()).log(Level.SEVERE, "Could not locate the drone 3D model " + MESH_NAME);
            return null;
        }
        ColModelImporter importer = new ColModelImporter();
        importer.read(file);
        return importer.getImport();
    }
    /**
     *  The rotations will be applied in the following order:
     * roll -> pitch -> yaw
     * yaw -> roll -> pitch
     * Unit: degrees.
     * Has to be called from the JavaFX thread.
     * Use {@link Platform#runLater(java.lang.Runnable) }
     * @param pitch THETA
     * @param roll  PHI
     * @param yaw PSI
     */
    public void setEulerAngleRotation(double pitch, double roll, double yaw) {
        ObservableList<Transform> transforms = droneModel.getTransforms();
        transforms.clear();
        //PHI - THETA - PSI ... roll - pitch - yaw
        Point3D rollAxis = new Point3D(0, 0, 1);  // x axis for the drone, z axis in JavaFX: away from the viewer.
        Point3D pitchAxis = new Point3D(1,0,0);     //y axis for the drone, x axis in JavaFX: to the right
        Point3D yawAxis = new Point3D(0,1,0);       //z axis for the drone, y axis in Java FX: pointing down
     
        // It appears that JavaFX automatically transforms the axes of rotation!
          Rotate yawRot = new Rotate(yaw, yawAxis);
          Point3D pitchAxis1 = yawRot.transform(pitchAxis);
          //Point3D rollAxis1 = yawRot.transform(rollAxis);
          
          Rotate rollRot = new Rotate(roll, rollAxis);
         // Point3D pitchAxis2 = rollRot.transform(pitchAxis1);
          
          Rotate pitchRot = new Rotate(pitch,pitchAxis); 
          
          transforms.add(yawRot);
          transforms.add(pitchRot);
          transforms.add(rollRot);        
    }
        
    private Group createSceneContent() {
        Node[] droneMesh = loadDroneMesh();
        droneModel = new Group(droneMesh);
        droneModel.setScaleX(.25);
        droneModel.setScaleZ(.25);
        droneModel.setScaleY(.25);
        droneModel.setTranslateZ(40);
       // droneModel.setRotationAxis(new Point3D(0,1,0));
        //droneModel.setRotate(180);
//        Timeline anim = new Timeline(new KeyFrame(Duration.millis(200), (ActionEvent event) -> {
//             droneModel.setRotate(droneModel.getRotate()+2);
//        }));
//        anim.setCycleCount(Timeline.INDEFINITE);
//        anim.play();
        Group sceneRoot = new Group(droneModel);
        ObservableList<Node> children = sceneRoot.getChildren();
        AmbientLight ambientLight = new AmbientLight();
        //ambientLight.
       children.add(ambientLight);
        return sceneRoot;
    }
    /**
     * Has to be called from the JavaFX thread.
     * use {@link Platform#runLater(java.lang.Runnable) }
     */
    void hide() {
        droneModel.setVisible(false);
    }
    /**
     * Has to be called from the JavaFX thread.
     * Use {@link Platform#runLater(java.lang.Runnable) }
     */
    void show() {
        droneModel.setVisible(true);
    }
}
