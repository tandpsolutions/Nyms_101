/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import support.Library;

/**
 *
 * @author Lenovo
 */
public class ErrorMessage extends javax.swing.JDialog {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    public DefaultTableModel dtm = null;
    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form ErrorMessage
     */
    public ErrorMessage(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });

        addDoctorError();
        if (dtm.getRowCount() == 0) {
            addPatientError();
        }
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void addDoctorError() {
        try {
            String sql = "SELECT a.ac_cd,a.ac_name,a1.add1,a1.add2,a1.add3,a1.city_cd,l.user_name,"
                    + " CASE WHEN d.sub_spec_cd IS NULL THEN 0 ELSE d.sub_spec_cd END AS spec_cd,  p.mobile1 FROM acntmst a"
                    + " LEFT JOIN adbkmst a1  ON a.ac_cd = a1.ac_cd LEFT JOIN phbkmst p ON a.ac_cd=p.ac_cd LEFT JOIN doctormaster d ON a.ac_cd=d.ac_cd"
                    + " LEFT JOIN login l ON a.user_id = l.user_id  WHERE p.mobile1 ='' OR a1.city_cd = 0 OR d.sub_spec_cd IS NULL OR d.sub_spec_cd = 0";
            PreparedStatement pstLcoal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLcoal.executeQuery();
            dtm.setRowCount(0);
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add(rsLocal.getString("ac_cd"));
                row.add(rsLocal.getString("ac_name"));
                row.add(rsLocal.getString("add1"));
                row.add(rsLocal.getString("add2"));
                row.add(rsLocal.getString("add3"));
                row.add(lb.getCityCd(rsLocal.getString("city_Cd"), "N"));
                row.add(lb.getSubSpecialistCd(rsLocal.getString("spec_cd"), "N"));
                row.add(rsLocal.getString("mobile1"));
                row.add(rsLocal.getString("user_name"));
                dtm.addRow(row);
            }
            lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at doctor wise", ex);
        }
    }

    private void addDoctorErrorExcel() {
        try {
            String sql = "SELECT a.ac_cd,a.ac_name,a1.add1,a1.add2,a1.add3,c.city_name,l.user_name,"
                    + " CASE WHEN s.spec_sub_name IS NULL THEN '' ELSE s.spec_sub_name END AS spec_sub_name,  p.mobile1 FROM acntmst a"
                    + " LEFT JOIN adbkmst a1  ON a.ac_cd = a1.ac_cd LEFT JOIN phbkmst p ON a.ac_cd=p.ac_cd LEFT JOIN doctormaster d ON a.ac_cd=d.ac_cd"
                    + " LEFT JOIN login l ON a.user_id = l.user_id left join citymst c on a1.city_cd=c.city_cd left join specsubmst s on "
                    + " d.sub_spec_cd = s.spec_sub_cd "
                    + " WHERE p.mobile1 ='' OR a1.city_cd = 0 OR d.sub_spec_cd IS NULL OR d.sub_spec_cd = 0";
            PreparedStatement pstLcoal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLcoal.executeQuery();
            ArrayList patient = new ArrayList();
            while (rsLocal.next()) {
                ArrayList ep = new ArrayList();
                ep.add(rsLocal.getString("ac_cd"));
                ep.add(rsLocal.getString("ac_name"));
                ep.add(rsLocal.getString("add1"));
                ep.add(rsLocal.getString("add2"));
                ep.add(rsLocal.getString("add3"));
                ep.add(rsLocal.getString("city_name"));
                ep.add(rsLocal.getString("user_name"));
                ep.add(rsLocal.getString("spec_sub_name"));
                ep.add(rsLocal.getString("mobile1"));
                patient.add(ep);
            }
            ArrayList header = new ArrayList();
            header.add("Account Code");
            header.add("Account Name");
            header.add("Address1");
            header.add("Address2");
            header.add("Address3");
            header.add("City Name");
            header.add("User Name");
            header.add("Speciality");
            header.add("Mobile");
            lb.exportToExcel("ErrorBookDoctor", header, patient, "ErrorBookDoctor");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at doctor wise", ex);
        }
    }

    private void addPatientError() {
        try {
            String sql = "SELECT a.opd_no,a.pt_name,a1.address,c.city_name,a1.mobile,a2.ac_name,l.user_name FROM patientmst a "
                    + "  LEFT JOIN patientinfomst a1  ON a.opd_no= a1.opd_no LEFT JOIN login l ON a.user_id = l.user_id "
                    + "  LEFT JOIN acntmst a2 ON a.ref_by = a2.ac_cd"
                    + "  LEFT JOIN citymst c ON a1.city_cd = c.city_cd  WHERE ((a1.mobile ='' OR a1.city_cd = 0 OR a1.city_cd = 33)"
                    + "  AND (a.opd_no IN (SELECT opd_no FROM roommst) OR a.opd_no IN (SELECT opd_no FROM appointmentmst))) AND a.ref_by=1";
            PreparedStatement pstLcoal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLcoal.executeQuery();
            dtm.setRowCount(0);
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add(rsLocal.getString("opd_no"));
                row.add(rsLocal.getString("pt_name"));
                row.add(rsLocal.getString("address"));
                row.add("");
                row.add("");
                row.add(rsLocal.getString("city_name"));
                row.add("");
                row.add(rsLocal.getString("mobile"));
                row.add(rsLocal.getString("user_name"));
                dtm.addRow(row);
            }
            lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at doctor wise", ex);
        }
    }

    private void addPatientErrorExcel() {
        try {
            String sql = "SELECT a.opd_no,a.pt_name,a1.address,c.city_name,a1.mobile,a2.ac_name,l.user_name FROM patientmst a "
                    + "  LEFT JOIN patientinfomst a1  ON a.opd_no= a1.opd_no LEFT JOIN login l ON a.user_id = l.user_id "
                    + "  LEFT JOIN acntmst a2 ON a.ref_by = a2.ac_cd"
                    + "  LEFT JOIN citymst c ON a1.city_cd = c.city_cd  WHERE ((a1.mobile ='' OR a1.city_cd = 0 OR a1.city_cd = 33)"
                    + "  AND (a.opd_no IN (SELECT opd_no FROM roommst) OR a.opd_no IN (SELECT opd_no FROM appointmentmst))) AND a.ref_by=1";
            PreparedStatement pstLcoal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLcoal.executeQuery();
            ArrayList patient = new ArrayList();
            while (rsLocal.next()) {
                ArrayList ep = new ArrayList();
                ep.add(rsLocal.getString("OPD_NO"));
                ep.add(rsLocal.getString("pt_name"));
                ep.add(rsLocal.getString("address"));
                ep.add(rsLocal.getString("city_name"));
                ep.add(rsLocal.getString("mobile"));
                ep.add(rsLocal.getString("ac_name"));
                ep.add(rsLocal.getString("user_name"));
                patient.add(ep);
            }
            ArrayList header = new ArrayList();
            header.add("OPD Number");
            header.add("Patient Name");
            header.add("Address");
            header.add("City Name");
            header.add("Mobile");
            header.add("Reference Doctor");
            header.add("User Name");
            lb.exportToExcel("ErrorBookPatient", header, patient, "ErrorBookPatient");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at doctor wise", ex);
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

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ac_cd", "Name", "Address", "", "", "City", "Speciality", "Mobile", "User"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("By Patient");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("By Doctor");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Doctor Error Excel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Patient  Error Excel");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
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
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton1)
                        .addComponent(jButton3)
                        .addComponent(jButton4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton)))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        addDoctorError();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        addPatientError();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        addDoctorErrorExcel();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        addPatientErrorExcel();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
