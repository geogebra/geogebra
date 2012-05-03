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
	private ProverEngine engine = ProverEngine.RECIOS_PROVER;
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
		if (statement != null) {

			String c = simplifiedXML(construction);
			AbstractApplication.trace("Construction: " + c);
			// getCASString may also be used 
			String cd = statement.getCommandDescription(StringTemplate.ogpTemplate);
			AbstractApplication.debug("Statement to prove: " + cd);

		}
		else {
			AbstractApplication.error("No statement to prove");
			result = ProofResult.UNKNOWN;
			return;
		}

		// Fallback for another prover if singularWS is not available:
		if (engine == ProverEngine.BOTANAS_PROVER) {
			if (AbstractApplication.singularWS == null)
				setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER);
			else if (!AbstractApplication.singularWS.isAvailable())
				setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER); 
		}
		
		if (engine == ProverEngine.BOTANAS_PROVER) {
			result = ProverBotanasMethod.prove(statement);
			return; // this will return later, now we calculate the other methods as well
		} else if (engine == ProverEngine.RECIOS_PROVER) {
					
			if (statement==null){
				result=ProofResult.UNKNOWN;
			} else if (statement instanceof SymbolicParametersAlgo){
				result = ProverReciosMethod.prove(((SymbolicParametersAlgo)statement).getSymbolicParameters());
			} else if (statement.getParentAlgorithm() instanceof SymbolicParametersAlgo){
				result = ProverReciosMethod.prove(((SymbolicParametersAlgo)statement.getParentAlgorithm()).getSymbolicParameters());
			} else {
				result=ProofResult.UNKNOWN;
			}
			return;
		}
		// Falling back to pure symbolic prover:
		result = ProverPureSymbolicMethod.prove(statement);
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
