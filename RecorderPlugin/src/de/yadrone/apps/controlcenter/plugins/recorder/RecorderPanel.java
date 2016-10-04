/**
 *
 */
package de.yadrone.apps.controlcenter.plugins.recorder;

import de.yadrone.ARDroneProvider;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.yadrone.apps.controlcenter.CCPropertyManager;
import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.recorder.Recorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * @author Formicarufa (Tomas Prochazka) 14. 3. 2016
 */
@TopComponent.Description(
        preferredID = "RecorderPanel",
        iconBase = "de/yadrone/apps/controlcenter/plugins/altitude/iconmonstr-puzzle-1-16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "config_mode", openAtStartup = true)
@ActionID(category = "Window", id = "de.yadrone.apps.controlcenter.plugins.attitudechart.RecorderPanel")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RecorderPanelName",
        preferredID = "RecorderPanel"
)
@NbBundle.Messages({
    "CTL_RecorderPanelName=RecorderPanel",
    "CTL_RecorderPanel=RecorderPanel",
    "HINT_RecorderPanel=This is the RecorderPanel"
})
public class RecorderPanel extends TopComponent implements ICCPlugin, ItemListener {

    /**
     *
     */
    private static final long serialVersionUID = -2069639302901417264L;
    private JTextField textField;
    private IARDrone drone;
    boolean recording = false;
    private Recorder recorder;
    private FileOutputStream navdataout;
    private FileOutputStream commandsout;
    private JButton buttonRecord;
    private JCheckBox checkBoxZip;
    private String recordingName;
    private JLabel labelError;
    private JLabel lblPath;
    private JTextField pathField;
    private JButton buttonBrowse;
    private final Action action = new BrowseAction();
    private CCPropertyManager props;
    private File recordingFolder;
    private File locationFolder;
    private JLabel lblSeparator;
    private JComboBox separatorComboBox;
    private boolean isSeparatorTab;
    private JCheckBox chckbxAppendNumber;
    private JCheckBox chckbxRecordautomatically;
    private AutomaticRecording automaticRecording;

