/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hms;

import chart.BarChartDemo;
import chart.HospitalOccupency;
import java.awt.Color;
import static java.awt.Frame.NORMAL;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import login.Login;
import master.AccountMaster;
import master.AreaMaster;
import master.BillingGroupMaster;
import master.BillingItemMaster;
import master.CityMaster;
import master.ContractMaster;
import master.ContractTemplateMaster;
import master.CountryMaster;
import master.DoctorMaster;
import master.GroupMaster;
import master.SiteMaster;
import master.SpecialityMsater;
import master.StateMaster;
import master.SubSpecialtyMaster;
import master.WardMaster;
import misReports.AdmissionList;
import misReports.ConsaltantDoctorItemGroupWise;
import misReports.ConsaltantDoctorItemWiseDetail;
import misReports.ConsaltantDoctorItemWiseSummary;
import misReports.CriticalStockByExp;
import misReports.CriticalVaccineStock;
import misReports.DCR;
import misReports.DischargeList;
import misReports.DoctorPatientWiseTotalBill;
import misReports.EstimateReport;
import misReports.DailyActivity;
import misReports.DailyActivitySummary;
import misReports.DoctorPatientWiseHospitalBill;
import misReports.PointSystem;
import misReports.StockLedger;
import misReports.StockSummaryByBatch;
import reports.opd.OPDCollectionReport;
import reports.opd.OPDPatientListDateWise;
import support.Library;
import support.UnCaughtException;
import transaction.PatientMaster;
import transaction.TransactionHome;
import transaction.VaccineStockAdd;
import utility.BackUp;
import utility.BranchMasterController;
import utility.BranchMasterModel;
import utility.ChangePassword;
import utility.CompanySettingModel;
import utility.ErrorMessage;
import utility.ManageUser;
import utility.NamingUtility;
import utility.UserGroupMaster;
import utility.UserPermission;

/**
 *
 * @author Bhaumik
 */
public class HMSHome extends javax.swing.JFrame {

    /**
     * Creates new form HMSHome
     */
    public static final String TITLE = "H.M.S.1.0.1";
    public static JTabbedPane tabbedPane = new JTabbedPane();
    public static int user_id;
    public static int role;
    private PrintStream fileStream = null;
    FileOutputStream errorFile = null;
    public static BufferedWriter logFile = null;
    static Library lb = new Library();
    public static BranchMasterModel clsSysInfo = new BranchMasterModel();
    public static CompanySettingModel clsSysEnv = new CompanySettingModel();
    Connection dataConnection = HMS101.connMpAdmin;
    private SystemTray systemTray = SystemTray.getSystemTray();
    private TrayIcon trayIcon = null;
    MenuItem jmenuShow = new MenuItem("Show");
    MenuItem jmenuExit = new MenuItem("Exit");
    public static String year;
    private HashMap<Integer, JMenuItem> hashMenu = null;
    private ArrayList<String> hasPermission = new ArrayList<String>();
    public static String forms;

