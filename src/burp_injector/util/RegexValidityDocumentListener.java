package burp_injector.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * An event listener that highlights invalid regexes in jtextfields
 */
public class RegexValidityDocumentListener implements DocumentListener {
    private JTextField jTextField;
    private Color defaultBackground;
    private Color defaultForeground;
    public RegexValidityDocumentListener(JTextField jTextField, Color defaultBackground, Color defaultForeground ) {
        this.jTextField = jTextField;
        this.defaultBackground = defaultBackground;
        this.defaultForeground = defaultForeground;
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        updateRegexValidityState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateRegexValidityState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateRegexValidityState();
    }

    public void updateRegexValidityState() {
        // Valid
        if (RegexUtil.validateRegex(jTextField.getText())) {
            jTextField.setBackground(defaultBackground);
            jTextField.setForeground(defaultForeground);
        }
        // Invalid
        else {
            jTextField.setBackground(new Color(207, 37, 37));
            jTextField.setForeground(Color.BLACK);
        }
    }
}
