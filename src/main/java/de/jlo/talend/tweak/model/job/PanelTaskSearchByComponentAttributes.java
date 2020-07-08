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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import de.jlo.talend.tweak.model.TalendModel;

public class PanelTaskSearchByComponentAttributes extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PanelTaskSearchByComponentAttributes.class);
	private JButton btnStart = null;
	private JTextField tfJobPattern = null;
	private JTextField tfComponentName = null;
	private JTextField tfAttributeName = null;
	private JTextField tfAttributeValuePattern = null;
	private JCheckBox cbReplaceValue = null;
	private JCheckBox cbShowValue = null;
	private JCheckBox cbOnlyInLatestVersion = null;
	private JTextField tfAttributeValueReplacement = null;
	private TalendModel model = null;
	private JLabel lbModel = null;
	
	public PanelTaskSearchByComponentAttributes() {
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
			JLabel label = new JLabel("Component Type Name");
			label.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(label, gbc);
		}
		{
			tfComponentName = new JTextField();
			tfComponentName.setToolTipText("Component Type Name");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 3;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(tfComponentName, gbc);
		}
		{
			JLabel label = new JLabel("Attribute Value Pattern");
			label.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 4;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(label, gbc);
		}
		{
			tfAttributeValuePattern = new JTextField();
			tfAttributeValuePattern.setToolTipText("Attribute Value Pattern");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 4;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(tfAttributeValuePattern, gbc);
		}
		{
			JLabel label = new JLabel("Attribute Name");
			label.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 5;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(label, gbc);
		}
		{
			tfAttributeName = new JTextField();
			tfAttributeName.setToolTipText("Attribute Name");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 5;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(tfAttributeName, gbc);
			tfAttributeName.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					checkHasContent();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					checkHasContent();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					checkHasContent();
				}
				
				private void checkHasContent() {
					if (cbReplaceValue != null) {
						String c = tfAttributeName.getText();
						if (c != null && c.trim().isEmpty() == false) {
							cbReplaceValue.setEnabled(true);
						} else {
							cbReplaceValue.setEnabled(false);
						}
					}
				}
			
			});
		}
		{
			cbReplaceValue = new JCheckBox("Replace Attribute Value");
			cbReplaceValue.setEnabled(false);
			cbReplaceValue.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (tfAttributeValueReplacement != null) {
						tfAttributeValueReplacement.setEnabled(cbReplaceValue.isSelected());
					}
					if (btnStart != null) {
						if (cbReplaceValue.isSelected()) {
							btnStart.setText("Start Search and Replace");
						} else {
							btnStart.setText("Start Search");
						}
					}
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 6;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(cbReplaceValue, gbc);
		}
		{
			JLabel label = new JLabel("New Attribute Value");
			label.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 7;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(label, gbc);
		}
		{
			tfAttributeValueReplacement = new JTextField();
			tfAttributeValueReplacement.setEnabled(false);
			tfAttributeValueReplacement.setToolTipText("New Attribute Value");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 7;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(tfAttributeValueReplacement, gbc);
		}
		{
			btnStart = new JButton("Start Search");
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
		{
			cbShowValue = new JCheckBox("Show Attribute Value in Result");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 9;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.WEST;
			this.add(cbShowValue, gbc);
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
				PanelTaskSearchByComponentAttributes.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		});
		TaskSearchJobByComponentAttribute task = new TaskSearchJobByComponentAttribute(model);
		try {
			task.setReplaceAttributeValue(cbReplaceValue.isSelected());
			task.setOnlyInLatestVersion(cbOnlyInLatestVersion.isSelected());
			task.setShowAttributeValueInResult(cbShowValue.isSelected());
			task.search(tfJobPattern.getText(), tfComponentName.getText(), tfAttributeName.getText(), tfAttributeValuePattern.getText(), tfAttributeValueReplacement.getText());
			LOG.info(task.getSummary());
		} catch (Exception e) {
			LOG.error("Search failed: " + e.getMessage(), e);
		} finally {
			btnStart.setEnabled(true);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					PanelTaskSearchByComponentAttributes.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
