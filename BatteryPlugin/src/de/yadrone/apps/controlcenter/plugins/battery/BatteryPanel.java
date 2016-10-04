package de.yadrone.apps.controlcenter.plugins.battery;

import de.yadrone.ARDroneProvider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.BatteryListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "BatteryPanel",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "battery_mode", openAtStartup = true)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.battery.BatteryPanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BatteryPanel",
        preferredID = "BatteryPanel"
)
@NbBundle.Messages({
    "CTL_BatteryPanel=Battery Panel Panel",
    "CTL_BatteryPanelTopComponent=BatteryPanel",
    "HINT_AttitudeTopCompTopComponent=This is the Battery Panel"
})
public class BatteryPanel extends TopComponent implements ICCPlugin {

    private IARDrone drone;

    private Font font = new Font("Helvetica", Font.PLAIN, 10);
    private int batteryLevel = 100;
    private int voltageLevel;

    public BatteryPanel() {
        //setSize(20, 60);
        this.setDisplayName(getTitle());
        this.setToolTipText(getDescription());
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(0, 0, this.getWidth(), this.getHeight());

        Color c;
        if (batteryLevel >= 50) {
            c = new Color((Math.abs(batteryLevel - 100f) / 100f) * 2f, 1f, 0f);
        } else {
            c = new Color(1f, (batteryLevel / 100f) * 2f, 0f);
        }

        g.setColor(c);
        g.fillRect(0, getHeight() * (batteryLevel / 100), this.getWidth(), this.getHeight());

        FontMetrics metrics = g.getFontMetrics(font);
        int hgt = metrics.getHeight();
        g.setFont(font);

        g.setColor(Color.black);
        g.drawString("Battery", (this.getWidth() / 2) - (metrics.stringWidth("Battery") / 2), (this.getHeight() / 2) - (hgt / 2));
        g.drawString(batteryLevel + " %", (this.getWidth() / 2) - (metrics.stringWidth(batteryLevel + " %") / 2), (this.getHeight() / 2) + (hgt / 2));
        g.drawString(voltageLevel + " V", (this.getWidth() / 2) - (metrics.stringWidth(voltageLevel + " V") / 2), (int) ((this.getHeight() / 2) + ((hgt / 2) * 2.5)));
    }

    private BatteryListener batteryListener = new BatteryListener() {

        public void voltageChanged(int vbat_raw) {

        }

        public void batteryLevelChanged(int batteryLevel) {
            if (batteryLevel != BatteryPanel.this.batteryLevel) {
                BatteryPanel.this.batteryLevel = batteryLevel;
                repaint();
            }
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
        drone.getNavDataManager().addBatteryListener(batteryListener);
    }

    public void deactivate() {
        drone.getNavDataManager().removeBatteryListener(batteryListener);
    }

    public String getTitle() {
        return "Battery";
    }

    public String getDescription() {
        return "Displays current battery and voltage levels";
    }

    public boolean isVisual() {
        return true;
    }

    public Dimension getScreenSize() {
        return new Dimension(60, 120);
    }

    public Point getScreenLocation() {
        return new Point(890, 0);
    }

}
