/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.CursorGlassPane;
import hms.HMS101;
import hms.HMSHome;
import static hms.HMSHome.addOnScreen;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import utility.VoucherDisplay;

/**
 *
 * @author Bhaumik
 */
public class IPDBillGenerationDischarge extends javax.swing.JInternalFrame {

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
    public String tableHD = "ipdreg", tableDT = "ipdpaymenthd";

    /**
     * Creates new form OPDBillGeneration
     */
    public IPDBillGenerationDischarge(String ipdNUmber, int status) {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        jComboBox1.setSelectedIndex(status);
        addInitialData();
        addNavigationPanel();
        lb.setUserRightsToPanel1(navLoad, "210");
        navLoad.callNew();
        jlblIPDNumber.setText(ipdNUmber);
        jlblOPDNUmber.setText(lb.getData("opd_no", "ipdreg", "ipd_no", ipdNUmber, 0));
        setData(ipdNUmber);
        setPickListView();
        addJtextBox();
        addJlabel();
        addJlabelNetAmt();
        addJlabelAdvance();
        addJlabelRefund();
        addJlabelDiscountAmt();
        addJlabelRemainingAmt();
        addJlabelPaidAmt();
        addJlabelDueAmt();
        jPanel4.setVisible(false);
//        jPanel10.setVisible(false);
        if (HMSHome.role != 1) {
            jbtnRefund.setVisible(false);
        }

    }

    private void showNote(String ipd_no) {
        try {
            String sql = "select * from notemst where ipd_no='" + ipd_no + "' and is_del =0";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            String note = "";
            while (rsLocal.next()) {
                note += "Note : ";
                note += rsLocal.getString("note").replaceAll("\n", " ");
                note += "    User : " + lb.getUserName(rsLocal.getString("user_id"), "N") + "\n";
            }
            if (!note.isEmpty()) {
                lb.showMessageDailog(note);
            }
        } catch (Exception ex) {

        }
    }

