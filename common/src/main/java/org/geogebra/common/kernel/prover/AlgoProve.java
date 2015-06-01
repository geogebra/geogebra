/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.prover;

import java.util.Date;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.Prover.ProverEngine;

/**
 * Algo for the Prove command.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoProve extends AlgoElement implements UsesCAS {

	private GeoElement root; // input
	private GeoBoolean bool; // output
	private Prover.ProofResult result;
	/*
	 * We need to count the processing number for giac.js: 0: normal state (no
	 * giac.js should be considered) 1: giac.js started, computation not yet
	 * done 2: giac.js loaded, computation should be done 3: computation done
	 */
	private int processing = 0;

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
		cons.addCASAlgo(this);
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

		super.setOutputLength(1);
		super.setOutput(0, bool);
		setDependencies(); // done by AlgoElement
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
	 * Heavy computation of the proof
	 */
	public final void initialCompute() {

		// Create and initialize the prover
		Prover p = UtilFactory.prototype.newProver();
		if ("OpenGeoProver".equalsIgnoreCase(ProverSettings.proverEngine)) {
			if ("Wu".equalsIgnoreCase(ProverSettings.proverMethod))
				p.setProverEngine(ProverEngine.OPENGEOPROVER_WU);
			else if ("Area".equalsIgnoreCase(ProverSettings.proverMethod))
				p.setProverEngine(ProverEngine.OPENGEOPROVER_AREA);
		} else if ("Botana".equalsIgnoreCase(ProverSettings.proverEngine))
			p.setProverEngine(ProverEngine.BOTANAS_PROVER);
		else if ("Recio".equalsIgnoreCase(ProverSettings.proverEngine))
			p.setProverEngine(ProverEngine.RECIOS_PROVER);
		else if ("PureSymbolic".equalsIgnoreCase(ProverSettings.proverEngine))
			p.setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER);
		else if ("Auto".equalsIgnoreCase(ProverSettings.proverEngine))
			p.setProverEngine(ProverEngine.AUTO);
		p.setTimeout(ProverSettings.proverTimeout);
		p.setConstruction(cons);
		p.setStatement(root);
		// Don't compute extra NDG's:
		p.setReturnExtraNDGs(false);

		// Adding benchmarking:
		Date date = new Date();
		long startTime = date.getTime();
		p.compute(); // the computation of the proof
		date = new Date();
		long elapsedTime = date.getTime() - startTime;
		
		// Don't remove this. It is needed for testing the web platform. (String match is assumed.)
		App.debug("Benchmarking: " + elapsedTime + " ms");

		result = p.getProofResult();
		if (result == ProofResult.PROCESSING) {
			processing = 1;
		}

		App.debug("STATEMENT IS " + result);
	}

	@Override
	// Not sure how to do this hack normally.
	final public String getCommandName(StringTemplate tpl) {
		return "Prove";
	}

	@Override
	public void compute() {
		if (processing == 1) {
			App.debug("PROCESSING mode: list undefined (1->2)");
			bool.setUndefinedProverOnly();
			processing = 2; // Next time we should call initialCompute()
			return;
		}
		if (processing == 2) {
			App.debug("PROCESSING mode: list should be created (2->3)");
			processing = 3; // Next time we don't need to do anything
			initialCompute();
		}

		if (result != null) {
			if (result == ProofResult.UNKNOWN) {
				bool.setUndefinedProverOnly();
				return;
			}
			bool.setDefined();
			if (result == ProofResult.TRUE) {
				bool.setValue(true);
			}
			if (result == ProofResult.FALSE) {
				bool.setValue(false);
			}
		}
		
		// Don't remove this. It is needed for testing the web platform. (String match is assumed.)
		App.debug("OUTPUT for Prove: " + bool);
		
	}

	// TODO Consider locusequability
}
