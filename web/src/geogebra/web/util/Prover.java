package geogebra.web.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implements web dependent parts of the Prover 
 */
public class Prover extends geogebra.common.util.Prover {
	
	public void compute() {
		decideStatement();
	}

}
