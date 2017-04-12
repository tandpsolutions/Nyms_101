/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMS101;
import hms.HMSHome;
import static hms.HMSHome.addOnScreen;
import static hms.HMSHome.role;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import support.Library;
import support.StatusColumnCellRenderer;

/**
 *
 * @author Bhaumik
 */
public class TransactionHome extends javax.swing.JInternalFrame {

    /**
     * Creates new form AppointmentBook
     */
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtmOPD = null;
    DefaultTableModel dtmIPD = null;
    private JTextField jtfFilter = new JTextField();
    private TableRowSorter<TableModel> rowSorter;

    public TransactionHome() {
        initComponents();
        dtmOPD = (DefaultTableModel) jTable1.getModel();
        dtmIPD = (DefaultTableModel) jTable2.getModel();
        jScrollPane1.setVisible(true);
        updateIPDList();
        searchOnTextFieldsIPD();
        jTable2.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer(10, 3, 2));
    }

    @Override
    public void dispose() {

    }

    private void searchOnTextFieldsOPD() {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jPanel2.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel2.add(jtfFilter, BorderLayout.CENTER);

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

    private void searchOnTextFieldsIPD() {
        this.rowSorter = new TableRowSorter<>(jTable2.getModel());
        jTable2.setRowSorter(rowSorter);
        jPanel2.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel2.add(jtfFilter, BorderLayout.CENTER);
        JButton excel = new JButton("Excel");
        jPanel2.add(excel, BorderLayout.EAST);

        excel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (jScrollPane1.isVisible()) {
                        ArrayList rows = new ArrayList();
                        for (int i = 0; i < jTable1.getRowCount(); i++) {
                            ArrayList row = new ArrayList();
                            row.add(jTable1.getValueAt(i, 1).toString());
                            row.add(jTable1.getValueAt(i, 2).toString());
                            row.add(jTable1.getValueAt(i, 3).toString());
                            row.add(jTable1.getValueAt(i, 4).toString());
                            row.add(jTable1.getValueAt(i, 5).toString());
                            row.add(jTable1.getValueAt(i, 6).toString());
                            row.add(jTable1.getValueAt(i, 7).toString());
                            row.add(jTable1.getValueAt(i, 8).toString());
                            rows.add(row);
                        }

                        ArrayList header = new ArrayList();
                        header.add("SR.No");
                        header.add("Token NO");
                        header.add("OPD No");
                        header.add("Patient Name");
                        header.add("Time");
                        header.add("Doctor");
                        header.add("For");
                        header.add("Entry BY");
                        lb.exportToExcel("OPD List", header, rows, "OPD List");
                    } else if (jScrollPane2.isVisible()) {

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
                        header.add("IPD NO");
                        header.add("OPD No");
                        header.add("Patient Name");
                        header.add("Ward");
                        header.add("Bed");
                        header.add("D.O.A");
                        header.add("Doctor");
                        header.add("Refer By");
                        header.add("Admited By");
                        lb.exportToExcel("IPD List", header, rows, "OPD List");

                    }
                } catch (Exception ex) {
                }
            }
        });

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

    private void updateOPDList() {
        try {
            String sql = "";
            sql = "SELECT l.user_name,ref_no,p.opd_no,p.pt_name,a1.ac_name,a.token_no,a.appoint_time,"
                    + " CASE WHEN a.case_type = 0 THEN 'New' ELSE 'Follow' END AS case_type FROM appointmentmst a "
                    + " LEFT JOIN patientmst p ON a.opd_no=p.opd_no LEFT JOIN acntmst a1 ON a.cons_doc = a1.ac_cd"
                    + " left join login l on l.user_id=a.user_id where a.appoint_date = CURRENT_DATE and is_can=0 "
                    + " and ref_no not in (select appoint_no from opdbillhd)";

            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            dtmOPD.setRowCount(0);
            while (rsLcoal.next()) {
                Vector row = new Vector();
                row.add(rsLcoal.getString("ref_no"));
                row.add(i);
                row.add(rsLcoal.getInt("token_no"));
                row.add(rsLcoal.getString("opd_no"));
                row.add(rsLcoal.getString("pt_name"));
                row.add(rsLcoal.getString("appoint_time"));
                row.add(rsLcoal.getString("ac_name"));
                row.add(rsLcoal.getString("case_type"));
                row.add(rsLcoal.getString("user_name"));
                dtmOPD.addRow(row);
                i++;
            }
            lb.setColumnSizeForTable(jTable1, jPanel4.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void updateIPDList() {
        try {
            String sql = "";
            sql = "SELECT a1.ac_name as ref, l.user_name,a.ac_name,i.ipd_no,i.opd_no,i.admit_date,ward_name,r.room_cd,p.pt_name,p.ref_opd_no"
                    + " FROM ipdreg i LEFT JOIN roommst r ON i.opd_no=r.opd_no "
                    + " LEFT JOIN wardmst w ON r.ward_cd=w.ward_cd LEFT JOIN patientmst p ON i.opd_no=p.opd_no left join acntmst a on i.doc_cd=a.ac_cd"
                    + " left join login l on i.user_id=l.user_id left join acntmst a1 on p.ref_by =a1.ac_cd WHERE is_close = 0 order by admit_date";

            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            dtmIPD.setRowCount(0);
            while (rsLcoal.next()) {
                Vector row = new Vector();
                row.add(i);
                row.add(rsLcoal.getString("ipd_no"));
                row.add(rsLcoal.getString("opd_no"));
                row.add(rsLcoal.getString("pt_name"));
                row.add(rsLcoal.getString("ward_name"));
                row.add(rsLcoal.getString("room_cd"));
                row.add(lb.ConvertDateFormetForDisply(rsLcoal.getString("admit_date")));
                row.add(rsLcoal.getString("ac_name"));
                row.add(rsLcoal.getString("ref"));
                row.add(rsLcoal.getString("user_name"));
                row.add(rsLcoal.getString("ref_opd_no"));
                dtmIPD.addRow(row);
                i++;
            }
            lb.setColumnSizeForTable(jTable2, jPanel4.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void showNote(String ipd_no) {
        try {
            String sql = "select * from notemst where ipd_no='" + ipd_no + "' and is_del =0";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            String note = "";
            while (rsLocal.next()) {
                note += "Note : ";
                note += rsLocal.getString("note").replaceAll("\n", " ");
                note += "    User : " + lb.getUserName(rsLocal.getString("user_id"), "N") + "\n";
            }
            if (!note.isEmpty()) {
                lb.showMessageDailog(note);
            }
        } catch (Exception ex) {

        }
    }

    private boolean checkRight(int form_cd) {
        if (role != 1) {
            return lb.getRight(form_cd + "", "VIEWS");
        } else {
            return true;
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

        jPanel1 = new javax.swing.JPanel();
        jpanelCode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jpanelCode1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jpanelCode2 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jpanelCode3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jpanelCode4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jpanelCode5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jpanelCode7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jpanelCode8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jpanelCode9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jpanelCode10 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jpanelCode11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jpanelCode12 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jpanelCode13 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jpanelCode14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jpanelCode15 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jpanelCode16 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jpanelCode17 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jpanelCode18 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jpanelCode19 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jpanelCode20 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();

        jpanelCode.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseClicked(evt);
            }
        });
        jpanelCode.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("OPD Entry");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText(" ");

        javax.swing.GroupLayout jpanelCodeLayout = new javax.swing.GroupLayout(jpanelCode);
        jpanelCode.setLayout(jpanelCodeLayout);
        jpanelCodeLayout.setHorizontalGroup(
            jpanelCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCodeLayout.setVerticalGroup(
            jpanelCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCodeLayout.createSequentialGroup()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode1.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode1.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode1MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode1MouseEntered(evt);
            }
        });
        jpanelCode1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("OPD Appt Book");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText(" ");

        javax.swing.GroupLayout jpanelCode1Layout = new javax.swing.GroupLayout(jpanelCode1);
        jpanelCode1.setLayout(jpanelCode1Layout);
        jpanelCode1Layout.setHorizontalGroup(
            jpanelCode1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode1Layout.setVerticalGroup(
            jpanelCode1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode1Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode2.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode2.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });
        jpanelCode2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode2MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode2MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode2MouseEntered(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText(" ");

        javax.swing.GroupLayout jpanelCode2Layout = new javax.swing.GroupLayout(jpanelCode2);
        jpanelCode2.setLayout(jpanelCode2Layout);
        jpanelCode2Layout.setHorizontalGroup(
            jpanelCode2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode2Layout.setVerticalGroup(
            jpanelCode2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode2Layout.createSequentialGroup()
                .addComponent(jLabel27)
                .addContainerGap(98, Short.MAX_VALUE))
        );

        jpanelCode3.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode3.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode3MouseExited(evt);
            }
        });
        jpanelCode3.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("OPD Bill Generate");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(" ");

        javax.swing.GroupLayout jpanelCode3Layout = new javax.swing.GroupLayout(jpanelCode3);
        jpanelCode3.setLayout(jpanelCode3Layout);
        jpanelCode3Layout.setHorizontalGroup(
            jpanelCode3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode3Layout.setVerticalGroup(
            jpanelCode3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode3Layout.createSequentialGroup()
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode4.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode4.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode4MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode4MouseEntered(evt);
            }
        });
        jpanelCode4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Clear Selection");

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText(" ");

        javax.swing.GroupLayout jpanelCode4Layout = new javax.swing.GroupLayout(jpanelCode4);
        jpanelCode4.setLayout(jpanelCode4Layout);
        jpanelCode4Layout.setHorizontalGroup(
            jpanelCode4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode4Layout.setVerticalGroup(
            jpanelCode4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode4Layout.createSequentialGroup()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode5.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode5.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode5MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode5MouseEntered(evt);
            }
        });
        jpanelCode5.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("IPD Entry");

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText(" ");

        javax.swing.GroupLayout jpanelCode5Layout = new javax.swing.GroupLayout(jpanelCode5);
        jpanelCode5.setLayout(jpanelCode5Layout);
        jpanelCode5Layout.setHorizontalGroup(
            jpanelCode5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode5Layout.setVerticalGroup(
            jpanelCode5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode5Layout.createSequentialGroup()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode7.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode7.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode7MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode7MouseEntered(evt);
            }
        });
        jpanelCode7.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("IPD Register");

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText(" ");

        javax.swing.GroupLayout jpanelCode7Layout = new javax.swing.GroupLayout(jpanelCode7);
        jpanelCode7.setLayout(jpanelCode7Layout);
        jpanelCode7Layout.setHorizontalGroup(
            jpanelCode7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode7Layout.setVerticalGroup(
            jpanelCode7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode7Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode8.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode8.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode8MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode8MouseEntered(evt);
            }
        });
        jpanelCode8.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Show OPD Entry");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText(" ");

        javax.swing.GroupLayout jpanelCode8Layout = new javax.swing.GroupLayout(jpanelCode8);
        jpanelCode8.setLayout(jpanelCode8Layout);
        jpanelCode8Layout.setHorizontalGroup(
            jpanelCode8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode8Layout.setVerticalGroup(
            jpanelCode8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode8Layout.createSequentialGroup()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode9.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode9.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode9MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode9MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode9MouseEntered(evt);
            }
        });
        jpanelCode9.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Show IPD Entry");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText(" ");

        javax.swing.GroupLayout jpanelCode9Layout = new javax.swing.GroupLayout(jpanelCode9);
        jpanelCode9.setLayout(jpanelCode9Layout);
        jpanelCode9Layout.setHorizontalGroup(
            jpanelCode9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode9Layout.setVerticalGroup(
            jpanelCode9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode9Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode10.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode10.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode10MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode10MouseEntered(evt);
            }
        });
        jpanelCode10.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Hospital Status");

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText(" ");

        javax.swing.GroupLayout jpanelCode10Layout = new javax.swing.GroupLayout(jpanelCode10);
        jpanelCode10.setLayout(jpanelCode10Layout);
        jpanelCode10Layout.setHorizontalGroup(
            jpanelCode10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode10Layout.setVerticalGroup(
            jpanelCode10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode10Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode11.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode11.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode11MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode11MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode11MouseEntered(evt);
            }
        });
        jpanelCode11.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Due Billing");

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText(" ");

        javax.swing.GroupLayout jpanelCode11Layout = new javax.swing.GroupLayout(jpanelCode11);
        jpanelCode11.setLayout(jpanelCode11Layout);
        jpanelCode11Layout.setHorizontalGroup(
            jpanelCode11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode11Layout.setVerticalGroup(
            jpanelCode11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode11Layout.createSequentialGroup()
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode12.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode12.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode12.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });
        jpanelCode12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode12MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode12MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode12MouseEntered(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Advance Receipt");

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText(" ");

        javax.swing.GroupLayout jpanelCode12Layout = new javax.swing.GroupLayout(jpanelCode12);
        jpanelCode12.setLayout(jpanelCode12Layout);
        jpanelCode12Layout.setHorizontalGroup(
            jpanelCode12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode12Layout.setVerticalGroup(
            jpanelCode12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode12Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode13.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode13.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode13.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode13MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode13MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode13MouseEntered(evt);
            }
        });
        jpanelCode13.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Day Wise Billing");

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText(" ");

        javax.swing.GroupLayout jpanelCode13Layout = new javax.swing.GroupLayout(jpanelCode13);
        jpanelCode13.setLayout(jpanelCode13Layout);
        jpanelCode13Layout.setHorizontalGroup(
            jpanelCode13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode13Layout.setVerticalGroup(
            jpanelCode13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode13Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode14.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode14.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode14.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });
        jpanelCode14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode14MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode14MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode14MouseEntered(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Discharge");

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText(" ");

        javax.swing.GroupLayout jpanelCode14Layout = new javax.swing.GroupLayout(jpanelCode14);
        jpanelCode14.setLayout(jpanelCode14Layout);
        jpanelCode14Layout.setHorizontalGroup(
            jpanelCode14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode14Layout.setVerticalGroup(
            jpanelCode14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode14Layout.createSequentialGroup()
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode15.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode15.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode15.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode15MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode15MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode15MouseEntered(evt);
            }
        });
        jpanelCode15.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Patho Bill Book");

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText(" ");

        javax.swing.GroupLayout jpanelCode15Layout = new javax.swing.GroupLayout(jpanelCode15);
        jpanelCode15.setLayout(jpanelCode15Layout);
        jpanelCode15Layout.setHorizontalGroup(
            jpanelCode15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jpanelCode15Layout.setVerticalGroup(
            jpanelCode15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode15Layout.createSequentialGroup()
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode16.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode16.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode16MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode16MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode16MouseEntered(evt);
            }
        });
        jpanelCode16.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Refund");

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText(" ");

        javax.swing.GroupLayout jpanelCode16Layout = new javax.swing.GroupLayout(jpanelCode16);
        jpanelCode16.setLayout(jpanelCode16Layout);
        jpanelCode16Layout.setHorizontalGroup(
            jpanelCode16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode16Layout.setVerticalGroup(
            jpanelCode16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode16Layout.createSequentialGroup()
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode17.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode17.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode17MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode17MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode17MouseEntered(evt);
            }
        });
        jpanelCode17.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Add Note");

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText(" ");

        javax.swing.GroupLayout jpanelCode17Layout = new javax.swing.GroupLayout(jpanelCode17);
        jpanelCode17.setLayout(jpanelCode17Layout);
        jpanelCode17Layout.setHorizontalGroup(
            jpanelCode17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode17Layout.setVerticalGroup(
            jpanelCode17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode17Layout.createSequentialGroup()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jpanelCode18.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode18.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode18.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode18MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode18MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode18MouseEntered(evt);
            }
        });
        jpanelCode18.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCodeMouseDragged(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Remove Note");

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText(" ");

        javax.swing.GroupLayout jpanelCode18Layout = new javax.swing.GroupLayout(jpanelCode18);
        jpanelCode18.setLayout(jpanelCode18Layout);
        jpanelCode18Layout.setHorizontalGroup(
            jpanelCode18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );
        jpanelCode18Layout.setVerticalGroup(
            jpanelCode18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode18Layout.createSequentialGroup()
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode19.setBackground(new java.awt.Color(4, 110, 152));
        jpanelCode19.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode19.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode19MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode19MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode19MouseEntered(evt);
            }
        });
        jpanelCode19.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCode19jpanelCodeMouseDragged(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Patient Info");

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText(" ");

        javax.swing.GroupLayout jpanelCode19Layout = new javax.swing.GroupLayout(jpanelCode19);
        jpanelCode19.setLayout(jpanelCode19Layout);
        jpanelCode19Layout.setHorizontalGroup(
            jpanelCode19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jpanelCode19Layout.setVerticalGroup(
            jpanelCode19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode19Layout.createSequentialGroup()
                .addComponent(jLabel44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanelCode20.setBackground(new java.awt.Color(153, 255, 153));
        jpanelCode20.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpanelCode20.setPreferredSize(new java.awt.Dimension(135, 110));
        jpanelCode20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpanelCode20MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpanelCode20MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jpanelCode20MouseEntered(evt);
            }
        });
        jpanelCode20.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jpanelCode20jpanelCodeMouseDragged(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("OPD Late Pmt");

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(255, 255, 255));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText(" ");

        javax.swing.GroupLayout jpanelCode20Layout = new javax.swing.GroupLayout(jpanelCode20);
        jpanelCode20.setLayout(jpanelCode20Layout);
        jpanelCode20Layout.setHorizontalGroup(
            jpanelCode20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jpanelCode20Layout.setVerticalGroup(
            jpanelCode20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpanelCode20Layout.createSequentialGroup()
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jpanelCode11, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpanelCode12, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpanelCode14, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jpanelCode15, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jpanelCode, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jpanelCode5, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode7, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode10, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode13, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(139, 139, 139)
                                .addComponent(jpanelCode16, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jpanelCode17, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(278, 278, 278)
                                .addComponent(jpanelCode18, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(417, 417, 417)
                                .addComponent(jpanelCode19, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jpanelCode8, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode9, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpanelCode20, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpanelCode, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpanelCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpanelCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpanelCode3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpanelCode13, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpanelCode10, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpanelCode7, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpanelCode5, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jpanelCode15, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpanelCode19, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jpanelCode11, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpanelCode17, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jpanelCode12, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpanelCode16, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jpanelCode14, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpanelCode18, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jpanelCode9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jpanelCode8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jpanelCode4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jpanelCode20, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setLayout(new java.awt.CardLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ref_no", "Sr No", "Token No", "OPD No", "Patient Name", "Time", "Doctor", "For", "Entry BY"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        jPanel4.add(jScrollPane1, "card2");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "IPD No", "OPD No", "Name", "Ward", "Bed No", "D.O.A", "Doctor", "Ref By", "Admit By", "mother"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
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
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
            jTable2.getColumnModel().getColumn(4).setResizable(false);
            jTable2.getColumnModel().getColumn(5).setResizable(false);
            jTable2.getColumnModel().getColumn(6).setResizable(false);
            jTable2.getColumnModel().getColumn(7).setResizable(false);
            jTable2.getColumnModel().getColumn(8).setResizable(false);
            jTable2.getColumnModel().getColumn(9).setResizable(false);
            jTable2.getColumnModel().getColumn(10).setMinWidth(0);
            jTable2.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTable2.getColumnModel().getColumn(10).setMaxWidth(0);
        }

        jPanel4.add(jScrollPane2, "card3");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jpanelCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCodeMouseClicked
        if (evt.getClickCount() == 1) {
            evt.consume();
//            if (hasPermission("21")) {
            JDialog d = new JDialog();
            d.setModal(true);
            SearchPatient sp = new SearchPatient(0, d);
            d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            d.setTitle("OPD Appointment Book");
            d.add(sp);
            d.setPreferredSize(new Dimension(sp.getWidth() + 20, sp.getHeight()));
            d.setLocationRelativeTo(this);
            d.setAlwaysOnTop(true);
            sp.setVisible(true);
            d.pack();
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
            d.setVisible(true);
        }
//            } else {
//                JOptionPane.showMessageDialog(this, "You do not have authority to view this form");
//            }
    }//GEN-LAST:event_jpanelCodeMouseClicked

    private void jpanelCode1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode1MouseClicked
        // TODO add your handling code here:
        this.dispose();
        OPDAppointmentBook opdbook = new OPDAppointmentBook("");
        addOnScreen(opdbook, "OPD Appointment Book", 22);
    }//GEN-LAST:event_jpanelCode1MouseClicked

    private void jpanelCode1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode1MouseEntered

    private void jpanelCode1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode1MouseExited

    private void jpanelCode2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode2MouseClicked

    private void jpanelCode2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode2MouseEntered

    private void jpanelCode2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode2MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode2MouseExited

    private void jpanelCode3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode3MouseClicked
        // TODO add your handling code here:
        this.dispose();
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            this.dispose();
            OPDBillGeneration opbBill = new OPDBillGeneration(jTable1.getValueAt(row, 3).toString(), jTable1.getValueAt(row, 0).toString(), "VO");
            HMSHome.addOnScreen(opbBill, "OPD Bill Generation Book", 24);
        } else {
            OPDBillGeneration opdBill = new OPDBillGeneration("VO");
            addOnScreen(opdBill, "OPD Bill Generation Book", 24);
        }
    }//GEN-LAST:event_jpanelCode3MouseClicked

    private void jpanelCode3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode3MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode3MouseEntered

    private void jpanelCode3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode3MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode3MouseExited

    private void jpanelCode4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode4MouseClicked
        // TODO add your handling code here:
        jTable1.clearSelection();
        jTable2.clearSelection();
    }//GEN-LAST:event_jpanelCode4MouseClicked

    private void jpanelCode4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode4MouseEntered

    private void jpanelCode4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode4MouseExited

    private void jpanelCode5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode5MouseClicked
        // TODO add your handling code here:

        evt.consume();
//            if (hasPermission("21")) {
        JDialog d = new JDialog();
        d.setModal(true);
        SearchPatient sp = new SearchPatient(1, d);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("IPD Register Book");
        d.add(sp);
        d.setPreferredSize(new Dimension(sp.getWidth() + 20, sp.getHeight()));
        d.setLocationRelativeTo(this);
        d.setAlwaysOnTop(true);
        sp.setVisible(true);
        d.pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
        d.setVisible(true);

    }//GEN-LAST:event_jpanelCode5MouseClicked

    private void jpanelCode5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode5MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode5MouseEntered

    private void jpanelCode5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode5MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode5MouseExited

    private void jpanelCode7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode7MouseClicked
        // TODO add your handling code here:
        this.dispose();
        IPDRegistrationForm ipd = new IPDRegistrationForm();
        addOnScreen(ipd, "IPD Register Book", 25);
    }//GEN-LAST:event_jpanelCode7MouseClicked

    private void jpanelCode7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode7MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode7MouseEntered

    private void jpanelCode7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode7MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode7MouseExited

    private void jpanelCode8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode8MouseClicked
        // TODO add your handling code here:
        updateOPDList();
        jScrollPane1.setVisible(true);
        jScrollPane2.setVisible(false);
        searchOnTextFieldsOPD();
    }//GEN-LAST:event_jpanelCode8MouseClicked

    private void jpanelCode8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode8MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode8MouseEntered

    private void jpanelCode8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode8MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode8MouseExited

    private void jpanelCode9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode9MouseClicked
        // TODO add your handling code here:
        updateIPDList();
        jScrollPane1.setVisible(false);
        jScrollPane2.setVisible(true);
        searchOnTextFieldsIPD();
    }//GEN-LAST:event_jpanelCode9MouseClicked

    private void jpanelCode9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode9MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode9MouseEntered

    private void jpanelCode9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode9MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode9MouseExited

    private void jpanelCode10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode10MouseClicked
        // TODO add your handling code here:
