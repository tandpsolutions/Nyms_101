/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import support.Library;
import support.StatusColumnCellRenderer;

/**
 *
 * @author Lenovo
 */
public class HospitalStatus extends javax.swing.JInternalFrame {

    /**
     * Creates new form HospitalStatus
     */
    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    DefaultTableModel dtm = null;

    public HospitalStatus() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        addInitialData();
        updateData();
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer(3, 2, 1));
    }

    private void addInitialData() {
        try {
            jPanel1.removeAll();
            jPanel1.add(jScrollPane1);
            String sql = "select w.ward_name,r.room_cd from wardmst w left join roommst r on w.ward_cd=r.ward_cd";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            dtm.setRowCount(0);
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add("");
                row.add(rsLocal.getString("ward_name"));
                row.add(rsLocal.getString("room_cd"));
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                dtm.addRow(row);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at add initialData in hospital status", ex);
        }
    }

    private void updateData() {
        try {
            String sql = "SELECT a.ac_name,i.opd_no,i.ipd_no,p.pt_name,r.room_cd,p.ref_opd_no FROM ipdreg i LEFT JOIN roommst r ON i.opd_no=r.opd_no "
                    + " LEFT JOIN patientmst p ON i.opd_no=p.opd_no left join acntmst a on i.doc_cd=a.ac_cd where i.dis_date is null";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    if (jTable1.getValueAt(i, 2).toString().equalsIgnoreCase(rsLocal.getString("room_cd"))) {
                        jTable1.setValueAt(rsLocal.getString("ipd_no"), i, 0);
                        jTable1.setValueAt(rsLocal.getString("opd_no"), i, 4);
                        jTable1.setValueAt(rsLocal.getString("pt_name"), i, 3);
                        jTable1.setValueAt(rsLocal.getString("ref_opd_no"), i, 5);
                        jTable1.setValueAt(rsLocal.getString("ac_name"), i, 6);
                        break;
                    }
                }
            }
            jlblTotalPatient.setText(((int) lb.isNumber(lb.getData("SELECT COUNT(i.opd_no) FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no WHERE i.dis_date IS NULL AND p.ref_opd_no=''"))) + "");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at updateData", ex);
        }
    }

    private void previewData() {
        try {
            addInitialData();
            updateData();
            ArrayList<reportDAo.HospitalStatus> rows = new ArrayList<reportDAo.HospitalStatus>();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                reportDAo.HospitalStatus hs = new reportDAo.HospitalStatus();
                hs.setIpd_no(jTable1.getValueAt(i, 0).toString());
                hs.setWard_name(jTable1.getValueAt(i, 1).toString());
                hs.setRoom_cd(jTable1.getValueAt(i, 2).toString());
                hs.setPt_name(jTable1.getValueAt(i, 3).toString());
                if (jTable1.getValueAt(i, 5) == null || jTable1.getValueAt(i, 5).toString().equalsIgnoreCase("")) {
                    hs.setMother_bed("");
                } else {
                    hs.setMother_bed("Mother Bed");
                }
                if (!jTable1.getValueAt(i, 0).toString().equalsIgnoreCase("")) {
                    rows.add(hs);
                }
            }
            lb.reportGenerator("HospitalStatus.jasper", null, rows, jPanel1);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at updateData", ex);
        }
    }

    private void printData() {
        try {
            addInitialData();
            updateData();
            ArrayList<reportDAo.HospitalStatus> rows = new ArrayList<reportDAo.HospitalStatus>();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                reportDAo.HospitalStatus hs = new reportDAo.HospitalStatus();
                hs.setIpd_no(jTable1.getValueAt(i, 0).toString());
                hs.setWard_name(jTable1.getValueAt(i, 1).toString());
                hs.setRoom_cd(jTable1.getValueAt(i, 2).toString());
                hs.setPt_name(jTable1.getValueAt(i, 3).toString());
                if (jTable1.getValueAt(i, 5) == null || jTable1.getValueAt(i, 5).toString().equalsIgnoreCase("")) {
                    hs.setMother_bed("");
                } else {
                    hs.setMother_bed("Mother Bed");
                }
                if (!jTable1.getValueAt(i, 0).toString().equalsIgnoreCase("")) {
                    rows.add(hs);
                }
            }
            lb.reportGeneratorWord("HospitalStatus.jasper", null, rows);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at updateData", ex);
        }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jlblTotalPatient = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IPD Number", "Ward Name", "Bed No.", "Patient Name", "IPD", "ref_opd", "Doctor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setMinWidth(0);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(5).setMinWidth(0);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("Transfer");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("View Detail");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setText("Total No. Of Patients in hospital");

        jlblTotalPatient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton4.setText("Refresh");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Preview");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Print");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblTotalPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblTotalPatient)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(jButton6)))
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jlblTotalPatient});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            WardTransfer wd = new WardTransfer(jTable1.getValueAt(row, 0).toString());
            HMSHome.addOnScreen(wd, "Ward Transfer", -1);
        } else {
            lb.showMessageDailog("Select Indoor patient first");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            DisplayData wd = new DisplayData(jTable1.getValueAt(row, 0).toString());
            HMSHome.addOnScreen(wd, "View Details", 210);
        } else {
            lb.showMessageDailog("Select Indoor patient first");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        addInitialData();
        updateData();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        previewData();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        printData();
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jlblTotalPatient;
    // End of variables declaration//GEN-END:variables
}
