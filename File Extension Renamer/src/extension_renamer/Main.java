package extension_renamer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/*
 * Links:
 * http://stackoverflow.com/questions/1844688/read-all-files-in-a-folder
 * 	Answer by rich
 * http://stackoverflow.com/questions/1914474/how-do-i-rename-not-move-a-file-in-jdk7
 * 	Answer by Avner
 * https://en.wikipedia.org/wiki/Ogg
 * 
 */

/*
 * Notes:
 * Java 8 must be installed on the computer
 * 
 */

//TODO: Change how I rename possibly?
//TODO: Add GUI for doing this?

/**
 * 
 * @author TANDLP
 * @since JAVA 8
 * @version 1.0
 */
public class Main {

	public static void main(String[] args) {
		//create required objects for input
		Scanner scanner = new Scanner(System.in);
		//use scanner.nextLine() to get input line
		//get folder
		
		
		/*
		 *  Options
		 */
		
		Options options = new Options();
		//option, long option, has argument, description
		Option folderPathOption = new Option("p", "path", true, "Folder path");
		folderPathOption.setRequired(true);
		
		Option oggFile = new Option("o", false, "Rename ogg file types to ogg");
		oggFile.setRequired(false);
		
		Option toExtName = new Option("t", "Rename-To", true, "Rename Extensions to");
		toExtName.setRequired(true);
		
		Option fromExtName = new Option("f", "Rename-From", true, "Rename extensions from");
		fromExtName.setRequired(true);
		fromExtName.setArgs(Option.UNLIMITED_VALUES);
		
		Option depthLimit = new Option("d", "depth", true, "Depth to go");
		depthLimit.setRequired(false);
		depthLimit.setType(int.class);
		
		options.addOption(folderPathOption);
		options.addOption(oggFile);
		options.addOption(toExtName);
		options.addOption(fromExtName);
		options.addOption(depthLimit);
		
		
		/*
		 * Parse options
		 */
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		
		CommandLine cmd = null;
		
		
		try{
			cmd = parser.parse(options, args);
		}
		catch(ParseException e){
			System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
		}
		
		
		
		//System.out.println("Please enter the path to files: ");
		String folderPath = cmd.getOptionValue("path");
		boolean doOggFile = cmd.hasOption("o");
		String toExt = cmd.getOptionValue("Rename-To");
		String[] fromExt = cmd.getOptionValues("Rename-From");
		int depth = -1;
		try{
			String depthS = cmd.getOptionValue("depth", "1");
			depth = Integer.parseInt(depthS);
			//don't allow 0 or less depth
			if(depth < 1){
				depth=1;
			}
		}
		catch(NumberFormatException r){
			System.err.println("Depth was not an integer");
			System.exit(1);
		}
		//String folderPath = scanner.nextLine(); //get the folder path
		
		/*
		 * Rename
		 */
		
		//access the folder
		Stream<java.nio.file.Path> paths = null;
		boolean error = false;
		try{
			//only go to depth 1 (no sub-directories)
			//go to depth
			paths = Files.walk(Paths.get(folderPath), depth);
			
		}
		catch(IOException e){
			System.out.println("Folder not found. Exiting now");
			error = true;
		}
		catch(SecurityException e){
			System.out.println("Security manager denied access to the folder");
			System.out.println("Please ensure that you have read/write permissions for this folder");
			error = true;
		}
		catch(InvalidPathException e){
			System.out.println(e.getMessage());
			System.out.println(e.getReason());
			System.out.println("Invalid path was entered. Exiting now");
			error = true;
		}
		finally{
			//will always execute anyways, even if no exception
			if(error == true){
				scanner.close();
				System.out.println("Exiting program now");
				System.exit(1);
			}
		}
		
		/*
		 * Actual renaming
		 */
		//Walk the path and select each file (as a Path object)
		//create arrayList to know what we renamed
		ArrayList<String> oldNames = new ArrayList<String>();
		paths.forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				//get the filename
				String fileName = (filePath.getFileName()).toString();
				//get the extension
				//now supporting non 3-char file extensions
				int lastDot = fileName.lastIndexOf('.');
				String ext = fileName.substring(lastDot + 1);
				
				//if it is a ogg extension, we'll want to rename the extension to ogg
				if(oggExt(ext) == true && ext.equals("ogg") == false && doOggFile == true){
					//add to list of files changed
					oldNames.add(fileName);
					//rename extension to be ogg
					fileName = fileName.substring(0, lastDot + 1)+"ogg";
					//attempt to rename the file using Files.move
					try{
						Files.move(filePath, filePath.resolveSibling(fileName), StandardCopyOption.REPLACE_EXISTING);
					}
					catch(IOException e){
						//something went wrong
						System.out.println("Something went wrong trying to rename "+fileName.substring(fileName.length()-3));
						System.out.println("Will continue with the rest of the files");
					}
				}
				else if(isIn(ext, fromExt) == true && doOggFile == false){
					//add to list of files changed
					oldNames.add(fileName);
					//rename extension to be to proper filename
					fileName = fileName.substring(0, lastDot + 1)+toExt;
					try{
						Files.move(filePath, filePath.resolveSibling(fileName), StandardCopyOption.REPLACE_EXISTING);
					}
					catch(IOException e){
						//something went wrong
						System.out.println("Something went wrong trying to rename "+fileName.substring(fileName.length()-3));
						System.out.println("Will continue with the rest of the files");
					}

				}
			}
		});
		
		//deal with results
		System.out.println("Finished running");
		if(oldNames.size() == 0){
			System.out.println("No files renamed");
		}else{
			System.out.println(oldNames.size() + " Files renamed");
			System.out.println("List of files renamed:");
			for(int i=0; i<oldNames.size(); i++){
				System.out.println(oldNames.get(i));
			}
		}
		//close things as needed
		scanner.close();
		paths.close();
	}
	
	//returns true if it's an "ogg" file type
	public static boolean oggExt(String str){
		if(str.equals("ogg")){
			return true;
		}
		else if(str.equals("ogx")){
			return true;
		}
		else if(str.equals("oga")){
			return true;
		}
		return false;
	}
	
	public static boolean isIn(String ext, String[] list){
		for(int i=0; i<list.length; i++){
			if(ext.equals(list[i])){
				return true;
			}
		}
		return false;
	}

}
