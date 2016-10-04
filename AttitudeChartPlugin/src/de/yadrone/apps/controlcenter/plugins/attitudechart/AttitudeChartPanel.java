package de.yadrone.apps.controlcenter.plugins.attitudechart;

import de.yadrone.ARDroneProvider;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "AttitudeChartPanel",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "plots_mode", openAtStartup = true)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.attitudechart.AttitudeChartPanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AttitudeTopCompName",
        preferredID = "AttitudeChartPanel"
)
@NbBundle.Messages({
    "CTL_AttitudeTopCompName=Attitude Chart Panel",
    "CTL_AttitudeTopCompTopComponent=AttitudeTopComp Window",
    "HINT_AttitudeTopCompTopComponent=This is the Attitude Chart Panel"
})
public class AttitudeChartPanel extends TopComponent implements ICCPlugin {

    private IARDrone drone;

    private AttitudeChart chart;

    public AttitudeChartPanel() {
        this.setLayout(new GridBagLayout());
        this.setDisplayName(getTitle());
        this.setToolTipText(getDescription());
        this.chart = new AttitudeChart();
        JPanel chartPanel = new ChartPanel(chart.getChart(), true, true, true, true, true);

        add(chartPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    }

    private AttitudeListener attitudeListener = new AttitudeListener() {
        public void windCompensation(float pitch, float roll) {

        }

        public void attitudeUpdated(float pitch, float roll) {

        }

        public void attitudeUpdated(float pitch, float roll, float yaw) {
            chart.setAttitude(pitch / 1000, roll / 1000, yaw / 1000);
        }
    };

    public void activate(IARDrone drone) {
        this.drone = drone;

        drone.getNavDataManager().addAttitudeListener(attitudeListener);
    }

    public void deactivate() {
        drone.getNavDataManager().removeAttitudeListener(attitudeListener);
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

    public String getTitle() {
        return "Attitude Chart";
    }

    public String getDescription() {
        return "Displays a chart with the latest pitch, roll and yaw";
    }

    public boolean isVisual() {
        return true;
    }

    public Dimension getScreenSize() {
        return new Dimension(330, 250);
    }

    public Point getScreenLocation() {
        return new Point(330, 390);
    }

}
