/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import hms.HMS101;
import hms.HMSHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import support.HeaderIntFrame;
import support.Library;
import support.NavigationPanel;
import support.PickList;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author Lenovo
 */
public class WardMaster extends javax.swing.JInternalFrame {

    Library lb = new Library();
    Connection dataConnection = HMS101.connMpAdmin;
    PickList floorList = null;
    SmallNavigation navLoad = null;
    int form_id = -1;
    int ward_cd = -1;
    ReportTable table = null;

    /**
     * Creates new form WardMaster
     */
    public WardMaster(int form_id) {
        initComponents();
        floorList = new PickList(dataConnection);
        setPickList();
        addNavigation();
        navLoad.setVoucher("Last");
        this.form_id = form_id;
        lb.setUserRightsToPanel(navLoad, form_id+"");
    }

    private void setPickList() {
        floorList.setLayer(this.getLayeredPane());
        floorList.setPickListComponent(jtxtFloorName);
        floorList.setReturnComponent(new JTextField[]{jtxtFloorName});
        floorList.setNextComponent(jtxtNoOfBeds);
    }

    private void setComponentText(String text) {
        jtxtWardName.setText(text);
        jtxtFloorName.setText("");
        jtxtNoOfBeds.setText("");
        jtxtRoomCharge.setText("");
        jtxtPrefix.setText("");
    }

    public void setID(String code) {
        ward_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Ward Code", 0, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Ward Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Charges", -1, java.lang.String.class, null, false);
        table.AddColumn(3, "Floor Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    private void addNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtWardName.requestFocusInWindow();
                ward_cd = -1;
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtWardName.requestFocusInWindow();
            }

            @Override
            public void callSave() {

                SwingWorker workerForjbtnGenerate = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            dataConnection.setAutoCommit(false);
                            lb.addGlassPane(navLoad);
                            String sql = "";
                            PreparedStatement pstLocal = null;
                            int oldRoom = 0;
                            if (getMode().equalsIgnoreCase("N")) {
                                sql = " insert into wardmst (ward_name,floor_cd,prefix,no_of_beds,room_charge,user_id)values (?,?,?,?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update wardmst set ward_name=?,floor_cd=?,prefix=?,no_of_beds=?,room_charge=?,user_id=?,"
                                        + "edit_no=edit_no+1,time_stamp=current_timestamp where ward_cd=" + ward_cd;
                                oldRoom = (int) lb.isNumber(lb.getData("select count(ward_cd) from roommst where ward_cd=" + ward_cd + " and is_del >= 0"));
                            }

                            if (getMode().equalsIgnoreCase("N")) {
                                sql = " insert into wardmst (ward_name,floor_cd,prefix,no_of_beds,room_charge,user_id)values (?,?,?,?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update wardmst set ward_name=?,floor_cd=?,prefix=?,no_of_beds=?,room_charge=?,user_id=?,"
                                        + "edit_no=edit_no+1,time_stamp=current_timestamp where ward_cd=" + ward_cd;
                            }
                            pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtWardName.getText());
                            pstLocal.setString(2, lb.getFloorCD(jtxtFloorName.getText(), "C"));
                            pstLocal.setString(3, jtxtPrefix.getText());
                            pstLocal.setInt(4, (int) lb.isNumber(jtxtNoOfBeds));
                            pstLocal.setDouble(5, lb.isNumber(jtxtRoomCharge));
                            pstLocal.setInt(6, HMSHome.user_id);
                            pstLocal.executeUpdate();

