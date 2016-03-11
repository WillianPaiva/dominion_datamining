package universite.bordeaux.app.ReadersAndParser;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class FileReader {
    private Scanner scan;
    private File log;
    private String line;

    public FileReader(File file){
        this.log = file;
        try{
            scan = new Scanner(file);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


	/**
	 * @return the scan
	 */
	public Scanner getScan() {
		return scan;
	}

    public String getName(){
        return this.log.getName();
    }

	/**
	 *
	 *
   * @return the actual line of the file
	 */
    public String getLine(){
        return this.line;
    }

	/**
   * moves the pointer to the next line
   *
   * @return next line
	 */
    public String jumpline(){
        try{
        this.line = this.scan.nextLine();
        }
        catch(NoSuchElementException e){
        System.out.println("error with file: "+this.getName());
        }
        return this.line;
    }

	/**
   * search for the first line that contains a given string
	 *
	 * @param target
	 * @return line found or null
	 */
    public String searchLineWithString(String target){
        while(scan.hasNextLine()){
            this.line = this.scan.nextLine();
            if(line.matches(target)){
                return this.line;
            }
        }
        return null;
    }


	/**
   * close the scanner
	 *
	 */
    public void close(){
        scan.close();
    }

	/**
   * returns the pointer to the first line of the file;
	 *
	 */
    public void rewindFile(){
        scan.close();
        try{
            scan = new Scanner(this.log);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public boolean isEmpty(){
       this.rewindFile();
       return !(this.scan.hasNext());
    }
}
