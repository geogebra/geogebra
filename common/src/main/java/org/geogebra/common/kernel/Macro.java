/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * A macro is a user defined commmand. It has its own macro construction that is
 * used by all using AlgoMacro instances.
 * 
 * @author Markus Hohenwarter
 */
public class Macro {

	private Kernel kernel;
	private String cmdName = "";
	private String toolName = "";
	private String toolHelp = "";
	private String iconFileName = ""; // image file
	private boolean showInToolBar = true;

	private Construction macroCons; // macro construction
	private StringBuilder macroConsXML;
	private GeoElement[] macroInput;
	private GeoElement[] macroOutput; // input and output objects
	private String[] macroInputLabels;
	private String[] macroOutputLabels;
	private TestGeo[] inputTypes;
	private Integer viewId = null;
	private LinkedList<AlgoElement> usingAlgos = new LinkedList<>();
	private boolean copyCaptions;

	/**
	 * Creates a new macro using the given input and output GeoElements.
	 * 
	 * @param kernel
	 *            Kernel
	 * @param cmdName
	 *            Command name
	 * @param input
	 *            Array of input objects
	 * @param output
	 *            Array of output objects
	 * @throws Exception
	 *             if macro initialization fails (unnecessary input, independent
	 *             output)
	 */
	public Macro(Kernel kernel, String cmdName, GeoElement[] input,
			GeoElement[] output) throws Exception {
		this(kernel, cmdName);
		initMacro(input, output);
	}

	/**
	 * Creates a new macro. Note: you need to call initMacro() when using this
	 * constructor.
	 * 
	 * @param kernel
	 *            Kernel
	 * @param cmdName
	 *            Command name
	 */
	public Macro(Kernel kernel, String cmdName) {
		this.kernel = kernel;
		setCommandName(cmdName);
		copyCaptions = true;
	}

	/**
	 * Returns all input geos from the macro construction.
	 * 
	 * @return all input geos from the macro construction.
	 */
	public GeoElement[] getMacroInput() {
		return macroInput;
	}

	/**
	 * Returns kernel
	 * 
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * Returns all output geos from the macro construction.
	 * 
	 * @return Array of output elements
	 */
	public GeoElement[] getMacroOutput() {
		return macroOutput;
	}

	/**
	 * Returns whether geo is part of this macro's construction.
	 * 
	 * @param geo
	 *            Geo to be found in construction
	 * @return true iff geo is part of this macro's construction.
	 */
	final public boolean isInMacroConstruction(GeoElement geo) {
		return geo.cons == macroCons;
	}

	/**
	 * Returns the construction object of this macro.
	 * 
	 * @return construction object of this macro.
	 */
	public Construction getMacroConstruction() {
		return macroCons;
	}

	/**
	 * Initiates macro
	 * 
	 * @param macroCons1
	 *            macro construction
	 * @param inputLabels
	 *            labels for input
	 * @param outputLabels
	 *            labels for output
	 */
	public void initMacro(Construction macroCons1, String[] inputLabels,
			String[] outputLabels) {
		this.macroCons = macroCons1;
		this.macroConsXML = new StringBuilder();
		macroCons.getConstructionXML(macroConsXML, false);
		this.macroInputLabels = inputLabels;
		this.macroOutputLabels = outputLabels;

		initInputOutput();

		// init inputTypes array
		inputTypes = new TestGeo[macroInput.length];
		for (int i = 0; i < macroInput.length; i++) {
			inputTypes[i] = TestGeo.getSpecificTest(macroInput[i]);
		}

		// after initing we turn global variable lookup on again,
		// so we can use for example functions with parameters in macros too.
		// Such parameters are global variables
		if (macroCons1 instanceof MacroConstruction) {
			((MacroConstruction) macroCons1).setGlobalVariableLookup(true);
		}
	}

	private void initInputOutput() {
		// get the input and output geos from the macro construction
		macroInput = new GeoElement[macroInputLabels.length];
		macroOutput = new GeoElement[macroOutputLabels.length];

		for (int i = 0; i < macroInputLabels.length; i++) {
			macroInput[i] = macroCons.lookupLabel(macroInputLabels[i]);
			macroInput[i].setFixed(false);
		}

		for (int i = 0; i < macroOutputLabels.length; i++) {
			macroOutput[i] = macroCons.lookupLabel(macroOutputLabels[i]);
		}
	}

