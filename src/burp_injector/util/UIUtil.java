package burp_injector.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User interface utility functions
 */
public class UIUtil {
    /*
        Table stuff
     */

    /**
     * Select the next available table row for a given previous row. Used when deleting.
     * @param table The jtable
     * @param prevRowNum The previous row number which no longer exists
     */
    public static void selectNextAvailableRow(JTable table, int prevRowNum ) {
        if ( prevRowNum >= table.getRowCount()) {
            prevRowNum -= 1;
        }
        if ( prevRowNum >= 0 && table.getRowCount() > 0 ) {
            table.getSelectionModel().setSelectionInterval(prevRowNum,prevRowNum);
        }
    }

    /**
     * The first column in tables used by Injector contain a unique ID value. This function selects a row by that ID.
     * @param table The jtable
     * @param id The id of the row to select
     */
    public static void selectTableRowById( JTable table, String id ) {
        int row = getTableRowNumberById(table,id);
        if ( row >= 0 ) {
            table.setRowSelectionInterval(row,row);
        }
        else {
            table.clearSelection();
        }
    }

    /**
     * Returns a table row where the first column matches an id value
     * @param table The jtable
     * @param id The id to match
     * @return the row number
     */
    public static int getTableRowNumberById( JTable table, String id ) {
        int rowNum = -1;
        for ( int i = 0; i < table.getRowCount(); i++ ) {
            String curId = (String) table.getValueAt(i,0);
            if (curId.equals(id)) {
                rowNum = i;
                break;
            }
        }
        return rowNum;
    }

    /**
     * Returns the row number for a given ID from a default table model
     * @param model The default table model
     * @param id The id to match
     * @return The row number
     */
    public static int getRowNumberById(DefaultTableModel model, String id) {
        for ( int i = 0; i < model.getRowCount(); i++ ) {
            String curId = (String) model.getValueAt(i,0);
            if ( curId.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns an ID value for a given row number
     * @param model The default table model
     * @param rowNumber The row number
     * @return The id value at the given row number
     */
    public static String getIdByRowNumber(DefaultTableModel model, int rowNumber) {
        if ( rowNumber < model.getRowCount() ) {
            return (String) model.getValueAt(rowNumber,0);
        }
        return null;
    }

    /*
        Highlighting stuff
     */

    /**
     * Returns a colour for highlighting text in jtextarea fields
     * @param component
     * @return
     */
    public static Color getHighlightColour(Component component ) {
        return recalibrateColour(component,Color.BLACK);
    }

    public static Color recalibrateColour(Component component, Color color) {
        if ( component.getForeground().getRed() < 100 && component.getForeground().getGreen() < 100 && component.getForeground().getBlue() < 100 ) {
            color = new Color(255-color.getRed(),255-color.getGreen()/2,255-color.getBlue()/2);
        }
        return color;
    }

    /**
     * Highlights a given area of text in a jtextarea
     * @param regex The regex defining the area to highlight
     * @param captureGroup The capture group for the regex
     * @param jTextArea The jtextarea to highlight
     * @param highlightColour The colour of the highlighting
     */
    public static void updateHighlighting(String regex, int captureGroup, JTextArea jTextArea, Color highlightColour ) {
        try {
            if ( regex!= null ) {
                if (RegexUtil.validateRegex(regex)) {
                    if ( captureGroup <= RegexUtil.getMatchGroupCount(regex)) {
                        Pattern p = Pattern.compile(regex,Pattern.DOTALL|Pattern.MULTILINE);
                        Matcher m = p.matcher(jTextArea.getText());
                        while ( m.find() ) {
                            int start = m.start(captureGroup);
                            int end = m.end(captureGroup);
                            if ( highlightColour == null ) {
                                highlightColour = UIUtil.getHighlightColour(jTextArea);
                            }
                            jTextArea.getHighlighter().addHighlight(start,end,new DefaultHighlighter.DefaultHighlightPainter(recalibrateColour(jTextArea, highlightColour)));
                        }
                    }
                }
            }
        } catch ( Exception e ) {
            Logger.log("ERROR", String.format("Exception: %s", e.getMessage()));
        }
    }

    /**
     * Wrapper around updateHighlighting but using the default colour
     * @param regex
     * @param captureGroup
     * @param jTextArea
     */
    public static void updateHighlighting(String regex, int captureGroup, JTextArea jTextArea) {
        updateHighlighting(regex, captureGroup, jTextArea, null );
    }

    public static boolean comboboxContains( JComboBox cmb, String value ) {
        for ( int i = 0; i < cmb.getItemCount(); i++ ) {
            if ( cmb.getItemAt(i).equals(value)) {
                return true;
            }
        }
        return false;
    }
}
