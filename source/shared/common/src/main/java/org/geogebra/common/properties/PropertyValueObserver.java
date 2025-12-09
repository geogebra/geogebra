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

package org.geogebra.common.properties;

/**
 * A listener for value sets emitted by a {@link ValuedProperty}.
 */
@FunctionalInterface
public interface PropertyValueObserver<V> {

    /**
     * Called when the property value was set.
     *
     * @param property property
     */
    void onDidSetValue(ValuedProperty<V> property);

    /**
     * Called when the property will have it's value set multiple times.
     * For more information see {@link ValuedProperty#beginSetValue()}.
     *
     * @param property property
     */
    default void onBeginSetValue(ValuedProperty<V> property) {
        // empty default implementation
    }

    /**
     * Called right before value is set.
     * @param property property
     */
    default void onWillSetValue(ValuedProperty<V> property) {
        // empty default implementation
    }

    /**
     * Called when the property value ends changing.
     * For more information see {@link ValuedProperty#beginSetValue()}.
     *
     * @param property property
     */
    default void onEndSetValue(ValuedProperty<V> property) {
        // empty default implementation
    }
}
