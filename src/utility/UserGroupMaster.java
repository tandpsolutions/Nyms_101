/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.SwingWorker;
import support.HeaderIntFrame;
import support.Library;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author Bhaumik
 */
public class UserGroupMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form CountryMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int user_grp_cd = -1;
    ReportTable table = null;
    int form_cd = -1;

    public UserGroupMaster(int user_id) {
        initComponents();
        form_cd = user_id;
        connectToNavigation();
        navLoad.setVoucher("Last");
//        lb.setUserRightsToPanel(navLoad, form_cd+"");
    }

    private void setComponentText(String text) {
        jtxtCountryName.setText(text);
    }

    public void setID(String code) {
        user_grp_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "User Group Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "User Group Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addUserRightForm(String user_id) throws SQLException {
        String sql = "select * from formmst";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        PreparedStatement pstUpdate = dataConnection.prepareStatement("insert into userrights (user_id,form_id) values (?,?)");
        pstUpdate.setString(1, user_id);
        while (rsLocal.next()) {
            pstUpdate.setString(2, rsLocal.getString("form_id"));
            pstUpdate.executeUpdate();
        }
    }

    private void connectToNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtCountryName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtCountryName.requestFocusInWindow();
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
                                sql = " insert into usergrp  (user_grp_name ,user_id)values (?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update usergrp  set user_grp_name =?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " where user_grp_cd=" + user_grp_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtCountryName.getText());
                            pstLocal.setInt(2, HMSHome.user_id);
                            pstLocal.executeUpdate();
                            if (getMode().equalsIgnoreCase("N")) {
                                addUserRightForm(lb.getData("user_grp_cd", "usergrp", "user_grp_name", jtxtCountryName.getText(), 0));
                            }
                            setSaveFlag(true);
                            setMode("");
                            setVoucher("Edit");
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
                            if (lb.isExist("login", "user_grp_cd", user_grp_cd + "", dataConnection)) {
                                navLoad.setMessage("User Group exist in state master. you can not delete this country");
                            } else {
                                lb.confirmDialog("Do you want to delete this country?");
                                if (lb.type) {
                                    String sql = "delete from usergrp  where user_grp_cd=" + user_grp_cd;
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
                String sql = "select user_grp_cd,user_grp_name  from usergrp ";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, user_grp_cd + "", "User Group Master View", sql, "27", 1, UserGroupMaster.this, "User Group Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "User Group Master View", -1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from usergrp  where user_grp_cd=(select min(user_grp_cd) from usergrp )");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from usergrp  where user_grp_cd=(select max(user_grp_cd) from usergrp  where user_grp_cd <" + user_grp_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from usergrp  where user_grp_cd=(select min(user_grp_cd) from usergrp  where user_grp_cd >" + user_grp_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from usergrp  where user_grp_cd=(select max(user_grp_cd) from usergrp )");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from usergrp  where user_grp_cd=" + user_grp_cd);
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
            public void setComponentTextFromResultSet() {
                try {
                    user_grp_cd = viewData.getInt("user_grp_cd");
                    jtxtCountryName.setText(viewData.getString("user_grp_name"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in country master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtCountryName.setEnabled(flag);
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtCountryName)) {
                    setMessage("User Group name can not be left blank");
                    jtxtCountryName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("usergrp ", "user_grp_name ", jtxtCountryName.getText(), dataConnection)) {
                        setMessage("User Group name already exist");
                        jtxtCountryName.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("usergrp ", "user_grp_name ", jtxtCountryName.getText(), "user_grp_cd", user_grp_cd + "", dataConnection)) {
                        setMessage("User Group name already exist");
                        jtxtCountryName.requestFocusInWindow();
                        return false;
                    }
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
        jtxtCountryName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("User Group");

        jtxtCountryName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCountryNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCountryNameFocusLost(evt);
            }
        });
        jtxtCountryName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCountryNameKeyPressed(evt);
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
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jtxtCountryName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtCountryName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtCountryName});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtCountryNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCountryNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCountryNameFocusGained

    private void jtxtCountryNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCountryNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtCountryNameFocusLost

    private void jtxtCountryNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCountryNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtCountryNameKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtCountryName;
    // End of variables declaration//GEN-END:variables
}
