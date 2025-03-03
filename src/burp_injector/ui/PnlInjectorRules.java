package burp_injector.ui;

import burp_injector.enums.EditorState;
import burp_injector.model.RulesModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
/**
 * The injector rules panel that appears at the top left of the injector tab
 */
public class PnlInjectorRules extends JPanel {
    public final JButton jbtnNew = new JButton("New");
    public final JButton jbtnSave = new JButton("Save");
    public final JButton jbtnDelete = new JButton("Delete");
    public final JButton jbtnCancel = new JButton("Cancel");
    public final JButton jbtnTest = new JButton("Test");
    public JTable jtblInjectorRules;
    private RulesModel rulesModel;
    public PnlInjectorRules( RulesModel rulesModel ) {
        this.rulesModel = rulesModel;
        initComponents();
        initLayout();
    }

    private void initComponents() {
        jtblInjectorRules = new JTable(rulesModel.getInjectorRulesTableModel());
        int[] colWidths = { 0, 80,200 };
        for ( int i = 0; i < colWidths.length; i++ ) {
            jtblInjectorRules.getColumnModel().getColumn(i).setMinWidth(colWidths[i]);
            jtblInjectorRules.getColumnModel().getColumn(i).setMaxWidth(colWidths[i]);
        }
        jtblInjectorRules.setRowSelectionAllowed(true);
        jtblInjectorRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initLayout() {
        setBorder(new TitledBorder("Injection rules"));
        JScrollPane jScrollPaneTable = new JScrollPane(jtblInjectorRules);
        jScrollPaneTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPaneTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel pnlToolbar = new JPanel();
        pnlToolbar.setLayout(new GridBagLayout());

        int idx = 0;
        pnlToolbar.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlToolbar.add(jbtnNew,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlToolbar.add(jbtnSave,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);
        pnlToolbar.add(jbtnDelete,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,2,0,2);

        pnlToolbar.add(jbtnCancel,gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 2, 0, 2);
        pnlToolbar.add(jbtnTest, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,2,0,2);
        pnlToolbar.add(new JPanel(),gbc);

        setLayout(new GridBagLayout());
        int idy = 0;

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = idy++;
        add(pnlToolbar,gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = idy;
        gbc.insets = new Insets(2,2,2,2);
        add(jScrollPaneTable,gbc);

    }

    public void updateEditorButtonState(EditorState state) {
        switch (state) {
            case CREATE:
                jbtnNew.setEnabled(false);
                jbtnSave.setEnabled(true);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(true);
                jbtnTest.setEnabled(false);
                break;
            case EDIT:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(true);
                jbtnDelete.setEnabled(true);
                jbtnCancel.setEnabled(false);
                jbtnTest.setEnabled(true);
                break;
            case INITIAL:
                jbtnNew.setEnabled(true);
                jbtnSave.setEnabled(false);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(false);
                jbtnTest.setEnabled(false);
                break;
            case DISABLED:
                jbtnNew.setEnabled(false);
                jbtnSave.setEnabled(false);
                jbtnDelete.setEnabled(false);
                jbtnCancel.setEnabled(false);
                jbtnTest.setEnabled(false);
                break;
        }
    }
}
