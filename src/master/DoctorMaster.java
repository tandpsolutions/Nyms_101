/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class DoctorMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form StateMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int ac_cd = -1;
    ReportTable table = null;
    int form_id = -1;
    PickList acPickList = null;
    PickList splList = null;

    public DoctorMaster(int form_id) {
        initComponents();
        acPickList = new PickList(dataConnection);
        splList = new PickList(dataConnection);
        setPicklistView();
        connectToNavigation();
        navLoad.setVoucher("Last");
        this.form_id = form_id;
    }

    private void setComponentText(String text) {
        jtxtDoctorName.setText(text);
        jtxtSpeciality.setText(text);
    }

    private void setPicklistView() {
        acPickList.setLayer(this.getLayeredPane());
        acPickList.setPickListComponent(jtxtDoctorName);
        acPickList.setNextComponent(jtxtSpeciality);
        acPickList.setReturnComponent(new JTextField[]{jtxtDoctorName, jlblACAlias});

        splList.setLayer(this.getLayeredPane());
        splList.setPickListComponent(jtxtSpeciality);
        splList.setNextComponent(navLoad);
        splList.setReturnComponent(new JTextField[]{jtxtSpeciality});
    }

    public void setID(String code) {
        ac_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Doctor Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Doctor Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Sub Speciality Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void connectToNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setMode("N");
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                jtxtDoctorName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setMode("E");
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                jtxtSpeciality.requestFocusInWindow();
            }

            @Override
            public void callSave() {

                SwingWorker workerForjbtnGenerate = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            lb.addGlassPane(navLoad);
                            String sql = "";
                            if (getMode().equalsIgnoreCase("N")) {
                                sql = " insert into doctormaster (sub_spec_cd,user_id,ac_cd)values (?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update doctormaster set sub_spec_cd=?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " where ac_cd=?";
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, lb.getSubSpecialistCd(jtxtSpeciality.getText().toString(), "C"));
                            pstLocal.setInt(2, HMSHome.user_id);
                            pstLocal.setString(3, lb.getAcCode(jlblACAlias.getText(), "AC"));
                            pstLocal.executeUpdate();
                            setSaveFlag(true);
                            if (getMode().equalsIgnoreCase("N")) {
                                setVoucher("Last");
                            } else if (getMode().equalsIgnoreCase("E")) {
                                setVoucher("Edit");
                            }
                            setMode("");
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at saveVoucher at save country master", ex);
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
                            lb.addGlassPane(navLoad);
//                            if (lb.isExist("citymst", "ac_cd", ac_cd + "", dataConnection)) {
//                                navLoad.setMessage("State exist in city master. you can not delete this state.");
//                            } else {
                            lb.confirmDialog("Do you want to delete this doctor?");
                            if (lb.type) {
                                String sql = "delete from doctormaster where ac_cd=" + ac_cd;
                                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                pstLocal.executeUpdate();
                                setVoucher("Previous");
//                                }
                            }
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at call delete at country master", ex);
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
                String sql = " SELECT a.ac_cd,ac_name,spec_sub_name FROM doctormaster s LEFT JOIN specsubmst c ON c.spec_sub_cd=s.spec_sub_cd  "
                        + " LEFT JOIN acntmst a ON a.ac_cd=s.ac_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ac_cd + "", "Doctor Master View", sql, "19", 1, DoctorMaster.this, "Doctor Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Doctor Master View",-1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from doctormaster where ac_cd=(select min(ac_cd) from doctormaster)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from doctormaster where ac_cd=(select max(ac_cd) from doctormaster where ac_cd <" + ac_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from doctormaster where ac_cd=(select min(ac_cd) from doctormaster where ac_cd >" + ac_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from doctormaster where ac_cd=(select max(ac_cd) from doctormaster)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from doctormaster where ac_cd=" + ac_cd);
                }
                try {
                    if (viewData.next()) {
                        setComponentTextFromResultSet();
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher doctor master'", ex);
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
            public void setComponentTextFromResultSet() {
                try {
                    ac_cd = viewData.getInt("ac_cd");
                    jtxtDoctorName.setText(lb.getAcCode(viewData.getString("ac_cd"), "N"));
                    jtxtSpeciality.setText(lb.getSubSpecialistCd(viewData.getString("sub_spec_cd"), "N"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in country master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtDoctorName.setEnabled(flag);
                jlblACAlias.setEnabled(flag);
                jtxtSpeciality.setEnabled(flag);
                if (!getMode().equalsIgnoreCase("N")) {
                    jtxtDoctorName.setEnabled(false);
                    jlblACAlias.setEnabled(false);
                }
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtDoctorName)) {
                    setMessage("Doctor name can not be left blank");
                    jtxtDoctorName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("doctormaster", "ac_cd", lb.getAcCode(jtxtDoctorName.getText(), "C"), dataConnection)) {
                        setMessage("doctorname name already exist");
                        jtxtDoctorName.requestFocusInWindow();
                        return false;
                    }
                }

                if (lb.isBlank(jtxtSpeciality)) {
                    jtxtSpeciality.requestFocusInWindow();
                    navLoad.setMessage("Speciality name can not be left blank");
                    return false;
                }

                if (!lb.isExist("specsubmst", "spec_sub_name", jtxtSpeciality.getText().toString(), dataConnection)) {
                    jtxtSpeciality.requestFocusInWindow();
                    navLoad.setMessage("Invalid speciality name");
                    return false;
                }
                return true;
            }
        }
        navLoad = new navPanel();
        jPanel1.add(navLoad);
        navLoad.setVisible(true);
        navLoad.setprintFlag(false);
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
            navLoad.setComponentEnabledDisabled(false);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtDoctorName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jlblACAlias = new javax.swing.JTextField();
        jtxtSpeciality = new javax.swing.JTextField();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Doctor Name");

        jtxtDoctorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDoctorNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDoctorNameFocusLost(evt);
            }
        });
        jtxtDoctorName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDoctorNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtDoctorNameKeyReleased(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("User:");

        jLabel9.setText("Edit No:");

        jLabel11.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("Speciality");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jtxtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 45, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jlblACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(262, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtDoctorName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtSpeciality});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtDoctorNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDoctorNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDoctorNameFocusGained

    private void jtxtDoctorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDoctorNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtDoctorNameFocusLost

    private void jtxtDoctorNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorNameKeyPressed
        acPickList.setLocation(jtxtDoctorName.getX(), jtxtDoctorName.getY() + jtxtDoctorName.getHeight());
        acPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtDoctorNameKeyPressed

    private void jtxtDoctorNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorNameKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("select ac_name,ac_alias from acntmst "
                    + " where ac_name like  '" + jtxtDoctorName.getText().toUpperCase() + "%'");
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1});
            acPickList.setSecondAssociation(new int[]{0, 1});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtDoctorNameKeyReleased

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
        splList.setLocation(jtxtSpeciality.getX(), jtxtSpeciality.getY() + jtxtSpeciality.getHeight());
        splList.pickListKeyPress(evt);
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jlblACAlias;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtDoctorName;
    private javax.swing.JTextField jtxtSpeciality;
    // End of variables declaration//GEN-END:variables
}
