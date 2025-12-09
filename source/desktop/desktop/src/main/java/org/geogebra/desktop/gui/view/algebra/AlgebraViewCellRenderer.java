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

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.desktop.main.AppD;

/**
 * Renderer for algebra view. Add changeable description.
 * 
 * @author mathieu
 * 
 */
public class AlgebraViewCellRenderer extends AlgebraTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 *            application
	 * @param view
	 *            view
	 */
	public AlgebraViewCellRenderer(AppD app, AlgebraTree view) {
		super(app, view);
	}

	@Override
	protected String getDescription(GeoElement geo, GeoMutableTreeNode node) {

		String text = null;
		if (geo.isIndependent() && geo.getDefinition() == null) {
			text = node.getAlgebraDescription();
		} else {
			switch (app.getAlgebraStyle()) {
			default:
			case VALUE:
				text = node.getAlgebraDescription();
				break;

			case DESCRIPTION:
				IndexHTMLBuilder builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(geo.getDefinitionDescription(
						StringTemplate.defaultTemplate), builder);
				text = builder.toString();
				break;

			case DEFINITION:
				builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(
						geo.getDefinition(StringTemplate.defaultTemplate),
						builder);
				text = builder.toString();
				break;

			}

		}

		return text;
	}
}
