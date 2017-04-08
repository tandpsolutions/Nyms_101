/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMSHome;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import support.HeaderIntFrame;
import support.Library;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author nice
 */
public class ManageUser extends javax.swing.JInternalFrame {

    /**
     * Creates new form ManageUser
     */
    private SmallNavigation navLoad = null;
    private Library lb = null;
    private int user_id = 0;
    private Connection dataConnection = hms.HMS101.connMpAdmin;
    ReportTable table = null;

    public ManageUser() {
        initComponents();
        fillJcomboBox();
        addNavigation();
        navLoad.setComponentEnabledDisabled(false);
        lb = new Library();
        addValidation();
        navLoad.setVoucher("Last");
        navLoad.setprintFlag(false);
    }

    private void setCompText(String text) {
        jtxtName.setText(text);
        jtxtPass.setText(text);
        jComboBox1.setSelectedIndex(0);
        jtxtName.requestFocusInWindow();
    }

    private void fillJcomboBox() {
        try {
            jComboBox1.removeAllItems();
            jComboBox1.addItem("");
            String sql = "select user_grp_name from usergrp";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            while (rsLcoal.next()) {
                jComboBox1.addItem(rsLcoal.getString("user_grp_name"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at fillJcombobox at state master", ex);
        }
//        jComboBox1.setEditable(true);
//        JTextComponent editor = (JTextComponent) jComboBox1.getEditor().getEditorComponent();
//        editor.setDocument(new S09ShowPopup(jComboBox1, jComboBox1));
    }

    private void addValidation() {
        FieldValidation valid = new FieldValidation();
        jtxtName.setInputVerifier(valid);
        jtxtPass.setInputVerifier(valid);
    }

    public void setID(String strCode) {
        user_id = Integer.parseInt(strCode);
        navLoad.setVoucher("Edit");
    }

    class FieldValidation extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            boolean val = false;
            if (input.equals(jtxtName)) {
                val = fielddValid(input);
            } else if (input.equals(jtxtPass)) {
                val = fielddValid(input);
            }
            return val;
        }
    }

    private boolean fielddValid(Component comp) {
        navLoad.setMessage("");
        if (comp == jtxtName) {
            if (lb.isBlank(comp)) {
                navLoad.setMessage("User name should not Blank");
                comp.requestFocusInWindow();
                return false;
            }
            if (navLoad.getMode().equalsIgnoreCase("N")) {
                if (lb.isExist("login", "user_name", jtxtName.getText(), dataConnection)) {
                    navLoad.setMessage("User already exist !");
                    comp.requestFocusInWindow();
                    return false;
                }
            } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isExistForEdit("login", "user_name", jtxtName.getText(), "user_id", String.valueOf(user_id), dataConnection)) {
                    navLoad.setMessage("User already exist !");
                    comp.requestFocusInWindow();
                    return false;
                }
            }

        }
        if (comp == jtxtPass) {
            if (lb.isBlank(comp)) {
                navLoad.setMessage("Password should not blank");
                comp.requestFocusInWindow();
                return false;
            }
        }
        if (comp == jComboBox1) {
            if (jComboBox1.getSelectedIndex() == 0) {
                navLoad.setMessage("User Group should not blank");
                jComboBox1.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private boolean validateForm() {
        boolean flag = true;
        flag = flag && fielddValid(jtxtName);
        flag = flag && fielddValid(jtxtPass);
        flag = flag && fielddValid(jComboBox1);
        return flag;
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "User Id", -1, java.lang.Integer.class, null, false);
        table.AddColumn(1, "User Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "User Group Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }
    
    private void addNavigation() {
        class Navigation extends SmallNavigation {

            @Override
            public void callNew() {
                setSaveFlag(false);
                setCompText("");
                setComponentEnabledDisabled(true);
                setMode("N");
            }

            @Override
            public void callEdit() {
                setSaveFlag(false);
                setComponentEnabledDisabled(true);
                setMode("E");
            }

            @Override
            public void callSave() {
                try {
                    setSaveFlag(false);
                    boolean valid = validateForm();
                    if (valid) {
                        dataConnection.setAutoCommit(false);
                        save();
                        dataConnection.commit();
                        dataConnection.setAutoCommit(true);
                        navLoad.setSaveFlag(true);
                        if (navLoad.getMode().equalsIgnoreCase("N")) {
                            setVoucher("Last");
                        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                            setVoucher("Edit");
                        }
                        navLoad.setMode("");
                    }
                } catch (SQLException ex) {
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                        lb.printToLogFile("Error at save User", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Error at rollback User", ex1);
                    }
                }
            }

            @Override
            public void callDelete() {
//                try {
//                    if (JOptionPane.showConfirmDialog(this, "Do you want to Delete User " + jtxtName.getText() + " ?", HMSHome.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                        dataConnection.setAutoCommit(false);
//                        delete();
//                        dataConnection.commit();
//                        dataConnection.setAutoCommit(true);
//                        setVoucher("Last");
//                    }
//                } catch (SQLException ex) {
//                    try {
//                        dataConnection.rollback();
//                        dataConnection.setAutoCommit(true);
//                        lb.printToLogFile("Error at delete User", ex);
//                    } catch (SQLException ex1) {
//                        lb.printToLogFile("Error at rollback User", ex1);
//                    }
//                }
//                setSaveFlag(true);
            }

            @Override
            public void callView() {
                String sql = "SELECT l.user_id,user_name,u.user_grp_name FROM login l LEFT JOIN usergrp u ON l.user_grp=u.user_grp_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, user_id + "", "User Master View", sql, "30", 1, ManageUser.this, "User Master", table);
                header.makeView();
                closeORcancel();
                HMSHome.addOnScreen(header, "Area Master View",-1);
            }

            @Override
            public void callClose() {
                closeORcancel();
            }

            @Override
            public void callPrint() {

            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    user_id = navLoad.viewData.getInt("user_id");
                    jtxtName.setText(navLoad.viewData.getString("user_name"));
                    jtxtPass.setText(navLoad.viewData.getString("PASSWORD"));
                    jComboBox1.setSelectedItem(lb.getData("user_grp_name", "usergrp", "user_grp_cd", navLoad.viewData.getString("user_grp"), 1));
                } catch (SQLException ex) {
                    lb.printToLogFile("Error at navigate Data", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtName.setEnabled(flag);
                jtxtPass.setEnabled(flag);
                jComboBox1.setEnabled(flag);
                jtxtName.requestFocusInWindow();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from login  where user_id=(select min(user_id) from login )");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from login  where user_id=(select max(user_id) from login  where user_id <" + user_id + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from login  where user_id=(select min(user_id) from login  where user_id >" + user_id + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from login  where user_id=(select max(user_id) from login )");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from login  where user_id=" + user_id);
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
            public boolean validateVoucher() {
                return true;
            }
        }
        navLoad = new Navigation();
        navLoad.setVisible(true);
        jPanel1.add(navLoad);
        jPanel1.setVisible(true);

    }

    private void closeORcancel() {
        if (navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            navLoad.setComponentEnabledDisabled(false);
            navLoad.setVoucher("Edit");
            navLoad.setSaveFlag(true);
            navLoad.setMode("");

        }
    }

//    private void delete() throws SQLException {
//        PreparedStatement psLocal = null;
//
////        psLocal = dataConnection.prepareStatement("DELETE FROM login WHERE user_id=?");
////        psLocal.setInt(1, user_id);
////        psLocal.executeUpdate();
//
//    }
    private void save() throws SQLException {
        PreparedStatement psLocal = null;

        if (navLoad.getMode().equalsIgnoreCase("N")) {
            psLocal = dataConnection.prepareStatement("INSERT INTO login (user_name, user_grp,PASSWORD) VALUES (?,?,MD5(?))");
            psLocal.setString(3, jtxtPass.getText());
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            psLocal = dataConnection.prepareStatement("UPDATE login SET user_name=?,user_grp=? WHERE user_id=?");
            psLocal.setInt(3, user_id);
        }
        psLocal.setString(1, jtxtName.getText());
        psLocal.setString(2, lb.getData("user_grp_cd", "usergrp", "user_grp_name", jComboBox1.getSelectedItem().toString(), 0));
        psLocal.executeUpdate();

    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at Manage Master", ex);
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

        jtxtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtxtPass = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        jtxtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtNameKeyPressed(evt);
            }
        });

        jLabel1.setText("User Name");

        jtxtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPassKeyPressed(evt);
            }
        });

        jLabel2.setText("Password");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Group");

        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtPass, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(42, 42, 42)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel3});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPassKeyPressed
        lb.enterFocus(evt, jComboBox1);
    }//GEN-LAST:event_jtxtPassKeyPressed

    private void jtxtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyPressed
        lb.enterFocus(evt, jtxtPass);
    }//GEN-LAST:event_jtxtNameKeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jtxtName;
    private javax.swing.JPasswordField jtxtPass;
    // End of variables declaration//GEN-END:variables
}
