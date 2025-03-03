package burp_injector.ui;

import burp_injector.util.MontoyaUtil;

import javax.swing.*;
import java.awt.*;

public class JfrmTestRunnerOutput extends JFrame {
    public JButton btnCancel = new JButton("Cancel");
    public JLabel lblProgressMessage = new JLabel("",SwingConstants.CENTER);
    public JProgressBar jProgressBarTestStatus = new JProgressBar();

    public JfrmTestRunnerOutput() {
        initComponents();
        initLayout();
        setLocationRelativeTo(MontoyaUtil.getInstance().getApi().userInterface().swingUtils().suiteFrame());
    }

    private void initComponents() {
        setResizable(false);
        setVisible(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void initLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        gbc.insets = new Insets(2,0,2,0);
        add(lblProgressMessage,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        gbc.insets = new Insets(2,2,2,2);
        add(jProgressBarTestStatus,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(2,0,2,0);
        add(btnCancel,gbc);
        lblProgressMessage.setPreferredSize(new Dimension(500, 20));
        pack();
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width/2)-this.getWidth()/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-this.getHeight()/2;
        setLocation(x,y);
    }

}
