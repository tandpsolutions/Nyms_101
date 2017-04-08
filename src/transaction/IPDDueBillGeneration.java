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
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.PickList;
import support.ReportTable;

/**
 *
 * @author Bhaumik
 */
public class IPDDueBillGeneration extends javax.swing.JInternalFrame {

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
    ReportTable table = null;
    public String tableDt = "ipdbilldt";
    /**
     * Creates new form OPDBillGeneration
     */
    public IPDDueBillGeneration(String ipdNUmber) {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmBillGroup = (DefaultTableModel) jTable3.getModel();
        dtmBillItem = (DefaultTableModel) jTable2.getModel();
        addInitialData();
        addNavigationPanel();
        lb.setUserRightsToPanel1(navLoad, "28");
        navLoad.callNew();
        jlblIPDNumber.setText(ipdNUmber);
        setData(ipdNUmber);
        setPickListView();
        addJtextBox();
        addJlabel();
        //        addJlabelNetAmt();
        jPanel9.setVisible(false);
//        addJlabelAdvanceAmt();
        jPanel10.setVisible(false);
//        addJlabelRemainingAmt();
        jPanel11.setVisible(false);
        if(HMSHome.role != 1){
            jbtnOldVoucher.setVisible(false);
        }
    }

    public IPDDueBillGeneration() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmBillGroup = (DefaultTableModel) jTable3.getModel();
        dtmBillItem = (DefaultTableModel) jTable2.getModel();
        addInitialData();
        addNavigationPanel();
        setPickListView();
        addJtextBox();
        addJlabel();
//        addJlabelNetAmt();
        jPanel9.setVisible(false);
//        addJlabelAdvanceAmt();
        jPanel10.setVisible(false);
//        addJlabelRemainingAmt();
        jPanel11.setVisible(false);
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "28");
        if(HMSHome.role != 1){
            jbtnOldVoucher.setVisible(false);
        }
    }

    private void addInitialData() {
        try {
            String sql = "SELECT bill_grp_cd,bill_group_name FROM billgrpmst";
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

    private void setData(String ipd_no) {
        try {
            jlblIPDNumber.setText(ipd_no);
            String sql = "SELECT p.con_doc,p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name  \n"
                    + "FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd  LEFT JOIN areamst a ON p1.area_cd=a.area_cd \n"
                    + "LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd WHERE i.ipd_no='" + ipd_no + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblOPDNUmber.setText(" " + rsLcoal.getString("opd_no"));
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));
                jlblHeadDoc.setText(" " + lb.getAcCode(rsLcoal.getString("con_doc"), "N"));
            }

            sql = "SELECT p.pt_name,w.ward_name,r.room_cd FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                    + " LEFT JOIN roommst r ON p.opd_no = r.opd_no LEFT JOIN wardmst w ON r.ward_cd=w.ward_cd WHERE dis_date IS NULL "
                    + " AND i.opd_no=(SELECT opd_no FROM patientmst WHERE ref_opd_no='" + jlblOPDNUmber.getText() + "') ";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblRelative.setText(rsLcoal.getString("pt_name") + " is admitted in " + rsLcoal.getString("ward_name") + " ward at bed no." + rsLcoal.getString("room_cd"));
            }

            jlblTotalBill.setText(lb.Convert2DecFmtForRs(lb.isNumber(lb.getData("SELECT SUM(final_amt) FROM ipdbilldt WHERE is_del = 0 "
                    + "AND ipd_no='" + ipd_no + "'"))));

            jlblAdvance.setText(lb.Convert2DecFmtForRs(lb.isNumber(lb.getData("SELECT SUM(amount) FROM ipdpaymenthd WHERE amount >0 AND "
                    + "ipd_no='" + ipd_no + "'"))));

            jlblRefund.setText(lb.Convert2DecFmtForRs(lb.isNumber(lb.getData("SELECT SUM(amount) FROM ipdpaymenthd WHERE amount <0 AND "
                    + "ipd_no='" + ipd_no + "'"))));

            jlblDueAmount.setText(lb.Convert2DecFmtForRs(lb.isNumber(jlblTotalBill) - lb.isNumber(jlblAdvance) - lb.isNumber(jlblRefund)));

        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Ref NO", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "IPD Number", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Date", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Patient Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {

                int edit_no = (int) lb.isNumber(lb.getData("edit_no", "ipdbilldt", "ref_no", ref_no, 0));
                if (getMode().equalsIgnoreCase("E")) {
                    lb.generateLog("ipdbilldt", "ipdbilldtlg", "ref_no", ref_no);
                    edit_no += 1;
                }
                String sql = "delete from ipdbilldt where ref_no=?";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ref_no);
                pstLocal.executeUpdate();

                sql = "insert into ipdbilldt (ipd_no,ref_no,v_date,bill_item_cd,qty,rate,amt,disc,final_amt,doc_cd,edit_no,user_id) values(?,?,?,?,?,?,?,?,?,?," + edit_no + ",?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, jlblIPDNumber.getText());
                if (ref_no.equalsIgnoreCase("")) {
                    ref_no = lb.generateKey("ipdbilldt", "ref_no", 7, "");
                }
                pstLocal.setString(2, ref_no);
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    pstLocal.setString(3, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    pstLocal.setString(4, lb.getbillitemCode(jTable1.getValueAt(i, 0).toString(), "C"));
                    pstLocal.setInt(5, (int) lb.isNumber(jTable1.getValueAt(i, 1).toString()));
                    pstLocal.setDouble(6, lb.isNumber(jTable1.getValueAt(i, 2).toString()));
                    pstLocal.setDouble(7, lb.isNumber(jTable1.getValueAt(i, 3).toString()));
                    pstLocal.setDouble(8, lb.isNumber(jTable1.getValueAt(i, 4).toString()));
                    pstLocal.setDouble(9, lb.isNumber(jTable1.getValueAt(i, 5).toString()));
                    pstLocal.setString(10, lb.getAcCode(jTable1.getValueAt(i, 7).toString(), "AC"));
                    pstLocal.setString(11, HMSHome.user_id + "");
                    pstLocal.executeUpdate();
                }
            }

            @Override
            public void callDelete() throws Exception {
                lb.confirmDialog("Do you want to delete this voucher?");
                if (lb.type) {
                    String sql = "update ipdbilldt set is_del = 1,user_id =" + HMSHome.user_id + " where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();
                }
            }

            @Override
            public void callView() {
                String sql = "SELECT i1.ref_no,i1.ipd_no,i1.v_date,p.pt_name FROM ipdbilldt i1 LEFT JOIN ipdreg i ON i.ipd_no=i1.ipd_no "
                        + " LEFT JOIN patientmst p ON i.opd_no=p.opd_no where i1.is_del = 0 GROUP BY ref_no,v_date,pt_name";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "Ipd Due Billing View", sql, "23", 1, IPDDueBillGeneration.this, "Ipd Due Billing View", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Patient Master View", -1);
            }

            @Override
            public void callPrint() {
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabled(false);
                lb.setUserRightsToPanel1(navLoad, "28");
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,b.ref_no as ref,b.user_id as user,b.edit_no as edit,b.time_stamp as time from ipdreg a left join "+tableDt+" b on a.ipd_no=b.ipd_no where b.is_del = 0 and b.ref_no=(select min(ref_no) from "+tableDt+" where is_del = 0)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,b.ref_no as ref,b.user_id as user,b.edit_no as edit,b.time_stamp as time from ipdreg a left join "+tableDt+" b on a.ipd_no=b.ipd_no where b.is_del = 0 and b.ref_no=(select max(ref_no) from "+tableDt+" where is_del = 0 and ref_no <'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,b.ref_no as ref,b.user_id as user,b.edit_no as edit,b.time_stamp as time from ipdreg a left join "+tableDt+" b on a.ipd_no=b.ipd_no where b.is_del = 0 and b.ref_no=(select min(ref_no) from "+tableDt+" where is_del = 0 and ref_no >'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,b.ref_no as ref,b.user_id as user,b.edit_no as edit,b.time_stamp as time from ipdreg a left join "+tableDt+" b on a.ipd_no=b.ipd_no where b.is_del = 0 and b.ref_no=(select max(ref_no) from "+tableDt+" where is_del = 0)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("select *,b.doc_cd as doc,b.ref_no as ref,b.user_id as user,b.edit_no as edit,b.time_stamp as time from ipdreg a left join "+tableDt+" b on a.ipd_no=b.ipd_no where b.is_del = 0 and b.ref_no ='" + ref_no + "' and is_del = 0");
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
                Component[] cont = jPanel1.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JLabel) {
                        if (((JLabel) cont[i]).getBorder() != null) {
                            ((JLabel) cont[i]).setText("");
                        }
                    } else if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setText("");
                    }
                }
                lb.setDateChooserPropertyInit(jtxtVdate);
                ref_no = "";
            }

            @Override
            public void setComponentEnabled(boolean bFlag) {
                jtxtRefNo.setEnabled(!bFlag);
                if (getMode().equalsIgnoreCase("N")) {
                    jtxtVdate.setEnabled(bFlag);
//                    jBillDateBtn.setEnabled(bFlag);
                } else {
                    jtxtVdate.setEnabled(bFlag);
//                    jBillDateBtn.setEnabled(bFlag);
//                    jtxtVdate.setEnabled(false);
//                    jBillDateBtn.setEnabled(false);
                }
                jtxtItem.setEnabled(bFlag);
                jtxtQty.setEnabled(bFlag);
                jtxtRate.setEnabled(bFlag);
                jtxtAmt.setEnabled(bFlag);
                jtxtRefAlias.setEnabled(bFlag);
                jtxtRefBy.setEnabled(bFlag);
                jtxtDiscAmt.setEnabled(bFlag);
                jtxtFinalAmt.setEnabled(bFlag);
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                ref_no = viewDataRs.getString("ref");
                jtxtRefNo.setText(viewDataRs.getString("ref"));
                jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("v_date")));
                jlblOPDNUmber.setText(viewDataRs.getString("opd_no"));
                jlblIPDNumber.setText(viewDataRs.getString("ipd_no"));
                setData(viewDataRs.getString("ipd_no"));
                jlblEditNo.setText(viewDataRs.getString("edit"));
                jlblLstUpdate.setText(viewDataRs.getString("time"));
                jlblUserName.setText(lb.getUserName(viewDataRs.getString("user"), "N"));

                dtm.setRowCount(0);
                Vector row = new Vector();
                row.add(lb.getbillitemCode(viewDataRs.getString("bill_item_cd"), "N"));
                row.add(viewDataRs.getInt("qty"));
                row.add(viewDataRs.getDouble("rate"));
                row.add(viewDataRs.getDouble("amt"));
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
                    row.add(viewDataRs.getDouble("amt"));
                    row.add(viewDataRs.getDouble("disc"));
                    row.add(viewDataRs.getDouble("final_amt"));
                    row.add(lb.getAcCode(viewDataRs.getString("doc"), "N"));
                    row.add(lb.getAcCode(viewDataRs.getString("doc"), "CA"));
                    dtm.addRow(row);
                }
                setTotal();
            }

            @Override
            public boolean checkEdit() {
                if (HMSHome.role != 1) {
                    int edt_no = (int) lb.isNumber(jlblEditNo);
                    if (edt_no < 1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return lb.getData("ref_no", "ipdreg", "ipd_no", jlblIPDNumber.getText(), 0).equalsIgnoreCase("");
                }
            }

            @Override
            public boolean validateForm() {
                if (lb.isBlank(jlblIPDNumber)) {
                    navLoad.setMessage("IPD Number can not be left blank");
                    return false;
                }
                if (jTable1.getRowCount() == 0) {
                    jtxtItem.requestFocusInWindow();
                    navLoad.setMessage("Voucher can not be left blank.");
                    return false;
                }

                if (lb.checkDate2(jtxtVdate)) {
                    navLoad.setMessage("Invalid Date");
                    jtxtVdate.requestFocusInWindow();
                    return false;
                }
                return true;
            }

        }
        navLoad = new navPanel();
        jPanelNavigation.add(navLoad);
        navLoad.setVisible(true);
    }

    private void addJtextBox() {
        jPanel4.removeAll();
        jtxtItem.setVisible(false);
        jtxtQty.setVisible(false);
        jtxtRate.setVisible(false);
        jtxtAmt.setVisible(false);
        jtxtDiscAmt.setVisible(false);
        jtxtFinalAmt.setVisible(false);
        jtxtRefAlias.setVisible(false);
        jtxtRefBy.setVisible(false);

        jtxtItem.setBounds(0, 0, 20, 20);
        jtxtItem.setVisible(true);
        jPanel4.add(jtxtItem);

        jtxtQty.setBounds(0, 0, 20, 20);
        jtxtQty.setVisible(true);
        jPanel4.add(jtxtQty);

        jtxtRate.setBounds(0, 0, 20, 20);
        jtxtRate.setVisible(true);
        jPanel4.add(jtxtRate);

        jtxtAmt.setBounds(0, 0, 20, 20);
        jtxtAmt.setVisible(true);
        jPanel4.add(jtxtAmt);

        jtxtDiscAmt.setBounds(0, 0, 20, 20);
        jtxtDiscAmt.setVisible(true);
        jPanel4.add(jtxtDiscAmt);

        jtxtFinalAmt.setBounds(0, 0, 20, 20);
        jtxtFinalAmt.setVisible(true);
        jPanel4.add(jtxtFinalAmt);

        jtxtRefBy.setBounds(0, 0, 20, 20);
        jtxtRefBy.setVisible(true);
        jPanel4.add(jtxtRefBy);

        setTable();
    }

    private void addJlabel() {
        jPanel5.removeAll();
        jlblTotal.setVisible(false);
        jLabel9.setVisible(false);

        jlblTotal.setBounds(0, 0, 20, 20);
        jlblTotal.setVisible(true);
        jPanel5.add(jlblTotal);

        jLabel9.setBounds(0, 0, 20, 20);
        jLabel9.setVisible(true);
        jPanel5.add(jLabel9);
        setTable();
    }

    private void addJlabelNetAmt() {
        jPanel9.removeAll();
        jlblNetAmt.setVisible(false);
        jLabel10.setVisible(false);

        jlblNetAmt.setBounds(0, 0, 20, 20);
        jlblNetAmt.setVisible(true);
        jPanel9.add(jlblNetAmt);

        jLabel10.setBounds(0, 0, 20, 20);
        jLabel10.setVisible(true);
        jPanel9.add(jLabel10);
        setTable();
    }

    private void addJlabelAdvanceAmt() {
        jPanel10.removeAll();
        jLabel11.setVisible(false);
        jtxtDiscount.setVisible(false);

        jLabel11.setBounds(0, 0, 20, 20);
        jLabel11.setVisible(true);
        jPanel10.add(jLabel11);

        jtxtDiscount.setBounds(0, 0, 20, 20);
        jtxtDiscount.setVisible(true);
        jPanel10.add(jtxtDiscount);
        setTable();
    }

    private void addJlabelRemainingAmt() {
        jPanel11.removeAll();
        jLabel12.setVisible(false);
        jlblRemainingAmt.setVisible(false);

        jLabel12.setBounds(0, 0, 20, 20);
        jLabel12.setVisible(true);
        jPanel11.add(jLabel12);

        jlblRemainingAmt.setBounds(0, 0, 20, 20);
        jlblRemainingAmt.setVisible(true);
        jPanel11.add(jlblRemainingAmt);
        setTable();
    }

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{jtxtItem, jtxtQty, jtxtRate, jtxtAmt, jtxtDiscAmt, jtxtFinalAmt, jtxtRefBy, null});
        lb.setTable(jTable1, new JComponent[]{null, jLabel9, null, null, null, jlblTotal, null, null});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel7, jcmbTax, jlblTaxableAmt});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel8, null, jlblTaxAmt});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel9, null, jlblAddTaxAmt});
