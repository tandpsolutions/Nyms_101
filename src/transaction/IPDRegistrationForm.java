/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMS101;
import hms.HMSHome;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.PickList;
import support.ReportTable;
import support.SMS;

/**
 *
 * @author Lenovo
 */
public class IPDRegistrationForm extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    NavigationPanel navLoad = null;
    PickList acPickList = null;
    DefaultTableModel dtmWardMaster = null;
    String ipd_no = "";
    String old_ref_no = "";
    ReportTable table = null;

    /**
     * Creates new form IPDRegistrationForm
     */
    public IPDRegistrationForm(String opd_no) {
        initComponents();
        dtmWardMaster = (DefaultTableModel) jTable3.getModel();
        addInitialData();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        addNavigationPanel();
        navLoad.callNew();
        setData(opd_no);
        lb.setDateChooserPropertyInit(jtxtAptDate);
        String timeStamp = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());
        jtxtAptTime.setText(timeStamp);
    }

    public IPDRegistrationForm() {
        initComponents();
        dtmWardMaster = (DefaultTableModel) jTable3.getModel();
        addInitialData();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        addNavigationPanel();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "25");
    }

    private void addInitialData() {
        try {
            String sql = "SELECT ward_cd,ward_name FROM wardmst";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add(rsLocal.getInt("ward_cd"));
                row.add(rsLocal.getString("ward_name"));
                dtmWardMaster.addRow(row);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at addInitial Data ad OPDBillGeneration", ex);
        }
    }

    public void setID(String ipd) {
        ipd_no = ipd;
        navLoad.setVoucher("EDIT");
    }

    private void setData(String opd_no) {
        try {
            String sql = "SELECT p1.mobile,p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name  "
                    + " FROM patientinfomst p1 LEFT JOIN patientmst p ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd "
                    + " LEFT JOIN areamst a ON p1.area_cd=a.area_cd LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd where p.opd_no='" + opd_no + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jtxtOPDNo.setText(opd_no);
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));
                jtxtMobile.setText(" " + rsLcoal.getString("mobile"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "IPD No", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "OPD No", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Patient Name", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Admit Date", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "Admit Time", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void sendSms() {
        try {
            String doc_message = "Thank you for refering " + jlblName.getText() + ", we will discuss with you soon "
                    + "regarding clinical course and progress of the patient.";
            String pt_message = jlblName.getText() + " has been admitted at Apple Children Hospital. Please contact"
                    + " Shekhar sing/Dhruv Chauhan for billing, Vatsalaben or Nalinbhai for housekeeping. "
                    + "Please give your valuable feedback to Mitalben.";

            SMS pt_data = lb.sendMessage(pt_message, jtxtMobile.getText());
            String sql = "update ipdreg set pt_sms_id='" + pt_data.getMessageid() + "' where ipd_no='" + ipd_no + "'";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.executeUpdate();

            SMS doc_data = lb.sendMessage(doc_message, lb.getData("mobile1", "phbkmst", "ac_cd", lb.getAcCode(jtxtDocAlias.getText(), "AC"), 1));
            sql = "update ipdreg set doc_sms_id='" + doc_data.getMessageid() + "' where ipd_no='" + ipd_no + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.executeUpdate();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at send message", ex);
        }
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {
                String sql = "";
                PreparedStatement pstLocal = null;
                if (getMode().equalsIgnoreCase("N")) {
                    sql = "insert into ipdreg (opd_no,admit_date,admit_time,doc_cd,ref_doc_cd,room_cd,user_id,remark,is_medi,ipd_no)"
                            + " values (?,?,?,?,?,?,?,?,?,?)";
                    ipd_no = lb.generateKey("ipdreg", "ipd_no", 8, "IPD-");
                } else if (getMode().equalsIgnoreCase("E")) {
                    sql = "update ipdreg set opd_no=?,admit_date=?,admit_time=?,doc_cd=?,ref_doc_cd=?,room_cd=?,user_id=?,remark=?,is_medi=? where ipd_no=?";
                }
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, jtxtOPDNo.getText());
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtAptDate.getText()));
                String time = jtxtAptTime.getText().replaceAll("\\.", ":") + ":00";
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date date = null;
                date = sdf.parse(time);
                pstLocal.setString(3, sdf.format(date));
                pstLocal.setString(4, lb.getAcCode(jtxtDocAlias.getText(), "AC"));
                pstLocal.setString(5, "0");
                pstLocal.setString(6, jlblBedNo.getText());
                pstLocal.setInt(7, HMSHome.user_id);
                pstLocal.setString(8, jtxtRemark.getText());
                pstLocal.setInt(9, jcmbMedi.getSelectedIndex());
                pstLocal.setString(10, ipd_no);
                pstLocal.executeUpdate();

                sql = "delete from oldb0_2 where ipd_no='" + ipd_no + "' and doc_cd='ADT'";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.executeUpdate();

                if (!old_ref_no.equalsIgnoreCase("")) {
                    sql = "update roommst set opd_no=?,is_del =0 where room_cd=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, null);
                    pstLocal.setString(2, old_ref_no);
                    pstLocal.executeUpdate();
                }

                sql = "insert into oldb0_2 (ipd_no,v_date,doc_cd,room_cd,v_time,ref_no) values (?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ipd_no);
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtAptDate.getText()));
                pstLocal.setString(3, "ADT");
                pstLocal.setString(4, jlblBedNo.getText());
                pstLocal.setString(5, sdf.format(date));
                pstLocal.setString(6, ipd_no);
                pstLocal.executeUpdate();

                sql = "update roommst set opd_no=?,is_del =1 where room_cd=?";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, jtxtOPDNo.getText());
                pstLocal.setString(2, jlblBedNo.getText());
                pstLocal.executeUpdate();

                if (getMode().equalsIgnoreCase("N")) {
                    sendSms();
                }
            }

            @Override
            public void callDelete() throws Exception {
                if (checkEdit()) {
                    lb.confirmDialog("Do you want to delete this register entry?");
                    if (lb.type) {
                        String sql = "delete from oldb0_2 where ipd_no='" + ipd_no + "'";
                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.executeUpdate();

                        sql = "delete from ipdreg where ipd_no='" + ipd_no + "'";
                        pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.executeUpdate();

                        sql = "update roommst set opd_no=?,is_del =0 where room_cd=?";
                        pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.setString(1, null);
                        pstLocal.setString(2, jlblBedNo.getText());
                        pstLocal.executeUpdate();
                    }
                }
            }

            @Override
            public void callView() {
                String sql = "SELECT ipd_no,i.opd_no,p.pt_name,admit_date,admit_time FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                        + "order by admit_date";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ipd_no + "", "IPD Registration View", sql, "28", 1, IPDRegistrationForm.this, "IPD Registration", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "IPD Registration View", -1);
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
                lb.setUserRightsToPanel1(navLoad, "25");
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("select * from ipdreg a where a.ipd_no=(select min(ipd_no) from ipdreg)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("select * from ipdreg a where a.ipd_no=(select max(ipd_no) from ipdreg where ipd_no <'" + ipd_no + "')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("select * from ipdreg a where a.ipd_no=(select min(ipd_no) from ipdreg where ipd_no >'" + ipd_no + "')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("select * from ipdreg a where a.ipd_no=(select max(ipd_no) from ipdreg)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("select * from ipdreg a where a.ipd_no='" + ipd_no + "'");
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

                cont = jPanel2.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JLabel) {
                        if (((JLabel) cont[i]).getBorder() != null) {
                            ((JLabel) cont[i]).setText("");
                        }
                    } else if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setText("");
                    }
                }
                jPanel6.removeAll();
                old_ref_no = "";
            }

            @Override
            public void setComponentEnabled(boolean bFlag) {
                Component[] cont = jPanel1.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setEnabled(bFlag);
                    }
                }

                cont = jPanel2.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setEnabled(bFlag);
                    } else if (cont[i] instanceof JButton) {
                        ((JButton) cont[i]).setEnabled(bFlag);
                    }
                }
                jtxtAptDate.setEnabled(bFlag);
                jcmbMedi.setEnabled(bFlag);
                jTable3.setEnabled(bFlag);
                if (!bFlag) {
                    jPanel6.removeAll();
                }
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                ipd_no = viewDataRs.getString("IPD_NO");
                jtxtOPDNo.setText(viewDataRs.getString("OPD_NO"));
                setData(viewDataRs.getString("OPD_NO"));
                jtxtAptDate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("admit_date")));
                jcmbMedi.setSelectedIndex(viewDataRs.getInt("is_medi"));
                jtxtAptTime.setText(viewDataRs.getString("admit_time"));
                jtxtDocAlias.setText(lb.getAcCode(viewDataRs.getString("doc_cd"), "CA"));
                jtxtDoctor.setText(lb.getAcCode(viewDataRs.getString("doc_cd"), "N"));
                jlblEditNo.setText(viewDataRs.getString("edit_no"));
                jlblLstUpdate.setText(viewDataRs.getString("time_stamp"));
                jlblUserName.setText(lb.getUserName(viewDataRs.getString("user_id"), "N"));
                jlblBedNo.setText(viewDataRs.getString("room_cd"));
                jtxtRemark.setText(viewDataRs.getString("remark"));
                old_ref_no = viewDataRs.getString("room_cd");
                jlblWardName.setText(lb.getData("ward_name", "wardmst", "ward_cd", lb.getData("ward_cd", "roommst", "room_cd", viewDataRs.getString("room_cd"), 0), 1));
            }

            @Override
            public boolean validateForm() {
                if (!lb.isExist("acntmst", "ac_alias", jtxtDocAlias.getText(), dataConnection)) {
                    jtxtDoctor.requestFocusInWindow();
                    lb.showMessageDailog("Please select valid consultant doctor");
                    return false;
                }

                if (lb.checkDate2(jtxtAptDate)) {
                    lb.showMessageDailog("Invalid date");
                    jtxtAptDate.requestFocusInWindow();
                    return false;
                }

                if (!lb.isExist("wardmst", "ward_name", jlblWardName.getText(), dataConnection)) {
                    jTable3.requestFocusInWindow();
                    lb.showMessageDailog("Please select valid ward");
                    return false;
                }

                if (!lb.isExist("roommst", "room_cd", jlblBedNo.getText(), dataConnection)) {
                    jTable3.requestFocusInWindow();
                    lb.showMessageDailog("Please select valid bed.");
                    return false;
                }

                if (lb.isBlank(jtxtOPDNo)) {
                    jtxtOPDNo.requestFocusInWindow();
                    navLoad.setMessage("Patient name can not be left blank");
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    try {
                        String sql = "select * from ipdreg where opd_no='" + jtxtOPDNo.getText() + "' and dis_date is null";
                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                        ResultSet rsLocal = pstLocal.executeQuery();
                        if (rsLocal.next()) {
                            navLoad.setMessage("Patient already registerd in I.P.D");
                            return false;
                        }
                    } catch (Exception ex) {
                        lb.printToLogFile("Exception at register form", ex);
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean checkEdit() {
                if (lb.getData("count(*)", "oldb0_2", "IPD_NO", ipd_no, 0).equalsIgnoreCase("1")) {
                    return true;
                } else {
                    return false;
                }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jlblName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jlblCity = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jlblSex = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlblArea = new javax.swing.JLabel();
        jlblRefBy = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtOPDNo = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JLabel();
        jPanelNavigation = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jtxtDoctor = new javax.swing.JTextField();
        jtxtDocAlias = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jtxtAptTime = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jlblWardName = new javax.swing.JLabel();
        jlblBedNo = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jtxtRemark = new javax.swing.JTextField();
        jtxtAptDate = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jcmbMedi = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlblName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Sex");

        jlblCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Ref BY");

        jLabel1.setText("OPD Number");

        jlblSex.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("Area");

        jlblArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblRefBy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Name");

        jLabel5.setText("City");

        jtxtOPDNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jtxtMobile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jtxtMobile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblArea, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtOPDNo});

        jPanelNavigation.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("Admit Under Doctor");

        jtxtDoctor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDoctorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDoctorFocusLost(evt);
            }
        });
        jtxtDoctor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDoctorKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtDoctorKeyReleased(evt);
            }
        });

        jLabel18.setText("Admit Date");

        jLabel19.setText("Admit Time");

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

        jLabel8.setText("Admit Under Ward");

        jLabel9.setText("Bed No.");

        jlblWardName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblBedNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel16.setText("Edit No:");

        jLabel17.setText("Last Updated:");

        jLabel15.setText("User:");

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jLabel10.setText("Remark");

        jtxtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRemarkFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRemarkFocusLost(evt);
            }
        });
        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyPressed(evt);
            }
        });

        jLabel11.setText("Mediclaim");

        jcmbMedi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Yes" }));
        jcmbMedi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbMediKeyPressed(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 1, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlblBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jlblWardName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jtxtAptTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jtxtDoctor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jtxtRemark)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxtAptDate, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(jcmbMedi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 147, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcmbMedi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jtxtAptDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblWardName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel16)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel19, jLabel7, jLabel8});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jcmbMedi});

        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ward_cd", "Ward Name"
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
        }

        jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtDoctorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDoctorFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDoctorFocusGained

    private void jtxtDoctorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDoctorFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtDoctorFocusLost

    private void jtxtDoctorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorKeyPressed
        acPickList.setPickListComponent(jtxtDoctor);
        acPickList.setNextComponent(jcmbMedi);
        acPickList.setLocation(jtxtDoctor.getX() + jPanel2.getX(), jtxtDoctor.getY() + jtxtDoctor.getHeight() + jPanel2.getY());
        acPickList.setReturnComponent(new JTextField[]{jtxtDoctor, jtxtDocAlias});
        acPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtDoctorKeyPressed

    private void jtxtDoctorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("SELECT ac_name,ac_alias,s.spec_sub_name FROM acntmst a "
                    + " LEFT JOIN doctormaster d ON a.ac_cd=d.ac_cd LEFT JOIN specsubmst s ON d.sub_spec_cd=s.spec_sub_cd "
                    + " WHERE a.ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '%" + jtxtDoctor.getText().toUpperCase() + "%'");
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1});
            acPickList.setSecondAssociation(new int[]{0, 1});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtDoctorKeyReleased

    private void jtxtAptTimeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAptTimeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAptTimeFocusGained

    private void jtxtAptTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtRemark);
    }//GEN-LAST:event_jtxtAptTimeKeyPressed

    private void jtxtAptTimeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 5);
    }//GEN-LAST:event_jtxtAptTimeKeyTyped

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int selRow = jTable3.getSelectedRow();
            if (selRow != -1) {
                int code = (int) lb.isNumber(jTable3.getValueAt(selRow, 0).toString());
                try {
                    String sql = "SELECT room_cd,is_del FROM roommst WHERE is_del >=0 and ward_cd=" + code;
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    jPanel6.removeAll();
                    GridLayout gp = new GridLayout(0, 3);
                    jPanel6.setLayout(gp);
                    while (rsLocal.next()) {
                        JButton jb = new JButton(rsLocal.getString("room_cd"));
                        if (rsLocal.getInt("is_del") == 0) {
                            jb.setBackground(Color.GREEN);
                            jb.addActionListener(new java.awt.event.ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (((JButton) e.getSource()).getBackground() == Color.GREEN) {
                                        jlblWardName.setText(jTable3.getValueAt(jTable3.getSelectedRow(), 1).toString());
                                        jlblBedNo.setText(e.getActionCommand());
                                    }
                                }
                            });
                        } else if (rsLocal.getInt("is_del") == 1) {
                            jb.setBackground(Color.RED);
                            jb.setEnabled(false);
                        }
                        jPanel6.add(jb);
                    }
                    jPanel6.updateUI();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at load data for item from group", ex);
                }
            }
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jtxtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarkFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRemarkFocusGained

    private void jtxtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarkFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtRemarkFocusLost

    private void jtxtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jTable3);
    }//GEN-LAST:event_jtxtRemarkKeyPressed

    private void jcmbMediKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbMediKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtAptDate);
    }//GEN-LAST:event_jcmbMediKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable3;
    private javax.swing.JComboBox jcmbMedi;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblBedNo;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JLabel jlblWardName;
    private com.toedter.calendar.JDateChooser jtxtAptDate;
    private javax.swing.JTextField jtxtAptTime;
    private javax.swing.JTextField jtxtDocAlias;
    private javax.swing.JTextField jtxtDoctor;
    private javax.swing.JLabel jtxtMobile;
    private javax.swing.JLabel jtxtOPDNo;
    private javax.swing.JTextField jtxtRemark;
    // End of variables declaration//GEN-END:variables
}
