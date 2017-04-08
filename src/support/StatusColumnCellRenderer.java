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
public class StatusColumnCellRenderer extends DefaultTableCellRenderer {

    private Library lb = new Library();
    private int column = 0;
    private int ChangeColumn = 0;
    private int mode;

    public StatusColumnCellRenderer(int column, int ChangeColumn, int mode) {
        this.column = column;
        this.ChangeColumn = ChangeColumn;
        this.mode = mode;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, ChangeColumn);
        //Get the status for the current row.
        TableModel tableModel = (TableModel) table.getModel();

        if (mode == 1) {
            String val = tableModel.getValueAt(row, column).toString();
            if (!val.equalsIgnoreCase("")) {
                l.setForeground(Color.white);
                val = tableModel.getValueAt(row, 5).toString();
                if (val.equalsIgnoreCase("")) {
                    l.setBackground(Color.RED);
                } else {
                    l.setBackground(Color.orange);
                    l.setForeground(Color.black);
                }
            } else {
                l.setBackground(Color.green);
                l.setForeground(Color.black);
            }
        } else if (mode == 2) {
            String val = tableModel.getValueAt(row, column).toString();
            if (!val.equalsIgnoreCase("")) {
                l.setBackground(Color.orange);
                l.setForeground(Color.black);
            } else {
                l.setBackground(Color.WHITE);
                l.setForeground(Color.BLACK);
            }
        }
        if (isSelected) {
            l.setBackground(Color.WHITE);
            l.setForeground(Color.BLACK);
        }

        //Return the JLabel which renders the cell.
        return l;
    }
}
