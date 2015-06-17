package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Prover package for GeoGebra. Allows using multiple backends for theorem
 * proving.
 */

public abstract class Prover {

	/**
	 * Enum list of supported prover backends for GeoGebra
	 * 
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public enum ProverEngine {
		/**
		 * Tomas Recio's method
		 */
		RECIOS_PROVER, /**
		 * Francisco Botana's method
		 */
		BOTANAS_PROVER, /**
		 * OpenGeoProver
		 * (http://code.google.com/p/open-geo-prover/), Wu's method
		 */
		OPENGEOPROVER_WU, /**
		 * OpenGeoProver, Area method
		 */
		OPENGEOPROVER_AREA, /**
		 * pure symbolic prover (every object is calculated
		 * symbolically, also the statements)
		 */
		PURE_SYMBOLIC_PROVER,
		/**
		 * Default prover (GeoGebra decides internally)
		 */
		AUTO
	}

	/**
	 * Possible results of an attempted proof
	 * 
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public enum ProofResult {
		/**
		 * The proof is completed, the statement is generally true (with some
		 * NDG conditions)
		 */
		TRUE,
		/**
		 * The statement is generally true (with some NDG conditions) but no
		 * readable NDGs were found
		 */
		TRUE_NDG_UNREADABLE,
		/**
		 * The proof is completed, the statement is generally false
		 */
		FALSE,
		/**
		 * The statement cannot be proved by using the current backend within the
		 * given timeout
		 */
		UNKNOWN,
		/**
		 * "?", usually from giac.js --- processing in progress
		 */
		PROCESSING
	}

	/**
	 * Maximal time to be spent in the prover subsystem
	 */
	/* input */
	protected int timeout = 5;
	private ProverEngine engine = ProverEngine.AUTO;
	/**
	 * The full GeoGebra construction, containing all geos and algos.
	 */
	protected Construction construction;
	/**
	 * The statement to be prove
	 */
	protected GeoElement statement;

	/**
	 * Recio's prover.
	 */
	protected static AbstractProverReciosMethod reciosProver;

	/**
	 * Gives the current statement to prove
	 * 
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
	 * Should the prover return extra NDG conditions? If not, some computation
	 * time may be saved.
	 */
	private boolean returnExtraNDGs;

	/**
	 * @author Zoltan Kovacs <zoltan@geogebra.org> An object which contains a
	 *         condition description (e.g. "AreCollinear") and an ordered list
	 *         of GeoElement's (e.g. A, B, C)
	 */
	public static class NDGCondition {
		/**
		 * The condition String
		 */
		String condition;

		/**
		 * How human readable is this condition? The lower the better. This
		 * number is always >= 0;
		 */
		double readability = 1.0;

		/**
		 * Gets readability score for this NDG condition.
		 * 
		 * @return score
		 */
		public double getReadability() {
			return readability;
		}

		/**
		 * Sets readability score for this NDG condition.
		 * 
		 * @param readability
		 *            score
		 */
		public void setReadability(double readability) {
			this.readability = readability;
		}

		/**
		 * Array of GeoElements (parameters of the condition)
		 */
		GeoElement[] geos;

		/**
		 * A short textual description of the condition
		 * 
		 * @return the condition
		 */
		public String getCondition() {
			return condition;
		}

		/**
		 * Sets a condition text
		 * 
		 * @param condition
		 *            the text, e.g. "AreCollinear"
		 */
		public void setCondition(String condition) {
			this.condition = condition;
		}

		/**
		 * Returns the GeoElements for a given condition
		 * 
		 * @return the array of GeoElements
		 */
		public GeoElement[] getGeos() {
			return geos;
		}

		/**
		 * Sets the GeoElements for a given condition
		 * 
		 * @param object
		 *            the array of GeoElements
		 */
		public void setGeos(GeoElement[] object) {
			this.geos = object;
		}

		@Override
		public int hashCode() {
			int result = condition.hashCode();
			if (geos != null) {
				for (GeoElement geo : geos) {
					if (geo != null) {
						result += geo.hashCode();
					}
				}
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (obj.getClass() != getClass())
				return false;

			return this.hashCode() == obj.hashCode();
		}

		private static GeoLine line(GeoPoint P1, GeoPoint P2, Construction cons) {
			TreeSet<GeoElement> ges = cons.getGeoSetConstructionOrder();
			Iterator<GeoElement> it = ges.iterator();
			// TODO: Maybe there is a better way here to lookup the appropriate
			// line
			// if it already exists (by using kernel).
			while (it.hasNext()) {
				GeoElement ge = it.next();
				if (ge instanceof GeoLine) {
					GeoPoint Q1 = ((GeoLine) ge).getStartPoint();
					GeoPoint Q2 = ((GeoLine) ge).getEndPoint();
					if ((Q1 != null && Q2 != null)
							&& ((Q1.equals(P1) && Q2.equals(P2)) || (Q1
									.equals(P2) && Q2.equals(P1)))) {
						return (GeoLine) ge;
					}
				}
			}
			// If there is no such line, we simply create one.
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(false);
			AlgoJoinPoints ajp = new AlgoJoinPoints(cons, null, P1, P2);
			GeoLine line = ajp.getLine();
			line.setEuclidianVisible(true);
			line.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
			line.setLabelVisible(true);
			line.updateVisualStyle();
			cons.setSuppressLabelCreation(oldMacroMode);
			return line;
		}

		/* TODO: Unify this code with line(). */
		private static GeoSegment segment(GeoPoint P1, GeoPoint P2,
				Construction cons) {
			TreeSet<GeoElement> ges = cons.getGeoSetConstructionOrder();
			Iterator<GeoElement> it = ges.iterator();
			// TODO: Maybe there is a better way here to lookup the appropriate
			// line
			// if it already exists (by using kernel).
			while (it.hasNext()) {
				GeoElement ge = it.next();
				if (ge instanceof GeoSegment) {
					GeoPoint Q1 = ((GeoSegment) ge).getStartPoint();
					GeoPoint Q2 = ((GeoSegment) ge).getEndPoint();
					if ((Q1 != null && Q2 != null)
							&& ((Q1.equals(P1) && Q2.equals(P2)) || (Q1
									.equals(P2) && Q2.equals(P1)))) {
						return (GeoSegment) ge;
					}
				}
			}
			// If there is no such line, we simply create one.
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(false);
			AlgoJoinPointsSegment ajp = new AlgoJoinPointsSegment(cons, null,
					P1, P2);
			GeoSegment segment = ajp.getSegment();
			segment.setEuclidianVisible(true);
			segment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
			segment.setLabelVisible(true);
			segment.updateVisualStyle();
			cons.setSuppressLabelCreation(oldMacroMode);
			return segment;
		}

		private void sortGeos() {
			// We need this because geos are sorted in the order of creation.
			Arrays.sort(geos, new Comparator<GeoElement>() {
				@Override
				public int compare(GeoElement g1, GeoElement g2) {
					return g1.getLabelSimple().compareTo(g2.getLabelSimple());
				}
			});
		}

		/**
		 * Rewrites the NDG to a simpler form.
		 * 
		 * @param cons
		 *            the current construction
		 */
		public void rewrite(Construction cons) {
			String cond = this.getCondition();
			if ("AreCollinear".equals(cond)) {
				sortGeos();
			} else if ("ArePerpendicular".equals(cond) && this.geos.length == 3) {
				// ArePerpendicular[Line[P1,P3],Line[P3,P2]].
				GeoPoint P1 = (GeoPoint) this.geos[0];
				GeoPoint P2 = (GeoPoint) this.geos[1];
				GeoPoint P3 = (GeoPoint) this.geos[2];
				GeoLine l1 = line(P1, P3, cons);
				GeoLine l2 = line(P3, P2, cons);
				if (l1 != null && l2 != null) {
					geos = new GeoElement[2];
					geos[0] = l1;
					geos[1] = l2;
					sortGeos();
				}
			} else if (("AreEqual".equals(cond)
					|| "ArePerpendicular".equals(cond) || "AreParallel"
						.equals(cond))) {
				if (this.geos.length == 4) {
					// This is an AreEqual[P1,P2,P3,P4]-like condition.
					// We should try to rewrite it to
					// AreEqual[Line[P1,P2],Line[P3,P4]].
					GeoPoint P1 = (GeoPoint) this.geos[0];
					GeoPoint P2 = (GeoPoint) this.geos[1];
					GeoLine l1 = line(P1, P2, cons);
					GeoPoint P3 = (GeoPoint) this.geos[2];
					GeoPoint P4 = (GeoPoint) this.geos[3];
					GeoLine l2 = line(P3, P4, cons);
					if (l1 != null && l2 != null) {
						geos = new GeoElement[2];
						geos[0] = l1;
						geos[1] = l2;
						sortGeos();
					}
				} else if (this.geos.length == 2) {
					// This is an AreEqual[l1,l2]-like condition.
					// We should sort l1 and l2.
					sortGeos();
					// Unsure if this is called at all.
				}
			} else if ("IsIsoscelesTriangle".equals(cond)) {
				GeoPoint P1 = (GeoPoint) this.geos[0];
				GeoPoint P2 = (GeoPoint) this.geos[1];
				GeoPoint P3 = (GeoPoint) this.geos[2];
				GeoSegment l1 = segment(P1, P2, cons);
				GeoSegment l2 = segment(P2, P3, cons);
				if (l1 != null && l2 != null) {
					geos = new GeoElement[2];
					geos[0] = l1;
					geos[1] = l2;
					sortGeos();
					this.setCondition("AreEqual");
					/*
					 * This equality is length equality, but this is the natural
					 * interpretation.
					 */
				}
			}
		}
	}

	/**
	 * Constructor for the package.
	 */
	public Prover() {
		proveAutoOrder = new ArrayList<ProverEngine>();
		// Order of Prove[] for the AUTO prover:
		// Recio is the fastest.
		proveAutoOrder.add(ProverEngine.RECIOS_PROVER);
		// Botana's prover is also fast for general problems.
		proveAutoOrder.add(ProverEngine.BOTANAS_PROVER);
		// Wu may be a bit slower.
		proveAutoOrder.add(ProverEngine.OPENGEOPROVER_WU);
		// Area method is not polished yet.
		proveAutoOrder.add(ProverEngine.OPENGEOPROVER_AREA);

		// Order of ProveDetails[] for the AUTO prover:
		proveDetailsAutoOrder = new ArrayList<ProverEngine>();
		// Botana's prover based on elimination (with no presumed NDGs) gives
		// the shortest conditions, best for educational use.
		proveDetailsAutoOrder.add(ProverEngine.BOTANAS_PROVER);
		// Wu's method does the most general good job.
		proveDetailsAutoOrder.add(ProverEngine.OPENGEOPROVER_WU);
		// Recio does not give NDGs:
		// proveDetailsAutoOrder.add(ProverEngine.RECIOS_PROVER);
		// Area method is buggy at the moment, needs Damien's fixes.
		// It returns {true} always at the moment, not useful.
		// proveDetailsAutoOrder.add(ProverEngine.OPENGEOPROVER_AREA);
	}

	/**
	 * Sets the maximal time spent in the Prover for the given proof.
	 * 
	 * @param timeout
	 *            The timeout in seconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Sets the prover engine.
	 * 
	 * @param engine
	 *            The engine subsystem
	 */
	public void setProverEngine(ProverEngine engine) {
		this.engine = engine;
	}

	/**
	 * Sets the GeoGebra construction as the set of the used objects in the
	 * proof.
	 * 
	 * @param construction
	 *            The GeoGebra construction
	 */
	public void setConstruction(Construction construction) {
		this.construction = construction;
	}

	/**
	 * Sets the statement to be proven.
	 * 
	 * @param root
	 *            The statement to be proven
	 */
	public void setStatement(GeoElement root) {
		this.statement = root;
	}

	/**
	 * Adds a non-degeneracy condition to the prover object
	 * 
	 * @param ndgc
	 *            the condition itself
	 */
	public void addNDGcondition(NDGCondition ndgc) {
		ndgConditions.add(ndgc);
	}

	private List<ProverEngine> proveAutoOrder;
	private List<ProverEngine> proveDetailsAutoOrder;

	/**
	 * The real computation of decision of a statement. The statement is
	 * forwarded to an engine (or more engines).
	 */
	public void decideStatement() {
		// Step 1: Checking if the statement is null.
		if (statement == null) {
			App.error("No statement to prove");
			result = ProofResult.UNKNOWN;
			return;
		}

		// Step 2:
		// Maybe an already computed value is asked to be proven, e.g.
		// Prove[1==1], i.e. Prove[true]
		AlgoElement algoParent = statement.getParentAlgorithm();
		if (algoParent == null) {
			if (statement.getValueForInputBar().equals("true"))
				result = ProofResult.TRUE; // Trust in kernel's wisdom
			else if (statement.getValueForInputBar().equals("false"))
				result = ProofResult.FALSE; // Trust in kernel's wisdom
			else
				result = ProofResult.UNKNOWN; // Not sure if this is executed at
												// all, but for sure.
			return;
		}

		// Step 3: Non-AUTO provers
		if (engine != ProverEngine.AUTO) {
			callEngine(engine);
			return;
		}

		// Step 4: AUTO prover
		App.debug("Using " + engine);
		Iterator<ProverEngine> it;
		if (isReturnExtraNDGs())
			it = proveDetailsAutoOrder.iterator();
		else
			it = proveAutoOrder.iterator();
		result = ProofResult.UNKNOWN;
		while ((result == ProofResult.UNKNOWN || result == ProofResult.TRUE_NDG_UNREADABLE)
				&& it.hasNext()) {
			ProverEngine pe = it.next();
			callEngine(pe);
		}
	}

	/**
	 * A helper method to override the last found proof result with the new one,
	 * if the new one is not unknown, or if the result is null yet, then we
	 * prefer the unknown result.
	 * 
	 * @param pr
	 *            the new result
	 * @return decision which result is better
	 */
	private ProofResult override(ProofResult pr) {
		if (result == null || pr != ProofResult.UNKNOWN) {
			return pr;
		}
		return result;
	}

	private void callEngine(ProverEngine currentEngine) {
		App.debug("Using " + currentEngine);
		ndgConditions = new HashSet<NDGCondition>(); // reset
		if (currentEngine == ProverEngine.BOTANAS_PROVER) {
			result = override(org.geogebra.common.kernel.prover.ProverBotanasMethod
					.prove(this));
			return;
		} else if (currentEngine == ProverEngine.RECIOS_PROVER) {
			result = override(reciosProver.prove(this));
			return;
		} else if (currentEngine == ProverEngine.PURE_SYMBOLIC_PROVER) {
			result = override(org.geogebra.common.kernel.prover.ProverPureSymbolicMethod
					.prove(this));
			return;
		} else if (currentEngine == ProverEngine.OPENGEOPROVER_WU
				|| currentEngine == ProverEngine.OPENGEOPROVER_AREA) {
			result = override(openGeoProver(currentEngine));
			return;
		}

	}

	/**
	 * Gets non-degeneracy conditions of the current proof.
	 * 
	 * @return The XML output string of the NDG condition
	 */
	public HashSet<NDGCondition> getNDGConditions() {
		return ndgConditions;
	}

	/**
	 * Gets the proof result
	 * 
	 * @return The result (TRUE, FALSE or UNKNOWN)
	 */
	public ProofResult getProofResult() {
		return result;
	}

	/**
	 * If the result of the proof can be expressed by a boolean value, then it
	 * returns that value.
	 * 
	 * @return The result of the proof (true, false or null)
	 */
	public Boolean getYesNoAnswer() {
		if (result != null) {
			if (result == Prover.ProofResult.TRUE
					|| result == Prover.ProofResult.TRUE_NDG_UNREADABLE)
				return true;
			if (result == Prover.ProofResult.FALSE)
				return false;
		}
		return null;
	}

	/**
	 * A minimal version of the construction XML. Only elements/commands are
	 * preserved, the rest is deleted.
	 * 
	 * @param cons
	 *            The construction
	 * @param statement
	 *            The statement to prove
	 * @return The simplified XML
	 */
	// TODO: Cut even more unneeded parts to reduce unneeded traffic between OGP
	// and GeoGebra.
	protected static String simplifiedXML(Construction cons,
			GeoElement statement) {
		StringBuilder sb = new StringBuilder();
		cons.getConstructionElementsXML_OGP(sb, statement);

		// /* FIXME: EXTREMELY DIRTY HACK. This should be handled in OGP instead
		// here.
		// * In GeoGebra3D some objects get a 3D parameter, e.g. Circle. OGP is
		// not
		// * yet prepared for handling this, so we simply remove the
		// a2="xOyPlane" texts
		// * from the XML. Hopefully this works for most cases...
		// */
		// return "<construction>\n" + sb.toString().replace(" a2=\"xOyPlane\"",
		// "") + "</construction>";

		return "<construction>\n" + sb.toString() + "</construction>";
	}

	/**
	 * Does the real computation for the proof
	 */
	public void compute() {
		// Will be overridden by web and desktop
	}

	/**
	 * Calls OpenGeoProver
	 * 
	 * @param pe
	 *            Prover Engine
	 * @return the proof result
	 */
	protected abstract ProofResult openGeoProver(ProverEngine pe);

	/**
	 * Will the prover return extra NDGs?
	 * 
	 * @return yes or no
	 */
	public boolean isReturnExtraNDGs() {
		return returnExtraNDGs;
	}

	/**
	 * The prover may return extra NDGs
	 * 
	 * @param returnExtraNDGs
	 *            setting for the prover
	 */
	public void setReturnExtraNDGs(boolean returnExtraNDGs) {
		this.returnExtraNDGs = returnExtraNDGs;
	}

}
