package com.TextEditor;
import java.awt.EventQueue;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;

import com.inet.jortho.PopupListener;
import com.inet.jortho.SpellChecker;
import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellCheckerOptions;

public class Editor {

    private JFrame frame;
    final JFileChooser chooser = new JFileChooser();
    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private static File openedfile;
    public static SpellCheck spellCheck;
    public static SpellCheckerOptions checkerOptions;
    public static Saver saver;
    private static JTextArea textArea;
    private static String dictionaryPath;




    /**
     * Launch the application.
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    textArea = new JTextArea();
                    checkerOptions = new SpellCheckerOptions();
                    spellCheck = new SpellCheck(textArea);

                    spellCheck.setTextArea(textArea);

                    saver = new Saver(openedfile, textArea);
                    dictionaryPath = "/dictionary/";

                    Editor window = new Editor();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //spellCheck.getThread().start();
    }

    /**
     * Create the application.
     * @throws IOException
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public Editor() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        openedfile = null;
        initialize();
        spellCheck.getThread().start();

    }

    /**
     * Initialize the contents of the frame.
     * @throws IOException
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private void initialize() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {



        frame = new JFrame("Untitled");
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setBounds(100, 100, 707, 525);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);

        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.setForeground(new Color(0, 206, 209));
        textArea.setCaretColor(new Color(230, 230, 230));
        textArea.setBackground(Color.DARK_GRAY);
        JScrollPane scroll = new JScrollPane (textArea);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(scroll);


        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                if (openedfile != null) {
                    try {
                        int readchar;
                        StringBuffer readString = new StringBuffer("");
                        FileReader reader = new FileReader(openedfile);
                        while((readchar = reader.read()) != -1) {
                            readString.append((char)readchar);
                        }
                        reader.close();
                        if (!textArea.getText().equals(readString.toString())) {
                            int opt = JOptionPane.showConfirmDialog(frame, "Would you like to save your changes?");
                            if (opt != JOptionPane.CANCEL_OPTION && opt != JOptionPane.CLOSED_OPTION) {
                                if (opt == JOptionPane.YES_OPTION) {
                                    String writeString = textArea.getText();
                                    try {
                                        FileWriter writer = new FileWriter(openedfile);
                                        writer.write(writeString);
                                        writer.close();
                                        if(saver != null){
                                            saver.stop();
                                        }
                                    }catch (IOException error) {
                                        error.printStackTrace();
                                    }
                                }
                                frame.setVisible(false);
                            }
                        }
                        else {
                            frame.setVisible(false);
                        }
                    }catch (IOException error) {
                        error.printStackTrace();
                    }
                }

                else {
                    if (!textArea.getText().equals("")) {
                        int opt = JOptionPane.showConfirmDialog(frame, "Would you like to save your changes?");
                        if (opt != JOptionPane.CANCEL_OPTION && opt != JOptionPane.CLOSED_OPTION) {
                            if (opt == JOptionPane.YES_OPTION) {
                                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                int chooserret = chooser.showSaveDialog(frame);
                                if (chooserret == JFileChooser.APPROVE_OPTION) {
                                    if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt"))
                                        openedfile = new File (chooser.getSelectedFile().getAbsolutePath() + ".txt");
                                    else
                                        openedfile = chooser.getSelectedFile();
                                    try {
                                        if (openedfile.createNewFile()){
                                            String writeString = textArea.getText();
                                            FileWriter writer = new FileWriter(openedfile);
                                            writer.write(writeString);
                                            writer.close();

                                            if(saver != null){
                                                saver.stop();
                                            }
                                            frame.setVisible(false);
                                        }
                                    }catch (IOException error) {
                                        openedfile = null;
                                        error.printStackTrace();
                                    }
                                }
                            }
                            else
                                frame.setVisible(false);
                        }
                    }
                    else {
                        frame.setVisible(false);
                    }
                }
            }
        });


        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


                if (openedfile != null) {
                    try {
                        int readchar;
                        StringBuffer readString = new StringBuffer("");
                        FileReader reader = new FileReader(openedfile);
                        while((readchar = reader.read()) != -1) {
                            readString.append((char)readchar);
                        }
                        reader.close();
                        if (!textArea.getText().equals(readString.toString())) {
                            int opt = JOptionPane.showConfirmDialog(frame, "Would you like to save your changes?");
                            if (opt == JOptionPane.YES_OPTION) {
                                String writeString = textArea.getText();
                                try {
                                    FileWriter writer = new FileWriter(openedfile);
                                    writer.write(writeString);
                                    writer.close();
                                    saver.setOpenedFile(openedfile);
                                    saver.saveThread.start();
                                }catch (IOException error) {
                                    error.printStackTrace();
                                }
                                openedfile = null;
                                frame.setTitle("Untitled");
                                textArea.setText("");
                            }
                            else if (opt == JOptionPane.NO_OPTION) {
                                openedfile = null;
                                frame.setTitle("Untitled");
                                textArea.setText("");
                            }
                        }

                        else {
                            openedfile = null;
                            frame.setTitle("Untitled");
                            textArea.setText("");
                        }

                    }catch (IOException error) {
                        error.printStackTrace();
                    }
                }

                else {
                    if (!textArea.getText().equals("")) {
                        int opt = JOptionPane.showConfirmDialog(frame, "Would you like to save your changes?");
                        if (opt == JOptionPane.YES_OPTION) {
                            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            int chooserret = chooser.showSaveDialog(frame);
                            if (chooserret == JFileChooser.APPROVE_OPTION) {
                                if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt"))
                                    openedfile = new File (chooser.getSelectedFile().getAbsolutePath() + ".txt");
                                else
                                    openedfile = chooser.getSelectedFile();
                                try {
                                    if (openedfile.createNewFile()){
                                        String writeString = textArea.getText();
                                        FileWriter writer = new FileWriter(openedfile);
                                        writer.write(writeString);
                                        writer.close();

                                        saver.setOpenedFile(openedfile);
                                        saver.saveThread.start();
                                    }
                                }catch (IOException error) {
                                    openedfile = null;
                                    error.printStackTrace();
                                }
                                openedfile = null;
                                textArea.setText("");
                            }
                        }

                        else if (opt == JOptionPane.NO_OPTION) {
                            textArea.setText("");
                        }

                    }
                }

            }
        });
        mnFile.add(mntmNew);

        JMenuItem mntmOpen = new JMenuItem("Open");
        mntmOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == mntmOpen) {
                    int chooserret = chooser.showOpenDialog(frame);
                    if (chooserret == JFileChooser.APPROVE_OPTION) {
                        openedfile = chooser.getSelectedFile();
                        try {
                            int readchar;
                            StringBuffer readString = new StringBuffer("");
                            FileReader reader = new FileReader(openedfile);
                            while((readchar = reader.read()) != -1) {
                                readString.append((char)readchar);
                            }
                            frame.setTitle(openedfile.getName());
                            textArea.setText(readString.toString());
                            reader.close();

                            saver.setOpenedFile(openedfile);
                            saver.saveThread.start();
                        }catch (IOException error) {
                            error.printStackTrace();
                        }
                    }
                }
            }
        });

        mnFile.add(mntmOpen);

        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (openedfile != null && openedfile.canWrite()) {
                    String writeString = textArea.getText();
                    try {
                        FileWriter writer = new FileWriter(openedfile);
                        writer.write(writeString);
                        writer.close();
                        saver.setOpenedFile(openedfile);
                        if(!saver.saveThread.isAlive()){
                            saver.saveThread.start();
                        }

                    }catch (IOException error) {
                        error.printStackTrace();
                    }
                }
                else {
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int chooserret = chooser.showSaveDialog(frame);
                    if (chooserret == JFileChooser.APPROVE_OPTION) {
                        if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt"))
                            openedfile = new File (chooser.getSelectedFile().getAbsolutePath() + ".txt");
                        else
                            openedfile = chooser.getSelectedFile();
                        try {
                            if (openedfile.createNewFile()) {

                                String writeString = textArea.getText();
                                FileWriter writer = new FileWriter(openedfile);
                                writer.write(writeString);
                                writer.close();
                                frame.setTitle(openedfile.getName());

                                //saver = new Saver(openedfile, writeString); // start saving
                                saver.setOpenedFile(openedfile);
                                saver.saveThread.start();
                            }
                        }catch (IOException error) {
                            openedfile = null;
                            error.printStackTrace();
                        }
                    }
                }
            }
        });
        mnFile.add(mntmSave);

        JMenuItem mntmSaveAs = new JMenuItem("Save as");
        mntmSaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int chooserret = chooser.showSaveDialog(frame);
                if (chooserret == JFileChooser.APPROVE_OPTION) {
                    if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt"))
                        openedfile = new File (chooser.getSelectedFile().getAbsolutePath() + ".txt");
                    else
                        openedfile = chooser.getSelectedFile();
                    try {
                        if (openedfile.createNewFile()) {
                            String writeString = textArea.getText();
                            FileWriter writer = new FileWriter(openedfile);
                            writer.write(writeString);
                            writer.close();
                            //saver = new Saver(openedfile, writeString); // start saving

                            saver.setOpenedFile(openedfile);
                            saver.saveThread.start();
                            frame.setTitle(openedfile.getName());
                        }
                    }catch (IOException error) {
                        openedfile = null;
                        error.printStackTrace();
                    }
                }
            }
        });
        mnFile.add(mntmSaveAs);

        JMenuItem mntmClose = new JMenuItem("Close");
        mntmClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (openedfile != null) {
                    try {
                        int readchar;
                        StringBuffer readString = new StringBuffer("");
                        FileReader reader = new FileReader(openedfile);
                        while((readchar = reader.read()) != -1) {
                            readString.append((char)readchar);
                        }
                        reader.close();
                        if (!textArea.getText().equals(readString.toString())) {
                            int opt = JOptionPane.showConfirmDialog(frame, "Would you like to save your changes?");
                            if (opt != JOptionPane.CANCEL_OPTION && opt != JOptionPane.CLOSED_OPTION) {
                                if (opt == JOptionPane.YES_OPTION) {
                                    String writeString = textArea.getText();
                                    try {
                                        FileWriter writer = new FileWriter(openedfile);
                                        writer.write(writeString);
                                        writer.close();
                                        if(saver != null){
                                            saver.stop();
                                        }
                                    }catch (IOException error) {
                                        error.printStackTrace();
                                    }
                                }

                                frame.setVisible(false);
                            }
                        }
                        else {
                            frame.setVisible(false);
                        }
                    }catch (IOException error) {
                        error.printStackTrace();
                    }
                }

                else {
                    if (!textArea.getText().equals("")) {
                        int opt = JOptionPane.showConfirmDialog(frame, "Would you like to save your changes?");
                        if (opt != JOptionPane.CANCEL_OPTION && opt != JOptionPane.CLOSED_OPTION) {
                            if (opt == JOptionPane.YES_OPTION) {
                                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                int chooserret = chooser.showSaveDialog(frame);
                                if (chooserret == JFileChooser.APPROVE_OPTION) {
                                    if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt"))
                                        openedfile = new File (chooser.getSelectedFile().getAbsolutePath() + ".txt");
                                    else
                                        openedfile = chooser.getSelectedFile();
                                    try {
                                        if (openedfile.createNewFile()){
                                            String writeString = textArea.getText();
                                            FileWriter writer = new FileWriter(openedfile);
                                            writer.write(writeString);
                                            writer.close();
                                            if(saver != null){
                                                saver.stop();
                                            }
                                            frame.setVisible(false);
                                        }
                                    }catch (IOException error) {
                                        openedfile = null;
                                        error.printStackTrace();
                                    }

                                }
                            }
                            else
                                frame.setVisible(false);
                        }
                    }

                    else {
                        frame.setVisible(false);
                    }
                }

            }
        });
        mnFile.add(mntmClose);

        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        JMenuItem mntmCut = new JMenuItem("Cut");
        mntmCut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!textArea.getText().equals("")) {
                    String cutString;
                    if (textArea.getSelectedText() != null ) {
                        cutString = textArea.getSelectedText();
                        textArea.replaceSelection("");
                    }
                    else {
                        cutString = textArea.getText();
                        textArea.setText("");
                    }
                    StringSelection stringSelection = new StringSelection(cutString);
                    clipboard.setContents(stringSelection, null);
                }

            }
        });
        mnEdit.add(mntmCut);

        JMenuItem mntmCopy = new JMenuItem("Copy");
        mntmCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (!textArea.getText().equals("")) {
                    String copyString;
                    if (textArea.getSelectedText() != null ) {
                        copyString = textArea.getSelectedText();
                    }
                    else {
                        copyString = textArea.getText();
                    }
                    StringSelection stringSelection = new StringSelection(copyString);
                    clipboard.setContents(stringSelection, null);
                }

            }
        });
        mnEdit.add(mntmCopy);

        JMenuItem mntmPaste = new JMenuItem("Paste");
        mntmPaste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int pos = textArea.getCaretPosition();


                Transferable t = clipboard.getContents(this);
                if (t != null) {
                    try {
                        textArea.insert((String) t.getTransferData(DataFlavor.stringFlavor), pos);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });
        mnEdit.add(mntmPaste);

        JMenuItem mntmDelete = new JMenuItem("Delete");
        mntmDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (textArea.getSelectedText() != null)
                    textArea.replaceSelection("");
                else
                    textArea.setText("");
            }
        });
        mnEdit.add(mntmDelete);

        JMenu mnTheme = new JMenu("Theme");
        menuBar.add(mnTheme);

        JMenuItem mntmDark = new JMenuItem("Dark");
        mntmDark.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().setBackground(Color.DARK_GRAY);
                textArea.setForeground(new Color(0, 206, 209));
                textArea.setCaretColor(new Color(230, 230, 230));
                textArea.setBackground(Color.DARK_GRAY);
            }
        });
        mnTheme.add(mntmDark);

        JMenuItem mntmLight = new JMenuItem("Light");
        mntmLight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().setBackground(Color.WHITE);
                textArea.setForeground(Color.BLUE);
                textArea.setCaretColor(new Color(0,0,0));
                textArea.setBackground(Color.WHITE);
            }
        });
        mnTheme.add(mntmLight);


    }

    public static void setCheckerOptions(){
        checkerOptions.setCaseSensitive(false);
        checkerOptions.setIgnoreAllCapsWords(true);
        checkerOptions.setIgnoreWordsWithNumbers(true);
        checkerOptions.setSuggestionsLimitMenu(7);
        checkerOptions.setLanguageDisableVisible(false);
        JPopupMenu popupMenu = SpellChecker.createCheckerPopup(checkerOptions);
        textArea.addMouseListener(new PopupListener(popupMenu));
    }
}
