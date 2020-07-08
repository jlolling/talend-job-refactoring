package de.jlo.talend.tweak.deploy;

import java.io.File;

public abstract class ServiceDeployer {
	
	protected String artifactId = null;
	protected String groupId = "de.gvl";
	protected String version = null;
	protected String nexusUrl = "http://localhost:8081";
	protected HttpClient httpClient = null;
	protected String nexusUser = "admin";
	protected String nexusPasswd = "Talend123";
	protected File jobFile = null;
	protected boolean deleteLocalArtifactFile = true;

	public abstract void deployBundleToNexus() throws Exception;

	public abstract void deployFeatureToNexus() throws Exception;
	
	public abstract String getNexusRepository();

	public abstract String getNexusVersion();

	public void setJobFile(String jobJarFilePath) {
		this.jobFile = new File(jobJarFilePath);
		String fileName = jobFile.getName();
		int posDot = fileName.lastIndexOf(".");
		String extension = fileName.substring(posDot, fileName.length());
		if (extension == null || extension.trim().isEmpty()) {
			throw new IllegalArgumentException("job file name has not extension. file path: " + jobJarFilePath);
		} else if (extension.toLowerCase().endsWith("jar")) {
			// ignore
		} else if (extension.toLowerCase().endsWith("zip")) {
			// ignore
		} else {
			throw new IllegalArgumentException("job file name has invalid extension: " + extension + ". file path: " + jobJarFilePath);
		}
		version = extractVersion(fileName, extension);
		int pos = fileName.indexOf(version);
		artifactId = fileName.substring(0, pos - 1);
		version = version + ".0";
	}
	
	public void checkJobFile() throws Exception {
		if (jobFile == null) {
			throw new Exception("OSGi bundle file not set!");
		} else if (jobFile.exists() == false) {
			throw new Exception("OSGi bundle file: " + jobFile.getAbsolutePath() + " does not exist.");
		}
	}

	public void checkIfArtifactAlreadyExists() {
		
	}
	
	public void deleteLocalFile() {
		if (this.jobFile != null && this.jobFile.exists()) {
			this.jobFile.delete();
		}
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}
	
	public String extractVersion(String fileName, String extension) {
		return RegexUtil.extractByRegexGroups(fileName, "([0-9]{0,}[\\.]*[0-9]{1,}\\.[0-9]{1,})\\" + extension);
	}
	
	public void connect() throws Exception {
		httpClient = new HttpClient(nexusUrl, nexusUser, nexusPasswd, 10000);
		httpClient.setMaxRetriesInCaseOfErrors(5);
	}
	
	public String getNexusUrl() {
		return nexusUrl;
	}

	public void setNexusUrl(String nexusUrl) {
		this.nexusUrl = nexusUrl;
	}

	public String getNexusUser() {
		return nexusUser;
	}

	public void setNexusUser(String nexusUser) {
		this.nexusUser = nexusUser;
	}

	public String getNexusPasswd() {
		return nexusPasswd;
	}

	public void setNexusPasswd(String nexusPasswd) {
		this.nexusPasswd = nexusPasswd;
	}
	
	public void close() {
		try {
			httpClient.close();
		} catch (Exception e) {}
	}

	public File getJobFile() {
		return jobFile;
	}

	public boolean isDeleteLocalArtifactFile() {
		return deleteLocalArtifactFile;
	}

	public void setDeleteLocalArtifactFile(boolean deleteLocalArtifactFile) {
		this.deleteLocalArtifactFile = deleteLocalArtifactFile;
	}

}
