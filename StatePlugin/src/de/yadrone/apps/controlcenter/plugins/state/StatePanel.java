package de.yadrone.apps.controlcenter.plugins.state;

import de.yadrone.ARDroneProvider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JTextArea;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
@TopComponent.Description(
        preferredID = "StatePanel",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "config_mode", openAtStartup = true)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.state.StatePanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_StatePanelName",
        preferredID = "StatePanel"
)
@NbBundle.Messages({
    "CTL_StatePanelName=StatePanel",
    "CTL_StatePanel=StatePanel",
    "HINT_StatePanel=This is the StatePanel"
})
public class StatePanel extends TopComponent implements ICCPlugin {

    private IARDrone drone;

    private JTextArea text;

    public StatePanel() {
        super();
        this.setLayout(new GridBagLayout());
        setBackground(Color.white);
        this.setDisplayName(getTitle());
        this.setToolTipText(getDescription());

        text = new JTextArea("Waiting for State ...");
        text.setEditable(false);
        text.setFont(new Font("Helvetica", Font.PLAIN, 10));
        add(new JScrollPane(text), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    private StateListener stateListener = new StateListener() {
        public void stateChanged(DroneState data) {
            StringBuffer sb = new StringBuffer();

            sb.append("IsFlying: " + data.isFlying() + "\n");
            sb.append("IsVideoEnabled: " + data.isVideoEnabled() + "\n");
            sb.append("IsVisionEnabled: " + data.isVisionEnabled() + "\n");
            sb.append("controlAlgo: " + data.getControlAlgorithm() + "\n");
            sb.append("AltitudeControlActive: " + data.isAltitudeControlActive() + "\n");
            sb.append("IsUserFeedbackOn: " + data.isUserFeedbackOn() + "\n");
            sb.append("ControlReceived: " + data.isVideoEnabled() + "\n");
            sb.append("IsTrimReceived: " + data.isTrimReceived() + "\n");
            sb.append("IsTrimRunning: " + data.isTrimRunning() + "\n");
            sb.append("IsTrimSucceeded: " + data.isTrimSucceeded() + "\n");
            sb.append("IsNavDataDemoOnly: " + data.isNavDataDemoOnly() + "\n");
            sb.append("IsNavDataBootstrap: " + data.isNavDataBootstrap() + "\n");
            sb.append("IsMotorsDown: " + data.isMotorsDown() + "\n");
            sb.append("IsGyrometersDown: " + data.isGyrometersDown() + "\n");
            sb.append("IsBatteryLow: " + data.isBatteryTooLow() + "\n");
            sb.append("IsBatteryHigh: " + data.isBatteryTooHigh() + "\n");
            sb.append("IsTimerElapsed: " + data.isTimerElapsed() + "\n");
            sb.append("isNotEnoughPower: " + data.isNotEnoughPower() + "\n");
            sb.append("isAngelsOutOufRange: " + data.isAngelsOutOufRange() + "\n");
            sb.append("isTooMuchWind: " + data.isTooMuchWind() + "\n");
            sb.append("isUltrasonicSensorDeaf: " + data.isUltrasonicSensorDeaf() + "\n");
            sb.append("isCutoutSystemDetected: " + data.isCutoutSystemDetected() + "\n");
            sb.append("isPICVersionNumberOK: " + data.isPICVersionNumberOK() + "\n");
            sb.append("isATCodedThreadOn: " + data.isATCodedThreadOn() + "\n");
            sb.append("isNavDataThreadOn: " + data.isNavDataThreadOn() + "\n");
            sb.append("isVideoThreadOn: " + data.isVideoThreadOn() + "\n");
            sb.append("isAcquisitionThreadOn: " + data.isAcquisitionThreadOn() + "\n");
            sb.append("isControlWatchdogDelayed: " + data.isControlWatchdogDelayed() + "\n");
            sb.append("isADCWatchdogDelayed: " + data.isADCWatchdogDelayed() + "\n");
            sb.append("isCommunicationProblemOccurred: " + data.isCommunicationProblemOccurred() + "\n");
            sb.append("IsEmergency: " + data.isEmergency() + "\n");
//			sb.append("CtrlState: " + data.getControlState() + "\n");
//			sb.append("Battery: " + data.getBattery() + "\n");
//			sb.append("Altitude: " + data.getAltitude() + "\n");
//			sb.append("Pitch: " + data.getPitch() + "\n");
//			sb.append("Roll: " + data.getRoll() + "\n");
//			sb.append("Yaw: " + data.getYaw() + "\n");
//			sb.append("X velocity: " + data.getVx() + "\n");
//			sb.append("Y velocity: " + data.getLongitude() + "\n");
//			sb.append("Z velocity: " + data.getVz() + "\n");
//			sb.append("Vision Tags: " + data.getVisionTags() + "\n");

            text.setText(sb.toString());
        }

        public void controlStateChanged(ControlState state) {

        }
    };

    public void activate(IARDrone drone) {
        this.drone = drone;

        drone.getNavDataManager().addStateListener(stateListener);
    }

    public void deactivate() {
        drone.getNavDataManager().removeStateListener(stateListener);
    }

    public String getTitle() {
        return "State (NavData)";
    }

    public String getDescription() {
        return "Displays information about the drone's current state";
    }

    public boolean isVisual() {
        return true;
    }

    public Dimension getScreenSize() {
        return new Dimension(240, 650);
    }

    public Point getScreenLocation() {
        return new Point(650, 0);
    }

    @Override
    protected void componentOpened() {
        ARDrone d = ARDroneProvider.getDrone();
        activate(d);
    }

    @Override
    protected void componentClosed() {
        deactivate();
    }

}
