package burp_injector.ui;

import burp_injector.util.MontoyaUtil;

import javax.swing.*;
import java.awt.*;

public class DebugEditorDialog extends JDialog {
    private JTextArea jtxtDebugConfigParams = new JTextArea();
    private JButton jbtnApply = new JButton("Apply");
    public DebugEditorDialog() {
        initComponents();
        initLayout();
        initListeners();
    }

    private void initLayout() {
        setTitle("Debug Configuration");
        JScrollPane jScrollPane = new JScrollPane(jtxtDebugConfigParams);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        add(jScrollPane,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0,2,0,2);
        add(jbtnApply,gbc);
    }

    private void initComponents() {
        setModal(true);
        setSize(400,300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width/2)-this.getWidth()/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-this.getHeight()/2;
        setLocation(x,y);
        setVisible(false);
        setLocationRelativeTo(MontoyaUtil.getInstance().getApi().userInterface().swingUtils().suiteFrame());
    }

    private void initListeners() {
        jbtnApply.addActionListener(e -> {
            setVisible(false);
        });
    }

    public String getDebugConfigParams() {
        return jtxtDebugConfigParams.getText();
    }


}
