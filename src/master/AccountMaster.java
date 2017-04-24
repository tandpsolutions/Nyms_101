/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import hms.HMSHome;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import support.HeaderIntFrame;
import support.Library;
import support.PickList;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author Bhaumik
 */
public class AccountMaster extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    public SmallNavigation navLoad = null;
    PickList groupPicklist = null;
    PickList cityPickList = null;
    PickList areaPickList = null;
    private String ac_cd = "-1";
    ReportTable table = null;
    int form_id = -1;
    PickList splList = null;

    /**
     * Creates new form AccountMaster
     */
    public AccountMaster(int form_id) {
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
        groupPicklist = new PickList(dataConnection);
        splList = new PickList(dataConnection);

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

        groupPicklist.setLayer(getLayeredPane());
        groupPicklist.setPickListComponent(jtxtGroupName);
        groupPicklist.setReturnComponent(new JTextField[]{jtxtGroupName});
        groupPicklist.setNextComponent(jtxtSpeciality);

        splList.setLayer(this.getLayeredPane());
        splList.setPickListComponent(jtxtSpeciality);
        splList.setNextComponent(jcmbStar);
        splList.setReturnComponent(new JTextField[]{jtxtSpeciality});
    }

    private void setComponentText(String text) {
        jtxtAcAlias.setText(text);
        jtxtACName.setText(text);
        jtxtGroupName.setText(text);
        jtxtAddress1.setText(text);
        jtxtAddress2.setText(text);
        jtxtAddress3.setText(text);
        jtxtCity.setText(text);
        jtxtArea.setText(text);
        jtxtPincode.setText(text);
        jtxtLandline.setText(text);
        jtxtMobile.setText(text);
        jtxtMobile2.setText(text);
        jtxtFax.setText(text);
        jtxtEmail.setText(text);
        jtxtEmail2.setText(text);
        jtxtSpeciality.setText(text);
        jtxtOPB.setText("0.00");
        jcmbStar.setSelectedIndex(0);
    }

    public void setId(String ac_cd) {
        this.ac_cd = ac_cd;
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Account  Code", -1, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Account Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Address", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "City Name", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "Area Name", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Pincode", -1, java.lang.String.class, null, false);
        table.AddColumn(6, "State Name", -1, java.lang.String.class, null, false);
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
                jtxtACName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtACName.requestFocusInWindow();
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
                            String ac_alias = lb.generateKey("acntmst", "ac_alias", 5, "A");
                            if (getMode().equalsIgnoreCase("N")) {
                                sql = " insert into acntmst (ac_alias,ac_name,grp_cd,opb_amt,user_id,time_stamp,spec_sub_cd,is_star)"
                                        + " values (?,?,?,?,?,current_timestamp,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update acntmst set ac_alias=?,ac_name=?,grp_cd=?,opb_amt=?,user_id=?,edit_no=edit_no+1,"
                                        + "time_stamp=current_timestamp,spec_sub_cd=?,is_star=? where ac_cd=" + ac_cd;
                                ac_alias = jtxtAcAlias.getText();
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, ac_alias);
                            if (jtxtGroupName.getText().equalsIgnoreCase("Hospital Doctor")
                                    || jtxtGroupName.getText().equalsIgnoreCase("REFERENCE DOCTOR")) {
                                pstLocal.setString(2, "DR." + jtxtACName.getText().replaceAll("DR.", ""));
                            } else {
                                pstLocal.setString(2, jtxtACName.getText());
                            }
                            pstLocal.setString(3, lb.getGroupName(jtxtGroupName.getText(), "C"));
                            pstLocal.setDouble(4, lb.isNumber(jtxtOPB));
                            pstLocal.setInt(5, HMSHome.user_id);
                            pstLocal.setString(6, lb.getSubSpecialistCd(jtxtSpeciality.getText(), "C"));
                            pstLocal.setInt(7, jcmbStar.getSelectedIndex());
                            pstLocal.executeUpdate();

                            ac_cd = lb.getAcCode(ac_alias, "AC");

                            if (getMode().equalsIgnoreCase("N")) {
                                sql = " insert into doctormaster (sub_spec_cd,user_id,ac_cd) values (?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update doctormaster set sub_spec_cd=?,user_id=?,edit_no=edit_no+1,"
                                        + "time_stamp=current_timestamp where ac_cd=?";
                            }

                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, lb.getSubSpecialistCd(jtxtSpeciality.getText(), "C"));
                            pstLocal.setInt(2, HMSHome.user_id);
                            pstLocal.setString(3, ac_cd);
                            pstLocal.executeUpdate();

                            sql = "delete from adbkmst where ac_cd=" + ac_cd;
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.executeUpdate();

                            sql = "delete from phbkmst where ac_cd=" + ac_cd;
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.executeUpdate();

                            checkArea();
                            sql = "insert into adbkmst (ac_cd,add1,add2,add3,city_cd,area_cd) values (?,?,?,?,?,?)";
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, ac_cd);
                            pstLocal.setString(2, jtxtAddress1.getText());
                            pstLocal.setString(3, jtxtAddress2.getText());
                            pstLocal.setString(4, jtxtAddress3.getText());
                            pstLocal.setString(5, lb.getCityCd(jtxtCity.getText(), "C"));
                            pstLocal.setString(6, lb.getAreaCd(jtxtArea.getText(), "C"));
                            pstLocal.executeUpdate();

                            sql = " insert into phbkmst (ac_cd,ll_no,mobile1,mobile2,fax,email1,email2) values (?,?,?,?,?,?,?)";
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, ac_cd);
                            pstLocal.setString(2, jtxtLandline.getText());
                            pstLocal.setString(3, jtxtMobile.getText());
                            pstLocal.setString(4, jtxtMobile2.getText());
                            pstLocal.setString(5, jtxtFax.getText());
                            pstLocal.setString(6, jtxtEmail.getText());
                            pstLocal.setString(7, jtxtEmail2.getText());
                            pstLocal.executeUpdate();

                            setSaveFlag(true);
                            dataConnection.commit();
                            dataConnection.setAutoCommit(true);
                            if (getMode().equalsIgnoreCase("N")) {
                                setVoucher("Last");
                            } else if (getMode().equalsIgnoreCase("E")) {
                                setVoucher("Edit");
                            }
                            setMode("");
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
                            if (!lb.isExist("doc_cd", "opdbilldt", ac_cd + "", dataConnection)) {
                                if (lb.getData("doc_cd", "ipdbilldt", "is_del=0 and doc_cd", ac_cd + "", 1).equalsIgnoreCase("")) {
                                    if (!lb.isExist("ref_by", "patientmst", ac_cd + "", dataConnection) || !lb.isExist("con_doc", "patientmst", ac_cd + "", dataConnection)) {
                                        lb.addGlassPane(navLoad);
                                        lb.confirmDialog("Do you want to delete this account?");
                                        if (lb.type) {
                                            dataConnection.setAutoCommit(false);
                                            String sql = "delete from acntmst where ac_cd=" + ac_cd;
                                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                            pstLocal.executeUpdate();

                                            sql = "delete from adbkmst where ac_cd=" + ac_cd;
                                            pstLocal = dataConnection.prepareStatement(sql);
                                            pstLocal.executeUpdate();

                                            sql = "delete from phbkmst where ac_cd=" + ac_cd;
                                            pstLocal = dataConnection.prepareStatement(sql);
                                            pstLocal.executeUpdate();

                                            sql = "delete from doctormaster where ac_cd=" + ac_cd;
                                            pstLocal = dataConnection.prepareStatement(sql);
                                            pstLocal.executeUpdate();

                                            setVoucher("Previous");
                                            dataConnection.commit();
                                            dataConnection.setAutoCommit(true);
                                        }
                                    } else {
                                        setMessage("Doctor exist in Patient Master");
                                    }
                                } else {
                                    setMessage("Doctor exist in IPD due billing");
                                }
                            } else {
                                setMessage("Doctor exist in OPD billing");
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
                String sql = "SELECT a.ac_cd,ac_name,CONCAT(a1.add1,a1.add2,a1.add3) AS add1,CASE WHEN c.city_name IS NULL THEN '' ELSE "
                        + "c.city_name END AS city,CASE WHEN a2.area_name IS NULL THEN '' ELSE a2.area_name END AS AREA , "
                        + "CASE WHEN a2.pincode IS NULL THEN '' ELSE a2.pincode END AS pincode , CASE WHEN s.state_name IS NULL THEN '' ELSE "
                        + "s.state_name END AS state FROM acntmst a LEFT JOIN adbkmst a1 ON a.ac_cd=a1.ac_cd LEFT JOIN phbkmst p ON a.ac_cd = p.ac_cd "
                        + "LEFT JOIN citymst c ON a1.city_cd=c.city_cd LEFT JOIN areamst a2 ON a1.area_cd=a2.area_cd "
                        + "LEFT JOIN statemst s ON c.state_cd = s.state_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ac_cd + "", "Account Master View", sql, "15", 1, AccountMaster.this, "Account Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Area Master View", -1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id + "");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from acntmst a left join adbkmst b on a.ac_cd=b.ac_cd left join phbkmst p on a.ac_cd=p.ac_cd where a.ac_cd=(select min(ac_cd) from acntmst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from acntmst a left join adbkmst b on a.ac_cd=b.ac_cd left join phbkmst p on a.ac_cd=p.ac_cd where a.ac_cd=(select max(ac_cd) from acntmst where ac_cd <" + ac_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from acntmst a left join adbkmst b on a.ac_cd=b.ac_cd left join phbkmst p on a.ac_cd=p.ac_cd where a.ac_cd=(select min(ac_cd) from acntmst where ac_cd >" + ac_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from acntmst a left join adbkmst b on a.ac_cd=b.ac_cd left join phbkmst p on a.ac_cd=p.ac_cd where a.ac_cd=(select max(ac_cd) from acntmst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from acntmst a left join adbkmst b on a.ac_cd=b.ac_cd left join phbkmst p on a.ac_cd=p.ac_cd where a.ac_cd=" + ac_cd);
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
                if (lb.isBlank(jtxtACName)) {
                    jtxtACName.requestFocusInWindow();
                    navLoad.setMessage("Account name can not be left blank");
                    return false;
                }

                if (lb.isBlank(jtxtGroupName)) {
                    jtxtGroupName.requestFocusInWindow();
                    navLoad.setMessage("Group name can not be left blank");
                    return false;
                }

                if (!lb.isExist("groupmst", "group_name", jtxtGroupName.getText(), dataConnection)) {
                    jtxtGroupName.requestFocusInWindow();
                    navLoad.setMessage("Invalid group name");
                    return false;
                }
                if (!lb.isBlank(jtxtSpeciality)) {
                    if (lb.getSubSpecialistCd(jtxtSpeciality.getText(), "C").equalsIgnoreCase("") || lb.getSubSpecialistCd(jtxtSpeciality.getText(), "C").equalsIgnoreCase("0")) {
                        jtxtSpeciality.requestFocusInWindow();
                        navLoad.setMessage("Invalid Speciality");
                        return false;
                    }
                } else {
                    jtxtSpeciality.requestFocusInWindow();
                    navLoad.setMessage("Invalid Speciality");
                    return false;
                }

                if (!lb.isBlank(jtxtCity)) {
                    if (lb.getCityCd(jtxtCity.getText(), "C").equalsIgnoreCase("") || lb.getCityCd(jtxtCity.getText(), "C").equalsIgnoreCase("0")) {
                        jtxtCity.requestFocusInWindow();
                        navLoad.setMessage("Invalid City");
                        return false;
                    }
                } else {
                    jtxtCity.requestFocusInWindow();
                    navLoad.setMessage("Invalid City");
                    return false;
                }

                if (lb.isBlank(jtxtArea)) {
                    jtxtArea.requestFocusInWindow();
                    navLoad.setMessage("Invalid Area");
                    return false;
                }

                if (!lb.isBlank(jtxtMobile)) {
                    if (jtxtMobile.getText().length() == 10) {
                        if (getMode().equalsIgnoreCase("N")) {
                            if (lb.isExist("phbkmst", "mobile1", jtxtMobile.getText(), dataConnection)) {
                                jtxtMobile.requestFocusInWindow();
                                navLoad.setMessage("mobile number already exist");
                                return false;
                            }
                        } else if (getMode().equalsIgnoreCase("E")) {
                            if (lb.isExistForEdit("phbkmst", "mobile1", jtxtMobile.getText(), "ac_cd", ac_cd, dataConnection)) {
                                jtxtMobile.requestFocusInWindow();
                                navLoad.setMessage("Mobile number already exist.");
                                return false;
                            }
                        }
                    } else {
                        navLoad.setMessage("Mobile Number should be 10 digit");
                        jtxtMobile.requestFocusInWindow();
                        return false;
                    }
                } else {
                    jtxtMobile.requestFocusInWindow();
                    navLoad.setMessage("Invalid Mobile No");
                    return false;
                }
                return true;
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    setMessage("");
                    ac_cd = viewData.getString("ac_cd");
                    jtxtAcAlias.setText(viewData.getString("ac_alias"));
                    jtxtGroupName.setText(lb.getGroupName(viewData.getString("grp_cd"), "N"));
                    if (jtxtGroupName.getText().equalsIgnoreCase("Hospital Doctor") || jtxtGroupName.getText().equalsIgnoreCase("REFERENCE DOCTOR")) {
                        jlblDr.setText("DR.");
                    } else {
                        jlblDr.setText("");
                    }
                    jtxtACName.setText(viewData.getString("ac_name").replaceAll("DR.", ""));
                    jtxtAddress1.setText(viewData.getString("add1"));
                    jtxtAddress2.setText(viewData.getString("add2"));
                    jtxtAddress3.setText(viewData.getString("add3"));
                    jtxtCity.setText(lb.getCityCd(viewData.getString("city_cd"), "N"));
                    jtxtArea.setText(lb.getAreaCd(viewData.getString("area_cd"), "N"));
                    jtxtPincode.setText(lb.getAreaCd(viewData.getString("area_cd"), "CP"));
                    jtxtSpeciality.setText(lb.getSubSpecialistCd(viewData.getString("spec_sub_cd"), "N"));
                    jtxtLandline.setText(viewData.getString("ll_no"));
                    jtxtMobile.setText(viewData.getString("mobile1"));
                    jtxtMobile2.setText(viewData.getString("mobile2"));
                    jtxtFax.setText(viewData.getString("fax"));
                    jtxtEmail.setText(viewData.getString("email1"));
                    jtxtEmail2.setText(viewData.getString("email2"));
                    jtxtOPB.setText(viewData.getString("opb_amt"));
                    jcmbEffect.setSelectedIndex(viewData.getInt("opb_eff"));
                    jcmbStar.setSelectedIndex(viewData.getInt("is_star"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));

                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromResultSet", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag
            ) {
                jtxtAcAlias.setEnabled(!flag);
                jtxtACName.setEnabled(flag);
                jtxtGroupName.setEnabled(flag);
                jtxtSpeciality.setEnabled(flag);
                jtxtAddress1.setEnabled(flag);
                jtxtAddress2.setEnabled(flag);
                jtxtAddress3.setEnabled(flag);
                jtxtCity.setEnabled(flag);
                jtxtArea.setEnabled(flag);
                jtxtPincode.setEnabled(flag);
                jtxtLandline.setEnabled(flag);
                jtxtMobile.setEnabled(flag);
                jtxtMobile2.setEnabled(flag);
                jtxtFax.setEnabled(flag);
                jtxtEmail.setEnabled(flag);
                jtxtEmail2.setEnabled(flag);
                jtxtOPB.setEnabled(flag);
                jcmbEffect.setEnabled(flag);
                jcmbStar.setEnabled(flag);
            }

        }

        navLoad = new navPanel();

        jpanelNavigation.add(navLoad);

        navLoad.setVisible(
                true);
        navLoad.setprintFlag(
                false);
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
        jtxtACName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtGroupName = new javax.swing.JTextField();
        jlblDr = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtxtSpeciality = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jcmbStar = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jtxtAddress1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtAddress3 = new javax.swing.JTextField();
        jtxtCity = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtxtArea = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtxtPincode = new javax.swing.JTextField();
        jtxtAddress2 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jtxtLandline = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jtxtFax = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jtxtMobile2 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jtxtEmail = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jtxtEmail2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jtxtOPB = new javax.swing.JTextField();
        jcmbEffect = new javax.swing.JComboBox();
        jpanelNavigation = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Alias");

        jLabel2.setText("Name");

        jtxtACName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtACNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtACNameFocusLost(evt);
            }
        });
        jtxtACName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtACNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtACNameKeyTyped(evt);
            }
        });

        jLabel3.setText("Group Name");

        jtxtGroupName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGroupNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGroupNameFocusLost(evt);
            }
        });
        jtxtGroupName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyReleased(evt);
            }
        });

        jlblDr.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setText("Speciality");

        jtxtSpeciality.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSpecialityFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSpecialityFocusLost(evt);
            }
        });
        jtxtSpeciality.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSpecialityKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtSpecialityKeyReleased(evt);
            }
        });

        jLabel16.setText("Star Doctor");

        jcmbStar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Yes" }));
        jcmbStar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbStarKeyPressed(evt);
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
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblDr, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtACName, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcmbStar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlblDr, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtACName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbStar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtSpeciality)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jtxtGroupName)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtAcAlias});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jlblDr, jtxtACName});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel16, jLabel3, jLabel7, jcmbStar, jtxtGroupName, jtxtSpeciality});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Address");

        jtxtAddress1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtAddress1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAddress1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAddress1FocusLost(evt);
            }
        });
        jtxtAddress1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddress1KeyPressed(evt);
            }
        });

        jLabel4.setText("City");

        jtxtAddress3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtAddress3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAddress3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAddress3FocusLost(evt);
            }
        });
        jtxtAddress3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddress3KeyPressed(evt);
            }
        });

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

        jtxtAddress2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtAddress2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAddress2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAddress2FocusLost(evt);
            }
        });
        jtxtAddress2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddress2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxtAddress3, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jtxtAddress2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                        .addComponent(jtxtAddress1, javax.swing.GroupLayout.Alignment.LEADING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtAddress1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAddress2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAddress3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtPincode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, jtxtAddress1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jtxtCity});

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

        jLabel13.setText("Fax #");

        jtxtFax.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtFax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFaxFocusGained(evt);
            }
        });
        jtxtFax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFaxKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtFaxKeyTyped(evt);
            }
        });

        jLabel14.setText("Mobile (1)");

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

        jLabel26.setText("Email (2)");

        jtxtEmail2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtEmail2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtEmail2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtEmail2FocusLost(evt);
            }
        });
        jtxtEmail2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtEmail2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtLandline, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFax, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxtMobile)
                    .addComponent(jtxtMobile2)
                    .addComponent(jtxtEmail2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(61, 61, 61))
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
                    .addComponent(jLabel13)
                    .addComponent(jtxtFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jtxtMobile2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jtxtEmail2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel14, jtxtLandline, jtxtMobile});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel13, jLabel27, jtxtFax, jtxtMobile2});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel15, jLabel26, jtxtEmail, jtxtEmail2});

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel31.setText("Openin Cash");

        jLabel32.setText("Effect");

        jtxtOPB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtOPBFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtOPBFocusLost(evt);
            }
        });
        jtxtOPB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtOPBKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtOPBKeyTyped(evt);
            }
        });

        jcmbEffect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DR", "CR" }));
        jcmbEffect.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbEffectKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtOPB, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcmbEffect, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbEffect, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel31, jLabel32, jcmbEffect, jtxtOPB});

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jpanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtAddress1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress1FocusGained
        // TODO add your handling code here:
        jtxtAddress1.selectAll();
    }//GEN-LAST:event_jtxtAddress1FocusGained

    private void jtxtAddress1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress1FocusLost
        // TODO add your handling code here:
        ((JTextField) evt.getComponent()).setText(((JTextField) evt.getComponent()).getText().toUpperCase());
    }//GEN-LAST:event_jtxtAddress1FocusLost

    private void jtxtAddress1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtAddress2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtAddress1KeyPressed

    private void jtxtAddress3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress3FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAddress3FocusGained

    private void jtxtAddress3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress3FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAddress3FocusLost

    private void jtxtAddress3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress3KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtCity);
    }//GEN-LAST:event_jtxtAddress3KeyPressed

    private void jtxtLandlineFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLandlineFocusGained
        // TODO add your handling code here:
        jtxtLandline.selectAll();
    }//GEN-LAST:event_jtxtLandlineFocusGained

    private void jtxtLandlineKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLandlineKeyPressed
        lb.enterFocus(evt, jtxtFax);
    }//GEN-LAST:event_jtxtLandlineKeyPressed

    private void jtxtLandlineKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLandlineKeyTyped
        lb.fixLength(evt, 20);
    }//GEN-LAST:event_jtxtLandlineKeyTyped

    private void jtxtFaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFaxFocusGained
        // TODO add your handling code here:
        jtxtFax.selectAll();
    }//GEN-LAST:event_jtxtFaxFocusGained

    private void jtxtFaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFaxKeyPressed
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jtxtFaxKeyPressed

    private void jtxtFaxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFaxKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtFaxKeyTyped

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
        lb.enterFocus(evt, jtxtEmail2);
    }//GEN-LAST:event_jtxtEmailKeyPressed

    private void jtxtEmail2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmail2FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtEmail2FocusGained

    private void jtxtEmail2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmail2FocusLost
        // TODO add your handling code here:
        jtxtEmail2.setText(jtxtEmail2.getText().toUpperCase());
    }//GEN-LAST:event_jtxtEmail2FocusLost

    private void jtxtEmail2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtEmail2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtOPB);
    }//GEN-LAST:event_jtxtEmail2KeyPressed

    private void jtxtOPBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOPBFocusGained
        jtxtOPB.selectAll();
    }//GEN-LAST:event_jtxtOPBFocusGained

    private void jtxtOPBFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOPBFocusLost
        jtxtOPB.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtOPB)));
    }//GEN-LAST:event_jtxtOPBFocusLost

    private void jtxtOPBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPBKeyPressed
        lb.enterFocus(evt, jcmbEffect);
    }//GEN-LAST:event_jtxtOPBKeyPressed

    private void jtxtOPBKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPBKeyTyped
        lb.onlyNumber(evt, jtxtOPB.getText().length() + 1);
    }//GEN-LAST:event_jtxtOPBKeyTyped

    private void jcmbEffectKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbEffectKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jcmbEffectKeyPressed

    private void jtxtACNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtACNameFocusGained

    private void jtxtACNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtACNameFocusLost

    private void jtxtACNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtGroupName);
    }//GEN-LAST:event_jtxtACNameKeyPressed

    private void jtxtACNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACNameKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtACNameKeyTyped

    private void jtxtGroupNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGroupNameFocusGained

    private void jtxtGroupNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtGroupNameFocusLost

    private void jtxtAddress2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress2FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAddress2FocusGained

    private void jtxtAddress2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress2FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAddress2FocusLost

    private void jtxtAddress2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtAddress3);
    }//GEN-LAST:event_jtxtAddress2KeyPressed

    private void jtxtGroupNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyPressed
        // TODO add your handling code here:
        groupPicklist.setLocation(jtxtGroupName.getX() + jPanel1.getX(), jtxtGroupName.getY() + jtxtGroupName.getHeight() + jPanel1.getY());
        groupPicklist.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtGroupNameKeyPressed

    private void jtxtGroupNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("select GROUP_NAME from groupmst where (GROUP_NAME) like '" + jtxtGroupName.getText().toUpperCase() + "%'");
            groupPicklist.setPreparedStatement(psLocal);
            groupPicklist.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at jtxtGrpNAmeKeyReleased in account master man", ex);
        }
    }//GEN-LAST:event_jtxtGroupNameKeyReleased

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
                    CityMaster cm = new CityMaster(jtxtCity.getText(), this, 13);
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
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
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

    private void jtxtPincodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPincodeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPincodeFocusGained

    private void jtxtPincodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPincodeFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtPincodeFocusLost

    private void jtxtPincodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPincodeKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 6);
    }//GEN-LAST:event_jtxtPincodeKeyTyped

    private void jtxtPincodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPincodeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtLandline);
    }//GEN-LAST:event_jtxtPincodeKeyPressed

    private void jtxtSpecialityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSpecialityFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSpecialityFocusGained

    private void jtxtSpecialityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSpecialityFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtSpecialityFocusLost

    private void jtxtSpecialityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSpecialityKeyPressed
        // TODO add your handling code here:
        splList.setLocation(jtxtSpeciality.getX() + jPanel1.getX(), jtxtSpeciality.getY() + jtxtSpeciality.getHeight() + jPanel1.getY());
        splList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtSpecialityKeyPressed

    private void jtxtSpecialityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSpecialityKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("select spec_sub_name from specsubmst "
                    + " where spec_sub_name like  '" + jtxtSpeciality.getText().toUpperCase() + "%'");
            splList.setPreparedStatement(psLocal);
            splList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtSpecialityKeyReleased

    private void jcmbStarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbStarKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtAddress1);
    }//GEN-LAST:event_jcmbStarKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
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
    private javax.swing.JComboBox jcmbEffect;
    private javax.swing.JComboBox jcmbStar;
    private javax.swing.JLabel jlblDr;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JPanel jpanelNavigation;
    private javax.swing.JTextField jtxtACName;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAddress1;
    private javax.swing.JTextField jtxtAddress2;
    private javax.swing.JTextField jtxtAddress3;
    private javax.swing.JTextField jtxtArea;
    public javax.swing.JTextField jtxtCity;
    private javax.swing.JTextField jtxtEmail;
    private javax.swing.JTextField jtxtEmail2;
    private javax.swing.JTextField jtxtFax;
    private javax.swing.JTextField jtxtGroupName;
    private javax.swing.JTextField jtxtLandline;
    private javax.swing.JTextField jtxtMobile;
    private javax.swing.JTextField jtxtMobile2;
    private javax.swing.JTextField jtxtOPB;
    private javax.swing.JTextField jtxtPincode;
    private javax.swing.JTextField jtxtSpeciality;
    // End of variables declaration//GEN-END:variables
}
