package geogebra.gui.virtualkeyboard;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class RemoveMaxAndMinButton extends JDialog{
	  public RemoveMaxAndMinButton(JFrame frame, String str){
	    super(frame,str);
	    addWindowListener(new WindowAdapter(){
	            public void windowClosing(WindowEvent evt){
	        System.exit(0);
	            }
	        });
	    
	  }
	  
	  public void setLabels() {
		  
	  }
}