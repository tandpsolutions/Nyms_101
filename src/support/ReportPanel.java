/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Bhaumik Shah
 */
public abstract class ReportPanel extends javax.swing.JPanel {

    Library lb = new Library();

    /**
     * Creates new form ReportPanel
     */
    public ReportPanel() {
        initComponents();
        registerShortcutKey();
    }

    public abstract void callView();

    public abstract void callPrint();

    public abstract void callPreview();

    public abstract void callExcel();

    public abstract void callClose();

    private void registerShortcutKey() {
        KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action closeKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!PickList.isVisible) {
                    jClose.doClick();
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, "Close");
        this.getActionMap().put("Close", closeKeyAction);
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
        jButton1 = new javax.swing.JButton();
        jbtnPrint = new javax.swing.JButton();
        jClose = new javax.swing.JButton();
        jClose1 = new javax.swing.JButton();
        jbtnPrint1 = new javax.swing.JButton();

        jButton1.setMnemonic('V');
        jButton1.setText("View");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        jbtnPrint.setMnemonic('P');
        jbtnPrint.setText("Preview");
        jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPrintActionPerformed(evt);
            }
        });
        jbtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPrintKeyPressed(evt);
            }
        });

        jClose.setMnemonic('E');
        jClose.setText("Exit");
        jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseActionPerformed(evt);
            }
        });
        jClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCloseKeyPressed(evt);
            }
        });

        jClose1.setMnemonic('E');
        jClose1.setText("Excel");
        jClose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jClose1ActionPerformed(evt);
            }
        });
        jClose1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jClose1KeyPressed(evt);
            }
        });

        jbtnPrint1.setMnemonic('P');
        jbtnPrint1.setText("Print");
        jbtnPrint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPrint1ActionPerformed(evt);
            }
        });
        jbtnPrint1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPrint1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnPrint1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jClose1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jClose, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnPrint)
                        .addComponent(jClose, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jClose1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtnPrint1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jClose, jbtnPrint});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
//        SwingWorker workerForjbtnGenerate = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                lb.addGlassPane(ReportPanel.this);
        callView();
//                lb.removeGlassPane(ReportPanel.this);
//                return null;
//            }
//        };
//        workerForjbtnGenerate.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrintActionPerformed
        // TODO add your handling code here:
//        SwingWorker workerForjbtnGenerate = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                lb.addGlassPane(ReportPanel.this);
            callPreview();
//                lb.removeGlassPane(ReportPanel.this);
//                return null;
//            }
//        };
//        workerForjbtnGenerate.execute();
    }//GEN-LAST:event_jbtnPrintActionPerformed

    private void jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCloseActionPerformed
        // TODO add your handling code here:
        callClose();
    }//GEN-LAST:event_jCloseActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jButton1KeyPressed

    private void jbtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPrintKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnPrintKeyPressed

    private void jCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCloseKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jCloseKeyPressed

    private void jClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jClose1ActionPerformed
        // TODO add your handling code here:
//        SwingWorker workerForjbtnGenerate = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                lb.addGlassPane(ReportPanel.this);
        callExcel();
//                lb.removeGlassPane(ReportPanel.this);
//                return null;
//            }
//        };
//        workerForjbtnGenerate.execute();
    }//GEN-LAST:event_jClose1ActionPerformed

    private void jClose1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jClose1KeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jClose1KeyPressed

    private void jbtnPrint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrint1ActionPerformed
        // TODO add your handling code here:
        callPrint();
    }//GEN-LAST:event_jbtnPrint1ActionPerformed

    private void jbtnPrint1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPrint1KeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnPrint1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jClose;
    private javax.swing.JButton jClose1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnPrint;
    private javax.swing.JButton jbtnPrint1;
    // End of variables declaration//GEN-END:variables
}
