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
    
    private static FileWriter writer;
    private static volatile ErrorLogger instance = null;
    
    private ErrorLogger(){
        try{
            writer = new FileWriter(new File("errorLog.txt"));
        }catch (IOException e){
            System.err.println("couldn't create the errorLog.txt file");
        }
    }
    
    public final static ErrorLogger getInstance(){
        if (ErrorLogger.instance == null){
            synchronized (ErrorLogger.class){
                if (ErrorLogger.instance == null){
                    ErrorLogger.instance = new ErrorLogger();
                }
            }
        }
        return ErrorLogger.instance;
    }
    
    public void logError(String exceptionMessage){
        try{
        ErrorLogger.writer.write(exceptionMessage+"\n");
        }catch(IOException e){
            System.err.println("cannot log error");
        }
    }
    
    public void closeLogger(){
        try{
            ErrorLogger.writer.close();
        }catch (IOException e){
            System.err.println("cannot close errorLog.txt");
        }
    }
}
