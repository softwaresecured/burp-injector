package burp_injector.ui;

import burp_injector.view.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The top level injector configuration panel that makes the main layout
 */
public class InjectorConfigTab extends JPanel {
    private RulesView rulesView;
    private RegexTargetsView regexTargetsView;
    private ScriptView scriptView;
    private TestRequestView testRequestView;

    public InjectorConfigTab(RulesView rulesView, RegexTargetsView regexTargetsView, ScriptView scriptView, TestRequestView testRequestView) {
        this.rulesView = rulesView;
        this.regexTargetsView = regexTargetsView;
        this.scriptView = scriptView;
        this.testRequestView = testRequestView;
        initComponents();
        initLayout();
    }



    private void initLayout() {

        // Scripts
        scriptView.jTabbedPaneScripts.addTab("Decode",scriptView.pnlDecodeScript);
        scriptView.jTabbedPaneScripts.addTab("Encode",scriptView.pnlEncodeScript);
        scriptView.jTabbedPaneScripts.addTab("Payload",scriptView.pnlPayloadProcess);

        // Rules / Targets panel
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new GridBagLayout());
        int idy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        pnlLeft.add(rulesView.pnlInjectorRules,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        pnlLeft.add(rulesView.pnlInjectorRuleEditor,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.insets = new Insets(0,2,0,2);
        pnlLeft.add(regexTargetsView.pnlRegexTargetEditor,gbc);

        JPanel pnlScriptTabs = new JPanel();
        pnlScriptTabs.setLayout(new GridBagLayout());
        pnlScriptTabs.setBorder(new TitledBorder("Python scripts"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlScriptTabs.add(scriptView.jTabbedPaneScripts,gbc);


        // Script / Test panel

        // Editor split
        JSplitPane splitPaneScriptOutput = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlScriptTabs,scriptView.pnlScriptOutput);
        JSplitPane splitPaneTopSplitTestRequest = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPaneScriptOutput,testRequestView.pnlTestRequest);

        // Main split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft,splitPaneTopSplitTestRequest);
        setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(splitPane,gbc);

        splitPaneTopSplitTestRequest.setResizeWeight(0.5);
        splitPaneScriptOutput.setResizeWeight(0.5);
        splitPaneTopSplitTestRequest.setDividerLocation(0.5);
        splitPaneScriptOutput.setDividerLocation(0.5);

    }

    private void initComponents() {
        setPreferredWidth(scriptView.pnlScriptOutput.jtxtSamplePayload,200);
        setPreferredWidth(scriptView.pnlScriptOutput.jtxtPythonPath,200);
    }

    private void setPreferredWidth(JComponent field, int width) {
        field.setPreferredSize(new Dimension(width, (int)field.getPreferredSize().getHeight()));
    }
}
