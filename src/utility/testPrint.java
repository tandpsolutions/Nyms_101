/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author Bhaumik
 */
public class testPrint {
    
    public static void main(String[] args) {
        
        
        // Prepare date to print in dd/mm/yyyy format
        // Search for an installed zebra printer...
        // is a printer with "zebra" in its name
        try {
            PrintService psZebra = null;
            String sPrinterName = null;
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (int i = 0; i < services.length && services[i].getName().toString().equalsIgnoreCase("ZDesigner GT800 (EPL)"); i++) {
                psZebra = services[i];
                break;
            }

            if (psZebra == null) {
                System.out.println("Zebra printer is not found.");
                return;
            }

            System.out.println("Found printer: " + sPrinterName);
            DocPrintJob job = psZebra.createPrintJob();

            // Prepare string to send to the printer
            String s = "^XA\n"
                    + "^FO160,15^BY1\n"
                    + "^BCN,30,N,Y,N\n"
                    + "^FDSGI5SM12C5100008^FS\n"
                    + "^CF0,23"
                    + "^FO160,57^FDSGI5SM12C5100008^FS"
                    + "^FO160,83^FDMAT SG I5S^FS"
                    + "^FO160,107^FD150^FS"
                    //Second Label
                    + "^FO450,15^BY1\n"
                    + "^BCN,30,Y,Y,N\n"
                    + "^FDSGI5SM12C5100008^FS\n"
                    + "^CF0,23"
                    + "^FO450,57^FDSGI5SM12C5100008^FS"
                    + "^FO450,83^FDMAT SG I5S^FS"
                    + "^FO450,107^FD150^FS"
                    + "^XZ";   // Print content of buffer, 1 label

            byte[] by = s.getBytes();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            // MIME type = "application/octet-stream",
            // print data representation class name = "[B" (byte array).
            Doc doc = new SimpleDoc(by, flavor, null);
            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    }
}
