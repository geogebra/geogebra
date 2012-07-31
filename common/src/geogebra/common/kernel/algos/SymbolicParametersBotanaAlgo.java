package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

/**
 * This interface describes the symbolic parameters of algorithms
 * for the Botana method. Based on Simon's SymbolicParametersAlgo.java.
 * @author Zoltan Kovacs
 */
public interface SymbolicParametersBotanaAlgo {
	
	/**
	 * Calculates the free variables of an object for the Botana method
	 * @param geo The corresponding GeoElement 
	 * @return array of the free variables
	 */
	public Variable[] getBotanaVars(GeoElement geo);
	
	/**
	 * Calculates the polynomials of an object for the Botana method
	 * @param geo The corresponding GeoElement
	 * @return array of the polynomials
	 * @throws NoSymbolicParametersException if it is not possible to obtain suitable polynomials
	 */
	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException;
}
