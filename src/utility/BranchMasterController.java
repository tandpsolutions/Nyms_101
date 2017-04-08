/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import support.Library;

/**
 *
 * @author Bhaumik
 */
public class BranchMasterController extends javax.swing.JInternalFrame {

    /**
     * Creates new form BranchMasterController
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    int form_cd = -1;

    public BranchMasterController(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        SetComponentText();
        setComponentEnebledDisabled(false);
        jbtnClose.setText("Close");
        jbtnEdit.setEnabled(true);
        jbtnSave.setEnabled(false);
        lb.setShortcut(this, jbtnClose);
    }

    private void setComponentEnebledDisabled(boolean flag) {
        jtxtCompanyName.setEnabled(flag);
        jtxtaddress1.setEnabled(flag);
        jtxtaddress2.setEnabled(flag);
        jtxtaddress3.setEnabled(flag);
        jtxtEmail.setEnabled(flag);
        jtxtphone.setEnabled(flag);
        jtxtlstno.setEnabled(flag);
        if (flag) {
            jtxtCompanyName.requestFocusInWindow();
        }
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

    private void cancelOrClose() {
        if (jbtnClose.getText().equalsIgnoreCase("Cancel")) {
            SetComponentText();
            setComponentEnebledDisabled(false);
            jbtnClose.setText("Close");
            jbtnEdit.setEnabled(true);
            jbtnSave.setEnabled(false);
        } else if (jbtnClose.getText().equalsIgnoreCase("Close")) {
            this.dispose();
        }
    }

    private void SetComponentText() {
        jtxtCompanyName.setText(HMSHome.clsSysInfo.getCMPN_NAME());
        jtxtaddress1.setText(HMSHome.clsSysInfo.getADDRESS1());
        jtxtaddress2.setText(HMSHome.clsSysInfo.getADDRESS2());
        jtxtaddress3.setText(HMSHome.clsSysInfo.getADDRESS3());
        jtxtphone.setText(HMSHome.clsSysInfo.getMOBILE());
        jtxtEmail.setText(HMSHome.clsSysInfo.getEMAIL());
        jtxtlstno.setText(HMSHome.clsSysInfo.getTIN_NO());
    }

    private void jbtnSaveActionPerformedRoutine() {
        try {
            String sql = "update branchmst set ADDRESS1=?,ADDRESS2=?,ADDRESS3=?,MOBILE=?,EMAIL=?,TIN_NO=?";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, jtxtaddress1.getText());
            pstLocal.setString(2, jtxtaddress2.getText());
            pstLocal.setString(3, jtxtaddress3.getText());
            pstLocal.setString(4, jtxtphone.getText());
            pstLocal.setString(5, jtxtEmail.getText());
            pstLocal.setString(6, jtxtlstno.getText());
            int result = pstLocal.executeUpdate();
            if (result > 0) {
                lb.showMessageDailog("You must restart the application \n Otherwise data will be lost.");
                setComponentEnebledDisabled(false);
                jbtnSave.setEnabled(false);
                jbtnClose.setText("close");
                jbtnEdit.setEnabled(true);
                this.dispose();
            } else {
                lb.showMessageDailog("Voucher is not saved on server");
                jbtnSave.requestFocusInWindow();
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jbtnSave Company", ex);
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

        jbtnEdit = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jtxtaddress1 = new javax.swing.JTextField();
        jtxtEmail = new javax.swing.JTextField();
        jtxtphone = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtlstno = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jtxtaddress3 = new javax.swing.JTextField();
        jtxtaddress2 = new javax.swing.JTextField();
        jtxtCompanyName = new javax.swing.JTextField();

        jbtnEdit.setMnemonic('E');
        jbtnEdit.setText("Edit");
        jbtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditActionPerformed(evt);
            }
        });

        jbtnSave.setMnemonic('S');
        jbtnSave.setText("Save");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });
        jbtnSave.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jbtnSaveFocusGained(evt);
            }
        });
        jbtnSave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnSaveKeyPressed(evt);
            }
        });

        jbtnClose.setMnemonic('C');
        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jtxtaddress1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtaddress1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtaddress1FocusLost(evt);
            }
        });
        jtxtaddress1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtaddress1KeyPressed(evt);
            }
        });

        jtxtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtEmailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtEmailFocusLost(evt);
            }
        });
        jtxtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtEmailKeyPressed(evt);
            }
        });

        jtxtphone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtphoneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtphoneFocusLost(evt);
            }
        });
        jtxtphone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtphoneKeyPressed(evt);
            }
        });

        jLabel3.setText("Mobile :");

        jLabel2.setText("Address:");

        jLabel4.setText("Email");

        jtxtlstno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtlstnoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtlstnoFocusLost(evt);
            }
        });
        jtxtlstno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtlstnoKeyPressed(evt);
            }
        });

        jLabel1.setText("Name:");

        jLabel8.setText("TIN NO");

        jtxtaddress3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtaddress3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtaddress3FocusLost(evt);
            }
        });
        jtxtaddress3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtaddress3KeyPressed(evt);
            }
        });

        jtxtaddress2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtaddress2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtaddress2FocusLost(evt);
            }
        });
        jtxtaddress2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtaddress2KeyPressed(evt);
            }
        });

        jtxtCompanyName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCompanyNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCompanyNameFocusLost(evt);
            }
        });
        jtxtCompanyName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCompanyNameKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtCompanyName, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtphone, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtaddress2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtaddress1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtaddress3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtlstno, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCompanyName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtaddress1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtaddress2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtaddress3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtphone, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtlstno, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnEdit)
                    .addComponent(jbtnSave)
                    .addComponent(jbtnClose))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtCompanyNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCompanyNameFocusGained
        // TODO add your handling code here:
        jtxtCompanyName.selectAll();
    }//GEN-LAST:event_jtxtCompanyNameFocusGained

    private void jtxtCompanyNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCompanyNameKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtaddress1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtCompanyNameKeyPressed

    private void jtxtaddress1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtaddress1FocusGained
        // TODO add your handling code here:
        jtxtaddress1.selectAll();
    }//GEN-LAST:event_jtxtaddress1FocusGained

    private void jtxtaddress1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtaddress1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtaddress2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtaddress1KeyPressed

    private void jtxtaddress2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtaddress2FocusGained
        // TODO add your handling code here:
        jtxtaddress2.selectAll();
    }//GEN-LAST:event_jtxtaddress2FocusGained

    private void jtxtaddress2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtaddress2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtaddress3);
    }//GEN-LAST:event_jtxtaddress2KeyPressed

    private void jtxtaddress3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtaddress3FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtaddress3FocusGained

    private void jtxtaddress3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtaddress3KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtphone);
    }//GEN-LAST:event_jtxtaddress3KeyPressed

    private void jtxtphoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtphoneFocusGained
        // TODO add your handling code here:
        jtxtphone.selectAll();
    }//GEN-LAST:event_jtxtphoneFocusGained

    private void jtxtphoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtphoneKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtEmail.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtphoneKeyPressed

    private void jtxtEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmailFocusGained
        // TODO add your handling code here:
        jtxtEmail.selectAll();
    }//GEN-LAST:event_jtxtEmailFocusGained

    private void jtxtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtEmailKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtlstno.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtEmailKeyPressed

    private void jtxtlstnoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtlstnoFocusGained
        // TODO add your handling code here:
        jtxtlstno.selectAll();
    }//GEN-LAST:event_jtxtlstnoFocusGained

    private void jtxtlstnoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtlstnoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnSave.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtlstnoKeyPressed

    private void jbtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditActionPerformed
        // TODO add your handling code here:
        setComponentEnebledDisabled(true);
        jbtnClose.setText("Cancel");
        jbtnEdit.setEnabled(false);
        jbtnSave.setEnabled(true);
    }//GEN-LAST:event_jbtnEditActionPerformed

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        // TODO add your handling code here:
        jbtnSaveActionPerformedRoutine();
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnSaveFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jbtnSaveFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_jbtnSaveFocusGained

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnSaveActionPerformedRoutine();
        }
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        cancelOrClose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jtxtCompanyNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCompanyNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtCompanyNameFocusLost

    private void jtxtaddress1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtaddress1FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtaddress1FocusLost

    private void jtxtaddress2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtaddress2FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtaddress2FocusLost

    private void jtxtaddress3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtaddress3FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtaddress3FocusLost

    private void jtxtphoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtphoneFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtphoneFocusLost

    private void jtxtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmailFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtEmailFocusLost

    private void jtxtlstnoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtlstnoFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtlstnoFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnEdit;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JTextField jtxtCompanyName;
    private javax.swing.JTextField jtxtEmail;
    private javax.swing.JTextField jtxtaddress1;
    private javax.swing.JTextField jtxtaddress2;
    private javax.swing.JTextField jtxtaddress3;
    private javax.swing.JTextField jtxtlstno;
    private javax.swing.JTextField jtxtphone;
    // End of variables declaration//GEN-END:variables
}