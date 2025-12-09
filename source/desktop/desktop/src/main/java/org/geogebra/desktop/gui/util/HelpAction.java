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
