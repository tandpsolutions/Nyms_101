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
import reportDAo.DailyAdvanceRefundDetailDAO;
import reportDAo.DailyOPDBillDAO;
import support.Library;
import transaction.OPDBillGeneration;

/**
 *
 * @author Bhaumik
 */
public class AdvancedRefund extends javax.swing.JInternalFrame {

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
    public AdvancedRefund(JDialog jd, int mode, String from, String to) {
        initComponents();
        this.jd = jd;
        dtm = (DefaultTableModel) jTable2.getModel();
        updateIPDList(mode, from, to);
        searchOnTextFieldsOPD();
    }

    public AdvancedRefund(JDialog jd) {
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
        JButton excel = new JButton("Excel");
        JButton Print = new JButton("Print");
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(excel);
        panel.add(Print);
        jPanel1.add(panel, BorderLayout.EAST);

        Print.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AdvancedRefund.this.dispose();
                ArrayList<DailyAdvanceRefundDetailDAO> rows = new ArrayList<DailyAdvanceRefundDetailDAO>();
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    DailyAdvanceRefundDetailDAO row = new DailyAdvanceRefundDetailDAO();
                    row.setSr_no(jTable2.getValueAt(i, 0).toString());
                    row.setRef_no(jTable2.getValueAt(i, 1).toString());
                    row.setV_date(jTable2.getValueAt(i, 2).toString());
                    row.setParticular(jTable2.getValueAt(i, 3).toString());
                    row.setAmount(jTable2.getValueAt(i, 4).toString());
                    row.setCash(jTable2.getValueAt(i, 5).toString());
                    row.setBank(jTable2.getValueAt(i, 6).toString());
                    row.setCard(jTable2.getValueAt(i, 7).toString());
                    row.setUser(jTable2.getValueAt(i, 8).toString());
                    row.setIpd_no(jTable2.getValueAt(i, 9).toString());
                    rows.add(row);
                }
                lb.reportGeneratorWord("DailyAdvanceRefundDetail.jasper", null, rows);
            }
        });
        excel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
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
                    row.add(jTable2.getValueAt(i, 8).toString());
                    row.add(jTable2.getValueAt(i, 9).toString());
                    rows.add(row);
                }

                ArrayList header = new ArrayList();
                header.add("SR.No");
                header.add("Voucher NO");
                header.add("Voucher Date");
                header.add("Patient Name");
                header.add("Amount");
                header.add("Cash");
                header.add("Bank");
                header.add("Card");
                header.add("Entry BY");
                header.add("Ipd No");
                try {
                    lb.exportToExcel("Advance Receipt Refund", header, rows, "Advance Receipt Refund");
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

    public JRBeanCollectionDataSource getData() {
        AdvancedRefund.this.dispose();
        ArrayList<DailyAdvanceRefundDetailDAO> rows = new ArrayList<DailyAdvanceRefundDetailDAO>();
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            DailyAdvanceRefundDetailDAO row = new DailyAdvanceRefundDetailDAO();
            row.setSr_no(jTable2.getValueAt(i, 0).toString());
            row.setRef_no(jTable2.getValueAt(i, 1).toString());
            row.setV_date(jTable2.getValueAt(i, 2).toString());
            row.setParticular(jTable2.getValueAt(i, 3).toString());
            row.setAmount(jTable2.getValueAt(i, 4).toString());
            row.setCash(jTable2.getValueAt(i, 5).toString());
            row.setBank(jTable2.getValueAt(i, 6).toString());
            row.setCard(jTable2.getValueAt(i, 7).toString());
            row.setUser(jTable2.getValueAt(i, 8).toString());
            row.setIpd_no(jTable2.getValueAt(i, 9).toString());
            rows.add(row);
        }
        return new JRBeanCollectionDataSource(rows);
    }

    public ArrayList<DailyAdvanceRefundDetailDAO> getDataArrayList() {
        AdvancedRefund.this.dispose();
        ArrayList<DailyAdvanceRefundDetailDAO> rows = new ArrayList<DailyAdvanceRefundDetailDAO>();
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            DailyAdvanceRefundDetailDAO row = new DailyAdvanceRefundDetailDAO();
            row.setSr_no(jTable2.getValueAt(i, 0).toString());
            row.setRef_no(jTable2.getValueAt(i, 1).toString());
            row.setV_date(jTable2.getValueAt(i, 2).toString());
            row.setParticular(jTable2.getValueAt(i, 3).toString());
            row.setAmount(jTable2.getValueAt(i, 4).toString());
            row.setCash(jTable2.getValueAt(i, 5).toString());
            row.setBank(jTable2.getValueAt(i, 6).toString());
            row.setCard(jTable2.getValueAt(i, 7).toString());
            row.setUser(jTable2.getValueAt(i, 8).toString());
            row.setIpd_no(jTable2.getValueAt(i, 9).toString());
            rows.add(row);
        }
        return (rows);
    }

    private void updateIPDList(int mode, String from, String to) {
        try {
            String sql = "";
            sql = "SELECT i.ipd_no,i1.ref_no,p1.pt_name,i1.amount,p.cash_amt,p.bank_amt,p.card_amt,l.user_name,i1.v_date FROM ipdreg i "
                    + " LEFT JOIN ipdpaymenthd i1 ON i.ipd_no=i1.ipd_no LEFT JOIN payment p ON i1.ref_no=p.ref_no "
                    + " LEFT JOIN patientmst p1 ON i.opd_no=p1.opd_no LEFT JOIN login l ON l.user_id=i1.user_id "
                    + " WHERE i1.v_date >='" + lb.ConvertDateFormetForDB(from) + "' and "
                    + " i1.v_date <='" + lb.ConvertDateFormetForDB(to) + "'";
            if (mode == 3) {
                sql += " and i1.ref_no LIKE 'AR%' and i1.amount >=0";
            } else if (mode == 4) {
                sql += " and i1.ref_no LIKE 'IP%' and i1.amount >=0";
            } else if (mode == 5) {
                sql += " and i1.ref_no LIKE 'AR%' and i1.amount <0";
            }
            sql += " order by i1.v_date";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            dtm.setRowCount(0);
            while (rsLcoal.next()) {
                Vector row = new Vector();
                row.add(i);
                row.add(rsLcoal.getString("ref_no"));
                row.add(lb.ConvertDateFormetForDisply(rsLcoal.getString("v_date")));
                row.add(rsLcoal.getString("pt_name"));
                row.add(rsLcoal.getString("Amount"));
                row.add(rsLcoal.getString("cash_amt"));
                row.add(rsLcoal.getString("bank_amt"));
                row.add(rsLcoal.getString("card_amt"));
                row.add(rsLcoal.getString("user_name"));
                row.add(rsLcoal.getString("ipd_no"));
                dtm.addRow(row);
                i++;
            }
            setTotal();
            lb.setColumnSizeForTable(jTable2, jPanel2.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
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
        row.add(" ");
        row.add(" ");
        dtm.addRow(row);
    }

    private void setTotal() {
        double bill_amt = 0.00;
        double cash = 0.00;
        double card = 0.00;
        double bank = 0.00;
        for (int i = 0; i < jTable2.getRowCount(); i++) {
            if (!jTable2.getValueAt(i, 1).toString().isEmpty()) {
                bill_amt += lb.isNumber(jTable2.getValueAt(i, 4).toString());
                cash += lb.isNumber(jTable2.getValueAt(i, 5).toString());
                bank += lb.isNumber(jTable2.getValueAt(i, 6).toString());
                card += lb.isNumber(jTable2.getValueAt(i, 7).toString());
            }
        }

        addBlankRow();
        Vector row = new Vector();
        row.add("");
        row.add("");
        row.add("");
        row.add("Total");
        row.add(lb.Convert2DecFmtForRs(bill_amt));
        row.add(lb.Convert2DecFmtForRs(cash));
        row.add(lb.Convert2DecFmtForRs(bank));
        row.add(lb.Convert2DecFmtForRs(card));
        row.add("");
        row.add("");
        dtm.addRow(row);
        addBlankRow();
    }

    public void setData(ArrayList<DailyAdvanceRefundDetailDAO> rows) {
        dtm.setRowCount(0);
        for (int i = 0; i < rows.size(); i++) {
            Vector row = new Vector();
            row.add(i + 1);
            row.add(rows.get(i).getRef_no());
            row.add(rows.get(i).getV_date());
            row.add(rows.get(i).getParticular());
            row.add(rows.get(i).getAmount());
            row.add(rows.get(i).getCash());
            row.add(rows.get(i).getBank());
            row.add(rows.get(i).getCard());
            row.add(rows.get(i).getUser());
            row.add(rows.get(i).getIpd_no());
            dtm.addRow(row);
        }
        lb.setColumnSizeForTable(jTable2, jPanel2.getWidth());
    }

    @Override
    public void dispose() {
        jd.dispose();

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

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Voucher No", "Voucher Date", "Name", "Bill Amt", "Cash", "Bank", "Card", "User", "IPD No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1067, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                if (ref_no.substring(0, 2).equalsIgnoreCase("AR")) {
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
