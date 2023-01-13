package de.jlo.talend.tweak.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;

public class TalendModel {
	
	private static final Logger LOG = Logger.getLogger(TalendModel.class);
	private Map<String, List<Talendjob>> mapNameJobs = new HashMap<>();
	private Map<String, DatabaseConnection> mapIdConnections = new HashMap<>();
	private List<Talendjob> listAllJobs = new ArrayList<>();
	private String projectRootDir = null;
	private String processFolderPath = null;
	private OutputFormat format = OutputFormat.createPrettyPrint();
	
	/**
	 * Reads all Talend jobs and fills the job map
	 * @param rootDir points to project folder written the in capital letters 
	 * @return number jobs read
	 * @throws Exception
	 */
	public int readProject(String rootDir) throws Exception {
    	LOG.info("Start read jobs and connections from project root: " + rootDir);
    	projectRootDir = rootDir;
		File processFolder = new File(rootDir, "process");
		processFolderPath = processFolder.getAbsolutePath();
		readJobPropertiesFiles(processFolder);
    	LOG.info("Finished read " + listAllJobs.size() + " jobs from project root: " + rootDir);
    	LOG.info("Read database connections...");
    	File metadataConnectionFolder = new File(rootDir, "metadata/connections"); 
    	readDatabaseItemFiles(metadataConnectionFolder);
    	LOG.info("Finished read " + mapIdConnections.size() + " connections from project root: " + rootDir);
		return listAllJobs.size();
	}

	private void registerJob(Talendjob job) throws Exception {
		listAllJobs.add(job);
		List<Talendjob> list = mapNameJobs.get(job.getJobName());
		if (list == null) {
			list = new ArrayList<Talendjob>();
			mapNameJobs.put(job.getJobName(), list);
		}
		if (list.contains(job) == false) {
			list.add(job);
		}
	}
	
	public Talendjob getJobByVersion(String jobName, String version) {
		if (version == null || version.equals("Latest")) {
			return getLatestJob(jobName);
		} else {
			List<Talendjob> list = mapNameJobs.get(jobName);
			if (list != null && list.isEmpty() == false) {
				for (Talendjob job : list) {
					if (job.getVersion().equals(version)) {
						return job;
					}
				}
			}
			return null;
		}
	}
	
    public Talendjob getLatestJob(String jobName) {
		List<Talendjob> list = mapNameJobs.get(jobName);
		if (list != null && list.isEmpty() == false) {
			Collections.sort(list);
			// after sort, the latest is the first element
			return list.get(0);
		} else {
			return null;
		}
	}
    
    public List<Talendjob> getAllJobs() {
    	return listAllJobs;
    }
    
    public List<Talendjob> getJobs(String jobNamePattern) {
    	return getJobs(jobNamePattern, false);
    }

    public Talendjob getJobById(String id) {
    	if (id == null || id.trim().isEmpty()) {
    		throw new IllegalArgumentException("id cannot be null or empty");
    	}
    	for (Talendjob job : listAllJobs) {
    		if (id.equals(job.getId())) {
    			return job;
    		}
    	}
    	return null;
    }
    
    public List<Talendjob> getJobs(String jobNamePattern, boolean onlyLatestVersion) {
    	List<Talendjob> list = new ArrayList<Talendjob>();
    	Set<String> uniqueJobNames = new HashSet<>();
    	Pattern pattern = null;
    	if (jobNamePattern != null && jobNamePattern.trim().isEmpty() == false) {
        	pattern = Pattern.compile(jobNamePattern, Pattern.CASE_INSENSITIVE);
    	}
    	for (Talendjob job : listAllJobs) {
    		if (pattern != null) {
        		Matcher m = pattern.matcher(job.getJobName());
        		if (m.find()) {
            		if (onlyLatestVersion) {
            			if (uniqueJobNames.contains(job.getJobName())) {
            				continue;
            			} else {
            				uniqueJobNames.add(job.getJobName());
            				job = getLatestJob(job.getJobName());
            			}
            		}
        			list.add(job);
        		}
    		} else {
        		if (onlyLatestVersion) {
        			if (uniqueJobNames.contains(job.getJobName())) {
        				continue;
        			} else {
        				uniqueJobNames.add(job.getJobName());
        				job = getLatestJob(job.getJobName());
        			}
        		}
    			list.add(job);
    		}
    	}
    	return list;
    }

    public List<Node> getComponents(Talendjob job, String componentName) throws Exception {
    	Document doc = readItem(job);
    	return getComponents(doc, componentName);
    }
	
    public List<Node> getComponents(Document doc, String ... componentName) throws Exception {
    	Element root = doc.getRootElement();
    	Iterator<Element> it = root.elementIterator("node");
    	List<Node> allNodes = new ArrayList<Node>();
    	while (it.hasNext()) {
    		Element e = it.next();
    		String cn = e.attributeValue("componentName");
    		if (componentName != null) {
        		for (String name : componentName) {
            		if (cn.toLowerCase().contains(name.toLowerCase())) {
                		allNodes.add(e);
            		}
        		}
    		}
    	}
    	return allNodes;
    }
    
    public List<Node> getAllComponents(Document doc) throws Exception {
    	Element root = doc.getRootElement();
    	Iterator<Element> it = root.elementIterator("node");
    	List<Node> allNodes = new ArrayList<Node>();
    	while (it.hasNext()) {
    		Element e = it.next();
    		String cn = e.attributeValue("componentName");
    		if (cn != null) {
        		allNodes.add(e);
    		}
    	}
    	return allNodes;
    }

