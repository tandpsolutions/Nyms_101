/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.CursorGlassPane;
import hms.HMS101;
import hms.HMSHome;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.PickList;
import support.ReportTable;
import utility.VoucherDisplay;

/**
 *
 * @author Bhaumik
 */
public class OPDBillGeneration extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    Component oldGlass = null;
    CursorGlassPane glassPane = new CursorGlassPane();
    DefaultTableModel dtm = null;
    NavigationPanel navLoad = null;
    PickList itemPicklist = null;
    String ref_no = "";
    PickList acPickList = null;
    DefaultTableModel dtmBillGroup = null;
    DefaultTableModel dtmBillItem = null;
    private PaymentDialog pd = new PaymentDialog(null, true);
    String start = "";
    ReportTable table = null;
    String tableHD = "OPDBILLHD", tableDT = "OPDBILLDT";

    /**
     * Creates new form OPDBillGeneration
     */
    public OPDBillGeneration(String opd_no, String appoint_no, String start) {
        initComponents();
        this.start = start;
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmBillGroup = (DefaultTableModel) jTable3.getModel();
        dtmBillItem = (DefaultTableModel) jTable2.getModel();
        addInitialData();
        addNavigationPanel();
        if (start.equalsIgnoreCase("OP")) {
            lb.setUserRightsToPanel1(navLoad, "24");
        } else if (start.equalsIgnoreCase("PL")) {
            lb.setUserRightsToPanel1(navLoad, "211");
        }
        navLoad.callNew();
        jtxtOPDNumber.setText(opd_no);
        jlblAppointNo.setText(appoint_no);
        setData();
        setPickListView();
        addJtextBox();
        addJlabel();
        addJlabelNetAmt();
        addJlabelOtherPaid();
        addJlabelAdvanceAmt();
        addJlabelRemainingAmt();
        jbtnSelPat.setEnabled(false);

    }

    public OPDBillGeneration(String start) {
        initComponents();
        this.start = start;
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmBillGroup = (DefaultTableModel) jTable3.getModel();
        dtmBillItem = (DefaultTableModel) jTable2.getModel();
        addInitialData();
        addNavigationPanel();
        setPickListView();
        addJtextBox();
        addJlabel();
        addJlabelNetAmt();
        addJlabelOtherPaid();
        addJlabelAdvanceAmt();
        addJlabelRemainingAmt();
        navLoad.setVoucher("Last");
        if (start.equalsIgnoreCase("OP")) {
            lb.setUserRightsToPanel1(navLoad, "24");
        } else if (start.equalsIgnoreCase("PL")) {
            lb.setUserRightsToPanel1(navLoad, "211");
        }
        setTable();
    }

    public OPDBillGeneration(String start, int mode) {
        initComponents();
        this.start = start;
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmBillGroup = (DefaultTableModel) jTable3.getModel();
        dtmBillItem = (DefaultTableModel) jTable2.getModel();
        addInitialData();
        addNavigationPanel();
        setPickListView();
        addJtextBox();
        addJlabel();
        addJlabelNetAmt();
        addJlabelOtherPaid();
        addJlabelAdvanceAmt();
        addJlabelRemainingAmt();
        navLoad.setVoucher("Last");
        tableHD = "OPDBILLHDLG";
        tableDT = "OPDBILLDTLG";
        if (start.equalsIgnoreCase("OP")) {
            lb.setUserRightsToPanel1(navLoad, "24");
        } else if (start.equalsIgnoreCase("PL")) {
            lb.setUserRightsToPanel1(navLoad, "211");
        }
    }

    private void addInitialData() {
        try {
            String sql = "SELECT bill_grp_cd,bill_group_name FROM billgrpmst";
            if (start.equalsIgnoreCase("OP")) {
                sql += " where bill_group_name <>'PATHOLOGY'";
            } else if (start.equalsIgnoreCase("PL")) {
                sql += " where bill_group_name <>'O.P.D.'";
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add(rsLocal.getInt("bill_grp_cd"));
                row.add(rsLocal.getString("bill_group_name"));
                dtmBillGroup.addRow(row);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at addInitial Data ad OPDBillGeneration", ex);
        }
    }

    private void addGlassPane() {
        oldGlass = this.getGlassPane();
        this.setGlassPane(glassPane);
        this.getGlassPane().setVisible(true);
    }

    private void removeGlassPane() {
        this.getGlassPane().setVisible(false);
        this.setGlassPane(oldGlass);
    }

    private void setPickListView() {
        itemPicklist = new PickList(dataConnection);
        acPickList = new PickList(dataConnection);

        itemPicklist.setLayer(this.getLayeredPane());
        itemPicklist.setPickListComponent(jtxtItem);
        itemPicklist.setReturnComponent(new JTextField[]{jtxtItem, jtxtQty, jtxtRate});
        itemPicklist.setNextComponent(jtxtQty);

        acPickList.setLayer(this.getLayeredPane());
        acPickList.setPickListComponent(jtxtRefBy);
        acPickList.setReturnComponent(new JTextField[]{jtxtRefBy, jtxtRefAlias});
        acPickList.setNextComponent(jbtnAdd);
    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at city master", ex);
        }
    }

    private void cancelOrClose() {
        if (navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            navLoad.setMode("");
            navLoad.setComponentEnabled(false);
            navLoad.setMessage("");
            navLoad.setSaveFlag(true);
            navLoad.setVoucher("Edit");
        }
    }

    public void setData() {
        try {
            String sql = "SELECT p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name  "
                    + " FROM patientinfomst p1 LEFT JOIN patientmst p ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd "
                    + " LEFT JOIN areamst a ON p1.area_cd=a.area_cd LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd "
                    + " where p.opd_no='" + jtxtOPDNumber.getText() + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    public void loadDataOldVoucher(String ref_no) {
        if (!navLoad.getSaveFlag()) {
            cancelOrClose();
        }
        this.ref_no = ref_no;
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Ref No", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "Voucher Date", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Patient Name", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Paid Amount", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "User", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {
                if (lb.isNumber(jtxtPaidAmt) == 0) {
                    lb.showErrorDailog("You are about to save 0 Amount voucher. \n Press Ok to continue.");
                }
                pd.setReturnComp(jtxtPaidAmt);
                pd.jlblSale.setText(jtxtPaidAmt.getText());
                if (getMode().equalsIgnoreCase("N")) {
                    pd.jcbCash.setSelected(true);
                    pd.jtxtCashAmt.setText(jtxtPaidAmt.getText());
                }
                if (ref_no.equalsIgnoreCase("")) {
                    pd.setInitialAmt(0);
                }
                pd.setTotal();
                pd.setFocusComp(navLoad);
                pd.show();

                if (pd.getReturnStatus() == pd.RET_OK) {
                    String sql = "";
                    if (getMode().equalsIgnoreCase("N")) {
                        sql = "insert into opdbillhd (v_date,opd_no,det_tot,net_amt,user_id,appoint_no,disc_amt,doc_cd,remark,ref_no) values (?,?,?,?,?,?,?,?,?,?)";
                        ref_no = lb.generateKey("opdbillhd", "ref_no", 13, start + HMSHome.year + "/");
                    } else if (getMode().equalsIgnoreCase("E")) {
                        lb.generateLog("OPDBILLHD", "OPDBILLHDLG", "ref_no", ref_no);
                        lb.generateLog("OPDBILLDT", "OPDBILLDTLG", "ref_no", ref_no);
                        sql = "update opdbillhd set v_date=?,opd_no=?,det_tot=?,net_amt=?,user_id=?,edit_no=edit_no+1,appoint_no=?,"
                                + "time_stamp=current_timestamp,disc_amt=?,doc_cd=?,remark=? where ref_no =?";
                    }
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    pstLocal.setString(2, jtxtOPDNumber.getText());
                    pstLocal.setDouble(3, lb.isNumber(jlblTotal));
                    pstLocal.setDouble(4, lb.isNumber(jlblNetAmt));
                    pstLocal.setInt(5, HMSHome.user_id);
                    pstLocal.setString(6, jlblAppointNo.getText());
                    pstLocal.setDouble(7, lb.isNumber(jtxtPaidAmt));
                    pstLocal.setString(8, "0");
                    pstLocal.setString(9, jtxtRemrk.getText());
                    pstLocal.setString(10, ref_no);
                    pstLocal.executeUpdate();

                    sql = "delete from opdbilldt where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.executeUpdate();

                    sql = "delete from payment where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.executeUpdate();

                    sql = "insert into payment (ref_no,cash_amt,bank_name,bank_branch,cheque_no,cheque_date,bank_amt,card_amt,card_no,user_id) values (?,?,?,?,?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    if (pd.jcbCash.isSelected()) {
                        pstLocal.setDouble(2, lb.isNumber(pd.jtxtCashAmt));
                    } else {
                        pstLocal.setDouble(2, 0.00);
                    }
                    if (pd.jcbBank.isSelected()) {
                        pstLocal.setString(3, pd.jtxtBankName.getText());
                        pstLocal.setString(4, pd.jtxtBranchName.getText());
                        pstLocal.setString(5, pd.jtxtChequeNo.getText());
                        if (lb.isBlank(pd.jtxtChequeDate)) {
                            pstLocal.setString(6, null);
                        } else {
                            pstLocal.setString(6, lb.ConvertDateFormetForDB(pd.jtxtChequeDate.getText()));
                        }

                        pstLocal.setDouble(7, lb.isNumber(pd.jtxtChequeAmt));
                    } else {
                        pstLocal.setString(3, "");
                        pstLocal.setString(4, "");
                        pstLocal.setString(5, "");
                        pstLocal.setString(6, null);
                        pstLocal.setDouble(7, 0.00);
                    }
                    if (pd.jcbCard.isSelected()) {
                        pstLocal.setDouble(8, lb.isNumber(pd.jtxtCardAmt));
                        pstLocal.setString(9, pd.jtxtCardNo.getText());
                    } else {
                        pstLocal.setDouble(8, 0.00);
                        pstLocal.setString(9, "");
                    }
                    pstLocal.setInt(10, HMSHome.user_id);
                    pstLocal.executeUpdate();

                    sql = "insert into opdbilldt (ref_no,sr_no,bill_item_cd,qty,rate,amount,disc,final_amt,doc_cd) values(?,?,?,?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        pstLocal.setInt(2, i + 1);
                        pstLocal.setString(3, lb.getbillitemCode(jTable1.getValueAt(i, 0).toString(), "C"));
                        pstLocal.setInt(4, (int) lb.isNumber(jTable1.getValueAt(i, 1).toString()));
                        pstLocal.setDouble(5, lb.isNumber(jTable1.getValueAt(i, 2).toString()));
                        pstLocal.setDouble(6, lb.isNumber(jTable1.getValueAt(i, 3).toString()));
                        pstLocal.setDouble(7, lb.isNumber(jTable1.getValueAt(i, 4).toString()));
                        pstLocal.setDouble(8, lb.isNumber(jTable1.getValueAt(i, 5).toString()));
                        pstLocal.setString(9, lb.getAcCode(jTable1.getValueAt(i, 7).toString(), "AC"));
                        pstLocal.executeUpdate();
                    }
                    setVoucher("edit");
                } else {
                    throw new SQLException("Cancel Button was clicked");
                }
            }

            @Override
            public void callDelete() throws Exception {
                lb.confirmDialog("Do you want to delete this receipt entry?");
                if (lb.type) {
                    lb.generateLog("OPDBILLHD", "OPDBILLHDLG", "ref_no", ref_no);
                    lb.generateLog("OPDBILLDT", "OPDBILLDTLG", "ref_no", ref_no);
                    String sql = "delete from opdbillhd where ref_no=?";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.executeUpdate();

                    sql = "delete from opdbilldt where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.executeUpdate();
                }
            }

            @Override
            public void callView() {
                if (start.equalsIgnoreCase("OP")) {
                    String sql = "SELECT o.ref_no,o.v_date,p.pt_name,o.disc_amt,l.user_name FROM " + tableHD + " o LEFT JOIN patientmst p ON o.opd_no=p.opd_no "
                            + " LEFT JOIN login l ON o.user_id=l.user_id where ref_no like '" + start + "%'";
                    makeViewTable();
                    HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "OPD Bill Generation View", sql, "26", 1, OPDBillGeneration.this, "OPD Bill Generation View", table);
                    header.makeView();
                    cancelOrClose();
                    HMSHome.addOnScreen(header, "OPD Bill Generation View", 24);
                } else if (start.equalsIgnoreCase("PL")) {
                    String sql = "SELECT o.ref_no,o.v_date,p.pt_name,o.disc_amt,l.user_name FROM " + tableHD + " o LEFT JOIN patientmst p ON o.opd_no=p.opd_no "
                            + " LEFT JOIN login l ON o.user_id=l.user_id where ref_no like '" + start + "%'";
                    makeViewTable();
                    HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "Pathology Bill Generation View", sql, "26", 1, OPDBillGeneration.this, "Pathology Bill Generation View", table);
                    header.makeView();
                    cancelOrClose();
                    HMSHome.addOnScreen(header, "Pathology Bill Generation View", 211);
                }
            }

            @Override
            public void callPrint() {
                VoucherDisplay vd = new VoucherDisplay(ref_no, "OPD");
                HMSHome.addOnScreen(vd, "Voucher Display", -1);
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabled(false);
                if (start.equalsIgnoreCase("OP")) {
                    lb.setUserRightsToPanel1(navLoad, "24");
                } else if (start.equalsIgnoreCase("PL")) {
                    lb.setUserRightsToPanel1(navLoad, "211");
                }
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,a.user_id as user,a.edit_no as edit,a.time_stamp as time from " + tableHD + " a left join " + tableDT + "  b on a.ref_no=b.ref_no left join payment p on a.ref_no=p.ref_no where a.ref_no like '" + start + "%' and a.ref_no=(select min(ref_no) from " + tableHD + " where ref_no like '" + start + "%')");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,a.user_id as user,a.edit_no as edit,a.time_stamp as time from " + tableHD + "  a left join " + tableDT + "  b on a.ref_no=b.ref_no left join  payment p on a.ref_no=p.ref_no where a.ref_no like '" + start + "%' and a.ref_no=(select max(ref_no) from " + tableHD + " where ref_no <'" + ref_no + "' and ref_no like '" + start + "%')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,a.user_id as user,a.edit_no as edit,a.time_stamp as time from " + tableHD + "  a left join " + tableDT + "  b on a.ref_no=b.ref_no left join payment p on a.ref_no=p.ref_no where a.ref_no like '" + start + "%' and a.ref_no=(select min(ref_no) from " + tableHD + " where ref_no >'" + ref_no + "' and ref_no like '" + start + "%')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,a.user_id as user,a.edit_no as edit,a.time_stamp as time from " + tableHD + "  a left join " + tableDT + "  b on a.ref_no=b.ref_no left join payment p on a.ref_no=p.ref_no where a.ref_no like '" + start + "%' and a.ref_no=(select max(ref_no) from " + tableHD + " where ref_no like '" + start + "%' )");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,a.user_id as user,a.edit_no as edit,a.time_stamp as time from " + tableHD + "  a left join " + tableDT + "  b on a.ref_no=b.ref_no left join payment p on a.ref_no=p.ref_no where a.ref_no like '" + start + "%' and a.ref_no='" + ref_no + "'");
                }
                try {
                    if (viewDataRs.next()) {
                        setComponentTextFromRs();
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher cityMaster'", ex);
                }

            }

            @Override
            public void setComponentText() {
                pd.reset();
                jtxtRefNo.setText("");
                jtxtOPDNumber.setText("");
                jtxtRemrk.setText("");
                jlblName.setText("");
                jlblSex.setText("");
                jlblRefBy.setText("");
                jlblArea.setText("");
                jlblCity.setText("");
                jlblTotal.setText("0.00");
                jlblNetAmt.setText("0.00");
                jtxtPaidAmt.setText("0.00");
                jlblRemainingAmt.setText("0.00");
                lb.setDateChooserPropertyInit(jtxtVdate);
                jtxtRefAlias.setText("");
                jtxtRefBy.setText("");
                jlblAppointNo.setText("");
                dtm.setRowCount(0);
                jlblOtherPaid.setText("0.00");
                clearRow();
                setTable();
            }

            @Override
            public void setComponentEnabled(boolean bFlag) {
                jtxtRefNo.setEnabled(!bFlag);
                jtxtOPDNumber.setEnabled(bFlag);
                jtxtPaidAmt.setEnabled(bFlag);
                jtxtVdate.setEnabled(bFlag);
                jtxtItem.setEnabled(bFlag);
                jtxtRate.setEnabled(bFlag);
                jtxtAmt.setEnabled(bFlag);
                jtxtDiscAmt.setEnabled(bFlag);
                jtxtFinalAmt.setEnabled(bFlag);
                jtxtQty.setEnabled(bFlag);
                jbtnSelPat.setEnabled(bFlag);
                jbtnAdd.setEnabled(bFlag);
//                jBillDateBtn.setEnabled(bFlag);
                jTable1.setEnabled(bFlag);
                jTable2.setEnabled(bFlag);
                jTable3.setEnabled(bFlag);
                jtxtRefBy.setEnabled(bFlag);
                jtxtRefAlias.setEnabled(bFlag);
                jtxtRemrk.setEnabled(bFlag);
                clearRow();
                if (getMode().equalsIgnoreCase("E")) {
                    jbtnSelPat.setEnabled(false);
                }
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                try {
                    ref_no = viewDataRs.getString("ref_no");
                    jtxtRefNo.setText(viewDataRs.getString("ref_no"));
                    jtxtRemrk.setText(viewDataRs.getString("remark"));
                    jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("v_date")));
                    jtxtOPDNumber.setText(viewDataRs.getString("opd_no"));
                    jlblAppointNo.setText(viewDataRs.getString("appoint_no"));
                    jlblEditNo1.setText(viewDataRs.getString("edit"));
                    jlblLstUpdate1.setText(viewDataRs.getString("time"));
                    jlblUserName1.setText(lb.getUserName(viewDataRs.getString("user"), "N"));
                    setData();
                    jlblTotal.setText(lb.Convert2DecFmtForRs(viewDataRs.getDouble("det_tot")));
                    jlblNetAmt.setText(lb.Convert2DecFmtForRs(viewDataRs.getDouble("net_amt")));
                    jtxtPaidAmt.setText(lb.Convert2DecFmtForRs(viewDataRs.getDouble("disc_amt")));
                    pd.reset();
                    if (viewDataRs.getDouble("CASH_AMT") > 0) {
                        pd.jcbCash.setSelected(true);
                        pd.jtxtCashAmt.setText(lb.Convert2DecFmtForRs(viewDataRs.getDouble("CASH_AMT")));
                    }
                    if (viewDataRs.getDouble("BANK_AMT") > 0) {
                        pd.jcbBank.setSelected(true);
                        pd.jtxtBankName.setText(viewDataRs.getString("BANK_NAME"));
                        pd.jtxtBranchName.setText(viewDataRs.getString("BANK_BRANCH"));
                        pd.jtxtChequeNo.setText(viewDataRs.getString("CHEQUE_NO"));
                        pd.jtxtChequeDate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("CHEQUE_DATE")));
                        pd.jtxtChequeAmt.setText(lb.Convert2DecFmtForRs(viewDataRs.getDouble("BANK_AMT")));
                    }
                    if (viewDataRs.getDouble("CARD_AMT") > 0) {
                        pd.jcbCard.setSelected(true);
                        pd.jtxtCardAmt.setText(lb.Convert2DecFmtForRs(viewDataRs.getDouble("CARD_AMT")));
                        pd.jtxtCardNo.setText(viewDataRs.getString("card_no"));
                    }
                    dtm.setRowCount(0);
                    Vector row = new Vector();
                    row.add(lb.getbillitemCode(viewDataRs.getString("bill_item_cd"), "N"));
                    row.add(viewDataRs.getInt("qty"));
                    row.add(viewDataRs.getDouble("rate"));
                    row.add(viewDataRs.getDouble("amount"));
                    row.add(viewDataRs.getDouble("disc"));
                    row.add(viewDataRs.getDouble("final_amt"));
                    row.add(lb.getAcCode(viewDataRs.getString("doc"), "N"));
                    row.add(lb.getAcCode(viewDataRs.getString("doc"), "CA"));
                    dtm.addRow(row);
                    while (viewDataRs.next()) {
                        row = new Vector();
                        row.add(lb.getbillitemCode(viewDataRs.getString("bill_item_cd"), "N"));
                        row.add(viewDataRs.getInt("qty"));
                        row.add(viewDataRs.getDouble("rate"));
                        row.add(viewDataRs.getDouble("amount"));
                        row.add(viewDataRs.getDouble("disc"));
                        row.add(viewDataRs.getDouble("final_amt"));
                        row.add(lb.getAcCode(viewDataRs.getString("doc"), "N"));
                        row.add(lb.getAcCode(viewDataRs.getString("doc"), "CA"));
                        dtm.addRow(row);
                    }
                    double otherAmt = lb.isNumber(lb.getData("SELECT SUM(amount) FROM opdpaymenthd WHERE ref_no like 'LP%' and "
                            + "voucher_no='" + ref_no + "'"));
                    jlblOtherPaid.setText(lb.Convert2DecFmtForRs(otherAmt));
                    setTotal();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromRs", ex);
                }
            }

            @Override
            public boolean validateForm() {
                if (lb.isBlank(jtxtOPDNumber)) {
                    jtxtOPDNumber.requestFocusInWindow();
                    navLoad.setMessage("Patient name can not be left blank");
                    return false;
                }

                if (jTable1.getRowCount() == 0) {
                    jtxtItem.requestFocusInWindow();
                    navLoad.setMessage("Voucher can not be left blank.");
                    return false;
                }
                return true;
            }

            @Override
            public boolean checkEdit() {
                if (HMSHome.role != 1) {
                    int edt_no = (int) lb.isNumber(jlblEditNo1);
                    if (edt_no < 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            }

        }
        navLoad = new navPanel();
        jPanelNavigation.add(navLoad);
        navLoad.setVisible(true);
    }

    private void addJtextBox() {
//        jPanel4.removeAll();
//        jtxtItem.setVisible(false);
//        jtxtQty.setVisible(false);
//        jtxtRate.setVisible(false);
//        jtxtAmt.setVisible(false);
//        jtxtDiscAmt.setVisible(false);
//        jtxtFinalAmt.setVisible(false);
//        jtxtRefAlias.setVisible(false);
//        jtxtRefBy.setVisible(false);
//
//        jtxtItem.setBounds(0, 0, 20, 20);
//        jtxtItem.setVisible(true);
//        jPanel4.add(jtxtItem);
//
//        jtxtQty.setBounds(0, 0, 20, 20);
//        jtxtQty.setVisible(true);
//        jPanel4.add(jtxtQty);
//
//        jtxtRate.setBounds(0, 0, 20, 20);
//        jtxtRate.setVisible(true);
//        jPanel4.add(jtxtRate);
//
//        jtxtAmt.setBounds(0, 0, 20, 20);
//        jtxtAmt.setVisible(true);
//        jPanel4.add(jtxtAmt);
//
//        jtxtDiscAmt.setBounds(0, 0, 20, 20);
//        jtxtDiscAmt.setVisible(true);
//        jPanel4.add(jtxtDiscAmt);
//
//        jtxtFinalAmt.setBounds(0, 0, 20, 20);
//        jtxtFinalAmt.setVisible(true);
//        jPanel4.add(jtxtFinalAmt);
//
//        jtxtRefBy.setBounds(0, 0, 20, 20);
//        jtxtRefBy.setVisible(true);
//        jPanel4.add(jtxtRefBy);

        setTable();
    }

    private void addJlabel() {
//        jPanel5.removeAll();
//        jlblTotal.setVisible(false);
//        jLabel9.setVisible(false);
//
//        jlblTotal.setBounds(0, 0, 20, 20);
//        jlblTotal.setVisible(true);
//        jPanel5.add(jlblTotal);
//
//        jLabel9.setBounds(0, 0, 20, 20);
//        jLabel9.setVisible(true);
//        jPanel5.add(jLabel9);
        setTable();
    }

    private void addJlabelNetAmt() {
//        jPanel9.removeAll();
//        jlblNetAmt.setVisible(false);
//        jLabel10.setVisible(false);
//
//        jlblNetAmt.setBounds(0, 0, 20, 20);
//        jlblNetAmt.setVisible(true);
//        jPanel9.add(jlblNetAmt);
//
//        jLabel10.setBounds(0, 0, 20, 20);
//        jLabel10.setVisible(true);
//        jPanel9.add(jLabel10);
        setTable();
    }

    private void addJlabelOtherPaid() {
//        jPanel13.removeAll();
//        jlblOtherPaid.setVisible(false);
//        jLabel15.setVisible(false);
//
//        jlblOtherPaid.setBounds(0, 0, 20, 20);
//        jlblOtherPaid.setVisible(true);
//        jPanel13.add(jlblOtherPaid);
//
//        jLabel15.setBounds(0, 0, 20, 20);
//        jLabel15.setVisible(true);
//        jPanel13.add(jLabel15);
        setTable();
    }

    private void addJlabelAdvanceAmt() {
//        jPanel10.removeAll();
//        jLabel11.setVisible(false);
//        jtxtPaidAmt.setVisible(false);
//
//        jLabel11.setBounds(0, 0, 20, 20);
//        jLabel11.setVisible(true);
//        jPanel10.add(jLabel11);
//
//        jtxtPaidAmt.setBounds(0, 0, 20, 20);
//        jtxtPaidAmt.setVisible(true);
//        jPanel10.add(jtxtPaidAmt);
        setTable();
    }

    private void addJlabelRemainingAmt() {
//        jPanel11.removeAll();
//        jLabel12.setVisible(false);
//        jlblRemainingAmt.setVisible(false);
//
//        jLabel12.setBounds(0, 0, 20, 20);
//        jLabel12.setVisible(true);
//        jPanel11.add(jLabel12);
//
//        jlblRemainingAmt.setBounds(0, 0, 20, 20);
//        jlblRemainingAmt.setVisible(true);
//        jPanel11.add(jlblRemainingAmt);
        setTable();
    }

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{jtxtItem, jtxtQty, jtxtRate, jtxtAmt, jtxtDiscAmt, jtxtFinalAmt, jtxtRefBy, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblTotal, jLabel9, null});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel7, jcmbTax, jlblTaxableAmt});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel8, null, jlblTaxAmt});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel9, null, jlblAddTaxAmt});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblNetAmt, jLabel10, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblOtherPaid, jLabel15, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jtxtPaidAmt, jLabel11, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblRemainingAmt, jLabel12, null});
    }

    private boolean validateRow() {
        if (!lb.isExist("billitemmst", "bill_item_name", jtxtItem.getText(), dataConnection)) {
            navLoad.setMessage("Item name does not es=xist in database");
            jtxtItem.requestFocusInWindow();
            return false;
        }

        if (!lb.isExist("acntmst", "ac_alias", jtxtRefAlias.getText(), dataConnection)) {
            jtxtRefBy.requestFocusInWindow();
            navLoad.setMessage("Please select valid consultant doctor");
            return false;
        }

        return true;
    }

    private void setTotal() {
        double amt = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            amt += lb.isNumber(jTable1.getValueAt(i, 5).toString());
        }
        jlblNetAmt.setText(lb.Convert2DecFmtForRs(amt));
        jlblTotal.setText(lb.Convert2DecFmtForRs(amt));
        jlblRemainingAmt.setText(lb.Convert2DecFmtForRs(amt - lb.isNumber(jtxtPaidAmt) - lb.isNumber(jlblOtherPaid)));
    }

    private void clearRow() {
        jtxtItem.setText("");
        jtxtQty.setText("");
        jtxtRate.setText("");
        jtxtAmt.setText("");
        jtxtFinalAmt.setText("");
        jtxtDiscAmt.setText("");
        jtxtRefAlias.setText("");
        jtxtRefBy.setText("");
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
        jLabel1 = new javax.swing.JLabel();
        jtxtOPDNumber = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtRefNo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jlblName = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlblCity = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlblSex = new javax.swing.JLabel();
        jlblRefBy = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jlblArea = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jbtnSelPat = new javax.swing.JButton();
        jlblAppointNo = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
        jbtnPrevVou = new javax.swing.JButton();
        jPanelNavigation = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jlblNetAmt = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jlblTotal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jtxtPaidAmt = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jtxtQty = new javax.swing.JTextField();
        jtxtRate = new javax.swing.JTextField();
        jtxtAmt = new javax.swing.JTextField();
        jtxtItem = new javax.swing.JTextField();
        jtxtRefBy = new javax.swing.JTextField();
        jtxtRefAlias = new javax.swing.JTextField();
        jtxtDiscAmt = new javax.swing.JTextField();
        jtxtFinalAmt = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jlblRemainingAmt = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jlblOtherPaid = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jlblLstUpdate1 = new javax.swing.JLabel();
        jlblEditNo1 = new javax.swing.JLabel();
        jlblUserName1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jtxtRemrk = new javax.swing.JTextField();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("OPD Number");

        jtxtOPDNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtOPDNumberKeyPressed(evt);
            }
        });

        jLabel2.setText("Receipt No");

        jtxtRefNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRefNoKeyPressed(evt);
            }
        });

        jLabel7.setText("Date");

        jLabel3.setText("Name");

        jlblName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("City");

        jlblCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Sex");

        jlblSex.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblRefBy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("Ref BY");

        jLabel8.setText("Area");

        jlblArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jbtnAdd.setText("Add");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });
        jbtnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnAddKeyPressed(evt);
            }
        });

        jbtnSelPat.setText("Select Patient");
        jbtnSelPat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSelPatActionPerformed(evt);
            }
        });

        jlblAppointNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("Appointment Number");

        jbtnPrevVou.setText("Previous Voucher");
        jbtnPrevVou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPrevVouActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnAdd)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnSelPat, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jtxtOPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jbtnPrevVou, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 49, Short.MAX_VALUE))
                            .addComponent(jlblArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(403, 403, 403)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblAppointNo, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtnSelPat))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAppointNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtOPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtnPrevVou))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnAdd))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtOPDNumber});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel7, jtxtRefNo});

        jPanelNavigation.setLayout(new java.awt.BorderLayout());

        jPanel9.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblNetAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Net Amt");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(jlblNetAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblNetAmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel5.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setText("Total");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120)
                .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
            .addComponent(jLabel9)
        );

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Qty", "Rate", "Amount", "Disc", "Final", "Doc Name", "Doc Alias"
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
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel10.setPreferredSize(new java.awt.Dimension(177, 30));

        jLabel11.setText("Paid Amt");

        jtxtPaidAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPaidAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPaidAmtFocusLost(evt);
            }
        });
        jtxtPaidAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPaidAmtKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167)
                .addComponent(jtxtPaidAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jtxtPaidAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.setPreferredSize(new java.awt.Dimension(826, 30));

        jtxtQty.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jtxtQtyComponentMoved(evt);
            }
        });
        jtxtQty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtQtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtQtyFocusLost(evt);
            }
        });
        jtxtQty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtQtyKeyPressed(evt);
            }
        });

        jtxtRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRateFocusLost(evt);
            }
        });
        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRateKeyPressed(evt);
            }
        });

        jtxtAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusLost(evt);
            }
        });
        jtxtAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmtKeyPressed(evt);
            }
        });

        jtxtItem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtItemFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtItemFocusLost(evt);
            }
        });
        jtxtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtItemKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtItemKeyReleased(evt);
            }
        });

        jtxtRefBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRefByFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRefByFocusLost(evt);
            }
        });
        jtxtRefBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRefByKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtRefByKeyReleased(evt);
            }
        });

        jtxtDiscAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscAmtFocusLost(evt);
            }
        });
        jtxtDiscAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscAmtKeyPressed(evt);
            }
        });

        jtxtFinalAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFinalAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFinalAmtFocusLost(evt);
            }
        });
        jtxtFinalAmt.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jtxtFinalAmtComponentMoved(evt);
            }
        });
        jtxtFinalAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFinalAmtKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtxtRefAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtItem, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtDiscAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtFinalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRefAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDiscAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFinalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanel11.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblRemainingAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setText("Due Amt");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(jlblRemainingAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblRemainingAmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel13.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblOtherPaid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setText("Other Paid Amt");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(150, 150, 150)
                .addComponent(jlblOtherPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblOtherPaid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "bill_grp_cd", "Bill Item Group"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setMinWidth(0);
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable3.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable3.getColumnModel().getColumn(1).setResizable(false);
        }

        jPanel6.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "bill_item_cd", "Bill Item Name", "Rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
        jScrollPane3.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(0);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanel7.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel20.setText("User:");

        jLabel21.setText("Edit No:");

        jLabel22.setText("Last Updated:");

        jlblLstUpdate1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblUserName1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblLstUpdate1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jlblEditNo1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jlblUserName1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jlblLstUpdate1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jlblEditNo1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setText("Remark");

        jtxtRemrk.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRemrkFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRemrkFocusLost(evt);
            }
        });
        jtxtRemrk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemrkKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtRemrk))
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtRemrk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel13, jtxtRemrk});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtOPDNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPDNumberKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            SwingWorker workerForjbtnGenerate = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    try {
                        addGlassPane();
                        setData();
                        jtxtItem.requestFocusInWindow();
                    } catch (Exception ex) {
                        lb.printToLogFile("Exception at saveVoucher at save area master", ex);
                    } finally {
                        removeGlassPane();
                    }
                    return null;
                }
            };
            workerForjbtnGenerate.execute();
        }
    }//GEN-LAST:event_jtxtOPDNumberKeyPressed

    private void jtxtRefNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefNoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtRefNoKeyPressed

    private void jtxtQtyComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtQtyComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jtxtQtyComponentMoved

    private void jtxtQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyPressed
        // TODO add your handling code here:
        //        lb.onlyNumber(evt, 3);
        lb.enterFocus(evt, jtxtRate);
    }//GEN-LAST:event_jtxtQtyKeyPressed

    private void jtxtRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jtxtAmt.setText((Double.parseDouble(jtxtQty.getText()) * Double.parseDouble(jtxtRate.getText())) + "");
            jtxtAmt.requestFocusInWindow();
        }
        //        lb.onlyNumber(evt, jtxtRate.getText().length()+1);
    }//GEN-LAST:event_jtxtRateKeyPressed

    private void jtxtAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtDiscAmt);
    }//GEN-LAST:event_jtxtAmtKeyPressed

    private void jtxtItemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtItemFocusLost

    private void jtxtItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemKeyPressed
        // TODO add your handling code here:
        itemPicklist.setLocation(jtxtItem.getX() + jPanel4.getX() + jPanel3.getX(), jtxtItem.getY() + jtxtItem.getHeight() + jPanel4.getY() + jPanel3.getY());
        itemPicklist.pickListKeyPress(evt);
        lb.downFocus(evt, jtxtPaidAmt);
    }//GEN-LAST:event_jtxtItemKeyPressed

    private void jtxtItemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtItemFocusGained

    private void jtxtItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemKeyReleased
        // TODO add your handling code here:
        try {
            if (jTable3.getSelectedRow() != -1) {
                String sql = "SELECT bill_item_name,1 as qty,def_rate FROM billitemmst"
                        + " WHERE bill_item_name LIKE '" + jtxtItem.getText().toUpperCase() + "%'"
                        + " and bill_grp_cd=" + lb.getbillGrpCode(jTable3.getValueAt(jTable3.getSelectedRow(), 1).toString(), "C");
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                itemPicklist.setPreparedStatement(pstLocal);
                itemPicklist.setFirstAssociation(new int[]{0, 1, 2});
                itemPicklist.setSecondAssociation(new int[]{0, 1, 2});
                itemPicklist.setValidation(dataConnection.prepareStatement("SELECT bill_item_name FROM billitemmst WHERE bill_item_name =? "
                        + "and bill_grp_cd=" + lb.getbillGrpCode(jTable3.getValueAt(jTable3.getSelectedRow(), 1).toString(), "C")));
                itemPicklist.pickListKeyRelease(evt);
            }
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_jtxtItemKeyReleased

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        // TODO add your handling code here:
        if (validateRow()) {
            int selRow = jTable1.getSelectedRow();
            if (selRow == -1) {
                Vector row = new Vector();
                row.add(jtxtItem.getText());
                row.add((int) lb.isNumber(jtxtQty));
                row.add(lb.isNumber(jtxtRate));
                row.add(lb.isNumber(jtxtAmt));
                row.add(lb.isNumber(jtxtDiscAmt));
                row.add(lb.isNumber(jtxtFinalAmt));
                row.add(jtxtRefBy.getText());
                row.add(jtxtRefAlias.getText());
                dtm.addRow(row);
            } else {
                jTable1.setValueAt(jtxtItem.getText(), selRow, 0);
                jTable1.setValueAt((int) lb.isNumber(jtxtQty), selRow, 1);
                jTable1.setValueAt(lb.isNumber(jtxtRate), selRow, 2);
                jTable1.setValueAt(lb.isNumber(jtxtAmt), selRow, 3);
                jTable1.setValueAt(lb.isNumber(jtxtDiscAmt), selRow, 4);
                jTable1.setValueAt(lb.isNumber(jtxtFinalAmt), selRow, 5);
                jTable1.setValueAt(jtxtRefBy.getText(), selRow, 6);
                jTable1.setValueAt(jtxtRefAlias.getText(), selRow, 7);
            }
            setTotal();
            clearRow();
            lb.confirmDialog("Do you want to add another row?");
            if (lb.type) {
                jtxtItem.requestFocusInWindow();
            } else {
                jtxtDiscAmt.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jtxtPaidAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPaidAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPaidAmtFocusGained

    private void jtxtPaidAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPaidAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtPaidAmtFocusLost

    private void jtxtPaidAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPaidAmtKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jtxtRemrk.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtPaidAmtKeyPressed

    private void jtxtQtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtQtyFocusGained

    private void jtxtRateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateFocusGained

    private void jtxtAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtFocusGained

    private void jtxtQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtQtyFocusLost

    private void jtxtRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtRateFocusLost

    private void jtxtAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtAmtFocusLost

    private void jtxtRefByFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRefByFocusGained

    private void jtxtRefByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtRefByFocusLost

    private void jtxtRefByKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyPressed
        acPickList.setLocation(jtxtRefBy.getX() + jPanel4.getX() + jPanel3.getX(), jtxtRefBy.getY() + jtxtRefBy.getHeight() + jPanel1.getY() + jPanel4.getY() + jPanel3.getY());
        acPickList.setReturnComponent(new JTextField[]{jtxtRefBy, jtxtRefAlias});
        acPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtRefByKeyPressed

    private void jtxtRefByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("SELECT ac_name,ac_alias FROM acntmst "
                    + " WHERE ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '%" + jtxtRefBy.getText().toUpperCase() + "%'");
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1});
            acPickList.setSecondAssociation(new int[]{0, 1});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtRefByKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                jtxtItem.setText(jTable1.getValueAt(row, 0).toString());
                jtxtQty.setText(jTable1.getValueAt(row, 1).toString());
                jtxtRate.setText(jTable1.getValueAt(row, 2).toString());
                jtxtAmt.setText(jTable1.getValueAt(row, 3).toString());
                jtxtDiscAmt.setText(jTable1.getValueAt(row, 4).toString());
                jtxtFinalAmt.setText(jTable1.getValueAt(row, 5).toString());
                jtxtRefBy.setText(jTable1.getValueAt(row, 6).toString());
                jtxtRefAlias.setText(jTable1.getValueAt(row, 7).toString());
                jtxtItem.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                lb.confirmDialog("Do you want to delte this row?");
                if (lb.type) {
                    dtm.removeRow(row);
                    setTotal();
                    clearRow();
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jbtnSelPatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSelPatActionPerformed
        // TODO add your handling code here:
        JDialog d = new JDialog();
        d.setModal(true);
        SearchPatient sp = new SearchPatient(2, d);
        sp.setOpdBill(this);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("Search Patient");
        d.add(sp);
        d.setPreferredSize(new Dimension(sp.getWidth() + 20, sp.getHeight()));
        d.setLocationRelativeTo(this);
        d.setAlwaysOnTop(true);
        sp.setVisible(true);
        d.pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
        d.setVisible(true);
        jtxtOPDNumber.setText(sp.opd_no);
        setData();
        jlblAppointNo.setText("");
    }//GEN-LAST:event_jbtnSelPatActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int selRow = jTable3.getSelectedRow();
            if (selRow != -1) {
                int code = (int) lb.isNumber(jTable3.getValueAt(selRow, 0).toString());
                try {
                    String sql = "SELECT bill_item_cd,bill_item_name,def_rate FROM billitemmst WHERE bill_grp_cd=" + code;
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtmBillItem.setRowCount(0);
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        row.add(rsLocal.getInt("bill_item_cd"));
                        row.add(rsLocal.getString("bill_item_name"));
                        row.add(rsLocal.getDouble("def_rate"));
                        dtmBillItem.addRow(row);
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at load data for item from group", ex);
                }
            }
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int selRow = jTable2.getSelectedRow();
            if (selRow != -1) {
                jtxtItem.setText(jTable2.getValueAt(selRow, 1).toString());
                jtxtQty.setText("1");
                jtxtRate.setText(jTable2.getValueAt(selRow, 2).toString());
                jtxtQty.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jtxtDiscAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDiscAmtFocusGained

    private void jtxtDiscAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        jtxtFinalAmt.setText((Double.parseDouble(jtxtAmt.getText()) - Double.parseDouble(jtxtDiscAmt.getText())) + "");
        jtxtFinalAmt.requestFocusInWindow();
    }//GEN-LAST:event_jtxtDiscAmtFocusLost

    private void jtxtDiscAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscAmtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtFinalAmt);
    }//GEN-LAST:event_jtxtDiscAmtKeyPressed

    private void jtxtFinalAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFinalAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFinalAmtFocusGained

    private void jtxtFinalAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFinalAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtFinalAmtFocusLost

    private void jtxtFinalAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFinalAmtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtRefBy);
    }//GEN-LAST:event_jtxtFinalAmtKeyPressed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jtxtRemrkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemrkFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRemrkFocusGained

    private void jtxtRemrkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemrkFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtRemrkFocusLost

    private void jtxtRemrkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemrkKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtRemrkKeyPressed

    private void jbtnPrevVouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrevVouActionPerformed
        // TODO add your handling code here:
        JDialog d = new JDialog();
        d.setModal(true);
        OPDOldVoucher sp = new OPDOldVoucher(d, this);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("Indor Patient List");
        d.add(sp);
        d.setPreferredSize(new Dimension(sp.getWidth() + 20, sp.getHeight()));
        d.setLocationRelativeTo(this);
        d.setAlwaysOnTop(true);
        sp.setVisible(true);
        d.pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
        d.setVisible(true);
    }//GEN-LAST:event_jbtnPrevVouActionPerformed

    private void jtxtFinalAmtComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtFinalAmtComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jtxtFinalAmtComponentMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnPrevVou;
    private javax.swing.JButton jbtnSelPat;
    private javax.swing.JLabel jlblAppointNo;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblEditNo1;
    private javax.swing.JLabel jlblLstUpdate1;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblNetAmt;
    private javax.swing.JLabel jlblOtherPaid;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblRemainingAmt;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblUserName1;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JTextField jtxtDiscAmt;
    private javax.swing.JTextField jtxtFinalAmt;
    private javax.swing.JTextField jtxtItem;
    public javax.swing.JTextField jtxtOPDNumber;
    private javax.swing.JTextField jtxtPaidAmt;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRefAlias;
    private javax.swing.JTextField jtxtRefBy;
    private javax.swing.JTextField jtxtRefNo;
    private javax.swing.JTextField jtxtRemrk;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables

    public void setID(String strCode) {
        this.ref_no = strCode;
        navLoad.setVoucher("Edit");
    }
}
