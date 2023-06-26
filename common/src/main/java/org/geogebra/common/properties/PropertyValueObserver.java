package org.geogebra.common.properties;

/**
 * A listener for value sets emitted by a {@link ValuedProperty}.
 */
public interface PropertyValueObserver<V> {

    /**
     * Called when the property value was set.
     *
     * @param property property
     */
    void didSet(ValuedProperty<V> property);

    /**
     * Called when the property will have it's value set multiple times.
     * For more information see {@link ValuedProperty#startSettingValue()}.
     *
     * @param property property
     */
    void onStartSetting(ValuedProperty<V> property);

    /**
     * Called when the property value ends changing.
     * For more information see {@link ValuedProperty#startSettingValue()}.
     *
     * @param property property
     */
    void onEndSetting(ValuedProperty<V> property);
}
