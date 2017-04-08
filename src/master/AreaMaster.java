/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;
import support.HeaderIntFrame;
import support.Library;
import support.ReportTable;
import support.S09ShowPopup;
import support.SmallNavigation;

/**
 *
 * @author Bhaumik
 */
public class AreaMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form StateMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int area_cd = -1;
    ReportTable table = null;
    int form_id = -1;

    public AreaMaster(int form_id) {
        initComponents();
        connectToNavigation();
        fillJcomboBox();
        navLoad.setVoucher("Last");
        this.form_id = form_id;
        lb.setUserRightsToPanel(navLoad, form_id+"");
    }

    private void setComponentText(String text) {
        jtxtAreaName.setText(text);
        jcmbArea.getEditor().setItem("");
        jcmbArea.setSelectedIndex(0);
        jtxtPinCode.setText("");
    }

    public void setID(String code) {
        area_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Area Code", -1, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Area Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Pincode Code", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "City Name", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "State Name", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Country Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void fillJcomboBox() {
        try {
            jcmbArea.removeAllItems();
            jcmbArea.addItem("");
            String sql = "select city_name from citymst";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            while (rsLcoal.next()) {
                jcmbArea.addItem(rsLcoal.getString("city_name"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at fillJcombobox at area master", ex);
        }
        jcmbArea.setEditable(true);
        JTextComponent editor = (JTextComponent) jcmbArea.getEditor().getEditorComponent();
        editor.setDocument(new S09ShowPopup(jcmbArea, jtxtPinCode));
    }

    private void connectToNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtAreaName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtAreaName.requestFocusInWindow();
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
                                sql = " insert into areamst (area_name,city_cd,pincode,user_id) values (?,?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update areamst set area_name=?,city_cd=?,pincode=?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " where area_cd=" + area_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtAreaName.getText());
                            pstLocal.setString(2, lb.getCityCd(jcmbArea.getSelectedItem().toString(), "C"));
                            pstLocal.setString(3, jtxtPinCode.getText());
                            pstLocal.setInt(4, HMSHome.user_id);
                            pstLocal.executeUpdate();
                            setSaveFlag(true);
                            if (getMode().equalsIgnoreCase("N")) {
                                setVoucher("Last");
                            } else if (getMode().equalsIgnoreCase("E")) {
                                setVoucher("Edit");
                            }
                            setMode("");
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at saveVoucher at save area master", ex);
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
                            if (lb.isExist("adbkmst", "area_cd", area_cd + "", dataConnection)) {
                                navLoad.setMessage("area exist in account master. you can not delete this area.");
                            } else {
                                lb.confirmDialog("Do you want to delete this area?");
                                if (lb.type) {
                                    String sql = "delete from areamst where area_cd=" + area_cd;
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
                String sql = "select area_cd,area_name,pincode,city_name,state_name,country_name from areamst a left join citymst c1  on "
                        + "a.city_cd = c1.city_cd left join statemst s on c1.state_cd= s.state_cd left join countrymst c on c.country_cd=s.country_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, area_cd + "", "Area Master View", sql, "13", 1, AreaMaster.this, "Area Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Area Master View",-1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id+"");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from areamst where area_cd=(select min(area_cd) from areamst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from areamst where area_cd=(select max(area_cd) from areamst where area_cd <" + area_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from areamst where area_cd=(select min(area_cd) from areamst where area_cd >" + area_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from areamst where area_cd=(select max(area_cd) from areamst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from areamst where area_cd=" + area_cd);
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
                    area_cd = viewData.getInt("area_cd");
                    jtxtAreaName.setText(viewData.getString("area_name"));
                    jtxtPinCode.setText(viewData.getString("pincode"));
                    jcmbArea.setSelectedItem(lb.getCityCd(viewData.getString("city_cd"), "N"));
                    jcmbArea.getEditor().setItem(lb.getCityCd(viewData.getString("city_cd"), "N"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in area master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtAreaName.setEnabled(flag);
                jcmbArea.setEnabled(flag);
                jtxtPinCode.setEnabled(flag);
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtAreaName)) {
                    setMessage("Area name can not be left blank");
                    jtxtAreaName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("areamst", "area_name", jtxtAreaName.getText(), dataConnection)) {
                        setMessage("Area name already exist");
                        jtxtAreaName.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("areamst", "area_name", jtxtAreaName.getText(), "area_cd", area_cd + "", dataConnection)) {
                        setMessage("Area name already exist");
                        jtxtAreaName.requestFocusInWindow();
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
        jtxtAreaName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcmbArea = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jtxtPinCode = new javax.swing.JTextField();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Area Name");

        jtxtAreaName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAreaNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAreaNameFocusLost(evt);
            }
        });
        jtxtAreaName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAreaNameKeyPressed(evt);
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

        jLabel2.setText("City Name");

        jcmbArea.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Picode");

        jtxtPinCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPinCodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPinCodeFocusLost(evt);
            }
        });
        jtxtPinCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPinCodeKeyPressed(evt);
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
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jcmbArea, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jtxtAreaName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jtxtPinCode, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAreaName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtPinCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtAreaName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jcmbArea});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jtxtPinCode});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtAreaNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAreaNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAreaNameFocusGained

    private void jtxtAreaNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAreaNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAreaNameFocusLost

    private void jtxtAreaNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAreaNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jcmbArea.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtAreaNameKeyPressed

    private void jtxtPinCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPinCodeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPinCodeFocusGained

    private void jtxtPinCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPinCodeFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtPinCodeFocusLost

    private void jtxtPinCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPinCodeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtPinCodeKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox jcmbArea;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtAreaName;
    private javax.swing.JTextField jtxtPinCode;
    // End of variables declaration//GEN-END:variables
}
