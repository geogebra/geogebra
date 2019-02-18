package org.geogebra.common.kernel;

import java.util.Iterator;
import java.util.SortedSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.gui.util.RelationMore;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.javax.swing.RelationPane.RelationRow;
import org.geogebra.common.kernel.RelationNumerical.Report;
import org.geogebra.common.kernel.RelationNumerical.Report.RelationCommand;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.prover.AlgoAreCollinear;
import org.geogebra.common.kernel.prover.AlgoAreConcurrent;
import org.geogebra.common.kernel.prover.AlgoAreConcyclic;
import org.geogebra.common.kernel.prover.AlgoAreCongruent;
import org.geogebra.common.kernel.prover.AlgoAreEqual;
import org.geogebra.common.kernel.prover.AlgoAreParallel;
import org.geogebra.common.kernel.prover.AlgoArePerpendicular;
import org.geogebra.common.kernel.prover.AlgoIsOnPath;
import org.geogebra.common.kernel.prover.AlgoIsTangent;
import org.geogebra.common.kernel.prover.AlgoProve;
import org.geogebra.common.kernel.prover.AlgoProveDetails;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Compares two objects, first numerically, then symbolically (when the
 * "More..." button is pressed). The original content of this file has been
 * moved into RelationNumerical.java and extensively rewritten.
 * 
 * @author Zoltan Kovacs
 */
