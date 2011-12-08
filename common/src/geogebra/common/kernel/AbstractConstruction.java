package geogebra.common.kernel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.util.StringUtil;

public abstract class AbstractConstruction {

	/**
	 * Added for Intergeo File Format (Yves Kreis) --> writes the <elements> and
	 * the <constraints> part
	 */
	public static final int CONSTRUCTION = 0;
	/**
	 * Added for Intergeo File Format (Yves Kreis) writes the <display> part
	 * with the <display> tag
	 */
	public static final int DISPLAY = 1;

	// TODO: make private once we port ClearConstruction
	protected String title, author, date;
	// text for dynamic worksheets: 0 .. above, 1 .. below
	protected String[] worksheetText = new String[2];

	// showOnlyBreakpoints in construction protocol
	private boolean showOnlyBreakpoints;

	// construction belongs to kernel
	protected AbstractKernel kernel;

	// current construction step (-1 ... ceList.size() - 1)
	// step == -1 shows empty construction
	// TODO: make private again
	protected int step;

	// in macro mode no new labels or construction elements
	// can be added
	// TODO: make private again
	protected boolean supressLabelCreation = false;

	// a map for sets with all labeled GeoElements in alphabetical order of
	// specific types
	// (points, lines, etc.)
	// TODO private
	protected HashMap<GeoClass, TreeSet<GeoElement>> geoSetsTypeMap;

	// ConstructionElement List (for objects of type ConstructionElement)
	protected ArrayList<ConstructionElement> ceList;

	// AlgoElement List (for objects of type AlgoElement)
	protected ArrayList<AlgoElement> algoList; // used in updateConstruction()

	/** Table for (label, GeoElement) pairs, contains global variables */
	protected HashMap<String, GeoElement> geoTable;

	// list of algorithms that need to be updated when EuclidianView changes
	// TODO: make private
	protected ArrayList<EuclidianViewCE> euclidianViewCE;

	/** Table for (label, GeoElement) pairs, contains local variables */
	protected HashMap<String, GeoElement> localVariableTable;

	// set with all labeled GeoElements in ceList order
	protected TreeSet<GeoElement> geoSetConsOrder; 

	// set with all labeled GeoElements in alphabetical order
	protected TreeSet<GeoElement> geoSetLabelOrder;
	protected TreeSet<GeoElement> geoSetWithCasCells;

	// list of random numbers or lists
	private TreeSet<GeoElementInterface> randomElements;
	