//        lb.setTable(jTable1, new JComponent[]{null, jLabel10, null, jlblNetAmt, null, null});
//        lb.setTable(jTable1, new JComponent[]{null, jLabel11, null, jtxtDiscount, null, null});
//        lb.setTable(jTable1, new JComponent[]{null, jLabel12, null, jlblRemainingAmt, null, null});
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
//        jlblRemainingAmt.setText(lb.Convert2DecFmtForRs(amt - lb.isNumber(jtxtPaidAmt)));
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
        jlblOPDNUmber = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jlblIPDNumber = new javax.swing.JLabel();
        jlblRelative = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jlblHeadDoc = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
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
        jtxtDiscount = new javax.swing.JTextField();
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
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jlblTotalBill = new javax.swing.JLabel();
        jlblAdvance = new javax.swing.JLabel();
        jlblRefund = new javax.swing.JLabel();
        jlblDueAmount = new javax.swing.JLabel();
        jbtnOldVoucher = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("IPD Number");

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

        jlblOPDNUmber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("OPD NUmber");

        jlblIPDNumber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblRelative.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton1.setText("Day Wise Billing");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jlblHeadDoc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel21.setText("Treating Doctor");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblIPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblRelative, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblHeadDoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jlblOPDNUmber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(157, 157, 157))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap())))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtnAdd)
                                .addGap(110, 110, 110))))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel5, jLabel6});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButton1)
                    .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlblIPDNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jlblRelative, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblOPDNUmber, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblHeadDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnAdd)))
                .addGap(1, 1, 1))
        );

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
                .addGap(0, 6, Short.MAX_VALUE))
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
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel10.setPreferredSize(new java.awt.Dimension(177, 30));

        jLabel11.setText("Discount");

        jtxtDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscountFocusLost(evt);
            }
        });
        jtxtDiscount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscountKeyPressed(evt);
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
                .addComponent(jtxtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jtxtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(171, Short.MAX_VALUE))
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

        jLabel12.setText("Remaining");

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
                .addGap(0, 6, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setText("Total Bill");

        jLabel15.setText("Advance");

        jLabel16.setText("Refund");

        jLabel17.setText("Due Amount");

        jlblTotalBill.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblAdvance.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblRefund.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblDueAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jbtnOldVoucher.setText("Show Old Voucher");
        jbtnOldVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOldVoucherActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnOldVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblDueAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAdvance, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotalBill, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(187, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotalBill, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAdvance, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblDueAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnOldVoucher)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel13, jlblTotalBill});

        jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel15, jlblAdvance});

        jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel16, jlblRefund});

        jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel17, jlblDueAmount});

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel18.setText("User:");

        jLabel19.setText("Edit No:");

        jLabel20.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 712, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtRefNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefNoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtRefNoKeyPressed

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
                jTable1.clearSelection();
            }
            setTotal();
            clearRow();
            lb.confirmDialog("Do you want to add another row?");
            if (lb.type) {
                jtxtItem.requestFocusInWindow();
            } else {
                navLoad.setSaveFocus();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jtxtDiscountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscountFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDiscountFocusGained

    private void jtxtDiscountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscountFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtDiscountFocusLost

    private void jtxtDiscountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscountKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtDiscountKeyPressed

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

    private void jtxtQtyComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtQtyComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jtxtQtyComponentMoved

    private void jtxtQtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtQtyFocusGained

    private void jtxtQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtQtyFocusLost

    private void jtxtQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyPressed
        // TODO add your handling code here:
        //        lb.onlyNumber(evt, 3);
        lb.enterFocus(evt, jtxtRate);
    }//GEN-LAST:event_jtxtQtyKeyPressed

    private void jtxtRateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateFocusGained

    private void jtxtRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtRateFocusLost

    private void jtxtRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jtxtAmt.setText((Double.parseDouble(jtxtQty.getText()) * Double.parseDouble(jtxtRate.getText())) + "");
            jtxtAmt.requestFocusInWindow();
        }
        //        lb.onlyNumber(evt, jtxtRate.getText().length()+1);
    }//GEN-LAST:event_jtxtRateKeyPressed

    private void jtxtAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtFocusGained

    private void jtxtAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtAmtFocusLost

    private void jtxtAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtDiscAmt);
    }//GEN-LAST:event_jtxtAmtKeyPressed

    private void jtxtItemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtItemFocusGained

    private void jtxtItemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtItemFocusLost

    private void jtxtItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemKeyPressed
        // TODO add your handling code here:
        itemPicklist.setLocation(jtxtItem.getX() + jPanel4.getX() + jPanel3.getX(), jtxtItem.getY() + jtxtItem.getHeight() + jPanel4.getY() + jPanel3.getY());
        itemPicklist.pickListKeyPress(evt);