public class Relation {
	/**
	 * @param app
	 *            currently used application
	 * @param ra
	 *            first object
	 * @param rb
	 *            second object
	 * @param rc
	 *            third object (optional, can be null)
	 * @param rd
	 *            forth object (optional, can be null)
	 * 
	 * @author Zoltan Kovacs
	 */
	public static void showRelation(final App app, final GeoElement ra,
			final GeoElement rb, final GeoElement rc, final GeoElement rd) {
		// Forcing CAS to load. This will be essential for the web version
		// to run the Prove[Are...] commands with getting no "undefined":
		Kernel k = app.getKernel();
		GeoGebraCAS cas = (GeoGebraCAS) k.getGeoGebraCAS();
		try {
			cas.getCurrentCAS().evaluateRaw("1");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Creating Relation popup window:
		RelationPane tablePane = app.getFactory().newRelationPane();
		// Computing numerical results and collecting them alphabetically:
		SortedSet<Report> relInfosAll = RelationNumerical.sortAlphabetically(
				new RelationNumerical(k).relation(ra, rb, rc,
						rd));
		// Collecting information for showing them in the popup window:
		Iterator<Report> it = relInfosAll.iterator();
		int rels = relInfosAll.size();

		String[] relInfos = new String[rels];
		RelationCommand[] relAlgos = new RelationCommand[rels];
		Boolean[] relBools = new Boolean[rels];
		int i = 0;
		while (it.hasNext()) {
			Report r = it.next();
			relInfos[i] = r.stringResult;
			relAlgos[i] = r.symbolicCheck;
			relBools[i] = r.boolResult;
			i++;
		}
		final RelationRow[] rr = new RelationRow[rels];
		for (i = 0; i < rels; i++) {
			rr[i] = new RelationRow();
			final String relInfo = relInfos[i].replace("\n", "<br>");
			// First information shown (result of numerical checks):
			rr[i].setInfo("<html>" + relInfo + "<br>"
					+ app.getLocalization().getMenuDefault("CheckedNumerically",
							"(checked numerically)")
					+ "</html>");
			final RelationCommand relAlgo = relAlgos[i];

			RelationMore rm = new RelationMore() {
				@Override
				public void action(RelationPane table, int row) {
					final RelationRow rel = new RelationRow();
					app.setWaitCursor();

					Boolean result = null;
					if (!app.has(Feature.PROVE_UNIFY)) {
						result = checkGenerally(relAlgo, ra, rb, rc, rd);
					}
					Localization loc = ra.getConstruction().getApplication()
							.getLocalization();
					String and = loc.getMenu("Symbol.And").toLowerCase();
					String or = loc.getMenu("Symbol.Or").toLowerCase();
					String trueOnParts = loc.getMenuDefault(
							"TrueOnPartsFalseOnParts",
							"(true on parts, false on parts)");
					String generallyFalse = loc.getMenuDefault("FalseInGeneral",
							"(false in general)");
					rel.setInfo("<html>");
					if (result != null && !result) {
						// Prove==false
						rel.setInfo(rel.getInfo() + relInfo + "<br><b>"
								+ generallyFalse
								+ "</b>");
						app.setDefaultCursor();
					} else {
						// We don't show the second information unless
						// ProveDetails is unsuccessful.

						// Third info start:
						String[] ndgResult = getNDGConditions(relAlgo, ra, rb,
								rc, rd);
						app.setDefaultCursor();
						// This style is defined in the CSS. It is harmless in
						// desktop but
						// helpful to show nice list look in web:
						String liStyle = "class=\"RelationTool\"";
						// Third information shown (result of ProveDetails
						// command):
						if (ndgResult.length == 1) {
							// ProveDetails=={true} or =={false} or ==undefined
							rel.setInfo(
									rel.getInfo() + relInfo + "<br><b>");
							if ("".equals(ndgResult[0])) {
								// ProveDetails==undefined
								if (result != null && result) {
									// Using Prove's result (since ProveDetails
									// couldn't find any interesting):
									String generallyTrue = loc
											.getMenuDefault("GenerallyTrue",
													"(generally true)");
									int gtl = generallyTrue.length();
									// the first ) and
									// the second ( will be removed
									rel.setInfo(
											rel.getInfo()
													+ generallyTrue.substring(0,
															gtl - 1)
													+ " " + or + " "
													+ trueOnParts
															.substring(1));
								} else {
									// Prove==ProveDetails==undefined
									rel.setInfo(
											rel.getInfo() + loc
													.getMenuDefault(
															"CheckedNumerically",
															"(checked numerically)"));
								}
							} else if ("1".equals(ndgResult[0])) {
								// ProveDetails=={true}
								rel.setInfo(rel.getInfo() + loc.getMenuDefault(
										"AlwaysTrue", "(always true)"));
							} else if ("2".equals(ndgResult[0])) {
								// ProveDetails=={true,"c"}
								rel.setInfo(rel.getInfo()
										+ trueOnParts);
							} else { // "0"
								Log.error("Internal error in prover:"
												+ " Prove==true <-> ProveDetails==false");
								rel.setInfo(rel.getInfo() + generallyFalse);
							}
							rel.setInfo(rel.getInfo() + "</b>");
						} else {
							int ndgs = ndgResult.length;
							if ((ndgs == 2) && ((Unicode.ELLIPSIS + "")
									.equals(ndgResult[1]))) {
								// Formerly UnderCertainConditionsA
								rel.setInfo(rel.getInfo() + loc.getPlain(
										"GenerallyTrueAcondB",
										"<ul><li " + liStyle + ">" + relInfo
												+ "</ul>",
										"<ul><li " + liStyle + ">"
												+ loc.getMenuDefault(
														"ConstructionNotDegenerate",
														"the construction is not degenerate")
												+ "</ul>"));

							} else {
								if ("2".equals(ndgResult[0])) {
									// ProveDetails=={true,{...},"c"}
									rel.setInfo(
											rel.getInfo() + relInfo + "<br><b>"
												+ trueOnParts + "</b>");
								} else {
									// GenerallyTrueAcondB
									StringBuilder conds = new StringBuilder("<ul>");
									for (int j = 1; j < ndgs; ++j) {
										conds.append("<li ");
										conds.append(liStyle);
										conds.append(">");
										conds.append(ndgResult[j]);
										if ((j < ndgs - 1)) {
											conds.append(" ");
											conds.append(and);
										}
									}
									conds.append("</ul>");
									rel.setInfo(rel.getInfo() + loc
											.getPlain("GenerallyTrueAcondB",
													"<ul><li " + liStyle + ">"
															+ relInfo + "</ul>",
													conds.toString()));
								}
							}
						}
					}
					rel.setInfo(rel.getInfo() + "</html>");
					rel.setCallback(null);
					table.updateRow(row, rel);
				}
			};

			if (relBools[i] != null && relBools[i] && relAlgos[i] != null) {
				rr[i].setCallback(rm);
			}
		}

		// just send first row to event
		app.dispatchEvent(new Event(EventType.RELATION_TOOL, null, rr[0].getInfo()));

		tablePane.showDialog(app.getLocalization().getCommand("Relation"), rr,
				ra.getConstruction().getApplication());
	}

	/**
	 * Tries to compute if a geometry statement holds generally.
	 * 
	 * @param command
	 *            Are... command
	 * @param g1
	 *            first object
	 * @param g2
	 *            second object
	 * @param g3
	 *            third object (optional)
	 * @param g4
	 *            forth object (optional)
	 * @return true if statement holds generally, false if it does not hold,
	 *         null if cannot be decided by GeoGebra
	 * 
	 * @author Zoltan Kovacs
	 */
	final public static Boolean checkGenerally(RelationCommand command,
			GeoElement g1, GeoElement g2, GeoElement g3, GeoElement g4) {
		Boolean ret = null;
		Construction cons = g1.getConstruction();
		GeoElement root = new GeoBoolean(cons);
		AlgoElement ae = null;
		try {
			switch (command) {
			case AreEqual:
				ae = new AlgoAreEqual(cons, g1, g2);
				break;
			case AreCongruent:
				ae = new AlgoAreCongruent(cons, g1, g2);
				break;
			case AreParallel:
				ae = new AlgoAreParallel(cons, g1, g2);
				break;
			case ArePerpendicular:
				ae = new AlgoArePerpendicular(cons, g1, g2);
				break;
			case IsOnPath:
				if ((g1 instanceof GeoPoint) && (g2 instanceof Path)) {
					ae = new AlgoIsOnPath(cons, (GeoPoint) g1, (Path) g2);
				} else if ((g2 instanceof GeoPoint) && (g1 instanceof Path)) {
					ae = new AlgoIsOnPath(cons, (GeoPoint) g2, (Path) g1);
				}
				break;
			case AreConcyclic:
				ae = new AlgoAreConcyclic(cons, (GeoPoint) g1, (GeoPoint) g2,
						(GeoPoint) g3, (GeoPoint) g4);
				break;
			case AreCollinear:
				ae = new AlgoAreCollinear(cons, (GeoPoint) g1, (GeoPoint) g2,
						(GeoPoint) g3);
				break;
			case AreConcurrent:
				ae = new AlgoAreConcurrent(cons, (GeoLine) g1, (GeoLine) g2,
						(GeoLine) g3);
				break;
			case IsTangent:
				Log.debug("Missing case: " + command);
			}
		} catch (Exception ex) {
			return ret; // there was an error during Prove
		}
		if (ae == null) {
			return ret; // which is null here
		}
		root.setParentAlgorithm(ae);
		AlgoProve ap = new AlgoProve(cons, null, root);
		ap.compute();
		GeoElement[] o = ap.getOutput();
		GeoBoolean ans = ((GeoBoolean) o[0]);
		if (ans.isDefined()) {
			ret = ans.getBoolean();
		}
		root.remove();
		o[0].remove();
		return ret;
	}

	/**
	 * Tries to compute a necessary condition for a given statement to hold.
	 * 
	 * @param command
	 *            Are... command
	 * @param g1
	 *            first object
	 * @param g2
	 *            second object
	 * @param g3
	 *            third object (may be null)
	 * @param g4
	 *            forth object (may be null)
	 * 
	 * @return [""]: undefined, ["0"]: false, ["1"]: always true, ["1", cond1,
	 *         cond2, ...]: true under cond1 and cond2 and ...
	 * 
	 * @author Zoltan Kovacs
	 * 
	 */
	final public static String[] getNDGConditions(RelationCommand command,
			GeoElement g1, GeoElement g2, GeoElement g3, GeoElement g4) {
		Construction cons = g1.getConstruction();
		GeoElement root = new GeoBoolean(cons);
		AlgoElement ae = null;
		String[] ret;
		try {
			switch (command) {
			case AreCongruent:
				ae = new AlgoAreCongruent(cons, g1, g2);
				break;
			case AreEqual:
				ae = new AlgoAreEqual(cons, g1, g2);
				break;
			case AreParallel:
				ae = new AlgoAreParallel(cons, g1, g2);
				break;
			case ArePerpendicular:
				ae = new AlgoArePerpendicular(cons, g1, g2);
				break;
			case IsOnPath:
				if ((g1 instanceof GeoPoint) && (g2 instanceof Path)) {
					ae = new AlgoIsOnPath(cons, (GeoPoint) g1, (Path) g2);
				} else if ((g2 instanceof GeoPoint) && (g1 instanceof Path)) {
					ae = new AlgoIsOnPath(cons, (GeoPoint) g2, (Path) g1);
				}
				break;
			case AreConcyclic:
				ae = new AlgoAreConcyclic(cons, (GeoPoint) g1, (GeoPoint) g2,
						(GeoPoint) g3, (GeoPoint) g4);
				break;
			case AreCollinear:
				ae = new AlgoAreCollinear(cons, (GeoPoint) g1, (GeoPoint) g2,
						(GeoPoint) g3);
				break;
			case AreConcurrent:
				ae = new AlgoAreConcurrent(cons, (GeoLine) g1, (GeoLine) g2,
						(GeoLine) g3);
				break;
			case IsTangent:
				if ((g1 instanceof GeoLine) && (g2 instanceof GeoConic)) {
					ae = new AlgoIsTangent(cons, (GeoLine) g1, (GeoConic) g2);
				} else if ((g1 instanceof GeoConic)
						&& (g2 instanceof GeoLine)) {
					ae = new AlgoIsTangent(cons, (GeoLine) g2, (GeoConic) g1);
				}
				break;
			}
		} catch (RuntimeException ex) {
			ret = new String[1];
			ret[0] = ""; // on error: undefined (UNKNOWN)
			return ret;
		}
		if (ae == null) {
			ret = new String[1];
			ret[0] = ""; // undefined (UNKNOWN)
			return ret;
		}
		root.setParentAlgorithm(ae);
		AlgoProveDetails ap = new AlgoProveDetails(cons, root, true);
		ap.compute();
		GeoElement[] o = ap.getOutput();

		GeoList list = ((GeoList) o[0]);
		// Turning the output of ProveDetails into an array:
		if (list.size() >= 2 && list.get(1).isGeoList()) {
			GeoList conds = (GeoList) list.get(1);
			int condsSize = conds.size();
			ret = new String[condsSize + 1];
			for (int i = 0; i < condsSize; ++i) {
				String cond = conds.get(i)
						.toString(StringTemplate.defaultTemplate);
				// Removing quotes:
				ret[i + 1] = cond.substring(1, cond.length() - 1);
			}
		} else {
			ret = new String[1];
		}
		if (list.size() != 0) {
			Boolean ans = ((GeoBoolean) list.get(0)).getBoolean();
			if (!((GeoBoolean) list.get(0)).isDefined()) {
				ret[0] = ""; // undefined (UNKNOWN)
			} else if (ans) {
				ret[0] = "1"; // TRUE
			} else {
				ret[0] = "0"; // FALSE
			}
		} else {
			ret[0] = ""; // undefined (UNKNOWN)
		}
		if (list.size() > 0) {
			GeoElement last = list.get(list.size() - 1);
			if (last.isGeoText()) {
				String lastText = ((GeoText) last).getTextString();
				if ("c".equals(lastText)) {
					// true on components
					ret[0] = "2";
				}
			}
		}
		root.remove();
		o[0].remove();
		return ret;
	}
}
