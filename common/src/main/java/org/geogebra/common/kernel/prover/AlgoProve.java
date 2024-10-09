/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.prover;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.Prover.ProverEngine;
import org.geogebra.common.util.debug.Log;

/**
 * Algo for the Prove command.
 * 
 * @author Zoltan Kovacs
 */
public class AlgoProve extends AlgoElement implements UsesCAS {

	private GeoElement root; // input
	private GeoBoolean bool; // output
	private String inputFingerprint;

	/**
	 * Proves the given statement and gives a yes/no answer (boolean)
	 * 
	 * @param cons
	 *            The construction
	 * @param label
	 *            Label for the output
	 * @param root
	 *            Input statement
	 */
	public AlgoProve(Construction cons, String label, GeoElement root) {
		super(cons);
		this.root = root;

		bool = new GeoBoolean(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		initialCompute();
		compute();
		bool.setLabel(label);
		
	}

	@Override
	public Commands getClassName() {
		return Commands.Prove;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = root;

		setOnlyOutput(bool);
		setDependencies(); // done by AlgoElement
		inputFingerprint = fingerprint(root);
	}

	/**
	 * Returns the output for the Prove command
	 * 
	 * @return A boolean: true/false
	 */
	public GeoBoolean getGeoBoolean() {
		return bool;
	}

	/**
	 * Heavy computation of the proof.
	 */
	public final void initialCompute() {
		ProverSettings proverSettings = ProverSettings.get();
		// Create and initialize the prover
		Prover p = UtilFactory.getPrototype().newProver();
		if ("OpenGeoProver".equalsIgnoreCase(proverSettings.proverEngine)) {
			if ("Wu".equalsIgnoreCase(proverSettings.proverMethod)) {
				p.setProverEngine(ProverEngine.OPENGEOPROVER_WU);
			} else if ("Area".equalsIgnoreCase(proverSettings.proverMethod)) {
				p.setProverEngine(ProverEngine.OPENGEOPROVER_AREA);
			}
		} else if ("Botana".equalsIgnoreCase(proverSettings.proverEngine)) {
			p.setProverEngine(ProverEngine.BOTANAS_PROVER);
		} else if ("Recio".equalsIgnoreCase(proverSettings.proverEngine)) {
			p.setProverEngine(ProverEngine.RECIOS_PROVER);
		} else if ("PureSymbolic".equalsIgnoreCase(proverSettings.proverEngine)) {
			p.setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER);
		} else if ("Auto".equalsIgnoreCase(proverSettings.proverEngine)) {
			p.setProverEngine(ProverEngine.AUTO);
		}
		p.setTimeout(proverSettings.proverTimeout);
		p.setConstruction(cons);
		p.setStatement(root);
		// Don't compute extra NDG's:
		p.setReturnExtraNDGs(false);

		// Adding benchmarking:
		double startTime = UtilFactory.getPrototype().getMillisecondTime();
		p.compute(); // the computation of the proof
		int elapsedTime = (int) (UtilFactory.getPrototype().getMillisecondTime()
				- startTime);

		/*
		 * Don't remove this. It is needed for automated testing. (String match
		 * is assumed.)
		 */
		Log.debug("Benchmarking: " + elapsedTime + " ms");

		ProofResult result = p.getProofResult();

		Log.debug("STATEMENT IS " + result);

		if (result != null) {
			if (result == ProofResult.UNKNOWN
					|| result == ProofResult.PROCESSING) {
				bool.setUndefinedProverOnly();
				return;
			}
			bool.setDefined();
			if (result == ProofResult.TRUE
					|| result == ProofResult.TRUE_NDG_UNREADABLE
					|| result == ProofResult.TRUE_ON_COMPONENTS) {
				bool.setValue(true);
			}
			if (result == ProofResult.FALSE) {
				bool.setValue(false);
			}
		}

		/*
		 * Don't remove this. It is needed for testing the web platform. (String
		 * match is assumed.)
		 */
		Log.debug("OUTPUT for Prove: " + bool);

	}

	@Override
	public void compute() {
		if (!kernel.getGeoGebraCAS().getCurrentCAS().isLoaded()) {
			inputFingerprint = null;
			return;
		}
		String inputFingerprintPrev = inputFingerprint;

		setInputOutput();

		/*
		 * Not really sure if this is needed, but it cleans up the list of algos
		 * and constructions:
		 */
		do {
			cons.removeFromAlgorithmList(this);
		} while (cons.getAlgoList().contains(this));
		// Adding this again:
		cons.addToAlgorithmList(this);
		cons.removeFromConstructionList(this);
		// Adding this again:
		cons.addToConstructionList(this, true);
		// TODO: consider moving setInputOutput() out from compute()

		if (inputFingerprintPrev == null
				|| !inputFingerprintPrev.equals(inputFingerprint)) {
			Log.trace(inputFingerprintPrev + " -> " + inputFingerprint);
			initialCompute();
		}
	}

	/*
	 * We use a very hacky way to avoid recomputing proof when the input is not
	 * changed. To achieve that, we create a fingerprint of the current input.
	 * The fingerprint function should eventually be improved. Here we assume
	 * that the input objects are always in the same order (that seems sensible)
	 * and the obtained algebraic description changes iff the object does. This
	 * may not be the case if rounding/precision is not as presumed.
	 */
	private static String fingerprint(GeoElement statement) {
		return Prover.getTextFormat(statement);
	}

}
