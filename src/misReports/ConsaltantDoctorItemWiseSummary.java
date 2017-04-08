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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.Library;
import support.PickList;
import support.ReportPanel;

/**
 *
 * @author Lenovo
 */
public class ConsaltantDoctorItemWiseSummary extends javax.swing.JInternalFrame {

    Library lb = new Library();
    ReportPanel rp = null;
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    DefaultTableModel model = null;
    DefaultTableModel modelgroup = null;
    PickList acPickList = null;
    PickList billGroupList = null;
    int form_cd = -1;

    /**
     * Creates new form OPDPatientListDateWise
     */
    public ConsaltantDoctorItemWiseSummary(int form_cd) {
        initComponents();
        this.form_cd = form_cd;
        setPIckListView();
        addInitialData();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        dtm = (DefaultTableModel) jTable1.getModel();
        model = (DefaultTableModel) jTable2.getModel();
        modelgroup = (DefaultTableModel) jTable3.getModel();
        addReportPanel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
    }

    private void setPIckListView() {
        billGroupList = new PickList(dataConnection);
        billGroupList.setLayer(getLayeredPane());
        billGroupList.setPickListComponent(jtxtItemGrpName1);
        billGroupList.setReturnComponent(new JTextField[]{jtxtItemGrpName1});
        billGroupList.setNextComponent(jtxtItemGrpName1);
    }

