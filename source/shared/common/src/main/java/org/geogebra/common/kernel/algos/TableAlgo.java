package org.geogebra.common.kernel.algos;

/**
 * Tagging interface for LaTeX tables
 */
public interface TableAlgo {
	default boolean isTransposed() {
		return false;
	}
}
