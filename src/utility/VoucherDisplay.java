/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import support.AmountInWords;
import support.Library;

/**
 *
 * @author nice
 */
public class VoucherDisplay extends javax.swing.JInternalFrame {

    Library lb = new Library();
    String ref_no = "";
    String tag = "";
    int lang = 0;
    Connection dataConnecrtion = HMS101.connMpAdmin;
    public JasperPrint print = null;

    /**
     * Creates new form voucherDisplay
     *
     * @param ref_no
     * @param tag
     */
    public VoucherDisplay(String ref_no, String tag) {
        initComponents();
        registerShortKeys();
        this.ref_no = ref_no;
        this.tag = tag;
        getVoucher(tag, ref_no, "Original");
    }

    private void registerShortKeys() {
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cancelOrClose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private void cancelOrClose() {
        this.dispose();
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

    private void getVoucher(String tag, String ref_no, String type) {
        if (tag.equalsIgnoreCase("OPD")) {
            generateOPDBill(ref_no, type, -1);
        } else if (tag.equalsIgnoreCase("VCC")) {
            generateVaccine(ref_no, type, -1);
        } else if (tag.equalsIgnoreCase("Advance") || tag.equalsIgnoreCase("Refund")) {
            generateAdvanced(ref_no, tag, -1);
        } else if (tag.equalsIgnoreCase("Late Payment")) {
            generateLatePayment(ref_no, type, -1);
        } else if (tag.equalsIgnoreCase("Discharge")) {
            generateDischarge(ref_no, type, -1);
        } else if (tag.equalsIgnoreCase("Intrim")) {
            generateDischargeIntrim(ref_no, type, -1);
        } else if (tag.equalsIgnoreCase("Label")) {
            generateLabel(ref_no, -1);
        }
    }

    private void getVoucherRTF(String tag, String ref_no, String type, int mode) {
        if (tag.equalsIgnoreCase("OPD")) {
            generateOPDBill(ref_no, type, mode);
        } else if (tag.equalsIgnoreCase("VCC")) {
            generateVaccine(ref_no, type, mode);
        } else if (tag.equalsIgnoreCase("Advanced") || tag.equalsIgnoreCase("Refund")) {
            generateAdvanced(ref_no, type, mode);
        } else if (tag.equalsIgnoreCase("Late Payment")) {
            generateLatePayment(ref_no, type, mode);
        } else if (tag.equalsIgnoreCase("Discharge")) {
            generateDischarge(ref_no, type, mode);
        } else if (tag.equalsIgnoreCase("Intrim")) {
            generateDischargeIntrim(ref_no, type, mode);
        } else if (tag.equalsIgnoreCase("Label")) {
            generateLabel(ref_no, mode);
        }
    }

    private void generateLabel(String ipd_no, int mode) {
        try {
            String opd_no = lb.getData("opd_no", "ipdreg", "ipd_no", ipd_no, 0);
            String name = lb.getData("pt_name", "patientmst", "opd_no", opd_no, 0);
            String sex = lb.getData("sex", "patientmst", "opd_no", opd_no, 0);
            String age = lb.getData("dob", "patientmst", "opd_no", opd_no, 0);
            String room_cd = lb.getData("room_cd", "ipdreg", "ipd_no", ipd_no, 0);
            String doc_cd = lb.getAcCode(lb.getData("doc_cd", "ipdreg", "ipd_no", ipd_no, 0), "N");
            age = lb.getDateDifferenceInDDMMYYYY(lb.dbFormat.parse(age), Calendar.getInstance().getTime());
            String admit_date = lb.ConvertDateFormetForDisply(lb.getData("admit_date", "ipdreg", "ipd_no", ipd_no, 0));

            String sql = "SELECT '" + name + "' AS  pt_name,case when " + sex + "=0 then 'Male' else 'Female' end AS sex,"
                    + "'" + age + "' as age,'" + admit_date + "' as admit_date,'" + ipd_no + "' as ipd_no,'" + opd_no + "' as opd_no,"
                    + "'" + room_cd + "' as room_cd, '" + doc_cd + "' as doc_cd "
                    + " FROM patientmst p LIMIT 0,30";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (mode == -1) {
                lb.reportGenerator("LabelPrint.jasper", null, rsLocal, jPanel1);
            } else {
                lb.reportGeneratorWord("LabelPrint.jasper", null, rsLocal);
            }
        } catch (Exception ex) {

        }
    }

    private void generateOPDBill(String ref_no, String type, int mode) {
        HashMap params = new HashMap();
        try {
            String sql = "SELECT o.ref_no,o.v_date,o.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex "
                    + " ,floor((DATEDIFF(CURDATE(),p.dob)/365)) AS age1,"
                    + " DATEDIFF(CURDATE(),p.dob) / 365 AS age,ac_name,b.bill_item_name,o1.amount,o1.disc,o1.final_amt,b1.BRANCH_NAME,"
                    + " b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO "
                    + "  FROM opdbillhd o LEFT JOIN opdbilldt o1 ON o.ref_no=o1.ref_no"
                    + " LEFT JOIN acntmst a ON o1.doc_cd=a.ac_cd LEFT JOIN patientmst p ON o.opd_no=p.opd_no  "
                    + " LEFT JOIN billitemmst b ON o1.bill_item_cd=b.bill_item_cd LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1"
                    + " where o.ref_no='" + ref_no + "'";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            params.put("type", type);
            params.put("words", new AmountInWords().convertToWords((int) lb.isNumber(lb.getData("net_amt", "OPDBILLHD", "REF_NO", ref_no, 0))));
            if (mode == -1) {
                print = lb.reportGenerator("OPDBill.jasper", params, rsLocal, jPanel1);
            } else {
                print = lb.reportGeneratorWord("OPDBill.jasper", params, rsLocal);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at generateOPDBill", ex);
        }
    }

    private void generateVaccine(String ref_no, String type, int mode) {
        HashMap params = new HashMap();
        try {
            String sql = "SELECT o.ref_no,o.v_date,o.opd_no,p.pt_name, "
                    + "CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex  , "
                    + "DATEDIFF(CURDATE(),dob) / 365 AS age,ac_name,b.bill_item_name,o.pur_qty,o.batch_no,o.exp_date,s.site_name, "
                    + "b1.BRANCH_NAME, b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO  "
                    + "FROM stkdsp o LEFT JOIN acntmst a ON o.doc_cd=a.ac_cd  "
                    + "LEFT JOIN patientmst p ON o.opd_no=p.opd_no  "
                    + "LEFT JOIN sitemst s ON o.site_cd=s.site_cd "
                    + "LEFT JOIN billitemmst b ON o.bill_item_cd=b.bill_item_cd LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1  "
                    + "WHERE o.ref_no='" + ref_no + "'";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            sql = "SELECT n.next_due_date,b.bill_item_name FROM  next_itt n LEFT JOIN billitemmst b ON n.bill_item_cd=b.bill_item_cd "
                    + "WHERE ref_no='" + ref_no + "'";
            ResultSet rsSel = dataConnecrtion.prepareStatement(sql).executeQuery();
            JRResultSetDataSource data = new JRResultSetDataSource(rsSel);
            params.put("data", data);
            params.put("dir", HMS101.currentDirectory);
            if (mode == -1) {
                print = lb.reportGenerator("vaccine.jasper", params, rsLocal, jPanel1);
            } else {
                print = lb.reportGeneratorWord("vaccine.jasper", params, rsLocal);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at generateVaccineBill", ex);
        }
    }

    private void generateAdvanced(String ref_no, String type, int mode) {
        HashMap params = new HashMap();
        try {
            String sql = "SELECT o1.ipd_no,o.ref_no,o.v_date,o1.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex "
                    + "  ,DATEDIFF(CURDATE(),dob) / 365 AS age,ac_name,o.amount,b1.BRANCH_NAME,"
                    + "  b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO "
                    + "  FROM ipdpaymenthd o LEFT JOIN ipdreg o1 ON o.ipd_no=o1.ipd_no"
                    + "  LEFT JOIN acntmst a ON o1.doc_cd=a.ac_cd LEFT JOIN patientmst p ON o1.opd_no=p.opd_no"
                    + "  LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1"
                    + "  WHERE o.ref_no='" + ref_no + "'";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            params.put("type", type);
            params.put("words", new AmountInWords().convertToWords(Math.abs((int) lb.isNumber(lb.getData("amount", "ipdpaymenthd", "REF_NO", ref_no, 0)))));
            params.put("dir1", HMS101.currentDirectory);
            if (mode == -1) {
                if (type.equalsIgnoreCase("Advanced")) {
                    print = lb.reportGenerator("AdvancedRefund.jasper", params, rsLocal, jPanel1);
                } else {
                    print = lb.reportGenerator("Refund.jasper", params, rsLocal, jPanel1);
                }
            } else {
                if (type.equalsIgnoreCase("Advanced")) {
                    print = lb.reportGeneratorWord("AdvancedRefund.jasper", params, rsLocal);
                } else {
                    print = lb.reportGeneratorWord("Refund.jasper", params, rsLocal);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at generateOPDBill", ex);
        }
    }

    private void generateLatePayment(String ref_no, String type, int mode) {
        HashMap params = new HashMap();
        try {
            String sql = "SELECT o1.opd_no as ipd_no,o.ref_no,o.v_date,o1.opd_no,p.pt_name,CASE WHEN p.sex = 0 THEN 'Male' ELSE 'Female' END AS sex   ,"
                    + "DATEDIFF(CURDATE(),dob) / 365 AS age,ac_name,o.amount,b1.BRANCH_NAME,  b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,"
                    + "b1.MOBILE,b1.EMAIL,b1.TIN_NO   FROM opdpaymenthd o LEFT JOIN opdbillhd o1 ON o.voucher_no=o1.ref_no "
                    + "LEFT JOIN acntmst a ON o1.doc_cd=a.ac_cd LEFT JOIN patientmst p ON o1.opd_no=p.opd_no "
                    + " LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1  WHERE o.ref_no='" + ref_no + "'";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            params.put("type", type);
            params.put("words", new AmountInWords().convertToWords(Math.abs((int) lb.isNumber(lb.getData("amount", "opdpaymenthd", "REF_NO", ref_no, 0)))));
            if (mode == -1) {
                print = lb.reportGenerator("AdvancedRefund.jasper", params, rsLocal, jPanel1);
            } else {
                print = lb.reportGeneratorWord("AdvancedRefund.jasper", params, rsLocal);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at generateOPDBill", ex);
        }
    }

    private void generateDischarge(String ref_no, String type, int mode) {
        HashMap params = new HashMap();
        try {
            String opd_no = lb.getData("OPD_NO", "IPDREG", "IPD_NO", ref_no, 0);
            String sql = "SELECT b1.bill_grp_cd,i.ref_no,i.dis_date,i.dis_time,i.ipd_no,i.opd_no,p.pt_name,p.sex,\n"
                    + "floor((DATEDIFF(CURDATE(),p.dob)/365)) AS age1, DATEDIFF(CURDATE(),p.dob) / 365 AS age,p1.address, \n"
                    + "b.bill_item_name,SUM(i1.qty) AS qty,i1.rate AS rate,SUM(i1.amt) AS amt,a1.ac_name as treat,b.third_party,\n"
                    + "  SUM(i1.disc) AS disc,SUM(i1.final_amt) AS final_amt,a.ac_name as head_dr,i.admit_date,i.admit_time,bill_group_name,\n"
                    + "  b2.BRANCH_NAME, i.disc_amt,i.paid_amt,b2.CMPN_NAME,b2.ADDRESS1,b2.ADDRESS2,b2.ADDRESS3,b2.MOBILE,b2.EMAIL,b2.TIN_NO \n"
                    + "  FROM ipdreg i LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no \n"
                    + "  LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no LEFT JOIN billitemmst b ON i1.bill_item_cd=b.bill_item_cd \n"
                    + "  LEFT JOIN acntmst a ON i.doc_cd=a.ac_cd LEFT JOIN billgrpmst b1 ON b.bill_grp_cd=b1.bill_grp_cd\n"
                    + "  LEFT JOIN acntmst a1 ON i1.doc_cd=a1.ac_cd LEFT JOIN branchmst b2 ON b2.BRANCH_CD=1 WHERE i1.is_hidden=0 \n"
                    + "  and i1.is_del=0 and i.ipd_no='" + ref_no + "' GROUP BY i1.bill_item_cd,i1.rate,a1.ac_name \n"
                    + "  order by b1.is_show,b1.bill_group_name,i.opd_no,a1.ac_name";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            sql = "SELECT v_date,amount,ref_no FROM ipdpaymenthd WHERE ipd_no ='" + ref_no + "'";
            PreparedStatement pstSel = dataConnecrtion.prepareStatement(sql);
            ResultSet rsSel = pstSel.executeQuery();
            net.sf.jasperreports.engine.JRResultSetDataSource result = new net.sf.jasperreports.engine.JRResultSetDataSource(rsSel);
            params.put("words", new AmountInWords().convertToWords((int) lb.isNumber(lb.getData("sum(amount)", "ipdpaymenthd", "ipd_no", ref_no, 0))));
            params.put("result", result);
            params.put("dir", HMS101.currentDirectory + File.separatorChar + "Reports" + File.separatorChar);
            params.put("dir1", HMS101.currentDirectory);
            params.put("advance", lb.isNumber(lb.getData("sum(amount)", "ipdpaymenthd", "ref_no like'AR%' and amount > 0 and ipd_no", ref_no, 0)));
            params.put("refund", lb.isNumber(lb.getData("sum(amount)", "ipdpaymenthd", "ref_no like'AR%' and amount < 0 and ipd_no", ref_no, 0)));
            params.put("service_charge", lb.isNumber(lb.getData("service_charge", "ipdreg", "ipd_no", ref_no, 0)));
            if (mode == -1) {
                print = lb.reportGenerator("IPDBill.jasper", params, rsLocal, jPanel1);
            } else {
                print = lb.reportGeneratorWord("IPDBill.jasper", params, rsLocal);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at generateOPDBill", ex);
        }
    }

    public void generateDischargeIntrim(String ref_no, String type, int mode) {
        HashMap params = new HashMap();
        try {
            String opd_no = lb.getData("OPD_NO", "IPDREG", "IPD_NO", ref_no, 0);
            String sql = "SELECT b1.bill_grp_cd,i.ref_no,i.dis_date,i.dis_time,i.ipd_no,i.opd_no,p.pt_name,p.sex,\n"
                    + "floor((DATEDIFF(CURDATE(),p.dob)/365)) AS age1, DATEDIFF(CURDATE(),p.dob) / 365 AS age,p1.address, \n"
                    + "b.bill_item_name,SUM(i1.qty) AS qty,i1.rate AS rate,SUM(i1.amt) AS amt,a1.ac_name as treat,b.third_party,\n"
                    + "  SUM(i1.disc) AS disc,SUM(i1.final_amt) AS final_amt,a.ac_name as head_dr,i.admit_date,i.admit_time,bill_group_name,\n"
                    + "  b2.BRANCH_NAME, i.disc_amt,i.paid_amt,b2.CMPN_NAME,b2.ADDRESS1,b2.ADDRESS2,b2.ADDRESS3,b2.MOBILE,b2.EMAIL,b2.TIN_NO \n"
                    + "  FROM ipdreg i LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no \n"
                    + "  LEFT JOIN patientinfomst p1 ON p.opd_no=p1.opd_no LEFT JOIN billitemmst b ON i1.bill_item_cd=b.bill_item_cd \n"
                    + "  LEFT JOIN acntmst a ON i.doc_cd=a.ac_cd LEFT JOIN billgrpmst b1 ON b.bill_grp_cd=b1.bill_grp_cd\n"
                    + "  LEFT JOIN acntmst a1 ON i1.doc_cd=a1.ac_cd LEFT JOIN branchmst b2 ON b2.BRANCH_CD=1 WHERE i1.is_hidden=0 \n"
                    + "  and i1.is_del=0 and i.ipd_no='" + ref_no + "' GROUP BY i1.bill_item_cd,i1.rate,a1.ac_name \n"
                    + "  order by b1.is_show,b1.bill_group_name,i.opd_no,a1.ac_name";
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            sql = "SELECT v_date,amount,ref_no FROM ipdpaymenthd WHERE ipd_no ='" + ref_no + "'";
            PreparedStatement pstSel = dataConnecrtion.prepareStatement(sql);
            ResultSet rsSel = pstSel.executeQuery();
            net.sf.jasperreports.engine.JRResultSetDataSource result = new net.sf.jasperreports.engine.JRResultSetDataSource(rsSel);
            params.put("words", new AmountInWords().convertToWords((int) lb.isNumber(lb.getData("sum(amount)", "ipdpaymenthd", "ipd_no", ref_no, 0))));
            params.put("result", result);
            params.put("dir", HMS101.currentDirectory + File.separatorChar + "Reports" + File.separatorChar);
            params.put("dir1", HMS101.currentDirectory);
            params.put("advance", lb.isNumber(lb.getData("sum(amount)", "ipdpaymenthd", "ref_no like'AR%' and amount > 0 and ipd_no", ref_no, 0)));
            params.put("refund", lb.isNumber(lb.getData("sum(amount)", "ipdpaymenthd", "ref_no like'AR%' and amount < 0 and ipd_no", ref_no, 0)));
            params.put("service_charge", lb.isNumber(lb.getData("service_charge", "ipdreg", "ipd_no", ref_no, 0)));
            if (mode == -1) {
                print = lb.reportGenerator("IPDIntrim.jasper", params, rsLocal, jPanel1);
            } else {
                print = lb.reportGeneratorWord("IPDIntrim.jasper", params, rsLocal);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at generateOPDBill", ex);
        }
    }

    private void printVoucher(int mode) {
        try {
            getVoucherRTF(tag, ref_no, "Original", mode);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at printVoucher", ex);
        }
    }

    public void afterUpdate(int mode) {
        printVoucher(mode);
        this.dispose();
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(348, 348, 348)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        afterUpdate(1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
