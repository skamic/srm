package com.sds.tech.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sds.tech.ServerResourceMonitor;
import com.sds.tech.ui.ResourceMonitorUI;

// TODO: Auto-generated Javadoc
/**
 * The Class CpuUsageCollector.
 */
public class CpuUsageCollector implements Runnable {

	/** The Constant CPU_USAGE_COMMAND. */
	private static final String CPU_USAGE_COMMAND = "vmstat 5 10000";

	/** The Constant RESOURCE_TYPE. */
	private static final String RESOURCE_TYPE = "cpu";

	/** The srm. */
	private ServerResourceMonitor srm;

	/** The server name. */
	private String serverName;

	/** The os type. */
	private String osType;

	/** The session. */
	private Session session;

	/** The seq. */
	private int seq;

	/**
	 * Instantiates a new cpu usage collector.
	 */
	public CpuUsageCollector() {

	}

	/**
	 * Instantiates a new cpu usage collector.
	 *
	 * @param serverConnector
	 *            the server connector
	 */
	public CpuUsageCollector(ServerConnector serverConnector) {
		this.srm = serverConnector.getSrm();
		this.serverName = serverConnector.getServerName();
		this.osType = serverConnector.getOsType();
		this.session = serverConnector.getSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ResourceMonitorUI mainUI = srm.getMainUI();
		StringBuffer message = new StringBuffer();

		message.append(serverName).append("'s CPU usage monitoring start.");
		mainUI.displayMessage(message.toString());

		seq = 1;

		try {
			executeCommand();
		} catch (IOException e) {
			e.printStackTrace();
		}

		message.delete(0, message.length());
		message.append(serverName).append("'s CPU usage monitoring stop.");
		mainUI.displayMessage(message.toString());
	}

	/**
	 * Execute command.
	 * 
	 * @throws IOException
	 */
	public void executeCommand() throws IOException {
		Channel channel = null;
		String buffer = null;
		BufferedReader br = null;

		try {
			channel = ConnectionUtil.getChannel(session, CPU_USAGE_COMMAND);

			br = new BufferedReader(new InputStreamReader(
					channel.getInputStream()));

			channel.connect();

			while (true) {
				while ((buffer = br.readLine()) != null) {
					buffer = buffer.trim();
					char firstChar = buffer.charAt(0);

					if (firstChar < '0' || firstChar > '9') {
						continue;
					}

					insertData(buffer);
				}

				if (!srm.isStarted() || channel.isClosed()) {
					break;
				}

				Thread.sleep(1000);
			}

			channel.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	/**
	 * Insert data.
	 *
	 * @param result
	 *            the result
	 */
	public void insertData(String result) {
		int percent = getCpuUsage(result);
		DataAccessManager dataAccessManager = srm.getDataAccessManager();

		dataAccessManager.insertData(seq++, serverName, RESOURCE_TYPE, percent);
	}

	/**
	 * Gets the cpu usage.
	 *
	 * @param result
	 *            the result
	 * @return the cpu usage
	 */
	private int getCpuUsage(String result) {
		int percent = 100;
		StringTokenizer tokenizer = new StringTokenizer(result, " ");
		String[] token = new String[tokenizer.countTokens()];
		int index = 0;

		while (tokenizer.hasMoreTokens()) {
			token[index++] = tokenizer.nextToken();
		}

		if (ServerConnector.OS_AIX.equals(osType)) {
			percent -= Integer.parseInt(token[token.length - 2].trim());
		} else if (ServerConnector.OS_HPUX.equals(osType)) {
			percent -= Integer.parseInt(token[token.length - 1].trim());
		} else if (ServerConnector.OS_LINUX.equals(osType)) {
			percent -= Integer.parseInt(token[token.length - 3].trim());
		} else if (ServerConnector.OS_SOLARIS.equals(osType)) {
			percent -= Integer.parseInt(token[token.length - 1].trim());
		}

		return percent;
	}
}