    /**
     * Create the panel.
     */
    public RecorderPanel() {
        this.setDisplayName(getTitle());
        this.setToolTipText(getDescription());
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{26, 0, 108, 153, 44, 0};
        gridBagLayout.rowHeights = new int[]{16, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        JLabel lblContent = new JLabel("Recording name:");
        GridBagConstraints gbc_lblContent = new GridBagConstraints();
        gbc_lblContent.anchor = GridBagConstraints.WEST;
        gbc_lblContent.insets = new Insets(0, 0, 5, 5);
        gbc_lblContent.gridx = 2;
        gbc_lblContent.gridy = 1;
        add(lblContent, gbc_lblContent);
        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridx = 3;
        gbc_textField.gridy = 1;
        add(textField, gbc_textField);
        textField.setColumns(10);

        lblPath = new JLabel("Path:");
        GridBagConstraints gbc_lblPath = new GridBagConstraints();
        gbc_lblPath.anchor = GridBagConstraints.WEST;
        gbc_lblPath.insets = new Insets(0, 0, 5, 5);
        gbc_lblPath.gridx = 2;
        gbc_lblPath.gridy = 2;
        add(lblPath, gbc_lblPath);

        pathField = new JTextField();
        GridBagConstraints gbc_pathField = new GridBagConstraints();
        gbc_pathField.fill = GridBagConstraints.BOTH;
        gbc_pathField.insets = new Insets(0, 0, 5, 5);
        gbc_pathField.gridx = 3;
        gbc_pathField.gridy = 2;
        add(pathField, gbc_pathField);
        pathField.setColumns(10);

        buttonBrowse = new JButton("...");
        buttonBrowse.setAction(action);
        GridBagConstraints gbc_buttonBrowse = new GridBagConstraints();
        gbc_buttonBrowse.insets = new Insets(0, 0, 5, 0);
        gbc_buttonBrowse.gridx = 4;
        gbc_buttonBrowse.gridy = 2;
        add(buttonBrowse, gbc_buttonBrowse);

        lblSeparator = new JLabel("Separator");
        GridBagConstraints gbc_lblSeparator = new GridBagConstraints();
        gbc_lblSeparator.anchor = GridBagConstraints.WEST;
        gbc_lblSeparator.insets = new Insets(0, 0, 5, 5);
        gbc_lblSeparator.gridx = 2;
        gbc_lblSeparator.gridy = 3;
        add(lblSeparator, gbc_lblSeparator);

        separatorComboBox = new JComboBox();
        separatorComboBox.setModel(new DefaultComboBoxModel(new String[]{"Tab", "Comma"}));
        GridBagConstraints gbc_separatorComboBox = new GridBagConstraints();
        gbc_separatorComboBox.insets = new Insets(0, 0, 5, 5);
        gbc_separatorComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_separatorComboBox.gridx = 3;
        gbc_separatorComboBox.gridy = 3;
        add(separatorComboBox, gbc_separatorComboBox);
        checkBoxZip = new JCheckBox("Package to .zip");
        checkBoxZip.setToolTipText("The resulting text files will be packaged into a zip archive.");
        checkBoxZip.setSelected(true);
        GridBagConstraints gbc_checkBoxZip = new GridBagConstraints();
        gbc_checkBoxZip.anchor = GridBagConstraints.NORTHWEST;
        gbc_checkBoxZip.insets = new Insets(0, 0, 5, 5);
        gbc_checkBoxZip.gridx = 2;
        gbc_checkBoxZip.gridy = 4;
        add(checkBoxZip, gbc_checkBoxZip);

        chckbxAppendNumber = new JCheckBox("Append number");
        chckbxAppendNumber.setToolTipText("The number will be appended to the name of the recording.");
        chckbxAppendNumber.setSelected(true);
        GridBagConstraints gbc_chckbxAppendNumber = new GridBagConstraints();
        gbc_chckbxAppendNumber.anchor = GridBagConstraints.WEST;
        gbc_chckbxAppendNumber.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxAppendNumber.gridx = 2;
        gbc_chckbxAppendNumber.gridy = 5;
        add(chckbxAppendNumber, gbc_chckbxAppendNumber);

        chckbxRecordautomatically = new JCheckBox("Record automatically");
        chckbxRecordautomatically.setToolTipText("Automatically starts recording when the drones takes off and stops recording when the drone lands.");
        GridBagConstraints gbc_chckbxRecordautomatically = new GridBagConstraints();
        gbc_chckbxRecordautomatically.anchor = GridBagConstraints.WEST;
        gbc_chckbxRecordautomatically.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxRecordautomatically.gridx = 2;
        gbc_chckbxRecordautomatically.gridy = 6;
        add(chckbxRecordautomatically, gbc_chckbxRecordautomatically);

        buttonRecord = new JButton("Start recording");
        buttonRecord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                labelError.setText("");
                if (!recording) {
                    startRecording(getNameFieldText(),chckbxAppendNumber.isSelected());
                } else {
                    stopRecording();
                }
            }
        });
        GridBagConstraints gbc_buttonRecord = new GridBagConstraints();
        gbc_buttonRecord.anchor = GridBagConstraints.NORTHWEST;
        gbc_buttonRecord.insets = new Insets(0, 0, 5, 5);
        gbc_buttonRecord.gridx = 2;
        gbc_buttonRecord.gridy = 7;
        add(buttonRecord, gbc_buttonRecord);

        labelError = new JLabel("");
        GridBagConstraints gbc_labelError = new GridBagConstraints();
        gbc_labelError.insets = new Insets(0, 0, 5, 0);
        gbc_labelError.gridwidth = 2;
        gbc_labelError.gridx = 2;
        gbc_labelError.gridy = 8;
        add(labelError, gbc_labelError);
        props = CCPropertyManager.getInstance();
        textField.addActionListener(x->chckbxRecordautomatically.requestFocus());
    }

    /**
     *
     */
    public void startRecording(String name, boolean appendNumber) {
        recordingName = name;
        if (recordingName.isEmpty()) {
            labelError.setText("Please, insert a name.");
            return;
        }
        locationFolder = new File(pathField.getText());
        if (appendNumber) {
            recordingFolder = nameWithAppendedNumber(locationFolder, recordingName);
            recordingName = recordingFolder.getName();
        } else {
            recordingFolder = new File(locationFolder, recordingName);
            if (recordingFolder.exists()) {
                labelError.setText("Sorry. Select a unique name.");
                return;
            }

        }
        recordingFolder.mkdir();
        if (!recordingFolder.exists()) {
            labelError.setText("Sorry. Unable to create a directory.");
        }
        File navdataFile;
        File commandsFile;
        isSeparatorTab = separatorComboBox.getSelectedIndex() == 0;
        if (isSeparatorTab) {
            navdataFile = new File(recordingFolder, "navdata.tsv");
            commandsFile = new File(recordingFolder, "commands.tsv");
        } else {
            navdataFile = new File(recordingFolder, "navdata.csv");
            commandsFile = new File(recordingFolder, "commands.csv");
        }
        try {
            navdataout = new FileOutputStream(navdataFile);
            commandsout = new FileOutputStream(commandsFile);
        } catch (IOException e) {
            labelError.setText("Sorry. Can not open files for writing.");
            e.printStackTrace();
            return;
        }
        recording = true;
        buttonRecord.setText("Stop recording.");
        this.disableFocus();
        char separator = isSeparatorTab ? '\t' : ',';
        recorder.startRecordingNavdata(new PrintStream(navdataout), separator);
        recorder.startRecordingCommands(new PrintStream(commandsout), separator);
        labelError.setText("Recording of \"" + recordingName + "\" started.");
    }

    private File nameWithAppendedNumber(File locationFolder, String recordingName) {
        File recording;
        String suffix;
        int num = 1;
        File recPacked;
        do {
            if (num < 9) {
                suffix = "0" + num;
            } else {
                suffix = Integer.toString(num);
            }
            recording = new File(locationFolder, recordingName + suffix);
            recPacked = new File(locationFolder, recordingName + suffix + ".zip");
            num++;
        } while (recording.exists() || recPacked.exists());
        return recording;
    }

    public void disableFocus() {
        setElementsFocusable(false);
    }

    public void enableFocus() {
        setElementsFocusable(true);
    }

    public void setElementsFocusable(boolean b) {
        buttonBrowse.setFocusable(b);
        buttonRecord.setFocusable(b);
        textField.setFocusable(b);
        pathField.setFocusable(b);
        checkBoxZip.setFocusable(b);
        separatorComboBox.setFocusable(b);
        chckbxAppendNumber.setFocusable(b);
        chckbxRecordautomatically.setFocusable(b);
    }

    /**
     *
     */
    public void stopRecording() {
        recorder.stopRecordingCommands();
        recorder.stopRecordingNavdata();
        recording = false;
        try {
            navdataout.close();
            commandsout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        copyContentsInfoXml();
        if (checkBoxZip.isSelected()) {
            try {
                ZipFolder.pack(recordingFolder, new File(locationFolder, recordingName + ".zip"));
            } catch (IOException e) {
                labelError.setText("Sorry. Unable to save .zip.");
                e.printStackTrace();
                return;
            }
            boolean res = DirectoryRemover.remove(recordingFolder);
            if (!res) {
                labelError.setText("Sorry. Unable to delete old files.");
            }
        }
        buttonRecord.setText("Start recording");
        enableFocus();
        labelError.setText("Recording of \"" + recordingName + "\" finished.");
    }

    /**
     *
     */
    private void copyContentsInfoXml() {
        InputStream filesInfo;
        if (isSeparatorTab) {
            filesInfo = getClass().getResourceAsStream("/de/yadrone/base/recorder/description.xml");
        } else {
            filesInfo = getClass().getResourceAsStream("/de/yadrone/base/recorder/description.csv.xml");
        }
        if (filesInfo == null) {
            labelError.setText("Sorry. Content description file missing");
        } else {
            try {
                Files.copy(filesInfo, new File(recordingFolder, "description.xml").toPath());
            } catch (IOException e) {
                labelError.setText("Sorry. Unable to add description.");
                e.printStackTrace();
            }
        }
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

    @Override
    public void activate(IARDrone drone) {
        this.drone = drone;
        recorder = new Recorder(drone);
        pathField.setText(props.getRecordingStoragePath());
        automaticRecording = new AutomaticRecording(drone.getNavDataManager(), this);
        if (chckbxRecordautomatically.isSelected()) {
            activateAutomaticRecording();
        }
        chckbxRecordautomatically.addItemListener(this);

    }

    private void activateAutomaticRecording() {
        automaticRecording.activate();
        buttonRecord.setEnabled(false);
    }

    public String getNameFieldText() {
        return textField.getText();
    }

    @Override
    public void deactivate() {
        if (recording) {
            stopRecording();
        }
        if (automaticRecording.isActive()) {
            deactivateAutomaticRecording();
        }
        chckbxRecordautomatically.removeItemListener(this);
    }

    private void deactivateAutomaticRecording() {
        automaticRecording.deactivate();
        buttonRecord.setEnabled(true);
    }

    public boolean isRecording() {
        return recording;
    }

    @Override
    public String getTitle() {
        return "NavData Recorder";
    }

    /* (non-Javadoc)
	 * @see de.yadrone.apps.controlcenter.ICCPlugin#getDescription()
     */
    @Override
    public String getDescription() {
        return "Saves navdata and commands to a text file.";
    }

    /* (non-Javadoc)
	 * @see de.yadrone.apps.controlcenter.ICCPlugin#isVisual()
     */
    @Override
    public boolean isVisual() {
        return true;
    }

    /* (non-Javadoc)
	 * @see de.yadrone.apps.controlcenter.ICCPlugin#getScreenSize()
     */
    @Override
    public Dimension getScreenSize() {
        return new Dimension(450, 230);
    }

    /* (non-Javadoc)
	 * @see de.yadrone.apps.controlcenter.ICCPlugin#getScreenLocation()
     */
    @Override
    public Point getScreenLocation() {
        return new Point(330, 390);
    }

    /**
     * State of the checkbox "record automatically" has changed.
     *
     * @param e
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (chckbxRecordautomatically.isSelected()) {
            activateAutomaticRecording();
        } else {
            deactivateAutomaticRecording();
        }
    }

    private class BrowseAction extends AbstractAction {

        public BrowseAction() {
            putValue(NAME, "...");
            putValue(SHORT_DESCRIPTION, "Choose path");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser(props.getVideoStoragePath());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = fc.showOpenDialog(RecorderPanel.this);
            if (res == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getPath();
                props.setRecordingStoragePath(path);
                pathField.setText(path);
            }
        }
    }
}
