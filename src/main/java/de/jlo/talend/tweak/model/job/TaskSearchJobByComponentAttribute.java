package de.jlo.talend.tweak.model.job;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import de.jlo.talend.tweak.model.TalendModel;
import de.jlo.talend.tweak.model.Talendjob;

public class TaskSearchJobByComponentAttribute {

	private static Logger LOG = Logger.getLogger(TaskSearchJobByComponentAttribute.class);
	private TalendModel model = null;
	private List<SearchResult> currentSearchResult = null;
	private boolean replaceAttributeValue = false;
	private boolean onlyInLatestVersion = false;
	private boolean showAttributeValueInResult = false;
	
	public TaskSearchJobByComponentAttribute(TalendModel model) {
		this.model = model;
	}
	
	public static class SearchResult {
		
		private Talendjob job;
		private String componentId;
		private String attribute;
		private String attributeValue;
		private String replacedAttributeValue;
		
		public SearchResult(Talendjob job, String componentId) {
			this.job = job;
			if (componentId != null && componentId.trim().isEmpty() == false) {
				this.componentId = componentId;
			}
		}

		public SearchResult(Talendjob job, String componentId, String attribute, String attributeValue) {
			this.job = job;
			if (componentId != null && componentId.trim().isEmpty() == false) {
				this.componentId = componentId;
			}
			this.attribute = attribute;
			this.attributeValue = attributeValue;
		}

		public SearchResult(Talendjob job, String componentId, String attribute, String attributeValue, String replacedAttributeValue) {
			this.job = job;
			if (componentId != null && componentId.trim().isEmpty() == false) {
				this.componentId = componentId;
			}
			this.attribute = attribute;
			this.attributeValue = attributeValue;
			this.replacedAttributeValue = replacedAttributeValue;
		}

		public Talendjob getJob() {
			return job;
		}

		public String getComponentId() {
			return componentId;
		}

		public String getAttribute() {
			return attribute;
		}

		public String getAttributeValue() {
			return attributeValue;
		}

		public String getReplacedAttributeValue() {
			return replacedAttributeValue;
		}

		public void setReplacedAttributeValue(String replacedAttributeValue) {
			this.replacedAttributeValue = replacedAttributeValue;
		}

	}

	public List<SearchResult> search(String jobNamePattern, String componentName, String attribute, String valuePatternStr, String valueReplacement) throws Exception {
		if (attribute != null && attribute.trim().isEmpty()) {
			attribute = null;
		}
		if (valuePatternStr != null && valuePatternStr.trim().isEmpty()) {
			valuePatternStr = null;
		}
		List<Talendjob> jobs = model.getJobs(jobNamePattern, onlyInLatestVersion);
		Pattern valuePattern = null;
		if (valuePatternStr != null && valuePatternStr.trim().isEmpty() == false) {
			valuePattern = Pattern.compile(valuePatternStr, Pattern.CASE_INSENSITIVE);	
		}
		List<SearchResult> result = new ArrayList<>();
		for (Talendjob job : jobs) {
			job.setItemDoc(model.readItem(job));
			List<SearchResult> sr = findValue(job, componentName, attribute, valuePattern, valueReplacement);
			result.addAll(sr);
			LOG.debug(job.getJobName() + ": found " + sr.size() + " components");
			if (replaceAttributeValue && sr.size() > 0) {
				LOG.info(job.getJobName() + ": write changes");
				writeFixedJobs(job);
			}
		}
		currentSearchResult = result;
		return result;
	}
	
	public List<SearchResult> findValue(Talendjob job, String componentName, String searchAttribute, Pattern valuePattern, String valueReplacement) throws Exception {
		List<SearchResult> result = new ArrayList<SearchResult>();
		List<Node> list = model.getComponents(job.getItemDoc(), componentName);
		if (list.isEmpty() == false) {
			if (valuePattern == null && searchAttribute == null) {
				for (Node cn : list) {
					SearchResult r = new SearchResult(job, getComponentId((Element) cn));
					if (r != null) {
						result.add(r);
					}
				}
			} else {
				for (Node cn : list) {
					SearchResult r = findValue(job, (Element) cn, searchAttribute, valuePattern, valueReplacement);
					if (r != null) {
						result.add(r);
					}
				}
			}
		}
		return result;
	}
	
