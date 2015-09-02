package com.sds.tech.core;

import com.jcraft.jsch.Session;
import com.sds.tech.ServerResourceMonitor;
import com.sds.tech.util.ConnectionUtil;

public class MemoryUsageCollector implements Runnable {
	private static final String LINUX_MEM_TOTAL_COMMAND = "free | grep ^Mem | gawk '{print $2}'";
	private static final String LINUX_MEM_FREE_COMMAND = "free | grep ^-/+ | gawk '{print $4}'";

	private static final String AIX_MEM_TOTAL_COMMAND = "svmon -G | grep ^memory | awk '{print $2}'";
	private static final String AIX_MEM_FREE_COMMAND = "svmon -G | grep ^memory | awk '{print $4}'";

	private static final String SOLARIS_MEM_TOTAL_COMMAND = "prtconf | grep Memory | head -1 | awk 'BEGIN {FS=\" \"} {print $3}'";
	private static final String SOLARIS_MEM_FREE_COMMAND = "sar -r 1 1 | tail -1 | awk 'BEGIN {FS=\" \"} {print $2}'";

	private static final String HPUX_MEM_TOTAL_COMMAND = "swapinfo -tam | grep total | awk '{print $2}'";
	private static final String HPUX_MEM_FREE_COMMAND = "swapinfo -tam | grep total | awk '{print $4}'";

	private static final String RESOURCE_TYPE = "memory";

	private ServerResourceMonitor srm;
	private String serverName;
	private String osType;
	private Session session;

	private int seq;

	public MemoryUsageCollector() {

	}

	public MemoryUsageCollector(ServerConnector serverConnector) {
		this.srm = serverConnector.getSrm();
		this.serverName = serverConnector.getServerName();
		this.osType = serverConnector.getOsType();
		this.session = serverConnector.getSession();
	}

	public ServerResourceMonitor getSrm() {
		return srm;
	}

	public void setServerConnector(ServerResourceMonitor srm) {
		this.srm = srm;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public void run() {
		String memTotalCommand = null;
		String memFreeCommand = null;
		long memTotal = 0;
		float memFreeFactor = 1;
		StringBuffer message = new StringBuffer();

		message.append(serverName).append("'s Memory usage monitoring start.");
		getSrm().getMainUI().displayMessage(message.toString());

		seq = 1;

		if (ServerConnector.OS_AIX.equals(osType)) {
			memTotalCommand = AIX_MEM_TOTAL_COMMAND;
			memFreeCommand = AIX_MEM_FREE_COMMAND;
		} else if (ServerConnector.OS_HPUX.equals(osType)) {
			memTotalCommand = HPUX_MEM_TOTAL_COMMAND;
			memFreeCommand = HPUX_MEM_FREE_COMMAND;
		} else if (ServerConnector.OS_LINUX.equals(osType)) {
			memTotalCommand = LINUX_MEM_TOTAL_COMMAND;
			memFreeCommand = LINUX_MEM_FREE_COMMAND;
		} else if (ServerConnector.OS_SOLARIS.equals(osType)) {
			memTotalCommand = SOLARIS_MEM_TOTAL_COMMAND;
			memFreeCommand = SOLARIS_MEM_FREE_COMMAND;
			memFreeFactor = 8 / 1024;
		}

		memTotal = Long.parseLong(ConnectionUtil.execute(session,
				memTotalCommand));
		checkCurrentMemoryUsage(memFreeCommand, memTotal, memFreeFactor);

		message.delete(0, message.length()).append(serverName)
				.append("'s Memory usage monitoring stop.");
		getSrm().getMainUI().displayMessage(message.toString());
	}

	private void checkCurrentMemoryUsage(final String MEM_FREE_COMMAND,
			final long memTotal, final float memFreeFactor) {
		long memFree = 0;
		double percent = 0;
		long startTime, elapsedTime;

		while (srm.isStarted()) {
			startTime = System.currentTimeMillis();

			memFree = Long.parseLong(ConnectionUtil.execute(session,
					MEM_FREE_COMMAND));
			percent = Math
					.round(((memTotal - memFree * memFreeFactor) / memTotal) * 100);

			insertData((int) percent);

			elapsedTime = System.currentTimeMillis() - startTime;

			try {
				Thread.sleep(5000 - elapsedTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertData(int percent) {
		DataAccessManager dataAccessManager = srm.getDataAccessManager();

		dataAccessManager.insertData(seq++, serverName, RESOURCE_TYPE, percent);
	}
}
