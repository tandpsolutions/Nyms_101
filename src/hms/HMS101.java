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
    public static String ver = "1";
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

}