    public IPDBillGenerationDischarge() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        addInitialData();
        addNavigationPanel();
        setPickListView();
        addJtextBox();
        addJlabel();
        addJlabelNetAmt();
        addJlabelAdvance();
        addJlabelRefund();
        addJlabelDiscountAmt();
        addJlabelRemainingAmt();
        addJlabelPaidAmt();
        addJlabelDueAmt();
//        jPanel4.setVisible(false);
//        jPanel10.setVisible(false);
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "210");
        if (HMSHome.role != 1) {
            jbtnRefund.setVisible(false);
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
        acPickList.setNextComponent(jtxtDiscount);
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
            String sql = "SELECT SUM(amount) FROM ipdpaymenthd WHERE amount > 0 and ipd_no='" + ipd_no + "' AND ref_no <>'" + ref_no + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblAdvanceAmt.setText(lb.Convert2DecFmtForRs(lb.isNumber(rsLcoal.getString(1))));
            }

            sql = "SELECT SUM(amount) FROM ipdpaymenthd WHERE amount < 0 and ipd_no='" + ipd_no + "' AND ref_no <>'" + ref_no + "'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblRefund.setText(lb.Convert2DecFmtForRs(Math.abs(lb.isNumber(rsLcoal.getString(1)))));
            }
            sql = "seLECT i.is_medi,i.dis_user_id,i.dis_edit_no,i.dis_time_stamp,i1.sr_no,i1.ref_no,p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name,"
                    + " i1.bill_item_cd,i1.qty,i1.rate,i1.amt,i1.doc_cd,i1.disc,i1.final_amt FROM " + tableHD + " i LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no "
                    + " LEFT JOIN patientmst p ON i.opd_no=p.opd_no LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no "
                    + " LEFT JOIN citymst c ON p1.city_cd = c.city_cd  LEFT JOIN areamst a ON p1.area_cd=a.area_cd "
                    + " LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd WHERE i1.is_del=0 and (i.ipd_no='" + ipd_no + "'"
                    + " OR i.ipd_no in (SELECT ipd_no FROM ipdreg WHERE ipd_no >= '" + ipd_no + "' AND "
                    + " opd_no in(SELECT opd_no FROM patientmst WHERE ref_opd_no='" + jlblOPDNUmber.getText() + "'))) order by opd_no";
            pstLocal = dataConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblOPDNUmber.setText(rsLcoal.getString("opd_no"));
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));

                dtm.setRowCount(0);
                Vector row = new Vector();
                row.add(lb.getbillitemCode(rsLcoal.getString("bill_item_cd"), "N"));
                row.add(rsLcoal.getString("qty"));
                row.add(rsLcoal.getString("rate"));
                row.add(rsLcoal.getString("amt"));
                row.add(rsLcoal.getString("disc"));
                row.add(rsLcoal.getString("final_amt"));
                row.add(lb.getAcCode(rsLcoal.getString("doc_cd"), "N"));
                row.add(lb.getAcCode(rsLcoal.getString("doc_cd"), "CA"));
                row.add(rsLcoal.getString("ref_no"));
                row.add(rsLcoal.getString("sr_no"));
                dtm.addRow(row);
                while (rsLcoal.next()) {
                    row = new Vector();
                    row.add(lb.getbillitemCode(rsLcoal.getString("bill_item_cd"), "N"));
                    row.add(rsLcoal.getString("qty"));
                    row.add(rsLcoal.getString("rate"));
                    row.add(rsLcoal.getString("amt"));
                    row.add(rsLcoal.getString("disc"));
                    row.add(rsLcoal.getString("final_amt"));
                    row.add(lb.getAcCode(rsLcoal.getString("doc_cd"), "N"));
                    row.add(lb.getAcCode(rsLcoal.getString("doc_cd"), "CA"));
                    row.add(rsLcoal.getString("ref_no"));
                    row.add(rsLcoal.getString("sr_no"));
                    dtm.addRow(row);
                }

                setTotal();
                rsLcoal.beforeFirst();
                rsLcoal.next();
                if (rsLcoal.getInt("is_medi") == 1) {
                    jlblMedi.setVisible(true);
                } else {
                    jlblMedi.setVisible(false);
                }
                jlblEditNo.setText(rsLcoal.getString("dis_edit_no"));
                jlblLstUpdate.setText(rsLcoal.getString("dis_time_stamp"));
                jlblUserName.setText(lb.getUserName(rsLcoal.getString("dis_user_id"), "N"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Ref No", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "IPD No", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "OPD No", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Patient Name", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "Discharge Date", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Discount Amount", -1, java.lang.String.class, null, false);
        table.AddColumn(6, "Paid Amount", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {
                pd.setReturnComp(jtxtPaidAmount);
                pd.jlblSale.setText(jtxtPaidAmount.getText());
                if (getMode().equalsIgnoreCase("N")) {
                    pd.jcbCash.setSelected(true);
                    pd.jtxtCashAmt.setText(jtxtPaidAmount.getText());
                }
                if (ref_no.equalsIgnoreCase("")) {
                    pd.setInitialAmt(0);
                    ref_no = lb.generateKey("payment", "ref_no", 11, "IP" + HMSHome.year + "/");
                }
                pd.setTotal();
                pd.setFocusComp(navLoad);
                pd.show();

                if (pd.getReturnStatus() == pd.RET_OK) {

                    String sql = "delete from payment where ref_no=?";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.executeUpdate();

                    sql = "insert into payment (ref_no,cash_amt,bank_name,bank_branch,cheque_no,cheque_date,bank_amt,card_amt,card_no) values (?,?,?,?,?,?,?,?,?)";
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
                    pstLocal.executeUpdate();

                    sql = "update ipdreg set dis_date=?,disc_amt=?,paid_amt=?,is_close =1,ref_no=?,dis_user_id=?, dis_time_stamp = current_timestamp,"
                            + "dis_time=?,discharge_type=? ";
                    if (getMode().equalsIgnoreCase("E")) {
                        lb.generateLog("ipdreg", "ipdreglg", "ipd_no", jlblIPDNumber.getText());
                        lb.generateLog("ipdpaymenthd", "ipdpaymenthdlg", "ref_no", ref_no);
                        sql += ",dis_edit_no=dis_edit_no+1";
                    }
                    sql += " where ipd_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    pstLocal.setDouble(2, lb.isNumber(jtxtDiscount));
                    pstLocal.setDouble(3, lb.isNumber(jtxtPaidAmount));
                    pstLocal.setString(4, ref_no);
                    pstLocal.setInt(5, HMSHome.user_id);
                    String time = jtxtAptTime.getText().replaceAll("\\.", ":") + ":00";
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date date = null;
                    date = sdf.parse(time);
                    pstLocal.setString(6, sdf.format(date));
                    pstLocal.setInt(7, jComboBox1.getSelectedIndex());
                    pstLocal.setString(8, jlblIPDNumber.getText());
                    pstLocal.executeUpdate();

                    if (getMode().equalsIgnoreCase("N")) {
                        sql = "insert into ipdpaymenthd (ipd_no, amount,user_id,v_date,ref_no) values (?,?,?,?,?)";
                    } else if (getMode().equalsIgnoreCase("E")) {
                        sql = "update ipdpaymenthd set ipd_no=?,amount=?,edit_no=edit_no+1,user_id=?,time_stamp=current_timestamp,v_date=? "
                                + " where ref_no=?";
                    }
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, jlblIPDNumber.getText());
                    pstLocal.setDouble(2, lb.isNumber(jtxtPaidAmount));
                    pstLocal.setInt(3, HMSHome.user_id);
                    pstLocal.setString(4, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    pstLocal.setString(5, ref_no);
                    pstLocal.executeUpdate();

                    sql = "delete from oldb0_2 where ref_no='" + jlblIPDNumber.getText() + "' and doc_cd = 'TRZ'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "insert into oldb0_2 (ref_no,v_date,doc_cd,room_cd,v_time,ipd_no) values (?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, jlblIPDNumber.getText());
                    pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    pstLocal.setString(3, "TRZ");
                    pstLocal.setString(4, lb.getData("room_Cd", "roommst", "opd_no", jlblOPDNUmber.getText(), 0));
                    pstLocal.setString(5, new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime()));
                    pstLocal.setString(6, jlblIPDNumber.getText());
                    pstLocal.executeUpdate();

                    if (getMode().equalsIgnoreCase("N")) {
                        sql = "update roommst set opd_no=?,is_del=0 where room_cd=?";
                        pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.setString(1, null);
                        pstLocal.setString(2, lb.getData("room_Cd", "roommst", "opd_no", jlblOPDNUmber.getText(), 0));
                        pstLocal.executeUpdate();
                    }

//                    sql = "update ipdbilldt set rate=?,amt=?,disc=?,final_amt=? where sr_no=?";
//                    pstLocal = dataConnection.prepareStatement(sql);
//                    for (int i = 0; i < jTable1.getRowCount(); i++) {
//                        pstLocal.setDouble(1, lb.isNumber(jTable1.getValueAt(i, 2).toString()));
//                        pstLocal.setDouble(2, lb.isNumber(jTable1.getValueAt(i, 3).toString()));
//                        pstLocal.setDouble(3, lb.isNumber(jTable1.getValueAt(i, 4).toString()));
//                        pstLocal.setDouble(4, lb.isNumber(jTable1.getValueAt(i, 5).toString()));
//                        pstLocal.setDouble(5, (int) lb.isNumber(jTable1.getValueAt(i, 9).toString()));
//                        pstLocal.executeUpdate();
//                    }
                } else {
                    throw new SQLException();
                }
            }

            @Override
            public void callDelete() throws Exception {
                lb.confirmDialog("Do you want to delete this receipt entry?");
                if (lb.type) {
                    String sql = "update ipdreg set dis_date=?,disc_amt=?,paid_amt=?,is_close=0,ref_no='' where ipd_no=?";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, null);
                    pstLocal.setDouble(2, 0.0);
                    pstLocal.setDouble(3, 0.00);
                    pstLocal.setString(4, jlblIPDNumber.getText());
                    pstLocal.executeUpdate();

                    sql = "update roommst set opd_no=?,is_del=1 where room_cd=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, jlblOPDNUmber.getText());
                    pstLocal.setString(2, lb.getData("room_Cd", "oldb0_2", "doc_cd='TRZ' and ipd_no", jlblIPDNumber.getText(), 0));
                    pstLocal.executeUpdate();

                    sql = "delete from oldb0_2 where ref_no='" + jlblIPDNumber.getText() + "' and doc_cd = 'TRZ'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from ipdpaymenthd where ref_no='" + ref_no + "' ";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from payment where ref_no='" + ref_no + "' ";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from oldb0_2 where ref_no='" + jlblIPDNumber.getText() + "' and doc_cd = 'TRZ'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();
                }
            }

            @Override
            public void callView() {
                String sql = "SELECT i.ref_no,i.ipd_no,p.opd_no,p.pt_name,i.dis_date,i.disc_amt,i.paid_amt FROM ipdreg i "
                        + "LEFT JOIN patientmst p ON i.opd_no=p.opd_no WHERE dis_date IS NOT NULL ORDER BY dis_date";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "IPD Billing and Discharge View", sql, "25", 1, IPDBillGenerationDischarge.this, "IPD Billing and Discharge", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "IPD Billing and Discharge View", -1);
            }

            @Override
            public void callPrint() {
                VoucherDisplay vd = new VoucherDisplay(jlblIPDNumber.getText(), "Discharge");
                HMSHome.addOnScreen(vd, "Voucher Display", -1);
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabled(false);
                lb.setUserRightsToPanel1(navLoad, "210");
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("select * from " + tableHD + " a left join payment pt on a.ref_no=pt.ref_no where a.dis_date is  not null and a.ref_no=(select min(ref_no) from " + tableHD + " where dis_date IS NOT null)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("select * from " + tableHD + " a left join payment pt on a.ref_no=pt.ref_no where a.dis_date is not null and a.ref_no=(select max(ref_no) from " + tableHD + " where ref_no <'" + ref_no + "' and dis_date IS NOT null)");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("select * from " + tableHD + " a left join payment pt on a.ref_no=pt.ref_no where a.dis_date is not null and a.ref_no=(select min(ref_no) from " + tableHD + " where ref_no >'" + ref_no + "' and dis_date IS NOT null)");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("select * from " + tableHD + " a left join payment pt on a.ref_no=pt.ref_no where a.dis_date is not null and a.ref_no=(select max(ref_no) from " + tableHD + " where dis_date IS NOT null)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("select * from " + tableHD + " a left join payment pt on a.ref_no=pt.ref_no where a.dis_date is not null and a.ref_no='" + ref_no + "'");
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
                jlblTotal.setText("");
                jlblNetAmt.setText("");
                jtxtDiscount.setText("");
                jlblRemainingAmt.setText("");
                jtxtPaidAmount.setText("");
                jlblDueAmount.setText("");
                ref_no = "";
                String timeStamp = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());
                jtxtAptTime.setText(timeStamp);
            }

            @Override
            public void setComponentEnabled(boolean bFlag) {
                if (getMode().equalsIgnoreCase("N")) {
                    jtxtVdate.setEnabled(bFlag);
                } else {
                    jtxtVdate.setEnabled(bFlag);
                }
                jComboBox1.setEnabled(bFlag);
                jtxtItem.setEnabled(false);
                jtxtQty.setEnabled(false);
                jtxtRate.setEnabled(bFlag);
                jtxtAmt.setEnabled(bFlag);
                jtxtRefAlias.setEnabled(false);
                jtxtRefBy.setEnabled(false);
                jtxtDiscount.setEnabled(bFlag);
                jtxtPaidAmount.setEnabled(bFlag);
                jtxtDiscAmt.setEnabled(bFlag);
                jtxtFinalAmt.setEnabled(bFlag);
                jtxtAptTime.setEnabled(bFlag);

            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                ref_no = viewDataRs.getString("ref_no");
                jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("dis_date")));
                jlblOPDNUmber.setText(viewDataRs.getString("opd_no"));
                jlblIPDNumber.setText(viewDataRs.getString("ipd_no"));
                jtxtDiscount.setText(viewDataRs.getString("disc_amt"));
                jtxtPaidAmount.setText(viewDataRs.getString("paid_amt"));
                jtxtAptTime.setText(viewDataRs.getString("dis_time"));
                jComboBox1.setSelectedIndex(viewDataRs.getInt("discharge_type"));
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
                setData(viewDataRs.getString("ipd_no"));

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
                }
                return true;
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

    private void addJlabelDiscountAmt() {
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

    private void addJlabelPaidAmt() {
        jPanel13.removeAll();
        jLabel15.setVisible(false);
        jtxtPaidAmount.setVisible(false);

        jLabel15.setBounds(0, 0, 20, 20);
        jLabel15.setVisible(true);
        jPanel13.add(jLabel15);

        jtxtPaidAmount.setBounds(0, 0, 20, 20);
        jtxtPaidAmount.setVisible(true);
        jPanel13.add(jtxtPaidAmount);
        setTable();
    }

    private void addJlabelDueAmt() {
        jPanel12.removeAll();
        jLabel13.setVisible(false);
        jlblDueAmount.setVisible(false);

        jLabel13.setBounds(0, 0, 20, 20);
        jLabel13.setVisible(true);
        jPanel12.add(jLabel13);

        jlblDueAmount.setBounds(0, 0, 20, 20);
        jlblDueAmount.setVisible(true);
        jPanel12.add(jlblDueAmount);
        setTable();
    }

    private void addJlabelAdvance() {
        jPanel14.removeAll();
        jLabel16.setVisible(false);
        jlblAdvanceAmt.setVisible(false);

        jLabel16.setBounds(0, 0, 20, 20);
        jLabel16.setVisible(true);
        jPanel14.add(jLabel16);

        jlblAdvanceAmt.setBounds(0, 0, 20, 20);
        jlblAdvanceAmt.setVisible(true);
        jPanel14.add(jlblAdvanceAmt);
        setTable();
    }

    private void addJlabelRefund() {
        jPanel15.removeAll();
        jLabel20.setVisible(false);
        jlblRefund.setVisible(false);

        jLabel20.setBounds(0, 0, 20, 20);
        jLabel20.setVisible(true);
        jPanel15.add(jLabel20);

        jlblRefund.setBounds(0, 0, 20, 20);
        jlblRefund.setVisible(true);
        jPanel15.add(jlblRefund);
        setTable();
    }

    private void setTable() {
        jPanel5.setVisible(false);
        jPanel4.setVisible(false);
        lb.setTable(jTable1, new JComponent[]{jtxtItem, jtxtQty, jtxtRate, jtxtAmt, jtxtDiscAmt, jtxtFinalAmt, jtxtRefBy, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblTotal, jLabel9, null, null, null});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel7, jcmbTax, jlblTaxableAmt});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel8, null, jlblTaxAmt});
