package com.sds.tech;

import org.junit.Before;
import org.junit.Test;

import com.sds.tech.core.CpuUsageCollector;
import com.sds.tech.core.ServerConnector;

public class ResourceCollectorTest {
	private ServerResourceMonitor srm;

	@Before
	public void initialize() {
		this.srm = new ServerResourceMonitor();
	}

	@Test
	public void executeCommandTest() {
		ServerConnector serverConnector = new ServerConnector();

		serverConnector.setSrm(srm);
		serverConnector.setServerName("test");
		serverConnector.setServerIP("182.193.2.50");
		serverConnector.setServerPort(22);
		serverConnector.setUserId("webuser");
		serverConnector.setUserPw("csprod@015");
		serverConnector.setOsType("Linux");

		serverConnector.startMonitoring();
		
//		CpuUsageCollector cpuUsageCollector = new CpuUsageCollector(
//				serverConnector);
//		Thread thread = new Thread(cpuUsageCollector, "Test");
//
//		thread.run();
	}
}
