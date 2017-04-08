/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import javax.swing.JOptionPane;
import support.Library;

/**
 *
 * @author nice
 */
public class BackUpProcess {

    Library clib = new Library();

    public void jButton1ActionPerformed(String db, String user, String pwd, String Path, String exe) {//GEN-FIRST:event_jButton1ActionPerformed

        try {

            int processComplete; // to verify that either process completed or not

            Process runtimeProcess = Runtime.getRuntime().exec(exe + " -u " + user + " -p" + pwd + " " + db + " -r " + Path);

// call the mysqldump in terminal and execute it
            processComplete = runtimeProcess.waitFor();//store the state in variable

            if (processComplete != 0) {//if values equal 1 process failed

//                JOptionPane.showMessageDialog(null, "Backup Failed.This is not server computer");//display message
            } else if (processComplete == 0) {//if values equal 0 process failed

                JOptionPane.showMessageDialog(null, "\n Backup created Successfully..\n Check the Backup File in the " + CronScheduler.filePath + " Directory named as backup.sql");
//display message
            }

        } catch (Exception e) {
            clib.printToLogFile("Exception at BackUpProcess", e);
        }

// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    public void jButton1ActionPerformedauto(String db, String user, String pwd, String Path, String exe) {//GEN-FIRST:event_jButton1ActionPerformed

        try {

            int processComplete; // to verify that either process completed or not

            Process runtimeProcess = Runtime.getRuntime().exec(exe + " -u " + user + " -p" + pwd + " " + db + " -r " + Path);

// call the mysqldump in terminal and execute it
            processComplete = runtimeProcess.waitFor();//store the state in variable
        } catch (Exception e) {
            new Library().printToLogFile("Exception at back up process", e);
        }

// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
}