	private void writeFixedJobs(Talendjob job) throws Exception {
		model.writeItemFile(job, model.getProjectRootDir());
	}
	
	private String getComponentId(Element comp) {
		List<Element> params = comp.elements();
		String id = null;
		for (Element param : params) {
			String name = param.attributeValue("name");
			String value = param.attributeValue("value");
			if ("UNIQUE_NAME".equals(name)) {
				id = value;
				break;
			}
		}
		return id;
	}
	
	public SearchResult findValue(Talendjob job, Element comp, String searchAttribute, Pattern valuePattern, String valueReplacement) throws Exception {
		SearchResult result = null;
		List<Element> params = comp.elements();
		for (Element param : params) {
			String componentId = getComponentId(comp);
			String componentAttribute = param.attributeValue("name");
			if (componentAttribute != null) {
				String value = param.attributeValue("value");
				if (valuePattern != null) {
					// we search for a value and optionally an attribute
					if (value != null && value.isEmpty() == false) {
						Matcher m = valuePattern.matcher(value);
						if (m.find()) {
							if (searchAttribute != null && searchAttribute.trim().isEmpty() == false) {
								if (componentAttribute.equals(searchAttribute)) {
									if (replaceAttributeValue) {
										//  replace only if we have defined an attribute
										param.addAttribute("value", valueReplacement);
										result = new SearchResult(job, componentId, componentAttribute, value, valueReplacement);
									} else {
										result = new SearchResult(job, componentId, componentAttribute, value);
									}
								}
							} else {
								result = new SearchResult(job, componentId, componentAttribute, value);
							}
						}
					}
				} else if (searchAttribute != null && searchAttribute.isEmpty() == false) {
					// we search for an attribute
					if (componentAttribute.equals(searchAttribute)) {
						if (replaceAttributeValue) {
							//  replace only if we have defined an attribute
							param.addAttribute("value", valueReplacement);
							result = new SearchResult(job, componentId, componentAttribute, value, valueReplacement);
						} else {
							result = new SearchResult(job, componentId, componentAttribute, value);
						}
					}
				}
			}
		}
		return result;
	}

	public List<SearchResult> getCurrentSearchResult() {
		return currentSearchResult;
	}
	
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n############# Result ################\n");
		sb.append("Found " + currentSearchResult.size() + " jobs/components:\n");
		if (replaceAttributeValue) {
			sb.append("Replaced all found values with the new value\n");
		}
		for (SearchResult r : currentSearchResult) {
			sb.append(r.getJob().getJobName());
			sb.append(" (");
			sb.append(r.getJob().getVersion());
			sb.append(") ");
			if (r.getComponentId() != null) {
				sb.append("->");
				sb.append(r.getComponentId());
				if (r.getAttribute() != null) {
					sb.append(": ");
					sb.append(r.getAttribute());
					if (replaceAttributeValue && r.getReplacedAttributeValue() != null) {
						sb.append("=");
						sb.append(r.getAttributeValue() != null ? "\"" + r.getAttributeValue() + "\"" : "null");
						sb.append(" replaced by value=\"" + r.getReplacedAttributeValue() + "\"");
					} else if (showAttributeValueInResult) {
						sb.append("=");
						sb.append(r.getAttributeValue() != null ? "\"" + r.getAttributeValue() + "\"" : "null");
					}
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public boolean isReplaceAttributeValue() {
		return replaceAttributeValue;
	}

	public void setReplaceAttributeValue(boolean replaceAttributeValue) {
		this.replaceAttributeValue = replaceAttributeValue;
	}

	public boolean isOnlyInLatestVersion() {
		return onlyInLatestVersion;
	}

	public void setOnlyInLatestVersion(boolean onlyInLatestVersion) {
		this.onlyInLatestVersion = onlyInLatestVersion;
	}

	public boolean isShowAttributeValueInResult() {
		return showAttributeValueInResult;
	}

	public void setShowAttributeValueInResult(boolean showAttributeValueInResult) {
		this.showAttributeValueInResult = showAttributeValueInResult;
	}

}
