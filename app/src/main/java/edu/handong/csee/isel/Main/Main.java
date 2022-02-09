/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {

	private String os;
	private String language;
	private String DiffTool;
	private String input;
	private HashMap<String, ArrayList<String>> fileMap;
	private HashMap<String, ArrayList<String>> hunkMap;
	private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;

    public static void main(String[] args) throws IOException {
    	Main main = new Main();
    	main.run(args);
    }

    private void run(String[] args) throws IOException {
		checkOS();
		CLI option = new CLI();
		ArrayList<String> inputs = option.CommonCLI(args);
		language = option.getLanguage();
		DiffTool = option.getDiffTool();
		input = option.getOptionValueP();

		if (inputs.size() == 0)
			return;

		CommitMiner commitMine;
		ChangeMiner changeMine;
		ChangeAnalyzer changeAnalyzer = new ChangeAnalyzer();

		fileMap = new HashMap<String, ArrayList<String>>();
		hunkMap = new HashMap<String, ArrayList<String>>();
		coreMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
		int total_count = 0;
		int file_count = 0;
		int hunk_count = 0;
		int core_count = 0;

		try {
			for (String str : inputs) {
				System.out.println(str);
				System.out.print("ASTChangeAnalyzing...");
				commitMine = new CommitMiner(str);
				if (commitMine.isCompleted()) {
					System.out.print("Change Mining...");
					changeMine = new ChangeMiner();
					changeMine.setProperties(commitMine.getRepo(), language, DiffTool);
					ArrayList<ChangeInfo> changeInfoList = changeMine.collect(commitMine.getCommitList());
					if (changeInfoList.size() < 1) {
						System.out.println("Change Mining Failed\n");
						continue;
					}
					for (ChangeInfo changeInfo : changeInfoList) {
						String fkey;
						String hkey;
						String projectName = changeInfo.getProjectName();
						String commitID = changeInfo.getCommitID();
						switch (language) {
							case "LAS":
								fkey = changeAnalyzer.computeSHA256Hash(changeInfo.getEditOpWithName());
								break;
							default:
								fkey = changeAnalyzer.computeSHA256Hash(changeInfo.getActionsWithName());
								hkey = changeAnalyzer.computeSHA256Hash(changeInfo.getActionsWithType());
								if (hunkMap.containsKey(hkey)) {
									hunkMap.get(hkey).add(projectName + "," + commitID);
									hunk_count++;
								}
								else {
									ArrayList<String> hunkList = new ArrayList<String>();
									hunkList.add(projectName + "," + commitID);
									hunkMap.put(hkey, hunkList);
								}

								if (coreMap.containsKey(fkey)) {
									if (coreMap.get(fkey).containsKey(hkey)) {
										coreMap.get(fkey).get(hkey).add(projectName + "," + commitID);
										core_count++;
									}
									else {
										ArrayList<String> combineList = new ArrayList<String>();
										combineList.add(projectName + "," + commitID);
										coreMap.get(fkey).put(hkey, combineList);
									}
								}
								else {
									ArrayList<String> combineList = new ArrayList<String>();
									combineList.add(projectName + "," + commitID);
									HashMap <String, ArrayList<String>> newCoreMap = new HashMap <String, ArrayList<String>>();
									newCoreMap.put(hkey, combineList);
									coreMap.put(fkey, newCoreMap);
								}
								break;
						}
						if (fileMap.containsKey(fkey)) {
							fileMap.get(fkey).add(projectName + "," + commitID);
							file_count++;
						}
						else {
							ArrayList<String> fileList = new ArrayList<String>();
							fileList.add(projectName + "," + commitID);
							fileMap.put(fkey, fileList);
						}
						total_count++;
					}
					System.out.println("Finish\n");
				}
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));
			writer.write("Mined Repository Path : " + input
					+ "\nAnalyzed Change size : " + total_count
					+ "\nHashMap(file level) size: " + file_count
					+ "\nHashMap(hunk level) size: " + hunk_count
					+ "\nHashMap(core level) size: " + core_count);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void checkOS() {
		String cmd;
    	if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
            setOS("MAC");
			System.setProperty("gt.pp.path", new File("").getAbsolutePath()
					+ File.separator + "app"
					+ File.separator + "pythonparser"
					+ File.separator + "pythonparser");

			System.setProperty("gt.cgum.path", new File("").getAbsolutePath()
					+ File.separator + "app"
					+ File.separator + "cgum"
					+ File.separator + "cgum");

			cmd = "pip3 install -r " + new File("").getAbsolutePath()
                    + File.separator + "app"
                    + File.separator + "pythonparser"
                    + File.separator + "requirements.txt";
        } else {
			setOS("LINUX");
			System.setProperty("gt.pp.path", "../../../../pythonparser/pythonparser");
			System.setProperty("gt.cgum.path", "/data/CGYW/ASTChangeAnalyzer/app/cgum/cgum");
			cmd = "pip3 install -r ../../../../pythonparser/requirements.txt";
        }
		CommandLineExecutor cli = new CommandLineExecutor();
		cli.executeSettings(cmd);
    }

	public void setOS(String os) {
		this.os = os;
	}

}

