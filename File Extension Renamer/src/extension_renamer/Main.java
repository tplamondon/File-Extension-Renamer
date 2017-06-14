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
		
		Ext_rename.rename(folderPath, "ogg", null, 1);
		
		//deal with results
		/* (this is done in the thing so we're good
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
		*/
		//close things as needed
		scanner.close();
	}
	
}
