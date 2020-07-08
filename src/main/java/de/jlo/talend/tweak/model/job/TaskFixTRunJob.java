package de.jlo.talend.tweak.model.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import de.jlo.talend.tweak.model.AbstractTask;
import de.jlo.talend.tweak.model.TalendModel;
import de.jlo.talend.tweak.model.Talendjob;

public class TaskFixTRunJob extends AbstractTask {

	private static Logger LOG = Logger.getLogger(TaskFixTRunJob.class);
	private String projectRootPath = null;
	private int countRepairedJobs = 0;
	private int countComponents = 0;
	private int countAffectedComponents = 0;
	private int countFixedComponents = 0;
	private int countMissingJobs = 0;
	private List<Talendjob> listFixedTalendJobs = new ArrayList<Talendjob>();
	private List<Talendjob> listTalendJobsRefMissingJobs = new ArrayList<Talendjob>();
	private String outputDir = null;
	private boolean simulate = false; 
	
	public TaskFixTRunJob(TalendModel model) {
		super(model);
		projectRootPath = model.getProjectRootDir();
	}
	
	@Override
	public void execute() throws Exception {
		if (outputDir == null || outputDir.trim().isEmpty()) {
			outputDir = projectRootPath;
		}
		List<Talendjob> list = getModel().getAllJobs();
		for (Talendjob job : list) {
			if (checkAndRepair(job)) {
				listFixedTalendJobs.add(job);
				countRepairedJobs++;
			}
		}
		LOG.info(getSummary());
	}
	
	private void writeFixedJobs(Talendjob job) throws Exception {
		getModel().writeItemFile(job, outputDir);
	}
	
	private boolean checkAndRepair(Talendjob job) throws Exception {
		try {
			job.setItemDoc(getModel().readItem(job));
		} catch (Exception e) {
			LOG.error("Check and repair tRunJob failed: Load item file failed: " + e.getMessage());
			return false;
		}
		List<Node> listTRunJobs = getModel().getComponents(job.getItemDoc(), "tRunJob");
		String message = "Check job: " + job;
		if (listTRunJobs != null && listTRunJobs.isEmpty() == false) {
			message = message + ": " + listTRunJobs.size() + " tRunJob components";
		}
		LOG.info(message);
		boolean jobFixed = false;
		for (Node el : listTRunJobs) {
			countComponents++;
			if (checkAndRepairOneTRunJob(job, el)) {
				if (simulate == false) {
					writeFixedJobs(job);
				}
				jobFixed = true;
			}
		}
		return jobFixed;
	}
	
	private boolean checkAndRepairOneTRunJob(Talendjob job, Node tRunJob) throws Exception {
    	Element e = (Element) tRunJob;
		List<Element> params = e.elements();
		String referencedJobName = null;
		String referencedJobVersion = null;
		String referencedJobId = null;
		String compUniqeName = null;
		Element processId = null;
		for (Element param : params) {
			String name = param.attributeValue("name");
			String value = param.attributeValue("value");
			if (name.equals("PROCESS")) {
				referencedJobName = value;
			} else if (name.equals("PROCESS:PROCESS_TYPE_PROCESS")) {
				referencedJobId = value;
				processId = param;
			} else if (name.equals("PROCESS:PROCESS_TYPE_VERSION")) {
				referencedJobVersion = value;
			} else if (name.equals("UNIQUE_NAME")) {
				compUniqeName = value;
			}
		}
		LOG.debug("Check tRunJob component: " + compUniqeName + " referencing job: " + referencedJobName + ":" + referencedJobVersion);
		if (referencedJobId == null || referencedJobId.trim().isEmpty()) {
			countAffectedComponents++;
			Talendjob referencedJob = getModel().getJobByVersion(referencedJobName, referencedJobVersion);
			if (referencedJob == null) {
				LOG.error("Missing referenced job in job: " + job + " component: " + compUniqeName + " referenced job: " + referencedJobName + ":" + referencedJobVersion);
				if (listTalendJobsRefMissingJobs.contains(job) == false) {
					listTalendJobsRefMissingJobs.add(job);
				}
				countMissingJobs++;
			} else {
				LOG.info("Fix reference in job: " + job + " component: " + compUniqeName + " referenced job: " + referencedJobName + ":" + referencedJobVersion + " current job version: " + referencedJob.getVersion() + " id: " + referencedJob.getId());
				processId.addAttribute("value", referencedJob.getId());
				countFixedComponents++;
				return true;
			}
		}
		return false;
	}

	public int getCountRepairedJobs() {
		return countRepairedJobs;
	}

	public int getCountComponents() {
		return countComponents;
	}

	public int getCountAffectedComponents() {
		return countAffectedComponents;
	}

	public int getCountFixedComponents() {
		return countFixedComponents;
	}

	public int getCountMissingJobs() {
		return countMissingJobs;
	}
	
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("Checked project: " + projectRootPath + "\n");
		sb.append("* Count repaired jobs: " + countRepairedJobs + "\n");
		sb.append("* Count affected components: " + countAffectedComponents + "\n");
		sb.append("* Count components with missing references: " + countMissingJobs + "\n");
		sb.append("## List jobs sucessfully changed: ");
		if (simulate == false) {
			sb.append("written to output folder: " + getOutputDir() + "\n");
		} else {
			sb.append("\n");
		}
		Collections.sort(listFixedTalendJobs);
		for (Talendjob job : listFixedTalendJobs) {
			sb.append(job);
			sb.append("\n");
		}
		sb.append("\n");
		sb.append("## List jobs with missing referenced jobs: \n");
		Collections.sort(listTalendJobsRefMissingJobs);
		for (Talendjob job : listTalendJobsRefMissingJobs) {
			sb.append(job);
			sb.append("\n");
		}
		return sb.toString();
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public boolean isSimulate() {
		return simulate;
	}

	public void setSimulate(boolean simulate) {
		this.simulate = simulate;
	}
	
}
