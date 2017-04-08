/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMS101;
import hms.HMSHome;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.ReportTable;
import utility.VoucherDisplay;

/**
 *
 * @author Lenovo
 */
public class OPDLatePayment extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    NavigationPanel navLoad = null;
    DefaultTableModel dtm = null;
    String ref_no = "";
    private PaymentDialog pd = new PaymentDialog(null, true);
    ReportTable table = null;

    /**
     * Creates new form IPDAdvanceReceipt
     */
    public OPDLatePayment() {
        initComponents();
        addNavigationPanel();
        dtm = (DefaultTableModel) jTable1.getModel();
        addJlabel();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "24");
    }

    public void setData() {
        try {
            String sql = " SELECT p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name,"
                    + " i1.bill_item_cd,i1.qty,i1.rate,i1.amount,i1.doc_cd,i.v_date,i.disc_amt  FROM opdbillhd i LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                    + " LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd  "
                    + " LEFT JOIN areamst a ON p1.area_cd=a.area_cd  LEFT JOIN opdbilldt i1 ON i1.ref_no=i.ref_no "
                    + " LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd WHERE i.ref_no='" + jlblVoucherNo.getText() + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            dtm.setRowCount(0);
            double amt = lb.isNumber(lb.getData("SELECT SUM(amount) FROM opdpaymenthd WHERE voucher_no='" + jlblVoucherNo.getText() + "' "
                    + "AND ref_no like 'LP%'"));
            double disc_amt =0.00;
            while (rsLcoal.next()) {
                disc_amt = rsLcoal.getDouble("disc_amt");
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));

                Vector row = new Vector();
                row.add(lb.ConvertDateFormetForDisply(rsLcoal.getString("v_date")));
                row.add(lb.getbillitemCode(rsLcoal.getString("bill_item_cd"), "N"));
                row.add(rsLcoal.getInt("qty"));
                row.add(rsLcoal.getDouble("rate"));
                row.add(rsLcoal.getDouble("amount"));
                row.add(lb.getAcCode(rsLcoal.getString("doc_cd"), "N"));
                row.add(lb.getAcCode(rsLcoal.getString("doc_cd"), "CA"));
                dtm.addRow(row);

            }
            jlblTotalPayment.setText(lb.Convert2DecFmtForRs(amt+disc_amt));
            setTotal();
            lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void addJlabel() {
        jPanel5.removeAll();
        jlblTotal.setVisible(false);
        jLabel11.setVisible(false);

        jlblTotal.setBounds(0, 0, 20, 20);
        jlblTotal.setVisible(true);
        jPanel5.add(jlblTotal);

        jLabel11.setBounds(0, 0, 20, 20);
        jLabel11.setVisible(true);
        jPanel5.add(jLabel11);
        setTable();
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Voucher No", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "Date", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Patient Name", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Amount", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "Cash", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Bank", -1, java.lang.String.class, null, false);
        table.AddColumn(6, "Card", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {
                pd.setReturnComp(jtxtAmount);
                pd.jlblSale.setText(jtxtAmount.getText());
                if (getMode().equalsIgnoreCase("N")) {
                    pd.jcbCash.setSelected(true);
                    pd.jtxtCashAmt.setText(Math.abs(lb.isNumber(jtxtAmount.getText())) + "");
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
                        sql = "insert into opdpaymenthd (opd_no,voucher_no, amount,user_id,v_date,ref_no) values (?,?,?,?,?,?)";
                        ref_no = lb.generateKey("opdpaymenthd", "ref_no", 11, "LP" + HMSHome.year + "/");
                    } else if (getMode().equalsIgnoreCase("E")) {
                        lb.generateLog("opdpaymenthd", "opdpaymenthdlg", "ref_no", ref_no);
                        sql = "update opdpaymenthd set opd_no=?,voucher_no=?,amount=?,edit_no=edit_no+1,user_id=?,time_stamp=current_timestamp,v_date=?"
                                + "  where ref_no=?";
                    }
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, jlblOPDNumber.getText());
                    pstLocal.setString(2, jlblVoucherNo.getText());
                    pstLocal.setDouble(3, Math.abs(lb.isNumber(jtxtAmount)));
                    pstLocal.setInt(4, HMSHome.user_id);
                    pstLocal.setString(5, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    pstLocal.setString(6, ref_no);
                    pstLocal.executeUpdate();

                    sql = "delete from payment where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.executeUpdate();

                    sql = "insert into payment (ref_no,cash_amt,bank_name,bank_branch,cheque_no,cheque_date,bank_amt,card_amt,card_no,user_id) values (?,?,?,?,?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    if (pd.jcbCash.isSelected()) {
                        pstLocal.setDouble(2, Math.abs(lb.isNumber(pd.jtxtCashAmt)));
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

                        pstLocal.setDouble(7, Math.abs(lb.isNumber(pd.jtxtChequeAmt)));
                    } else {
                        pstLocal.setString(3, "");
                        pstLocal.setString(4, "");
                        pstLocal.setString(5, "");
                        pstLocal.setString(6, null);
                        pstLocal.setDouble(7, 0.00);
                    }
                    if (pd.jcbCard.isSelected()) {
                        pstLocal.setDouble(8, Math.abs(lb.isNumber(pd.jtxtCardAmt)));
                        pstLocal.setString(9, pd.jtxtCardNo.getText());
                    } else {
                        pstLocal.setDouble(8, 0.00);
                        pstLocal.setString(9, "");
                    }
                    pstLocal.setInt(10, HMSHome.user_id);
                    pstLocal.executeUpdate();
                } else {
                    throw new SQLException("Cancel Button was clicked");
                }
            }

            @Override
            public void callDelete() throws Exception {
                lb.confirmDialog("Do you want to delete this voucher?");
                if (lb.type) {
                    String sql = "delete from payment where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from opdpaymenthd where ref_no='" + ref_no + "'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();
                }
            }

            @Override
            public void callView() {
                String sql = "SELECT i.ref_no,i.v_date,p.pt_name,i.amount,p1.cash_amt,p1.bank_amt,p1.card_amt"
                        + " FROM opdpaymenthd i LEFT JOIN opdbillhd i1 ON i.voucher_no=i1.ref_no"
                        + "  LEFT JOIN patientmst p ON i1.opd_no=p.opd_no LEFT JOIN payment p1 ON i.ref_no=p1.ref_no  WHERE i.ref_no LIKE 'LP%' "
                        + "ORDER BY v_date";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "OPD Late Payment View", sql, "29", 1, OPDLatePayment.this, "OPD Late Payment", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "OPD Late Payment View", 21);
            }

            @Override
            public void callPrint() {
                VoucherDisplay vd = new VoucherDisplay(ref_no, "Late Payment");
                HMSHome.addOnScreen(vd, "Voucher Display", -1);
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabled(false);
                lb.setUserRightsToPanel1(navLoad, "24");
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("select * from opdpaymenthd a  left join payment p on a.ref_no=p.ref_no where a.ref_no=(select min(ref_no) from opdpaymenthd where ref_no like 'LP%')");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("select * from opdpaymenthd a  left join  payment p on a.ref_no=p.ref_no where a.ref_no like 'LP%' and a.ref_no=(select max(ref_no) from opdpaymenthd where ref_no <'" + ref_no + "' and ref_no like 'LP%')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("select * from opdpaymenthd a  left join payment p on a.ref_no=p.ref_no where a.ref_no like 'LP%' and a.ref_no=(select min(ref_no) from opdpaymenthd where ref_no >'" + ref_no + "' and ref_no like 'LP%')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("select * from opdpaymenthd a  left join payment p on a.ref_no=p.ref_no where a.ref_no=(select max(ref_no) from opdpaymenthd where ref_no like 'LP%')");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("select * from opdpaymenthd a  left join payment p  on a.ref_no=p.ref_no where a.ref_no='" + ref_no + "'");
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
                jtxtAmount.setText("0.00");
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
                }
                jtxtAmount.setEnabled(bFlag);
                jbtnSeleBill.setEnabled(bFlag);
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
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
                ref_no = viewDataRs.getString("ref_no");
                jtxtRefNo.setText(ref_no);
                jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("v_date")));
                jlblVoucherNo.setText(viewDataRs.getString("voucher_no"));
                jlblOPDNumber.setText(viewDataRs.getString("opd_no"));
                setData();
                jlblTotalBill.setText(jlblTotal.getText());
                jlblTotalPayment.setText(lb.Convert2DecFmtForRs(lb.isNumber(lb.getData("SELECT SUM(amount) FROM opdpaymenthd WHERE voucher_no='" + jlblVoucherNo.getText() + "' "
                        + "AND ref_no <>'" + ref_no + "'"))));
                jtxtAmount.setText(viewDataRs.getString("amount"));
                jlblEditNo.setText(viewDataRs.getString("edit_no"));
                jlblLstUpdate.setText(viewDataRs.getString("time_stamp"));
                jlblUserName.setText(lb.getUserName(viewDataRs.getString("user_id"), "N"));
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
                    return lb.getData("ref_no", "ipdreg", "ipd_no", jlblOPDNumber.getText(), 0).equalsIgnoreCase("");
                }
            }

            @Override
            public boolean validateForm() {
                if (lb.isBlank(jlblOPDNumber)) {
                    navLoad.setMessage("IPD Number can not be left blank");
                    return false;
                }

                if (lb.checkDate2(jtxtVdate)) {
                    navLoad.setMessage("Invalid Date");
                    jtxtVdate.requestFocusInWindow();
                    return false;
                }

                if (lb.isNumber(jtxtAmount) == 0.00) {
                    navLoad.setMessage("Zero amount Can not accept it");
                    jtxtAmount.requestFocusInWindow();
                    return false;
                }
                return true;
            }

        }
        navLoad = new navPanel();
        jPanelNavigation.add(navLoad);
        navLoad.setVisible(true);
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

    private void setTotal() {
        double amt = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            amt += lb.isNumber(jTable1.getValueAt(i, 4).toString());
        }
        jlblTotal.setText(lb.Convert2DecFmtForRs(amt));
        jlblTotalBill.setText(lb.Convert2DecFmtForRs(amt));
        jlblRemPayment.setText(lb.Convert2DecFmtForRs(amt - lb.isNumber(jlblTotalPayment) - lb.isNumber(jtxtAmount)));
    }

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{null, null, jLabel11, null, jlblTotal, null, null});
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
        jlblOPDNumber = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jlblVoucherNo = new javax.swing.JLabel();
        jbtnSeleBill = new javax.swing.JButton();
        jPanelNavigation = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jlblTotal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jtxtAmount = new javax.swing.JTextField();
        jlblRemPayment = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jlblTotalBill = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jlblTotalPayment = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("OPD Number");

        jLabel2.setText("Receipt No");

        jtxtRefNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRefNoKeyPressed(evt);
            }
        });

        jLabel3.setText("Name");

        jlblName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("City");

        jlblCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Sex");

        jlblSex.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblRefBy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("Refe By");

        jLabel8.setText("Area");

        jlblArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblOPDNumber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setText("Voucher Number");

        jlblVoucherNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jbtnSeleBill.setText("Select Due Bill");
        jbtnSeleBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSeleBillActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnSeleBill, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblOPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblVoucherNo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtnSeleBill)))
                            .addComponent(jlblOPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblVoucherNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanelNavigation.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Item", "Qty", "Rate", "Amount", "Doc Name", "Doc Alias"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(500);
            jTable1.getColumnModel().getColumn(5).setMinWidth(200);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(200);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel5.setPreferredSize(new java.awt.Dimension(177, 30));

        jlblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setText("Total");
        jLabel11.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jLabel11ComponentMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120)
                .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
            .addComponent(jLabel11)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setText("User:");

        jLabel16.setText("Edit No:");

        jLabel17.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setText("Date");

        jtxtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmountFocusLost(evt);
            }
        });
        jtxtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmountKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmountKeyTyped(evt);
            }
        });

        jlblRemPayment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("Remaining Amouunt");

        jlblTotalBill.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setText("Other Paid  Amount");

        jLabel10.setText("Total Bill");

        jlblTotalPayment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setText("Amount");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblRemPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(jlblTotalPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTotalBill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jlblRemPayment, jlblTotalBill, jlblTotalPayment, jtxtAmount});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel12, jLabel13, jLabel14});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotalBill, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotalPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblRemPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel10, jtxtAmount});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addGap(11, 11, 11)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtRefNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefNoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtRefNoKeyPressed

    private void jLabel11ComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLabel11ComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jLabel11ComponentMoved

    private void jtxtAmountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmountFocusGained

    private void jtxtAmountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtAmountFocusLost

    private void jtxtAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtAmountKeyPressed

    private void jtxtAmountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtAmountKeyTyped

    private void jbtnSeleBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSeleBillActionPerformed
        // TODO add your handling code here:
        JDialog d = new JDialog();
        d.setModal(true);
        OPDDueAmtBill sp = new OPDDueAmtBill(d, this);
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
    }//GEN-LAST:event_jbtnSeleBillActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnSeleBill;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblName;
    public javax.swing.JLabel jlblOPDNumber;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblRemPayment;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblTotalBill;
    private javax.swing.JLabel jlblTotalPayment;
    private javax.swing.JLabel jlblUserName;
    public javax.swing.JLabel jlblVoucherNo;
    private javax.swing.JTextField jtxtAmount;
    private javax.swing.JTextField jtxtRefNo;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables

    public void setID(String strCode) {
        this.ref_no = strCode;
        navLoad.setVoucher("Edit");
    }
}
