package de.yadrone.apps.controlcenter.plugins.altitude;

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
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "AltitudeTopCompTopComponent",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "plots_mode", openAtStartup = true)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.altitude.AltitudeChartPanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AltitudeTopCompAction",
        preferredID = "AltitudeTopCompTopComponent"
)
@NbBundle.Messages({
    "CTL_AltitudeTopCompAction=Altitude Chart Panel",
    "CTL_AltitudeTopCompTopComponent=AltitudeTopComp Window",
    "HINT_AltitudeTopCompTopComponent=This is the Altitude Chart Panel"
})
public class AltitudeChartPanel extends TopComponent implements ICCPlugin {

    private IARDrone drone;

    private AltitudeChart chart;

    public AltitudeChartPanel() {
            this.setLayout(new GridBagLayout());
            this.setDisplayName(getTitle());
            this.setToolTipText(getDescription());
        this.chart = new AltitudeChart();
        JPanel chartPanel = new ChartPanel(chart.getChart(), true, true, true, true, true);

        add(chartPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    }

    private AltitudeListener altitudeListener = new AltitudeListener() {

        public void receivedAltitude(int altitude) {
            chart.setAltitude(altitude);
        }

        public void receivedExtendedAltitude(Altitude altitude) {
        }

    };

    @Override
    protected void componentOpened() {
        ARDrone d = ARDroneProvider.getDrone();
        activate(d);
    }

    @Override
    protected void componentClosed() {
        deactivate();
    }

    public void activate(IARDrone drone) {
        this.drone = drone;

        drone.getNavDataManager().addAltitudeListener(altitudeListener);
    }

    public void deactivate() {
        drone.getNavDataManager().removeAltitudeListener(altitudeListener);
    }

    public String getTitle() {
        return "Altitude Chart";
    }

    public String getDescription() {
        return "Displays a chart with the latest altitude";
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
