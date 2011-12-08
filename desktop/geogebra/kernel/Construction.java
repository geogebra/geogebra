/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.EuclidianViewCE;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.io.MyXMLio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A Construction consists of a construction list with objects of type
 * ConstructionElement (i.e. GeoElement or AlgoElement) and a GeoElement table
 * with (String label, GeoElement geo) pairs. Every ConstructionElement is
 * responsible to add or remove itself from the construction list. Every
 * GeoElement is responsible to add or remove itself from the GeoElement table.
 * 
 * To remove a ConstructionElement ce form its construction call ce.remove();
 * 
 * @author Markus Hohenwarter
 */
public class Construction extends AbstractConstruction {

	

	/** UndoManager */
	protected AbstractUndoManager undoManager;

	

	/** default elements */
	protected ConstructionDefaults consDefaults;

	/**
	 * Creates a new Construction.
	 * 
	 * @param k
	 *            Kernel
	 */
	public Construction(Kernel k) {
		this(k, null);
	}

	/**
	 * Creates a new Construction.
	 * 
	 * @param k
	 *            Kernel
	 * @param parentConstruction
	 *            parent construction (used for macro constructions)
	 */
	Construction(Kernel k, Construction parentConstruction) {
		kernel = k;

		ceList = new ArrayList<ConstructionElement>();
		algoList = new ArrayList<AlgoElement>();
		step = -1;

		geoSetConsOrder = new TreeSet<GeoElement>();
		geoSetWithCasCells = new TreeSet<GeoElement>();
		geoSetLabelOrder = new TreeSet<GeoElement>(new LabelComparator());
		geoSetsTypeMap = new HashMap<GeoClass, TreeSet<GeoElement>>();
		euclidianViewCE = new ArrayList<EuclidianViewCE>();

		if (parentConstruction != null)
			consDefaults = parentConstruction.getConstructionDefaults();
		else
			newConstructionDefaults();
		// consDefaults = new ConstructionDefaults(this);

		initAxis();

		geoTable = new HashMap<String, GeoElement>(200);
		initGeoTables();
	}

	

	/**
	 * Returns the UndoManager (for Copy & Paste)
	 * 
	 * @return UndoManager
	 */
	public AbstractUndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * creates the ConstructionDefaults consDefaults
	 */
	protected void newConstructionDefaults() {
		consDefaults = new ConstructionDefaults(this);
	}

	/**
	 * Returns the construction default object of this construction.
	 * 
	 * @return construction default object of this construction.
	 */
	final public ConstructionDefaults getConstructionDefaults() {
		return consDefaults;
	}

	

	
	/**
	 * Returns equation solver
	 * 
	 * @return equation solver
	 */
	public EquationSolver getEquationSolver() {
		return (EquationSolver) kernel.getEquationSolver();
	}

	/**
	 * Returns extremum finder
	 * 
	 * @return extremum finder
	 */
	public ExtremumFinder getExtremumFinder() {
		return kernel.getExtremumFinder();
	}

	


	// update all indices >= pos

	
	/**
	 * Build a set with all algorithms of this construction (in topological
	 * order). The method updateAll() of this set can be used to update the
	 * whole construction.
	 * 
	 * public AlgorithmSet buildOveralAlgorithmSet() { // 1) get all independent
	 * GeoElements in construction and update them // 2) build one overall
	 * updateSet from all updateSets of (1)
	 * 
	 * // 1) get all independent geos in construction LinkedHashSet indGeos =
	 * new LinkedHashSet(); int size = ceList.size(); for (int i = 0; i < size;
	 * ++i) { ConstructionElement ce = (ConstructionElement) ceList.get(i); if
	 * (ce.isIndependent()) indGeos.add(ce); else {
	 * indGeos.addAll(ce.getAllIndependentPredecessors()); } }
	 * 
	 * // 2) build one overall updateSet AlgorithmSet algoSet = new
	 * AlgorithmSet(); Iterator it = indGeos.iterator(); while (it.hasNext()) {
	 * GeoElement geo = (GeoElement) it.next();
	 * 
	 * // update this geo only geo.update();
	 * 
	 * // get its update set and add it to the overall updateSet
	 * algoSet.addAll(geo.getAlgoUpdateSet()); }
	 * 
	 * return algoSet; }
	 */









	
	
	

	
	// /////////////////////////////////////////////
	// LABELS DEPENDING ON ALGOS
	// /////////////////////////////////////////////

