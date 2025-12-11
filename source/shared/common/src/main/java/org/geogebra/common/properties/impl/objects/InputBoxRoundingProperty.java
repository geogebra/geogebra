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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractGroupedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.InputBoxRoundingProperty.Rounding;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for configuring rounding in input boxes using
 * either decimal places or significant figures.
 */
public class InputBoxRoundingProperty extends AbstractGroupedEnumeratedProperty<Rounding> {

	private final TextProperties element;
	private final int[] decimalPlaces;
	private final int[] significantFigures;

	enum RoundingType {
		DECIMAL_PLACES, SIGNIFICANT_FIGURES
	}

	public static class Rounding {
		final int value;
		final RoundingType type;

		Rounding(int value, RoundingType type) {
			this.value = value;
			this.type = type;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Rounding)) {
				return false;
			}
			Rounding r = (Rounding) o;
			return value == r.value && type == r.type;
		}

		@Override
		public int hashCode() {
			return Objects.hash(value, type);
		}
	}

	/**
	 * Creates a rounding property for the given element.
	 * @param localization the localization
	 * @param element the element
	 * @throws NotApplicablePropertyException if the element doesn't support rounding
	 */
	public InputBoxRoundingProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Rounding");
		if (!isApplicable(element)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (TextProperties) element;
		this.decimalPlaces = localization.getDecimalPlaces();
		this.significantFigures = localization.getSignificantFigures();
		setNamedValues(getNamedValues());
		setGroupDividerIndices(new int[]{decimalPlaces.length});
	}

	@Override
	protected void doSetValue(Rounding value) {
		if (value.type == RoundingType.DECIMAL_PLACES) {
			element.setPrintDecimals(value.value, true);
		} else if (value.type == RoundingType.SIGNIFICANT_FIGURES) {
			element.setPrintFigures(value.value, true);
		}
		element.updateRepaint();
	}

	@Override
	public Rounding getValue() {
		if (!element.useSignificantFigures()) {
			return new Rounding(element.getPrintDecimals(), RoundingType.DECIMAL_PLACES);
		} else {
			return new Rounding(element.getPrintFigures(), RoundingType.SIGNIFICANT_FIGURES);
		}
	}

	private boolean isApplicable(GeoElement element) {
		return element instanceof TextProperties
				&& !element.isIndependent() && !element.isGeoList();
	}

	private List<Map.Entry<Rounding, String>> getNamedValues() {
		Localization localization = getLocalization();

		Stream<Map.Entry<Rounding, String>> decimals = Arrays.stream(decimalPlaces)
				.mapToObj(decimal -> {
					String key = "ADecimalPlaces";
					// zero is singular in eg French
					if (decimal == 0 && !localization.isZeroPlural()) {
						key = "ADecimalPlace";
					}
					String display = localization.getPlain(key, String.valueOf(decimal));
					return entry(new Rounding(decimal, RoundingType.DECIMAL_PLACES), display);
				});

		Stream<Map.Entry<Rounding, String>> significant =
				Arrays.stream(significantFigures).mapToObj(significantFigure -> {
					String display = localization.getPlain("ASignificantFigures",
							String.valueOf(significantFigure));
					return entry(new Rounding(significantFigure, RoundingType.SIGNIFICANT_FIGURES),
							display);
				});

		return Stream.concat(decimals, significant).collect(Collectors.toUnmodifiableList());
	}
}
