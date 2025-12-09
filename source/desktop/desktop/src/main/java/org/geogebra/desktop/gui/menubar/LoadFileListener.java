/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * @param app application
	 * @param file file
	 */
	public LoadFileListener(AppD app, File file) {
		this.app = app;
		this.file = file;
	}

	@Override
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
