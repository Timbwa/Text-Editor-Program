package com.TextEditor;

import java.io.*;
import java.util.HashMap;

public class Dictionary {
    private String filepath;
    private HashMap<String, String> dictMap;
    private File dictionaryFile;
    private BufferedReader bufferedReader;

    public Dictionary(String filepath) {
        this.filepath = filepath;
        this.dictionaryFile = new File(this.filepath);
        dictMap = new HashMap<>();
        loadDictionary();
    }

    private void loadDictionary(){
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile)));
            String word;
            while((word = bufferedReader.readLine()) != null){
                dictMap.put(word, word);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean inDictionary(String word){
        return dictMap.containsKey(word);
    }
}
