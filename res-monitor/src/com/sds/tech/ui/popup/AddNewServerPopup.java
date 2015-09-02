package com.sds.tech.ui.popup;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.sds.tech.component.ServerConnector;
import com.sds.tech.ui.ResourceMonitorUI;

public class AddNewServerPopup extends JFrame {
	private static final long serialVersionUID = 8506499428711180729L;

	private ResourceMonitorUI parent;

	private JTextField serverName;
	private JTextField serverIP;
	private JTextField serverPort;
	private JTextField userId;
	private JTextField userPw;

	public AddNewServerPopup(ResourceMonitorUI parent) {
		setMaximumSize(new Dimension(370, 260));
		setMinimumSize(new Dimension(370, 260));
		setName("");
		getContentPane().setName("");
		this.parent = parent;
		initUI();
	}

	public void initUI() {
		setType(Type.POPUP);
		setTitle("Add New Server");

		getContentPane().add(createServerInformationPanel(),
				BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * @return
	 */
	private JPanel createServerInformationPanel() {
		JPanel serverInformationPanel = new JPanel();
		serverInformationPanel.setLayout(new MigLayout("", "[fill][fill]",
				"[fill][fill][13.00][fill][fill][fill][fill]"));

		JLabel lblServerName = new JLabel("Server Name");
		serverInformationPanel.add(lblServerName, "cell 0 0,grow");

		serverName = new JTextField();
		serverInformationPanel.add(serverName, "cell 0 1,grow");

		JLabel lblIP = new JLabel("IP");
		serverInformationPanel.add(lblIP, "cell 0 3,grow");

		JLabel lblPort = new JLabel("Port");
		serverInformationPanel.add(lblPort, "cell 1 3,grow");

		serverIP = new JTextField();
		serverIP.setColumns(15);
		serverInformationPanel.add(serverIP, "cell 0 4,grow");

		serverPort = new JTextField();
		serverPort.setColumns(5);
		serverPort.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char key = e.getKeyChar();

				if (!(Character.isDigit(key) || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_DELETE)) {
					e.consume();
				}

				int currentLen = ((JTextField) e.getSource()).getText()
						.length();

				if (currentLen >= 5) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		serverInformationPanel.add(serverPort, "cell 1 4,grow");

		JLabel lblId = new JLabel("ID");
		serverInformationPanel.add(lblId, "cell 0 5,grow");

		userId = new JTextField();
		userId.setColumns(15);
		serverInformationPanel.add(userId, "cell 0 6,grow");

		JLabel lblPassword = new JLabel("Password");
		serverInformationPanel.add(lblPassword, "cell 1 5,grow");

		userPw = new JPasswordField();
		userPw.setColumns(15);
		serverInformationPanel.add(userPw, "cell 1 6,grow");
		return serverInformationPanel;
	}

	/**
	 * @return
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		buttonPanel.setLayout(new MigLayout("", "[fill][fill]", "[fill]"));

		JButton btnCancel = new JButton("취소");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resetForm();
				setVisible(false);
			}
		});
		buttonPanel.add(btnCancel, "cell 0 0,grow");

		JButton btnAddServer = new JButton("추가");
		btnAddServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ServerConnector newServer = new ServerConnector(
						getServerName(), getServerIP(), getServerPort(), getUserId(),
						getUserPw());

				getParent().getSrm().addServer(newServer);
				resetForm();
				setVisible(false);
			}
		});
		buttonPanel.add(btnAddServer, "cell 1 0,grow");

		return buttonPanel;
	}

	private void resetForm() {
		this.serverName.setText(null);
		this.serverIP.setText(null);
		this.serverPort.setText(null);
		this.userId.setText(null);
		this.userPw.setText(null);
	}

	public String getServerName() {
		return serverName.getText();
	}

	public void setServerName(JTextField serverName) {
		this.serverName = serverName;
	}

	public String getServerIP() {
		return serverIP.getText();
	}

	public void setServerIP(JTextField serverIP) {
		this.serverIP = serverIP;
	}

	public String getServerPort() {
		return serverPort.getText();
	}

	public void setServerPort(JTextField serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserId() {
		return userId.getText();
	}

	public void setUserId(JTextField userId) {
		this.userId = userId;
	}

	public String getUserPw() {
		return userPw.getText();
	}

	public void setUserPw(JTextField password) {
		this.userPw = password;
	}

	public ResourceMonitorUI getParent() {
		return parent;
	}

	public void setParent(ResourceMonitorUI parent) {
		this.parent = parent;
	}
}
