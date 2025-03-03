package burp_injector.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The test HTTP request located at the bottom right
 */
public class PnlTestRequest extends JPanel {
    public final JTextArea testRequest = new JTextArea();
    public PnlTestRequest() {
        initComponents();
        initLayout();
    }

    private void initComponents() {
        testRequest.setTabSize(1);
        testRequest.setLineWrap(true);
        testRequest.setLineWrap(true);
    }

    private void initLayout() {
        setBorder(new TitledBorder("Sample HTTP request"));
        JScrollPane jScrollPane = new JScrollPane(testRequest);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(jScrollPane,gbc);
    }

}
