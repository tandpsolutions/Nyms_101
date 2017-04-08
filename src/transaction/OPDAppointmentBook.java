/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.Library;
import support.PickList;

/**
 *
 * @author Bhaumik
 */
public class OPDAppointmentBook extends javax.swing.JInternalFrame {

    String opdNo = "";
    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    PickList acPickList = null;
    DefaultTableModel dtm = null;
    int mode = -1;

    /**
     * Creates new form OPDAppointmentBook
     *
     * @param opdNo
     */
    public OPDAppointmentBook(String opdNo) {
        initComponents();
        this.opdNo = opdNo;
        dtm = (DefaultTableModel) jTable1.getModel();
        lb.setDateChooserPropertyInit(jtxtVdate);
        setData();
        updateList();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        jtxtDoctor.requestFocusInWindow();
        lb.setDateChooserPropertyInit(jtxtAptDate);
        String timeStamp = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());
        jtxtAptTime.setText(timeStamp);
        jtxtOPDNo.setEditable(false);
        jbtnRevertCan.setEnabled(false);
//        if (opdNo.equalsIgnoreCase("")) {
//            jPanel2.setVisible(false);
//            jbtnConfApp.setEnabled(false);
//        }
    }

    public OPDAppointmentBook(int mode) {
        initComponents();
        this.mode = mode;
        dtm = (DefaultTableModel) jTable1.getModel();
        lb.setDateChooserPropertyInit(jtxtVdate);
        updateList();
        acPickList = new PickList(dataConnection);
        acPickList.setLayer(this.getLayeredPane());
        jtxtDoctor.requestFocusInWindow();
        lb.setDateChooserPropertyInit(jtxtAptDate);
        String timeStamp = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());
        jtxtAptTime.setText(timeStamp);
        jtxtOPDNo.setEditable(false);
        if (mode == 0) {
            jPanel2.setVisible(false);
            jbtnConfApp.setEnabled(false);
            jbtnCancelAppt.setEnabled(false);
            jbtnGnBill.setEnabled(false);
        }
    }

    private void setData() {
        try {
            String sql = "SELECT p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name  "
                    + " FROM patientinfomst p1 LEFT JOIN patientmst p ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd "
                    + " LEFT JOIN areamst a ON p1.area_cd=a.area_cd LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd where p.opd_no='" + opdNo + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jtxtOPDNo.setText(opdNo);
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

    private void updateList() {
        try {
            String sql = "";
            if (mode == -1) {
                sql = "SELECT ref_no,p.opd_no,p.pt_name,a1.ac_name,a.token_no,a.appoint_time,"
                        + " CASE WHEN a.case_type = 0 THEN 'New' ELSE 'Follow' END AS case_type FROM appointmentmst a "
                        + " LEFT JOIN patientmst p ON a.opd_no=p.opd_no LEFT JOIN acntmst a1 ON a.cons_doc = a1.ac_cd"
                        + " where a.appoint_date = '" + lb.ConvertDateFormetForDB(jtxtVdate.getText()) + "' and is_can=0 "
                        + " and ref_no not in (select appoint_no from opdbillhd)";
            } else if (mode == 0) {

                sql = "SELECT ref_no,p.opd_no,p.pt_name,a1.ac_name,a.token_no,a.appoint_time,"
                        + " CASE WHEN a.case_type = 0 THEN 'New' ELSE 'Follow' END AS case_type FROM appointmentmst a "
                        + " LEFT JOIN patientmst p ON a.opd_no=p.opd_no LEFT JOIN acntmst a1 ON a.cons_doc = a1.ac_cd"
                        + " where a.appoint_date = '" + lb.ConvertDateFormetForDB(jtxtVdate.getText()) + "' and is_can=1 "
                        + " and ref_no not in (select appoint_no from opdbillhd)";

            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            int i = 1;
            dtm.setRowCount(0);
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
                dtm.addRow(row);
                i++;
            }
            lb.setColumnSizeForTable(jTable1, jPanel4.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
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

    private boolean validateData() {
        if (!lb.isExist("acntmst", "ac_alias", jtxtDocAlias.getText(), dataConnection)) {
            jtxtDoctor.requestFocusInWindow();
            lb.showMessageDailog("Please select valid consultant doctor");
            return false;
        }

        if (lb.checkDate2(jtxtAptDate)) {
            lb.showMessageDailog("Invalid date");
            jtxtAptDate.requestFocusInWindow();
            return false;
        }

        if (!lb.getData("SELECT * FROM appointmentmst a WHERE a.appoint_date='" + lb.ConvertDateFormetForDB(jtxtAptDate.getText()) + "' "
                + "AND a.opd_no='" + jtxtOPDNo.getText() + "'").equalsIgnoreCase("")) {
            lb.confirmDialog("Appointment already book for this patient. \n Do you want to addanother appointment?");
            return lb.type;
        }
        return true;
    }

    private int generateTokenNO() throws SQLException {
        String sql = "select max(token_no) from appointmentmst where appoint_date='" + lb.ConvertDateFormetForDB(jtxtAptDate.getText()) + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            int no = (int) lb.isNumber(rsLocal.getString(1));
            no++;
            return no;
        } else {
            return -1;
        }
    }

    private void saveVoucher() {
        try {
            String ref_no = lb.generateKey("appointmentmst", "ref_no", 7, "OPD");
            int token = generateTokenNO();
            if (jTable1.getSelectedRow() != -1) {
                String sql = "delete from appointmentmst where ref_no='" + jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString() + "'";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.executeUpdate();
                ref_no = jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString();
                token = (int) lb.isNumber(jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString());
            }
            String sql = "insert into appointmentmst (ref_no,opd_no,appoint_date,cons_doc,appoint_time,case_type,token_no,user_id)"
                    + "  values (?,?,?,?,?,?,?,?)";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, ref_no);
            pstLocal.setString(2, jtxtOPDNo.getText());
            pstLocal.setString(3, lb.ConvertDateFormetForDB(jtxtAptDate.getText()));
            pstLocal.setString(4, lb.getAcCode(jtxtDocAlias.getText(), "AC"));
            String time = jtxtAptTime.getText().replaceAll("\\.", ":") + ":00";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date date = null;
            date = sdf.parse(time);
            pstLocal.setString(5, sdf.format(date));
            pstLocal.setInt(6, jcmbPurpose.getSelectedIndex());
            pstLocal.setInt(7, token);
            pstLocal.setInt(8, HMSHome.user_id);
            pstLocal.executeUpdate();
            lb.showMessageDailog("Appointment has been booked sucessfully. Automatetoken number is " + token);
            opdNo = "";
            updateList();
            jtxtOPDNo.setText(opdNo);
            jlblName.setText("");
            jlblRefBy.setText("");
            jlblCity.setText("");
            jlblArea.setText("");
            jlblSex.setText("");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at saveVoucher", ex);
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
        jlblName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jlblCity = new javax.swing.JLabel();
        jtxtOPDNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jlblSex = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlblArea = new javax.swing.JLabel();
        jlblRefBy = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jtxtDoctor = new javax.swing.JTextField();
        jtxtDocAlias = new javax.swing.JTextField();
        jtxtSpeciality = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jtxtAptTime = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jcmbPurpose = new javax.swing.JComboBox();
        jtxtAptDate = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jbtnConfApp = new javax.swing.JButton();
        jbtnCancelAppt = new javax.swing.JButton();
        jbtnGnBill = new javax.swing.JButton();
        jbtnRevertCan = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlblName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Sex");

        jlblCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Ref BY");

        jLabel1.setText("OPD Number");

        jlblSex.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("Area");

        jlblArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblRefBy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Name");

        jLabel5.setText("City");

        jbtnClose.setMnemonic('C');
        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jLabel21.setText("Date");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(434, 434, 434)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jtxtVdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(203, 203, 203)
                                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnClose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel21, jbtnClose, jtxtOPDNo, jtxtVdate});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4, jlblName, jlblRefBy, jlblSex});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jLabel6, jlblArea, jlblCity});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        jLabel18.setText("Appointment Date");

        jLabel19.setText("Appointment Time");

        jtxtAptTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAptTimeFocusGained(evt);
            }
        });
        jtxtAptTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAptTimeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAptTimeKeyTyped(evt);
            }
        });

        jLabel20.setText("Purpose of App.");

        jcmbPurpose.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "New Case", "Follow Up" }));
        jcmbPurpose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbPurposeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcmbPurpose, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtAptDate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel19, jLabel20, jLabel7});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtDocAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jtxtAptDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbPurpose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel19, jLabel20, jcmbPurpose});

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jbtnConfApp.setText("Confirm Apointment");
        jbtnConfApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnConfAppActionPerformed(evt);
            }
        });

        jbtnCancelAppt.setText("Cancel Appointment");
        jbtnCancelAppt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelApptActionPerformed(evt);
            }
        });

        jbtnGnBill.setText("Generate Bill");
        jbtnGnBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnGnBillActionPerformed(evt);
            }
        });

        jbtnRevertCan.setText("Revert Cancelation");
        jbtnRevertCan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRevertCanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jbtnConfApp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnCancelAppt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnGnBill, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jbtnRevertCan)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnConfApp)
                    .addComponent(jbtnCancelAppt)
                    .addComponent(jbtnGnBill))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnRevertCan)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jPanel4.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ref_no", "Sr No", "Token No", "OPD No", "Patient Name", "Time", "Doctor", "For"
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
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
        }

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 122, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

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
        acPickList.setNextComponent(jtxtAptDate);
        acPickList.setLocation(jtxtDoctor.getX() + jPanel2.getX(), jtxtDoctor.getY() + jtxtDoctor.getHeight() + jPanel2.getY());
        acPickList.setReturnComponent(new JTextField[]{jtxtDoctor, jtxtDocAlias, jtxtSpeciality});
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
            acPickList.setFirstAssociation(new int[]{0, 1, 2});
            acPickList.setSecondAssociation(new int[]{0, 1, 2});
            acPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Error at txtcityKeyReleased in account master", ex);
        }
    }//GEN-LAST:event_jtxtDoctorKeyReleased

    private void jbtnConfAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConfAppActionPerformed
        // TODO add your handling code here:
        if (validateData()) {
            String msg = jlblName.getText() + "'s appointment has been booked on " + jtxtAptDate.getText() + " at " + jtxtAptTime.getText() + ".";
            lb.confirmDialog(msg);
            if (lb.type) {
                saveVoucher();
            }
            jTable1.clearSelection();
        }
    }//GEN-LAST:event_jbtnConfAppActionPerformed

    private void jtxtAptTimeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAptTimeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAptTimeFocusGained

    private void jtxtAptTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbPurpose);
    }//GEN-LAST:event_jtxtAptTimeKeyPressed

    private void jtxtAptTimeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 5);
    }//GEN-LAST:event_jtxtAptTimeKeyTyped

    private void jbtnCancelApptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelApptActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            lb.confirmDialog("Do you want to cancel this appointment?");
            if (lb.type) {
                String ref_no = jTable1.getValueAt(row, 0).toString();
                try {
                    String sql = "update appointmentmst set is_can = 1 where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();
                    updateList();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at cancel appointment.", ex);
                }
            }
        } else {
            lb.showMessageDailog("Please select appointment first.");
        }
    }//GEN-LAST:event_jbtnCancelApptActionPerformed

    private void jbtnGnBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGnBillActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            this.dispose();
            OPDBillGeneration opbBill = new OPDBillGeneration(jTable1.getValueAt(row, 3).toString(), jTable1.getValueAt(row, 0).toString(), "VO");
            HMSHome.addOnScreen(opbBill, "OPD Bill Generation Book", 24);
        } else {
            lb.showMessageDailog("Please select appointment first.");
        }
    }//GEN-LAST:event_jbtnGnBillActionPerformed

    private void jcmbPurposeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbPurposeKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jbtnConfApp.requestFocusInWindow();
        }
    }//GEN-LAST:event_jcmbPurposeKeyPressed

    private void jbtnRevertCanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRevertCanActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            lb.confirmDialog("Do you want to revert this cancelation?");
            if (lb.type) {
                String ref_no = jTable1.getValueAt(row, 0).toString();
                try {
                    String sql = "update appointmentmst set is_can = 0 where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();
                    updateList();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at cancel appointment.", ex);
                }
            }
        } else {
            lb.showMessageDailog("Please select appointment first.");
        }
    }//GEN-LAST:event_jbtnRevertCanActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 2){
            int row = jTable1.getSelectedRow();
            opdNo = jTable1.getValueAt(row, 3).toString();
            setData();
            jtxtAptDate.setText(jtxtVdate.getText());
            jtxtAptTime.setText(jTable1.getValueAt(row, 5).toString());
            jtxtDoctor.setText(jTable1.getValueAt(row, 6).toString());
            jtxtDocAlias.setText(lb.getAcCode(lb.getAcCode(jTable1.getValueAt(row, 6).toString(), "C"),"CA"));
            if(jTable1.getValueAt(row, 7).toString().equalsIgnoreCase("Follow")){
                jcmbPurpose.setSelectedIndex(1);
            } else {
                jcmbPurpose.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnCancelAppt;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnConfApp;
    private javax.swing.JButton jbtnGnBill;
    private javax.swing.JButton jbtnRevertCan;
    private javax.swing.JComboBox jcmbPurpose;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblSex;
    private com.toedter.calendar.JDateChooser jtxtAptDate;
    private javax.swing.JTextField jtxtAptTime;
    private javax.swing.JTextField jtxtDocAlias;
    private javax.swing.JTextField jtxtDoctor;
    private javax.swing.JTextField jtxtOPDNo;
    private javax.swing.JTextField jtxtSpeciality;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables
}
