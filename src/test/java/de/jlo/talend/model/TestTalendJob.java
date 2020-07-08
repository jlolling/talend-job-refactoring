package de.jlo.talend.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jlo.talend.tweak.model.Talendjob;

public class TestTalendJob {
	
	@Test
	public void testCompare1() {
		Talendjob job1 = new Talendjob();
		job1.setId("12345");
		job1.setJobName("testjob");
		job1.setVersion("1.5");
		Talendjob job2 = new Talendjob();
		job2.setId("123456");
		job2.setJobName("testjob");
		job2.setVersion("1.3");
		System.out.println("compare=" + job1.compareTo(job2));
		assertTrue("compare failed", job1.compareTo(job2) < 0);
	}

	@Test
	public void testCompare2() {
		Talendjob job1 = new Talendjob();
		job1.setId("12345");
		job1.setJobName("testjob");
		job1.setVersion("1.3");
		Talendjob job2 = new Talendjob();
		job2.setId("123456");
		job2.setJobName("testjob");
		job2.setVersion("1.3");
		System.out.println("compare=" + job1.compareTo(job2));
		assertTrue("compare failed", job1.compareTo(job2) == 0);
	}

	@Test
	public void testCompare3() {
		Talendjob job1 = new Talendjob();
		job1.setId("12345");
		job1.setJobName("testjob");
		job1.setVersion("2.0");
		Talendjob job2 = new Talendjob();
		job2.setId("123456");
		job2.setJobName("testjob");
		job2.setVersion("3.1");
		System.out.println("compare=" + job1.compareTo(job2));
		assertTrue("compare failed", job1.compareTo(job2) > 0);
	}

}
