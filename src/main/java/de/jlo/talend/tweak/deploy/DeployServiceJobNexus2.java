package de.jlo.talend.tweak.deploy;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;

public class DeployServiceJobNexus2 extends ServiceDeployer {
	
	private static final Logger LOG = Logger.getLogger(DeployServiceJobNexus2.class);
	protected String nexusRepository = "releases";
	protected String restPath = "/service/local/artifact/maven/content";

	@Override
	public String getNexusVersion() {
		return BatchjobDeployer.NEXUS_2;
	}
	
	@Override
	public String getNexusRepository() {
		return nexusRepository;
	}

	@Override
	public void setNexusRepository(String repo) {
		if (repo == null || repo.trim().isEmpty()) {
			throw new IllegalArgumentException("Repo cannot be null or empty");
		}
		this.nexusRepository = repo;
	}

	private String buildBundlePom() {
		StringBuilder pom = new StringBuilder();
		pom.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
		pom.append("<modelVersion>4.0.0</modelVersion>\n");
		pom.append("<groupId>");
		pom.append(groupId);
		pom.append("</groupId>\n");
		pom.append("<artifactId>");
		pom.append(artifactId);
		pom.append("</artifactId>\n");
		pom.append("<version>");
		pom.append(version);
		pom.append("</version>\n");
		pom.append("<packaging>bundle</packaging>\n");
		pom.append("</project>");
		return pom.toString();
	}
	
	private String buildFeaturePom() {
		StringBuilder pom = new StringBuilder();
		pom.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
		pom.append("<modelVersion>4.0.0</modelVersion>\n");
		pom.append("<groupId>"); 
		pom.append(groupId);
		pom.append("</groupId>\n");
		pom.append("<artifactId>");
		pom.append(artifactId);
		pom.append("-feature</artifactId>\n");
		pom.append("<version>");
		pom.append(version);
		pom.append("</version>\n");
		pom.append("<packaging>pom</packaging>\n");
		pom.append("<type>xml</type>\n");
		pom.append("<classifier>features</classifier>\n");
		pom.append("<dependencies>\n");
		pom.append("<dependency>\n");
		pom.append("<groupId>"); 
		pom.append(groupId);
		pom.append("</groupId>\n");
		pom.append("<artifactId>");
		pom.append(artifactId);
		pom.append("</artifactId>\n");
		pom.append("<version>");
		pom.append(version);
		pom.append("</version>\n");
		pom.append("</dependency>\n");
		pom.append("</dependencies>\n");
		pom.append("</project>");
		return pom.toString();
	}

	private String buildFeatureXML() {
		StringBuilder xml = new StringBuilder();
		xml.append("<features xmlns=\"http://karaf.apache.org/xmlns/features/v1.0.0\" name=\"");
		xml.append(artifactId);
		xml.append("-feature");
		xml.append("\">\n");
		xml.append("<feature name=\"");
		xml.append(artifactId);
		xml.append("-feature\"");
		xml.append(" version=\"");
		xml.append(version);
		xml.append("\">\n"); 
		xml.append("<bundle>");
		xml.append("mvn:");
		xml.append(groupId);
		xml.append("/");
		xml.append(artifactId);
		xml.append("/");
		xml.append(version);
		xml.append("</bundle>\n");
		xml.append("<config name=\"");
		xml.append(artifactId);
		xml.append(".talendcontext.Default\">\n");
		xml.append("</config>\n");
		xml.append("</feature>\n");
		xml.append("</features>");
		return xml.toString();
	}

	@Override
	public void deployBundleToNexus() throws Exception {
		checkJobFile();
		LOG.info("Deploy bundle artifact: " + artifactId + " version: " + version);
		String pom = buildBundlePom();
		LOG.info("pom.xml:\n" + pom);
		HttpPost post = new HttpPost(nexusUrl + restPath);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("r", nexusRepository, ContentType.DEFAULT_TEXT);
		builder.addTextBody("hasPom", "true", ContentType.DEFAULT_TEXT);
		builder.addTextBody("e", "jar", ContentType.DEFAULT_TEXT);
		builder.addBinaryBody("file", pom.getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "pom.xml");
		InputStream inputStream = new FileInputStream(jobFile);
		builder.addBinaryBody("file", inputStream, ContentType.DEFAULT_BINARY, jobFile.getName());
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		String response = httpClient.execute(post, false);
		System.out.println(response);
		if (deleteLocalArtifactFile && (httpClient.getStatusCode() >= 200 || httpClient.getStatusCode() <= 204)) {
			deleteLocalFile();
		}
	}

	@Override
	public void deployFeatureToNexus() throws Exception {
		LOG.info("Deploy feature artifact: " + artifactId + "-feature version: " + version);
		String pom = buildFeaturePom();
		LOG.info("pom.xml:\n" + pom);
		String feature = buildFeatureXML();
		LOG.info("feature.xml:\n" + feature);
		HttpPost post = new HttpPost(nexusUrl + restPath);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("r", nexusRepository, ContentType.DEFAULT_TEXT);
		builder.addTextBody("hasPom", "true", ContentType.DEFAULT_TEXT);
		builder.addTextBody("e", "xml", ContentType.DEFAULT_TEXT);
		builder.addBinaryBody("file", pom.getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "pom.xml");
		builder.addBinaryBody("file", feature.getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "feature.xml");
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		String response = httpClient.execute(post, false);
		System.out.println(response);
	}

}
