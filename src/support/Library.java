/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;
import hms.CursorGlassPane;
import hms.HMS101;
import hms.HMSHome;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import retrofit.RestAdapter;

/**
 *
 * @author Bhaumik
 */
public class Library {

    public boolean type;
    public int typeThree;
    public SimpleDateFormat userFormat = new SimpleDateFormat("dd/MM/yyyy");
    public SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat timestamp = new SimpleDateFormat("dd-MM-yyyy hh:mm:SS a");
    private Robot robotVar = null;
    CursorGlassPane glassPane = new CursorGlassPane();
    Component oldGlass = null;
    Connection dataConnection = HMS101.connMpAdmin;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public Library() {
    }

    public SMS sendMessage(String message, String mobile_no) {
        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://sms.24x7developers.com")
                    .setConverter(new SimpleXmlConverter())
                    .build();
            ApiService apiService = restAdapter.create(ApiService.class);
            Message object = apiService.sendSms("bhaumiks", "ap123@", mobile_no, message, 0, "APPLEh", 3);
            List<SMS> sms = object.getSmsList();
            for (int i = 0; i < sms.size(); i++) {
                System.out.println(sms.get(i).getMessageid());
                System.out.println(sms.get(i).getSmsclientid());
            }
            return sms.get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public void selectAll(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).selectAll();
    }

    {
        try {
            robotVar = new Robot();
        } catch (Exception ex) {
        }
    }

