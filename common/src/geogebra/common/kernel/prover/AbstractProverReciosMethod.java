package geogebra.common.kernel.prover;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.util.Prover;
import geogebra.common.util.Prover.NDGCondition;
import geogebra.common.util.Prover.ProofResult;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A prover which uses Tomas Recios method to prove geometric theorems.
 * 
 * @author Simon Weitzhofer
 *
 */
public abstract class AbstractProverReciosMethod {
	
	
	private static GeoElement[] fixedPoints;
	
	
	/**
	 * The prover which tries to prove the statement with the help of Tomas Recios method.
	 * @param prover the prover input object 
	 * @return The result of the prove.
	 */
	public ProofResult prove(Prover prover){

		SymbolicParameters s;

		if (prover.getStatement() instanceof SymbolicParametersAlgo)
			s = (((SymbolicParametersAlgo)prover.getStatement()).getSymbolicParameters());
		else if (prover.getStatement().getParentAlgorithm() instanceof SymbolicParametersAlgo)
			s = (((SymbolicParametersAlgo)prover.getStatement().getParentAlgorithm()).getSymbolicParameters());
		else return ProofResult.UNKNOWN;
		
		HashSet<Variable> variables;
		
		try {
			variables = s.getFreeVariables();
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		
		// setting two points fixed (the first to (0,0) and the second to (0,1))
		// all other variables are stores in freeVariables
		Iterator<Variable> it=variables.iterator();
		HashMap<Variable, BigInteger> values=new HashMap<Variable, BigInteger>();
		TreeSet<Variable> fixedVariables=new TreeSet<Variable>(new Comparator<Variable>(){
			public int compare(Variable v1, Variable v2) {
				String nameV1, nameV2;
				if (v1.getParent()==null || (nameV1=v1.getParent().getLabel(StringTemplate.defaultTemplate))==null){
					if (v2.getParent()==null || v1.getParent().getLabel(StringTemplate.defaultTemplate)==null){
						return v1.compareTo(v2);
					}
					return -1;
				}
				if (v2.getParent()==null || (nameV2=v2.getParent().getLabel(StringTemplate.defaultTemplate))==null){
					return 1;
				}
				int compareNames=nameV1.compareTo(nameV2);
				if (compareNames==0){
					return v1.compareTo(v2);
				}
				return compareNames;
			}	
		});
		HashSet<Variable> freeVariables=new HashSet<Variable>();
		while(it.hasNext()){
			Variable fv=it.next();
			if (fv.getTwin()==null || !variables.contains(fv.getTwin())){
				freeVariables.add(fv);
				continue;
			}
			fixedVariables.add(fv);
		}
		
		it = fixedVariables.iterator();
		int nrOfFixedPoints=0;
		GeoElement fixedElement1=null, fixedElement2=null;
		while (it.hasNext()){
			Variable var;
			if (nrOfFixedPoints==0){
				var = it.next();
				values.put(var, BigInteger.ZERO);
				values.put(it.next(), BigInteger.ZERO);
				fixedElement1=var.getParent();
				nrOfFixedPoints=1;
			} else if (nrOfFixedPoints==1){
				var = it.next();
				values.put(var, BigInteger.ZERO);
				values.put(it.next(), BigInteger.ONE);
				fixedElement2=var.getParent();
				nrOfFixedPoints=2;
			} else {
				freeVariables.add(it.next());
			}
		}
		
		if (nrOfFixedPoints==1){
			fixedPoints=new GeoElement[1];
			fixedPoints[0]=fixedElement1;
		} else if (nrOfFixedPoints==2){
			fixedPoints=new GeoElement[2];
			fixedPoints[0]=fixedElement1;
			fixedPoints[1]=fixedElement2;
		}
		
		int nrFreeVariables=freeVariables.size();

		/* We always set the NDG condition if there are
		 * exactly two fixed points. I hope this is OK now
		 * (reported by Noel Lambert as a bug).
		 */
		if (nrOfFixedPoints==2 /* && nrFreeVariables<=2 */){
			NDGCondition ndg = new NDGCondition();
			ndg.setCondition("AreEqual");
			GeoElement[] geos = { fixedElement1, fixedElement2 };
			ndg.setGeos(geos);
			prover.addNDGcondition(ndg);
		}
		
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

		switch (nrFreeVariables) {
			case 0:
				return compute0d(values, s);
			case 1:
				return compute1d(freeVariables,values,deg,s);
			case 2:
 				return compute2d(freeVariables, values, deg, s);
			default:
				return computeNd(freeVariables, values, deg, s);
		}

	}

	private static ProofResult compute0d(HashMap<Variable, BigInteger> values,
			SymbolicParameters s) {
		try {
			BigInteger[] exactCoordinates = s.getExactCoordinates(values);
			for (BigInteger result : exactCoordinates) {
				if (!result.equals(BigInteger.ZERO)) {
					return ProofResult.FALSE;
				}
			}
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		return ProofResult.TRUE;
	}

	private static ProofResult compute1d(
			final HashSet<Variable> freeVariables,
			final HashMap<Variable, BigInteger> values, final int deg,
			final SymbolicParameters s) {
		Variable variable = freeVariables.iterator().next();
		for (int i = 1; i <= deg + 2; i++) {
			values.put(variable, BigInteger.valueOf(i));
			try {
				BigInteger[] exactCoordinates = s.getExactCoordinates(values);
				for (BigInteger result : exactCoordinates) {
					if (!result.equals(BigInteger.ZERO)) {
						return ProofResult.FALSE;
					}
				}
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		}
		return ProofResult.TRUE;
	}
	
	private static ProofResult compute2d(final HashSet<Variable> freeVariables,
			final HashMap<Variable, BigInteger> values, final int deg,
			final SymbolicParameters s) {
		Variable[] variables = new Variable[freeVariables.size()];
		Iterator<Variable> it = freeVariables.iterator();
		for (int i = 0; i < variables.length; i++) {
			variables[i] = it.next();
		}

		int nrOfTests = ((deg + 2) * (deg + 1)) / 2;
		App.debug("nr of tests: " + nrOfTests);
		for (int i = 1; i <= deg + 2; i++) {
			for (int j = 1; j <= i; j++) {
				values.put(variables[0],
						BigInteger.valueOf((deg + 2 - i) * (deg + 2 - j)));
				values.put(variables[1], BigInteger.valueOf(i * j));
				try {
					BigInteger[] exactCoordinates = s
							.getExactCoordinates(values);
					for (BigInteger result : exactCoordinates) {
						if (!result.equals(BigInteger.ZERO)) {
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
	
	/**
	 * More complicated calculations are done by multiple threads
	 * in desktop
	 * 
	 * @param freeVariables The free variables ruling the construction
	 * @param values The values for the fixed variables (If e.g. one point gets fixed coordinates (0,0) and another (0,1)
	 * @param deg The bound for the degree of the statement
	 * @param s The Symbolic parameters class that is used to test the statement for a fixed point 
	 * @return the result of the proof
	 */
	
	protected abstract ProofResult computeNd(final HashSet<Variable> freeVariables,
			final HashMap<Variable, BigInteger> values, final int deg,
			final SymbolicParameters s);

	/**
	 * Returns the elements which are fixed by Recio's method prover
	 * @return the fixed elements
	 */
	public static GeoElement[] getFixedPoints() {
		return fixedPoints;
	}
	
}