    private void addInitialData() {
        try {
            String sql = "select distinct(id) from conttemp";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            jcmbOldContract.addItem("");
            while (rsLocal.next()) {
                jcmbOldContract.addItem(rsLocal.getInt(1));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at addInitial Data ad OPDBillGeneration", ex);
        }
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
        dtm.addRow(row);
    }

    private void addTotal(int startIndex) {
        addBlankRow();
        double discount = 0.00;
        double total = 0.00;
        double advance = 0.00;
        int qty = 0;
        for (int i = startIndex; i < jTable1.getRowCount(); i++) {
            qty += (int) lb.isNumber(jTable1.getValueAt(i, 3).toString());
            total += lb.isNumber(jTable1.getValueAt(i, 5).toString());
            advance += lb.isNumber(jTable1.getValueAt(i, 6).toString());
            discount += lb.isNumber(jTable1.getValueAt(i, 7).toString());
        }

        Vector row = new Vector();
        row.add("");
        row.add("Total ");
        row.add("");
        row.add(qty);
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
                    dtm.setRowCount(0);
                    int startIndex = 0;
                    if (jRadioButton1.isSelected() || jRadioButton3.isSelected()) {
                        String sql = "SELECT b.bill_item_cd,p.pt_name,i1.ref_no,b.bill_item_name,i1.v_date,i1.rate,SUM(i1.qty) as qty,SUM(i1.amt) as amt, "
                                + "SUM(i1.disc) as disc, SUM(i1.final_amt) as final_amt,a.ac_name,i1.doc_cd FROM ipdreg i LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no "
                                + " LEFT JOIN patientmst p ON i.opd_no=p.opd_no LEFT JOIN billitemmst b ON i1.bill_item_cd=b.bill_item_cd"
                                + " LEFT JOIN acntmst a ON i1.doc_cd=a.ac_cd WHERE i1.is_del=0  and "
                                + " i.dis_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                                + " i.dis_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' ";
                        sql += " and i1.doc_cd in(";
                        int[] rows = jTable2.getSelectedRows();
                        for (int i = 0; i < rows.length; i++) {
                            sql += "" + lb.getAcCode(jTable2.getValueAt(rows[i], 0).toString(), "AC") + ",";
                        }
                        if (sql.endsWith(",")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        sql += ")";

                        sql += " and b.bill_item_cd  in(";
                        rows = jTable3.getSelectedRows();
                        for (int i = 0; i < rows.length; i++) {
                            sql += "" + lb.getbillitemCode(jTable3.getValueAt(rows[i], 0).toString(), "C") + ",";
                        }
                        if (sql.endsWith(",")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        sql += ")";
                        sql += " GROUP BY b.bill_item_name,i1.rate,i1.doc_cd,p.opd_no  order by i1.doc_cd,p.opd_no,i.dis_date,i1.v_date";
                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                        ResultSet rsLocal = pstLocal.executeQuery();
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
                                dtm.addRow(row);
                                addBlankRow();
                                startIndex = dtm.getRowCount();
                            }
                            Vector row = new Vector();
                            row.add(j + 1);
                            row.add(rsLocal.getString("pt_name"));
                            row.add(rsLocal.getString("bill_item_name"));
                            row.add((rsLocal.getInt("qty")));
                            row.add(rsLocal.getString("rate"));
                            row.add(lb.isNumber(rsLocal.getString("final_amt")));
                            double per = lb.isNumber(lb.getData("SELECT cont_per FROM contractmst WHERE "
                                    + "bill_item_cd = " + rsLocal.getString("bill_item_cd") + " AND cont_date > '" + lb.ConvertDateFormetForDB(rsLocal.getString("v_date")) + "'"
                                    + " and ac_cd=" + rsLocal.getString("doc_cd") + " ORDER BY cont_date"));
                            if (per == 0) {
                                per = lb.isNumber(lb.getData("SELECT cont_per FROM contractmst WHERE "
                                        + "bill_item_cd = " + rsLocal.getString("bill_item_cd") + " AND cont_date > '" + lb.ConvertDateFormetForDB(rsLocal.getString("v_date")) + "'"
                                        + " and ac_cd=" + rsLocal.getString("doc_cd") + " ORDER BY cont_date"));
                            }
                            row.add(per);
                            row.add(rsLocal.getDouble("final_amt") * per / 100);
                            dtm.addRow(row);
                            j++;
                        }
                    }
                    if (jRadioButton2.isSelected() || jRadioButton3.isSelected()) {
                        String sql = "SELECT i1.doc_cd,b.bill_item_cd,p.pt_name,i1.ref_no,b.bill_item_name,i.v_date,i1.rate,sum(i1.qty) as qty,sum(i1.amount) as amount,"
                                + " sum(i1.disc) as disc,sum(i1.final_amt) as final_amt,a.ac_name FROM opdbillhd i "
                                + " LEFT JOIN opdbilldt i1 ON i.ref_no=i1.ref_no "
                                + " LEFT JOIN patientmst p ON i.opd_no=p.opd_no LEFT JOIN billitemmst b ON i1.bill_item_cd=b.bill_item_cd"
                                + " LEFT JOIN acntmst a ON i1.doc_cd=a.ac_cd WHERE "
                                + " i.v_date >='" + lb.ConvertDateFormetForDB(jtxtFromDate.getText()) + "' and "
                                + " i.v_date <='" + lb.ConvertDateFormetForDB(jtxtToDate.getText()) + "' ";
                        sql += " and i1.doc_cd in(";
                        int[] rows = jTable2.getSelectedRows();
                        for (int i = 0; i < rows.length; i++) {
                            sql += "" + lb.getAcCode(jTable2.getValueAt(rows[i], 0).toString(), "AC") + ",";
                        }
                        if (sql.endsWith(",")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        sql += ")";

                        sql += " and b.bill_item_cd in(";
                        rows = jTable3.getSelectedRows();
                        for (int i = 0; i < rows.length; i++) {
                            sql += "" + lb.getbillitemCode(jTable3.getValueAt(rows[i], 0).toString(), "C") + ",";
                        }
                        if (sql.endsWith(",")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        sql += ")";
                        sql += " b.bill_item_name,i1.rate,i1.doc_cd,p.opd_no  order by i1.doc_cd,p.opd_no,i.v_date";
                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                        ResultSet rsLocal = pstLocal.executeQuery();
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
                                dtm.addRow(row);
                                addBlankRow();
                                startIndex = dtm.getRowCount();
                            }
                            Vector row = new Vector();
                            row.add(j + 1);
                            row.add(rsLocal.getString("pt_name"));
                            row.add(rsLocal.getString("bill_item_name"));
                            row.add((int)lb.isNumber(rsLocal.getString("qty")));
                            row.add(lb.isNumber(rsLocal.getString("rate")));
                            row.add(lb.isNumber(rsLocal.getString("final_amt")));
                            double per = lb.isNumber(lb.getData("SELECT cont_per FROM contractmst WHERE "
                                    + "bill_item_cd = " + rsLocal.getString("bill_item_cd") + " AND cont_date > '" + lb.ConvertDateFormetForDB(rsLocal.getString("v_date")) + "'"
                                    + " and ac_cd=" + rsLocal.getString("doc_cd") + " ORDER BY cont_date"));
                            if (per == 0) {
                                per = lb.isNumber(lb.getData("SELECT cont_per FROM contractmst WHERE "
                                        + "bill_item_cd = " + rsLocal.getString("bill_item_cd") + " AND cont_date > '" + lb.ConvertDateFormetForDB(rsLocal.getString("v_date")) + "'"
                                        + " and ac_cd=" + rsLocal.getString("doc_cd") + " ORDER BY cont_date"));
                            }
                            row.add(per);
                            row.add(rsLocal.getDouble("final_amt") * per / 100);
                            dtm.addRow(row);
                            j++;
                        }
                    }

                    addTotal(startIndex);
                    lb.setColumnSizeForTable(jTable1, jPanel3.getWidth());
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
                        rows.add(row);
                    }

                    ArrayList header = new ArrayList();
                    header.add("SR.No");
                    header.add("Patient Name");
                    header.add("Particular");
                    header.add("Qty");
                    header.add("Rate");
                    header.add("Amount");
                    header.add("Discount");
                    header.add("Net");
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
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jtxtItemGrpName1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jcmbOldContract = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jtxtToDate = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();

        jLabel4.setText("From Date");

        jLabel5.setText("To Date");

        jButton1.setText("Clear Doctor");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel19.setText("Consaltant Doc");

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
        jRadioButton1.setText("IPD");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("OPD");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Both");

        jLabel2.setText("Item  Name");

        jtxtItemGrpName1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtItemGrpName1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtItemGrpName1FocusLost(evt);
            }
        });
        jtxtItemGrpName1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtItemGrpName1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtItemGrpName1KeyReleased(evt);
            }
        });

        jButton2.setText("Clear Group");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Load Doctor");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jcmbOldContract.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcmbOldContractItemStateChanged(evt);
            }
        });

        jLabel1.setText("Contract Number");

        jtxtFromDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDateKeyPressed(evt);
            }
        });

        jtxtToDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtToDateKeyPressed(evt);
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
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtConsBy, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtConsAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtItemGrpName1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbOldContract, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton2)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtConsBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtConsAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtItemGrpName1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jcmbOldContract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton3)
                        .addComponent(jButton2))
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jRadioButton1, jRadioButton2, jRadioButton3});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtItemGrpName1});

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(265, 111));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Patient Name", "Particular", "Qty", "Rate", "Amount", "Per.", "Net"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

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

        jPanel6.setLayout(new java.awt.BorderLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable3KeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jPanel6.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                        .addGap(0, 0, 0))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
            PreparedStatement psLocal = dataConnection.prepareStatement("SELECT ac_name,ac_alias FROM acntmst "
                    + " WHERE ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '%" + jtxtConsBy.getText().toUpperCase() + "%'");
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

    private void jtxtItemGrpName1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemGrpName1FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtItemGrpName1FocusGained

    private void jtxtItemGrpName1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemGrpName1FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtItemGrpName1FocusLost

    private void jtxtItemGrpName1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemGrpName1KeyPressed
        // TODO add your handling code here:
        billGroupList.setLocation(jtxtItemGrpName1.getX() + jPanel2.getX(), jtxtItemGrpName1.getY() + jtxtItemGrpName1.getHeight() + jPanel2.getY());
        billGroupList.pickListKeyPress(evt);
        if (lb.isEnter(evt)) {
            if (!(lb.getbillitemCode(jtxtItemGrpName1.getText(), "C").equalsIgnoreCase("")
                    || lb.getbillitemCode(jtxtItemGrpName1.getText(), "C").equalsIgnoreCase("0"))) {
                Vector row = new Vector();
                row.add(jtxtItemGrpName1.getText());
                modelgroup.addRow(row);
                jtxtItemGrpName1.setText("");
            }
        }
    }//GEN-LAST:event_jtxtItemGrpName1KeyPressed

    private void jtxtItemGrpName1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemGrpName1KeyReleased
        // TODO add your handling code here:
        try {
            String sql = "SELECT bill_item_name FROM billitemmst"
                    + " WHERE bill_item_name LIKE '" + jtxtItemGrpName1.getText().toUpperCase() + "%'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            billGroupList.setPreparedStatement(pstLocal);
            billGroupList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtBillItemName", ex);
        }
    }//GEN-LAST:event_jtxtItemGrpName1KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        modelgroup.setRowCount(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable3KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int row = jTable3.getSelectedRow();
            if (row != -1) {
                lb.confirmDialog("Do you want to delete this item Group?");
                if (lb.type) {
                    modelgroup.removeRow(row);
                }
            }
        }
    }//GEN-LAST:event_jTable3KeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try {
            String sql = "";
            model.setRowCount(0);
            dtm.setRowCount(0);
            if (jRadioButton1.isSelected() || jRadioButton3.isSelected()) {
                sql = "SELECT distinct(ac_alias),ac_name FROM acntmst WHERE ac_cd IN (select doc_cd from ipdbilldt where is_del = 0 and ipd_no in (SELECT ipd_no FROM ipdreg WHERE dis_date >=? and dis_date <=?)) order by ac_name";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, lb.ConvertDateFormetForDB(jtxtFromDate.getText()));
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtToDate.getText()));
                ResultSet rsLocal = pstLocal.executeQuery();
                while (rsLocal.next()) {
                    Vector row = new Vector();
                    row.add(rsLocal.getString("ac_alias"));
                    row.add(rsLocal.getString("ac_name"));
                    model.addRow(row);
                }
            }
            if (jRadioButton2.isSelected() || jRadioButton3.isSelected()) {
                sql = "SELECT distinct(ac_alias),ac_name FROM acntmst WHERE ac_cd IN (SELECT o1.doc_cd FROM opdbillhd o left join opdbilldt o1 "
                        + " on o.ref_no=o1.ref_no WHERE v_date >=? and v_date <=? ) order by ac_name";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, lb.ConvertDateFormetForDB(jtxtFromDate.getText()));
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtToDate.getText()));
                ResultSet rsLocal = pstLocal.executeQuery();
                while (rsLocal.next()) {
                    Vector row = new Vector();
                    row.add(rsLocal.getString("ac_alias"));
                    row.add(rsLocal.getString("ac_name"));
                    model.addRow(row);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at load doctor", ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jcmbOldContractItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmbOldContractItemStateChanged
        // TODO add your handling code here:

        try {
            String sql = "SELECT * FROM billitemmst b WHERE "
                    + " b.bill_item_cd in (select bill_item_cd from conttemp where id=" + jcmbOldContract.getSelectedItem().toString() + ")";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            boolean flag = false;
            while (rsLocal.next()) {
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    if (jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(rsLocal.getString("bill_item_Cd"))) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    Vector row = new Vector();
                    row.add(rsLocal.getString("bill_item_name"));
                    row.add(0.00);
                    modelgroup.addRow(row);
                } else {
                    flag = false;
                }
            }
        } catch (SQLException ex) {
            lb.printToLogFile("Exception at create new contract", ex);
        }

    }//GEN-LAST:event_jcmbOldContractItemStateChanged

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtToDate);
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtToDateKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JComboBox jcmbOldContract;
    private javax.swing.JTextField jtxtConsAlias;
    private javax.swing.JTextField jtxtConsBy;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private javax.swing.JTextField jtxtItemGrpName1;
    private com.toedter.calendar.JDateChooser jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
