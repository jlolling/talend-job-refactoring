package de.jlo.talend.tweak.deploy;

import java.io.File;

public abstract class BatchjobDeployer {
	
	protected String artifactId = null;
	protected String groupId = "com.example";
	protected String version = null;
	protected String nexusUrl = "http://localhost:8081";
	protected HttpClient httpClient = null;
	protected String nexusUser = "admin";
	protected String nexusPasswd = "Talend123";
	protected File jobFile = null;
	protected boolean deleteLocalArtifactFile = true;
	protected static final String NEXUS_2 = "Nexus 2";
	protected static final String NEXUS_3 = "Nexus 3";
	
	public abstract String getNexusVersion();

	public abstract void deployJobToNexus() throws Exception;
	
	public abstract String getNexusRepository();

	public abstract void setNexusRepository(String repo);

	public void setJobFile(String jobJarFilePath) {
		if (jobJarFilePath == null || jobJarFilePath.trim().isEmpty()) {
			throw new IllegalArgumentException("jobJarFilePath cannot be null or empty");
		}
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
		if (groupId == null || groupId.trim().isEmpty()) {
			throw new IllegalArgumentException("groupId cannot be null or empty");
		}
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
	
	public void checkJobFile() throws Exception {
		if (jobFile == null) {
			throw new Exception("Job file not set!");
		} else if (jobFile.exists() == false) {
			throw new Exception("Job file: " + jobFile.getAbsolutePath() + " does not exist.");
		}
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
