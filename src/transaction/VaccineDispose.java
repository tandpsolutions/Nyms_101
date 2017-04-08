/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.CursorGlassPane;
import hms.HMS101;
import hms.HMSHome;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import support.DateSelect;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.PickList;
import support.ReportTable;
import support.S09ShowPopup;
import utility.VoucherDisplay;

/**
 *
 * @author BHAUMIK
 */
public class VaccineDispose extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    DefaultTableModel dtm = null;
    NavigationPanel navLoad = null;
    PickList itemPicklist = null;
    String ref_no = "";
    ReportTable table = null;
    Component oldGlass = null;
    CursorGlassPane glassPane = new CursorGlassPane();
    PickList acPickList = null;
    DateSelect ds = new DateSelect(null, true);

    /**
     * Creates new form VaccineStockAdd
     */
    public VaccineDispose() {
        initComponents();
        fillJcomboBox();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        dtm = (DefaultTableModel) jTable1.getModel();
        addNavigationPanel();
        setPickListView();
        addJtextBox();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "55");
    }

    private void fillJcomboBox() {
        try {
            jcmbSite.removeAllItems();
            jcmbSite.addItem("");
            String sql = "select site_name from sitemst";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            while (rsLcoal.next()) {
                jcmbSite.addItem(rsLcoal.getString("site_name"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at fillJcombobox at site    master", ex);
        }
        jcmbSite.setEditable(true);
        JTextComponent editor = (JTextComponent) jcmbSite.getEditor().getEditorComponent();
        editor.setDocument(new S09ShowPopup(jcmbSite, jbtnAdd));
    }

    public VaccineDispose(String opd_no) {
        initComponents();
        fillJcomboBox();
        dtm = (DefaultTableModel) jTable1.getModel();
        addNavigationPanel();
        setPickListView();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel1(navLoad, "55");
        navLoad.callNew();
        jtxtOPDNumber.setText(opd_no);
        setData();
        addJtextBox();
        setTable();
    }

    private void setPickListView() {
        itemPicklist = new PickList(dataConnection);

        itemPicklist.setLayer(this.getLayeredPane());
        itemPicklist.setPickListComponent(jtxtItem);
        itemPicklist.setReturnComponent(new JTextField[]{jtxtItem, jtxtQty, jtxtBatch, jtxtExpDate});
        itemPicklist.setNextComponent(jcmbSite);

        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {
                if (getMode().equalsIgnoreCase("N")) {
                    ds = new DateSelect(null, true);
                }
                ds.setVisible(true);
                if (ds.getReturnStatus() == ds.RET_OK) {
                    try {
                        Date dt = lb.userFormat.parse(ds.jtxtFromDate.getText());
                        dt = new Date(dt.getTime() - 2 * 24 * 3600 * 1000); //Subtract n days

                    } catch (ParseException ex) {

                    }
                    String sql = "";
                    if (getMode().equalsIgnoreCase("N")) {
                        ref_no = lb.generateKey("stkdsp", "ref_no", 7, "SD");
                    }
                    sql = "delete from stkdsp where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from oldb0_3 where ref_no='" + ref_no + "'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from next_itt where ref_no='" + ref_no + "'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "insert into stkdsp (ref_no,sr_no,v_date,bill_item_cd,pur_qty,batch_no,"
                            + "exp_date,opd_no,doc_cd,site_cd) values (?,?,?,?,?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.setString(3, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        pstLocal.setInt(2, i + 1);
                        pstLocal.setString(4, lb.getbillitemCode(jTable1.getValueAt(i, 1).toString(), "C"));
                        pstLocal.setInt(5, (int) lb.isNumber(jTable1.getValueAt(i, 2).toString()));
                        pstLocal.setString(6, jTable1.getValueAt(i, 3).toString());
                        pstLocal.setString(7, lb.ConvertDateFormetForDB(jTable1.getValueAt(i, 4).toString()));
                        pstLocal.setString(8, jtxtOPDNumber.getText());
                        pstLocal.setString(9, lb.getAcCode(jtxtDocAlias.getText(), "AC"));
                        pstLocal.setString(10, lb.getSiteCD(jTable1.getValueAt(i, 5).toString(), "C"));
                        pstLocal.executeUpdate();
                    }

                    sql = "insert into next_itt (ref_no,sr_no,bill_item_cd,next_due_date,alert_date,msg_id"
                            + ") values (?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.setString(4, lb.ConvertDateFormetForDB(ds.jtxtFromDate.getText()));
                    for (int i = 0; i < ds.jTable1.getRowCount(); i++) {
                        pstLocal.setInt(2, i + 1);
                        pstLocal.setString(3, lb.getbillitemCode(ds.jTable1.getValueAt(i, 0).toString(), "C"));
                        pstLocal.setString(5, null);
                        pstLocal.setString(6, "");
                        pstLocal.executeUpdate();
                    }
                    sql = "insert into oldb0_3 (ref_no,v_date,bill_item_cd,QTY,batch_no,doc_cd,opd_no) values (?,?,?,?,?,?,?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, ref_no);
                    pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        pstLocal.setString(3, lb.getbillitemCode(jTable1.getValueAt(i, 1).toString(), "C"));
                        pstLocal.setInt(4, (int) lb.isNumber(jTable1.getValueAt(i, 2).toString()));
                        pstLocal.setString(5, jTable1.getValueAt(i, 3).toString());
                        pstLocal.setString(6, "SAL");
                        pstLocal.setString(7, jtxtOPDNumber.getText());
                        pstLocal.executeUpdate();
                    }
                } else {
                    throw new SQLException("User press cancel");
                }

            }

            @Override
            public void callDelete() throws Exception {
                lb.confirmDialog("Do you want to delete this voucher?");
                if (lb.type) {
                    String sql = "delete from stkdsp where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "delete from oldb0_3 where ref_no='" + ref_no + "'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();
                }
            }

            @Override
            public void callView() {
                String sql = "SELECT ref_no,v_date,b.bill_item_name,m.exp_date,m.batch_no,m.pur_qty "
                        + "FROM stkdsp m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ref_no + "", "Stock Entry Vaccine View", sql, "55", 1, VaccineDispose.this, "Stock Entry Vaccine View", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Stock Entry Vaccine", -1);
            }

            @Override
            public void callPrint() {
                VoucherDisplay vd = new VoucherDisplay(ref_no, "vcc");
                HMSHome.addOnScreen(vd, "Voucher Display", -1);
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabled(false);
                lb.setUserRightsToPanel1(navLoad, "55");
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("SELECT site_cd,doc_cd,opd_no,ref_no,sr_no,v_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM stkdsp m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select min(ref_no) from stkdsp)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("SELECT site_cd,doc_cd,opd_no,ref_no,sr_no,v_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM stkdsp m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select max(ref_no) from stkdsp where ref_no <'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("SELECT site_cd,doc_cd,opd_no,ref_no,sr_no,v_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM stkdsp m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select min(ref_no) from stkdsp where ref_no >'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("SELECT site_cd,doc_cd,opd_no,ref_no,sr_no,v_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM stkdsp m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no=(select max(ref_no) from stkdsp)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("SELECT site_cd,doc_cd,opd_no,ref_no,sr_no,v_date,m.exp_date,m.batch_no,b.bill_item_name,m.pur_qty FROM stkdsp m LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd where ref_no='" + ref_no + "'");
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
                jtxtExpDate.setText("");
                jtxtRefNo.setText("");
                jtxtOPDNumber.setText("");
                jtxtDocAlias.setText("");
                jtxtDoctor.setText("");
                jcmbSite.getEditor().setItem("");
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
                jtxtExpDate.setEnabled(bFlag);
                jtxtOPDNumber.setEnabled(bFlag);
                jtxtBatch.setEnabled(bFlag);
                jbtnSelPat.setEnabled(bFlag);
                jtxtRefNo.setEnabled(!bFlag);
                jtxtDocAlias.setEnabled(bFlag);
                jtxtDoctor.setEnabled(bFlag);
                jcmbSite.setEnabled(bFlag);
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                ref_no = viewDataRs.getString("ref_no");
                jtxtRefNo.setText(viewDataRs.getString("ref_no"));
                jtxtOPDNumber.setText(viewDataRs.getString("opd_no"));
                setData();
                jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("v_date")));
                jtxtDocAlias.setText(lb.getAcCode(viewDataRs.getString("doc_cd"), "CA"));
                jtxtDoctor.setText(lb.getAcCode(viewDataRs.getString("doc_cd"), "N"));
                dtm.setRowCount(0);
                Vector row = new Vector();
                row.add(1);
                row.add(viewDataRs.getString("bill_item_name"));
                row.add(viewDataRs.getInt("pur_qty"));
                row.add(viewDataRs.getString("batch_no"));
                row.add(lb.ConvertDateFormetForDisply(viewDataRs.getString("exp_date")));
                row.add(lb.getSiteCD(viewDataRs.getString("site_cd"), "N"));
                dtm.addRow(row);
                while (viewDataRs.next()) {
                    row.add(jTable1.getRowCount() + 1);
                    row.add(viewDataRs.getString("bill_item_name"));
                    row.add(viewDataRs.getInt("pur_qty"));
                    row.add(viewDataRs.getString("batch_no"));
                    row.add(lb.ConvertDateFormetForDisply(viewDataRs.getString("exp_date")));
                    row.add(lb.getSiteCD(viewDataRs.getString("site_cd"), "N"));
                }

                viewDataRs = navLoad.fetchData("select * from next_itt where ref_no='" + ref_no + "'");
                while (viewDataRs.next()) {
                    row = new Vector();
                    row.add(lb.getbillitemCode(viewDataRs.getString("bill_item_cd"), "N"));
                    ds.dtm.addRow(row);
                    ds.jtxtFromDate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("next_due_date")));
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
                if (!lb.isExist("acntmst", "ac_alias", jtxtDocAlias.getText(), dataConnection)) {
                    jtxtDoctor.requestFocusInWindow();
                    navLoad.setMessage("Please select valid consultant doctor");
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
        jcmbSite.setVisible(false);
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

        jcmbSite.setBounds(0, 0, 20, 20);
        jcmbSite.setVisible(true);
        jPanel3.add(jcmbSite);

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
        table.AddColumn(3, "EXP Date", -1, java.lang.String.class, null, false);
        table.AddColumn(4, "Batch No", -1, java.lang.String.class, null, false);
        table.AddColumn(5, "Qty", -1, java.lang.Integer.class, null, false);
        table.makeTable();
    }

    private void setTable() {
        lb.setTable(jTable1, new JComponent[]{null, jtxtItem, jtxtQty, jtxtBatch, jtxtExpDate, jcmbSite});
    }

    private boolean validateRow() {
        if (!lb.isExist("billitemmst", "bill_item_name", jtxtItem.getText(), dataConnection)) {
            navLoad.setMessage("Item name does not exist in database");
            jtxtItem.requestFocusInWindow();
            return false;
        }

        if (lb.getSiteCD(jcmbSite.getSelectedItem().toString(), "C").equalsIgnoreCase("")
                || lb.getSiteCD(jcmbSite.getSelectedItem().toString(), "C").equalsIgnoreCase("0")) {
            jcmbSite.requestFocusInWindow();
            navLoad.setMessage("Please select valid site");
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
        jtxtExpDate.setText("");
        jcmbSite.getEditor().setItem("");
        navLoad.setMessage("");
    }

    private void addGlassPane() {
        oldGlass = this.getGlassPane();
        this.setGlassPane(glassPane);
        this.getGlassPane().setVisible(true);
    }

    private void removeGlassPane() {
        this.getGlassPane().setVisible(false);
        this.setGlassPane(oldGlass);
    }

    public void setData() {
        try {
            String sql = "SELECT p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name  "
                    + " FROM patientinfomst p1 LEFT JOIN patientmst p ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd "
                    + " LEFT JOIN areamst a ON p1.area_cd=a.area_cd LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd "
                    + " where p.opd_no='" + jtxtOPDNumber.getText() + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
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

        jPanelNavigation = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtxtRefNo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
        jbtnAdd = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jlblName = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlblCity = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlblSex = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlblRefBy = new javax.swing.JLabel();
        jlblArea = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jtxtOPDNumber = new javax.swing.JTextField();
        jbtnSelPat = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jtxtDoctor = new javax.swing.JTextField();
        jtxtDocAlias = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jtxtItem = new javax.swing.JTextField();
        jtxtQty = new javax.swing.JTextField();
        jtxtExpDate = new javax.swing.JTextField();
        jtxtBatch = new javax.swing.JTextField();
        jcmbSite = new javax.swing.JComboBox();

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

        jLabel3.setText("Name");

        jlblName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("City");

        jlblCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Sex");

        jlblSex.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setText("Area");

        jLabel6.setText("Ref BY");

        jlblRefBy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("OPD Number");

        jtxtOPDNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtOPDNumberKeyPressed(evt);
            }
        });

        jbtnSelPat.setText("Select Patient");
        jbtnSelPat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSelPatActionPerformed(evt);
            }
        });

        jLabel9.setText("Doctor Name");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnSelPat, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(159, 159, 159)
                                .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jtxtOPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlblArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnSelPat))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtOPDNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel7, jtxtRefNo, jtxtVdate});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtOPDNumber});

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Vaccine", "Qty", "Batch", "Exp Date", "Site"
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
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
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
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

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
        jPanel3.add(jtxtItem);

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
        jPanel3.add(jtxtQty);

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
        jPanel3.add(jtxtExpDate);

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
        jPanel3.add(jtxtBatch);

        jcmbSite.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcmbSite.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbSiteKeyPressed(evt);
            }
        });
        jPanel3.add(jcmbSite);

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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
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
            String sql = "SELECT b.bill_item_name as item,1 as qty,m.batch_no as batch,DATE_FORMAT(m.mfg_date, '%d/%m/%Y')"
                    + " as mfg,DATE_FORMAT(m.exp_date, '%d/%m/%Y') as exp,(SELECT SUM(CASE WHEN DOC_CD='PUR' THEN QTY"
                    + " ELSE QTY*1 END) AS opb FROM OLDB0_3 WHERE bill_item_cd=m.bill_item_cd "
                    + " AND batch_no=m.batch_no) AS qty FROM medstk m \n"
                    + " LEFT JOIN billitemmst b ON m.bill_item_cd=b.bill_item_cd \n"
                    + " WHERE 0 < (SELECT SUM(CASE WHEN DOC_CD='PUR' THEN QTY ELSE QTY*1 END) AS opb FROM OLDB0_3\n"
                    + " WHERE bill_item_cd=m.bill_item_cd) "
                    + " AND b.bill_item_name LIKE '%" + jtxtItem.getText() + "%' AND b.bill_grp_cd=8";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            itemPicklist.setPreparedStatement(pstLocal);
            itemPicklist.setFirstAssociation(new int[]{0, 1, 2, 3});
            itemPicklist.setSecondAssociation(new int[]{0, 1, 2, 3});
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
                jcmbSite.requestFocusInWindow();
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
                rowData.add(jtxtExpDate.getText());
                rowData.add(jcmbSite.getSelectedItem().toString());
                dtm.addRow(rowData);
            } else {
                jTable1.setValueAt(jtxtItem.getText(), row, 1);
                jTable1.setValueAt(jtxtQty.getText(), row, 2);
                jTable1.setValueAt(jtxtBatch.getText(), row, 3);
                jTable1.setValueAt(jtxtExpDate.getText(), row, 4);
                jTable1.setValueAt(jcmbSite.getSelectedItem().toString(), row, 5);
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
        if (!navLoad.getMode().equalsIgnoreCase("")) {
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
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (!navLoad.getMode().equalsIgnoreCase("")) {
            if (evt.getClickCount() == 2) {
                int row = jTable1.getSelectedRow();
                if (row != -1) {
                    jtxtItem.setText(jTable1.getValueAt(row, 1).toString());
                    jtxtQty.setText(jTable1.getValueAt(row, 2).toString());
                    jtxtBatch.setText(jTable1.getValueAt(row, 3).toString());
                    jcmbSite.setSelectedItem(jTable1.getValueAt(row, 4).toString());
                    jtxtExpDate.setText(jTable1.getValueAt(row, 5).toString());
                    jtxtItem.requestFocusInWindow();
                }
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
        lb.enterFocus(evt, jtxtExpDate);
    }//GEN-LAST:event_jtxtBatchKeyPressed

    private void jtxtOPDNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPDNumberKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            SwingWorker workerForjbtnGenerate = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    try {
                        addGlassPane();
                        setData();
                        jtxtItem.requestFocusInWindow();
                    } catch (Exception ex) {
                        lb.printToLogFile("Exception at saveVoucher at save area master", ex);
                    } finally {
                        removeGlassPane();
                    }
                    return null;
                }
            };
            workerForjbtnGenerate.execute();
        }
    }//GEN-LAST:event_jtxtOPDNumberKeyPressed

    private void jbtnSelPatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSelPatActionPerformed
        // TODO add your handling code here:
        JDialog d = new JDialog();
        d.setModal(true);
        SearchPatient sp = new SearchPatient(2, d);
        sp.setOpdBill(this);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("Search Patient");
        d.add(sp);
        d.setPreferredSize(new Dimension(sp.getWidth() + 20, sp.getHeight()));
        d.setLocationRelativeTo(this);
        d.setAlwaysOnTop(true);
        sp.setVisible(true);
        d.pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((screenSize.width - d.getWidth()) / 2, (screenSize.height - d.getHeight()) / 2);
        d.setVisible(true);
        jtxtOPDNumber.setText(sp.opd_no);
        setData();
    }//GEN-LAST:event_jbtnSelPatActionPerformed

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
        acPickList.setNextComponent(jtxtItem);
        acPickList.setLocation(jtxtDoctor.getX() + jPanel1.getX(), jtxtDoctor.getY() + jtxtDoctor.getHeight() + jPanel1.getY());
        acPickList.setReturnComponent(new JTextField[]{jtxtDoctor, jtxtDocAlias});
        acPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtDoctorKeyPressed

    private void jtxtDoctorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDoctorKeyReleased
        // TODO add your handling code here:
        try {
            PreparedStatement psLocal = dataConnection.prepareStatement("SELECT ac_name,ac_alias,s.spec_sub_name FROM acntmst a "
                    + " LEFT JOIN doctormaster d ON a.ac_cd=d.ac_cd LEFT JOIN specsubmst s ON d.sub_spec_cd=s.spec_sub_cd "
                    + " WHERE a.ac_cd IN (SELECT ac_cd FROM doctormaster) and  "
                    + " ac_name like  '%" + jtxtDoctor.getText().toUpperCase() + "%'");
            acPickList.setPreparedStatement(psLocal);
            acPickList.setFirstAssociation(new int[]{0, 1});
            acPickList.setSecondAssociation(new int[]{0, 1});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtDoctorKeyReleased

    private void jcmbSiteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbSiteKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jcmbSiteKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnSelPat;
    private javax.swing.JComboBox jcmbSite;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JTextField jtxtBatch;
    private javax.swing.JTextField jtxtDocAlias;
    private javax.swing.JTextField jtxtDoctor;
    private javax.swing.JTextField jtxtExpDate;
    private javax.swing.JTextField jtxtItem;
    public javax.swing.JTextField jtxtOPDNumber;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRefNo;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables

    public void setID(String strCode) {
        ref_no = strCode;
        navLoad.setVoucher("Edit");
    }
}