//        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jLabel9, null, jlblAddTaxAmt});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblNetAmt, jLabel10, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblAdvanceAmt, jLabel16, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblRefund, jLabel20, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jtxtDiscount, jLabel11, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblRemainingAmt, jLabel12, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jtxtPaidAmount, jLabel15, null, null, null});
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, jlblDueAmount, jLabel13, null, null, null});
    }

    private boolean validateRow() {
        if (!lb.isExist("billitemmst", "bill_item_name", jtxtItem.getText(), dataConnection)) {
            navLoad.setMessage("Item name does not exsist in database");
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
        jlblRemainingAmt.setText(lb.Convert2DecFmtForRs(amt - lb.isNumber(jtxtDiscount) - lb.isNumber(jlblAdvanceAmt) + lb.isNumber(jlblRefund)));
        jlblDueAmount.setText(lb.Convert2DecFmtForRs(amt - lb.isNumber(jtxtDiscount) - lb.isNumber(jlblAdvanceAmt) + lb.isNumber(jlblRefund) - lb.isNumber(jtxtPaidAmount)));
    }

    private void clearRow() {
        jtxtItem.setText("");
        jtxtQty.setText("");
        jtxtRate.setText("");
        jtxtAmt.setText("");
        jtxtDiscAmt.setText("");
        jtxtFinalAmt.setText("");
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
        jlblOPDNUmber = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jlblIPDNumber = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jtxtAptTime = new javax.swing.JTextField();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
        jbtnRefund = new javax.swing.JButton();
        jlblMedi = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
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
        jPanel12 = new javax.swing.JPanel();
        jlblDueAmount = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jtxtPaidAmount = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jlblAdvanceAmt = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jlblRefund = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("IPD Number");

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

        jlblOPDNUmber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("OPD NUmber");

        jlblIPDNumber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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

        jButton1.setText("Register");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Advance");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel21.setText("Admit Time");

        jtxtAptTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAptTimeFocusGained(evt);
            }
        });
        jtxtAptTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAptTimeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAptTimeKeyTyped(evt);
            }
        });

        jbtnRefund.setText("Refund");
        jbtnRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRefundActionPerformed(evt);
            }
        });

        jlblMedi.setBackground(new java.awt.Color(51, 51, 255));
        jlblMedi.setForeground(new java.awt.Color(255, 255, 0));
        jlblMedi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblMedi.setText("MediClaim");
        jlblMedi.setOpaque(true);

        jLabel22.setText("Discharge Type");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Discharge", "Dama", "Death" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jlblOPDNUmber, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlblMedi, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(117, 117, 117)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jbtnAdd)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblIPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel5});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jbtnRefund)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jlblIPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnAdd)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jlblMedi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlblOPDNUmber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton2, jLabel1, jLabel21, jLabel7, jlblIPDNumber, jtxtAptTime});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel22});

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
                "Item", "Qty", "Rate", "Amount", "Disc", "Final", "Doc Name", "Doc Alias", "ref_no", "SR No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(8).setMinWidth(0);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(8).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(9).setMinWidth(0);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(9).setMaxWidth(0);
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel10.setPreferredSize(new java.awt.Dimension(177, 30));

        jLabel11.setText("Discount");
        jLabel11.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jLabel11ComponentMoved(evt);
            }
        });

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
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtDiscountKeyTyped(evt);
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel12.setText("Bill Amount");

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

        jPanel12.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblDueAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setText("Due Amount");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(jlblDueAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblDueAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel13.setPreferredSize(new java.awt.Dimension(177, 30));

        jLabel15.setText("Paid Amount");

        jtxtPaidAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPaidAmountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPaidAmountFocusLost(evt);
            }
        });
        jtxtPaidAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPaidAmountKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPaidAmountKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167)
                .addComponent(jtxtPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel15)
                .addComponent(jtxtPaidAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel14.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblAdvanceAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel16.setText("Advance Amt");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(jlblAdvanceAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblAdvanceAmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel15.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblRefund.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel20.setText("Refund");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(jlblRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblRefund, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1061, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel17.setText("User:");

        jLabel18.setText("Edit No:");

        jLabel19.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel17, jLabel18, jLabel19, jlblEditNo, jlblLstUpdate, jlblUserName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        lb.enterFocus(evt, jtxtPaidAmount);
    }//GEN-LAST:event_jtxtDiscountKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
//            int row = jTable1.getSelectedRow();
//            if (row != -1) {
//                jtxtItem.setText(jTable1.getValueAt(row, 0).toString());
//                jtxtQty.setText(jTable1.getValueAt(row, 1).toString());
//                jtxtRate.setText(jTable1.getValueAt(row, 2).toString());
//                jtxtAmt.setText(jTable1.getValueAt(row, 3).toString());
//                jtxtDiscAmt.setText(jTable1.getValueAt(row, 4).toString());
//                jtxtFinalAmt.setText(jTable1.getValueAt(row, 5).toString());
//                jtxtRefBy.setText(jTable1.getValueAt(row, 6).toString());
//                jtxtRefAlias.setText(jTable1.getValueAt(row, 7).toString());
//                jtxtItem.requestFocusInWindow();
//            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
//        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
//            int row = jTable1.getSelectedRow();
//            if (row != -1) {
//                lb.confirmDialog("Do you want to delte this row?");
//                if (lb.type) {
//                    dtm.removeRow(row);
//                    setTotal();
//                    clearRow();
//                }
//            }
//        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jtxtPaidAmountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPaidAmountFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPaidAmountFocusGained

    private void jtxtPaidAmountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPaidAmountFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtPaidAmountFocusLost

    private void jtxtPaidAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPaidAmountKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtPaidAmountKeyPressed

    private void jtxtDiscountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscountKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtDiscountKeyTyped

    private void jtxtPaidAmountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPaidAmountKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtPaidAmountKeyTyped

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
            String sql = "SELECT bill_item_name,1 as qty,def_rate FROM billitemmst"
                    + " WHERE bill_item_name LIKE '" + jtxtItem.getText().toUpperCase() + "%'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            itemPicklist.setPreparedStatement(pstLocal);
            itemPicklist.setFirstAssociation(new int[]{0, 1, 2});
            itemPicklist.setSecondAssociation(new int[]{0, 1, 2});
            itemPicklist.setValidation(dataConnection.prepareStatement("SELECT bill_item_name FROM billitemmst WHERE bill_item_name =?"));
            itemPicklist.pickListKeyRelease(evt);
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
                    + " ac_name like  '" + jtxtRefBy.getText().toUpperCase() + "%'");
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
        lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtFinalAmtKeyPressed

    private void jLabel11ComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLabel11ComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jLabel11ComponentMoved

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        // TODO add your handling code here:
//        if (validateRow()) {
//            int selRow = jTable1.getSelectedRow();
//            if (selRow == -1) {
//            } else {
//                jTable1.setValueAt(jtxtItem.getText(), selRow, 0);
//                jTable1.setValueAt((int) lb.isNumber(jtxtQty), selRow, 1);
//                jTable1.setValueAt(lb.isNumber(jtxtRate), selRow, 2);
//                jTable1.setValueAt(lb.isNumber(jtxtAmt), selRow, 3);
//                jTable1.setValueAt(lb.isNumber(jtxtDiscAmt), selRow, 4);
//                jTable1.setValueAt(lb.isNumber(jtxtFinalAmt), selRow, 5);
//                jTable1.setValueAt(jtxtRefBy.getText(), selRow, 6);
//                jTable1.setValueAt(jtxtRefAlias.getText(), selRow, 7);
//                jTable1.clearSelection();
//            }
//            setTotal();
//            clearRow();
//            lb.confirmDialog("Do you want to add another row?");
//            if (lb.type) {
//                jtxtItem.requestFocusInWindow();
//            } else {
//                navLoad.setSaveFocus();
//            }
//        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        lb.confirmDialog("Do you want to open Registeration of this patient?");
        if (lb.type) {
            IPDRegistrationForm ipd = new IPDRegistrationForm();
            ipd.setID(jlblIPDNumber.getText());
            addOnScreen(ipd, "IPD Register Book", 25);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        IPDAdvanceReceipt ipd = new IPDAdvanceReceipt(jlblIPDNumber.getText(), 1);
        addOnScreen(ipd, "IPD Advance Receipt", 29);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jtxtAptTimeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAptTimeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAptTimeFocusGained

    private void jtxtAptTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtDiscount);
    }//GEN-LAST:event_jtxtAptTimeKeyPressed

    private void jtxtAptTimeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 5);
    }//GEN-LAST:event_jtxtAptTimeKeyTyped

    private void jbtnRefundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRefundActionPerformed
        // TODO add your handling code here:
        IPDAdvanceReceipt ipd = new IPDAdvanceReceipt(jlblIPDNumber.getText(), -1);
        addOnScreen(ipd, "IPD Advance Receipt", 29);
    }//GEN-LAST:event_jbtnRefundActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
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
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnRefund;
    private javax.swing.JLabel jlblAdvanceAmt;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblDueAmount;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblIPDNumber;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblMedi;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblNetAmt;
    private javax.swing.JLabel jlblOPDNUmber;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblRefund;
    private javax.swing.JLabel jlblRemainingAmt;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JTextField jtxtAptTime;
    private javax.swing.JTextField jtxtDiscAmt;
    private javax.swing.JTextField jtxtDiscount;
    private javax.swing.JTextField jtxtFinalAmt;
    private javax.swing.JTextField jtxtItem;
    private javax.swing.JTextField jtxtPaidAmount;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRefAlias;
    private javax.swing.JTextField jtxtRefBy;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables

    public void setID(String strCode) {
        ref_no = strCode;
        navLoad.setVoucher("Edit");
    }
}
