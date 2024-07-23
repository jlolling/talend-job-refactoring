package de.jlo.talend.tweak.model.conflict;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jlo.talend.tweak.TalendTweakTool;

public class PanelSolveConflicts extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(PanelSolveConflicts.class);
	private TaskSolveConflicts solver = new TaskSolveConflicts();
	private JButton btnFileChooser = null;
	private JButton btnStart = null;
	private JTextField tfFile = null;
	private JComboBox<String> cbGitSide = null;

	public PanelSolveConflicts() {
		initialize();
	}

	private void setDir() {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String folder = TalendTweakTool.getProperty("solveConflicts.folder", System.getProperty("user.home"));
		if (folder != null) {
			File ff = new File(folder);
			File p = ff.getParentFile();
			fileChooser.setCurrentDirectory(p);
			fileChooser.setSelectedFile(ff);
			fileChooser.ensureFileIsVisible(ff);
			fileChooser.setDialogTitle("Open Folder To Check For Conflicts");
		}
		int answer = fileChooser.showOpenDialog(this);
		if (answer == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			tfFile.setText(f.getAbsolutePath());
			solver.setEntryPath(f.getAbsolutePath());
			TalendTweakTool.setProperty("solveConflicts.folder", f.getAbsolutePath());
		}
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		{
			JLabel label = new JLabel("Entry Directory: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 1;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(label, gbc);
		}
		{
			tfFile = new JTextField();
			tfFile.setToolTipText("Starting point for conflict resolving");
			tfFile.setEditable(false);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 1;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfFile, gbc);
		}
		{
			btnFileChooser = new JButton("...");
			btnFileChooser.setToolTipText("Choose dir");
			btnFileChooser.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setDir();
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 1;
			gbc.gridx = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(btnFileChooser, gbc);
		}
		{
			cbGitSide = new JComboBox<>();
			cbGitSide.addItem("mine");
			cbGitSide.addItem("theirs");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 2;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.anchor = GridBagConstraints.WEST;
			this.add(cbGitSide, gbc);
			String side = (String) cbGitSide.getSelectedItem();
			solver.setGitSide(side);
			cbGitSide.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						String side = (String) cbGitSide.getSelectedItem();
						solver.setGitSide(side);
					}
				}
			});
		}
		{
			btnStart = new JButton("Start Solving Conflicts");
			btnStart.setToolTipText("Run solving conflicts");
			btnStart.setEnabled(true);
			btnStart.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					run(tfFile.getText());
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 2;
			gbc.gridx = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(btnStart, gbc);
		}
		
	}
	
	private void run(final String startDir) {
		Thread loaderThread = new Thread(new Runnable() {

			@Override
			public void run() {
				cbGitSide.setEnabled(false);
				btnFileChooser.setEnabled(false);
				btnStart.setEnabled(false);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						PanelSolveConflicts.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					}
				});
				try {
					solver.execute();
				} catch (Exception e) {
					LOG.error("Solve conflicts failed: " + e.getMessage(), e);
				} finally {
					cbGitSide.setEnabled(true);
					btnFileChooser.setEnabled(true);
					btnStart.setEnabled(true);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							PanelSolveConflicts.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					});
				}
			}
			
		});
		loaderThread.start();
	}

	
}
