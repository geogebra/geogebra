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

package org.geogebra.desktop.gui.dialog;

import java.awt.Frame;

import javax.swing.JDialog;

import org.geogebra.desktop.main.AppD;

public class Dialog extends JDialog {

	public Dialog() {
		super();
	}

	public Dialog(Frame owner) {
		super(owner);
	}

	public Dialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public Dialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	@Override
	public void setResizable(boolean resizable) {
		// On Mac OS Big Sur resizable dialogs become tabs
		// and freeze the screen. See APPS-2581
		super.setResizable(resizable && !AppD.isMacOsBigSurOrLater());
	}
}
