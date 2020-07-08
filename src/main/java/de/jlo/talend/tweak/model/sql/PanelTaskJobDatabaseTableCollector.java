package de.jlo.talend.tweak.model.sql;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jlo.talend.tweak.model.TalendModel;

public class PanelTaskJobDatabaseTableCollector extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PanelTaskJobDatabaseTableCollector.class);
	private JButton btnStart = null;
	private JTextField tfJobPattern = null;
	private JCheckBox cbOnlyInLatestVersion = null;
	private TalendModel model = null;
	private JLabel lbModel = null;
	
	public PanelTaskJobDatabaseTableCollector() {
		initialize();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		{
			JLabel label = new JLabel("Talend Project Root Folder: ");
			label.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(label, gbc);
		}
		{
			lbModel = new JLabel();
			lbModel.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(lbModel, gbc);
		}
		{
			JLabel label = new JLabel("Job Name Pattern");
			label.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(label, gbc);
		}
		{
			tfJobPattern = new JTextField();
			tfJobPattern.setToolTipText("Job Name");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(tfJobPattern, gbc);
		}
		{
			cbOnlyInLatestVersion = new JCheckBox("Only latest job version");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(cbOnlyInLatestVersion, gbc);
		}
		{
			btnStart = new JButton("Start Search Tables");
			btnStart.setEnabled(false);
			btnStart.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					start();
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 8;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(btnStart, gbc);
		}
	}
	
	private void start() {
		btnStart.setEnabled(false);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				doExecute();
			}
		});
		thread.start();
	}
	
	private void doExecute() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PanelTaskJobDatabaseTableCollector.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		});
		TaskJobDatabaseTableCollector task = new TaskJobDatabaseTableCollector(model);
		try {
			task.search(tfJobPattern.getText(), cbOnlyInLatestVersion.isSelected());
			LOG.info(task.getSummary());
		} catch (Exception e) {
			LOG.error("Search tables failed: " + e.getMessage(), e);
		} finally {
			btnStart.setEnabled(true);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					PanelTaskJobDatabaseTableCollector.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
	}

	public TalendModel getModel() {
		return model;
	}

	public void setModel(TalendModel model) {
		this.model = model;
		lbModel.setText(model.getProjectRootDir());
		btnStart.setEnabled(true);
	}
	
}