	// collect replace() requests to improve performance
		// when many cells in the spreadsheet are redefined at once
	//TODO: private again
		protected boolean collectRedefineCalls = false;
		protected HashMap<GeoElement,GeoElement> redefineMap;

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
	public AbstractKernel getKernel() {
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
	public AbstractApplication getApplication() {
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
	protected GeoElement geoTableVarLookup(String label) {
		GeoElement ret = (GeoElement) geoTable.get(label);
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
			((ConstructionElement) ceList.get(i)).setConstructionIndex(i);
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
			AlgoElement algo = (AlgoElement) algoList.get(i);
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
			AbstractApplication app = kernel.getApplication();
			if (app.isUsingFullGui())
				app.updateConstructionProtocol();
		}

		return updateAlgos != null;
	}

	/**
	 * Adds the given Construction Element to this Construction at position
	 * index
	 * 
	 * @param ce
	 * @param index
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

	protected abstract void updateCasCellRows();

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
		if (ce instanceof GeoCasCell || ce.isAlgoDependentCasCell())
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
	public void addToAlgorithmList(AlgoElementInterface algo) {
		algoList.add((AlgoElement) algo);
	}

	/**
	 * Removes the given algorithm from this construction's algorithm list
	 * 
	 * @param algo
	 *            algo to be removed
	 */
	public void removeFromAlgorithmList(AlgoElementInterface algo) {
		algoList.remove((AlgoElement) algo);
	}

	/**
	 * Moves geo to given position toIndex in this construction. Note: if ce (or
	 * its parent algorithm) is not in the construction list nothing is done.
	 * 
	 * @return whether construction list was changed or not.
	 */
	public boolean moveInConstructionList(GeoElement geo, int toIndex) {
		AlgoElement algoParent = geo.getParentAlgorithm();
		int fromIndex = (algoParent == null) ? ceList.indexOf(geo) : ceList
				.indexOf(algoParent);
		if (fromIndex >= 0)
			return moveInConstructionList(fromIndex, toIndex);
		else
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
		if (geo.isIndependent())
			return geo.isInConstructionList();
		else
			return geo.getParentAlgorithm().isInConstructionList();
	}

	/**
	 * Updates all algorithms in this construction
	 */
	public final void updateAllAlgorithms() {
		// update all algorithms

		// *** algoList.size() can change during the loop
		for (int i = 0; i < algoList.size(); ++i) {
			AlgoElement algo = (AlgoElement) algoList.get(i);
			algo.update();
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

		Iterator<GeoElementInterface> it = randomElements.iterator();
		while (it.hasNext()) {
			GeoElement num = (GeoElement) it.next();
			num.updateRandomGeo();
		}
	}

	/**
	 * Adds a number to the set of random numbers of this construction.
	 * 
	 * @param num
	 *            Element to be added
	 */
	public void addRandomGeo(GeoElementInterface num) {
		if (randomElements == null)
			randomElements = new TreeSet<GeoElementInterface>();
		randomElements.add(num);
		num.setRandomGeo(true);
	}

	/**
	 * Removes a number from the set of random numbers of this construction.
	 * 
	 * @param num
	 *            Element to be removed
	 */
	public void removeRandomGeo(GeoElementInterface num) {
		if (randomElements != null)
			randomElements.remove(num);
		num.setRandomGeo(false);
	}

	/**
	 * Updates all objects in this construction.
	 */
	final public void updateConstruction() {
		// G.Sturr 2010-5-28: turned this off so that random numbers can be
		// traced
		// if (!kernel.isMacroKernel() && kernel.app.hasGuiManager())
		// kernel.app.getGuiManager().startCollectingSpreadsheetTraces();

		// update all independent GeoElements
		int size = ceList.size();
		for (int i = 0; i < size; ++i) {
			ConstructionElement ce = (ConstructionElement) ceList.get(i);
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
			AlgoElement algo = (AlgoElement) algoList.get(i);
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

	/**
	 * Returns this construction in XML format. GeoGebra File Format.
	 * 
	 * @param sb
	 *            StringBuilder to which the XML is appended
	 */
	public void getConstructionXML(StringBuilder sb) {

		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		StringType oldPrintForm = kernel.getCASPrintForm();
		boolean oldValue = kernel.isPrintLocalizedCommandNames();
		kernel.setCoordStyle(AbstractKernel.COORD_STYLE_DEFAULT);
		kernel.setCASPrintForm(StringType.GEOGEBRA_XML);
		kernel.setPrintLocalizedCommandNames(false);

		try {
			// save construction elements
			sb.append("<construction title=\"");
			sb.append(StringUtil.encodeXML(getTitle()));
			sb.append("\" author=\"");
			sb.append(StringUtil.encodeXML(getAuthor()));
			sb.append("\" date=\"");
			sb.append(StringUtil.encodeXML(getDate()));
			sb.append("\">\n");

			// worksheet text
			if (worksheetTextDefined()) {
				sb.append("\t<worksheetText above=\"");
				sb.append(StringUtil.encodeXML(getWorksheetText(0)));
				sb.append("\" below=\"");
				sb.append(StringUtil.encodeXML(getWorksheetText(1)));
				sb.append("\"/>\n");
			}

			ConstructionElement ce;
			int size = ceList.size();
			for (int i = 0; i < size; ++i) {
				ce = ceList.get(i);
				ce.getXML(sb);
			}

			sb.append("</construction>\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			kernel.setCoordStyle(oldCoordStlye);
			kernel.setCASPrintForm(oldPrintForm);
			kernel.setPrintLocalizedCommandNames(oldValue);
		}

	}

	/**
	 * Returns this construction in regression file .out format. Markus
	 * suggested to use the logic from getConstructionXML for this method. --
	 * Zoltan, 2011-07-26
	 * 
	 * @param sb
	 */
	public void getConstructionRegressionOut(StringBuilder sb) {

		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		StringType oldPrintForm = kernel.getCASPrintForm();
		boolean oldValue = kernel.isPrintLocalizedCommandNames();
		kernel.setCoordStyle(AbstractKernel.COORD_STYLE_DEFAULT);
		// kernel.setCASPrintForm(StringType.GEOGEBRA_XML);
		kernel.setCASPrintForm(StringType.GEOGEBRA);
		kernel.setPrintLocalizedCommandNames(false);
		kernel.setTemporaryPrintDecimals(6);
		kernel.setTemporaryPrintFigures(6);

		try {
			ConstructionElement ce;
			int size = ceList.size();
			for (int i = 0; i < size; ++i) {
				ce = ceList.get(i);
				sb.append(ce.getNameDescription() + " = ");

				if (ce instanceof GeoElement) {
					// sb.append(((GeoElement) ce).toValueString());
					((GeoElement) ce).getXMLtagsMinimal(sb);

				} else if (ce instanceof AlgoElement) {
					sb.append(((AlgoElement) ce).getCommandDescription());
					sb.append(" == ");
					sb.append(((AlgoElement) ce).getAlgebraDescriptionRegrOut());
				}
				sb.append("\n");
			}
		} catch (Exception e) {
			sb.append(e.getMessage());
		} finally {
			kernel.setCoordStyle(oldCoordStlye);
			kernel.setCASPrintForm(oldPrintForm);
			kernel.setPrintLocalizedCommandNames(oldValue);
		}

	}

	/**
	 * Returns this construction in I2G format. Intergeo File Format. (Yves
	 * Kreis)
	 * 
	 * @param sb
	 *            String builder to which the XML is appended
	 * @param mode
	 *            output mode, either CONSTRUCTION (0) or DISPLAY (1)
	 */
	public void getConstructionI2G(StringBuilder sb, int mode) {

		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		StringType oldPrintForm = kernel.getCASPrintForm();
		boolean oldValue = kernel.isPrintLocalizedCommandNames();
		kernel.setCoordStyle(AbstractKernel.COORD_STYLE_DEFAULT);
		kernel.setCASPrintForm(StringType.GEOGEBRA_XML);
		kernel.setPrintLocalizedCommandNames(false);

		try {
			ConstructionElement ce;
			int size = ceList.size();

			if (mode == CONSTRUCTION) {
				sb.append("\t<elements>\n");
				for (int i = 0; i < size; ++i) {
					ce = ceList.get(i);
					ce.getI2G(sb, ConstructionElement.ELEMENTS);
				}
				sb.append("\t</elements>\n");

				sb.append("\t<constraints>\n");
				for (int i = 0; i < size; ++i) {
					ce = ceList.get(i);
					ce.getI2G(sb, ConstructionElement.CONSTRAINTS);
				}
				sb.append("\t</constraints>\n");
			} else if (mode == DISPLAY) {
				for (int i = 0; i < size; ++i) {
					ce = ceList.get(i);
					ce.getI2G(sb, ConstructionElement.DISPLAY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);
		kernel.setPrintLocalizedCommandNames(oldValue);

	}

	private boolean undoEnabled = true;

	public boolean isUndoEnabled() {
		return undoEnabled;
	}

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
	 * @see #processCollectedRedefineCalls()
	 */
	public void startCollectingRedefineCalls() {
		collectRedefineCalls = true;
		if (redefineMap == null)
			redefineMap = new HashMap<GeoElement,GeoElement>();
		redefineMap.clear();
	}
	
	/**
	 * Stops collecting redefine calls.
	 * @see #processCollectedRedefineCalls()
	 */
	public void stopCollectingRedefineCalls() {
		collectRedefineCalls = false;
		if (redefineMap != null)
			redefineMap.clear();
	}
	/**
	 * Replaces oldGeo by newGeo in consXML.
	 */
	protected void doReplaceInXML(StringBuilder consXML, GeoElement oldGeo,
			GeoElement newGeo) {
		String oldXML, newXML; // a = old string, b = new string

		AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
		AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();

		// change kernel settings temporarily

		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		StringType oldPrintForm = kernel.getCASPrintForm();
		kernel.setCoordStyle(AbstractKernel.COORD_STYLE_DEFAULT);
		kernel.setCASPrintForm(StringType.GEOGEBRA_XML);

		// set label to get replaceable XML
		if (newGeo.isLabelSet()) { // newGeo already exists in construction
			// oldGeo is replaced by newGeo, so oldGeo get's newGeo's label
			oldGeo.label = newGeo.label;

			oldXML = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo
					.getXML();
			newXML = ""; // remove oldGeo from construction
		} else {
			// newGeo doesn't exist in construction, so we take oldGeo's label
			newGeo.label = oldGeo.label;
			newGeo.labelSet = true; // to get right XML output
			newGeo.setAllVisualProperties(oldGeo, false);

			// NEAR-TO-RELATION for dependent new geo:
			// copy oldGeo's values to newGeo so that the
			// near-to-relationship can do its job if possible
			if (newGeoAlgo != null && newGeoAlgo.isNearToAlgorithm()) {
				try {
					newGeo.set(oldGeo);
				} catch (Exception e) {
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
		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);

		// replace Strings: oldXML by newXML in consXML
		// Application.debug("cons=\n"+consXML+"\nold=\n"+oldXML+"\nnew=\n"+newXML);
		int pos = consXML.indexOf(oldXML);
		if (pos < 0) {
			restoreCurrentUndoInfo();
			AbstractApplication
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
			for (int i = s + 1; i <= step; ++i) {
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
	public void putLabel(GeoElementInterface geoI) {
		GeoElement geo = (GeoElement) geoI;
		if (supressLabelCreation || geo.label == null)
			return;

		geoTable.put(geo.label, geo);
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
	public void removeLabel(GeoElementInterface geoI) {
		GeoElement geo = (GeoElement) geoI;
		geo.unbindVariableInCAS();
		geoTable.remove(geo.label);
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

			return GeoElement.compareLabels(geo1.label, geo2.label,
					kernel.getGeoElementSpreadsheet());
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

	protected abstract void restoreCurrentUndoInfo();

	protected abstract boolean moveInConstructionList(int fromIndex, int toIndex);

	public abstract boolean isFreeLabel(String newLabel);

	public abstract String getIndexLabel(String prefix, int startIndex);

	public abstract String getIndexLabel(String prefix);

	public abstract void removeCasCellLabel(String variable);

	public abstract void removeCasCellLabel(String variable, boolean b);

	public abstract AbstractConstructionDefaults getConstructionDefaults();

	public abstract GeoElementInterface lookupLabel(String label,
			boolean allowAutoCreate);// package private

	public abstract void updateLocalAxesNames();

	public abstract void clearConstruction();

	public abstract void putCasCellLabel(GeoCasCell geoCasCell, String assignmentVar);

	public abstract void addToGeoSetWithCasCells(GeoCasCell geoCasCell);

	public abstract void removeFromGeoSetWithCasCells(GeoCasCell geoCasCell);
	
	public abstract GeoCasCell getCasCell(int row);
	
	public abstract GeoCasCell getLastCasCell();

}
