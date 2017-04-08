/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMS101;
import hms.HMSHome;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import reportDAo.DailyActivityDAO;
import support.Library;
import support.ReportPanel;

/**
 *
 * @author Lenovo
 */
public class DailyActivitySummary extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public DailyActivitySummary(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        dtm = (DefaultTableModel) jTable1.getModel();
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
    }

    private void addBlankRow() {
        Vector row = new Vector();
        row.add("   ");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        dtm.addRow(row);
    }

    private void addDotedBlankRow() {
        Vector row = new Vector();
        row.add("------------");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        dtm.addRow(row);
    }

    private void addReportPanel() {
        class reportPanel extends ReportPanel {

            @Override
            public void callView() {
                try {

                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    dtm.setRowCount(0);

                    String sql = "SELECT o.v_date,SUM(p.cash_amt) AS cash,SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM opdbillhd o "
                            + " LEFT JOIN payment p ON o.ref_no=p.ref_no where o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and o.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and (o.ref_no like 'OP%' or o.ref_no like 'VO%' or o.ref_no like 'PL%') group by o.v_date";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    Vector row = new Vector();
                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        row.add(lb.isNumber(rsLocal.getString("cash")) + lb.isNumber(rsLocal.getString("bank")) + lb.isNumber(rsLocal.getString("card")));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        dtm.addRow(row);
                    }

                    int rowIndex = jTable1.getRowCount();
                    sql = "SELECT i.v_date,SUM(amount) AS amt, "
                            + " SUM(p.cash_amt) AS cash , SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM ipdpaymenthd i "
                            + " LEFT JOIN payment p ON i.ref_no=p.ref_no where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "'  GROUP BY i.v_date";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();

                    while (rsLocal.next()) {
                        boolean flag = false;
                        for (int i = 0; i < rowIndex; i++) {
                            if (jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(lb.ConvertDateFormetForDisply(rsLocal.getString("V_DATE")))) {
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 1).toString()) + lb.isNumber(rsLocal.getString("cash")) + lb.isNumber(rsLocal.getString("bank")) + lb.isNumber(rsLocal.getString("card")), i, 1);
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 2).toString()) + lb.isNumber(rsLocal.getString("cash")), i, 2);
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 3).toString()) + lb.isNumber(rsLocal.getString("bank")), i, 3);
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 4).toString()) + lb.isNumber(rsLocal.getString("card")), i, 4);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            row = new Vector();
                            row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                            row.add(lb.isNumber(rsLocal.getString("cash")) + lb.isNumber(rsLocal.getString("bank")) + lb.isNumber(rsLocal.getString("card")));
                            row.add(lb.isNumber(rsLocal.getString("cash")));
                            row.add(lb.isNumber(rsLocal.getString("bank")));
                            row.add(lb.isNumber(rsLocal.getString("card")));
                            dtm.addRow(row);
                        }
                    }

                    sql = "SELECT i.v_date, "
                            + " SUM(p.cash_amt) AS cash , SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM opdpaymenthd i "
                            + " LEFT JOIN payment p ON i.ref_no=p.ref_no where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.ref_no like 'LP%'"
                            + " GROUP BY i.v_date";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();

                    while (rsLocal.next()) {
                        boolean flag = false;
                        for (int i = 0; i < rowIndex; i++) {
                            if (jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(lb.ConvertDateFormetForDisply(rsLocal.getString("V_DATE")))) {
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 1).toString()) + lb.isNumber(rsLocal.getString("cash")) + lb.isNumber(rsLocal.getString("bank")) + lb.isNumber(rsLocal.getString("card")), i, 1);
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 2).toString()) + lb.isNumber(rsLocal.getString("cash")), i, 2);
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 3).toString()) + lb.isNumber(rsLocal.getString("bank")), i, 3);
                                jTable1.setValueAt(lb.isNumber(jTable1.getValueAt(i, 4).toString()) + lb.isNumber(rsLocal.getString("card")), i, 4);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            row = new Vector();
                            row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                            row.add(lb.isNumber(rsLocal.getString("cash")) + lb.isNumber(rsLocal.getString("bank")) + lb.isNumber(rsLocal.getString("card")));
                            row.add(lb.isNumber(rsLocal.getString("cash")));
                            row.add(lb.isNumber(rsLocal.getString("bank")));
                            row.add(lb.isNumber(rsLocal.getString("card")));
                            dtm.addRow(row);
                        }
                    }

                    double cash = 0.00;
                    double bank = 0.00;
                    double card = 0.00;

                    addBlankRow();
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        cash += lb.isNumber(jTable1.getValueAt(i, 1).toString());
                        bank += lb.isNumber(jTable1.getValueAt(i, 2).toString());
                        card += lb.isNumber(jTable1.getValueAt(i, 3).toString());
                    }

                    row = new Vector();
                    row.add("Net Collection");
                    row.add(cash);
                    row.add(bank);
                    row.add(card);
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {

            }

            @Override
            public void callExcel() {
                callView();
                ArrayList rows = new ArrayList();
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    ArrayList row = new ArrayList();
                    row.add(jTable1.getValueAt(i, 0) + "");
                    row.add(jTable1.getValueAt(i, 1) + "");
                    row.add(jTable1.getValueAt(i, 2) + "");
                    row.add(jTable1.getValueAt(i, 3) + "");
                    rows.add(row);
                }

                ArrayList header = new ArrayList();
                header.add("Particular");
                header.add("Cash");
                header.add("Bank");
                header.add("Card");
                try {
                    lb.exportToExcel("Daily Activity Summary", header, rows, "Daily Activity Summary");
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at excel in DailyActivity", ex);
                }
            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void callPreview() {
//                try {
//                    callView();
//                    ArrayList<DailyActivityDAO> rows = new ArrayList<DailyActivityDAO>();
//                    for (int i = 0; i < jTable1.getRowCount(); i++) {
//                        DailyActivityDAO row = new DailyActivityDAO();
//                        row.setParticular(jTable1.getValueAt(i, 0) + "");
//                        row.setAmount(jTable1.getValueAt(i, 1) + "");
//                        row.setCash(jTable1.getValueAt(i, 2) + "");
//                        row.setBank(jTable1.getValueAt(i, 3) + "");
//                        row.setCard(jTable1.getValueAt(i, 4) + "");
//                        rows.add(row);
//                    }
//                    HashMap params = new HashMap();
//                    params.put("fromDate", jtxtFromDate.getText());
//                    params.put("toDate", jtxtToDate.getText());
//                    lb.reportGenerator("DailyActivity.jasper", params, rows, jPanel3);
//                } catch (Exception ex) {
//                    lb.printToLogFile("Exception at DailtActivity Print", ex);
//                }
            }

        }

        rp = new reportPanel();

        jPanel1.add(rp);

        rp.setVisible(
                true);
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

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtToDate = new com.toedter.calendar.JDateChooser();
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(265, 111));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Total Collection", "Cash", "Bank", "Card"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false
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

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                DailyActivity da = new DailyActivity(31);
                da.setData(jTable1.getValueAt(row, 0).toString(), jTable1.getValueAt(row, 0).toString());
                HMSHome.addOnScreen(da, "Daily Activity", 31);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private com.toedter.calendar.JDateChooser jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
