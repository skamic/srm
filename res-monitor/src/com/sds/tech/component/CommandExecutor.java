package com.sds.tech.component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandExecutor.
 */
public class CommandExecutor {

	/**
	 * Execute.
	 *
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the result of command execution
	 */
	public static String execute(Session session, final String command) {
		Channel channel = null;
		BufferedReader br = null;
		String buffer = null;
		String result = null;

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			br = new BufferedReader(new InputStreamReader(
					channel.getInputStream()));

			channel.connect();

			while ((buffer = br.readLine()) != null) {
				result = buffer;
			}

			channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}
