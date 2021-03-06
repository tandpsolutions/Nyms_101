/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import support.Library;

/**
 *
 * @author Bhaumik
 */
public class OPDDueAmtBill extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    JDialog jd = null;
    OPDLatePayment pm = null;

    /**
     * Creates new form SearchPatient
     */
    public OPDDueAmtBill(JDialog jd, OPDLatePayment pm) {
        initComponents();
        this.pm = pm;
        this.jd = jd;
        dtm = (DefaultTableModel) jTable2.getModel();
        updateIPDList();
    }

    private void updateIPDList() {
        try {
            String sql = "";
            sql = "SELECT o.ref_no,o.v_date,o.opd_no,p.pt_name,o.net_amt,o.disc_amt,"
                    + "(SELECT SUM(amount) FROM opdpaymenthd WHERE ref_no LIKE 'LP%' AND voucher_no=o.ref_no) AS late_pmt "
                    + " FROM opdbillhd o LEFT JOIN patientmst p ON o.opd_no=p.opd_no LEFT JOIN opdpaymenthd op ON o.ref_no=op.voucher_no "
                    + " WHERE o.net_amt-o.disc_amt >0  GROUP BY o.ref_no";

            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            dtm.setRowCount(0);
            while (rsLcoal.next()) {
                Vector row = new Vector();
                row.add(i);
                row.add(rsLcoal.getString("ref_no"));
                row.add(lb.ConvertDateFormetForDisply(rsLcoal.getString("v_date")));
                row.add(rsLcoal.getString("opd_no"));
                row.add(rsLcoal.getString("pt_name"));
                row.add(lb.Convert2DecFmtForRs(rsLcoal.getDouble("net_amt")));
                row.add(lb.Convert2DecFmtForRs(rsLcoal.getDouble("disc_amt")));
                row.add(lb.Convert2DecFmtForRs(lb.isNumber(rsLcoal.getString("late_pmt"))));
                row.add(lb.Convert2DecFmtForRs(rsLcoal.getDouble("net_amt")-rsLcoal.getDouble("disc_amt")-lb.isNumber(rsLcoal.getString("late_pmt"))));
                if((rsLcoal.getDouble("net_amt")-rsLcoal.getDouble("disc_amt")-lb.isNumber(rsLcoal.getString("late_pmt"))) != 0){
                    dtm.addRow(row);
                }
                i++;
            }
            lb.setColumnSizeForTable(jTable2, jPanel2.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    @Override
    public void dispose() {
        jd.dispose();

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

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Voucher No", "Date", "OPD No", "Name", "Bill Amt", "Paid Amount", "Late Payment", "Due Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true, false
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                String opd_no = jTable2.getValueAt(row, 1).toString();
                pm.jlblVoucherNo.setText(opd_no);
                opd_no = jTable2.getValueAt(row, 3).toString();
                pm.jlblOPDNumber.setText(opd_no);
                this.dispose();
                pm.setData();
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
