package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.main.AppD;

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
				if (app.macsandbox) {
					// show the file dialog window and open file in application
					// window
					((GuiManagerD) app.getGuiManager()).openFile(file);
				} else {
					if (app.isSaved() || app.saveCurrentFile()) {
						// open file in application window
						((GuiManagerD) app.getGuiManager()).loadFile(file,
								false);
					}
				}
			} else {
				// there is an instance with this file opened
				inst.requestFocus();
			}
		}
	}
}
