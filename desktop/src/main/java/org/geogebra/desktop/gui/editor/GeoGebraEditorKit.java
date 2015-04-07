/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.ViewFactory;

import org.geogebra.desktop.main.AppD;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class GeoGebraEditorKit extends DefaultEditorKit {

	private static final long serialVersionUID = 1L;

	/**
	 * The mimetype for a GeoGebra code
	 */
	public static final String MIMETYPE = "text/geogebra";

	private GeoGebraContext preferences;
	private AppD app;

	/**
	 * 
	 * @param app
	 *            the Application where this kit is used
	 */
	public GeoGebraEditorKit(AppD app) {
		this.app = app;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContentType() {
		return MIMETYPE;
	}

	/**
	 * @return the context associated with the ScilabDocument
	 */
	public GeoGebraContext getStylePreferences() {
		if (preferences == null) {
			preferences = new GeoGebraContext(app);
		}

		return preferences;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewFactory getViewFactory() {
		return getStylePreferences();
	}
}
