package burp_injector.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
/**
 * The script output tab that appears in the middle of the right hand frame ( right split )
 */
public class PnlScriptOutput extends JPanel {
    public final JTextField jtxtSamplePayload = new JTextField();
    public final JTextArea jtxtOutput = new JTextArea();
    public final JRadioButton jRadioButtonStderr = new JRadioButton("Error");
    public final JRadioButton jRadioButtonStdout = new JRadioButton("Output");
    public final JLabel jlblExecutionTimeMs = new JLabel("Execution time: 0 ms");
    public final JTextField jtxtPythonPath = new JTextField();
    public final JButton jbtnImportScripts = new JButton("Import Scripts");
    public final JButton jbtnExportScripts = new JButton("Export Scripts");
    public final JButton jbtnExecuteToggle = new JButton("Execute");
    private JScrollPane jScrollPaneStdout;
    public ButtonGroup outputFileRadioButtonGroup;
    public PnlScriptOutput() {
        initComponents();
        initLayout();
        jRadioButtonStdout.setSelected(true);
    }

    @Override
    public void setEnabled( boolean enabled ) {
        jtxtSamplePayload.setEnabled(enabled);
        jtxtOutput.setEnabled(enabled);
        jRadioButtonStderr.setEnabled(enabled);
        jRadioButtonStdout.setEnabled(enabled);
        jlblExecutionTimeMs.setEnabled(enabled);
        jbtnExecuteToggle.setEnabled(enabled);
    }

    private void initComponents() {
        jtxtOutput.setTabSize(1);
        jtxtOutput.setEditable(false);
    }

    private void initLayout() {
        setBorder(new TitledBorder("Python script debug"));
        jScrollPaneStdout = new JScrollPane(jtxtOutput);
        jScrollPaneStdout.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPaneStdout.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Toolbar
        JPanel jpnlExecutionToolbar = new JPanel();
        int idx = 0;
        jpnlExecutionToolbar.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(new JLabel("Test payload"),gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(jtxtSamplePayload,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(new JLabel("Python path"),gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(jtxtPythonPath,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(jbtnImportScripts,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(jbtnExportScripts,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlExecutionToolbar.add(jbtnExecuteToggle,gbc);

        // Toolbar
        JPanel jpnlOutputToolbar = new JPanel();
        outputFileRadioButtonGroup = new ButtonGroup();
        outputFileRadioButtonGroup.add(jRadioButtonStdout);
        outputFileRadioButtonGroup.add(jRadioButtonStderr);
        idx = 0;
        jpnlOutputToolbar.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlOutputToolbar.add(jRadioButtonStdout,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlOutputToolbar.add(jRadioButtonStderr,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = idx;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlOutputToolbar.add(new JPanel(),gbc);

        int idy = 0;
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        add(jpnlExecutionToolbar,gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,2,0,2);
        add(jlblExecutionTimeMs,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        add(jpnlOutputToolbar,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        add(jScrollPaneStdout,gbc);
    }

    public void updateExecutionTime( long runTime ) {
        jlblExecutionTimeMs.setText(String.format("Last execution time: %d ms", runTime));
    }
}