    public void setUserRightsToPanel(SmallNavigation navLoad, String FormID) {
        navLoad.setNewEnable(getRight(FormID, "ADDS"));
        navLoad.setEditEnable(getRight(FormID, "EDIT"));
        navLoad.setDeleteEnable(getRight(FormID, "DELETES"));
        navLoad.setFirstEnable(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setPreviousEnable(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setNextEnable(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setLastEnable(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setViewEnable(getRight(FormID, "NAVIGATE_VIEW"));

    }

    public void setUserRightsToPanel1(NavigationPanel navLoad, String FormID) {
        navLoad.setEnableNew(getRight(FormID, "ADDS"));
        navLoad.setEnableEdit(getRight(FormID, "EDIT"));
        navLoad.setEnableDelete(getRight(FormID, "DELETES"));
        navLoad.setEnableFirst(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setEnablePrevious(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setEnableNext(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setEnableLast(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setEnableView(getRight(FormID, "NAVIGATE_VIEW"));
        navLoad.setEnablePrint(getRight(FormID, "PRINT"));
    }

    public boolean getRight(String form_id, String right) {
        boolean flag = false;
        try {
            if (HMSHome.role == 1) {
                return true;
            }
            String query = "SELECT " + right + " FROM USERRIGHTS WHERE USER_ID=" + getData("user_grp", "login", "user_id", HMSHome.user_id + "", 1) + ""
                    + "  AND FORM_ID=" + form_id;
            PreparedStatement pstLocal = dataConnection.prepareStatement(query);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                if (rsLocal.getInt(1) == 1) {
                    flag = true;
                }
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getRight", ex);
        }
        return flag;
    }

    public void setDateChooserPropertyInit(JTextField jcmbDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        jcmbDate.setText(sdf.format(cal.getTime()));
    }

    public void setDateChooserPropertyInit(com.toedter.calendar.JDateChooser jcmbDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        jcmbDate.setText(sdf.format(cal.getTime()));
    }

    public String getMonth(String i, String tag) {
        if (tag.equalsIgnoreCase("n")) {
            if (i.equalsIgnoreCase("1")) {
                return "January";
            } else if (i.equalsIgnoreCase("2")) {
                return "February";
            } else if (i.equalsIgnoreCase("3")) {
                return "March";
            } else if (i.equalsIgnoreCase("4")) {
                return "April";
            } else if (i.equalsIgnoreCase("5")) {
                return "May";
            } else if (i.equalsIgnoreCase("6")) {
                return "June";
            } else if (i.equalsIgnoreCase("7")) {
                return "July";
            } else if (i.equalsIgnoreCase("8")) {
                return "August";
            } else if (i.equalsIgnoreCase("9")) {
                return "September";
            } else if (i.equalsIgnoreCase("10")) {
                return "October";
            } else if (i.equalsIgnoreCase("11")) {
                return "November";
            } else if (i.equalsIgnoreCase("12")) {
                return "December";
            }
        } else if (tag.equalsIgnoreCase("c")) {
            if (i.equalsIgnoreCase("January")) {
                return "01";
            } else if (i.equalsIgnoreCase("February")) {
                return "02";
            } else if (i.equalsIgnoreCase("March")) {
                return "03";
            } else if (i.equalsIgnoreCase("April")) {
                return "04";
            } else if (i.equalsIgnoreCase("May")) {
                return "05";
            } else if (i.equalsIgnoreCase("June")) {
                return "06";
            } else if (i.equalsIgnoreCase("July")) {
                return "07";
            } else if (i.equalsIgnoreCase("August")) {
                return "08";
            } else if (i.equalsIgnoreCase("September")) {
                return "09";
            } else if (i.equalsIgnoreCase("October")) {
                return "10";
            } else if (i.equalsIgnoreCase("November")) {
                return "11";
            } else if (i.equalsIgnoreCase("December")) {
                return "12";
            }
        }
        return "";
    }

    public String getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year, month, day;
        System.out.println(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment = fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        System.out.println("increment" + increment);
// DAY CALCULATION
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }

// MONTH CALCULATION
        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

// YEAR CALCULATION
        year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);
        return year + " Years " + month + " Months " + day + " Days";
    }

    public String getBirthDateFromDifferenceInDDMMYYYY(int year, int month, int days) {
        Date cur = new Date();
        String strMonth = (((cur.getMonth() + 1 - month) > 0) ? (cur.getMonth() + 1 - month) : ((cur.getMonth() + 1 - month) + 12)) + "";
        return (cur.getDate() - days) + "/" + ((strMonth.length() == 1) ? "0" + strMonth : strMonth)
                + "/" + (((cur.getMonth() + 1 - month) > 0) ? (cur.getYear() + 1900 - year) : (cur.getYear() + 1900 - year - 1));

    }

    public String getYearMonthDays(String date, int mode) throws ParseException {
        Date cur = new Date();
        Date from = userFormat.parse(date);
        String diff = getDateDifferenceInDDMMYYYY(from, cur);
        switch (mode) {
            case 2:
                return diff.substring(0, diff.indexOf("Years "));
            case 1:
                return diff.substring(diff.indexOf("Years") + 5, diff.indexOf("Months "));
            case 0:
                return diff.substring(diff.indexOf("Months ") + 6, diff.indexOf("Days"));
            default:
                return "0";
        }
    }

    public boolean checkDate(JTextField jtxtDate) {
        boolean flag = false;
        try {
            if (jtxtDate.getText().contains("/")) {
                jtxtDate.setText(jtxtDate.getText().replace("/", ""));
            }
            if (jtxtDate.getText().length() == 8) {
                String temp = jtxtDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtDate.setText(setDate);
                flag = true;
            }

        } catch (Exception ex) {
            jtxtDate.requestFocusInWindow();

        }
        return flag;
    }

    public boolean checkDate(com.toedter.calendar.JDateChooser jtxtDate) {
        boolean flag = false;
        try {
            if (jtxtDate.getText().contains("/")) {
                jtxtDate.setText(jtxtDate.getText().replace("/", ""));
            }
            if (jtxtDate.getText().length() == 8) {
                String temp = jtxtDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtDate.setText(setDate);
                flag = true;
            }

        } catch (Exception ex) {
            jtxtDate.requestFocusInWindow();

        }
        return flag;
    }

    public String getUserName(String strVal, String tag) {
        String userName = "";
        try {
            PreparedStatement pstLocal = null;
            if (tag.equalsIgnoreCase("n")) {
                pstLocal = dataConnection.prepareStatement("select user_name from login where user_id=" + Integer.parseInt(strVal));
            } else if (tag.equalsIgnoreCase("c")) {
                pstLocal = dataConnection.prepareStatement("select user_id from login where user_name='" + strVal + "'");
            }
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                userName = rsLocal.getString(1);
            }
            if (rsLocal != null) {
                rsLocal.close();
            }
            if (pstLocal != null) {
                pstLocal.close();
            }
        } catch (Exception ex) {
            printToLogFile("Exception at getUserName", ex);
        }
        return userName;
    }

    public String getUserGroup(String strVal, String tag) {
        String userName = "";
        try {
            PreparedStatement pstLocal = null;
            if (tag.equalsIgnoreCase("n")) {
                pstLocal = dataConnection.prepareStatement("select user_grp_name from usergrp where user_grp_cd=" + Integer.parseInt(strVal));
            } else if (tag.equalsIgnoreCase("c")) {
                pstLocal = dataConnection.prepareStatement("select user_grp_cd from usergrp where user_grp_name='" + strVal + "'");
            }
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                userName = rsLocal.getString(1);
            }
            if (rsLocal != null) {
                rsLocal.close();
            }
            if (pstLocal != null) {
                pstLocal.close();
            }
        } catch (Exception ex) {
            printToLogFile("Exception at getUserName", ex);
        }
        return userName;
    }

    public void searchOnTextFields(JTable jTable1, JPanel panel) {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        panel.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        panel.add(jtfFilter, BorderLayout.CENTER);

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

    public String ConvertDateFormetForDB(String strOrgDate) {
        //Changed
        String strConvDate = "";
        try {
            strOrgDate = strOrgDate.trim();
            if (!strOrgDate.startsWith("/")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date dt = sdf.parse(strOrgDate);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                strConvDate = sdf2.format(dt);
            }
        } catch (Exception ex) {
            printToLogFile("Error in ConvertDateFormetForDB in clSysLib...:", ex);
        }
        return strConvDate;
    }

    public String ConvertDateFormetForDBAccess(String strOrgDate) {
        //Changed
        String strConvDate = "";
        try {
            strOrgDate = strOrgDate.trim();
            if (!strOrgDate.startsWith("/")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                java.util.Date dt = sdf.parse(strOrgDate);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                strConvDate = sdf2.format(dt);
            }
        } catch (Exception ex) {
            printToLogFile("Error in ConvertDateFormetForDB in clSysLib...:", ex);
        }
        return strConvDate;
    }

    public String getGroupName(String strVal, String tag) {
        PreparedStatement pstLocal = null;
        ResultSet rsLocal = null;
        String returnVal = "";
        String sql = "";
        if (strVal.trim().equalsIgnoreCase("") && tag.equalsIgnoreCase("c")) {
            return "0";
        }
        try {
            if (tag.equalsIgnoreCase("C")) {
                sql = "select grp_cd from groupmst where group_name='" + strVal + "'";
            } else if (tag.equalsIgnoreCase("n")) {
                sql = "select group_name from groupmst where grp_cd=" + strVal;
            }

            if (sql != null) {
                pstLocal = dataConnection.prepareStatement(sql);
                rsLocal = pstLocal.executeQuery();
                while (rsLocal.next()) {
                    returnVal = rsLocal.getString(1);
                }
                if (rsLocal != null) {
                    rsLocal.close();
                }
                if (pstLocal != null) {
                    pstLocal.close();
                }
            }
        } catch (Exception ex) {
            printToLogFile("Exception at getGroupName", ex);
        }

        return returnVal;
    }

    public boolean checkDate2(JTextField jtxtDate) {

        boolean flag = checkDate(jtxtDate);
        try {
            if (!flag) {
                return flag;
            }
            String[] date = new String[3];
            StringTokenizer stToken = new StringTokenizer(jtxtDate.getText(), "/");
            int i = 0;
            while (stToken.hasMoreElements()) {
                String token = stToken.nextToken().trim();
                if (!token.equalsIgnoreCase("")) {
                    date[i] = token;
                    i++;
                }
            }

            int day = 0, month = 0, year = 0;
            if (i == 3) {
                day = (int) isNumber(date[0]);
                month = (int) isNumber(date[1]);
                year = (int) isNumber(date[2]);

                if (day < 0 || day > 31) {
                    flag = false;
                }
                if (month < 1 || month > 12) {
                    flag = false;
                }

                if ((year + "").length() == 4) {
                } else if ((year + "").length() == 2) {
                    year += 2000;
                } else {
                    flag = false;
                }
                if (year < 1900 || year > 2099) {
                    flag = false;
                }
            } else {
                flag = false;
            }
            Date d = null;
            if (flag) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, day);
                d = cal.getTime();
                jtxtDate.setText(userFormat.format(d));
            }

        } catch (Exception ex) {
            flag = false;
            jtxtDate.requestFocusInWindow();

        }
        if (!flag) {
            jtxtDate.setText(userFormat.format(new Date()));
        }
        return flag;
    }

    public boolean checkDate2(com.toedter.calendar.JDateChooser jtxtDate) {

        boolean flag = checkDate(jtxtDate);
        try {
            if (!flag) {
                return flag;
            }
            String[] date = new String[3];
            StringTokenizer stToken = new StringTokenizer(jtxtDate.getText(), "/");
            int i = 0;
            while (stToken.hasMoreElements()) {
                String token = stToken.nextToken().trim();
                if (!token.equalsIgnoreCase("")) {
                    date[i] = token;
                    i++;
                }
            }

            int day = 0, month = 0, year = 0;
            if (i == 3) {
                day = (int) isNumber(date[0]);
                month = (int) isNumber(date[1]);
                year = (int) isNumber(date[2]);

                if (day < 0 || day > 31) {
                    flag = false;
                }
                if (month < 1 || month > 12) {
                    flag = false;
                }

                if ((year + "").length() == 4) {
                } else if ((year + "").length() == 2) {
                    year += 2000;
                } else {
                    flag = false;
                }
                if (year < 1900 || year > 2099) {
                    flag = false;
                }
            } else {
                flag = false;
            }
            Date d = null;
            if (flag) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, day);
                d = cal.getTime();
                jtxtDate.setText(userFormat.format(d));
            }

        } catch (Exception ex) {
            flag = false;
            jtxtDate.requestFocusInWindow();

        }
        if (!flag) {
            jtxtDate.setText(userFormat.format(new Date()));
        }
        return flag;
    }

    public void setTable(JTable jTableDet, JComponent[] compHeader) {
        int maxHeightHeaderComp = 0;
        int maxHeightFooterComp = 0;
        int x = 0;
        int y = 0;

        if (compHeader != null) {
            for (int i = 0; i < compHeader.length; i++) {
                if (compHeader[i] != null) {
                    if (maxHeightHeaderComp < compHeader[i].getHeight()) {
                        maxHeightHeaderComp = compHeader[i].getHeight();
                    }
                }
            }

            // SETTING HEADER
            x = 0;
            y = 0;
            for (int i = 0; i < jTableDet.getColumnCount(); i++) {
                if (compHeader[i] != null) {
                    compHeader[i].setBounds(x, y, jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth() - 1, maxHeightHeaderComp);
                }
                x += jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth();
            }
        }
    }

    public void toInteger(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).setText((int) isNumber(((JTextField) evt.getSource()).getText()) + "");
    }

