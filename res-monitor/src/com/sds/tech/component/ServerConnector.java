package com.sds.tech.component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.sds.tech.ServerResourceMonitor;

public class ServerConnector {
	private String serverId;
	private String serverName;
	private String serverIP;
	private int serverPort;
	private String userId;
	private String userPw;
	private String osType;

	private ServerResourceMonitor srm;
	private JSch sch;
	private Session session;
	private CpuUsageCollector cpuUsageCollector;
	private MemoryUsageCollector memoryUsageCollector;

	public static final String OS_AIX = "AIX";
	public static final String OS_HPUX = "HP-UX";
	public static final String OS_LINUX = "Linux";
	public static final String OS_SOLARIS = "SunOS";

	private static final String OS_TYPE_COMMAND = "uname";

	public ServerConnector() {
		sch = new JSch();
	}

	public ServerConnector(String serverName, String serverIP,
			String serverPort, String userId, String userPw) {
		this();

		this.serverName = serverName;
		this.serverIP = serverIP;
		this.serverPort = Integer.parseInt(serverPort);
		this.userId = userId;
		this.userPw = userPw;

		this.serverId = (new StringBuffer()).append(serverIP).append(":")
				.append(serverPort).toString();
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPw() {
		return userPw;
	}

	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return "ServerConnector [serverName=" + serverName + ", serverIP="
				+ serverIP + ", serverPort=" + serverPort + ", userId="
				+ userId + ", password=" + userPw + ", osType=" + osType + "]";
	}

	public String toCsvFormatString() {
		StringBuffer sb = new StringBuffer();

		sb.append(serverName);
		sb.append(",").append(serverIP);
		sb.append(",").append(serverPort);
		sb.append(",").append(userId);
		sb.append(",").append(userPw);
		sb.append("\n");

		return sb.toString();
	}

	public ServerResourceMonitor getSrm() {
		return this.srm;
	}

	public void setSrm(ServerResourceMonitor srm) {
		this.srm = srm;
	}

	public JSch getSch() {
		return sch;
	}

	public void setSch(JSch sch) {
		this.sch = sch;
	}

	public void startMonitoring() {
		StringBuffer message = new StringBuffer();

		try {
			session = sch.getSession(userId, serverIP, serverPort);
			session.setUserInfo(new UserInfo() {
				@Override
				public void showMessage(String arg0) {

				}

				@Override
				public boolean promptYesNo(String arg0) {
					return true;
				}

				@Override
				public boolean promptPassword(String arg0) {
					return true;
				}

				@Override
				public boolean promptPassphrase(String arg0) {
					return true;
				}

				@Override
				public String getPassword() {
					return userPw;
				}

				@Override
				public String getPassphrase() {
					return null;
				}
			});

			session.connect();
			
			message.append(serverIP).append(":").append(serverPort)
					.append(" connected.");
			getSrm().getMainUI().displayMessage(message.toString());

			osType = CommandExecutor.execute(session, OS_TYPE_COMMAND);

			cpuUsageCollector = new CpuUsageCollector(this);
			memoryUsageCollector = new MemoryUsageCollector(this);

			Thread cpuUsageCollectorThread = new Thread(cpuUsageCollector,
					"CPU Usage Collector");
			Thread memoryUsageCollectorThread = new Thread(
					memoryUsageCollector, "Memory Usage Collector");

			cpuUsageCollectorThread.start();
			memoryUsageCollectorThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopMonitoring() {
		session.disconnect();

		StringBuffer message = new StringBuffer();
		message.append(serverIP).append(":").append(serverPort)
				.append(" disconnected.");

		getSrm().getMainUI().displayMessage(message.toString());
	}
}
