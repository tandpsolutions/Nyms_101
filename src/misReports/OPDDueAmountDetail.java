/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMSHome;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import reportDAo.DailyOPDBillDAO;
import support.Library;
import transaction.OPDBillGeneration;

/**
 *
 * @author Bhaumik
 */
public class OPDDueAmountDetail extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    JDialog jd = null;
    String ref_no = "";
    private JTextField jtfFilter = new JTextField();
    private TableRowSorter<TableModel> rowSorter;

    /**
     * Creates new form SearchPatient
     */
    public OPDDueAmountDetail(JDialog jd, int mode, String from, String to) {
        initComponents();
        this.jd = jd;
        dtm = (DefaultTableModel) jTable2.getModel();
        updateIPDList(mode, from, to);
        searchOnTextFieldsOPD();
    }

    private void addTotal(String voucher, int index) {

        double bill_amt = 0.00;
        for (int i = index; i < jTable2.getRowCount(); i++) {
            bill_amt += lb.isNumber(jTable2.getValueAt(i, 4).toString());
        }

        Vector row = new Vector();
        row.add("");
        row.add("");
        row.add("");
        row.add("Total of " + voucher);
        row.add(lb.Convert2DecFmtForRs(bill_amt));
        row.add("");
        row.add("");
        dtm.addRow(row);

    }

    private void searchOnTextFieldsOPD() {
        this.rowSorter = new TableRowSorter<>(jTable2.getModel());
        jTable2.setRowSorter(rowSorter);
        jPanel1.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel1.add(jtfFilter, BorderLayout.CENTER);
        JButton excel = new JButton("Excel");
        JButton Print = new JButton("Print");
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(excel);
        panel.add(Print);
        jPanel1.add(panel, BorderLayout.EAST);
        Print.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OPDDueAmountDetail.this.dispose();
                ArrayList<DailyOPDBillDAO> rows = new ArrayList<DailyOPDBillDAO>();
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    DailyOPDBillDAO row = new DailyOPDBillDAO();
                    row.setSr_no(jTable2.getValueAt(i, 0) + "");
                    row.setRef_no(jTable2.getValueAt(i, 1) + "");
                    row.setV_date(jTable2.getValueAt(i, 2) + "");
                    row.setPt_name(jTable2.getValueAt(i, 3) + "");
                    row.setTot_amt(jTable2.getValueAt(i, 4) + "");
                    row.setUser(jTable2.getValueAt(i, 5) + "");
                    rows.add(row);
                }
                HashMap params = new HashMap();
                params.put("title", "OPD Due Payment");
                lb.reportGeneratorWord("DailyOPDDueBillDetail.jasper", params, rows);
            }
        });

        excel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OPDDueAmountDetail.this.dispose();
                ArrayList rows = new ArrayList();
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    ArrayList row = new ArrayList();
                    row.add(jTable2.getValueAt(i, 0).toString());
                    row.add(jTable2.getValueAt(i, 1).toString());
                    row.add(jTable2.getValueAt(i, 2).toString());
                    row.add(jTable2.getValueAt(i, 3).toString());
                    row.add(jTable2.getValueAt(i, 4).toString());
                    row.add(jTable2.getValueAt(i, 5).toString());
                    rows.add(row);
                }

                ArrayList header = new ArrayList();
                header.add("SR.No");
                header.add("Voucher NO");
                header.add("Voucher Date");
                header.add("Patient Name");
                header.add("Amount");
                header.add("Entry BY");
                try {
                    lb.exportToExcel("Late Payment Detail", header, rows, "Late Payment Detail");
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at call excel", ex);
                }
            }

        });
        jtfFilter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    private void updateIPDList(int mode, String from, String to) {
        try {
            String sql = "";
            sql = "SELECT o.ref_no,v_date,p.pt_name,(o.net_amt-o.disc_amt) AS disc_amt,l.user_name FROM opdbillhd o"
                    + " LEFT JOIN patientmst p ON o.opd_no=p.opd_no  LEFT JOIN login l ON o.user_id=l.user_id "
                    + " WHERE o.v_date >='" + lb.ConvertDateFormetForDB(from) + "' and "
                    + " o.v_date <='" + lb.ConvertDateFormetForDB(to) + "'  AND (o.net_amt-o.disc_amt) <> 0 ";
            if (mode == 1) {
                sql += " and o.ref_no LIKE 'OP%'";
            } else if (mode == 2) {
                sql += " and o.ref_no LIKE 'PL%'";
            }
            sql += " order by v_date,o.ref_no";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            int startIndex = 0;
            dtm.setRowCount(0);
            String old_ref_no = "";
            while (rsLcoal.next()) {
                if (!old_ref_no.equalsIgnoreCase("") && !old_ref_no.equalsIgnoreCase(rsLcoal.getString("ref_no"))) {
                    addBlankRow();
                    addTotal(old_ref_no, startIndex);
                    addBlankRow();
                    startIndex = jTable2.getRowCount() - 1;
                }
                Vector row = new Vector();
                row.add(i);
                row.add(rsLcoal.getString("ref_no"));
                row.add(lb.ConvertDateFormetForDisply(rsLcoal.getString("v_date")));
                row.add(rsLcoal.getString("pt_name"));
                row.add(rsLcoal.getString("disc_amt"));
                row.add(rsLcoal.getString("user_name"));
                dtm.addRow(row);
                old_ref_no = rsLcoal.getString("ref_no");
                i++;
            }
            addBlankRow();
            addTotal(old_ref_no, startIndex);
            setTotal();

            lb.setColumnSizeForTable(jTable2, jPanel2.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    @Override
    public void dispose() {
        jd.dispose();

    }

    private void addBlankRow() {
        Vector row = new Vector();
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        dtm.addRow(row);
    }

    private void setTotal() {
        double bill_amt = 0.00;
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            if (!jTable2.getValueAt(i, 1).toString().isEmpty()) {
                bill_amt += lb.isNumber(jTable2.getValueAt(i, 4).toString());
            }
        }

        addBlankRow();
        addBlankRow();
        Vector row = new Vector();
        row.add("");
        row.add("");
        row.add("");
        row.add("Total");
        row.add(lb.Convert2DecFmtForRs(bill_amt));
        row.add("");
        row.add("");
        dtm.addRow(row);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Voucher No", "Voucher Date", "Name", "Bill Amt", "User"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1110, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                ref_no = jTable2.getValueAt(row, 1).toString();
                this.dispose();
                OPDBillGeneration opd = new OPDBillGeneration(ref_no.substring(0, 2));
                opd.setID(ref_no);
                if (ref_no.substring(0, 2).equalsIgnoreCase("OP")) {
                    HMSHome.addOnScreen(opd, "OPD Bill Generation Book", 24);
                } else {
                    HMSHome.addOnScreen(opd, "Pathology Bill Generation Book", 211);
                }
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
