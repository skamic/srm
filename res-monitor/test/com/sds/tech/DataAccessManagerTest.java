package com.sds.tech;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.sds.tech.core.DataAccessManager;
import com.sds.tech.core.ServerConnector;
import com.sds.tech.core.ServerManager;

public class DataAccessManagerTest {
	private ServerResourceMonitor srm;

	@Before
	public void initialize() {
		this.srm = new ServerResourceMonitor();
	}

//	@Test
	public void createTableTest() throws Exception {
		DataAccessManager dataLoggingManager = this.srm.getDataAccessManager();

		dataLoggingManager.createTable();
	}

//	@Test
	public void insertDataTest() throws Exception {
		DataAccessManager dataLoggingManager = this.srm.getDataAccessManager();

		dataLoggingManager.insertData(1, "WEB_1", "cpu", 4);
		dataLoggingManager.insertData(1, "WEB_2", "cpu", 5);
		dataLoggingManager.insertData(1, "WAS_1", "cpu", 12);
		dataLoggingManager.insertData(1, "WAS_2", "cpu", 15);
		dataLoggingManager.insertData(1, "DB_1", "cpu", 7);
	}

	@Test
	public void selectDataTest() {
		ServerManager serverManager = this.srm.getServerManager();
		DataAccessManager dataAccessManager = this.srm.getDataAccessManager();
		Map<String, ServerConnector> serverMap = serverManager
				.getServerMap();

		serverMap.put("123.123.123.123:22", new ServerConnector("WEB_1",
				"123.123.123.123", "22", "", ""));
		serverMap.put("123.123.123.124:22", new ServerConnector("WEB_2",
				"123.123.123.124", "22", "", ""));
		serverMap.put("123.123.123.125:22", new ServerConnector("WAS_1",
				"123.123.123.125", "22", "", ""));
		serverMap.put("123.123.123.126:22", new ServerConnector("WAS_2",
				"123.123.123.126", "22", "", ""));
		serverMap.put("123.123.123.127:22", new ServerConnector("DB_1",
				"123.123.123.127", "22", "", ""));

//		dataAccessManager.selectData("cpu", 1);
	}

	public ServerResourceMonitor getSrm() {
		return srm;
	}

	public void setSrm(ServerResourceMonitor srm) {
		this.srm = srm;
	}
}
