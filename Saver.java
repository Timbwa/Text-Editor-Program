package com.TextEditor;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Saver implements Runnable{
    Thread saveThread;
    File openedFile;
    JTextArea textArea;
    public static int counter = 0;
    private boolean flag;

    public Saver(File openedFile, JTextArea textArea) {
        this.saveThread = new Thread(this, "saveThread");
        this.openedFile = openedFile;
        this.textArea = textArea;
        this.flag = true;
        counter++;
    }

    @Override
    public void run() {
        while(flag){
            try {
                saveWork();
                System.out.println("Counter: " + counter);
                Thread.sleep(120000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stopped Saving Thread");
    }

    private synchronized void saveWork() throws IOException {
        //System.out.println(openedFile.createNewFile());
        //if(openedFile.createNewFile()){
            FileWriter writer = new FileWriter(openedFile);
            System.out.println(textArea.getText());
            writer.write(textArea.getText());
            writer.close();
            System.out.println("Saved, counter: " + counter);
        //}
    }
    public void stop(){
        if(flag){
            flag = false;
        }
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }
}
