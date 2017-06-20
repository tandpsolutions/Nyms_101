/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import hms.HMSHome;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import support.HeaderIntFrame;
import support.Library;
import support.PickList;
import support.ReportTable;
import support.SmallNavigation;

/**
 *
 * @author Bhaumik
 */
public class BillingItemMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form BillingItemMaster
     */
    Library lb = new Library();
    Connection dataConnection = hms.HMS101.connMpAdmin;
    SmallNavigation navLoad = null;
    private int bill_item_cd = -1;
    PickList billGroupList = null;
    private int form_id = -1;
    ReportTable table = null;

    public BillingItemMaster(int form_id) {
        initComponents();
        this.form_id = form_id;
        connectToNavigation();
        setPIckListView();
        navLoad.setVoucher("Last");
        lb.setUserRightsToPanel(navLoad, form_id + "");
    }

    private void setComponentText(String text) {
        jtxtBillItemName.setText(text);
        jtxtDefaultRate.setText(text);
        jtxtItemGrpName.setText(text);
        jtxtPoint.setText(text);
        jtxtMinStkLevel.setText(text);
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
    }

    private void setPIckListView() {
        billGroupList = new PickList(dataConnection);
        billGroupList.setLayer(getLayeredPane());
        billGroupList.setPickListComponent(jtxtItemGrpName);
        billGroupList.setReturnComponent(new JTextField[]{jtxtItemGrpName});
        billGroupList.setNextComponent(jtxtDefaultRate);
    }

    private void makeViewTable() {
        table = new ReportTable();
        table.AddColumn(0, "Billing Item Code", -1, java.lang.Integer.class, null, false);
        table.AddColumn(1, "Billing Item Name", -1, java.lang.String.class, null, false);
        table.AddColumn(2, "Biiling Group Name", -1, java.lang.String.class, null, false);
        table.makeTable();
    }

    public void setId(String code) {
        bill_item_cd = Integer.parseInt(code);
        navLoad.setVoucher("Edit");
    }

    private void connectToNavigation() {
        class navPanel extends SmallNavigation {

            @Override
            public void callNew() {
                setComponentEnabledDisabled(true);
                setComponentText("");
                setSaveFlag(false);
                setMode("N");
                jtxtBillItemName.requestFocusInWindow();
            }

            @Override
            public void callEdit() {
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                setMode("E");
                jtxtBillItemName.requestFocusInWindow();
            }

            @Override
            public void callSave() {
                SwingWorker workerForjbtnGenerate = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            lb.addGlassPane(navLoad);
                            String sql = "";
                            if (getMode().equalsIgnoreCase("N")) {
                                sql = "insert into billitemmst (bill_item_name,bill_grp_cd,def_rate,user_id"
                                        + ",third_party,point_charge,min_stk_level,service_charge) values (?,?,?,?,?,?,?,?)";
                            } else if (getMode().equalsIgnoreCase("E")) {
                                sql = "update billitemmst set bill_item_name=?,bill_grp_cd=?,def_rate=?,user_id=?,edit_no = edit_no+1,"
                                        + "time_stamp = current_timestamp,third_party=?,point_charge=?"
                                        + ",min_stk_level=?,service_charge=? where bill_item_cd=" + bill_item_cd;
                            }
                            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                            pstLocal.setString(1, jtxtBillItemName.getText());
                            pstLocal.setString(2, lb.getbillGrpCode(jtxtItemGrpName.getText(), "C"));
                            pstLocal.setDouble(3, lb.isNumber(jtxtDefaultRate));
                            pstLocal.setInt(4, hms.HMSHome.user_id);
                            pstLocal.setInt(5, jComboBox1.getSelectedIndex());
                            pstLocal.setDouble(6, lb.isNumber(jtxtPoint));
                            pstLocal.setDouble(7, lb.isNumber(jtxtMinStkLevel));
                            pstLocal.setInt(8, jComboBox2.getSelectedIndex());
                            pstLocal.executeUpdate();
                            setSaveFlag(true);
                            if (getMode().equalsIgnoreCase("N")) {
                                setVoucher("Last");
                            } else if (getMode().equalsIgnoreCase("E")) {
                                setVoucher("Edit");
                            }
                            setMode("");
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at call save in billing item master", ex);
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

                SwingWorker workerForjbtnGenerate = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            if (lb.isExist("bill_item_cd", "ipdbilldt", bill_item_cd + "", dataConnection)) {
                                if (lb.isExist("bill_item_cd", "opdbilldt", bill_item_cd + "", dataConnection)) {
                                    lb.addGlassPane(navLoad);
                                    lb.confirmDialog("Do you want to delete this billing item?");
                                    if (lb.type) {
                                        String sql = "delete from billitemmst where bill_item_cd=" + bill_item_cd;
                                        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                        pstLocal.executeUpdate();
                                        setVoucher("Previous");
                                    }
                                } else {
                                    setMessage("Item exist in ipd bill");
                                }
                            } else {
                                setMessage("Item exist in opd bill");
                            }
                        } catch (Exception ex) {
                            lb.printToLogFile("Exception at call delete at country master", ex);
                        } finally {
                            lb.removeGlassPane(navLoad);
                        }
                        return null;
                    }
                };
                workerForjbtnGenerate.execute();

            }

            @Override
            public void callView() {
                String sql = "select bill_item_cd,bill_item_name,bill_group_name from billitemmst a left join billgrpmst c1  on "
                        + "a.bill_grp_cd = c1.bill_grp_cd ";
                makeViewTable();
                HeaderIntFrame header = new HeaderIntFrame(dataConnection, bill_item_cd + "", "Biling item Master View", sql, "17", 1, BillingItemMaster.this, "Biling item  Master", table);
                header.makeView();
                cancelOrClose();
                HMSHome.addOnScreen(header, "Billing Item Master View", -1);
            }

            @Override
            public void setVoucher(String tag) {
                setComponentEnabledDisabled(false);
                lb.setUserRightsToPanel(navLoad, form_id + "");
                if (tag.equalsIgnoreCase("First")) {
                    viewData = fetchData("select * from billitemmst where bill_item_cd=(select min(bill_item_cd) from billitemmst)");
                } else if (tag.equalsIgnoreCase("Previous")) {
                    viewData = fetchData("select * from billitemmst where bill_item_cd=(select max(bill_item_cd) from billitemmst where bill_item_cd <" + bill_item_cd + ")");
                } else if (tag.equalsIgnoreCase("Next")) {
                    viewData = fetchData("select * from billitemmst where bill_item_cd=(select min(bill_item_cd) from billitemmst where bill_item_cd >" + bill_item_cd + ")");
                } else if (tag.equalsIgnoreCase("Last")) {
                    viewData = fetchData("select * from billitemmst where bill_item_cd=(select max(bill_item_cd) from billitemmst)");
                } else if (tag.equalsIgnoreCase("edit")) {
                    viewData = fetchData("select * from billitemmst where bill_item_cd=" + bill_item_cd);
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
                if (lb.isBlank(jtxtBillItemName)) {
                    jtxtBillItemName.requestFocusInWindow();
                    navLoad.setMessage("Billing item name can not be left blank");
                    return false;
                }

                if (getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("billitemmst", "bill_item_name", jtxtBillItemName.getText(), dataConnection)) {
                        jtxtBillItemName.requestFocusInWindow();
                        navLoad.setMessage("Billing item already exist");
                        return false;
                    }
                } else if (getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("billitemmst", "bill_item_name", jtxtBillItemName.getText(), "bill_item_cd", bill_item_cd + "", dataConnection)) {
                        jtxtBillItemName.requestFocusInWindow();
                        navLoad.setMessage("Billing item already exist");
                        return false;
                    }
                }

                if (lb.isBlank(jtxtItemGrpName)) {
                    jtxtItemGrpName.requestFocusInWindow();
                    navLoad.setMessage("Billing Item group can not be left blank");
                    return false;
                }

                if (!lb.isExist("billgrpmst", "bill_group_name", jtxtItemGrpName.getText(), dataConnection)) {
                    jtxtItemGrpName.requestFocusInWindow();
                    navLoad.setMessage("Invalid biill item group");
                    return false;
                }
                return true;
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    bill_item_cd = viewData.getInt("bill_item_cd");
                    jtxtBillItemName.setText(viewData.getString("bill_item_name"));
                    jtxtItemGrpName.setText(lb.getbillGrpCode(viewData.getString("bill_grp_cd"), "N"));
                    jtxtDefaultRate.setText(viewData.getString("def_rate"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(viewData.getString("time_stamp"));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_id"), "N"));
                    jComboBox1.setSelectedIndex(viewData.getInt("third_party"));
                    jComboBox2.setSelectedIndex(viewData.getInt("service_charge"));
                    jtxtPoint.setText(viewData.getString("point_charge"));
                    jtxtMinStkLevel.setText(viewData.getString("min_stk_level"));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromRs", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtBillItemName.setEnabled(flag);
                jtxtItemGrpName.setEnabled(flag);
                jtxtDefaultRate.setEnabled(flag);
                jComboBox1.setEnabled(flag);
                jComboBox2.setEnabled(flag);
                jtxtPoint.setEnabled(flag);
                jtxtMinStkLevel.setEnabled(flag);
            }

        }

        navLoad = new navPanel();
        jPanel1.add(navLoad);
        navLoad.setVisible(true);
        navLoad.setprintFlag(false);
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

        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtBillItemName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtxtDefaultRate = new javax.swing.JTextField();
        jtxtItemGrpName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jtxtPoint = new javax.swing.JTextField();
        jtxtMinStkLevel = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();

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

        jLabel1.setText("Billing Item Name");

        jtxtBillItemName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBillItemNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBillItemNameFocusLost(evt);
            }
        });
        jtxtBillItemName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBillItemNameKeyPressed(evt);
            }
        });

        jLabel2.setText("Item Group Name");

        jLabel3.setText("Default rate");

        jtxtDefaultRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDefaultRateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDefaultRateFocusLost(evt);
            }
        });
        jtxtDefaultRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDefaultRateKeyPressed(evt);
            }
        });

        jtxtItemGrpName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtItemGrpNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtItemGrpNameFocusLost(evt);
            }
        });
        jtxtItemGrpName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtItemGrpNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtItemGrpNameKeyReleased(evt);
            }
        });

        jLabel4.setText("Show Doc Name in Bill");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Yes" }));
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel5.setText("Point System");

        jtxtPoint.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPointFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPointFocusLost(evt);
            }
        });
        jtxtPoint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPointKeyPressed(evt);
            }
        });

        jtxtMinStkLevel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMinStkLevelFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMinStkLevelFocusLost(evt);
            }
        });
        jtxtMinStkLevel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMinStkLevelKeyPressed(evt);
            }
        });

        jLabel6.setText("Min Stock Level");

        jLabel7.setText("Service Charge");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Yes" }));
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDefaultRate, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtItemGrpName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtBillItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtPoint, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtMinStkLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBillItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtItemGrpName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDefaultRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMinStkLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtBillItemName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtItemGrpName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jtxtDefaultRate});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel4});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jtxtPoint});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jtxtMinStkLevel});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtBillItemNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBillItemNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBillItemNameFocusGained

    private void jtxtBillItemNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBillItemNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtBillItemNameFocusLost

    private void jtxtBillItemNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBillItemNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jtxtItemGrpName.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtBillItemNameKeyPressed

    private void jtxtDefaultRateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDefaultRateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDefaultRateFocusGained

    private void jtxtDefaultRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDefaultRateFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtDefaultRateFocusLost

    private void jtxtDefaultRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDefaultRateKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtPoint);
    }//GEN-LAST:event_jtxtDefaultRateKeyPressed

    private void jtxtItemGrpNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemGrpNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtItemGrpNameFocusGained

    private void jtxtItemGrpNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemGrpNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtItemGrpNameFocusLost

    private void jtxtItemGrpNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemGrpNameKeyPressed
        // TODO add your handling code here:
        billGroupList.setLocation(jtxtItemGrpName.getX(), jtxtItemGrpName.getY() + jtxtItemGrpName.getHeight());
        billGroupList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtItemGrpNameKeyPressed

    private void jtxtItemGrpNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemGrpNameKeyReleased
        // TODO add your handling code here:
        try {
            String sql = "select bill_group_name from billgrpmst where bill_group_name like '" + jtxtItemGrpName.getText() + "%'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            billGroupList.setPreparedStatement(pstLocal);
            billGroupList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtBillItemName", ex);
        }
    }//GEN-LAST:event_jtxtItemGrpNameKeyReleased

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jtxtPointFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPointFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPointFocusGained

    private void jtxtPointFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPointFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtPointFocusLost

    private void jtxtPointKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPointKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMinStkLevel);
    }//GEN-LAST:event_jtxtPointKeyPressed

    private void jtxtMinStkLevelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMinStkLevelFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMinStkLevelFocusGained

    private void jtxtMinStkLevelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMinStkLevelFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtMinStkLevelFocusLost

    private void jtxtMinStkLevelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMinStkLevelKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jComboBox1);
    }//GEN-LAST:event_jtxtMinStkLevelKeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtBillItemName;
    private javax.swing.JTextField jtxtDefaultRate;
    private javax.swing.JTextField jtxtItemGrpName;
    private javax.swing.JTextField jtxtMinStkLevel;
    private javax.swing.JTextField jtxtPoint;
    // End of variables declaration//GEN-END:variables
}
