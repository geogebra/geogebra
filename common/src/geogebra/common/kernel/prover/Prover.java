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

import geogebra.common.io.QDParser;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ValidExpression;

public class Prover {

	public enum ProverEngine {RECIOS_PROVER, BOTANAS_PROVER, OPENGEOPROVER, AUTO};
	public enum ProofResult {TRUE, FALSE, UNKNOWN};
	
	/* input */
	private int timeout = 10;
	private ProverEngine engine = ProverEngine.BOTANAS_PROVER;
	private Construction construction;
	private ValidExpression statement;
	
	/* output */
	private String NDGconditions;
	private ProofResult result;
	
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
	 * @param statement The statement to be proven
	 */
	public void setStatement(ValidExpression statement) {
		this.statement = statement;
	}
	
	/**
	 * Starts computation of the proof, based on the defined
	 * subsystem.
	 */
	public void compute() {
		/* to be implemented */
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

	private String simplifyXML(Construction cons) {
		QDParser xmlParser;
		xmlParser = new QDParser();
		return null; // TOD: implementation
	}


}
