package Assignment2;

import java.awt.Color;
import java.awt.Font;
import java.io.*;

import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.ChartUtilities; 

public class Plot_results {  
	String title;
	String x_title;
	String y_title;
	DefaultXYDataset line_chart_dataset = new DefaultXYDataset();
	
	/**
	 * Constructor for setting up a plot export
	 * @param title Plot title
	 * @param x_title Label for X axis
	 * @param y_title Label for Y axis
	 */
	public Plot_results(String title, String x_title, String y_title){
		  this.title = title;
		  this.x_title = x_title;
		  this.y_title = y_title;
	}
	
	/**
	 * Inserts a data serie into the plot when used with an exsitsing label the previous serie is overwritten
	 * @param x_data vector with data on X-axis
	 * @param y_data vector with data on Y-axis
	 * @param label serie label (cannot be a duplicate)
	 */
	//public void add_data_series(double[] x_data, double[] y_data, String label){
	public void add_data_series(double[] x_data, double[] y_data, String label){
		double[][] data = {x_data, y_data};
    	line_chart_dataset.addSeries(label, data);
    	
    	//final XYSeriesCollection dataset = new XYSeriesCollection();
    	
	}
	
	public void averaged_data(double[] x_data, double[] y_data, String label, int sampleSize, int nEpisodes){
		double average = 0;
		double[] averagedData = new double[nEpisodes/sampleSize];
		double[] xData = new double[nEpisodes/sampleSize];
		int c = 0;
		
		for(int i = 1; i <= y_data.length; i++){
			if( i % sampleSize == 0){
				System.out.printf("Average: %f,  %d\n", average, c);
				averagedData[c] = average/sampleSize;
				xData[c] = i - sampleSize;
				average = 0;
				c++;
				
			}
			else{
				System.out.println(y_data[i]);
				average = average + y_data[i];
			}
		}
		
		double[][] data = {xData, averagedData};
		line_chart_dataset.addSeries(label, data);
	}
	
	/**
	 * Exports the plot to an image
	 * @param filename filename (must be .PNG)
	 * @param width image width
	 * @param height image height
	 * @throws IOException 
	 */
	public void export_image(String filename, int width, int height, int sampleSize, int nEpisodes) throws IOException{
    	 	JFreeChart lineChartObject=ChartFactory.createXYLineChart(title,x_title,y_title,line_chart_dataset,PlotOrientation.VERTICAL,false,false, false);                
    	 	//JFreeChart chart = ChartFactory.createXYLineChart("hoi","Power [mW]","Current [mA]",line_chart_dataset,PlotOrientation.HORIZONTAL, false, true, false);
    	    XYPlot plot = (XYPlot) lineChartObject.getPlot();
    	    //plot.setDataset(this.datasetNumber,createDataset(col,retrieveData(pFile),1000));
    	   // plot.setRenderer( new XYLineAndShapeRenderer(true, false));
    	    
	     
    	 	NumberAxis yAxis = new NumberAxis();
    	 	NumberAxis xAxis = new NumberAxis();
        	yAxis.setTickUnit(new NumberTickUnit(10));
        	yAxis.setRange(0, 40);
        	yAxis.setLabel(y_title);
        	yAxis.setLabelFont(new Font("Dialog", Font.PLAIN, 15));
        	xAxis.setLabel(x_title);
        	xAxis.setLabelFont(new Font("Dialog", Font.PLAIN, 15));
            //xAxis.setTickUnit(new NumberTickUnit(sampleSize));
        	//xAxis.setRange(0, 5000);

        	//XYPlot plot = (XYPlot) lineChartObject.getPlot();
        	//plot.setRenderer(new XYSplineRenderer());
        	plot.setRangeAxis(yAxis);
        	
        	LegendTitle lt = new LegendTitle(plot);
        	lt.setItemFont(new Font("Dialog", Font.PLAIN, 10));
        	lt.setBackgroundPaint(Color.WHITE);
        	lt.setFrame(new BlockBorder(Color.white));
        	lt.setPosition(RectangleEdge.TOP);
    
        	XYTitleAnnotation ta = new XYTitleAnnotation(0.95, 0.95, lt,RectangleAnchor.TOP_RIGHT);

        	ta.setMaxWidth(0.3);
        	ta.setMaxHeight(0.1);
        	plot.addAnnotation(ta);
        	
    	 	File lineChart=new File(filename);              
	        ChartUtilities.saveChartAsPNG(lineChart,lineChartObject,width,height); 
	  
	}
	
	/*
	public static void main( String[] args ) {
		Plot_results plot = new Plot_results("Test", "x", "y");
		double[] x_data = {1.0, 4.0, 5.0};
		double[] y_data = {2.0, 5.0, 42.0};
		plot.add_data_series(x_data, y_data, "Sample");
		plot.export_image("test.png", 640, 480);
	}*/
 }