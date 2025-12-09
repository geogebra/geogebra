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

package org.geogebra.web.full.gui.view.consprotocol;

import org.gwtproject.cell.client.AbstractCell;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.gwtproject.text.shared.SafeHtmlRenderer;
import org.gwtproject.text.shared.SimpleSafeHtmlRenderer;

/**
 * Based on GWT's NumberCell (Apache 2.0 license). Does not use GWT's NumberFormat to avoid
 * duplicate code.
 */
public class IntegerCell extends AbstractCell<Integer> {

	/**
	 * The {@link SafeHtmlRenderer} used to render the formatted number as HTML.
	 */
	private final SafeHtmlRenderer<String> renderer;

	/**
	 * Construct a new {@link IntegerCell} using default
	 * {@link SimpleSafeHtmlRenderer}.
	 */
	public IntegerCell() {
		this.renderer = SimpleSafeHtmlRenderer.getInstance();
	}

	@Override
	public void render(Context context, Integer value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.append(renderer.render(String.valueOf(value)));
		}
	}
}
