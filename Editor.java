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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;

import com.inet.jortho.PopupListener;
import com.inet.jortho.SpellChecker;
import com.inet.jortho.SpellCheckerOptions;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * The entry point class to the application that will provide interaction over GUI
 * @author Ahmed El Cheikh Ammar
 * @version 8, 26 May 2020
 * */
public class Editor {

    private JFrame frame;
    final JFileChooser chooser = new JFileChooser();
    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private static File openedfile;
    public static SpellCheck spellCheck;
    public static SpellCheckerOptions checkerOptions;
    public static Saver saver;
    public static ButtonMonitor monitor;
    private static JTextArea textArea;
    final static UndoManager undo = new UndoManager();

    /**
     * @param args
     *         no command line arguments are used. Possibly in the future.
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        EventQueue.invokeLater(() -> {
            try {
                textArea = new JTextArea();
                checkerOptions = new SpellCheckerOptions();
                spellCheck = new SpellCheck(textArea);

                spellCheck.setTextArea(textArea);
                saver = new Saver(openedfile, textArea);

                Editor window = new Editor();
                window.frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
     *
     */
    private void initialize(){
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
                closeEditor();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmNew = new JMenuItem("New                                    Ctrl+N");
        
        mntmNew.addActionListener(e -> {
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
                            }catch (IOException error) {
                                error.printStackTrace();
                            }
                            openedfile = null;
                            frame.setTitle("Untitled");
                            synchronized(textArea) {
                            textArea.setText("");
                            }
                            if (saver.saveThread.isAlive())
                                saver.stop();
                        }
                        else if (opt == JOptionPane.NO_OPTION) {
                            openedfile = null;
                            frame.setTitle("Untitled");
                            synchronized(textArea) {
                            textArea.setText("");
                            }
                            if (saver.saveThread.isAlive())
                                saver.stop();
                        }
                    }

