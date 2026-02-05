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

package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.HiddenLineStyleProperty.HiddenLineStyle;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the hidden line style.
 * A line is "hidden" when, in the 3D calculator, it falls behind a plane at least partially.
 */
public class HiddenLineStyleProperty extends AbstractNamedEnumeratedProperty<HiddenLineStyle> {
	/**
	 * Possible styles for hidden lines.
	 */
	public enum HiddenLineStyle {
		INVISIBLE(EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE, "Hidden.Invisible"),
		DASHED(EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED, "Hidden.Dashed"),
		UNCHANGED(EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN, "Hidden.Unchanged");

		final int euclidianStyleConstant;
		final String transKey;

		HiddenLineStyle(int euclidianStyleConstant, String transKey) {
			this.euclidianStyleConstant = euclidianStyleConstant;
			this.transKey = transKey;
		}

		static @CheckForNull HiddenLineStyle fromEuclidianStyleConstant(
				int euclidianStyleConstant) {
			return Arrays.stream(HiddenLineStyle.values())
					.filter(lineStyle -> lineStyle.euclidianStyleConstant == euclidianStyleConstant)
					.findFirst().orElse(null);
		}
	}

	private final GeoElement geoElement;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public HiddenLineStyleProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "HiddenLineStyle");
		if (!geoElement.showLineProperties()
				|| !(geoElement.getApp().getConfig() instanceof AppConfigGraphing3D)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		setNamedValues(Arrays.stream(HiddenLineStyle.values())
				.map(hiddenLineStyle -> entry(hiddenLineStyle, hiddenLineStyle.transKey))
				.collect(Collectors.toList()));
		this.geoElement = geoElement;
	}

	@Override
	protected void doSetValue(HiddenLineStyle value) {
		geoElement.setLineTypeHidden(value.euclidianStyleConstant);
		geoElement.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public HiddenLineStyle getValue() {
		return HiddenLineStyle.fromEuclidianStyleConstant(geoElement.getLineTypeHidden());
	}
}
