package de.yadrone.apps.controlcenter.plugins.remotelog;

import de.yadrone.ARDroneProvider;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.apache.commons.net.telnet.TelnetClient;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import java.awt.GridBagLayout;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "RemoteLogPanel",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.remotelog.RemoteLogPanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RemoteLogPanelName",
        preferredID = "RemoteLogPanel"
)
@NbBundle.Messages({
    "CTL_RemoteLogPanelName=RemoteLogPanel",
    "CTL_RemoteLogPanel=RemoteLogPanel",
    "HINT_RemoteLogPanel=This is the RemoteLogPanel"
})

public class RemoteLogPanel extends TopComponent implements ICCPlugin {

    private TelnetClient telnet;
    private JTextArea text;

    public RemoteLogPanel() {
        this.setLayout(new GridBagLayout());
        this.setDisplayName(getTitle());
        this.setToolTipText(getDescription());
        text = new JTextArea("Waiting for remote log ...");
        // text.setEditable(false);
        text.setFont(new Font("Courier", Font.PLAIN, 10));

        DefaultCaret caret = (DefaultCaret) text.getCaret(); // auto scroll
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        add(new JScrollPane(text), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    }

    public void telnetConnect() {
        new Thread(new Runnable() {

            public void run() {
                try {
                    telnet = new TelnetClient();

                    telnet.connect("192.168.1.1");

                    new Thread(new Runnable() {
                        public void run() {
                            InputStream instr = telnet.getInputStream();

                            byte[] buff = new byte[1024];
                            int ret_read = 0;

                            try {
                                do {
                                    ret_read = instr.read(buff);
                                    if (ret_read > 0) {
                                        final String str = new String(buff, 0, ret_read);
                                        text.append(str);
                                        text.setCaretPosition(text.getDocument().getLength());
                                    }
                                } while (ret_read >= 0);
                            } catch (Exception exc) {
                                exc.printStackTrace();
                            }
                        }
                    }).start();

                    telnet.getOutputStream().write("tail -f /data/syslog.bin \r\n".getBytes());
                    telnet.getOutputStream().flush();

                } catch (Exception e) {
                    System.err.println("Exception while reading socket:" + e.getMessage());
                }
            }

        }).start();
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

    public void activate(IARDrone drone) {
        telnetConnect();
    }

    public void deactivate() {
        try {
            telnet.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            //System.exit(1);
        }
    }

    public String getTitle() {
        return "Logging Console (remote)";
    }

    public String getDescription() {
        return "Displays the drone's onboard log-file (syslog.bin).";
    }

    public boolean isVisual() {
        return true;
    }

    public Dimension getScreenSize() {
        return new Dimension(600, 300);
    }

    public Point getScreenLocation() {
        return new Point(600, 400);
    }

}
