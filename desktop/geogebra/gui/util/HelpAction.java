/* 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
package geogebra.gui.util;

import geogebra.main.AppD;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
/**
 * Action that opens GeoGebraWiki at specified article.
 * @author Zbynek
 *
 */
public class HelpAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private AppD app;
	private String articleName;
	public HelpAction(AppD app, ImageIcon icon, String name, String articleName){
		super(name,icon);
		this.app=app;
		this.articleName=articleName;
	}
	public void actionPerformed(ActionEvent e) {
		Thread runner = new Thread() {
			@Override
			public void run() {
				app.getGuiManagerD().openHelp(articleName);
			}
		};
		runner.start();
	}

}
