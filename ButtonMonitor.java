import javax.swing.*;
import javax.swing.undo.UndoManager;


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
    
    //JMenuItem mntmUndo;
    //JMenuItem mntmRedo;
   
    private boolean flag;

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

    @Override
    public void run() {
        while(flag){
        	
        	checkAbility();
        	checkDoability();
        	try {
				monitorThread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }

    }

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
    public void stop(){
        if(flag){
            flag = false;
            
        }
    }

    
}