    public HMSHome() {
        initComponents();
        setTrayIcon();
        jDesktopPane1.add(tabbedPane);
        tabbedPane.setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);
        openLogFile();
        clsSysEnv.setAc_year("2014");
//        setTextFromRS();
        ErrorMessage error = new ErrorMessage(null, true);
        error.setLocationRelativeTo(null);
        if (error.dtm.getRowCount() != 0) {
            error.show();
        }
        Date d = new Date();
        if (d.getMonth() >= 3) {
            year = (d.getYear() + 1900 + "").substring(2) + ((d.getYear() + 1900 + 1) + "").substring(2);
        } else {
            year = ((d.getYear() + 1900 - 1) + "").substring(2) + (d.getYear() + 1900 + "").substring(2);
        }
    }

    private void setTrayIcon() {
        String path = HMS101.currentDirectory + "/Resources/Images/logo.png";
        try {
            if (systemTray.isSupported()) {
                settrayImage(path);
                trayIcon.displayMessage(TITLE + " running", "", TrayIcon.MessageType.INFO);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void settrayImage(String path) {
        try {
            Image image = new ImageIcon(this.getClass().getResource("/hms/logo.png")).getImage();
            setIconImage(image);
            removeTrayIcon();
            trayIcon = null;
            trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            systemTray.add(trayIcon);

            PopupMenu BlinderPopUp = new PopupMenu();
            BlinderPopUp.add(jmenuShow);
            BlinderPopUp.add(jmenuExit);
            trayIcon.setPopupMenu(BlinderPopUp);
            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setVisible(true);
                        setState(NORMAL);
                    }
                }
            };

            trayIcon.addMouseListener(ma);
            trayIcon.setToolTip("JewelWonder");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void removeTrayIcon() {
        if (trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }

    private void setTextFromRS() {
        try {
            String sql = "select * from company_setting";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                clsSysEnv.setCash_ac_cd(rsLocal.getInt("cash_ac_cd") + "");
                clsSysEnv.setDefault_ac_cd(rsLocal.getInt("default_ac_cd") + "");
                clsSysEnv.setPur_ret_cd(rsLocal.getInt("pur_ret_cd") + "");
                clsSysEnv.setPurchase_ac_cd(rsLocal.getInt("pur_ac_cd") + "");
                clsSysEnv.setSales_ac_cd(rsLocal.getInt("sales_ac_cd") + "");
                clsSysEnv.setSales_ret_cd(rsLocal.getInt("sales_ret_cd") + "");
                clsSysEnv.setTax_ac_cd(rsLocal.getInt("tax_ac_cd") + "");
                clsSysEnv.setDisc_ac_cd(rsLocal.getInt("disc_ac_cd") + "");
            }
        } catch (Exception ex) {
            new Library().printToLogFile("Exception at setTextFromRs in company setting", ex);
        }
    }

    public static void removeFromScreen(int index) {
        tabbedPane.removeTabAt(index);
    }

    public static int checkAlradyOpen(String Title) {
        double count = tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            if (tabbedPane.getComponentAt(i).getName().equalsIgnoreCase(Title)) {
//                System.out.println("Already Open");
                return i;
            }
        }
        return -1;
    }

    private static boolean checkRight(int form_cd) {
        if (role != 1) {
            return lb.getRight(form_cd + "", "VIEWS");
        } else {
            return true;
        }
    }

    public static void addOnScreen(JInternalFrame inFrame, String title, int form_cd) {
        if (form_cd == -1 || checkRight(form_cd)) {
            int index = checkAlradyOpen(title);
            if (index == -1) {
                javax.swing.plaf.InternalFrameUI ifu = inFrame.getUI();
                ((javax.swing.plaf.basic.BasicInternalFrameUI) ifu).setNorthPane(null);
                Border b1 = new LineBorder(Color.darkGray, 5) {
                };
                tabbedPane.setBounds(0, 0, jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
                boolean flag = true;
                if (inFrame instanceof ChangePassword || inFrame instanceof CountryMaster || inFrame instanceof StateMaster
                        || inFrame instanceof CityMaster || inFrame instanceof AreaMaster || inFrame instanceof GroupMaster
                        || inFrame instanceof BillingGroupMaster || inFrame instanceof BillingItemMaster
                        || inFrame instanceof SpecialityMsater || inFrame instanceof DoctorMaster || inFrame instanceof WardMaster
                        || inFrame instanceof SubSpecialtyMaster || inFrame instanceof BackUp || inFrame instanceof UserGroupMaster
                        || inFrame instanceof ManageUser || inFrame instanceof NamingUtility
                        || inFrame instanceof SiteMaster) {
                    flag = false;
                }
                if (flag) {
                    inFrame.setLocation(0, 0);
                    inFrame.setSize(jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
                }
                inFrame.setBorder(b1);
                JPanel jp = new JPanel();
                if (flag) {
                    jp.setLayout(new GridLayout());
                }
                jp.add(inFrame);
                jp.setBackground(new Color(201, 212, 216));
                if (flag) {
                    jp.setSize(jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
                }
                jp.setName(title);
                tabbedPane.addTab(title, jp);
                tabbedPane.setSelectedComponent(jp);
                inFrame.setVisible(true);
                inFrame.requestFocusInWindow();
                tabbedPane.setVisible(true);
            } else {
                tabbedPane.setSelectedIndex(index);
            }
        } else {
            lb.showMessageDailog("You don't have right to open this voucher.");
        }
    }

    private void openLogFile() {
        try {
            File folder = new File("LOG");
            if (folder.exists()) {
                File list[] = folder.listFiles();
                for (File f : list) {
                    if (f.isFile()) {
                        if (f.length() == 0) {
                            f.delete();
                        }
                    }
                }
            }
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hh_mm_ss aaa");
            folder = new File("LOG");
            if (!folder.exists()) {
                folder.mkdir();
            }
            File localFile = new File(folder, "logFileCatch" + "_" + sdf.format(cal.getTime()) + ".ini");
            FileWriter fw = new FileWriter(localFile);
            logFile = new BufferedWriter(fw);
            File fileName = new File(folder, "logFileUnCaught" + "_" + sdf.format(cal.getTime()) + ".ini");
            errorFile = new FileOutputStream(fileName, true);
            start();
//            gSysLib.printToLogFile(strCurVer,null,true);
        } catch (Exception ex) {
//            gSysLib.printToLogFile("Exception at makeLogFile in MedikingDMain...", ex);
        }
    }

    public void start() {
        // Saving the orginal stream
        fileStream = new UnCaughtException(errorFile);
        //fileStream = new PrintStream(errorFile);
        // Redirecting console output to file
        System.setOut(fileStream);
        // Redirecting runtime exceptions to file
        System.setErr(fileStream);
    }

    public void setUserRights() {
        menuMaster.setEnabled(true);
        jmnTransaction.setEnabled(true);
        jmnReports.setEnabled(true);
        jmnUtility.setEnabled(true);

        if (role != 1) {
            setPermission();
        }

    }

    public void setPermission() {
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;
        try {
            createMenuList();
            setMenuFalse();
            if (hashMenu != null) {
                Set set = hashMenu.entrySet();
                Iterator i = set.iterator();
//                while (i.hasNext()) {
//                    Map.Entry me = (Map.Entry) i.next();
//                    if (me.getValue() != null) {
//                        ((JMenuItem) me.getValue()).setVisible(false);
//                    }
//                }

                forms = "";
                set = hashMenu.entrySet();
                i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    forms += (me.getKey().toString() + ",");
                }
                if (!forms.isEmpty()) {
                    forms = forms.substring(0, forms.length() - 1);
                }
                String sql = "SELECT FORM_ID, VIEWS FROM USERRIGHTS WHERE USER_ID=? AND VIEWS=1";
                if (!forms.isEmpty()) {
                    sql += " AND FORM_ID IN (" + forms + ")";
                }
                psLocal = dataConnection.prepareStatement(sql);
                psLocal.setString(1, lb.getData("user_grp", "login", "user_id", user_id + "", 1) + "");
                rsLocal = psLocal.executeQuery();
                hasPermission.clear();

                while (rsLocal.next()) {
                    hasPermission.add(rsLocal.getString("FORM_ID"));
                    if (hashMenu.get(rsLocal.getInt("FORM_ID")) != null) {
                        hashMenu.get(rsLocal.getInt("FORM_ID")).setVisible(true);
                    }
                }
            }
            setMenuPermission();
            jMenuItem9.setVisible(true);
            jMenuItem21.setVisible(true);
            jMenuItem15.setVisible(true);
        } catch (Exception ex) {
            lb.printToLogFile("Error at setPermission in jwm home", ex);
        } finally {
            lb.closeResultSet(rsLocal);
            lb.closeStatement(psLocal);
        }
    }

    private void setMenuPermission() {

        menuMaster.setVisible(lb.isExist("select F.FORM_ID from USERRIGHTS R, FORMMST F WHERE F.FORM_ID=R.FORM_ID AND R.VIEWS=1 AND F.MENU_ID=1"));
        jmnTransaction.setVisible(lb.isExist("select F.FORM_ID from USERRIGHTS R, FORMMST F WHERE F.FORM_ID=R.FORM_ID AND R.VIEWS=1 AND F.MENU_ID=2"));
        jmnReports.setVisible(lb.isExist("select F.FORM_ID from USERRIGHTS R, FORMMST F WHERE F.FORM_ID=R.FORM_ID AND R.VIEWS=1 AND F.MENU_ID=3"));
        jMenu10.setVisible(lb.isExist("select F.FORM_ID from USERRIGHTS R, FORMMST F WHERE F.FORM_ID=R.FORM_ID AND R.VIEWS=1 AND F.MENU_ID=5"));
    }

    private void setMenuFalse() throws SQLException {

        for (int i = 1; i < jMenuBar1.getMenuCount() - 1; i++) {
            JMenu menu = jMenuBar1.getMenu(i);
            setMenuVisibleFasle(menu);
        }
    }

    private void setMenuVisibleFasle(JMenu menu) {
        for (int j = 0; j < menu.getMenuComponentCount(); j++) {
            if (menu.getMenuComponent(j) instanceof JMenu) {
                setMenuVisibleFasle((JMenu) menu.getMenuComponent(j));
            } else if (menu.getMenuComponent(j) instanceof JMenuItem) {
                ((JMenuItem) menu.getMenuComponent(j)).setVisible(false);
            }
        }
    }

    private void createMenuList() {
        hashMenu = new HashMap<Integer, JMenuItem>();
        hashMenu.put(11, jmnCountry);
        hashMenu.put(12, jmnStateMst);
        hashMenu.put(13, jmnCityMst);
        hashMenu.put(14, jmnAreaMst);
        hashMenu.put(15, jmnGrpMaster);
        hashMenu.put(16, jmnAcntmst);
        hashMenu.put(17, jmnBillItemMst);
        hashMenu.put(18, jmnBillGrpMst);
        hashMenu.put(19, jmnSpeciality);
        hashMenu.put(110, jmnSubSpeciality);
        hashMenu.put(111, jmnWardMaster);
        hashMenu.put(112, jmnContTempMst);
        hashMenu.put(113, jmnContractMst);

        hashMenu.put(21, jmnAddPatient);

        hashMenu.put(31, jmnDailyActivity);
        hashMenu.put(32, jmnOPDList);
        hashMenu.put(33, jmnOPDCollection);
        hashMenu.put(34, jmnEstimateReport);
        hashMenu.put(35, jmnDischargeList);
        hashMenu.put(36, jmnRefDoctor);
        hashMenu.put(37, jmnDoctorGroup);
        hashMenu.put(38, jmnDoctorItem);
        hashMenu.put(39, jmnDCR);
        hashMenu.put(310, jmnHospitalBill);
        hashMenu.put(311, jmnPointSystem);
        hashMenu.put(312, jmnDateWiseAdmission);
        hashMenu.put(313, jmnHospitalOccupency);
        hashMenu.put(314, jmnDailyActivitySummary);

        hashMenu.put(41, jmnHeaderDetail);
        hashMenu.put(42, user_rights);
        hashMenu.put(43, jmnNamingUtil);
        hashMenu.put(51, jmnVaccineStockAdd);
        hashMenu.put(52, jmnVacLedger);
        hashMenu.put(53, jmnVacStkSum);
        hashMenu.put(54, jmnSiteMaster);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        menuMaster = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jmnCountry = new javax.swing.JMenuItem();
        jmnStateMst = new javax.swing.JMenuItem();
        jmnCityMst = new javax.swing.JMenuItem();
        jmnAreaMst = new javax.swing.JMenuItem();
        jmnGrpMaster = new javax.swing.JMenuItem();
        jmnAcntmst = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jmnBillItemMst = new javax.swing.JMenuItem();
        jmnBillGrpMst = new javax.swing.JMenuItem();
        jmnSpeciality = new javax.swing.JMenuItem();
        jmnSubSpeciality = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jmnWardMaster = new javax.swing.JMenuItem();
        jmnContTempMst = new javax.swing.JMenuItem();
        jmnContractMst = new javax.swing.JMenuItem();
        jmnTransaction = new javax.swing.JMenu();
        jmnAddPatient = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jmnReports = new javax.swing.JMenu();
        jmnDailyActivity = new javax.swing.JMenuItem();
        jmnDailyActivitySummary = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jmnOPDList = new javax.swing.JMenuItem();
        jmnOPDCollection = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jmnEstimateReport = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jmnDischargeList = new javax.swing.JMenuItem();
        jmnRefDoctor = new javax.swing.JMenuItem();
        jmnDoctorGroup = new javax.swing.JMenuItem();
        jmnDoctorItem1 = new javax.swing.JMenuItem();
        jmnDoctorItem = new javax.swing.JMenuItem();
        jmnHospitalBill = new javax.swing.JMenuItem();
        jmnPointSystem = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jmnDCR = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jmnDateWiseAdmission = new javax.swing.JMenuItem();
        jmnHospitalOccupency = new javax.swing.JMenuItem();
        jmnUtility = new javax.swing.JMenu();
        jmnHeaderDetail = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jmnNamingUtil = new javax.swing.JMenuItem();
        user_rights = new javax.swing.JMenuItem();
        userGrpMaster = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jmnError = new javax.swing.JMenu();
        jMenu10 = new javax.swing.JMenu();
        jmnVaccineStockAdd = new javax.swing.JMenuItem();
        jmnVacLedger = new javax.swing.JMenuItem();
        jmnVacStkSum = new javax.swing.JMenuItem();
        jmnSiteMaster = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDesktopPane1.setBackground(new java.awt.Color(255, 255, 255));
        jDesktopPane1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 735, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        jMenu1.setMnemonic('L');
        jMenu1.setText("Login");

        jMenuItem6.setMnemonic('L');
        jMenuItem6.setText("Log Out");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem14.setMnemonic('E');
        jMenuItem14.setText("Exit");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem14);

        jMenuBar1.add(jMenu1);

        menuMaster.setMnemonic('M');
        menuMaster.setText("Master");
        menuMaster.setToolTipText("");

        jMenu2.setMnemonic('A');
        jMenu2.setText("Account");
        jMenu2.setToolTipText("");

        jmnCountry.setMnemonic('C');
        jmnCountry.setText("Country Master");
        jmnCountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCountryActionPerformed(evt);
            }
        });
        jMenu2.add(jmnCountry);

        jmnStateMst.setMnemonic('S');
        jmnStateMst.setText("State Master");
        jmnStateMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStateMstActionPerformed(evt);
            }
        });
        jMenu2.add(jmnStateMst);

        jmnCityMst.setMnemonic('T');
        jmnCityMst.setText("City Master");
        jmnCityMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCityMstActionPerformed(evt);
            }
        });
        jMenu2.add(jmnCityMst);

        jmnAreaMst.setMnemonic('A');
        jmnAreaMst.setText("Area Master");
        jmnAreaMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnAreaMstActionPerformed(evt);
            }
        });
        jMenu2.add(jmnAreaMst);

        jmnGrpMaster.setMnemonic('G');
        jmnGrpMaster.setText("Group Master");
        jmnGrpMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGrpMasterActionPerformed(evt);
            }
        });
        jMenu2.add(jmnGrpMaster);

        jmnAcntmst.setMnemonic('A');
        jmnAcntmst.setText("Account Master");
        jmnAcntmst.setToolTipText("");
        jmnAcntmst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnAcntmstActionPerformed(evt);
            }
        });
        jMenu2.add(jmnAcntmst);

        menuMaster.add(jMenu2);

        jMenu3.setMnemonic('B');
        jMenu3.setText("Billing Master");

        jmnBillItemMst.setMnemonic('I');
        jmnBillItemMst.setText("Bill Item Master");
        jmnBillItemMst.setToolTipText("");
        jmnBillItemMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBillItemMstActionPerformed(evt);
            }
        });
        jMenu3.add(jmnBillItemMst);

        jmnBillGrpMst.setMnemonic('G');
        jmnBillGrpMst.setText("Billing Group Master");
        jmnBillGrpMst.setToolTipText("");
        jmnBillGrpMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBillGrpMstActionPerformed(evt);
            }
        });
        jMenu3.add(jmnBillGrpMst);

        menuMaster.add(jMenu3);

        jmnSpeciality.setMnemonic('S');
        jmnSpeciality.setText("Speciality Master");
        jmnSpeciality.setToolTipText("");
        jmnSpeciality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSpecialityActionPerformed(evt);
            }
        });
        menuMaster.add(jmnSpeciality);

        jmnSubSpeciality.setMnemonic('S');
        jmnSubSpeciality.setText("Sub Speciality Master");
        jmnSubSpeciality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSubSpecialityActionPerformed(evt);
            }
        });
        menuMaster.add(jmnSubSpeciality);

        jMenu6.setMnemonic('I');
        jMenu6.setText("IPD");

        jmnWardMaster.setMnemonic('W');
        jmnWardMaster.setText("Ward Master");
        jmnWardMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnWardMasterActionPerformed(evt);
            }
        });
        jMenu6.add(jmnWardMaster);

        menuMaster.add(jMenu6);

        jmnContTempMst.setMnemonic('C');
        jmnContTempMst.setText("Contract Template Master");
        jmnContTempMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnContTempMstActionPerformed(evt);
            }
        });
        menuMaster.add(jmnContTempMst);

        jmnContractMst.setMnemonic('C');
        jmnContractMst.setText("Contract Master");
        jmnContractMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnContractMstActionPerformed(evt);
            }
        });
        menuMaster.add(jmnContractMst);

        jMenuBar1.add(menuMaster);

        jmnTransaction.setMnemonic('T');
        jmnTransaction.setText("Transaction");

        jmnAddPatient.setMnemonic('A');
        jmnAddPatient.setText("Add patient");
        jmnAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnAddPatientActionPerformed(evt);
            }
        });
        jmnTransaction.add(jmnAddPatient);

        jMenuItem9.setMnemonic('T');
        jMenuItem9.setText("Transaction Home");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jmnTransaction.add(jMenuItem9);

        jMenuBar1.add(jmnTransaction);

        jmnReports.setMnemonic('R');
        jmnReports.setText("Reports");

        jmnDailyActivity.setMnemonic('D');
        jmnDailyActivity.setText("Daily Activity");
        jmnDailyActivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDailyActivityActionPerformed(evt);
            }
        });
        jmnReports.add(jmnDailyActivity);

        jmnDailyActivitySummary.setText("Daily Activity Summary");
        jmnDailyActivitySummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDailyActivitySummaryActionPerformed(evt);
            }
        });
        jmnReports.add(jmnDailyActivitySummary);

        jMenu5.setMnemonic('O');
        jMenu5.setText("OPD");

        jmnOPDList.setMnemonic('P');
        jmnOPDList.setText("OPD Patient List Date Wise");
        jmnOPDList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnOPDListActionPerformed(evt);
            }
        });
        jMenu5.add(jmnOPDList);

        jmnOPDCollection.setMnemonic('C');
        jmnOPDCollection.setText("OPD Patient Collection Date Wise");
        jmnOPDCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnOPDCollectionActionPerformed(evt);
            }
        });
        jMenu5.add(jmnOPDCollection);

        jmnReports.add(jMenu5);

        jMenu7.setMnemonic('I');
        jMenu7.setText("IPD");

        jmnEstimateReport.setMnemonic('E');
        jmnEstimateReport.setText("Estimate Report");
        jmnEstimateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnEstimateReportActionPerformed(evt);
            }
        });
        jMenu7.add(jmnEstimateReport);

        jMenuItem1.setText("Admission Report");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem1);

        jmnDischargeList.setMnemonic('D');
        jmnDischargeList.setText("Discharge List");
        jmnDischargeList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDischargeListActionPerformed(evt);
            }
        });
        jMenu7.add(jmnDischargeList);

        jmnRefDoctor.setMnemonic('R');
        jmnRefDoctor.setText("Referance Doctor Bill");
        jmnRefDoctor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnRefDoctorActionPerformed(evt);
            }
        });
        jMenu7.add(jmnRefDoctor);

        jmnDoctorGroup.setMnemonic('G');
        jmnDoctorGroup.setText("Doctor Wise Item Group Wise Report");
        jmnDoctorGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDoctorGroupActionPerformed(evt);
            }
        });
        jMenu7.add(jmnDoctorGroup);

        jmnDoctorItem1.setMnemonic('I');
        jmnDoctorItem1.setText("Doctor Wise Item Wise Report Summary");
        jmnDoctorItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDoctorItem1ActionPerformed(evt);
            }
        });
        jMenu7.add(jmnDoctorItem1);

        jmnDoctorItem.setMnemonic('I');
        jmnDoctorItem.setText("Doctor Wise Item Wise Report Detail");
        jmnDoctorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDoctorItemActionPerformed(evt);
            }
        });
        jMenu7.add(jmnDoctorItem);

        jmnHospitalBill.setText("Doctor Wise Hospital Bill");
        jmnHospitalBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnHospitalBillActionPerformed(evt);
            }
        });
        jMenu7.add(jmnHospitalBill);

        jmnPointSystem.setText("Point System");
        jmnPointSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPointSystemActionPerformed(evt);
            }
        });
        jMenu7.add(jmnPointSystem);

        jmnReports.add(jMenu7);

        jMenu8.setMnemonic('U');
        jMenu8.setText("User Report");

        jmnDCR.setMnemonic('D');
        jmnDCR.setText("DCR");
        jmnDCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDCRActionPerformed(evt);
            }
        });
        jMenu8.add(jmnDCR);

        jmnReports.add(jMenu8);

        jMenu4.setText("Chart");

        jmnDateWiseAdmission.setText("Date Wise Admission");
        jmnDateWiseAdmission.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDateWiseAdmissionActionPerformed(evt);
            }
        });
        jMenu4.add(jmnDateWiseAdmission);

        jmnHospitalOccupency.setText("Hospital Occupency Report");
        jmnHospitalOccupency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnHospitalOccupencyActionPerformed(evt);
            }
        });
        jMenu4.add(jmnHospitalOccupency);

        jmnReports.add(jMenu4);

        jMenuBar1.add(jmnReports);

        jmnUtility.setMnemonic('U');
        jmnUtility.setText("Utility");

        jmnHeaderDetail.setMnemonic('H');
        jmnHeaderDetail.setText("Header Detail");
        jmnHeaderDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnHeaderDetailActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnHeaderDetail);

        jMenuItem21.setMnemonic('C');
        jMenuItem21.setText("Change Password");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem21);

        jMenuItem15.setMnemonic('B');
        jMenuItem15.setText("Back Up");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem15);

        jmnNamingUtil.setText("Naming Utility");
        jmnNamingUtil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnNamingUtilActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnNamingUtil);

        user_rights.setMnemonic('U');
        user_rights.setText("User Rights");
        user_rights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_rightsActionPerformed(evt);
            }
        });
        jmnUtility.add(user_rights);

        userGrpMaster.setMnemonic('G');
        userGrpMaster.setText("User Group Master");
        userGrpMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userGrpMasterActionPerformed(evt);
            }
        });
        jmnUtility.add(userGrpMaster);

        jMenuItem2.setMnemonic('C');
        jMenuItem2.setText("Create User");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem2);

        jMenuBar1.add(jmnUtility);

        jmnError.setText("Error");
        jmnError.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jmnErrorMouseClicked(evt);
            }
        });
        jMenuBar1.add(jmnError);

        jMenu10.setMnemonic('V');
        jMenu10.setText("Vaccine");

        jmnVaccineStockAdd.setMnemonic('S');
        jmnVaccineStockAdd.setText("Stock Entry Vaccine");
        jmnVaccineStockAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnVaccineStockAddActionPerformed(evt);
            }
        });
        jMenu10.add(jmnVaccineStockAdd);

        jmnVacLedger.setText("Stock Ledger Vaccine");
        jmnVacLedger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnVacLedgerActionPerformed(evt);
            }
        });
        jMenu10.add(jmnVacLedger);

        jmnVacStkSum.setText("Stock Summary by Exp Date");
        jmnVacStkSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnVacStkSumActionPerformed(evt);
            }
        });
        jMenu10.add(jmnVacStkSum);

        jmnSiteMaster.setText("Site Master");
        jmnSiteMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSiteMasterActionPerformed(evt);
            }
        });
        jMenu10.add(jmnSiteMaster);

        jMenuItem10.setText("Critical Limit Stock");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem10);

        jMenuItem11.setText("Critical Stock By Exp");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem11);

        jMenuBar1.add(jMenu10);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmnHeaderDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnHeaderDetailActionPerformed
        // TODO add your handling code here:
        BranchMasterController bmc = new BranchMasterController(42);
        addOnScreen(bmc, "Branch Master Controller", 42);
    }//GEN-LAST:event_jmnHeaderDetailActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // TODO add your handling code here:
        ChangePassword cp = new ChangePassword();
        addOnScreen(cp, "Change Password", -1);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        Login lg = new Login();
        lg.setLocationRelativeTo(null);
        lg.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        // TODO add your handling code here:
        lb.confirmDialog("Do you want to exit from system?");
        if (lb.type) {
            //            MAIS101.main(null);
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jmnCountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCountryActionPerformed
        // TODO add your handling code here:
        CountryMaster cm = new CountryMaster(11);
        addOnScreen(cm, "Country Master", 11);
    }//GEN-LAST:event_jmnCountryActionPerformed

    private void jmnStateMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStateMstActionPerformed
        // TODO add your handling code here:
        StateMaster sm = new StateMaster(12);
        addOnScreen(sm, "State Master", 12);
    }//GEN-LAST:event_jmnStateMstActionPerformed

    private void jmnCityMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCityMstActionPerformed
        // TODO add your handling code here:
        CityMaster cm = new CityMaster(13);
        addOnScreen(cm, "City Master", 13);
    }//GEN-LAST:event_jmnCityMstActionPerformed

    private void jmnAreaMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnAreaMstActionPerformed
        // TODO add your handling code here:
        AreaMaster am = new AreaMaster(14);
        addOnScreen(am, "Area Master", 14);
    }//GEN-LAST:event_jmnAreaMstActionPerformed

    private void jmnGrpMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGrpMasterActionPerformed
        // TODO add your handling code here:
        GroupMaster gm = new GroupMaster(15);
        addOnScreen(gm, "Group Master", 15);
    }//GEN-LAST:event_jmnGrpMasterActionPerformed

    private void jmnAcntmstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnAcntmstActionPerformed
        // TODO add your handling code here:
        AccountMaster am = new AccountMaster(16);
        addOnScreen(am, "Account Master", 16);
    }//GEN-LAST:event_jmnAcntmstActionPerformed

    private void jmnBillGrpMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBillGrpMstActionPerformed
        // TODO add your handling code here:
        BillingGroupMaster bgm = new BillingGroupMaster(18);
        addOnScreen(bgm, "Billing Group Master", 18);
    }//GEN-LAST:event_jmnBillGrpMstActionPerformed

    private void jmnBillItemMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBillItemMstActionPerformed
        // TODO add your handling code here:
        BillingItemMaster bim = new BillingItemMaster(17);
        addOnScreen(bim, "Billing Item Master", 17);
    }//GEN-LAST:event_jmnBillItemMstActionPerformed

    private void jmnSpecialityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSpecialityActionPerformed
        // TODO add your handling code here:
        SpecialityMsater sm = new SpecialityMsater(19);
        addOnScreen(sm, "Speciality Master", 19);
    }//GEN-LAST:event_jmnSpecialityActionPerformed

    private void jmnAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnAddPatientActionPerformed
        // TODO add your handling code here:
        PatientMaster pm = new PatientMaster(21);
        addOnScreen(pm, "Patient Master", 21);
    }//GEN-LAST:event_jmnAddPatientActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        TransactionHome th = new TransactionHome();
        addOnScreen(th, "Transaction Home", -1);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jmnOPDListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnOPDListActionPerformed
        // TODO add your handling code here:
        OPDPatientListDateWise opdptlst = new OPDPatientListDateWise(32);
        addOnScreen(opdptlst, "OPD Patient List Date Wise", 32);
    }//GEN-LAST:event_jmnOPDListActionPerformed

    private void jmnWardMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnWardMasterActionPerformed
        // TODO add your handling code here:
        WardMaster wm = new WardMaster(111);
        addOnScreen(wm, "Ward Master", 111);
    }//GEN-LAST:event_jmnWardMasterActionPerformed

    private void jmnErrorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmnErrorMouseClicked
        // TODO add your handling code here:
        ErrorMessage error = new ErrorMessage(null, true);
        error.setLocationRelativeTo(null);
        if (error.dtm.getRowCount() != 0) {
            error.show();
        }
    }//GEN-LAST:event_jmnErrorMouseClicked

    private void jmnSubSpecialityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSubSpecialityActionPerformed
        // TODO add your handling code here:
        SubSpecialtyMaster sub = new SubSpecialtyMaster(110);
        addOnScreen(sub, "Sub Speciality Master", 110);
    }//GEN-LAST:event_jmnSubSpecialityActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // TODO add your handling code here:
        BackUp bp = new BackUp();
        addOnScreen(bp, "Back Up", -1);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jmnContractMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnContractMstActionPerformed
        // TODO add your handling code here:
        ContractMaster cm = new ContractMaster(113);
        addOnScreen(cm, "Contract Master", 113);
    }//GEN-LAST:event_jmnContractMstActionPerformed

    private void jmnEstimateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnEstimateReportActionPerformed
        // TODO add your handling code here:
        EstimateReport er = new EstimateReport(34);
        addOnScreen(er, "Estimate Report", 34);
    }//GEN-LAST:event_jmnEstimateReportActionPerformed

    private void jmnDCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDCRActionPerformed
        // TODO add your handling code here:
        DCR d = new DCR(39);
        addOnScreen(d, "DCR", 39);
    }//GEN-LAST:event_jmnDCRActionPerformed

    private void jmnDischargeListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDischargeListActionPerformed
        // TODO add your handling code here:
        DischargeList dl = new DischargeList(35);
        addOnScreen(dl, "Discharge List", 35);
    }//GEN-LAST:event_jmnDischargeListActionPerformed

    private void jmnDailyActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDailyActivityActionPerformed
        // TODO add your handling code here:
        DailyActivity hs = new DailyActivity(31);
        addOnScreen(hs, "Daily Activity", 31);
    }//GEN-LAST:event_jmnDailyActivityActionPerformed

    private void jmnRefDoctorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnRefDoctorActionPerformed
        // TODO add your handling code here:
        DoctorPatientWiseTotalBill dpb = new DoctorPatientWiseTotalBill(36);
        addOnScreen(dpb, "Doctor wise Bill", 36);
    }//GEN-LAST:event_jmnRefDoctorActionPerformed

    private void jmnDoctorGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDoctorGroupActionPerformed
        // TODO add your handling code here:
        ConsaltantDoctorItemGroupWise csi = new ConsaltantDoctorItemGroupWise(37);
        addOnScreen(csi, "Doctor Wise Item Group Wise Report", 37);
    }//GEN-LAST:event_jmnDoctorGroupActionPerformed

    private void jmnDoctorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDoctorItemActionPerformed
        // TODO add your handling code here:
        ConsaltantDoctorItemWiseDetail csi = new ConsaltantDoctorItemWiseDetail(38);
        addOnScreen(csi, "Doctor Wise Item Wise Report", 38);
    }//GEN-LAST:event_jmnDoctorItemActionPerformed

    private void jmnOPDCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnOPDCollectionActionPerformed
        // TODO add your handling code here:
        OPDCollectionReport opd = new OPDCollectionReport(33);
        addOnScreen(opd, "OPD Collection Report", 33);
    }//GEN-LAST:event_jmnOPDCollectionActionPerformed

    private void jmnContTempMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnContTempMstActionPerformed
        // TODO add your handling code here:
        ContractTemplateMaster ctm = new ContractTemplateMaster(112);
        addOnScreen(ctm, "Contract Template Master", 112);
    }//GEN-LAST:event_jmnContTempMstActionPerformed

    private void user_rightsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_rightsActionPerformed
        // TODO add your handling code here:
        UserPermission up = new UserPermission(42);
        addOnScreen(up, "User Permission", 42);
    }//GEN-LAST:event_user_rightsActionPerformed

    private void userGrpMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userGrpMasterActionPerformed
        // TODO add your handling code here:
        UserGroupMaster ugp = new UserGroupMaster(-1);
        addOnScreen(ugp, "User Group Master", -1);
    }//GEN-LAST:event_userGrpMasterActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        ManageUser mu = new ManageUser();
        addOnScreen(mu, "Manage User", -1);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jmnDoctorItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDoctorItem1ActionPerformed
        // TODO add your handling code here:
        ConsaltantDoctorItemWiseSummary cdibs = new ConsaltantDoctorItemWiseSummary(38);
        addOnScreen(cdibs, "Consaltant Doctor Item Wise Bill Summary", 38);
    }//GEN-LAST:event_jmnDoctorItem1ActionPerformed

    private void jmnDateWiseAdmissionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDateWiseAdmissionActionPerformed
        // TODO add your handling code here:
        BarChartDemo dwa = new BarChartDemo();
        addOnScreen(dwa, "Date Wise Admission", 312);
    }//GEN-LAST:event_jmnDateWiseAdmissionActionPerformed

    private void jmnHospitalOccupencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnHospitalOccupencyActionPerformed
        // TODO add your handling code here:
        HospitalOccupency ho = new HospitalOccupency();
        addOnScreen(ho, "Hospital Occupency", 313);
    }//GEN-LAST:event_jmnHospitalOccupencyActionPerformed

    private void jmnHospitalBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnHospitalBillActionPerformed
        // TODO add your handling code here:
        DoctorPatientWiseHospitalBill dph = new DoctorPatientWiseHospitalBill(310);
        addOnScreen(dph, "Doctor wise hospital bill", 310);
    }//GEN-LAST:event_jmnHospitalBillActionPerformed

    private void jmnPointSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPointSystemActionPerformed
        // TODO add your handling code here:
        PointSystem ps = new PointSystem(311);
        addOnScreen(ps, "Point System", 311);
    }//GEN-LAST:event_jmnPointSystemActionPerformed

    private void jmnNamingUtilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnNamingUtilActionPerformed
        // TODO add your handling code here:
        NamingUtility mu = new NamingUtility();
        addOnScreen(mu, "Naming Utility", 43);
    }//GEN-LAST:event_jmnNamingUtilActionPerformed

    private void jmnVaccineStockAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnVaccineStockAddActionPerformed
        // TODO add your handling code here:
        VaccineStockAdd vca = new VaccineStockAdd();
        addOnScreen(vca, "Stock Entry Vaccine", 51);
    }//GEN-LAST:event_jmnVaccineStockAddActionPerformed

    private void jmnVacLedgerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnVacLedgerActionPerformed
        // TODO add your handling code here:
        StockLedger stk = new StockLedger();
        addOnScreen(stk, "Stock Ledger Vaccine", 52);
    }//GEN-LAST:event_jmnVacLedgerActionPerformed

    private void jmnVacStkSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnVacStkSumActionPerformed
        // TODO add your handling code here:
        StockSummaryByBatch skb = new StockSummaryByBatch();
        addOnScreen(skb, "Stock SUMMARY NY BATCH", 53);
    }//GEN-LAST:event_jmnVacStkSumActionPerformed

    private void jmnSiteMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSiteMasterActionPerformed
        // TODO add your handling code here:
        SiteMaster sm = new SiteMaster(54);
        addOnScreen(sm, "Site Master", 54);
    }//GEN-LAST:event_jmnSiteMasterActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        CriticalVaccineStock cvs = new CriticalVaccineStock();
        addOnScreen(cvs, "Critical Vaccine Stock", 56);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        CriticalStockByExp cse = new CriticalStockByExp();
        addOnScreen(cse, "Critical Stock BY  Exp", 57);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jmnDailyActivitySummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDailyActivitySummaryActionPerformed
        // TODO add your handling code here:
        DailyActivitySummary das = new DailyActivitySummary(314);
        addOnScreen(das, "Daily Activity Summary", 314);
    }//GEN-LAST:event_jmnDailyActivitySummaryActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        AdmissionList ad = new AdmissionList(314);
        addOnScreen(ad, "Admission List", 314);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jmnAcntmst;
    private javax.swing.JMenuItem jmnAddPatient;
    private javax.swing.JMenuItem jmnAreaMst;
    private javax.swing.JMenuItem jmnBillGrpMst;
    private javax.swing.JMenuItem jmnBillItemMst;
    private javax.swing.JMenuItem jmnCityMst;
    private javax.swing.JMenuItem jmnContTempMst;
    private javax.swing.JMenuItem jmnContractMst;
    private javax.swing.JMenuItem jmnCountry;
    private javax.swing.JMenuItem jmnDCR;
    private javax.swing.JMenuItem jmnDailyActivity;
    private javax.swing.JMenuItem jmnDailyActivitySummary;
    private javax.swing.JMenuItem jmnDateWiseAdmission;
    private javax.swing.JMenuItem jmnDischargeList;
    private javax.swing.JMenuItem jmnDoctorGroup;
    private javax.swing.JMenuItem jmnDoctorItem;
    private javax.swing.JMenuItem jmnDoctorItem1;
    private javax.swing.JMenu jmnError;
    private javax.swing.JMenuItem jmnEstimateReport;
    private javax.swing.JMenuItem jmnGrpMaster;
    private javax.swing.JMenuItem jmnHeaderDetail;
    private javax.swing.JMenuItem jmnHospitalBill;
    private javax.swing.JMenuItem jmnHospitalOccupency;
    private javax.swing.JMenuItem jmnNamingUtil;
    private javax.swing.JMenuItem jmnOPDCollection;
    private javax.swing.JMenuItem jmnOPDList;
    private javax.swing.JMenuItem jmnPointSystem;
    private javax.swing.JMenuItem jmnRefDoctor;
    private javax.swing.JMenu jmnReports;
    private javax.swing.JMenuItem jmnSiteMaster;
    private javax.swing.JMenuItem jmnSpeciality;
    private javax.swing.JMenuItem jmnStateMst;
    private javax.swing.JMenuItem jmnSubSpeciality;
    private javax.swing.JMenu jmnTransaction;
    private javax.swing.JMenu jmnUtility;
    private javax.swing.JMenuItem jmnVacLedger;
    private javax.swing.JMenuItem jmnVacStkSum;
    private javax.swing.JMenuItem jmnVaccineStockAdd;
    private javax.swing.JMenuItem jmnWardMaster;
    private javax.swing.JMenu menuMaster;
    private javax.swing.JMenuItem userGrpMaster;
    private javax.swing.JMenuItem user_rights;
    // End of variables declaration//GEN-END:variables
}
