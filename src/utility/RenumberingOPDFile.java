/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMSHome;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import support.Library;

/**
 *
 * @author Lenovo
 */
public class RenumberingOPDFile extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    Connection dataConnection = hms.HMS101.connMpAdmin;
    Library lb = new Library();

    /**
     * Creates new form RenumberingOPDFile
     */
    public RenumberingOPDFile(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private String checkGapBetweenNumbers() throws SQLException {
        String old_ref_no = "OP" + HMSHome.year + "/0000";
        String sql = "select ref_no from opdbillhd where ref_no like 'OP%' and ref_no >'" + old_ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        String new_ref_no = "";
        String max_ref_no = lb.getData("select max(ref_no) from opdbillhd where ref_no like 'OP%'");
        while (rsLocal.next()) {
            if (old_ref_no.equalsIgnoreCase(max_ref_no)) {
                break;
            }
            new_ref_no = rsLocal.getString("ref_no");
            String temp_ref_no = lb.generateKey(old_ref_no, 11, "OP" + HMSHome.year + "/");
            if (temp_ref_no.equalsIgnoreCase(new_ref_no)) {
                old_ref_no = new_ref_no;
            } else {
                break;
            }
        }
        if (!old_ref_no.equalsIgnoreCase(max_ref_no)) {
            return old_ref_no;
        } else {
            return null;
        }
    }

    private void updateOldRefNumber(String ref_no) throws SQLException {
        String sql = "update payment set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldt set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhd set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhdlg set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldtlg set old_ref_no = ref_no where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthd set old_ref_no = voucher_no where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthdlg set old_ref_no = voucher_no where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();
    }

    private void updateNewRefNumber(String ref_no) throws SQLException {
        String sql = "update payment set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldt set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhd set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhdlg set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldtlg set ref_no = concat('TP',ref_no) where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthd set voucher_no = concat('TP',voucher_no) where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthdlg set voucher_no = concat('TP',voucher_no) where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();
    }

    private void performSomeMagic(String ref_no) throws SQLException {
        String sql = "select old_ref_no from opdbillhd where old_ref_no like 'OP%' and old_ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);
        ResultSet rsLocal = pstLocal.executeQuery();
        rsLocal.last();
        jProgressBar1.setMaximum(rsLocal.getRow());
        rsLocal.beforeFirst();
        jProgressBar1.setValue(0);
        int i = 0;
        while (rsLocal.next()) {
            String new_ref_no = lb.generateKey(ref_no, 11, "OP" + HMSHome.year + "/");

            //Perform some magic
            sql = "update payment set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbilldt set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbillhd set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbillhdlg set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdbilldtlg set ref_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdpaymenthd set voucher_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            sql = "update opdpaymenthdlg set voucher_no = '" + new_ref_no + "' where old_ref_no='" + rsLocal.getString("old_ref_no") + "'";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.execute();

            ref_no = new_ref_no;
            i++;
            jProgressBar1.setValue(i);
        }
    }

    private void updateOldRefNumberToBlank(String ref_no) throws SQLException {
        String sql = "update payment set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldt set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhd set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbillhdlg set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdbilldtlg set old_ref_no = '' where ref_no like 'OP%' and ref_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthd set old_ref_no = '' where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();

        sql = "update opdpaymenthdlg set old_ref_no = '' where voucher_no like 'OP%' and voucher_no >'" + ref_no + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.execute();
    }

    private void performReNumbering() {
        try {
            dataConnection.setAutoCommit(false);
            gapWiseRenumbering();
            dataConnection.commit();
            dataConnection.setAutoCommit(true);

        } catch (Exception ex) {
            lb.printToLogFile("Exception at renumbering opd number", ex);
            lb.showMessageDailog("Auto Numbering of opd voucher is failed");
            doClose(RET_CANCEL);
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
            } catch (Exception e) {

            }
        }
    }

    private void gapWiseRenumbering() throws SQLException {
        String ref_no = checkGapBetweenNumbers();
        if (ref_no != null) {
            jprogressMain.setMaximum(4);
            jprogressMain.setValue(0);

            updateOldRefNumber(ref_no);
            jprogressMain.setValue(1);
            updateNewRefNumber(ref_no);
            jprogressMain.setValue(2);
            performSomeMagic(ref_no);
            jprogressMain.setValue(3);
            updateOldRefNumberToBlank(ref_no);
            jprogressMain.setValue(4);
            lb.showMessageDailog("Auto Numbering of opd voucher is complete");
        } else {
            lb.showMessageDailog("OPD vouchers are in perfect order.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jprogressMain = new javax.swing.JProgressBar();

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

        jprogressMain.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 240, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jprogressMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jprogressMain, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        SwingWorker workerForjbtnGenerate = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                performReNumbering();
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
                doClose(RET_OK);
                return null;
            }
        };
        workerForjbtnGenerate.execute();
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

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jprogressMain;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
