/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author nice
 */
public class StatusColumnRowRenderer extends DefaultTableCellRenderer {

    private Library lb = new Library();
    private int ChangeColumn = 0;
    private Color bgcolor = null;
    private Color forcolor = null;
    private int changeRow = -1;

    public StatusColumnRowRenderer(int row, int ChangeColumn, Color bgColor, Color forColor) {
        this.bgcolor = bgColor;
        this.forcolor = forColor;
        this.changeRow = row;
        this.ChangeColumn = ChangeColumn;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        //Get the status for the current row.
        if (row == changeRow && col == ChangeColumn) {
            l.setBackground(bgcolor);
            l.setForeground(forcolor);
        } else {
            l.setBackground(Color.WHITE);
            l.setForeground(Color.BLACK);
        }

        //Return the JLabel which renders the cell.
        return l;
    }
}
