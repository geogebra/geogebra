package org.geogebra.common.kernel.cas;

import java.util.TreeSet;

import org.geogebra.common.kernel.AlgoCasCellInterface;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Algorithm to take care of GeoCasCells and possibly other GeoElements in the
 * construction. This algorithm updates a given output GeoCasCell (e.g. m := c +
 * 3) and possibly a twin GeoElement object (e.g. GeoNumeric m = c + 3 when c is
 * defined).
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentCasCell extends AlgoElement implements
		AlgoCasCellInterface {

	// output CAS cell of this algorithm
	private GeoCasCell casCell;

	/**
	 * Creates a new algorithm to handle updates of the given cell.
	 * 
	 * @param casCell
	 *            the output cell that this algorithm should update.
	 */
	public AlgoDependentCasCell(GeoCasCell casCell) {
		super(casCell.getConstruction());
		this.casCell = casCell;
		protectedInput = true;
		// make sure all input geos' values are present in the CAS
		initInput();

		// We need to compute the output so that the
		// arbitrary constants are created before the
		// definition in the saved file
		if (casCell.isOutputEmpty()) {
			compute();
		} else {
			casCell.updateTwinGeo(false);
		}

		// initialize algorithm dependencies
		setInputOutput();

		// tell construction that order of CAS cells may have changed
		cons.updateCasCellRows();

		// setLabel of twinGeo if we got one
		casCell.setLabelOfTwinGeo();

		if (casCell.getInputVE().isTopLevelCommand()) {
			if ("SlopeField".equals(casCell.getInputVE().getTopLevelCommand()
					.getName())) {
				cons.registerEuclidianViewCE(this);
			}
		}
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	private void initInput() {
		// input
		// m := c + 3 has input variable c
		TreeSet<GeoElement> geoVars = casCell.getGeoElementVariables();
		if (geoVars == null) {
			input = new GeoElement[0];
		} else {
			GeoElement[] geos = new GeoElement[geoVars.size()];
			input = geoVars.toArray(geos);
			// If the cell contains circular definition, reload might cause
			// stack overflow
			for (int i = 0; i < input.length; i++) {
				if (input[i] == casCell) {
					input = new GeoElement[0];
					return;
				}
			}
		}

	}

	/**
	 * Initializes input and output dependencies of this algorithm. This method
	 * can be called again after the GeoCasCell's input expression has changed.
	 */
	@Override
	protected void setInputOutput() {
		// init output
		// twin geo that may be created as a side effect
		// e.g. the CAS cell m := c + 3 will create a GeoNumeric m
		GeoElement twinGeo = casCell.getTwinGeo();

		setOutputLength(twinGeo == null ? 1 : 2);
		setOutput(0, casCell);
		if (twinGeo != null)
			setOutput(1, twinGeo);

		// set input and output dependencies
		setDependencies();
	}

	/**
	 * @return resulting CAS cell
	 */
	public GeoCasCell getCasCell() {
		return casCell;
	}

	@Override
	public void compute() {
		// check if all input variables are defined
		boolean inputDefined = true;
		for (GeoElement geo : input) {
			if (!geo.isDefined()) {
				inputDefined = false;
				break;
			}
		}

		/*
		 * This was required until PhantomJS did not support Giac because
		 * missing Float64Array support:
		 * 
		 * if (kernel.getApplication().isScreenshotGenerator()) { return; }
		 */
		if (inputDefined) {
			// compute output of CAS cell and update twin GeoElement
			casCell.computeOutput();
		} else {
			casCell.setUndefined();
		}
	}

	/**
	 * This might appear when we use KeepInput and display the result => we want
	 * to show symbolic version
	 */
	@Override
	final public String toString(StringTemplate tpl) {
		// return input string, e.g. "m := c + 3"
		return casCell.getLabel(tpl);
	}

	/**
	 * Returns <cellPair> tag instead of <expression> XML
	 */
	@Override
	protected String getExpXML(StringTemplate tpl) {
		return casCell.getXML();
	}

	@Override
	public String getCommandDescription(StringTemplate tpl) {
		if (input == null)
			return null;
		if (getCasCell() != null && getCasCell().getInputVE() != null) {
			if (getCasCell().getInputVE() != null) {
				return getCasCell().getInputVE().toString(tpl);
			}
		}
		return "super" + super.getCommandDescription(tpl);

	}

	@Override
	public void update() {
		if (stopUpdateCascade) {
			return;
		}

		// update input random numbers without label
		updateUnlabeledRandomGeos();

		boolean hadTwinGeo = casCell.hasTwinGeo();

		compute();

		if (!hadTwinGeo && casCell.hasTwinGeo()) { // we got a new twin
													// GeoElement
			// reinitialize algo object
			setInputOutput();
			// set label of the newly created twin GeoElement
			casCell.setLabelOfTwinGeo();
		}

		updateDependentGeos();
	}

	// /**
	// * Initializes input and output dependencies of this algorithm.
	// * This method can be called again after the GeoCasCell's input expression
	// * has changed.
	// */
	// private void initAlgorithm() {
	// // old and new predecessors of casCell
	// TreeSet<GeoElement> oldPred = null;
	//
	// if (input != null) {
	// // remember old predecessors of casCell
	// // for updateConstructionOrder below
	// oldPred = casCell.getAllPredecessors();
	// }
	//
	// // input
	// // m := c + 3 has input variable c
	// TreeSet<GeoElement> geoVars = casCell.getGeoElementVariables();
	// GeoElement [] geos = new GeoElement[geoVars.size()];
	// input = geoVars.toArray(geos);
	//
	//
	//
	// // init output
	// // twin geo that may be created as a side effect
	// // e.g. the CAS cell m := c + 3 will create a GeoNumeric m
	// GeoElement twinGeo = casCell.getTwinGeo();
	//
	// setOutputLength(twinGeo == null ? 1 : 2);
	// setOutput(0, casCell);
	// if (twinGeo != null)
	// setOutput(1, twinGeo);
	//
	// // set dependencies
	// setInputOutput();
	//
	// // Make sure that geoCasCell comes after all its predecessors
	// // in the construction list.
	// updateConstructionOrder(oldPred);
	// }

	// TODO Consider locusequability

}
