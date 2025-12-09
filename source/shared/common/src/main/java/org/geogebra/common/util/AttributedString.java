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

package org.geogebra.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * String representation that can have attributes applied to specific ranges of characters.
 */
public final class AttributedString {
    /**
     * Possible attributes that can be applied to specific ranges of characters.
     */
    public enum Attribute {
        Subscript
    }

    private final String rawValue;
    private final Map<Attribute, Set<Range>> attributes;

    /**
     * @param rawValue The raw value of the string.
     */
    public AttributedString(@Nonnull String rawValue) {
        this.rawValue = rawValue;
        this.attributes = new HashMap<>();
    }

    /**
     * Adds an attribute to the specified range of characters.
     * @param attribute The attribute to apply.
     * @param range The range of characters to which the attribute should be applied.
     * The supplied range is not checked for validity.
     */
    public void add(@Nonnull Attribute attribute, @Nonnull Range range) {
        attributes.computeIfAbsent(attribute, k -> new HashSet<>());
        attributes.get(attribute).add(range);
    }

	/**
	 * Retrieves the set of ranges where the specified attribute is applied.
	 *
	 * @param attribute The attribute to look for.
	 * @return A set of {@link Range} objects where the attribute is applied, or {@code null} if
	* the attribute is not present.
	 */
	public @CheckForNull Set<Range> getAttribute(@Nonnull Attribute attribute) {
        Set<Range> ranges = attributes.get(attribute);
        return ranges == null ? null : Collections.unmodifiableSet(ranges);
    }

	public @Nonnull String getRawValue() {
        return rawValue;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AttributedString that = (AttributedString) object;
        return Objects.equals(rawValue, that.rawValue) && Objects.equals(attributes,
                that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue, attributes);
    }
}
