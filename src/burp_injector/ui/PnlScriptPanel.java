package burp_injector.ui;
import javax.swing.*;
import java.awt.*;
/**
 *  The script pane within the tabbed pane at the top right
 */
public class PnlScriptPanel extends JPanel {
    public final JTextArea script = new JTextArea();
    public PnlScriptPanel() {
        initComponents();
        initLayout();
    }

    private void initComponents() {
        script.setTabSize(1);
    }

    @Override
    public void setEnabled( boolean enabled ) {
        script.setEnabled(enabled);
    }

    private void initLayout() {
        JScrollPane jScrollPane = new JScrollPane(script);
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
    }

}
