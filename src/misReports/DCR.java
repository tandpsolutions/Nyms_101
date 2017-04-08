/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import reportDAo.DCRDao;
import support.JCheckBoxList;
import support.Library;
import support.ReportPanel;

/**
 *
 * @author Lenovo
 */
public class DCR extends javax.swing.JInternalFrame {

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
    public DCR(int form_cd) {
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
        jPanel5.setVisible(false);
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

            for (int i = 0; i < model.getSize(); i++) {
                if (model.get(i).isSelected()) {
                    ((JCheckBox) model.get(i)).setSelected(true);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at addUser in OPDCollectionReport", ex);
        }
    }

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotBill, jlblTotCash, jlblTotCheque, jlblTotCard, null});
    }

    private void setTotal() {
        double bill_amt = 0.00;
        double advance = 0.00;
        double refund = 0.00;
        double remaining = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            bill_amt += lb.isNumber(jTable1.getValueAt(i, 4).toString());
            advance += lb.isNumber(jTable1.getValueAt(i, 5).toString());
            refund += lb.isNumber(jTable1.getValueAt(i, 6).toString());
            remaining += lb.isNumber(jTable1.getValueAt(i, 7).toString());
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
                    String sql = "SELECT i.ref_no,i.v_date,l.user_name,i.amount,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM ipdpaymenthd i "
                            + " LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no "
                            + " left join ipdreg i1 on i1.ipd_no=i.ipd_no LEFT JOIN patientmst p1 ON i1.opd_no=p1.opd_no "
                            + " where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
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
                        if (rsLocal.getString("ref_no").startsWith("IP")) {
                            row.add("Receipt");
                        } else if (rsLocal.getString("ref_no").startsWith("AR")) {
                            if (rsLocal.getDouble("amount") > 0) {
                                row.add("Advance");
                            } else {
                                row.add("Refund");
                            }
                        } else {
                            row.add("");
                        }

                        row.add(rsLocal.getString("ref_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        row.add(rsLocal.getString("user_name"));
                        row.add(lb.isNumber(rsLocal.getString("amount")));
                        row.add(lb.isNumber(rsLocal.getString("cash_amt")));
                        row.add(lb.isNumber(rsLocal.getString("bank_amt")));
                        row.add(lb.isNumber(rsLocal.getString("card_amt")));
                        row.add(rsLocal.getString("pt_name"));
                        dtm.addRow(row);
                    }

                    sql = "SELECT i.ref_no,i.v_date,l.user_name,i.amount,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM opdpaymenthd i "
                            + " LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no "
                            + " left join opdbillhd i1 on i1.ref_no=i.voucher_no LEFT JOIN patientmst p1 ON i1.opd_no=p1.opd_no "
                            + " where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.ref_no like 'LP%' and i.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        row.add("OPD Late Payment");
                        row.add(rsLocal.getString("ref_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        row.add(rsLocal.getString("user_name"));
                        row.add(lb.isNumber(rsLocal.getString("amount")));
                        row.add(lb.isNumber(rsLocal.getString("cash_amt")));
                        row.add(lb.isNumber(rsLocal.getString("bank_amt")));
                        row.add(lb.isNumber(rsLocal.getString("card_amt")));
                        row.add(rsLocal.getString("pt_name"));
                        dtm.addRow(row);
                    }

                    sql = "SELECT i.ref_no,i.v_date,l.user_name,i.disc_amt,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM opdbillhd i "
                            + "  LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no"
                            + " LEFT JOIN patientmst p1 ON i.opd_no=p1.opd_no "
                            + "  where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        if (rsLocal.getString("ref_no").startsWith("OP")) {
                            row.add("OPD");
                        } else if (rsLocal.getString("ref_no").startsWith("PL")) {
                            row.add("Pathology");
                        } else {
                            row.add("");
                        }
                        row.add(rsLocal.getString("ref_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        row.add(rsLocal.getString("user_name"));
                        row.add(lb.isNumber(rsLocal.getString("disc_amt")));
                        row.add(lb.isNumber(rsLocal.getString("cash_amt")));
                        row.add(lb.isNumber(rsLocal.getString("bank_amt")));
                        row.add(lb.isNumber(rsLocal.getString("card_amt")));
                        row.add(rsLocal.getString("pt_name"));
                        dtm.addRow(row);
                    }

                    double bill_amt = 0.00;
                    double advance = 0.00;
                    double refund = 0.00;
                    double remaining = 0.00;
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        bill_amt += lb.isNumber(jTable1.getValueAt(i, 4).toString());
                        advance += lb.isNumber(jTable1.getValueAt(i, 5).toString());
                        refund += lb.isNumber(jTable1.getValueAt(i, 6).toString());
                        remaining += lb.isNumber(jTable1.getValueAt(i, 7).toString());
                    }

                    Vector row = new Vector();
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("Total ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(bill_amt);
                    row.add(advance);
                    row.add(refund);
                    row.add(remaining);
                    row.add(" ");
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
                    setTable();
                    setTotal();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {

                try {
                    jPanel3.removeAll();
                    String sql = "SELECT i.ref_no,i.v_date,l.user_name,i.amount,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM ipdpaymenthd i "
                            + " LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no "
                            + " left join ipdreg i1 on i1.ipd_no=i.ipd_no LEFT JOIN patientmst p1 ON i1.opd_no=p1.opd_no "
                            + " where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
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
                    ArrayList<DCRDao> dcrDao = new ArrayList<DCRDao>();
                    while (rsLocal.next()) {
                        DCRDao dcr = new DCRDao();
                        if (rsLocal.getString("ref_no").startsWith("IP")) {
                            dcr.setDept("Receipt");
                        } else if (rsLocal.getString("ref_no").startsWith("AR")) {
                            if (rsLocal.getDouble("amount") > 0) {
                                dcr.setDept("Advance");
                            } else {
                                dcr.setDept("Refund");
                            }
                        } else {
                            dcr.setDept("");
                        }
                        dcr.setRef_no(rsLocal.getString("ref_no"));
                        dcr.setDate(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        dcr.setUser_name(rsLocal.getString("user_name"));
                        dcr.setAmount(lb.isNumber(rsLocal.getString("amount")));
                        dcr.setCash(lb.isNumber(rsLocal.getString("cash_amt")));
                        dcr.setBank(lb.isNumber(rsLocal.getString("bank_amt")));
                        dcr.setCard(lb.isNumber(rsLocal.getString("card_amt")));
                        dcr.setPt_name(rsLocal.getString("pt_name"));
                        dcrDao.add(dcr);
                    }

                    sql = "SELECT i.ref_no,i.v_date,l.user_name,i.amount,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM opdpaymenthd i "
                            + " LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no "
                            + " left join opdbillhd i1 on i1.ref_no=i.voucher_no LEFT JOIN patientmst p1 ON i1.opd_no=p1.opd_no "
                            + " where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.ref_no like 'LP%' and i.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    while (rsLocal.next()) {
                        DCRDao dcr = new DCRDao();
                        dcr.setDept("Late Payment");
                        dcr.setRef_no(rsLocal.getString("ref_no"));
                        dcr.setDate(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        dcr.setUser_name(rsLocal.getString("user_name"));
                        dcr.setAmount(lb.isNumber(rsLocal.getString("amount")));
                        dcr.setCash(lb.isNumber(rsLocal.getString("cash_amt")));
                        dcr.setBank(lb.isNumber(rsLocal.getString("bank_amt")));
                        dcr.setCard(lb.isNumber(rsLocal.getString("card_amt")));
                        dcr.setPt_name(rsLocal.getString("pt_name"));
                        dcrDao.add(dcr);
                    }

                    sql = "SELECT i.ref_no,i.v_date,l.user_name,i.disc_amt,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM opdbillhd i "
                            + "  LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no"
                            + " LEFT JOIN patientmst p1 ON i.opd_no=p1.opd_no "
                            + "  where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    while (rsLocal.next()) {
                        DCRDao dcr = new DCRDao();
                        if (rsLocal.getString("ref_no").startsWith("OP")) {
                            dcr.setDept("OPD");
                        } else if (rsLocal.getString("ref_no").startsWith("PL")) {
                            dcr.setDept("Pathology");
                        } else {
                            dcr.setDept("");
                        }
                        dcr.setRef_no(rsLocal.getString("ref_no"));
                        dcr.setDate(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        dcr.setUser_name(rsLocal.getString("user_name"));
                        dcr.setAmount(lb.isNumber(rsLocal.getString("disc_amt")));
                        dcr.setCash(lb.isNumber(rsLocal.getString("cash_amt")));
                        dcr.setBank(lb.isNumber(rsLocal.getString("bank_amt")));
                        dcr.setCard(lb.isNumber(rsLocal.getString("card_amt")));
                        dcr.setPt_name(rsLocal.getString("pt_name"));
                        dcrDao.add(dcr);
                    }
                    HashMap params = new HashMap();
                    params.put("fromDate", jtxtFromDate.getText());
                    params.put("toDate", jtxtToDate.getText());
                    lb.reportGeneratorWord("DCR.jasper", params, dcrDao);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callExcel() {
                try {
                    callView();
                    ArrayList rows = new ArrayList();
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        ArrayList row = new ArrayList();
                        row.add(jTable1.getValueAt(i, 0).toString());
                        row.add(jTable1.getValueAt(i, 1).toString());
                        row.add(jTable1.getValueAt(i, 2).toString());
                        row.add(jTable1.getValueAt(i, 3).toString());
                        row.add(jTable1.getValueAt(i, 4).toString());
                        row.add(jTable1.getValueAt(i, 5).toString());
                        row.add(jTable1.getValueAt(i, 6).toString());
                        row.add(jTable1.getValueAt(i, 7).toString());
                        row.add(jTable1.getValueAt(i, 8).toString());
                        rows.add(row);
                    }

                    ArrayList header = new ArrayList();
                    header.add("Department");
                    header.add("Voucehr No");
                    header.add("Date");
                    header.add("User");
                    header.add("Total");
                    header.add("Cash");
                    header.add("Card");
                    header.add("Bank");
                    header.add("Patient");
                    lb.exportToExcel("DCR", header, rows, "DCR");
                } catch (Exception ex) {
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
                    String sql = "SELECT i.ref_no,i.v_date,l.user_name,i.amount,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM ipdpaymenthd i "
                            + " LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no "
                            + " left join ipdreg i1 on i1.ipd_no=i.ipd_no LEFT JOIN patientmst p1 ON i1.opd_no=p1.opd_no "
                            + " where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
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
                    ArrayList<DCRDao> dcrDao = new ArrayList<DCRDao>();
                    while (rsLocal.next()) {
                        DCRDao dcr = new DCRDao();
                        if (rsLocal.getString("ref_no").startsWith("IP")) {
                            dcr.setDept("Receipt");
                        } else if (rsLocal.getString("ref_no").startsWith("AR")) {
                            if (rsLocal.getDouble("amount") > 0) {
                                dcr.setDept("Advance");
                            } else {
                                dcr.setDept("Refund");
                            }
                        } else {
                            dcr.setDept("");
                        }
                        dcr.setRef_no(rsLocal.getString("ref_no"));
                        dcr.setDate(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        dcr.setUser_name(rsLocal.getString("user_name"));
                        dcr.setAmount(lb.isNumber(rsLocal.getString("amount")));
                        dcr.setCash(lb.isNumber(rsLocal.getString("cash_amt")));
                        dcr.setBank(lb.isNumber(rsLocal.getString("bank_amt")));
                        dcr.setCard(lb.isNumber(rsLocal.getString("card_amt")));
                        dcr.setPt_name(rsLocal.getString("pt_name"));
                        dcrDao.add(dcr);
                    }

                    sql = "SELECT i.ref_no,i.v_date,l.user_name,i.amount,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM opdpaymenthd i "
                            + " LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no "
                            + " left join opdbillhd i1 on i1.ref_no=i.voucher_no LEFT JOIN patientmst p1 ON i1.opd_no=p1.opd_no "
                            + " where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    while (rsLocal.next()) {
                        DCRDao dcr = new DCRDao();
                        dcr.setDept("Late Payment");
                        dcr.setRef_no(rsLocal.getString("ref_no"));
                        dcr.setDate(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        dcr.setUser_name(rsLocal.getString("user_name"));
                        dcr.setAmount(lb.isNumber(rsLocal.getString("amount")));
                        dcr.setCash(lb.isNumber(rsLocal.getString("cash_amt")));
                        dcr.setBank(lb.isNumber(rsLocal.getString("bank_amt")));
                        dcr.setCard(lb.isNumber(rsLocal.getString("card_amt")));
                        dcr.setPt_name(rsLocal.getString("pt_name"));
                        dcrDao.add(dcr);
                    }

                    sql = "SELECT i.ref_no,i.v_date,l.user_name,i.disc_amt,p.cash_amt,p.bank_amt,p.card_amt,p1.pt_name FROM opdbillhd i "
                            + "  LEFT JOIN login l ON i.user_id=l.user_id LEFT JOIN payment p ON i.ref_no=p.ref_no"
                            + " LEFT JOIN patientmst p1 ON i.opd_no=p1.opd_no "
                            + "  where i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and i.user_id in (";
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.get(i).isSelected()) {
                            sql += "" + lb.getUserName(model.get(i).getText(), "C") + ",";
                        }
                    }
                    if (sql.endsWith(",")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += ")";
                    pstLocal = dataConnection.prepareStatement(sql);
                    rsLocal = pstLocal.executeQuery();
                    while (rsLocal.next()) {
                        DCRDao dcr = new DCRDao();
                        if (rsLocal.getString("ref_no").startsWith("OP")) {
                            dcr.setDept("OPD");
                        } else if (rsLocal.getString("ref_no").startsWith("PL")) {
                            dcr.setDept("Pathology");
                        } else {
                            dcr.setDept("");
                        }
                        dcr.setRef_no(rsLocal.getString("ref_no"));
                        dcr.setDate(lb.ConvertDateFormetForDisply(rsLocal.getString("v_date")));
                        dcr.setUser_name(rsLocal.getString("user_name"));
                        dcr.setAmount(lb.isNumber(rsLocal.getString("disc_amt")));
                        dcr.setCash(lb.isNumber(rsLocal.getString("cash_amt")));
                        dcr.setBank(lb.isNumber(rsLocal.getString("bank_amt")));
                        dcr.setCard(lb.isNumber(rsLocal.getString("card_amt")));
                        dcr.setPt_name(rsLocal.getString("pt_name"));
                        dcrDao.add(dcr);
                    }
                    HashMap params = new HashMap();
                    params.put("fromDate", jtxtFromDate.getText());
                    params.put("toDate", jtxtToDate.getText());
                    lb.reportGenerator("DCR.jasper", params, dcrDao, jPanel3);
                } catch (Exception ex) {
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
        jPanel5 = new javax.swing.JPanel();
        jlblTotBill = new javax.swing.JLabel();
        jlblTotCash = new javax.swing.JLabel();
        jlblTotCard = new javax.swing.JLabel();
        jlblTotCheque = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        jCheckBox1.setText("Select ALl");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });

        jtxtFromDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDateKeyPressed(evt);
            }
        });

        jtxtToDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtToDateKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox1)))
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
                "Department", "Ref NO", "Date", "User Name", "Amount", "Cash", "Bank", "Card", "Patient Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

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

        jPanel4.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jlblTotCardComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblTotCardComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jlblTotCardComponentMoved

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

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtToDate);
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtToDateKeyPressed


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
