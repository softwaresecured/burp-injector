package burp_injector.ui;

import burp_injector.enums.EditorState;
import burp_injector.enums.TargetingMethod;
import burp_injector.model.RulesModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The injector rule editor, appears middle left on the injector tab
 */
public class PnlInjectorRuleEditor extends JPanel {

    private RulesModel rulesModel;

    // Top level details
    public final JTextField jtxtRuleName = new JTextField();
    public final JTextField jtxtRuleDescription = new JTextField();
    public final JTextField jtxtRuleScope = new JTextField();
    public final JTextField jtxtTargetAreaRegex = new JTextField();
    public final JSpinner jspnTargetAreaCaptureGroup = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    public final JCheckBox jchkRuleEnabled = new JCheckBox("Enabled");

    // Target selection method
    public final JRadioButton jRadioTargetMethodAuto = new JRadioButton("Auto");
    public final JRadioButton jRadioTargetMethodCustomAuto = new JRadioButton("Custom auto");
    public final JRadioButton jRadioTargetMethodRegex = new JRadioButton("Regex");

    // Custom auto target
    public final JTextField jtxtCustomAutoRegex = new JTextField();
    public final JSpinner jspnCustomAutoNameCaptureGroup = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    public final JSpinner jspnCustomAutoValueCaptureGroup = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    public final JCheckBox jchkAutoName = new JCheckBox("Auto-generate name");

    public ButtonGroup targetMethodRadioGroup;

    public JPanel jpnlRuleGeneral;
    public JPanel jpnlAutoRule;
    public JPanel jpnlCustomAutoRule;
    public JPanel jpnlRegexRule;

    public PnlInjectorRuleEditor(RulesModel rulesModel) {
        this.rulesModel = rulesModel;
        initComponents();
        initLayout();
    }

    private void initComponents() {

    }

