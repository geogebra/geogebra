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
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.EuclidianViewCE;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.util.StringUtil;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.io.MyXMLio;
import geogebra.kernel.algos.AlgoDistancePoints;
import geogebra.kernel.cas.AlgoDependentCasCell;
import geogebra.kernel.geos.GeoAxis;
import geogebra.kernel.geos.GeoCasCell;
import geogebra.kernel.geos.GeoElementSpreadsheet;
import geogebra.kernel.geos.GeoText;
import geogebra.kernel.optimization.ExtremumFinder;
//import geogebra.main.Application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;


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
public class Construction extends AbstractConstruction{

	/** Table for (label, GeoCasCell) pairs, contains global variables used in CAS view */
	protected HashMap<String,GeoCasCell> geoCasCellTable;


	private GeoPoint2 origin;
	

		

	// list of Macro commands used in this construction
	private ArrayList<Macro> usedMacros;
	
	

	/** UndoManager */
	protected UndoManager undoManager;

	

	
	
	

	

	

	// axis objects
	private GeoAxis xAxis, yAxis;
	private String xAxisLocalName, yAxisLocalName;

	/** default elements */
	protected ConstructionDefaults consDefaults;

	/**
	 * Creates a new Construction.
	 * @param k Kernel
	 */
	public Construction(Kernel k) {
		this(k, null);
	}

	/**
	 * Returns the point (0,0)
	 * @return point (0,0)
	 */
	public final GeoPoint2 getOrigin(){
		if(origin==null){
			origin=new GeoPoint2(this);
			origin.setCoords(0.0, 0.0, 1.0);
		}
		return origin;
	}
	/**
	 * Creates a new Construction.
	 * @param k Kernel
	 * @param parentConstruction parent construction (used for macro constructions)
	 */
	Construction(Kernel k, Construction parentConstruction) {
		kernel = k;

		ceList = new ArrayList<ConstructionElement>();
		algoList = new ArrayList<AlgoElement>();
		step = -1;

		geoSetConsOrder = new TreeSet<GeoElement>();
		geoSetWithCasCells = new TreeSet<GeoElement>();
		geoSetLabelOrder = new TreeSet<GeoElement>(new LabelComparator());
		geoSetsTypeMap = new HashMap<GeoClass,TreeSet<GeoElement>>();
		euclidianViewCE = new ArrayList<EuclidianViewCE>();

		if (parentConstruction != null)
			consDefaults = parentConstruction.getConstructionDefaults();
		else
			newConstructionDefaults();
			//consDefaults = new ConstructionDefaults(this);

		initAxis();

		geoTable = new HashMap<String,GeoElement>(200);
		initGeoTables();
	}
	
	
	/**
	 * init the axis
	 */
	protected void initAxis(){
		xAxis = new GeoAxis(this, GeoAxis.X_AXIS);
		yAxis = new GeoAxis(this, GeoAxis.Y_AXIS);
	}

	/**
	 * Returns the UndoManager (for Copy & Paste)
	 * @return UndoManager 
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
	/**
	 * Returns the last GeoElement object in the construction list.
	 * @return the last GeoElement object in the construction list.
	 */
	public GeoElement getLastGeoElement() {
		if (geoSetWithCasCells.size() > 0)
			return geoSetWithCasCells.last();
		else
			return null;
	}
	
	
	/**
	 * creates the ConstructionDefaults consDefaults
	 */
	protected void newConstructionDefaults(){
		consDefaults = new ConstructionDefaults(this);
	}

	/**
	 * Returns the construction default object of this construction.
	 * @return construction default object of this construction.
	 */
	final public ConstructionDefaults getConstructionDefaults() {
		return consDefaults;
	}

	/**
	 * Make geoTable contain only xAxis and yAxis
	 */
	protected void initGeoTables() {
		geoTable.clear();
		geoCasCellTable = null; 
		localVariableTable = null;
		
		// add axes labels both in English and current language
		geoTable.put("xAxis", xAxis);
		geoTable.put("yAxis", yAxis);
		if (xAxisLocalName != null) {
			geoTable.put(xAxisLocalName, xAxis);
			geoTable.put(yAxisLocalName, yAxis);
		}		
	}

	/** 
	 * Renames xAxis and yAxis in the geoTable
	 * and sets *AxisLocalName-s acordingly 
	 */
	public void updateLocalAxesNames() {		
		geoTable.remove(xAxisLocalName);
		geoTable.remove(yAxisLocalName);

		AbstractApplication app = kernel.getApplication();
		xAxisLocalName = app.getPlain("xAxis");
		yAxisLocalName = app.getPlain("yAxis");
		geoTable.put(xAxisLocalName, xAxis);
		geoTable.put(yAxisLocalName, yAxis);	
	}

	

	/** 
	 * Returns equation solver
	 * @return equation solver
	 */
	public EquationSolver getEquationSolver() {
		return (EquationSolver)kernel.getEquationSolver();
	}

	/**
	 * Returns extremum finder
	 * @return extremum finder
	 */
	public ExtremumFinder getExtremumFinder() {
		return (ExtremumFinder) kernel.getExtremumFinder();
	}

	/**
	 * Returns x-axis
	 * @return x-axis
	 */
	final public GeoAxis getXAxis() {
		return xAxis;
	}

