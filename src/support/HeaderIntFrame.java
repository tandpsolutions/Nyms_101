/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * HeaderDlg.java
 *
 * Created on 22-Sep-2009, 16:52:30
 */
package support;

import hms.HMSHome;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.InputMap;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import master.AccountMaster;
import master.AreaMaster;
import master.BillingGroupMaster;
import master.BillingItemMaster;
import master.CityMaster;
import master.CountryMaster;
import master.DoctorMaster;
import master.GroupMaster;
import master.SiteMaster;
import master.SpecialityMsater;
import master.StateMaster;
import master.SubSpecialtyMaster;
import master.WardMaster;
import transaction.IPDAdvanceReceipt;
import transaction.IPDBillGenerationDischarge;
import transaction.IPDDueBillGeneration;
import transaction.IPDRegistrationForm;
import transaction.OPDBillGeneration;
import transaction.OPDLatePayment;
import transaction.PatientMaster;
import transaction.VaccineDispose;
import transaction.VaccineStockAdd;
import utility.ManageUser;
import utility.UserGroupMaster;

/**
 *
 * @author Administrator
 */
public class HeaderIntFrame extends HeaderFooterMain {

    public ResultSet viewRSHeader = null;
    private DefaultTableModel dtmHeader = null;
    Library lb = new Library();
    private String strHeader = "";
    private Connection dataConnection;
    private String strCode = "";
    private String docCd = "";
    private int iColNo = 1;
    private Object form;
    private KeyListener keyListen;
    boolean direcMode = false;
    Object[] header = null;
    ArrayList<Class> classNameArray = new ArrayList<Class>();
    HashMap headerName = new HashMap();
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    String returnTitle = "";
    ReportTable rptHeader = null;
    /**
     * Creates new form HeaderDlg
     */
    String Syspath = System.getProperty("user.dir");

    public HeaderIntFrame(Connection con, String strCode, String titleName, String strHeader, String docCd, int iColNo, Object form, String returnTitle, ReportTable table) {
        initComponents();
        setTitle(titleName);
        dataConnection = con;
        this.strHeader = strHeader;
        this.docCd = docCd;
        this.strCode = strCode;
        this.iColNo = iColNo;
        this.form = form;
        rptHeader = table;
        searchOnTextFields();
        this.returnTitle = returnTitle;
    }

    private String GetCode(String strAlias) {
        String strLocalCode = "";

        try {
//            if (docCd.equalsIgnoreCase("10")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("11")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("12")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("13")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("14")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("15")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("16")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("17")) {
//                strLocalCode = strAlias;
//            } else if (docCd.equalsIgnoreCase("18")) {
            strLocalCode = strAlias;
//            }

        } catch (Exception ex) {
            lb.printToLogFile("Exception at GetInvNo in HeaderFooterDlg..!!", ex);
        }
        return strLocalCode;
    }

