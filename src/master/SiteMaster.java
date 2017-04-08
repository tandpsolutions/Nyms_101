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
import javax.swing.SwingWorker;
import support.HeaderIntFrame;
import support.Library;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author Bhaumik
 */
public class SiteMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form SiteMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int site_cd = -1;
    ReportTable table = null;
    int form_cd = -1;

    public SiteMaster(int form_cd) {
        initComponents();
        form_cd = form_cd;
        connectToNavigation();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel(navLoad, form_cd + "");
    }

    private void setComponentText(String text) {
        jtxtSiteName.setText(text);
    }

    public void setID(String code) {
        site_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Site Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Site Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void connectToNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtSiteName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtSiteName.requestFocusInWindow();
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
                                sql = " insert into sitemst (site_name,user_id)values (?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update sitemst set site_name=?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " where site_cd=" + site_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtSiteName.getText());
                            pstLocal.setInt(2, HMSHome.user_id);
                            pstLocal.executeUpdate();
                            setSaveFlag(true);
                            setMode("");
                            setVoucher("Edit");
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at saveVoucher at save Site master", ex);
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
                            if (lb.isExist("statemst", "site_cd", site_cd + "", dataConnection)) {
                                navLoad.setMessage("Site exist in state master. you can not delete this Site");
                            } else {
                                lb.confirmDialog("Do you want to delete this Site?");
                                if (lb.type) {
                                    String sql = "delete from sitemst where site_cd=" + site_cd;
                                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                    pstLocal.executeUpdate();
                                    setVoucher("Previous");
                                }
                            }
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at call delete at Site master", ex);
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
                String sql = "select site_cd,site_name from sitemst";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, site_cd + "", "Site Master View", sql, "54", 1, SiteMaster.this, "Site Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Site Master View", -1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_cd + "");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from sitemst where site_cd=(select min(site_cd) from sitemst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from sitemst where site_cd=(select max(site_cd) from sitemst where site_cd <" + site_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from sitemst where site_cd=(select min(site_cd) from sitemst where site_cd >" + site_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from sitemst where site_cd=(select max(site_cd) from sitemst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from sitemst where site_cd=" + site_cd);
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
                    site_cd = viewData.getInt("site_cd");
                    jtxtSiteName.setText(viewData.getString("site_name"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in Site master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtSiteName.setEnabled(flag);
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtSiteName)) {
                    setMessage("Site name can not be left blank");
                    jtxtSiteName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("sitemst", "site_name", jtxtSiteName.getText(), dataConnection)) {
                        setMessage("Site name already exist");
                        jtxtSiteName.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("sitemst", "site_name", jtxtSiteName.getText(), "site_cd", site_cd + "", dataConnection)) {
                        setMessage("Site name already exist");
                        jtxtSiteName.requestFocusInWindow();
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
        jtxtSiteName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Site  Name");

        jtxtSiteName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSiteNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSiteNameFocusLost(evt);
            }
        });
        jtxtSiteName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSiteNameKeyPressed(evt);
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
                        .addComponent(jtxtSiteName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtSiteName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtSiteName});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtSiteNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSiteNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSiteNameFocusGained

    private void jtxtSiteNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSiteNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtSiteNameFocusLost

    private void jtxtSiteNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSiteNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtSiteNameKeyPressed


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
    private javax.swing.JTextField jtxtSiteName;
    // End of variables declaration//GEN-END:variables
}
