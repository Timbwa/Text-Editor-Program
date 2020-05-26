package com.TextEditor;

import javax.swing.*;
import javax.swing.undo.UndoManager;

/**
 * <p>
 *     A class that checks if the GUI buttons can be pressed.
 *     Makes sure text is available before doing editing functionality
 * </p>
 * @author Ahmed El Cheikh Ammar
 * @version 8, 26 May 2020
 * */
public class ButtonMonitor implements Runnable{
    Thread monitorThread;
    JTextArea textArea;
    JMenuItem mntmCut;
    JMenuItem mntmCopy;
    JMenuItem mntmDelete;
    JMenuItem mntmUndo;
    JMenuItem mntmRedo;
    UndoManager undo;
    boolean enabled = false;
    private boolean flag;

    /**
     * <p>
     *     The constructor that initializes the attributes of this class
     * </p>
     *
     * @param textArea
     *          <code>JTextArea</code> containing the text
     * @param mntmCut
     *          <code>Cut</code> menu item
     * @param mntmCopy
     *          <code>Copy</code> menu item
     * @param mntmDelete
     *          <code>Delete</code> menu item
     * @param mntmRedo
     *          <code>Redo</code> menu item
     * @param mntmUndo
     *          <code>Undo</code> menu item
     * @param undo
     *          <code>UndoManager</code> object reference
     * */
    public ButtonMonitor(JMenuItem mntmCut, JMenuItem mntmCopy, JMenuItem mntmDelete, JMenuItem mntmUndo, JMenuItem mntmRedo, JTextArea textArea, UndoManager undo) {
        this.monitorThread = new Thread(this, "monitorThread");
        this.mntmCut = mntmCut;
        this.mntmCopy = mntmCopy;
        this.mntmDelete = mntmDelete;
        this.mntmUndo = mntmUndo;
        this.mntmRedo = mntmRedo;
        this.textArea = textArea;
        this.undo = undo;
        this.flag = true;
    }

    /**
     * <p>
     *     Checks if text is available and enables buttons when necessary
     * </p>
     * @see Runnable
     * */

    @Override
    public void run() {
        while(flag){
        	checkAbility();
        	checkDoability();
        	try {
				monitorThread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

    }

    /**
     * <p>
     *     Enables or disables <code>cut, copy and delete</code> menu items
     * </p>
     * */
    private synchronized void checkAbility(){
    	synchronized(textArea) {
    	if (textArea.getSelectedText() == null) {
        	mntmCut.setEnabled(false);
        	mntmCopy.setEnabled(false);
        	mntmDelete.setEnabled(false);
        	enabled = false;
        	
    	}
    	else if (!enabled) {
    		mntmCut.setEnabled(true);
	    	mntmCopy.setEnabled(true);
	    	mntmDelete.setEnabled(true);
    		enabled = true;
    	    }
        }
    }
    /**
     * <p>
     *     Enables or disables <code>undo and redo</code> menu items
     * </p>
     * */
    private synchronized void checkDoability() {
    	if (!undo.canUndo())
    		mntmUndo.setEnabled(false);
    	else if (!mntmUndo.isEnabled())
    		mntmUndo.setEnabled(true);
    	
    	if (!undo.canRedo())
    		mntmRedo.setEnabled(false);
    	else if (!mntmRedo.isEnabled())
    		mntmRedo.setEnabled(true);  		
    }

    /**
     * <p>
     *     Stops the thread using a flag if running
     * </p>
     * */
    public void stop(){
        if(flag){
            flag = false;
        }
    }
}
