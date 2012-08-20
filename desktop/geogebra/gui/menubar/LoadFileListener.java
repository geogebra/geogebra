package geogebra.gui.menubar;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class LoadFileListener implements ActionListener {

		private AppD app;
		private File file;
		
		public LoadFileListener(AppD app, File file) {
			this.app = app;
			this.file = file;
		}

		public void actionPerformed(ActionEvent e) {
			if (file.exists()) {	        			    				        
				// standard GeoGebra file
    			GeoGebraFrame inst = GeoGebraFrame.getInstanceWithFile(file);
    			if (inst == null) {        
    				if (app.isSaved() || app.saveCurrentFile()) {
    					// open file in application window		        				
    					app.getGuiManagerD().loadFile(file, false);
    				}
    			} else {		        				    				
    				// there is an instance with this file opened
    				inst.requestFocus();
    			}    			
    		}		
		}
	}
