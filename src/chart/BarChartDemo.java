/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import hms.HMSHome;
import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
import support.Library;

/**
 *
 * @author nice
 */
public class BarChartDemo extends javax.swing.JInternalFrame {

    /**
     * Creates new form BarChartDemo
     */
    private Library lb = new Library();
    private String xTitle = "", yTitle = "", cTitle = "";

    public BarChartDemo() {
        initComponents();
        initOther();
    }

    private void initOther() {
        lb.setDateChooserPropertyInit(jtxtToDate2);
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setShortcut(this, jbtnClose);
//        setCounter();
        compEnable();
    }

    private void compEnable() {

        if (jchkDate.isEnabled()) {
            jtxtFromDate.setEnabled(jchkDate.isSelected());
            jtxtToDate2.setEnabled(jchkDate.isSelected());
        } else {
            jtxtFromDate.setEnabled(false);
            jtxtToDate2.setEnabled(false);
        }

        if (jchkMonth.isEnabled()) {
            jcmbMonth.setEnabled(jchkMonth.isSelected());
        } else {
            jcmbMonth.setEnabled(false);
        }
    }

    private void generateChart() {
        jPanel1.removeAll();
        if (jchkDate.isSelected()) {
            jPanel1.add(new BranchWiseItemCountChart("Date Wise Admission", jtxtFromDate.getText(), jtxtToDate2.getText()));
        } else if (jchkMonth.isSelected()) {
            jPanel1.add(new BranchWiseItemCountChart("Date Wise Admission", jcmbMonth.getSelectedIndex() + 1));
        }

        SwingUtilities.updateComponentTreeUI(jPanel1);
    }

    private JFreeChart createChart(CategoryDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
                cTitle, // chart title
                xTitle, // domain axis label
                yTitle, // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("0.00"));
//            rangeAxis.setTickUnit(new NumberTickUnit(0.5));
//            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        chart.getCategoryPlot().setRenderer(renderer);
        renderer.setDrawBarOutline(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));

        // set up gradient paints for series...
//        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
//                0.0f, 0.0f, new Color(0, 0, 64));
//        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
//                0.0f, 0.0f, new Color(0, 64, 0));
//        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
//                0.0f, 0.0f, new Color(64, 0, 0));
//        renderer.setSeriesPaint(0, gp0);
//        renderer.setSeriesPaint(1, gp1);
//        renderer.setSeriesPaint(2, gp2);
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;

    }

    
    @Override
    public void dispose() {
        try {
            HMSHome.removeFromScreen(HMSHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at " + getTitle(), ex);
        }
    }

    private boolean validateData() {
        if (jchkDate.isSelected()) {
            //        if(jcmbType.getSelectedIndex()==3){
            if (lb.checkDate(jtxtFromDate)) {
                JOptionPane.showMessageDialog(this, "Invalid From Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtFromDate.requestFocusInWindow();
                return false;
            }
            if (lb.checkDate(jtxtToDate2)) {
                JOptionPane.showMessageDialog(this, "Invalid To Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtToDate2.requestFocusInWindow();
                return false;
            }
            //        }
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jcmbMonth = new javax.swing.JComboBox();
        jbtnView = new javax.swing.JButton();
        jchkDate = new javax.swing.JRadioButton();
        jchkMonth = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();
        jtxtFromDate = new com.toedter.calendar.JDateChooser();
        jtxtToDate2 = new com.toedter.calendar.JDateChooser();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jcmbMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));

        jbtnView.setText("View");
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });
        jbtnView.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnViewKeyPressed(evt);
            }
        });

        buttonGroup1.add(jchkDate);
        jchkDate.setText("By Date");
        jchkDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchkDateItemStateChanged(evt);
            }
        });

        buttonGroup1.add(jchkMonth);
        jchkMonth.setSelected(true);
        jchkMonth.setText("By Month");
        jchkMonth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchkMonthItemStateChanged(evt);
            }
        });

        jLabel4.setText("To");

        jLabel3.setText("From");

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jchkDate)
                    .addComponent(jchkMonth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtToDate2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jcmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 425, Short.MAX_VALUE)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnClose)
                        .addComponent(jbtnView))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jchkMonth)
                        .addComponent(jcmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jchkDate)
                    .addComponent(jLabel3)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtToDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabel4, jchkDate, jtxtFromDate, jtxtToDate2});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        SwingWorker workerForjbtnGenerate = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                lb.addGlassPane(BarChartDemo.this);
                if (validateData()) {
                    generateChart();
                }
                lb.removeGlassPane(BarChartDemo.this);
                return null;
            }
        };
        workerForjbtnGenerate.execute();
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jchkMonthItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchkMonthItemStateChanged
        compEnable();
    }//GEN-LAST:event_jchkMonthItemStateChanged

    private void jchkDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchkDateItemStateChanged
        compEnable();
    }//GEN-LAST:event_jchkDateItemStateChanged

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnView;
    private javax.swing.JRadioButton jchkDate;
    private javax.swing.JRadioButton jchkMonth;
    private javax.swing.JComboBox jcmbMonth;
    private com.toedter.calendar.JDateChooser jtxtFromDate;
    private com.toedter.calendar.JDateChooser jtxtToDate2;
    // End of variables declaration//GEN-END:variables
}
