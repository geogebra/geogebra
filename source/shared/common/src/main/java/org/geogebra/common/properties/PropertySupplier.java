package org.geogebra.common.properties;

/**
 * Supplier of properties.
 */
public interface PropertySupplier {
	Property updateAndGet();

	Property get();
}
