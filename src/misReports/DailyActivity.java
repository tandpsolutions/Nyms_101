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
import reportDAo.DailyAdvanceRefundDetailDAO;
import reportDAo.DailyOPDBillDAO;
import support.Library;
import support.ReportPanel;

/**
 *
 * @author Lenovo
 */
public class DailyActivity extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public DailyActivity(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        dtm = (DefaultTableModel) jTable1.getModel();
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
    }

    public void setData(String fromDate, String toDate) {
        jtxtFromDate.setText(fromDate);
        jtxtToDate.setText(toDate);
        rp.callView();
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
                    double opdCollection = 0.00;

                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT b1.bill_group_name,SUM(o1.final_amt) as amount FROM opdbillhd o LEFT JOIN opdbilldt o1 ON o.ref_no=o1.ref_no"
                            + " LEFT JOIN billitemmst b ON o1.bill_item_cd = b.bill_item_cd"
                            + " LEFT JOIN billgrpmst b1 ON b.bill_grp_cd=b1.bill_grp_cd "
                            + " where o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + " o.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and (o.ref_no not like 'PL%' OR o.ref_no not like 'PL%')"
                            + " GROUP BY b1.bill_group_name";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    Vector row = new Vector();
                    row.add("O.P.D Department");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    addDotedBlankRow();
                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add(rsLocal.getString("bill_group_name"));
                        row.add(lb.isNumber(rsLocal.getString("amount")));
                        opdCollection += lb.isNumber(rsLocal.getString("amount"));
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("1");
                        dtm.addRow(row);
                    }

                    sql = "SELECT sum(o.net_amt-o.disc_amt) AS due_amt FROM opdbillhd o LEFT JOIN patientmst p ON o.opd_no=p.opd_no "
                            + "WHERE v_date >= '" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' "
                            + " AND v_date <= '" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' AND (o.net_amt-o.disc_amt) <> 0"
                            + " and (o.ref_no not like 'PL%' OR o.ref_no not like 'PL%')";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add("Due Amount");
                        row.add(lb.isNumber(rsLocal.getString("due_amt")) * -1);
                        opdCollection -= lb.isNumber(rsLocal.getString("due_amt"));
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("8");
                        dtm.addRow(row);
                    }

                    sql = "SELECT SUM(p.cash_amt) AS cash,SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM opdbillhd o "
                            + " LEFT JOIN payment p ON o.ref_no=p.ref_no where o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and o.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and (o.ref_no not like 'PL%' OR o.ref_no not like 'PL%')";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    addBlankRow();
                    if (rsLocal.next()) {
                        row = new Vector();
                        row.add("Gross OPD Collection");
                        row.add(lb.Convert2DecFmtForRs(opdCollection));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        dtm.addRow(row);
                    }

                    addBlankRow();

                    opdCollection = 0;
                    sql = "SELECT b1.bill_group_name,SUM(o1.final_amt) as amount FROM opdbillhd o LEFT JOIN opdbilldt o1 ON o.ref_no=o1.ref_no"
                            + " LEFT JOIN billitemmst b ON o1.bill_item_cd = b.bill_item_cd"
                            + " LEFT JOIN billgrpmst b1 ON b.bill_grp_cd=b1.bill_grp_cd "
                            + " where o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + " o.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and o.ref_no like 'PL%' GROUP BY b1.bill_group_name";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    row = new Vector();
                    row.add("Pathology Department");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    addDotedBlankRow();
                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add(rsLocal.getString("bill_group_name"));
                        row.add(lb.isNumber(rsLocal.getString("amount")));
                        opdCollection += lb.isNumber(rsLocal.getString("amount"));
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("2");
                        dtm.addRow(row);
                    }

                    sql = "SELECT sum(o.net_amt-o.disc_amt) AS due_amt FROM opdbillhd o LEFT JOIN patientmst p ON o.opd_no=p.opd_no "
                            + " WHERE v_date >= '" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' "
                            + " and v_date <= '" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' AND (o.net_amt-o.disc_amt) <> 0"
                            + " and ref_no like 'PL%'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add("Due Amount");
                        row.add(lb.isNumber(rsLocal.getString("due_amt")) * -1);
                        opdCollection -= lb.isNumber(rsLocal.getString("due_amt"));
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("7");
                        dtm.addRow(row);
                    }

                    sql = "SELECT SUM(p.cash_amt) AS cash,SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM opdbillhd o "
                            + " LEFT JOIN payment p ON o.ref_no=p.ref_no where o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and o.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and o.ref_no like 'PL%'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    addBlankRow();
                    if (rsLocal.next()) {
                        row = new Vector();
                        row.add("Gross Pathology Collection");
                        row.add(lb.Convert2DecFmtForRs(opdCollection));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        dtm.addRow(row);
                    }

                    addBlankRow();

                    int rowIndex = jTable1.getRowCount();
                    sql = "SELECT CASE WHEN i.ref_no LIKE 'AR%' THEN 'ADVANCE' WHEN i.ref_no LIKE 'IP%' THEN 'RECEIPT' END AS part,SUM(amount) AS amt, "
                            + " SUM(p.cash_amt) AS cash , SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM ipdpaymenthd i "
                            + " LEFT JOIN payment p ON i.ref_no=p.ref_no where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' AND i.amount >0"
                            + " and i.ref_no like 'AR%'  GROUP BY SUBSTRING(i.ref_no,1,2)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();

                    addBlankRow();
                    row = new Vector();
                    row.add("Advance/Receipt Department");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    dtm.addRow(row);
                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add("          " + rsLocal.getString("part"));
                        row.add(lb.isNumber(rsLocal.getString("amt")));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        if (rsLocal.getString("part").equalsIgnoreCase("ADVANCE")) {
                            row.add("3");
                        } else {
                            row.add("4");
                        }
                        dtm.addRow(row);
                    }

                    sql = "SELECT CASE WHEN i.ref_no LIKE 'AR%' THEN 'ADVANCE' WHEN i.ref_no LIKE 'IP%' THEN 'DISCHARGE' END AS part,SUM(amount) AS amt, "
                            + " SUM(p.cash_amt) AS cash , SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM ipdpaymenthd i "
                            + " LEFT JOIN payment p ON i.ref_no=p.ref_no where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' AND i.amount >0 "
                            + " and i.ref_no like 'IP%' GROUP BY SUBSTRING(i.ref_no,1,2)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();

                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add("          " + rsLocal.getString("part"));
                        row.add(lb.isNumber(rsLocal.getString("amt")));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        if (rsLocal.getString("part").equalsIgnoreCase("ADVANCE")) {
                            row.add("3");
                        } else {
                            row.add("4");
                        }
                        dtm.addRow(row);
                    }

                    sql = "SELECT 'OPD Late Payment' AS part,SUM(amount) AS amt, "
                            + " SUM(p.cash_amt) AS cash , SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM opdpaymenthd i "
                            + " LEFT JOIN payment p ON i.ref_no=p.ref_no where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.ref_no like 'LP%'"
                            + " GROUP BY SUBSTRING(i.ref_no,1,2)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();

                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add("          " + rsLocal.getString("part"));
                        row.add(lb.isNumber(rsLocal.getString("amt")));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        row.add("6");
                        dtm.addRow(row);
                    }

                    sql = "SELECT CASE WHEN i.ref_no LIKE 'AR%' THEN 'REFUND' WHEN i.ref_no LIKE 'IP%' THEN 'REFUND' END AS part,SUM(amount) AS amt, "
                            + " SUM(p.cash_amt) AS cash , SUM(p.bank_amt) AS bank,SUM(p.card_amt) AS card FROM ipdpaymenthd i "
                            + " LEFT JOIN payment p ON i.ref_no=p.ref_no where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' AND i.amount <0 GROUP BY SUBSTRING(i.ref_no,1,2)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();

                    while (rsLocal.next()) {
                        row = new Vector();
                        row.add("          " + rsLocal.getString("part"));
                        row.add(lb.isNumber(rsLocal.getString("amt")));
                        row.add(lb.isNumber(rsLocal.getString("cash")));
                        row.add(lb.isNumber(rsLocal.getString("bank")));
                        row.add(lb.isNumber(rsLocal.getString("card")));
                        row.add("5");
                        dtm.addRow(row);
                    }
                    addBlankRow();

                    double amt = 0.00;
                    double cash = 0.00;
                    double bank = 0.00;
                    double card = 0.00;

                    addBlankRow();
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        if (jTable1.getValueAt(i, 5) != null) {
                            amt += lb.isNumber(jTable1.getValueAt(i, 1).toString());
                        }
                        cash += lb.isNumber(jTable1.getValueAt(i, 2).toString());
                        bank += lb.isNumber(jTable1.getValueAt(i, 3).toString());
                        card += lb.isNumber(jTable1.getValueAt(i, 4).toString());
                    }

                    row = new Vector();
                    row.add("Net Collection");
                    row.add(amt);
                    row.add(cash);
                    row.add(bank);
                    row.add(card);
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
//                    jTable1.getColumnModel().getColumn(0).setCellRenderer(new StatusColumnRowRenderer(0, 0, Color.GREEN, Color.BLACK));
//                    jTable1.getColumnModel().getColumn(0).setCellRenderer(new StatusColumnRowRenderer(8, 0, Color.GREEN, Color.BLACK));
//                    jTable1.getColumnModel().getColumn(0).setCellRenderer(new StatusColumnRowRenderer(15, 0, Color.GREEN, Color.BLACK));
//                    jTable1.getColumnModel().getColumn(0).setCellRenderer(new StatusColumnRowRenderer(21, 0, Color.RED, Color.BLACK));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {
                try {
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
//                    lb.reportGeneratorWord("DailyActivity.jasper", params, rows);.

                    rp.callView();
                    JDialog d = new JDialog();
                    d.setModal(true);
                    OPDBillDetail opd = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), "O.P.D.", false);
                    OPDBillDetail vaccine = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), "VACCINE", false);
                    OPDBillDetail patho = new OPDBillDetail(d, 2, jtxtFromDate.getText(), jtxtToDate.getText(), "PATHOLOGY", false);
                    AdvancedRefund advance = new AdvancedRefund(d, 3, jtxtFromDate.getText(), jtxtToDate.getText());
                    AdvancedRefund refund = new AdvancedRefund(d, 5, jtxtFromDate.getText(), jtxtToDate.getText());
                    AdvancedRefund discharge = new AdvancedRefund(d, 4, jtxtFromDate.getText(), jtxtToDate.getText());

                    HashMap params = new HashMap();
                    params.put("dir", System.getProperty("user.dir"));
                    params.put("opd_bill", opd.getData());
                    params.put("vaccine", vaccine.getData());
                    params.put("patho", patho.getData());
                    params.put("advance", advance.getData());
                    params.put("refund", refund.getData());
                    params.put("discharge", discharge.getData());
                    lb.reportGeneratorWord("DailyActivityCombine.jasper", params);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at DailtActivity Print", ex);
                }
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
                    row.add(jTable1.getValueAt(i, 4) + "");
                    rows.add(row);
                }

                ArrayList header = new ArrayList();
                header.add("Particular");
                header.add("Amount");
                header.add("Cash");
                header.add("Bank");
                header.add("Card");
                try {
                    lb.exportToExcel("Daily Activity", header, rows, "Daily Activity");
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
                try {
                    callView();
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
                    rp.callView();
                    JDialog d = new JDialog();
                    d.setModal(true);
                    OPDBillDetail opd = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), "O.P.D.", false);
                    OPDBillDetail vaccine = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), "VACCINE", false);
                    OPDBillDetail patho = new OPDBillDetail(d, 2, jtxtFromDate.getText(), jtxtToDate.getText(), "PATHOLOGY", false);
                    AdvancedRefund advance = new AdvancedRefund(d, 3, jtxtFromDate.getText(), jtxtToDate.getText());
                    AdvancedRefund refund = new AdvancedRefund(d, 5, jtxtFromDate.getText(), jtxtToDate.getText());
                    AdvancedRefund discharge = new AdvancedRefund(d, 4, jtxtFromDate.getText(), jtxtToDate.getText());

                    HashMap params = new HashMap();
                    params.put("dir", System.getProperty("user.dir"));
                    params.put("opd_bill", opd.getData());
                    params.put("vaccine", vaccine.getData());
                    params.put("patho", patho.getData());
                    params.put("advance", advance.getData());
                    params.put("refund", refund.getData());
                    params.put("discharge", discharge.getData());
                    lb.reportGenerator("DailyActivityCombine.jasper", params, jPanel3);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at DailtActivity Print", ex);
                }
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        jButton1.setText("Combine Report OPD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Combine Report IPD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
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
                "Particular", "Amount", "Cash", "Bank", "Card", "mode"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(5).setMinWidth(0);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
        }

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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                if (jTable1.getValueAt(row, 5) != null) {
                    if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("1")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        OPDBillDetail opd = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), jTable1.getValueAt(row, 0).toString(), true);
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("OPD bill List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("2")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        OPDBillDetail opd = new OPDBillDetail(d, 2, jtxtFromDate.getText(), jtxtToDate.getText(), jTable1.getValueAt(row, 0).toString(), true);
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("Pathology bill List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("3")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        AdvancedRefund opd = new AdvancedRefund(d, 3, jtxtFromDate.getText(), jtxtToDate.getText());
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("Advanced List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("4")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        AdvancedRefund opd = new AdvancedRefund(d, 4, jtxtFromDate.getText(), jtxtToDate.getText());
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("Refund List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("5")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        AdvancedRefund opd = new AdvancedRefund(d, 5, jtxtFromDate.getText(), jtxtToDate.getText());
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("Receipt List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("8")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        OPDDueAmountDetail opd = new OPDDueAmountDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText());
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("OPD Due bill List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("7")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        OPDDueAmountDetail opd = new OPDDueAmountDetail(d, 2, jtxtFromDate.getText(), jtxtToDate.getText());
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("Pathology Due bill List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    } else if (jTable1.getValueAt(row, 5).toString().equalsIgnoreCase("6")) {
                        JDialog d = new JDialog();
                        d.setModal(true);
                        LatePaymentDetail opd = new LatePaymentDetail(d, jtxtFromDate.getText(), jtxtToDate.getText());
                        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        d.setTitle("Pathology Due bill List");
                        d.add(opd);
                        d.setPreferredSize(new Dimension(opd.getWidth() + 20, opd.getHeight()));
                        d.setLocationRelativeTo(this);
                        d.setAlwaysOnTop(true);
                        opd.setVisible(true);
                        d.pack();
                        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
                        d.setVisible(true);
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        rp.callView();
        JDialog d = new JDialog();
        d.setModal(true);
        OPDBillDetail opd = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), "O.P.D.", false);
        OPDBillDetail vaccine = new OPDBillDetail(d, 1, jtxtFromDate.getText(), jtxtToDate.getText(), "VACCINE", false);
        OPDBillDetail patho = new OPDBillDetail(d, 2, jtxtFromDate.getText(), jtxtToDate.getText(), "PATHOLOGY", false);

        OPDBillDetail all = new OPDBillDetail(d);
        ArrayList<DailyOPDBillDAO> allRows = new ArrayList<>();
        allRows.addAll(opd.getDataArrayList());
        allRows.addAll(vaccine.getDataArrayList());
        allRows.addAll(patho.getDataArrayList());
        all.setData(allRows);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("OPD bill List");
        d.add(all);
        d.setPreferredSize(new Dimension(all.getWidth() + 20, all.getHeight()));
        d.setLocationRelativeTo(this);
        d.setAlwaysOnTop(true);
        all.setVisible(true);
        d.pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
        d.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        rp.callView();
        JDialog d = new JDialog();
        d.setModal(true);
        AdvancedRefund advance = new AdvancedRefund(d, 3, jtxtFromDate.getText(), jtxtToDate.getText());
        AdvancedRefund discharge = new AdvancedRefund(d, 4, jtxtFromDate.getText(), jtxtToDate.getText());
        AdvancedRefund refund = new AdvancedRefund(d, 5, jtxtFromDate.getText(), jtxtToDate.getText());

        AdvancedRefund all = new AdvancedRefund(d);
        ArrayList<DailyAdvanceRefundDetailDAO> allRows = new ArrayList<>();
        allRows.addAll(advance.getDataArrayList());
        allRows.addAll(discharge.getDataArrayList());
        allRows.addAll(refund.getDataArrayList());
        all.setData(allRows);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("OPD bill List");
        d.add(all);
        d.setPreferredSize(new Dimension(all.getWidth() + 20, all.getHeight()));
        d.setLocationRelativeTo(this);
        d.setAlwaysOnTop(true);
        all.setVisible(true);
        d.pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
        d.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
