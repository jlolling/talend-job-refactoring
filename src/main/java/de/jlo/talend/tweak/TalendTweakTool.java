package de.jlo.talend.tweak;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.jlo.talend.tweak.context.passwd.PanelEncryptPassword;
import de.jlo.talend.tweak.deploy.PanelDeployDIJob;
import de.jlo.talend.tweak.deploy.PanelDeployServiceJob;
import de.jlo.talend.tweak.log.LogPanel;
import de.jlo.talend.tweak.model.TalendModel;
import de.jlo.talend.tweak.model.job.PanelTaskFixTRunJob;
import de.jlo.talend.tweak.model.job.PanelTaskSearchByComponentAttributes;
import de.jlo.talend.tweak.model.sql.PanelTaskJobDatabaseTableCollector;

public class TalendTweakTool extends JFrame {

	private static final Logger LOG = Logger.getLogger(TalendTweakTool.class);
	private static final long serialVersionUID = 1L;
	private TalendModel model = null;
	private JTabbedPane tabbedPane = null;
	private JTextField tfProjectPath = null;
	private JButton btnFileChooser = null;
	private JLabel lbNumberJobs = null;
	private PanelTaskFixTRunJob pnTaskFixTRunJob = null;
	private PanelTaskSearchByComponentAttributes pnTaskSearchByComponentAttributes = null;
	private PanelTaskJobDatabaseTableCollector pnTaskJobDatabaseTableCollector = null;
	private PanelDeployDIJob pnDeployDIJob = null;
	private PanelDeployServiceJob pnDeployServiceJob = null;
	private static final String RELATIVE_CONFIG_FILE_DIR = ".talend-tweak_tool";
	private static String userPropertiesFile;
	private static Properties userProperties = new Properties();
	public static final String PARAM_NEXUS_VERSION = "nexus_version";
	public static final String PARAM_NEXUS_URL = "nexus_url";
	public static final String PARAM_NEXUS_USER = "nexus_user";
	public static final String PARAM_NEXUS_PW = "nexus_password";
	public static final String PARAM_NEXUS_REPO_BATCH = "nexus_repo_jobs";
	public static final String PARAM_NEXUS_REPO_SERVICE = "nexus_repo_servives";
	public static final String PARAM_GROUP_ID = "group_id";
	public static final String PARAM_LAST_DIR = "last_job_dir";
	public static final String PARAM_DELETE_LOCAL = "delete_local_file";

