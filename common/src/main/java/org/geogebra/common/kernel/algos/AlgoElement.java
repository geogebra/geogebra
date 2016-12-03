/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoElement.java
 *
 * Created on 30. August 2001, 21:36
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EuclidianViewCE;
import org.geogebra.common.kernel.GTemplate;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoScriptAction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * AlgoElement is the superclass of all algorithms.
 * 
 * @author Markus
 */
public abstract class AlgoElement extends ConstructionElement implements
		EuclidianViewCE {
	/** input elements */
	public GeoElement[] input;
	/**
	 * list of output
	 * 
	 * @deprecated use setOutputLength(), setOutput(), getOutputLength(),
	 *             getOutput() instead
	 */
	@Deprecated
	private GeoElement[] output;
	private GeoElementND[] efficientInput;

	private boolean isPrintedInXML = true;
	protected boolean stopUpdateCascade = false;

	/**
	 * Creates new algorithm
	 * 
	 * @param c
	 *            construction
	 */
	public AlgoElement(Construction c) {
		this(c, true);
	}

	/**
	 * Creates new algorithm
	 * 
	 * @param c
	 *            construction
	 * @param addToConstructionList
	 *            true to add this to construction list
	 */
	protected AlgoElement(Construction c, boolean addToConstructionList) {
		super(c);

		if (addToConstructionList) {
			c.addToConstructionList(this, false);
		}
	}

	/**
	 * add the algo to the construction (if disabled before by
	 * Kernel.silentMode)
	 */
	public void addToConstructionList() {
		cons.addToConstructionList(this, false);
	}

	/**
	 * initialize output list
	 * 
	 * @param n
	 *            Output length
	 * 
	 */
	protected void setOutputLength(int n) {
		output = new GeoElement[n];
	}

	/**
	 * initialize output list
	 * 
	 * @param g
	 *            only output
	 * 
	 */
	protected void setOnlyOutput(GeoElementND g) {
		output = new GeoElement[1];
		output[0] = g.toGeoElement();
	}

	/**
	 * set output number i
	 * 
	 * @param i
	 *            index
	 * @param geo
	 *            output geo
	 */
	protected void setOutput(int i, GeoElement geo) {
		output[i] = geo;
	}

	/**
	 * sets the output to the given array
	 * 
	 * @param geo
	 *            the output to set
	 */
	protected void setOutput(GeoElement[] geo) {
		output = geo;
	}

	/**
	 * @param i
	 *            index
	 * @return output geo at position i
	 */
	public GeoElement getOutput(int i) {
		return output[i];
	}

	/**
	 * 
	 * @return number of outputs
	 */
	public int getOutputLength() {
		if (output == null) {
			return 0;
		}

		return output.length;
	}

	/**
	 * list of registered outputHandler of this AlgoElement
	 */
	private List<OutputHandler<?>> outputHandler;
	private boolean mayHaveRandomAncestors = true;

	/**
	 * One OutputHandler has been changed, we put together the new output.
	 */
	protected void refreshOutput() {
		Iterator<OutputHandler<?>> it = getOutputHandler().iterator();
		int n = 0;
		while (it.hasNext()) {
			n += it.next().size();
		}
		output = new GeoElement[n];
		it = getOutputHandler().iterator();
		int i = 0;
		while (it.hasNext()) {
			OutputHandler<?> handler = it.next();
			for (int k = 0; k < handler.size(); k++) {
				output[i++] = handler.getElement(k);
			}
		}
	}

	/**
	 * OutputHandler can manage several different output types, each with
	 * increasing length. For each occurring type, you need one OutputHandler in
	 * the Subclass (or OutputHandler<GeoElement> if the type doesn't matter).<br />
	 * Don't use this if you are accessing output directly, or use setOutput or
	 * setOutputLength, because the OutputHandler changes output on it's own and
	 * will 'overwrite' any direct changes.
	 * 
	 * @param <T>
	 *            extends GeoElement: type of the OutputHandler
	 */
	public class OutputHandler<T extends GeoElement> {
		private elementFactory<T> fac;
		private ArrayList<T> outputList;
		private String[] labels;
		private String indexLabel;
		/**
		 * use Labels for this outputs
		 */
		public boolean setLabels;

		/** number of labels already set */
		private int labelsSetLength = 0;

		/**
		 * @param fac
		 *            elementFactory to create new Elements of type T
		 */
		public OutputHandler(elementFactory<T> fac) {
			this.fac = fac;
			outputList = new ArrayList<T>();
			if (getOutputHandler() == null) {
				setOutputHandler(new ArrayList<OutputHandler<?>>());
			}
			getOutputHandler().add(this);
		}

		/**
		 * @param fac
		 *            elementFactory to create new Elements of type T
		 * @param labels
		 *            array of labels to use for Outputelements.
		 */
		public OutputHandler(elementFactory<T> fac, String[] labels) {
			this(fac);
			this.labels = labels;
			if (labels != null) {
				adjustOutputSize(labels.length);
			}
		}

		/**
		 * Remove this from handler
		 */
		public void removeFromHandler() {
			getOutputHandler().remove(this);
		}

		/**
		 * @param size
		 *            makes room in this OutputHandler for size Objects.<br />
		 *            if there are currently more objects than size, they become
		 *            undefined.
		 */
		public void adjustOutputSize(int size) {
			adjustOutputSize(size, true);
		}

		/**
		 * makes room in this OutputHandler for size Objects.<br />
		 * if there are currently more objects than size, they become undefined.
		 *
		 * @param size
		 *            new size
		 * 
		 * @param setDependencies
		 *            set dependencies
		 */
		public void adjustOutputSize(int size, boolean setDependencies) {
			if (outputList.size() < size) {
				augmentOutputSize(size - outputList.size(), setDependencies);
			} else {
				for (int i = size; i < outputList.size(); i++) {
					outputList.get(i).setUndefined();
				}
			}
		}

		/**
		 * increases size of output by given number
		 * 
		 * @param size
		 *            size increment
		 */
		public void augmentOutputSize(int size) {
			augmentOutputSize(size, true);
		}

		/**
		 * increases size of output to given size
		 * 
		 * @param increment
		 *            new size
		 * @param setDependencies
		 *            true to set dependencies right away
		 */
		public void augmentOutputSize(int increment, boolean setDependencies) {
			int size = increment + outputList.size();
			outputList.ensureCapacity(size);
			for (int i = outputList.size(); i < size; i++) {
				T newGeo = fac.newElement();
				outputList.add(newGeo);
				if (setDependencies) {
					setOutputDependencies(newGeo);
				}
			}
			refreshOutput();

			if (setLabels) {
				updateLabels();
			}
		}

		/**
		 * add the geos list to the output
		 * 
		 * @param geos
		 *            geos to be added
		 * @param setDependencies
		 *            says if the dependencies have to be set for this output
		 * @param refresh
		 *            if true, output array is recomputed using outputhandler
		 */
		public void addOutput(T[] geos, boolean setDependencies, boolean refresh) {
			for (int i = 0; i < geos.length; i++) {
				addOutput(geos[i], setDependencies);
			}

			if (refresh) {
				refreshOutput();
			}
		}

		/**
		 * @param geo
		 *            geo to be added
		 * @param setDependencies
		 *            true to set dependencies of given geo now
		 */
		public void addOutput(T geo, boolean setDependencies) {
			outputList.add(geo);
			if (setDependencies) {
				setOutputDependencies(geo);
			}
		}

		/**
		 * set setLabels to true
		 * 
		 * @param labels
		 *            use this Strings as labels. If labels == null, default
		 *            labels are used
		 */
		public void setLabels(String[] labels) {
			this.labels = labels;
			// setLabels=true;
			setLabels = !cons.isSuppressLabelsActive();
			if (labels != null) {
				if (labels.length == 1) {
					setIndexLabels(labels[0]);
				}
				adjustOutputSize(labels.length);
			} else {
				updateLabels();
			}
		}

		/**
		 * set setLabels to true
		 * 
		 * @param label
		 *            use this String as indexed labels.
		 */
		public void setIndexLabels(String label) {
			this.indexLabel = label;
			// setLabels=true;
			setLabels = !cons.isSuppressLabelsActive();
			updateLabels();
		}

		/**
		 * assigns Labels to unlabeled elements
		 */
		public void updateLabels() {
			for (int i = 0; i < outputList.size(); i++) {
				if (!outputList.get(i).isLabelSet()) {
					if (indexLabel != null) { // use indexed label
						outputList.get(i).setLabel(
								outputList.get(i).getIndexLabel(indexLabel));
					} else if ((labels != null) && (i < labels.length)) {
						outputList.get(i).setLabel(labels[i]);
					} else {
						outputList.get(i).setLabel(null);
					}
				}
			}
		}

		/**
		 * set all geos undefined
		 */
		public void setUndefined() {
			for (int i = 0; i < outputList.size(); i++) {
				outputList.get(i).setUndefined();
			}
		}

		/**
		 * call update for each geo
		 */
		public void update() {
			for (int i = 0; i < outputList.size(); i++) {
				outputList.get(i).update();
			}
		}

		/**
		 * call update for each geo parent algo
		 */
		public void updateParentAlgorithm() {
			for (int i = 0; i < outputList.size(); i++) {
				outputList.get(i).getParentAlgorithm().update();
			}
		}

		/**
		 * set the label to the next geo with no label (or create new one)
		 * 
		 * @param label
		 *            label
		 * @return corresponding geo
		 */
		public T addLabel(String label) {
			T geo;
			if (labelsSetLength < outputList.size()) {
				geo = getElement(labelsSetLength);
				// Application.debug(label+", geo="+geo);
			} else {
				geo = fac.newElement();
				outputList.add(geo);
				setOutputDependencies(geo);
				refreshOutput();
			}

			labelsSetLength++;
			geo.setLabel(label);

			return geo;

		}

		/**
		 * Returns output element at given position
		 * 
		 * @param i
		 *            position (starting with 0)
		 * @return get the i<sup>th</sup> Element of this OutputHandler
		 */
		public T getElement(int i) {
			return outputList.get(i);
		}

		/**
		 * @param a
		 *            type of the Output
		 * @return content of this OutputHandler as array
		 */
		public T[] getOutput(T[] a) {
			// Application.debug("getOutput: "+Arrays.deepToString(outputList.toArray())+" length:"+outputList.toArray().length);
			return outputList.toArray(a);
		}

		/**
		 * @return size of the content of this OutputHandler
		 */
		public int size() {
			return outputList.size();
		}

		public void setLabelsMulti(String[] labels2) {
			// if only one label (e.g. "A") for more than one output, new labels
			// will be A_1, A_2, ...
			if (labels2 != null && labels2.length == 1 &&
			// outputPoints.size() > 1 &&
					labels2[0] != null && !labels2[0].equals("")) {
				this.setIndexLabels(labels2[0]);
			} else {

				this.setLabels(labels2);
				this.setIndexLabels(getElement(0).getLabelSimple());
			}

		}

	}

	/**
	 * Produces objects of type &lt;S>
	 * 
	 * @param <S>
	 *            element type
	 */
	public interface elementFactory<S extends GeoElement> {

		/**
		 * this is called by the OutputHandler every Time a new Element is
		 * needed.
		 * 
		 * @return a new Element of type S. (e.g. new GeoPoint(cons))
		 */
		public S newElement();
	}

	/*
	 * needed so that JavaScript commands work:
	 * ggbApplet.getCommandString(objName); ggbApplet.getValueString(objName);
	 */

	/**
	 * Converts algorithm identifier into command name
	 * 
	 * @param classname
	 *            algorithm identifier
	 * @return internal command name
	 */
	final static String getCommandString(GetCommand classname) {
		// init rbalgo2command if needed
		// for translation of Algo-classname to command name

		if (classname == null)
			return "";
		// translate algorithm class name to internal command name
		return classname.getCommand();
	}

	/**
	 * in setInputOutput() the member vars input and output are set
	 */
	abstract protected void setInputOutput();

	/**
	 * in compute() the output is derived from the input
	 */
	public abstract void compute();

	/**
	 * Inits this algorithm for the near-to-relationship. This is important to
	 * init the intersection algorithms when loading a file, so that they have a
	 * look at the current location of their output points.
	 */
	public void initForNearToRelationship() {
		// overriden in subclasses
	}

	/**
	 * @return whether this algo has NEAR-TO relations (ie ambiguous output =>
	 *         we pick the nearest possibility to last output)
	 */
	public boolean isNearToAlgorithm() {
		return false;
	}

	// public static double startTime, endTime;
	// public static double computeTime, updateTime;
	// public static double counter;

	@Override
	public void update() {
		if (stopUpdateCascade) {
			return;
		}

		updateUnlabeledRandomGeos();

		// counter++;
		// startTime = System.currentTimeMillis();

		// compute output from input
		compute();

		// endTime = System.currentTimeMillis();
		// computeTime += (endTime - startTime);
		// startTime = System.currentTimeMillis();

		updateDependentGeos();

		// endTime = System.currentTimeMillis();
		// updateTime += (endTime - startTime );
	}

	/**
	 * update input random numbers without label
	 * 
	 * @return whether something was updated
	 */
	public boolean updateUnlabeledRandomGeos() {
		if (!mayHaveRandomAncestors) {
			return false;
		}
		boolean ret = false;
		for (int i = 0; i < input.length; i++) {
			if (!input[i].isLabelSet()) {
				if (input[i].getParentAlgorithm() != null) {
					// if Mod[RandomBetween[1,3],2], we must go deeper and
					// update
					// if just RandomBetween[1,3] we just update
					if (input[i].getParentAlgorithm()
							.updateUnlabeledRandomGeos()
							|| input[i].isRandomGeo()) {
						input[i].getParentAlgorithm().compute();
						ret = true;
					}
				} else {
					// random() has no parent algo
					if (input[i].isRandomGeo()) {
						input[i].updateRandomGeo();
						ret = true;
					}
				}
			}
		}
		if (!ret) {
			this.mayHaveRandomAncestors = false;
		}
		return ret;
	}

	/**
	 * update output geos
	 */
	protected void updateDependentGeos() {
		// update dependent objects
		for (int i = 0; i < getOutputLength(); i++) {
			getOutput(i).update();
		}
	}

	/**
	 * Updates all AlgoElements in the given ArrayList. Note: this method is
	 * more efficient than calling updateCascade() for all individual
	 * AlgoElements.
	 * 
	 * @param algos
	 *            list of algos that need updating
	 */
	public static void updateCascadeAlgos(ArrayList<AlgoElement> algos) {
		if (algos == null) {
			return;
		}
		int size = algos.size();
		if (size == 0) {
			return;
		}

		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (int i = 0; i < size; i++) {
			AlgoElement algo = algos.get(i);
			algo.compute();
			for (int j = 0; j < algo.getOutputLength(); j++) {
				algo.getOutput(j).update();
				geos.add(algo.getOutput(j));
			}
		}

		// update all geos
		GeoElement.updateCascade(geos, getTempSet(), true);
	}

	private static TreeSet<AlgoElement> tempSet;

	private static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}

	// public part
	/**
	 * @return list of output
	 */
	public GeoElement[] getOutput() {
		return output;
	}

	/**
	 * @return array of input elements
	 */
	final public GeoElement[] getInput() {
		return input;
	}

	/**
	 * Note : maybe overridden for xOy plane additionnal input
	 * 
	 * @param i
	 *            index
	 * @return i-th input
	 */
	public GeoElementND getInput(int i) {
		return input[i];
	}

	public GeoElement[] getInputForUpdateSetPropagation() {
		return input;
	}

	/**
	 * DEPENDENCY handling the dependencies are treated here by using input and
	 * output which must be set by every algorithm. Note: setDependencies() is
	 * called by every algorithm in topological order (i.e. possible helper
	 * algos call this method before the using algo does).
	 * 
	 * @see #setInputOutput()
	 */
	final protected void setDependencies() {
		// dependents on input
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		doSetDependencies();
	}

	/**
	 * Adds this algorithm to the update set of all inputs without adding input
	 * dependencies.
	 */
	final protected void setDependenciesOutputOnly() {

		for (int i = 0; i < input.length; i++) {
			input[i].addToUpdateSetOnly(this);
		}

		doSetDependencies();
	}

	protected void doSetDependencies() {
		this.mayHaveRandomAncestors = true;
		setOutputDependencies();
		cons.addToAlgorithmList(this);
	}

	protected final void setEfficientDependencies(GeoElement[] standardInput,
			GeoElementND[] efficientInput) {
		// dependens on standardInput
		for (int i = 0; i < standardInput.length; i++) {
			standardInput[i].addToAlgorithmListOnly(this);
		}

		// we use efficientInput for updating
		for (int i = 0; i < efficientInput.length; i++) {
			efficientInput[i].addToUpdateSetOnly(this);
		}

		// input is standardInput
		input = standardInput;
		this.efficientInput = efficientInput;

		doSetDependencies();
	}

	private void setOutputDependencies() {

		// parent algorithm of output
		for (int i = 0; i < getOutputLength(); i++) {

			setOutputDependencies(getOutput(i));

		}
	}

	/**
	 * sets the given geo to be child of this algo
	 * 
	 * @param output
	 */
	protected void setOutputDependencies(GeoElement output) {
		// parent algorithm of output
		output.setParentAlgorithm(this);

		// every algorithm with an image as output
		// should be notified about view changes
		if (output.isGeoImage()) {
			cons.registerEuclidianViewCE(this);
		}

		// make sure that every output has same construction as this algorithm
		// this is important for macro constructions that have input geos from
		// outside the macro: the output should be part of the macro
		// construction!
		if (cons != output.cons) {
			output.setConstruction(cons);
		}

	}

	@Override
	public boolean euclidianViewUpdate() {
		update();
		return false;
	}

	/** flag stating whether remove() on this algo was already called */
	protected boolean removed = false;

	/**
	 * delete dependent objects
	 */
	protected void removeOutput() {
		for (int i = 0; i < getOutputLength(); i++) {
			getOutput(i).doRemove();
		}
	}

	/**
	 * Removes algorithm and all dependent objects from construction.
	 */

	@Override
	public void remove() {
		if (removed)
			return;
		removed = true;
		cons.removeFromConstructionList(this);
		cons.removeFromAlgorithmList(this);

		// delete dependent objects
		removeOutput();

		// delete from algorithm lists of input
		for (int i = 0; i < input.length; i++) {
			if (!protectedInput && input[i].canBeRemovedAsInput()
					&& !input[i].isLabelSet() && !input[i].isGeoCasCell())
				input[i].remove();
			input[i].removeAlgorithm(this);
		}

		// delete from algorithm lists of efficient input
		if (efficientInput != null) {
			for (int i = 0; i < efficientInput.length; i++) {
				efficientInput[i].removeAlgorithm(this);
			}
		}
	}

	public boolean protectedInput = false;

	/**
	 * sets if the "not labeled" inputs are protected from remove
	 * 
	 * @param flag
	 *            flag
	 */
	public void setProtectedInput(boolean flag) {
		protectedInput = flag;
	}

	/**
	 * Tells this algorithm to react on the deletion of one of its outputs.
	 * 
	 * @param out
	 *            output to be removed
	 */
	public void remove(GeoElement out) {
		remove();
	}

	/**
	 * Calls doRemove() for all output objects of this algorithm except for
	 * keepGeo.
	 * 
	 * @param keepGeo
	 *            geo to be kept
	 */
	public void removeOutputExcept(GeoElement keepGeo) {

		for (int i = 0; i < getOutputLength(); i++) {
			GeoElement geo = getOutput(i);
			if (geo != keepGeo) {
				geo.doRemove();
			}
		}
	}

	/**
	 * Tells all views to add all output GeoElements of this algorithm.
	 */
	@Override
	final public void notifyAdd() {
		for (int i = 0; i < getOutputLength(); ++i) {
			getOutput(i).notifyAdd();
		}
	}

	/**
	 * Tells all views to remove all output GeoElements of this algorithm.
	 */
	@Override
	final public void notifyRemove() {
		for (int i = 0; i < getOutputLength(); ++i) {
			getOutput(i).notifyRemove();
		}
	}

	@Override
	final public GeoElementND[] getGeoElements() {
		return getOutput();
	}

	/**
	 * Returns whether all output objects have the same type.
	 * 
	 * @return whether all outputs have the same type
	 */
	final public boolean hasSingleOutputType() {
		GeoClass type = getOutput(0).getGeoClassType();

		for (int i = 1; i < getOutputLength(); ++i) {
			if (getOutput(i).getGeoClassType() != type) {
				return false;
			}
		}
		return true;
	}

	@Override
	final public boolean isAlgoElement() {
		return true;
	}

	@Override
	final public boolean isGeoElement() {
		return false;
	}

	/**
	 * Returns true iff one of the output geos is shown in the construction
	 * protocol
	 */
	@Override
	final public boolean isConsProtocolBreakpoint() {
		for (int i = 0; i < getOutputLength(); i++) {
			if (getOutput(i).isConsProtocolBreakpoint()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compares using getConstructionIndex() to order algos in update order.
	 * Note: 0 is only returned for this == obj.
	 * 
	 * @overwrite ConstructionElement.compareTo()
	 */
	@Override
	public int compareTo(ConstructionElement obj) {
		if (this == obj) {
			return 0;
		}

		ConstructionElement ce = obj;
		int thisIndex = getConstructionIndex();
		int objIndex = ce.getConstructionIndex();
		if (thisIndex == objIndex) {
			// two help algorithms can have same construction index
			// if they have same parent geos, see #2693
			// in this case we use the creation ID to distinguish them
			return super.compareTo(obj);
		} else if (thisIndex < objIndex) {
			return -1;
		}
		return 1;
	}

	/**
	 * Returns construction index in current construction. For an algorithm that
	 * is not in the construction list, the largest construction index of its
	 * inputs is returned.
	 */
	@Override
	public int getConstructionIndex() {
		int index = super.getConstructionIndex();
		// algorithm is in construction list
		if (index >= 0) {
			return index;
		}

		// algorithm is not in construction list
		for (int i = 0; i < input.length; i++) {
			int temp = input[i].getConstructionIndex();
			if (temp > index) {
				index = temp;
			}
		}
		return index;
	}

	/**
	 * Returns the smallest possible construction index for this object in its
	 * construction.
	 */
	@Override
	public int getMinConstructionIndex() {
		// index must be greater than every input's index
		int max = 0;
		for (int i = 0; i < input.length; ++i) {
			int index = input[i].getConstructionIndex();
			if (index > max) {
				max = index;
			}
		}
		return max + 1;
	}

	/**
	 * Returns the largest possible construction index for this object in its
	 * construction.
	 */
	@Override
	public int getMaxConstructionIndex() {
		// index is less than minimum of all dependent algorithm's index of all
		// output
		ArrayList<AlgoElement> algoList;
		int size, index;
		int min = cons.steps();
		for (int k = 0; k < getOutputLength(); ++k) {
			algoList = getOutput(k).getAlgorithmList();
			size = algoList.size();
			for (int i = 0; i < size; ++i) {
				index = algoList.get(i).getConstructionIndex();
				if (index < min) {
					min = index;
				}
			}
		}
		return min - 1;
	}



	/**
	 * adds all predecessors of this object to the given list the set is kept
	 * topologically sorted
	 * 
	 * @param set
	 *            set of geos to be added
	 * @param onlyIndependent
	 *            whether only indpendent geos should be added
	 */
	@Override
	public final void addPredecessorsToSet(TreeSet<GeoElement> set,
			boolean onlyIndependent) {
		for (int i = 0; i < input.length; i++) {
			GeoElement parent = input[i];

			if (!set.contains(parent)) {
				if (!onlyIndependent) {
					set.add(parent);
				}
				parent.addPredecessorsToSet(set, onlyIndependent);
			}
		}
	}

	public final void addPredecessorsToSet(TreeSet<GeoElement> set,
			Inspecting check) {
		for (int i = 0; i < input.length; i++) {
			GeoElement parent = input[i];

			if (!set.contains(parent)) {
				if (check.check(parent)) {
					set.add(parent);
				}
				parent.addPredecessorsToSet(set, check);
			}
		}
	}

	public final void addRandomizablePredecessorsToSet(TreeSet<GeoElement> set) {
		for (int i = 0; i < input.length; i++) {
			GeoElement parent = input[i];

			if (!set.contains(parent)) {
				parent.addRandomizablePredecessorsToSet(set);
			}
		}
	}

	/**
	 * Returns all moveable input points of this algorithm.
	 * 
	 * @return list of moveable input points
	 */
	public ArrayList<GeoPointND> getFreeInputPoints() {
		if (freeInputPoints == null) {
			freeInputPoints = new ArrayList<GeoPointND>(input.length);

			// don't use free points from dependent algos with expression trees
			if (!(this instanceof DependentAlgo)) {
				for (int i = 0; i < input.length; i++) {
					if (input[i].isGeoPoint() && input[i].isIndependent()) {
						freeInputPoints.add((GeoPointND) input[i]);
					}
				}
			}
		}

		return freeInputPoints;
	}

	private ArrayList<GeoPointND> freeInputPoints;

	/**
	 * Returns all input points of this algorithm.
	 * 
	 * @return list of input points
	 */
	public ArrayList<GeoPointND> getInputPoints() {
		if (inputPoints == null) {
			inputPoints = new ArrayList<GeoPointND>(input.length);
			for (int i = 0; i < input.length; i++) {
				if (input[i].isGeoPoint()) {
					inputPoints.add((GeoPointND) input[i]);
				}
			}
		}

		return inputPoints;
	}

	private ArrayList<GeoPointND> inputPoints;

	@Override
	final public boolean isIndependent() {
		return false;
	}

	protected StringBuilder sbAE = new StringBuilder();

	@Override
	public String getNameDescription() {
		sbAE.setLength(0);
		if (getOutput(0).isLabelSet()) {
			sbAE.append(getOutput(0).getNameDescription());
		}
		for (int i = 1; i < getOutputLength(); ++i) {
			if (getOutput(i).isLabelSet()) {
				sbAE.append("\n");
				sbAE.append(getOutput(i).getNameDescription());
			}
		}
		return sbAE.toString();
	}

	public String getAlgebraDescriptionRegrOut(StringTemplate tpl) {
		sbAE.setLength(0);

		if (getOutput(0).isLabelSet()) {
			sbAE.append(getOutput(0).getAlgebraDescriptionRegrOut(tpl));
		}
		for (int i = 1; i < getOutputLength(); ++i) {
			if (getOutput(i).isLabelSet()) {
				sbAE.append("\n");
				sbAE.append(getOutput(i).getAlgebraDescriptionRegrOut(tpl));
			}
		}
		return sbAE.toString();
	}

	@Override
	public String getDefinitionDescription(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public String getDefinition(StringTemplate tpl) {
		String def = getDefinitionName(tpl);

		// command name
		if (def.equals("Expression")) {
			return toString(tpl);
		}
		// #2706
		if (input == null)
			return null;
		sbAE.setLength(0);
		if (tpl.isPrintLocalizedCommandNames()) {
			sbAE.append(getLoc().getCommand(def));
		} else {
			sbAE.append(def);
		}

		int length = getInputLengthForCommandDescription();

		sbAE.append(tpl.leftSquareBracket());
		// input legth is 0 for ConstructionStep[]
		if (length > 0) {
			sbAE.append(getInput(0).getLabel(tpl));
		}
		for (int i = 1; i < length; ++i) {
			sbAE.append(", ");
			appendCheckVector(sbAE, getInput(i), tpl);
		}
		sbAE.append(tpl.rightSquareBracket());
		return sbAE.toString();

	}

	/*
	 * see #1377 g:X = (-5, 5) + t (4, -3)
	 */
	private void appendCheckVector(StringBuilder sb, GeoElementND geo,
			StringTemplate tpl) {
		String cmd = geo.getLabel(tpl);
		if (geo.isGeoVector()) {
			String vectorCommand = "Vector[";
			if (tpl.isPrintLocalizedCommandNames())
				vectorCommand = getLoc().getCommand("Vector") + "["; // want it
																		// translated
																		// eg
																		// for
																		// redefine
																		// dialog
			boolean needsWrapping = !geo.isLabelSet()
					&& !cmd.startsWith(vectorCommand);

			if (needsWrapping) {
				sb.append(vectorCommand);
			}
			sb.append(cmd);
			if (needsWrapping) {
				sb.append(']');
			}
		} else {
			sb.append(cmd);
		}
	}

	@Override
	@Deprecated
	public final String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	/**
	 * translate class name to internal command name GeoGebra File Format
	 * 
	 * @param tpl
	 *            string template
	 * 
	 * @return internal command name
	 */
	public String getDefinitionName(StringTemplate tpl) {
		String cmdname;
		GetCommand classname;
		// get class name
		classname = getClassName();
		// translate algorithm class name to internal command name
		cmdname = getCommandString(classname);
		// dependent algorithm is an "Expression"
		if (!cmdname.equals("Expression")) {
			if (tpl.isUseTempVariablePrefix()) {
				// protect GeoGebra commands when sent to CAS
				// e.g. Element[list, 1] becomes ggbtmpvarElement[list, 1] to
				// make sure that the CAS does not evaluate this command, see
				// #1447
				cmdname = tpl.printVariableName(cmdname);
			}
		}
		return cmdname;
	}

	/**
	 * Returns this algorithm and it's output objects (GeoElement) in XML
	 * format. GeoGebra File Format.
	 */
	@Override
	public void getXML(boolean getlistenersToo, StringBuilder sb) {
		getXML(sb, true);
	}

	@Override
	public void getXML_OGP(StringBuilder sb) {
		getXML_OGP(sb, true);
	}

	/**
	 * @return XML representation of this algo, including output objects
	 */
	public String getXML() {
		StringBuilder sb = new StringBuilder();
		getXML(sb, true);
		return sb.toString();
	}

	/**
	 * Adds XML representation of this algo to the string builder
	 * 
	 * @param sb
	 *            string builder
	 * @param includeOutputGeos
	 *            true to include output geos
	 */
	public final void getXML(StringBuilder sb, boolean includeOutputGeos) {
		// this is needed for helper commands like
		// intersect for single intersection points
		if (!isPrintedInXML) {
			return;
		}

		// turn off eg Arabic digits

		// USE INTERNAL COMMAND NAMES IN EXPRESSION
		try {
			// command
			StringTemplate tpl = StringTemplate.xmlTemplate;
			String cmdname = getDefinitionName(tpl);
			if (cmdname.equals("Expression")) {
				sb.append(getExpXML(tpl));

			} else {
				sb.append(getCmdXML(cmdname, tpl));
			}

			if (includeOutputGeos) {// && output != null) {
				getOutputXML(sb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Adds XML representation of this algo to the string builder OGP version
	 * 
	 * @param sb
	 *            string builder
	 * @param includeOutputGeos
	 *            true to include output geos
	 */
	public final void getXML_OGP(StringBuilder sb, boolean includeOutputGeos) {
		// this is needed for helper commands like
		// intersect for single intersection points
		if (!isPrintedInXML) {
			return;
		}

		// turn off eg Arabic digits

		// USE INTERNAL COMMAND NAMES IN EXPRESSION
		try {
			// command
			StringTemplate tpl = StringTemplate.ogpTemplate;
			String cmdname = getDefinitionName(tpl);
			if (cmdname.equals("Expression")) {
				sb.append(getExpXML(tpl));
			} else {
				sb.append(getCmdXML(cmdname, tpl));
			}

			if (includeOutputGeos) {// && output != null) {
				getOutputXML(sb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * concatenate output XML to sb
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getOutputXML(StringBuilder sb) {
		// output
		GeoElement geo;
		for (int i = 0; i < getOutputLength(); i++) {
			geo = getOutput(i);
			// save only GeoElements that have a valid label
			// Application.debug(geo.toString()+"--"+geo.isLabelSet());
			if (geo.isLabelSet()) {
				geo.getXML(false, sb);
			}
		}
	}

	/**
	 * Expressions should be shown as out = expression e.g. <expression
	 * label="u" exp="a + 7 b"/>
	 * 
	 * @param tpl
	 *            string template
	 * @return expression XML tag
	 */
	protected String getExpXML(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("<expression");
		// add label
		if (/* output != null && */getOutputLength() == 1) {
			if (getOutput(0).isLabelSet()) {
				sb.append(" label=\"");
				StringUtil.encodeXML(sb, getOutput(0).getLabel(tpl));
				sb.append("\"");
			}
		}
		// add expression
		sb.append(" exp=\"");
		StringUtil.encodeXML(sb, toExpString(tpl));
		sb.append("\"");

		// make sure that a vector remains a vector and a point remains a point
		if (getOutputLength() > 0)// (output != null)
		{
			if (getOutput(0).isGeoPoint()) {
				sb.append(" type=\"point\"");
			} else if (getOutput(0).isGeoVector()) {
				sb.append(" type=\"vector\"");
			} else if (getOutput(0).isGeoLine()) {
				sb.append(" type=\"line\"");
			} else if (getOutput(0).isGeoPlane()) {
				sb.append(" type=\"plane\"");
			} else if (getOutput(0).isGeoConic()) {
				sb.append(" type=\"conic\"");
			} else if (getOutput(0).isGeoQuadric()) {
				sb.append(" type=\"quadric\"");
			} else if (getOutput(0).isGeoImplicitPoly()) {
				sb.append(" type=\"implicitPoly\"");
			}

		}

		// expression
		sb.append(" />\n");

		return sb.toString();
	}

	// standard command has cmdname, output, input
	private String getCmdXML(String cmdname, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		if (getOutputLength() > 0 && getOutput(0) instanceof GeoScriptAction) {
			return "";
		}
		sb.append("<command name=\"");
		if ("".equals(cmdname))
			sb.append("AlgoNonCommand"); // In such cases we may want to add a
											// Command for the Algo
		else
			sb.append(cmdname);
		sb.append("\"");
		if (!"".equals(cmdname)
				&& (this instanceof AlgoListElement
						|| this.getClassName().equals(Commands.Cell) || this
						.getClassName().equals(Commands.Object))) {
			// need to write the geo type in the XML if it's undefined
			// so that it's the same type when the file is loaded again
			sb.append(" type=\"");
			sb.append(getOutput()[0].getXMLtypeString());
			sb.append("\"");
		}
		if (getOutputLength() > 0 && getOutput(0) instanceof GeoFunction) {
			// need to write the geo type in the XML if it's undefined
			// so that it's the same type when the file is loaded again
			String varStr = ((GeoFunction) getOutput(0))
					.getVarString(StringTemplate.defaultTemplate);
			if (!"x".equals(varStr)) {
				sb.append(" var=\"");
				sb.append(varStr);
				sb.append("\"");
			}
		}
		sb.append(">\n");
		if (getInputLength() > 0
				&& getInput(0) instanceof CasEvaluableFunction
				&& !getInput(0).isLabelSet()) {

			((CasEvaluableFunction) getInput(0)).printCASEvalMapXML(sb);
		}
		// add input information
		if (input != null) {
			sb.append("\t<input");
			for (int i = 0; i < getInputLengthForXML(); i++) {
				sb.append(" a");
				sb.append(i);
				// attribute name is input No.
				sb.append("=\"");

				GeoElementND inputGeo = getInput(i);
				String cmd = StringUtil.encodeXML(inputGeo.getLabel(tpl));

				// ensure a vector stays a vector!
				// eg g:X = (-5, 5) + t (4, -3)
				if (inputGeo.isGeoVector() && !inputGeo.isLabelSet()
						&& !cmd.startsWith("Vector[")) {
					// add Vector[ ] command around argument
					// to make sure that this really becomes a vector again
					// eg g:X = (-5, 5) + t (4, -3)
					sb.append("Vector["); // in XML, so don't want this
											// translated
					sb.append(cmd);
					sb.append("]");
				} else {
					// standard case
					sb.append(cmd);
				}

				sb.append("\"");
			}
			sb.append("/>\n");
		}

		// add output information
		if (getOutputLength() > 0)
			getCmdOutputXML(sb, tpl);

		sb.append("</command>\n");
		return sb.toString();
	}

	/**
	 * 
	 * @return input length
	 */
	final public int getInputLength() {
		return input == null ? 0 : input.length;
	}

	/**
	 * 
	 * @return input length for XML (undo/redo/replace)
	 */
	protected int getInputLengthForXML() {
		return getInputLength();
	}

	/**
	 * 
	 * @return input length for command description
	 */
	protected int getInputLengthForCommandDescription() {
		return getInputLength();
	}

	/**
	 * 
	 * @return input length for XML (undo/redo), maybe +1 for xOy plane
	 */
	final protected int getInputLengthForXMLMayNeedXOYPlane() {

		if (!cons.isGettingXMLForReplace() || kernel.getXOYPlane() == null) { // saving
																				// mode,
																				// or
																				// 2D
			return getInputLength();
		}

		return getInputLength() + 1; // add "xOyPlane"
	}

	/**
	 * 
	 * @return input length for command description, maybe +1 for xOy plane
	 */
	final protected int getInputLengthForCommandDescriptionMayNeedXOYPlane() {

		if (kernel.isSaving() || kernel.noNeedToSpecifyXOYPlane()) { // saving
																		// mode,
																		// or 2D
			return getInputLength();
		}

		return getInputLength() + 1; // add "xOyPlane"
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return input or xOy plane if i == input length
	 */
	final protected GeoElementND getInputMaybeXOYPlane(int i) {

		if (i == getInputLength()) {
			return kernel.getXOYPlane();
		}

		return input[i];
	}

	/**
	 * get XML for command output
	 * 
	 * @param sb
	 *            current string builder
	 * @param tpl
	 *            template for string
	 */
	protected void getCmdOutputXML(StringBuilder sb, StringTemplate tpl) {
		sb.append("\t<output");
		for (int i = 0; i < getOutputLength(); i++) {
			sb.append(" a");
			sb.append(i);
			// attribute name is output No.
			sb.append("=\"");
			GeoElement geo = getOutputForCmdXML(i);
			if (geo.isLabelSet()) {
				StringUtil.encodeXML(sb, geo.getLabel(tpl));
			}
			sb.append("\"");
		}

		sb.append("/>\n");
	}

	/**
	 * @param i
	 *            index
	 * @return output geo at position i for command output XML
	 */
	protected GeoElement getOutputForCmdXML(int i) {
		return getOutput(i);
	}

	/**
	 * @return class identificator (may not be unique)
	 */
	public abstract GetCommand getClassName();

	/**
	 * Sets whether the output of this command should be labeled. This setting
	 * is used for getXML().
	 * 
	 * @param flag
	 *            whether the output of this command should be labeled
	 */
	public void setPrintedInXML(boolean flag) {
		isPrintedInXML = flag;
		if (flag) {
			cons.addToConstructionList(this, true);
		} else {
			cons.removeFromConstructionList(this);
		}
	}

	/**
	 * @return whether the output of this command should be labeled (for GetXML)
	 */
	protected boolean isPrintedInXML() {
		return isPrintedInXML;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getDefinition(tpl);
	}

	protected String toExpString(StringTemplate tpl) {
		return toString(tpl);
	}

	final boolean doStopUpdateCascade() {
		return stopUpdateCascade;
	}

	protected final void setStopUpdateCascade(boolean stopUpdateCascade) {
		this.stopUpdateCascade = stopUpdateCascade;
	}

	public boolean wantsConstructionProtocolUpdate() {
		return false;
	}

	/**
	 * Makes sure that this algorithm will be updated after the given algorithm.
	 * 
	 * @param updateAfterAlgo
	 *            algo after which this should be updated
	 * @see #getUpdateAfterAlgo()
	 */
	final public void setUpdateAfterAlgo(AlgoElement updateAfterAlgo) {
		this.updateAfterAlgo = updateAfterAlgo;
	}

	private AlgoElement updateAfterAlgo;

	/**
	 * Returns the algorithm the should be updated right before this algorithm.
	 * This is being used in AlgorithmSet to sort algorithms by updating order.
	 * 
	 * @return null when there is no special algorithm that needs to be updated
	 *         first
	 * @see #getUpdateAfterAlgo()
	 */
	final public AlgoElement getUpdateAfterAlgo() {
		return updateAfterAlgo;
	}

	// /////////////////////////////////////////
	// USED FOR PREVIEWABLES
	// /////////////////////////////////////////

	/**
	 * remove all outputs from algebra view
	 */
	public void removeOutputFromAlgebraView() {
		View av = kernel.getApplication().getAlgebraView();
		if (av != null) {
			for (int i = 0; i < getOutputLength(); i++) {
				av.remove(getOutput(i));
			}
		}
	}

	/**
	 * remove all outputs from picking
	 */
	public void removeOutputFromPicking() {
		for (int i = 0; i < getOutputLength(); i++) {
			getOutput(i).setIsPickable(false);
		}
	}

	/**
	 * @return the outputHandler
	 */
	public List<OutputHandler<?>> getOutputHandler() {
		return outputHandler;
	}

	/**
	 * @param outputHandler
	 *            the outputHandler to set
	 */
	public void setOutputHandler(List<OutputHandler<?>> outputHandler) {
		this.outputHandler = outputHandler;
	}

	/**
	 * @return true for latex commands
	 */
	public boolean isLaTeXTextCommand() {
		return false;
	}

	/**
	 * Creates a new EquationElement given a {@link GeoElement} and an
	 * {@link EquationScope}.
	 * 
	 * @param element
	 *            the {@link GeoElement} needed.
	 * @param scope
	 *            the scope containing the points.
	 * @return a new EquationElement.
	 */
	public EquationElementInterface buildEquationElementForGeo(
			GeoElement element, EquationScopeInterface scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		return false;
	}

	public boolean isUndefined() {
		for (GeoElement geo : getInput()) {
			if (!geo.isDefined()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(GTemplate tpl) {
		return toString(tpl.getTemplate());
	}

}