    private void setValueToVoucher() {
        if (docCd.equalsIgnoreCase("10")) {
            ((CountryMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("11")) {
            ((StateMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("12")) {
            ((CityMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("13")) {
            ((AreaMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("14")) {
            ((GroupMaster) form).setGroupid(strCode);
        } else if (docCd.equalsIgnoreCase("15")) {
            ((AccountMaster) form).setId(strCode);
        } else if (docCd.equalsIgnoreCase("16")) {
            ((BillingGroupMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("17")) {
            ((BillingItemMaster) form).setId(strCode);
        } else if (docCd.equalsIgnoreCase("18")) {
            ((SpecialityMsater) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("19")) {
            ((DoctorMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("20")) {
            ((PatientMaster) form).setId(strCode);
        } else if (docCd.equalsIgnoreCase("21")) {
            ((WardMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("22")) {
            ((SubSpecialtyMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("23")) {
            ((IPDDueBillGeneration) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("24")) {
            ((IPDAdvanceReceipt) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("25")) {
            ((IPDBillGenerationDischarge) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("26")) {
            ((OPDBillGeneration) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("27")) {
            ((UserGroupMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("28")) {
            ((IPDRegistrationForm) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("29")) {
            ((OPDLatePayment) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("30")) {
            ((ManageUser) form).setID(strCode);
        }  else if (docCd.equalsIgnoreCase("51")) {
            ((VaccineStockAdd) form).setID(strCode);
        }  else if (docCd.equalsIgnoreCase("54")) {
            ((SiteMaster) form).setID(strCode);
        } else if (docCd.equalsIgnoreCase("55")) {
            ((VaccineDispose) form).setID(strCode);
        }
    }

    @Override
    public void dispose() {
        try {

            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    public void makeView() {

        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

//        SwingWorker swingWorkerForViewProperty = new SwingWorker() {
//
//            @Override
//            protected Object doInBackground() throws Exception {
        SetFormProperty();
        makeViewRoutine();
        selectRecord();
        lb.setColumnSizeForTable(rptHeader, jPanelHeader.getWidth());
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

//                return null;
//            }
//        };
//        swingWorkerForViewProperty.execute();
    }

    private void makeViewRoutine() {

        rptHeader.setEnabled(false);
        jbtnViewResult.setEnabled(false);

        dtmHeader = (DefaultTableModel) rptHeader.getModel();

        jPanelHeader.removeAll();
        rptHeader.addTable(jPanelHeader);

        if (direcMode) {
            fillTableHeaderDirect();
        } else {
            fillTableHeader();
        }

        SetFormProperty();

        headerTableListener();
        setLayer(1);

        InputMap im = rptHeader.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

        rptHeader.setEnabled(true);
        jbtnViewResult.setEnabled(true);

    }

    private void fillTableHeader() {
        try {
            PreparedStatement pst = dataConnection.prepareStatement(strHeader, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            viewRSHeader = pst.executeQuery();
            Vector newrow = null;
            dtmHeader.setRowCount(0);

            while (viewRSHeader.next()) {
                newrow = new Vector();
                for (int i = 0; i < rptHeader.getColumnCount(); i++) {
                    if (viewRSHeader.getMetaData().getColumnType(i + 1) == Types.DATE) {
                        newrow.add(lb.ConvertDateFormetForDisply(viewRSHeader.getString(i + 1)));
                    } else if (viewRSHeader.getMetaData().getColumnType(i + 1) == Types.DOUBLE) {
                        newrow.add(viewRSHeader.getDouble(i + 1));
                    } else {
                        newrow.add(viewRSHeader.getObject(i + 1));
                    }
                }
                dtmHeader.addRow(newrow);
            }
            System.gc();
            viewRSHeader.beforeFirst();
//            serchOnViewTable(jPanelHeader,rptHeader);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at fillTableHeader in HeaderIntFrame..!!", ex);
        }
    }

    private void fillTableHeaderDirect() {
        try {
            Vector newrow = null;
            dtmHeader.removeRow(0);

            while (viewRSHeader.next()) {
                newrow = new Vector();
                for (int i = 0; i < rptHeader.getColumnCount(); i++) {
                    if (viewRSHeader.getMetaData().getColumnType(i + 1) == Types.DATE) {
                        newrow.add(lb.ConvertDateFormetForDisply(viewRSHeader.getString(i + 1)));
                    } else if (viewRSHeader.getMetaData().getColumnType(i + 1) == Types.DOUBLE) {
                        newrow.add(viewRSHeader.getDouble(i + 1));
                    } else {
                        newrow.add(viewRSHeader.getObject(i + 1));
                    }
                }
                dtmHeader.addRow(newrow);
            }
            System.gc();
            viewRSHeader.beforeFirst();
//            serchOnViewTable(jPanelHeader,rptHeader);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at fillTableHeader in HeaderIntFrame..!!", ex);
        }
    }

    public void selectRecord() {
        try {
            rptHeader.requestFocusInWindow();
            int iIndex = 0;
            viewRSHeader.beforeFirst();
            while (viewRSHeader.next()) {
                if (!viewRSHeader.getString(iColNo).equalsIgnoreCase(strCode)) {
                    iIndex++;
                } else {
                    break;
                }
            }
            rptHeader.setRowSelectionInterval(iIndex, iIndex);
            setViewPort(rptHeader, iIndex, 0);
            lb.setColumnSizeForTable(rptHeader, jPanelHeader.getWidth());
        } catch (Exception ex) {
            lb.printToLogFile("Exception at selectRecord in HeaderIntFrame..!!", ex);
        } finally {
            lb.closeResultSet(viewRSHeader);
        }
    }

    private void searchOnTextFields() {
        this.rowSorter = new TableRowSorter<>(rptHeader.getModel());
        rptHeader.setRowSorter(rowSorter);
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

    private void setViewPort(JTable tableViewOnViewAndSearchPanel, int iCol, int iTheCol) {
        tableViewOnViewAndSearchPanel.setRowSelectionAllowed(true);
        tableViewOnViewAndSearchPanel.setRowSelectionInterval(iCol, iCol);
        tableViewOnViewAndSearchPanel.changeSelection(iCol, iTheCol, false, false);

        //Get the row at First
        JViewport viewport = (JViewport) tableViewOnViewAndSearchPanel.getParent();
        Rectangle rect = tableViewOnViewAndSearchPanel.getCellRect(iCol, iTheCol, true);
        Rectangle viewRect = viewport.getViewRect();
        rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);
        int centerX = (viewRect.width - rect.width);
        int centerY = (viewRect.height - rect.height);
        rect.translate(centerX, centerY);
        viewport.scrollRectToVisible(rect);
    }

    private void setAlias(String strAlias) {
        try {
            strCode = GetCode(strAlias);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setAlias in HeaderIntFrame..!!", ex);
        }
    }

    private void setAliasRoutine() {

        if (rptHeader.getSelectedRow() != -1) {

            Object tableValue = rptHeader.getValueAt(rptHeader.getSelectedRow(), (iColNo - 1));

            if (tableValue != null) {
                setAlias(tableValue.toString().trim());
            }

        }
    }

    private void headerTableListener() {
        rptHeader.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                final MouseEvent tempE = e;

                setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                javax.swing.SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        Thread threadTableClick = new Thread() {
                            public void run() {

                                if (tempE.getClickCount() >= 1) {
                                    //setAlias(rptHeader.getValueAt(rptHeader.getSelectedRow(), (iColNo-1)).toString().trim());
                                    setAliasRoutine();
                                }

                                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                            }
                        };//ThreadGoNavigate End

                        threadTableClick.start();
                    }
                });

            }
        });

        keyListen = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
//                if(e.getKeyCode() == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED)
//                {
//                    jbtnViewResult.doClick();
//                }

                final KeyEvent tempE = e;

                setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        Thread threadForrptHeaderKeyPressed = new Thread() {
                            @Override
                            public void run() {
                                rptHeaderkeyPressedRoutine(tempE);
                                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                            }
                        };
                        threadForrptHeaderKeyPressed.start();

                    }
                });

            }

            private void rptHeaderkeyPressedRoutine(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    //setAlias(rptHeader.getValueAt(rptHeader.getSelectedRow(), (iColNo-1)).toString().trim());
                    setAliasRoutine();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_RELEASED) {
                    jbtnClose.doClick();
                }

            }

            public void keyTyped(KeyEvent e) {
            }

        };

        rptHeader.addKeyListener(keyListen);
    }

    private void SetFormProperty() {
        jbtnClose.setVisible(true);
        jbtnViewResult.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelHeader = new javax.swing.JPanel();
        jbtnViewResult = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        panel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(950, 621));

        jPanelHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelHeader.setPreferredSize(new java.awt.Dimension(750, 420));
        jPanelHeader.setLayout(new javax.swing.BoxLayout(jPanelHeader, javax.swing.BoxLayout.LINE_AXIS));

        jbtnViewResult.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jbtnViewResult.setMnemonic('V');
        jbtnViewResult.setText("View Form");
        jbtnViewResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewResultActionPerformed(evt);
            }
        });

        jbtnClose.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jbtnClose.setMnemonic('C');
        jbtnClose.setText("Back");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanelHeader, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jbtnViewResult)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtnClose)))
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(39, 39, 39)
                    .addComponent(jPanelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnClose)
                        .addComponent(jbtnViewResult))
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnViewResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewResultActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                Thread threadGoNavigate = new Thread() {
                    public void run() {
                        jbtnViewResultActionPerformedRoutine();
                        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    }
                };//ThreadGoNavigate End

                threadGoNavigate.start();
            }
        });
}//GEN-LAST:event_jbtnViewResultActionPerformed

    private void jbtnViewResultActionPerformedRoutine() {

        setValueToVoucher();
        rptHeader.removeKeyListener(keyListen);
        this.dispose();
        HMSHome.addOnScreen(((JInternalFrame) form), returnTitle, -1);

    }

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed

        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                Thread threadForClose = new Thread() {
                    public void run() {
                        jbtnCloseActionPerformedRoutine();
                        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    }
                };//ThreadGoNavigate End

                threadForClose.start();
            }
        });
        //this.dispose();
}//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseActionPerformedRoutine() {

        this.dispose();
        HMSHome.addOnScreen((JInternalFrame) form, returnTitle, -1);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnViewResult;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}
