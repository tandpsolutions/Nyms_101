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
public class SubSpecialtyMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form StateMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int spec_sub_cd = -1;
    ReportTable table = null;
    int form_id = -1;
    PickList splList = null;

    public SubSpecialtyMaster(int form_id) {
        initComponents();
        splList = new PickList(dataConnection);
        setPicklistView();
        connectToNavigation();
        navLoad.setVoucher("Last");
        this.form_id = form_id;
        lb.setUserRightsToPanel(navLoad, form_id+"");
    }

    private void setComponentText(String text) {
        jtxtSubSpecialityName.setText(text);
        jtxtSpeciality.setText(text);
    }

    private void setPicklistView() {

        splList.setLayer(this.getLayeredPane());
        splList.setPickListComponent(jtxtSpeciality);
        splList.setNextComponent(navLoad);
        splList.setReturnComponent(new JTextField[]{jtxtSpeciality});
    }

    public void setID(String code) {
        spec_sub_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Sub Speciality Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Sub Speciality Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Speciality Name", -1, java.lang.String.class, null, false);
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
                jtxtSubSpecialityName.requestFocusInWindow();
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
                                sql = " insert into specsubmst (spec_cd,user_id,spec_sub_name)values (?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update specsubmst set spec_cd=?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " ,spec_sub_name=? where spec_sub_cd=" + spec_sub_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, lb.getSpecialityCD(jtxtSpeciality.getText().toString(), "C"));
                            pstLocal.setInt(2, HMSHome.user_id);
                            pstLocal.setString(3, jtxtSubSpecialityName.getText());
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
                            if (lb.isExist("acntmst", "spec_sub_cd", spec_sub_cd + "", dataConnection)) {
                                navLoad.setMessage("Sub Speciality exist in speciality master. you can not delete this sub speciality.");
                            } else {
                                lb.confirmDialog("Do you want to delete this sub speciality?");
                                if (lb.type) {
                                    String sql = "delete from specsubmst where spec_sub_cd=" + spec_sub_cd;
                                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                    pstLocal.executeUpdate();
                                    setVoucher("Previous");
                                }
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
                String sql = " SELECT s.spec_sub_cd,spec_sub_name,speciality_name FROM specsubmst s LEFT JOIN specialitymst c ON c.speciality_cd=s.spec_cd ";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, spec_sub_cd + "", "Sub Speciality Master View", sql, form_id + "", 1, SubSpecialtyMaster.this, "Doctor Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Doctor Master View",-1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id+"");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from specsubmst where spec_sub_cd=(select min(spec_sub_cd) from specsubmst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from specsubmst where spec_sub_cd=(select max(spec_sub_cd) from specsubmst where spec_sub_cd <" + spec_sub_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from specsubmst where spec_sub_cd=(select min(spec_sub_cd) from specsubmst where spec_sub_cd >" + spec_sub_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from specsubmst where spec_sub_cd=(select max(spec_sub_cd) from specsubmst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from specsubmst where spec_sub_cd=" + spec_sub_cd);
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
                    spec_sub_cd = viewData.getInt("spec_sub_cd");
                    jtxtSubSpecialityName.setText(viewData.getString("spec_sub_name"));
                    jtxtSpeciality.setText(lb.getSpecialityCD(viewData.getString("spec_cd"), "N"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in country master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtSubSpecialityName.setEnabled(flag);
                jtxtSpeciality.setEnabled(flag);
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtSubSpecialityName)) {
                    setMessage("Sub speciality name can not be left blank");
                    jtxtSubSpecialityName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("specsubmst", "spec_sub_name", jtxtSubSpecialityName.getText(), dataConnection)) {
                        setMessage("Sub speciality name already exist");
                        jtxtSubSpecialityName.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("specsubmst", "spec_sub_name", jtxtSubSpecialityName.getText(), "spec_sub_cd", spec_sub_cd + "", dataConnection)) {
                        setMessage("Sub speciality name already exist");
                        jtxtSubSpecialityName.requestFocusInWindow();
                        return false;
                    }
                }

                if (lb.isBlank(jtxtSpeciality)) {
                    jtxtSpeciality.requestFocusInWindow();
                    navLoad.setMessage("Speciality name can not be left blank");
                    return false;
                }

                if (!lb.isExist("specialitymst", "speciality_name", jtxtSpeciality.getText().toString(), dataConnection)) {
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
        jtxtSubSpecialityName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtSpeciality = new javax.swing.JTextField();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Sub Speciality");

        jtxtSubSpecialityName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSubSpecialityNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSubSpecialityNameFocusLost(evt);
            }
        });
        jtxtSubSpecialityName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSubSpecialityNameKeyPressed(evt);
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
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jtxtSubSpecialityName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtSubSpecialityName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 48, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtSubSpecialityName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtSpeciality});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtSubSpecialityNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubSpecialityNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSubSpecialityNameFocusGained

    private void jtxtSubSpecialityNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubSpecialityNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtSubSpecialityNameFocusLost

    private void jtxtSubSpecialityNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubSpecialityNameKeyPressed
        lb.enterFocus(evt, jtxtSpeciality);
    }//GEN-LAST:event_jtxtSubSpecialityNameKeyPressed

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
            PreparedStatement psLocal = dataConnection.prepareStatement("select speciality_name from specialitymst "
                    + " where speciality_name like  '" + jtxtSpeciality.getText().toUpperCase() + "%'");
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
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtSpeciality;
    private javax.swing.JTextField jtxtSubSpecialityName;
    // End of variables declaration//GEN-END:variables
}
