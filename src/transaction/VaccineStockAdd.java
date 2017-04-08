/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.PickList;
import support.ReportTable;

/**
 *
 * @author BHAUMIK
 */
public class VaccineStockAdd extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    NavigationPanel navLoad = null;
    PickList itemPicklist = null;
    String ref_no = "";
    ReportTable table = null;

    /**
     * Creates new form VaccineStockAdd
     */
    public VaccineStockAdd() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        addNavigationPanel();
        setPickListView();
        addJtextBox();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "51");
    }

    private void setPickListView() {
        itemPicklist = new PickList(dataConnection);

        itemPicklist.setLayer(this.getLayeredPane());
        itemPicklist.setPickListComponent(jtxtItem);
        itemPicklist.setReturnComponent(new JTextField[]{jtxtItem, jtxtQty});
        itemPicklist.setNextComponent(jtxtQty);
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {
                String sql = "";
                if (getMode().equalsIgnoreCase("N")) {
                    ref_no = lb.generateKey("medstk", "ref_no", 7, "MS");
                }
                sql = "delete from medstk where ref_no='" + ref_no + "'";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.executeUpdate();

                sql = "delete from oldb0_3 where ref_no='" + ref_no + "'";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.executeUpdate();

                sql = "insert into medstk (ref_no,sr_no,v_date,bill_item_cd,pur_qty,batch_no,mfg_date,"
                        + "exp_date) values (?,?,?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ref_no);
                pstLocal.setString(3, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    pstLocal.setInt(2, i + 1);
                    pstLocal.setString(4, lb.getbillitemCode(jTable1.getValueAt(i, 1).toString(), "C"));
                    pstLocal.setInt(5, (int) lb.isNumber(jTable1.getValueAt(i, 2).toString()));
                    pstLocal.setString(6, jTable1.getValueAt(i, 3).toString());
                    pstLocal.setString(7, lb.ConvertDateFormetForDB(jTable1.getValueAt(i, 4).toString()));
                    pstLocal.setString(8, lb.ConvertDateFormetForDB(jTable1.getValueAt(i, 5).toString()));
                    pstLocal.executeUpdate();
                }

                sql = "insert into oldb0_3 (ref_no,v_date,bill_item_cd,QTY,batch_no,doc_cd) values (?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ref_no);
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    pstLocal.setString(3, lb.getbillitemCode(jTable1.getValueAt(i, 1).toString(), "C"));
                    pstLocal.setInt(4, (int) lb.isNumber(jTable1.getValueAt(i, 2).toString()));
                    pstLocal.setString(5, jTable1.getValueAt(i, 3).toString());
                    pstLocal.setString(6, "PUR");
                    pstLocal.executeUpdate();
                }

            }

            @Override
            public void callDelete() throws Exception {
                if (lb.getData("SELECT * FROM oldb0_3 WHERE doc_cd <>'PUR' AND "
                        + " batch_no NOT IN (SELECT batch_no FROM oldb0_3 WHERE ref_no='')")
                        .equalsIgnoreCase("")) {
                    lb.confirmDialog("Do you want to delete this voucher?");
                    if (lb.type) {
                        String sql = "delete from medstk where ref_no='" + ref_no + "'";
                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.executeUpdate();

                        sql = "delete from oldb0_3 where ref_no='" + ref_no + "'";
                        pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.executeUpdate();
                    }
                }
            }

            @Override
            public void callView() {
                String sql = "SELECT ref_no,v_date,b.bill_item_name,m.mfg_date,m.exp_date,m.batch_no,m.pur_qty "
                        + "FROM medstk m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "Stock Entry Vaccine View", sql, "51", 1, VaccineStockAdd.this, "Stock Entry Vaccine View", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Stock Entry Vaccine", -1);
            }

            @Override
            public void callPrint() {
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabled(false);
                lb.setUserRightsToPanel1(navLoad, "51");
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("SELECT ref_no,sr_no,v_date,m.mfg_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM medstk m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select min(ref_no) from medstk)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("SELECT ref_no,sr_no,v_date,m.mfg_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM medstk m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select max(ref_no) from medstk where ref_no <'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("SELECT ref_no,sr_no,v_date,m.mfg_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM medstk m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select min(ref_no) from medstk where ref_no >'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("SELECT ref_no,sr_no,v_date,m.mfg_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM medstk m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select max(ref_no) from medstk)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("SELECT ref_no,sr_no,v_date,m.mfg_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM medstk m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no='" + ref_no + "'");
                }
                try {
                    if (viewDataRs.next()) {
                        setComponentTextFromRs();
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher cityMaster'", ex);
                }

            }

            @Override
            public void setComponentText() {
                lb.setDateChooserPropertyInit(jtxtVdate);
                jtxtVdate.setText("");
                jbtnAdd.setText("");
                jtxtItem.setText("");
                jtxtQty.setText("");
                jtxtMFGDate.setText("");
                jtxtExpDate.setText("");
                jtxtRefNo.setText("");
                ref_no = "";
                jtxtItem.requestFocusInWindow();
                dtm.setRowCount(0);
            }

            @Override
            public void setComponentEnabled(boolean bFlag) {
                jtxtVdate.setEnabled(bFlag);
                jbtnAdd.setEnabled(bFlag);
                jtxtItem.setEnabled(bFlag);
                jtxtQty.setEnabled(bFlag);
                jtxtMFGDate.setEnabled(bFlag);
                jtxtExpDate.setEnabled(bFlag);
                jtxtBatch.setEnabled(bFlag);
                jtxtRefNo.setEnabled(!bFlag);
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                ref_no = viewDataRs.getString("ref_no");
                jtxtRefNo.setText(viewDataRs.getString("ref_no"));
                jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("v_date")));
                dtm.setRowCount(0);
                Vector row = new Vector();
                row.add(1);
                row.add(viewDataRs.getString("bill_item_name"));
                row.add(viewDataRs.getInt("pur_qty"));
                row.add(viewDataRs.getString("batch_no"));
                row.add(lb.ConvertDateFormetForDisply(viewDataRs.getString("mfg_date")));
                row.add(lb.ConvertDateFormetForDisply(viewDataRs.getString("exp_date")));
                dtm.addRow(row);
                while (viewDataRs.next()) {
                    row = new Vector();
                    row.add(jTable1.getRowCount() + 1);
                    row.add(viewDataRs.getString("bill_item_name"));
                    row.add(viewDataRs.getInt("pur_qty"));
                    row.add(viewDataRs.getString("batch_no"));
                    row.add(lb.ConvertDateFormetForDisply(viewDataRs.getString("mfg_date")));
                    row.add(lb.ConvertDateFormetForDisply(viewDataRs.getString("exp_date")));
                    dtm.addRow(row);
                }
            }

            @Override
            public boolean checkEdit() {
                return true;
            }

            @Override
            public boolean validateForm() {
                if (lb.checkDate2(jtxtVdate)) {
                    jtxtVdate.requestFocusInWindow();
                    navLoad.setMessage("Invalid voucher date");
                    return false;
                }
                return true;
            }

        }
        navLoad = new navPanel();
        jPanelNavigation.add(navLoad);
        navLoad.setVisible(true);
    }

    private void cancelOrClose() {
        if (navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            navLoad.setMode("");
            navLoad.setComponentEnabled(false);
            navLoad.setMessage("");
            navLoad.setSaveFlag(true);
            navLoad.setVoucher("Edit");
        }
    }

    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at city master", ex);
        }
    }

    private void addJtextBox() {
        jPanel3.removeAll();
        jtxtItem.setVisible(false);
        jtxtQty.setVisible(false);
        jtxtMFGDate.setVisible(false);
        jtxtBatch.setVisible(false);
        jtxtExpDate.setVisible(false);

        jtxtItem.setBounds(0, 0, 20, 20);
        jtxtItem.setVisible(true);
        jPanel3.add(jtxtItem);

        jtxtQty.setBounds(0, 0, 20, 20);
        jtxtQty.setVisible(true);
        jPanel3.add(jtxtQty);

        jtxtBatch.setBounds(0, 0, 20, 20);
        jtxtBatch.setVisible(true);
        jPanel3.add(jtxtBatch);

        jtxtMFGDate.setBounds(0, 0, 20, 20);
        jtxtMFGDate.setVisible(true);
        jPanel3.add(jtxtMFGDate);

        jtxtExpDate.setBounds(0, 0, 20, 20);
        jtxtExpDate.setVisible(true);
        jPanel3.add(jtxtExpDate);

        setTable();
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Ref NO", -1, java.lang.String.class, null, false);
        table.AddColumn(1, "Voucher Date", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Item Name", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "MFG Date", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "EXP Date", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Batch No", -1, java.lang.String.class, null, false);
        table.AddColumn(6, "Qty", -1, java.lang.Integer.class, null, false);
        table.makeTable();
    }

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{null, jtxtItem, jtxtQty, jtxtBatch, jtxtMFGDate, jtxtExpDate});
    }

    private boolean validateRow() {
        if (!lb.isExist("billitemmst", "bill_item_name", jtxtItem.getText(), dataConnection)) {
            navLoad.setMessage("Item name does not exist in database");
            jtxtItem.requestFocusInWindow();
            return false;
        }

        if (!lb.checkDate2(jtxtMFGDate)) {
            jtxtMFGDate.requestFocusInWindow();
            navLoad.setMessage("Invalid MFG Date");
            return false;
        }

        if (!lb.checkDate2(jtxtExpDate)) {
            jtxtExpDate.requestFocusInWindow();
            navLoad.setMessage("Invalid EXp Date");
            return false;
        }

        if (lb.isBlank(jtxtBatch)) {
            jtxtBatch.requestFocusInWindow();
            navLoad.setMessage("Batch cannot be left blank");
            return false;
        }
        return true;
    }

    private void clearRow() {
        jtxtItem.setText("");
        jtxtQty.setText("");
        jtxtBatch.setText("");
        jtxtMFGDate.setText("");
        jtxtExpDate.setText("");
        navLoad.setMessage("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNavigation = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtxtRefNo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
        jbtnAdd = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jtxtItem = new javax.swing.JTextField();
        jtxtQty = new javax.swing.JTextField();
        jtxtMFGDate = new javax.swing.JTextField();
        jtxtExpDate = new javax.swing.JTextField();
        jtxtBatch = new javax.swing.JTextField();

        jPanelNavigation.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Receipt No");

        jtxtRefNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRefNoKeyPressed(evt);
            }
        });

        jLabel7.setText("Date");

        jbtnAdd.setText("ADD");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });
        jbtnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnAddKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(292, 292, 292)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(159, 159, 159)
                .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel7, jtxtRefNo, jtxtVdate});

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Vaccine", "Qty", "Batch", "MFG Date", "Exp Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(500);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setPreferredSize(new java.awt.Dimension(443, 25));

        jtxtItem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtItemFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtItemFocusLost(evt);
            }
        });
        jtxtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtItemKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtItemKeyReleased(evt);
            }
        });

        jtxtQty.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jtxtQtyComponentMoved(evt);
            }
        });
        jtxtQty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtQtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtQtyFocusLost(evt);
            }
        });
        jtxtQty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtQtyKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtQtyKeyTyped(evt);
            }
        });

        jtxtMFGDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMFGDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMFGDateFocusLost(evt);
            }
        });
        jtxtMFGDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMFGDateKeyPressed(evt);
            }
        });

        jtxtExpDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtExpDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtExpDateFocusLost(evt);
            }
        });
        jtxtExpDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtExpDateKeyPressed(evt);
            }
        });

        jtxtBatch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBatchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBatchFocusLost(evt);
            }
        });
        jtxtBatch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBatchKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jtxtItem, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtMFGDate, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtxtBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMFGDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 884, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtRefNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefNoKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
            navLoad.setVoucher("Edit");
        }
    }//GEN-LAST:event_jtxtRefNoKeyPressed

    private void jtxtItemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtItemFocusGained

    private void jtxtItemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtItemFocusLost

    private void jtxtItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemKeyPressed
        // TODO add your handling code here:
        itemPicklist.setLocation(jtxtItem.getX() + jPanel3.getX(), jtxtItem.getY() + jtxtItem.getHeight() + jPanel3.getY());
        itemPicklist.pickListKeyPress(evt);
        //        lb.downFocus(evt, jtxtPaidAmt);
    }//GEN-LAST:event_jtxtItemKeyPressed

    private void jtxtItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemKeyReleased
        // TODO add your handling code here:
        try {
            String sql = "SELECT bill_item_name,0 as qty FROM billitemmst"
                    + " WHERE bill_item_name LIKE '%" + jtxtItem.getText().toUpperCase() + "%'"
                    + " and bill_grp_cd=8";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            itemPicklist.setPreparedStatement(pstLocal);
            itemPicklist.setFirstAssociation(new int[]{0, 1});
            itemPicklist.setSecondAssociation(new int[]{0, 1});
            itemPicklist.setValidation(dataConnection.prepareStatement("SELECT bill_item_name FROM billitemmst WHERE bill_item_name =? "
                    + "and bill_grp_cd=8"));
            itemPicklist.pickListKeyRelease(evt);
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_jtxtItemKeyReleased

    private void jtxtQtyComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtQtyComponentMoved
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_jtxtQtyComponentMoved

    private void jtxtQtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtQtyFocusGained

    private void jtxtQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtQtyFocusLost

    private void jtxtQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtBatch);
    }//GEN-LAST:event_jtxtQtyKeyPressed

    private void jtxtMFGDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMFGDateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMFGDateFocusGained

    private void jtxtMFGDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMFGDateFocusLost
        // TODO add your hndling code here:
        lb.checkDate(jtxtMFGDate);
    }//GEN-LAST:event_jtxtMFGDateFocusLost

    private void jtxtMFGDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMFGDateKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
            if (lb.isBlank(jtxtMFGDate)) {
                lb.setDateChooserPropertyInit(jtxtMFGDate);
            }
            if (lb.checkDate2(jtxtMFGDate)) {
                jtxtExpDate.requestFocusInWindow();
            } else {
                lb.showMessageDailog("Invalid Date");
                jtxtMFGDate.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtMFGDateKeyPressed

    private void jtxtExpDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExpDateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtExpDateFocusGained

    private void jtxtExpDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExpDateFocusLost
        // TODO add your handling code here:
        lb.checkDate(jtxtExpDate);
    }//GEN-LAST:event_jtxtExpDateFocusLost

    private void jtxtExpDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExpDateKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
            if (lb.isBlank(jtxtExpDate)) {
                lb.setDateChooserPropertyInit(jtxtExpDate);
            }
            if (lb.checkDate2(jtxtExpDate)) {
                jbtnAdd.requestFocusInWindow();
            } else {
                lb.showMessageDailog("Invalid Date");
                jtxtExpDate.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtExpDateKeyPressed

    private void jtxtQtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyTyped
        // TODO add your handling code here:
        lb.onlyInteger(evt, 5);
    }//GEN-LAST:event_jtxtQtyKeyTyped

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        // TODO add your handling code here:
        if (validateRow()) {
            int row = jTable1.getSelectedRow();
            if (row == -1) {
                Vector rowData = new Vector();
                rowData.add(jTable1.getRowCount() + 1);
                rowData.add(jtxtItem.getText());
                rowData.add(jtxtQty.getText());
                rowData.add(jtxtBatch.getText());
                rowData.add(jtxtMFGDate.getText());
                rowData.add(jtxtExpDate.getText());
                dtm.addRow(rowData);
            } else {
                jTable1.setValueAt(jtxtItem.getText(), row, 1);
                jTable1.setValueAt(jtxtQty.getText(), row, 2);
                jTable1.setValueAt(jtxtBatch.getText(), row, 3);
                jTable1.setValueAt(jtxtMFGDate.getText(), row, 4);
                jTable1.setValueAt(jtxtExpDate.getText(), row, 5);
            }
            clearRow();
            lb.confirmDialog("Do you want to add another row?");
            if (lb.type) {
                jtxtItem.requestFocusInWindow();
            } else {
                navLoad.setSaveFocus();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                lb.confirmDialog("Do you want to delte this row?");
                if (lb.type) {
                    dtm.removeRow(row);
                    clearRow();
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                jtxtItem.setText(jTable1.getValueAt(row, 1).toString());
                jtxtQty.setText(jTable1.getValueAt(row, 2).toString());
                jtxtBatch.setText(jTable1.getValueAt(row, 3).toString());
                jtxtMFGDate.setText(jTable1.getValueAt(row, 4).toString());
                jtxtExpDate.setText(jTable1.getValueAt(row, 5).toString());
                jtxtItem.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtBatchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBatchFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBatchFocusGained

    private void jtxtBatchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBatchFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtBatchFocusLost

    private void jtxtBatchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBatchKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMFGDate);
    }//GEN-LAST:event_jtxtBatchKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JTextField jtxtBatch;
    private javax.swing.JTextField jtxtExpDate;
    private javax.swing.JTextField jtxtItem;
    private javax.swing.JTextField jtxtMFGDate;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRefNo;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables

    public void setID(String strCode) {
        ref_no = strCode;
        navLoad.setVoucher("Edit");
    }
}