	private void initMacro(GeoElement[] input, GeoElement[] output)
			throws Exception {
		// check that every output object depends on an input object
		// and that all input objects are really needed
		for (int i = 0; i < output.length; i++) {
			boolean dependsOnInput = false;

			for (int k = 0; k < input.length; k++) {
				boolean dependencyFound = output[i].isChildOf(input[k]);
				if (dependencyFound) {
					dependsOnInput = true;
				}
			}

			if (!dependsOnInput) {
				throw new Exception(kernel.getApplication().getLocalization()
						.getError("Tool.OutputNotDependent") + ": "
						+ output[i].getNameDescription());
			}
		}

		// steps to create a macro
		// 1) outputAndParents = set of all predecessors of output objects
		// 2) inputChildren = set of all children of input objects
		// 3) macroElements = intersection of outputParents and inputChildren
		// 4) add input and output objects to macroElements
		// 5) create XML representation for macro-construction
		// 6) create a new macro-construction from this XML representation

		// 1) create the set of all parents of this macro's output objects
		TreeSet<GeoElement> outputParents = new TreeSet<>();
		for (int i = 0; i < output.length; i++) {
			output[i].addPredecessorsToSet(outputParents, false);

			// note: Locateables (like Texts, Images, Vectors) may depend on
			// points,
			// these points must be part of the macro construction
			if (output[i] instanceof Locateable) {
				Locateable loc = (Locateable) output[i];
				GeoPointND[] points = loc.getStartPoints();
				if (points != null) {
					for (int k = 0; k < points.length; k++) {
						outputParents.add((GeoElement) points[k]);
						((GeoElement) points[k])
								.addPredecessorsToSet(outputParents, false);
					}
				}
			}
		}
		// 2) and 3) get intersection of inputChildren and outputParents
		TreeSet<ConstructionElement> macroConsOrigElements = new TreeSet<>();
		TreeSet<Long> usedAlgoIds = new TreeSet<>();
		Iterator<GeoElement> it = outputParents.iterator();
		while (it.hasNext()) {
			GeoElement outputParent = it.next();
			if (outputParent.isLabelSet()) {
				for (int i = 0; i < input.length; i++) {
					if (outputParent.isChildOf(input[i])) {
						addDependentElement(outputParent, macroConsOrigElements,
								usedAlgoIds);
						// add parent only once: get out of loop
						i = input.length;
					}
				}
			}
		}

		// 4) add input and output objects to macroElements
		// ensure that all input and all output objects have labels set
		// Note: we have to undo this at the end of this method !!!
		boolean[] isInputLabeled = new boolean[input.length];
		boolean[] isOutputLabeled = new boolean[output.length];
		String[] inputLabels = new String[input.length];
		String[] outputLabels = new String[output.length];
		GeoPointND[] startPoints = new GeoPointND[input.length];
		for (int i = 0; i < input.length; i++) {
			isInputLabeled[i] = input[i].isLabelSet();
			if (!isInputLabeled[i]) {
				input[i].setLabelSimple(input[i].getDefaultLabel());
				input[i].setLabelSet(true);
			}
			if (input[i] instanceof GeoVector) {
				startPoints[i] = ((GeoVector) input[i]).getStartPoint();
				((GeoVector) input[i]).setStartPoint(null);
			}
			inputLabels[i] = input[i].getLabelSimple();

			// add input element to macroConsOrigElements
			// we handle some special cases for input types like segment,
			// polygons, etc.
			switch (input[i].getGeoClassType()) {
			case SEGMENT:
			case RAY:
			case POLYGON:
			case FUNCTION:
			case POLYHEDRON:
			case CURVE_CARTESIAN:// needed for
				// https://help.geogebra.org/topic/tool-creator-confuses-curves-with-conics
				// add parent algo and its input objects to
				// macroConsOrigElements
				addSpecialInputElement(input[i], macroConsOrigElements);
				break;

			default:
				// add input element to macroConsOrigElements
				macroConsOrigElements.add(input[i]);

				// make sure we don't have any parent algorithms of input[i] in
				// our construction
				AlgoElement algo = input[i].getParentAlgorithm();
				if (algo != null) {
					macroConsOrigElements.remove(algo);
				}
			}

		}
		for (int i = 0; i < output.length; i++) {
			isOutputLabeled[i] = output[i].isLabelSet();
			if (!isOutputLabeled[i]) {
				output[i].setLabelSimple(output[i].getDefaultLabel());
				output[i].setLabelSet(true);
			}
			outputLabels[i] = output[i].getLabelSimple();

			// add output element and its algorithm to macroConsOrigElements
			addDependentElement(output[i], macroConsOrigElements, usedAlgoIds);
		}

		// 5) create XML representation for macro-construction
		macroConsXML = buildMacroXML(
				input.length == 0 ? kernel : input[0].kernel,
				macroConsOrigElements);

		// if we used temp labels in step (4) remove them again
		for (int i = 0; i < input.length; i++) {
			if (!isInputLabeled[i]) {
				input[i].setLabelSet(false);
			}
			if (input[i] instanceof GeoVector) {
				((GeoVector) input[i]).setStartPoint(startPoints[i]);
			}
		}
		for (int i = 0; i < output.length; i++) {
			if (!isOutputLabeled[i]) {
				output[i].setLabelSet(false);
			}
		}
		Log.debug(macroConsXML);
		// 6) create a new macro-construction from this XML representation
		Construction macroCons2 = createMacroConstruction(
				macroConsXML.toString());

		// init macro
		initMacro(macroCons2, inputLabels, outputLabels);
	}