	/**
	 * Returns y-axis
	 * @return y-axis
	 */
	final public GeoAxis getYAxis() {
		return yAxis;
	}



	

	


	/**
	 * Returns a set with all labeled GeoElement objects sorted in alphabetical
	 * order of their type strings and labels (e.g. Line g, Line h, Point A,
	 * Point B, ...). Note: the returned TreeSet is a copy of the current
	 * situation and is not updated by the construction later on.
	 * @return Set of all labeld GeoElements orted by name and description
	 */
	final public TreeSet<GeoElement> getGeoSetNameDescriptionOrder() {
		// sorted set of geos
		TreeSet<GeoElement> sortedSet = new TreeSet<GeoElement>(new NameDescriptionComparator());

		// get all GeoElements from construction and sort them
		Iterator<GeoElement> it = geoSetConsOrder.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// sorted inserting using name description of geo
			sortedSet.add(geo);
		}
		return sortedSet;
	}

		
	
	
	
	/***
	 * Returns the n-th GeoCasCell object (free or dependent) in the construction list.
	 * This is the GeoCasCell in the n-th row of the CAS view.
	 * 
	 * @param row number starting at 0
	 * @return cas cell or null if there are less cas cells in the construction list
	 */
	public GeoCasCell getCasCell(int row) {
		if (row < 0) return null;
		
		int counter = 0;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				if (counter == row)
					return (GeoCasCell) ce;
				++counter;
			}
			else if (ce instanceof AlgoDependentCasCell) {
				if (counter == row)
					return ((AlgoDependentCasCell) ce).getCasCell();
				++counter;
			}						
		}
		
		// less than n casCell
		return null;
	}
	
	/***
	 * Returns the last GeoCasCell object (free or dependent) in the construction list.
	 * 
	 * @return last cas cell
	 */
	public GeoCasCell getLastCasCell() {
		GeoCasCell lastCell = null;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {			
				lastCell = (GeoCasCell) ce;
			}
			else if (ce instanceof AlgoDependentCasCell) {			
				lastCell = ((AlgoDependentCasCell) ce).getCasCell();
			}						
		}	
		return lastCell;
	}
	
	/***
	 * Adds the given GeoCasCell object to the construction list so that
	 * it becomes the n-th GeoCasCell in the list. Other cas cells are shifted right.
	 * 
	 * @param n number starting at 0
	 */
	public void setCasCellRow(GeoCasCell casCell, int n) {
		GeoCasCell nthCasCell = getCasCell(n);
		if (nthCasCell == null) {
			addToConstructionList(casCell, false);			
		} else {
			addToConstructionList(casCell, nthCasCell.getConstructionIndex());			
		}
		
		addToGeoSetWithCasCells(casCell);
	}
	
