package de.jlo.talend.tweak.deploy;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;

public class DeployServiceJobNexus3 extends ServiceDeployer {
	
	private static final Logger LOG = Logger.getLogger(DeployServiceJobNexus3.class);
	protected String nexusRepository = "releases";
	protected String restPath = "/service/rest/v1/components";

	@Override
	public String getNexusRepository() {
		return nexusRepository;
	}

	@Override
	public String getNexusVersion() {
		return BatchjobDeployer.NEXUS_3;
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
		LOG.info("Deploy bundle artifact: " + artifactId + " version: " + version + " to " + getNexusRepository());
		String pom = buildBundlePom();
		LOG.info("pom.xml:\n" + pom);
		HttpPost post = new HttpPost(nexusUrl + restPath + "?repository=" + nexusRepository);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("maven2.generate-pom", "false");
		builder.addBinaryBody("maven2.asset1", pom.getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "pom.xml");
		builder.addTextBody("maven2.asset1.extension", "pom", ContentType.DEFAULT_TEXT);
		InputStream inputStream = new FileInputStream(jobFile);
		builder.addBinaryBody("maven2.asset2", inputStream, ContentType.create("application/java-archive"), jobFile.getName());
		builder.addTextBody("maven2.asset2.extension", "jar");
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
		HttpPost post = new HttpPost(nexusUrl + restPath + "?repository=" + nexusRepository);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("maven2.generate-pom", "false");
		builder.addBinaryBody("maven2.asset1", pom.getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "pom.xml");
		builder.addTextBody("maven2.asset1.extension", "pom", ContentType.DEFAULT_TEXT);
		builder.addBinaryBody("maven2.asset2", feature.getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "feature.xml");
		builder.addTextBody("maven2.asset2.extension", "xml");
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		String response = httpClient.execute(post, false);
		System.out.println(response);
	}

}
