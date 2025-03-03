package burp_injector.editors;


import burp_injector.model.data.InjectorRule;
import burp_injector.util.InjectorUtil;
import burp_injector.util.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * The user interface panel that is loaded into repeater tab
 */
public class RequestEditorUI extends JPanel {

    public JTextArea jtxtDecodedTargetArea = new JTextArea();
    public JComboBox<String> jcmbApplicableRule = new JComboBox<String>();
    public JLabel jblRulesMatchedCount = new JLabel();
    public JLabel jlblStatusMessage = new JLabel();
    private boolean changed;
    public RequestEditorUI() {
        initLayout();
        initEventListeners();
    }
    private void initLayout() {


        JPanel pnlRulesToolbar = new JPanel();
        pnlRulesToolbar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlRulesToolbar.add(new JLabel("Applicable rules"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlRulesToolbar.add(jcmbApplicableRule,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlRulesToolbar.add(jblRulesMatchedCount,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlRulesToolbar.add(jlblStatusMessage,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        pnlRulesToolbar.add(new JPanel(),gbc);


        jtxtDecodedTargetArea.setLineWrap(true);
        jtxtDecodedTargetArea.setWrapStyleWord(true);
        JScrollPane jScrollPane = new JScrollPane(jtxtDecodedTargetArea);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(pnlRulesToolbar,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(jScrollPane,gbc);
    }

    public void initEventListeners() {
        jtxtDecodedTargetArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setChanged(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setChanged(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setChanged(true);
            }
        });
    }

    public void setEditorContent( String content ) {
        jtxtDecodedTargetArea.setText(content);
        jtxtDecodedTargetArea.setTabSize(1);
        setChanged(false);
    }

    public String getEditorContent() {
        return jtxtDecodedTargetArea.getText();
    }

    public void setChanged(boolean status) {
        changed = status;
    }

    public boolean isChanged() {
        return changed;
    }

    public void updateApplicableRules(ArrayList<InjectorRule> rules ) {
        jcmbApplicableRule.removeAllItems();
        for ( InjectorRule rule : rules ) {
            jcmbApplicableRule.addItem(rule.getName());
        }
        jblRulesMatchedCount.setText(String.format("%d applicable %s", rules.size(), rules.size() > 1 ? "rules" : "rule"));
    }
    
    public void selectRuleByName( String name ) {
        jcmbApplicableRule.setSelectedItem(name);
    }
}
