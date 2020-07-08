package de.jlo.talend.deploy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.jlo.talend.tweak.deploy.BatchjobDeployer;
import de.jlo.talend.tweak.deploy.DeployDIJobNexus2;
import de.jlo.talend.tweak.deploy.DeployDIJobNexus3;

public class TestDeployDIJob {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetJobZipFile() {
		DeployDIJobNexus2 d = new DeployDIJobNexus2();
		String zipFilePath = "/path/to/my_artifact-1.23.zip";
		d.setJobFile(zipFilePath);
		String expectedArtifactid = "my_artifact";
		String expectedVersion = "1.23.0";
		String actualArtifactId = d.getArtifactId();
		String actualVersion = d.getVersion();
		Assert.assertEquals("ArtifactId does not match", expectedArtifactid, actualArtifactId);
		Assert.assertEquals("Version does not match", expectedVersion, actualVersion);
	}
	
	@Test
	public void testDeloyBatchjobNexus3() throws Exception {
		BatchjobDeployer d = new DeployDIJobNexus3();
		d.setNexusUrl("http://localhost:8081");
		d.setNexusUser("admin");
		d.setNexusPasswd("Talend123");
		String zipFilePath = "/Data/exported_jobs/test_calendar_0.1.zip";
		d.setJobFile(zipFilePath);
		String expectedArtifactid = "test_calendar";
		String expectedVersion = "0.1.0";
		String actualArtifactId = d.getArtifactId();
		String actualVersion = d.getVersion();
		Assert.assertEquals("ArtifactId does not match", expectedArtifactid, actualArtifactId);
		Assert.assertEquals("Version does not match", expectedVersion, actualVersion);
		d.connect();
		d.deployJobToNexus();
		Assert.assertTrue(true);
	}

}
