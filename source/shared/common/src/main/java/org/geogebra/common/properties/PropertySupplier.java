package org.geogebra.common.properties;

/**
 * Supplier of properties.
 */
public interface PropertySupplier {
	/**
	 * Update the internal state, may change the wrapped property.
	 * @return the current property
	 */
	Property updateAndGet();

	/**
	 * @return the current property
	 */
	Property get();
}
