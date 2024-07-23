package de.jlo.talend.tweak.model.conflict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class TaskSolveConflicts {
	
	private static Logger LOG = Logger.getLogger(TaskSolveConflicts.class);
	private String entryPath = null;
	private String gitSide = null;
	private List<String> listChangedFiles = new ArrayList<>();

	public void execute() throws Exception {
		if (entryPath == null || entryPath.trim().isEmpty()) {
			throw new IllegalArgumentException("The entry path to search for git conflicted files cannot be null or empty. Please choose a folder or file before.");
		}
		File entryDir = new File(entryPath);
		if (entryDir.exists() == false) {
			throw new IOException("Entry dir: " + entryDir.getAbsolutePath() + " does not exist");
		}
		if (entryDir.isFile()) {
			entryDir = entryDir.getParentFile();
		}
		processDir(entryDir);
		LOG.info(listChangedFiles.size() + " files had conflicts");
		if (listChangedFiles.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("List affected files:\n");
			for (String filePath : listChangedFiles) {
				sb.append(filePath);
				sb.append("\n");
			}
			LOG.info(sb.toString());
		}
	}
	
	private void processDir(File entry) throws Exception {
		LOG.info("Start checking conflicted files in folder: " + entry);
		if (entry.isFile()) {
			solveConflicts(entry, gitSide);
		} else {
			File[] files = entry.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						processDir(f);
					} else {
						solveConflicts(f, gitSide);
					}
				}
			}
		}
	}

	public String getEntryPath() {
		return entryPath;
	}

	public void setEntryPath(String entryPath) {
		this.entryPath = entryPath;
	}
	
	public void solveConflicts(File f, String side) throws Exception {
		LOG.debug("Start check conflicts using: " + side + " in file: " + f.getAbsolutePath());
		if (side == null || side.trim().isEmpty()) {
			throw new IllegalArgumentException("The side cannot be null or empty");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		boolean inContent = false;
		boolean inHEAD = false;
		boolean inRemote = false;
		boolean hasConflicts = false;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("<<<<<<<")) {
				inHEAD = true;
				inContent = false;
				hasConflicts = true;
			} else if (line.startsWith("=======")) {
				inRemote = true;
				inContent = false;
				inHEAD = false;
				hasConflicts = true;
			} else if (line.startsWith(">>>>>>>")) {
				inHEAD = false;
				inContent = false;
				inRemote = false;
				hasConflicts = true;
			} else {
				inContent = true;
			}
			if (inContent) {
				if (inHEAD && "mine".equalsIgnoreCase(side)) {
					sb.append(line);
					sb.append("\n");
				} else if (inRemote && "theirs".equalsIgnoreCase(side)) {
					sb.append(line);
					sb.append("\n");
				} else if (inHEAD == false && inRemote == false) {
					sb.append(line);
					sb.append("\n");
				}
			}
		}
		reader.close();
		if (hasConflicts) {
			LOG.info("    File has conflicts, write solution");
			// write file back
			String content = sb.toString();
			Files.write(java.nio.file.Paths.get(f.getAbsolutePath()), content.getBytes("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);
			listChangedFiles.add(f.getAbsolutePath());
		}
	}

	public String getGitSide() {
		return gitSide;
	}

	public void setGitSide(String gitSide) {
		if (gitSide == null || gitSide.trim().isEmpty()) {
			throw new IllegalArgumentException("gitSide cannot be null or empty");
		} else if (gitSide.equalsIgnoreCase("mine") || gitSide.equalsIgnoreCase("theirs")) {
			this.gitSide = gitSide;
		} else {
			throw new IllegalArgumentException("Git resolute side is invalid: " + gitSide);
		}
		this.gitSide = gitSide;
	}

}
