package reader;
import java.io.IOException;
import java.io.File;
import java.util.*;


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
        this.line = this.scan.nextLine();
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
            if(line.contains(target)){
                return this.line;
            }
        }
        return this.line;
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
        try{
            scan = new Scanner(this.log);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
