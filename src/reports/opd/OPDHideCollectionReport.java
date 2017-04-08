/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports.opd;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import support.JCheckBoxList;
import support.Library;
import support.ReportPanel;

/**
 *
 * @author Lenovo
 */
public class OPDHideCollectionReport extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    DefaultListModel<JCheckBox> model = null;
    JCheckBoxList checkBoxList = null;
    int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public OPDHideCollectionReport(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        addJlabelTotalAmt();
        dtm = (DefaultTableModel) jTable1.getModel();
        model = new DefaultListModel<JCheckBox>();
        checkBoxList = new JCheckBoxList(model);
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
        addUser();
        jCheckBox1.setSelected(true);
    }

    private void addUser() {
        try {
            String sql = "select user_name from login";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                model.add(rsLocal.getRow() - 1, new JCheckBox(rsLocal.getString("user_name")));
            }
            JScrollPane jsc = new JScrollPane(checkBoxList);
            jsc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            jsc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jPanel4.add(jsc);
            checkBoxList.setVisible(true);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at addUser in OPDCollectionReport", ex);
        }
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

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotBill, jlblTotCash, jlblTotCheque, jlblTotCard});
    }

    private void setTotal() {
        double bill_amt = 0.00;
        double cash_amt = 0.00;
        double chq_amt = 0.00;
        double card_amt = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            bill_amt += lb.isNumber(jTable1.getValueAt(i, 4).toString());
            cash_amt += lb.isNumber(jTable1.getValueAt(i, 5).toString());
            chq_amt += lb.isNumber(jTable1.getValueAt(i, 6).toString());
            card_amt += lb.isNumber(jTable1.getValueAt(i, 7).toString());
        }
        jlblTotBill.setText(lb.Convert2DecFmtForRs(bill_amt));
        jlblTotCash.setText(lb.Convert2DecFmtForRs(cash_amt));
        jlblTotCheque.setText(lb.Convert2DecFmtForRs(chq_amt));
        jlblTotCard.setText(lb.Convert2DecFmtForRs(card_amt));
    }

    private void addReportPanel() {
        class reportPanel extends ReportPanel {

            @Override
            public void callView() {
                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT o.rec_no,CASE WHEN o.ref_no IS NULL THEN '' ELSE o.ref_no END AS ref_no,o.v_date,p1.pt_name,p.cash_amt,"
                            + "  p.bank_amt,p.card_amt,o.net_amt FROM opdhidehd o LEFT JOIN paymenthide p ON o.rec_no=p.rec_no"
                            + "  LEFT JOIN patientmst p1 ON o.opd_no=p1.opd_no "
                            + "  WHERE o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  o.v_date  <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and o.ref_no is not null"
                            + "  and o.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        row.add(rsLocal.getString("rec_no"));
                        row.add(rsLocal.getString("ref_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        row.add(rsLocal.getString("pt_name"));
                        row.add(rsLocal.getDouble("net_amt"));
                        row.add(rsLocal.getDouble("cash_amt"));
                        row.add(rsLocal.getDouble("bank_amt"));
                        row.add(rsLocal.getDouble("card_amt"));
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
                    String sql = "SELECT CASE WHEN o.ref_no IS NULL THEN '' ELSE o.ref_no END AS ref_no,o.v_date,p1.pt_name,p.cash_amt,"
                            + "  p.bank_amt,p.card_amt,o.net_amt FROM opdhidehd o LEFT JOIN paymenthide p ON o.ref_no=p.ref_no"
                            + "  LEFT JOIN patientmst p1 ON o.opd_no=p1.opd_no "
                            + "  WHERE o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  o.v_date  <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and o.ref_no is not null"
                            + "  and o.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    lb.reportGeneratorWord("OPDCollectionReport.jasper", null, rsLocal);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at OPDCOllectionReport", ex);
                }
            }

            @Override
            public void callExcel() {
                callView();
                ArrayList rows = new ArrayList();
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    ArrayList row = new ArrayList();
                    row.add((i + 1) + "");
                    row.add(jTable1.getValueAt(i, 0).toString());
                    row.add(jTable1.getValueAt(i, 1).toString());
                    row.add(jTable1.getValueAt(i, 2).toString());
                    row.add(jTable1.getValueAt(i, 3).toString());
                    row.add(jTable1.getValueAt(i, 4).toString());
                    row.add(jTable1.getValueAt(i, 5).toString());
                    row.add(jTable1.getValueAt(i, 6).toString());
                    rows.add(row);
                }
                ArrayList header = new ArrayList();
                header.add("SR.No");
                header.add("Voucher #");
                header.add("Date");
                header.add("Particular");
                header.add("Amount");
                header.add("Cash");
                header.add("Bank");
                header.add("Card");
                try {
                    lb.exportToExcel("OPD Collection Report", header, rows, "OPD Collection Report");
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at excel in opd collection", ex);
                }
            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void callPreview() {
                try {
                    String sql = "SELECT CASE WHEN o.ref_no IS NULL THEN '' ELSE o.ref_no END AS ref_no,o.v_date,p1.pt_name,p.cash_amt,"
                            + "  p.bank_amt,p.card_amt,o.net_amt FROM opdhidehd o LEFT JOIN paymenthide p ON o.ref_no=p.ref_no"
                            + "  LEFT JOIN patientmst p1 ON o.opd_no=p1.opd_no "
                            + "  WHERE o.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  o.v_date  <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and o.ref_no is not null"
                            + "  and o.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    lb.reportGenerator("OPDCollectionReport.jasper", null, rsLocal, jPanel3);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at OPDCOllectionReport", ex);
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

    private void moveBackToOriginal(String rec_no, String new_ref_no) throws SQLException {
        lb.generateLog("opdhidehd", "opdbillhd", "ref_no", rec_no, new_ref_no);
        lb.generateLog("opdhidedt", "opdbilldt", "ref_no", rec_no, new_ref_no);
        lb.generateLog("paymenthide", "payment", "ref_no", rec_no, new_ref_no);

        String sql = "delete from opdhidehd where ref_no=?";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.setString(1, new_ref_no);
        pstLocal.executeUpdate();

        sql = "delete from opdhidedt where ref_no=?";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.setString(1, new_ref_no);
        pstLocal.executeUpdate();

        sql = "delete from paymenthide where ref_no=?";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.setString(1, new_ref_no);
        pstLocal.executeUpdate();
    }

    private String checkGapBetweenDates(String date) throws SQLException {
        String old_ref_no = "OP" + HMSHome.year + "/0000";
        String sql = "select ref_no from opdbillhd where ref_no like 'OP%' AND v_date >='" + date + "'and ref_no >'" + old_ref_no + "'"
                + " ORDER BY v_date,ref_no";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        String new_ref_no = "";
        String max_ref_no = lb.getData("select max(ref_no) from opdbillhd where ref_no like 'OP%'");
        while (rsLocal.next()) {
//            if (old_ref_no.equalsIgnoreCase(max_ref_no)) {
//                break;
//            }
            if (old_ref_no.equalsIgnoreCase("OP" + HMSHome.year + "/0000")) {
                old_ref_no = rsLocal.getString("ref_no");
                continue;
            }
            new_ref_no = rsLocal.getString("ref_no");
            String temp_ref_no = lb.generateKey(old_ref_no, 11, "OP" + HMSHome.year + "/");
            if (temp_ref_no.equalsIgnoreCase(new_ref_no)) {
                old_ref_no = new_ref_no;
            } else {
                break;
            }
        }
        if (!old_ref_no.equalsIgnoreCase(max_ref_no)) {
            return old_ref_no;
        } else {
            return null;
        }
    }

    private void updateOldRefNumber(String ref_no) throws SQLException {
        String sql = "update payment set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldt set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhd set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhdlg set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldtlg set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthd set old_ref_no = voucher_no where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthdlg set old_ref_no = voucher_no where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();
    }

    private void updateNewRefNumber(String ref_no) throws SQLException {
        String sql = "update payment set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldt set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhd set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhdlg set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldtlg set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthd set voucher_no = concat('TP',voucher_no) where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthdlg set voucher_no = concat('TP',voucher_no) where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();
    }

    private void performSomeMagic(String ref_no) throws SQLException {
        String sql = "select old_ref_no from opdbillhd where old_ref_no like 'OP%' and old_ref_no >'" + ref_no + "' order by v_date,ref_no";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);
        ResultSet rsLocal = pstLocal.executeQuery();
        rsLocal.last();
        rsLocal.beforeFirst();
        int i = 0;
        while (rsLocal.next()) {
            String new_ref_no = lb.generateKey(ref_no, 11, "OP" + HMSHome.year + "/");

            //Perform some magic
            sql = "update payment set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbilldt set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbillhd set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbillhdlg set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbilldtlg set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdpaymenthd set voucher_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdpaymenthdlg set voucher_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            ref_no = new_ref_no;
            i++;
        }
    }

    private void updateOldRefNumberToBlank(String ref_no) throws SQLException {
        String sql = "update payment set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldt set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhd set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhdlg set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldtlg set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthd set old_ref_no = '' where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthdlg set old_ref_no = '' where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();
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
        jCheckBox1 = new javax.swing.JCheckBox();
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jtxtToDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jlblTotBill = new javax.swing.JLabel();
        jlblTotCash = new javax.swing.JLabel();
        jlblTotCard = new javax.swing.JLabel();
        jlblTotCheque = new javax.swing.JLabel();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        jCheckBox1.setText("Select ALl");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
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
                "rec_no", "Receipt No", "Date", "Patient Name", "Bill Amount", "Cash Amount", "Cheque", "Card Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.BorderLayout());

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        // TODO add your handling code here:
        for (int i = 0; i < model.getSize(); i++) {
            model.get(i).setSelected(jCheckBox1.isSelected());
        }
        jPanel4.repaint();
        if (!jCheckBox1.isSelected()) {
            checkBoxList.clearSelection();
        }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jlblTotCardComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblTotCardComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jlblTotCardComponentMoved

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here
        if (SwingUtilities.isRightMouseButton(evt)) {
            lb.confirmDialog("Do you want to move back to original place?");
            if (lb.type) {
                try {
                    int row = jTable1.getSelectedRow();
                    if (row != -1) {
                        String new_ref_no = lb.generateKey("opdbillhd", "ref_no", 11, "OP" + HMSHome.year + "/");
                        String rec_no = jTable1.getValueAt(row, 0).toString();
                        String date = lb.ConvertDateFormetForDB(jTable1.getValueAt(row, 2).toString());
                        dataConnection.setAutoCommit(false);
                        moveBackToOriginal(rec_no, new_ref_no);
                        String old_ref_no = checkGapBetweenDates(date);
                        if (rec_no != null) {
                            updateOldRefNumber(old_ref_no);
                            updateNewRefNumber(old_ref_no);
                            performSomeMagic(old_ref_no);
                            updateOldRefNumberToBlank(old_ref_no);
                        }
                        lb.showMessageDailog("UnHide voucher successfull");
                        dataConnection.commit();
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at Hide OPD Voucher", ex);
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                    } catch (Exception e) {

                    }
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jlblTotBill;
    private javax.swing.JLabel jlblTotCard;
    private javax.swing.JLabel jlblTotCash;
    private javax.swing.JLabel jlblTotCheque;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private com.toedter.calendar.JDateChooser jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
