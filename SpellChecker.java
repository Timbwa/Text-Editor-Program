package com.TextEditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;

public class SpellChecker implements Runnable{
    private Dictionary programDictionary;
    private String filePath = "words.txt";
    public Thread spellCheckThread;
    private JTextArea textArea;


    public SpellChecker(JTextArea textArea){
        programDictionary = new Dictionary(filePath);
        spellCheckThread = new Thread(this, "spellCheck");
        this.textArea = textArea;
    }

    @Override
    public void run() {

        while(true){
            spellCheck();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void spellCheck(){
        this.textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkLastWord();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                //checkLastWord();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
               checkLastWord();
            }
        });
    }

    private synchronized void checkLastWord(){
        int startIndex, endIndex;
        try {
            startIndex = Utilities.getWordStart(this.textArea, this.textArea.getCaretPosition());
            endIndex = Utilities.getWordEnd(this.textArea, this.textArea.getCaretPosition());
            String word = this.textArea.getDocument().getText(startIndex, endIndex - startIndex);
            if(!(startIndex == endIndex || word.startsWith(" "))){
                //System.out.println(programDictionary.inDictionary(word));
            }
        } catch (BadLocationException e) {

            e.printStackTrace();
        }
    }
}
