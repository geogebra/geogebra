package geogebra.gui.app;

import geogebra.common.main.App;
import geogebra.main.AppD;

import java.io.File;

public class MacApplicationListener implements com.apple.eawt.ApplicationListener  {
	
	public static void initMacApplicationListener() {
		com.apple.eawt.Application app = new com.apple.eawt.Application();
		app.addApplicationListener(new MacApplicationListener());
	}
	
	/**
	 * Gets active instance of GeoGebra window. This method waits
	 * until an active instance was created by GeoGebra.main()
	 * @return
	 */
	private synchronized static GeoGebraFrame getGGBInstance() {
		GeoGebraFrame wnd = null;
		while (wnd == null) {
			try {
				Thread.sleep(100);			
				wnd = GeoGebraFrame.getActiveInstance();	
			} catch (Exception e) {
				App.debug("MacApplicationListener.getGGBInstance(): " + e.getMessage());
				wnd = null;
			}
		}
		return wnd;
	}
	
	public synchronized void handleQuit(com.apple.eawt.ApplicationEvent ev) {
		// quit all frames
		AppD app = getGGBInstance().getApplication();					
		app.exitAll();	
	}			
						
	public synchronized void handleAbout(com.apple.eawt.ApplicationEvent event) {
		 event.setHandled(true);
         AppD app = getGGBInstance().getApplication();	
         app.getGuiManagerD().showAboutDialog();
     }

	public synchronized void handleOpenFile(com.apple.eawt.ApplicationEvent ev) {	
		App.debug("handleOpenFile event, filename: " + ev.getFilename());
		
		// open file			
		String fileName = ev.getFilename();		
											
		if (fileName != null) {				
			File openFile = new File(fileName);
			if (openFile.exists()) {
				// get application instance
				GeoGebraFrame ggb = getGGBInstance();
				AppD app = ggb.getApplication();
				
				// open file 
				File [] files = { openFile };
				// #1541
				boolean openInThisWindow = app.isSaved();
				app.getGuiManagerD().doOpenFiles(files, openInThisWindow);
				
				// make sure window is visible
				if (openInThisWindow)
					ggb.setVisible(true);							
			}
		}
	}
	
	public synchronized void handlePrintFile(com.apple.eawt.ApplicationEvent event) {
		App.debug("handlePrintFile event, filename: " + event.getFilename());
		
		handleOpenFile(event);
		getGGBInstance().getApplication().getGuiManagerD().showPrintPreview();
	}

	public synchronized void handleOpenApplication(com.apple.eawt.ApplicationEvent ev) {
		App.debug("handleOpenApplication event, filename: " + ev.getFilename());
		
		// open file			
		String fileName = ev.getFilename();		
		if (fileName != null) {
			handleOpenFile(ev);
		} else {
			GeoGebraFrame wnd = getGGBInstance();
			if (!wnd.isShowing())
				wnd.setVisible(true);
		}
	}

	public synchronized void handlePreferences(com.apple.eawt.ApplicationEvent arg0) {
		App.debug("handlePreferences event, filename: " + arg0.getFilename());
	}

	public synchronized void handleReOpenApplication(com.apple.eawt.ApplicationEvent arg0) {
		App.debug("handleReOpenApplication event, filename: " + arg0.getFilename());
	}
		
}
