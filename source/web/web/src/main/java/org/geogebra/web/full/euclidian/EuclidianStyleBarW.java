/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.euclidian;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.util.ElementPropertySetter;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PointStylePopup;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.StyleBarW2;
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
	private PopupMenuButtonW btnTextSize;
	private PopupMenuButtonW btnLabelStyle;
	private PopupMenuButtonW btnAngleInterval;
	private PopupMenuButtonW btnShowGrid;
	protected PopupMenuButtonW btnPointCapture;
	protected PopupMenuButtonW btnChangeView;

	private ToggleButton btnShowAxes;
	private ToggleButton btnBold;
	private ToggleButton btnItalic;

	private ToggleButton btnFixPosition;
	private ToggleButton btnFixObject;

	private StandardButton btnLabelsToLatex;

	private ToggleButton[] toggleBtnList;
	private final ToggleButton[] btnDeleteSizes = new ToggleButton[3];
	private PopupMenuButtonW[] popupBtnList;

	private StyleBarMethod waitingOperation = StyleBarMethod.NONE;
	private final Localization loc;
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
			case UPDATE:
				updateStyleBar();
				break;
			case UPDATE_STYLE:
				updateButtons();
				break;
			default:
				// do nothing
				break;
			}
			this.waitingOperation = StyleBarMethod.NONE;
		}
	}

	protected boolean hasVisibleGeos(List<GeoElement> geoList) {
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
			activeGeoList = selection.getDefaultGeos();

			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}
		updateButtons();
		addButtons();
	}

	protected void updateButtons() {
		if (activeGeoList == null) {
			return;
		}

		ArrayList<GeoElement> geos = activeGeoList;
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

		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);

		add(btnLineStyle);
		add(btnSegmentStartStyle);
		add(btnSegmentEndStyle);
		add(btnPointStyle);

		// add text decoration buttons
		if (btnBold.isVisible()) {
			addSeparator();
		}

		add(btnBold);
		add(btnItalic);
		add(btnTextSize);

		add(btnAngleInterval);
		add(btnLabelStyle);
		add(btnLabelsToLatex);

		if (btnFixPosition.isVisible() || btnFixObject.isVisible()) {
			addSeparator();
		}

		add(btnFixPosition);
		add(btnFixObject);

		for (int i = 0; i < 3; i++) {
			add(btnDeleteSizes[i]);
		}

		addMenuButton();

		if (getViewButton() == null) {
			addViewButton();
		} else {
			add(getViewButton());
		}
	}

	private boolean isFocusedGroupElement() {
		return app.getSelectionManager().getFocusedGroupElement() != null;
	}

	/*
	 * Some style button removed from dynamic stylebar. Those will be shown in
	 * the default stylebar yet.
	 */
	private boolean showAllStyleButtons() {
		return !(this.getView() instanceof EuclidianView3DInterface);
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
				btnItalic, btnFixPosition, btnFixObject, btnDeleteSizes[0],
				btnDeleteSizes[1], btnDeleteSizes[2]};
	}

	protected PopupMenuButtonW[] newPopupBtnList() {
		return new PopupMenuButtonW[] { getAxesOrGridPopupMenuButton(), btnSegmentStartStyle,
				btnSegmentEndStyle, btnColor, btnBgColor, btnTextColor, btnLineStyle,
				btnPointStyle, btnTextSize, btnAngleInterval, btnLabelStyle, btnPointCapture,
				btnChangeView
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
		createBgColorBtn();
		createTextColorBtn();
		createTextBoldBtn();
		createTextItalicBtn();
		createFixPositionBtn();
		createFixObjectBtn();
		createTextSizeBtn();
		createChangeViewButtons();
		createLabelsToLatexBtn();
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
			super.setVisible(geos.isEmpty() && mode != EuclidianConstants.MODE_PEN);
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
				super.setVisible(geos.isEmpty() && !EuclidianView.isPenMode(mode)
						&& mode != EuclidianConstants.MODE_DELETE
						&& mode != EuclidianConstants.MODE_ERASER);
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
				3, 4, SelectionTable.MODE_ICON) {
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

	private boolean handleSegmentStart(List<GeoElement> targetGeos) {
		SegmentStyle segmentStyle
				= SegmentStyle.values()[btnSegmentStartStyle.getSelectedIndex()];
		return applySegmentStartStyle(targetGeos, segmentStyle, true);
	}

	private boolean handleSegmentEnd(List<GeoElement> targetGeos) {
		SegmentStyle segmentStyle
				= SegmentStyle.values()[btnSegmentEndStyle.getSelectedIndex()];
		return applySegmentStartStyle(targetGeos, segmentStyle, false);
	}

	private void createSegmentEndStyleBtn() {
		ImageOrText[] segmentEndStyleIcons = GeoGebraIconW.createSegmentEndStyleIcons();
		btnSegmentEndStyle = new PopupMenuButtonW(app, segmentEndStyleIcons,
				3, 4, SelectionTable.MODE_ICON) {
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
				.convert(new String[] { loc.getMenu("stylebar.Hidden"), // index 4
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
		btnLabelStyle.setFixedIcon(new ImageOrText(ic, 24));
		setPopupHandlerWithUndoPoint(btnLabelStyle, this::handleLabelStyle);
		btnLabelStyle.setKeepVisible(false);
	}

	private boolean handleLabelStyle(List<GeoElement> targetGeos) {
		return EuclidianStyleBarStatic.applyCaptionStyle(targetGeos,
				mode, btnLabelStyle.getSelectedIndex());
	}

	private void createAngleIntervalBtn() {
		String[] angleIntervalString = new String[GeoAngle.AngleStyle.values().length - 1];
		for (int i = 0; i < angleIntervalString.length - 1; i++) {
			GeoAngle.AngleStyle angleStyle = GeoAngle.AngleStyle.values()[i];
			angleIntervalString[i] = app.getLocalization()
					.getPlain("AngleBetweenAB.short", angleStyle.getMin(), angleStyle.getMax());
		}

		ImageOrText[] angleIntervalArray = ImageOrText
				.convert(angleIntervalString);

		btnAngleInterval = new PopupMenuButtonW(app, angleIntervalArray, -1, 1,
				SelectionTable.MODE_TEXT) {

			@Override
			public void update(List<GeoElement> geos) {
				GeoElement geo = EuclidianStyleBarStatic
						.checkGeosForAngleInterval(geos);
				boolean geosOK = geo != null;
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

	private boolean handleAngleInterval(List<GeoElement> targetGeos) {
		return EuclidianStyleBarStatic.applyAngleInterval(targetGeos,
				btnAngleInterval.getSelectedIndex());
	}

	/**
	 * set EV to standard view
	 */
	protected void setEvStandardView() {
		getView().setStandardView(true);
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
		btnBgColor.setKeepVisible(true);
		setPopupHandlerWithUndoAction(btnBgColor, this::handleBackgroundColor);
	}

	private boolean handleBackgroundColor(List<GeoElement> targetGeos) {
		if (btnBgColor.getSelectedIndex() >= 0) {
			GColor color = btnBgColor.getSelectedColor();
			if (color == null) {
				openColorChooser(true);
				return false;
			}
			double alpha = btnBgColor.getSliderValue() / 100.0;
			return EuclidianStyleBarStatic.applyBgColor(targetGeos,
					color, alpha);
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
				return new ImageOrText(MaterialDesignResources.INSTANCE.text_color(), 24);
			}
		};
		btnTextColor.setEnableTable(true);
		btnTextColor.addStyleName("btnTextColor");
		setPopupHandlerWithUndoAction(btnTextColor, this::handleTextColor);
	}

	private boolean handleTextColor(List<GeoElement> targetGeos) {
		if (btnTextColor.getSelectedIndex() >= 0) {
			GColor color = btnTextColor.getSelectedColor();
			if (color == null) {
				openColorChooser(false);
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

	private boolean handleBold(List<GeoElement> targetGeos) {
		return applyFontStyle(targetGeos, GFont.BOLD, btnBold.isSelected());
	}

	protected void addFastClickHandlerWithUndoPoint(ToggleButton btn,
			Function<ArrayList<GeoElement>, Boolean> action) {
		btn.addFastClickHandler(ignore -> processSelectionWithUndo(action));
	}

	protected void addFastClickHandlerWithUndoAction(ToggleButton btn,
			ElementPropertySetter action) {
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

	private boolean handleFixPosition(List<GeoElement> targetGeos) {
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

	private boolean handleFixObject(List<GeoElement> targetGeos) {
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

	private boolean handleItalic(List<GeoElement> targetGeos) {
		return applyFontStyle(targetGeos, GFont.ITALIC, btnItalic.isSelected());
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

	private boolean handleTextSize(List<GeoElement> targetGeos) {
		return applyTextSize(targetGeos, btnTextSize.getSelectedIndex());
	}

	private void createLabelsToLatexBtn() {
		btnLabelsToLatex = new StandardButton(
				MaterialDesignResources.INSTANCE.functions(), app);
		btnLabelsToLatex.addFastClickHandler(widget -> convertAllLabelsToLatex());
	}

	private void convertAllLabelsToLatex() {
		// Get all GeoElements from the construction
		Set<String> labels = app.getKernel().getConstruction().getAllGeoLabels();
		boolean changed = false;

		for (String label : labels) {
			GeoElement geo = app.getKernel().lookupLabel(label);
			if (geo != null && geo.isLabelVisible()) {
				// Set caption to LaTeX format: $label$
				String latexCaption = "$" + geo.getLabelSimple() + "$";
				if (geo.setCaption(latexCaption)) {
					geo.updateVisualStyleRepaint(GProperty.LABEL_STYLE);
					changed = true;
				}
			}
		}

		if (changed) {
			app.storeUndoInfo();
		}
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

	private static boolean checkGeoSegment(List<GeoElement> geos) {
		return checkGeos(geos, geo -> geo instanceof GeoSegment);
	}

	private Object getFirst(List<GeoElement> geos) {
		return geos.get(0).getGeoElementForPropertiesDialog();
	}

	private static boolean checkGeos(List<GeoElement> geos, Predicate<GeoElement> check) {
		boolean geosOK = !geos.isEmpty();
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

	private boolean applyTextSize(List<GeoElement> targetGeos,
			int selectedIndex) {
		return EuclidianStyleBarStatic.applyTextSize(targetGeos, selectedIndex);
	}

	private boolean applyFontStyle(List<GeoElement> targetGeos, int mask,
			boolean add) {
		return EuclidianStyleBarStatic.applyFontStyle(targetGeos, mask,
				add);
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
						.convert(new String[] { loc.getMenu("stylebar.Hidden"), // index 4
								loc.getMenu("Name"), // index 0
								loc.getMenu("NameAndValue"), // index 1
								loc.getMenu("Value"), // index 2
								loc.getMenu("Caption") // index 3
		}));

		String[] angleIntervalArray = new String[GeoAngle.AngleStyle.values().length - 1];
		for (int i = 0; i < angleIntervalArray.length; i++) {
			GeoAngle.AngleStyle angleStyle = GeoAngle.AngleStyle.values()[i];
			angleIntervalArray[i] = app.getLocalization()
					.getPlain("AngleBetweenAB.short", angleStyle.getMin(), angleStyle.getMax());
		}

		this.btnAngleInterval.getMyTable()
				.updateText(ImageOrText.convert(angleIntervalArray));

		this.btnTextSize.getMyTable().updateText(ImageOrText
				.convert(app.getLocalization().getFontSizeStrings()));

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
		setAxesAndGridToolTips(loc);
		setToolTipText(btnLabelStyle, "stylebar.Label");
		setToolTipText(btnAngleInterval, "AngleBetween");
		setToolTipText(btnColor, "stylebar.Color");
		setToolTipText(btnBgColor, "stylebar.BgColor");
		setToolTipText(btnLineStyle, "stylebar.LineStyle");
		setToolTipText(btnPointStyle, "stylebar.PointStyle");
		setToolTipText(btnTextSize, "stylebar.TextSize");
		btnBold.setTitle(loc.getMenu("stylebar.Bold"));
		btnItalic.setTitle(loc.getMenu("stylebar.Italic"));
		setToolTipText(btnPointCapture, "stylebar.Capture");
		btnFixPosition.setTitle(loc.getMenu("AbsoluteScreenLocation"));
		btnFixObject.setTitle(loc.getMenu("FixObject"));
		setToolTipText(btnTextColor, "stylebar.Color");

		setToolTipText(btnSegmentStartStyle, "stylebar.LineStartStyle");
		setToolTipText(btnSegmentEndStyle, "stylebar.LineEndStyle");

		btnLabelsToLatex.setTitle(loc.getPlainTooltip("stylebar.LabelsToLatex"));
	}

	private void setToolTipText(StandardButton btn, String key) {
		if (btn != null) {
			btn.setTitle(loc.getPlainTooltip(key));
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
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}
