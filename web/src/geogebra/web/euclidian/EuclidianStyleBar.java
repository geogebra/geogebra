package geogebra.web.euclidian;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoTableText;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.util.MyToggleButton;
import geogebra.web.css.GuiResources;
import geogebra.web.euclidian.EuclidianController;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.main.Application;
import geogebra.web.util.ImageManager;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class EuclidianStyleBar extends HorizontalPanel
	implements geogebra.common.euclidian.EuclidianStyleBar, ValueChangeHandler {

	EuclidianController ec;
	protected EuclidianViewInterfaceCommon ev;
	protected AbstractApplication app;
	private Construction cons;

	private HashMap<Integer, Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;

	// flags and constants
	protected int iconHeight = 18;
	//private Dimension iconDimension = new Dimension(16, iconHeight);
	public int mode = -1;
	private boolean isIniting;
	private boolean needUndo = false;
	private Integer oldDefaultMode;
	private boolean modeChanged = true;

	// button-specific fields
	// TODO: create button classes so these become internal
	AlgoTableText tableText;
	Integer[] lineStyleArray;

	Integer[] pointStyleArray;
	HashMap<Integer, Integer> lineStyleMap;

	HashMap<Integer, Integer> pointStyleMap;
	final String[] bracketArray = { "\u00D8", "{ }", "( )", "[ ]", "| |",
			"|| ||" };
	private final String[] bracketArray2 = { "\u00D8", "{ }", "( )", "[ ]",
			"||", "||||" };


	private MyToggleButton btnCopyVisualStyle, btnPen, btnShowGrid,
	btnShowAxes;

	MyToggleButton btnBold;

	MyToggleButton btnItalic;

	private MyToggleButton btnDelete;

	private MyToggleButton btnLabel;

	private MyToggleButton btnPenEraser;

	MyToggleButton btnHideShowLabel;

	private MyToggleButton btnTableTextLinesV;

	private MyToggleButton btnTableTextLinesH;

	private MyToggleButton[] toggleBtnList;


	public EuclidianStyleBar(AbstractEuclidianView ev) {
		isIniting = true;

		this.ev = ev;
		ec = (EuclidianController)ev.getEuclidianController();
		app = ev.getApplication();
		cons = app.getKernel().getConstruction();

		// init handling of default geos
		createDefaultMap();
		defaultGeos = new ArrayList<GeoElement>();

		// toolbar display settings
		//setFloatable(false);
		//Dimension d = getPreferredSize();
		//d.height = iconHeight + 8;
		//setPreferredSize(d);

		// init button-specific fields
		// TODO: put these in button classes
		pointStyleArray = EuclidianView.getPointStyles();
		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < pointStyleArray.length; i++)
			pointStyleMap.put(pointStyleArray[i], i);

		lineStyleArray = EuclidianView.getLineTypes();
		lineStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < lineStyleArray.length; i++)
			lineStyleMap.put(lineStyleArray[i], i);

		initGUI();
		isIniting = false;

		setMode(ev.getMode()); // this will also update the stylebar
	}

	public int getMode() {
		return mode;
	}

	public void applyVisualStyle(ArrayList<GeoElement> selectedGeos) { }

	public void updateButtonPointCapture(int mode) { }

	public void setMode(int mode) {

		if (this.mode == mode) {
			modeChanged = false;
			return;
		}
		modeChanged = true;
		this.mode = mode;

		// MODE_TEXT temporarily switches to MODE_SELECTION_LISTENER
		// so we need to ignore this.
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			modeChanged = false;
			return;
		}

		updateStyleBar();

    }

	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(ev.getViewID());
	}

	public void restoreDefaultGeo() {
		if (oldDefaultGeo != null)
			oldDefaultGeo = cons.getConstructionDefaults().getDefaultGeo(
					oldDefaultMode);
	}

	public void setLabels() {
	    AbstractApplication.debug("implementation needed for GUI"); // TODO Auto-generated
	    
    }

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	public void updateStyleBar() {

		if (mode == EuclidianConstants.MODE_VISUAL_STYLE)
			return;

		// -----------------------------------------------------
		// Create activeGeoList, a list of geos the stylebar can adjust.
		// These are either the selected geos or the current default geo.
		// Each button uses this list to update its gui and set visibility
		// -----------------------------------------------------
		ArrayList<GeoElement> activeGeoList = new ArrayList<GeoElement>();

		// -----------------------------------------------------
		// MODE_MOVE case: load activeGeoList with all selected geos
		// -----------------------------------------------------
		if (mode == EuclidianConstants.MODE_MOVE) {

			boolean hasGeosInThisView = false;
			for (GeoElement geo : ((Application) ev.getApplication())
					.getSelectedGeos()) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible()) {
					hasGeosInThisView = true;
					break;
				}
			}
			for (GeoElement geo : ec.getJustCreatedGeos()) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible()) {
					hasGeosInThisView = true;
					break;
				}
			}
			if (hasGeosInThisView) {
				activeGeoList = ((Application) ev.getApplication())
						.getSelectedGeos();

				// we also update stylebars according to just created geos
				activeGeoList.addAll(ec.getJustCreatedGeos());
			}
		}

		// -----------------------------------------------------
		// All other modes: load activeGeoList with current default geo
		// -----------------------------------------------------
		else if (defaultGeoMap.containsKey(mode)) {

			// Save the current default geo state in oldDefaultGeo.
			// Stylebar buttons can temporarily change a default geo, but this
			// default
			// geo is always restored to its previous state after a mode change.

			if (oldDefaultGeo != null && modeChanged) {
				// add oldDefaultGeo to the default map so that the old default
				// is restored
				cons.getConstructionDefaults().addDefaultGeo(oldDefaultMode,
						oldDefaultGeo);
				oldDefaultGeo = null;
				oldDefaultMode = null;
			}

			// get the current default geo
			GeoElement geo = cons.getConstructionDefaults().getDefaultGeo(
					defaultGeoMap.get(mode));
			if (geo != null)
				activeGeoList.add(geo);

			// update the defaultGeos field (needed elsewhere for adjusting
			// default geo state)
			defaultGeos = activeGeoList;

			// update oldDefaultGeo
			if (modeChanged) {
				if (defaultGeos.size() == 0) {
					oldDefaultGeo = null;
					oldDefaultMode = -1;
				} else {
					oldDefaultGeo = defaultGeos.get(0);
					oldDefaultMode = defaultGeoMap.get(mode);
				}
			}

			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}

		/*
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		updateTableText(activeGeoList.toArray());
		for (int i = 0; i < popupBtnList.length; i++) {
			popupBtnList[i].update(activeGeoList.toArray());
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			toggleBtnList[i].update(activeGeoList.toArray());
		}

		// show the pen delete button
		// TODO: handle pen mode in code above
		btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		*/
    }

	private void updateTableText(Object[] geos) {

		tableText = null;
		if (geos == null || geos.length == 0
				|| mode == EuclidianConstants.MODE_PEN)
			return;

		boolean geosOK = true;
		AlgoElement algo;

		for (int i = 0; i < geos.length; i++) {
			algo = ((GeoElement) geos[i]).getParentAlgorithm();
			if (algo == null || !(algo instanceof AlgoTableText)) {
				geosOK = false;
			}
		}

		if (geosOK && geos[0] != null) {
			algo = ((GeoElement) geos[0]).getParentAlgorithm();
			tableText = (AlgoTableText) algo;
		}
	}

	private void createDefaultMap() {
		defaultGeoMap = new HashMap<Integer, Integer>();
		defaultGeoMap.put(EuclidianConstants.MODE_POINT,
				ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_COMPLEX_NUMBER,
				ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_POINT_ON_OBJECT,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		defaultGeoMap.put(EuclidianConstants.MODE_INTERSECT,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		defaultGeoMap.put(EuclidianConstants.MODE_MIDPOINT,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);

		defaultGeoMap.put(EuclidianConstants.MODE_JOIN,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_SEGMENT,
				ConstructionDefaults.DEFAULT_SEGMENT);
		defaultGeoMap.put(EuclidianConstants.MODE_SEGMENT_FIXED,
				ConstructionDefaults.DEFAULT_SEGMENT);
		defaultGeoMap.put(EuclidianConstants.MODE_RAY,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR,
				ConstructionDefaults.DEFAULT_VECTOR);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR_FROM_POINT,
				ConstructionDefaults.DEFAULT_VECTOR);

		defaultGeoMap.put(EuclidianConstants.MODE_ORTHOGONAL,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_PARALLEL,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_LINE_BISECTOR,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_ANGULAR_BISECTOR,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_TANGENTS,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_POLAR_DIAMETER,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_FITLINE,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_CREATE_LIST,
				ConstructionDefaults.DEFAULT_LIST);
		defaultGeoMap.put(EuclidianConstants.MODE_LOCUS,
				ConstructionDefaults.DEFAULT_LOCUS);

		defaultGeoMap.put(EuclidianConstants.MODE_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_REGULAR_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_RIGID_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_POLYLINE,
				ConstructionDefaults.DEFAULT_POLYGON);

		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_TWO_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_COMPASSES,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_SEMICIRCLE,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(
				EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC_SECTOR);
		defaultGeoMap.put(
				EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC_SECTOR);

		defaultGeoMap.put(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_PARABOLA,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CONIC_FIVE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);

		defaultGeoMap.put(EuclidianConstants.MODE_ANGLE,
				ConstructionDefaults.DEFAULT_ANGLE);
		defaultGeoMap.put(EuclidianConstants.MODE_ANGLE_FIXED,
				ConstructionDefaults.DEFAULT_ANGLE);

		defaultGeoMap.put(EuclidianConstants.MODE_DISTANCE,
				ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_AREA,
				ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_SLOPE,
				ConstructionDefaults.DEFAULT_POLYGON);

		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_LINE,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_POINT,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_ROTATE_BY_ANGLE,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_DILATE_FROM_POINT,
				ConstructionDefaults.DEFAULT_NONE);

		defaultGeoMap.put(EuclidianConstants.MODE_TEXT,
				ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_SLIDER,
				ConstructionDefaults.DEFAULT_NUMBER);
		defaultGeoMap.put(EuclidianConstants.MODE_IMAGE,
				ConstructionDefaults.DEFAULT_IMAGE);

		defaultGeoMap.put(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
				ConstructionDefaults.DEFAULT_BOOLEAN);
		defaultGeoMap.put(EuclidianConstants.MODE_BUTTON_ACTION,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_TEXTFIELD_ACTION,
				ConstructionDefaults.DEFAULT_NONE);
	}

	// =====================================================
	// Init GUI
	// =====================================================

	private void initGUI() {

		createButtons();

		addButtons();
	
		toggleBtnList = newToggleBtnList();
	}

	/**
	 * adds/removes buttons 
	 * (must be called on updates so that separators are drawn only when needed)
	 */
	private void addButtons() {

		clear();

		//--- order matters here
		
		// add graphics decoration buttons
		addGraphicsDecorationsButtons();
	}

	/**
	 * add axes, grid, ... buttons
	 */
	protected void addGraphicsDecorationsButtons(){
		add(btnShowAxes);
		add(btnShowGrid);
	}

	protected MyToggleButton[] newToggleBtnList() {
		return new MyToggleButton[] { btnCopyVisualStyle, btnPen, btnShowGrid,
				btnShowAxes, btnBold, btnItalic, btnDelete, btnLabel,
				btnPenEraser, btnHideShowLabel, btnTableTextLinesV,
				btnTableTextLinesH };
	}

	// =====================================================
	// Create Buttons
	// =====================================================

	protected void createButtons() {
		// TODO: fill in

		// ========================================
		// show axes button
		btnShowAxes = new MyToggleButton(
			GuiResources.INSTANCE.axes(),
			iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(mode != EuclidianConstants.MODE_PEN);
			}
		};

		// btnShowAxes.setPreferredSize(new Dimension(16,16));
		btnShowAxes.addValueChangeHandler(this);

		// ========================================
		// show grid button
		btnShowGrid = new MyToggleButton(
			GuiResources.INSTANCE.grid(),
			iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(mode != EuclidianConstants.MODE_PEN);
			}
		};
		// btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnShowGrid.addValueChangeHandler(this);
	}

	// =====================================================
	// Event Handlers
	// =====================================================

	protected void updateGUI() {
		if (isIniting)
			return;

		acceptValueChangeEvents(false);
		btnShowAxes.setValue(ev.getShowXaxis());
		acceptValueChangeEvents(true);

		acceptValueChangeEvents(false);
		btnShowGrid.setValue(ev.getShowGrid());
		acceptValueChangeEvents(true);
	}

	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i]).getGeoElementForPropertiesDialog() instanceof TextProperties)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	private boolean acceptValueChangeEvents = true;
	public void acceptValueChangeEvents(boolean accept) {
		acceptValueChangeEvents = accept;
	}

	public void onValueChange(ValueChangeEvent event) {
		if (acceptValueChangeEvents) {
			Object source = event.getSource();

			needUndo = false;

			ArrayList<GeoElement> targetGeos = new ArrayList<GeoElement>();
			targetGeos.addAll(ec.getJustCreatedGeos());
			if (mode != EuclidianConstants.MODE_MOVE)
				targetGeos.addAll(defaultGeos);
			else
				targetGeos.addAll(app.getSelectedGeos());

			processSource(source, targetGeos);

			if (needUndo) {
				app.storeUndoInfo();
				needUndo = false;
			}

			updateGUI();
		}
	}

	/**
	 * process the action performed
	 * 
	 * @param source
	 * @param targetGeos
	 */
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos) {

		if (source.equals(btnShowAxes)) {
			if (app.getEuclidianView1() == ev)
				app.getSettings().getEuclidian(1)
						.setShowAxes(!ev.getShowXaxis(), !ev.getShowXaxis());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.setShowAxes(!ev.getShowXaxis(), true);
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2)
						.setShowAxes(!ev.getShowXaxis(), !ev.getShowXaxis());
			else
				ev.setShowAxes(!ev.getShowXaxis(), true);
			ev.repaint();
		}

		else if (source.equals(btnShowGrid)) {
			if (app.getEuclidianView1() == ev)
				app.getSettings().getEuclidian(1).showGrid(!ev.getShowGrid());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.showGrid(!ev.getShowGrid());
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2).showGrid(!ev.getShowGrid());
			else
				ev.showGrid(!ev.getShowGrid());
			ev.repaint();
		}
	}
}
