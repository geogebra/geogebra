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
import java.util.stream.Stream;

import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoStickyNote;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.common.util.StringUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * {@code Property} responsible for setting the font size of texts within Notes,
 * using numeric values.
 */
public class NotesFontSizeProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, GeoElementDependentProperty {

	private static final List<String> SUGGESTIONS =
			List.of("12", "16", "24", "36", "48", "60", "72", "96", "120");
	private static final int DEFAULT_SIZE = 16;
	private static final int MIN_SIZE = 1;
	private static final int MAX_SIZE = 999;
	private static final int AUTO_LABEL_LENGTH = 4;

	private final GeoElementDelegate delegate;

	/**
	 * Creates a numeric font size property for Notes inline elements.
	 * @param localization localization for translating the property name
	 * @param element element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the element
	 */
	public NotesFontSizeProperty(@NonNull Localization localization, @NonNull GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "FontSize");
		this.delegate = new TextFormatterDelegate(element);
	}

	@Override
	protected void doSetValue(String value) {
		GeoElement element = getGeoElement();
		boolean automatic = isAutoValue(value);
		Integer fontSize = automatic ? null : parse(value);
		if (!automatic && (fontSize == null || !fitsAtFontSize(fontSize))) {
			return;
		}
		if (element instanceof GeoStickyNote note) {
			note.setAutoFontSize(automatic);
		}
		if (fontSize != null) {
			double layoutFontSize =
					element instanceof GeoStickyNote note ? fontSize / note.getScale() : fontSize;
			((HasTextFormatter) element).format("size", layoutFontSize);
		}
		element.updateVisualStyleRepaint(GProperty.FONT);
	}

	@Override
	public String getValue() {
		if (getGeoElement() instanceof GeoStickyNote note && note.isAutoFontSize()) {
			return getAutoLabel();
		}
		Number size = ((HasTextFormatter) getGeoElement()).getFormat("size", (Number) DEFAULT_SIZE);
		double scale = getGeoElement() instanceof GeoStickyNote note ? note.getScale() : 1;
		return String.valueOf(Math.round(size.doubleValue() * scale));
	}

	@Override
	public @Nullable String validateValue(String value) {
		return isSuggestionEnabled(value) ? null : getLocalization().getError("InvalidInput");
	}

	@Override
	public @NonNull List<String> getSuggestions() {
		return getGeoElement() instanceof GeoStickyNote
				? Stream.concat(Stream.of(getAutoLabel()), SUGGESTIONS.stream()).toList()
				: SUGGESTIONS;
	}

	@Override
	public boolean isSuggestionEnabled(@NonNull String value) {
		Integer size = parse(value);
		return isAutoValue(value) || size != null && fitsAtFontSize(size);
	}

	private boolean fitsAtFontSize(int size) {
		return !(getGeoElement() instanceof GeoStickyNote note)
				|| !(note.getFormatter() instanceof InlineTextController controller)
				|| controller.fitsAtFontSize(size / note.getScale());
	}

	private boolean isAutoValue(@Nullable String value) {
		return getGeoElement() instanceof GeoStickyNote && getAutoLabel().equals(value);
	}

	private @NonNull String getAutoLabel() {
		String automatic = getLocalization().getMenu("Automatic");
		return automatic.substring(0, Math.min(automatic.length(), AUTO_LABEL_LENGTH));
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
	public static @Nullable Integer parse(@Nullable String value) {
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
