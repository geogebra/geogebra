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

import javax.annotation.CheckForNull;

/**
 * A property whose values have icons associated with.
 */
public interface IconsEnumeratedProperty<V> extends EnumeratedProperty<V> {

    /**
     * Returns an array with the icon resources. The identifiers are usually
     * tied to a specific property.
     *
     * @return an array of identifiers
     */
    PropertyResource[] getValueIcons();

    /**
     * Returns an array of labels with same length as
     * {@link IconsEnumeratedProperty#getValueIcons()}, used as aria-title and data-title.
     * It is defined only in specific cases, null otherwise.
     * @return an array of labels
     */
    @CheckForNull String[] getToolTipLabels();
}
