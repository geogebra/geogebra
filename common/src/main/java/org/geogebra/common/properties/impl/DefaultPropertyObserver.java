package org.geogebra.common.properties.impl;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyObserver;

/**
 * Default observer.
 */
public class DefaultPropertyObserver implements PropertyObserver {
    @Override
    public void onChange(Property property) {
    }

    @Override
    public void onStartChange(Property property) {
    }

    @Override
    public void onEndChange(Property property) {
    }
}
