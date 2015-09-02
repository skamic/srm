package com.sds.tech.ui.popup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.sds.tech.ui.ResourceMonitorUI;

public class ResultSettingsPopup extends JFrame {
	private static final long serialVersionUID = 8506499428711180729L;

	private ResourceMonitorUI mainUI;

	private JTextField resultName;
	private JTextField resultDirectory;

	public ResultSettingsPopup(ResourceMonitorUI mainUI) {
		setMaximumSize(new Dimension(400, 140));
		setMinimumSize(new Dimension(400, 140));
		this.mainUI = mainUI;
		setName("");
		initUI();
	}

	public void initUI() {
		setType(Type.POPUP);
		setTitle("Result Settings");

		getContentPane().add(createResultSettingPanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * @return
	 */
	private JPanel createResultSettingPanel() {
		JPanel resultSettingPanel = new JPanel();

		JLabel lblResultName = new JLabel("Result Name");
		lblResultName.setAlignmentX(Component.CENTER_ALIGNMENT);
		resultName = new JTextField();
		resultName.setColumns(100);

		JLabel lblResultDirectory = new JLabel("Result Directory");
		resultDirectory = new JTextField();

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		resultSettingPanel.setLayout(new MigLayout("", "[fill][fill][fill]",
				"[fill][fill]"));
		resultSettingPanel.add(lblResultName, "cell 0 0,grow");
		resultSettingPanel.add(resultName, "cell 1 0,grow");
		resultSettingPanel.add(lblResultDirectory, "cell 0 1,grow");
		resultSettingPanel.add(resultDirectory, "cell 1 1,grow");

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();

					resultDirectory.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		resultSettingPanel.add(btnBrowse, "cell 2 1,grow");

		return resultSettingPanel;
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

		JButton btnConfirm = new JButton("확인");
		btnConfirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getParent().getSrm().saveResultSettings(
						getResultName().getText(),
						getResultDirectory().getText());
//				resetForm();
				setVisible(false);
			}
		});

		buttonPanel.add(btnConfirm, "cell 1 0,grow");

		return buttonPanel;
	}

	private void resetForm() {
		this.resultName.setText(null);
		this.resultDirectory.setText(null);
	}

	public ResourceMonitorUI getParent() {
		return mainUI;
	}

	public void setParent(ResourceMonitorUI parent) {
		this.mainUI = parent;
	}

	public JTextField getResultName() {
		return resultName;
	}

	public void setResultName(JTextField resultName) {
		this.resultName = resultName;
	}

	public JTextField getResultDirectory() {
		return resultDirectory;
	}

	public void setResultDirectory(JTextField resultDirectory) {
		this.resultDirectory = resultDirectory;
	}
}
