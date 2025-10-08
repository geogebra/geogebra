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
    @CheckForNull String[] getLabels();
}
