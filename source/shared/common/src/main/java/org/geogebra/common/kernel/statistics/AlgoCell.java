/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.statistics;

import java.util.Iterator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Returns the GeoElement from an object's coordinates in the spreadsheet.
 * 
 * @author Michael, Markus
 */
public class AlgoCell extends AlgoElement {

	private GeoElement geo; // output
	private GeoNumberValue a; // input
	private GeoNumberValue b; // input

	private String currentLabel;
	private GeoElement refObject; // referenced object
	private GeoElement[] inputForUpdateSetPropagation;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param a
	 *            column
	 * @param b
	 *            row
	 */
	public AlgoCell(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons);
		this.a = a;
		this.b = b;

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();

		// register as rename listener algorithm
		kernel.registerRenameListenerAlgo(this);

		geo.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Cell;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// input is the text
		input = new GeoElement[2];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();

		// input for updateSet propagation is text and reference object
		inputForUpdateSetPropagation = new GeoElement[3];
		inputForUpdateSetPropagation[0] = input[0];
		inputForUpdateSetPropagation[1] = input[1];

		// get referenced object
		updateReferencedObject();

		// create output object as copy of referenced object
		if (refObject != null) {
			geo = refObject.copyInternal(cons);
			geo.setVisualStyle(refObject);
			geo.setUseVisualDefaults(false);
		} else {
			geo = cons.getOutputGeo();
			geo.setUndefined();
		}

		// output is a copy of the referenced object
		setOnlyOutput(geo);

		setDependencies();
	}

	/**
	 * 
	 * @return cell at given position
	 */
	public GeoElement getResult() {
		return geo;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			updateReferencedObject();
			// }

			// check if updateInput has same type
			if (refObject != null
					&& refObject.getGeoClassType() == geo.getGeoClassType()) {
				geo.set(refObject);
			} else {
				geo.setUndefined();
			}
		} else {
			geo.setUndefined();
		}
	}

	private void updateReferencedObject() {
		// get new object
		currentLabel = GeoElementSpreadsheet.getSpreadsheetCellName(
				(int) a.getDouble() - 1, (int) b.getDouble() - 1);

		/*
		 * Do not remove this algorithm from update set of old referenced
		 * object: This will speed up the calls to
		 * refObject.addToUpdateSetOnly() below, because it will always stop
		 * propagating up at refObject.
		 * 
		 * if (refObject != null) { refObject.getAlgoUpdateSet().remove(this); }
		 */

		// lookup new object for new label
		refObject = kernel.lookupLabel(currentLabel);
		inputForUpdateSetPropagation[2] = refObject;

		// change dependencies for this newly referenced object
		if (refObject != null) {
			// add this algorithm and all its dependent algos to the update set
			// of the newly referenced geo
			refObject.addToUpdateSetOnly(this);
			if (geo != null) {
				Iterator<AlgoElement> it = geo.getAlgoUpdateSet().getIterator();
				while (it.hasNext()) {
					refObject.addToUpdateSetOnly(it.next());
				}
			}
		}
	}

	/**
	 * Returns an input array with the text and the referenced geo
	 */
	@Override
	public GeoElement[] getInputForUpdateSetPropagation() {
		if (refObject == null) {
			return input;
		}
		return inputForUpdateSetPropagation;
	}

}
