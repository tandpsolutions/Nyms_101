package utility;

import hms.HMS101;
import java.io.File;
import java.util.Date;
import utility.BackUpProcess;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CronJob implements Job {

    long processid = 0;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        try {
            System.out.println("Inside cron job");
            BackUpProcess bcp = new BackUpProcess();
            Date d = new Date();
            bcp.jButton1ActionPerformedauto(HMS101.database, "root", "root", HMS101.currentDirectory + File.separatorChar + "BackUp" + File.separator + (d.getDate()) + "_" + (d.getMonth() + 1) + "_" + (d.getYear() + 1900) + "_" + (d.getHours()) + "_" + (d.getMinutes()) + "_" + (d.getSeconds()) + ".sql",
                    HMS101.currentDirectory + File.separatorChar + "lib" + File.separatorChar + "bc.exe");
            /*String osName = System.getProperty("os.name").toLowerCase();
             Runtime.getRuntime().exec(CronSchedule.filePath);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
