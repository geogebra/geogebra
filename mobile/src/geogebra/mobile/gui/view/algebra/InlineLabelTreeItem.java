/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.mobile.gui.view.algebra;

import geogebra.common.main.App;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * InlineLabelTreeItem for the openable tree nodes of the algebra view
 * 
 * File created by Arpad Fekete (for the web-project)
 * 
 * (shortened as handlers are not required)
 */

public class InlineLabelTreeItem extends InlineLabel 
{

	TreeItem par;
	App application;

	/**
	 * 
	 * @param app Application
	 * @param parent TreeItem
	 * @param strlab String
	 */
	public InlineLabelTreeItem(App app, TreeItem parent, String strlab)
	{
		super(strlab);
		this.application = app;
		this.par = parent;
	}

}
