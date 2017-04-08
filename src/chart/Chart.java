/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import java.awt.Color;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author nice
 */
public class Chart {

        public static JFreeChart createBarChartByCategory(CategoryDataset dataset, String title, String xTitle, String yTitle, DecimalFormat decimalFormat) {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
                title, // chart title
                xTitle, // domain axis label
                yTitle, // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setBackgroundPaint(new Color(232,232,232));
        

        // set the range axis to display integers only...
        double max = getMax(dataset);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0D, max+50);
        if(decimalFormat!=null){
            rangeAxis.setNumberFormatOverride(decimalFormat);
        }else{
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
//        renderer.setSeriesPositiveItemLabelPosition(0,     
//                    new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -1.5707963267948966D)); 
//        renderer.setSeriesPositiveItemLabelPosition(1,     
//                    new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -1.5707963267948966D)); 

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));
        return chart;

    }
    
    public static String getMonthLabel(int month){
        String mth = "Unknown";
        switch(month){
            case 1:
                mth = "Jan";
                break;
            case 2:
                mth = "Feb";
                break;
            case 3:
                mth = "Mar";
                break;
            case 4:
                mth = "Apr";
                break;
            case 5:
                mth = "May";
                break;
            case 6:
                mth = "Jun";
                break;
            case 7:
                mth = "Jul";
                break;
            case 8:
                mth = "Aug";
                break;
            case 9:
                mth = "Sep";
                break;
            case 10:
                mth = "Oct";
                break;
            case 11:
                mth = "Nov";
                break;
            case 12:
                mth = "Dec";
                break;
        }
        return mth;
    }
    
    private static double getMax(CategoryDataset dataset) {
        double max = 0;
        for(int i=0;i<dataset.getRowCount();i++){
            for(int j=0;j<dataset.getColumnCount();j++){
                if(dataset.getValue(i, j).doubleValue()>max){
                    max = dataset.getValue(i, j).doubleValue();
                }
            }
        }
        return max;
    }
    
}