//	/***
//	 * Returns position of the given GeoCasCell object (free or dependent) in the construction list.
//	 * This is the row number used in the CAS view.
//	 * 
//	 * @return row number of casCell for CAS view or -1 if casCell is not in construction list
//	 */
//	public int getCasCellRow(GeoCasCell casCell) {
//		int counter = 0;
//		for (ConstructionElement ce : ceList) {
//			if (ce instanceof GeoCasCell) {
//				if (ce == casCell)
//					return counter;		
//				else
//					++counter;
//			}
//			else if (ce instanceof AlgoDependentCasCell) {
//				if (ce == ((AlgoDependentCasCell) ce).getCasCell())
//					return counter;
//				else
//					++counter;
//			}					
//		}
//		
//		// casCell not found
//		return -1;
//	}
	
	/**
	 * Tells all GeoCasCells that the order of cas cells may have changed.
	 * They can then update their row number and input strings with row references.
	 */
	public void updateCasCellRows() {
		// update all row numbers first
		int counter = 0;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				((GeoCasCell) ce).setRowNumber(counter);
				counter++;
			}
			else if (ce instanceof AlgoDependentCasCell) {
				((AlgoDependentCasCell) ce).getCasCell().setRowNumber(counter);
				counter++;
			}					
		}
		
		// now update all row references
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				((GeoCasCell) ce).updateInputStringWithRowReferences();
			}
			else if (ce instanceof AlgoDependentCasCell) {
				((AlgoDependentCasCell) ce).getCasCell().updateInputStringWithRowReferences();				
			}					
		}						
		
		// TODO remove
		System.out.println("*** updateCasCellRows() ***");
		counter = 0;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
			    int row = ((GeoCasCell) ce).getRowNumber();
				System.out.println("Row " + row + ": " + ((GeoCasCell) ce).toString());
				counter++;
			}
			else if (ce instanceof AlgoDependentCasCell) {
				int row = ((AlgoDependentCasCell) ce).getCasCell().getRowNumber();
				System.out.println("Row " + row + ": " + (((AlgoDependentCasCell) ce).getCasCell()).toString());
				counter++;
			}					
		}

	}

	
	/**
	 * Moves object at position from to position to in this construction.
	 * @param fromIndex index of element to be moved
	 * @param toIndex target index of this element
	 * @return whether construction list was changed or not.
	 */
	public boolean moveInConstructionList(int fromIndex, int toIndex) {
		// check if move is possible
		ConstructionElement ce = ceList.get(fromIndex);
		boolean change = fromIndex != toIndex
				&& ce.getMinConstructionIndex() <= toIndex
				&& toIndex <= ce.getMaxConstructionIndex();
		if (change) {
			
			if (ce instanceof GeoElement) {
				// TODO: update Algebra View
				AbstractApplication.debug("TODO: update Algebra View");
			}
			
			// move the construction element
			ceList.remove(fromIndex);
			ceList.add(toIndex, ce);
			
			// update construction indices
			updateConstructionIndex(Math.min(toIndex, fromIndex));
			
			// update construction step
			if (fromIndex <= step && step < toIndex) {
				--step;

				ce.notifyRemove();
			} else if (toIndex <= step && step < fromIndex) {
				++step;

				ce.notifyAdd();
			}					
			
			// update cas row references
			if (ce instanceof GeoCasCell || ce instanceof AlgoDependentCasCell)
				updateCasCellRows();
			
			updateAllConstructionProtocolAlgorithms(); // Michael Borcherds
		}											// 2008-05-15
		
		return change;
	}

	// update all indices >= pos
	



	/**
	 * Calls remove() for every ConstructionElement in the construction list.
	 * After this the construction list will be empty.
	 */
	public void clearConstruction() {		
		kernel.resetGeoGebraCAS();
		
		ceList.clear();
		algoList.clear();
		
		geoSetConsOrder.clear();
		geoSetWithCasCells.clear();
		geoSetLabelOrder.clear();
		
		geoSetsTypeMap.clear();		
		euclidianViewCE.clear();			
		initGeoTables();

		// reinit construction step
		step = -1;

		// delete title, author, date
		title = null;
		author = null;
		date = null;
		worksheetText[0] = null;
		worksheetText[1] = null;

		usedMacros = null;
	}

	
	
	
	

	

	
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






	
	/**
	 * Adds given GeoCasCell to a table where (label, object) pairs 
	 * of CAS view variables are stored.
	 * @param geoCasCell GeoElement to be added, must have assignment variable
	 * @see #removeCasCellLabel(String)
	 * @see #lookupCasCellLabel(String)
	 */
	public void putCasCellLabel(GeoCasCell geoCasCell, String label) {
		if (label == null) return;
		
		if (geoCasCellTable == null) 
			geoCasCellTable = new HashMap<String,GeoCasCell>(); 
		geoCasCellTable.put(label, geoCasCell);	
	}
	
	/**
	 * Removes given GeoCasCell from the CAS variable table
	 * and from the underlying CAS.
	 * 
	 * @param variable to be removed	
	 * @see #putCasCellLabel(GeoCasCell, String)
	 */
	public void removeCasCellLabel(String variable) {
		removeCasCellLabel(variable, true);	
	}		
	
	/**
	 * Removes given GeoCasCell from the CAS variable table
	 * and if wanted from the underlying CAS too.
	 * 
	 * @param unbindInCAS whether variable should be removed from underlying CAS too.
	 * @see #putCasCellLabel(GeoCasCell, String)
	 */
	public void removeCasCellLabel(String variable, boolean unbindInCAS) {
		if (geoCasCellTable != null) {
			GeoCasCell geoCasCell = geoCasCellTable.remove(variable);			
			if (unbindInCAS) 
				geoCasCell.unbindVariableInCAS();			
		}		
	}		
	
	
//	/**
//	 * Adds the given GeoCasCell to this construction, i.e.
//	 * to the construction list and the geoSetWithCasCells.
//	 */
//	public void addToConstruction(GeoCasCell geoCasCell) {
//		addToConstructionList(geoCasCell, true);
//		addToGeoSetWithCasCells(geoCasCell);
//	}
	
//	/**
//	 * Removes the given GeoCasCell from this construction, i.e.
//	 * from the construction list and the geoSetWithCasCells.
//	 */
//	public void removeFromConstruction(GeoCasCell geoCasCell) {
//		removeFromConstructionList(geoCasCell);
//		removeFromGeoSetWithCasCells(geoCasCell);
//	}
	
	/**
	 * Adds the given GeoCasCell to a set with all
	 * labeled GeoElements and CAS cells needed for notifyAll().
	 */
	public void addToGeoSetWithCasCells(GeoCasCell geoCasCell) {
		geoSetWithCasCells.add(geoCasCell);
	}
	
	/**
	 * Removes the given GeoCasCell from a set with all
	 * labeled GeoElements and CAS cells needed for notifyAll().
	 */
	public void removeFromGeoSetWithCasCells(GeoCasCell geoCasCell) {
		geoSetWithCasCells.remove(geoCasCell);
	}

	

	