    public void toDouble(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).setText(isNumber(((JTextField) evt.getSource()).getText()) + "");
    }

    public void toTime(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).setText(Convert2DecFmtForRs(isNumber(((JTextField) evt.getSource()).getText())));
        double time = isNumber(((JTextField) evt.getSource()).getText());
        if (time > 23.59) {
            ((JTextField) evt.getSource()).setText("23.59");
        }
    }

    public void setSelectRowShortcut(JInternalFrame form, final JTable table, final JComponent comp) {
        KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK, false);
        Action closeKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getRowCount();
                int selRow = table.getSelectedRow();
                if (row > 0 && selRow == -1) {
                    table.clearSelection();
                    table.requestFocusInWindow();
                    table.setRowSelectionInterval(0, 0);
                } else {
                    table.clearSelection();
                    comp.requestFocusInWindow();
                }
            }
        };
        form.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, "Table");
        form.getActionMap().put("Table", closeKeyAction);
    }

    public void keyPress(int code) {
        robotVar.keyPress(code);

    }

    public void setShortcut(JInternalFrame form, final JButton button) {
        KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action closeKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick();
            }
        };
        form.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, "Close");
        form.getActionMap().put("Close", closeKeyAction);
    }

    public void confirmDialog(String message) {
        final JButton yes = new JButton("Yes");
        final JButton no = new JButton("No");
        type = false;
        JOptionPane JP = new JOptionPane();
//                b1.setMnemonic(KeyEvent.VK_Y);
//                b2.setMnemonic(KeyEvent.VK_N);

        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                type = false;
                no.getTopLevelAncestor().setVisible(false);
            }
        });

        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                type = true;
                yes.getTopLevelAncestor().setVisible(false);
            }
        });

        Action yesKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yes.doClick();
            }
        };

        Action noKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                no.doClick();
            }
        };
        yes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "Click Me Button");
        yes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0, false), "Click Me");
        no.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0, false), "Click Me");
        no.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "Click Me Button");
        yes.getActionMap().put("Click Me Button", yesKeyAction);
        yes.getActionMap().put("Click Me", noKeyAction);
        no.getActionMap().put("Click Me Button", yesKeyAction);
        no.getActionMap().put("Click Me", noKeyAction);
        JButton[] options = {yes, no};
        JP.showOptionDialog(null, message + " \n (Press Y for Yes)  (Press N for No)", "", -1, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public void confirmDialog(String message, String option1, String option2) {
        final JButton yes = new JButton(option1);
        final JButton no = new JButton(option2);
        final JButton cancel = new JButton("Cancel");
        typeThree = -1;
        JOptionPane JP = new JOptionPane();
//                b1.setMnemonic(KeyEvent.VK_Y);
//                b2.setMnemonic(KeyEvent.VK_N);

        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                typeThree = 1;
                no.getTopLevelAncestor().setVisible(false);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                typeThree = -1;
                cancel.getTopLevelAncestor().setVisible(false);
            }
        });

        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                typeThree = 0;
                yes.getTopLevelAncestor().setVisible(false);
            }
        });

        Action yesKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yes.doClick();
            }
        };

        Action noKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                no.doClick();
            }
        };

        Action cancelKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel.doClick();
            }
        };

        yes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "Click Me Button");
        yes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0, false), "Click Me");
        no.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0, false), "Click Me");
        no.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "Click Me Button");
        cancel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, false), "Click Me");
        cancel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, false), "Click Me Button");
        yes.getActionMap().put("Click Me Button", yesKeyAction);
        yes.getActionMap().put("Click Me", noKeyAction);
        no.getActionMap().put("Click Me Button", yesKeyAction);
        no.getActionMap().put("Click Me", noKeyAction);
        cancel.getActionMap().put("Click Me Button", cancelKeyAction);
        cancel.getActionMap().put("Click Me", cancelKeyAction);
        JButton[] options = {yes, no, cancel};
        JP.showOptionDialog(null, message + " \n (Press Y for Yes)  (Press N for No)", "", -1, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public void toUpper(java.awt.event.FocusEvent evt) {
        if (evt.getSource() instanceof JTextField) {
            JTextField txt = (JTextField) evt.getSource();
            txt.setText(txt.getText().trim().toUpperCase());
        }
    }

    public void addGlassPane(SmallNavigation navLoad) {
        Component c = navLoad.getParent();
        while (!(c instanceof JInternalFrame)) {
            c = c.getParent();
        }
        if (!(((JInternalFrame) c).getGlassPane() instanceof CursorGlassPane)) {
            //If antother Glass Pane is set for this form then save it to restore it after saving..
            oldGlass = ((JInternalFrame) c).getGlassPane();
            ((JInternalFrame) c).setGlassPane(glassPane);

        }
        ((JInternalFrame) c).getGlassPane().setVisible(true);
        ((JInternalFrame) c).getGlassPane().requestFocusInWindow();

        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

    }

    public void removeGlassPane(SmallNavigation navLoad) {
        Component c = navLoad.getParent();
        while (!(c instanceof JInternalFrame)) {
            c = c.getParent();
        }
        navLoad.requestFocusInWindow();
        ((JInternalFrame) c).getGlassPane().setVisible(false);
        if (oldGlass != null) {
            ((JInternalFrame) c).setGlassPane(oldGlass);
        }
        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    public void addGlassPane(JInternalFrame navLoad) {
        if (!(((JInternalFrame) navLoad).getGlassPane() instanceof CursorGlassPane)) {
            //If antother Glass Pane is set for this form then save it to restore it after saving..
            oldGlass = ((JInternalFrame) navLoad).getGlassPane();
            ((JInternalFrame) navLoad).setGlassPane(glassPane);

        }
        ((JInternalFrame) navLoad).getGlassPane().setVisible(true);
        ((JInternalFrame) navLoad).getGlassPane().requestFocusInWindow();

        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

    }

    public void removeGlassPane(JInternalFrame navLoad) {
        navLoad.requestFocusInWindow();
        ((JInternalFrame) navLoad).getGlassPane().setVisible(false);
        if (oldGlass != null) {
            ((JInternalFrame) navLoad).setGlassPane(oldGlass);
        }
        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    public void addGlassPane(ReportPanel navLoad) {
        Component c = navLoad.getParent();
        while (!(c instanceof JInternalFrame)) {
            c = c.getParent();
        }
        if (!(((JInternalFrame) c).getGlassPane() instanceof CursorGlassPane)) {
            //If antother Glass Pane is set for this form then save it to restore it after saving..
            oldGlass = ((JInternalFrame) c).getGlassPane();
            ((JInternalFrame) c).setGlassPane(glassPane);

        }
        ((JInternalFrame) c).getGlassPane().setVisible(true);
        ((JInternalFrame) c).getGlassPane().requestFocusInWindow();

        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

    }

    public void removeGlassPane(ReportPanel navLoad) {
        Component c = navLoad.getParent();
        while (!(c instanceof JInternalFrame)) {
            c = c.getParent();
        }
        navLoad.requestFocusInWindow();
        ((JInternalFrame) c).getGlassPane().setVisible(false);
        if (oldGlass != null) {
            ((JInternalFrame) c).setGlassPane(oldGlass);
        }
        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    public void toRupee(java.awt.event.FocusEvent evt) {
        if (evt.getSource() instanceof JTextField) {
            JTextField txt = (JTextField) evt.getSource();
            txt.setText(Convert2DecFmtForRs(isNumber(txt)));
        }
    }

    public boolean isBlank(Component comp) {
        JTextField jText = (JTextField) comp;
        if (jText.getText().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBlank(JComponent comp) {
        if (comp instanceof JTextField) {
            JTextField jText = (JTextField) comp;
            if (jText.getText().trim().length() == 0) {
                return true;
            } else {
                return false;
            }
        } else if (comp instanceof JLabel) {
            JLabel jText = (JLabel) comp;
            if (jText.getText().trim().length() == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public JasperPrint reportGenerator(String fileName, HashMap params, ResultSet viewDataRs, JPanel panelReport) {
        JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRs);
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + fileName, params, dataSource);
            panelReport.removeAll();
            JRViewer jrViewer = new JRViewer(print);
            ((JPanel) jrViewer.getComponent(0)).remove(1);
            jrViewer.setSize(panelReport.getWidth(), panelReport.getHeight());
            panelReport.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(panelReport);
            panelReport.requestFocusInWindow();
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public void exportToExcel(String sheetName, ArrayList headers,
            ArrayList data, String fileName) throws HPSFException {
        final JFileChooser jfc = new JFileChooser(hms.HMS101.currentDirectory);

        final JTextField jf = new JTextField();
        jfc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equalsIgnoreCase("ApproveSelection")) {
                    jf.setText(jfc.getSelectedFile().getAbsolutePath());
                } else {
                    jf.setText("");
                    return;
                }
            }
        });

        jfc.setCurrentDirectory(new File(hms.HMS101.currentDirectory));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setDialogTitle("Select Destination Folder");
        jfc.setApproveButtonText("Select");
        jfc.showOpenDialog(null);

        if (!jf.getText().isEmpty()) {
            File f1 = new File(jf.getText() + File.separatorChar + sheetName.replaceAll(".jasper", "") + ".xls");
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(sheetName);

            int rowIdx = 0;
            short cellIdx = 0;

            // Header
            HSSFRow hssfHeader = sheet.createRow(rowIdx);
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            for (Iterator cells = headers.iterator(); cells.hasNext();) {
                HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
                hssfCell.setCellStyle(cellStyle);
                hssfCell.setCellValue((String) cells.next());
            }
            // Data
            rowIdx = 1;
            for (Iterator rows = data.iterator(); rows.hasNext();) {
                ArrayList row = (ArrayList) rows.next();
                HSSFRow hssfRow = sheet.createRow(rowIdx++);
                cellIdx = 0;
                for (Iterator cells = row.iterator(); cells.hasNext();) {
                    HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
                    hssfCell.setCellValue(cells.next() + "");
                }
            }

            wb.setSheetName(0, sheetName);
            try {
                FileOutputStream outs = new FileOutputStream(f1);
                wb.write(outs);
                outs.close();
                confirmDialog(f1.getAbsolutePath() + " has been generated successfully.");
                if (type) {
                    Desktop.getDesktop().open(f1);
                }
            } catch (IOException e) {
                throw new HPSFException(e.getMessage());
            }
        }

    }

    public JasperPrint reportGeneratorWord(String fileName, HashMap params) {
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            String printFileName = null;
            printFileName = JasperFillManager.fillReportToFile(System.getProperty("user.dir") + File.separatorChar + "Reports/" + fileName,
                    params);
            if (printFileName != null) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                        printFileName);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        System.getProperty("user.dir") + File.separatorChar + fileName.replaceAll(".jasper", "") + ".pdf");
                exporter.exportReport();

                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + File.separatorChar + fileName.replaceAll(".jasper", "") + ".pdf"));

            }
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public JasperPrint reportGeneratorWord(String fileName, HashMap params, ResultSet dataList) {
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            String printFileName = null;
            JRResultSetDataSource beanColDataSource
                    = new JRResultSetDataSource(dataList);
            printFileName = JasperFillManager.fillReportToFile(System.getProperty("user.dir") + File.separatorChar + "Reports/" + fileName,
                    params, beanColDataSource);
            if (printFileName != null) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                        printFileName);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        System.getProperty("user.dir") + File.separatorChar + fileName.replaceAll(".jasper", "") + ".pdf");
                exporter.exportReport();

                Desktop.getDesktop().print(new File(System.getProperty("user.dir") + File.separatorChar + fileName.replaceAll(".jasper", "") + ".pdf"));

            }
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public JasperPrint reportGeneratorWord(String fileName, HashMap params, ArrayList dataList) {
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource
                    = new JRBeanCollectionDataSource(dataList);
            printFileName = JasperFillManager.fillReportToFile(System.getProperty("user.dir") + File.separatorChar + "Reports/" + fileName,
                    params, beanColDataSource);
            if (printFileName != null) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                        printFileName);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                        System.getProperty("user.dir") + File.separatorChar + fileName.replaceAll(".jasper", "") + ".pdf");
                exporter.exportReport();

                Desktop.getDesktop().print(new File(System.getProperty("user.dir") + File.separatorChar + fileName.replaceAll(".jasper", "") + ".pdf"));

            }
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public JasperPrint reportGenerator(String fileName, HashMap params, JRDataSource viewDataRs, JPanel panelReport) {
//        JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRs);
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + fileName, params, viewDataRs);
            panelReport.removeAll();
            JRViewer jrViewer = new JRViewer(print);
