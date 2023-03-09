package org.geogebra.common.properties;

/**
 * Property that has a value
 *
 * @param <S> value type
 */
public interface ValuedProperty<S> extends Property {

    /**
     * Get the property value
     */
    S getValue();

    /**
     * Set the property value
     *
     * @param value value
     */
    void setValue(S value);

    /**
     * Start changing property value.
     */
    void startChange();

    /**
     * End changing property value.
     */
    void endChange();

    /**
     * Add property value observer.
     *
     * @param observer observer
     */
    void addObserver(PropertyObserver observer);

    /**
     * Remove the property observer.
     *
     * @param observer observer
     */
    void removeObserver(PropertyObserver observer);
}
