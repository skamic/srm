package com.sds.tech.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sds.tech.ServerResourceMonitor;

public class ServerManager {
	private ServerResourceMonitor srm;

	private Map<String, ServerConnector> serverMap;

	public ServerManager(ServerResourceMonitor srm) {
		this.srm = srm;

		serverMap = new HashMap<String, ServerConnector>();
	}

	public ServerResourceMonitor getSrm() {
		return srm;
	}

	public void setSrm(ServerResourceMonitor srm) {
		this.srm = srm;
	}

	public Map<String, ServerConnector> getServerMap() {
		return serverMap;
	}

	public void addServer(ServerConnector server) {
		server.setSrm(this.srm);
		this.serverMap.put(server.getServerId(), server);
	}

	public void removeServer(String serverId) {
		this.serverMap.remove(serverId);
	}

	public void initServerList() {
		this.serverMap.clear();
	}

	public void saveServerList(File serverListFile) {
		BufferedWriter bw = null;

		try {
			int serverCount = this.serverMap.size();

			bw = new BufferedWriter(new FileWriter(serverListFile));

			for (String serverId : this.serverMap.keySet()) {
				bw.write(this.serverMap.get(serverId).toCsvFormatString());
			}

			StringBuffer message = new StringBuffer();
			message.append(serverCount).append(" Servers have been saved.");
			getSrm().getMainUI().displayMessage(message.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startMonitoring() {
		for (String serverId : this.serverMap.keySet()) {
			ServerConnector serverConnector = this.serverMap.get(serverId);

			serverConnector.startMonitoring();
		}
	}

	public void stopMonitoring() {
		for (String serverId : this.serverMap.keySet()) {
			ServerConnector serverConnector = this.serverMap.get(serverId);

			serverConnector.stopMonitoring();
		}
	}
}
