package org.geogebra.common.properties;

/**
 * This object can be registered to an ObservableProperty, and in turn will receive
 * changes to the property.
 */
public interface PropertyObserver {

    /**
     * Called when the property has changed.
     *
     * @param property property
     */
    void onChange(Property property);

    /**
     * Called when the property starts changing.
     *
     * @param property property
     */
    void onStartChange(Property property);

    /**
     * Called when the property ends changing.
     *
     * @param property property
     */
    void onEndChange(Property property);
}