	/*
	 * redo / undo
	 */

	/**
	 * Clears the undo info list of this construction and adds the current
	 * construction state to the undo info list.
	 */
	public void initUndoInfo() {
		if (undoManager == null)
			undoManager = new UndoManager(this);
		undoManager.initUndoInfo();
	}

	/**
	 * Stores current state of construction.
	 * 
	 * @see UndoManager#storeUndoInfo
	 */
	public void storeUndoInfo() {
		// undo unavailable in applets
		// if (getApplication().isApplet()) return;

		if (!isUndoEnabled())
			return;

		undoManager.storeUndoInfo();
	}

	/**
	 * Restores undo info
	 * 
	 * @see UndoManager#restoreCurrentUndoInfo()
	 */
	public void restoreCurrentUndoInfo() {
		// undo unavailable in applets
		// if (getApplication().isApplet()) return;
		collectRedefineCalls = false;

		if (undoManager != null)
			undoManager.restoreCurrentUndoInfo();
	}

	/**
	 * Redoes last undone step
	 */
	public void redo() {
		// undo unavailable in applets
		// if (getApplication().isApplet()) return;

		undoManager.redo();
	}

	/**
	 * Undoes last operation
	 */
	public void undo() {
		// undo unavailable in applets
		// if (getApplication().isApplet()) return;

		undoManager.undo();
	}

	/**
	 * Returns true iff undo is possible
	 * 
	 * @return true iff undo is possible
	 */
	public boolean undoPossible() {
		// undo unavailable in applets
		// if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.undoPossible();
	}

	/**
	 * Returns true iff redo is possible
	 * 
	 * @return true iff redo is possible
	 */
	public boolean redoPossible() {
		// undo unavailable in applets
		// if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.redoPossible();
	}

	/**
	 * Replaces oldGeo by newGeo in the current construction. This may change
	 * the logic of the construction and is a very powerful operation
	 * 
	 * @param oldGeo
	 *            Geo to be replaced.
	 * @param newGeo
	 *            Geo to be used instead.
	 * @throws Exception
	 */
	public void replace(GeoElement oldGeo, GeoElement newGeo) throws Exception {
		if (oldGeo == null || newGeo == null || oldGeo == newGeo)
			return;

		// if oldGeo does not have any children, we can simply
		// delete oldGeo and give newGeo the name of oldGeo
		if (!oldGeo.hasChildren()) {
			String oldGeoLabel = oldGeo.label;
			newGeo.moveDependencies(oldGeo);
			oldGeo.remove();

			if (newGeo.isIndependent())
				addToConstructionList(newGeo, true);
			else {
				AlgoElement parentAlgo = newGeo.getParentAlgorithm();
				addToConstructionList(parentAlgo, true);
				// make sure all output objects get labels, see #218
				GeoElement.setLabels(oldGeoLabel, parentAlgo.getOutput(),
						kernel.getGeoElementSpreadsheet());
			}

			// copy formatting of oldGeo to newGeo
			newGeo.setAllVisualProperties(oldGeo, false);

			// copy label of oldGeo to newGeo
			// use setLoadedLabel() instead of setLabel() to make sure that
			// hidden objects also get the label, see #379
			newGeo.setLoadedLabel(oldGeoLabel);

			if (newGeo.isGeoText())
				newGeo.updateRepaint();

			return;
		}

		// check for circular definition
		if (newGeo.isChildOf(oldGeo)) {

			// check for eg a = a + 1, A = A + (1,1), a = !a
			if (oldGeo.isIndependent() && oldGeo instanceof GeoNumeric) {

				((GeoNumeric) oldGeo).setValue(((GeoNumeric) newGeo)
						.getDouble());
				oldGeo.updateRepaint();
				return;

			} else if (oldGeo.isIndependent() && oldGeo instanceof GeoPoint2) {

				((GeoPoint2) oldGeo).set(newGeo);
				oldGeo.updateRepaint();
				return;

			} else if (oldGeo.isIndependent() && oldGeo instanceof GeoVector) {

				((GeoVector) oldGeo).set(newGeo);
				oldGeo.updateRepaint();
				return;

			} else if (oldGeo.isIndependent() && oldGeo instanceof GeoBoolean) {

				((GeoBoolean) oldGeo).set(newGeo);
				oldGeo.updateRepaint();
				return;

			} else if (oldGeo.isIndependent() && oldGeo.isGeoPoint()
					&& oldGeo.isGeoElement3D()) {// GeoPoint3D

				oldGeo.set(newGeo);
				oldGeo.updateRepaint();
				return;

			} else {

				restoreCurrentUndoInfo();
				throw new CircularDefinitionException();

			}

		}
		// 1) remove all brothers and sisters of oldGeo
		// 2) move all predecessors of newGeo to the left of oldGeo in
		// construction list
		prepareReplace(oldGeo, newGeo);

		if (collectRedefineCalls) {
			// collecting redefine calls in redefineMap
			redefineMap.put(oldGeo, newGeo);
			return;
		}
		AbstractApplication app = kernel.getApplication();
		boolean moveMode = app.getMode() == EuclidianConstants.MODE_MOVE
				&& app.getSelectedGeos().size() > 0;
		String oldSelection = null;
		if (moveMode) {
			oldSelection = app.getSelectedGeos().get(0).getLabel();
		}
		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();

		// 3) replace oldGeo by newGeo in XML
		doReplaceInXML(consXML, oldGeo, newGeo);
		// moveDependencies(oldGeo,newGeo);

		// 4) build new construction
		buildConstruction(consXML);
		if (moveMode) {
			GeoElement selGeo = kernel.lookupLabel(oldSelection);
			app.addSelectedGeo(selGeo, false);
			app.getEuclidianView()
					.getEuclidianController().handleMovedElement(selGeo, false);
		}
	}

