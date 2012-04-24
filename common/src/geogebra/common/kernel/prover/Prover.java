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
	public void compute() {
		/* to be implemented */
		if (statement != null) {

			String c = simplifiedXML(construction);
			AbstractApplication.debug("Construction: " + c);
			// getCASString may also be used 
			String cd = statement.getCommandDescription(StringTemplate.ogpTemplate);
			AbstractApplication.debug("Statement to prove: " + cd);

		}
		else {
			AbstractApplication.error("No statement to prove");
			result = Prover.ProofResult.UNKNOWN;
			return;
		}

		if (statement instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement;
			SymbolicParameters parameters = statementSymbolic.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
				//TODO: write here tomas prover
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
				return;
				
				//TODO: write here tomas prover
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
