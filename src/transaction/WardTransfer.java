/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import hms.HMS101;
import hms.HMSHome;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.Library;
import support.NavigationPanel;

/**
 *
 * @author Lenovo
 */
public class WardTransfer extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    NavigationPanel navLoad = null;
    String ref_no = "";
    String cur_bed_no = "";
    DefaultTableModel dtmWardMaster = null;

    /**
     * Creates new form WardTransfer
     *
     * @param ipd_no
     */
    public WardTransfer(String ipd_no) {
        initComponents();
        addNavigationPanel();
        dtmWardMaster = (DefaultTableModel) jTable3.getModel();
        navLoad.callNew();
        jtxtIPDNo.setText(ipd_no);
        setData(lb.getData("opd_no", "ipdreg", "ipd_no", ipd_no, 0));
        addInitialData();
        jtxtVdate.requestFocusInWindow();
    }

    public WardTransfer() {
        initComponents();
        addNavigationPanel();
        dtmWardMaster = (DefaultTableModel) jTable3.getModel();
        addInitialData();
        navLoad.setVoucher("Last");
    }

    private void addInitialData() {
        try {
            String sql = "SELECT ward_cd,ward_name FROM wardmst";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                Vector row = new Vector();
                row.add(rsLocal.getInt("ward_cd"));
                row.add(rsLocal.getString("ward_name"));
                dtmWardMaster.addRow(row);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at addInitial Data ad OPDBillGeneration", ex);
        }
    }

    private void setData(String opd_no) {
        try {
            String sql = "SELECT p.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex,c.city_name,a.area_name, a1.ac_name  "
                    + " FROM patientinfomst p1 LEFT JOIN patientmst p ON p.opd_no=p1.opd_no LEFT JOIN citymst c ON p1.city_cd = c.city_cd "
                    + " LEFT JOIN areamst a ON p1.area_cd=a.area_cd LEFT JOIN acntmst a1 ON p.ref_by= a1.ac_cd where p.opd_no='" + opd_no + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jtxtOPDNo.setText(opd_no);
                jlblName.setText(" " + rsLcoal.getString("pt_name"));
                jlblRefBy.setText(" " + rsLcoal.getString("ac_name"));
                jlblCity.setText(" " + rsLcoal.getString("city_name"));
                jlblArea.setText(" " + rsLcoal.getString("area_name"));
                jlblSex.setText(" " + rsLcoal.getString("Sex"));
            }

            sql = "SELECT r.room_cd,ward_name,o.v_date,o.v_time FROM oldb0_2 o LEFT JOIN roommst r ON o.room_cd=r.room_cd LEFT JOIN wardmst w ON r.ward_cd=w.ward_cd "
                    + " WHERE ipd_no='" + jtxtIPDNo.getText() + "' AND o.doc_cd<>'TRI' ORDER BY v_date DESC ,rec_no DESC,v_time DESC ";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLcoal = pstLocal.executeQuery();
            if (rsLcoal.next()) {
                jlblPrevAdmitDate.setText(lb.ConvertDateFormetForDisply(rsLcoal.getString("v_date")));
                jlblPrevAdmitTime.setText(rsLcoal.getString("v_time"));
                jlblPrevWardName.setText(rsLcoal.getString("WARD_NAME"));
                jlblPrevBedNo.setText(rsLcoal.getString("room_cd"));
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData in appointment Book", ex);
        }
    }

    private void addNavigationPanel() {
        class navPanel extends NavigationPanel {

            @Override
            public void callSave() throws Exception {

                if (getMode().equalsIgnoreCase("N")) {
                    ref_no = lb.generateKey("oldb0_2", "ref_no", 7, "TR");
                } else if (getMode().equalsIgnoreCase("E")) {

                    String sql = "delete from oldb0_2 where ref_no='" + ref_no + "'";
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    sql = "update roommst set opd_no=?,is_del =0 where room_cd=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, null);
                    pstLocal.setString(2, cur_bed_no);
                    pstLocal.executeUpdate();
                }
                String sql = "delete from oldb0_2 where ref_no='" + ref_no + "'";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.executeUpdate();

                String time = jtxtAptTime.getText().replaceAll("\\.", ":") + ":00";
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date date = null;
                date = sdf.parse(time);

                sql = "insert into oldb0_2 (ref_no,v_date,doc_cd,room_cd,v_time,ipd_no) values (?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ref_no);
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                pstLocal.setString(3, "TRI");
                pstLocal.setString(4, jlblPrevBedNo.getText());
                pstLocal.setString(5, sdf.format(date));
                pstLocal.setString(6, jtxtIPDNo.getText());
                pstLocal.executeUpdate();

                sql = "insert into oldb0_2 (ref_no,v_date,doc_cd,room_cd,v_time,ipd_no) values (?,?,?,?,?,?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, ref_no);
                pstLocal.setString(2, lb.ConvertDateFormetForDB(jtxtVdate.getText()));
                pstLocal.setString(3, "TRR");
                pstLocal.setString(4, jlblBedNo.getText());
                pstLocal.setString(5, sdf.format(date));
                pstLocal.setString(6, jtxtIPDNo.getText());
                pstLocal.executeUpdate();

                sql = "update roommst set opd_no=?,is_del =1 where room_cd=?";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, jtxtOPDNo.getText());
                pstLocal.setString(2, jlblBedNo.getText());
                pstLocal.executeUpdate();

                sql = "update roommst set opd_no=?,is_del =0 where room_cd=?";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, null);
                pstLocal.setString(2, jlblPrevBedNo.getText());
                pstLocal.executeUpdate();
            }

            @Override
            public void callDelete() throws Exception {
                if (lb.getData("SELECT COUNT(*) FROM oldb0_2 WHERE ref_no>'" + ref_no + "'").equalsIgnoreCase("0")) {

                    lb.confirmDialog("Do you want to delete this transfer note?");
                    if (lb.type) {
                        String sql = "delete from oldb0_2 where ref_no='" + ref_no + "'";
                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.executeUpdate();

                        sql = "update roommst set opd_no=?,is_del =0 where room_cd=?";
                        pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.setString(1, null);
                        pstLocal.setString(2, jlblBedNo.getText());
                        pstLocal.executeUpdate();

                        sql = "update roommst set opd_no=?,is_del =1 where room_cd=?";
                        pstLocal = dataConnection.prepareStatement(sql);
                        pstLocal.setString(1, jtxtOPDNo.getText());
                        pstLocal.setString(2, jlblPrevBedNo.getText());
                        pstLocal.executeUpdate();
                    }
                } else {
                    setMessage("New Transfer note has been generated for this IPD");
                }
            }

            @Override
            public void callView() {
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
                if (tag.equalsIgnoreCase("First")) {
                    viewDataRs = fetchData("select * from oldb0_2 a where a.doc_cd ='TRR' and a.doc_cd <>'ADT' and a.ref_no=(select min(ref_no) from oldb0_2)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewDataRs = fetchData("select * from oldb0_2 a where a.doc_cd ='TRR' and a.doc_cd <>'ADT' and a.ref_no=(select max(ref_no) from oldb0_2 where ref_no <'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewDataRs = fetchData("select * from oldb0_2 a where a.doc_cd ='TRR' and a.doc_cd <>'ADT' and a.ref_no=(select min(ref_no) from oldb0_2 where ref_no >'" + ref_no + "')");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewDataRs = fetchData("select * from oldb0_2 a where a.doc_cd ='TRR' and a.doc_cd <>'ADT' and a.ref_no=(select max(ref_no) from oldb0_2)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewDataRs = fetchData("select * from oldb0_2 a where a.doc_cd ='TRR' and a.doc_cd <>'ADT' and a.ref_no='" + ref_no + "'");
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
                Component[] cont = jPanel1.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JLabel) {
                        if (((JLabel) cont[i]).getBorder() != null) {
                            ((JLabel) cont[i]).setText("");
                        }
                    } else if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setText("");
                    }
                }

                cont = jPanel2.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JLabel) {
                        if (((JLabel) cont[i]).getBorder() != null) {
                            ((JLabel) cont[i]).setText("");
                        }
                    } else if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setText("");
                    }
                }
                jPanel6.removeAll();
                lb.setDateChooserPropertyInit(jtxtVdate);
                String timeStamp = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());
                jtxtAptTime.setText(timeStamp);
                jtxtVdate.requestFocusInWindow();
            }

            @Override
            public void setComponentEnabled(boolean bFlag) {
                Component[] cont = jPanel1.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setEnabled(bFlag);
                    }
                }

                cont = jPanel2.getComponents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] instanceof JTextField) {
                        ((JTextField) cont[i]).setEnabled(bFlag);
                    } else if (cont[i] instanceof JButton) {
                        ((JButton) cont[i]).setEnabled(bFlag);
                    }
                }
                jTable3.setEnabled(bFlag);
                if (!bFlag) {
                    jPanel6.removeAll();
                }
            }

            @Override
            public void setComponentTextFromRs() throws Exception {
                setMessage("");
                ref_no = viewDataRs.getString("ref_no");
                cur_bed_no = viewDataRs.getString("room_cd");
                jtxtTransferNo.setText(ref_no);
                jtxtIPDNo.setText(viewDataRs.getString("IPD_NO"));
                jtxtVdate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("V_date")));
                jtxtAptTime.setText(viewDataRs.getString("V_TIME"));
                setData(lb.getData("opd_no", "ipdreg", "ipd_no", viewDataRs.getString("IPD_NO"), 0));
                jlblWardName.setText(lb.getData("ward_name", "wardmst", "ward_cd", lb.getData("ward_cd", "roommst", "room_cd", viewDataRs.getString("room_cd"), 0), 1));
                jlblBedNo.setText(viewDataRs.getString("room_cd"));
                viewDataRs = fetchData("select * from oldb0_2 a where a.doc_cd <>'TRI' and a.ref_no<'" + ref_no + "' ORDER BY doc_cd desc");
                if (viewDataRs.next()) {
                    jlblPrevAdmitDate.setText(lb.ConvertDateFormetForDisply(viewDataRs.getString("V_date")));
                    jlblPrevAdmitTime.setText(viewDataRs.getString("V_TIME"));
                    jlblPrevBedNo.setText(viewDataRs.getString("room_cd"));
                    jlblPrevWardName.setText(lb.getData("ward_name", "wardmst", "ward_cd", lb.getData("ward_cd", "roommst", "room_cd", viewDataRs.getString("room_cd"), 0), 1));
                }
            }

            @Override
            public boolean checkEdit() {
                return false;
            }

            @Override
            public boolean validateForm() {
                if (lb.checkDate2(jtxtVdate)) {
                    lb.showMessageDailog("Invalid date");
                    jtxtVdate.requestFocusInWindow();
                    return false;
                }

                if (!lb.isExist("wardmst", "ward_name", jlblWardName.getText(), dataConnection)) {
                    jTable3.requestFocusInWindow();
                    lb.showMessageDailog("Please select valid ward");
                    return false;
                }

                if (!lb.isExist("roommst", "room_cd", jlblBedNo.getText(), dataConnection)) {
                    jTable3.requestFocusInWindow();
                    lb.showMessageDailog("Please select valid bed.");
                    return false;
                }

                if (lb.isBlank(jtxtIPDNo)) {
                    jtxtIPDNo.requestFocusInWindow();
                    navLoad.setMessage("Patient name can not be left blank");
                    return false;
                }

//                if(jlblBedNo.getText().equalsIgnoreCase(jlblPrevBedNo.getText())){
//                    navLoad.setMessage("Bed Number can not be same");
//                    return false;
//                }
                return true;
            }

        }
        navLoad = new navPanel();
        jPanelNavigation.add(navLoad);
        navLoad.setVisible(true);
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
        jlblName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jlblCity = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jlblSex = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlblArea = new javax.swing.JLabel();
        jlblRefBy = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtOPDNo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtxtIPDNo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jtxtTransferNo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jlblPrevAdmitDate = new javax.swing.JLabel();
        jlblPrevAdmitTime = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblWardName = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblBedNo = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jtxtAptTime = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jlblPrevWardName = new javax.swing.JLabel();
        jlblPrevBedNo = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jtxtVdate = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();

        jPanelNavigation.setLayout(new java.awt.BorderLayout());

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

        jtxtOPDNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setText("IPD Number");

        jtxtIPDNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setText("Transfer Note No.");

        jtxtTransferNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
                    .addComponent(jlblName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtIPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblRefBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtTransferNo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jlblCity, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(519, 519, 519))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTransferNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtIPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPDNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtOPDNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel7, jtxtIPDNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel9, jtxtTransferNo});

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setText("Admission Date & Time of Current Ward");

        jlblPrevAdmitDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblPrevAdmitTime.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Transfer Date");

        jLabel11.setText("Transfered Ward");

        jlblWardName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setText("Bed No.");

        jlblBedNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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

        jLabel13.setText("Previous Ward");

        jlblPrevWardName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblPrevBedNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("Previous Bed No.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jlblPrevBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jlblPrevWardName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblPrevAdmitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblPrevAdmitTime, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jlblBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jlblWardName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel12, jlblPrevAdmitTime});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jlblPrevAdmitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jtxtAptTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jlblPrevAdmitTime, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtVdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblPrevWardName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblPrevBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblWardName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel10, jLabel19, jLabel8, jlblPrevAdmitDate, jtxtAptTime});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jlblPrevAdmitTime, jlblWardName});

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ward_cd", "Ward Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setMinWidth(0);
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable3.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 169, Short.MAX_VALUE)
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtAptTimeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAptTimeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAptTimeFocusGained

    private void jtxtAptTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, null);
    }//GEN-LAST:event_jtxtAptTimeKeyPressed

    private void jtxtAptTimeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAptTimeKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 5);
    }//GEN-LAST:event_jtxtAptTimeKeyTyped

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int selRow = jTable3.getSelectedRow();
            if (selRow != -1) {
                int code = (int) lb.isNumber(jTable3.getValueAt(selRow, 0).toString());
                try {
                    String sql = "SELECT room_cd,is_del FROM roommst WHERE is_del >=0 and ward_cd=" + code;
                    PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                    ResultSet rsLocal = pstLocal.executeQuery();
                    jPanel6.removeAll();
                    GridLayout gp = new GridLayout(0, 3);
                    jPanel6.setLayout(gp);
                    while (rsLocal.next()) {
                        JButton jb = new JButton(rsLocal.getString("room_cd"));
                        if (rsLocal.getInt("is_del") == 0) {
                            jb.setBackground(Color.GREEN);
                            jb.addActionListener(new java.awt.event.ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (((JButton) e.getSource()).getBackground() == Color.GREEN) {
                                        jlblWardName.setText(jTable3.getValueAt(jTable3.getSelectedRow(), 1).toString());
                                        jlblBedNo.setText(e.getActionCommand());
                                    }
                                }
                            });
                        } else if (rsLocal.getInt("is_del") == 1) {
                            jb.setBackground(Color.RED);
                            jb.setEnabled(false);
                        }
                        jPanel6.add(jb);
                    }
                    jPanel6.updateUI();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at load data for item from group", ex);
                }
            }
        }
    }//GEN-LAST:event_jTable3MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelNavigation;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable3;
    private javax.swing.JLabel jlblArea;
    private javax.swing.JLabel jlblBedNo;
    private javax.swing.JLabel jlblCity;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblPrevAdmitDate;
    private javax.swing.JLabel jlblPrevAdmitTime;
    private javax.swing.JLabel jlblPrevBedNo;
    private javax.swing.JLabel jlblPrevWardName;
    private javax.swing.JLabel jlblRefBy;
    private javax.swing.JLabel jlblSex;
    private javax.swing.JLabel jlblWardName;
    private javax.swing.JTextField jtxtAptTime;
    private javax.swing.JLabel jtxtIPDNo;
    private javax.swing.JLabel jtxtOPDNo;
    private javax.swing.JLabel jtxtTransferNo;
    private com.toedter.calendar.JDateChooser jtxtVdate;
    // End of variables declaration//GEN-END:variables
}