                    else {
                        openedfile = null;
                        frame.setTitle("Untitled");
                        synchronized (textArea) {
                        textArea.setText("");
                        }
                        if (saver.saveThread.isAlive())
                            saver.stop();
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
                            closeIfyes(chooser);
                            openedfile = null;
                            synchronized (textArea) {
                            textArea.setText("");
                            }
                            if (saver.saveThread.isAlive())
                                saver.stop();

                        }
                    }

                    else if (opt == JOptionPane.NO_OPTION) {
                        synchronized (textArea) {
                            textArea.setText("");
                            }
                        if (saver.saveThread.isAlive())
                            saver.stop();

                    }

                }
            }

        });
       
        
        mnFile.add(mntmNew);

       
        
        JMenuItem mntmOpen = new JMenuItem("Open                                   Ctrl+O");
        mntmOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == mntmOpen) {
                	
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
                                    }catch (IOException error) {
                                        error.printStackTrace();
                                    }
                                    openFile();

                                }
                                else if (opt == JOptionPane.NO_OPTION) {
                                	openFile();

                                }
                            }

                            else {
                            	openFile();

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
                                    closeIfyes(chooser);

                                    openFile();

                                }
                            }

                            else if (opt == JOptionPane.NO_OPTION) {
                            	openFile();

                            }

                        }
                        
                        else
                        	openFile();
                    }
                }
            }
        });

        mnFile.add(mntmOpen);

        JMenuItem mntmSave = new JMenuItem("Save                                    Ctrl+S");
        mntmSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (openedfile != null && openedfile.canWrite()) {
                    String writeString = textArea.getText();
                    try {
                        FileWriter writer = new FileWriter(openedfile);
                        writer.write(writeString);
                        writer.close();
                        //saver.setOpenedFile(openedfile);
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
                                if (saver.saveThread.isAlive()) {
                                	saver.stop();
                                }
                                saver = new Saver(openedfile,textArea);
                                //saver.setOpenedFile(openedfile);
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

        JMenuItem mntmSaveAs = new JMenuItem("Save as                      Ctrl+Shift+S");
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
                            if (saver.saveThread.isAlive()) {
                            	saver.stop();
                            }
                            saver = new Saver(openedfile, textArea);
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
            	closeEditor();
            }
        });
        mnFile.add(mntmClose);

        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        

        JMenuItem mntmCut = new JMenuItem("Cut                                      Ctrl+X");
        mntmCut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	synchronized (textArea) {
                if (!textArea.getText().equals("")) {
                    String cutString;
                    cutString = textArea.getSelectedText();
                    textArea.replaceSelection("");
                    StringSelection stringSelection = new StringSelection(cutString);
                    clipboard.setContents(stringSelection, null);
                }
            	}
            }
        });
        
        Document doc = textArea.getDocument();

        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });
        
        JMenuItem mntmUndo = new JMenuItem("Undo                                   Ctrl+Z");
        mntmUndo.addActionListener(arg0 -> {
            try {
                    if (undo.canUndo()) {
                    undo.undo();
                    }
            } catch (CannotUndoException ignored) {
            }
        });
        mnEdit.add(mntmUndo);
        
        JMenuItem mntmRedo = new JMenuItem("Redo                                   Ctrl+Y");
        mntmRedo.addActionListener(arg0 -> {
            try {
                    if (undo.canRedo()) {
                    undo.redo();
                    }
                } catch (CannotRedoException ignored) {
                }
        });
        mnEdit.add(mntmRedo);
        mnEdit.add(mntmCut);

        JMenuItem mntmCopy = new JMenuItem("Copy                                   Ctrl+C");
        mntmCopy.addActionListener(e -> {

            if (!textArea.getText().equals("")) {
                String copyString;

                copyString = textArea.getSelectedText();

                StringSelection stringSelection = new StringSelection(copyString);
                clipboard.setContents(stringSelection, null);
            }

        });
        mnEdit.add(mntmCopy);

        JMenuItem mntmPaste = new JMenuItem("Paste                                   Ctrl+V");
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
        mntmDelete.addActionListener(e -> {
            synchronized (textArea) {
                textArea.replaceSelection("");
            }
        });
        mnEdit.add(mntmDelete);
        
        monitor = new ButtonMonitor(mntmCut, mntmCopy, mntmDelete, mntmUndo, mntmRedo, textArea, undo);
        monitor.monitorThread.start();


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

        textArea.getInputMap().put(KeyStroke.getKeyStroke("control N"), "newFile");
         textArea.getActionMap().put("newFile", new AbstractAction() {
	         	public void actionPerformed(ActionEvent e) {
	         		mntmNew.doClick();
	         	}
        });
         
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control O"), "openFile");
          textArea.getActionMap().put("openFile", new AbstractAction() {
	          	public void actionPerformed(ActionEvent e) {
	          		mntmOpen.doClick();
	          	}
        });
          
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control S"), "saveFile");
           textArea.getActionMap().put("saveFile", new AbstractAction() {
	           	public void actionPerformed(ActionEvent e) {
	           		mntmSave.doClick();
	           	}
        });
        
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control shift S"), "saveasFile");
           textArea.getActionMap().put("saveasFile", new AbstractAction() {
            	public void actionPerformed(ActionEvent e) {
            		mntmSaveAs.doClick();
            	}
        });
           
        textArea.getActionMap().put("Undo",
                   new AbstractAction("Undo") {
                       public void actionPerformed(ActionEvent evt) {
                           mntmUndo.doClick();
                       }
           });
           //UNDO SHORTCUT (CONTROL + Z)
           textArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

           //REDO
           textArea.getActionMap().put("Redo",
                   new AbstractAction("Redo") {
                       public void actionPerformed(ActionEvent evt) {
                           mntmRedo.doClick();
                       }
                   });
           //REDO SHORTCUT (CONTROL + Y)
           textArea.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        
    }

    private static void closeIfyes(@NotNull JFileChooser chooser) {
        if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt"))
            openedfile = new File(chooser.getSelectedFile().getAbsolutePath() + ".txt");
        else
            openedfile = chooser.getSelectedFile();
        try {
            if (openedfile.createNewFile()){
                String writeString = textArea.getText();
                FileWriter writer = new FileWriter(openedfile);
                writer.write(writeString);
                writer.close();
            }
        }catch (IOException error) {
            openedfile = null;
            error.printStackTrace();
        }
    }


    /**
     * <p>
     *     Closes the editor and stops the running threads
     * <p/>
     */
    private void closeEditor() {
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
	
	                        }catch (IOException error) {
	                            error.printStackTrace();
	                        }
	                    }
	                    if(saver.saveThread.isAlive()){
	                        saver.stop();
	                    }
	                    if(monitor.monitorThread.isAlive())
	                    	monitor.stop();
	                    
	                    frame.setVisible(false);
	                    frame.dispose();
	                }
	            }
	            else {
	            	if(saver.saveThread.isAlive()){
	                    saver.stop();
	                }
	            	if(monitor.monitorThread.isAlive())
                    	monitor.stop();
                    
	                frame.setVisible(false);
	                frame.dispose();
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
	
	                                if(saver.saveThread.isAlive()){
	                                    saver.stop();
	                                }
	                                if(monitor.monitorThread.isAlive())
	        	                    	monitor.stop();
	                                frame.setVisible(false);
	                                frame.dispose();
	                            }
	                        }catch (IOException error) {
	                            openedfile = null;
	                            error.printStackTrace();
	                        }
	                    }
	                }
	                else {
	                	if(monitor.monitorThread.isAlive())
	                    	monitor.stop();
	                    
	                    frame.setVisible(false);
	                    frame.dispose();
	                }
	            }
	        }
	        else {
	        	if(monitor.monitorThread.isAlive())
                	monitor.stop();
                
	            frame.setVisible(false);
	            frame.dispose();
	        }
	    }
	}

    /**
     * Opens a file using an explorer window
     */
    private void openFile() {
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
                synchronized (textArea) {
                textArea.setText(readString.toString());
                }
                reader.close();
                if (saver.saveThread.isAlive()) {
                	saver.stop();
                }
                saver = new Saver(openedfile,textArea);
                saver.saveThread.start();
            }catch (IOException error) {
                error.printStackTrace();
            }
        }
    	
    }

    /**
     * <p>
     *     Sets the spell checker options
     * @see <a href="http://jortho.sourceforge.net/">JOrtho library documentation </a>
     * </p>
     * */
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
