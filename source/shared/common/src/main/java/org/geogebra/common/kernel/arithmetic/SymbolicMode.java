package org.geogebra.common.kernel.arithmetic;

/**
 * Modes for resolving variables and evaluating commands
 * 
 * @author Zbynek
 *
 */
public enum SymbolicMode {
	/** no undefined vars allowed */
	NONE,

	/** CAS view: dummy variables */
	SYMBOLIC,

	/** AV: dummy variables, GeoSymbolic */
	SYMBOLIC_AV
}
