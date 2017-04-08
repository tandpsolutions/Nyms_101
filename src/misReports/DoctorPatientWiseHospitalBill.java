/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import support.Library;
import support.PickList;
import support.ReportPanel;
import transaction.IPDBillGenerationDischarge;

/**
 *
 * @author Lenovo
 */
public class DoctorPatientWiseHospitalBill extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    DefaultTableModel model = null;
    PickList acPickList = null;
    int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public DoctorPatientWiseHospitalBill(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        addJlabelTotalAmt();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        dtm = (DefaultTableModel) jTable1.getModel();
        model = (DefaultTableModel) jTable2.getModel();
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
    }

    private void addJlabelTotalAmt() {
        jPanel5.removeAll();
        jlblTotBill.setVisible(false);
        jlblTotCash.setVisible(false);
        jlblTotCheque.setVisible(false);

        jlblTotBill.setBounds(0, 0, 20, 20);
        jlblTotBill.setVisible(true);
        jPanel5.add(jlblTotBill);

        jlblTotCash.setBounds(0, 0, 20, 20);
        jlblTotCash.setVisible(true);
        jPanel5.add(jlblTotCash);

        jlblTotCheque.setBounds(0, 0, 20, 20);
        jlblTotCheque.setVisible(true);
        jPanel5.add(jlblTotCheque);

        setTable();
    }

    private void setTable() {
        jPanel5.setVisible(false);
        lb.setTable(jTable1, new JComponent[]{null, null, null, null, null, null, null, jlblTotBill, jlblTotCash, jlblTotCheque});
    }

    private void setTotal() {
        double discount = 0.00;
        double total = 0.00;
        double advance = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            total += lb.isNumber(jTable1.getValueAt(i, 7).toString());
            advance += lb.isNumber(jTable1.getValueAt(i, 8).toString());
            discount += lb.isNumber(jTable1.getValueAt(i, 9).toString());
        }
        jlblTotBill.setText(lb.Convert2DecFmtForRs(total));
        jlblTotCash.setText(lb.Convert2DecFmtForRs(advance));
        jlblTotCheque.setText(lb.Convert2DecFmtForRs(discount));
    }

    private void addBlankRow() {
        Vector row = new Vector();
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        row.add("  ");
        dtm.addRow(row);
    }

    private void addTotal(int startIndex) {
        addBlankRow();
        double discount = 0.00;
        double total = 0.00;
        double advance = 0.00;
        for (int i = startIndex; i < jTable1.getRowCount(); i++) {
            total += lb.isNumber(jTable1.getValueAt(i, 7).toString());
            advance += lb.isNumber(jTable1.getValueAt(i, 8).toString());
            discount += lb.isNumber(jTable1.getValueAt(i, 9).toString());
        }

        Vector row = new Vector();
        row.add("");
        row.add("Total ");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add(total);
        row.add(advance);
        row.add(discount);
        dtm.addRow(row);

    }

    private void addReportPanel() {
        class reportPanel extends ReportPanel {

            @Override
            public void callView() {
                String ac_name = "";
                try {
                    jPanel3.removeAll();
                    jPanel3.add(jScrollPane1);
                    String sql = "";
                    int startIndex = 0;
                    if (jRadioButton1.isSelected()) {
                        sql = "SELECT i.opd_no,i.ipd_no,p.pt_name,i.ref_no,i.admit_date,i.dis_date,a.ac_name, SUM(i1.final_amt) AS total_bill,"
                                + "(SELECT SUM(final_amt) FROM ipdbilldt WHERE bill_item_cd IN (SELECT bill_item_cd FROM conttemp WHERE id=2) "
                                + " AND ipd_no=i.ipd_no) AS doc_bill  FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no"
                                + " LEFT JOIN acntmst a ON i.doc_cd=a.ac_cd  "
                                + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no "
                                + " WHERE i.dis_date IS NOT NULL  AND  i.dis_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                                + " i.dis_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "'  and ref_opd_no ='' ";
                        sql += " and i.doc_cd in(";
                        int[] array = jTable2.getSelectedRows();
                        for (int i = 0; i < array.length; i++) {
                            sql += "" + lb.getAcCode(jTable2.getValueAt(array[i], 0).toString(), "AC") + ",";
                        }
                        if (sql.endsWith(",")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        sql += ")";
                    } else if (jRadioButton2.isSelected()) {
                        sql = "SELECT i.opd_no,i.ipd_no,p.pt_name,i.ref_no,i.admit_date,i.dis_date,a.ac_name, SUM(i1.final_amt) AS total_bill,"
                                + "(SELECT SUM(final_amt) FROM ipdbilldt WHERE bill_item_cd IN (SELECT bill_item_cd FROM conttemp WHERE id=2) "
                                + " AND ipd_no=i.ipd_no) AS doc_bill  FROM ipdreg i LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
                                + " LEFT JOIN acntmst a ON i.doc_cd=a.ac_cd  "
                                + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no "
                                + " WHERE i.dis_date IS NOT NULL  AND  i.dis_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                                + " i.dis_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "'  and ref_opd_no ='' ";
                        sql += " and p.ref_by in(";
                        int[] array = jTable2.getSelectedRows();
                        for (int i = 0; i < array.length; i++) {
                            sql += "" + lb.getAcCode(jTable2.getValueAt(array[i], 0).toString(), "AC") + ",";
                        }
                        if (sql.endsWith(",")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        sql += ")";
                    }
                    sql += "  GROUP BY i.ipd_no order by a.ac_cd,i.dis_date";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    dtm.setRowCount(0);
                    int j = 0;
                    while (rsLocal.next()) {

                        if (ac_name.equalsIgnoreCase("") || !ac_name.equalsIgnoreCase(rsLocal.getString("ac_name"))) {
                            j = 0;
                            if (!ac_name.equalsIgnoreCase("")) {
                                addTotal(startIndex);
                            }
                            addBlankRow();
                            Vector row = new Vector();
                            ac_name = rsLocal.getString("ac_name");
                            row.add("");
                            row.add(rsLocal.getString("ac_name"));
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                            dtm.addRow(row);
                            addBlankRow();
                            startIndex = dtm.getRowCount();
                        }
                        Vector row = new Vector();
                        row.add(j + 1);
                        row.add(rsLocal.getString("pt_name"));
                        row.add(rsLocal.getString("ref_no"));
                        row.add(rsLocal.getString("opd_no"));
                        row.add(rsLocal.getString("ipd_no"));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("admit_date")));
                        row.add(lb.ConvertDateFormetForDisply(rsLocal.getString("dis_date")));
                        row.add(lb.isNumber(rsLocal.getString("total_bill")));
                        row.add(lb.isNumber(rsLocal.getString("total_bill")) - lb.isNumber(rsLocal.getString("doc_bill")));
                        row.add(lb.isNumber(rsLocal.getString("doc_bill")));
                        dtm.addRow(row);
                        j++;
                    }
                    addTotal(startIndex);
                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
                    setTable();
                    setTotal();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
                }

            }

            @Override
            public void callPrint() {
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
                        row.add(jTable1.getValueAt(i, 7).toString());
                        row.add(jTable1.getValueAt(i, 8).toString());
                        row.add(jTable1.getValueAt(i, 9).toString());
                        rows.add(row);
                    }

                    ArrayList header = new ArrayList();
                    header.add("SR.No");
                    header.add("Patient Name");
                    header.add("Voucher Number");
                    header.add("OPD NUmber");
                    header.add("IPD Number");
                    header.add("D.O.A");
                    header.add("D.O.D");
                    header.add("Total Bill");
                    header.add("Advance");
                    header.add("Discount");
                    lb.exportToExcel("Patient Bill", header, rows, "Patient Bill");
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at generate Excep in DoctorPatientWiseTotal Bill", ex);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jtxtConsBy = new javax.swing.JTextField();
        jtxtConsAlias = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton2 = new javax.swing.JButton();
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jtxtToDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jlblTotBill = new javax.swing.JLabel();
        jlblTotCash = new javax.swing.JLabel();
        jlblTotCheque = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        jButton1.setText("Clear All");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel19.setText("Doctor");

        jtxtConsBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtConsByFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtConsByFocusLost(evt);
            }
        });
        jtxtConsBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtConsByKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtConsByKeyReleased(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Treating Docotor");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Reference Docotor");

        jButton2.setText("Load Doctor");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                .addComponent(jButton2))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jRadioButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton2))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jtxtConsBy, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtConsAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 6, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton2)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtConsBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtConsAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
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
                "Sr No", "Patient Name", "Voucher #", "OPD No", "IPD No", "D.O.A", "D.O.D", "Total Bill", "Hospital Bill", "Doctor's Bill"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
        }

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jlblTotBill.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblTotCash.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblTotCheque.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(156, 156, 156)
                .addComponent(jlblTotBill, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jlblTotCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlblTotCash, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTotCash, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotBill, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Alias", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(0);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
        }

        jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        model.setRowCount(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jtxtConsByFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtConsByFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtConsByFocusGained

    private void jtxtConsByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtConsByFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtConsByFocusLost

    private void jtxtConsByKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtConsByKeyPressed
        // TODO add your handling code here:
        acPickList.setLocation(jtxtConsBy.getX() + jPanel2.getX(), jtxtConsBy.getY() + jtxtConsBy.getHeight() + jPanel2.getHeight());
        acPickList.setPickListComponent(jtxtConsBy);
        acPickList.setNextComponent(jtxtConsBy);
        acPickList.setReturnComponent(new JTextField[]{jtxtConsBy, jtxtConsAlias});
        acPickList.pickListKeyPress(evt);
        if (lb.isEnter(evt)) {
            if (!(lb.getAcCode(jtxtConsAlias.getText(), "AC").equalsIgnoreCase("")
                    || lb.getAcCode(jtxtConsAlias.getText(), "AC").equalsIgnoreCase("0"))) {
                Vector row = new Vector();
                row.add(jtxtConsAlias.getText());
                row.add(jtxtConsBy.getText());
                model.addRow(row);
                jtxtConsAlias.setText("");
                jtxtConsBy.setText("");
            }
        }
    }//GEN-LAST:event_jtxtConsByKeyPressed

    private void jtxtConsByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtConsByKeyReleased
        // TODO add your handling code here:
        try {
            String sql = "SELECT ac_name,ac_alias FROM acntmst "
                    + " WHERE ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '%" + jtxtConsBy.getText().toUpperCase() + "%'";
            sql += " and is_star=1";
            PreparedStatement psLocal = dataConnection.prepareStatement(sql);
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1});
            acPickList.setSecondAssociation(new int[]{0, 1});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtConsByKeyReleased

    private void jTable2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                lb.confirmDialog("Do you want to delete this doctor?");
                if (lb.type) {
                    model.removeRow(row);
                }
            }
        }
    }//GEN-LAST:event_jTable2KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try {
            String sql = "";
            if (jRadioButton1.isSelected()) {
                sql = "SELECT distinct(ac_alias),ac_name FROM acntmst WHERE ac_cd IN (SELECT doc_cd FROM ipdreg WHERE dis_date >=? and dis_date <=?) order by ac_name";
            } else {
                sql = "SELECT distinct(ac_alias),ac_name FROM acntmst a LEFT JOIN patientmst p ON a.ac_cd = p.ref_by "
                        + " LEFT JOIN ipdreg i ON i.opd_no=p.opd_no WHERE ac_alias <>'A0001' and i.dis_date >=? AND i.dis_date <=? order by ac_name";
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, lb.ConvertDateFormetForDB(jtxtFromDate.getText()));
            pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtToDate.getText()));
            ResultSet rsLocal = pstLocal.executeQuery();
            dtm.setRowCount(0);
            model.setRowCount(0);
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add(rsLocal.getString("ac_alias"));
                row.add(rsLocal.getString("ac_name"));
                model.addRow(row);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at load doctor", ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String ipd = jTable1.getValueAt(row, 2).toString();
                IPDBillGenerationDischarge dis = new IPDBillGenerationDischarge();
                dis.setID(ipd);
                HMSHome.addOnScreen(dis, "IPD Billing and Discharge", 210);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel jlblTotBill;
    private javax.swing.JLabel jlblTotCash;
    private javax.swing.JLabel jlblTotCheque;
    private javax.swing.JTextField jtxtConsAlias;
    private javax.swing.JTextField jtxtConsBy;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private com.toedter.calendar.JDateChooser jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
