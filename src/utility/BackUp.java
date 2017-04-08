/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.io.File;
import java.util.Date;
import javax.swing.ImageIcon;
import support.Library;

/**
 *
 * @author nice
 */
public class BackUp extends javax.swing.JInternalFrame {

    /**
     * Creates new form backUp
     */
    Library lb = new Library();
    String Syspath = System.getProperty("user.dir");

    public BackUp() {
        initComponents();
    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    private void cancelOrClose() {
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnbackup = new javax.swing.JButton();
        jbtnclose = new javax.swing.JButton();
        jimg = new javax.swing.JLabel();

        jbtnbackup.setText("Back Up");
        jbtnbackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnbackupActionPerformed(evt);
            }
        });

        jbtnclose.setText("Close");
        jbtnclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtncloseActionPerformed(evt);
            }
        });

        jimg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jbtnbackup)
                        .addGap(77, 77, 77)
                        .addComponent(jbtnclose))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jimg, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnbackup, jbtnclose});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnbackup)
                    .addComponent(jbtnclose))
                .addGap(18, 18, 18)
                .addComponent(jimg, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnbackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnbackupActionPerformed
        // TODO add your handling code here:
        ImageIcon img = null, imgs = null;
        img = new ImageIcon(System.getProperty("user.dir") + "\\Resources\\images\\progress_bar1.gif");
        imgs = new ImageIcon(System.getProperty("user.dir") + "\\Resources\\images\\progress_bar1.png");
        jimg.setIcon(img);
        BackUpProcess bp = new BackUpProcess();
        Date d = new Date();
        bp.jButton1ActionPerformed(HMS101.database, "root", HMS101.pwd, HMS101.currentDirectory + File.separatorChar + "BackUp" + File.separator + (d.getDate()) + "_" + (d.getMonth() + 1) + "_" + (d.getYear() + 1900) + "_" + (d.getHours()) + "_" + (d.getMinutes()) + "_" + (d.getSeconds()) + ".sql",
                HMS101.currentDirectory + File.separatorChar + "lib" + File.separatorChar + "bc.exe");
        jimg.setIcon(imgs);
//        JOptionPane.showMessageDialog(null, "Backup Complete.");
        jimg.setIcon(null);
    }//GEN-LAST:event_jbtnbackupActionPerformed

    private void jbtncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtncloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtncloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtnbackup;
    private javax.swing.JButton jbtnclose;
    private javax.swing.JLabel jimg;
    // End of variables declaration//GEN-END:variables
}
