package de.jlo.talend.tweak.model.pw;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import de.jlo.talend.tweak.model.AbstractTask;
import de.jlo.talend.tweak.model.TalendModel;
import de.jlo.talend.tweak.model.Talendjob;

public class TaskFixContextPassword extends AbstractTask {
	
	private static Logger LOG = Logger.getLogger(TaskFixContextPassword.class);
	private boolean simulate = false; 
	private int countRepairedJobs = 0;
	private List<Talendjob> listFixedTalendJobs = new ArrayList<Talendjob>();
	

	public TaskFixContextPassword(TalendModel model) {
		super(model);
	}

	@Override
	public void execute() throws Exception {
		List<Talendjob> list = getModel().getAllJobs();
		for (Talendjob job : list) {
			if (checkAndRepair(job)) {
				listFixedTalendJobs.add(job);
				countRepairedJobs++;
			}
		}
		LOG.info(getSummary());
	}
	
	public boolean checkAndRepair(Talendjob job) throws Exception {
		// find all database components
		
		return false;
	}
	
	public boolean checkAndRepairComponent(Talendjob job, Node dbNode) throws Exception {
		
		
		return false;
	}

	public boolean isSimulate() {
		return simulate;
	}

	public void setSimulate(boolean simulate) {
		this.simulate = simulate;
	}
	
	public String getSummary() {
		return null;
	}

	public int getCountRepairedJobs() {
		return countRepairedJobs;
	}
	
}
