package org.geogebra.common.kernel;

import static org.geogebra.common.kernel.RelationNumerical.Report.RelationCommand.Compare;

import java.util.SortedSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.javax.swing.RelationPane.RelationRow;
import org.geogebra.common.kernel.RelationNumerical.Report;
import org.geogebra.common.kernel.RelationNumerical.Report.RelationCommand;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
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
import org.geogebra.common.kernel.prover.AlgoCompare;
import org.geogebra.common.kernel.prover.AlgoIsOnPath;
import org.geogebra.common.kernel.prover.AlgoIsTangent;
import org.geogebra.common.kernel.prover.AlgoProveDetails;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.Operation;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Compares two objects, first numerically, then symbolically (when the
 * "More..." button is pressed). The original content of this file has been
 * moved into RelationNumerical.java and extensively rewritten.
 *
 * @author Zoltan Kovacs
 */
public class Relation {
	private GeoElement ra;
	private GeoElement rb;
	private GeoElement rc;
	private GeoElement rd;
	private App app;
	private String[] relInfos;
	private RelationCommand[] relAlgos;
	private Kernel kernel;

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
	public Relation(final App app, final GeoElement ra,
					final GeoElement rb, final GeoElement rc, final GeoElement rd) {
		this.app = app;
		this.kernel = app.getKernel();
		this.ra = ra;
		this.rb = rb;
		this.rc = rc;
		this.rd = rd;
	}

	/**
	 * Show relation dialog. Shouldn't be here, but shouldn't be in App either.
	 */
	public void showDialog() {
		RelationPane tablePane = app.getFactory().newRelationPane();
		tablePane.showDialog(app.getLocalization().getCommand("Relation"), getRows(), app);
	}

