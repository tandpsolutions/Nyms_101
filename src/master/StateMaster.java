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
public class StateMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form StateMaster
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    int state_cd = -1;
    ReportTable table = null;
    int form_id = -1;

    public StateMaster(int form_id) {
        initComponents();
        connectToNavigation();
        fillJcomboBox();
        navLoad.setVoucher("Last");
        this.form_id = form_id;
        lb.setUserRightsToPanel(navLoad, form_id+"");
    }

    private void setComponentText(String text) {
        jtxtStateName.setText(text);
        jcmbCountry.getEditor().setItem("");
        jcmbCountry.setSelectedIndex(0);
    }

    public void setID(String code) {
        state_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "State Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "State Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Country Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void fillJcomboBox() {
        try {
            jcmbCountry.removeAllItems();
            jcmbCountry.addItem("");
            String sql = "select country_name from countrymst";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            while (rsLcoal.next()) {
                jcmbCountry.addItem(rsLcoal.getString("country_name"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at fillJcombobox at country master", ex);
        }
        jcmbCountry.setEditable(true);
        JTextComponent editor = (JTextComponent) jcmbCountry.getEditor().getEditorComponent();
        editor.setDocument(new S09ShowPopup(jcmbCountry, navLoad));
    }

    private void connectToNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtStateName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtStateName.requestFocusInWindow();
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
                                sql = " insert into statemst (state_name,country_cd,user_id)values (?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update statemst set state_name=?,country_cd=?,user_id=?,edit_no=edit_no+1,time_stamp=current_timestamp"
                                        + " where state_cd=" + state_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtStateName.getText());
                            pstLocal.setString(2, lb.getCountryCD(jcmbCountry.getSelectedItem().toString(), "C"));
                            pstLocal.setInt(3, HMSHome.user_id);
                            pstLocal.executeUpdate();
                            setSaveFlag(true);
                            if(getMode().equalsIgnoreCase("N")){
                                setVoucher("Last");
                            } else if(getMode().equalsIgnoreCase("E")){
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
                            if (lb.isExist("citymst", "state_cd", state_cd + "", dataConnection)) {
                                navLoad.setMessage("State exist in city master. you can not delete this state.");
                            } else {
                                lb.confirmDialog("Do you want to delete this state?");
                                if (lb.type) {
                                    String sql = "delete from statemst where state_cd=" + state_cd;
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
                String sql = "select state_cd,state_name,country_name from statemst s left join countrymst c on c.country_cd=s.country_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, state_cd + "", "State Master View", sql, "11", 1, StateMaster.this, "State Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "State Master View",-1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id+"");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from statemst where state_cd=(select min(state_cd) from statemst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from statemst where state_cd=(select max(state_cd) from statemst where state_cd <" + state_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from statemst where state_cd=(select min(state_cd) from statemst where state_cd >" + state_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from statemst where state_cd=(select max(state_cd) from statemst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from statemst where state_cd=" + state_cd);
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
                    state_cd = viewData.getInt("state_cd");
                    jtxtStateName.setText(viewData.getString("state_name"));
                    jcmbCountry.setSelectedItem(lb.getCountryCD(viewData.getString("country_cd"), "N"));
                    jcmbCountry.getEditor().setItem(lb.getCountryCD(viewData.getString("country_cd"), "N"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in country master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtStateName.setEnabled(flag);
                jcmbCountry.setEnabled(flag);
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtStateName)) {
                    setMessage("State name can not be left blank");
                    jtxtStateName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("statemst", "state_name", jtxtStateName.getText(), dataConnection)) {
                        setMessage("State name already exist");
                        jtxtStateName.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("statemst", "state_name", jtxtStateName.getText(), "state_cd", state_cd + "", dataConnection)) {
                        setMessage("State name already exist");
                        jtxtStateName.requestFocusInWindow();
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
        jtxtStateName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcmbCountry = new javax.swing.JComboBox();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("State Name");

        jtxtStateName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtStateNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtStateNameFocusLost(evt);
            }
        });
        jtxtStateName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtStateNameKeyPressed(evt);
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

        jLabel2.setText("Country Name");

        jcmbCountry.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                            .addComponent(jcmbCountry, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jtxtStateName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtStateName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcmbCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 40, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtStateName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jcmbCountry});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtStateNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtStateNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtStateNameFocusGained

    private void jtxtStateNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtStateNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtStateNameFocusLost

    private void jtxtStateNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtStateNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jcmbCountry.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtStateNameKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox jcmbCountry;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtStateName;
    // End of variables declaration//GEN-END:variables
}