//        lb.downFocus(evt, jtxtPaidAmt);
    }//GEN-LAST:event_jtxtItemKeyPressed

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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        IPDDayWiseBilling ipd = new IPDDayWiseBilling(null, true, jlblIPDNumber.getText());
        ipd.flag = false;
        ipd.setLocationRelativeTo(null);
        ipd.show();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jbtnOldVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOldVoucherActionPerformed
        // TODO add your handling code here:
        IPDDueBillGeneration ipd = new IPDDueBillGeneration();
        ipd.tableDt="ipdbilldtlg";
        ipd.setID(ref_no);
        HMSHome.addOnScreen(ipd, "Old IPD Due Billing", -1);
    }//GEN-LAST:event_jbtnOldVoucherActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnOldVoucher;
    private javax.swing.JLabel jlblAdvance;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblDueAmount;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblHeadDoc;
    private javax.swing.JLabel jlblIPDNumber;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblNetAmt;
    private javax.swing.JLabel jlblOPDNUmber;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblRefund;
    private javax.swing.JLabel jlblRelative;
    private javax.swing.JLabel jlblRemainingAmt;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblTotalBill;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JTextField jtxtDiscAmt;
    private javax.swing.JTextField jtxtDiscount;
    private javax.swing.JTextField jtxtFinalAmt;
    private javax.swing.JTextField jtxtItem;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRefAlias;
    private javax.swing.JTextField jtxtRefBy;
    private javax.swing.JTextField jtxtRefNo;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables

    public void setID(String strCode) {
        this.ref_no = strCode;
        navLoad.setVoucher("Edit");
    }
}
