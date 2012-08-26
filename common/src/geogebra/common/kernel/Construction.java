package geogebra.common.kernel;

import geogebra.common.cas.CASException;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoDistancePoints;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoProve;
import geogebra.common.kernel.algos.AlgoProveDetails;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoDependentCasCell;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Manages construction elements
 * 
 * @author Markus
 * 
 */
public class Construction {

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
	protected Construction(Kernel k, Construction parentConstruction) {
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

	public Map<Integer,GeoNumeric> constsM= new TreeMap<Integer,GeoNumeric>(), 
			intsM= new TreeMap<Integer,GeoNumeric>(), 
			complexNumbersM = new TreeMap<Integer,GeoNumeric>();
	
	/**
	 * creates the ConstructionDefaults consDefaults
	 */
	protected void newConstructionDefaults() {
		consDefaults = new ConstructionDefaults(this);
	}

	// list of Macro commands used in this construction
	// TODO: specify type once Macro is ported
	private ArrayList<Macro> usedMacros;
	/** UndoManager */
	protected UndoManager undoManager;

	/** default elements */
	protected ConstructionDefaults consDefaults;
	// TODO: make private once we port ClearConstruction
	private String title, author, date;
	// text for dynamic worksheets: 0 .. above, 1 .. below
	private String[] worksheetText = new String[2];

	// showOnlyBreakpoints in construction protocol
	private boolean showOnlyBreakpoints;

	/** construction belongs to kernel */
	protected Kernel kernel;

	// current construction step (-1 ... ceList.size() - 1)
	// step == -1 shows empty construction
	private int step;

	// in macro mode no new labels or construction elements
	// can be added
	private boolean supressLabelCreation = false;

	// a map for sets with all labeled GeoElements in alphabetical order of
	// specific types
	// (points, lines, etc.)
	//
	private HashMap<GeoClass, TreeSet<GeoElement>> geoSetsTypeMap;

	// ConstructionElement List (for objects of type ConstructionElement)
	private ArrayList<ConstructionElement> ceList;

	// AlgoElement List (for objects of type AlgoElement)
	private ArrayList<AlgoElement> algoList; // used in updateConstruction()

	/** Table for (label, GeoElement) pairs, contains global variables */
	protected HashMap<String, GeoElement> geoTable;

	// list of algorithms that need to be updated when EuclidianView changes
	private ArrayList<EuclidianViewCE> euclidianViewCE;

	/** Table for (label, GeoElement) pairs, contains local variables */
	protected HashMap<String, GeoElement> localVariableTable;

	// set with all labeled GeoElements in ceList order
	private TreeSet<GeoElement> geoSetConsOrder;

	// set with all labeled GeoElements in alphabetical order
	private TreeSet<GeoElement> geoSetLabelOrder;
	private TreeSet<GeoElement> geoSetWithCasCells;

	// list of random numbers or lists
	private TreeSet<GeoElement> randomElements;

	/**
	 * Table for (label, GeoCasCell) pairs, contains global variables used in
	 * CAS view
	 */
	protected HashMap<String, GeoCasCell> geoCasCellTable;

	// collect replace() requests to improve performance
	// when many cells in the spreadsheet are redefined at once
	private boolean collectRedefineCalls = false;
	private HashMap<GeoElement, GeoElement> redefineMap;
	private GeoElement keepGeo;

	/**
	 * @return geo temporarily kept inside this construction
	 */
	public GeoElement getKeepGeo() {
		return keepGeo;
	}

	// axis objects
	private GeoAxis xAxis, yAxis;
	private String xAxisLocalName, yAxisLocalName;
	private GeoPoint origin;

	/**
	 * Returns the point (0,0)
	 * 
	 * @return point (0,0)
	 */
	public final GeoPoint getOrigin() {
		if (origin == null) {
			origin = new GeoPoint(this);
			origin.setCoords(0.0, 0.0, 1.0);
		}
		return origin;
	}

	/**
	 * Returns x-axis
	 * 
	 * @return x-axis
	 */
	final public GeoAxis getXAxis() {
		return xAxis;
	}

	/**
	 * Returns y-axis
	 * 
	 * @return y-axis
	 */
	final public GeoAxis getYAxis() {
		return yAxis;
	}

	/**
	 * init the axis
	 */
	protected void initAxis() {
		xAxis = new GeoAxis(this, GeoAxisND.X_AXIS);
		yAxis = new GeoAxis(this, GeoAxisND.Y_AXIS);
	}

	
	/**
	 * Construction constants (xAxis, yAxis, ...)
	 *
	 */
	public enum Constants {
		/**
		 * not a constant
		 */
		NOT, 
		/**
		 * x axis
		 */
		X_AXIS,
		/**
		 * y axis
		 */
		Y_AXIS}

	
	/**
	 * 
	 * @param geo geo
	 * @return which constant geo (xAxis, yAxis, ...)
	 */
	public Constants isConstantElement(GeoElement geo){
		if (geo==xAxis)
			return Constants.X_AXIS;
		if (geo==yAxis)
			return Constants.Y_AXIS;
		
		return Constants.NOT;
	}
	

	/**
	 * Renames xAxis and yAxis in the geoTable and sets *AxisLocalName-s
	 * acordingly
	 */
	public void updateLocalAxesNames() {
		geoTable.remove(xAxisLocalName);
		geoTable.remove(yAxisLocalName);

		App app = kernel.getApplication();
		xAxisLocalName = app.getPlain("xAxis");
		yAxisLocalName = app.getPlain("yAxis");
		geoTable.put(xAxisLocalName, xAxis);
		geoTable.put(yAxisLocalName, yAxis);
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
	 * Returns construction's author
	 * 
	 * @return construction's author
	 */
	public String getAuthor() {
		return (author != null) ? author : "";
	}

	/**
	 * Returns construction's date
	 * 
	 * @return construction's date
	 */
	public String getDate() {
		return (date != null) ? date : "";
	}

	/**
	 * Returns construction's title
	 * 
	 * @return construction's title
	 */
	public String getTitle() {
		return (title != null) ? title : "";
	}

	/**
	 * Sets construction's author
	 * 
	 * @param string
	 *            new author
	 */
	public void setAuthor(String string) {
		author = string;
	}

	/**
	 * Sets construction's date
	 * 
	 * @param string
	 *            new date
	 */
	public void setDate(String string) {
		date = string;
	}

	/**
	 * Sets construction's title
	 * 
	 * @param string
	 *            new title
	 */
	public void setTitle(String string) {
		title = string;
	}

	/**
	 * Returns part of worksheet text
	 * 
	 * @param i
	 *            0 for first part, 1 for second part
	 * @return given part of worksheet text
	 */
	public String getWorksheetText(int i) {
		return (worksheetText[i] != null) ? worksheetText[i] : "";
	}

	/**
	 * Sets part of worksheet text
	 * 
	 * @param i
	 *            0 for first part, 1 for second part
	 * @param text
	 *            new text for that part
	 */
	public void setWorksheetText(String text, int i) {
		worksheetText[i] = text;
	}

	/**
	 * TODO: make private again
	 * 
	 * @return true if at least one text is nonempty
	 */
	protected boolean worksheetTextDefined() {
		for (int i = 0; i < worksheetText.length; i++) {
			if (worksheetText[i] != null && worksheetText[i].length() > 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns current kernel
	 * 
	 * @return current kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * If this is set to true new construction elements won't get labels.
	 * 
	 * @param flag
	 *            true iff labelcreation should be supressed
	 */
	public void setSuppressLabelCreation(boolean flag) {
		supressLabelCreation = flag;
	}

	/**
	 * Returns true iff new construction elements won't get labels.
	 * 
	 * @return true iff new construction elements won't get labels.
	 */
	public boolean isSuppressLabelsActive() {
		return supressLabelCreation;
	}

	/**
	 * Returns current application
	 * 
	 * @return current application
	 */
	public App getApplication() {
		return kernel.getApplication();
	}

	/**
	 * Tests if this construction has no elements.
	 * 
	 * @return true if this construction has no GeoElements; false otherwise.
	 */
	public boolean isEmpty() {
		return ceList.isEmpty();
	}

	/**
	 * Returns the total number of construction steps.
	 * 
	 * @return Total number of construction steps.
	 */
	public int steps() {
		return ceList.size();
	}

	/**
	 * Returns the last GeoElement object in the construction list.
	 * 
	 * @return the last GeoElement object in the construction list.
	 */
	public GeoElement getLastGeoElement() {
		if (geoSetWithCasCells.size() > 0) {
			return geoSetWithCasCells.last();
		}
		return null;
	}

	/***
	 * Returns the n-th GeoCasCell object (free or dependent) in the
	 * construction list. This is the GeoCasCell in the n-th row of the CAS
	 * view.
	 * 
	 * @param row
	 *            number starting at 0
	 * @return cas cell or null if there are less cas cells in the construction
	 *         list
	 */
	public GeoCasCell getCasCell(int row) {
		if (row < 0) {
			return null;
		}

		int counter = 0;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				if (counter == row) {
					return (GeoCasCell) ce;
				}
				++counter;
			} else if (ce instanceof AlgoDependentCasCell) {
				if (counter == row) {
					return ((AlgoDependentCasCell) ce).getCasCell();
				}
				++counter;
			}
		}

		// less than n casCell
		return null;
	}

	/***
	 * Returns the last GeoCasCell object (free or dependent) in the
	 * construction list.
	 * 
	 * @return last cas cell
	 */
	public GeoCasCell getLastCasCell() {
		GeoCasCell lastCell = null;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				lastCell = (GeoCasCell) ce;
			} else if (ce instanceof AlgoDependentCasCell) {
				lastCell = ((AlgoDependentCasCell) ce).getCasCell();
			}
		}
		return lastCell;
	}

	/***
	 * Adds the given GeoCasCell object to the construction list so that it
	 * becomes the n-th GeoCasCell in the list. Other cas cells are shifted
	 * right.
	 * 
	 * @param casCell
	 *            CAS cell to be added to construction list
	 * 
	 * @param n
	 *            number starting at 0
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

	/**
	 * Adds a geo to the list of local variables using the specified local
	 * variable name .
	 * 
	 * @param varname
	 *            local variable name
	 * @param geo
	 *            local variable object
	 */
	final public void addLocalVariable(String varname, GeoElement geo) {
		if (localVariableTable == null)
			localVariableTable = new HashMap<String, GeoElement>();
		localVariableTable.put(varname, geo);
		geo.setLocalVariableLabel(varname);
	}

	/**
	 * Removes local variable of given name. Note that the underlying GeoElement
	 * object gets back its previous label as a side effect.
	 * 
	 * @param varname
	 *            name of variable to be removed
	 */
	final public void removeLocalVariable(String varname) {
		if (localVariableTable != null) {
			GeoElement geo = localVariableTable.remove(varname);
			if(geo!=null)
				geo.undoLocalVariableLabel();
		}
	}

	/**
	 * Looks for geo with given label, doesn't work for e.g. A$1
	 * 
	 * @param label
	 *            Label to be looked up
	 * @return Geo with given label
	 */
	public GeoElement geoTableVarLookup(String label) {
		GeoElement ret = geoTable.get(label);
		return ret;
	}

	/**
	 * Sets how steps in the construction protocol are handled.
	 * 
	 * @param flag
	 *            true iff construction protocol should show only breakpoints
	 */
	public void setShowOnlyBreakpoints(boolean flag) {
		showOnlyBreakpoints = flag;
	}

	/**
	 * True iff construction protocol should show only breakpoints
	 * 
	 * @return true iff construction protocol should show only breakpoints
	 */
	final public boolean showOnlyBreakpoints() {
		return showOnlyBreakpoints;
	}

	/**
	 * TODO:Private
	 * 
	 * @param pos
	 *            position
	 */
	protected void updateConstructionIndex(int pos) {
		if (pos < 0)
			return;
		int size = ceList.size();
		for (int i = pos; i < size; ++i) {
			ceList.get(i).setConstructionIndex(i);
		}
	}

	/**
	 * Updates all algos
	 * 
	 * @author Michael Borcherds
	 * @version 2008-05-15
	 * @return true iff there were any algos that wanted update TODO make
	 *         private again
	 */
	protected final boolean updateAllConstructionProtocolAlgorithms() {
		// Application.debug("updateAllConstructionProtocolAlgorithms");
		// update all algorithms
		int size = algoList.size();
		ArrayList<AlgoElement> updateAlgos = null;
		for (int i = 0; i < size; ++i) {
			AlgoElement algo = algoList.get(i);
			if (algo.wantsConstructionProtocolUpdate()) {
				if (updateAlgos == null)
					updateAlgos = new ArrayList<AlgoElement>();
				updateAlgos.add(algo);
			}
		}

		// propagate update down all dependent GeoElements
		if (updateAlgos != null) {
			AlgoElement.updateCascadeAlgos(updateAlgos);
		}

		if (updateAlgos != null) {
			App app = kernel.getApplication();
			if (app.isUsingFullGui() && app.getGuiManager() != null)
				app.getGuiManager().updateConstructionProtocol();
		}

		return updateAlgos != null;
	}

	/**
	 * Adds the given Construction Element to this Construction at position
	 * index
	 * 
	 * @param ce
	 *            element to be added
	 * @param index
	 *            index
	 */
	public void addToConstructionList(ConstructionElement ce, int index) {

		++step;
		ceList.add(index, ce);
		updateConstructionIndex(index);

		// update cas row references
		if (ce instanceof GeoCasCell)
			updateCasCellRows();

		updateAllConstructionProtocolAlgorithms();
	}

	/**
	 * Tells all GeoCasCells that the order of cas cells may have changed. They
	 * can then update their row number and input strings with row references.
	 */
	public void updateCasCellRows() {
		// update all row numbers first
		int counter = 0;
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				((GeoCasCell) ce).setRowNumber(counter);
				counter++;
			} else if (ce instanceof AlgoDependentCasCell) {
				((AlgoDependentCasCell) ce).getCasCell().setRowNumber(counter);
				counter++;
			}
		}

