package org.geogebra.common.properties;

public interface RangePropertyObserver extends PropertyObserver {

    void onChangeStarted();

    void onChangeEnded();
}