//	
//	private void fixOrderingInGeoSets(GeoElement geo) {
//		fixOrderInSet(geo, geoSetConsOrder);
//		fixOrderInSet(geo, geoSetWithCasCells);
//		fixOrderInSet(geo, geoSetLabelOrder);
//
//		// set ordered type set
//		int type = geo.getGeoClassType();
//		TreeSet<GeoElement> typeSet = geoSetsTypeMap.get(type);
//		if (typeSet != null)
//			fixOrderInSet(geo, typeSet);
//	}
//	
//	private void fixOrderInSet(GeoElement geo, TreeSet<GeoElement> set ) {
//		// fix ordering by removing and adding again
//		if (set.contains(geo)) {
//			set.remove(geo);
//			set.add(geo);
//		}
//	}

	

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * @param label label to be looked for
	 * @return may return null
	 */
	public GeoElement lookupLabel(String label) {
		return lookupLabel(label, false);
	}
	
	/**
	 * Returns a GeoCasCell for the given label. Note: only objects with
	 * construction index 0 to step are available.
	 * @param label to be looked for
	 * @return may return null
	 */
	GeoCasCell lookupCasCellLabel(String label) {
		GeoCasCell geoCasCell = null;

		// global var handling
		if (geoCasCellTable != null)
			geoCasCell = geoCasCellTable.get(label);
		
		// TODO add lookupCasCellLabel support for construction steps
//		// STANDARD CASE: variable name found
//		if (geoCasCell != null) {
//			return (GeoCasCell) checkConstructionStep(geoCasCell);
//		}
		
		return geoCasCell;
	}
	
	/**
	 * Returns GeoCasCell referenced by given row label.
	 * 
	 * @param label row reference label, e.g. $5 for 5th row or $ for current row
	 * @return referenced row or null
	 */
	public GeoCasCell lookupCasRowReference(String label) {
		if (!label.startsWith(ExpressionNode.CAS_ROW_REFERENCE_PREFIX)) return null;				
			
		// $5 for 5th row
		int rowRef = -1;
		try {
			rowRef = Integer.parseInt(label.substring(1));
		} catch (Exception e) {
			System.err.println("Invalid CAS row reference: " + label);
		}			
		
		// we start to count at 0 internally but at 1 in the user interface
		return getCasCell(rowRef-1);			
	}

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * @param label to be looked for
	 * @param allowAutoCreate
	 *            : true = allow automatic creation of missing labels (e.g. for
	 *            spreadsheet)
	 * @return may return null
	 */
	public GeoElement lookupLabel(String label, boolean allowAutoCreate) {//package private
		if (label == null)
			return null;
		
		// local var handling
		if (localVariableTable != null) {
			GeoElement localGeo = localVariableTable.get(label);
			if (localGeo != null)
				return localGeo;
		}

		// global var handling
		GeoElement geo = geoTableVarLookup(label);

		// STANDARD CASE: variable name found
		if (geo != null) {
			return checkConstructionStep(geo);
		}
		
		// DESPARATE CASE: variable name not found	
		        
    	/*
		 * CAS VARIABLE HANDLING
		 * e.g. ggbtmpvara for a
		 */
        label = kernel.removeCASVariablePrefix(label);
		geo = geoTableVarLookup(label);				
		if (geo != null) {
			// geo found for name that starts with TMP_VARIABLE_PREFIX or GGBCAS_VARIABLE_PREFIX
			return checkConstructionStep(geo);
		}	
				
		/*
		 * SPREADSHEET $ HANDLING
		 * In the spreadsheet we may have variable names like
		 * "A$1" for the "A1" to deal with absolute references.
		 * Let's remove all "$" signs from label and try again.
		 */ 	
        if (label.indexOf('$') > -1) {
			StringBuilder labelWithout$ = new StringBuilder(label.length());
			for (int i=0; i < label.length(); i++) {
				char ch = label.charAt(i);
				if (ch != '$')
					labelWithout$.append(ch);
			}

			// allow automatic creation of elements
	        geo = lookupLabel(labelWithout$.toString(), allowAutoCreate);				
			if (geo != null) {
				// geo found for name that includes $ signs
				return checkConstructionStep(geo);
			}
        }	
        
        // try upper case version for spreadsheet label like a1
        if (allowAutoCreate) {	    	
			if (Character.isLetter(label.charAt(0)) // starts with letter
				&& Character.isDigit(label.charAt(label.length()-1)))  // ends with digit
			{
				String upperCaseLabel = label.toUpperCase();
				geo = geoTableVarLookup(upperCaseLabel);
				if (geo != null) {
					return checkConstructionStep(geo);
				}
			}
        }			
        
        // if we get here, nothing worked: 
        // possibly auto-create new GeoElement with that name			
		if (allowAutoCreate)
			return autoCreateGeoElement(label);
		else
			return null;			
	}	
	
	/**
	 * Returns geo if it is available at the current
	 * construction step, otherwise returns null.
	 */
	private GeoElement checkConstructionStep(GeoElement geo) {
		// check if geo is available for current step
		if (geo.isAvailableAtConstructionStep(step))
			return geo;
		else
			return null;
	}

	/**
	 * Automatically creates a GeoElement object for a certain label that is not
	 * yet used in the geoTable of this construction. This is done for e.g.
	 * point i = (0,1), number e = Math.E, empty spreadsheet cells
	 * 
	 * @param label
	 * @see #willAutoCreateGeoElement()
	 */
	private GeoElement autoCreateGeoElement(String label) {		
		GeoElement createdGeo = null;
		boolean fix = true;
		boolean auxilliary = true;
		
//		// if referring to variable "i" (complex) that is undefined, create it
//		if (label.equals("i") || label.equals(Unicode.IMAGINARY)) {
//			
//			GeoElement geo = kernel.lookupLabel(Unicode.IMAGINARY);
//			
//			if (geo != null && geo.isGeoPoint() && ((GeoPoint)geo).isI()) {
//				createdGeo = (GeoPoint)geo;
//			} else {			
//			
//				GeoPoint point = new GeoPoint(this);
//				point.setCoords(0.0d, 1.0d, 1.0d);
//				point.setEuclidianVisible(false);
//				point.setComplex();
//				point.setIsI();
//				createdGeo = point;
//				
//				if (geo == null) label = Unicode.IMAGINARY; // else just leave as "i" if label not free
//				
//			}
//		}
//
//		// if referring to variable "e" (Euler no) that is undefined, create it
//		// this is then changed into exp(x) in ExpressionNode.resolveVariables()
//		else if (label.equals("e")) {
//			GeoNumeric number = new GeoNumeric(this);
//			number.setValue(Math.E);
//			number.setNeedsReplacingInExpressionNode();
//			createdGeo = number;			
//		}
		
		// expression like AB, autocreate AB=Distance[A,B] or AB = A * B according to whether A,B are points or numbers
		if (label.length() == 2) {
			GeoElement geo1 = kernel.lookupLabel(label.charAt(0)+"");
			if (geo1 != null && geo1.isGeoPoint()) {
				GeoElement geo2 = kernel.lookupLabel(label.charAt(1)+"");
				if (geo2 != null && geo2.isGeoPoint()) {
					AlgoDistancePoints dist = new AlgoDistancePoints(this, null, (GeoPointND)geo1, (GeoPointND)geo2);
					createdGeo = dist.getDistance();
					fix = false;
				}
			} else if (geo1 != null && geo1.isNumberValue()) {
				GeoElement geo2 = kernel.lookupLabel(label.charAt(1)+"");
				if (geo2 != null && geo2.isNumberValue()) {
					ExpressionNode node = new ExpressionNode(kernel, ((NumberValue)geo1).evaluate(), Operation.MULTIPLY, ((NumberValue)geo2).evaluate());
					AlgoDependentNumber algo = new AlgoDependentNumber(this, null, node, false);
					createdGeo = algo.getNumber();
					fix = false;					
				}
			}

		} else if (label.length() == 3) {
			if (label.equals("lnx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("ln(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} 
		} else if (label.length() == 4) {
			if (label.equals("sinx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sin(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("tanx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("tan(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("secx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sec(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cscx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("csc(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cotx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cot(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("logx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("log(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 5) {
			if (label.equals("sinhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sinh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("coshx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cosh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("tanhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("tanh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("sechx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sech(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cothx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("coth(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("acosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("asinx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asin(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("atanx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atan(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 6) {
			if (label.equals("cosecx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cosec(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("asinhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asinh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("acoshx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acosh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("atanhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atanh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 7) {
			if (label.equals("arccosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcsinx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asin(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arctanx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atan(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} 
		} else if (label.length() == 8) {
			if (label.equals("arccoshx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acosh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcsinhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asinh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arctanhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atanh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} 
		}
		

		
		// handle i or e case
		if (createdGeo != null) {
			
			
			// removed: not needed for e,i and causes bug with using Circle[D, CD 2] in locus
			//boolean oldSuppressLabelsActive = isSuppressLabelsActive();
			//setSuppressLabelCreation(false);
			
			createdGeo.setAuxiliaryObject(auxilliary);
			createdGeo.setLabel(label);
			createdGeo.setFixed(fix);
			
			// revert to previous label creation state
			//setSuppressLabelCreation(oldSuppressLabelsActive);	
			return createdGeo;
		}
						
		// check spreadsheet cells
		else {
			// for missing spreadsheet cells, create object 
			// of same type as above
			Matcher cellNameMatcher = GeoElementSpreadsheet.spreadsheetPattern.matcher(label);
			if (cellNameMatcher.matches()) {
				String col = cellNameMatcher.group(1);
				int row = Integer.parseInt(cellNameMatcher.group(2));
	
				// try to get neighbouring cell for object type look above
				GeoElement neighbourCell = geoTableVarLookup(col + (row - 1));
				if (neighbourCell == null) // look below
					neighbourCell = geoTableVarLookup(col + (row + 1));
	
				label = col + row;			
				createdGeo = createSpreadsheetGeoElement(neighbourCell, label);
			}
		}	
			
		return createdGeo;
	}
	
	/**
	 * Returns whether the specified label will automatically create a GeoElement
	 * when autoCreateGeoElement() is called with it.
	 * @param label Label
	 * @return true iff the label will create new geo when autoCreateGeoElement() is called with it.
	 * 
	 */
	final public static boolean willAutoCreateGeoElement(String label) {
		if ("i".equals(label) || "e".equals(label))
			return true;
		
		Matcher cellNameMatcher = GeoElementSpreadsheet.spreadsheetPattern. matcher(label);
		if (cellNameMatcher.matches())
			return true;
		
		return false;		
	}
	
	/**
	 * Creates a new GeoElement for the spreadsheet of same type as neighbourCell.
	 * @return new GeoElement of desired type
	 * @param neighbourCell another geo of the desired type
	 * @param label Label for the new geo
	 */
	final public GeoElement createSpreadsheetGeoElement(GeoElement neighbourCell, String label) {	
		GeoElement result; 
		
		// found neighbouring cell: create geo of same type
		if (neighbourCell != null) {
			result = neighbourCell.copy();
		}
		// no neighbouring cell: create number with value 0
		else {
			result = new GeoNumeric(this);
		}				
		
		// make sure that label creation is turned on
		boolean oldSuppressLabelsActive = isSuppressLabelsActive();
		setSuppressLabelCreation(false);
		
		// set 0 and label
		result.setZero();
		result.setAuxiliaryObject(true);
		result.setLabel(label);
		
		// revert to previous label creation state
		setSuppressLabelCreation(oldSuppressLabelsActive);	
		
		return result;
	}

	

	/**
	 * Returns true if label is not occupied by any GeoElement including GeoCasCells.
	 * @param label label to be checked
	 * @return true iff label is not occupied by any GeoElement.
	 */
	public boolean isFreeLabel(String label) {
		return isFreeLabel(label, true);
	}
	
	/**
	 * Returns true if label is not occupied by any GeoElement.
	 * @param label label to be checked
	 * @param includeCASvariables whether GeoCasCell labels should be checked too
	 * @return true iff label is not occupied by any GeoElement.
	 */
	private boolean isFreeLabel(String label, boolean includeCASvariables) {
		if (label == null)
			return false;
		else {
			// check standard geoTable
			if (geoTable.containsKey(label))
				return false;
			
			// optional: also check CAS variable table
			if (includeCASvariables && 
					geoCasCellTable != null && 
					geoCasCellTable.containsKey(label)) {				
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * Returns the next free indexed label using the given prefix.
	 * @param prefix e.g. "c"
	 * @return indexed label, e.g. "c_2"
	 */
	public String getIndexLabel(String prefix) {	
		return getIndexLabel(prefix, 1);
	}

	/**
	 * Returns the next free indexed label using the given prefix
	 * starting with the given index number.
	 * @param prefix e.g. "c"
	 * @param startIndex e.g. 2
	 * @return indexed label, e.g. "c_2"
	 */
	public String getIndexLabel(String prefix, int startIndex) {
		// start numbering with indices using suggestedLabel
		// as prefix
		String pref;
		int pos = prefix.indexOf('_');
		if (pos == -1)
			pref = prefix;
		else
			pref = prefix.substring(0, pos);

		StringBuilder sbIndexLabel = new StringBuilder();				
	
		int n = startIndex;
//		int n = 1; // start index
//		if (startIndex != null) {
//	      	try {      	  
//	      		n = Integer.parseInt(startIndex);
//	      	} catch (NumberFormatException e) {
//	      		n = 1;
//	      	}			
//		}
				
		do {
			sbIndexLabel.setLength(0);
			sbIndexLabel.append(pref);
			// n as index
			
			if (n < 10) {
				sbIndexLabel.append('_');
				sbIndexLabel.append(n);
			} else {
				sbIndexLabel.append("_{");
				sbIndexLabel.append(n);
				sbIndexLabel.append('}');
			}
			n++;
		} while (!isFreeLabel(sbIndexLabel.toString()));
		return sbIndexLabel.toString();
	}
	
	///////////////////////////////////////////////
	// LABELS DEPENDING ON ALGOS
	///////////////////////////////////////////////
	

	
      
    
    
    
    
	
	
	

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
	 * @see UndoManager#storeUndoInfo 
	 */
	public void storeUndoInfo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;
		
		if (!isUndoEnabled()) return;

		undoManager.storeUndoInfo();		
	}

	/**
	 * Restores undo info
	 * @see UndoManager#restoreCurrentUndoInfo()
	 */
	public void restoreCurrentUndoInfo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;
		collectRedefineCalls = false;
		
		if (undoManager != null)
			undoManager.restoreCurrentUndoInfo();
	}

	/**
	 * Redoes last undone step
	 */
	public void redo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.redo();
	}

	/**
	 * Undoes last operation
	 */
	public void undo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.undo();
	}

	/**
	 * Returns true iff undo is possible
	 * @return true iff undo is possible
	 */
	public boolean undoPossible() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.undoPossible();
	}

	/**
	 * Returns true iff redo is possible
	 * @return true iff redo is possible
	 */
	public boolean redoPossible() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.redoPossible();
	}


	/**
	 * Replaces oldGeo by newGeo in the current construction.
	 * This may change the logic of the
	 * construction and is a very powerful operation
	 * @param oldGeo Geo to be replaced.
	 * @param newGeo Geo to be used instead.
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
				GeoElement.setLabels(oldGeoLabel, parentAlgo.getOutput(),kernel.getGeoElementSpreadsheet());							
			}

			// copy formatting of oldGeo to newGeo
			newGeo.setAllVisualProperties(oldGeo, false);
			
			// copy label of oldGeo to newGeo
			// use setLoadedLabel() instead of setLabel() to make sure that 
			// hidden objects also get the label, see #379	
			newGeo.setLoadedLabel(oldGeoLabel);

			if (newGeo instanceof GeoText)
				newGeo.updateRepaint();
			
			return;
		}
		
	    // check for circular definition
	    if (newGeo.isChildOf(oldGeo)) {

	        // check for eg a = a + 1, A = A + (1,1), a = !a
	    	if (oldGeo.isIndependent() && oldGeo instanceof GeoNumeric) {

	            ((GeoNumeric)oldGeo).setValue(((GeoNumeric)newGeo).getDouble());
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo instanceof GeoPoint2) {

	            ((GeoPoint2)oldGeo).set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo instanceof GeoVector) {

	            ((GeoVector)oldGeo).set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo instanceof GeoBoolean) {

	            ((GeoBoolean)oldGeo).set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo.isGeoPoint() && oldGeo.isGeoElement3D()) {//GeoPoint3D

	            oldGeo.set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else {

	            restoreCurrentUndoInfo();
	            throw new CircularDefinitionException();

	        }

	    }				
		// 1) remove all brothers and sisters of oldGeo
		// 2) move all predecessors of newGeo to the left of oldGeo in construction list
		prepareReplace(oldGeo, newGeo);
				
		if (collectRedefineCalls) {
			// collecting redefine calls in redefineMap
			redefineMap.put(oldGeo, newGeo);
			return;
		}			
		AbstractApplication app = kernel.getApplication();
		boolean moveMode = app.getMode() == EuclidianConstants.MODE_MOVE 
			&& app.getSelectedGeos().size()>0;
		String oldSelection = null;
		if(moveMode){
			oldSelection = app.getSelectedGeos().get(0).getLabel();
		}
		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();
							
		// 3) replace oldGeo by newGeo in XML
		doReplaceInXML(consXML, oldGeo, newGeo);
		//moveDependencies(oldGeo,newGeo);
		
		
		// 4) build new construction
		buildConstruction(consXML);
		if(moveMode){
			GeoElement selGeo = kernel.lookupLabel(oldSelection);
			app.addSelectedGeo(selGeo, false);		
			((EuclidianViewInterface)app.getEuclidianView()).getEuclidianController().handleMovedElement(selGeo,false);
		}
	}
	
	/**
	 * Changes the given casCell taking care of necessary redefinitions.
	 * This may change the logic of the construction and is a very powerful operation.
	 * 
	 * @param casCell casCell to be changed
	 * @throws Exception 
	 */
	public void changeCasCell(GeoCasCell casCell) throws Exception {								
		// move all predecessors of casCell to the left of casCell in construction list
		updateConstructionOrder(casCell);			
		
		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();
						
		// build new construction to make sure all ceIDs are correct after the redefine
		buildConstruction(consXML);		
	}

	private GeoElement keepGeo;
	public GeoElement getKeepGeo(){
		return keepGeo;
	}
	

	// 1) remove all brothers and sisters of oldGeo
	// 2) move all predecessors of newGeo to the left of oldGeo in construction list
	private void prepareReplace(GeoElement oldGeo, GeoElement newGeo)  {
		AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
		AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();
		
		// 1) remove all brothers and sisters of oldGeo
		if (oldGeoAlgo != null) {
			keepGeo=oldGeo;
			oldGeoAlgo.removeOutputExcept(oldGeo);
			keepGeo=null;
		}

		// if newGeo is not in construction index, we must set its index now
		// in order to let (2) and (3) work
		if (newGeo.getConstructionIndex() == -1) {
			int ind = ceList.size();
			if (newGeoAlgo == null)
				newGeo.setConstructionIndex(ind);
			else
				newGeoAlgo.setConstructionIndex(ind);
		}
		
		// make sure all output objects of newGeoAlgo are labeled, otherwise
		// we may end up with several objects that have the same label
		if (newGeoAlgo != null) {
			for (int i=0; i < newGeoAlgo.getOutputLength(); i++) {
				GeoElement geo = newGeoAlgo.getOutput(i);
				if (geo != newGeo && geo.isDefined() && !geo.isLabelSet()) {
					geo.setLabel(null); // get free label
				}				
			}
		}

		// 2) move all predecessors of newGeo to the left of oldGeo in
		// construction list
		updateConstructionOrder(oldGeo, newGeo);
	}

	/**
	 * Moves all predecessors of newGeo (i.e. all objects that newGeo depends
	 * upon) to the left of oldGeo in the construction list
	 */
	private void updateConstructionOrder(GeoElement oldGeo, GeoElement newGeo) {
		TreeSet<GeoElement> predSet = newGeo.getAllPredecessors();

		// check if moving is needed
		// find max construction index of newGeo's predecessors and newGeo
		// itself
		int maxPredIndex = newGeo.getConstructionIndex();
		for (GeoElement pred : predSet) {
			int predIndex = pred.getConstructionIndex();
			if (predIndex > maxPredIndex)
				maxPredIndex = predIndex;
		}
		
		// no reordering is needed
		if (oldGeo.getConstructionIndex() > maxPredIndex)
			return;

		// reordering is needed
		// move all predecessors of newGeo (i.e. all objects that geo depends
		// upon) as far as possible to the left in the construction list
		for (GeoElement pred: predSet) {
			moveInConstructionList(pred, pred.getMinConstructionIndex());
		}

		// move newGeo to the left as well (important if newGeo already existed
		// in construction)
		moveInConstructionList(newGeo, newGeo.getMinConstructionIndex());

		// move oldGeo to its maximum construction index
		moveInConstructionList(oldGeo, oldGeo.getMaxConstructionIndex());
	}
	
	/**
	 * Makes sure that geoCasCell comes after all its predecessors 
	 * in  the construction list.
	 * @return whether construction list order was changed
	 */
	private boolean updateConstructionOrder(GeoCasCell casCell) {
		// collect all predecessors of casCell
		TreeSet<GeoElement> allPred = new TreeSet<GeoElement>();
		for (GeoElementInterface directInput : casCell.getGeoElementVariables()) {
			allPred.addAll(((GeoElement) directInput).getAllPredecessors());
			allPred.add((GeoElement) directInput);
		}
		
		// Find max construction index of casCell's predecessors 
		int maxPredIndex = 0;
		for (GeoElement pred : allPred) {
			int predIndex = pred.getConstructionIndex();
			if (predIndex > maxPredIndex)
				maxPredIndex = predIndex;
		}

		// if casCell comes after all its new predecessors,
		// no reordering is needed
		if (casCell.getConstructionIndex() > maxPredIndex)
			return false;
		
		// reordering is needed
		// maybe we can move casCell down in the construction list
		if (casCell.getMaxConstructionIndex() > maxPredIndex) {
			moveInConstructionList(casCell, maxPredIndex + 1);
			return true;
		}

		// reordering is needed but we cannot simply move down the casCell
		// because it has dependent objects:
		// move all predecessors of casCell up as far as possible
		maxPredIndex = 0;
		for (GeoElement pred: allPred) {
			moveInConstructionList(pred, pred.getMinConstructionIndex());
			maxPredIndex = Math.max(maxPredIndex, pred.getConstructionIndex());			
		}
		
		// if casCell still comes before one of its predecessors
		// we have to move casCell
		if (casCell.getConstructionIndex() < maxPredIndex) {	
			return true;
		}

		// maybe we can move casCell down in the construction list now
		if (casCell.getMaxConstructionIndex() > maxPredIndex) {
			moveInConstructionList(casCell, maxPredIndex+1);
			return true;
		}
		else {
			System.err.println("Construction.updateConstructionOrder(GeoCasCell) failed: " + casCell);
			return false;
		}			
	}
	
	
	
	/**
	 * Processes all collected redefine calls as a batch to improve performance.
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
		} 
		catch (Exception e) {						
			throw e;
		}
		finally {
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

	/*
	 * XML output
	 */

//	/**
//	 * Returns this construction in XML format. GeoGebra File Format.
//	 */
//	public String getXML(boolean includeConstruction) {
//		StringBuilder sb = new StringBuilder();
//
//		// kernel settings
//		sb.append("<kernel>\n");
//
//		// continuity: true or false, since V3.0
//		sb.append("\t<continuous val=\"");
//		sb.append(kernel.isContinuous());
//		sb.append("\"/>\n");
//		
//		if (kernel.useSignificantFigures) {
//			// significant figures
//			sb.append("\t<significantfigures val=\"");
//			sb.append(kernel.getPrintFigures());
//			sb.append("\"/>\n");			
//		}
//		else
//		{
//			// decimal places
//			sb.append("\t<decimals val=\"");
//			sb.append(kernel.getPrintDecimals());
//			sb.append("\"/>\n");
//		}
//		
//		// angle unit
//		sb.append("\t<angleUnit val=\"");
//		sb.append(angleUnit == Kernel.ANGLE_RADIANT ? "radiant" : "degree");
//		sb.append("\"/>\n");
//
//		// coord style
//		sb.append("\t<coordStyle val=\"");
//		sb.append(kernel.getCoordStyle());
//		sb.append("\"/>\n");
//		
//		// animation
//		if (kernel.isAnimationRunning()) {
//			sb.append("\t<startAnimation val=\"");
//			sb.append(kernel.isAnimationRunning());
//			sb.append("\"/>\n");
//		}
//
//		sb.append("</kernel>\n");
//
//		// construction XML
//		if (includeConstruction)
//			sb.append(getConstructionXML());
//
//		return sb.toString();
//	}

//	/**
//	 * Returns this construction in I2G format. Intergeo File Format.
//	 * (Yves Kreis)
//	 */
//	public String getI2G(int mode) {
//		StringBuilder sb = new StringBuilder();
//
//		// construction I2G
//		sb.append(getConstructionI2G(mode));
//
//		return sb.toString();
//	}


	
	
	
	
	
	/**
	 * Returns undo xml string of this construction.
	 * @return StringBuilder with xml  of this construction.
	 */
	public StringBuilder getCurrentUndoXML() {
		return xmlio.getUndoXML(this);
	}
	
	private MyXMLio xmlio;
	
	public void setXMLio(MyXMLio xmlio){
		this.xmlio=xmlio;
	}

	
	
	/**
	 * Add a macro to list of used macros
	 * @param macro Macro to be added
	 */
	public final void addUsedMacro(Macro macro) {
		if (usedMacros == null)
			usedMacros = new ArrayList<Macro>();
		usedMacros.add(macro);
	}

	/**
	 * Returns list of macros used in this construction
	 * @return list of macros used in this construction
	 */
	public ArrayList<Macro> getUsedMacros() {
		return usedMacros;
	}
	
		
	
    
    
    
   
    
}
