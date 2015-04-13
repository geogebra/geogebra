package org.geogebra.web.web.euclidian;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.color.ColorPopupMenuButton;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.AppResourcesConverter;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.util.ButtonPopupMenu;
import org.geogebra.web.web.gui.util.GeoGebraIcon;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyCJButton;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.PointStylePopup;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.StyleBarW2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

/**
 * StyleBar for euclidianView
 */
public class EuclidianStyleBarW extends StyleBarW2 implements
        org.geogebra.common.euclidian.EuclidianStyleBar,
		ValueChangeHandler<Boolean> {

	/**
	 * Toggle button that should be visible if no geos are selected or to be
	 * created and no special icons appear in stylebar (eg. delete mode)
	 */
	protected class MyToggleButtonForEV extends MyToggleButton2 {
		/**
		 * @param img
		 *            image
		 */
		public MyToggleButtonForEV(ImageResource img) {
			super(img);
		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0 && !EuclidianView.isPenMode(mode)
			        && mode != EuclidianConstants.MODE_DELETE);
		}
	}

	private enum StyleBarMethod {
		NONE, UPDATE, UPDATE_STYLE
	}

	public static ButtonPopupMenu CURRENT_POP_UP = null;
	private EuclidianController ec;
	protected EuclidianView ev;
	private Construction cons;

	protected HashMap<Integer, Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;

	// flags and constants
	public int mode = -1;
	private boolean isIniting;
	private Integer oldDefaultMode;
	private boolean modeChanged = true;
	private boolean firstPaint = true;

	// button-specific fields
	// TODO: create button classes so these become internal

	private HashMap<Integer, Integer> lineStyleMap;
	private HashMap<Integer, Integer> pointStyleMap;
	private ArrayList<GeoElement> activeGeoList;
	private boolean visible;

	// // buttons and lists of buttons
	private ColorPopupMenuButton btnBgColor, btnTextColor;
	private PopupMenuButton btnTextSize;
	private PopupMenuButton btnLabelStyle;
	private PopupMenuButton btnAngleInterval;
	private PopupMenuButton btnShowGrid;
	protected PopupMenuButton btnPointCapture;

	private MyToggleButton2 btnShowAxes;
	MyToggleButton2 btnBold;
	MyToggleButton2 btnItalic;
	
	private MyToggleButton2 btnFixPosition, btnFixObject;
	
	protected MyCJButton btnStandardView;

	private MyToggleButton2[] toggleBtnList;
	private MyToggleButton2[] btnDeleteSizes = new MyToggleButton2[3];
	private PopupMenuButton[] popupBtnList;

	private StyleBarMethod waitingOperation = StyleBarMethod.NONE;


	/**
	 * @param ev
	 *            {@link EuclidianView}
	 * @param viewID
	 *            id of the panel
	 */
	public EuclidianStyleBarW(EuclidianView ev, int viewID) {
		super((AppW) ev.getApplication(), viewID);

		isIniting = true;
		this.ev = ev;
		ec = ev.getEuclidianController();
		cons = app.getKernel().getConstruction();
		// init handling of default geos
		createDefaultMap();
		defaultGeos = new ArrayList<GeoElement>();

		// init button-specific fields
		// TODO: put these in button classes
		EuclidianStyleBarStatic.pointStyleArray = EuclidianView
		        .getPointStyles();
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
		setToolTips();

		if (ev.equals(app.getEuclidianView1())) {
			optionType = OptionType.EUCLIDIAN;
		} else {
			optionType = OptionType.EUCLIDIAN2;
		}
	}

	/**
	 * create default map between default geos and modes
	 */
	protected void createDefaultMap() {
		defaultGeoMap = EuclidianStyleBarStatic.createDefaultMap();
	}

	/**
	 * 
	 * @return euclidian view attached
	 */
	public EuclidianView getView() {
		return ev;
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

	@Override
	public void setOpen(boolean visible) {
		this.visible = visible;
		if (visible) {
			switch (this.waitingOperation) {
			case UPDATE:
				updateStyleBar();
				break;
			case UPDATE_STYLE:
				updateButtons();
				break;
			}
			this.waitingOperation = StyleBarMethod.NONE;
		}
	}

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	public void updateStyleBar() {
		if (!visible) {
			this.waitingOperation = StyleBarMethod.UPDATE;
			return;
		}

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
			SelectionManager selection = ev.getApplication()
			        .getSelectionManager();
			for (GeoElement geo : selection.getSelectedGeos()) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible()
				        && !geo.isAxis()) {
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
				activeGeoList = selection.getSelectedGeos();

				// we also update stylebars according to just created geos
				activeGeoList.addAll(ec.getJustCreatedGeos());
			}
		}

		// -----------------------------------------------------
		// MODE_PEN: for the pen mode the default construction is
		// saved in EuclidianPen
		// All other modes: load activeGeoList with current default geo
		// -----------------------------------------------------
		else if (defaultGeoMap.containsKey(mode)
		        || mode == EuclidianConstants.MODE_PEN) {

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
			ArrayList<GeoElement> justCreatedGeos = ec.getJustCreatedGeos();
			Integer type = defaultGeoMap.get(mode);
			if (mode == EuclidianConstants.MODE_PEN) {
				GeoElement geo = ec.getPen().DEFAULT_PEN_LINE;
				if (geo != null) {
					activeGeoList.add(geo);
				}
			} else {
				if (type.equals(ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX) && justCreatedGeos.size() == 1){
					GeoElement justCreated = justCreatedGeos.get(0);
					if (justCreated.isGeoPoint()){
						// get default type regarding what type of point has been created
						if (((GeoPointND) justCreated).hasPath()){
							type = ConstructionDefaults.DEFAULT_POINT_ON_PATH;
						}else if (((GeoPointND) justCreated).hasRegion()){
							type = ConstructionDefaults.DEFAULT_POINT_IN_REGION;
						}else if (!((GeoPointND) justCreated).isIndependent()){
							type = ConstructionDefaults.DEFAULT_POINT_DEPENDENT;
						}else{
							type = ConstructionDefaults.DEFAULT_POINT_FREE;
						}
					}
				}
				
				if (type.equals(ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)){
					// add all non-complex default points
					activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
							ConstructionDefaults.DEFAULT_POINT_FREE));
					activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
							ConstructionDefaults.DEFAULT_POINT_ON_PATH));
					activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
							ConstructionDefaults.DEFAULT_POINT_IN_REGION));
					activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
							ConstructionDefaults.DEFAULT_POINT_DEPENDENT));
				}else{
					GeoElement geo = cons.getConstructionDefaults().getDefaultGeo(type);
					if (geo != null){
						activeGeoList.add(geo);
					}
				}
			}

			

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
					oldDefaultMode = type;
				}
			}

			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}
		updateButtons();
		// show the pen delete button
		// TODO: handle pen mode in code above
		// btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		addButtons();
	}

	private void updateButtons() {
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		if (activeGeoList == null) {
			return;
		}
		double l = System.currentTimeMillis();
		Object[] geos = activeGeoList.toArray();
		App.debug("tabletext" + (System.currentTimeMillis() - l));
		for (int i = 0; i < popupBtnList.length; i++) {
			if (popupBtnList[i] != null) {// null pointer fix until necessary
				popupBtnList[i].update(geos);
				App.debug(i + "popup" + (System.currentTimeMillis() - l));
			}
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			if (toggleBtnList[i] != null) {// null pointer fix until necessary
				toggleBtnList[i].update(geos);
				App.debug(i + "toggle" + (System.currentTimeMillis() - l));
			}
		}

	}

	public void updateVisualStyle(GeoElement geo) {
		if (activeGeoList != null && activeGeoList.contains(geo)) {
			if (!visible) {
				this.waitingOperation = StyleBarMethod.UPDATE_STYLE;
				return;
			}
			updateButtons();
		}
	}

	// =====================================================
	// Init GUI
	// =====================================================

	private void initGUI() {
		createButtons();
		setActionCommands();

		addButtons();

		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
	}

	protected void setActionCommands() {
		setActionCommand(btnShowAxes, "showAxes");
		setActionCommand(btnPointCapture, "pointCapture");
	}

	/**
	 * adds/removes buttons (must be called on updates so that separators are
	 * drawn only when needed)
	 */
	private void addButtons() {

		clear();

		// --- order matters here

		// add graphics decoration buttons
		addGraphicsDecorationsButtons();
		add(btnPointCapture);

		// add color and style buttons
		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);
		add(btnLineStyle);
		add(btnPointStyle);

		// add text decoration buttons
		if (btnBold.isVisible())
			addSeparator();

		add(btnBold);
		add(btnItalic);
		add(btnTextSize);

		add(btnAngleInterval);


		add(btnLabelStyle);

		addBtnRotateView();

		if (btnFixPosition.isVisible() || btnFixObject.isVisible())
			addSeparator();

		add(btnFixPosition);
		add(btnFixObject);

		for (int i = 0; i < 3; i++) {
			add(btnDeleteSizes[i]);
		}

		addMenuButton();
		if(getViewButton() == null){
			addViewButton();
		}else{
			add(getViewButton());
		}
	}

	protected void addBtnRotateView() {
		//
	}

	/**
	 * add axes, grid, ... buttons
	 */
	protected void addGraphicsDecorationsButtons() {
		addAxesAndGridButtons();
		add(btnStandardView);
	}

	/**
	 * add axes and grid buttons
	 */
	protected void addAxesAndGridButtons() {
		add(btnShowAxes);
		add(btnShowGrid);
	}

	protected MyToggleButton2 getAxesOrGridToggleButton() {
		return btnShowAxes;
	}

	protected PopupMenuButton getAxesOrGridPopupMenuButton() {
		return btnShowGrid;
	}

	private MyToggleButton2[] newToggleBtnList() {
		return new MyToggleButton2[] { getAxesOrGridToggleButton(), btnBold,
		        btnItalic, btnFixPosition, btnFixObject, btnDeleteSizes[0],
		        btnDeleteSizes[1],
		        btnDeleteSizes[2] };
	}

	protected PopupMenuButton[] newPopupBtnList() {
		return new PopupMenuButton[] { getAxesOrGridPopupMenuButton(),
		        btnColor, btnBgColor, btnTextColor, btnLineStyle,
		        btnPointStyle, btnTextSize, btnAngleInterval, btnLabelStyle,
		        btnPointCapture };
	}

	// =====================================================
	// Create Buttons
	// =====================================================

	protected void createButtons() {
		// TODO: fill in
		createAxesAndGridButtons();
		createStandardViewBtn();
		createLineStyleBtn(mode);
		createPointStyleBtn(mode);
		createLabelStyleBtn();
		createAngleIntervalBtn();
		createPointCaptureBtn();
		createDeleteSiztBtn();
		createColorBtn();
		createBgColorBtn();
		createTextColorBtn();
		createTextBoldBtn();
		createTextItalicBtn();
		createFixPositionBtn();
		createFixObjectBtn();
		createTextSizeBtn();
	}

	protected void createAxesAndGridButtons() {
		// ========================================
		// show axes button
		btnShowAxes = new MyToggleButtonForEV(StyleBarResources.INSTANCE.axes());
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		// ========================================
		// show grid button
		ImageOrText[] grids = new ImageOrText[4];
		for (int i = 0; i < 4; i++)
			grids[i] = GeoGebraIcon
			        .createGridStyleIcon(EuclidianStyleBarStatic.pointStyleArray[i]);
		btnShowGrid = new GridPopup(app, grids, -1, 4,
		        org.geogebra.common.gui.util.SelectionTable.MODE_ICON, ev);
		btnShowGrid.addPopupHandler(this);
	}

    private void createDeleteSiztBtn() {
		ImageResource[] delBtns = new ImageResource[] {
		        StyleBarResources.INSTANCE.stylingbar_delete_small(),
		        StyleBarResources.INSTANCE.stylingbar_delete_medium(),
		        StyleBarResources.INSTANCE.stylingbar_delete_large() };
		for (int i = 0; i < 3; i++) {
			btnDeleteSizes[i] = new MyToggleButton2(delBtns[i]) {

				@Override
				public void update(Object[] geos) {
					// always show this button unless in pen mode
					this.setVisible(mode == EuclidianConstants.MODE_DELETE);
				}

			};
			btnDeleteSizes[i].addValueChangeHandler(this);
		}
    }

	private void createPointCaptureBtn() {
		ImageOrText[] strPointCapturing = ImageOrText.convert(new String[] {
		        app.getMenu("Labeling.automatic"), app.getMenu("SnapToGrid"),
		        app.getMenu("FixedToGrid"), app.getMenu("off") });

		btnPointCapture = new PopupMenuButton(app, strPointCapturing, -1, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {
				// same as axes
				this.setVisible(geos.length == 0
				        && !EuclidianView.isPenMode(mode)
				        && mode != EuclidianConstants.MODE_DELETE);
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}

		};

		// it is not needed, must be an Image preloaded like others.
		ImageResource ptCaptureIcon = StyleBarResources.INSTANCE.magnet();
		// must be done in callback btnPointCapture.setIcon(ptCaptureIcon);
		AppResourcesConverter.setIcon(ptCaptureIcon, btnPointCapture);
		btnPointCapture.addPopupHandler(this);
		btnPointCapture.setKeepVisible(false);
	}

    private void createLabelStyleBtn() {
		ImageOrText[] captionArray = ImageOrText.convert(new String[] {
		        app.getPlain("stylebar.Hidden"), // index
		        // 4
		        app.getPlain("Name"), // index 0
		        app.getPlain("NameAndValue"), // index 1
		        app.getPlain("Value"), // index 2
		        app.getPlain("Caption") // index 3
		        });

		btnLabelStyle = new PopupMenuButton(app, captionArray, -1, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {
				GeoElement geo = EuclidianStyleBarStatic
				        .checkGeosForCaptionStyle(geos, mode, app);
				boolean geosOK = geo != null;
				this.setVisible(geosOK);

				if (geosOK) {
					setSelectedIndex(EuclidianStyleBarStatic
					        .getIndexForLabelMode(geo, app));
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}
		};
		ImageResource ic = AppResources.INSTANCE.mode_showhidelabel_16();
		// must be done with callback btnLabelStyle.setIcon(ic);
		AppResourcesConverter.setIcon(ic, btnLabelStyle);
		btnLabelStyle.addPopupHandler(this);
		btnLabelStyle.setKeepVisible(false);
    }

	private void createAngleIntervalBtn() {

		String[] angleIntervalString = new String[GeoAngle.INTERVAL_MIN.length - 1];
		for (int i = 0; i < GeoAngle.INTERVAL_MIN.length - 1; i++) {
			angleIntervalString[i] = app.getLocalization().getPlain(
			        "AngleBetweenAB.short", GeoAngle.INTERVAL_MIN[i],
			        GeoAngle.INTERVAL_MAX[i]);
		}

		ImageOrText[] angleIntervalArray = ImageOrText
		        .convert(angleIntervalString);

		btnAngleInterval = new PopupMenuButton(app, angleIntervalArray, -1, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {
				GeoElement geo = EuclidianStyleBarStatic
				        .checkGeosForAngleInterval(geos);
				boolean geosOK = (geo != null);
				this.setVisible(geosOK);

				if (geosOK) {
					setSelectedIndex(((AngleProperties) geo).getAngleStyle()
					        .getXmlVal());
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}
		};
		ImageResource ic = AppResources.INSTANCE.stylingbar_angle_interval();
		// must be done with callback btnLabelStyle.setIcon(ic);
		AppResourcesConverter.setIcon(ic, btnAngleInterval);
		btnAngleInterval.addPopupHandler(this);
		btnAngleInterval.setKeepVisible(false);
	}

    private void createStandardViewBtn() {
		btnStandardView = new MyCJButton();
		ImageOrText icon = new ImageOrText();
		icon.setUrl(StyleBarResources.INSTANCE.standard_view().getSafeUri()
		        .asString());
		btnStandardView.setIcon(icon);
		btnStandardView.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ev.setStandardView(true);
			}
		});
    }

	private void createColorBtn() {

		final GDimensionW colorIconSize = new GDimensionW(20, ICON_HEIGHT);
		btnColor = new ColorPopupMenuButton(app, colorIconSize,
		        ColorPopupMenuButton.COLORSET_DEFAULT, true) {

			@Override
			public void update(Object[] geos) {

				if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE) {
					App.debug("MODE_FREEHAND_SHAPE not working in StyleBar yet");
				} else {
					boolean geosOK = (geos.length > 0 || EuclidianView
					        .isPenMode(mode));
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
						org.geogebra.common.awt.GColor geoColor;
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
							setTitle(app.getPlain("stylebar.ColorTransparency"));
						else
							setTitle(app.getPlain("stylebar.Color"));
						setSliderVisible(hasFillable);

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
		btnColor.addPopupHandler(this);
	}

	private void createBgColorBtn() {

		final GDimensionW bgColorIconSize = new GDimensionW(20, ICON_HEIGHT);

		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
		        ColorPopupMenuButton.COLORSET_BGCOLOR, false) {

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
					org.geogebra.common.awt.GColor geoColor;
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
						        geoColor, null));
					}
				}
			}
		};
		btnBgColor.setKeepVisible(true);
		btnBgColor.addPopupHandler(this);
	}

	private void createTextColorBtn() {
		final GDimensionW textColorIconSize = new GDimensionW(24, ICON_HEIGHT);

		btnTextColor = new ColorPopupMenuButton(app, textColorIconSize,
		        ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private org.geogebra.common.awt.GColor geoColor;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
					        .getGeoElementForPropertiesDialog();
					geoColor = geo.getObjectColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return GeoGebraIcon.createTextSymbolIcon("A",
				        getSelectedColor(), null);
			}

		};
		btnTextColor.addStyleName("btnTextColor");
		btnTextColor.addPopupHandler(this);

	}

	private void createTextBoldBtn() {
		btnBold = new MyToggleButton2(app.getMenu("Bold.Short")) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos)
				        && !((GeoElement) geos[0]).isGeoTextField();
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
					        .getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setValue((style & org.geogebra.common.awt.GFont.BOLD) != 0);
				}
			}
		};
		btnBold.addStyleName("btnBold");
		btnBold.addValueChangeHandler(this);
	}

	private void createFixPositionBtn() {
		btnFixPosition = new MyToggleButton2(
		        StyleBarResources.INSTANCE.fixPosition()) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = EuclidianStyleBarStatic
				        .checkGeosForFixPosition(geos);
				setVisible(geosOK);
				if (geosOK) {
					btnFixPosition.setValue(EuclidianStyleBarStatic
					        .checkSelectedFixPosition((GeoElement) geos[0]));
				}
			}
		};
		// btnFixPosition.addStyleName("btnFixPosition");
		btnFixPosition.addValueChangeHandler(this);
	}

	private void createFixObjectBtn() {

		btnFixObject = new MyToggleButton2(
		        StyleBarResources.INSTANCE.objectUnfixed(),
		        StyleBarResources.INSTANCE.objectFixed()) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = EuclidianStyleBarStatic
				        .checkGeosForFixObject(geos);
				setVisible(geosOK);
				if (geosOK) {
					btnFixObject.setValue(EuclidianStyleBarStatic
					        .checkSelectedFixObject((GeoElement) geos[0]));
				}
			}
		};
		btnFixObject.addValueChangeHandler(this);
	}

	private void createTextItalicBtn() {
		btnItalic = new MyToggleButton2(app.getMenu("Italic.Short")) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos)
				        && !((GeoElement) geos[0]).isGeoTextField();
				setVisible(geosOK);
				this.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
					        .getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnItalic
					        .setValue((style & org.geogebra.common.awt.GFont.ITALIC) != 0);
				}
			}

		};
		btnItalic.addStyleName("btnItalic");
		btnItalic.addValueChangeHandler(this);
	}

	private void createTextSizeBtn() {

		// ========================================
		// text size button

		ImageOrText[] textSizeArray = ImageOrText.convert(app.getLocalization()
		        .getFontSizeStrings());

		btnTextSize = new PopupMenuButton(app, textSizeArray, -1, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
					        .getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText
					        .getFontSizeIndex(((TextProperties) geo)
					                .getFontSizeMultiplier())); // font size
																// ranges from
					// -4 to 4, transform
					// this to 0,1,..,4
				}
			}
		};
		btnTextSize.addPopupHandler(this);
		btnTextSize.setKeepVisible(false);
		btnTextSize
		        .setIcon(GeoGebraIcon
		                .createResourceImageIcon(StyleBarResources.INSTANCE
		                        .font_size()));

	}

	// =====================================================
	// Event Handlers
	// =====================================================

	public void updateGUI() {

		if (isIniting)
			return;

		btnPointCapture.removeActionListener(this);
		updateButtonPointCapture(ev.getPointCapturingMode());

		updateAxesAndGridGUI();
	}

	protected void updateAxesAndGridGUI() {
		btnShowAxes.removeValueChangeHandler();
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelectedIndex(gridIndex(ev));
	}

	public void onValueChange(ValueChangeEvent event) {
		Object source = event.getSource();

		handleEventHandlers(source);
	}

	@Override
	protected void handleEventHandlers(Object source) {
		needUndo = false;

		ArrayList<GeoElement> targetGeos = new ArrayList<GeoElement>();
		targetGeos.addAll(ec.getJustCreatedGeos());
		if (mode != EuclidianConstants.MODE_MOVE) {
			targetGeos.addAll(defaultGeos);
			Previewable p = ev.getPreviewDrawable();
			if (p != null) {
				GeoElement geo = p.getGeoElement();
				if (geo != null) {
					targetGeos.add(geo);
				}
			}
		} else
			targetGeos.addAll(app.getSelectionManager().getSelectedGeos());

		processSource(source, targetGeos);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}

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

	protected boolean processSourceForAxesAndGrid(Object source) {
		if (source == btnShowGrid) {
			if (btnShowGrid.getSelectedValue() != null) {
				setGridType(ev, btnShowGrid.getSelectedIndex());
			}
			return true;
		}
		return false;
	}

	/**
	 * process the action performed
	 * 
	 * @param source
	 * @param targetGeos
	 */
	@Override
	protected boolean processSource(Object source,
			ArrayList<GeoElement> targetGeos) {

		if ((source instanceof Widget)
		        && (EuclidianStyleBarStatic.processSourceCommon(
		                getActionCommand((Widget) source), targetGeos, ev)))
			return true;

		// processes btnColor, btnLineStyle and btnPointStyle
		if (super.processSource(source, targetGeos)) {
			return true;
		}

		if (source == btnBgColor) {
			if (btnBgColor.getSelectedIndex() >= 0) {
				GColor color = btnBgColor.getSelectedColor();
				float alpha = btnBgColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyBgColor(targetGeos,
				        color, alpha);
			}
		} else if (source == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				GColor color = btnTextColor.getSelectedColor();
				needUndo = EuclidianStyleBarStatic.applyTextColor(targetGeos,
				        color);
			}
		} else if (processSourceForAxesAndGrid(source)) {
			// done in method
		} else if (source == btnBold) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos,
			        GFont.ITALIC, btnBold.isDown() ? GFont.BOLD : GFont.PLAIN);
		} else if (source == btnItalic) {
			needUndo = EuclidianStyleBarStatic
			        .applyFontStyle(targetGeos, GFont.BOLD,
			                btnItalic.isDown() ? GFont.ITALIC : GFont.PLAIN);
		} else if (source == btnTextSize) {
			needUndo = EuclidianStyleBarStatic.applyTextSize(targetGeos,
			        btnTextSize.getSelectedIndex());
		} else if (source == btnAngleInterval) {
			needUndo = EuclidianStyleBarStatic.applyAngleInterval(targetGeos,
			        btnAngleInterval.getSelectedIndex());
		} else if (source == btnLabelStyle) {
			needUndo = EuclidianStyleBarStatic.applyCaptionStyle(targetGeos,
			        mode, btnLabelStyle.getSelectedIndex());
		} else if (source == btnFixPosition) {
			needUndo = EuclidianStyleBarStatic.applyFixPosition(targetGeos,
			        btnFixPosition.isSelected(), ev) != null;
		} else if (source == btnFixObject) {
			needUndo = EuclidianStyleBarStatic.applyFixObject(targetGeos,
			        btnFixObject.isSelected(), ev) != null;
			btnFixObject.update(targetGeos.toArray());
		}

		else {
			for (int i = 0; i < 3; i++) {
				if (source == btnDeleteSizes[i]) {
					setDelSize(i);
					return true;
				}
			}
			return false;
		}

		return true;
	}

	public static void setGridType(EuclidianView ev, int val) {
		EuclidianSettings evs = ev.getSettings();
		boolean gridChanged = false;
		if (val == 0) {
			gridChanged = evs.showGrid(false);
		} else {
			evs.beginBatch();
			gridChanged = evs.showGrid(true);
			switch (val) {
			case 2:
				evs.setGridType(EuclidianView.GRID_POLAR);
				break;
			case 3:
				evs.setGridType(EuclidianView.GRID_ISOMETRIC);
				break;
			default:
				evs.setGridType(EuclidianView.GRID_CARTESIAN);
			}
			evs.endBatch();
		}
		if (gridChanged) {
			ev.getApplication().storeUndoInfo();
		}
	}

	private void setDelSize(int s) {
		ev.getSettings().setDeleteToolSize(EuclidianSettings.DELETE_SIZES[s]);
		for (int i = 0; i < 3; i++) {
			btnDeleteSizes[i].setDown(i == s);
			btnDeleteSizes[i].setEnabled(i != s);
		}
	}

	public int getPointCaptureSelectedIndex() {
		return btnPointCapture.getSelectedIndex();
	}

	protected void setActionCommand(Widget widget, String actionCommand) {
		widget.getElement().setAttribute("actionCommand", actionCommand);
	}

	private String getActionCommand(Widget widget) {
		return widget.getElement().getAttribute("actionCommand");
	}

	public static int gridIndex(EuclidianView ev) {
		if (!ev.getShowGrid()) {
			return 0;
		}
		if (ev.getGridType() == EuclidianView.GRID_POLAR) {
			return 2;
		}
		if (ev.getGridType() == EuclidianView.GRID_ISOMETRIC) {
			return 3;
		}
		return 1;
	}

	@Override
	public void hidePopups() {
		if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
			EuclidianStyleBarW.CURRENT_POP_UP.hide();
		}
	}

	public void resetFirstPaint() {
		firstPaint = true;
	}

	@Override
	public void onAttach() {

		if (firstPaint) {
			firstPaint = false;
			updateGUI();
		}

		super.onAttach();
	}

	public PointStylePopup getBtnPointStyle() {
	    return btnPointStyle;
    }

	@Override
	public void setLabels() {
		super.setLabels();
		// set labels for popups
		this.btnPointCapture.getMyTable().updateText(
		        ImageOrText.convert(new String[] {
		                app.getMenu("Labeling.automatic"),
		                app.getMenu("SnapToGrid"), app.getMenu("FixedToGrid"),
		                app.getMenu("off") }));
		this.btnLabelStyle.getMyTable().updateText(
		        ImageOrText.convert(new String[] {
		                app.getPlain("stylebar.Hidden"), // index 4
		                app.getPlain("Name"), // index 0
		                app.getPlain("NameAndValue"), // index 1
		                app.getPlain("Value"), // index 2
		                app.getPlain("Caption") // index 3
		                }));

		String[] angleIntervalArray = new String[GeoAngle.INTERVAL_MIN.length - 1];
		for (int i = 0; i < GeoAngle.INTERVAL_MIN.length - 1; i++) {
			angleIntervalArray[i] = app.getLocalization().getPlain(
			        "AngleBetweenAB.short", GeoAngle.INTERVAL_MIN[i],
			        GeoAngle.INTERVAL_MAX[i]);
		}

		this.btnAngleInterval.getMyTable().updateText(
		        ImageOrText.convert(angleIntervalArray));

		this.btnTextSize.getMyTable()
		        .updateText(
		                ImageOrText.convert(app.getLocalization()
		                        .getFontSizeStrings()));

		// set labels for buttons with text e.g. button "bold" or "italic"
		this.btnBold.getDownFace().setText(app.getMenu("Bold.Short"));
		this.btnItalic.getDownFace().setText(app.getMenu("Italic.Short"));
		this.btnBold.getUpFace().setText(app.getMenu("Bold.Short"));
		this.btnItalic.getUpFace().setText(app.getMenu("Italic.Short"));
		// set labels for ToolTips
		setToolTips();
	}

	protected void setAxesAndGridToolTips(Localization loc) {
		btnShowGrid.setToolTipText(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxes.setToolTipText(loc.getPlainTooltip("stylebar.Axes"));
	}

	/**
	 * set tool tips
	 */
	protected void setToolTips() {

		Localization loc = app.getLocalization();

		setAxesAndGridToolTips(loc);
		btnStandardView.setToolTipText(loc
		        .getPlainTooltip("stylebar.ViewDefault"));
		btnLabelStyle.setToolTipText(loc.getPlainTooltip("stylebar.Label"));
		btnAngleInterval.setToolTipText(loc.getPlainTooltip("AngleBetween"));
		btnColor.setToolTipText(loc.getPlainTooltip("stylebar.Color"));
		btnBgColor.setToolTipText(loc.getPlainTooltip("stylebar.BgColor"));
		btnLineStyle.setToolTipText(loc.getPlainTooltip("stylebar.LineStyle"));
		btnPointStyle
		        .setToolTipText(loc.getPlainTooltip("stylebar.PointStyle"));
		btnTextColor.setToolTipText(loc.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(loc.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
		btnPointCapture.setToolTipText(loc.getPlainTooltip("stylebar.Capture"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));

		btnFixPosition.setToolTipText(loc
		        .getPlainTooltip("AbsoluteScreenLocation"));
		btnFixObject.setToolTipText(loc.getPlainTooltip("FixObject"));

	}

	public void reinit() {
		// TODO Auto-generated method stub

	}
}
