/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import hms.HMS101;
import hms.HMSHome;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import support.Library;

/**
 *
 * @author LENOVO
 */
public class ExcelReadExample {

    static Connection dataConnection = HMS101.connMpAdmin;
    static Library lb = new Library();

    public static void main(String args[]) {
        //
        // An excel file name. You can create a file name with a full path
        // information.
        //
        try {
//            dataConnection.setAutoCommit(false);
            String filename = HMS101.currentDirectory + File.separatorChar + "Account.xls";

            //
            // Create an ArrayList to store the data read from excel sheet.
            //
            List sheetData = new ArrayList();

            FileInputStream fis = null;
            //
            // Create a FileInputStream that will be use to read the excel file.
            //
            fis = new FileInputStream(filename);

            //
            // Create an excel workbook from the file system.
            //
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            //
            // Get the first sheet on the workbook.
            //
            HSSFSheet sheet = workbook.getSheetAt(0);

            //
            // When we have a sheet object in hand we can iterator on each
            // sheet's rows and on each row's cells. We store the data read
            // on an ArrayList so that we can printed the content of the excel
            // to the console.
            //
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                Iterator cells = row.cellIterator();

                List data = new ArrayList();
                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell) cells.next();
                    data.add(cell.toString().toUpperCase());
                }

                sheetData.add(data);
            }
            showExelData(sheetData);
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
            } catch (Exception e) {
            }
        }
    }

    private static void showExelData(List sheetData) throws SQLException {
        //
        // Iterates the data and print it out to the console.
        //
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            System.out.println(list.get(0));
        }
    }

}
