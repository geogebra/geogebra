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
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.RelationNumerical;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.NDGCondition;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.Prover.ProverEngine;

/**
 * Algo for the ProveDetails command.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoProveDetails extends AlgoElement implements UsesCAS {

	private GeoElement root; // input
	private GeoList list; // output
	private Boolean result, unreadable;
	private HashSet<NDGCondition> ndgresult;
	private boolean relTool = false;

	/*
	 * We need to count the processing number for giac.js: 0: normal state (no
	 * giac.js should be considered) 1: giac.js started, computation not yet
	 * done 2: giac.js loaded, computation should be done 3: computation done
	 */
	private int processing = 0;

	/**
	 * Proves the given statement and gives some details in a list
	 * 
	 * @param cons
	 *            The construction
	 * @param label
	 *            Label for the output
	 * @param root
	 *            Input statement
	 */
	public AlgoProveDetails(Construction cons, String label, GeoElement root) {
		super(cons);
		cons.addCASAlgo(this);
		this.root = root;

		list = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		initialCompute();
		compute();
		list.setLabel(label);
	}

	/**
	 * Proves the given statement and gives some details in a list
	 * 
	 * @param cons
	 *            The construction
	 * @param label
	 *            Label for the output
	 * @param root
	 *            Input statement
	 * @param relationTool
	 *            true if output should be given for Relation Tool (which is
	 *            more readable)
	 */
	public AlgoProveDetails(Construction cons, String label, GeoElement root,
			boolean relationTool) {
		this(cons, label, root);
		this.relTool = relationTool;
	}

	@Override
	public Commands getClassName() {
		return Commands.ProveDetails;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = root;

		super.setOutputLength(1);
		super.setOutput(0, list);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the output for the ProveDetails command
	 * 
	 * @return A list: {true/false, {array of NDGConditions}}
	 */
	public GeoList getGeoList() {
		return list;
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
		// Compute extra NDG's:
		p.setReturnExtraNDGs(true);

		// Adding benchmarking:
		Date date = new Date();
		long startTime = date.getTime();
		p.compute(); // the computation of the proof
		date = new Date();
		long elapsedTime = date.getTime() - startTime;
		App.debug("Benchmarking: " + elapsedTime + " ms");

		result = p.getYesNoAnswer();
		ndgresult = p.getNDGConditions();
		if (p.getProofResult() == ProofResult.TRUE_NDG_UNREADABLE) {
			unreadable = true;
		}
		if (p.getProofResult() == ProofResult.TRUE) {
			unreadable = false;
		}
		if (p.getProofResult() == ProofResult.PROCESSING) {
			processing = 1;
		}

		App.debug("Statement is " + result);

	}

	@Override
	// Not sure how to do this hack normally.
	final public String getCommandName(StringTemplate tpl) {
		return "ProveDetails";
	}

	@Override
	public void compute() {
		if (processing == 1) {
			App.debug("PROCESSING mode: list undefined (1->2)");
			list.setUndefined();
			processing = 2; // Next time we should call initialCompute()
			return;
		}
		if (processing == 2) {
			App.debug("PROCESSING mode: list should be created (2->3)");
			processing = 3; // Next time we don't need to do anything
			initialCompute();
		}

		list.setDefined(true);
		list.clear();

		if (result != null) {
			GeoBoolean answer = new GeoBoolean(cons);
			answer.setValue(result);
			list.add(answer);
			if (result) {
				GeoList ndgConditionsList = new GeoList(cons);
				ndgConditionsList.clear();
				ndgConditionsList.setDrawAsComboBox(true);
				Iterator<NDGCondition> it = ndgresult.iterator();
				TreeSet<GeoText> sortedSet = new TreeSet<GeoText>(
						GeoText.getComparator());

				// Collecting the set of NDG conditions.
				// The OGP data collector may left some unreadable conditions
				// so we make sure if the condition is readable.
				while (!unreadable && it.hasNext()) {
					GeoText ndgConditionText = new GeoText(cons);
					NDGCondition ndgc = it.next();
					// Do not print unnecessary conditions:
					if (ndgc.getReadability() > 0) {
						ndgc.rewrite(cons);
						String s = null;

						if (relTool) {
							String cond = ndgc.getCondition();
							if ("AreParallel".equals(cond)) {
								// non-parallism in 2D means intersecting
								// FIXME: this is not true for 3D
								s = RelationNumerical.intersectString(
										ndgc.getGeos()[0], ndgc.getGeos()[1],
										true, getLoc());
							} else if ("AreCollinear".equals(cond)) {
								s = RelationNumerical
										.triangleNonDegenerateString(
												(GeoPoint) ndgc.getGeos()[0],
												(GeoPoint) ndgc.getGeos()[1],
												(GeoPoint) ndgc.getGeos()[2],
												getLoc());
							} else if ("AreEqual".equals(cond)) {
								s = RelationNumerical.equalityString(
										ndgc.getGeos()[0], ndgc.getGeos()[1],
										false, getLoc());
							} else if ("ArePerpendicular".equals(cond)) {
								s = RelationNumerical.perpendicularString(
										(GeoLine) ndgc.getGeos()[0],
										(GeoLine) ndgc.getGeos()[1], false,
										getLoc());
							}
						}
						if (s == null || !relTool) {
							s = getLoc().getCommand(ndgc.getCondition());
							s += "[";
							for (int i = 0; i < ndgc.getGeos().length; ++i) {
								if (i > 0) {
									s += ',';
								}
								/*
								 * There can be a case when the underlying
								 * prover sends such objects which cannot be
								 * understood by GeoGebra. In this case we use
								 * the "Objects" word. In this case we normally
								 * return ProveResult.UNKNOWN to not confuse the
								 * student, but for sure, we still do the check
								 * here as well.
								 */
								GeoElement geo = ndgc.getGeos()[i];
								if (geo != null)
									s += ndgc.getGeos()[i].getLabelSimple();
								else
									s += "...";
							}
							s += "]";
							if (relTool) {
								s = getLoc().getPlain("not") + " " + s;
							}
						}

						ndgConditionText.setTextString(s);
						ndgConditionText.setLabelVisible(false);
						ndgConditionText.setEuclidianVisible(false);
						sortedSet.add(ndgConditionText);
					}
					// For alphabetically ordering, we need a sorted set here:
				}
				// Copy the sorted list into the output:
				Iterator<GeoText> it2 = sortedSet.iterator();
				while (it2.hasNext()) {
					ndgConditionsList.add(it2.next());
				}

				if (unreadable) {
					GeoText ndgConditionText = new GeoText(cons);
					String cond = "...";
					ndgConditionText.setTextString(cond);
					ndgConditionText.setLabelVisible(false);
					ndgConditionText.setEuclidianVisible(false);
					sortedSet.add(ndgConditionText);
					ndgConditionsList.add(ndgConditionText);
				}

				// Put this list to the final output (if non-empty):
				if (ndgConditionsList.size() > 0)
					list.add(ndgConditionsList);
			}
		}
	}

	// TODO Consider locusequability

}
