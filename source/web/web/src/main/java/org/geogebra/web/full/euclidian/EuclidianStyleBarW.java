package org.geogebra.web.full.euclidian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.geos.properties.BorderType;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.undo.UpdateStyleActionStore;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.color.BgColorPopup;
import org.geogebra.web.full.gui.color.BorderTextPopup;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.color.FillingStyleButton;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.BorderStylePopup;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PointStylePopup;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.StyleBarW2;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * StyleBar for euclidianView
 */
public class EuclidianStyleBarW extends StyleBarW2
		implements org.geogebra.common.euclidian.EuclidianStyleBar {

	private enum StyleBarMethod {
		NONE, UPDATE, UPDATE_STYLE
	}

	private final EuclidianController ec;
	protected EuclidianView ev;
	protected EuclidianStyleBarSelection selection;

	// flags and constants
	private boolean isIniting;
	private boolean modeChanged = true;
	private boolean firstPaint = true;

	protected ArrayList<GeoElement> activeGeoList;
	private boolean visible;

	// // buttons and lists of buttons
	private ColorPopupMenuButton btnBgColor;
	private ColorPopupMenuButton btnTextColor;
	private BgColorPopup btnTextBgColor;
	private PopupMenuButtonW btnTextSize;
	private PopupMenuButtonW btnLabelStyle;
	private PopupMenuButtonW btnAngleInterval;
	private PopupMenuButtonW btnShowGrid;
	protected PopupMenuButtonW btnPointCapture;
	protected PopupMenuButtonW btnChangeView;
	protected FillingStyleButton btnFilling;

	private ToggleButton btnShowAxes;
	private ToggleButton btnBold;
	private ToggleButton btnItalic;
	private ToggleButton btnUnderline;

	private BorderStylePopup btnBorderStyle;
	private BorderTextPopup btnBorderText;
	private PopupMenuButtonW btnHorizontalAlignment;
	private PopupMenuButtonW btnVerticalAlignment;

	private ToggleButton btnFixPosition;
	private ToggleButton btnFixObject;

	private ToggleButton[] toggleBtnList;
	private final ToggleButton[] btnDeleteSizes = new ToggleButton[3];
	private PopupMenuButtonW[] popupBtnList;

	private StyleBarMethod waitingOperation = StyleBarMethod.NONE;
	private final Localization loc;
	private @CheckForNull ContextMenuPopup btnContextMenu = null;
	private ToggleButton btnCrop;
	private LabelSettingsPopup btnLabel;
	private PopupMenuButtonW btnSegmentStartStyle;
	private PopupMenuButtonW btnSegmentEndStyle;

	/**
	 * @param ev
	 *            {@link EuclidianView}
	 * @param viewID
	 *            id of the panel
	 */
	public EuclidianStyleBarW(EuclidianView ev, int viewID) {
		super((AppW) ev.getApplication(), viewID);
		this.loc = ev.getApplication().getLocalization();
		isIniting = true;
		this.ev = ev;
		ec = ev.getEuclidianController();
		// init handling of default geos
		selection = new EuclidianStyleBarSelection(app, ec);
		createDefaultMap();

		initGUI();
		isIniting = false;
		setMode(ev.getMode()); // this will also update the stylebar
		setToolTips();

		setOptionType();
	}

	protected void setOptionType() {
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
		// overridden in 3d
	}

	/**
	 * 
	 * @return euclidian view attached
	 */
	public EuclidianView getView() {
		return ev;
	}

	@Override
	public void updateButtonPointCapture(int captureMode) {
		if (captureMode > 3) {
			return;
		}
		int captureModeIndex = captureMode;
		if (captureModeIndex == 3 || captureModeIndex == 0) {
			captureModeIndex = 3 - captureModeIndex; // swap 0 and 3
		}
		btnPointCapture.setSelectedIndex(captureModeIndex);
	}

	@Override
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

		PopupMenuButtonW.hideCurrentPopup();

		updateStyleBar();
	}

	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(ev.getViewID());
	}

	@Override
	public void restoreDefaultGeo() {
		selection.restoreDefaultGeoFromConstruction();
	}

	@Override
	public void setOpen(boolean visible) {
		this.visible = visible;
		if (visible) {
			switch (this.waitingOperation) {
			default:
				// do nothing
				break;
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

	protected boolean hasVisibleGeos(ArrayList<GeoElement> geoList) {
		for (GeoElement geo : geoList) {
			if (isVisibleInThisView(geo) && geo.isEuclidianVisible()
					&& !geo.isAxis()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	@Override
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
		activeGeoList = new ArrayList<>();

		// -----------------------------------------------------
		// MODE_MOVE case: load activeGeoList with all selected geos
		// -----------------------------------------------------
		if (EuclidianConstants.isMoveOrSelectionMode(mode)) {
			SelectionManager selection = ev.getApplication()
					.getSelectionManager();

			if (selection.getFocusedGroupElement() != null) {
				activeGeoList.add(selection.getFocusedGroupElement());
			} else if (hasVisibleGeos(selection.getSelectedGeos())
					|| hasVisibleGeos(ec.getJustCreatedGeos())) {
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
		else if (selection.getDefaultMap().containsKey(mode)
				|| EuclidianView.isPenMode(mode)) {
			// Save the current default geo state in oldDefaultGeo.
			// Stylebar buttons can temporarily change a default geo, but this
			// default
			// geo is always restored to its previous state after a mode change.
			if (modeChanged) {
				selection.restoreConstructionDefaults();
			}
			selection.updateDefaultsForMode(mode);
			if (btnContextMenu != null) {
				btnContextMenu.hideMenu();
			}
			activeGeoList = selection.getDefaultGeos();

			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}
		updateButtons();
		// show the pen delete button
		// TODO: handle pen mode in code above
		// btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		addButtons();
	}

	protected void updateButtons() {
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		if (activeGeoList == null) {
			return;
		}

		ArrayList<GeoElement> geos;
		if (app.isUnbundledOrWhiteboard()) {
			if (!isDynamicStylebar()
					&& (this.getView() instanceof EuclidianViewW)
					&& app.isUnbundledOrWhiteboard()) {
				// in view stylebar won't be appeared object stylebar
				geos = new ArrayList<>();
			} else if (!isDynamicStylebar()
					&& (this.getView() instanceof EuclidianView3DInterface)
					&& (!EuclidianConstants
							.isMoveOrSelectionMode(app.getMode()))
					&& (app.getMode() != EuclidianConstants.MODE_PEN)
					&& app.isUnbundledOrWhiteboard()) {
				// show the object stylebar in 3D view, when the user selects a
				// tool
				geos = activeGeoList;
			} else {
				ArrayList<GeoElement> geoList = new ArrayList<>();
				for (GeoElement geo0 : activeGeoList) {
					if (geo0.isEuclidianVisible()) {
						geoList.add(geo0);
					}
				}
				geos = geoList;
			}
		} else {
			geos = activeGeoList;
		}
		boolean hasButtons = !isFocusedGroupElement(); // at least context menu
		for (PopupMenuButtonW popupButton : popupBtnList) {
			if (popupButton != null) { // null pointer fix until necessary
				popupButton.update(geos);
				hasButtons |= popupButton.isVisible();
			}
		}
		for (ToggleButton toggleButton : toggleBtnList) {
			if (toggleButton != null) { // null pointer fix until necessary
				toggleButton.update(geos);
				hasButtons |= toggleButton.isVisible();
			}
		}
		if (!hasButtons) {
			setVisible(false);
		}
	}

	@Override
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
		addButtons();
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
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
		if (btnBorderText != null) {
			add(btnBorderText);
		}

		// add color and style buttons
		if (btnTextBgColor != null) {
			add(btnTextBgColor);
		}
		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);
		if (btnFilling != null) {
			add(btnFilling);
		}
		add(btnLineStyle);
		add(btnSegmentStartStyle);
		add(btnSegmentEndStyle);
		add(btnPointStyle);
		if (app.isWhiteboardActive()) {
			// update language of descriptions in color, line style and point
			// style dialogs
			btnColor.setLabels();
			btnLineStyle.setLabels();
			btnPointStyle.setLabels();
		}
		if (app.isUnbundledOrWhiteboard()) {
			// order of button changed
			add(btnTextSize);
		}

		// add text decoration buttons
		if (btnBold.isVisible() && !app.isUnbundledOrWhiteboard()) {
			addSeparator();
		}

		add(btnBold);
		add(btnItalic);
		if (app.isWhiteboardActive()) {
			add(btnUnderline);

			add(btnHorizontalAlignment);
			add(btnVerticalAlignment);
			add(btnBorderStyle);
		}

		if (!app.isUnbundledOrWhiteboard()) {
			add(btnTextSize);
		}

		add(btnAngleInterval);
		add(btnLabelStyle);

		if (btnFixPosition.isVisible() || btnFixObject.isVisible()) {
			addSeparator();
		}

		add(btnFixPosition);
		add(btnFixObject);

		for (int i = 0; i < 3; i++) {
			add(btnDeleteSizes[i]);
		}

		if (!app.isUnbundledOrWhiteboard() || !isDynamicStylebar()) {
			addMenuButton();
		}

		if (!app.isUnbundledOrWhiteboard()) {
			if (getViewButton() == null) {
				addViewButton();
			} else {
				add(getViewButton());
			}
		}

		if (!app.isWhiteboardActive() && isDynamicStylebar()) {
			add(getLabelPopup());
		}

		if (app.isWhiteboardActive() && isImageGeoSelected()
				&& ev.getMode() != EuclidianConstants.MODE_SELECT) {
			addCropButton();
		}

		if (app.isUnbundledOrWhiteboard()) {
			addDeleteButton();

			if (hasActiveGeos() && !isBackground() && !isMaskSelectedInGroup()) {
				addContextMenuButton();
			}
		}
	}

	private boolean isMaskSelectedInGroup() {
		return isFocusedGroupElement()
				&& app.getSelectionManager().getFocusedGroupElement().isMask();
	}

	private GeoElementND getFirstGeo() {
		return ev.getEuclidianController().getAppSelectedGeos().get(0);
	}

	private boolean isImageGeoSelected() {
		return ev.getEuclidianController().getAppSelectedGeos().size() == 1
				&& getFirstGeo().isGeoImage();
	}

	private boolean hasActiveGeos() {
		return !ev.getEuclidianController().getAppSelectedGeos().isEmpty();
	}

	private void addCropButton() {
		if (btnCrop == null) {
			btnCrop = new ToggleButton(MaterialDesignResources.INSTANCE.crop_black()) {
				@Override
				public void update(List<GeoElement> geos) {
					setEnabled(true);
					for (GeoElement geo : geos) {
						if (geo.isLocked()) {
							setEnabled(false);
						}
					}
				}
			};
			btnCrop.addStyleName("btnCrop");
			btnCrop.addFastClickHandler((e) ->
					toggleCrop(btnCrop.isSelected()));
		}

		btnCrop.setSelected(ev.getBoundingBox() != null
				&& ev.getBoundingBox().isCropBox());
		add(btnCrop);
	}

	private void toggleCrop(boolean val) {
		if (btnCrop != null) {
			ev.getEuclidianController().updateBoundingBoxFromSelection(val);
			ev.repaintView();
			updateStyleBar();
		}
	}

	/**
	 * add delete button to dynamic stylebar
	 */
	private void addDeleteButton() {
		if (isFocusedGroupElement()) {
			return;
		}

		StandardButton btnDelete = new StandardButton(
				MaterialDesignResources.INSTANCE.delete_black(), null, 24);
		btnDelete.setStyleName("IconButton");
		btnDelete.addFastClickHandler((widget) ->
				app.splitAndDeleteSelectedObjects());
		add(btnDelete);
	}

	protected void closeLabelPopup() {
		if (btnLabel != null && btnLabel.getMyPopup().isShowing()) {
			btnLabel.getMyPopup().hide();
		}
	}

	// For unbundled apps: three dot button instead of view dropdown
	private void addContextMenuButton() {
		if (btnContextMenu == null) {
			ContextMenuGeoElementW contextMenu = ((GuiManagerW) app.getGuiManager())
					.getPopupMenu(ec.getAppSelectedGeos());
			btnContextMenu = new ContextMenuPopup(app, contextMenu.getWrappedPopup()) {

				@Override
				public void updatePopup() {
					contextMenu.update();
				}
			};
		}
		btnContextMenu.addStyleName("matDynStyleContextButton");
		add(btnContextMenu);
	}

	private boolean isFocusedGroupElement() {
		return app.getSelectionManager().getFocusedGroupElement() != null;
	}

	protected int getContextMenuButtonWidth() {
		return btnContextMenu == null ? 0 : btnContextMenu.getOffsetWidth();
	}

	/*
	 * Some style button removed from dynamic stylebar. Those will be shown in
	 * the default stylebar yet.
	 */
	private boolean showAllStyleButtons() {
		return !app.isUnbundledOrWhiteboard() || (!isDynamicStylebar()
				&& !(this.getView() instanceof EuclidianView3DInterface));
	}

	protected boolean isDynamicStylebar() {
		return false;
	}

	protected boolean isBackground() {
		return btnShowGrid != null && btnShowGrid.isVisible();
	}

	/**
	 * add axes, grid, ... buttons
	 */
	private void addGraphicsDecorationsButtons() {
		addAxesAndGridButtons();
		addChangeViewButtons();
		addBtnRotateView();
	}

	/**
	 * add axes and grid buttons
	 */
	protected void addAxesAndGridButtons() {
		add(btnShowAxes);

		if (mode != EuclidianConstants.MODE_ERASER) {
			add(btnShowGrid);
		}
	}

	/**
	 * add standard view, show all objects, etc. buttons
	 */
	private void addChangeViewButtons() {
		add(btnChangeView);
	}

	/**
	 * add automatic rotate 3D view button
	 */
	protected void addBtnRotateView() {
		// used in 3D
	}

	protected ToggleButton getAxesOrGridToggleButton() {
		return btnShowAxes;
	}

	protected PopupMenuButtonW getAxesOrGridPopupMenuButton() {
		return btnShowGrid;
	}

	protected ToggleButton[] newToggleBtnList() {
		return new ToggleButton[] { getAxesOrGridToggleButton(), btnBold,
				btnItalic, btnUnderline, btnFixPosition, btnFixObject, btnDeleteSizes[0],
				btnDeleteSizes[1], btnDeleteSizes[2], btnCrop };
	}

	protected PopupMenuButtonW[] newPopupBtnList() {
		return new PopupMenuButtonW[] { getAxesOrGridPopupMenuButton(), btnSegmentStartStyle,
				btnSegmentEndStyle, btnBorderText, btnColor, btnBgColor, btnTextColor,
				btnTextBgColor, btnFilling, btnLineStyle, btnPointStyle, btnTextSize,
				btnAngleInterval, btnBorderStyle, btnHorizontalAlignment, btnVerticalAlignment,
				btnLabelStyle, btnPointCapture, btnChangeView
		};
	}

	// =====================================================
	// Create Buttons
	// =====================================================

	protected void createButtons() {
		// TODO: fill in
		createAxesAndGridButtons();
		createLineStyleBtn();
		createSegmentStartStyleBtn();
		createSegmentEndStyleBtn();
		createPointStyleBtn(mode);
		createLabelStyleBtn();
		createAngleIntervalBtn();
		createPointCaptureBtn();
		createDeleteSiztBtn();

		createColorBtn();
		if (app.isWhiteboardActive()) {
			createFillingBtn();
			createTextBorderColorBtn();
			createTextBgColorBtn();
		}
		createBgColorBtn();
		createTextColorBtn();
		createTextBoldBtn();
		createTextItalicBtn();
		createTextUnderlineBtn();
		createTableBorderStyleBtn();
		createTableHorizontalAlignmentBtn();
		createTableVerticalAlignmentBtn();
		createFixPositionBtn();
		createFixObjectBtn();
		createTextSizeBtn();
		createChangeViewButtons();
	}

	private LabelSettingsPopup getLabelPopup() {
		if (btnLabel == null) {
			btnLabel = new LabelSettingsPopup(app);
		}

		return btnLabel;
	}

	public class ProjectionPopup extends PopupMenuButtonW {

		/**
		 * @param app
		 *            application
		 * @param projectionIcons
		 *            icons
		 */
		public ProjectionPopup(AppW app, ImageOrText[] projectionIcons) {
			super(app, projectionIcons, 1, projectionIcons.length,
					SelectionTable.MODE_ICON);
		}

		@Override
		public void update(List<GeoElement> geos) {
			if (app.isUnbundledOrWhiteboard()) {
				super.setVisible(geos.size() == 0);
			} else {
				super.setVisible(geos.size() == 0
						&& mode != EuclidianConstants.MODE_PEN);
			}
		}
	}

	protected void createChangeViewButtons() {
		ImageOrText[] directionIcons = ImageOrText
				.convert(
						new SVGResource[] {
								MaterialDesignResources.INSTANCE.home_black(),
								MaterialDesignResources.INSTANCE.show_all_objects_black() },
						24);

		btnChangeView = new ProjectionPopup(app, directionIcons);
		btnChangeView.setFixedIcon(
				new ImageOrText(MaterialDesignResources.INSTANCE.home_black(), 24));
		setPopupHandlerWithUndoPoint(btnChangeView, this::processChangeView);
	}

	protected void createAxesAndGridButtons() {
		// ========================================
		// show axes button
		btnShowAxes = new ToggleButtonWforEV(
				MaterialDesignResources.INSTANCE.axes_black(), this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		addFastClickHandlerWithUndoPoint(btnShowAxes,
				geos -> EuclidianStyleBarStatic.processAxes(getView()));

		// ========================================
		// show grid button
		ImageOrText[] grids = new ImageOrText[4];
		for (int i = 0; i < 4; i++) {
			grids[i] = GeoGebraIconW
					.createGridStyleIcon(EuclidianView.getPointStyle(i));
		}
		btnShowGrid = new GridPopup(app, grids, ev);
		setPopupHandlerWithUndoPoint(btnShowGrid, this::handleGrid);
	}

	private void createDeleteSiztBtn() {
		SVGResource[] delBtns = new SVGResource[] {
				MaterialDesignResources.INSTANCE.delete_small(),
				MaterialDesignResources.INSTANCE.delete_medium(),
				MaterialDesignResources.INSTANCE.delete_large() };
		for (int i = 0; i < 3; i++) {
			btnDeleteSizes[i] = new ToggleButton(delBtns[i]) {

				@Override
				public void update(List<GeoElement> geos) {
					// always show this button unless in pen mode
					super.setVisible(mode == EuclidianConstants.MODE_DELETE
							|| mode == EuclidianConstants.MODE_ERASER);
				}
			};
			final int index = i;
			btnDeleteSizes[i].addFastClickHandler(w -> setDelSize(index));
		}
	}

	private void createPointCaptureBtn() {
		ImageOrText[] strPointCapturing = ImageOrText.convert(new String[] {
				loc.getMenu("Labeling.automatic"), loc.getMenu("SnapToGrid"),
				loc.getMenu("FixedToGrid"), loc.getMenu("Off") });
		btnPointCapture = new PopupMenuButtonW(app, strPointCapturing, -1, 1,
				SelectionTable.MODE_TEXT) {

			@Override
			public void update(List<GeoElement> geos) {
				if (app.isUnbundledOrWhiteboard()) {
					super.setVisible(geos.size() == 0);
				} else {
					// same as axes
					super.setVisible(
							geos.size() == 0 && !EuclidianView.isPenMode(mode)
									&& mode != EuclidianConstants.MODE_DELETE
									&& mode != EuclidianConstants.MODE_ERASER);
				}
			}
		};

		// it is not needed, must be an Image preloaded like others.
		SVGResource ptCaptureIcon = MaterialDesignResources.INSTANCE.snap_to_grid();
		btnPointCapture.setFixedIcon(new ImageOrText(ptCaptureIcon, 24));
		setPopupHandlerWithUndoPoint(btnPointCapture,
				geos -> EuclidianStyleBarStatic.processPointCapture(getView()));
		btnPointCapture.setKeepVisible(false);
	}

	private void createSegmentStartStyleBtn() {
		ImageOrText[] segmentStartStyleIcons = GeoGebraIconW.createSegmentStartStyleIcons();
		btnSegmentStartStyle = new PopupMenuButtonW(app, segmentStartStyleIcons,
				2, 4, SelectionTable.MODE_ICON) {
			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoSegment(geos);
				super.setVisible(geosOK);

				if (geosOK) {
					SegmentStyle style = ((GeoSegment) getFirst(geos)).getStartStyle();
					btnSegmentStartStyle.setSelectedIndex(style == null ? -1 : style.ordinal());
					if (btnSegmentStartStyle.getSelectedIndex() == -1) {
						btnSegmentStartStyle.setIcon(segmentStartStyleIcons[0]);
					}
				}
			}
		};

		btnSegmentStartStyle.setIcon(segmentStartStyleIcons[0]);
		setPopupHandlerWithUndoAction(btnSegmentStartStyle, this::handleSegmentStart);
		btnSegmentStartStyle.setKeepVisible(false);
	}

	private boolean handleSegmentStart(ArrayList<GeoElement> targetGeos) {
		SegmentStyle segmentStyle
				= SegmentStyle.values()[btnSegmentStartStyle.getSelectedIndex()];
		return applySegmentStartStyle(targetGeos, segmentStyle, true);
	}

	private boolean handleSegmentEnd(ArrayList<GeoElement> targetGeos) {
		SegmentStyle segmentStyle
				= SegmentStyle.values()[btnSegmentEndStyle.getSelectedIndex()];
		return applySegmentStartStyle(targetGeos, segmentStyle, false);
	}

	private void createSegmentEndStyleBtn() {
		ImageOrText[] segmentEndStyleIcons = GeoGebraIconW.createSegmentEndStyleIcons();
		btnSegmentEndStyle = new PopupMenuButtonW(app, segmentEndStyleIcons,
				2, 4, SelectionTable.MODE_ICON) {
			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoSegment(geos);
				super.setVisible(geosOK);

				if (geosOK) {
					SegmentStyle style = ((GeoSegment) getFirst(geos)).getEndStyle();
					btnSegmentEndStyle.setSelectedIndex(style == null ? -1 : style.ordinal());
					if (btnSegmentEndStyle.getSelectedIndex() == -1) {
						btnSegmentEndStyle.setIcon(segmentEndStyleIcons[0]);
					}
				}
			}
		};

		btnSegmentEndStyle.setIcon(segmentEndStyleIcons[0]);
		setPopupHandlerWithUndoAction(btnSegmentEndStyle, this::handleSegmentEnd);
		btnSegmentEndStyle.setKeepVisible(false);
	}

	private void createLabelStyleBtn() {
		ImageOrText[] captionArray = ImageOrText
				.convert(new String[] { loc.getMenu("stylebar.Hidden"), // index
						// 4
						loc.getMenu("Name"), // index 0
						loc.getMenu("NameAndValue"), // index 1
						loc.getMenu("Value"), // index 2
						loc.getMenu("Caption") // index 3
		});

		btnLabelStyle = new PopupMenuButtonW(app, captionArray, -1, 1,
				SelectionTable.MODE_TEXT) {

			@Override
			public void update(List<GeoElement> geos) {
				GeoElement geo = EuclidianStyleBarStatic
						.checkGeosForCaptionStyle(geos);
				boolean geosOK = geo != null && showAllStyleButtons();
				super.setVisible(geosOK);
				if (geosOK) {
					setSelectedIndex(EuclidianStyleBarStatic
							.getIndexForLabelMode(geo, app));
				}
			}
		};
		SVGResource ic = ToolbarSvgResourcesSync.INSTANCE.mode_showhidelabel_32();
		// must be done with callback btnLabelStyle.setIcon(ic);
		btnLabelStyle.setFixedIcon(new ImageOrText(ic, 24));
		setPopupHandlerWithUndoPoint(btnLabelStyle, this::handleLabelStyle);
		btnLabelStyle.setKeepVisible(false);
	}

	private boolean handleLabelStyle(ArrayList<GeoElement> targetGeos) {
		return EuclidianStyleBarStatic.applyCaptionStyle(targetGeos,
				mode, btnLabelStyle.getSelectedIndex());
	}

	private void createAngleIntervalBtn() {
		String[] angleIntervalString = new String[GeoAngle
				.getIntervalMinListLength() - 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervalString[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		ImageOrText[] angleIntervalArray = ImageOrText
				.convert(angleIntervalString);

		btnAngleInterval = new PopupMenuButtonW(app, angleIntervalArray, -1, 1,
				SelectionTable.MODE_TEXT) {

			@Override
			public void update(List<GeoElement> geos) {
				GeoElement geo = EuclidianStyleBarStatic
						.checkGeosForAngleInterval(geos);
				boolean geosOK = geo != null
						&& !app.isUnbundledOrWhiteboard();
				super.setVisible(geosOK);
				if (geosOK) {
					setSelectedIndex(((AngleProperties) geo).getAngleStyle()
							.getXmlVal());
				}
			}
		};
		ImageOrText icon = new ImageOrText(
				MaterialDesignResources.INSTANCE.stylingbar_angle_interval(), 24);
		btnAngleInterval.setFixedIcon(icon);
		setPopupHandlerWithUndoAction(btnAngleInterval, this::handleAngleInterval);
		btnAngleInterval.setKeepVisible(false);
	}

	private boolean handleAngleInterval(ArrayList<GeoElement> targetGeos) {
		return EuclidianStyleBarStatic.applyAngleInterval(targetGeos,
				btnAngleInterval.getSelectedIndex());
	}

	/**
	 * set EV to standard view
	 */
	protected void setEvStandardView() {
		getView().setStandardView(true);
	}

	private FillType[] getFillTypes() {
		Set<FillType> wantedTypes = new HashSet<>(Arrays.asList(FillType.STANDARD, FillType.HATCH,
				FillType.DOTTED, FillType.CROSSHATCHED, FillType.HONEYCOMB));
		Set<FillType> availableTypes = app.getConfig().getAvailableFillTypes();
		wantedTypes.retainAll(availableTypes);
		return wantedTypes.toArray(new FillType[0]);
	}

	private void createFillingBtn() {
		btnFilling = new FillingStyleButton(app, getFillTypes()) {
			@Override
			public void update(List<GeoElement> geos) {

				if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE) {
					Log.debug(
							"MODE_FREEHAND_SHAPE not working in StyleBar yet");
				} else {
					boolean geosOK = geos.size() > 0 || (EuclidianView
							.isPenMode(mode)
							&& !app.isUnbundledOrWhiteboard());
					for (GeoElement geoElement : geos) {
						GeoElement geo = geoElement
								.getGeoElementForPropertiesDialog();
						if (geo instanceof GeoText || geo instanceof GeoButton
								|| geo instanceof GeoInline
								|| geo instanceof GeoPoint
								|| geo instanceof GeoLocusStroke
								|| geo instanceof GeoWidget
								|| geo instanceof GeoLine
								|| geo.isGeoImage()) {
							geosOK = false;
							break;
						}
					}
					super.setVisible(geosOK);

					if (geosOK) {

						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha
						// value
						// double alpha = 1.0;
						boolean hasFillable = false;
						boolean alphaOnly = false;
						FillType fillType = null;
						for (GeoElement geo : geos) {
							if (geo.isFillable()) {
								alphaOnly = geo.isAngle() || geo.isGeoImage();
								hasFillable = true;
								fillType = geo.getFillType();
								break;
							}
							if (geo instanceof GeoPolyLine
									&& EuclidianView.isPenMode(mode)) {
								hasFillable = true;
								break;
							}
						}

						setTitle(loc.getMenu("stylebar.Filling"));

						boolean enableFill = hasFillable && !alphaOnly;
						super.setVisible(enableFill);
						setFillEnabled(enableFill);
						if (enableFill) {
							setFillType(fillType);
						}

						this.setKeepVisible(
								EuclidianConstants.isMoveOrSelectionMode(mode));
					}
				}
			}

			@Override
			public void onClickAction() {
				if (btnCrop != null) {
					btnCrop.setSelected(false);
					toggleCrop(false);
				}
			}
		};
		btnFilling.setFixedIcon(btnFilling.getButtonIcon());
		setPopupHandlerWithUndoAction(btnFilling, this::handleFilling);
	}

	private boolean handleFilling(ArrayList<GeoElement> targetGeos) {
		FillType fillType = btnFilling.getSelectedFillType();
		return EuclidianStyleBarStatic.applyFillType(targetGeos, fillType);
	}

	private void createBgColorBtn() {
		btnBgColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_BGCOLOR, false) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeos(geos, geo -> geo instanceof TextProperties);
				super.setVisible(geosOK);

				if (geosOK) {
					// get color from first geo
					GColor geoColor;
					geoColor = geos.get(0).getBackgroundColor();
					double alpha = 1.0;
					updateColorTable();

					// find the geoColor in the table and select it
					int index = getColorIndex(geoColor);
					setSelectedIndex(index);
					setDefaultColor(alpha, geoColor);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						setIcon(GeoGebraIconW.createColorSwatchIcon(alpha,
								geoColor, null));
					}
				}
			}
		};

		btnBgColor.setEnableTable(true);
		btnBgColor.setKeepVisible(!app.isUnbundledOrWhiteboard());
		setPopupHandlerWithUndoAction(btnBgColor, this::handleBackgroundColor);
	}

	private boolean handleBackgroundColor(ArrayList<GeoElement> targetGeos) {
		if (btnBgColor.getSelectedIndex() >= 0) {
			GColor color = btnBgColor.getSelectedColor();
			if (color == null) {
				openColorChooser(targetGeos, true);
				return false;
			}
			double alpha = btnBgColor.getSliderValue() / 100.0;
			return EuclidianStyleBarStatic.applyBgColor(targetGeos,
					color, alpha);
		}
		return false;
	}

	private void createTextBorderColorBtn() {
		btnBorderText = new BorderTextPopup(app, ColorPopupMenuButton.COLORSET_DEFAULT,
				selection) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeos(geos, geo
						-> geo instanceof GeoInlineText || geo instanceof GeoMindMapNode);
				super.setVisible(geosOK);

				if (geosOK) {
					int borderThickness = geos.get(0).getLineThickness();
					btnBorderText.selectBorderThickness(borderThickness);
				}
			}
		};
		btnBorderText.setFixedIcon(new ImageOrText(
				MaterialDesignResources.INSTANCE.color_border(), 24));
		btnBorderText.setEnableTable(true);
		setPopupHandlerWithUndoAction(btnBorderText, this::handleBorderText);
	}

	private boolean handleBorderText(ArrayList<GeoElement> targetGeos) {
		if (btnBorderText.getSelectedIndex() >= 0) {
			GColor color = btnBorderText.getSelectedColor();
			if (color == null) {
				handleBorderColorChooser(targetGeos);
				return false;
			}
			return applyBorderColorText(targetGeos, color);
		}
		return false;
	}

	private void createTextBgColorBtn() {
		btnTextBgColor = new BgColorPopup(app, ColorPopupMenuButton.COLORSET_DEFAULT,
				selection) {

			@Override
			public void update(List<GeoElement> geos) {
				super.setVisible(checkGeos(geos,
						geo -> (geo instanceof TextStyle && !geo.isGeoInputBox())
								|| geo instanceof GeoFormula));
			}
		};
		btnTextBgColor.setFixedIcon(new ImageOrText(
				MaterialDesignResources.INSTANCE.color_black(), 24));
		btnTextBgColor.setEnableTable(true);
		setPopupHandlerWithUndoAction(btnTextBgColor, this::handleTextBackground);
	}

	private boolean handleTextBackground(ArrayList<GeoElement> targetGeos) {
		if (btnTextBgColor.getSelectedIndex() >= 0) {
			GColor color = btnTextBgColor.getSelectedColor();
			if (color == null) {
				openColorChooser(targetGeos, true);
				return false;
			}
			return EuclidianStyleBarStatic.applyBgColor(targetGeos,
					color, 1);
		}
		return false;
	}

	private void createTextColorBtn() {
		btnTextColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeos(geos, EuclidianStyleBarW.this::hasTextColor);
				super.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = geos.get(0)
							.getGeoElementForPropertiesDialog();
					GColor geoTextColor = geo.getObjectColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoTextColor);
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
				return new ImageOrText(
						MaterialDesignResources.INSTANCE.text_color(), 24);
			}
		};
		btnTextColor.setEnableTable(true);
		btnTextColor.addStyleName("btnTextColor");
		setPopupHandlerWithUndoAction(btnTextColor, this::handleTextColor);
	}

	private boolean handleTextColor(ArrayList<GeoElement> targetGeos) {
		if (btnTextColor.getSelectedIndex() >= 0) {
			GColor color = btnTextColor.getSelectedColor();
			if (color == null) {
				openColorChooser(targetGeos, false);
				return false;
			}
			return applyColor(targetGeos, color, 1);
		}
		return false;
	}

	private boolean checkTextNoInputBox(List<GeoElement> geos) {
		return checkGeos(geos, geo -> geo instanceof TextStyle && !geo.isGeoInputBox());
	}

	private void createTextBoldBtn() {
		btnBold = new ToggleButton(MaterialDesignResources.INSTANCE.text_bold_black()) {
			@Override
			public void update(List<GeoElement> geos) {
					updateFontToggle(btnBold, GFont.BOLD, geos);
				}
		};
		btnBold.addStyleName("btnBold");
		addFastClickHandlerWithUndoAction(btnBold, this::handleBold);
	}

	private boolean handleBold(ArrayList<GeoElement> targetGeos) {
		return applyFontStyle(targetGeos, GFont.BOLD, btnBold.isSelected());
	}

	protected void addFastClickHandlerWithUndoPoint(ToggleButton btn,
			Function<ArrayList<GeoElement>, Boolean> action) {
		btn.addFastClickHandler(ignore -> processSelectionWithUndo(action));
	}

	protected void addFastClickHandlerWithUndoAction(ToggleButton btn,
			Function<ArrayList<GeoElement>, Boolean> action) {
		btn.addFastClickHandler(ignore -> processSelectionWithUndoAction(action));
	}

	private void updateFontToggle(ToggleButton btn, int mask, List<GeoElement> geos) {
		boolean geosOK = checkTextNoInputBox(geos);
		btn.setVisible(geosOK);
		if (geosOK) {
			int style = EuclidianStyleBarStatic.getFontStyle(geos);
			btn.setSelected((style & mask) != 0);
		}
	}

	private void createFixPositionBtn() {
		btnFixPosition = new ToggleButton(
				MaterialDesignResources.INSTANCE.pin_black()) {

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = EuclidianStyleBarStatic
						.checkGeosForFixPosition(geos) && showAllStyleButtons();
				super.setVisible(geosOK);
				if (geosOK) {
					setSelected(EuclidianStyleBarStatic
							.checkSelectedFixPosition(geos.get(0)));
				}
			}
		};
		addFastClickHandlerWithUndoAction(btnFixPosition, this::handleFixPosition);
	}

	private boolean handleFixPosition(ArrayList<GeoElement> targetGeos) {
		return EuclidianStyleBarStatic.applyFixPosition(targetGeos,
				btnFixPosition.isSelected(), ev) != null;
	}

	private void createFixObjectBtn() {
		btnFixObject = new ToggleButton(
				MaterialDesignResources.INSTANCE.lock_open_black(),
				MaterialDesignResources.INSTANCE.lock_black()) {

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = EuclidianStyleBarStatic
						.checkGeosForFixObject(geos) && showAllStyleButtons();
				super.setVisible(geosOK);
				if (geosOK) {
					boolean isSelected = EuclidianStyleBarStatic
							.checkSelectedFixObject(geos.get(0));
					setSelected(isSelected);
					Dom.toggleClass(this, "selected", isSelected);
				}
			}
		};
		addFastClickHandlerWithUndoAction(btnFixObject, this::handleFixObject);
	}

	private boolean handleFixObject(ArrayList<GeoElement> targetGeos) {
		boolean needUndo = EuclidianStyleBarStatic.applyFixObject(targetGeos,
				btnFixObject.isSelected(), ev) != null;
		btnFixObject.update(targetGeos);
		return needUndo;
	}

	private void createTextItalicBtn() {
		btnItalic = new ToggleButton(MaterialDesignResources.INSTANCE.text_italic_black()) {

			@Override
			public void update(List<GeoElement> geos) {
					updateFontToggle(btnItalic, GFont.ITALIC, geos);
				}
		};
		btnItalic.addStyleName("btnItalic");
		addFastClickHandlerWithUndoAction(btnItalic, this::handleItalic);
	}

	private boolean handleItalic(ArrayList<GeoElement> targetGeos) {
		return applyFontStyle(targetGeos, GFont.ITALIC, btnItalic.isSelected());
	}

	private void createTextUnderlineBtn() {
		btnUnderline = new ToggleButton(MaterialDesignResources.INSTANCE.text_underline_black()) {

			@Override
			public void update(List<GeoElement> geos) {
				updateFontToggle(btnUnderline, GFont.UNDERLINE, geos);
			}
		};

		btnUnderline.addStyleName("btnUnderline");
		addFastClickHandlerWithUndoAction(btnUnderline, this::handleUnderline);
	}

	private boolean handleUnderline(ArrayList<GeoElement> targetGeos) {
		return applyFontStyle(targetGeos, GFont.UNDERLINE, btnUnderline.isSelected());
	}

	private ImageOrText getImgResource(SVGResource src) {
		return new ImageOrText(src, 24);
	}

	private void createTableBorderStyleBtn() {
		MaterialDesignResources resources = MaterialDesignResources.INSTANCE;
		ImageOrText[] borderStyles = new ImageOrText[] { getImgResource(resources.border_all()),
				getImgResource(resources.border_inner()), getImgResource(resources.border_outer()),
				getImgResource(resources.border_clear()) };

		btnBorderStyle = new BorderStylePopup(app, borderStyles) {

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = checkGeoTable(geos);
				super.setVisible(geosOK);

				if (geosOK) {
					InlineTableController formatter = getTableFormatter(geos.get(0));

					BorderType border = formatter != null ? formatter.getBorderStyle()
							: BorderType.MIXED;
					setSelectedIndex(btnBorderStyle.getBorderTypeIndex(border));
					if (btnBorderStyle.getSelectedIndex() == -1) {
						btnBorderStyle.setIcon(new ImageOrText(
								MaterialDesignResources.INSTANCE.border_all(), 24));
					}
					int borderThickness = formatter != null ? formatter.getBorderThickness() : 1;
					btnBorderStyle.setBorderThickness(borderThickness);
				}
			}
		};
		setPopupHandlerWithUndoAction(btnBorderStyle, this::handleBorderStyle);
		btnBorderStyle.getBorderThicknessPopup().addPopupHandler(this);
		btnBorderStyle.setKeepVisible(false);
		btnBorderStyle.setIcon(new ImageOrText(
				MaterialDesignResources.INSTANCE.border_all(), 24));
		btnBorderStyle.addStyleName("withIcon");
	}

	private void createTableHorizontalAlignmentBtn() {
		MaterialDesignResources resources = MaterialDesignResources.INSTANCE;
		ImageOrText[] verticalAlignments = new ImageOrText[] {
				getImgResource(resources.horizontal_align_left()),
				getImgResource(resources.horizontal_align_center()),
				getImgResource(resources.horizontal_align_right()),
		};

		btnHorizontalAlignment = new PopupMenuButtonW(app, verticalAlignments, 1, 3,
				SelectionTable.MODE_ICON) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeos(geos, geo -> geo instanceof HasTextFormatter);
				super.setVisible(geosOK);

				if (geosOK) {
					HasTextFormat formatter
							= ((HasTextFormatter) getFirst(geos)).getFormatter();

					HorizontalAlignment alignment = formatter != null
							? formatter.getHorizontalAlignment() : null;
					setSelectedIndex(alignment != null ? alignment.ordinal() : -1);
					if (btnHorizontalAlignment.getSelectedIndex() == -1) {
						btnHorizontalAlignment.setIcon(new ImageOrText(
								MaterialDesignResources.INSTANCE.horizontal_align_left(), 24));
					}
				}
			}
		};
		btnHorizontalAlignment.addPopupHandler(idx -> handleHorizontalAlignment(getTargetGeos()));
		btnHorizontalAlignment.setKeepVisible(false);
		btnHorizontalAlignment.setIcon(new ImageOrText(
				MaterialDesignResources.INSTANCE.vertical_align_top(), 24));
		btnHorizontalAlignment.addStyleName("withIcon");
		btnHorizontalAlignment.getMyPopup().addStyleName("mowPopup");
	}

	private void handleHorizontalAlignment(ArrayList<GeoElement> targetGeos) {
		HorizontalAlignment alignment
				= HorizontalAlignment.values()[btnHorizontalAlignment.getSelectedIndex()];
		inlineFormatter.formatInlineText(targetGeos, (formatter) -> {
			if (alignment != null && !alignment.equals(formatter.getHorizontalAlignment())) {
				formatter.setHorizontalAlignment(alignment);
				return true;
			}

			return false;
		});
	}

	private InlineTableController getTableFormatter(GeoElement geoElement) {
		HasTextFormat formatter = ((GeoInlineTable) geoElement).getFormatter();
		return formatter == null ? null : (InlineTableController) formatter;
	}

	private void createTableVerticalAlignmentBtn() {
		MaterialDesignResources resources = MaterialDesignResources.INSTANCE;
		ImageOrText[] verticalAlignments = new ImageOrText[] {
				getImgResource(resources.vertical_align_top()),
				getImgResource(resources.vertical_align_middle()),
				getImgResource(resources.vertical_align_bottom()),
		};

		btnVerticalAlignment = new PopupMenuButtonW(app, verticalAlignments, 1, 3,
				SelectionTable.MODE_ICON) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeos(geos, geo -> geo instanceof HasTextFormatter);
				super.setVisible(geosOK);

				if (geosOK) {
					HasTextFormat formatter
							= ((HasTextFormatter) getFirst(geos)).getFormatter();

					VerticalAlignment alignment = formatter != null
							? formatter.getVerticalAlignment() : null;
					setSelectedIndex(alignment != null ? alignment.ordinal() : -1);
					if (btnVerticalAlignment.getSelectedIndex() == -1) {
						btnVerticalAlignment.setIcon(new ImageOrText(
								MaterialDesignResources.INSTANCE.vertical_align_top(), 24));
					}
				}
			}
		};
		btnVerticalAlignment.addPopupHandler(idx -> handleVerticalAlignment(getTargetGeos()));
		btnVerticalAlignment.setKeepVisible(false);
		btnVerticalAlignment.setIcon(new ImageOrText(
				MaterialDesignResources.INSTANCE.vertical_align_top(), 24));
		btnVerticalAlignment.addStyleName("withIcon");
		btnVerticalAlignment.getMyPopup().addStyleName("mowPopup");
	}

	private void handleVerticalAlignment(ArrayList<GeoElement> targetGeos) {
		VerticalAlignment alignment
				= VerticalAlignment.values()[btnVerticalAlignment.getSelectedIndex()];
		UpdateStyleActionStore store = new UpdateStyleActionStore(targetGeos, app.getUndoManager());
		inlineFormatter.formatInlineText(targetGeos, (formatter) -> {
			if (alignment != null && !alignment.equals(formatter.getVerticalAlignment())) {
				formatter.setVerticalAlignment(alignment);
				return true;
			}

			return false;
		});
		if (store.needUndo()) {
			store.storeUndo();
		}
	}

	private void createTextSizeBtn() {
		// ========================================
		// text size button
		ImageOrText[] textSizeArray = ImageOrText
				.convert(app.getLocalization().getFontSizeStrings());

		btnTextSize = new PopupMenuButtonW(app, textSizeArray, -1, 1,
				SelectionTable.MODE_TEXT) {

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = checkGeoText(geos);
				super.setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = geos.get(0)
							.getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText.getFontSizeIndex(
							((TextStyle) geo).getFontSizeMultiplier()));
				}
			}
		};
		setPopupHandlerWithUndoAction(btnTextSize, this::handleTextSize);
		btnTextSize.setKeepVisible(false);
		btnTextSize.setFixedIcon(new ImageOrText(
						MaterialDesignResources.INSTANCE.text_size_black(), 24));
		btnTextSize.addStyleName("withIcon");
		btnTextSize.getMyPopup().removeStyleName("matPopupPanel");
		btnTextSize.getMyPopup().addStyleName("textSizePopupPanel");
	}

	private boolean handleTextSize(ArrayList<GeoElement> targetGeos) {
		return applyTextSize(targetGeos, btnTextSize.getSelectedIndex());
	}

	// =====================================================
	// Event Handlers
	// =====================================================

	@Override
	public void updateGUI() {
		if (isIniting) {
			return;
		}
		updateButtonPointCapture(ev.getPointCapturingMode());

		updateAxesAndGridGUI();
	}

	protected void updateAxesAndGridGUI() {
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowGrid.setSelectedIndex(gridIndex(ev));
	}

	@Override
	protected ArrayList<GeoElement> getTargetGeos() {
		return selection.getGeos();
	}

	private static boolean checkGeoText(List<GeoElement> geos) {
		return checkGeos(geos, geo -> geo instanceof TextStyle);
	}

	private static boolean checkGeoTable(List<GeoElement> geos) {
		return checkGeos(geos, geo -> geo instanceof GeoInlineTable);
	}

	private static boolean checkGeoSegment(List<GeoElement> geos) {
		return checkGeos(geos, geo -> geo instanceof GeoSegment);
	}

	private Object getFirst(List<GeoElement> geos) {
		return geos.get(0).getGeoElementForPropertiesDialog();
	}

	private static boolean checkGeos(List<GeoElement> geos, Predicate<GeoElement> check) {
		boolean geosOK = geos.size() > 0;
		for (GeoElement geo : geos) {
			if (!check.test(geo.getGeoElementForPropertiesDialog())) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	private boolean handleGrid(ArrayList<GeoElement> ignored) {
		if (btnShowGrid.getSelectedValue() != null) {
			return setGridType(ev, btnShowGrid.getSelectedIndex());
		}
		return false;
	}

	protected boolean processChangeView(ArrayList<GeoElement> targetGeos) {
		int si = btnChangeView.getSelectedIndex();
		switch (si) {
		case 0: // standard view
			setEvStandardView();
			break;
		case 1: // show all objects
			getView().setViewShowAllObjects(true, false);
			break;
		default:
			setDirection(si);
			break;
		}
		return false;
	}

	/**
	 * Change border type and thickness
	 * @param targetGeos selected geos
	 * @return whether style changed
	 */
	public boolean handleBorderStyle(ArrayList<GeoElement> targetGeos) {
		return applyBorderStyle(targetGeos, btnBorderStyle.getBorderType(),
				btnBorderStyle.getBorderThickness());
	}

	private void handleBorderColorChooser(final ArrayList<GeoElement> targetGeos) {
		final GeoElement geo0 = targetGeos.get(0);
		GColor originalColor = ((GeoInline) geo0).getBorderColor();

		DialogManagerW dm = (DialogManagerW) (app.getDialogManager());
		dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {

			@Override
			public void onForegroundSelected() {
				// no foreground/background switcher
			}

			@Override
			public void onColorChange(GColor color) {
				boolean changed = applyBorderColorText(targetGeos, color);

				if (changed) {
					app.storeUndoInfo();
				}
			}

			@Override
			public void onClearBackground() {
				// no clear background button
			}

			@Override
			public void onBarSelected() {
				// no bar chart support
			}

			@Override
			public void onBackgroundSelected() {
				// no foreground / background switcher
			}

			@Override
			public void onAlphaChange() {
				// no alpha slider
			}
		});
	}

	/**
	 * @param targetGeos
	 *            geos to selected (non-tables are ignored)
	 * @param borderType
	 *            border type
	 * @param borderThickness
	 *            border line thickness
	 * @return whether border style changed
	 */
	private boolean applyBorderStyle(List<GeoElement> targetGeos,
			BorderType borderType, int borderThickness) {
		boolean changed = false;
		for (GeoElement geo : targetGeos) {
			if (geo instanceof GeoInlineTable) {
				InlineTableController formatter = getTableFormatter(geo);
				if (formatter == null) {
					continue;
				}

				if (borderType != null && !formatter.getBorderStyle().equals(borderType)) {
					formatter.setBorderStyle(borderType);
					changed = true;
				}
				if (formatter.getBorderThickness() != borderThickness) {
					formatter.setBorderThickness(borderThickness);
					changed = true;
				}
			}
		}

		return changed;
	}

	private boolean applySegmentStartStyle(List<GeoElement> targetGeos, SegmentStyle style,
			boolean start) {
		boolean changed = false;
		for (GeoElement geo : targetGeos) {
			if (geo instanceof GeoSegment) {
				if (start) {
					((GeoSegment) geo).setStartStyle(style);
				} else {
					((GeoSegment) geo).setEndStyle(style);
				}
				geo.updateVisualStyleRepaint(GProperty.COMBINED);
				changed = true;
			}
		}

		return changed;
	}

	/**
	 * @param targetGeos
	 *            geos to selected (non-inlinetexts are ignored)
	 * @param borderColor
	 *            border color
	 * @return whether border color changed
	 */
	private boolean applyBorderColorText(List<GeoElement> targetGeos, GColor borderColor) {
		boolean changed = false;
		for (GeoElement geo : targetGeos) {
			if (geo instanceof GeoInline) {
				GeoInline text = (GeoInline) geo;
				if (borderColor != null && !text
						.getBorderColor().equals(borderColor)) {
					text.setBorderColor(borderColor);
					geo.updateVisualStyle(GProperty.LINE_STYLE);
					changed = true;
				}
			}
		}

		return changed;
	}

	private boolean applyTextSize(ArrayList<GeoElement> targetGeos,
			int selectedIndex) {
		boolean ret = EuclidianStyleBarStatic.applyTextSize(targetGeos, selectedIndex);
		double size = GeoText.getRelativeFontSize(selectedIndex)
				* ev.getFontSize();
		inlineFormatter.formatInlineText(targetGeos, "size", size);
		return ret;
	}

	private boolean applyFontStyle(ArrayList<GeoElement> targetGeos, int mask,
			boolean add) {
		boolean ret = EuclidianStyleBarStatic.applyFontStyle(targetGeos, mask,
				add);
		String property = mask == GFont.BOLD ? "bold" : (mask == GFont.ITALIC ? "italic"
				: "underline");
		inlineFormatter.formatInlineText(targetGeos, property, add);
		return ret;
	}

	/**
	 * For 3D
	 * 
	 * @param si
	 *            direction
	 */
	protected void setDirection(int si) {
		// nothing to do here
	}

	/**
	 * Update grid type.
	 * 
	 * @param ev
	 *            view
	 * @param val
	 *            grid type
	 */
	private static boolean setGridType(EuclidianView ev, int val) {
		EuclidianSettings evs = ev.getSettings();
		boolean gridChanged;
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
				evs.setGridType(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID);
			}
			evs.endBatch();
		}
		return gridChanged;
	}

	private void setDelSize(int s) {
		app.getSettings().getPenTools().setDeleteToolSize(EuclidianSettings.DELETE_SIZES[s]);
		for (int i = 0; i < 3; i++) {
			btnDeleteSizes[i].setSelected(i == s);
			btnDeleteSizes[i].setEnabled(i != s);
		}
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return btnPointCapture.getSelectedIndex();
	}

	/**
	 * Get index of selected grid type icon for a view.
	 * 
	 * @param ev
	 *            view
	 * @return which icon should be selected
	 */
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
		PopupMenuButtonW.hideCurrentPopup();
	}

	@Override
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
		this.btnPointCapture.getMyTable()
				.updateText(ImageOrText.convert(new String[] {
						loc.getMenu("Labeling.automatic"),
						loc.getMenu("SnapToGrid"), loc.getMenu("FixedToGrid"),
						loc.getMenu("Off") }));
		this.btnLabelStyle.getMyTable()
				.updateText(ImageOrText
						.convert(new String[] { loc.getMenu("stylebar.Hidden"), // index
																				// 4
								loc.getMenu("Name"), // index 0
								loc.getMenu("NameAndValue"), // index 1
								loc.getMenu("Value"), // index 2
								loc.getMenu("Caption") // index 3
		}));

		String[] angleIntervalArray = new String[GeoAngle
				.getIntervalMinListLength() - 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervalArray[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		this.btnAngleInterval.getMyTable()
				.updateText(ImageOrText.convert(angleIntervalArray));

		this.btnTextSize.getMyTable().updateText(ImageOrText
				.convert(app.getLocalization().getFontSizeStrings()));

		if (btnLabel != null) {
			btnLabel.setLabels();
		}
		btnLineStyle.setLabels();
		btnColor.setLabels();
		if (btnTextBgColor != null) {
			btnTextBgColor.setLabels();
		}
		if (btnCrop != null) {
			btnCrop.setTitle(loc.getMenu("stylebar.Crop"));
		}
		// set labels for ToolTips
		setToolTips();
	}

	protected void setAxesAndGridToolTips(Localization loc) {
		btnShowGrid.setTitle(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxes.setTitle(loc.getPlainTooltip("stylebar.Axes"));
	}

	/**
	 * set tool tips
	 */
	protected void setToolTips() {
		if (app.isUnbundled()) {
			return;
		}
		setAxesAndGridToolTips(loc);
		setToolTipText(btnLabelStyle, "stylebar.Label");
		setToolTipText(btnAngleInterval, "AngleBetween");
		setToolTipText(btnColor, "stylebar.Color");
		setToolTipText(btnBgColor, "stylebar.BgColor");
		setToolTipText(btnLineStyle, "stylebar.LineStyle");
		setToolTipText(btnPointStyle, "stylebar.PointStyle");
		setToolTipText(btnFilling, "stylebar.Filling");
		setToolTipText(btnTextSize, "stylebar.TextSize");
		btnBold.setTitle(loc.getMenu("stylebar.Bold"));
		btnItalic.setTitle(loc.getMenu("stylebar.Italic"));
		setToolTipText(btnPointCapture, "stylebar.Capture");
		btnFixPosition.setTitle(loc.getMenu("AbsoluteScreenLocation"));
		btnFixObject.setTitle(loc.getMenu("FixObject"));
		setToolTipText(btnTextColor, "stylebar.Color");
		setToolTipText(btnTextBgColor, "stylebar.BgColor");
		setToolTipText(btnBorderText, "stylebar.Borders");

		setToolTipText(btnBorderStyle, "stylebar.Borders");
		setPopupTooltips(btnBorderStyle, new String[] { "AllBorders", "InnerBorders",
				"OuterBorders", "ClearBorders" });
		btnBorderStyle.getBorderThicknessBtn()
				.setTitle(loc.getMenu("stylebar.BorderStyle"));

		setToolTipText(btnHorizontalAlignment, "stylebar.HorizontalAlign");
		setPopupTooltips(btnHorizontalAlignment, new String[] { "Left", "Center", "Right" });

		setToolTipText(btnVerticalAlignment, "stylebar.VerticalAlign");
		setPopupTooltips(btnVerticalAlignment, new String[] { "Top", "Middle", "Bottom" });

		setToolTipText(btnSegmentStartStyle, "stylebar.LineStartStyle");
		setToolTipText(btnSegmentEndStyle, "stylebar.LineEndStyle");
	}

	private void setToolTipText(StandardButton btn, String key) {
		if (btn != null) {
			btn.setTitle(loc.getPlainTooltip(key));
		}
	}

	/**
	 * set tooltips of buttons in the popup
	 */
	private void setPopupTooltips(PopupMenuButtonW popup, String[] tooltips) {
		for (int i = 0; i < popup.getMyTable().getColumnCount(); i++) {
			popup.getMyTable().getWidget(0, i)
					.setTitle(app.getLocalization().getMenu("stylebar." + tooltips[i]));
		}
	}

	@Override
	public void reinit() {
		// nothing to do here
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		super.setVisible(visible);
		if (btnContextMenu != null) {
			btnContextMenu.hideMenu();
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	protected void onColorClicked() {
		if (btnCrop != null) {
			btnCrop.setSelected(false);
			toggleCrop(false);
		}
	}
}
