package de.jlo.talend.tweak.model.job;

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
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jlo.talend.tweak.model.TalendModel;

public class PanelTaskFixTRunJob extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PanelTaskFixTRunJob.class);
	private JButton btnStart = null;
	private TalendModel model = null;
	private JLabel lbModel = null;
	private JCheckBox cbSimulate = null;
	
	public PanelTaskFixTRunJob() {
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
			gbc.anchor = GridBagConstraints.WEST;
			this.add(lbModel, gbc);
		}
		{
			btnStart = new JButton("Start repair");
			btnStart.setEnabled(false);
			btnStart.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					startRepair();
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(btnStart, gbc);
		}
		{
			cbSimulate = new JCheckBox("Read only");
			cbSimulate.setEnabled(false);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(cbSimulate, gbc);
		}

	}
	
	private void startRepair() {
		btnStart.setEnabled(false);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				repair();
			}
		});
		thread.start();
	}
	
	private void repair() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PanelTaskFixTRunJob.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		});
		TaskFixTRunJob task = new TaskFixTRunJob(model);
		try {
			task.setSimulate(cbSimulate.isSelected());
			task.execute();
		} catch (Exception e) {
			LOG.error("Repair failed: " + e.getMessage(), e);
		} finally {
			btnStart.setEnabled(true);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					PanelTaskFixTRunJob.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		cbSimulate.setEnabled(true);
	}
	
}
