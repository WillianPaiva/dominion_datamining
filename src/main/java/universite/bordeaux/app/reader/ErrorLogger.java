/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package universite.bordeaux.app.reader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author ebayol
 */
public final class ErrorLogger {
    
    private ErrorLogger(){
        
    }
    public static void logError(String exceptionMessage){
        FileWriter writer;
            try{
            writer = new FileWriter(new File("errorLog.txt"));
            writer.write(exceptionMessage+"\n");
            writer.close();
        }catch(IOException e){
            System.err.println("cannot log error");
        }
    }
}
