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

import java.util.Arrays;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.AnimationModeProperty.AnimationMode;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for controlling the animation mode of a GeoElement.
 */
public class AnimationModeProperty extends AbstractEnumeratedProperty<AnimationMode> implements
		NamedEnumeratedProperty<AnimationMode> {

	/**
	 * Enumeration of available animation modes.
	 */
	public enum AnimationMode {
		OSCILLATING(GeoElement.ANIMATION_OSCILLATING, "Oscillating", '\u21d4'),
		INCREASING(GeoElement.ANIMATION_INCREASING, "Increasing", '\u21d2'),
		DECREASING(GeoElement.ANIMATION_DECREASING, "Decreasing", '\u21d0'),
		INCREASING_ONCE(GeoElement.ANIMATION_INCREASING_ONCE, "IncreasingOnce", '\u21d2'),
		RANDOM(null, "Random", '\u2928');

		private final Integer animationType;
		private final String translationKey;
		private final char unicodeIcon;

		AnimationMode(Integer animationType, String translationKey, char unicodeIcon) {
			this.animationType = animationType;
			this.translationKey = translationKey;
			this.unicodeIcon = unicodeIcon;
		}

		private static AnimationMode of(GeoElement element) {
			if (element instanceof GeoNumeric && ((GeoNumeric) element).isRandom()) {
				return RANDOM;
			}
			return Arrays.stream(AnimationMode.values())
					.filter(mode -> mode.animationType == element.getAnimationType())
					.findFirst()
					.orElse(AnimationMode.OSCILLATING);
		}

		private boolean appliesTo(GeoElement element) {
			return this != AnimationMode.RANDOM || element instanceof GeoNumeric;
		}

		private String getDisplayString(Localization localization) {
			return unicodeIcon + " " + localization.getMenu(translationKey);
		}
	}

	private final GeoElement element;

	/**
	 * Constructs an AnimationModeProperty.
	 * @param localization the localization
	 * @param element the element to control
	 * @throws NotApplicablePropertyException if element is not animatable
	 */
	public AnimationModeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Mode");
		if (!element.isAnimatable()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
		setValues(
				Arrays.stream(AnimationMode.values())
						.filter(mode -> mode.appliesTo(element))
						.collect(Collectors.toUnmodifiableList())
		);
	}

	@Override
	public String[] getValueNames() {
		return getValues().stream()
				.map(mode -> mode.getDisplayString(getLocalization()))
				.toArray(String[]::new);
	}

	@Override
	protected void doSetValue(AnimationMode value) {
		if (element instanceof GeoNumeric) {
			((GeoNumeric) element).setRandom(value == AnimationMode.RANDOM);
		}
		if (value != AnimationMode.RANDOM) {
			element.setAnimationType(value.animationType);
		}
	}

	@Override
	public AnimationMode getValue() {
		return AnimationMode.of(element);
	}
}
