package chart;

import hms.HMS101;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import support.Library;

public class BranchWiseItemCountChart extends JPanel {

    private String title = "";
    private Library lb = new Library();
    private String from = null, to = null;
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
    public BranchWiseItemCountChart(String title, String from, String to) {
        this.title = title + " : By Date - From " + from + " To " + to;
        this.from = from;
        this.to = to;
        initOther();
    }

    public BranchWiseItemCountChart(String title, int month) {
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
            String sql = "SELECT COUNT(*),i.admit_date FROM ipdreg i left join patientmst p on i.opd_no = p.opd_no where p.ref_opd_no=''";
            if (from != null) {
                sql += " and  i.admit_date BETWEEN '" + lb.ConvertDateFormetForDB(from) + "' AND '" + lb.ConvertDateFormetForDB(to) + "'";
            }
            if (month != 0) {
                sql += " and  MONTH(i.admit_date)=" + month;
            }
            sql += " GROUP BY i.admit_date ";
            String series = "Admission Count";

            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                dataset.addValue(rsLocal.getInt(1), series, lb.ConvertDateFormetForDisply(rsLocal.getString("admit_date")));
            }

        } catch (Exception ex) {
            lb.printToLogFile("Error at createDataset in " + title, ex);
        } finally {
        }
        return dataset;

    }

}
