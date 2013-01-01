/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;

import java.util.Iterator;

/**
 * Returns the GeoElement from an object's coordinates in the spreadsheet.
 * 
 * @author Michael, Markus
 */
public class AlgoCell extends AlgoElement {

	private GeoElement geo; // output
	private NumberValue a, b; // input

	private String currentLabel;
	private GeoElement refObject; // referenced object
	private GeoElement[] inputForUpdateSetPropagation;

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param a column
	 * @param b row
	 */
	public AlgoCell(Construction cons, String label, NumberValue a,
			NumberValue b) {
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
		super.setOutputLength(1);
		super.setOutput(0, geo);

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
		} else
			geo.setUndefined();
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
				Iterator<AlgoElement> it = geo.getAlgoUpdateSet()
						.getIterator();
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

	

	// TODO Consider locusequability

}