	/**
	 * Changes the given casCell taking care of necessary redefinitions. This
	 * may change the logic of the construction and is a very powerful
	 * operation.
	 * 
	 * @param casCell
	 *            casCell to be changed
	 * @throws Exception
	 */
	public void changeCasCell(GeoCasCell casCell) throws Exception {
		// move all predecessors of casCell to the left of casCell in
		// construction list
		updateConstructionOrder(casCell);

		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();

		// build new construction to make sure all ceIDs are correct after the
		// redefine
		buildConstruction(consXML);
	}

	/**
	 * Processes all collected redefine calls as a batch to improve performance.
	 * 
	 * @see #startCollectingRedefineCalls()
	 * @throws Exception
	 */
	public void processCollectedRedefineCalls() throws Exception {
		collectRedefineCalls = false;

		if (redefineMap == null || redefineMap.size() == 0)
			return;

		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();

		// replace all oldGeo -> newGeo pairs in XML
		Iterator<GeoElement> it = redefineMap.keySet().iterator();
		while (it.hasNext()) {
			GeoElement oldGeo = it.next();
			GeoElement newGeo = redefineMap.get(oldGeo);

			// 3) replace oldGeo by newGeo in XML
			doReplaceInXML(consXML, oldGeo, newGeo);
		}

		try {
			// 4) build new construction for all changes at once
			buildConstruction(consXML);
		} catch (Exception e) {
			throw e;
		} finally {
			stopCollectingRedefineCalls();
			consXML.setLength(0);
			consXML = null;
			System.gc();
		}
	}

	/**
	 * Tries to build the new construction from the given XML string.
	 */
	private void buildConstruction(StringBuilder consXML) throws Exception {
		// try to process the new construction
		try {
			if (undoManager == null)
				undoManager = new UndoManager(this);
			undoManager.processXML(consXML.toString());
			kernel.notifyReset();
			kernel.updateConstruction();
		} catch (Exception e) {
			restoreCurrentUndoInfo();
			throw e;
		} catch (MyError err) {
			restoreCurrentUndoInfo();
			throw err;
		}
	}

	/**
	 * Returns undo xml string of this construction.
	 * 
	 * @return StringBuilder with xml of this construction.
	 */
	public StringBuilder getCurrentUndoXML() {
		return xmlio.getUndoXML(this);
	}

	private MyXMLio xmlio;

	public void setXMLio(MyXMLio xmlio) {
		this.xmlio = xmlio;
	}

	
}
