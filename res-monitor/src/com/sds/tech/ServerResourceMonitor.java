package com.sds.tech;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import com.sds.tech.core.DataAccessManager;
import com.sds.tech.core.GraphManager;
import com.sds.tech.core.ServerConnector;
import com.sds.tech.core.ServerManager;
import com.sds.tech.ui.ResourceMonitorUI;


// TODO: Auto-generated Javadoc
/**
 * The Class ServerResourceMonitor.
 */
public class ServerResourceMonitor {
	
	/** The main ui. */
	private ResourceMonitorUI mainUI;

	/** The data access manager. */
	private DataAccessManager dataAccessManager;
	
	/** The server manager. */
	private ServerManager serverManager;
	
	/** The cpu graph manager. */
	private GraphManager cpuGraphManager;
	
	/** The memory graph manager. */
	private GraphManager memoryGraphManager;

	/** The is started. */
	private boolean isStarted = false;

	/**
	 * Instantiates a new server resource monitor.
	 */
	public ServerResourceMonitor() {
		this.dataAccessManager = new DataAccessManager(this);
		this.serverManager = new ServerManager(this);
		this.cpuGraphManager = new GraphManager(this, "cpu");
		this.memoryGraphManager = new GraphManager(this, "memory");

		this.mainUI = new ResourceMonitorUI(this);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		ServerResourceMonitor srm = new ServerResourceMonitor();

		srm.openUI();
	}

	/**
	 * Gets the main ui.
	 *
	 * @return the main ui
	 */
	public ResourceMonitorUI getMainUI() {
		return mainUI;
	}

	/**
	 * Sets the main ui.
	 *
	 * @param mainUI the new main ui
	 */
	public void setMainUI(ResourceMonitorUI mainUI) {
		this.mainUI = mainUI;
	}

	/**
	 * Gets the server manager.
	 *
	 * @return the server manager
	 */
	public ServerManager getServerManager() {
		return serverManager;
	}

	/**
	 * Sets the server manager.
	 *
	 * @param connectionManager the new server manager
	 */
	public void setServerManager(ServerManager connectionManager) {
		this.serverManager = connectionManager;
	}

	/**
	 * Gets the cpu graph manager.
	 *
	 * @return the cpu graph manager
	 */
	public GraphManager getCpuGraphManager() {
		return cpuGraphManager;
	}

	/**
	 * Sets the cpu graph manager.
	 *
	 * @param cpuGraphManager the new cpu graph manager
	 */
	public void setCpuGraphManager(GraphManager cpuGraphManager) {
		this.cpuGraphManager = cpuGraphManager;
	}

	/**
	 * Gets the memory graph manager.
	 *
	 * @return the memory graph manager
	 */
	public GraphManager getMemoryGraphManager() {
		return memoryGraphManager;
	}

	/**
	 * Sets the memory graph manager.
	 *
	 * @param memoryGraphManager the new memory graph manager
	 */
	public void setMemoryGraphManager(GraphManager memoryGraphManager) {
		this.memoryGraphManager = memoryGraphManager;
	}

	/**
	 * Gets the data access manager.
	 *
	 * @return the data access manager
	 */
	public DataAccessManager getDataAccessManager() {
		return dataAccessManager;
	}

	/**
	 * Sets the data access manager.
	 *
	 * @param dataLoggingManager the new data access manager
	 */
	public void setDataAccessManager(DataAccessManager dataLoggingManager) {
		this.dataAccessManager = dataLoggingManager;
	}

	/**
	 * Checks if is started.
	 *
	 * @return true, if is started
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Sets the started.
	 *
	 * @param isStarted the new started
	 */
	private void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	/**
	 * Open ui.
	 */
	private void openUI() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				ResourceMonitorUI mainUI = getMainUI();

				mainUI.setExtendedState(JFrame.MAXIMIZED_BOTH);
				mainUI.setVisible(true);
			}
		});
	}

	/**
	 * Adds the server.
	 *
	 * @param serverConnector the server connector
	 */
	public void addServer(ServerConnector serverConnector) {
		this.serverManager.addServer(serverConnector);
		this.mainUI.addServer(serverConnector);
	}

	/**
	 * Removes the server.
	 *
	 * @param serverId the server id
	 */
	public void removeServer(String serverId) {
		this.serverManager.removeServer(serverId);
	}

	/**
	 * Load server list.
	 *
	 * @param serverListFile the server list file
	 */
	public void loadServerList(File serverListFile) {
		BufferedReader br = null;

		try {
			String buffer = null;
			int serverCount = 0;

			this.serverManager.initServerList();
			this.mainUI.removeAllServers();
			br = new BufferedReader(new FileReader(serverListFile));

			while ((buffer = br.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(buffer, ",");

				if (tokenizer.countTokens() == 5) {
					ServerConnector serverConnector = new ServerConnector(
							tokenizer.nextToken(), tokenizer.nextToken(),
							tokenizer.nextToken(), tokenizer.nextToken(),
							tokenizer.nextToken());

					addServer(serverConnector);
					serverCount++;
				}
			}

			StringBuffer message = new StringBuffer();
			message.append(serverCount).append(" Servers have been loaded.");
			this.mainUI.displayMessage(message.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save server list.
	 *
	 * @param serverListFile the server list file
	 */
	public void saveServerList(File serverListFile) {
		this.serverManager.saveServerList(serverListFile);
	}

	/**
	 * Start monitoring.
	 */
	public void startMonitoring() {
		try {
			setStarted(true);

			this.dataAccessManager.startMonitoring();
			this.serverManager.startMonitoring();

			Thread cpuGraphManagerThread = new Thread(this.cpuGraphManager,
					"CPU GraphManager");
			Thread memoryGraphManagerThread = new Thread(
					this.memoryGraphManager, "Memory GraphManager");

			cpuGraphManagerThread.start();
			memoryGraphManagerThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop monitoring.
	 */
	public void stopMonitoring() {
		setStarted(false);

		this.serverManager.stopMonitoring();
	}

	/**
	 * Save result settings.
	 *
	 * @param resultName the result name
	 * @param resultDirectoryPath the result directory path
	 */
	public void saveResultSettings(String resultName, String resultDirectoryPath) {
		this.dataAccessManager.saveResultSettings(resultName,
				resultDirectoryPath);
	}

	/**
	 * Save graph as image.
	 */
	public void saveGraphAsImage() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String imageName = formatter.format(new Date());

		this.dataAccessManager.createResultFolder();
		this.cpuGraphManager.saveChartAsImage(imageName);
		this.memoryGraphManager.saveChartAsImage(imageName);
	}

	/**
	 * Save resource usage log.
	 */
	public void saveResourceUsageLog() {
		this.dataAccessManager.createResultFolder();
		this.dataAccessManager.saveResourceUsageLog("cpu");
		this.dataAccessManager.saveResourceUsageLog("memory");
	}
}
