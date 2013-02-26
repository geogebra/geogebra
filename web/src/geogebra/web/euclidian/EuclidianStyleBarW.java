package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.algos.AlgoTableText;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.common.main.SelectionManager;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.web.awt.GColorW;
import geogebra.web.awt.GDimensionW;
import geogebra.web.awt.GFontW;
import geogebra.web.gui.color.ColorPopupMenuButton;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.util.ButtonPopupMenu;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.MyCJButton;
import geogebra.web.gui.util.MyToggleButton;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianStyleBarW extends HorizontalPanel
	implements geogebra.common.euclidian.EuclidianStyleBar, ValueChangeHandler, ClickHandler {

	public static ButtonPopupMenu CURRENT_POP_UP = null;
	EuclidianControllerW ec;
	protected EuclidianViewInterfaceCommon ev;
	protected App app;
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

	HashMap<Integer, Integer> lineStyleMap;

	HashMap<Integer, Integer> pointStyleMap;


	// buttons and lists of buttons
	private ColorPopupMenuButton btnColor, btnBgColor, btnTextColor;

	private PopupMenuButton btnLineStyle, btnPointStyle, btnTextSize, btnMode;

	PopupMenuButton btnTableTextJustify;

	PopupMenuButton btnTableTextBracket;

	private PopupMenuButton btnLabelStyle;

	private PopupMenuButton btnPointCapture;
	
	private PopupMenuButton btnDeleteSize;

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
	private PopupMenuButton[] popupBtnList;
	
	private MyCJButton btnDeleteGeo;


	public EuclidianStyleBarW(EuclidianView ev) {
		isIniting = true;

		this.ev = ev;
		ec = (EuclidianControllerW)ev.getEuclidianController();
		app = ev.getApplication();
		cons = app.getKernel().getConstruction();

		// init handling of default geos
		defaultGeoMap = EuclidianStyleBarStatic.createDefaultMap();
		defaultGeos = new ArrayList<GeoElement>();

		// toolbar display settings
		//setFloatable(false);
		//Dimension d = getPreferredSize();
		//d.height = iconHeight + 8;
		//setPreferredSize(d);

		// init button-specific fields
		// TODO: put these in button classes
		EuclidianStyleBarStatic.pointStyleArray = EuclidianView.getPointStyles();
		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleMap.put(EuclidianStyleBarStatic.pointStyleArray[i], i);

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();
		lineStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.lineStyleArray.length; i++)
			lineStyleMap.put(EuclidianStyleBarStatic.lineStyleArray[i], i);

		initGUI();
		isIniting = false;
		
		setMode(ev.getMode()); // this will also update the stylebar
		addStyleName("EuclidianStyleBar");
	}

	public int getMode() {
		return mode;
	}

	public void updateButtonPointCapture(int mode) {
		if (mode == 3 || mode == 0)
			mode = 3 - mode; // swap 0 and 3
		btnPointCapture.setSelectedIndex(mode);
	}

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
		
		if (CURRENT_POP_UP != null) {
			CURRENT_POP_UP.hide();
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

		initGUI();
		updateStyleBar();

	    App.debug("implementation needed for GUI"); // TODO Auto-generated
    }
	
	private ArrayList<GeoElement> activeGeoList;


	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	public void updateStyleBar() {

		// -----------------------------------------------------
		// Create activeGeoList, a list of geos the stylebar can adjust.
		// These are either the selected geos or the current default geo.
		// Each button uses this list to update its gui and set visibility
		// -----------------------------------------------------
		activeGeoList = new ArrayList<GeoElement>();
		
		// -----------------------------------------------------
		// MODE_MOVE case: load activeGeoList with all selected geos
		// -----------------------------------------------------
		if (mode == EuclidianConstants.MODE_MOVE) {

			boolean hasGeosInThisView = false;
			SelectionManager selection = ev.getApplication().getSelectionManager();
			for (GeoElement geo : selection
					.getSelectedGeos()) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible() && !geo.isAxis()) {
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
				activeGeoList = selection
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

		updateButtons();

		// show the pen delete button
		// TODO: handle pen mode in code above
		//btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		addButtons();
    }
	

	private void updateButtons(){
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		Object[] geos = activeGeoList.toArray();
		tableText = EuclidianStyleBarStatic.updateTableText(geos, mode);
		for (int i = 0; i < popupBtnList.length; i++) {
			if (popupBtnList[i] != null)//null pointer fix until necessary
				popupBtnList[i].update(geos);
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			if (toggleBtnList[i] != null)//null pointer fix until necessary
				toggleBtnList[i].update(geos);
		}

	}
	

	public void updateVisualStyle(GeoElement geo) {
		if(activeGeoList.contains(geo))
			updateButtons();
	}
	// =====================================================
	// Init GUI
	// =====================================================

	private void initGUI() {

		createButtons();
		createColorButton();
		createBgColorButton();
		createTextButtons();
		setActionCommands();

		addButtons();
	
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
	}
	
	protected void setActionCommands(){
		setActionCommand(btnShowAxes, "showAxes");
		setActionCommand(btnShowGrid, "showGrid");
		setActionCommand(btnPointCapture, "pointCapture");
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
		addBtnPointCapture();

		// add color and style buttons
		if(btnColor.isVisible() || btnTextColor.isVisible()) {
			addSeparator();
		}
		
		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);
		add(btnLineStyle);
		add(btnPointStyle);
		
		// add text decoration buttons
		if(btnBold.isVisible())
			addSeparator();
		
		add(btnBold);
		add(btnItalic);
		add(btnTextSize);

		/*add(btnTableTextJustify);
		add(btnTableTextLinesV);
		add(btnTableTextLinesH);
		add(btnTableTextBracket);*/

		// add(btnPenEraser);
		// add(btnHideShowLabel);
		add(btnLabelStyle);
		
		// add(btnPointCapture);
		addBtnRotateView();
		// add(btnPenDelete);
		
		if (btnDeleteSize.isVisible()){
			addSeparator();
		}
		
		add(btnDeleteSize);
	}
	
	protected void addBtnRotateView() {

	}

	private void addSeparator() {
	    App.debug("Implementation needed...");
    }

	/**
	 * add axes, grid, ... buttons
	 */
	protected void addGraphicsDecorationsButtons(){
		add(btnShowAxes);
		add(btnShowGrid);
	}
	
	protected void addBtnPointCapture() {
		add(btnPointCapture);
	}

	protected MyToggleButton[] newToggleBtnList() {
		return new MyToggleButton[] { btnCopyVisualStyle, btnPen, btnShowGrid,
				btnShowAxes, btnBold, btnItalic, btnDelete, btnLabel,
				btnPenEraser, btnHideShowLabel, btnTableTextLinesV,
				btnTableTextLinesH };
	}
	
	protected PopupMenuButton[] newPopupBtnList() {
		return new PopupMenuButton[] { btnColor, btnBgColor, btnTextColor,
		        btnLineStyle, btnPointStyle, btnTextSize, btnTableTextJustify,
		        btnTableTextBracket, btnLabelStyle, btnDeleteSize,
		        btnPointCapture };
	}

	// =====================================================
	// Create Buttons
	// =====================================================

	protected void createButtons() {
		// TODO: fill in
		
		// ========================================
		// mode button
		
		ImageResource [] modeArray = new ImageResource [] {
				AppResources.INSTANCE.cursor_arrow(),
				AppResources.INSTANCE.application_graphics(),
				AppResources.INSTANCE.delete_small(),
				AppResources.INSTANCE.mode_point_16(),
				AppResources.INSTANCE.mode_copyvisualstyle_16()
		};
		
		btnMode = new PopupMenuButton((AppW) ev.getApplication(),
				modeArray, -1, 1, new GDimensionW(20, iconHeight),
				geogebra.common.gui.util.SelectionTable.MODE_ICON);
		btnMode.addActionListener(this);
		btnMode.setKeepVisible(false);
		// add(btnMode);
		
		// ========================================
		// delete button
		btnDelete = new MyToggleButton(
				AppResources.INSTANCE.delete_small(),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE)
						|| mode == EuclidianConstants.MODE_DELETE);
			}
		};
		btnDelete.addValueChangeHandler(this);
		// add(btnDelete);
		// ========================================
		// hide/show labels button
		btnLabel = new MyToggleButton(
				AppResources.INSTANCE.mode_copyvisualstyle_16(),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE)
						|| mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);
			}
		};
		btnLabel.addValueChangeHandler(this);
		// add(btnLabel);
		
		// ========================================
		// visual style button

		btnCopyVisualStyle = new MyToggleButton(
				AppResources.INSTANCE.mode_copyvisualstyle_16(),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				this.setVisible((geos.length > 0 && mode == EuclidianConstants.MODE_MOVE));
			}
		};
		btnCopyVisualStyle.addValueChangeHandler(this);
		// add(this.btnCopyVisualStyle);
		
		
		
		// ========================================
		// show axes button
		btnShowAxes = new MyToggleButton(
			AppResources.INSTANCE.axes(),
			iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(!EuclidianView.isPenMode(mode));
			}
		};

		// btnShowAxes.setPreferredSize(new Dimension(16,16));
		btnShowAxes.addValueChangeHandler(this);

		// ========================================
		// show grid button
		btnShowGrid = new MyToggleButton(
			AppResources.INSTANCE.grid(),
			iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(!EuclidianView.isPenMode(mode));
			}
		};
		// btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnShowGrid.addValueChangeHandler(this);
		
		// ========================================
		// line style button

		// create line style icon array
		final GDimensionW lineStyleIconSize = new GDimensionW(80, iconHeight);
		ImageData [] lineStyleIcons = new ImageData[EuclidianStyleBarStatic.lineStyleArray.length];
		for (int i = 0; i < EuclidianStyleBarStatic.lineStyleArray.length; i++)
			lineStyleIcons[i] = GeoGebraIcon.createLineStyleIcon(
					EuclidianStyleBarStatic.lineStyleArray[i], 2, lineStyleIconSize, geogebra.common.awt.GColor.BLACK, null);

		// create button
		btnLineStyle = new PopupMenuButton((AppW) app, lineStyleIcons, -1, 1,
				lineStyleIconSize, geogebra.common.gui.util.SelectionTable.MODE_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				if (EuclidianView.isPenMode(mode)) {
					/*this.setVisible(true);
					setFgColor(ec.getPen().getPenColor());
					setSliderValue(ec.getPen().getPenSize());
					setSelectedIndex(lineStyleMap.get(ec.getPen()
							.getPenLineStyle()));*/
					App.debug("Not MODE_PEN in EuclidianStyleBar yet");
				} else {
					boolean geosOK = (geos.length > 0);
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i])
								.getGeoElementForPropertiesDialog();
						if (!(geo.isPath()
								|| (geo.isGeoList() ? ((GeoList) geo)
										.showLineProperties() : false)
								|| (geo.isGeoNumeric() ? (((GeoNumeric) geo)
										.isDrawable() || ((GeoNumeric) geo)
										.isSliderFixed()) : false) || geo
								.isGeoAngle())) {
							geosOK = false;
							break;
						}
					}

					this.setVisible(geosOK);

					if (geosOK) {
						// setFgColor(((GeoElement)geos[0]).getObjectColor());

						setFgColor((GColorW) geogebra.common.awt.GColor.black);
						setSliderValue(((GeoElement) geos[0])
								.getLineThickness());

						setSelectedIndex(lineStyleMap
								.get(((GeoElement) geos[0]).getLineType()));

						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}
				}
			}

			@Override
			public ImageData getButtonIcon() {
				if (getSelectedIndex() > -1) {
					return GeoGebraIcon.createLineStyleIcon(
							EuclidianStyleBarStatic.lineStyleArray[this.getSelectedIndex()],
							this.getSliderValue(), lineStyleIconSize,
							geogebra.common.awt.GColor.BLACK, null);
				}
				return GeoGebraIcon.createEmptyIcon(lineStyleIconSize.getWidth(),
						lineStyleIconSize.getHeight());
			}

		};

		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider().setMaximum(13);
		btnLineStyle.getMySlider().setMajorTickSpacing(2);
		btnLineStyle.getMySlider().setMinorTickSpacing(1);
		btnLineStyle.getMySlider().setPaintTicks(true);
		btnLineStyle.addActionListener(this);

		// ========================================
		// point style button

		// create line style icon array
		final GDimensionW pointStyleIconSize = new GDimensionW(20, iconHeight);
		ImageData[] pointStyleIcons = new ImageData[EuclidianStyleBarStatic.pointStyleArray.length];
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleIcons[i] = GeoGebraIcon.createPointStyleIcon(
					EuclidianStyleBarStatic.pointStyleArray[i], 4, pointStyleIconSize, geogebra.common.awt.GColor.BLACK,
					null);

		// create button
		btnPointStyle = new PopupMenuButton((AppW) app, pointStyleIcons, 2, -1,
				pointStyleIconSize, geogebra.common.gui.util.SelectionTable.MODE_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				GeoElement geo;
				boolean geosOK = (geos.length > 0);
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
							&& (!(geo.isGeoList() && ((GeoList) geo)
									.showPointProperties()))) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);

				if (geosOK) {
					// setFgColor(((GeoElement)geos[0]).getObjectColor());
					setFgColor((GColorW) geogebra.common.awt.GColor.black);

					// if geo is a matrix, this will return a GeoNumeric...
					geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();

					// ... so need to check
					if (geo instanceof PointProperties) {
						setSliderValue(((PointProperties) geo).getPointSize());
						int pointStyle = ((PointProperties) geo)
								.getPointStyle();
						if (pointStyle == -1) // global default point style
							pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
						setSelectedIndex(pointStyleMap.get(pointStyle));
						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}
				}
			}

			@Override
			public ImageData getButtonIcon() {
				if (getSelectedIndex() > -1) {
					return GeoGebraIcon.createPointStyleIcon(
							EuclidianStyleBarStatic.pointStyleArray[this.getSelectedIndex()],
							this.getSliderValue(), pointStyleIconSize,
							geogebra.common.awt.GColor.BLACK, null);
				}
				return GeoGebraIcon.createEmptyIcon(pointStyleIconSize.getWidth(),
						pointStyleIconSize.getHeight());
			}
		};
		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider().setMaximum(9);
		btnPointStyle.getMySlider().setMajorTickSpacing(2);
		btnPointStyle.getMySlider().setMinorTickSpacing(1);
		btnPointStyle.getMySlider().setPaintTicks(true);
		btnPointStyle.addActionListener(this);

		// ========================================
		// eraser button
		btnPenEraser = new MyToggleButton(AppResources.INSTANCE.delete_small(),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				this.setVisible(EuclidianView.isPenMode(mode));
			}
		};

		btnPenEraser.addValueChangeHandler(this);

		// ========================================
		// delete geo button
		btnDeleteGeo = new MyCJButton(AppResources.INSTANCE.delete_small());
		btnDeleteGeo.addClickHandler(this);
		// add(btnDeleteGeo);

		// ========================================
		// hide/show label button
		btnHideShowLabel = new MyToggleButton(
				AppResources.INSTANCE.mode_showhidelabel_16(), iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// only show this button when handling selection, do not use it
				// for defaults
				if (mode != EuclidianConstants.MODE_MOVE) {
					this.setVisible(false);
					return;
				}
				boolean geosOK = (geos.length > 0);
				for (int i = 0; i < geos.length; i++) {
					if ((((GeoElement) geos[i])
							.getGeoElementForPropertiesDialog().isGeoText())) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);
				if (geosOK) {
					btnHideShowLabel.setSelected(((GeoElement) geos[0])
							.isLabelVisible());
				}
			}

		};

		btnHideShowLabel.addValueChangeHandler(this);

		// ========================================
		// caption style button

		String[] captionArray = new String[] { app.getPlain("stylebar.Hidden"), // index
																				// 4
				app.getPlain("Name"), // index 0
				app.getPlain("NameAndValue"), // index 1
				app.getPlain("Value"), // index 2
				app.getPlain("Caption") // index 3
		};

		btnLabelStyle = new PopupMenuButton((AppW) app, captionArray, -1, 1,
				new GDimensionW(0, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				boolean geosOK = false;
				GeoElement geo = null;
				if (mode == EuclidianConstants.MODE_MOVE) {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isLabelShowable()
								|| ((GeoElement) geos[i]).isGeoAngle()
								|| (((GeoElement) geos[i]).isGeoNumeric() ? ((GeoNumeric) geos[i])
										.isSliderFixed() : false)) {
							geo = (GeoElement) geos[i];
							geosOK = true;
							break;
						}
					}
				} else if (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF) {
					this.setVisible(false);
					return;
				} else if (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY) {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isLabelShowable()
								&& ((GeoElement) geos[i]).isGeoPoint()) {
							geo = (GeoElement) geos[i];
							geosOK = true;
							break;
						}
					}
				} else {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isLabelShowable()
								|| ((GeoElement) geos[i]).isGeoAngle()
								|| (((GeoElement) geos[i]).isGeoNumeric() ? ((GeoNumeric) geos[i])
										.isSliderFixed() : false)) {
							geo = (GeoElement) geos[i];
							geosOK = true;
							break;
						}
					}
				}
				this.setVisible(geosOK);

				if (geosOK) {
					if (!geo.isLabelVisible())
						setSelectedIndex(0);
					else
						setSelectedIndex(geo.getLabelMode() + 1);

				}
			}

			@Override
			public ImageData getButtonIcon() {
				return this.getIcon();
			}
		};
		ImageResource ic = AppResources.INSTANCE.mode_showhidelabel_16();
		btnLabelStyle.setIconSize(new GDimensionW(ic.getWidth(), iconHeight));
		//must be done with callback btnLabelStyle.setIcon(ic);
		AppResourcesConverter.setIcon(ic, btnLabelStyle);
		btnLabelStyle.addActionListener(this);
		btnLabelStyle.setKeepVisible(false);

		// ========================================
		// point capture button

		String[] strPointCapturing = { app.getMenu("Labeling.automatic"),
				app.getMenu("SnapToGrid"), app.getMenu("FixedToGrid"),
				app.getMenu("off") };

		btnPointCapture = new PopupMenuButton((AppW) app, strPointCapturing, -1, 1,
				new GDimensionW(0, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(!EuclidianView.isPenMode(mode));

			}

			@Override
			public ImageData getButtonIcon() {
				return this.getIcon();
			}

		};
		//it is not needed, must be an Image preloaded like others.
		ImageResource ptCaptureIcon = AppResources.INSTANCE.magnet2();
		btnPointCapture.setIconSize(new GDimensionW(ptCaptureIcon.getWidth(),
				iconHeight));
		//must be done in callback btnPointCapture.setIcon(ptCaptureIcon);
		AppResourcesConverter.setIcon(ptCaptureIcon, btnPointCapture);
		btnPointCapture.addActionListener(this);
		btnPointCapture.setKeepVisible(false);
		
		
		// =====================================================
		// Delete Size Button
		
		btnDeleteSize= new PopupMenuButton((AppW)app, null, 0, 0, new GDimensionW(20, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_ICON, false, true){

			@Override
            public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(mode==EuclidianConstants.MODE_DELETE);
            }
			
		};
		btnDeleteSize.getMySlider().setMinimum(10);
		btnDeleteSize.getMySlider().setMaximum(100);
		btnDeleteSize.getMySlider().setMajorTickSpacing(20);
		btnDeleteSize.getMySlider().setMinorTickSpacing(5);
		btnDeleteSize.getMySlider().setPaintTicks(true);
		btnDeleteSize.addActionListener(this);
		ImageResource deleteSizeIcon = AppResources.INSTANCE.delete_small();
		AppResourcesConverter.setIcon(deleteSizeIcon, btnDeleteSize);
	}

	// ========================================
		// object color button (color for everything except text)

		private void createColorButton() {

			final GDimensionW colorIconSize = new GDimensionW(20, iconHeight);
			btnColor = new ColorPopupMenuButton((AppW) app, colorIconSize,
					ColorPopupMenuButton.COLORSET_DEFAULT, true) {

				private static final long serialVersionUID = 1L;

				@Override
				public void update(Object[] geos) {

					if (EuclidianView.isPenMode(mode)) {
						/*this.setVisible(true);

						setSelectedIndex(getColorIndex(ec.getPen().getPenColor()));

						setSliderValue(100);
						getMySlider().setVisible(false);*/
						App.debug("not MODE_PEN_working yet in StyleBar");

					} else {
						boolean geosOK = (geos.length > 0 || EuclidianView.isPenMode(mode));
						for (int i = 0; i < geos.length; i++) {
							GeoElement geo = ((GeoElement) geos[i])
									.getGeoElementForPropertiesDialog();
							if (geo instanceof GeoImage || geo instanceof GeoText
									|| geo instanceof GeoButton) {
								geosOK = false;
								break;
							}
						}

						setVisible(geosOK);

						if (geosOK) {
							// get color from first geo
							geogebra.common.awt.GColor geoColor;
							geoColor = ((GeoElement) geos[0]).getObjectColor();

							// check if selection contains a fillable geo
							// if true, then set slider to first fillable's alpha
							// value
							float alpha = 1.0f;
							boolean hasFillable = false;
							for (int i = 0; i < geos.length; i++) {
								if (((GeoElement) geos[i]).isFillable()) {
									hasFillable = true;
									alpha = ((GeoElement) geos[i]).getAlphaValue();
									break;
								}
							}
							
							if (hasFillable)
								setTitle(app
										.getPlain("stylebar.ColorTransparency"));
							else
								setTitle(app.getPlain("stylebar.Color"));

							setSliderValue(Math.round(alpha * 100));

							updateColorTable();

							// find the geoColor in the table and select it
							int index = this.getColorIndex(geoColor);
							setSelectedIndex(index);
							setDefaultColor(alpha, geoColor);

							this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
						}
					}
				}

			};

			btnColor.addActionListener(this);
		}
		
		private void createBgColorButton() {

			final GDimensionW bgColorIconSize = new GDimensionW(20, iconHeight);

			btnBgColor = new ColorPopupMenuButton((AppW) app, bgColorIconSize,
					ColorPopupMenuButton.COLORSET_BGCOLOR, false) {

				private static final long serialVersionUID = 1L;

				@Override
				public void update(Object[] geos) {

					boolean geosOK = (geos.length > 0);
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i])
								.getGeoElementForPropertiesDialog();
						if (!(geo instanceof GeoText)
								&& !(geo instanceof GeoButton)) {
							geosOK = false;
							break;
						}
					}

					setVisible(geosOK);

					if (geosOK) {
						// get color from first geo
						geogebra.common.awt.GColor geoColor;
						geoColor = ((GeoElement) geos[0]).getBackgroundColor();

						/*
						 * // check if selection contains a fillable geo // if true,
						 * then set slider to first fillable's alpha value float
						 * alpha = 1.0f; boolean hasFillable = false; for (int i =
						 * 0; i < geos.length; i++) { if (((GeoElement)
						 * geos[i]).isFillable()) { hasFillable = true; alpha =
						 * ((GeoElement) geos[i]).getAlphaValue(); break; } }
						 * getMySlider().setVisible(hasFillable);
						 * setSliderValue(Math.round(alpha * 100));
						 */
						float alpha = 1.0f;
						updateColorTable();

						// find the geoColor in the table and select it
						int index = getColorIndex(geoColor);
						setSelectedIndex(index);
						setDefaultColor(alpha, geoColor);

						// if nothing was selected, set the icon to show the
						// non-standard color
						if (index == -1) {
							this.setIcon(GeoGebraIcon.createColorSwatchIcon(alpha,
									bgColorIconSize,
									geoColor, null));
						}
					}
				}
			};
			btnBgColor.setKeepVisible(true);
			btnBgColor.addActionListener(this);
		}
	
	private void createTextButtons() {
		// ========================
		// text color button
		final GDimensionW textColorIconSize = new GDimensionW(20, iconHeight);

		btnTextColor = new ColorPopupMenuButton((AppW) app, textColorIconSize,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private static final long serialVersionUID = 1L;

			private geogebra.common.awt.GColor geoColor;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					geoColor =	geo
							.getObjectColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}

					setFgColor((GColorW) geoColor);
					setFontStyle(((TextProperties) geo).getFontStyle());
				}
			}

			@Override
			public ImageData getButtonIcon() {
				return GeoGebraIcon.createTextSymbolIcon("A",
						(GFontW) app.getPlainFontCommon(), textColorIconSize,
						getSelectedColor(),
						null);
			}

		};

		btnTextColor.addActionListener(this);


		// ========================================
		// bold text button
		//ImageIcon boldIcon = GeoGebraIcon.createStringIcon(app.getPlain("Bold")
		//		.substring(0, 1), app.getPlainFont(), true, false, true,
		//		iconDimension, Color.black, null);
		btnBold = new MyToggleButton(
				AppResources.INSTANCE.format_text_bold(),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setValue(style == GFontW.BOLD
							|| style == (GFontW.BOLD + geogebra.common.awt.GFont.ITALIC));
				}
			}
		};
		btnBold.addValueChangeHandler(this);

		// ========================================
		// italic text button
		//ImageIcon italicIcon = GeoGebraIcon.createStringIcon(
		//		app.getPlain("Italic").substring(0, 1), app.getPlainFont(),
		//		false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButton(
				AppResources.INSTANCE.format_text_italic(),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				this.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnItalic.setValue(style == geogebra.common.awt.GFont.ITALIC
							|| style == (GFontW.BOLD + geogebra.common.awt.GFont.ITALIC));
				}
			}

		};
		btnItalic.addValueChangeHandler(this);
		
		
		// ========================================
		// text size button

		String[] textSizeArray = app.getLocalization().getFontSizeStrings();

		btnTextSize = new PopupMenuButton((AppW) app, textSizeArray, -1, 1,
				new GDimensionW(-1, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText
							.getFontSizeIndex(((TextProperties) geo)
									.getFontSizeMultiplier())); // font size ranges from
														// -4 to 4, transform
														// this to 0,1,..,4
				}
			}
		};
		btnTextSize.addActionListener(this);
		btnTextSize.setKeepVisible(false);

	}
	

	// =====================================================
	// Event Handlers
	// =====================================================

	protected void updateGUI() {

		if (isIniting)
			return;

		btnMode.removeActionListener(this);
		switch (mode) {
		case EuclidianConstants.MODE_MOVE:
			btnMode.setSelectedIndex(0);
			break;
		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			btnMode.setSelectedIndex(1);
			break;
		case EuclidianConstants.MODE_DELETE:
			btnMode.setSelectedIndex(2);
			break;
		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			btnMode.setSelectedIndex(3);
			break;
		}
		btnMode.addActionListener(this);

		//btnPen.removeActionListener(this);
		//btnPen.setSelected(mode == EuclidianConstants.MODE_PEN);
		//btnPen.addActionListener(this);

		btnDelete.removeValueChangeHandler(this);
		btnDelete.setSelected(mode == EuclidianConstants.MODE_DELETE);
		btnDelete.addValueChangeHandler(this);

		btnLabel.removeValueChangeHandler(this);
		btnLabel.setSelected(mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		btnLabel.addValueChangeHandler(this);

		btnShowAxes.removeValueChangeHandler(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		btnShowGrid.removeValueChangeHandler(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		btnShowGrid.addValueChangeHandler(this);
	}

	public void onValueChange(ValueChangeEvent event) {
			Object source = event.getSource();

			handleEventHandlers(source);
	}

	private void handleEventHandlers(Object source) {
	    needUndo = false;

	    ArrayList<GeoElement> targetGeos = new ArrayList<GeoElement>();
	    targetGeos.addAll(ec.getJustCreatedGeos());
	    if (mode != EuclidianConstants.MODE_MOVE)
	    	targetGeos.addAll(defaultGeos);
	    else
	    	targetGeos.addAll(app.getSelectionManager().getSelectedGeos());

	    processSource(source, targetGeos);

	    if (needUndo) {
	    	app.storeUndoInfo();
	    	needUndo = false;
	    }

	    updateGUI();
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
	/**
	 * process the action performed
	 * 
	 * @param source
	 * @param targetGeos
	 */
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos) {

		if ((source instanceof Widget)
				&& (EuclidianStyleBarStatic.processSourceCommon(
						getActionCommand((Widget) source), targetGeos, ev)))
			return;

		if (source == btnColor) {
			if (mode == EuclidianConstants.MODE_PEN) {
				App.debug("Not MODE_PEN in EuclidianStyleBar yet");
				/*ec.getPen().setPenColor(
						geogebra.awt.Color.getAwtColor(btnColor
								.getSelectedColor()));
				// btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());*/
			} else {
				GColor color = btnColor.getSelectedColor();
				float alpha = btnColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyColor(targetGeos, color, alpha, app);
				// btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
				// btnPointStyle.setFgColor((Color)btnColor.getSelectedValue());
			}
		} else if (source == btnBgColor) {
			if (btnBgColor.getSelectedIndex() >= 0) {
				GColor color = btnBgColor.getSelectedColor();
				float alpha = btnBgColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyBgColor(targetGeos, color, alpha);
			}
		}

		else if (source == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				GColor color = btnTextColor.getSelectedColor();
				needUndo = EuclidianStyleBarStatic.applyTextColor(targetGeos, color);
				// btnTextColor.setFgColor((Color)btnTextColor.getSelectedValue());
				// btnItalic.setForeground((Color)btnTextColor.getSelectedValue());
				// btnBold.setForeground((Color)btnTextColor.getSelectedValue());
			}
		} else if (source == btnLineStyle) {
			if (btnLineStyle.getSelectedValue() != null) {
				if (EuclidianView.isPenMode(mode)) {
					/*ec.getPen().setPenLineStyle(
							lineStyleArray[btnLineStyle.getSelectedIndex()]);
					ec.getPen().setPenSize(btnLineStyle.getSliderValue());*/
					App.debug("Not MODE_PEN in EuclidianStyleBar yet");
				} else {
					int selectedIndex = btnLineStyle.getSelectedIndex();
					int lineSize = btnLineStyle.getSliderValue();
					needUndo = EuclidianStyleBarStatic.applyLineStyle(targetGeos, selectedIndex, lineSize);
				}

			}
		} else if (source == btnPointStyle) {
			if (btnPointStyle.getSelectedValue() != null) {
				int pointStyleSelIndex = btnPointStyle.getSelectedIndex();
				int pointSize = btnPointStyle.getSliderValue();
				needUndo = EuclidianStyleBarStatic.applyPointStyle(targetGeos, pointStyleSelIndex, pointSize);
			}
		} else if (source == btnBold) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos, 1);
		} else if (source == btnItalic) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos, 2);
		} else if (source == btnTextSize) {
			needUndo = EuclidianStyleBarStatic.applyTextSize(targetGeos, btnTextSize.getSelectedIndex());
		} else if (source == btnHideShowLabel) {
			applyHideShowLabel(targetGeos);
			updateStyleBar();
		} else if (source == btnLabelStyle) {
			needUndo = EuclidianStyleBarStatic.applyCaptionStyle(targetGeos, mode, btnLabelStyle.getSelectedIndex());
		}

		else if (source == btnTableTextJustify || source == btnTableTextLinesH|| source == btnTableTextLinesV || source == btnTableTextBracket) {
			EuclidianStyleBarStatic.applyTableTextFormat(targetGeos, btnTableTextJustify.getSelectedIndex(), btnTableTextLinesH.isSelected(), btnTableTextLinesV.isSelected(), btnTableTextBracket.getSelectedIndex(), app);
		}

		else if (source == btnDeleteSize){
			ec.setDeleteToolSize(btnDeleteSize.getSliderValue());
		}
	}
	
	// ==============================================
		// Apply Styles
		// ==============================================


		private void applyHideShowLabel(ArrayList<GeoElement> geos) {
			boolean visible = btnHideShowLabel.isSelected();
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				if (geo.isLabelVisible() != visible) {
					geo.setLabelVisible(visible);
					geo.updateRepaint();
					needUndo = true;
				}
			}
		}


	public void onClick(ClickEvent event) {
			Object source = event.getSource();

			handleEventHandlers(source);
    }

	/**
	 * @param actionButton runs programatically the action performed event.
	 */
	public void fireActionPerformed(Object actionButton) {
		handleEventHandlers(actionButton);
    }
	
	/**
	 *  programatically runs update on all buttons
	 */
	public void updateAllButtons() {
	
	}

	public int getPointCaptureSelectedIndex() {
	    return btnPointCapture.getSelectedIndex();
    }
	
	private void setActionCommand(Widget widget, String actionCommand){
		widget.getElement().setAttribute("actionCommand", actionCommand);
	}

	private String getActionCommand(Widget widget){
		return widget.getElement().getAttribute("actionCommand");
	}


	
}
