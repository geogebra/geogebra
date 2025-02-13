/* 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.desktop.gui.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geogebra.common.util.ManualPage;
import org.geogebra.desktop.main.AppD;

/**
 * Action that opens GeoGebraWiki at specified article.
 * 
 * @author Zbynek
 *
 */
public class HelpAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private final AppD app;
	private final ManualPage articleName;

	/**
	 * @param app application
	 * @param icon icon
	 * @param name name
	 * @param articleName help article name
	 */
	public HelpAction(AppD app, ImageIcon icon, String name,
			ManualPage articleName) {
		super(name, icon);
		this.app = app;
		this.articleName = articleName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread runner = new Thread() {
			@Override
			public void run() {
				app.getGuiManager().openHelp(articleName, null);
			}
		};
		runner.start();
	}

}
