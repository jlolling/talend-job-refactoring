package de.jlo.talend.tweak.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;

public class TRunJob {
	
	private Map<String, String> contextTransmissions = new HashMap<>();
	private Talendjob referencedJob = null;
	private String uniqueName = null;
	private Node node = null;
	private TalendModel model = null;
	
	public TRunJob(Node node, TalendModel model) {
		if (model == null) {
			throw new IllegalArgumentException("model cannot be null");
		}
		if (node == null) {
			throw new IllegalArgumentException("node cannot be null");
		}
		this.node = node;
		uniqueName = TalendModel.getComponentId((Element) node);
		String referencedJobId = TalendModel.getComponentAttribute((Element) node, "PROCESS:PROCESS_TYPE_PROCESS");
		if (referencedJobId == null) {
			// potential tRunJob bug found
			// try to get the job by name and version
			String jobName = TalendModel.getComponentAttribute((Element) node, "PROCESS");
			String version = TalendModel.getComponentAttribute((Element) node, "PROCESS:PROCESS_TYPE_VERSION");
			referencedJob = model.getJobByVersion(jobName, version);
		} else {
			referencedJobId = referencedJobId.replace("TALEND:", "");
			referencedJob = model.getJobById(referencedJobId);
		}
	}
	
	public String getUniqueId() {
		return uniqueName;
	}
	
	public Talendjob getReferencedTalendjob() {
		return referencedJob;
	}

	public TalendModel getModel() {
		return model;
	}

	public Node getNode() {
		return node;
	}
	
}
