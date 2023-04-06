package org.geogebra.common.properties;

/**
 * A listener for value changes emitted by a {@link ValuedProperty}.
 */
public interface PropertyValueObserver<V> {

    /**
     * Called when the property value did change.
     *
     * @param property property
     */
    void onChange(ValuedProperty<V> property);

    /**
     * Called when the property value starts changing.
     * For more information see {@link ValuedProperty#startChangingValue()}.
     *
     * @param property property
     */
    void onStartChanging(ValuedProperty<V> property);

    /**
     * Called when the property value ends changing.
     * For more information see {@link ValuedProperty#startChangingValue()}.
     *
     * @param property property
     */
    void onEndChanging(ValuedProperty<V> property);
}