                            if (ward_cd == -1) {
                                ward_cd = (int) lb.isNumber(lb.getData("select max(ward_cd) from wardmst"));
                            }
                            if (oldRoom < (int) lb.isNumber(jtxtNoOfBeds)) {
                                sql = "update roommst set is_del = 0 where is_del = -1 and ward_cd =" + ward_cd;
                                pstLocal = dataConnection.prepareStatement(sql);
                                oldRoom = (int) lb.isNumber(jtxtNoOfBeds) - oldRoom - pstLocal.executeUpdate();
                                while (oldRoom > 0) {
                                    sql = "insert into roommst (room_cd,ward_cd) values (?,?)";
                                    pstLocal = dataConnection.prepareStatement(sql);
                                    pstLocal.setString(1, lb.generateKey("roommst", "room_cd", 5, jtxtPrefix.getText() + "-"));
                                    pstLocal.setInt(2, ward_cd);
                                    oldRoom = oldRoom - pstLocal.executeUpdate();
                                }
                            } else if (oldRoom > (int) lb.isNumber(jtxtNoOfBeds)) {
                                oldRoom = oldRoom - (int) lb.isNumber(jtxtNoOfBeds);
                                sql = "select room_cd from roommst where ward_cd=" + ward_cd + " and is_del = 0 order by room_cd desc";
                                pstLocal = dataConnection.prepareStatement(sql);
                                ResultSet rsLocal = pstLocal.executeQuery();
                                while (oldRoom > 0 && rsLocal.next()) {
                                    sql = "update roommst set is_del = -1 where room_cd='" + rsLocal.getString("room_cd") + "'";
                                    oldRoom = oldRoom - dataConnection.prepareStatement(sql).executeUpdate();
                                }
                            }
                            setSaveFlag(true);
                            if (getMode().equalsIgnoreCase("N")) {
                                setVoucher("Last");
                            } else if (getMode().equalsIgnoreCase("E")) {
                                setVoucher("Edit");
                            }
                            dataConnection.commit();
                            dataConnection.setAutoCommit(true);
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at saveVoucher at save country master", ex);
                            try {
                                dataConnection.rollback();
                                dataConnection.setAutoCommit(true);
                            } catch (Exception e) {

                            }
                        } finally {
                            lb.removeGlassPane(navLoad);
                        }
                        return null;
                    }
                };
                workerForjbtnGenerate.execute();

            }

            @Override
            public void callDelete() {
            }

            @Override
            public void callView() {
                String sql = "select ward_cd,ward_name,room_charge,floor_name from wardmst s left join floormst c on c.floor_cd=s.floor_cd";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, ward_cd + "", "Ward Master View", sql, "21", 1, WardMaster.this, "Ward Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Ward Master View",-1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id+"");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from wardmst where ward_cd=(select min(ward_cd) from wardmst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from wardmst where ward_cd=(select max(ward_cd) from wardmst where ward_cd <" + ward_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from wardmst where ward_cd=(select min(ward_cd) from wardmst where ward_cd >" + ward_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from wardmst where ward_cd=(select max(ward_cd) from wardmst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from wardmst where ward_cd=" + ward_cd);
                }
                try {
                    if (viewData.next()) {
                        setComponentTextFromResultSet();
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher cityMaster'", ex);
                }
            }

            @Override
            public void callClose() {
                cancelOrClose();
            }

            @Override
            public void callPrint() {
            }

            @Override
            public boolean validateVoucher() {
                if (lb.isBlank(jtxtWardName)) {
                    setMessage("Ward name can not be left blank");
                    jtxtWardName.requestFocusInWindow();
                    return false;
                }

                if (lb.isBlank(jtxtPrefix)) {
                    setMessage("Prefix can not be left blank");
                    jtxtPrefix.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("wardmst", "prefix", jtxtPrefix.getText(), dataConnection)) {
                        setMessage("Prefix name already exist");
                        jtxtPrefix.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("wardmst", "prefix", jtxtPrefix.getText(), "ward_cd", ward_cd + "", dataConnection)) {
                        setMessage("Prefix name already exist");
                        jtxtPrefix.requestFocusInWindow();
                        return false;
                    }
                }

                if (!lb.isExist("floormst", "floor_name", jtxtFloorName.getText(), dataConnection)) {
                    setMessage("Floor name does not exist");
                    jtxtFloorName.requestFocusInWindow();
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("wardmst", "ward_name", jtxtWardName.getText(), dataConnection)) {
                        setMessage("Ward name already exist");
                        jtxtWardName.requestFocusInWindow();
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("wardmst", "ward_name", jtxtWardName.getText(), "ward_cd", ward_cd + "", dataConnection)) {
                        setMessage("Ward name already exist");
                        jtxtWardName.requestFocusInWindow();
                        return false;
                    }
                }
                String sql = "select count(room_cd) from roommst where ward_cd=" + ward_cd + " and is_del = 1";
                int rooms = (int) lb.isNumber(lb.getData(sql));
                if (rooms > lb.isNumber(jtxtNoOfBeds)) {
                    setMessage(rooms + " no of beds already occupied. you can't decerese bed no less than to " + rooms);
                    jtxtNoOfBeds.requestFocusInWindow();
                    return false;
                }
                return true;
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    ward_cd = viewData.getInt("ward_cd");
                    jtxtWardName.setText(viewData.getString("ward_name"));
                    jtxtFloorName.setText(lb.getFloorCD(viewData.getString("floor_cd"), "N"));
                    jtxtNoOfBeds.setText(viewData.getString("no_of_beds"));
                    jtxtRoomCharge.setText(viewData.getString("room_charge"));
                    jtxtPrefix.setText(viewData.getString("prefix"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setTextFromResultset in country master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtWardName.setEnabled(flag);
                jtxtFloorName.setEnabled(flag);
                jtxtNoOfBeds.setEnabled(flag);
                jtxtRoomCharge.setEnabled(flag);
                jtxtPrefix.setEnabled(flag);
            }
        }
        navLoad = new navPanel();
        navLoad.setprintFlag(false);
        jPanel1.add(navLoad);
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
            navLoad.setComponentEnabledDisabled(false);
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

        jLabel1 = new javax.swing.JLabel();
        jtxtWardName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtFloorName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtNoOfBeds = new javax.swing.JTextField();
        jtxtPrefix = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jtxtRoomCharge = new javax.swing.JTextField();

        jLabel1.setText("Ward Name");

        jtxtWardName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtWardNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtWardNameFocusLost(evt);
            }
        });
        jtxtWardName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtWardNameKeyPressed(evt);
            }
        });

        jLabel2.setText("Floor Master");

        jtxtFloorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFloorNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFloorNameFocusLost(evt);
            }
        });
        jtxtFloorName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFloorNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtFloorNameKeyReleased(evt);
            }
        });

        jLabel3.setText("No Of Beds");

        jtxtNoOfBeds.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtNoOfBedsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtNoOfBedsFocusLost(evt);
            }
        });
        jtxtNoOfBeds.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtNoOfBedsKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtNoOfBedsKeyTyped(evt);
            }
        });

        jtxtPrefix.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPrefixFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPrefixFocusLost(evt);
            }
        });
        jtxtPrefix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPrefixKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPrefixKeyTyped(evt);
            }
        });

        jLabel4.setText("Prefix");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("User:");

        jLabel9.setText("Edit No:");

        jLabel11.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel5.setText("Charge");

        jtxtRoomCharge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRoomChargeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRoomChargeFocusLost(evt);
            }
        });
        jtxtRoomCharge.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRoomChargeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRoomChargeKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtWardName, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtFloorName, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtNoOfBeds, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRoomCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtWardName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFloorName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtNoOfBeds, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRoomCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtWardName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtFloorName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jtxtRoomCharge});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtWardNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtWardNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtWardNameFocusGained

    private void jtxtWardNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtWardNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtWardNameFocusLost

    private void jtxtWardNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtWardNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtFloorName);
    }//GEN-LAST:event_jtxtWardNameKeyPressed

    private void jtxtFloorNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFloorNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFloorNameFocusGained

    private void jtxtFloorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFloorNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtFloorNameFocusLost

    private void jtxtFloorNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFloorNameKeyPressed
        // TODO add your handling code here:
        floorList.setLocation(jtxtFloorName.getX(), jtxtFloorName.getY() + jtxtFloorName.getHeight());
        floorList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtFloorNameKeyPressed

    private void jtxtFloorNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFloorNameKeyReleased
        // TODO add your handling code here:
        try {
            String sql = "select floor_name from floormst where floor_name like '" + jtxtFloorName.getText() + "%'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            floorList.setPreparedStatement(pstLocal);
            floorList.setValidation(dataConnection.prepareStatement("select floor_cd from floormst where floor_cd=?"));
            floorList.pickListKeyRelease(evt);
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_jtxtFloorNameKeyReleased

    private void jtxtNoOfBedsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNoOfBedsFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtNoOfBedsFocusGained

    private void jtxtNoOfBedsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNoOfBedsFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtNoOfBedsFocusLost

    private void jtxtNoOfBedsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNoOfBedsKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtPrefix);
    }//GEN-LAST:event_jtxtNoOfBedsKeyPressed

    private void jtxtNoOfBedsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNoOfBedsKeyTyped
        // TODO add your handling code here:
        lb.onlyInteger(evt, 2);
    }//GEN-LAST:event_jtxtNoOfBedsKeyTyped

    private void jtxtPrefixFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPrefixFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPrefixFocusGained

    private void jtxtPrefixFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPrefixFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtPrefixFocusLost

    private void jtxtPrefixKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPrefixKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtRoomCharge);
    }//GEN-LAST:event_jtxtPrefixKeyPressed

    private void jtxtPrefixKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPrefixKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 2);
    }//GEN-LAST:event_jtxtPrefixKeyTyped

    private void jtxtRoomChargeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRoomChargeFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRoomChargeFocusGained

    private void jtxtRoomChargeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRoomChargeFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtRoomChargeFocusLost

    private void jtxtRoomChargeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRoomChargeKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtRoomChargeKeyPressed

    private void jtxtRoomChargeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRoomChargeKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtRoomChargeKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtFloorName;
    private javax.swing.JTextField jtxtNoOfBeds;
    private javax.swing.JTextField jtxtPrefix;
    private javax.swing.JTextField jtxtRoomCharge;
    private javax.swing.JTextField jtxtWardName;
    // End of variables declaration//GEN-END:variables
}
