/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMS101;
import hms.HMSHome;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.swing.JRViewer;
import support.Library;
import support.ReportPanel;
import transaction.IPDDayWiseBilling;
import utility.VoucherDisplay;

/**
 *
 * @author Lenovo
 */
public class EstimateReport extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    int form_cd = -1;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    /**
     * Creates new form OPDPatientListDateWise
     */
    public EstimateReport(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        addJlabelTotalAmt();
        dtm = (DefaultTableModel) jTable1.getModel();
        addReportPanel();
        searchOnTextFields();
    }

    private void addJlabelTotalAmt() {
        jPanel5.removeAll();
        jlblTotBill.setVisible(false);
        jlblTotCard.setVisible(false);
        jlblTotCash.setVisible(false);
        jlblTotCheque.setVisible(false);

        jlblTotBill.setBounds(0, 0, 20, 20);
        jlblTotBill.setVisible(true);
        jPanel5.add(jlblTotBill);

        jlblTotCard.setBounds(0, 0, 20, 20);
        jlblTotCard.setVisible(true);
        jPanel5.add(jlblTotCard);

        jlblTotCash.setBounds(0, 0, 20, 20);
        jlblTotCash.setVisible(true);
        jPanel5.add(jlblTotCash);

        jlblTotCheque.setBounds(0, 0, 20, 20);
        jlblTotCheque.setVisible(true);
        jPanel5.add(jlblTotCheque);

        setTable();
    }

    private void searchOnTextFields() {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jPanel2.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel2.add(jtfFilter, BorderLayout.CENTER);

//        setLayout(new BorderLayout());
//        add(panel, BorderLayout.SOUTH);
//        add(new JScrollPane(jTable1), BorderLayout.CENTER);
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

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, null, jlblTotBill, jlblTotCash, jlblTotCheque, jlblTotCard, null});
    }

    private void setTotal() {
        double bill_amt = 0.00;
        double advance = 0.00;
        double refund = 0.00;
        double remaining = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            bill_amt += lb.isNumber(jTable1.getValueAt(i, 6).toString());
            advance += lb.isNumber(jTable1.getValueAt(i, 7).toString());
            refund += lb.isNumber(jTable1.getValueAt(i, 8).toString());
            remaining += lb.isNumber(jTable1.getValueAt(i, 9).toString());
        }
        jlblTotBill.setText(lb.Convert2DecFmtForRs(bill_amt));
        jlblTotCash.setText(lb.Convert2DecFmtForRs(advance));
        jlblTotCheque.setText(lb.Convert2DecFmtForRs(refund));
        jlblTotCard.setText(lb.Convert2DecFmtForRs(remaining));
    }

    private void addReportPanel() {
        class reportPanel extends ReportPanel {

            @Override
            public void callView() {
                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT i.ipd_no,p.pt_name,i.admit_date,i.admit_time,(SELECT SUM(amt) FROM ipdbilldt WHERE ipd_no=i.ipd_no AND is_del=0) as tot_bill,(SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
                            + " AND amount>0) AS advance ,"
                            + " (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) AS refund,r.room_cd,w.ward_name FROM ipdreg i"
                            + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                            + " LEFT JOIN roommst r ON i.opd_no=r.opd_no LEFT JOIN wardmst w ON w.ward_cd=r.ward_cd"
                            + " where i.dis_date IS NULL group by i.ipd_no ";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        row.add(dtm.getRowCount()+1);
                        row.add(rsLocal.getString("pt_name"));
                        row.add(rsLocal.getString("ward_name"));
                        row.add(rsLocal.getString("room_Cd"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("admit_date")));
                        row.add(rsLocal.getString("admit_time"));
                        row.add(lb.isNumber(rsLocal.getString("tot_bill")));
                        row.add(lb.isNumber(rsLocal.getString("advance")));
                        row.add(lb.isNumber(rsLocal.getString("refund")));
                        row.add(lb.isNumber(rsLocal.getString("tot_bill")) - lb.isNumber(rsLocal.getString("advance")) + lb.isNumber(rsLocal.getString("refund")));
                        row.add(rsLocal.getString("ipd_no"));
                        dtm.addRow(row);
                    }

                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
                    setTotal();
                } catch (Exception ex) {
                    dtm.setRowCount(0);
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {
                try {
                    jPanel3.removeAll();
                    String sql = "SELECT p.pt_name,i.admit_date,i.admit_time,case when (SELECT SUM(amt) FROM ipdbilldt "
                            + " WHERE ipd_no=i.ipd_no AND is_del=0) is null then 0 else"
                            + " (SELECT SUM(amt) FROM ipdbilldt WHERE ipd_no=i.ipd_no AND is_del=0) end as tot_bill,"
                            + " case when (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
                            + " AND amount>0) is null then 0 else (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
                            + " AND amount>0) end AS advance ,"
                            + " case when (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) is null then 0 else "
                            + " (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) end AS refund,r.room_cd,w.ward_name,"
                            + " b1.BRANCH_NAME, b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO FROM ipdreg i"
                            + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                            + " LEFT JOIN roommst r ON i.opd_no=r.opd_no LEFT JOIN wardmst w ON w.ward_cd=r.ward_cd"
                            + " LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1 where i.dis_date IS NULL group by i.ipd_no ";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    lb.reportGeneratorWord("EstimateReport.jasper", null, rsLocal);
                } catch (Exception ex) {
                    dtm.setRowCount(0);
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callExcel() {
                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT i.ipd_no,p.pt_name,i.admit_date,i.admit_time,(SELECT SUM(amt) FROM ipdbilldt WHERE ipd_no=i.ipd_no AND is_del=0) as tot_bill,(SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
                            + " AND amount>0) AS advance ,"
                            + " (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) AS refund,r.room_cd,w.ward_name FROM ipdreg i"
                            + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                            + " LEFT JOIN roommst r ON i.opd_no=r.opd_no LEFT JOIN wardmst w ON w.ward_cd=r.ward_cd"
                            + " where i.dis_date IS NULL group by i.ipd_no ";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    ArrayList rows = new ArrayList();
                    while (rsLocal.next()) {
                        ArrayList row = new ArrayList();
                        row.add(rsLocal.getString("pt_name"));
                        row.add(rsLocal.getString("ward_name"));
                        row.add(rsLocal.getString("room_Cd"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("admit_date")));
                        row.add(rsLocal.getString("admit_time"));
                        row.add(lb.isNumber(rsLocal.getString("tot_bill")) + "");
                        row.add(lb.isNumber(rsLocal.getString("advance")) + "");
                        row.add(lb.isNumber(rsLocal.getString("refund")) + "");
                        row.add((lb.isNumber(rsLocal.getString("tot_bill")) - lb.isNumber(rsLocal.getString("advance")) + lb.isNumber(rsLocal.getString("refund"))) + "");
                        row.add(rsLocal.getString("ipd_no"));
                        rows.add(row);
                    }

                    ArrayList header = new ArrayList();
                    header.add("Patient Name");
                    header.add("Ward Name");
                    header.add("Bed Number");
                    header.add("Admition Date");
                    header.add("Admition Time");
                    header.add("Total Bill");
                    header.add("Advance");
                    header.add("Refund");
                    header.add("Due Amount");
                    header.add("IPD No");

                    lb.exportToExcel("Estimate Report", header, rows, "Estimate Report");

                } catch (Exception ex) {
                    dtm.setRowCount(0);
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void callPreview() {
                try {
                    jPanel3.removeAll();
                    String sql = "SELECT p.pt_name,i.admit_date,i.admit_time,case when (SELECT SUM(amt) FROM ipdbilldt "
                            + " WHERE ipd_no=i.ipd_no AND is_del=0) is null then 0 else"
                            + " (SELECT SUM(amt) FROM ipdbilldt WHERE ipd_no=i.ipd_no AND is_del=0) end as tot_bill,"
                            + " case when (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
                            + " AND amount>0) is null then 0 else (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
                            + " AND amount>0) end AS advance ,"
                            + " case when (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) is null then 0 else "
                            + " (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) end AS refund,r.room_cd,w.ward_name,"
                            + " b1.BRANCH_NAME, b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO FROM ipdreg i"
                            + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                            + " LEFT JOIN roommst r ON i.opd_no=r.opd_no LEFT JOIN wardmst w ON w.ward_cd=r.ward_cd"
                            + " LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1 where i.dis_date IS NULL group by i.ipd_no ";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    lb.reportGenerator("EstimateReport.jasper", null, rsLocal, jPanel3);
                } catch (Exception ex) {
                    dtm.setRowCount(0);
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

        }

        rp = new reportPanel();
        jPanel1.add(rp);
        rp.setVisible(true);
    }

    private void close() {
        this.dispose();
    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jlblTotBill = new javax.swing.JLabel();
        jlblTotCash = new javax.swing.JLabel();
        jlblTotCard = new javax.swing.JLabel();
        jlblTotCheque = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(265, 111));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Name", "Ward", "Bed No", "D.O.A", "T.O.A", "Total Bill", "Advance", "Refund", "Balance", "IPD No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(10).setMinWidth(0);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(10).setMaxWidth(0);
        }

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jlblTotBill.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblTotCash.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblTotCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jlblTotCard.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblTotCardComponentMoved(evt);
            }
        });

        jlblTotCheque.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jlblTotCard, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblTotBill, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblTotCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 267, Short.MAX_VALUE)
                .addComponent(jlblTotCash, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTotCash, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotBill, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotCard, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Bill Preview");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Bill Print");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jlblTotCardComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblTotCardComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jlblTotCardComponentMoved

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                IPDDayWiseBilling ipd = new IPDDayWiseBilling(null, true, jTable1.getValueAt(row, 9).toString());
                ipd.setLocationRelativeTo(null);
                ipd.show();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            VoucherDisplay vd = new VoucherDisplay(jTable1.getValueAt(row, 9).toString(), "Intrim");
            jPanel3.removeAll();
            JRViewer jrViewer = new JRViewer(vd.print);
            ((JPanel) jrViewer.getComponent(0)).remove(1);
            jrViewer.setSize(jPanel3.getWidth(), jPanel3.getHeight());
            jPanel3.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(jPanel3);
            jPanel3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            VoucherDisplay vd = new VoucherDisplay(jTable1.getValueAt(row, 9).toString(), "Intrim");
            vd.generateDischargeIntrim(jTable1.getValueAt(row, 9).toString(), "", 1);
        }
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jlblTotBill;
    private javax.swing.JLabel jlblTotCard;
    private javax.swing.JLabel jlblTotCash;
    private javax.swing.JLabel jlblTotCheque;
    // End of variables declaration//GEN-END:variables
}
