/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import support.Library;

/**
 *
 * @author Lenovo
 */
public class UploadDataFromFile extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    Library lb = new Library();

    /**
     * Creates new form UploadDataFromFile
     */
    Connection dataConnection = HMS101.connMpAdmin;

    public UploadDataFromFile(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

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
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void main() {
        //
        // An excel file name. You can create a file name with a full path
        // information.
        //

        try {
            dataConnection.setAutoCommit(false);
            switch (jComboBox1.getSelectedIndex()) {
                case 1:
                    addPatientMaster();
                    break;
                case 2:
                    addBillItemMaster();
                    break;
                default:
                    lb.showMessageDailog("Please select appropriate index");
                    break;
            }
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
    }

    private void addBillItemMaster() throws IOException, SQLException {
        int i = 0;
        String filename = jtxtDatabaseLocation.getText();

        //
        // Create an ArrayList to store the data read from excel sheet.
        //
        List sheetData = new ArrayList();

        FileInputStream fis = null;
        //
        // Create a FileInputStream that will be use to read the excel file.
        //
        fis = new FileInputStream(filename);

        //
        // Create an excel workbook from the file system.
        //
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        //
        // Get the first sheet on the workbook.
        //
        HSSFSheet sheet = workbook.getSheetAt(0);

        //
        // When we have a sheet object in hand we can iterator on each
        // sheet's rows and on each row's cells. We store the data read
        // on an ArrayList so that we can printed the content of the excel
        // to the console.
        //
        Iterator rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = sheet.getRow(i);
            i++;

            List data = new ArrayList();
            if (row != null) {
                for (int j = 0; j < 8; j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        data.add(cell.toString().toUpperCase());
                    } else {
                        data.add("");
                    }
                }
            } else {
                break;
            }

            sheetData.add(data);
        }
        addBillItemMasterINDatabase(sheetData);
    }

    private void addDoctorAccountMaster() throws IOException, SQLException {
        int i = 0;
        String filename = jtxtDatabaseLocation.getText();

        //
        // Create an ArrayList to store the data read from excel sheet.
        //
        List sheetData = new ArrayList();

        FileInputStream fis = null;
        //
        // Create a FileInputStream that will be use to read the excel file.
        //
        fis = new FileInputStream(filename);

        //
        // Create an excel workbook from the file system.
        //
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        //
        // Get the first sheet on the workbook.
        //
        HSSFSheet sheet = workbook.getSheetAt(0);

        //
        // When we have a sheet object in hand we can iterator on each
        // sheet's rows and on each row's cells. We store the data read
        // on an ArrayList so that we can printed the content of the excel
        // to the console.
        //
        Iterator rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = sheet.getRow(i);
            i++;

            List data = new ArrayList();
            if (row != null) {
                for (int j = 0; j < 9; j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        data.add(cell.toString().toUpperCase());
                    } else {
                        data.add("");
                    }
                }
            } else {
                break;
            }

            sheetData.add(data);
        }
        addAccountInDatabase(sheetData);
    }

    private void addReferanceDoctorAccountMaster() throws IOException, SQLException {
        int i = 0;
        String filename = jtxtDatabaseLocation.getText();

        //
        // Create an ArrayList to store the data read from excel sheet.
        //
        List sheetData = new ArrayList();

        FileInputStream fis = null;
        //
        // Create a FileInputStream that will be use to read the excel file.
        //
        fis = new FileInputStream(filename);

        //
        // Create an excel workbook from the file system.
        //
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        //
        // Get the first sheet on the workbook.
        //
        HSSFSheet sheet = workbook.getSheetAt(0);

        //
        // When we have a sheet object in hand we can iterator on each
        // sheet's rows and on each row's cells. We store the data read
        // on an ArrayList so that we can printed the content of the excel
        // to the console.
        //
        Iterator rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = sheet.getRow(i);
            i++;

            List data = new ArrayList();
            if (row != null) {
                for (int j = 0; j < 8; j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        data.add(cell.toString().toUpperCase());
                    } else {
                        data.add("");
                    }
                }
            } else {
                break;
            }

            sheetData.add(data);
        }
        addRefDoctorAccountInDatabase(sheetData);
    }

    private void addDoctorMaster() throws IOException, SQLException {
        int i = 0;
        String filename = jtxtDatabaseLocation.getText();

        //
        // Create an ArrayList to store the data read from excel sheet.
        //
        List sheetData = new ArrayList();

        FileInputStream fis = null;
        //
        // Create a FileInputStream that will be use to read the excel file.
        //
        fis = new FileInputStream(filename);

        //
        // Create an excel workbook from the file system.
        //
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        //
        // Get the first sheet on the workbook.
        //
        HSSFSheet sheet = workbook.getSheetAt(0);

        //
        // When we have a sheet object in hand we can iterator on each
        // sheet's rows and on each row's cells. We store the data read
        // on an ArrayList so that we can printed the content of the excel
        // to the console.
        //
        Iterator rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = sheet.getRow(i);
            i++;

            List data = new ArrayList();
            if (row != null) {
                for (int j = 0; j < 3; j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        data.add(cell.toString().toUpperCase());
                    } else {
                        data.add("");
                    }
                }
            } else {
                break;
            }

            sheetData.add(data);
        }
        addDoctorInDatabase(sheetData);
    }

    private void addPatientMaster() throws IOException, SQLException {
        int i = 0;
        String filename = jtxtDatabaseLocation.getText();

        //
        // Create an ArrayList to store the data read from excel sheet.
        //
        List sheetData = new ArrayList();

        FileInputStream fis = null;
        //
        // Create a FileInputStream that will be use to read the excel file.
        //
        fis = new FileInputStream(filename);

        //
        // Create an excel workbook from the file system.
        //
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        //
        // Get the first sheet on the workbook.
        //
        HSSFSheet sheet = workbook.getSheetAt(0);

        //
        // When we have a sheet object in hand we can iterator on each
        // sheet's rows and on each row's cells. We store the data read
        // on an ArrayList so that we can printed the content of the excel
        // to the console.
        //
        Iterator rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = sheet.getRow(i);
            i++;

            List data = new ArrayList();
            if (row != null) {
                for (int j = 0; j < 11; j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        data.add(cell.toString().toUpperCase());
                    } else {
                        data.add("");
                    }
                }
            } else {
                break;
            }

            sheetData.add(data);
        }
        addPatientInDatabase(sheetData);
    }

    private void addAccountInDatabase(List sheetData) throws SQLException {
        //
        // Iterates the data and print it out to the console.
        //
        jProgressBar1.setMaximum(sheetData.size());
        jProgressBar1.setValue(0);
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            if (list.get(2).toString().equalsIgnoreCase("-20.0")) {
                jLabel1.setText(list.get(1).toString());
                String sql = "insert into acntmst (ac_alias,ac_name,grp_cd,user_id,ac_ref_no) values (?,?,?,?,?)";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                String alias = lb.generateKey("acntmst", "ac_alias", 5, "A");
                pstLocal.setString(1, alias);
                pstLocal.setString(2, list.get(1).toString());
                pstLocal.setString(3, "21");
                pstLocal.setInt(4, HMSHome.user_id);
                pstLocal.setString(5, list.get(0).toString());
                pstLocal.executeUpdate();
                String ac_cd = lb.getAcCode(alias, "AC");

                sql = "insert into adbkmst (ac_cd,add1,add2,add3,city_cd,area_cd) values(?,?,?,?,0,0)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ac_cd);
                pstLocal.setString(2, list.get(3).toString());
                pstLocal.setString(3, list.get(4).toString());
                pstLocal.setString(4, list.get(5).toString());
                pstLocal.executeUpdate();

                sql = "insert into phbkmst (ac_cd,ll_no,mobile1,mobile2,fax,email1,email2) values (?,?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ac_cd);
                pstLocal.setString(2, "");
                pstLocal.setString(3, list.get(6).toString());
                pstLocal.setString(4, list.get(7).toString());
                pstLocal.setString(5, "");
                pstLocal.setString(6, list.get(8).toString());
                pstLocal.setString(7, "");
                pstLocal.executeUpdate();

            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) + "/" + sheetData.size());
        }
    }

    private void addRefDoctorAccountInDatabase(List sheetData) throws SQLException {
        //
        // Iterates the data and print it out to the console.
        //
        jProgressBar1.setMaximum(sheetData.size());
        jProgressBar1.setValue(0);
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            {
                jLabel1.setText(list.get(1).toString());
                String sql = "insert into acntmst (ac_alias,ac_name,grp_cd,user_id,ac_ref_no) values (?,?,?,?,?)";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                String alias = lb.generateKey("acntmst", "ac_alias", 5, "A");
                pstLocal.setString(1, alias);
                pstLocal.setString(2, list.get(1).toString());
                pstLocal.setString(3, "20");
                pstLocal.setInt(4, HMSHome.user_id);
                pstLocal.setString(5, list.get(0).toString());
                pstLocal.executeUpdate();
                String ac_cd = lb.getAcCode(alias, "AC");

                sql = "insert into adbkmst (ac_cd,add1,add2,add3,city_cd,area_cd) values(?,?,?,?,?,0)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ac_cd);
                pstLocal.setString(2, list.get(2).toString());
                pstLocal.setString(3, list.get(3).toString());
                pstLocal.setString(4, "");
                if (!lb.getData("city_cd", "citymst", "city_ref_no", list.get(4).toString(), 1).equalsIgnoreCase("")) {
                    pstLocal.setString(5, lb.getData("city_cd", "citymst", "city_ref_no", list.get(4).toString(), 1));
                } else {
                    pstLocal.setInt(5, 0);
                }
                pstLocal.executeUpdate();

                sql = "insert into phbkmst (ac_cd,ll_no,mobile1,mobile2,fax,email1,email2) values (?,?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ac_cd);
                pstLocal.setString(2, "");
                pstLocal.setString(3, list.get(5).toString());
                pstLocal.setString(4, list.get(7).toString());
                pstLocal.setString(5, "");
                pstLocal.setString(6, list.get(6).toString());
                pstLocal.setString(7, "");
                pstLocal.executeUpdate();

                sql = "insert into doctormaster (ac_cd,spec_cd,user_id) values (?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ac_cd);
                pstLocal.setString(2, "14");
                pstLocal.setInt(3, HMSHome.user_id);
                pstLocal.executeUpdate();
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) + "/" + sheetData.size());
        }
    }

    private void addDoctorInDatabase(List sheetData) throws SQLException {
        //
        // Iterates the data and print it out to the console.
        //
        jProgressBar1.setMaximum(sheetData.size());
        jProgressBar1.setValue(0);
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            jLabel1.setText(list.get(1).toString());
            String sql = "insert into doctormaster (ac_cd,spec_cd,user_id) values (?,?,?)";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, lb.getData("ac_cd", "acntmst", "ac_ref_no", list.get(0).toString(), 1));
            pstLocal.setString(2, lb.getData("speciality_cd", "specialitymst", "spec_ref_no", list.get(2).toString(), 1));
            pstLocal.setInt(3, HMSHome.user_id);
            if (!lb.getData("ac_cd", "acntmst", "ac_ref_no", list.get(0).toString(), 1).equalsIgnoreCase("")) {
                pstLocal.executeUpdate();
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) + "/" + sheetData.size());
        }
    }

    private void addPatientInDatabase(List sheetData) throws SQLException {
        //
        // Iterates the data and print it out to the console.
        //
        jProgressBar1.setMaximum(sheetData.size());
        jProgressBar1.setValue(0);
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            jLabel1.setText(list.get(1).toString());
            String sql = "insert into patientmst (opd_no,pt_name,dob,sex,first_visit_date,ref_by,con_doc,user_id,rec_no) values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, list.get(0).toString());
            pstLocal.setString(2, list.get(1).toString());
            if (!lb.ConvertDateFormetForDBAccess(list.get(2).toString()).equalsIgnoreCase("")) {
                pstLocal.setString(3, lb.ConvertDateFormetForDBAccess(list.get(2).toString()));
            } else {
                pstLocal.setString(3, null);
            }
            if (list.get(3).toString().equalsIgnoreCase("M")) {
                pstLocal.setInt(4, 0);
            } else {
                pstLocal.setInt(4, 1);
            }
            if (!lb.ConvertDateFormetForDBAccess(list.get(4).toString()).equalsIgnoreCase("")) {
                pstLocal.setString(5, lb.ConvertDateFormetForDBAccess(list.get(4).toString()));
            } else {
                pstLocal.setString(5, null);
            }
            pstLocal.setInt(6, 1);
            pstLocal.setInt(7, 1);
            pstLocal.setInt(8, HMSHome.user_id);
            pstLocal.setString(9, list.get(10).toString());
            pstLocal.executeUpdate();

            sql = "insert into patientinfomst (opd_no,address,city_cd,area_cd,pincode,telephone,mobile,alt_mobile,email) values (?,?,?,?,?,?,?,?,?)";
            pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, list.get(0).toString());
            pstLocal.setString(2, list.get(5).toString());
            pstLocal.setInt(3, 33);
            pstLocal.setInt(4, 3);
            pstLocal.setString(5, "");
            pstLocal.setString(6, list.get(7).toString());
            pstLocal.setString(7, list.get(8).toString());
            pstLocal.setString(8, "");
            pstLocal.setString(9, "");
            pstLocal.executeUpdate();

            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) + "/" + sheetData.size());
        }
    }

    private void addBillItemMasterINDatabase(List sheetData) throws SQLException {
        //
        // Iterates the data and print it out to the console.
        //
        jProgressBar1.setMaximum(sheetData.size());
        jProgressBar1.setValue(0);
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            jLabel1.setText(list.get(1).toString());
            String sql = "insert into billitemmst (bill_item_name,bill_grp_cd,third_party,user_id,def_rate) values (?,?,?,?,?)";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, list.get(1).toString());
            pstLocal.setString(2, lb.getbillGrpCode(list.get(0).toString(), "C"));
            pstLocal.setString(3, "1");
            pstLocal.setInt(4, HMSHome.user_id);
            pstLocal.setDouble(5, lb.isNumber(list.get(2).toString()));
            pstLocal.executeUpdate();
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) + "/" + sheetData.size());
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

        cancelButton = new javax.swing.JButton();
        jbtnBrowse = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jtxtDatabaseLocation = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jbtnBrowse.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jbtnBrowse.setText("Browse");
        jbtnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBrowseActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("File Location");

        jtxtDatabaseLocation.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtDatabaseLocation.setEnabled(false);

        jButton1.setText("Upload");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jProgressBar1.setStringPainted(true);

        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Patient", "Bill Item Master" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDatabaseLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnBrowse)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnBrowse)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDatabaseLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jbtnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBrowseActionPerformed
        // TODO add your handling code here:
        final JFileChooser jfc = new JFileChooser(hms.HMS101.currentDirectory);

        jfc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equalsIgnoreCase("ApproveSelection")) {
                    jtxtDatabaseLocation.setText(jfc.getSelectedFile().getAbsolutePath().toString());
                }
            }
        });

        jfc.setCurrentDirectory(new File(hms.HMS101.currentDirectory));
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle("Select uploaded file");
        jfc.setApproveButtonText("Select");
        jfc.showOpenDialog(this);

    }//GEN-LAST:event_jbtnBrowseActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        SwingWorker workerForjbtnGenerate = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                jButton1.setEnabled(false);
                cancelButton.setEnabled(false);
                jComboBox1.setEnabled(false);
                main();
                jButton1.setEnabled(true);
                cancelButton.setEnabled(true);
                jComboBox1.setEnabled(true);
                jComboBox1.setSelectedIndex(0);
                return null;
            }
        };
        workerForjbtnGenerate.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JButton jbtnBrowse;
    private javax.swing.JTextField jtxtDatabaseLocation;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
