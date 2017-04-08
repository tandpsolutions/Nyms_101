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
public class SpecialityMsater extends javax.swing.JInternalFrame {

    /**
     * Creates new form SpecialityMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int speciality_cd = -1;
    ReportTable table = null;
    int form_cd = -1;

    public SpecialityMsater(int user_id) {
        initComponents();
        form_cd = user_id;
        connectToNavigation();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel(navLoad, form_cd+"");
    }

    private void setComponentText(String text) {
        jtxtSpeciality.setText(text);
    }

    public void setID(String code) {
        speciality_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Speciality Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Speciality Name", -1, java.lang.String.class, null, false);
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
                jtxtSpeciality.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
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
                                sql = " insert into specialitymst (speciality_name,user_id)values (?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update specialitymst set speciality_name=?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " where speciality_cd=" + speciality_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtSpeciality.getText());
                            pstLocal.setInt(2, HMSHome.user_id);
                            pstLocal.executeUpdate();
                            setSaveFlag(true);
                            if (getMode().equalsIgnoreCase("N")) {
                                setVoucher("Last");
                            } else if (getMode().equalsIgnoreCase("E")) {
                                setVoucher("Edit");
                            }
                            setMode("");
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at saveVoucher at save Speciality master", ex);
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
                            if (lb.isExist("statemst", "speciality_cd", speciality_cd + "", dataConnection)) {
                                navLoad.setMessage("Speciality exist in doctor master. you can not delete this Speciality");
                            } else {
                                lb.confirmDialog("Do you want to delete this Specialityy?");
                                if (lb.type) {
                                    String sql = "delete from specialitymst where speciality_cd=" + speciality_cd;
                                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                    pstLocal.executeUpdate();
                                    setVoucher("Previous");
                                }
                            }
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at call delete at Speciality master", ex);
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
                String sql = "select speciality_cd,speciality_name from specialitymst";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, speciality_cd + "", "Speciality Master View", sql, "18", 1, SpecialityMsater.this, "Speciality Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Speciality Master View",-1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_cd+"");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from specialitymst where speciality_cd=(select min(speciality_cd) from specialitymst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from specialitymst where speciality_cd=(select max(speciality_cd) from specialitymst where speciality_cd <" + speciality_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from specialitymst where speciality_cd=(select min(speciality_cd) from specialitymst where speciality_cd >" + speciality_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from specialitymst where speciality_cd=(select max(speciality_cd) from specialitymst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from specialitymst where speciality_cd=" + speciality_cd);
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
                    speciality_cd = viewData.getInt("speciality_cd");
                    jtxtSpeciality.setText(viewData.getString("speciality_name"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in Speciality master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtSpeciality.setEnabled(flag);
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtSpeciality)) {
                    setMessage("Speciality name can not be left blank");
                    jtxtSpeciality.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("specialitymst", "speciality_name", jtxtSpeciality.getText(), dataConnection)) {
                        setMessage("Speciality name already exist");
                        jtxtSpeciality.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("specialitymst", "speciality_name", jtxtSpeciality.getText(), "speciality_cd", speciality_cd + "", dataConnection)) {
                        setMessage("Speciality name already exist");
                        jtxtSpeciality.requestFocusInWindow();
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
        jtxtSpeciality = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Speciality Name");

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
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtSpeciality});

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtSpecialityKeyPressed


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
    private javax.swing.JTextField jtxtSpeciality;
    // End of variables declaration//GEN-END:variables
}
