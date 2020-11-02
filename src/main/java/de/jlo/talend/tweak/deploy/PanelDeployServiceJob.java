package de.jlo.talend.tweak.deploy;

import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jlo.talend.tweak.TalendTweakTool;

public class PanelDeployServiceJob extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PanelDeployServiceJob.class);
	private TalendTweakTool mainFrame = null;
	private ServiceDeployer deployer = new DeployServiceJobNexus2();
	private JComboBox<String> listNexus = null;
	private JTextField tfFile = null;
	private JTextField tfNexusURL = null;
	private JTextField tfNexusRepo = null;
	private JTextField tfNexusGroupId = null;
	private JTextField tfNexusUser = null;
	private JPasswordField tfNexusPassword = null;
	private JButton btnFileChooser = null;
	private JButton btnDeploy = null;
	private JCheckBox checkboxDeleteLocal = null;

	public PanelDeployServiceJob(TalendTweakTool mainFrame) throws Exception {
		this.mainFrame = mainFrame;
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(new GridBagLayout());
		int y = 0;
		{
			JLabel label = new JLabel("Nexus Version: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(label, gbc);
		}
		{
			listNexus = new JComboBox<>();
			listNexus.addItem(BatchjobDeployer.NEXUS_2);
			listNexus.addItem(BatchjobDeployer.NEXUS_3);
			listNexus.setSelectedItem(TalendTweakTool.getProperty(TalendTweakTool.PARAM_NEXUS_VERSION, BatchjobDeployer.NEXUS_2));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridwidth = 2;
			this.add(listNexus);
		}
		y++;
		{
			JLabel label = new JLabel("Nexus URL: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(label, gbc);
		}
		{
			tfNexusURL = new JTextField();
			tfNexusURL.setToolTipText("Path to the Service OSGi bundle");
			tfNexusURL.setText(TalendTweakTool.getProperty(TalendTweakTool.PARAM_NEXUS_URL, deployer.getNexusUrl()));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfNexusURL, gbc);
		}
		y++;
		{
			JLabel label = new JLabel("Nexus Job Repo and groupId: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(label, gbc);
		}
		{
			tfNexusRepo = new JTextField();
			tfNexusRepo.setToolTipText("Nexus repository");
			tfNexusRepo.setText(TalendTweakTool.getProperty(TalendTweakTool.PARAM_NEXUS_REPO_SERVICE, deployer.getNexusRepository()));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfNexusRepo, gbc);
		}
		{
			tfNexusGroupId = new JTextField();
			tfNexusGroupId.setToolTipText("Group-ID");
			tfNexusGroupId.setText(TalendTweakTool.getProperty(TalendTweakTool.PARAM_GROUP_ID, deployer.getGroupId()));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfNexusGroupId, gbc);
		}
		y++;
		{
			JLabel label = new JLabel("Nexus User and password: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(label, gbc);
		}
		{
			tfNexusUser = new JTextField();
			tfNexusUser.setToolTipText("User");
			tfNexusUser.setText(TalendTweakTool.getProperty(TalendTweakTool.PARAM_NEXUS_USER, deployer.getNexusUser()));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfNexusUser, gbc);
		}
		{
			tfNexusPassword = new JPasswordField();
			tfNexusPassword.setToolTipText("Password");
			tfNexusPassword.setText(TalendTweakTool.getProperty(TalendTweakTool.PARAM_NEXUS_PW, deployer.getNexusPasswd()));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfNexusPassword, gbc);
		}
		y++;
		{
			JLabel label = new JLabel("Choose exported Service OSGi bundle: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(label, gbc);
		}
		{
			tfFile = new JTextField();
			tfFile.setToolTipText("Path to the Talend Service OSGi bundle jar file");
			tfFile.setEditable(false);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(tfFile, gbc);
		}
		{
			btnFileChooser = new JButton("...");
			btnFileChooser.setToolTipText("Choose exported OSGI bundle");
			btnFileChooser.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectFile();
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 3;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(btnFileChooser, gbc);
		}
		y++;
		{
			btnDeploy = new JButton("Deploy Service");
        	btnDeploy.setEnabled(false);
			btnDeploy.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					PanelDeployServiceJob.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					btnDeploy.setEnabled(false);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								deploy();
							} finally {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										PanelDeployServiceJob.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										btnDeploy.setEnabled(true);
									}
								});
							}
						}

					}).start();
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(btnDeploy, gbc);
		}
		{
			checkboxDeleteLocal = new JCheckBox("Delete local jar file when successufully deployed");
			checkboxDeleteLocal.setSelected("true".equals(TalendTweakTool.getProperty(TalendTweakTool.PARAM_DELETE_LOCAL, "true")));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = y;
			gbc.gridx = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			this.add(checkboxDeleteLocal, gbc);
		}
	}

	private void selectFile() {
		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		FileDialog fd = new FileDialog(mainFrame, "Choose exported Talend Service OSGi bundle (jar file)", FileDialog.LOAD);
		fd.setDirectory(TalendTweakTool.getProperty(TalendTweakTool.PARAM_LAST_DIR, "user.home"));
        fd.setFilenameFilter(new FilenameFilter() {
			
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".jar");
			}
			
		});
        fd.setVisible(true);
        String dir = fd.getDirectory();
        String file = fd.getFile();
        if (file != null) {
        	tfFile.setText(new File(dir, file).getAbsolutePath());
        	btnDeploy.setEnabled(true);
        }
	}
	
	private void deploy() {
		if (BatchjobDeployer.NEXUS_2.equals(listNexus.getSelectedItem())) {
			deployer = new DeployServiceJobNexus2();
		} else if (BatchjobDeployer.NEXUS_3.equals(listNexus.getSelectedItem())) {
			deployer = new DeployServiceJobNexus3();
		} else {
			throw new IllegalStateException("No Nexus version choosen");
		}
		deployer.setNexusUrl(tfNexusURL.getText());
		deployer.setNexusRepository(tfNexusRepo.getText());
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_NEXUS_URL, deployer.getNexusUrl());
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_NEXUS_REPO_SERVICE, deployer.getNexusRepository());
		deployer.setGroupId(tfNexusGroupId.getText());
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_GROUP_ID, deployer.getGroupId());
		deployer.setNexusUser(tfNexusUser.getText());
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_NEXUS_USER, deployer.getNexusUser());
		deployer.setNexusPasswd(new String(tfNexusPassword.getPassword()));
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_NEXUS_PW, deployer.getNexusPasswd());
		deployer.setJobFile(tfFile.getText());
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_LAST_DIR, deployer.getJobFile().getParentFile().getAbsolutePath());
		deployer.setDeleteLocalArtifactFile(checkboxDeleteLocal.isSelected());
		TalendTweakTool.setProperty(TalendTweakTool.PARAM_DELETE_LOCAL, String.valueOf(checkboxDeleteLocal.isSelected()));
		try {
			LOG.info("Connecting to " + deployer.getNexusVersion() + "...");
			deployer.connect();
			LOG.info("Deploying bundle job: " + tfFile.getText() + ". Artifact-ID: " + deployer.getArtifactId() + " Version: " + deployer.getVersion());
			deployer.deployBundleToNexus();
			LOG.info("Deploying feature job: " + tfFile.getText() + ". Artifact-ID: " + deployer.getArtifactId() + " Version: " + deployer.getVersion());
			deployer.deployFeatureToNexus();
			LOG.info("Service Job: " + tfFile.getText() + " successfully deployed: Artifact-ID: " + deployer.getArtifactId() + " Version: " + deployer.getVersion());
			deployer.close();
		} catch (Exception e) {
			LOG.error("Deploy Service Job: " + tfFile.getText() + " failed: " + e.getMessage(), e);
		}
	}

}
