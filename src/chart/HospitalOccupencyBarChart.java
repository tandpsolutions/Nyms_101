package chart;

import hms.HMS101;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import support.Library;

public class HospitalOccupencyBarChart extends JPanel {

    private String title = "";
    private Library lb = new Library();
    private int month = 0;
    Connection dataConnection = HMS101.connMpAdmin;

    {
        // set a theme using the new shadow generator feature available in
        // 1.0.14 - for backwards compatibility it is not enabled by default
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
                true));
    }

    /**
     * Creates a new demo instance.
     *
     * @param title the frame title.
     */
    public HospitalOccupencyBarChart(String title, int month) {
        this.title = title + " : By Month - " + Chart.getMonthLabel(month);
        this.month = month;
        initOther();
    }

    private void initOther() {
        CategoryDataset dataset = createDataset();
        if (dataset != null) {
            JFreeChart chart = Chart.createBarChartByCategory(dataset, title, "Admission", "Count", null);
//            JFreeChart chart = createChart(dataset);
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setFillZoomRectangle(true);
            chartPanel.setMouseWheelEnabled(true);
            setLayout(new java.awt.BorderLayout());
            add(chartPanel);
        }
    }

    /**
     * Returns a sample dataset.
     *
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            String year = lb.getData("SELECT YEAR(CURDATE()) FROM DUAL");
            String sql = "";
            if (month != 0) {
                sql = "SELECT DATEDIFF(LAST_DAY('" + year + "-" + month + "-01'),'" + year + "-" + month + "-1')+1 FROM dual";
            }
            String series = "Hospital Occupency";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            int array[] = null;
            if (rsLocal.next()) {
                array = new int[rsLocal.getInt(1)];
            }
            for (int i = 0; i < array.length; i++) {
                array[i] = 0;
            }

            sql = "SELECT DAY(CASE WHEN (admit_date) <'" + year + "-" + month + "-01' THEN '" + year + "-" + month + "-01' ELSE admit_date END) AS START,"
                    + "DATEDIFF(CASE WHEN (admit_date) <'" + year + "-" + month + "-01' THEN '" + year + "-" + month + "-01' ELSE admit_date END,"
                    + "CASE WHEN dis_date IS NULL THEN CURDATE() ELSE dis_date END)*-1 AS dis_date FROM ipdreg i left join patientmst p on i.opd_no=p.opd_no "
                    + " WHERE ((dis_date >='" + year + "-" + month + "-01' AND dis_date <=LAST_DAY('" + year + "-" + month + "-01')) OR dis_date IS NULL)"
                    + " and admit_date <= LAST_DAY('" + year + "-" + month + "-01') and p.ref_opd_no ='' ";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                int j = rsLocal.getInt(1) - 1;
                for (int i = 0; i < rsLocal.getInt(2); i++) {
                    if (j<array.length) {
                        array[j] = array[j] + 1;
                        j++;
                    } else {
                        break;
                    }
                }
            }
            for (int i = 0; i < array.length; i++) {
                dataset.addValue(array[i], series, (i + 1) + "-" + month + "-" + year);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at createDataset in " + title, ex);
        } finally {
        }
        return dataset;

    }

}