//            ((JPanel)jrViewer.getComponent(0)).remove(0);
            jrViewer.setSize(panelReport.getWidth(), panelReport.getHeight());
            panelReport.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(panelReport);
            panelReport.requestFocusInWindow();
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public JasperPrint reportGenerator(String fileName, HashMap params, JPanel panelReport) {
//        JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRs);
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + fileName, params);
            panelReport.removeAll();
            JRViewer jrViewer = new JRViewer(print);
//            ((JPanel)jrViewer.getComponent(0)).remove(0);
            jrViewer.setSize(panelReport.getWidth(), panelReport.getHeight());
            panelReport.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(panelReport);
            panelReport.requestFocusInWindow();
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public JasperPrint reportGenerator(String fileName, HashMap params, ArrayList viewDataRs, JPanel panelReport) {
//        JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRs);
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            JRBeanCollectionDataSource beanColDataSource
                    = new JRBeanCollectionDataSource(viewDataRs);
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + fileName, params, beanColDataSource);
            panelReport.removeAll();
            JRViewer jrViewer = new JRViewer(print);
            ((JPanel) jrViewer.getComponent(0)).remove(0);
            jrViewer.setSize(panelReport.getWidth(), panelReport.getHeight());
            panelReport.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(panelReport);
            panelReport.requestFocusInWindow();
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public void enterFocus(KeyEvent evt, JComponent comp) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            ((JComponent) evt.getComponent()).setNextFocusableComponent(comp);
            comp.requestFocusInWindow();
        }
    }

    public void shiftFocus(KeyEvent evt, JComponent comp) {
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            if (evt.getModifiers() == KeyEvent.SHIFT_MASK) {
                evt.consume();
                ((JComponent) evt.getComponent()).setNextFocusableComponent(comp);
                comp.requestFocusInWindow();
            }
        }
    }

    public void downFocus(KeyEvent evt, JComponent comp) {
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                evt.consume();
                comp.requestFocusInWindow();
            }
        }
    }

    public void enterClick(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            ((JButton) evt.getSource()).doClick();
        }
    }

    public double isNumber(Component comp) {
        double ans = 0.00;
        JTextField text = (JTextField) comp;
        try {
            ans = Double.parseDouble(text.getText());
        } catch (Exception ex) {
//            printToLogFile("Error at isNumber in Library", ex);
        }
        return ans;
    }

    public double isNumber(String comp) {
        double ans = 0.00;
        try {
            ans = Double.parseDouble(comp);
        } catch (Exception ex) {
//            printToLogFile("Error at isNumber in Library", ex);
        }
        return ans;
    }

    public double isNumber(JComponent comp) {
        double ans = 0.00;
        String txt = "";
        if (comp instanceof JTextField) {
            txt = ((JTextField) comp).getText();
        } else if (comp instanceof JLabel) {
            txt = ((JLabel) comp).getText();
        }
        try {
            ans = Double.parseDouble(txt);
        } catch (Exception ex) {
//            printToLogFile("Error at isNumber in Library", ex);
        }
        return ans;
    }

    public String Convert2DecFmtForRs(double strSource) {
        String str = "0";
        try {
            String digit = "";
            for (int i = 1; i <= 2; i++) {
                digit += "0";
            }
            NumberFormat formatter = new DecimalFormat("#0.00");
            str = formatter.format(strSource);
        } catch (Exception ex) {
            System.out.println(ex.getCause());
            System.out.println(ex.getMessage());
        }
        return str;
    }

    public void showMessageDailog(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public void showErrorDailog(String msg) {
        JOptionPane.showMessageDialog(null, msg, "", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isEnter(KeyEvent evt) {
        boolean flag = false;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            flag = true;
        }
        return flag;
    }

    public void onlyInteger(KeyEvent event, int len) {

        try {
            int keyCode = event.getKeyChar();
            JTextComponent source = (JTextComponent) event.getSource();
            if (!(keyCode < 48 || keyCode > 58) || keyCode == 45) {
                if (event.isConsumed()) {
                    return;
                }
                if (source.getText().length() >= len && event.getKeyChar() != event.VK_BACK_SPACE && source.getSelectionStart() == source.getSelectionEnd()) {
                    source.getToolkit().beep();
                    event.consume();
                }
            } else {
                event.consume();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fixLength(KeyEvent event, int len) {

        try {
            if (event.isConsumed()) {
                return;
            }
            JTextComponent source = (JTextComponent) event.getSource();
            if (len == -1) {
                len = source.getText().length() + 1;
            }
            if (source.getText().length() >= len && event.getKeyChar() != event.VK_BACK_SPACE && source.getSelectionStart() == source.getSelectionEnd()) {
                source.getToolkit().beep();
                event.consume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onlyNumber(KeyEvent event, int len) {

        try {
            int keyCode = event.getKeyChar();
            JTextComponent source = (JTextComponent) event.getSource();
            if (len == -1) {
                len = source.getText().length() + 1;
            }
            if (!(keyCode < 48 || keyCode > 58) || keyCode == 46 || keyCode == 45) {
                if (event.isConsumed()) {
                    return;
                }
                if (source.getText().length() >= len && event.getKeyChar() != event.VK_BACK_SPACE && source.getSelectionStart() == source.getSelectionEnd()) {
                    source.getToolkit().beep();
                    event.consume();
                }
            } else {
                event.consume();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adjustJTableRowSizes(JTable jTable) {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < jTable.getColumnCount(); column++) {
                TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
                Object valueAt = jTable.getValueAt(row, column);
                Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(jTable, valueAt, false, false, row, column);
                int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
                maxHeight = Math.max(heightPreferable, maxHeight);
            }
            jTable.setRowHeight(row, maxHeight);
        }

    }

    public String ConvertDateFormetForDisply(String strOrgDate) throws ParseException {
        //Changed
        String strConvDate = "";
        //try
        //{
        strOrgDate = strOrgDate.trim();
        if (!strOrgDate.startsWith("/")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dt = sdf.parse(strOrgDate);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            strConvDate = sdf2.format(dt);
        }
        //} catch(Exception ex){
        //printToLogFile("Error in ConvertDateFormetForDB in clSysLib...:",ex);
        //}
        return strConvDate;
    }

    public void adjustColumnSizes(JTable table, int column, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(column);
        int width, minWidth;

        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, column);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
            int currentWidth = comp.getPreferredSize().width;
            width = Math.max(width, currentWidth);
        }
        minWidth = col.getMinWidth();
        width += 2 * margin;
        if (width < minWidth) {
            width = minWidth;
        }
        col.setPreferredWidth(width);
        col.setWidth(width);
    }

    public void setColumnSizeForTable(JTable table, int minTableWidth) {
        adjustJTableRowSizes(table);
        for (int i = 0; i < table.getColumnCount(); i++) {
            adjustColumnSizes(table, i, 2);
        }
        if (minTableWidth != 0) {
            if (table.getPreferredSize().width < minTableWidth) {
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            } else {
                table.setAutoResizeMode(0);
            }
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }
    }

    public void printToLogFile(String strMsg, Exception exType) {
        try {
            HashSet<String> hsFileName = new HashSet<String>();
            if (exType != null) {
                StackTraceElement str[] = exType.getStackTrace();
                int iIndex = 0;
                if (str.length > 10) {
                    iIndex = 10;
                } else {
                    iIndex = str.length;
                }

                for (int i = 0; i < iIndex; i++) {
                    if (str[i].getFileName() != null) {
                        hsFileName.add(str[i].getFileName());
                    }
                }

                if (!hsFileName.contains("DataFilterColumnArray.java")
                        && !hsFileName.contains("DataFilterDate.java")
                        && !hsFileName.contains("DataFilterNumber.java")
                        && !hsFileName.contains("DataFilterString.java")) {
                    HMSHome.logFile.write("Time : " + getCurrentDBServerTime());
                    HMSHome.logFile.newLine();
                    HMSHome.logFile.write("Exception From : " + strMsg.toString());
                    HMSHome.logFile.newLine();
                    HMSHome.logFile.write("Main Exception :" + exType.toString());
                    HMSHome.logFile.newLine();

                    for (int i = 0; i < iIndex; i++) {
                        HMSHome.logFile.write("          ======================           ");
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("Class Name  :" + str[i].getClassName());
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("File Name   :" + str[i].getFileName());
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("Method Name :" + str[i].getMethodName());
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("Line Number :" + str[i].getLineNumber());
                        HMSHome.logFile.newLine();
                    }

                    HMSHome.logFile.write("==================================================");
                    HMSHome.logFile.newLine();
                    HMSHome.logFile.write("==================================================");
                    HMSHome.logFile.newLine();
                }
            } else {
                HMSHome.logFile.write("Time : " + getCurrentDBServerTime());
                HMSHome.logFile.newLine();
                HMSHome.logFile.write("Message(For Information) : " + strMsg.toString());
                HMSHome.logFile.newLine();
                HMSHome.logFile.write("==================================================");
                HMSHome.logFile.newLine();
            }
            HMSHome.logFile.flush();
            if (exType instanceof java.sql.SQLNonTransientConnectionException) {
                JButton exit = new JButton("Exit");
//                    JButton cancel = new JButton("Cancel");
                JButton[] button = {exit};
                exit.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                JOptionPane.showOptionDialog(new HMSHome(), "Please Restart the Application and\n Check the Database Connection", "Connection Error",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, button, exit);
                return;
            }
        } catch (Exception ex) {
            printToLogFile(strMsg + " (And Error in PrintToLogFile : " + ex + ")", exType, true);
        }
    }

    public void printToLogFile(String strMsg, Exception exType, boolean withoutTiming) {
        try {
            HashSet<String> hsFileName = new HashSet<String>();
            if (exType != null) {
                StackTraceElement str[] = exType.getStackTrace();
                int iIndex = 0;
                if (str.length > 10) {
                    iIndex = 10;
                } else {
                    iIndex = str.length;
                }

                for (int i = 0; i < iIndex; i++) {
                    if (str[i].getFileName() != null) {
                        hsFileName.add(str[i].getFileName());
                    }
                }

                if (!hsFileName.contains("DataFilterColumnArray.java")
                        && !hsFileName.contains("DataFilterDate.java")
                        && !hsFileName.contains("DataFilterNumber.java")
                        && !hsFileName.contains("DataFilterString.java")) {
                    HMSHome.logFile.write("Exception From : " + strMsg.toString());
                    HMSHome.logFile.newLine();
                    HMSHome.logFile.write("Main Exception :" + exType.toString());
                    HMSHome.logFile.newLine();

                    for (int i = 0; i < iIndex; i++) {
                        HMSHome.logFile.write("          ======================           ");
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("Class Name  :" + str[i].getClassName());
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("File Name   :" + str[i].getFileName());
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("Method Name :" + str[i].getMethodName());
                        HMSHome.logFile.newLine();
                        HMSHome.logFile.write("Line Number :" + str[i].getLineNumber());
                        HMSHome.logFile.newLine();
                    }

                    HMSHome.logFile.write("==================================================");
                    HMSHome.logFile.newLine();
                    HMSHome.logFile.write("==================================================");
                    HMSHome.logFile.newLine();
                }
                if (exType instanceof java.sql.SQLNonTransientConnectionException) {
                    HMSHome.logFile.flush();
                    JButton exit = new JButton("Exit");
//                    JButton cancel = new JButton("Cancel");
                    JButton[] button = {exit};
                    exit.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    JOptionPane.showOptionDialog(new HMSHome(), "Please Restart the Application and\n Check the Database Connection", "Connection Error",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, button, exit);
                    return;
                }
            } else {
                HMSHome.logFile.write("Main Exception :" + strMsg.toString());
                HMSHome.logFile.newLine();
            }
            HMSHome.logFile.flush();
        } catch (Exception ex) {
            System.out.println("Exception at printToLogFile_withoutTiming in clSysLib..!!" + ex);
        }
    }

    public String getCurrentDBServerTime() {
        String strTime = "";
        try {
            Calendar cal = Calendar.getInstance();

            strTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(cal.getTime());
        } catch (Exception ex) {
            //Null is passed here, because it shold print the original exception,
            //If we passed this exception then on nontransientconnection exception
            //it will go for exit in printtoLogfile..
            printToLogFile("Exception at getCurrentDBServerTime in clSysLib..!!: ", null, true);
        }
        return strTime;
//       Temprority changed..
//        return DateFormat.getTimeInstance().format(new java.util.Date());
    }

    public boolean isExist(String table, String column, String data, Connection dataConnection) {
        boolean flag = false;
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;
        try {
            psLocal = dataConnection.prepareStatement("select " + column + " from " + table + " WHERE " + column + "=?");
            psLocal.setString(1, data.toUpperCase());
            rsLocal = psLocal.executeQuery();
            flag = rsLocal.next();
        } catch (Exception ex) {
            printToLogFile("Error at isExist in Library2", ex);
        } finally {
            closeResultSet(rsLocal);
            closeStatement(psLocal);
        }
        return flag;
    }

    public boolean isExistForEdit(String table, String column, String data, String primaryCol, String primaryVal, Connection dataConnection) {
        boolean flag = false;
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;
        try {
            psLocal = dataConnection.prepareStatement("select " + column + " from " + table + " WHERE " + column + "=? AND " + primaryCol + "<>?");
            psLocal.setString(1, data);
            psLocal.setString(2, primaryVal);
            rsLocal = psLocal.executeQuery();
            flag = rsLocal.next();
        } catch (Exception ex) {
            printToLogFile("Error at isExistForEdit in Library", ex);
        } finally {
            closeResultSet(rsLocal);
            closeStatement(psLocal);
        }
        return flag;
    }

    public void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception ex) {
            printToLogFile("Error at close resultset", ex);
        }
    }

    public void closeStatement(PreparedStatement pst) {
        try {
            if (pst != null) {
                pst.close();
            }
        } catch (Exception ex) {
            printToLogFile("Error at close statement", ex);
        }
    }

    public String getCountryCD(String code, String tag) {
        String stateCD = "";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select country_cd from countrymst where country_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select country_name from countrymst where country_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at get Country CD", ex);
        }
        return stateCD;
    }

    public String getSiteCD(String code, String tag) {
        String stateCD = "";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select site_cd from sitemst where site_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select site_name from sitemst where site_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at get site CD", ex);
        }
        return stateCD;
    }

    public String getSpecialityCD(String code, String tag) {
        String stateCD = "";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select speciality_cd from specialitymst where speciality_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select speciality_name from specialitymst where speciality_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at get Country CD", ex);
        }
        return stateCD;
    }

    public String getStateCd(String code, String tag) {
        String stateCD = "";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select state_cd from statemst where state_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select state_name from statemst where state_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getStateCD", ex);
        }
        return stateCD;
    }

    public String getAreaCd(String code, String tag) {
        String stateCD = "0";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("C")) {
                sql = "select area_cd from areamst where area_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select area_name from areamst where area_cd=" + code;
            } else if (tag.equalsIgnoreCase("CP")) {
                sql = "select pincode from areamst where area_cd=" + code;
            } else if (tag.equalsIgnoreCase("PC")) {
                sql = "select area_cd from areamst where pincode='" + code + "'";
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getStateCD", ex);
        }
        return stateCD;
    }

    public String getCityCd(String code, String tag) {
        String stateCD = "0";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select city_cd from citymst where city_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select city_name from citymst where city_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getStateCD", ex);
        }
        return stateCD;
    }

    public String getSubSpecialistCd(String code, String tag) {
        String stateCD = "0";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select spec_sub_cd from specsubmst where spec_sub_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select spec_sub_name from specsubmst where spec_sub_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getStateCD", ex);
        }
        return stateCD;
    }

    public String getData(String column, String table, String where, String whereData, int type) {
        String data = "";
        try {
            String sql = "";
            if (type == 0) {
                sql = "select " + column + " from " + table + " where " + where + "='" + whereData + "'";
            } else {
                sql = "select " + column + " from " + table + " where " + where + "=" + whereData + "";
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                data = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getData", ex);
        }
        return data;
    }

    public String getData(String sql) {
        String data = "";
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                data = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getData", ex);
        }
        return data;
    }

    public void generateLog(String fromTable, String toTable, String field, String value) throws SQLException {

        PreparedStatement psLocal = null;
        psLocal = dataConnection.prepareStatement("DELETE FROM " + toTable + " WHERE " + field + "='" + value + "'");
        psLocal.executeUpdate();

        psLocal = dataConnection.prepareStatement("INSERT INTO " + toTable + " SELECT *FROM " + fromTable + " WHERE " + field + "='" + value + "'");
        psLocal.executeUpdate();

    }

    public void generateLogForDataHide(String value) throws SQLException {
        String sql = "select * from opdbillhd where ref_no ='" + value + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLcoal = pstLocal.executeQuery();
        ResultSetMetaData rsMetadata = rsLcoal.getMetaData();
        sql = "insert into opdhidehd(";
        for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
            if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                sql += rsMetadata.getColumnLabel(i) + ",";
            }
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ") values (";
        for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
            if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                sql += "?,";
            }
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
        while (rsLcoal.next()) {
            for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
                if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                    pstUpdate.setString(i, rsLcoal.getString(rsMetadata.getColumnName(i)));
                }
            }
            pstUpdate.executeUpdate();
        }

        String rec_no = getData("select max(rec_no) from opdhidehd");

        //OPDBILLDT
        sql = "select * from opdbilldt where ref_no ='" + value + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        rsLcoal = pstLocal.executeQuery();
        rsMetadata = rsLcoal.getMetaData();
        sql = "insert into opdhidedt(";
        for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
            if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                sql += rsMetadata.getColumnLabel(i) + ",";
            }
        }
        sql += "rec_no) values (";
        for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
            if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                sql += "?,";
            }
        }
        sql += rec_no + ")";
        pstUpdate = dataConnection.prepareStatement(sql);
        while (rsLcoal.next()) {
            for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
                if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                    pstUpdate.setString(i, rsLcoal.getString(rsMetadata.getColumnName(i)));
                }
            }
            pstUpdate.executeUpdate();
        }

        //OPDBillDT finish
        //paymenthide
        sql = "select * from payment where ref_no ='" + value + "'";
        pstLocal = dataConnection.prepareStatement(sql);
        rsLcoal = pstLocal.executeQuery();
        rsMetadata = rsLcoal.getMetaData();
        sql = "insert into paymenthide(";
        for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
            if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                sql += rsMetadata.getColumnLabel(i) + ",";
            }
        }
        sql += "rec_no) values (";
        for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
            if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                sql += "?,";
            }
        }
        sql += rec_no + ")";
        pstUpdate = dataConnection.prepareStatement(sql);
        while (rsLcoal.next()) {
            for (int i = 1; i <= rsMetadata.getColumnCount(); i++) {
                if (!rsMetadata.getColumnLabel(i).equalsIgnoreCase("rec_no")) {
                    pstUpdate.setString(i, rsLcoal.getString(rsMetadata.getColumnName(i)));
                }
            }
            pstUpdate.executeUpdate();
        }

        //paymenthide finish
    }

    public void generateLog(String fromTable, String toTable, String field, String value, String new_ref_no) throws SQLException {

        PreparedStatement psLocal = null;
        psLocal = dataConnection.prepareStatement("update " + fromTable + " set " + field + "= '" + new_ref_no + "' WHERE rec_no=" + value + "");
        psLocal.executeUpdate();

        psLocal = dataConnection.prepareStatement("INSERT INTO " + toTable + " SELECT * FROM " + fromTable + " WHERE rec_no=" + value + "");
        psLocal.executeUpdate();

    }

    public String generateKey(String table, String column, int length, String prefix) {
        String code = "";
        int no = 0;
        PreparedStatement pstLocal = null;
        ResultSet rsLocal = null;
        try {
            pstLocal = dataConnection.prepareStatement("SELECT MAX(" + column + ") FROM " + table + " WHERE UPPER(" + column + ") LIKE '" + prefix.toUpperCase() + "%'");
            rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                if (rsLocal.getString(1) != null) {
                    String sno = rsLocal.getString(1).substring(prefix.length());
                    no = Integer.parseInt(sno);
                    no++;
                    for (int i = (no + "").length(); i < (length - prefix.length()); i++) {
                        code += "0";
                    }
                    code = prefix + code + no;
                } else {
                    code = prefix;
                    for (int i = 1; i < (length - prefix.length()); i++) {
                        code += "0";
                    }
                    code = code + "1";
                }
            } else {
                code = prefix;
                for (int i = 1; i < (length - prefix.length()); i++) {
                    code += "0";
                }
                code = code + "1";
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at generateKey", ex);
        }
        return code;
    }

    public String generateKey(String ref_no, int length, String prefix) {
        String code = "";
        int no = 0;
        String sno = ref_no.substring(prefix.length());
        no = Integer.parseInt(sno);
        no++;
        for (int i = (no + "").length(); i < (length - prefix.length()); i++) {
            code += "0";
        }
        code = prefix + code + no;
        return code;
    }

    public String getAcCode(String strVal, String tag) {
        PreparedStatement pstLocal = null;
        ResultSet rsLocal = null;
        String returnVal = "";
        String sql = "0";

        try {
            if (strVal.trim().equalsIgnoreCase("") && tag.equalsIgnoreCase("c")) {
                return "0";
            }
            if (tag.equalsIgnoreCase("C")) {
                sql = "select ac_cd from acntmst where ac_name='" + strVal + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select ac_name from acntmst where ac_cd=" + strVal + "";
            } else if (tag.equalsIgnoreCase("AC")) {
                sql = "select ac_cd from acntmst where ac_alias='" + strVal + "'";
            } else if (tag.equalsIgnoreCase("CA")) {
                sql = "select ac_alias from acntmst where ac_cd=" + strVal + "";
            }
            if (sql != null) {
                pstLocal = dataConnection.prepareStatement(sql);
                rsLocal = pstLocal.executeQuery();
                while (rsLocal.next()) {
                    returnVal = rsLocal.getString(1);
                }
                if (rsLocal != null) {
                    rsLocal.close();
                }
                if (pstLocal != null) {
                    pstLocal.close();
                }
            }
        } catch (Exception ex) {
            printToLogFile("Exception at getAcCode", ex);
        }

        return returnVal.trim();
    }

    public String getbillGrpCode(String code, String tag) {
        String stateCD = "0";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select bill_grp_cd from billgrpmst where bill_group_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select bill_group_name from billgrpmst where bill_grp_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getStateCD", ex);
        }
        return stateCD;
    }

    public String getbillitemCode(String code, String tag) {
        String stateCD = "0";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select bill_item_cd from billitemmst where bill_item_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select bill_item_name from billitemmst where bill_item_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getStateCD", ex);
        }
        return stateCD;
    }

    public String generateOPDNumber() {
        String opd = "";
        java.util.Date date = new java.util.Date();
        int a = (int) (isNumber(getData("select max(rec_no) from patientmst")));
        a++;
        opd += "O-12";
        for (int i = (a + "").length(); i < (9 - "O-12".length()); i++) {
            opd += "0";
        }
        opd += a;
        return opd;
    }

    public String generateOPDNumber(String table, String column, int length, String start) {
        String opd = start;
        java.util.Date date = new java.util.Date();
        opd += ((date.getYear() + 1900) + "").substring(2) + ((date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1)) + ((date.getDate() + 1) > 9 ? (date.getDate() + 1) : "0" + (date.getDate() + 1)) + "";
        opd = generateKey(table, column, length, opd);
        return opd;
    }

    public String getFloorCD(String code, String tag) {
        String stateCD = "";
        try {
            String sql = "";
            if (tag.equalsIgnoreCase("c")) {
                sql = "select floor_cd from floormst where floor_name='" + code + "'";
            } else if (tag.equalsIgnoreCase("N")) {
                sql = "select floor_name from floormst where floor_cd=" + code;
            }
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stateCD = rsLocal.getString(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at get Country CD", ex);
        }
        return stateCD;
    }

    public boolean CheckPatientError(String opd_no) {
        try {
            String sql = "SELECT a.opd_no,a.pt_name,a1.address,a1.city_cd,a1.mobile FROM patientmst a "
                    + "LEFT JOIN patientinfomst a1  ON a.opd_no= a1.opd_no WHERE (a1.mobile ='' OR a1.city_cd = 0) AND "
                    + "(a.opd_no ='" + opd_no + "')";
            PreparedStatement pstLcoal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLcoal.executeQuery();
            if (rsLocal.next()) {
                return false;
            }
        } catch (Exception ex) {
            printToLogFile("Exception at doctor wise", ex);
        }
        return true;
    }

    public boolean isExist(String sql) {
        boolean flag = false;
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;
        try {
            psLocal = dataConnection.prepareStatement(sql);
            rsLocal = psLocal.executeQuery();
            flag = rsLocal.next();
        } catch (Exception ex) {
            printToLogFile("Error at isExist in Library2", ex);
        } finally {
            closeResultSet(rsLocal);
            closeStatement(psLocal);
        }
        return flag;
    }

    public double getOpeningStock(String prd_cd, String column, String fromDate) {
        double stock = 0.00;
        try {
            String sql = "select sum(" + column + ") from oldb0_3 where bill_item_cd=" + prd_cd + " and (doc_cd='PUR') and V_date <'" + ConvertDateFormetForDB(fromDate) + "'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stock = rsLocal.getDouble(1);
            }
            sql = "select sum(" + column + ") from oldb0_3 where bill_item_cd=" + prd_cd + " and doc_cd='SAL' and V_date <'" + ConvertDateFormetForDB(fromDate) + "'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                stock -= rsLocal.getDouble(1);
            }
            closeResultSet(rsLocal);
            closeStatement(pstLocal);
        } catch (Exception ex) {
            printToLogFile("Exception at getopeningStock", ex);
        }
        return stock;
    }
}
