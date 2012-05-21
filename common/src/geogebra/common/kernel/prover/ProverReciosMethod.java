package geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.prover.Prover.ProofResult;
import geogebra.common.main.AbstractApplication;

/**
 * A prover which uses Tomas Recios method to prove geometric theorems.
 * 
 * @author Simon Weitzhofer
 *
 */
public class ProverReciosMethod {
	/**
	 * The prover which tries to prove the statement with the help of Tomas Recios method.
	 * @param prover the prover input object 
	 * @return The result of the prove.
	 */
	public static ProofResult prove(Prover prover){

		SymbolicParameters s;

		if (prover.statement instanceof SymbolicParametersAlgo)
			s = (((SymbolicParametersAlgo)prover.statement).getSymbolicParameters());
		else if (prover.statement.getParentAlgorithm() instanceof SymbolicParametersAlgo)
			s = (((SymbolicParametersAlgo)prover.statement.getParentAlgorithm()).getSymbolicParameters());
		else return ProofResult.UNKNOWN;
		
		int[] degs;
		try {
			degs = s.getDegrees();
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		int deg=0;
		for (int i:degs){
			deg=Math.max(deg, i);
		}
		
		HashSet<Variable> variables;
		
		try {
			variables = s.getFreeVariables();
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		
		// setting two points fixed (the first to (0,0) and the second to (0,1))
		// all other variables are stores in freeVariables
		Iterator<Variable> it=variables.iterator();
		Variable firstFixedVariable=null, secondFixedVariable=null;
		HashMap<Variable, BigInteger> values=new HashMap<Variable, BigInteger>();
		HashSet<Variable> freeVariables=new HashSet<Variable>();
		while(it.hasNext()){
			Variable fv=it.next();
			if (firstFixedVariable==null){
				if (fv.getTwin()==null){
					freeVariables.add(fv);
					continue;
				}
				firstFixedVariable=fv;
				values.put(fv, BigInteger.ZERO);
			} else if (firstFixedVariable.getTwin().equals(fv)){
				values.put(fv, BigInteger.ZERO);
			} else if (secondFixedVariable==null){
				if (fv.getTwin()==null){
					freeVariables.add(fv);
					continue;
				}
				secondFixedVariable=fv;
				values.put(fv, BigInteger.ZERO);
			} else if (secondFixedVariable.getTwin().equals(fv)){
				values.put(fv, BigInteger.ONE);
			} else {
				freeVariables.add(fv);
			}
		}
		int nrFreeVariables=freeVariables.size();
		
		switch (nrFreeVariables) {
			case 0:
				return compute0d(values, s);
			case 1:
				return compute1d(freeVariables,values,deg,s);
			case 2:
				return compute2d(freeVariables, values, deg, s);
			default:
				return ProofResult.UNKNOWN;
		}

	}

	private static ProofResult compute0d(
			HashMap<Variable, BigInteger> values, SymbolicParameters s) {
		try {
			BigInteger[] exactCoordinates=s.getExactCoordinates(values);
			for (BigInteger result:exactCoordinates){
				if (!result.equals(BigInteger.ZERO)){
					return ProofResult.FALSE;
				}
			}
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		return ProofResult.TRUE;
	}

	private static ProofResult compute1d(final HashSet<Variable> freeVariabless, final HashMap<Variable, BigInteger> values, final int deg, final SymbolicParameters s) {
		Variable variable=freeVariabless.iterator().next();
		for (int i=1; i<=deg+2;i++){
			values.put(variable, BigInteger.valueOf(i));
			try {
				BigInteger[] exactCoordinates=s.getExactCoordinates(values);
				for (BigInteger result:exactCoordinates){
					if (!result.equals(BigInteger.ZERO)){
						return ProofResult.FALSE;
					}
				}
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		}
		return ProofResult.TRUE;
	}
	
	private static ProofResult compute2d(final HashSet<Variable> freeVariabless,
			final HashMap<Variable, BigInteger> values, final int deg, final SymbolicParameters s) {
		Variable[] variables=new Variable[freeVariabless.size()];
		Iterator<Variable> it=freeVariabless.iterator();
		for (int i=0;i<variables.length;i++){
			variables[i]=it.next();
		}
		
		int nrOfTests=((deg+2)*(deg+1))/2;
		AbstractApplication.debug("nr of tests: "+nrOfTests);
		for (int i=1; i<=deg+2;i++){
			for (int j=1; j<=i;j++){
				values.put(variables[0], BigInteger.valueOf((deg+2-i)*(deg+2-j)));
				values.put(variables[1], BigInteger.valueOf(i*j));
				try {
					BigInteger[] exactCoordinates=s.getExactCoordinates(values);
					for (BigInteger result:exactCoordinates){
						if (!result.equals(BigInteger.ZERO)){
							return ProofResult.FALSE;
						}
					}
				} catch (NoSymbolicParametersException e) {
					return ProofResult.UNKNOWN;
				}
			}
		}
		return ProofResult.TRUE;

	}

}
