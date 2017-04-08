/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import support.Library;
import support.NavigationPanel;
import support.SmallNavigation;

/**
 *
 * @author nice
 */
public class UserPermission extends javax.swing.JInternalFrame {

    /**
     * Creates new form UserPermission
     */
    private Connection loginConn = HMS101.connMpAdmin;
    private Library blib = new Library();
    int noOfUser = 0;
    DefaultMutableTreeNode root;
    TreePath changePath;
    int form_cd = -1;

    public UserPermission(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        registerShortKeys();
        setUserValues();
        jPanel1.setVisible(false);
        setPermission();
    }

    private void setPermission(){
        boolean flag = blib.getRight(form_cd+"", "EDIT");
        jbtnApply.setEnabled(flag);
        jbtnOk.setEnabled(flag);
    }

    private void setUserValues() {
        try {
            PreparedStatement pst;
            ResultSet result;
            pst = loginConn.prepareStatement("select user_grp_name FROM usergrp ", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            result = pst.executeQuery();
            root = new DefaultMutableTreeNode(HMSHome.TITLE, true);
            jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            jTree1.setModel(new DefaultTreeModel(root));
            
            
            while (result.next()) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(result.getString(1), true);
                root.add(child);
                addFormToUser(child);
            }
            blib.closeResultSet(result);
            blib.closeStatement(pst);
        } catch (Exception ex) {
            blib.printToLogFile("Error in add user", ex);
        }


    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            blib.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    private void registerShortKeys() {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jbtnClose.doClick();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jbtnClose = new javax.swing.JToggleButton();
        jbtnApply = new javax.swing.JButton();
        jbtnOk = new javax.swing.JButton();

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("System");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        jCheckBox1.setText("View");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        jCheckBox2.setText("Print");
        jCheckBox2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox2StateChanged(evt);
            }
        });

        jCheckBox3.setText("Edit");
        jCheckBox3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox3StateChanged(evt);
            }
        });

        jCheckBox4.setText("Add");
        jCheckBox4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox4StateChanged(evt);
            }
        });

        jCheckBox5.setText("Delete");
        jCheckBox5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox5StateChanged(evt);
            }
        });

        jCheckBox6.setText("Navigation View");
        jCheckBox6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox6StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox1))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbtnClose.setMnemonic('C');
        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });
        jbtnClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jbtnCloseKeyReleased(evt);
            }
        });

        jbtnApply.setMnemonic('A');
        jbtnApply.setText("Apply");
        jbtnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnApplyActionPerformed(evt);
            }
        });
        jbtnApply.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jbtnApplyKeyReleased(evt);
            }
        });

        jbtnOk.setMnemonic('O');
        jbtnOk.setText("Ok");
        jbtnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOkActionPerformed(evt);
            }
        });
        jbtnOk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jbtnOkKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jbtnOk, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnApply, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtnClose)
                            .addComponent(jbtnApply)
                            .addComponent(jbtnOk))
                        .addGap(40, 40, 40))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private int getUserPathCount(String path) {
        boolean flag = false;
        ArrayList userPath = new ArrayList();
        StringTokenizer st = new StringTokenizer(path.substring(1, path.length() - 1), ",");
        int i = 0;
        while (st.hasMoreElements()) {
            i++;
            st.nextElement();
        }

        return i;
    }

    private ArrayList getUserArrayPath(TreePath path) {
        String temp;
        ArrayList userPath = new ArrayList();
        StringTokenizer st = new StringTokenizer(path.toString().substring(1, path.toString().length() - 1), ",");

        while (st.hasMoreElements()) {
            temp = (String) st.nextElement();
            userPath.add(temp.trim());

        }
        return userPath;
    }

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        changePath = evt.getPath();
        DefaultMutableTreeNode selectedNode = new DefaultMutableTreeNode(changePath.getLastPathComponent());

        if (getUserPathCount(evt.getPath().toString()) != 0) {
            if (selectedNode.isLeaf()) {
                if (getUserPathCount(evt.getPath().toString()) == 4) {

                    jPanel1.setVisible(true);
                    setPrivileges(evt.getPath());

                } else {
                    jPanel1.setVisible(false);
                }
            } else {
                jPanel1.setVisible(false);
            }
        } else {
            jPanel1.setVisible(false);
        }

    }//GEN-LAST:event_jTree1ValueChanged

    private void setPrivilegesValues(boolean b[]) {
        jCheckBox1.setSelected(b[0]);
        jCheckBox2.setSelected(b[1]);
        jCheckBox3.setSelected(b[2]);
        jCheckBox4.setSelected(b[3]);
        jCheckBox5.setSelected(b[4]);
        jCheckBox6.setSelected(b[5]);
    }

    private int[] getPrivilegesValues() {
        int[] b = new int[6];
        b[0] = (jCheckBox1.isSelected()) ? 1 : 0;
        b[1] = (jCheckBox2.isSelected()) ? 1 : 0;
        b[2] = (jCheckBox3.isSelected()) ? 1 : 0;
        b[3] = (jCheckBox4.isSelected()) ? 1 : 0;
        b[4] = (jCheckBox5.isSelected()) ? 1 : 0;
        b[5] = (jCheckBox6.isSelected()) ? 1 : 0;
        return b;
    }

    private void setPrivileges(TreePath path) {

        ArrayList userRights = getUserArrayPath(path);
        
//        System.out.println((String) userRights.get(1));
//        System.out.println((String) userRights.get(2));
//        System.out.println((String) userRights.get(3));

        boolean[] flag = new boolean[6];
        String query = "select USERRIGHTS.VIEWS, USERRIGHTS.PRINT, USERRIGHTS.EDIT,USERRIGHTS.ADDS, USERRIGHTS.DELETES, USERRIGHTS.NAVIGATE_VIEW "
                + "from FORMMST, USERRIGHTS "
                + " where USER_ID=" + blib.getUserGroup(userRights.get(1).toString(), "C") + " and FORMMST.FORM_NAME='" + (String) userRights.get(3) + "' and (FORMMST.FORM_ID=USERRIGHTS.FORM_ID)";
        try {
            PreparedStatement pst = loginConn.prepareStatement(query);
            ResultSet result = pst.executeQuery();

            if (result.next()) {
                flag[0] = (result.getInt(1) == 1) ? true : false;
                flag[1] = (result.getInt(2) == 1) ? true : false;
                flag[2] = (result.getInt(3) == 1) ? true : false;
                flag[3] = (result.getInt(4) == 1) ? true : false;
                flag[4] = (result.getInt(5) == 1) ? true : false;
                flag[5] = (result.getInt(6) == 1) ? true : false;
                setPrivilegesValues(flag);
            } else {
                setPrivilegesValues(flag);
            }
            if (result != null) {
                result.close();
            }
            if (pst != null) {
                pst.close();
            }

        } catch (Exception ex) {
            blib.printToLogFile("Error when select rights", ex);
        }

    }

    private void applyPrivileges() {
        ArrayList userRights = getUserArrayPath(changePath);
        int[] flag = new int[6];
        flag = getPrivilegesValues();
        String query = "update USERRIGHTS set VIEWS=" + flag[0] + ", EDIT=" + flag[2] + ", ADDS=" + flag[3] + ", DELETES=" + flag[4] + ", PRINT=" + flag[1] + ", NAVIGATE_VIEW=" + flag[5] + ""
                + " where  USER_ID = " + blib.getUserGroup(userRights.get(1).toString(), "C") + " and"
                + " FORM_ID = (select FORM_ID from FORMMST WHERE FORM_NAME='" + userRights.get(3).toString() + "')";
        try {
            PreparedStatement pst = loginConn.prepareStatement(query);
            pst.executeUpdate();

            if (pst != null) {
                pst.close();
            }

        } catch (Exception ex) {
            blib.printToLogFile("Error when select rights", ex);
        }
    }

    public boolean getRigthsForm(String user_id, String form_id)
    {
        boolean[] flag=new boolean[6];
        try
        {
            String query = "select VIEWS, EDIT, ADDS, DELETES, PRINT, NAVIGATE_VIEW from APP.USERRIGHTS where USER_ID=" + user_id + " and FORM_ID=" + form_id;
            PreparedStatement pst = loginConn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                for(int i=0;i<6;i++){
                    flag[i]=(rs.getInt(i+1)==1)?true:false;
                }
            }
            blib.closeResultSet(rs);
            blib.closeStatement(pst);
            return flag[0];
        }
        catch(Exception ex)
        {
            blib.printToLogFile("Error to get user rights", ex);
        }
        return flag[0];
    }
    
