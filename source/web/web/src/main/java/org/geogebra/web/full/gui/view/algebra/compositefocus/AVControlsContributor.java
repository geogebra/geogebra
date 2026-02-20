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

import static org.geogebra.web.full.gui.view.algebra.compositefocus.FocusPartAdder.addIfExists;

import java.util.Set;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItemFocusAccess;
import org.gwtproject.user.client.ui.Widget;

public final class AVControlsContributor implements FocusContributor {
	public static final String AV_OUTPUT_FORMAT_KEY = "av.output.format";
	public static final String AV_MORE_KEY = "av.more";

	@Override
	public void contribute(RadioTreeItemFocusAccess item, FocusableCompositeW focus,
			AccessibilityManagerInterface am) {
		if (item.isInputItem()) {
			return;
		}
		Widget moreButton = item.moreButton();
		Widget formatButton = item.outputFormatButton();

		Localization loc = item.geo().getApp().getLocalization();
		String formatLabelKey = getNextFormatLabelKey(item);
		addIfExists(focus, am, formatButton, AV_OUTPUT_FORMAT_KEY,
				() -> formatLabelKey.isEmpty() ? "" : loc.getMenu(formatLabelKey));

		addIfExists(focus, am, moreButton, AV_MORE_KEY,
				() -> loc.getMenu("more"));
	}

	private static String getNextFormatLabelKey(RadioTreeItemFocusAccess item) {
		GeoElement geo = item.geo();

		AlgebraSettings algebraSettings = geo.getApp().getSettings().getAlgebra();
		boolean engineering = algebraSettings.isEngineeringNotationEnabled();
		Set<AlgebraOutputFormatFilter> outputFormatFilters =
				algebraSettings.getAlgebraOutputFormatFilters();
		AlgebraOutputFormat nextFormat =
				AlgebraOutputFormat.getNextFormat(geo, engineering,
						outputFormatFilters);
		return nextFormat != null ? nextFormat.getScreenReaderLabel() : "";
	}

}
