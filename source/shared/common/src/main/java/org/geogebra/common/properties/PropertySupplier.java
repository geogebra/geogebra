package org.geogebra.common.properties;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Supplier of properties.
 */
public interface PropertySupplier {
	@MissingDoc
	Property updateAndGet();

	@MissingDoc
	Property get();
}
