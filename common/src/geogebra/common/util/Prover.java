package geogebra.common.util;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;

import geogebra.common.main.AbstractApplication;

import java.util.Iterator;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * 
 * Common abstract class for platform dependent parts of the Prover,
 * implemented by different ways in desktop and web
 */


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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Prover package for GeoGebra.
 * Allows using multiple backends for theorem proving.
 */

public abstract class Prover {

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
	
	/**
	 * Maximal time to be spent in the prover subsystem
	 */
	/* input */
	protected int timeout = 5;
	private ProverEngine engine = ProverEngine.AUTO;
	private Construction construction;
	/**
	 * The statement to be prove
	 */
	protected GeoElement statement;
	
	/**
	 * Gives the current statement to prove
	 * @return the statement (usually a GeoBoolean)
	 */
	public GeoElement getStatement() {
		return statement;
	}

	/* output */
	private HashSet<NDGCondition> ndgConditions = new HashSet<NDGCondition>();
	/**
	 * The result of the proof
	 */
	protected ProofResult result;

	/**
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 * An object which contains a condition description (e.g. "AreCollinear")
	 * and an ordered list of GeoElement's (e.g. A, B, C)
	 */
	public static class NDGCondition {
		/**
		 * The condition String
		 */
		String condition;
		/**
		 * Array of GeoElements (parameters of the condition)
		 */
		GeoElement[] geos;
		/**
		 * A short textual description of the condition
		 * @return the condition
		 */
		public String getCondition() {
			return condition;
		}
		/**
		 * Sets a condition text
		 * @param condition the text, e.g. "AreCollinear"
		 */
		public void setCondition(String condition) {
			this.condition = condition;
		}
		/**
		 * Returns the GeoElements for a given condition
		 * @return the array of GeoElements
		 */
		public GeoElement[] getGeos() {
			return geos;
		}
		/**
		 * Sets the GeoElements for a given condition
		 * @param object the array of GeoElements
		 */
		public void setGeos(GeoElement[] object) {
			this.geos = object;
		}
	}
	
	/**
	 * Constructor for the package.
	 */
	public Prover() {
		 proverAutoOrder = new ArrayList<ProverEngine>();
		 // Order for the AUTO prover:
		 proverAutoOrder.add(ProverEngine.RECIOS_PROVER);
		 proverAutoOrder.add(ProverEngine.BOTANAS_PROVER);
		 proverAutoOrder.add(ProverEngine.PURE_SYMBOLIC_PROVER);
	}

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
	 * Adds a non-degeneracy condition to the prover object
	 * @param ndgc the condition itself
	 */
	public void addNDGcondition(NDGCondition ndgc) {
		ndgConditions.add(ndgc);
	}
	
	private List<ProverEngine> proverAutoOrder;

		
	/**
	 * The real computation of decision of a statement.
	 * The statement is forwarded to an engine (or more engines).
	 */
	public void decideStatement() {
		// Step 1: Checking if the statement is null.
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

		// Step 2:
		// Maybe an already computed value is asked to be proven, e.g. Prove[1==1], i.e. Prove[true]
		AlgoElement algoParent = statement.getParentAlgorithm();
		if (algoParent == null) {
			if (statement.getValueForInputBar().equals("true"))
				result = ProofResult.TRUE; // Trust in kernel's wisdom
			else if (statement.getValueForInputBar().equals("false"))
				result = ProofResult.FALSE; // Trust in kernel's wisdom
			else
				result = ProofResult.UNKNOWN; // Not sure if this is executed at all, but for sure.
			return;
		}
		
		// Step 3: Non-AUTO provers
		if (engine != ProverEngine.AUTO) {
			callEngine(engine);
			return;
		}
		
		// Step 4: AUTO prover
		AbstractApplication.debug("Using " + engine);
		Iterator<ProverEngine> it = proverAutoOrder.iterator();
		result = ProofResult.UNKNOWN;
		while (result == ProofResult.UNKNOWN && it.hasNext()) {
			ProverEngine pe = it.next();
			callEngine(pe);
		}
	}

	private void callEngine(ProverEngine currentEngine) {
		AbstractApplication.debug("Using " + currentEngine);
		ndgConditions = new HashSet<NDGCondition>(); // reset
		if (currentEngine == ProverEngine.BOTANAS_PROVER) {
			// Botana's prover needs singularWS.
			// So don't try to use it if singularWS is not available:
			if (AbstractApplication.singularWS == null) {
				AbstractApplication.debug(currentEngine + " cannot be used, since singularWS is null");
				result = ProofResult.UNKNOWN;
				return;
			}
			if (!AbstractApplication.singularWS.isAvailable()) {
				AbstractApplication.debug(currentEngine + " cannot be used, since singularWS is unavailable");
				result = ProofResult.UNKNOWN;
				return;
			}
			result = geogebra.common.kernel.prover.ProverBotanasMethod.prove(this);
			return;
		} else if (currentEngine == ProverEngine.RECIOS_PROVER) {
			result = geogebra.common.kernel.prover.ProverReciosMethod.prove(this);
			return;
		} else if (currentEngine == ProverEngine.PURE_SYMBOLIC_PROVER) {
			result = geogebra.common.kernel.prover.ProverPureSymbolicMethod.prove(this);
			return;
		}

	}
	
	/**
	 * Gets non-degeneracy conditions of the current proof.
	 * @return The XML output string of the NDG condition
	 */
	public HashSet<NDGCondition> getNDGConditions() {
		return ndgConditions;
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

	/**
	 * Does the real computation for the proof
	 */
	public void compute() {
		// Will be overridden by web and desktop
		
	}

}
