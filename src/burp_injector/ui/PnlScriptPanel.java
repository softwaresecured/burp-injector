package burp_injector.ui;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
/**
 *  The script pane within the tabbed pane at the top right
 */
public class PnlScriptPanel extends JPanel {
    public RSyntaxTextArea script;
    public PnlScriptPanel() {
        initComponents();
        initLayout();
    }

    private void initComponents() {
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RTextAreaUI.inputMap", null);

        script = new RSyntaxTextArea();
        script.setEditable(true);
        script.setBackground(new JTextField().getBackground());
        script.setForeground(new JTextField().getForeground());
        script.setHighlightCurrentLine(false);
        script.setAntiAliasingEnabled(true);
        script.setFont(new JTextField().getFont());
        script.setAutoIndentEnabled(true);
        script.setCloseCurlyBraces(true);
        script.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
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