	/**
	 * @return content for the relation dialog
	 */
	public RelationRow[] getRows() {
		// Forcing CAS to load. This will be essential for the web version
		// to run the Prove[Are...] commands with getting no "undefined":
		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		try {
			cas.getCurrentCAS().evaluateRaw("1");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// Computing numerical results and collecting them alphabetically:
		SortedSet<Report> relInfosAll = RelationNumerical.sortAlphabetically(
				new RelationNumerical(kernel).relation(ra, rb, rc, rd));
		// Collecting information for showing them in the popup window:
		int rels = relInfosAll.size();

		relInfos = new String[rels];
		relAlgos = new RelationCommand[rels];
		final RelationRow[] rr = new RelationRow[rels];
		int i = 0;
		for (Report r : relInfosAll) {
			relInfos[i] = r.stringResult.replace("\n", "<br>");
			relAlgos[i] = r.symbolicCheck;
			Boolean result = r.boolResult;
			rr[i] = new RelationRow();
			final String relInfo = relInfos[i];
			// First information shown (result of numerical checks):
			rr[i].setInfo(
					"<html>" + relInfo + "<br>"
							+ app.getLocalization().getMenuDefault(
							"CheckedNumerically",
							"(checked numerically)")
							+ "</html>");
			if (result != null && result && relAlgos[i] != null) {
				rr[i].setCallback(this);
			}
			i++;
		}

		// just send first row to event
		app.dispatchEvent(
				new Event(EventType.RELATION_TOOL, null, rr[0].getInfo()));
		return rr;
	}

	/**
	 * Get the expanded value of a row
	 *
	 * @param row the row to be updated after the action is finished
	 * @return updated row
	 */
	public RelationRow getExpandedRow(int row) {
		final RelationCommand relAlgo = relAlgos[row];
		final String relInfo = relInfos[row];
		final RelationRow rel = new RelationRow();
		app.setWaitCursor();

		Localization loc = ra.getConstruction().getApplication().getLocalization();
		String and = loc.getMenu("Symbol.And").toLowerCase();
		String trueOnParts = loc.getMenuDefault("TrueOnPartsFalseOnParts",
				"(true on parts, false on parts)");
		String generallyFalse = loc.getMenuDefault("FalseInGeneral", "(false in general)");
		rel.setInfo("<html>");

		// Computing symbolic data:
		String[] ndgResult = getNDGConditions(relAlgo);
		app.setDefaultCursor();
		// This style is defined in the CSS. It is harmless in
		// desktop but
		// helpful to show nice list look in web:
		String liStyle = "class=\"RelationTool\"";
		// Show symbolic result:
		int ndgs = ndgResult.length;
		if (ndgs == 1) {
			// ProveDetails=={true} or =={false} or ==undefined
			rel.setInfo(rel.getInfo() + relInfo + "<br><b>");
			if ("".equals(ndgResult[0])) {
				// ProveDetails==undefined
				rel.setInfo(rel.getInfo()
						+ loc.getMenuDefault("CheckedNumerically", "(checked numerically)"));
			} else if ("1".equals(ndgResult[0])) {
				// ProveDetails=={true}
				rel.setInfo(rel.getInfo() + loc.getMenuDefault("AlwaysTrue", "(always true)"));
			} else if ("2".equals(ndgResult[0])) {
				// ProveDetails=={true,"c"}
				rel.setInfo(rel.getInfo() + trueOnParts);
			} else {
				// if ("0".equals(ndgResult[0]))
				// ProveDetails=={false}
				rel.setInfo(rel.getInfo() + generallyFalse);
			}
			rel.setInfo(rel.getInfo() + "</b>");
		} else {
			if ((ndgs == 2) && !("2".equals(ndgResult[0]))
					&& ((Unicode.ELLIPSIS + "").equals(ndgResult[1]))) {
				// ProveDetails=={true,"..."}
				rel.setInfo(
						rel.getInfo()
								+ loc.getPlain("GenerallyTrueAcondB",
								"<ul><li " + liStyle + ">" + relInfo + "</ul>",
								"<ul><li " + liStyle + ">"
										+ loc.getMenuDefault("ConstructionNotDegenerate",
										"the construction is not degenerate")
										+ "</ul>"));

			} else {
				if ("2".equals(ndgResult[0])) {
					// ProveDetails=={true,{...},"c"}
					rel.setInfo(rel.getInfo() + relInfo + "<br><b>"
							+ trueOnParts + "</b>");
				} else if ("3".equals(ndgResult[0])) { // String output
					/*
					 * A comparison based result can be true under some conditions that are not tracked
					 * at the moment. The first four variables are fixed in GeoGebra/Giac (but which one?),
					 * and also the first four variables are fixed in RealGeom/Mathematica (also, not completely
					 * clear which ones). Instead of keeping track of this in both subsystems, we simply admit
					 * that there is not enough information collected at this point, and therefore we omit
					 * communicating more details than the condition is some kind of non-degeneracy.
					 *
					 * We should consider keeping track of this kind of information in both subsystems,
					 * maybe by maintaining an NDGCondition object in AlgoCompare. Also, in RealGeom
					 * we should be able to set a parameter to set the variables to be fixes, or even better,
					 * those variables might be substituted before calling RealGeom.
					 *
					 * Actually, the theorems we obtain this way are usually always true. However, in a strict
					 * mathematical sense we cannot claim this. It would be nice to find a counterexample
					 * or to prove that all such theorems are indeed true. TODO.
					 */
					rel.setInfo(
							rel.getInfo()
									+ loc.getPlain("GenerallyTrueAcondB",
									"<ul><li " + liStyle + ">" + ndgResult[1] + "</ul>",
									"<ul><li " + liStyle + ">"
											+ loc.getMenuDefault("ConstructionNotDegenerate",
											"the construction is not degenerate")
											+ "</ul>"));
				} else {
					// e.g. ProveDetails=={true,{"AreEqual(A,B)"}}
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
					rel.setInfo(rel.getInfo()
							+ loc.getPlain("GenerallyTrueAcondB",
							"<ul><li " + liStyle + ">"
									+ relInfo + "</ul>",
							conds.toString()));
				}
			}
		}
		rel.setInfo(rel.getInfo() + "</html>");
		rel.setCallback(null);
		return rel;
	}

	/**
	 * Tries to compute a necessary condition for a given statement to hold.
	 *
	 * @param command
	 *            Are... command
	 * @return [""]: undefined, ["0"]: false, ["1"]: always true, ["1", cond1,
	 *         cond2, ...]: true under cond1 and cond2 and ...
	 *
	 * @author Zoltan Kovacs
	 *
	 */

	final public String[] getNDGConditions(RelationCommand command) {
		Construction cons = ra.getConstruction();

		Command ae = new Command(kernel, command.name(), false);
		String[] ret;
		try {
			switch (command) {
			case AreCongruent:
			case AreEqual:
			case AreParallel:
			case ArePerpendicular:
				addArguments(ae, ra, rb);
				break;
			case IsOnPath:
				if ((ra instanceof GeoPoint) && (rb instanceof Path)) {
					addArguments(ae, ra, rb);
				} else if ((rb instanceof GeoPoint) && (ra instanceof Path)) {
					addArguments(ae, rb, ra);
				}
				break;
			case AreConcyclic:
				addArguments(ae, ra, rb, rc, rd);
				break;
			case AreCollinear:
			case AreConcurrent:
				addArguments(ae, ra, rb, rc);
				break;
			case IsTangent:
				if ((ra instanceof GeoLine) && (rb instanceof GeoConic)) {
					addArguments(ae, ra, rb);
				} else if ((ra instanceof GeoConic) && (rb instanceof GeoLine)) {
					addArguments(ae, rb, ra);
				}
				break;
			case Compare:
				addArguments(ae, ra, rb);
				break;
			}
		} catch (RuntimeException ex) {
			ret = new String[1];
			ret[0] = ""; // on error: undefined (UNKNOWN)
			return ret;
		}
		if (ae.getArgumentNumber() == 0) {
			ret = new String[1];
			ret[0] = ""; // undefined (UNKNOWN)
			return ret;
		}

		// RealGeom based comparison is a special case because it returns a String
		if (command == Compare) {
			AlgoCompare ac = new AlgoCompare(cons, ra, rb, true);
			GeoElement[] o = ac.getOutput();
			String out = ((GeoText) o[0]).getTextString();
			if ("".equals(out)) {
				ret = new String[1];
				ret[0] = ""; // undefined (UNKNOWN)
				return ret;
			}
			ret = new String[2];
			ret[0] = "3";
			ret[1] = out;
			ac.remove();
			return ret;
		}

		Command proveCommand = new Command(kernel, Commands.ProveDetails.name(), false);
		addArguments(proveCommand, ae, new GeoBoolean(cons, true));
		GeoElement[] proveResult = kernel.getAlgebraProcessor().processCommand(proveCommand,
				new EvalInfo(false));

		GeoList list = (GeoList) proveResult[0];
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
					// true on parts, false on parts
					ret[0] = "2";
				}
			}
		}
		proveResult[0].remove();
		return ret;
	}

	private static void addArguments(Command ae, ExpressionValue... geos) {
		for (ExpressionValue geo : geos) {
			ae.addArgument(geo.wrap());
		}
	}

}
