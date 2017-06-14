package extension_renamer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

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
		System.out.println("Please enter the path to files: ");
		String folderPath = scanner.nextLine(); //get the folder path
		
		//access the folder
		Stream<Path> paths = null;
		boolean error = false;
		try{
			//only go to depth 1 (no sub-directories)
			paths = Files.walk(Paths.get(folderPath), 1);
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
		
		//Walk the path and select each file (as a Path object)
		//create arrayList to know what we renamed
		ArrayList<String> oldNames = new ArrayList<String>();
		paths.forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				//get the filename
				String fileName = (filePath.getFileName()).toString();
				//get the extension
				String ext = fileName.substring(fileName.length() - 3);
				//if it is a ogg extension, we'll want to rename the extension to ogg
				if(oggExt(ext) == true && ext.equals("ogg") == false){
					//add to list of files changed
					oldNames.add(fileName);
					//rename extension to be ogg
					fileName = fileName.substring(0, fileName.length()-3)+"ogg";
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

}