		// now update all row references
		for (ConstructionElement ce : ceList) {
			if (ce instanceof GeoCasCell) {
				((GeoCasCell) ce).updateInputStringWithRowReferences();
			} else if (ce instanceof AlgoDependentCasCell) {
				((AlgoDependentCasCell) ce).getCasCell()
						.updateInputStringWithRowReferences();
			}
		}
	}

	/**
	 * Moves object at position from to position to in this construction.
	 * 
	 * @param fromIndex
	 *            index of element to be moved
	 * @param toIndex
	 *            target index of this element
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
				App.debug("TODO: update Algebra View");
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
		} // 2008-05-15

		return change;
	}

	/**
	 * Adds the given Construction Element to this Construction at position
	 * getStep() + 1.
	 * 
	 * @param ce
	 *            Construction element to be added
	 * @param checkContains
	 *            : true to first check if ce is already in list
	 */
	public void addToConstructionList(ConstructionElement ce,
			boolean checkContains) {
		if (supressLabelCreation)
			return;
		if (checkContains && ce.isInConstructionList())
			return;

		/*
		 * ++step; updateAllConstructionProtocolAlgorithms(); // Michael
		 * Borcherds // 2008-05-15
		 * 
		 * ceList.add(step, ce); updateConstructionIndex(step);
		 */
		addToConstructionList(ce, step + 1);
	}

	/**
	 * Removes the given Construction Element from this Construction and updates
	 * step if necessary (i.e. if ce.getConstructionIndex() <= getStep()).
	 * 
	 * @param ce
	 *            ConstuctionElement to be removed
	 */
	public void removeFromConstructionList(ConstructionElement ce) {
		int pos = ceList.indexOf(ce);
		if (pos == -1)
			return;
		else if (pos <= step) {
			ceList.remove(ce);
			ce.setConstructionIndex(-1);
			--step;
		} else { // pos > step
			ceList.remove(ce);
			ce.setConstructionIndex(-1);
		}

		updateConstructionIndex(pos);

		// update cas row references
		if (ce instanceof GeoCasCell
				|| (ce instanceof AlgoElement && ((AlgoElement) ce)
						.getClassName() == Algos.AlgoDependentCasCell))
			updateCasCellRows();

		updateAllConstructionProtocolAlgorithms(); // Michael Borcherds
													// 2008-05-15
		/*
		 * if (ce.getClassName().equals("AlgoPrism")||ce.getClassName().equals(
		 * "AlgoPyramid")) Application.printStacktrace(ce.getClassName()); else
		 * Application.debug(ce.getClassName());
		 */
	}

	/**
	 * Adds the given algorithm to this construction's algorithm list
	 * 
	 * @param algo
	 *            to be added
	 * @see #updateConstruction()
	 */
	public void addToAlgorithmList(AlgoElement algo) {
		algoList.add(algo);
	}

	/**
	 * Removes the given algorithm from this construction's algorithm list
	 * 
	 * @param algo
	 *            algo to be removed
	 */
	public void removeFromAlgorithmList(AlgoElement algo) {
		algoList.remove(algo);
	}

	/**
	 * Moves geo to given position toIndex in this construction. Note: if ce (or
	 * its parent algorithm) is not in the construction list nothing is done.
	 * 
	 * @param geo
	 *            element to bemoved
	 * @param toIndex
	 *            new index
	 * 
	 * @return whether construction list was changed or not.
	 */
	public boolean moveInConstructionList(GeoElement geo, int toIndex) {
		AlgoElement algoParent = geo.getParentAlgorithm();
		int fromIndex = (algoParent == null) ? ceList.indexOf(geo) : ceList
				.indexOf(algoParent);
		if (fromIndex >= 0) {
			return moveInConstructionList(fromIndex, toIndex);
		}
		return false;
	}

	/**
	 * Returns true iff geo is independent and in the construction list or geo
	 * is dependent and its parent algorithm is in the construction list.
	 * 
	 * @param geo
	 *            GeoElement to be looked for
	 * @return true iff geo or its parent algo are in construction list
	 */
	public boolean isInConstructionList(GeoElement geo) {
		if (geo.isIndependent()) {
			return geo.isInConstructionList();
		}
		return geo.getParentAlgorithm().isInConstructionList();
	}

	/**
	 * Updates all algorithms in this construction
	 */
	public final void updateAllAlgorithms() {
		// update all algorithms

		// *** algoList.size() can change during the loop
		for (int i = 0; i < algoList.size(); ++i) {
			AlgoElement algo = algoList.get(i);
			algo.update();
			// AbstractApplication.debug("#"+i+" : "+algo);
		}
	}

	/**
	 * Registers an algorithm that wants to be notified when
	 * setEuclidianViewBounds() is called.
	 * 
	 * @param elem
	 *            construction element to be registered
	 */
	public final void registerEuclidianViewCE(EuclidianViewCE elem) {
		if (!euclidianViewCE.contains(elem))
			euclidianViewCE.add(elem);
	}

	/**
	 * Unregisters an algorithm that wants to be notified when
	 * setEuclidianViewBounds() is called.
	 * 
	 * @param elem
	 *            construction element to be unregistered
	 */
	public final void unregisterEuclidianViewCE(EuclidianViewCE elem) {
		euclidianViewCE.remove(elem);
	}

	/**
	 * Calls euclidianViewUpdate on all registered euclidian view construction
	 * elements Those elements which return true, will also get an update of
	 * their dependent objects.
	 * 
	 * @return true iff there were any elements to update
	 */
	public boolean notifyEuclidianViewCE() {
		boolean didUpdate = false;
		int size = euclidianViewCE.size();
		AlgorithmSet updateSet = null;
		for (int i = 0; i < size; i++) {
			didUpdate = true;
			boolean needsUpdateCascade = euclidianViewCE.get(i)
					.euclidianViewUpdate();
			if (needsUpdateCascade) {
				if (updateSet == null)
					updateSet = new AlgorithmSet();
				if (euclidianViewCE.get(i) instanceof GeoElement) {
					GeoElement geo = (GeoElement) euclidianViewCE.get(i);
					updateSet.addAll(geo.getAlgoUpdateSet());
				} else if (euclidianViewCE.get(i) instanceof AlgoElement) {
					AlgoElement algo = (AlgoElement) euclidianViewCE.get(i);
					GeoElement[] geos = algo.getOutput();
					for (GeoElement geo : geos) {
						updateSet.addAll(geo.getAlgoUpdateSet());
					}
				}
			}
		}
		if (updateSet != null)
			updateSet.updateAll();
		return didUpdate;
	}

	/**
	 * Returns true iff there are any euclidian view construction elements in
	 * this construction
	 * 
	 * @return true iff there are any euclidian view construction elements in
	 *         this construction
	 */
	public boolean hasEuclidianViewCE() {
		return euclidianViewCE.size() > 0;
	}

	/**
	 * Updates all random numbers of this construction.
	 */
	final public void updateAllRandomGeos() {
		if (randomElements == null)
			return;

		Iterator<GeoElement> it = randomElements.iterator();
		while (it.hasNext()) {
			GeoElement num = it.next();
			num.updateRandomGeo();
		}
	}

	/**
	 * Adds a number to the set of random numbers of this construction.
	 * 
	 * @param num
	 *            Element to be added
	 */
	public void addRandomGeo(GeoElement num) {
		if (randomElements == null)
			randomElements = new TreeSet<GeoElement>();
		randomElements.add(num);
		num.setRandomGeo(true);
	}

	/**
	 * Removes a number from the set of random numbers of this construction.
	 * 
	 * @param num
	 *            Element to be removed
	 */
	public void removeRandomGeo(GeoElement num) {
		if (randomElements != null)
			randomElements.remove(num);
		num.setRandomGeo(false);
	}

	/**
	 * Updates all objects in this construction.
	 */
	final public void updateConstruction() {
		// collect notifyUpdate calls using xAxis as dummy geo
		GeoElement dummyGeo = xAxis;
		kernel.startCollectingNotifyUpdate(dummyGeo);
		updateConstructionRunning = true;
		try {			
			// G.Sturr 2010-5-28: turned this off so that random numbers can be
			// traced
			// if (!kernel.isMacroKernel() && kernel.app.hasGuiManager())
			// kernel.app.getGuiManager().startCollectingSpreadsheetTraces();
	
			// update all independent GeoElements
			int size = ceList.size();
			for (int i = 0; i < size; ++i) {
				ConstructionElement ce = ceList.get(i);
				if (ce.isIndependent()) {
					ce.update();
				}
			}
	
			// update all random numbers()
			updateAllRandomGeos();
	
			// init and update all algorithms
			// make sure we call algo.initNearToRelationship() fist
			// for all algorithms because algo.update() could have
			// the side-effect to call updateCascade() for points
			// that have locateables (see GeoPoint.update())
			size = algoList.size();
	
			// init near to relationship for all algorithms:
			// this makes sure intersection points stay at their saved positions
			for (int i = 0; i < size; ++i) {
				AlgoElement algo = algoList.get(i);
				algo.initForNearToRelationship();
			}
	
			// copy array to avoid problems with the list changing during the loop
			// eg Polygon[A,B,RandomBetween[4,5]]
			// http://www.geogebra.org/forum/viewtopic.php?p=56618
			ArrayList<AlgoElement> tempList = new ArrayList<AlgoElement>(algoList);
	
			// update all algorithms
			for (int i = 0; i < size; ++i) {
				AlgoElement algo = tempList.get(i);
	
				// reinit near to relationship to make sure points stay at their
				// saved position
				// keep this line, see
				// http://code.google.com/p/geogebra/issues/detail?id=62
				algo.initForNearToRelationship();
	
				// update algorithm
				algo.update();
			}
	
			// G.Sturr 2010-5-28:
			// if (!kernel.isMacroKernel() && kernel.app.hasGuiManager())
			// kernel.app.getGuiManager().stopCollectingSpreadsheetTraces();
		}
		finally {
			kernel.stopCollectingNotifyUpdate(dummyGeo);
			updateConstructionRunning = false;
		}
	}

	/**
	 * Returns this construction in XML format. GeoGebra File Format.
	 * 
	 * @param sb
	 *            StringBuilder to which the XML is appended
	 */
	public void getConstructionXML(StringBuilder sb) {

		try {
			// save construction elements
			sb.append("<construction title=\"");
			StringUtil.encodeXML(sb, getTitle());
			sb.append("\" author=\"");
			StringUtil.encodeXML(sb, getAuthor());
			sb.append("\" date=\"");
			StringUtil.encodeXML(sb, getDate());
			sb.append("\">\n");

			// worksheet text
			if (worksheetTextDefined()) {
				sb.append("\t<worksheetText above=\"");
				StringUtil.encodeXML(sb,getWorksheetText(0));
				sb.append("\" below=\"");
				StringUtil.encodeXML(sb, getWorksheetText(1));
				sb.append("\"/>\n");
			}

			getConstructionElementsXML(sb);

			sb.append("</construction>\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Appends minimal version of the construction XML to given string builder.
	 * Only elements/commands are preserved,
	 * the rest is ignored. 
	 * @param sb String builder 
	 */
	public void getConstructionElementsXML(StringBuilder sb) {

		ConstructionElement ce;
		int size = ceList.size();
		for (int i = 0; i < size; ++i) {
			ce = ceList.get(i);
			ce.getXML(sb);
		}
	}

	/**
	 * Appends minimal version of the construction XML to given string builder.
	 * OGP version.
	 * Only elements/commands are preserved,
	 * the rest is ignored. 
	 * @param sb String builder 
	 * @param statement The statement to prove
	 */
	public void getConstructionElementsXML_OGP(StringBuilder sb, GeoElement statement) {
		AlgoElement statementAlgo = statement.getParentAlgorithm();
		StringTemplate tpl = StringTemplate.ogpTemplate;
		ConstructionElement ce;
		int size = ceList.size();
		for (int i = 0; i < size; ++i) {
			ce = ceList.get(i);
			if (ce instanceof AlgoProve || ce instanceof AlgoProveDetails) {
				// Don't put all Prove/ProveDetails commands into the XML,
				// only the current one.
				// Not really sure if this could be done a bit more elegant...
				AlgoElement ceAlgo = ce.getGeoElements()[0].getParentAlgorithm().getInput()[0].getParentAlgorithm();
				if (ceAlgo.getCommandDescription(tpl).equals(statementAlgo.getCommandDescription(tpl))) {
					ce.getXML_OGP(sb);
				}
			} else
				ce.getXML_OGP(sb);
		}
	}
	
	/**
	 * Returns this construction in regression file .out format.
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 * 
	 * @param sb
	 *            string builder
	 */
	public void getConstructionRegressionOut(StringBuilder sb) {

		// change kernel settings temporarily
		StringTemplate tpl = StringTemplate.regression;
		try {
			ConstructionElement ce;
			int size = ceList.size();
			for (int i = 0; i < size; ++i) {
				ce = ceList.get(i);
				sb.append(ce.getNameDescription() + " = ");

				if (ce instanceof GeoElement) {
					// sb.append(((GeoElement) ce).toValueString());
					((GeoElement) ce).getXMLtagsMinimal(sb, tpl);

				} else if (ce instanceof AlgoElement) {
					sb.append(((AlgoElement) ce).getCommandDescription(tpl));
					sb.append(" == ");
					sb.append(((AlgoElement) ce)
							.getAlgebraDescriptionRegrOut(tpl));
				}
				sb.append("\n");
			}
		} catch (Exception e) {
			sb.append(e.getMessage());
		}

	}

	private boolean undoEnabled = true;

	/**
	 * @return true if undo is enabled
	 */
	public boolean isUndoEnabled() {
		return undoEnabled;
	}

	/**
	 * @param b
	 *            true to enable undo
	 */
	public void setUndoEnabled(boolean b) {
		undoEnabled = b;

	}

	/*
	 * Construction List Management
	 */

	/**
	 * Returns the ConstructionElement for the given construction index.
	 * 
	 * @return the ConstructionElement for the given construction index.
	 * @param index
	 *            Construction index of element to look for
	 */
	public ConstructionElement getConstructionElement(int index) {
		if (index < 0 || index >= ceList.size())
			return null;
		return ceList.get(index);
	}
	
	/**
	 * 
	 * @return first geo if exists
	 */
	public GeoElement getFirstGeo(){
		
		ConstructionElement ce = null;
		GeoElement geo = null;
		int index = 0;
		
		while(index<ceList.size() && geo == null){
			ce = ceList.get(index);
			if (ce instanceof GeoElement)
				geo = (GeoElement) ce;
			index++;
		}
		
		return geo;

	}

	/**
	 * Returns a set with all labeled GeoElement objects of this construction in
	 * construction order.
	 * 
	 * @return set with all labeled geos in construction order.
	 */
	final public TreeSet<GeoElement> getGeoSetConstructionOrder() {
		return geoSetConsOrder;
	}

	/**
	 * Returns a set with all labeled GeoElement and all GeoCasCell objects of
	 * this construction in construction order.
	 * 
	 * @return set with all labeled geos and CAS cells in construction order.
	 */
	final public TreeSet<GeoElement> getGeoSetWithCasCellsConstructionOrder() {
		return geoSetWithCasCells;
	}

	/**
	 * Returns a set with all labeled GeoElement objects of this construction in
	 * alphabetical order of their labels.
	 * 
	 * @return set with all labeled geos in alphabetical order.
	 */
	final public TreeSet<GeoElement> getGeoSetLabelOrder() {
		return geoSetLabelOrder;
	}

	/**
	 * Starts to collect all redefinition calls for the current construction.
	 * This is used to improve performance of many redefines in the spreadsheet
	 * caused by e.g. relative copy.
	 * 
	 * @see #processCollectedRedefineCalls()
	 */
	public void startCollectingRedefineCalls() {
		collectRedefineCalls = true;
		if (redefineMap == null)
			redefineMap = new HashMap<GeoElement, GeoElement>();
		redefineMap.clear();
	}

	/**
	 * Stops collecting redefine calls.
	 * 
	 * @see #processCollectedRedefineCalls()
	 */
	public void stopCollectingRedefineCalls() {
		collectRedefineCalls = false;
		if (redefineMap != null)
			redefineMap.clear();
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
	 *             i.e. for circular definition
	 */
	public void replace(GeoElement oldGeo, GeoElement newGeo) throws Exception {
		if (oldGeo == null || newGeo == null || oldGeo == newGeo)
			return;
		//assignment v=? should make v undefined, not change its type
		if (oldGeo.isIndependent() && newGeo instanceof GeoNumeric
				&& newGeo.isIndependent() && !newGeo.isDefined()) {
			oldGeo.setUndefined();
			oldGeo.updateRepaint();
			return;
		}
		App.debug(oldGeo.getCommandDescription(StringTemplate.maxPrecision)+newGeo.getCommandDescription(StringTemplate.maxPrecision));
		if(oldGeo.getCommandDescription(StringTemplate.maxPrecision).equals(
				newGeo.getCommandDescription(StringTemplate.maxPrecision)) &&
				oldGeo.getParentAlgorithm()!=null){
			ArrayList<AlgoElement> ae = new ArrayList<AlgoElement>();
			ae.add(oldGeo.getParentAlgorithm());
			AlgoElement.updateCascadeAlgos(ae);
			return;
		}
			
		// if oldGeo does not have any children, we can simply
		// delete oldGeo and give newGeo the name of oldGeo
		if (!oldGeo.hasChildren()) {
			String oldGeoLabel = oldGeo.getLabelSimple();
			newGeo.moveDependencies(oldGeo);
			oldGeo.remove();

			if (newGeo.isIndependent())
				addToConstructionList(newGeo, true);
			else {
				AlgoElement parentAlgo = newGeo.getParentAlgorithm();
				addToConstructionList(parentAlgo, true);
				// make sure all output objects get labels, see #218
				GeoElement.setLabels(oldGeoLabel, parentAlgo.getOutput());
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

			} else if (oldGeo.isIndependent() && oldGeo instanceof GeoPoint) {

				((GeoPoint) oldGeo).set(newGeo);
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
		App app = kernel.getApplication();
		boolean moveMode = app.getMode() == EuclidianConstants.MODE_MOVE
				&& app.getSelectedGeos().size() > 0;
		String oldSelection = null;
		if (moveMode) {
			oldSelection = app.getSelectedGeos().get(0)
					.getLabel(StringTemplate.defaultTemplate);
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
			app.addSelectedGeo(selGeo, false, true);
			app.getActiveEuclidianView().getEuclidianController()
					.handleMovedElement(selGeo, false);
		}
	}

	/**
	 * Processes all collected redefine calls as a batch to improve performance.
	 * 
	 * @see #startCollectingRedefineCalls()
	 * @throws Exception
	 *             i.e. for circular definition
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
	 *             in case of malformed XML
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
	 * Replaces oldGeo by newGeo in consXML.
	 * 
	 * @param consXML
	 *            string builder
	 * @param oldGeo
	 *            old element
	 * @param newGeo
	 *            replacement
	 */
	protected void doReplaceInXML(StringBuilder consXML, GeoElement oldGeo,
			GeoElement newGeo) {
		String oldXML, newXML; // a = old string, b = new string

		AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
		AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();

		// change kernel settings temporarily

		// change kernel settings temporarily
		// set label to get replaceable XML
		if (newGeo.isLabelSet()) { // newGeo already exists in construction
			// oldGeo is replaced by newGeo, so oldGeo get's newGeo's label
			oldGeo.setLabelSimple(newGeo.getLabelSimple());

			oldXML = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo
					.getXML();
			newXML = ""; // remove oldGeo from construction
		} else {
			// newGeo doesn't exist in construction, so we take oldGeo's label
			newGeo.setLabelSimple(oldGeo.getLabelSimple());
			newGeo.labelSet = true; // to get right XML output
			newGeo.setAllVisualProperties(oldGeo, false);

			// NEAR-TO-RELATION for dependent new geo:
			// copy oldGeo's values to newGeo so that the
			// near-to-relationship can do its job if possible
			if (newGeoAlgo != null && newGeoAlgo.isNearToAlgorithm()) {
				try {
					newGeo.set(oldGeo);
				} catch (Exception e) {
					// do nothing
				}
			}

			oldXML = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo
					.getXML();
			newXML = (newGeoAlgo == null) ? newGeo.getXML() : newGeoAlgo
					.getXML();

			// Application.debug("oldGeo: " + oldGeo + ", visible: " +
			// oldGeo.isEuclidianVisible() + ", algo: " + oldGeoAlgo);
			// Application.debug("newGeo: " + newGeo + ", visible: " +
			// newGeo.isEuclidianVisible() + ", algo: " + newGeoAlgo);
		}

		// restore old kernel settings

		// replace Strings: oldXML by newXML in consXML
		// Application.debug("cons=\n"+consXML+"\nold=\n"+oldXML+"\nnew=\n"+newXML);
		int pos = consXML.indexOf(oldXML);
		if (pos < 0) {
			restoreCurrentUndoInfo();
			App
					.debug("replace failed: oldXML string not found:\n"
							+ oldXML);
			// Application.debug("consXML=\n" + consXML);
			throw new MyError(getApplication(), "ReplaceFailed");
		}

		// System.out.println("REDEFINE: oldGeo: " + oldGeo + ", newGeo: " +
		// newGeo);
		// System.out.println(" old XML:\n" + consXML.substring(pos, pos +
		// oldXML.length()));
		// System.out.println(" new XML:\n" + newXML);
		// System.out.println("END redefine.");

		// replace oldXML by newXML in consXML
		consXML.replace(pos, pos + oldXML.length(), newXML);

	}

	/**
	 * Sets construction step position. Objects 0 to step in the construction
	 * list will be visible in the views, objects step+1 to the end will be
	 * hidden.
	 * 
	 * @param s
	 *            : step number from range -1 ... steps()-1 where -1 shows an
	 *            empty construction.
	 */
	public void setStep(int s) {
		// Application.debug("setStep"+step+" "+s);

		if (s == step || s < -1 || s >= ceList.size())
			return;

		kernel.setAllowVisibilitySideEffects(false);

		boolean cpara = kernel
				.isNotifyConstructionProtocolViewAboutAddRemoveActive();
		kernel.setNotifyConstructionProtocolViewAboutAddRemoveActive(false);

		if (s < step) {
			//we must go from high to low there as otherwise the CAS cells would 
			//rearrange their numbers meanwhile
			for (int i = step; i >= s+1; i--) {
				ceList.get(i).notifyRemove();
			}
		} else {
			for (int i = step + 1; i <= s; ++i) {
				// Application.debug(i+"");
				ceList.get(i).notifyAdd();
			}
		}

		kernel.setNotifyConstructionProtocolViewAboutAddRemoveActive(cpara);

		step = s;

		kernel.setAllowVisibilitySideEffects(true);

		// Michael Borcherds 2008-05-15
		updateAllConstructionProtocolAlgorithms();
	}

	/**
	 * Returns current construction step position.
	 * 
	 * @return current construction step position.
	 */
	public int getStep() {
		return step;
	}

	/*
	 * GeoElementTable Management
	 */
	/**
	 * Adds given GeoElement to a table where (label, object) pairs are stored.
	 * 
	 * @param geo
	 *            GeoElement to be added, must be labeled
	 * @see #removeLabel(GeoElement)
	 * @see #lookupLabel(String)
	 */
	public void putLabel(GeoElement geo) {
		if (supressLabelCreation || geo.getLabelSimple() == null)
			return;

		geoTable.put(geo.getLabelSimple(), geo);
		addToGeoSets(geo);
	}

	/**
	 * Removes given GeoElement from a table where (label, object) pairs are
	 * stored.
	 * 
	 * @param geo
	 *            GeoElement to be removed
	 * @see #putLabel(GeoElement)
	 */
	public void removeLabel(GeoElement geo) {
		geo.unbindVariableInCAS();
		geoTable.remove(geo.getLabelSimple());
		removeFromGeoSets(geo);
	}

	private void addToGeoSets(GeoElement geo) {
		geoSetConsOrder.add(geo);
		geoSetWithCasCells.add(geo);
		geoSetLabelOrder.add(geo);

		// get ordered type set
		GeoClass type = geo.getGeoClassType();
		TreeSet<GeoElement> typeSet = geoSetsTypeMap.get(type);
		if (typeSet == null) {
			typeSet = createTypeSet(type);
		}
		typeSet.add(geo);

		/*
		 * Application.debug("*** geoSet order (add " + geo + ") ***"); Iterator
		 * it = geoSet.iterator();
		 * 
		 * while (it.hasNext()) { GeoElement g = (GeoElement) it.next();
		 * Application.debug(g.getConstructionIndex() + ": " + g); }
		 */
	}

	/**
	 * 
	 * TODO: private again
	 * 
	 */
	public class LabelComparator implements Comparator<GeoElement> {
		public int compare(GeoElement ob1, GeoElement ob2) {
			GeoElement geo1 = ob1;
			GeoElement geo2 = ob2;

			return GeoElement.compareLabels(geo1.getLabelSimple(),
					geo2.getLabelSimple());
		}
	}

	/**
	 * Returns a set with all labeled GeoElement objects of a specific type in
	 * alphabetical order of their labels.
	 * 
	 * @param geoClassType
	 *            use {@link GeoClass} constants
	 * @return Set of elements of given type.
	 */
	final public TreeSet<GeoElement> getGeoSetLabelOrder(GeoClass geoClassType) {
		TreeSet<GeoElement> typeSet = geoSetsTypeMap.get(geoClassType);
		if (typeSet == null) {
			typeSet = createTypeSet(geoClassType);
		}
		return typeSet;
	}

	private TreeSet<GeoElement> createTypeSet(GeoClass type) {
		TreeSet<GeoElement> typeSet = new TreeSet<GeoElement>(
				new LabelComparator());
		geoSetsTypeMap.put(type, typeSet);
		return typeSet;
	}

	private void removeFromGeoSets(GeoElement geo) {
		geoSetConsOrder.remove(geo);
		geoSetWithCasCells.remove(geo);
		geoSetLabelOrder.remove(geo);

		// set ordered type set
		GeoClass type = geo.getGeoClassType();
		TreeSet<GeoElement> typeSet = geoSetsTypeMap.get(type);
		if (typeSet != null)
			typeSet.remove(geo);

		/*
		 * Application.debug("*** geoSet order (remove " + geo + ") ***");
		 * Iterator it = geoSet.iterator(); int i = 0; while (it.hasNext()) {
		 * GeoElement g = (GeoElement) it.next();
		 * Application.debug(g.getConstructionIndex() + ": " + g); }
		 */
	}

	/**
	 * Adds given GeoCasCell to a table where (label, object) pairs of CAS view
	 * variables are stored.
	 * 
	 * @param geoCasCell
	 *            GeoElement to be added, must have assignment variable
	 * @param label
	 *            label for CAS cell
	 * @see #removeCasCellLabel(String)
	 * @see #lookupCasCellLabel(String)
	 */
	public void putCasCellLabel(GeoCasCell geoCasCell, String label) {
		if (label == null)
			return;

		if (geoCasCellTable == null)
			geoCasCellTable = new HashMap<String, GeoCasCell>();
		geoCasCellTable.put(label, geoCasCell);
	}

	/**
	 * Removes given GeoCasCell from the CAS variable table and from the
	 * underlying CAS.
	 * 
	 * @param variable
	 *            to be removed
	 * @see #putCasCellLabel(GeoCasCell, String)
	 */
	public void removeCasCellLabel(String variable) {
		removeCasCellLabel(variable, true);
	}

	/**
	 * Removes given GeoCasCell from the CAS variable table and if wanted from
	 * the underlying CAS too.
	 * 
	 * @param variable
	 *            variable name
	 * 
	 * @param unbindInCAS
	 *            whether variable should be removed from underlying CAS too.
	 * @see #putCasCellLabel(GeoCasCell, String)
	 */
	public void removeCasCellLabel(String variable, boolean unbindInCAS) {
		if (geoCasCellTable != null) {
			GeoCasCell geoCasCell = geoCasCellTable.remove(variable);
			if (unbindInCAS && geoCasCell!=null)
				geoCasCell.unbindVariableInCAS();
		}
	}

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * 
	 * @param label
	 *            label to be looked for
	 * @return may return null
	 */
	public GeoElement lookupLabel(String label) {
		return lookupLabel(label, false);
	}

	/**
	 * Returns a GeoCasCell for the given label. Note: only objects with
	 * construction index 0 to step are available.
	 * 
	 * @param label
	 *            to be looked for
	 * @return may return null
	 */
	public GeoCasCell lookupCasCellLabel(String label) {
		GeoCasCell geoCasCell = null;

		// global var handling
		if (geoCasCellTable != null) {
			geoCasCell = geoCasCellTable.get(label);
		}

		// TODO add lookupCasCellLabel support for construction steps
		// // STANDARD CASE: variable name found
		// if (geoCasCell != null) {
		// return (GeoCasCell) checkConstructionStep(geoCasCell);
		// }

		return geoCasCell;
	}

	/**
	 * Returns GeoCasCell referenced by given row label.
	 * 
	 * @param label
	 *            row reference label, e.g. $5 for 5th row or $ for previous row
	 * @return referenced row or null
	 * @throws CASException
	 *             thrown if one or more row references are invalid (like $x or
	 *             if the number is higher than the number of rows)
	 */
	public GeoCasCell lookupCasRowReference(String label) throws CASException {
		if (!label.startsWith(ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX)) {
			return null;
		}

		// $5 for 5th row
		int rowRef = -1;
		try {
			rowRef = Integer.parseInt(label.substring(1));
		} catch (NumberFormatException e) {
			System.err.println("Malformed CAS row reference: " + label);
			CASException ex = new CASException("CAS.InvalidReferenceError");
			ex.setKey("CAS.InvalidReferenceError");
			throw ex;
		}

		// we start to count at 0 internally but at 1 in the user interface
		GeoCasCell ret = getCasCell(rowRef - 1);
		if (ret == null) {
			System.err.println("invalid CAS row reference: " + label);
			CASException ex = new CASException("CAS.InvalidReferenceError");
			ex.setKey("CAS.InvalidReferenceError");
			throw ex;
		}
		return ret;
	}

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * 
	 * @param label
	 *            to be looked for
	 * @param allowAutoCreate
	 *            : true = allow automatic creation of missing labels (e.g. for
	 *            spreadsheet)
	 * @return may return null
	 */
	public GeoElement lookupLabel(String label, boolean allowAutoCreate) {// package
																			// private
		String label1 = label;
		if (label1 == null) {
			return null;
		}

		// local var handling
		if (localVariableTable != null) {
			GeoElement localGeo = localVariableTable.get(label1);
			if (localGeo != null) {
				return localGeo;
			}
		}

		// global var handling
		GeoElement geo = geoTableVarLookup(label1);

		// STANDARD CASE: variable name found
		if (geo != null) {
			return checkConstructionStep(geo);
		}

		// DESPARATE CASE: variable name not found

		/*
		 * CAS VARIABLE HANDLING e.g. ggbtmpvara for a
		 */
		label1 = kernel.removeCASVariablePrefix(label1);
		geo = geoTableVarLookup(label1);
		if (geo != null) {
			// geo found for name that starts with TMP_VARIABLE_PREFIX or
			// GGBCAS_VARIABLE_PREFIX
			return checkConstructionStep(geo);
		}

		/*
		 * SPREADSHEET $ HANDLING In the spreadsheet we may have variable names
		 * like "A$1" for the "A1" to deal with absolute references. Let's
		 * remove all "$" signs from label and try again.
		 */
		if (label1.indexOf('$') > -1) {
			StringBuilder labelWithout$ = new StringBuilder(label1.length() - 1);
			for (int i = 0; i < label1.length(); i++) {
				char ch = label1.charAt(i);
				if (ch != '$') {
					labelWithout$.append(ch);
				}
			}
			// allow automatic creation of elements
			geo = lookupLabel(labelWithout$.toString(), allowAutoCreate);
			if (geo != null) {
				// geo found for name that includes $ signs
				return checkConstructionStep(geo);
			}
			int cell = 0;
			try{
				cell = Integer.parseInt(labelWithout$.toString());
				}
			catch(Exception e){
				e.printStackTrace();
			}
			if(cell>0){
				return this.getCasCell(cell-1);
			}
		}

		// try upper case version for spreadsheet label like a1
		if (allowAutoCreate) {
			if (StringUtil.isLetter(label1.charAt(0)) // starts with letter
					&& StringUtil.isDigit(label1.charAt(label1.length() - 1))) // ends
																				// with
																				// digit
			{
				String upperCaseLabel = label1.toUpperCase();
				geo = geoTableVarLookup(upperCaseLabel);
				if (geo != null) {
					return checkConstructionStep(geo);
				}
			}
		}

		// if we get here, nothing worked:
		// possibly auto-create new GeoElement with that name
		if (allowAutoCreate) {
			return autoCreateGeoElement(label1);
		}
		return null;
	}

	/**
	 * Returns geo if it is available at the current construction step,
	 * otherwise returns null.
	 */
	private GeoElement checkConstructionStep(GeoElement geo) {
		// check if geo is available for current step
		if (geo.isAvailableAtConstructionStep(step)) {
			return geo;
		}
		return null;
	}

	/**
	 * Returns true if label is not occupied by any GeoElement including
	 * GeoCasCells.
	 * 
	 * @param label
	 *            label to be checked
	 * @return true iff label is not occupied by any GeoElement.
	 */
	public boolean isFreeLabel(String label) {
		return isFreeLabel(label, true, false);
	}

	/**
	 * Returns true if label is not occupied by any GeoElement.
	 * 
	 * @param label
	 *            label to be checked
	 * @param includeCASvariables
	 *            whether GeoCasCell labels should be checked too
	 * @return true iff label is not occupied by any GeoElement.
	 */
	public boolean isFreeLabel(String label, boolean includeCASvariables, boolean includeDummies) {
		if (label == null) {
			return false;
		}
		if(!fileLoading && getKernel().getApplication().
				getParserFunctions().isReserved(label))
			return false;
		// check standard geoTable
		if (geoTable.containsKey(label))
			return false;

		// optional: also check CAS variable table
		if (includeCASvariables && geoCasCellTable != null
				&& geoCasCellTable.containsKey(label)) {
			return false;
		}
		
		if (includeDummies && casDummies.contains(label)) {
			return false;
		}

		return true;
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
		for (GeoElement pred : predSet) {
			moveInConstructionList(pred, pred.getMinConstructionIndex());
		}

		// move newGeo to the left as well (important if newGeo already existed
		// in construction)
		moveInConstructionList(newGeo, newGeo.getMinConstructionIndex());

		// move oldGeo to its maximum construction index
		moveInConstructionList(oldGeo, oldGeo.getMaxConstructionIndex());
	}

	/**
	 * Makes sure that geoCasCell comes after all its predecessors in the
	 * construction list.
	 * 
	 * @param casCell
	 *            CAS cell
	 * 
	 * @return whether construction list order was changed
	 */
	protected boolean updateConstructionOrder(GeoCasCell casCell) {
		// collect all predecessors of casCell
		TreeSet<GeoElement> allPred = new TreeSet<GeoElement>();
		if(casCell.getGeoElementVariables()!=null){
		for (GeoElement directInput : casCell.getGeoElementVariables()) {
			allPred.addAll(directInput.getAllPredecessors());
			allPred.add(directInput);
		}
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
		for (GeoElement pred : allPred) {
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
			moveInConstructionList(casCell, maxPredIndex + 1);
			return true;
		}
		System.err
				.println("Construction.updateConstructionOrder(GeoCasCell) failed: "
						+ casCell);
		return false;
	}

	// 1) remove all brothers and sisters of oldGeo
	// 2) move all predecessors of newGeo to the left of oldGeo in construction
	// list
	/**
	 * @param oldGeo
	 *            old element
	 * @param newGeo
	 *            replacement
	 */
	protected void prepareReplace(GeoElement oldGeo, GeoElement newGeo) {
		AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
		AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();

		// 1) remove all brothers and sisters of oldGeo
		if (oldGeoAlgo != null) {
			keepGeo = oldGeo;
			oldGeoAlgo.removeOutputExcept(oldGeo);
			keepGeo = null;
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
			for (int i = 0; i < newGeoAlgo.getOutputLength(); i++) {
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
	 * Adds the given GeoCasCell to a set with all labeled GeoElements and CAS
	 * cells needed for notifyAll().
	 * 
	 * @param geoCasCell
	 *            CAS cell to be added
	 */
	public void addToGeoSetWithCasCells(GeoCasCell geoCasCell) {
		geoSetWithCasCells.add(geoCasCell);
	}

	/**
	 * Removes the given GeoCasCell from a set with all labeled GeoElements and
	 * CAS cells needed for notifyAll().
	 * 
	 * @param geoCasCell
	 *            CAS cell to be removed
	 */
	public void removeFromGeoSetWithCasCells(GeoCasCell geoCasCell) {
		geoSetWithCasCells.remove(geoCasCell);
	}

	/**
	 * Creates a new GeoElement for the spreadsheet of same type as
	 * neighbourCell.
	 * 
	 * @return new GeoElement of desired type
	 * @param neighbourCell
	 *            another geo of the desired type
	 * @param label
	 *            Label for the new geo
	 */
	final public GeoElement createSpreadsheetGeoElement(
			GeoElement neighbourCell, String label) {
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
	 * Returns the next free indexed label using the given prefix starting with
	 * the given index number.
	 * 
	 * @param prefix
	 *            e.g. "c"
	 * @param startIndex
	 *            e.g. 2
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
		// int n = 1; // start index
		// if (startIndex != null) {
		// try {
		// n = Integer.parseInt(startIndex);
		// } catch (NumberFormatException e) {
		// n = 1;
		// }
		// }

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

	/**
	 * Returns the next free indexed label using the given prefix.
	 * 
	 * @param prefix
	 *            e.g. "c"
	 * @return indexed label, e.g. "c_2"
	 */
	public String getIndexLabel(String prefix) {
		return getIndexLabel(prefix, 1);
	}

	/**
	 * Automatically creates a GeoElement object for a certain label that is not
	 * yet used in the geoTable of this construction. This is done for e.g.
	 * point i = (0,1), number e = Math.E, empty spreadsheet cells
	 * 
	 * @param labelNew
	 *            label for new element
	 * @return created element
	 */
	protected GeoElement autoCreateGeoElement(String labelNew) {
		GeoElement createdGeo = null;
		boolean fix = true;
		boolean auxilliary = true;
		String label = labelNew;
		// expression like AB, autocreate AB=Distance[A,B] or AB = A * B
		// according to whether A,B are points or numbers
		if (label.length() == 2) {
			GeoElement geo1 = kernel.lookupLabel(label.charAt(0) + "");
			if (geo1 != null && geo1.isGeoPoint()) {
				GeoElement geo2 = kernel.lookupLabel(label.charAt(1) + "");
				if (geo2 != null && geo2.isGeoPoint()) {
					AlgoDistancePoints dist = new AlgoDistancePoints(this,
							null, (GeoPointND) geo1, (GeoPointND) geo2);
					createdGeo = dist.getDistance();
					fix = false;
				}
			} else if (geo1 != null && geo1.isNumberValue()) {
				GeoElement geo2 = kernel.lookupLabel(label.charAt(1) + "");
				if (geo2 != null && geo2.isNumberValue()) {
					ExpressionNode node = new ExpressionNode(kernel,
							((NumberValue) geo1).evaluateNum(),
							Operation.MULTIPLY, ((NumberValue) geo2).evaluateNum());
					AlgoDependentNumber algo = new AlgoDependentNumber(this,
							null, node, false);
					createdGeo = algo.getNumber();
					fix = false;
				}
			}

		} else if (label.length() == 1) {
			if (label.equals("O")) {
				
				createdGeo = new GeoPoint(this, 0d, 0d, 1d);
				label = "O";
				auxilliary = true;
				fix = true;
			} 
		} else if (label.length() == 3) {
			if (label.equals("lnx")) {
				
				createdGeo = createFunction(Operation.LOG);
				label = null;
				auxilliary = false;
				fix = false;
			} 
		} else if (label.length() == 4) {
			if (label.equals("sinx")) {
				createdGeo = createFunction(Operation.SIN);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("cosx")) {
				createdGeo = createFunction(Operation.COS);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("tanx")) {
				createdGeo = createFunction(Operation.TAN);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("secx")) {
				createdGeo = createFunction(Operation.SEC);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("cscx")) {
				createdGeo = createFunction(Operation.CSC);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("cotx")) {
				createdGeo = createFunction(Operation.COT);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("logx")) {
				createdGeo = createFunction(Operation.LOG);
				label = null;
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 5) {
			if (label.equals("sinhx")) {
				createdGeo = createFunction(Operation.SINH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("coshx")) {
				createdGeo = createFunction(Operation.COSH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("tanhx")) {
				createdGeo = createFunction(Operation.TANH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("sechx")) {
				createdGeo = createFunction(Operation.SECH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("cothx")) {
				createdGeo = createFunction(Operation.COTH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("acosx")) {
				createdGeo = createFunction(Operation.ARCCOS);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("asinx")) {
				createdGeo = createFunction(Operation.ARCSIN);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("atanx")) {
				createdGeo = createFunction(Operation.ARCTAN);
				label = null;
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 6) {
			if (label.equals("cosecx")) {
				createdGeo = createFunction(Operation.CSC);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcosx")) {
				createdGeo = createFunction(Operation.ARCCOS);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("asinhx")) {
				createdGeo = createFunction(Operation.ASINH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("acoshx")) {
				createdGeo = createFunction(Operation.ACOSH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("atanhx")) {
				createdGeo = createFunction(Operation.ATANH);
				label = null;
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 7) {
			if (label.equals("arccosx")) {
				createdGeo = createFunction(Operation.ARCCOS);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcsinx")) {
				createdGeo = createFunction(Operation.ARCSIN);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("arctanx")) {
				createdGeo = createFunction(Operation.ARCTAN);
				label = null;
				auxilliary = false;
				fix = false;
			} 
		} else if (label.length() == 8) {
			if (label.equals("arccoshx")) {
				createdGeo = createFunction(Operation.ACOSH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcsinhx")) {
				createdGeo = createFunction(Operation.ASINH);
				label = null;
				auxilliary = false;
				fix = false;
			} else if (label.equals("arctanhx")) {
				createdGeo = createFunction(Operation.ATANH);
				label = null;
				auxilliary = false;
				fix = false;
			} 
		}
		// handle i or e case
		if (createdGeo != null) {

			// removed: not needed for e,i and causes bug with using Circle[D,
			// CD 2] in locus
			// boolean oldSuppressLabelsActive = isSuppressLabelsActive();
			// setSuppressLabelCreation(false);

			createdGeo.setAuxiliaryObject(auxilliary);
			if (label != null) {
				createdGeo.setLabel(label);
			}
			createdGeo.setFixed(fix);

			// revert to previous label creation state
			// setSuppressLabelCreation(oldSuppressLabelsActive);
			return createdGeo;
		}

		// check spreadsheet cells
		// for missing spreadsheet cells, create object
		// of same type as above
		createdGeo = GeoElementSpreadsheet.autoCreate(label, this);

		return createdGeo;
	}

	private GeoFunction createFunction(Operation op) {
		FunctionVariable x = new FunctionVariable(kernel);
		ExpressionNode en = new ExpressionNode(kernel, x, op, null);
		Function fun = new Function(en, x);
		return new GeoFunction(this, fun);
	}

	/**
	 * Make geoTable contain only xAxis and yAxis
	 */
	protected void initGeoTables() {
		geoTable.clear();
		geoCasCellTable = null;
		localVariableTable = null;
		constsM.clear();
		complexNumbersM.clear();
		intsM.clear();
		// add axes labels both in English and current language
		geoTable.put("xAxis", xAxis);
		geoTable.put("yAxis", yAxis);
		if (xAxisLocalName != null) {
			geoTable.put(xAxisLocalName, xAxis);
			geoTable.put(yAxisLocalName, yAxis);
		}
	}

	/**
	 * Returns a set with all labeled GeoElement objects sorted in alphabetical
	 * order of their type strings and labels (e.g. Line g, Line h, Point A,
	 * Point B, ...). Note: the returned TreeSet is a copy of the current
	 * situation and is not updated by the construction later on.
	 * 
	 * @return Set of all labeld GeoElements orted by name and description
	 */
	final public TreeSet<GeoElement> getGeoSetNameDescriptionOrder() {
		// sorted set of geos
		TreeSet<GeoElement> sortedSet = new TreeSet<GeoElement>(
				new NameDescriptionComparator());

		// get all GeoElements from construction and sort them
		Iterator<GeoElement> it = geoSetConsOrder.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// sorted inserting using name description of geo
			sortedSet.add(geo);
		}
		return sortedSet;
	}

	/**
	 * Returns extremum finder
	 * 
	 * @return extremum finder
	 */
	public ExtremumFinder getExtremumFinder() {
		return kernel.getExtremumFinder();
	}

	/*
	 * redo / undo
	 */

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
	 * Add a macro to list of used macros
	 * 
	 * @param macro
	 *            Macro to be added
	 */
	public final void addUsedMacro(Macro macro) {
		if (usedMacros == null)
			usedMacros = new ArrayList<Macro>();
		usedMacros.add(macro);
	}

	/**
	 * Returns list of macros used in this construction
	 * 
	 * @return list of macros used in this construction
	 */
	public ArrayList<Macro> getUsedMacros() {
		return usedMacros;
	}

	/**
	 * Calls remove() for every ConstructionElement in the construction list.
	 * After this the construction list will be empty.
	 */
	public void clearConstruction() {
		kernel.resetGeoGebraCAS();
		constsM.clear();
		complexNumbersM.clear();
		intsM.clear();
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
	 * Returns undo xml string of this construction.
	 * 
	 * @return StringBuilder with xml of this construction.
	 */
	public StringBuilder getCurrentUndoXML() {
		return xmlio.getUndoXML(this);
	}

	private MyXMLio xmlio;
	private GeoElement outputGeo;

	/**
	 * @param xmlio
	 *            XMLio object
	 */
	public void setXMLio(MyXMLio xmlio) {
		this.xmlio = xmlio;
	}

	/**
	 * Clears the undo info list of this construction and adds the current
	 * construction state to the undo info list.
	 */
	public void initUndoInfo() {
		if (undoManager == null)
			undoManager = kernel.getApplication().getUndoManager(this);
		undoManager.initUndoInfo();
	}

	/**
	 * Tries to build the new construction from the given XML string.
	 */
	private void buildConstruction(StringBuilder consXML) throws Exception {
		// try to process the new construction
		try {
			if (undoManager == null)
				undoManager = kernel.getApplication().getUndoManager(this);
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
	 * Returns the UndoManager (for Copy & Paste)
	 * 
	 * @return UndoManager
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * used by commands Element[] and Cell[] as they need to know their output
	 * type in advance
	 * 
	 * @param type
	 *            type generated by getXMLTypeString()
	 */
	public void setOutputGeo(String type) {
		if (type == null) {
			this.outputGeo = null;
			return;
		}
		this.outputGeo = kernel.createGeoElement(this, type);
	}

	/**
	 * used by commands Element[] and Cell[] as they need to know their output
	 * type in advance default: return new GeoNumeric(this)
	 * 
	 * @return output of command currently parsed from XML
	 */
	public GeoElement getOutputGeo() {
		return outputGeo == null ? new GeoNumeric(this) : outputGeo;
	}
	private TreeSet<String> registredFV = new TreeSet<String>();
	/**
	 * Registers function variable that should be recognized in If and Function commands
	 * @param fv local function variable
	 */
	public void registerFunctionVariable(String fv) {
		if(fv == null)
			registredFV.clear();
		else
			registredFV.add(fv);
		
	}
	/**
	 * 
	 * @param s variable name
	 * @return whether s is among registred function variables
	 */
	public boolean isRegistredFunctionVariable(String s){
		return registredFV.contains(s);
	}
	/**
	 * Returns function variable that should be recognized in If and Function commands
	 * @return local function variable or null if there is none
	 */
	public String getRegistredFunctionVariable(){
		Iterator<String> it = registredFV.iterator();
		if(it.hasNext())
				return it.next();
		return null;
	}
	private boolean fileLoading;
	private boolean updateConstructionRunning;
	
	/**
	 * Let construction know about file being loaded.
	 * When this is true, user defined objects called sin, cos, ...
	 * are accepted 
	 * @param b true if file is loading
	 */
	public void setFileLoading(boolean b) {
		fileLoading = b;	
	}

	/**
	 * @return whether we are just loading a file
	 */
	public boolean isFileLoading(){
		return fileLoading;
	}
	// update all indices >= pos

	public boolean isUpdateConstructionRunning() {
		return updateConstructionRunning;
	}
	private TreeSet<String> casDummies = new TreeSet<String>();
	public TreeSet<String> getCASdummies() {
		return casDummies;
	}

	/**
	 * TODO place this JavaDoc to the correct spot Build a set with all
	 * algorithms of this construction (in topological order). The method
	 * updateAll() of this set can be used to update the whole construction.
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

}
