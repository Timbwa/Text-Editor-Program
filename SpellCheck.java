package com.TextEditor;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;
import com.inet.jortho.SpellCheckerOptions;
import com.inet.jortho.UserDictionaryProvider;

import javax.swing.*;
import java.util.Iterator;
import java.util.Locale;

public class SpellCheck  implements Runnable{
    Thread check;
    JTextArea textArea;
    String dictionaryPath;

    public SpellCheck(JTextArea textArea) {
        check = new Thread(this, "spell Check");
        this.textArea = textArea;
        this.dictionaryPath = "/dictionary/";
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void run() {
        System.out.println("Running...");
        initializeSpellCheck();
    }

    public Thread getThread() {
        return check;
    }

    private synchronized void initializeSpellCheck(){
        SpellChecker.setUserDictionaryProvider(new FileUserDictionary(this.dictionaryPath));
        SpellChecker.registerDictionaries(getClass().getResource(dictionaryPath), "en");
        SpellChecker.register(this.textArea);
        Editor.setCheckerOptions();
        System.out.println("initialized spell check");
    }
}
