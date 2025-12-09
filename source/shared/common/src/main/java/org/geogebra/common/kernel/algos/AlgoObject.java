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

package org.geogebra.common.kernel.algos;

import java.util.Iterator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Returns the GeoElement from an object's label.
 * 
 * @author Michael, Markus
 */
public class AlgoObject extends AlgoElement {

	private GeoElement geo; // output
	private GeoText text; // input

	private String currentLabel;
	private GeoElement refObject; // referenced object
	private GeoElement[] inputForUpdateSetPropagation;

	/**
	 * Creates new algorithm for Object[name].
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param text
	 *            object name
	 */
	public AlgoObject(Construction cons, String label, GeoText text) {
		super(cons);
		this.text = text;

		// Object["A" + d] might gives "A2.00" if significant figures used
		// globally
		// want it to give "A2"
		if (!text.isLabelSet()) {
			text.setPrintDecimals(0, true);
		}

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();

		// register as rename listener algorithm
		kernel.registerRenameListenerAlgo(this);

		geo.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Object;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// input is the text
		input = new GeoElement[1];
		input[0] = text;

		// input for updateSet propagation is text and reference object
		inputForUpdateSetPropagation = new GeoElement[2];
		inputForUpdateSetPropagation[0] = text;

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
	 * @return object with given name
	 */
	public GeoElement getResult() {
		return geo;
	}

	@Override
	public final void compute() {
		// did name of object change?
		// removed - doesn't update when referenced object deleted
		// if (currentLabel != text.getTextString() || refObject == null) {
		updateReferencedObject();
		// }

		// check if updateInput has same type
		if (refObject != null
				&& refObject.getGeoClassType() == geo.getGeoClassType()) {
			geo.set(refObject);
		} else {
			geo.setUndefined();
		}
	}

	private void updateReferencedObject() {
		// get new object
		currentLabel = text.getTextString();

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
		inputForUpdateSetPropagation[1] = refObject;

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