    private void initLayout() {
        targetMethodRadioGroup = new ButtonGroup();
        targetMethodRadioGroup.add(jRadioTargetMethodAuto);
        targetMethodRadioGroup.add(jRadioTargetMethodCustomAuto);
        targetMethodRadioGroup.add(jRadioTargetMethodRegex);

        // Rule general details
        jpnlRuleGeneral = new JPanel();
        jpnlRuleGeneral.setLayout(new GridBagLayout());
        int idy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(new JLabel("Name"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(jtxtRuleName,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(new JLabel("Rule scope"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(jtxtRuleScope,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(new JLabel("Target area regex"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(jtxtTargetAreaRegex,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(new JLabel("Target area capture group"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(jspnTargetAreaCaptureGroup,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(new JLabel("Description"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(jtxtRuleDescription,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRuleGeneral.add(jchkRuleEnabled,gbc);

        // Auto rule
        jpnlAutoRule = new JPanel();
        jpnlAutoRule.setBorder(new TitledBorder("Auto target rule"));
        jpnlAutoRule.setLayout(new GridBagLayout());
        idy = 0;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlAutoRule.add(jRadioTargetMethodAuto, gbc);


        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        jpnlAutoRule.add(new JPanel(),gbc);


        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpnlAutoRule.add(getDescriptionComponent("Automatically target insertion points using built in regexes that match common formats like JSON, XML and key value pairs."), gbc);

        // Custom auto rule
        jpnlCustomAutoRule = new JPanel();
        jpnlCustomAutoRule.setBorder(new TitledBorder("Custom auto target rule"));
        jpnlCustomAutoRule.setLayout(new GridBagLayout());
        idy = 0;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(jRadioTargetMethodCustomAuto, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(getDescriptionComponent("Automatically target insertion points using a custom provided regex. Use this if auto-targeting does not match the given format."),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(new JLabel("Regex"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(jtxtCustomAutoRegex,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(jchkAutoName,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(new JLabel("Name capture group"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(jspnCustomAutoNameCaptureGroup,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(new JLabel("Value capture group"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlCustomAutoRule.add(jspnCustomAutoValueCaptureGroup,gbc);

        // Regex rule
        jpnlRegexRule = new JPanel();
        jpnlRegexRule.setBorder(new TitledBorder("Individual regex target rule"));
        jpnlRegexRule.setLayout(new GridBagLayout());
        idy = 0;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRegexRule.add(jRadioTargetMethodRegex,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRegexRule.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,2,0,2);
        jpnlRegexRule.add(getDescriptionComponent("Define multiple insertion targets based on regex."),gbc);

        // Main
        setBorder(new TitledBorder("Rule editor"));
        setLayout(new GridBagLayout());
        idy = 0;
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        add(jpnlRuleGeneral,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        add(jpnlAutoRule,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        add(jpnlCustomAutoRule,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.insets = new Insets(0,2,0,2);
        add(jpnlRegexRule,gbc);
    }

    private JTextArea getDescriptionComponent( String text ) {
        JTextArea jTextArea = new JTextArea(text);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        return jTextArea;
    }

    @Override
    public void setEnabled( boolean status ) {
        jtxtRuleName.setEnabled(status);
        jtxtRuleDescription.setEnabled(status);
        jtxtRuleScope.setEnabled(status);
        jtxtTargetAreaRegex.setEnabled(status);
        jspnTargetAreaCaptureGroup.setEnabled(status);
        jchkRuleEnabled.setEnabled(status);
        jRadioTargetMethodAuto.setEnabled(status);
        jRadioTargetMethodCustomAuto.setEnabled(status);
        jRadioTargetMethodRegex.setEnabled(status);
        jtxtCustomAutoRegex.setEnabled(status);
        jspnCustomAutoNameCaptureGroup.setEnabled(status);
        jspnCustomAutoValueCaptureGroup.setEnabled(status);
        jchkAutoName.setEnabled(status);
        jpnlRuleGeneral.setEnabled(status);
        jpnlAutoRule.setEnabled(status);
        jpnlCustomAutoRule.setEnabled(status);
        jpnlRegexRule.setEnabled(status);
    }

    public void toggleInputStatusByTargetMethod( TargetingMethod targetingMethod ) {
        switch ( targetingMethod ) {
            case REGEX:
            case AUTO:
                jtxtCustomAutoRegex.setEnabled(false);
                jspnCustomAutoNameCaptureGroup.setEnabled(false);
                jspnCustomAutoValueCaptureGroup.setEnabled(false);
                jchkAutoName.setEnabled(false);
                break;
            case CUSTOM_AUTO:
                jtxtCustomAutoRegex.setEnabled(true);
                jspnCustomAutoNameCaptureGroup.setEnabled(true);
                jspnCustomAutoValueCaptureGroup.setEnabled(true);
                jchkAutoName.setEnabled(true);
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

    private void toggleEditorInputs(boolean status) {
        jtxtRuleName.setEnabled(status);
        jtxtRuleDescription.setEnabled(status);
        jtxtRuleScope.setEnabled(status);
        jtxtTargetAreaRegex.setEnabled(status);
        jspnTargetAreaCaptureGroup.setEnabled(status);
        jchkRuleEnabled.setEnabled(status);
        jRadioTargetMethodAuto.setEnabled(status);
        jRadioTargetMethodCustomAuto.setEnabled(status);
        jRadioTargetMethodRegex.setEnabled(status);
        jtxtCustomAutoRegex.setEnabled(status);
        jspnCustomAutoNameCaptureGroup.setEnabled(status);
        jspnCustomAutoValueCaptureGroup.setEnabled(status);
        jchkAutoName.setEnabled(status);
        jpnlRuleGeneral.setEnabled(status);
        jpnlAutoRule.setEnabled(status);
        jpnlCustomAutoRule.setEnabled(status);
        jpnlRegexRule.setEnabled(status);
        if ( status == true ) {
            toggleInputStatusByTargetMethod(rulesModel.getRuleTargetingMethod());
        }
    }
}