//    public boolean blib.getRight(String form_id, String right) {
//        boolean flag = false;
//        try {
//            String query = "SELECT " + right + " FROM USERRIGHTS WHERE USER_ID=" + HMSHome.user_id + " AND FORM_ID=" + form_id;
//            PreparedStatement pstLocal = loginConn.prepareStatement(query);
//            ResultSet rsLocal = pstLocal.executeQuery();
//            if (rsLocal.next()) {
//                if (rsLocal.getInt(1) == 1) {
//                    flag = true;
//                }
//            }
//            blib.closeResultSet(rsLocal);
//            blib.closeStatement(pstLocal);
//        } catch (Exception ex) {
//            blib.printToLogFile("Exception at blib.getRight", ex);
//        }
//        return flag;
//    }
//    
    public void setUserRightsToPanel(SmallNavigation navLoad, String FormID) {
        navLoad.setNewEnable(blib.getRight(FormID, "ADDS"));
        navLoad.setEditEnable(blib.getRight(FormID, "EDIT"));
        navLoad.setDeleteEnable(blib.getRight(FormID, "DELETES"));
        
        boolean navigate = blib.getRight(FormID, "NAVIGATE_VIEW");
        navLoad.setFirstEnable(navigate);
        navLoad.setPreviousEnable(navigate);
        navLoad.setNextEnable(navigate);
        navLoad.setLastEnable(navigate);
        navLoad.setViewEnable(navigate);
    }
    
    public void setUserRightsToPanel(NavigationPanel navLoad, String FormID) {
        navLoad.setNewEnable(blib.getRight(FormID, "ADDS"));
        navLoad.setEditEnable(blib.getRight(FormID, "EDIT"));
        navLoad.setDeleteEnable(blib.getRight(FormID, "DELETES"));
        
        boolean navigate = blib.getRight(FormID, "NAVIGATE_VIEW");
        navLoad.setFirstEnable(navigate);
        navLoad.setPreviousEnable(navigate);
        navLoad.setNextEnable(navigate);
        navLoad.setLastEnable(navigate);
        navLoad.setViewEnable(navigate);
        
        navLoad.setPrintEnable(blib.getRight(FormID, "PRINT"));
    }
    
    private void jbtnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnApplyActionPerformed
        // TODO add your handling code here:
        applyPrivileges();
    }//GEN-LAST:event_jbtnApplyActionPerformed

    private void jbtnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOkActionPerformed
        // TODO add your handling code here:
        applyPrivileges();
        this.dispose();
    }//GEN-LAST:event_jbtnOkActionPerformed

    private void jbtnOkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnOkKeyReleased
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            evt.consume();
            jbtnOk.doClick();
        }
    }//GEN-LAST:event_jbtnOkKeyReleased

    private void jbtnApplyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnApplyKeyReleased
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            evt.consume();
            jbtnApply.doClick();
        }
    }//GEN-LAST:event_jbtnApplyKeyReleased

    private void jbtnCloseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyReleased
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            evt.consume();
            jbtnClose.doClick();
        }
    }//GEN-LAST:event_jbtnCloseKeyReleased

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
//        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jCheckBox2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox2StateChanged
//        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox2StateChanged

    private void jCheckBox3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox3StateChanged
