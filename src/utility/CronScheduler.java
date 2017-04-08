package utility;

import hms.HMS101;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class CronScheduler {

    static int i = 0;
    public static String filePath = "";

    public CronScheduler(int i) throws Exception {
        System.out.println("In CronSch Constructor..");
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        File f = new File("Scheduler.properties");
        Properties properties = new Properties();
        properties.load(new FileReader(f));
        String seconds = properties.getProperty("second");
        System.out.println("Second : " + seconds);
        String minutes = properties.getProperty("minute");
        System.out.println("Minutes : " + minutes);
        String hours = properties.getProperty("hour");
        System.out.println("Hours : " + hours);
        String dayOfMonth = properties.getProperty("day_of_month");
        String dayOfWeek = properties.getProperty("day_of_week");
        String year = properties.getProperty("year");
        filePath = HMS101.currentDirectory + File.separator + "backup.sql";

        JobDetail jd = new JobDetail("job" + i, "group" + i, CronJob.class);
		 // CronTrigger ct=new CronTrigger("cronTrigger","group2","0 0/5 * * * ?");

        CronTrigger ct = new CronTrigger("cronTrigger" + i, "group" + i, seconds + " " + minutes + " " + hours + " " + dayOfMonth + " " + dayOfWeek + " " + year);
        System.out.println("ct : " + ct.toString());
        sched.scheduleJob(jd, ct);
        sched.start();
    }

    public static void main(String args[]) {
        try {
            new CronScheduler(i);
            i++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