    private void readJobPropertiesFiles(File processFolder) throws Exception {
        File[] list = processFolder.listFiles();
        if (list == null) {
        	return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
            	readJobPropertiesFiles(f);
            	LOG.debug("Read jobs in: " + f.getAbsoluteFile());
            } else if (f.getName().endsWith(".properties")) {
            	try {
					Talendjob job = readTalendJobFromProperties(f);
					registerJob(job);
				} catch (Exception e) {
					LOG.error("Failed to read properties file: " + f.getAbsolutePath(), e);
					throw new Exception("Failed to read properties file: " + f.getAbsolutePath(), e);
				}
            }
        }
    }
    
    private void readDatabaseItemFiles(File metadataConnectionFolder) throws Exception {
    	LOG.debug("Read database connection item files from folder: " + metadataConnectionFolder.getAbsolutePath());
        File[] list = metadataConnectionFolder.listFiles();
        if (list == null) {
        	return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
            	readDatabaseItemFiles(f);
            	LOG.debug("Read jobs in: " + f.getAbsoluteFile());
            } else if (f.getName().endsWith(".item")) {
            	try {
					DatabaseConnection conn = readDatabaseConnectionFromFile(f);
					if (conn != null) {
						mapIdConnections.put(conn.getId(), conn);
					}
				} catch (Exception e) {
					LOG.error("Failed to read item file: " + f.getAbsolutePath(), e);
					throw new Exception("Failed to read item file: " + f.getAbsolutePath(), e);
				}
            }
        }
    }
    
    public DatabaseConnection getDatabaseConnectionById(String id) {
    	return mapIdConnections.get(id);
    }

    public Document readItem(Talendjob job) throws Exception {
    	String filePath = job.getPathWithoutExtension() + ".item";
    	return readFile(new File(filePath));
    }
    
    private DatabaseConnection readDatabaseConnectionFromFile(File itemFile) throws Exception {
    	Document itemDoc = readFile(itemFile);
    	Element databaseConnectionNode = (Element) itemDoc.selectSingleNode("/xmi:XMI/TalendMetadata:DatabaseConnection");
    	if (databaseConnectionNode != null) {
        	return new DatabaseConnection(databaseConnectionNode);
    	} else {
    		return null;
    	}
    }
    
    public Talendjob readTalendJobFromProperties(File propertiesFile) throws Exception {
    	Document propDoc = readFile(propertiesFile);
    	Talendjob job = new Talendjob(this);
    	Element propertyNode = (Element) propDoc.selectSingleNode("/xmi:XMI/TalendProperties:Property");
    	QName nameId = new QName("id", null);
    	job.setId(propertyNode.attributeValue(nameId));
    	job.setJobName(propertyNode.attributeValue("label"));
    	job.setPath(propertiesFile.getAbsolutePath());
    	job.setVersion(propertyNode.attributeValue("version"));
    	String folder = propertiesFile.getParentFile().getAbsolutePath().replace(processFolderPath, "");
    	job.setJobFolder(folder);
    	if (LOG.isDebugEnabled()) {
        	LOG.debug("Read Talend job properties from file: " + propertiesFile.getAbsolutePath() + ". Id=" + job.getId());
    	}
    	return job;
    }

    public static Document readFile(File f) throws Exception {
    	try {
        	return DocumentHelper.parseText(readFileText(f));
    	} catch (Exception e) {
    		throw new Exception("Read file: " + f.getAbsolutePath() + " failed: " + e.getMessage(), e);
    	}
    }
    
    public static String readFileText(File f) throws Exception {
    	try {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
    		String line = null;
    		StringBuilder sb = new StringBuilder();
    		while ((line = reader.readLine()) != null) {
    			sb.append(line.trim());
    			sb.append('\n');
    		}
    		reader.close();
        	return sb.toString();
    	} catch (Exception e) {
    		throw new Exception("Read file: " + f.getAbsolutePath() + " failed: " + e.getMessage(), e);
    	}
    }

	public String getProjectRootDir() {
		return projectRootDir;
	}
	
	private String getRelativePath(Talendjob job) {
		String path = job.getJobFolder() + "/" + job.getJobName() + "_" + job.getVersion();
		return path;
	}
	
	public String writeItemFile(Talendjob job, String targetRootDir) throws Exception {
		Document itemDoc = job.getItemDoc();
		if (itemDoc == null) {
			throw new Exception("Talend job: " + job + " does not carry a document.");
		}
		String targetFilePath = null;
		if (targetRootDir == null) {
			targetFilePath = job.getPathWithoutExtension() + ".item";
		} else {
			String relPath = getRelativePath(job);
			if (targetRootDir.endsWith("/") == false) {
				targetRootDir = targetRootDir + "/";
			}
			targetFilePath = targetRootDir + "/process" + relPath + ".item";
		}
		File targetFile = new File(targetFilePath);
		File targetDir = targetFile.getParentFile();
		targetDir.mkdirs();
		if (targetDir.exists() == false) {
			throw new Exception("Cannot create or use target dir: " + targetDir.getAbsolutePath());
		}
		LOG.debug("Write item file: " + targetFile.getAbsolutePath());
		TalendXMLWriter writer = new TalendXMLWriter(new FileOutputStream(targetFile), format);
		writer.setEscapeText(true);
        writer.write( itemDoc );
        writer.close();
		return targetFilePath;
	}
    
	public int getCountJobs() {
		return listAllJobs.size();
	}

	public static String getComponentId(Element comp) {
		return getComponentAttribute(comp, "UNIQUE_NAME");
	}

	public static String getComponentAttribute(Element comp, String attributeName) {
		if (comp == null) {
			throw new IllegalArgumentException("comp cannot be null");
		}
		if (attributeName == null || attributeName.trim().isEmpty()) {
			throw new IllegalArgumentException("attributeName cannot be null or empty");
		}
		List<Element> params = comp.elements();
		for (Element param : params) {
			String name = param.attributeValue("name");
			String value = param.attributeValue("value");
			if (attributeName.equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}

}
