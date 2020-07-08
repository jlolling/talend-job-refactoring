package de.jlo.talend.tweak.deploy;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;

public class DeployDIJobNexus3 extends BatchjobDeployer {

	private static final Logger LOG = Logger.getLogger(DeployDIJobNexus3.class);
	protected String nexusRepository = "job-releases";
	protected String restPath = "/service/rest/v1/components";

	@Override
	public String getNexusVersion() {
		return NEXUS_3;
	}
	
	@Override
	public String getNexusRepository() {
		return nexusRepository;
	}

	private String buildDIJobPom() {
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
		pom.append("<type>zip</type>\n");
		pom.append("</project>");
		return pom.toString();
	}
	
	@Override
	public void deployJobToNexus() throws Exception {
		checkJobFile();
		LOG.info("Deploy job artifact: " + artifactId + " version: " + version + " to " + getNexusVersion() + " repository: " + getNexusRepository());
		if (httpClient == null) {
			throw new IllegalStateException("Http client not connected");
		}
		HttpPost post = new HttpPost(nexusUrl + restPath + "?repository=" + nexusRepository);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("maven2.generate-pom", "false", ContentType.DEFAULT_TEXT);
		builder.addBinaryBody("maven2.asset1", buildDIJobPom().getBytes("UTF-8"), ContentType.DEFAULT_BINARY, "pom.xml");
		builder.addTextBody("maven2.asset1.extension", "pom", ContentType.DEFAULT_TEXT);
		InputStream inputStream = new FileInputStream(getJobFile());
		builder.addBinaryBody("maven2.asset2", inputStream, ContentType.create("application/java-archive"), getJobFile().getName());
		builder.addTextBody("maven2.asset2.extension", "zip", ContentType.DEFAULT_TEXT);
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		String response = httpClient.execute(post, false);
		if (httpClient.getStatusCode() >= 200 || httpClient.getStatusCode() <= 204) {
			System.out.println(response);
			if (deleteLocalArtifactFile) {
				deleteLocalFile();
			}
		} else if (httpClient.getStatusCode() > 204) {
			System.err.println(response);
		}
	}

}