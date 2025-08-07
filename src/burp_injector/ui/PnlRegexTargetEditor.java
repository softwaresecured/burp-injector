package burp_injector.ui;

import burp_injector.enums.EditorState;
import burp_injector.model.RegexTargetsModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The injector target editor that appears at the bottom left of the injector tab.
 */
public class PnlRegexTargetEditor extends JPanel {

    public final JButton jbtnNew = new JButton("New");
    public final JButton jbtnSave = new JButton("Save");
    public final JButton jbtnDelete = new JButton("Delete");
    public final JButton jbtnCancel = new JButton("Cancel");

    private RegexTargetsModel regexTargetsModel;
    public JTable jtblTargets;
    public final JTextField jtxtRegexName = new JTextField();
    public final JTextField jtxtRegexPattern = new JTextField();
    public final JSpinner jspnRegexPatternCaptureGroup = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    private JPanel pnlCurrentRuleTargets;
    private JPanel pnlTargetDetails;
    private JPanel jpnlToolbar;

    public PnlRegexTargetEditor(RegexTargetsModel regexTargetsModel) {
        this.regexTargetsModel = regexTargetsModel;
        initComponents();
        initLayout();
    }

    private void initLayout() {
        // Targets table
        JScrollPane jScrollPaneTable = new JScrollPane(jtblTargets);
        jScrollPaneTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPaneTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pnlCurrentRuleTargets = new JPanel();
        pnlCurrentRuleTargets.setBorder(new TitledBorder("Current targets"));
        pnlCurrentRuleTargets.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        pnlCurrentRuleTargets.add(jScrollPaneTable,gbc);

        // Button toolbar
        jpnlToolbar = new JPanel();
        jpnlToolbar.setLayout(new GridBagLayout());
        int idx = 0;
        jpnlToolbar.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlToolbar.add(jbtnNew,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlToolbar.add(jbtnSave,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlToolbar.add(jbtnDelete,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        jpnlToolbar.add(jbtnCancel,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlToolbar.add(new JPanel(),gbc);

        // Target editor panel
        int idy = 0;
        pnlTargetDetails = new JPanel();
        pnlTargetDetails.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        pnlTargetDetails.add(new JLabel("Name"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        pnlTargetDetails.add(jtxtRegexName,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        pnlTargetDetails.add(new JLabel("Regex"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        pnlTargetDetails.add(jtxtRegexPattern,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        pnlTargetDetails.add(new JLabel("Value capture group"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,2,0,2);
        pnlTargetDetails.add(jspnRegexPatternCaptureGroup,gbc);

        // Main layout
        setBorder(new TitledBorder("Targets"));
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        add(pnlTargetDetails,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        add(jpnlToolbar,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.insets = new Insets(0,2,0,2);
        add(pnlCurrentRuleTargets,gbc);
    }

    private void initComponents() {
        jtblTargets = new JTable(regexTargetsModel.getTargetsTableModel()) {
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        int[] colWidths = { 0, 80 };
        for ( int i = 0; i < colWidths.length; i++ ) {
            jtblTargets.getColumnModel().getColumn(i).setMinWidth(colWidths[i]);
            jtblTargets.getColumnModel().getColumn(i).setMaxWidth(colWidths[i]);
        }
        jtblTargets.setRowSelectionAllowed(true);
        jtblTargets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void setEnabled( boolean status ) {
        pnlCurrentRuleTargets.setEnabled(status);
        pnlTargetDetails.setEnabled(status);
        jpnlToolbar.setEnabled(status);
        jbtnNew.setEnabled(status);
        jbtnSave.setEnabled(status);
        jbtnDelete.setEnabled(status);
        jbtnCancel.setEnabled(status);
        jtxtRegexName.setEnabled(status);
        jtxtRegexPattern.setEnabled(status);
        jspnRegexPatternCaptureGroup.setEnabled(status);
        jtblTargets.setEnabled(status);
    }

    private void toggleEditorInputs(boolean status) {
        jtxtRegexName.setEnabled(status);
        jtxtRegexPattern.setEnabled(status);
        jspnRegexPatternCaptureGroup.setEnabled(status);
    }

    public void updateEditorButtonState(EditorState state) {
        switch (state) {
            case CREATE:
                jbtnNew.setEnabled(false);
                jbtnSave.setEnabled(true);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(true);
                break;
            case EDIT:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(true);
                jbtnDelete.setEnabled(true);
                jbtnCancel.setEnabled(false);
                break;
            case INITIAL:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(false);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(false);
                break;
            case DISABLED:
                jbtnNew.setEnabled(false);
                jbtnSave.setEnabled(false);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(false);
                break;
        }
    }

    public void updateEditorInputState(EditorState state) {
        switch (state) {
            case CREATE:
            case EDIT:
                toggleEditorInputs(true);
                break;
            case INITIAL:
            case DISABLED:
                toggleEditorInputs(false);
                break;
        }
    }
}
