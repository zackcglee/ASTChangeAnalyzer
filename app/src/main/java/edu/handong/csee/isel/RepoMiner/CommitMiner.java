package edu.handong.csee.isel.RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import edu.handong.csee.isel.Main.CommandLineExecuter;


public class CommitMiner {
	
	private List<RevCommit> commitList;
	private File file = null;
	private Git git;
	private boolean erase = false;
	
	public CommitMiner(String path) throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		
		Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com()(.*?)$");
		Matcher matcher = pattern.matcher(path);
		String repoPath = "";
		Scanner scanner = new Scanner(System.in);;
		
		if (matcher.find()) {
			try{
				System.out.print("Cloned repository absolute path (default: " + System.getProperty("user.home") + "/Desktop): ");
				repoPath = scanner.nextLine();
				//wrong path
				if (!repoPath.matches("^((?:\\/[a-zA-Z0-9]+(?:_[a-zA-Z0-9]+)*(?:\\-[a-zA-Z0-9]+)*(?:.+))+)$"))
					repoPath = System.getProperty("user.home") + "/Desktop";
				
				if (repoPath.contains("\\")) {
					repoPath = repoPath.replace("/", "\\");
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			
			file = new File(repoPath + "/" + matcher.group(3));
			if (file.exists()) {
				System.err.println("File(PATH:" +file.toString() + ") already exists\n");
				System.err.print("Choose the proceeding action :\n"
						+ "  1: Rewrite the cloned repository to proceed\n"
						+ "  2: Terminate the program\n"
						+ "Enter selection (default: Rewrite) [1..2] ");
				int opt = scanner.nextInt();
				switch(opt) {
				case 1:
					new CommandLineExecuter().executeDeletion(file);
					break;
				case 2:
					System.out.println("\nProgram Terminated\n");
					System.exit(0);
					return;
				}
			}
			
			git = Git.cloneRepository()
					.setURI(path)
					.setDirectory(file).call();
			
			System.out.println("\nGit Clone Completed");
			System.out.println("L repository path: " + getRepoPath());
			System.out.println();
			erase = true;
		}
		
		else {
			file = new File(path + "/.git");
			git = Git.open(file);
		}
		
		Iterable<RevCommit> walk = git.log().call();
		commitList = IterableUtils.toList(walk);
		
	}
	
	public File getRepoPath() {
    	return file;
    }
	
	public List<RevCommit> getCommitList() {
		return commitList;
	}
	
	public Repository getRepo() {
		return git.getRepository();
	}
	
	public boolean getErase() {
		return erase;
	}
    
}

