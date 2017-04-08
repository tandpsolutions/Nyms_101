/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misReports;

import hms.HMSHome;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import reportDAo.DailyOPDBillDAO;
import support.Library;
import transaction.OPDBillGeneration;

/**
 *
 * @author Bhaumik
 */
public class OPDBillDetail extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    JDialog jd = null;
    String ref_no = "";
    private JTextField jtfFilter = new JTextField();
    private TableRowSorter<TableModel> rowSorter;

    /**
     * Creates new form SearchPatient
     */
    public OPDBillDetail(JDialog jd, int mode, String from, String to, String grp, boolean total) {
        initComponents();
        this.jd = jd;
        dtm = (DefaultTableModel) jTable2.getModel();
        updateIPDList(mode, from, to, grp, total
        );
        searchOnTextFieldsOPD();
    }

    public OPDBillDetail(JDialog jd) {
        initComponents();
        this.jd = jd;
        dtm = (DefaultTableModel) jTable2.getModel();
        searchOnTextFieldsOPD();
    }

    private void searchOnTextFieldsOPD() {
        this.rowSorter = new TableRowSorter<>(jTable2.getModel());
        jTable2.setRowSorter(rowSorter);
        jPanel1.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel1.add(jtfFilter, BorderLayout.CENTER);
        JButton excel = new JButton("Excel");
        JButton Print = new JButton("Print");
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(excel);
        panel.add(Print);
        jPanel1.add(panel, BorderLayout.EAST);
        Print.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OPDBillDetail.this.dispose();
                ArrayList<DailyOPDBillDAO> rows = new ArrayList<DailyOPDBillDAO>();
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    DailyOPDBillDAO row = new DailyOPDBillDAO();
                    row.setSr_no(jTable2.getValueAt(i, 0) + "");
                    row.setRef_no(jTable2.getValueAt(i, 1) + "");
                    row.setV_date(jTable2.getValueAt(i, 2) + "");
                    row.setPt_name(jTable2.getValueAt(i, 3) + "");
                    row.setParticular(jTable2.getValueAt(i, 4) + "");
                    row.setTot_amt(jTable2.getValueAt(i, 5) + "");
                    row.setAc_name(jTable2.getValueAt(i, 6) + "");
                    row.setUser(jTable2.getValueAt(i, 7) + "");
                    rows.add(row);
                }
                lb.reportGeneratorWord("DailyOPDBillDetail.jasper", null, rows);
            }
        });

        excel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OPDBillDetail.this.dispose();
                ArrayList rows = new ArrayList();
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    ArrayList row = new ArrayList();
                    row.add(jTable2.getValueAt(i, 0).toString());
                    row.add(jTable2.getValueAt(i, 1).toString());
                    row.add(jTable2.getValueAt(i, 2).toString());
                    row.add(jTable2.getValueAt(i, 3).toString());
                    row.add(jTable2.getValueAt(i, 4).toString());
                    row.add(jTable2.getValueAt(i, 5).toString());
                    row.add(jTable2.getValueAt(i, 6).toString());
                    row.add(jTable2.getValueAt(i, 7).toString());
                    rows.add(row);
                }

                ArrayList header = new ArrayList();
                header.add("SR.No");
                header.add("Voucher NO");
                header.add("Voucher Date");
                header.add("Patient Name");
                header.add("Particular");
                header.add("Amount");
                header.add("Doctor");
                header.add("Entry BY");
                try {
                    lb.exportToExcel("Late Payment Detail", header, rows, "Late Payment Detail");
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at call excel", ex);
                }
            }

        });
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

    private void addTotal(String voucher, int index, boolean total) {

        double bill_amt = 0.00;
        for (int i = index; i < jTable2.getRowCount(); i++) {
            bill_amt += lb.isNumber(jTable2.getValueAt(i, 5).toString());
        }

        Vector row = new Vector();
        row.add("");
        row.add("");
        row.add("");
        row.add("Total of " + voucher);
        row.add("");
        row.add(lb.Convert2DecFmtForRs(bill_amt));
        row.add("");
        row.add("");
        if (total) {
            dtm.addRow(row);
        }

    }

    private void updateIPDList(int mode, String from, String to, String grp, boolean total) {
        try {
            String sql = "";
            sql = "SELECT o.ref_no,v_date,p.pt_name,o1.final_amt AS disc_amt,l.user_name,b.bill_item_name,a.ac_name FROM opdbillhd o"
                    + " LEFT JOIN patientmst p ON o.opd_no=p.opd_no  LEFT JOIN login l ON o.user_id=l.user_id "
                    + " LEFT JOIN opdbilldt o1 ON o.ref_no=o1.ref_no LEFT JOIN billitemmst b ON o1.bill_item_cd=b.bill_item_cd "
                    + " LEFT JOIN acntmst a ON o1.doc_cd=a.ac_cd WHERE o.v_date >='" + lb.ConvertDateFormetForDB(from) + "' and "
                    + " o.v_date <='" + lb.ConvertDateFormetForDB(to) + "' and b.bill_grp_cd=" + lb.getbillGrpCode(grp, "C") + " ";
            if (mode == 1) {
                sql += " and o.ref_no not LIKE 'PL%'";
            } else if (mode == 2) {
                sql += " and o.ref_no LIKE 'PL%'";
            }
            sql += " order by v_date,o.ref_no";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            int startIndex = 0;
            dtm.setRowCount(0);
            String old_ref_no = "";
            while (rsLcoal.next()) {
                if (total && !old_ref_no.equalsIgnoreCase("") && !old_ref_no.equalsIgnoreCase(rsLcoal.getString("ref_no"))) {
                    addBlankRow();
                    addTotal(old_ref_no, startIndex, total
                    );
                    startIndex = jTable2.getRowCount();
                }
                Vector row = new Vector();
                row.add(i);
                row.add(rsLcoal.getString("ref_no"));
                row.add(lb.ConvertDateFormetForDisply(rsLcoal.getString("v_date")));
                row.add(rsLcoal.getString("pt_name"));
                row.add(rsLcoal.getString("bill_item_name"));
                row.add(rsLcoal.getString("disc_amt"));
                row.add(rsLcoal.getString("ac_name"));
                row.add(rsLcoal.getString("user_name"));
                dtm.addRow(row);
                old_ref_no = rsLcoal.getString("ref_no");
                i++;
            }
            if (total) {
                addBlankRow();
                addTotal(old_ref_no, startIndex, total);
            }
            setTotal();

            lb.setColumnSizeForTable(jTable2, jPanel2.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    @Override
    public void dispose() {
        jd.dispose();

    }

    private void addBlankRow() {
        Vector row = new Vector();
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        row.add(" ");
        dtm.addRow(row);
    }

    private void setTotal() {
        double bill_amt = 0.00;
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            if (!jTable2.getValueAt(i, 1).toString().isEmpty()) {
                bill_amt += lb.isNumber(jTable2.getValueAt(i, 5).toString());
            }
        }

        addBlankRow();
        Vector row = new Vector();
        row.add("");
        row.add("");
        row.add("");
        row.add("Total");
        row.add("");
        row.add(lb.Convert2DecFmtForRs(bill_amt));
        row.add("");
        row.add("");
        dtm.addRow(row);
        addBlankRow();
    }

    public JRBeanCollectionDataSource getData() {
        OPDBillDetail.this.dispose();
        ArrayList<DailyOPDBillDAO> rows = new ArrayList<DailyOPDBillDAO>();
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            DailyOPDBillDAO row = new DailyOPDBillDAO();
            row.setSr_no(jTable2.getValueAt(i, 0) + "");
            row.setRef_no(jTable2.getValueAt(i, 1) + "");
            row.setV_date(jTable2.getValueAt(i, 2) + "");
            row.setPt_name(jTable2.getValueAt(i, 3) + "");
            row.setParticular(jTable2.getValueAt(i, 4) + "");
            row.setTot_amt(jTable2.getValueAt(i, 5) + "");
            row.setAc_name(jTable2.getValueAt(i, 6) + "");
            row.setUser(jTable2.getValueAt(i, 7) + "");
            rows.add(row);
        }
        return new JRBeanCollectionDataSource(rows);
    }

    public ArrayList<DailyOPDBillDAO> getDataArrayList() {
        OPDBillDetail.this.dispose();
        ArrayList<DailyOPDBillDAO> rows = new ArrayList<DailyOPDBillDAO>();
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            DailyOPDBillDAO row = new DailyOPDBillDAO();
            row.setSr_no(jTable2.getValueAt(i, 0) + "");
            row.setRef_no(jTable2.getValueAt(i, 1) + "");
            row.setV_date(jTable2.getValueAt(i, 2) + "");
            row.setPt_name(jTable2.getValueAt(i, 3) + "");
            row.setParticular(jTable2.getValueAt(i, 4) + "");
            row.setTot_amt(jTable2.getValueAt(i, 5) + "");
            row.setAc_name(jTable2.getValueAt(i, 6) + "");
            row.setUser(jTable2.getValueAt(i, 7) + "");
            rows.add(row);
        }
        return (rows);
    }

    public void setData(ArrayList<DailyOPDBillDAO> rows) {
        dtm.setRowCount(0);
        for (int i = 0; i < rows.size(); i++) {
            Vector row = new Vector();
            row.add(i + 1);
            row.add(rows.get(i).getRef_no());
            row.add(rows.get(i).getV_date());
            row.add(rows.get(i).getPt_name());
            row.add(rows.get(i).getParticular());
            row.add(rows.get(i).getTot_amt());
            row.add(rows.get(i).getAc_name());
            row.add(rows.get(i).getUser());
            dtm.addRow(row);
        }
        lb.setColumnSizeForTable(jTable2, jPanel2.getWidth());
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
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Voucher No", "Voucher Date", "Name", "Particular", "Bill Amt", "Doctor Name", "User"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1110, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                ref_no = jTable2.getValueAt(row, 1).toString();
                this.dispose();
                OPDBillGeneration opd = new OPDBillGeneration(ref_no.substring(0, 2));
                opd.setID(ref_no);
                if (ref_no.substring(0, 2).equalsIgnoreCase("OP")) {
                    HMSHome.addOnScreen(opd, "OPD Bill Generation Book", 24);
                } else {
                    HMSHome.addOnScreen(opd, "Pathology Bill Generation Book", 211);
                }
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
