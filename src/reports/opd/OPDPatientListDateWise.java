/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports.opd;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.Library;
import support.PickList;
import support.ReportPanel;
import transaction.OPDBillGeneration;

/**
 *
 * @author Lenovo
 */
public class OPDPatientListDateWise extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    PickList acPickList = null;
    private int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public OPDPatientListDateWise(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        dtm = (DefaultTableModel) jTable1.getModel();
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
    }

    private void addReportPanel() {
        class reportPanel extends ReportPanel {

            @Override
            public void callView() {
                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT case when o.ref_no is null then '' else o.ref_no end as ref_no,a1.appoint_date,p1.pt_name,a.ac_name,"
                            + "  CASE WHEN a1.case_type = 0 THEN 'New Case'WHEN a1.case_type =1 THEN 'Follow Up' END AS case_type,p2.mobile,"
                            + "  a1.appoint_time, o.disc_amt FROM  appointmentmst a1"
                            + "  LEFT JOIN  opdbillhd o ON  o.appoint_no=a1.ref_no LEFT JOIN payment p ON o.ref_no=p.ref_no"
                            + "  LEFT JOIN acntmst a ON a1.cons_doc=a.ac_cd  LEFT JOIN patientmst p1 ON a1.opd_no=p1.opd_no "
                            + " left join patientinfomst p2 on p1.opd_no=p2.opd_no "
                            + "  where a1.appoint_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + "  a1.appoint_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' and o.ref_no <>'' ";
                    if (!jtxtDocAlias.getText().equalsIgnoreCase("")) {
                        sql += " and a1.cons_doc=" + lb.getAcCode(jtxtDocAlias.getText(), "AC");
                    }
                    if (jcmbPurpose.getSelectedIndex() != 0) {
                        sql += " and a1.case_type = " + (jcmbPurpose.getSelectedIndex() - 1);
                    }
                    sql += " order by o.v_date";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        row.add(rsLocal.getString("ref_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("appoint_date")));
                        row.add(rsLocal.getString("pt_name"));
                        row.add(rsLocal.getString("ac_name"));
                        row.add(rsLocal.getString("case_type"));
                        row.add(rsLocal.getString("mobile"));
                        row.add(rsLocal.getString("appoint_time"));
                        row.add(rsLocal.getString("disc_amt"));
                        dtm.addRow(row);
                    }
                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {

                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT a1.appoint_date,a1.appoint_time,p1.mobile as m1,p.pt_name,ac_name,b1.BRANCH_NAME, b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,"
                            + " b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO, CASE WHEN a1.case_type =0 THEN 'New Case' ELSE 'Follow Up' END AS case_type "
                            + " FROM  appointmentmst a1 LEFT JOIN acntmst a ON a1.cons_doc=a.ac_cd LEFT JOIN patientmst p ON a1.opd_no=p.opd_no "
                            + " LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1 "
                            + " where a1.appoint_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + " a1.appoint_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "'";
                    if (!jtxtDocAlias.getText().equalsIgnoreCase("")) {
                        sql += " and a1.cons_doc=" + lb.getAcCode(jtxtDocAlias.getText(), "AC");
                    }
                    if (jcmbPurpose.getSelectedIndex() != 0) {
                        sql += " and a1.case_type = " + (jcmbPurpose.getSelectedIndex() - 1);
                    }
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    HashMap params = new HashMap();
                    params.put("fromDate", jtxtFromDate.getText());
                    params.put("toDate", jtxtToDate.getText());
                    params.put("doctor", jtxtDoctor.getText());
                    params.put("caseType", jcmbPurpose.getSelectedItem().toString());
                    lb.reportGeneratorWord("OPDAppoinmentBook.jasper", params, rsLocal);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }
            }

            @Override
            public void callExcel() {

                try {
                    callView();
                    ArrayList rows = new ArrayList();
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        ArrayList row = new ArrayList();
                        row.add(jTable1.getValueAt(i, 0).toString());
                        row.add(jTable1.getValueAt(i, 1).toString());
                        row.add(jTable1.getValueAt(i, 2).toString());
                        row.add(jTable1.getValueAt(i, 3).toString());
                        row.add(jTable1.getValueAt(i, 4).toString());
                        row.add(jTable1.getValueAt(i, 5).toString());
                        row.add(jTable1.getValueAt(i, 6).toString());
                        rows.add(row);
                    }
                    ArrayList header = new ArrayList();
                    header.add("Receipt NO");
                    header.add("Date");
                    header.add("Patient Name");
                    header.add("Doctor Name");
                    header.add("Case Type");
                    header.add("Mobile");
                    header.add("Time");
                    lb.exportToExcel("OPD Patient List", header, rows, "OPD Patient List");
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void callPreview() {

                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT a1.appoint_date,a1.appoint_time,p1.mobile as m1,p.pt_name,ac_name,b1.BRANCH_NAME, b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,"
                            + " b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO, CASE WHEN a1.case_type =0 THEN 'New Case' ELSE 'Follow Up' END AS case_type "
                            + " FROM  appointmentmst a1 LEFT JOIN acntmst a ON a1.cons_doc=a.ac_cd LEFT JOIN patientmst p ON a1.opd_no=p.opd_no "
                            + " LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1 "
                            + " where a1.appoint_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                            + " a1.appoint_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "'";
                    if (!jtxtDocAlias.getText().equalsIgnoreCase("")) {
                        sql += " and a1.cons_doc=" + lb.getAcCode(jtxtDocAlias.getText(), "AC");
                    }
                    if (jcmbPurpose.getSelectedIndex() != 0) {
                        sql += " and a1.case_type = " + (jcmbPurpose.getSelectedIndex() - 1);
                    }
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    HashMap params = new HashMap();
                    params.put("fromDate", jtxtFromDate.getText());
                    params.put("toDate", jtxtToDate.getText());
                    params.put("doctor", jtxtDoctor.getText());
                    params.put("caseType", jcmbPurpose.getSelectedItem().toString());
                    lb.reportGenerator("OPDAppoinmentBook.jasper", params, rsLocal, jPanel3);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }
            }
        }

        rp = new reportPanel();
        jPanel1.add(rp);
        rp.setVisible(true);
    }

    private void close() {
        this.dispose();
    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jcmbPurpose = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jtxtDoctor = new javax.swing.JTextField();
        jtxtDocAlias = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jtxtToDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        jLabel20.setText("Case Type");

        jcmbPurpose.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "New Case", "Follow Up" }));
        jcmbPurpose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbPurposeKeyPressed(evt);
            }
        });

        jLabel7.setText("Doctor Name");

        jtxtDoctor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDoctorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDoctorFocusLost(evt);
            }
        });
        jtxtDoctor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDoctorKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtDoctorKeyReleased(evt);
            }
        });

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jcmbPurpose, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jcmbPurpose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel20, jcmbPurpose});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jLabel5, jtxtFromDate, jtxtToDate});

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(265, 111));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Receipt No", "Date", "Patient Name", "Doctor Name", "Case Type", "Mobile", "Time", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcmbPurposeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbPurposeKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            rp.requestFocusInWindow();
        }
    }//GEN-LAST:event_jcmbPurposeKeyPressed

    private void jtxtDoctorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDoctorFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDoctorFocusGained

    private void jtxtDoctorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDoctorFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtDoctorFocusLost

    private void jtxtDoctorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorKeyPressed
        acPickList.setPickListComponent(jtxtDoctor);
        acPickList.setNextComponent(rp);
        acPickList.setLocation(jtxtDoctor.getX() + jPanel2.getX(), jtxtDoctor.getY() + jtxtDoctor.getHeight() + jPanel2.getY());
        acPickList.setReturnComponent(new JTextField[]{jtxtDoctor, jtxtDocAlias});
        acPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtDoctorKeyPressed

    private void jtxtDoctorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("SELECT ac_name,ac_alias,s.spec_sub_name FROM acntmst a "
                    + " LEFT JOIN doctormaster d ON a.ac_cd=d.ac_cd LEFT JOIN specsubmst s ON d.sub_spec_cd=s.spec_sub_cd "
                    + " WHERE a.ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '" + jtxtDoctor.getText().toUpperCase() + "%'");
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1, 2});
            acPickList.setSecondAssociation(new int[]{0, 1, 2});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtDoctorKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jtxtDocAlias.setText("");
        jtxtDoctor.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 2){
            int row = jTable1.getSelectedRow();
            if(row != -1){
                if(!jTable1.getValueAt(row, 0).toString().equalsIgnoreCase("") && jTable1.getValueAt(row, 0).toString().startsWith("OP")){
                    OPDBillGeneration opdbill= new OPDBillGeneration("OP");
                    opdbill.setID(jTable1.getValueAt(row, 0).toString());
                    HMSHome.addOnScreen(opdbill, "OPD Bill Generataion", 24);
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox jcmbPurpose;
    private javax.swing.JTextField jtxtDocAlias;
    private javax.swing.JTextField jtxtDoctor;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private com.toedter.calendar.JDateChooser jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
