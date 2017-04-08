/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMSHome;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import master.AccountMaster;
import master.CityMaster;
import support.HeaderIntFrame;
import support.Library;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author Bhaumik
 */
public class PatientMaster extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    PickList cityPickList = null;
    PickList areaPickList = null;
    PickList acPickList = null;
    private String opd_no = "";
    private String rec_no = "";
    ReportTable table = null;
    int form_id = -1;

    /**
     * Creates new form PatientMaster
     */
    public PatientMaster(int form_id) {
        initComponents();
        this.form_id = form_id;
        setPickList();
        addNavigationPanel();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel(navLoad, form_id + "");
    }

    private void setPickList() {
        cityPickList = new PickList(dataConnection);
        areaPickList = new PickList(dataConnection);
        acPickList = new PickList(dataConnection);

        cityPickList.setLayer(getLayeredPane());
        cityPickList.setPickListComponent(jtxtCity);
        cityPickList.setReturnComponent(new JTextField[]{jtxtCity});
        cityPickList.setNextComponent(jtxtArea);
        cityPickList.setAllowBlank(true);

        areaPickList.setLayer(getLayeredPane());
        areaPickList.setPickListComponent(jtxtArea);
        areaPickList.setFirstAssociation(new int[]{0, 1});
        areaPickList.setSecondAssociation(new int[]{0, 1});
        areaPickList.setReturnComponent(new JTextField[]{jtxtArea, jtxtPincode});
        areaPickList.setNextComponent(jtxtPincode);
        areaPickList.setAllowBlank(true);

        acPickList.setLayer(this.getLayeredPane());

    }

    private void setComponentText(String text) {
        jtxtAcAlias.setText(text);
        jtxtPatientName.setText(text);
        jtxtDob.setText(text);
        jtxtFstVstDate.setText(text);
        jtxtRefBy.setText(text);
        jtxtRefAlias.setText(text);
        jtxtConsAlias.setText(text);
        jtxtConsBy.setText(text);
        jtxtAddress.setText(text);
        jtxtCity.setText(text);
        jtxtArea.setText(text);
        jtxtPincode.setText(text);
        jtxtLandline.setText(text);
        jtxtMobile.setText(text);
        jtxtMobile2.setText(text);
        jtxtEmail.setText(text);
        jlblUserName.setText(text);
        jlblEditNo.setText(text);
        jlblLstUpdate.setText(text);
        jlblRefOPDNo.setText(text);
        jcmbBlood1.setSelectedIndex(0);
        jtxtMonth.setText(text);
        jtxtDays.setText(text);
        jtxtYear.setText(text);
    }

    public void setId(String opd_no) {
        this.rec_no = opd_no;
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Sr. No", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "Patient  Code", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Patient Name", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Address", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "City Name", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Area Name", -1, java.lang.String.class, null, false);
        table.AddColumn(6, "Pincode", -1, java.lang.String.class, null, false);
        table.AddColumn(7, "State Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addNavigationPanel() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtPatientName.requestFocusInWindow();

            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtPatientName.requestFocusInWindow();
            }

            @Override
            public void callSave() {

                SwingWorker workerForjbtnGenerate = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            lb.addGlassPane(navLoad);
                            dataConnection.setAutoCommit(false);
                            String sql = "";
                            if (getMode().equalsIgnoreCase("N")) {
                                sql = " insert into patientmst (pt_name,dob,sex,status,blood_group,"
                                        + "first_visit_date,ref_by,con_doc,user_id,is_mother,ref_opd_no,rec_no,opd_no) "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                opd_no = lb.generateOPDNumber();
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update patientmst set pt_name=?,dob=?,sex=?,status=?,blood_group=?,"
                                        + "first_visit_date=?,ref_by=?,con_doc=?,user_id=?,edit_no=edit_no+1,"
                                        + "time_stamp=current_timestamp,is_mother=?,ref_opd_no=?,rec_no=? where opd_no=?";
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtPatientName.getText());
                            pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtDob.getText()));
                            pstLocal.setInt(3, jcmbSex.getSelectedIndex());
                            pstLocal.setInt(4, jcmbStatus.getSelectedIndex());
                            pstLocal.setInt(5, jcmbBlood.getSelectedIndex());
                            pstLocal.setString(6, lb.ConvertDateFormetForDB(jtxtFstVstDate.getText()));
                            pstLocal.setString(7, lb.getAcCode(jtxtRefAlias.getText(), "AC"));
                            pstLocal.setString(8, lb.getAcCode(jtxtConsAlias.getText(), "AC"));
                            pstLocal.setInt(9, HMSHome.user_id);
                            pstLocal.setInt(10, jcmbBlood1.getSelectedIndex());
                            if (jcmbBlood1.getSelectedIndex() == 0) {
                                pstLocal.setString(11, "");
                            } else {
                                pstLocal.setString(11, jlblRefOPDNo.getText());
                            }
                            rec_no = ((int) (lb.isNumber(lb.getData("select max(rec_no) from patientmst"))) + 1) + "";
                            pstLocal.setString(12, rec_no);
                            pstLocal.setString(13, opd_no);
                            pstLocal.executeUpdate();

                            sql = "delete from patientinfomst where opd_no='" + opd_no + "'";
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.executeUpdate();

                            checkArea();
                            sql = "insert into patientinfomst (opd_no,address,alt_address,city_cd,area_cd,pincode,telephone,mobile,alt_mobile,email)"
                                    + " values (?,?,'',?,?,?,?,?,?,?)";
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, opd_no);
                            pstLocal.setString(2, jtxtAddress.getText());
                            pstLocal.setString(3, lb.getCityCd(jtxtCity.getText(), "C"));
                            pstLocal.setString(4, lb.getAreaCd(jtxtArea.getText(), "C"));
                            pstLocal.setString(5, jtxtPincode.getText());
                            pstLocal.setString(6, jtxtLandline.getText());
                            pstLocal.setString(7, jtxtMobile.getText());
                            pstLocal.setString(8, jtxtMobile2.getText());
                            pstLocal.setString(9, jtxtEmail.getText());
                            pstLocal.executeUpdate();

                            setSaveFlag(true);
                            dataConnection.commit();
                            dataConnection.setAutoCommit(true);