//        this.dispose();
//        int row = jTable2.getSelectedRow();
//        if (row != -1) {
//            WardTransfer wd = new WardTransfer(jTable2.getValueAt(row, 1).toString());
//            addOnScreen(wd, "Ward Transfer");
//        } else {
//            WardTransfer wd = new WardTransfer();
//            addOnScreen(wd, "Ward Transfer");
//        }

        HospitalStatus hs = new HospitalStatus();
        addOnScreen(hs, "Hospital Status", 26);
    }//GEN-LAST:event_jpanelCode10MouseClicked

    private void jpanelCode10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode10MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode10MouseEntered

    private void jpanelCode10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode10MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode10MouseExited

    private void jpanelCode11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode11MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode11MouseExited

    private void jpanelCode11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode11MouseClicked
        // TODO add your handling code here:
        this.dispose();
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            showNote(jTable2.getValueAt(row, 1).toString());
            IPDDueBillGeneration ipd = new IPDDueBillGeneration(jTable2.getValueAt(row, 1).toString());
            addOnScreen(ipd, "IPD Due Billing", 28);
        } else {
            IPDDueBillGeneration ipd = new IPDDueBillGeneration();
            addOnScreen(ipd, "IPD Due Billing", 28);
        }
    }//GEN-LAST:event_jpanelCode11MouseClicked

    private void jpanelCode11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode11MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode11MouseEntered

    private void jpanelCode12MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode12MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode12MouseExited

    private void jpanelCode12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode12MouseClicked
        // TODO add your handling code here:
        this.dispose();
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            IPDAdvanceReceipt ipd = new IPDAdvanceReceipt(jTable2.getValueAt(row, 1).toString(), 1);
            addOnScreen(ipd, "IPD Advance Receipt", 29);
        } else {
            IPDAdvanceReceipt ipd = new IPDAdvanceReceipt(1);
            addOnScreen(ipd, "IPD Advance Receipt", 29);
        }
    }//GEN-LAST:event_jpanelCode12MouseClicked

    private void jpanelCode12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode12MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode12MouseEntered

    private void jpanelCode13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode13MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode13MouseExited

    private void jpanelCode13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode13MouseClicked
        // TODO add your handling code here:
        if (checkRight(28)) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                this.dispose();
                IPDDayWiseBilling ipd = new IPDDayWiseBilling(null, true, jTable2.getValueAt(row, 1).toString());
                ipd.setLocationRelativeTo(null);
                ipd.show();
            } else {
                lb.showMessageDailog("Please select I.P.D patient first");
            }
        } else {
            lb.showMessageDailog("You don't have rights to view this form.");
        }
    }//GEN-LAST:event_jpanelCode13MouseClicked

    private void jpanelCode13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode13MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode13MouseEntered

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            if (!lb.CheckPatientError(jTable1.getValueAt(row, 3).toString())) {
                lb.showMessageDailog("Some Detail has not been filled. Please fill them immediately.");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            if (!lb.CheckPatientError(jTable2.getValueAt(row, 2).toString())) {
                lb.showMessageDailog("Some Detail has not been filled. Please fill them immediately.");
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jpanelCode14MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode14MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode14MouseExited

    private void jpanelCode14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode14MouseClicked
        // TODO add your handling code here:
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            DischargeType dp = new DischargeType(null, true);
            dp.setLocationRelativeTo(null);
            dp.setVisible(true);
            if (dp.getStatus() != -1) {
                IPDBillGenerationDischarge ipd = new IPDBillGenerationDischarge(jTable2.getValueAt(row, 1).toString(), dp.getStatus());
                addOnScreen(ipd, "IPD Billing and Discharge", 210);
            }
        } else {
            IPDBillGenerationDischarge ipd = new IPDBillGenerationDischarge();
            addOnScreen(ipd, "IPD Billing and Discharge", 210);
        }
    }//GEN-LAST:event_jpanelCode14MouseClicked

    private void jpanelCode14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode14MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode14MouseEntered

    private void jpanelCode15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode15MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode15MouseEntered

    private void jpanelCode15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode15MouseClicked
        // TODO add your handling code here:
        this.dispose();
        if (jScrollPane1.isVisible()) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                this.dispose();
                OPDBillGeneration opbBill = new OPDBillGeneration(jTable1.getValueAt(row, 3).toString(), jTable1.getValueAt(row, 0).toString(), "PL");
                HMSHome.addOnScreen(opbBill, "Pathology Bill Generation Book", 211);
            } else {
                OPDBillGeneration opdBill = new OPDBillGeneration("PL");
                addOnScreen(opdBill, "Pathology Bill Generation Book", 211);
            }
        } else if (jScrollPane2.isVisible()) {

            int row = jTable2.getSelectedRow();
            if (row != -1) {
                this.dispose();
                OPDBillGeneration opbBill = new OPDBillGeneration(jTable2.getValueAt(row, 2).toString(), "", "PL");
                HMSHome.addOnScreen(opbBill, "Pathology Bill Generation Book", 211);
            } else {
                OPDBillGeneration opdBill = new OPDBillGeneration("PL");
                addOnScreen(opdBill, "Pathology Bill Generation Book", 211);
            }

        }
    }//GEN-LAST:event_jpanelCode15MouseClicked

    private void jpanelCode15MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode15MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode15MouseExited

    private void jpanelCode16MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode16MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode16MouseExited

    private void jpanelCode16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode16MouseClicked
        // TODO add your handling code here:
        this.dispose();
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            IPDAdvanceReceipt ipd = new IPDAdvanceReceipt(jTable2.getValueAt(row, 1).toString(), -1);
            addOnScreen(ipd, "IPD Refund Receipt", 212);
        } else {
            IPDAdvanceReceipt ipd = new IPDAdvanceReceipt(-1);
            addOnScreen(ipd, "IPD Refund Receipt", 212);
        }
    }//GEN-LAST:event_jpanelCode16MouseClicked

    private void jpanelCode16MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode16MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode16MouseEntered

    private void jpanelCode17MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode17MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode17MouseExited

    private void jpanelCode17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode17MouseClicked
        // TODO add your handling code here:
        if (checkRight(28)) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                this.dispose();
                AddNote ipd = new AddNote(null, true, jTable2.getValueAt(row, 1).toString());
                ipd.setLocationRelativeTo(null);
                ipd.show();
            } else {
                lb.showMessageDailog("Please select I.P.D patient first");
            }
        } else {
            lb.showMessageDailog("You don't have rights to open this form.");
        }
    }//GEN-LAST:event_jpanelCode17MouseClicked

    private void jpanelCode17MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode17MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode17MouseEntered

    private void jpanelCode18MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode18MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode18MouseExited

    private void jpanelCode18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode18MouseClicked
        // TODO add your handling code here:
        if (lb.getRight(213 + "", "VIEWS")) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                this.dispose();
                RemoveNote ipd = new RemoveNote(null, true, jTable2.getValueAt(row, 1).toString());
                ipd.setLocationRelativeTo(null);
                ipd.show();
            } else {
                lb.showMessageDailog("Please select I.P.D patient first");
            }
        } else {
            lb.showMessageDailog("You don't have rights to view this form.");
        }
    }//GEN-LAST:event_jpanelCode18MouseClicked

    private void jpanelCode18MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode18MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode18MouseEntered

    private void jpanelCodeMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCodeMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCodeMouseDragged

    private void jpanelCode19MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode19MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode19MouseExited

    private void jpanelCode19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode19MouseClicked
        // TODO add your handling code here:
        if (jScrollPane1.isVisible()) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                this.dispose();
                PatientInfo ipd = new PatientInfo(null, true, jTable1.getValueAt(row, 3).toString(), 1);
                ipd.setLocationRelativeTo(null);
                ipd.show();
            } else {
                lb.showMessageDailog("Please select O.P.D patient first");
            }
        } else if (jScrollPane2.isVisible()) {
            int row = jTable2.getSelectedRow();
            if (row != -1) {
                this.dispose();
                PatientInfo ipd = new PatientInfo(null, true, jTable2.getValueAt(row, 1).toString(), 0);
                ipd.setLocationRelativeTo(null);
                ipd.show();
            } else {
                lb.showMessageDailog("Please select I.P.D patient first");
            }
        }
    }//GEN-LAST:event_jpanelCode19MouseClicked

    private void jpanelCode19MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode19MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode19MouseEntered

    private void jpanelCode19jpanelCodeMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode19jpanelCodeMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode19jpanelCodeMouseDragged

    private void jpanelCode20MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode20MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode20MouseExited

    private void jpanelCode20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode20MouseClicked
        // TODO add your handling code here:
        OPDLatePayment ipd = new OPDLatePayment();
        addOnScreen(ipd, "IPD Advance Receipt", 24);
    }//GEN-LAST:event_jpanelCode20MouseClicked

    private void jpanelCode20MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode20MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode20MouseEntered

    private void jpanelCode20jpanelCodeMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpanelCode20jpanelCodeMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_jpanelCode20jpanelCodeMouseDragged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JPanel jpanelCode;
    private javax.swing.JPanel jpanelCode1;
    private javax.swing.JPanel jpanelCode10;
    private javax.swing.JPanel jpanelCode11;
    private javax.swing.JPanel jpanelCode12;
    private javax.swing.JPanel jpanelCode13;
    private javax.swing.JPanel jpanelCode14;
    private javax.swing.JPanel jpanelCode15;
    private javax.swing.JPanel jpanelCode16;
    private javax.swing.JPanel jpanelCode17;
    private javax.swing.JPanel jpanelCode18;
    private javax.swing.JPanel jpanelCode19;
    private javax.swing.JPanel jpanelCode2;
    private javax.swing.JPanel jpanelCode20;
    private javax.swing.JPanel jpanelCode3;
    private javax.swing.JPanel jpanelCode4;
    private javax.swing.JPanel jpanelCode5;
    private javax.swing.JPanel jpanelCode7;
    private javax.swing.JPanel jpanelCode8;
    private javax.swing.JPanel jpanelCode9;
    // End of variables declaration//GEN-END:variables
}
