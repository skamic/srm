package com.sds.tech.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;

import com.sds.tech.ServerResourceMonitor;
import com.sds.tech.component.DataAccessManager;
import com.sds.tech.component.GraphManager;
import com.sds.tech.component.ServerConnector;
import com.sds.tech.ui.popup.AddNewServerPopup;
import com.sds.tech.ui.popup.ResultSettingsPopup;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceMonitorUI.
 */
public class ResourceMonitorUI extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8623816897416048151L;

	/** The srm. */
	private ServerResourceMonitor srm;

	/** The add new server popup. */
	private AddNewServerPopup addNewServerPopup;

	/** The result settings popup. */
	private ResultSettingsPopup resultSettingsPopup;

	/** The server list panel. */
	private JPanel serverListPanel;

	/** The status bar. */
	private JLabel statusBar;

	/** The log message area. */
	private JTextArea logMessageArea;

	/**
	 * Instantiates a new resource monitor ui.
	 *
	 * @param srm
	 *            the srm
	 */
	public ResourceMonitorUI(ServerResourceMonitor srm) {
		getContentPane().setName("");
		this.srm = srm;

		getContentPane().setComponentOrientation(
				ComponentOrientation.LEFT_TO_RIGHT);
		setMinimumSize(new Dimension(600, 400));
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(2);
		borderLayout.setHgap(2);

		initUI();

		this.addNewServerPopup = new AddNewServerPopup(this);
		this.resultSettingsPopup = new ResultSettingsPopup(this);
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
	 * Gets the status bar.
	 *
	 * @return the status bar
	 */
	public JLabel getStatusBar() {
		return statusBar;
	}

	/**
	 * Sets the status bar.
	 *
	 * @param statusBar
	 *            the new status bar
	 */
	public void setStatusBar(JLabel statusBar) {
		this.statusBar = statusBar;
	}

	/**
	 * Gets the log message area.
	 *
	 * @return the log message area
	 */
	public JTextArea getLogMessageArea() {
		return this.logMessageArea;
	}

	/**
	 * Inits the ui.
	 */
	private void initUI() {
		setTitle("System Resource Monitor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		createMenuBar();
		createLayout();
	}

	/**
	 * Creates the menu bar.
	 */
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		final JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
				"*.sl(Server List File)", "sl");
		fileChooser.setFileFilter(fileFilter);

		JMenuItem loadServerListMenuItem = new JMenuItem("Load Server List");
		loadServerListMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int result = fileChooser.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					File serverListFile = fileChooser.getSelectedFile();
					String fileName = serverListFile.getName();
					String extension = fileName.substring(
							fileName.lastIndexOf(".") + 1, fileName.length());

					if (DataAccessManager.SERVER_LIST_FILE_EXTENSION
							.equals(extension)) {
						getSrm().loadServerList(serverListFile);
					} else {
						// TODO alert popup
					}
				}
			}
		});

		JMenuItem saveServerListMenuItem = new JMenuItem("Save Server List");
		saveServerListMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showSaveDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					File serverListFile = fileChooser.getSelectedFile();

					getSrm().saveServerList(serverListFile);
				}
			}
		});

		fileMenu.add(loadServerListMenuItem);
		fileMenu.add(saveServerListMenuItem);

		JMenu optionMenu = new JMenu("Option");

		JMenuItem resultSettingsMenuItem = new JMenuItem("Result Settings");
		resultSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resultSettingsPopup.setVisible(true);
			}
		});

		optionMenu.add(resultSettingsMenuItem);

		menuBar.add(fileMenu);
		menuBar.add(optionMenu);

		setJMenuBar(menuBar);
	}

	/**
	 * Creates the layout.
	 */
	private void createLayout() {
		getContentPane().add(createLeftPanel(), BorderLayout.WEST);
		getContentPane().add(createRightPanel(), BorderLayout.CENTER);
		getContentPane().add(createLogPanel(), BorderLayout.EAST);
		getContentPane().add(createBottomPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Creates the log panel.
	 *
	 * @return the j panel
	 */
	private JPanel createLogPanel() {
		JPanel logPanel = new JPanel();
		logPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		logPanel.setLayout(new BorderLayout(0, 0));

		logMessageArea = new JTextArea();
		logMessageArea.setText("** System Resource Monitor v1.0 **");
		logMessageArea.setEditable(false);
		logPanel.add(logMessageArea, BorderLayout.CENTER);

		return logPanel;
	}

	/**
	 * Creates the left panel.
	 *
	 * @return the j panel
	 */
	private JPanel createLeftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(250, 10));
		leftPanel.setAutoscrolls(true);
		leftPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		leftPanel.setLayout(new BorderLayout(2, 2));

		JLabel lblServerList = new JLabel("Server List");
		leftPanel.add(lblServerList, BorderLayout.NORTH);

		createLeftServerListPanel();
		leftPanel.add(serverListPanel, BorderLayout.CENTER);

		return leftPanel;
	}

	/**
	 * Creates the left server list panel.
	 */
	private void createLeftServerListPanel() {
		serverListPanel = new JPanel();
		serverListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		serverListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		serverListPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED,
				null, null, null, null));
		serverListPanel.setAutoscrolls(true);

		JButton btnAddServer = new JButton("Add New Server");
		btnAddServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!srm.isStarted()) {
					addNewServerPopup.setVisible(true);
				}
			}
		});
		serverListPanel.setLayout(new MigLayout("", "[left]", "[top][]"));
		serverListPanel.add(btnAddServer, "cell 0 0,alignx left,growy,wrap");
	}

	/**
	 * Creates the right panel.
	 *
	 * @return the j panel
	 */
	private JPanel createRightPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		rightPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		rightPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		rightPanel.setLayout(new BorderLayout(0, 0));

		rightPanel.add(createRightButtonPanel(), BorderLayout.NORTH);
		rightPanel.add(createRightGraphPanel(), BorderLayout.CENTER);

		return rightPanel;
	}

	/**
	 * Creates the right button panel.
	 *
	 * @return the j panel
	 */
	private JPanel createRightButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		buttonPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		buttonPanel
				.setLayout(new MigLayout("", "[fill][fill][fill]", "[fill]"));

		JButton btnStartStop = new JButton("Start Monitoring");
		btnStartStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton source = (JButton) e.getSource();

				if (getSrm().getServerManager().getServerMap().size() == 0) {
					displayMessage("Please Add Server.");

					return;
				}

				if (getSrm().isStarted()) {
					getSrm().stopMonitoring();
					source.setText("Start Monitoring");
					displayMessage("Server Resource Monitoring Stop.");
				} else {
					getSrm().startMonitoring();
					source.setText("Stop Monitoring");
					displayMessage("Server Resource Monitoring Start.");
				}
			}
		});

		buttonPanel.add(btnStartStop, "cell 0 0,grow");

		JButton btnSaveImage = new JButton("Save as Image");
		btnSaveImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!srm.isStarted()) {
					getSrm().saveGraphAsImage();
				}
			}
		});
		buttonPanel.add(btnSaveImage, "cell 1 0,grow");

		JButton btnSaveUsageLog = new JButton("Save Usage Log");
		btnSaveUsageLog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!srm.isStarted()) {
					getSrm().saveResourceUsageLog();
				}
			}
		});
		buttonPanel.add(btnSaveUsageLog, "cell 2 0,grow");

		return buttonPanel;
	}

	/**
	 * Creates the right graph panel.
	 *
	 * @return the j panel
	 */
	private JPanel createRightGraphPanel() {
		GraphManager cpuGraphManager = srm.getCpuGraphManager();
		GraphManager memoryGraphManager = srm.getMemoryGraphManager();
		JPanel graphPanel = new JPanel();

		graphPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		graphPanel.setLayout(new MigLayout("", "[fill]", "[fill][fill]"));

		JPanel cpuUsagePanel = createCpuUsagePanel();
		JPanel memoryUsagePanel = createMemoryUsagePanel();

		graphPanel.add(cpuUsagePanel, "cell 0 0,grow");
		graphPanel.add(memoryUsagePanel, "cell 0 1,grow");

		cpuGraphManager.setChart(cpuUsagePanel);
		memoryGraphManager.setChart(memoryUsagePanel);

		return graphPanel;
	}

	/**
	 * Creates the cpu usage panel.
	 *
	 * @return the j panel
	 */
	private JPanel createCpuUsagePanel() {
		JPanel cpuUsagePanel = new JPanel();

		cpuUsagePanel.setLayout(new MigLayout("", "[fill]", "[top][fill]"));

		JLabel lblCpuUsage = new JLabel("CPU Usage (%)");
		cpuUsagePanel.add(lblCpuUsage, "cell 0 0,grow");

		final TimeSeriesCollection cpuDataset = new TimeSeriesCollection();
		JFreeChart cpuChart = ChartFactory.createTimeSeriesChart(null,
				"Elapsed Time (s)", "CPU Usage (%)", cpuDataset, true, true,
				false);

		final XYPlot plot = cpuChart.getXYPlot();
		srm.getCpuGraphManager().setPlot(plot);

		plot.setBackgroundPaint(new Color(0xffffe0));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.lightGray);

		ValueAxis xaxis = plot.getDomainAxis();
		xaxis.setAutoRange(true);
		xaxis.setVerticalTickLabels(true);

		ValueAxis yaxis = plot.getRangeAxis();
		yaxis.setRange(0.0, 100.0);

		ChartPanel cpuChartPanel = new ChartPanel(cpuChart);
		cpuChartPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		cpuChartPanel.setLayout(new MigLayout("", "[]", "[]"));

		cpuUsagePanel.add(cpuChartPanel, "cell 0 1,grow");

		return cpuUsagePanel;
	}

	/**
	 * Creates the memory usage panel.
	 *
	 * @return the j panel
	 */
	private JPanel createMemoryUsagePanel() {
		JPanel memoryUsagePanel = new JPanel();

		memoryUsagePanel.setLayout(new MigLayout("", "[fill]", "[top][fill]"));

		JLabel lblMemoryUsage = new JLabel("Memory Usage (%)");
		memoryUsagePanel.add(lblMemoryUsage, "cell 0 0,grow");

		final TimeSeriesCollection memoryDataset = new TimeSeriesCollection();
		JFreeChart memoryChart = ChartFactory.createTimeSeriesChart(null,
				"Elapsed Time (s)", "Memory Usage (%)", memoryDataset, true,
				true, false);

		final XYPlot plot = memoryChart.getXYPlot();
		srm.getMemoryGraphManager().setPlot(plot);

		plot.setBackgroundPaint(new Color(0xffffe0));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.lightGray);

		ValueAxis xaxis = plot.getDomainAxis();
		xaxis.setAutoRange(true);
		xaxis.setVerticalTickLabels(true);

		ValueAxis yaxis = plot.getRangeAxis();
		yaxis.setRange(0.0, 100.0);

		ChartPanel memoryChartPanel = new ChartPanel(memoryChart);
		memoryChartPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED,
				null, null, null, null));
		memoryChartPanel.setLayout(new MigLayout("", "[]", "[]"));

		memoryUsagePanel.add(memoryChartPanel, "cell 0 1,grow");

		return memoryUsagePanel;
	}

	/**
	 * Creates the bottom panel.
	 *
	 * @return the j panel
	 */
	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		bottomPanel.setPreferredSize(new Dimension(10, 40));
		bottomPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		bottomPanel.setLayout(new MigLayout("", "[fill]", "[15px]"));

		statusBar = new JLabel("");
		bottomPanel.add(statusBar, "cell 0 0,grow");

		return bottomPanel;
	}

	/**
	 * Adds the server.
	 *
	 * @param server
	 *            the server
	 */
	public void addServer(ServerConnector server) {
		serverListPanel.add(
				createServerItemPanel(server.getServerName(),
						server.getServerId()), "wrap");
		serverListPanel.revalidate();
		serverListPanel.repaint();
	}

	/**
	 * Creates the server item panel.
	 *
	 * @param serverName
	 *            the server name
	 * @param serverId
	 *            the server id
	 * @return the j panel
	 */
	private JPanel createServerItemPanel(String serverName, String serverId) {
		JPanel serverItemPanel = new JPanel();

		serverItemPanel.setLayout(new BoxLayout(serverItemPanel,
				BoxLayout.X_AXIS));
		serverItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		serverItemPanel.setVisible(true);

		JCheckBox chckbxServerName = new JCheckBox(serverName.toString());
		chckbxServerName.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxServerName.setSelected(true);
		chckbxServerName.setVisible(true);
		chckbxServerName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();

				srm.getCpuGraphManager().toggleChartDataset(source.getText(),
						source.isSelected());
				srm.getMemoryGraphManager().toggleChartDataset(
						source.getText(), source.isSelected());
			}
		});
		serverItemPanel.add(chckbxServerName);

		JButton btnDelete = new JButton("Delete");
		btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDelete.setVisible(true);
		btnDelete.setToolTipText(serverId);
		btnDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!srm.isStarted()) {
					JButton btnObj = (JButton) e.getSource();
					String serverId = btnObj.getToolTipText();

					getSrm().removeServer(serverId);

					serverListPanel.remove(btnObj.getParent());
					serverListPanel.revalidate();
					serverListPanel.repaint();
				}
			}

		});
		serverItemPanel.add(btnDelete);

		return serverItemPanel;
	}

	/**
	 * Removes the all servers.
	 */
	public void removeAllServers() {
		int compCount = serverListPanel.getComponentCount();

		for (int i = compCount - 1; i > 0; --i) {
			serverListPanel.remove(i);
		}

		serverListPanel.revalidate();
		serverListPanel.repaint();
	}

	/**
	 * Display message.
	 *
	 * @param message
	 *            the message
	 */
	public synchronized void displayMessage(String message) {
		getLogMessageArea().append("\n");
		getLogMessageArea().append(message);
	}
}
