package com.TextEditor;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * A class that handles the save feature every 2 minutes
 * @author David T. Auna
 * @version 8, 26 May 2020
 *
 * */
public class Saver implements Runnable{
    Thread saveThread;
    File openedFile;
    JTextArea textArea;
    private boolean flag;
    private int TIMEOUT = 120000;

    /**
     * <p><code>Saver</code> initializing it's attributes
     * @param openedFile
     *        file that needs to be saved
     * @param textArea
     *        the text area containing text to be saved
     * </p>
     * */
    public Saver(File openedFile, JTextArea textArea) {
        this.saveThread = new Thread(this, "saveThread");
        this.openedFile = openedFile;
        this.textArea = textArea;
        this.flag = true;
    }


    /**
     * <p>
     *     Saves the text when the thread runs.
     *     Executes every <code>TIMEOUT</code> milliseconds
     * </p>
     * @see Runnable
     * */
    @Override
    public void run() {
        while(flag){
            try {
            	synchronized (saveThread) {
                saveWork();
                saveThread.wait(TIMEOUT);
            	}
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * <p>
     *     Saves the work to the current file
     * </p>
     * @throws IOException
     * */
    private synchronized void saveWork() throws IOException {
            FileWriter writer = new FileWriter(openedFile);
            writer.write(textArea.getText());
            writer.close();
    }
    /**<p>
     *      Stops the current thread from running after closing
     * </p>*/
    public void stop(){
        if(flag){
            flag = false;
            synchronized(saveThread) {
            	saveThread.notifyAll();
            }
        }
    }

}
