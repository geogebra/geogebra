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

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import static org.geogebra.common.main.settings.AlgebraStyle.*;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

/**
 * Creates the {@link FocusContributor}s needed to build composite focus parts for an algebra
 * view item.
 *
 * <p>The selected contributors depend on the current {@link AlgebraStyle} and whether the item
 * should expose a single or dual representation (definition and value). Control contributors
 * (e.g. format and more) are always included.</p>
 */
public final class AVFocusContributorFactory {

	private AVFocusContributorFactory() {
		// utility class
	}

	/**
	 * Creates contributors for the given item using the current algebra style.
	 *
	 * @param item the algebra item to build focus contributors for
	 * @return contributors defining which focusable parts to add for this item
	 */
	public static List<FocusContributor> forItem(RadioTreeItem item) {
		return forItem(item, item.getGeo().getApp().getSettings().getAlgebra().getStyle());
	}

	/**
	 * Creates contributors for the given item and algebra style.
	 *
	 * @param item the algebra item to build focus contributors for
	 * @param style the algebra style determining how the item content is presented
	 * @return contributors defining which focusable parts to add for this item
	 */
	public static List<FocusContributor> forItem(RadioTreeItem item, AlgebraStyle style) {
		List<FocusContributor> list = new ArrayList<>();
		AccessibleContentProvider labels = new AccessibleContentProvider(item.getGeo(), style);
		if (shouldUseDualRepresentation(style, item)) {
			list.add(new AVDualRepresentationContributor(labels));
		} else {
			list.add(new AVSingleRepresentationContributor(labels));
		}

		list.add(new AVControlsContributor());
		return list;
	}

	private static boolean shouldUseDualRepresentation(AlgebraStyle style, RadioTreeItem item) {
		return DEFINITION_AND_VALUE.equals(style) || LINEAR_NOTATION.equals(style)
				|| (DESCRIPTION.equals(style) && item.shouldBuildItemWithTwoRows());
	}
}
