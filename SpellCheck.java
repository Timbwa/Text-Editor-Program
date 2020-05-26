package com.TextEditor;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import javax.swing.*;

/**
 * <p>
 *     Thread that initializes Spell Checking feature.
 *     It runs functions of the <code>JOrtho</code> library.
 * </p>
 * @author David T. Auna
 * @version 8, 26 May 2020
 * */
public class SpellCheck  implements Runnable{
    Thread check;
    JTextArea textArea;
    String dictionaryPath;

    /**
     * <p>
     *     Constructor that initializes the <code>textArea</code> and
     *     the dictionary path attributes
     * </p>
     * */
    public SpellCheck(JTextArea textArea) {
        check = new Thread(this, "spell Check");
        this.textArea = textArea;
        this.dictionaryPath = "/dictionary/";
    }

    /**
     * sets the text Area
     * @param textArea
     *        working text Area
     * */
    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }
    /**
     * <p>initializes the spell checker</p>
     * */
    @Override
    public void run() {
        initializeSpellCheck();
    }

    /**
     * Returns this thread
     * @return thread
     * */
    public Thread getThread() {
        return check;
    }

    /**
     * <p>
     *     initializes the library spellchecking settings
     *     as described at <a href="http://jortho.sourceforge.net/">JOrtho library documentation </a>
     * </p>
     * */
    private synchronized void initializeSpellCheck(){
        SpellChecker.setUserDictionaryProvider(new FileUserDictionary(this.dictionaryPath));
        SpellChecker.registerDictionaries(getClass().getResource(dictionaryPath), "en");
        SpellChecker.register(this.textArea);
        Editor.setCheckerOptions();
    }
}
