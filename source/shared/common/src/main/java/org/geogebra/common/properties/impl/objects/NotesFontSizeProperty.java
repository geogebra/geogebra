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

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.common.util.StringUtil;

/**
 * {@code Property} responsible for setting the font size of texts within Notes,
 * using numeric values.
 */
public class NotesFontSizeProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, GeoElementDependentProperty {

	private static final List<String> SUGGESTIONS = List.of(
			"12", "16", "24", "36", "48", "60", "72", "96", "120");
	private static final int DEFAULT_SIZE = 16;
	private static final int MIN_SIZE = 1;
	private static final int MAX_SIZE = 999;

	private final GeoElementDelegate delegate;

	/**
	 * Creates a numeric font size property for Notes inline elements.
	 * @param localization localization for translating the property name
	 * @param element element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the element
	 */
	public NotesFontSizeProperty(@Nonnull Localization localization, @Nonnull GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "FontSize");
		this.delegate = new TextFormatterDelegate(element);
	}

	@Override
	protected void doSetValue(String value) {
		Integer fontSize = parse(value);
		if (fontSize == null) {
			return;
		}
		GeoElement element = getGeoElement();
		((HasTextFormatter) element).format("size", (double) fontSize);
		element.updateVisualStyleRepaint(GProperty.FONT);
	}

	@Override
	public String getValue() {
		Number size = ((HasTextFormatter) getGeoElement()).getFormat("size", (Number) DEFAULT_SIZE);
		return String.valueOf(Math.round(size.doubleValue()));
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return parse(value) == null
				? getLocalization().getError("InvalidInput") : null;
	}

	@Override
	public @Nonnull List<String> getSuggestions() {
		return SUGGESTIONS;
	}

	@Override
	public boolean restoresPreviousValueOnInvalidInput() {
		return true;
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}

	/**
	 * @param value Input text
	 * @return Limited integer font size, or {@code null} for invalid input.
	 */
	public static @CheckForNull Integer parse(@CheckForNull String value) {
		if (StringUtil.emptyTrim(value)) {
			return null;
		}
		try {
			int fontSize = (int) Math.round(Double.parseDouble(value.trim()));
			if (fontSize < MIN_SIZE) {
				return MIN_SIZE;
			}
			return Math.min(fontSize, MAX_SIZE);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}
