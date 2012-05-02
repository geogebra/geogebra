package geogebra.common.kernel.prover;

/*
 * Prover package for GeoGebra.
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * @author Simon Weitzhofer <simon@geogebra.org>
 * 
 * Thanks to Tomas Recio, Francisco Botana, Miguel A. Abanades,
 * Sergio Arbeo, Predrag Janicic and Ivan Petrovic for their
 * kind help. And Markus, of course. ;-)
 * 
 * Tomas' and Francisco's work has partially been supported by
 * grants MTM2008-M04699-C03-03 and MTM2011-25816-C02-02.
 * 
 */

import java.util.HashSet;
import java.util.Iterator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

/**
 * Prover package for GeoGebra.
 * Allows using multiple backends for theorem proving.
 */

public class Prover {

	/**
	 * Enum list of supported prover backends for GeoGebra
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public enum ProverEngine {/**
	 * Tomas Recio's method
	 */
	RECIOS_PROVER, /**
	 * Francisco Botana's method
	 */
	BOTANAS_PROVER, /**
	 * OpenGeoProver (http://code.google.com/p/open-geo-prover/)
	 */
	OPENGEOPROVER, /**
	 * pure symbolic prover (every object is calculated symbolically, also the statements)
	 */ 
	PURE_SYMBOLIC_PROVER,
	/**
	 * Default prover (GeoGebra decides internally)
	 */
	AUTO}
	
	/**
	 * Possible results of an attempted proof
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public enum ProofResult {/**
	 * The proof is completed, the statement is generally true (with some NDG conditions)
	 */
	TRUE, /**
	 * The proof is completed, the statement is generally false
	 */
	FALSE, /**
	 * The statement cannot be proved by using the current backed within the given timeout 
	 */
	UNKNOWN}
	
	/* input */
	private int timeout = 10;
	private ProverEngine engine = ProverEngine.BOTANAS_PROVER;
	private Construction construction;
	private GeoElement statement;
	
	/* output */
	private String NDGconditions;
	private ProofResult result;

	/**
	 * Constructor for the package.
	 */
	public Prover() {}

	/**
	 * Sets the maximal time spent in the Prover for the given proof.
	 * @param timeout The timeout in seconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Sets the prover engine.
	 * @param engine The engine subsystem
	 */
	public void setProverEngine(ProverEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * Sets the GeoGebra construction as the set of the used objects
	 * in the proof. 
	 * @param construction The GeoGebra construction
	 */
	public void setConstruction(Construction construction) {
		this.construction = construction;
	}
	
	/**
	 * Sets the statement to be proven.
	 * @param root The statement to be proven
	 */
	public void setStatement(GeoElement root) {
		this.statement = root;
	}
	
	/**
	 * Starts computation of the proof, based on the defined
	 * subsystem.
	 */
	
	private HashSet<GeoElement> getFreePoints() {
		HashSet<GeoElement> freePoints = new HashSet<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint() && geo.getParentAlgorithm() == null) { // this is a free point
				freePoints.add(geo);
			}
		}
		return freePoints;
	}
	
	/** 
	 * Creates those polynomials which describe that none of 3 free points
	 * can lie on the same line. 
	 * @return the NDG polynomials (in denial form)
	 */
	private Polynomial[] create3FreePointsNeverCollinearNDG() {
		// Creating the set of free points first:
		HashSet<GeoElement> freePoints = getFreePoints();
		int setSize = freePoints.size();
		// The output will contain $\binom{n}{3}$ elements:
		Polynomial[] ret = new Polynomial[setSize * (setSize - 1) * (setSize - 2) / 6];
		int i = 0;
		// Creating the set of triplets:
		HashSet<HashSet<GeoElement>> triplets = new HashSet<HashSet<GeoElement>>();
		Iterator<GeoElement> it1 = freePoints.iterator();
		while (it1.hasNext()) {
			GeoElement geo1 = it1.next();
			Iterator<GeoElement> it2 = freePoints.iterator();
			while (it2.hasNext()) {
				GeoElement geo2 = it2.next();
				if (!geo1.isEqual(geo2)) {
					Iterator<GeoElement> it3 = freePoints.iterator();
					while (it3.hasNext()) {
						GeoElement geo3 = it3.next();
						if (!geo1.isEqual(geo3) && !geo2.isEqual(geo3)) {
							HashSet<GeoElement> triplet = new HashSet<GeoElement>();
							triplet.add(geo1);
							triplet.add(geo2);
							triplet.add(geo3);
							// Only the significantly new triplets will be processed:
							if (!triplets.contains(triplet)) {
								triplets.add(triplet);
								AbstractApplication.debug(geo1.getLabelSimple() + 
										geo2.getLabelSimple() + 
										geo3.getLabelSimple() + " should never be collinear");
								FreeVariable[] fv1 = ((SymbolicParametersAlgo)geo1).getBotanaVars();
								FreeVariable[] fv2 = ((SymbolicParametersAlgo)geo2).getBotanaVars();
								FreeVariable[] fv3 = ((SymbolicParametersAlgo)geo3).getBotanaVars();
								// Creating the polynomial for collinearity:
								Polynomial p = Polynomial.setCollinear(fv1[0], fv1[1],
										fv2[0], fv2[1], fv3[0], fv3[1]);
								// Rabinowitsch trick for prohibiting collinearity:
								ret[i] = p.multiply(new Polynomial(new FreeVariable())).subtract(new Polynomial(1));
								// FIXME: this always introduces an extra variable, shouldn't do
								i++;
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Uses a minimal heuristics to fix the first four variables to certain "easy" numbers.
	 * The first two variables (usually the coordinates of the first point) are set to 0,
	 * and the second two variables (usually the coordinates of the second point) are set to 0 and 1.
	 * @return the string of the extra polynomials (e.g. "a1,a2,b1,b2-1")
	 */
	Polynomial[] fixValues() { // TODO: this is not used yet
		HashSet<GeoElement> freePoints = getFreePoints();
		int setSize = freePoints.size();
		int retSize = 0;
		if (setSize >= 2)
			retSize = 4;
		if (retSize == 1)
			retSize = 2;
		Polynomial[] ret = new Polynomial[retSize];
		Iterator<GeoElement> it = freePoints.iterator();
		int i = 0;
		while (it.hasNext() && i<4) {
			FreeVariable[] fv = ((SymbolicParametersAlgo) it.next()).getBotanaVars();
			if (i==0) {
				ret[i] = new Polynomial(fv[0]);
				++i;
				ret[i] = new Polynomial(fv[1]);
				++i;
			}
			else {
				ret[i] = new Polynomial(fv[0]);
				++i;
				ret[i] = new Polynomial(fv[1]).subtract(new Polynomial(1));
				++i;
			}
		}
		return ret;
	}
	
	private void BotanasProver() {
		// Getting the hypotheses:
		Polynomial[] hypotheses = null;
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// AbstractApplication.debug(geo);
			if (geo instanceof SymbolicParametersAlgo) {
				try {
					Polynomial[] geoPolys = ((SymbolicParametersAlgo) geo).getBotanaPolynomials();
					if (geoPolys != null) {
						int nHypotheses = 0;
						if (hypotheses != null)
							nHypotheses = hypotheses.length;
						Polynomial[] allPolys = new Polynomial[nHypotheses + geoPolys.length];
						for (int i=0; i<nHypotheses; ++i)
							allPolys[i] = hypotheses[i];
						for (int i=0; i<geoPolys.length; ++i)
							allPolys[nHypotheses + i] = geoPolys[i];
						hypotheses = allPolys;
					}
				} catch (NoSymbolicParametersException e) {
					AbstractApplication.warn("This prover cannot give an answer, try another one");
				}
			}
		}
		try {
			// The statement polynomials. If there are more ones, then a new equation
			// system will be created and solved for each.
			Polynomial[] statements = ((SymbolicParametersAlgo) statement.getParentAlgorithm()).getBotanaPolynomials();
			// The NDG conditions (automatically created):
			Polynomial[] ndgConditions = create3FreePointsNeverCollinearNDG();
			// Fix points (heuristics):
			Polynomial[] fixValues = fixValues();
			int nHypotheses = 0;
			int nNdgConditions = 0;
			int nStatements = 0;
			int nFixValues = 0;
			if (hypotheses != null)
				nHypotheses = hypotheses.length;
			if (ndgConditions != null)
				nNdgConditions = ndgConditions.length;
			if (statements != null)
				nStatements = statements.length;
			if (fixValues != null)
				nFixValues = fixValues.length;
			
			// These polynomials will be in the equation system always:
			Polynomial[] eqSystem = new Polynomial[nHypotheses + nNdgConditions + nFixValues + 1];
			for (int j=0; j<nHypotheses; ++j)
				eqSystem[j] = hypotheses[j];
			for (int j=0; j<nNdgConditions; ++j)
				eqSystem[j + nHypotheses] = ndgConditions[j];
			for (int j=0; j<nFixValues; ++j)
				eqSystem[j + nHypotheses + nNdgConditions] = fixValues[j];
			
			boolean ans = true;
			// Solving the equation system for each polynomial of the statement:
			for (int i=0; i<nStatements && ans; ++i) {
				// Rabinowitsch trick
				Polynomial spoly = statements[i].multiply(new Polynomial(new FreeVariable())).subtract(new Polynomial(1));
				// FIXME: this always introduces an extra variable, shouldn't do
				eqSystem[nHypotheses + nNdgConditions + nFixValues] = spoly;
				if (Polynomial.solvable(eqSystem)) // FIXME: here seems NPE if SingularWS not initialized 
					ans = false;
			}
			if (ans)
				result = ProofResult.TRUE;
			else
				result = ProofResult.FALSE;
			AbstractApplication.info("BOTANAS_PROVER: this statement is " + result);
		} catch (NoSymbolicParametersException e) {
			// TODO Auto-generated catch block
			AbstractApplication.warn("This prover cannot give an answer, try another one");
		}
	}
	
	public void compute() {
		if (statement != null) {

			String c = simplifiedXML(construction);
			AbstractApplication.trace("Construction: " + c);
			// getCASString may also be used 
			String cd = statement.getCommandDescription(StringTemplate.ogpTemplate);
			AbstractApplication.debug("Statement to prove: " + cd);

		}
		else {
			AbstractApplication.error("No statement to prove");
			result = Prover.ProofResult.UNKNOWN;
			return;
		}

		if (engine == ProverEngine.BOTANAS_PROVER && !AbstractApplication.singularWS.isAvailable()) {
			setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER); 
		}
		
		if (engine == ProverEngine.BOTANAS_PROVER) {
			BotanasProver();
			return; // this will return later, now we calculate the other methods as well
		}
		
		if (statement instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement;
			SymbolicParameters parameters = statementSymbolic.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
				// TODO: write here Recio's prover
			} catch (NoSymbolicParametersException e) {
				AbstractApplication.warn("This prover cannot give an answer, try another one");
				// TODO: to implement this correctly
			}
		} else if (statement.getParentAlgorithm() instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement.getParentAlgorithm();
			/*SymbolicParameters parameters = statementSymbolic.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
			} catch (NoSymbolicParametersException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			try {
				Polynomial[] poly = statementSymbolic.getPolynomials();
				AbstractApplication.debug(poly[0]);
				if (poly.length==1 && poly[0].isZero()){
					result = Prover.ProofResult.TRUE;
				} else {
					result = Prover.ProofResult.FALSE;
				}
				AbstractApplication.info("PURE_SYMBOLIC_PROVER: this statement is " + result);
				return;
				
				// TODO: write here Recio's prover
			} catch (NoSymbolicParametersException e) {
				AbstractApplication.warn("This prover cannot give an answer, try another one");
				// TODO: to implement this correctly
			}
		}
		
		result = Prover.ProofResult.UNKNOWN;
	}

	/**
	 * Gets non-degeneracy conditions of the current proof.
	 * @return The XML output string of the NDG condition
	 */
	public String getNDGConditions() {
		return NDGconditions;
	}
	
	/**
	 * Gets the proof result
	 * @return The result (TRUE, FALSE or UNKNOWN)
	 */
	public ProofResult getProofResult() {
		return result;
	}
	
	/**
	 * If the result of the proof can be expressed by a boolean value,
	 * then it returns that value. 
	 * @return The result of the proof (true, false or null)
	 */
	public Boolean getYesNoAnswer() {
		if (result != null)
		{
			if (result == Prover.ProofResult.TRUE)
				return true;
			if (result == Prover.ProofResult.FALSE)
				return false;
		}
		return null;
	}

	/**
	 * A minimal version of the construction XML. Only elements/commands are preserved,
	 * the rest is deleted. 
	 * @param cons The construction
	 * @return The simplified XML 
	 */
	// TODO: Cut even more unneeded parts to reduce unneeded traffic between OGP and GeoGebra.
	private static String simplifiedXML(Construction cons) {
		StringBuilder sb = new StringBuilder();
		cons.getConstructionElementsXML(sb);
		return "<construction>\n" + sb.toString() + "</construction>";
	}

}