//                            if (getMode().equalsIgnoreCase("N")) {
//                                setVoucher("Last");
//                            } else if (getMode().equalsIgnoreCase("E")) {
                            setVoucher("Edit");
//                            }
                            if (getMode().equalsIgnoreCase("N")) {
                                lb.confirmDialog("Please select your option to transfer to department", "OPD", "IPD");
                                if (lb.typeThree == 0) {
                                    cancelOrClose();
                                    OPDAppointmentBook opd = new OPDAppointmentBook(opd_no);
                                    HMSHome.addOnScreen(opd, "OPD Appointment Book", 22);
                                } else if (lb.typeThree == 1) {
                                    cancelOrClose();
                                    IPDRegistrationForm opd = new IPDRegistrationForm(opd_no);
                                    HMSHome.addOnScreen(opd, "IPD Register Book", 25);
                                }
                            }
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at saveVoucher at save area master", ex);
                            try {
                                dataConnection.rollback();
                                dataConnection.setAutoCommit(true);
                            } catch (Exception e) {

                            }
                        } finally {
                            lb.removeGlassPane(navLoad);
                        }
                        return null;
                    }
                };
                workerForjbtnGenerate.execute();
            }

            @Override
            public void callDelete() {
                SwingWorker workerForjbtnGenerate = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            if (lb.isExist("opd_no", "ipdreg", opd_no, dataConnection)) {
                                if (lb.isExist("opd_no", "opdbillhd", opd_no, dataConnection)) {
                                    lb.addGlassPane(navLoad);
                                    lb.confirmDialog("Do you want to delete this Patient?");
                                    if (lb.type) {
                                        dataConnection.setAutoCommit(false);
                                        String sql = "delete from patientmst where opd_no='" + opd_no + "'";
                                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                        pstLocal.executeUpdate();

                                        sql = "delete from patientinfomst where opd_no='" + opd_no + "'";
                                        pstLocal = dataConnection.prepareStatement(sql);
                                        pstLocal.executeUpdate();

                                        setVoucher("Previous");
                                        dataConnection.commit();
                                        dataConnection.setAutoCommit(true);
                                    }
                                } else {
                                    setMessage("OPD Bill or pathologuy bill has been generated of this patient.");
                                }
                            } else {
                                navLoad.setMessage("Patient is registered in IPD Department");
                            }
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at call delete at country master", ex);
                            try {
                                dataConnection.rollback();
                                dataConnection.setAutoCommit(true);
                            } catch (Exception e) {

                            }
                        } finally {
                            lb.removeGlassPane(navLoad);
                        }
                        return null;
                    }
                };
                workerForjbtnGenerate.execute();
            }

            @Override
            public void callView() {
                String sql = "SELECT a.rec_no,a.opd_no,pt_name,address,CASE WHEN c.city_name IS NULL THEN '' ELSE "
                        + "c.city_name END AS city,CASE WHEN a2.area_name IS NULL THEN '' ELSE a2.area_name END AS AREA , "
                        + "CASE WHEN a2.pincode IS NULL THEN '' ELSE a2.pincode END AS pincode , CASE WHEN s.state_name IS NULL THEN '' ELSE "
                        + "s.state_name END AS state FROM patientmst a LEFT JOIN patientinfomst a1 ON a.opd_no=a1.opd_no "
                        + "LEFT JOIN citymst c ON a1.city_cd=c.city_cd LEFT JOIN areamst a2 ON a1.area_cd=a2.area_cd "
                        + "LEFT JOIN statemst s ON c.state_cd = s.state_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, opd_no + "", "Patient Master View", sql, "20", 1, PatientMaster.this, "Patient Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Patient Master View", -1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id + "");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from patientmst a left join patientinfomst b on a.opd_no=b.opd_no where a.rec_no=(select min(rec_no) from patientmst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from patientmst a left join patientinfomst b on a.opd_no=b.opd_no where a.rec_no=(select max(rec_no) from patientmst where rec_no <" + rec_no + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from patientmst a left join patientinfomst b on a.opd_no=b.opd_no where a.rec_no=(select min(rec_no) from patientmst where rec_no >" + rec_no + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from patientmst a left join patientinfomst b on a.opd_no=b.opd_no where a.rec_no=(select max(rec_no) from patientmst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from patientmst a left join patientinfomst b on a.opd_no=b.opd_no where a.rec_no=" + rec_no + "");
                }
                try {
                    if (viewData.next()) {
                        setComponentTextFromResultSet();
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher cityMaster'", ex);
                }

            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void callPrint() {
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtPatientName)) {
                    jtxtPatientName.requestFocusInWindow();
                    navLoad.setMessage("Patient name can not be left blank");
                    return false;
                }

                if (!lb.isExist("acntmst", "ac_alias", jtxtRefAlias.getText(), dataConnection)) {
                    jtxtRefBy.requestFocusInWindow();
                    navLoad.setMessage("Please select valid reference doctor");
                    return false;
                }

                if (!lb.isExist("acntmst", "ac_alias", jtxtConsAlias.getText(), dataConnection)) {
                    jtxtRefBy.requestFocusInWindow();
                    navLoad.setMessage("Please select valid consultant doctor");
                    return false;
                }

                if (lb.isBlank(jtxtMobile)) {
                    jtxtMobile.requestFocusInWindow();
                    setMessage("Mobile number can not be left blank");
                    return false;
                }

                if (lb.getCityCd(jtxtCity.getText(), "C").equalsIgnoreCase("") || lb.getCityCd(jtxtCity.getText(), "C").equalsIgnoreCase("0")) {
                    jtxtCity.requestFocusInWindow();
                    navLoad.setMessage("Invalid City");
                    return false;
                }

