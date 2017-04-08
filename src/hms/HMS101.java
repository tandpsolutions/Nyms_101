/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hms;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import java.sql.Connection;
import java.util.Properties;
import javax.swing.JOptionPane;
import login.Login;

/**
 *
 * @author Bhaumik
 */
public class HMS101 {

    /**
     */
    public static String ver = "2";
    public static String ip = "root";
    public static String port = "3306";
    public static String username = "root";
    public static String pwd = "root", database = "";
    public static Connection connMpAdmin = null;
    public static String currentDirectory = System.getProperty("user.dir");

    public static void main(String[] args) {
        // TODO code application logic here
        try {
            try {
                Properties property = new Properties();
                property.put("logoString", "");
                AcrylLookAndFeel.setTheme(property);
                com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Large-Font", "", "");
                javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            }
//            if (connectToDatabase()) {
                login.Login lg = new Login();
                lg.setVisible(true);
                lg.setLocationRelativeTo(null);
//            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getCause().getMessage());
        }
    }

//    private static void loadReport() {
//        SwingWorker workerForjbtnGenerate = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                try {
//                    JPanel jPanel3 = new JPanel();
//                    jPanel3.removeAll();
//                    String sql = "SELECT p.pt_name,i.admit_date,i.admit_time,case when (SELECT SUM(amt) FROM ipdbilldt "
//                            + " WHERE ipd_no=i.ipd_no AND is_del=0) is null then 0 else"
//                            + " (SELECT SUM(amt) FROM ipdbilldt WHERE ipd_no=i.ipd_no AND is_del=0) end as tot_bill,"
//                            + " case when (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
//                            + " AND amount>0) is null then 0 else (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no "
//                            + " AND amount>0) end AS advance ,"
//                            + " case when (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) is null then 0 else "
//                            + " (SELECT SUM(amount) FROM ipdpaymenthd WHERE ipd_no=i.ipd_no AND amount<0) end AS refund,r.room_cd,w.ward_name,"
//                            + " b1.BRANCH_NAME, b1.CMPN_NAME,b1.ADDRESS1,b1.ADDRESS2,b1.ADDRESS3,b1.MOBILE,b1.EMAIL,b1.TIN_NO FROM ipdreg i"
//                            + " LEFT JOIN ipdbilldt i1 ON i.ipd_no=i1.ipd_no LEFT JOIN patientmst p ON i.opd_no=p.opd_no "
//                            + " LEFT JOIN roommst r ON i.opd_no=r.opd_no LEFT JOIN wardmst w ON w.ward_cd=r.ward_cd"
//                            + " LEFT JOIN branchmst b1 ON b1.BRANCH_CD =1 where i.dis_date IS NULL group by i.ipd_no ";
//                    PreparedStatement pstLocal = connMpAdmin.prepareStatement(sql);
//                    ResultSet rsLocal = pstLocal.executeQuery();
//                    new Library().reportGenerator("EstimateReport.jasper", null, rsLocal, jPanel3);
//
//                    Library lb = new Library();
//                    sql = "select * from userrights";
//                    pstLocal = connMpAdmin.prepareStatement(sql);
//                    rsLocal = pstLocal.executeQuery();
//                    if (!rsLocal.next()) {
//                        sql = "insert into userrights (USER_ID,FORM_ID,VIEWS,EDIT,ADDS,DELETES,PRINT,NAVIGATE_VIEW) values (?,?,?,?,?,?,?,?)";
//                        PreparedStatement pstUpdate = connMpAdmin.prepareStatement(sql);
//                        for (int i = 1; i <= 10; i++) {
//                            pstUpdate.setInt(1, i);
//                            pstUpdate.setString(3, "0");
//                            pstUpdate.setString(4, "0");
//                            pstUpdate.setString(5, "0");
//                            pstUpdate.setString(6, "0");
//                            pstUpdate.setString(7, "0");
//                            pstUpdate.setString(8, "0");
//                            pstLocal = connMpAdmin.prepareStatement("select form_id from formmst");
//                            rsLocal = pstLocal.executeQuery();
//                            while (rsLocal.next()) {
//                                pstUpdate.setInt(2, rsLocal.getInt("form_id"));
//                                pstUpdate.execute();
//                            }
//                        }
//                    }
//                    sql = "UPDATE roommst SET opd_no = NULL,is_del=0 WHERE opd_no NOT IN(SELECT opd_no FROM ipdreg WHERE dis_date IS NULL) ";
//                    pstLocal = connMpAdmin.prepareStatement(sql);
//                    pstLocal.executeUpdate();
//                } catch (Exception ex) {
//                    System.out.println("");
////                    lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
//                }
//                return null;
//            }
//        };
//        workerForjbtnGenerate.execute();
//
//    }
   

}
