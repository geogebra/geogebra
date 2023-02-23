package org.geogebra.common.properties;

/**
 * A property that can be observed by PropertyObservers.
 */
public interface ObservableProperty extends Property {

	/**
	 * Add an observer to this property.
	 * @param observer observer
	 */
	void addObserver(PropertyObserver observer);

	/**
	 * Remove and observer from this property.
	 * @param observer observer
	 */
	void removeObserver(PropertyObserver observer);
}