	public static void main(String[] args) throws Exception {
		TalendTweakTool tool = new TalendTweakTool();
		tool.loadUserProperties();
		tool.initialize();
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			saveUserProperties();
			System.exit(0);
		}
		super.processWindowEvent(e);
	}

	private void initialize() throws Exception {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.INFO);
		setTitle("Talend Tweak Tool - v" + readVersionNumber());
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Deploy DI Job to Nexus", getPanelDeployDIJob());
		tabbedPane.add("Deploy OSGi bundle to Nexus", getPanelDeployServiceJob());
		tabbedPane.add("Model setup", getConfigPane());
		tabbedPane.add("Search Jobs/Components/Attributes", getPanelTaskSearchByComponentAttributes());
		tabbedPane.add("Search Tables in jobs", getPanelTaskJobDatabaseTableCollector());
		tabbedPane.add("Fix tRunJob", getPanelTaskFixTRunJob());
		tabbedPane.add("Encrypt Password in context properties", new PanelEncryptPassword());
		JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, LogPanel.getInstance());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitpane);
		setVisible(true);
		setPreferredSize(new Dimension(800,800));
		pack();
		WindowHelper.locateWindowAtMiddleOfDefaultScreen(this);
	}
	
	private JPanel getConfigPane() {
		JPanel configPane = new JPanel();
		configPane.setLayout(new GridBagLayout());
		{
			JLabel label = new JLabel("Talend project root dir");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPane.add(label, gbc);
		}
		{
			tfProjectPath = new JTextField();
			tfProjectPath.setToolTipText("Path to the Talend project");
			tfProjectPath.setEditable(false);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			configPane.add(tfProjectPath, gbc);
		}
		{
			btnFileChooser = new JButton("...");
			btnFileChooser.setToolTipText("Choose Talend project root dir");
			btnFileChooser.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectProjectRootDir();
				}
			});
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			configPane.add(btnFileChooser, gbc);
		}
		{
			JLabel label = new JLabel("Number Talend jobs: ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			configPane.add(label, gbc);
		}
		{
			lbNumberJobs = new JLabel("0");
			lbNumberJobs.setHorizontalAlignment(JLabel.LEFT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			configPane.add(lbNumberJobs, gbc);
		}
		return configPane;
	}
	
	private JPanel getPanelTaskFixTRunJob() {
		pnTaskFixTRunJob = new PanelTaskFixTRunJob();
		return pnTaskFixTRunJob;
	}
	
	private JPanel getPanelDeployDIJob() throws Exception {
		pnDeployDIJob = new PanelDeployDIJob(this);
		return pnDeployDIJob;
	}

	private JPanel getPanelDeployServiceJob() throws Exception {
		pnDeployServiceJob = new PanelDeployServiceJob(this);
		return pnDeployServiceJob;
	}

	private JPanel getPanelTaskSearchByComponentAttributes() {
		pnTaskSearchByComponentAttributes = new PanelTaskSearchByComponentAttributes();
		return pnTaskSearchByComponentAttributes;
	}

	private JPanel getPanelTaskJobDatabaseTableCollector() {
		pnTaskJobDatabaseTableCollector = new PanelTaskJobDatabaseTableCollector();
		return pnTaskJobDatabaseTableCollector;
	}
	
	private void selectProjectRootDir() {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog fd = new FileDialog(this, "Choose Talend root project dir (or file talend.project)", FileDialog.LOAD);
        fd.setFilenameFilter(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory() || name.equals("talend.project");
			}
			
		});
        fd.setVisible(true);
        String file = fd.getFile();
        if (file != null) {
            File projectDir = new File(fd.getDirectory(),  file);
            if (projectDir != null) {
            	if (projectDir.isFile()) {
            		projectDir = projectDir.getParentFile();
            	}
    			tfProjectPath.setText(projectDir.getAbsolutePath());
    			initializeModel(projectDir.getAbsolutePath());
            }
        }
	}
	
	private void initializeModel(final String projectRootDir) {
		Thread loaderThread = new Thread(new Runnable() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						TalendTweakTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					}
				});
				final TalendModel _model = new TalendModel();
				try {
					_model.readProject(projectRootDir);
					setModel(_model);
				} catch (Exception e) {
					LOG.error("Load model from: " + projectRootDir + " failed: " + e.getMessage(), e);
				} finally {
					btnFileChooser.setEnabled(true);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							TalendTweakTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					});
				}
			}
			
		});
		loaderThread.start();
	}
	
	private void setModel(final TalendModel model) {
		this.model = model;
		lbNumberJobs.setText(String.valueOf(model.getCountJobs()));
		pnTaskFixTRunJob.setModel(model);
		pnTaskSearchByComponentAttributes.setModel(model);
		pnTaskJobDatabaseTableCollector.setModel(model);
	}

	public TalendModel getModel() {
		return model;
	}
	
	private void setupUserPropertiesFilePath() {
		File configDir = new File(System.getProperty("user.home") + "/" + RELATIVE_CONFIG_FILE_DIR);
		if (configDir.exists() == false) {
			configDir.mkdir();
		}
		userPropertiesFile = configDir.getAbsolutePath() + "/user.properties";
	}
	
	public void loadUserProperties() {
		if (userPropertiesFile == null) {
			setupUserPropertiesFilePath();
		}
		loadUserProperties(userPropertiesFile);
	}
	
    private void saveUserProperties() {
        if (LOG.isDebugEnabled()) {
        	LOG.debug("save user properties in file " + userPropertiesFile);
        }
        try {
            final FileOutputStream inifileOut = new FileOutputStream(userPropertiesFile);
            userProperties.store(inifileOut, "Talend Tweak Tool user-config");
            inifileOut.close();
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
            	LOG.debug("saveUserProp: write in file failed:" + userPropertiesFile);
            }
        }
    }

	private boolean loadUserProperties(String filePath) {
		LOG.info("Attempt loading config from file: " + filePath);
		File cf = new File(filePath);
		if (cf.canRead() == false) {
			LOG.error("Config file " + cf.getAbsolutePath() + " does not exists or cannot be read!");
			return false;
		} else {
			try {
				Reader in = new InputStreamReader(new FileInputStream(cf), "UTF-8");
				try {
					userProperties.clear();
					userProperties.load(in);
				} finally {
					in.close();
				}
			} catch (Exception e) {
				LOG.error("loadConfigProperties failed: Input stream to config file cannot be established: " + e.getMessage(), e);
				return false;
			}
		}
		return true;
	}

	public static String getProperty(String key, String defaultValue) {
		Object o = userProperties.get(key);
		if (o != null) {
			String s = (String) o; 
			if (s.trim().isEmpty()) {
				return defaultValue;
			}
			return s.trim();
		} else {
			return defaultValue;
		}
	}

	public static void setProperty(String key, String value) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("key cannot be null or empty");
		} else { 
			if (value != null) {
				userProperties.put(key, value);
			} else {
				userProperties.remove(key);
			}
		}
	}
	
	public static String readVersionNumber() {
		String groupId = "de.jlo.talend.tweak";
		String artifactId = "jlo-talend-job-refactoring";
		String pomPropertyResource = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
		try {
			InputStream in = TalendTweakTool.class.getResourceAsStream(pomPropertyResource);
			if (in == null) {
				LOG.warn("Resource: " + pomPropertyResource + " not found!");
			} else {
				Properties mavenProps = new Properties();
				mavenProps.load(in);
				in.close();
				return mavenProps.getProperty("version");
			}
		} catch (Exception e) {
			LOG.warn("Load maven properties failed: " + e.getMessage(), e);
		}
		return null;
	}
	
}
