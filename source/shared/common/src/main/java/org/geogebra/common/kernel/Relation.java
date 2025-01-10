package org.geogebra.common.kernel;

import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.javax.swing.RelationPane.RelationRow;
import org.geogebra.common.kernel.RelationNumerical.Report;
import org.geogebra.common.kernel.RelationNumerical.Report.RelationCommand;
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
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
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
		RelationPane tablePane = app.getFactory().newRelationPane(getSubTitle());
		tablePane.showDialog(app.getLocalization().getCommand("Relation"), getRows(), app);
	}

	private String getSubTitle() {
		List<String> labels = Stream.of(ra, rb, rc, rd).filter(Objects::nonNull)
				.map(geo -> GeoElement.indicesToHTML(geo.getLabelSimple(), false))
				.collect(Collectors.toList());
		String first = StringUtil.join(", ", labels.subList(0, labels.size() - 1));
		return app.getLocalization().getPlainDefault("AandB", "%0 and %1",
					first, labels.get(labels.size() - 1));
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
			// input is valid, CAS not loaded doesn't throw, only timeout can get here
			Log.warn(e);
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
			if (app.isDesktop()) {
				relInfos[i] = r.stringResult.replace("\n", "<br>");
			} else {
				relInfos[i] = r.stringResult;
			}
			relAlgos[i] = r.symbolicCheck;
			Boolean result = r.boolResult;
			rr[i] = new RelationRow();
			final String relInfo = relInfos[i];
			// First information shown (result of numerical checks):
			if (app.isDesktop()) {
				rr[i].setInfo(
						"<html>" + relInfo + "<br>"
								+ app.getLocalization().getMenuDefault(
								"CheckedNumerically",
								"(checked numerically)")
								+ "</html>");
			} else {
				rr[i].setInfo(relInfo);
			}
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
	 * @return updated row
	 * @param row
	 *            the row to be updated after the action is finished
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
					rel.setInfo(rel.getInfo() + relInfo + "<br><b>" + trueOnParts + "</b>");
				} else {
					// e.g. ProveDetails=={true,{"AreEqual(A,B)"}}
					StringBuilder conds = new StringBuilder("<ul>");
					for (int j = 1; j < ndgs; ++j) {
						conds.append("<li ");
						conds.append(liStyle);
						conds.append(">");
						conds.append(ndgResult[j]);
						if (j < ndgs - 1) {
							conds.append(" ");
							conds.append(and);
						}
					}
					conds.append("</ul>");
					rel.setInfo(rel.getInfo() + loc.getPlain("GenerallyTrueAcondB",
							"<ul><li " + liStyle + ">" + relInfo + "</ul>", conds.toString()));
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

		ExpressionValue ae = getProvableExpression(command);
		if (ae == null) {
			return new String[]{""};
		}
		String[] ret;
		Command proveCommand = buildCommand(Commands.ProveDetails.name(),
				ae, new GeoBoolean(cons, true));
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
			boolean ans = ((GeoBoolean) list.get(0)).getBoolean();
			if (!list.get(0).isDefined()) {
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

	private ExpressionValue getProvableExpression(RelationCommand command) {
		switch (command) {
		case AreCongruent:
		case AreEqual:
		case AreParallel:
		case ArePerpendicular:
			return buildCommand(command.name(), ra, rb);
		case IsOnPath:
			if ((ra instanceof GeoPoint) && (rb instanceof Path)) {
				return new ExpressionNode(kernel, ra, Operation.IS_ELEMENT_OF, rb);
			} else if ((rb instanceof GeoPoint) && (ra instanceof Path)) {
				return new ExpressionNode(kernel, rb, Operation.IS_ELEMENT_OF, ra);
			}
			break;
		case AreConcyclic:
			return buildCommand(command.name(), ra, rb, rc, rd);
		case AreCollinear:
		case AreConcurrent:
			return buildCommand(command.name(), ra, rb, rc);
		case IsTangent:
			if ((ra instanceof GeoLine) && (rb instanceof GeoConic)) {
				return buildCommand(command.name(), ra, rb);
			} else if ((ra instanceof GeoConic) && (rb instanceof GeoLine)) {
				return buildCommand(command.name(), rb, ra);
			}
			break;
		}
		return null;
	}

	private Command buildCommand(String commandName, ExpressionValue... geos) {
		Command provable = new Command(kernel, commandName, false);
		for (ExpressionValue geo : geos) {
			provable.addArgument(geo.wrap());
		}
		return provable;
	}
}
