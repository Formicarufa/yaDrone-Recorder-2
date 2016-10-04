package de.yadrone.apps.controlcenter.plugins.keyboardplug;

import de.yadrone.ARDroneProvider;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import de.yadrone.apps.controlcenter.CCPropertyManager;
import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;
import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManagerAlternative;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "KeyboardLayoutPanel",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "output", openAtStartup = true)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.keyboardplug.KeyboardLayoutPanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_KeyboardLayoutPanelName",
        preferredID = "KeyboardLayoutPanel"
)
@NbBundle.Messages({
    "CTL_KeyboardLayoutPanelName=Keyboard Layout Panel",
    "CTL_KeyboardLayoutPanel=Keyboard Layout Panel",
    "HINT_KeyboardLayoutPanel=This is the Keyboard Layout Panel"
})

public class KeyboardLayoutPanel extends TopComponent implements ICCPlugin {

    private KeyboardCommandManager cmdManager;

    private String currentKey = "";

    private Image originalImage;
    private Image scaledImage;
    private int width;
    private int height;

    private JRadioButton originalButton;
    ;
    private JRadioButton alternativeButton;
    ;
    
    private IARDrone drone;

    public KeyboardLayoutPanel() {
        loadImage(CCPropertyManager.getInstance().isKeyboardCommandManagerAlternative());
        this.setDisplayName(getTitle());
        this.setToolTipText(getDescription());
        originalButton = new JRadioButton("Original Layout");
        originalButton.setOpaque(false);
        originalButton.setFocusable(false);
        originalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdManager = new KeyboardCommandManager(drone);
                CCPropertyManager.getInstance().setKeyboardCommandManagerAlternative(false);
                loadImage(CCPropertyManager.getInstance().isKeyboardCommandManagerAlternative());
                repaint();
            }
        });
        originalButton.setSelected(!CCPropertyManager.getInstance().isKeyboardCommandManagerAlternative());

        alternativeButton = new JRadioButton("Alternative WASD");
        alternativeButton.setOpaque(false);
        alternativeButton.setFocusable(false);
        alternativeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdManager = new KeyboardCommandManagerAlternative(drone);
                CCPropertyManager.getInstance().setKeyboardCommandManagerAlternative(true);
                loadImage(CCPropertyManager.getInstance().isKeyboardCommandManagerAlternative());
                repaint();
            }
        });
        alternativeButton.setSelected(CCPropertyManager.getInstance().isKeyboardCommandManagerAlternative());

        ButtonGroup group = new ButtonGroup();
        group.add(originalButton);
        group.add(alternativeButton);

        setLayout(new GridBagLayout());
        add(originalButton, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(alternativeButton, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void loadImage(boolean isAlternative) {
        ImageIcon icon = new ImageIcon(KeyboardLayoutPanel.class.getResource("keyboard_control" + (isAlternative ? "_alternative" : "") + ".png"));
        originalImage = icon.getImage();
        scaledImage = originalImage;
        width = 0;
        height = 0;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if ((width != getWidth()) || (height != getHeight())) {
            width = getWidth();
            height = getHeight();
            scaledImage = getScaledImage();
        }

        g.drawImage(scaledImage, 0, 0, this);
        g.drawString(currentKey, 10, 20);
    }

    private Image getScaledImage() {
        return originalImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING);
    }

    private KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {

        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getSource() instanceof JTextField) {
                return false; // We do not want to interpret text typed to a text field as a command for the drone.
            }
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                cmdManager.keyPressed(e);
                currentKey = "KEY " + e.getKeyChar();
                repaint();
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                cmdManager.keyReleased(e);
                currentKey = "";
                repaint();
            }
            return false;
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

        if (CCPropertyManager.getInstance().isKeyboardCommandManagerAlternative()) {
            cmdManager = new KeyboardCommandManagerAlternative(drone);
        } else {
            cmdManager = new KeyboardCommandManager(drone);
        }

        // CommandManager handles (keyboard) input and dispatches events to the drone		
        System.out.println("KeyboardLayoutPanel: grab the whole keyboard input from now on ...");
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyEventDispatcher);
    }

    public void deactivate() {
        System.out.println("KeyboardLayoutPanel: release key event dispatcher ...");

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.removeKeyEventDispatcher(keyEventDispatcher);
    }

    public String getTitle() {
        return "Keyboard Control";
    }

    public String getDescription() {
        return "Allow to control the drone via keyboard";
    }

    public boolean isVisual() {
        return true;
    }

    public Dimension getScreenSize() {
        return new Dimension(400, 180);
    }

    public Point getScreenLocation() {
        return new Point(330, 260);
    }
}