	/**
	 * Adds the geo, its parent algorithm and all its siblings to the
	 * consElementSet and its id to used AlgoIds
	 * 
	 * @param geo
	 *            Element to be added (with parent and siblings)
	 * @param consElementSet
	 *            Set of geos & algos used in macro construction
	 * @param usedAlgoIds
	 *            Set of IDs of algorithms used in macro construction
	 */
	public static void addDependentElement(GeoElement geo,
			Set<ConstructionElement> consElementSet, Set<Long> usedAlgoIds) {
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo.isInConstructionList()) {
			addDependentAlgo(algo, consElementSet, usedAlgoIds);
		} else {
			// HELPER algorithm, e.g. segment of polygon
			// we only add the geo because it is output
			// of some other algorithm in construction list
			consElementSet.add(geo);
		}
	}

	/**
	 * Adds the geo, its parent algorithm and all its siblings to the
	 * consElementSet and its id to used AlgoIds
	 * 
	 * @param algo
	 *            Element to be added
	 * @param consElementSet
	 *            Set of geos & algos used in macro construction
	 * @param usedAlgoIds
	 *            Set of IDs of algorithms used in macro construction
	 */
	public static void addDependentAlgo(AlgoElement algo,
			Set<ConstructionElement> consElementSet, Set<Long> usedAlgoIds) {

		// STANDARD case
		// add algorithm
		Long algoID = Long.valueOf(algo.getID());
		if (!usedAlgoIds.contains(algoID)) {
			consElementSet.add(algo);
		}
		usedAlgoIds.add(algoID);

		// add all output elements including geo
		GeoElement[] algoOutput = algo.getOutput();
		for (int i = 0; i < algoOutput.length; i++) {
			consElementSet.add(algoOutput[i]);
		}

	}

	/**
	 * Adds the geo, its parent algorithm and all input of the parent algorithm
	 * to the consElementSet. This is used for e.g. a segment that is used as an
	 * input object of a macro. We also need to have the segment's start and
	 * endpoint.
	 * 
	 * @param geo
	 *            special element
	 * @param consElementSet
	 *            set to add this element elements
	 */
	public static void addSpecialInputElement(GeoElement geo,
			Set<ConstructionElement> consElementSet) {
		// add geo
		consElementSet.add(geo);

		// add parent algo and input objects
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo != null && algo.isInConstructionList()) {
			// STANDARD case
			// add algorithm
			consElementSet.add(algo);

			// add all output elements including geo
			GeoElement[] algoInput = algo.getInput();
			for (int i = 0; i < algoInput.length; i++) {
				if (algoInput[i].isLabelSet()) {
					consElementSet.add(algoInput[i]);
				}
			}
		}
	}

	/**
	 * Note: changes macroConsElements
	 * 
	 * @param kernel
	 *            Kernel
	 * @param macroConsElements
	 *            elements involved in macro (input, internal, output)
	 * @return XML string of macro construction
	 */
	public static StringBuilder buildMacroXML(Kernel kernel,
			Set<ConstructionElement> macroConsElements) {

		// get the XML for all macro construction elements
		StringBuilder macroConsXML = new StringBuilder(500);
		macroConsXML.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		macroConsXML.append("<geogebra format=\""
				+ GeoGebraConstants.XML_FILE_FORMAT + "\">\n");
		macroConsXML
				.append("<construction author=\"\" title=\"\" date=\"\">\n");

		Iterator<ConstructionElement> it = macroConsElements.iterator();
		while (it.hasNext()) {
			ConstructionElement ce = it.next();

			if (ce.isGeoElement()) {
				ce.getXML(false, macroConsXML);
			} else if (ce.isAlgoElement()) {
				AlgoElement algo = (AlgoElement) ce;
				algo.getXML(macroConsXML, false);
			}
		}

		macroConsXML.append("</construction>\n");
		macroConsXML.append("</geogebra>");

		return macroConsXML;
	}

	/**
	 * Creates a macro construction from a given xml string. The names of the
	 * input and output objects within this construction are given by
	 * inputLabels and outputLabels
	 * 
	 * @param macroConstructionXML
	 *            XML content
	 */
	private Construction createMacroConstruction(String macroConstructionXML)
			throws Exception {
		// build macro construction
		MacroKernel mk = kernel.newMacroKernel();
		mk.setContinuous(false);

		// during initing we turn global variable lookup off, so we can be sure
		// that the macro construction only dependes on it's input
		mk.setGlobalVariableLookup(false);

		try {
			mk.loadXML(macroConstructionXML);
		} catch (MyError e) {
			String msg = e.getLocalizedMessage();
			Log.debug(msg);
			e.printStackTrace();
			throw new Exception(msg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return mk.getConstruction();
	}

	/**
	 * Add link to algo using this macro
	 * 
	 * @param algoMacro
	 *            macro algorithm
	 */
	public void registerAlgorithm(AlgoMacroInterface algoMacro) {
		usingAlgos.add((AlgoElement) algoMacro);
	}

	/**
	 * Remove link to algo using this macro
	 * 
	 * @param algoMacro
	 *            macro algorithm
	 */
	public void unregisterAlgorithm(AlgoElement algoMacro) {
		usingAlgos.remove(algoMacro);
	}

	/**
	 * Returns whether this macro is being used by algorithms in the current
	 * construction.
	 * 
	 * @return true iff this macro is being used by algorithms in the current
	 *         construction
	 */
	final public boolean isUsed() {
		return usingAlgos.size() > 0;
	}

	/**
	 * Removes links to all algos using this macro
	 */
	final public void setUnused() {
		usingAlgos.clear();
	}

	/**
	 * Returns the types of input objects of the default macro construction.
	 * This can be used to check whether a given GeoElement array can be used as
	 * input for this macro.
	 * 
	 * @return types of input objects
	 */
	final public TestGeo[] getInputTypes() {
		return inputTypes;
	}

	/**
	 * Returns the tool help
	 * 
	 * @return tool help
	 */
	public String getToolHelp() {

		if (toolHelp == null || "".equals(toolHelp)) {
			return toString();
		}
		return toolHelp;
	}

	/**
	 * Returns a String showing all needed types of this macro. eg [
	 * &lt;Text&gt;, &lt;Number&gt; ]
	 * 
	 * @return string showing all needed types of this macro.
	 */
	public String getNeededTypesString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < macroInput.length; ++i) {
			sb.append(macroInput[i].translatedTypeString());
			if (i != macroInput.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	/**
	 * Sets tool help.
	 * 
	 * @param toolHelp
	 *            Tool help. Either "","null" or null for empty.
	 */
	public void setToolHelp(String toolHelp) {
		if (toolHelp == null || "null".equals(toolHelp)) {
			this.toolHelp = "";
		} else {
			this.toolHelp = toolHelp;
		}
	}

	/**
	 * Returns command name
	 * 
	 * @return Command name
	 */
	public String getCommandName() {
		return cmdName;
	}

	/**
	 * Sets commandd name
	 * 
	 * @param name
	 *            Command name
	 */
	public void setCommandName(String name) {
		if (name != null) {
			this.cmdName = name;
		}
	}

	/**
	 * Returns tool name
	 * 
	 * @return Tool name
	 */
	public String getToolName() {
		return toolName;
	}

	/**
	 * Returns toolname, if empty, returns command name.
	 * 
	 * @return Toolname, if empty, returns command name.
	 */
	public String getToolOrCommandName() {
		if (!"".equals(toolName)) {
			return toolName;
		}
		return cmdName;
	}

	/**
	 * Sets tool name
	 * 
	 * @param name
	 *            new tool name
	 */
	public void setToolName(String name) {
		if (name == null || "null".equals(name) || name.length() == 0) {
			this.toolName = cmdName;
		} else {
			this.toolName = name;
		}
	}

	/**
	 * Sets icon filename
	 * 
	 * @param name
	 *            Icon filename, "" or null for empty
	 */
	public void setIconFileName(String name) {
		if (name == null) {
			this.iconFileName = "";
		} else {
			this.iconFileName = name;
		}
	}

	/**
	 * Returns icon filename
	 * 
	 * @return icon filename
	 */
	public String getIconFileName() {
		return iconFileName;
	}

	/**
	 * Returns the syntax descriptiont of this macro.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(cmdName);
		sb.append("[ ");

		// input types
		for (int i = 0; i < macroInput.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append('<');
			sb.append(macroInput[i].translatedTypeString());
			sb.append('>');
		}
		sb.append(" ]");

		return sb.toString();
	}

	/**
	 * Adds XML representation of this macro for saving in a ggb file to given
	 * string builder.
	 * 
	 * @param sb
	 *            StringBuilder for adding the macro representation
	 */
	public void getXML(StringBuilder sb) {
		sb.append("<macro cmdName=\"");
		StringUtil.encodeXML(sb, cmdName);
		sb.append("\" toolName=\"");
		StringUtil.encodeXML(sb, toolName);
		sb.append("\" toolHelp=\"");
		StringUtil.encodeXML(sb, toolHelp);
		sb.append("\" iconFile=\"");
		StringUtil.encodeXML(sb, iconFileName);
		sb.append("\" showInToolBar=\"");
		sb.append(showInToolBar);
		sb.append("\" copyCaptions=\"");
		sb.append(copyCaptions);
		if (viewId != null) {
			sb.append("\" viewId=\"");
			sb.append(viewId);
		}

		sb.append("\">\n");

		// add input labels
		sb.append("<macroInput");
		for (int i = 0; i < macroInputLabels.length; i++) {
			// attribute name is input no.
			sb.append(" a");
			sb.append(i);
			sb.append("=\"");
			StringUtil.encodeXML(sb, macroInputLabels[i]);
			sb.append("\"");
		}
		sb.append("/>\n");

		// add output labels
		sb.append("<macroOutput");
		for (int i = 0; i < macroOutputLabels.length; i++) {
			// attribute name is output no.
			sb.append(" a");
			sb.append(i);
			sb.append("=\"");
			StringUtil.encodeXML(sb, macroOutputLabels[i]);
			sb.append("\"");
		}
		sb.append("/>\n");

		// macro construction XML
		if (macroConsXML != null && macroConsXML.length() > 0) {
			sb.append(macroConsXML.toString());
		} else {
			macroCons.getConstructionXML(sb, false);
		}

		sb.append("</macro>\n");

	}

	/**
	 * Returns whether this macro should be shown in toolbar
	 * 
	 * @return true iff this macro should be shown in toolbar
	 */
	public final boolean isShowInToolBar() {
		return showInToolBar;
	}

	/**
	 * Sets whether this macro should be shown in toolbar
	 * 
	 * @param showInToolBar
	 *            true iff this macro should be shown in toolbar
	 */
	public final void setShowInToolBar(boolean showInToolBar) {
		this.showInToolBar = showInToolBar;
	}

	/**
	 * Returns list of macros used by this one
	 * 
	 * @return list of macros used by this one
	 */
	public ArrayList<Macro> getUsedMacros() {
		return macroCons.getUsedMacros();
	}

	/**
	 * Returns list of geos created using this macro
	 * 
	 * @return list of geos created using this macro
	 */
	public ArrayList<GeoElement> getDependentGeos() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		Iterator<AlgoElement> curr = usingAlgos.iterator();
		while (curr.hasNext()) {
			AlgoElement algo = curr.next();

			// seek for the first visible geo
			GeoElement geo = algo.getOutput(0);
			while (!geo.isLabelSet() && geo.getAllChildren().size() > 0) {
				geo = geo.getAllChildren().first();
			}

			// add that geo and its siblings
			algo = geo.getParentAlgorithm();
			for (int i = 0; i < algo.getOutputLength(); i++) {
				geos.add(algo.getOutput(i));
			}
		}
		return geos;
	}

	/**
	 * Set whether the macro should copy captions of resulting objects
	 * 
	 * @param copyCaptions
	 *            true to copy
	 */
	public void setCopyCaptionsAndVisibility(boolean copyCaptions) {
		this.copyCaptions = copyCaptions;
	}

	/**
	 * @return true if the macro copies captions of resulting objects
	 */
	public boolean isCopyCaptionsAndVisibility() {
		return copyCaptions;
	}

	/**
	 * @return to which view's toolbar this belongs
	 */
	public Integer getViewId() {
		return viewId;
	}

	/**
	 * @param viewId
	 *            to which view's toolbar this belongs
	 */
	public void setViewId(Integer viewId) {
		this.viewId = viewId;
	}
}
