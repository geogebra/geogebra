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

package org.geogebra.desktop.gui.view.algebra;

import javax.swing.tree.DefaultMutableTreeNode;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.IndexHTMLBuilder;

public class GeoMutableTreeNode extends DefaultMutableTreeNode {
	private final GeoElement geo;
	private String algebraDescription;

	/**
	 * @param geo construction element
	 */
	public GeoMutableTreeNode(GeoElement geo) {
		super(geo);
		this.geo = geo;
	}

	/**
	 * @return algebra description (label [: or =] value)
	 */
	public String getAlgebraDescription() {
		if (algebraDescription == null) {
			algebraDescription = ((GeoElement) getUserObject())
					.getAlgebraDescriptionTextOrHTMLDefault(new IndexHTMLBuilder(true));
		}
		return algebraDescription;
	}

	/**
	 * Reset the description.
	 */
	public void reset() {
		algebraDescription = null;
	}

	public GeoElement getGeo() {
		return geo;
	}
}
