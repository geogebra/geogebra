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
// This code has been written initially for Scilab (http://www.scilab.org/).

package org.geogebra.desktop.gui.editor;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.ViewFactory;

import org.geogebra.desktop.main.AppD;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class LaTeXEditorKit extends DefaultEditorKit {

	private static final long serialVersionUID = 1L;

	/**
	 * The mimetype for a LaTeX code
	 */
	public static final String MIMETYPE = "text/latex";

	private LaTeXContext preferences;
	private AppD app;

	/**
	 * Default Constructor
	 * 
	 * @param app
	 *            the Application where this kit is used
	 */
	public LaTeXEditorKit(AppD app) {
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
	public LaTeXContext getStylePreferences() {
		if (preferences == null) {
			preferences = new LaTeXContext(app);
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