//        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox3StateChanged

    private void jCheckBox4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox4StateChanged
//        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox4StateChanged

    private void jCheckBox5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox5StateChanged
//        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox5StateChanged

    private void jCheckBox6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox6StateChanged
//        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox6StateChanged

    private void addUserMenu(TreeNode node) {
        try {
            PreparedStatement pst;
            ResultSet result;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node;

                String query = "select FORM_NAME from FORMMST where FORMMST.MENU_ID in "
                    + "(select MENU_ID from MENUMST where MENU_NAME='" + node.toString() + "')";
//            if (HMSHome.forms != null || !HMSHome.forms.isEmpty()) {
//                query += " AND FORMMST.FORM_ID IN (" + HMSHome.forms + ")";
//            }
            pst = loginConn.prepareStatement(query);
            result = pst.executeQuery();
            while (result.next()) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(result.getString(1), true);
                parent.add(child);
            }
            blib.closeResultSet(result);
            blib.closeStatement(pst);
        } catch (Exception ex) {
            blib.printToLogFile("Error in add user", ex);
        }
    }

    private void addFormToUser(TreeNode node) {
        try {
            PreparedStatement pst;
            ResultSet result;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node;
            pst = loginConn.prepareStatement("select MENU_NAME from MENUMST", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            result = pst.executeQuery();

            while (result.next()) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(result.getString(1), true);
                parent.add(child);
                addUserMenu(child);
            }
            if (result != null) {
                result.close();
            }
            if (pst != null) {
                pst.close();
            }
        } catch (Exception ex) {
            blib.printToLogFile("Error in add user", ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JButton jbtnApply;
    private javax.swing.JToggleButton jbtnClose;
    private javax.swing.JButton jbtnOk;
    // End of variables declaration//GEN-END:variables
}