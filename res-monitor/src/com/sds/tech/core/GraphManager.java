package com.sds.tech.core;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.sds.tech.ServerResourceMonitor;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphManager.
 */
public class GraphManager implements Runnable {

	/** The srm. */
	private ServerResourceMonitor srm;

	/** The type. */
	private String type;

	/** The chart. */
	private Component chart;

	/** The plot. */
	private XYPlot plot;

	/** The time series map. */
	private Map<String, TimeSeries> timeSeriesMap;

	/**
	 * Instantiates a new graph manager.
	 *
	 * @param srm
	 *            the srm
	 * @param type
	 *            the type
	 */
	public GraphManager(ServerResourceMonitor srm, String type) {
		this.srm = srm;
		this.type = type;
		this.timeSeriesMap = new HashMap<String, TimeSeries>();
	}

	/**
	 * Gets the srm.
	 *
	 * @return the srm
	 */
	public ServerResourceMonitor getSrm() {
		return srm;
	}

	/**
	 * Sets the srm.
	 *
	 * @param srm
	 *            the new srm
	 */
	public void setSrm(ServerResourceMonitor srm) {
		this.srm = srm;
	}

	/**
	 * Sets the chart.
	 *
	 * @param graph
	 *            the new chart
	 */
	public void setChart(Component graph) {
		this.chart = graph;
	}

	/**
	 * Gets the plot.
	 *
	 * @return the plot
	 */
	public XYPlot getPlot() {
		return plot;
	}

	/**
	 * Sets the plot.
	 *
	 * @param plot
	 *            the new plot
	 */
	public void setPlot(XYPlot plot) {
		this.plot = plot;
	}

	/**
	 * Save chart as image.
	 *
	 * @param imageName
	 *            the image name
	 */
	public void saveChartAsImage(String imageName) {
		DataAccessManager dataAccessManager = srm.getDataAccessManager();
		StringBuffer imageNameBuffer = new StringBuffer();

		imageNameBuffer.append(imageName).append("_").append(this.type)
				.append(".png");

		String imagePath = dataAccessManager.getFileFullPath(imageNameBuffer
				.toString());

		BufferedImage image = new BufferedImage(this.chart.getWidth(),
				this.chart.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = image.createGraphics();

		this.chart.paint(graphics2D);

		try {
			ImageIO.write(image, "png", new File(imagePath));

			imageNameBuffer.append(" has been created.");
			srm.getMainUI().displayMessage(imageNameBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		int sequence = 1;
		DataAccessManager dataAccessManager = srm.getDataAccessManager();
		StringBuffer message = new StringBuffer();

		message.append(type.toUpperCase()).append(" GraphManager start.");
		srm.getMainUI().displayMessage(message.toString());

		initializeTimeSeriesMap();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		do {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Map<String, Integer> data = dataAccessManager.selectData(this.type,
					sequence);
			int datasetIndex = 0;

			for (String key : data.keySet()) {
				int percent = data.get(key);
				TimeSeries series = timeSeriesMap.get(key);
				RegularTimePeriod t = new Second();

				series.add(t, percent);

				if (sequence == 1) {
					plot.setDataset(datasetIndex, new TimeSeriesCollection(
							series));
					plot.setRenderer(datasetIndex++,
							new StandardXYItemRenderer());
				}
			}

			sequence++;
		} while (srm.isStarted());

		System.out.println(this.type + " : " + new Date());
	}

	/**
	 * Initialize time series map.
	 */
	private void initializeTimeSeriesMap() {
		Map<String, ServerConnector> serverMap = srm.getServerManager()
				.getServerMap();

		timeSeriesMap.clear();

		for (String serverId : serverMap.keySet()) {
			ServerConnector serverConnector = serverMap.get(serverId);
			String serverName = serverConnector.getServerName();

			timeSeriesMap.put(serverName, new TimeSeries(serverName));
		}
	}

	/**
	 * Toggle chart dataset.
	 *
	 * @param text
	 *            the text
	 * @param selected
	 *            the selected
	 */
	public void toggleChartDataset(String text, boolean selected) {
		int index = 0;

		for (String key : timeSeriesMap.keySet()) {
			if (key.equals(text)) {
				break;
			}

			index++;
		}

		plot.getRenderer(index).setSeriesVisible(0, new Boolean(selected));
	}
}
