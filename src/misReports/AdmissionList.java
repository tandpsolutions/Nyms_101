/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMS101;
import hms.HMSHome;
import static hms.HMSHome.role;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import support.Library;
import support.ReportPanel;
import transaction.IPDBillGenerationDischarge;
import transaction.IPDDayWiseBilling;

/**
 *
 * @author Lenovo
 */
public class AdmissionList extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public AdmissionList(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        dtm = (DefaultTableModel) jTable1.getModel();
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
        searchOnTextFields();
    }

    private void searchOnTextFields() {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jPanel4.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel4.add(jtfFilter, BorderLayout.CENTER);

//        setLayout(new BorderLayout());
//        add(panel, BorderLayout.SOUTH);
//        add(new JScrollPane(jTable1), BorderLayout.CENTER);
        jtfFilter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    private void addReportPanel() {
        class reportPanel extends ReportPanel {

            @Override
            public void callView() {
                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "SELECT i.opd_no,i.ipd_no,p.pt_name,i.admit_date,i.admit_time,i.dis_date,i.dis_time,\n"
                            + "a.ac_name as ref,a1.ac_name as cons,p1.address,p1.mobile,i.discharge_type,i.is_medi FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no \n"
                            + " LEFT JOIN acntmst a ON p.ref_by=a.ac_cd  \n"
                            + " LEFT JOIN acntmst a1 ON p.con_doc = a1.ac_cd \n"
                            + " left join patientinfomst p1 on p.opd_no=p1.opd_no where "
                            + " i.admit_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "'"
                            + " and i.admit_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "'  "
                            + " order by admit_date";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    while (rsLocal.next()) {
                        Vector row = new Vector();
                        row.add(jTable1.getRowCount() + 1);
                        row.add(rsLocal.getString("opd_no"));
                        row.add(rsLocal.getString("ipd_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("admit_date")));
                        row.add((rsLocal.getString("admit_time")));
                        row.add(rsLocal.getString("pt_name"));
                        row.add(rsLocal.getString("address"));
                        row.add(rsLocal.getString("mobile"));
                        row.add(rsLocal.getString("cons"));
                        row.add(rsLocal.getString("ref"));
                        row.add((rsLocal.getString("dis_date") == null) ? "" : lb.ConvertDateFormetForDisply(rsLocal.getString("dis_date")));
                        row.add((rsLocal.getString("dis_date") == null) ? "" : (rsLocal.getString("dis_time")));
                        row.add((rsLocal.getString("dis_date") == null) ? "Not Discharged" : (rsLocal.getInt("discharge_type") == 0) ? "Discharge" : (rsLocal.getInt("discharge_type") == 1 ? "DAMA" : "DEATH"));
                        row.add((rsLocal.getInt("is_medi") == 0) ? "" : "Mediclaim");
                        dtm.addRow(row);
                    }
                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
                } catch (Exception ex) {
                    dtm.setRowCount(0);
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {

            }

            @Override
            public void callExcel() {
                int i = 0;
                try {
                    callView();
                    ArrayList rows = new ArrayList();
                    for (i = 0; i < jTable1.getRowCount(); i++) {
                        ArrayList row = new ArrayList();
                        row.add(jTable1.getValueAt(i, 0).toString());
                        row.add(jTable1.getValueAt(i, 1).toString());
                        row.add(jTable1.getValueAt(i, 2).toString());
                        row.add(jTable1.getValueAt(i, 3).toString());
                        row.add(jTable1.getValueAt(i, 4).toString());
                        row.add(jTable1.getValueAt(i, 5).toString());
                        row.add(jTable1.getValueAt(i, 6).toString());
                        row.add(jTable1.getValueAt(i, 7).toString());
                        row.add(jTable1.getValueAt(i, 8).toString());
                        row.add(jTable1.getValueAt(i, 9).toString());
                        row.add(jTable1.getValueAt(i, 10).toString());
                        row.add(jTable1.getValueAt(i, 11).toString());
                        row.add(jTable1.getValueAt(i, 12).toString());
                        row.add(jTable1.getValueAt(i, 13).toString());
                        rows.add(row);
                    }
                    ArrayList header = new ArrayList();
                    header.add("Sr.No");
                    header.add("OPD Number");
                    header.add("IPD Number");
                    header.add("Admit Date");
                    header.add("Admit Time");
                    header.add("Patient Name");
                    header.add("Address");
                    header.add("Phone");
                    header.add("Consoltant Doctor");
                    header.add("Reference Doctor");
                    header.add("Discharge Date");
                    header.add("Discharge Time");
                    header.add("Type");
                    header.add("Mediclaim");
                    lb.exportToExcel("Admit List", header, rows, "Admit List");
                } catch (Exception ex) {
                    dtm.setRowCount(0);
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void callPreview() {
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
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jtxtToDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(265, 111));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SR No", "OPD No", "IPD No", "D.O.A", "Time", "Name", "Address", "Phone", "Consaltant", "Reference", "D.O.D", "Time", "Type", "Mediclaim"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        jPanel4.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String ipd1 = jTable1.getValueAt(row, 3).toString();
                IPDBillGenerationDischarge ipd = new IPDBillGenerationDischarge();
                ipd.setID(lb.getData("ref_no", "ipdreg", "ipd_no", ipd1, 0));
                HMSHome.addOnScreen(ipd, "IPD Billing and Discharge", 210);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private boolean checkRight(int form_cd) {
        if (role != 1) {
            return lb.getRight(form_cd + "", "VIEWS");
        } else {
            return true;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private com.toedter.calendar.JDateChooser jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
