/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GroupMaster.java`
 *
 * Created on Oct 3, 2012, 4:11:05 PM
 */
package master;

import hms.HMS101;
import hms.HMSHome;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import support.HeaderIntFrame;
import support.Library;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author nice
 */
public class GroupMaster extends javax.swing.JInternalFrame {

    private SmallNavigation navLoad;
    private Library lb = new Library();
    private Connection dataConnection = HMS101.connMpAdmin;
    private String grp_cd = "";
    private ReportTable groupMstHeader;
    int form_id = -1;

    /**
     * Creates new form GroupMaster
     */
    public GroupMaster(int form_id) {
        initComponents();
        this.form_id = form_id;
        connectNavigation();
        makeChildTableGroupMaster();
        addJcomboBox();
        navLoad.setVoucher("last");
        lb.setUserRightsToPanel(navLoad, form_id+"");
    }

    private void addJcomboBox() {
        try {

            jcmbHeadGroup.removeAllItems();
            jcmbHeadGroup.addItem("");
            String sql = "select group_name from groupmst where head_grp = 0";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            while (rsLcoal.next()) {
                jcmbHeadGroup.addItem(rsLcoal.getString("group_name"));
            }

        } catch (Exception ex) {
            lb.printToLogFile("Exception at addJcombobox", ex);
        }
    }

    public boolean fldValid(Component comp) {
        navLoad.setMessage("");
        if (comp == jtxtGroup) {
            if (jtxtGroup.getText().trim().length() == 0) {
                navLoad.setMessage("Group Name cannot be left blank");
                comp.requestFocusInWindow();
                return false;
            }
            if (navLoad.getMode().equalsIgnoreCase("N")) {
                if (lb.isExist("groupmst", "grp_cd", "group_name", dataConnection)) {
                    navLoad.setMessage("Group Name already exist or Blank. Enter another name.");
                    comp.requestFocusInWindow();
                    return false;
                }
            } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isExistForEdit("groupmst", "grp_cd", "group_name", jtxtGroup.getText(), grp_cd, dataConnection)) {
                    navLoad.setMessage("Group Name already exist or Blank. Enter another name.");
                    comp.requestFocusInWindow();
                    return false;
                }

            }
        }
        if (comp == jcmbHeadGroup) {
            int code = jcmbHeadGroup.getSelectedIndex();
            if (code == -1) {
                navLoad.setMessage("Enter valid Head Group");
                comp.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private boolean validateFormValue() {
        boolean flag = true;
        flag = flag && fldValid(jtxtGroup);
        flag = flag && fldValid(jcmbHeadGroup);

        return flag;
    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
//            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    public void setGroupid(String grp_cd) {
        this.grp_cd = grp_cd;
        navLoad.setVoucher("edit");
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

    private void makeChildTableGroupMaster() {
        groupMstHeader = new ReportTable();
        groupMstHeader.AddColumn(0, "Group Code", -1, java.lang.Integer.class, null, false);
        groupMstHeader.AddColumn(1, "Group Name", -1, java.lang.String.class, null, false);
        groupMstHeader.makeTable();
    }

    private void onViewVoucher() {
        String sql = "select grp_cd,group_name from groupmst order by grp_cd";
        groupMstHeader.setColumnValue(new int[]{1, 2});
        HeaderIntFrame rptDetail = new HeaderIntFrame(dataConnection, grp_cd + "", "Group Master View", sql, "14", 1, this, this.getTitle(), groupMstHeader);
        rptDetail.makeView();
        Component c = HMSHome.tabbedPane.add("Group Master Master View", rptDetail);
        this.dispose();
        rptDetail.setVisible(true);
        HMSHome.tabbedPane.setSelectedComponent(c);
    }

    private int saveVoucher() {
        PreparedStatement pstLocal = null;
        String sql = "";

        try {
            if (navLoad.getMode().equalsIgnoreCase("n")) {
                sql = "insert into groupmst (Group_Name,User_id,head_grp,acc_eff,side)"
                        + "values (?,?,?,?,?)";
            } else {
                sql = "update groupmst set group_Name=?,user_id=?,head_grp=?,edit_No=edit_No+1,acc_eff=?,side=? where grp_cd=" + grp_cd;
            }
            pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, jtxtGroup.getText().trim().toUpperCase());
            pstLocal.setInt(2, HMSHome.user_id);
            pstLocal.setString(3, lb.getGroupName(jcmbHeadGroup.getSelectedItem().toString(), "C"));
            pstLocal.setInt(4, jComboBox1.getSelectedIndex());
            pstLocal.setString(5, lb.getData("SIDE", "groupmst", "grp_cd", lb.getGroupName(jcmbHeadGroup.getSelectedItem().toString(), "C"), 1));
            return pstLocal.executeUpdate();

        } catch (Exception ex) {
            lb.printToLogFile("Exception at save voucher new", ex);
            return 0;
        }

    }

    private void connectNavigation() {
        class smallNavigation extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtGroup.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                if (jcmbHeadGroup.getSelectedIndex() != -1 && jcmbHeadGroup.getSelectedIndex() != 0) {
                    setComponentEnabledDisabled(true);
                    setSaveFlag(false);
                    setMode("E");
                    jtxtGroup.requestFocusInWindow();
                }
            }

            @Override
            public void callSave() {
                valueUpdateToDatabase();
            }

            @Override
            public void callDelete() {
            }

            @Override
            public void callView() {
                onViewVoucher();
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void callPrint() {
            }

            public void setComponentText(String strText) {
                jcmbHeadGroup.setSelectedIndex(0);
                jlblEditNo.setText(strText);
                jlblLstUpdate.setText(strText);
                jlblUserName.setText(strText);
                jtxtGroup.setText(strText);
            }

            @Override
            public void setComponentEnabledDisabled(boolean bFlag) {
                jcmbHeadGroup.setEnabled(bFlag);
                jtxtGroup.setEnabled(bFlag);
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    navLoad.setMessage("");
                    grp_cd = navLoad.viewData.getString("grp_cd");
                    jtxtGroup.setText(navLoad.viewData.getString("GROUP_NAME"));
                    jlblUserName.setText(lb.getUserName(navLoad.viewData.getString("user_id"), "N"));
                    jlblEditNo.setText(navLoad.viewData.getString("edit_no"));
                    jlblLstUpdate.setText(lb.timestamp.format(new Date(viewData.getTimestamp("TIME_STAMP").getTime())));
                    jcmbHeadGroup.setSelectedItem(lb.getGroupName(viewData.getString("head_grp"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentFromRS", ex);
                }
            }

            public int valueUpdateToDatabase() {
                try {
                    if (validateFormValue()) {
                        dataConnection.setAutoCommit(false);
                        saveVoucher();
                        dataConnection.commit();
                        dataConnection.setAutoCommit(true);

                        if (navLoad.getMode().equalsIgnoreCase("N")) {
                            setVoucher("last");
                        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                            setVoucher("Edit");
                        }
                        cancelOrClose();
                    } else {

                    }

                } catch (Exception ex) {
                    try {
                        lb.printToLogFile("Exception at saveVoucher ataccount master", ex);
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                    } catch (SQLException ex1) {
                    }
                }
                return 1;
            }

            @Override
            public void setVoucher(String tag) {

                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id+"");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from groupmst where grp_cd=(select min(grp_cd) from groupmst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from groupmst where grp_cd=(select max(grp_cd) from groupmst where grp_cd <" + grp_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from groupmst where grp_cd=(select min(grp_cd) from groupmst where grp_cd >" + grp_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from groupmst where grp_cd=(select max(grp_cd) from groupmst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from groupmst where grp_cd=" + grp_cd);
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
                return validateFormValue();
            }

        }

        navLoad = new smallNavigation();
        jpanelNavigation.add(navLoad);
        navLoad.setVisible(true);
        navLoad.setprintFlag(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jpanelNavigation = new javax.swing.JPanel();
        jlblUserName = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtGroup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jcmbHeadGroup = new javax.swing.JComboBox();

        setClosable(true);
        setTitle("Group Master");

        jLabel1.setText("Head Group");

        jLabel6.setText("User:");

        jLabel7.setText("Edit No:");

        jLabel8.setText("Last Updated:");

        jpanelNavigation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jpanelNavigation.setLayout(new java.awt.BorderLayout());

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jLabel2.setText("Group Name");

        jtxtGroup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGroupFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGroupFocusLost(evt);
            }
        });
        jtxtGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGroupKeyReleased(evt);
            }
        });

        jLabel3.setText("Effect To");

        jComboBox1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Trading A/c", "Profit & Loss A/C", "Ballance Sheet" }));
        jComboBox1.setEnabled(false);

        jcmbHeadGroup.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcmbHeadGroup.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcmbHeadGroupItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxtGroup)
                            .addComponent(jComboBox1, 0, 162, Short.MAX_VALUE)
                            .addComponent(jcmbHeadGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbHeadGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jlblUserName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel7, jlblEditNo});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, jlblLstUpdate});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtGroup});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel3});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jcmbHeadGroup});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtGroupFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupFocusGained
        // TODO add your handling code here:
        jtxtGroup.selectAll();
    }//GEN-LAST:event_jtxtGroupFocusGained

    private void jtxtGroupFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupFocusLost
        // TODO add your handling code here:
        jtxtGroup.setText(jtxtGroup.getText().toUpperCase());
    }//GEN-LAST:event_jtxtGroupFocusLost

    private void jtxtGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupKeyReleased
        lb.enterFocus(evt, jcmbHeadGroup);
    }//GEN-LAST:event_jtxtGroupKeyReleased

    private void jcmbHeadGroupItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmbHeadGroupItemStateChanged
        // TODO add your handling code here:
        if (jcmbHeadGroup.getSelectedIndex() != -1) {
            setComboIndex();
        }
    }//GEN-LAST:event_jcmbHeadGroupItemStateChanged

    private void setComboIndex() {
        String sql = "select acc_eff from groupmst where group_name='" + jcmbHeadGroup.getSelectedItem().toString() + "'";
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                jComboBox1.setSelectedIndex(rsLocal.getInt(1));
            }
            if (rsLocal != null) {
                rsLocal.close();
            }
            if (pstLocal != null) {
                pstLocal.close();
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setComboIndex", ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JComboBox jcmbHeadGroup;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JPanel jpanelNavigation;
    private javax.swing.JTextField jtxtGroup;
    // End of variables declaration//GEN-END:variables
}