//                if (getMode().equalsIgnoreCase("N")) {
//                    if (lb.isExist("patientinfomst", "mobile", jtxtMobile.getText(), dataConnection)) {
//                        jtxtMobile.requestFocusInWindow();
//                        navLoad.setMessage("mobile number already exist");
//                        return false;
//                    }
//                } else if (getMode().equalsIgnoreCase("E")) {
//                    if (lb.isExistForEdit("patientinfomst", "mobile", jtxtMobile.getText(), "opd_no", opd_no, dataConnection)) {
//                        jtxtMobile.requestFocusInWindow();
//                        navLoad.setMessage("Mobile number already exist.");
//                        return false;
//                    }
//                }
                if (jcmbBlood1.getSelectedIndex() == 1) {
                    if (jlblRefOPDNo.getText().equalsIgnoreCase("")) {
                        navLoad.setMessage("Please select indoor patient");
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    setMessage("");
                    jlblRefOPDNo.setText("");
                    jcmbBlood1.setSelectedIndex(0);
                    rec_no = viewData.getString("rec_no");
                    opd_no = viewData.getString("opd_no");
                    jtxtAcAlias.setText(viewData.getString("opd_no"));
                    jtxtPatientName.setText(viewData.getString("pt_name"));
                    jtxtDob.setText(lb.ConvertDateFormetForDisply(viewData.getString("dob")));
                    jtxtDays.setText(lb.getYearMonthDays(jtxtDob.getText(), 0) + "");
                    jtxtMonth.setText(lb.getYearMonthDays(jtxtDob.getText(), 1) + "");
                    jtxtYear.setText(lb.getYearMonthDays(jtxtDob.getText(), 2) + "");
                    jcmbSex.setSelectedIndex(viewData.getInt("sex"));
                    jcmbStatus.setSelectedIndex(viewData.getInt("status"));
                    jcmbBlood.setSelectedIndex(viewData.getInt("blood_group"));
                    jtxtFstVstDate.setText(lb.ConvertDateFormetForDisply(viewData.getString("first_visit_date")));
                    jtxtRefAlias.setText(lb.getAcCode(viewData.getString("ref_by"), "CA"));
                    jtxtRefBy.setText(lb.getAcCode(viewData.getString("ref_by"), "N"));
                    jtxtConsAlias.setText(lb.getAcCode(viewData.getString("con_doc"), "CA"));
                    jtxtConsBy.setText(lb.getAcCode(viewData.getString("con_doc"), "N"));
                    jtxtAddress.setText(viewData.getString("address"));
                    jtxtCity.setText(lb.getCityCd(viewData.getString("city_cd"), "N"));
                    jtxtArea.setText(lb.getAreaCd(viewData.getString("area_cd"), "N"));
                    jtxtPincode.setText(lb.getAreaCd(viewData.getString("area_cd"), "CP"));
                    jtxtLandline.setText(viewData.getString("telephone"));
                    jtxtMobile.setText(viewData.getString("mobile"));
                    jtxtMobile2.setText(viewData.getString("alt_mobile"));
                    jtxtEmail.setText(viewData.getString("email"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                    jcmbBlood1.setSelectedIndex(viewData.getInt("is_mother"));
                    jlblRefOPDNo.setText(viewData.getString("ref_opd_no"));

                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromResultSet", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag
            ) {
                jtxtAcAlias.setEnabled(!flag);
                jtxtPatientName.setEnabled(flag);
                jtxtAddress.setEnabled(flag);
                jtxtCity.setEnabled(flag);
                jtxtArea.setEnabled(flag);
                jtxtPincode.setEnabled(flag);
                jtxtLandline.setEnabled(flag);
                jtxtMobile.setEnabled(flag);
                jtxtMobile2.setEnabled(flag);
                jtxtEmail.setEnabled(flag);
                jtxtDob.setEnabled(flag);
                jBillDateBtn.setEnabled(flag);
                jBillDateBtn1.setEnabled(flag);
                jtxtFstVstDate.setEnabled(flag);
                jcmbSex.setEnabled(flag);
                jcmbBlood.setEnabled(flag);
                jcmbStatus.setEnabled(flag);
                jtxtRefAlias.setEnabled(false);
                jtxtRefBy.setEnabled(flag);
                jtxtConsBy.setEnabled(flag);
                jtxtConsAlias.setEnabled(false);
                jtxtAddress.setEnabled(flag);
                jButton1.setEnabled(flag);
                jcmbBlood1.setEnabled(flag);
                jtxtDays.setEnabled(flag);
                jtxtMonth.setEnabled(flag);
                jtxtYear.setEnabled(flag);
                if (flag) {
                    jcmbBlood1ItemStateChanged(null);
                }
            }
        }
        navLoad = new navPanel();
        jpanelNavigation.add(navLoad);
        navLoad.setVisible(true);
        navLoad.setprintFlag(false);
    }

    private void checkArea() throws SQLException {
        if (lb.getAreaCd(jtxtArea.getText(), "C").equalsIgnoreCase("") || lb.getAreaCd(jtxtArea.getText(), "C").equalsIgnoreCase("0")) {
            String sql = "INSERT INTO areamst (area_name, pincode,city_cd, USER_ID) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = dataConnection.prepareStatement(sql);
            ps.setString(1, jtxtArea.getText());
            ps.setInt(2, (int) lb.isNumber(jtxtPincode.getText()));
            ps.setString(3, lb.getCityCd(jtxtCity.getText(), "C"));
            ps.setInt(4, HMSHome.user_id);
            ps.executeUpdate();
        }
    }

    private void cancelOrClose() {
        if (navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            navLoad.setMode("");
            navLoad.setComponentEnabledDisabled(false);
            navLoad.setMessage("");
            navLoad.setSaveFlag(true);
            navLoad.setVoucher("Edit");
        }
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
        jtxtAcAlias = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtPatientName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxtDob = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jcmbSex = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jcmbStatus = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jcmbBlood = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jtxtFstVstDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jtxtRefBy = new javax.swing.JTextField();
        jtxtRefAlias = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jtxtConsBy = new javax.swing.JTextField();
        jtxtConsAlias = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jcmbBlood1 = new javax.swing.JComboBox();
        jlblRefOPDNo = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jtxtYear = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jtxtMonth = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jtxtDays = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtCity = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtxtArea = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtxtPincode = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxtAddress = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jtxtLandline = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jtxtMobile2 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jtxtEmail = new javax.swing.JTextField();
        jpanelNavigation = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("OPD No.");

        jLabel2.setText("Name");

        jtxtPatientName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPatientNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPatientNameFocusLost(evt);
            }
        });
        jtxtPatientName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPatientNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPatientNameKeyTyped(evt);
            }
        });

        jLabel7.setText("D.O.B");

        jtxtDob.setBackground(new java.awt.Color(255, 204, 102));
        jtxtDob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDobFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDobFocusLost(evt);
            }
        });
        jtxtDob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDobKeyPressed(evt);
            }
        });

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jLabel13.setText("Sex");

        jcmbSex.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));
        jcmbSex.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbSexKeyPressed(evt);
            }
        });

        jLabel16.setText("Status");

        jcmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Married", "Single" }));
        jcmbStatus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbStatusKeyPressed(evt);
            }
        });

        jLabel17.setText("Blood Group");

        jcmbBlood.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-" }));
        jcmbBlood.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbBloodKeyPressed(evt);
            }
        });

        jLabel18.setText("First Visit");

        jtxtFstVstDate.setBackground(new java.awt.Color(255, 204, 102));
        jtxtFstVstDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFstVstDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFstVstDateFocusLost(evt);
            }
        });
        jtxtFstVstDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFstVstDateKeyPressed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Ref By");

        jtxtRefBy.setBackground(new java.awt.Color(255, 204, 102));
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

        jLabel19.setText("Consaltant Doc");

        jtxtConsBy.setBackground(new java.awt.Color(255, 204, 102));
        jtxtConsBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtConsByFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtConsByFocusLost(evt);
            }
        });
        jtxtConsBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtConsByKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtConsByKeyReleased(evt);
            }
        });

        jLabel20.setText("Is Mother");

        jcmbBlood1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Yes" }));
        jcmbBlood1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcmbBlood1ItemStateChanged(evt);
            }
        });
        jcmbBlood1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbBlood1KeyPressed(evt);
            }
        });

        jlblRefOPDNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton1.setText("Indoor Patient List");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel21.setText("Year");

        jtxtYear.setBackground(new java.awt.Color(255, 204, 102));
        jtxtYear.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtYearFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtYearFocusLost(evt);
            }
        });
        jtxtYear.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtYearKeyPressed(evt);
            }
        });

        jLabel22.setText("Month");

        jtxtMonth.setBackground(new java.awt.Color(255, 204, 102));
        jtxtMonth.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMonthFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMonthFocusLost(evt);
            }
        });
        jtxtMonth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMonthKeyPressed(evt);
            }
        });

        jLabel23.setText("Days");

        jtxtDays.setBackground(new java.awt.Color(255, 204, 102));
        jtxtDays.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDaysFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDaysFocusLost(evt);
            }
        });
        jtxtDays.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDaysKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDob, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFstVstDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtConsBy, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jcmbSex, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbBlood, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbBlood1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jtxtRefAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblRefOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1))
                            .addComponent(jtxtConsAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jtxtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jtxtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jtxtDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jtxtDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jcmbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jcmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jcmbBlood, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jcmbBlood1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jlblRefOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRefAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFstVstDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtConsBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtConsAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtAcAlias});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtPatientName});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel13, jLabel7, jtxtDob});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel16, jcmbStatus});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel17, jcmbBlood});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn1, jLabel18, jtxtFstVstDate});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jtxtRefAlias, jtxtRefBy});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel19, jtxtConsAlias, jtxtConsBy});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel20, jcmbBlood1});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jlblRefOPDNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel21, jLabel22, jLabel23, jtxtDays, jtxtMonth, jtxtYear});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Address");

        jLabel4.setText("City");

        jtxtCity.setBackground(new java.awt.Color(255, 204, 102));
        jtxtCity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCityFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCityFocusLost(evt);
            }
        });
        jtxtCity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCityKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtCityKeyReleased(evt);
            }
        });

        jLabel5.setText("Area");

        jtxtArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAreaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAreaFocusLost(evt);
            }
        });
        jtxtArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAreaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAreaKeyReleased(evt);
            }
        });

        jLabel6.setText("Pincode");

        jtxtPincode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPincodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPincodeFocusLost(evt);
            }
        });
        jtxtPincode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPincodeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPincodeKeyTyped(evt);
            }
        });

        jtxtAddress.setColumns(20);
        jtxtAddress.setRows(5);
        jtxtAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddressKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jtxtAddress);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtArea, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtPincode, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtPincode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jtxtCity});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jtxtArea});

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setText("Landline #");

        jtxtLandline.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtLandline.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtLandlineFocusGained(evt);
            }
        });
        jtxtLandline.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtLandlineKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtLandlineKeyTyped(evt);
            }
        });

        jLabel14.setText("Mobile (1)");

        jtxtMobile.setBackground(new java.awt.Color(255, 204, 102));
        jtxtMobile.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtMobile.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMobileFocusGained(evt);
            }
        });
        jtxtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyTyped(evt);
            }
        });

        jLabel27.setText("Mobile (2)");

        jtxtMobile2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtMobile2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMobile2FocusGained(evt);
            }
        });
        jtxtMobile2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMobile2KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMobile2KeyTyped(evt);
            }
        });

        jLabel15.setText("Email (1)");

        jtxtEmail.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtEmailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtEmailFocusLost(evt);
            }
        });
        jtxtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtEmailKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtLandline, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(111, 111, 111)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                    .addComponent(jtxtMobile2))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jtxtLandline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jtxtMobile2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel14, jtxtLandline, jtxtMobile});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel27, jtxtMobile2});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel15, jtxtEmail});

        jpanelNavigation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jpanelNavigation.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("User:");

        jLabel9.setText("Edit No:");

        jLabel11.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpanelNavigation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtLandlineFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLandlineFocusGained
        // TODO add your handling code here:
        jtxtLandline.selectAll();
    }//GEN-LAST:event_jtxtLandlineFocusGained

    private void jtxtLandlineKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLandlineKeyPressed
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jtxtLandlineKeyPressed

    private void jtxtLandlineKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLandlineKeyTyped
        lb.fixLength(evt, 20);
    }//GEN-LAST:event_jtxtLandlineKeyTyped

    private void jtxtMobileFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMobileFocusGained
        // TODO add your handling code here:
        jtxtMobile.selectAll();
    }//GEN-LAST:event_jtxtMobileFocusGained

    private void jtxtMobileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyPressed
        lb.enterFocus(evt, jtxtMobile2);
    }//GEN-LAST:event_jtxtMobileKeyPressed

    private void jtxtMobileKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtMobileKeyTyped

    private void jtxtMobile2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMobile2FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMobile2FocusGained

    private void jtxtMobile2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobile2KeyPressed
        lb.enterFocus(evt, jtxtEmail);
    }//GEN-LAST:event_jtxtMobile2KeyPressed

    private void jtxtMobile2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobile2KeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtMobile2KeyTyped

    private void jtxtEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmailFocusGained
        // TODO add your handling code here:
        jtxtEmail.selectAll();
    }//GEN-LAST:event_jtxtEmailFocusGained

    private void jtxtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmailFocusLost
        // TODO add your handling code here:
        ((JTextField) evt.getComponent()).setText(((JTextField) evt.getComponent()).getText().toUpperCase());
    }//GEN-LAST:event_jtxtEmailFocusLost

    private void jtxtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtEmailKeyPressed
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtEmailKeyPressed

    private void jtxtPatientNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPatientNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPatientNameFocusGained

    private void jtxtPatientNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPatientNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtPatientNameFocusLost

    private void jtxtPatientNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPatientNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtYear);
    }//GEN-LAST:event_jtxtPatientNameKeyPressed

    private void jtxtPatientNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPatientNameKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtPatientNameKeyTyped

    private void jtxtCityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCityFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCityFocusGained

    private void jtxtCityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCityFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
        ((JTextField) evt.getComponent()).setText(((JTextField) evt.getComponent()).getText().toUpperCase());
        if (!jtxtCity.getText().isEmpty()) {
            if (lb.getCityCd(jtxtCity.getText(), "c").equalsIgnoreCase("") || lb.getCityCd(jtxtCity.getText(), "c").equalsIgnoreCase("0")) {
                if (JOptionPane.showConfirmDialog(null, "City does not exist in city master.\n Do you want to add city?", "Account Master", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    CityMaster cm = new CityMaster(jtxtCity.getText(), this, 12);
                    HMSHome.addOnScreen(cm, "City Master", 13);
                    cm.setFocus();
                }
            }
        }
    }//GEN-LAST:event_jtxtCityFocusLost

    private void jtxtCityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCityKeyPressed
        // TODO add your handling code here:
        cityPickList.setLocation(jtxtCity.getX() + jPanel1.getX(), jtxtCity.getY() + jtxtCity.getHeight() + jPanel2.getY());
        cityPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtCityKeyPressed

    private void jtxtCityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCityKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("select (city_name) as \"City Name\",(state_name) as \"State\",(country_name) as \"Country\" \n"
                    + "from citymst c,countrymst ck,statemst s "
                    + "where s.state_cd=c.state_cd and ck.country_cd=s.country_cd "
                    + "and (c.city_name) like  '" + jtxtCity.getText().toUpperCase() + "%'");
            cityPickList.setPreparedStatement(psLocal);
            cityPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in Patient master", ex);
        }
    }//GEN-LAST:event_jtxtCityKeyReleased

    private void jtxtAreaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAreaFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAreaFocusGained

    private void jtxtAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAreaFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAreaFocusLost

    private void jtxtAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAreaKeyPressed
        // TODO add your handling code here:
        areaPickList.setLocation(jtxtArea.getX() + jPanel2.getX(), jtxtArea.getY() + jtxtArea.getHeight() + jPanel2.getY());
        areaPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtAreaKeyPressed

    private void jtxtAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAreaKeyReleased
        // TODO add your handling code here:
        try {
            areaPickList.setFirstAssociation(new int[]{0, 1});
            PreparedStatement psLocal = dataConnection.prepareStatement("select area_name as \"Area Name\",pincode as \"Pin Code\" from areamst where (area_name) like '" + jtxtArea.getText().toUpperCase() + "%'");
            areaPickList.setPreparedStatement(psLocal);
            areaPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at jtxtAreaNameKeyReleased in sales man", ex);
        }
    }//GEN-LAST:event_jtxtAreaKeyReleased

    private void jtxtDobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDobFocusGained
        // TODO add your handling code here:
        jtxtDob.selectAll();
    }//GEN-LAST:event_jtxtDobFocusGained

    private void jtxtDobFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDobFocusLost
        lb.checkDate(jtxtDob);
    }//GEN-LAST:event_jtxtDobFocusLost

    private void jtxtDobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDobKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (lb.isBlank(jtxtDob)) {
                lb.setDateChooserPropertyInit(jtxtDob);
            }
            evt.consume();
            if (lb.checkDate2(jtxtDob)) {
                jcmbSex.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtDob.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtDobKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtDob);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtDob.getX(), jtxtDob.getY() + 125, jtxtDob.getX() + odc.getWidth(), jtxtDob.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jtxtFstVstDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFstVstDateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFstVstDateFocusGained

    private void jtxtFstVstDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFstVstDateFocusLost
        // TODO add your hndling code here:
        lb.checkDate(jtxtFstVstDate);
    }//GEN-LAST:event_jtxtFstVstDateFocusLost

    private void jtxtFstVstDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFstVstDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (lb.isBlank(jtxtFstVstDate)) {
                lb.setDateChooserPropertyInit(jtxtFstVstDate);
            }
            if (lb.checkDate2(jtxtFstVstDate)) {
                jtxtRefBy.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtFstVstDate.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtFstVstDateKeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        // TODO add your handling code here
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFstVstDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFstVstDate.getX(), jtxtFstVstDate.getY() + 125, jtxtFstVstDate.getX() + odc.getWidth(), jtxtFstVstDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jtxtRefByFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRefByFocusGained

    private void jtxtRefByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtRefByFocusLost

    private void jtxtRefByKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyPressed
        acPickList.setLocation(jtxtRefBy.getX() + jPanel1.getX(), jtxtRefBy.getY() + jtxtRefBy.getHeight() + jPanel1.getY());
        acPickList.setPickListComponent(jtxtRefBy);
        acPickList.setNextComponent(jtxtConsBy);
        acPickList.setReturnComponent(new JTextField[]{jtxtRefBy, jtxtRefAlias});
        acPickList.pickListKeyPress(evt);
        if (lb.isEnter(evt)) {
            if (lb.getAcCode(jtxtRefAlias.getText(), "AC").equalsIgnoreCase("")
                    || lb.getAcCode(jtxtRefAlias.getText(), "AC").equalsIgnoreCase("0")) {
                lb.confirmDialog("Doctor does not exist. Do you want to add this doctor?");
                if (lb.type) {
                    AccountMaster am = new AccountMaster(15);
                    HMSHome.addOnScreen(am, "Account Master", 16);
                    am.navLoad.callNew();
                    return;
                }
            }
        }
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

    private void jtxtConsByFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtConsByFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtConsByFocusGained

    private void jtxtConsByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtConsByFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtConsByFocusLost

    private void jtxtConsByKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtConsByKeyPressed
        // TODO add your handling code here:
        acPickList.setLocation(jtxtConsBy.getX() + jPanel1.getX(), jtxtConsBy.getY() + jtxtConsBy.getHeight() + jPanel1.getHeight());
        acPickList.setPickListComponent(jtxtConsBy);
        acPickList.setNextComponent(jtxtAddress);
        acPickList.setReturnComponent(new JTextField[]{jtxtConsBy, jtxtConsAlias});
        acPickList.pickListKeyPress(evt);
        if (lb.isEnter(evt)) {
            if (lb.getAcCode(jtxtConsAlias.getText(), "AC").equalsIgnoreCase("")
                    || lb.getAcCode(jtxtConsAlias.getText(), "AC").equalsIgnoreCase("0")) {
                lb.confirmDialog("Doctor does not exist. Do you want to add this doctor?");
                if (lb.type) {
                    AccountMaster am = new AccountMaster(15);
                    HMSHome.addOnScreen(am, "Account Master", 16);
                    am.navLoad.callNew();
                    return;
                }
            }
        }
    }//GEN-LAST:event_jtxtConsByKeyPressed

    private void jtxtConsByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtConsByKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("SELECT ac_name,ac_alias FROM acntmst "
                    + " WHERE ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '%" + jtxtConsBy.getText().toUpperCase() + "%'");
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1});
            acPickList.setSecondAssociation(new int[]{0, 1});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtConsByKeyReleased

    private void jcmbSexKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbSexKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbStatus);
    }//GEN-LAST:event_jcmbSexKeyPressed

    private void jcmbStatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbStatusKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbBlood);
    }//GEN-LAST:event_jcmbStatusKeyPressed

    private void jcmbBloodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbBloodKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbBlood1);
    }//GEN-LAST:event_jcmbBloodKeyPressed

    private void jtxtAddressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddressKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                jtxtCity.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtAddressKeyPressed

    private void jtxtPincodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPincodeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPincodeFocusGained

    private void jtxtPincodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPincodeFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtPincodeFocusLost

    private void jtxtPincodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPincodeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtLandline);
    }//GEN-LAST:event_jtxtPincodeKeyPressed

    private void jtxtPincodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPincodeKeyTyped
        // TODO add your handling code here:
        lb.onlyInteger(evt, 6);
    }//GEN-LAST:event_jtxtPincodeKeyTyped

    private void jcmbBlood1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbBlood1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtFstVstDate);
    }//GEN-LAST:event_jcmbBlood1KeyPressed

    private void jcmbBlood1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmbBlood1ItemStateChanged
        // TODO add your handling code here:
        if (jcmbBlood1.getSelectedIndex() == 0) {
            jButton1.setEnabled(false);
        } else {
            jButton1.setEnabled(true);
        }
    }//GEN-LAST:event_jcmbBlood1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        JDialog d = new JDialog();
        d.setModal(true);
        IndorPatientList sp = new IndorPatientList(d, this);
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

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jtxtYearFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtYearFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtYearFocusGained

    private void jtxtYearFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtYearFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtYearFocusLost

    private void jtxtYearKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtYearKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMonth);
        jtxtDob.setText(lb.getBirthDateFromDifferenceInDDMMYYYY((int) lb.isNumber(jtxtYear), (int) lb.isNumber(jtxtMonth), (int) lb.isNumber(jtxtDays)));
    }//GEN-LAST:event_jtxtYearKeyPressed

    private void jtxtMonthFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMonthFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMonthFocusGained

    private void jtxtMonthFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMonthFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtMonthFocusLost

    private void jtxtMonthKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMonthKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtDays);
        jtxtDob.setText(lb.getBirthDateFromDifferenceInDDMMYYYY((int) lb.isNumber(jtxtYear), (int) lb.isNumber(jtxtMonth), (int) lb.isNumber(jtxtDays)));
    }//GEN-LAST:event_jtxtMonthKeyPressed

    private void jtxtDaysFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDaysFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDaysFocusGained

    private void jtxtDaysFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDaysFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
        jtxtDob.setText(lb.getBirthDateFromDifferenceInDDMMYYYY((int) lb.isNumber(jtxtYear), (int) lb.isNumber(jtxtMonth), (int) lb.isNumber(jtxtDays)));
    }//GEN-LAST:event_jtxtDaysFocusLost

    private void jtxtDaysKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDaysKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtDob);
    }//GEN-LAST:event_jtxtDaysKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel27;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jcmbBlood;
    private javax.swing.JComboBox jcmbBlood1;
    private javax.swing.JComboBox jcmbSex;
    private javax.swing.JComboBox jcmbStatus;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    public javax.swing.JLabel jlblRefOPDNo;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JPanel jpanelNavigation;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextArea jtxtAddress;
    private javax.swing.JTextField jtxtArea;
    public javax.swing.JTextField jtxtCity;
    private javax.swing.JTextField jtxtConsAlias;
    private javax.swing.JTextField jtxtConsBy;
    private javax.swing.JTextField jtxtDays;
    private javax.swing.JTextField jtxtDob;
    private javax.swing.JTextField jtxtEmail;
    private javax.swing.JTextField jtxtFstVstDate;
    private javax.swing.JTextField jtxtLandline;
    private javax.swing.JTextField jtxtMobile;
    private javax.swing.JTextField jtxtMobile2;
    private javax.swing.JTextField jtxtMonth;
    private javax.swing.JTextField jtxtPatientName;
    private javax.swing.JTextField jtxtPincode;
    private javax.swing.JTextField jtxtRefAlias;
    private javax.swing.JTextField jtxtRefBy;
    private javax.swing.JTextField jtxtYear;
    // End of variables declaration//GEN-END:variables
}
