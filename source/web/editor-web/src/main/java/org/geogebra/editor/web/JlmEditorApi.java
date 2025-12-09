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

package org.geogebra.editor.web;

import org.gwtproject.dom.client.Element;

import com.himamis.retex.renderer.web.JlmApi;

import jsinterop.annotations.JsType;

@JsType
public class JlmEditorApi extends JlmApi {

	@SuppressWarnings("unusable-by-js")
	public JlmEditorApi(JlmEditorLib library) {
		super(library);
	}

	/**
	 * Create an editor in specific element.
	 * @param element element
	 */
	public void edit(Element element) {
		((JlmEditorLib) this.library).edit(element);
	}

}
