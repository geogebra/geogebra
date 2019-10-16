package org.geogebra.desktop.euclidian;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.color.ColorPopupMenuButton;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.MyToggleButtonD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * Stylebar for the Euclidian Views
 * 
 * @author G. Sturr
 */
public class EuclidianStyleBarD extends JToolBar
		implements ActionListener, EuclidianStyleBar {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * Class for buttons visible only when no geo is selected and no geo is to
	 * be created
	 * 
	 * @author mathieu
	 * 
	 */
	protected class MyToggleButtonDforEV extends MyToggleButtonD {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * constructor
		 * 
		 * @param icon
		 *            icon of the button
		 * @param height
		 *            height of the button
		 */
		public MyToggleButtonDforEV(ImageIcon icon, int height) {
			super(icon, height);

		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0 && !EuclidianView.isPenMode(mode)
					&& mode != EuclidianConstants.MODE_DELETE
					&& mode != EuclidianConstants.MODE_ERASER);
		}

		/*
		 * @Override public Point getToolTipLocation(MouseEvent e) { return new
		 * Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
		 */

	}

	// ggb
	EuclidianController ec;
	protected EuclidianViewInterfaceCommon ev;
	protected AppD app;
	private Construction cons;

	// buttons and lists of buttons
	protected ColorPopupMenuButton btnColor, btnBgColor, btnTextColor;

	protected PopupMenuButtonD btnLineStyle, btnPointStyle, btnTextSize,
			btnTableTextJustify, btnTableTextBracket, btnLabelStyle,
			btnPointCapture, btnAngleInterval;

	protected MyToggleButtonD btnShowGrid;

	protected MyToggleButtonD btnStandardView;

	protected MyToggleButtonD btnShowAxes;
	protected MyToggleButtonD btnDeleteSize[];

	MyToggleButtonD btnBold;

	MyToggleButtonD btnItalic;

	private MyToggleButtonD btnTableTextLinesV;

	private MyToggleButtonD btnTableTextLinesH;

	MyToggleButtonD btnFixPosition, btnFixObject;

	private PopupMenuButtonD[] popupBtnList;
	private MyToggleButtonD[] toggleBtnList;

	// fields for setting/unsetting default geos
	protected HashMap<Integer, Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;

	// flags and constants
	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	public int mode = -1;
	protected boolean isIniting;
	private boolean needUndo = false;
	private Integer oldDefaultMode;
	private boolean modeChanged = true;

	// button-specific fields
	// TODO: create button classes so these become internal
	AlgoTableText tableText;

	HashMap<Integer, Integer> lineStyleMap;

	HashMap<Integer, Integer> pointStyleMap;
	protected final LocalizationD loc;

	/*************************************************
	 * Constructs a styleBar
	 * 
	 * @param ev
	 *            view
	 */
	public EuclidianStyleBarD(EuclidianViewInterfaceCommon ev) {

		isIniting = true;

		this.ev = ev;
		ec = ev.getEuclidianController();
		app = (AppD) ev.getApplication();
		this.loc = app.getLocalization();
		cons = app.getKernel().getConstruction();

		// init handling of default geos
		createDefaultMap();
		defaultGeos = new ArrayList<>();

		// toolbar display settings
		setFloatable(false);
		updatePreferredSize();
		// init button-specific fields
		// TODO: put these in button classes
		pointStyleMap = new HashMap<>();
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleMap.put(EuclidianView.getPointStyle(i), i);
		}

		Integer[] lineStyleArray = EuclidianView.getLineTypes();
		lineStyleMap = new HashMap<>();
		for (int i = 0; i < lineStyleArray.length; i++) {
			lineStyleMap.put(lineStyleArray[i], i);
		}

		setLabels(); // this will also init the GUI

		isIniting = false;

		setMode(ev.getMode()); // this will also update the stylebar

	}

	private void updatePreferredSize() {
		iconHeight = app.getScaledIconSize();
		iconDimension = new Dimension(Math.max(16, iconHeight), iconHeight);
		Dimension d = getPreferredSize();
		d.width = getIconWidth() + 8;
		d.height = iconHeight + 8;
		setPreferredSize(d);

	}

	private boolean firstPaint = true;

	@Override
	public void resetFirstPaint() {
		firstPaint = true;
	}

	@Override
	public void paint(Graphics g) {

		if (firstPaint) {
			firstPaint = false;
			updateGUI();
		}

		super.paint(g);
	}

	/**
	 * create default map between default geos and modes
	 */
	protected void createDefaultMap() {
		defaultGeoMap = EuclidianStyleBarStatic.createDefaultMap();
	}

	/**
	 * @return euclidian mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Handles ggb mode changes.
	 * 
	 * @param mode
	 *            new mode
	 */
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

		updateStyleBar();

	}

	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(ev.getViewID());
	}

	@Override
	public void restoreDefaultGeo() {
		if (oldDefaultGeo != null) {
			oldDefaultGeo = cons.getConstructionDefaults()
					.getDefaultGeo(oldDefaultMode);
		}
	}

	protected ArrayList<GeoElement> activeGeoList;
	protected String specialJustification;

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	@Override
	public void updateStyleBar() {

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
			boolean hasGeosInThisView = false;
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
		// display a selection for the drag-delete-tool
		// can't use a geo element for this
		// -----------------------------------------------------


		// -----------------------------------------------------
		// All other modes: load activeGeoList with current default geo
		// -----------------------------------------------------
		else if (!deleteMode(mode) && defaultGeoMap.containsKey(mode)) {

			// Save the current default geo state in oldDefaultGeo.
			// Stylebar buttons can temporarily change a default geo, but this
			// default geo is always restored to its previous state after a mode
			// change.

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
			if (type.equals(ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)
					&& justCreatedGeos.size() == 1) {
				GeoElement justCreated = justCreatedGeos.get(0);
				if (justCreated.isGeoPoint()) {
					// get default type regarding what type of point has been
					// created
					if (((GeoPointND) justCreated).isPointOnPath()) {
						type = ConstructionDefaults.DEFAULT_POINT_ON_PATH;
					} else if (((GeoPointND) justCreated).hasRegion()) {
						type = ConstructionDefaults.DEFAULT_POINT_IN_REGION;
					} else if (!((GeoPointND) justCreated).isIndependent()) {
						type = ConstructionDefaults.DEFAULT_POINT_DEPENDENT;
					} else {
						type = ConstructionDefaults.DEFAULT_POINT_FREE;
					}
				}
			}

			if (type.equals(
					ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)) {
				// add all non-complex default points
				activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
						ConstructionDefaults.DEFAULT_POINT_FREE));
				activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
						ConstructionDefaults.DEFAULT_POINT_ON_PATH));
				activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
						ConstructionDefaults.DEFAULT_POINT_IN_REGION));
				activeGeoList.add(cons.getConstructionDefaults().getDefaultGeo(
						ConstructionDefaults.DEFAULT_POINT_DEPENDENT));
			} else {
				GeoElement geo = cons.getConstructionDefaults()
						.getDefaultGeo(type);
				if (geo != null) {
					activeGeoList.add(geo);
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
			activeGeoList.addAll(justCreatedGeos);
		}

		updatePreferredSize();
		updateButtons();

		addButtons();

	}

	private static boolean deleteMode(int mode2) {
		return mode2 == EuclidianConstants.MODE_DELETE
				|| mode2 == EuclidianConstants.MODE_ERASER;
	}

	protected void updateButtons() {
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		Object[] geos = activeGeoList.toArray();
		tableText = EuclidianStyleBarStatic.updateTableText(geos, mode);
		for (int i = 0; i < popupBtnList.length; i++) {
			popupBtnList[i].update(geos);
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			toggleBtnList[i].update(geos);
		}

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		if (activeGeoList.contains(geo)) {
			updateButtons();
		}
	}

	// =====================================================
	// Init GUI
	// =====================================================

	private void initGUI() {

		createButtons();
		createColorButton();
		createBgColorButton();
		createTextButtons();
		createTableTextButtons();
		setActionCommands();

		addButtons();

		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();

		for (int i = 0; i < popupBtnList.length; i++) {
			// popupBtnList[i].setStandardButton(true);
		}

	}

	protected void setActionCommands() {
		btnShowAxes.setActionCommand("showAxes");
		btnShowGrid.setActionCommand("showGrid");
		btnStandardView.setActionCommand("standardView");
		btnPointCapture.setActionCommand("pointCapture");
	}

	/**
	 * adds/removes buttons (must be called on updates so that separators are
	 * drawn only when needed)
	 */
	private void addButtons() {

		removeAll();

		// --- order matters here

		// add graphics decoration buttons
		addGraphicsDecorationsButtons();
		addBtnPointCapture();

		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);
		add(btnLineStyle);
		add(btnPointStyle);

		// add text decoration buttons
		if (btnBold.isVisible()) {
			addSeparator();
		}

		add(btnBold);
		add(btnItalic);
		add(btnTextSize);

		add(btnTableTextJustify);
		add(btnTableTextLinesV);
		add(btnTableTextLinesH);
		add(btnTableTextBracket);

		// add(btnPenEraser);

		add(btnAngleInterval);

		add(btnLabelStyle);
		// add(btnPointCapture);
		addBtnRotateView();
		// add(btnPenDelete);

		if (btnFixPosition.isVisible() || btnFixObject.isVisible()) {
			addSeparator();
		}

		add(btnFixPosition);
		add(btnFixObject);

		if (btnDeleteSize[0].isVisible() && btnColor.isVisible()) {
			addSeparator();
		}
		for (int i = 0; i < 3; i++) {
			add(btnDeleteSize[i]);
		}

	}

	/**
	 * add axes, grid, ... buttons
	 */
	protected void addGraphicsDecorationsButtons() {
		add(btnShowAxes);
		add(btnShowGrid);
		addBtnShowPlane();
		add(btnStandardView);
	}

	/**
	 * in 3D, add show plane button
	 */
	protected void addBtnShowPlane() {
		// nothing to do in 2D
	}

	protected PopupMenuButtonD[] newPopupBtnList() {
		return new PopupMenuButtonD[] { btnColor, btnBgColor, btnTextColor,
				btnLineStyle, btnPointStyle, btnTextSize, btnTableTextJustify,
				btnTableTextBracket, btnAngleInterval, btnLabelStyle,
				btnPointCapture, };
	}

	protected MyToggleButtonD[] newToggleBtnList() {
		return new MyToggleButtonD[] { btnShowGrid, btnShowAxes, btnStandardView,
				btnBold, btnItalic, btnTableTextLinesV, btnTableTextLinesH,
				btnFixPosition, btnFixObject, this.btnDeleteSize[0],
				this.btnDeleteSize[1], this.btnDeleteSize[2] };
	}

	protected void addBtnPointCapture() {
		add(btnPointCapture);
	}

	protected void addBtnRotateView() {
		// do nothing here (overridden function)
	}

	// =====================================================
	// Create Buttons
	// =====================================================

	protected void createButtons() {

		ImageIcon axesIcon = app
				.getScaledIcon(GuiResourcesD.STYLINGBAR_GRAPHICS_SHOW_AXES);
		iconHeight = axesIcon.getIconHeight();
		updatePreferredSize();
		// ========================================
		// mode button

		// ========================================
		// delete-drag square size
		btnDeleteSize = new MyToggleButtonD[3];
		ImageResourceD[] deleteIcons = new ImageResourceD[] {
				GuiResourcesD.STYLINGBAR_DELETE_SMALL,
				GuiResourcesD.STYLINGBAR_DELETE_MEDIUM,
				GuiResourcesD.STYLINGBAR_DELETE_BIG };
		for (int i = 0; i < 3; i++) {
			btnDeleteSize[i] = new MyToggleButtonD(
					app.getScaledIcon(deleteIcons[i]), iconHeight) {

				private static final long serialVersionUID = 1L;

				@Override
				public void update(Object[] geos) {
					this.setVisible(mode == EuclidianConstants.MODE_DELETE
							|| mode == EuclidianConstants.MODE_ERASER);
				}
			};
			btnDeleteSize[i].addActionListener(this);
		}
		// ========================================
		// show axes button
		btnShowAxes = new MyToggleButtonDforEV(axesIcon, iconHeight);
		// btnShowAxes.setPreferredSize(new Dimension(16,16));
		btnShowAxes.addActionListener(this);

		// ========================================
		// show grid button
		btnShowGrid = new MyToggleButtonDforEV(
				app.getScaledIcon(GuiResourcesD.STYLINGBAR_GRAPHICS_SHOW_GRID),
				iconHeight);
		// btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnShowGrid.addActionListener(this);

		// ========================================
		// standard view button
		btnStandardView = new MyToggleButtonDforEV(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_GRAPHICS_STANDARDVIEW),
				iconHeight);
		// btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnStandardView.setFocusPainted(false);
		btnStandardView.setBorderPainted(false);
		btnStandardView.setContentAreaFilled(false);
		btnStandardView.addActionListener(this);

		// ========================================
		// line style button

		// create line style icon array
		final Dimension lineStyleIconSize = new Dimension(
				Math.max(80, iconHeight * 4), iconHeight);
		ImageIcon[] lineStyleIcons = new ImageIcon[EuclidianView
				.getLineTypeLength()];
		for (int i = 0; i < EuclidianView.getLineTypeLength(); i++) {
			lineStyleIcons[i] = GeoGebraIconD.createLineStyleIcon(
					EuclidianView.getLineType(i), 2, lineStyleIconSize,
					Color.BLACK, null);
		}

		// create button
		btnLineStyle = new PopupMenuButtonD(app, lineStyleIcons, -1, 1,
				lineStyleIconSize, SelectionTable.MODE_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				if (EuclidianView.isPenMode(mode)) {
					this.setVisible(true);
					setFgColor(ec.getPen().getPenColor());
					setSliderValue(ec.getPen().getPenSize());
					setSelectedIndex(
							lineStyleMap.get(ec.getPen().getPenLineStyle()));
				} else {
					boolean geosOK = (geos.length > 0);
					int maxMinimumThickness = 0;
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i])
								.getGeoElementForPropertiesDialog();
						if (!geo.showLineProperties()) {
							geosOK = false;
							break;
						}
						if (geo.getMinimumLineThickness() == 1) {
							maxMinimumThickness = 1;
						}
					}

					this.setVisible(geosOK);

					if (geosOK) {
						// setFgColor(((GeoElement)geos[0]).getObjectColor());

						removeThisActionListenerTo(this);
						setFgColor(GColor.BLACK);
						getMySlider().setMinimum(maxMinimumThickness);
						setSliderValue(
								((GeoElement) geos[0]).getLineThickness());

						setSelectedIndex(lineStyleMap
								.get(((GeoElement) geos[0]).getLineType()));
						addThisActionListenerTo(this);

						this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
					}
				}
			}

			@Override
			public ImageIcon getButtonIcon() {
				if (getSelectedIndex() > -1) {
					return GeoGebraIconD.createLineStyleIcon(
							EuclidianView.getLineType(this.getSelectedIndex()),
							this.getSliderValue(), lineStyleIconSize,
							Color.BLACK, null);
				}
				return GeoGebraIconD.createEmptyIcon(lineStyleIconSize.width,
						lineStyleIconSize.height);
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};

		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider().setMaximum(GeoElement.MAX_LINE_WIDTH);
		btnLineStyle.getMySlider().setMajorTickSpacing(2);
		btnLineStyle.getMySlider().setMinorTickSpacing(1);
		btnLineStyle.getMySlider().setPaintTicks(true);
		btnLineStyle.setStandardButton(true); // popup on the whole button
		btnLineStyle.addActionListener(this);

		// ========================================
		// point style button

		// create line style icon array
		final Dimension pointStyleIconSize = new Dimension(getIconWidth(),
				iconHeight);
		ImageIcon[] pointStyleIcons = new ImageIcon[EuclidianView
				.getPointStyleLength()];
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleIcons[i] = GeoGebraIconD.createPointStyleIcon(
					EuclidianView.getPointStyle(i), 4, pointStyleIconSize,
					Color.BLACK, null);
		}

		// create button
		btnPointStyle = new PopupMenuButtonD(app, pointStyleIcons, 2, -1,
				pointStyleIconSize, SelectionTable.MODE_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				GeoElement geo;
				boolean geosOK = (geos.length > 0);
				// btnPointStyle.getMyTable().setVisible(true);
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
					setFgColor(GColor.BLACK);

					// if geo is a matrix, this will return a GeoNumeric...
					geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();

					// ... so need to check
					if (geo instanceof PointProperties) {
						setSliderValue(((PointProperties) geo).getPointSize());
						int pointStyle = ((PointProperties) geo)
								.getPointStyle();
						if (pointStyle == -1) {
							pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
						}
						selectPointStyle(pointStyleMap.get(pointStyle));
						this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
					}
				}
			}

			@Override
			public ImageIcon getButtonIcon() {
				if (getSelectedIndex() > -1) {
					return GeoGebraIconD.createPointStyleIcon(
							EuclidianView
									.getPointStyle(this.getSelectedIndex()),
							this.getSliderValue(), pointStyleIconSize,
							Color.BLACK, null);
				}
				return GeoGebraIconD.createEmptyIcon(pointStyleIconSize.width,
						pointStyleIconSize.height);
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};
		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider()
				.setMaximum(EuclidianStyleConstants.MAX_POINT_SIZE);
		btnPointStyle.getMySlider().setMajorTickSpacing(2);
		btnPointStyle.getMySlider().setMinorTickSpacing(1);
		btnPointStyle.getMySlider().setPaintTicks(true);
		btnPointStyle.setStandardButton(true); // popup on the whole button
		btnPointStyle.addActionListener(this);

		// ========================================
		// angle interval button

		String[] angleIntervalArray = new String[GeoAngle
				.getIntervalMinListLength() - 1];

		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervalArray[i] = loc.getPlain("AngleBetweenAB.short",
					GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		btnAngleInterval = new PopupMenuButtonD(app, angleIntervalArray, -1, 1,
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

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
			public ImageIcon getButtonIcon() {
				return (ImageIcon) this.getIcon();
			}

		};
		ImageIcon ic = app.getScaledIcon(GuiResourcesD.STYLEBAR_ANGLE_INTERVAL);
		btnAngleInterval
				.setIconSize(new Dimension(ic.getIconWidth(), iconHeight));
		btnAngleInterval.setIcon(ic);
		btnAngleInterval.setStandardButton(true);
		btnAngleInterval.addActionListener(this);
		btnAngleInterval.setKeepVisible(false);

		// ========================================
		// caption style button

		String[] captionArray = new String[] { loc.getMenu("stylebar.Hidden"), // index
																				// 4
				loc.getMenu("Name"), // index 0
				loc.getMenu("NameAndValue"), // index 1
				loc.getMenu("Value"), // index 2
				loc.getMenu("Caption") // index 3
		};

		btnLabelStyle = new PopupMenuButtonD(app, captionArray, -1, 1,
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

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
			public ImageIcon getButtonIcon() {
				return (ImageIcon) this.getIcon();
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};

		ic = app.getScaledIconCommon(GuiResourcesD.MODE_SHOWHIDELABEL);
		btnLabelStyle.setIconSize(new Dimension(ic.getIconWidth(), iconHeight));
		btnLabelStyle.setIcon(ic);
		btnLabelStyle.setStandardButton(true);
		btnLabelStyle.addActionListener(this);
		btnLabelStyle.setKeepVisible(false);

		// ========================================
		// point capture button

		String[] strPointCapturing = { loc.getMenu("Labeling.automatic"),
				loc.getMenu("SnapToGrid"), loc.getMenu("FixedToGrid"),
				loc.getMenu("Off") };

		btnPointCapture = new PopupMenuButtonD(app, strPointCapturing, -1, 1,
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				this.setVisible(
						geos.length == 0 && !EuclidianView.isPenMode(mode)
								&& mode != EuclidianConstants.MODE_DELETE
								&& mode != EuclidianConstants.MODE_ERASER);
			}

			@Override
			public ImageIcon getButtonIcon() {
				return (ImageIcon) this.getIcon();
			}
		};

		ImageIcon ptCaptureIcon = app.getScaledIcon(
				GuiResourcesD.STYLINGBAR_GRAPHICS_POINT_CAPTURING);
		btnPointCapture.setIconSize(
				new Dimension(ptCaptureIcon.getIconWidth(), iconHeight));
		btnPointCapture.setIcon(ptCaptureIcon);
		btnPointCapture.setStandardButton(true); // popup on the whole button
		btnPointCapture.addActionListener(this);
		btnPointCapture.setKeepVisible(false);

		// ========================================
		// fixed position button
		btnFixPosition = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.MENU_PIN), iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeos(geos);

				setVisible(geosOK);
				if (geosOK) {
					btnFixPosition.setSelected(EuclidianStyleBarStatic
							.checkSelectedFixPosition((GeoElement) geos[0]));
				}
			}

			private boolean checkGeos(Object[] geos) {
				return EuclidianStyleBarStatic.checkGeosForFixPosition(geos);
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};
		btnFixPosition.addActionListener(this);

		// ========================================
		// fixed object button
		btnFixObject = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.STYLINGBAR_OBJECT_UNFIXED),
				iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeos(geos);

				setVisible(geosOK);
				if (geosOK) {
					boolean selected = EuclidianStyleBarStatic
							.checkSelectedFixObject((GeoElement) geos[0]);
					btnFixObject.setSelected(selected);
					if (selected) {
						btnFixObject.setIcon(app.getScaledIcon(
								GuiResourcesD.STYLINGBAR_OBJECT_FIXED));
					} else {
						btnFixObject.setIcon(app.getScaledIcon(
								GuiResourcesD.STYLINGBAR_OBJECT_UNFIXED));
					}
				}
			}

			private boolean checkGeos(Object[] geos) {
				return EuclidianStyleBarStatic.checkGeosForFixObject(geos);
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};
		btnFixObject.addActionListener(this);

	}

	void addThisActionListenerTo(AbstractButton button) {
		button.addActionListener(this);
	}

	void removeThisActionListenerTo(AbstractButton button) {
		button.removeActionListener(this);
	}

	// ========================================
	// object color button (color for everything except text)

	protected void createColorButton() {

		final Dimension colorIconSize = new Dimension(Math.max(20, iconHeight),
				iconHeight);
		btnColor = new ColorPopupMenuButton(app, colorIconSize,
				ColorPopupMenuButton.COLORSET_DEFAULT, true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				if (EuclidianView.isPenMode(mode)) {
					this.setVisible(true);

					setSelectedIndex(getColorIndex(ec.getPen().getPenColor()));

					setSliderValue(100);
					getMySlider().setVisible(false);

				} else {
					boolean geosOK = (geos.length > 0
							|| EuclidianView.isPenMode(mode));
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
						GColor geoColor;
						geoColor = ((GeoElement) geos[0]).getObjectColor();

						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha
						// value
						double alpha = 1.0;
						boolean hasFillable = false;
						for (int i = 0; i < geos.length; i++) {
							if (((GeoElement) geos[i]).isFillable()) {
								hasFillable = true;
								// can be -1 for lists
								alpha = ((GeoElement) geos[i]).getAlphaValue();
								break;
							}
						}

						if (hasFillable) {
							setToolTipText(
									loc.getMenu("stylebar.ColorTransparency"));
						} else {
							setToolTipText(loc.getMenu("stylebar.Color"));
						}

						setSliderValue((int) Math.round(alpha * 100));

						updateColorTable();

						// find the geoColor in the table and select it
						int index = this.getColorIndex(geoColor);
						setSelectedIndex(index);
						setDefaultColor(alpha < 0 ? 0 : alpha, geoColor);

						this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
					}
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};

		btnColor.setStandardButton(true); // popup on the whole button
		btnColor.addActionListener(this);
	}

	protected void createBgColorButton() {

		final Dimension bgColorIconSize = new Dimension(
				Math.max(20, iconHeight), iconHeight);

		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
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
					GColor geoColor;
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
						this.setIcon(GeoGebraIconD.createColorSwatchIcon(alpha,
								bgColorIconSize, GColorD.getAwtColor(geoColor),
								null));
					}
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};
		btnBgColor.setKeepVisible(true);
		btnBgColor.setStandardButton(true); // popup on the whole button
		btnBgColor.addActionListener(this);
	}

	// =====================================================
	// Text Format Buttons
	// =====================================================

	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i])
					.getGeoElementForPropertiesDialog() instanceof TextProperties)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	protected void createTextButtons() {

		// ========================
		// text color button
		final Dimension textColorIconSize = new Dimension(getIconWidth(),
				iconHeight);

		btnTextColor = new ColorPopupMenuButton(app, textColorIconSize,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private static final long serialVersionUID = 1L;

			private GColor geoColor;

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

					setFgColor(geoColor);
					// setFontStyle(((TextProperties) geo).getFontStyle());
				}
			}

			@Override
			public ImageIcon getButtonIcon() {
				return GeoGebraIconD.createTextSymbolIcon("A",
						app.getPlainFont(), textColorIconSize,
						GColorD.getAwtColor(getSelectedColor()), null);
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};

		btnTextColor.setStandardButton(true); // popup on the whole button
		btnTextColor.addActionListener(this);

		// ========================================
		// bold text button
		ImageIcon boldIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Bold").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnBold = new MyToggleButtonD(boldIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos)
						&& !((GeoElement) geos[0]).isGeoInputBox();
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setSelected(style == Font.BOLD
							|| style == (Font.BOLD + Font.ITALIC));
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};
		btnBold.addActionListener(this);

		// ========================================
		// italic text button
		ImageIcon italicIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Italic").substring(0, 1), app.getPlainFont(),
				false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButtonD(italicIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos)
						&& !((GeoElement) geos[0]).isGeoInputBox();
				setVisible(geosOK);
				this.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnItalic.setSelected(style == Font.ITALIC
							|| style == (Font.BOLD + Font.ITALIC));
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */

		};
		btnItalic.addActionListener(this);

		// ========================================
		// text size button

		String[] textSizeArray = app.getLocalization().getFontSizeStrings();

		btnTextSize = new PopupMenuButtonD(app, textSizeArray, -1, 1,
				new Dimension(-1, iconHeight), SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText.getFontSizeIndex(
							((TextProperties) geo).getFontSizeMultiplier())); // font
																				// size
																				// ranges
																				// from
					// -4 to 4, transform
					// this to 0,1,..,4
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};
		btnTextSize.addActionListener(this);
		btnTextSize.setStandardButton(true); // popup on the whole button
		btnTextSize.setKeepVisible(false);
	}

	private int getIconWidth(int base) {
		return Math.max(base, iconHeight);
	}

	private int getIconWidth() {
		return Math.max(20, iconHeight);
	}

	// ================================================
	// Create TableText buttons
	// ================================================

	protected void createTableTextButtons() {

		// ==============================
		// justification popup
		ImageIcon[] justifyIcons = new ImageIcon[] {
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_LEFT),
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_CENTER),
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_RIGHT) };
		btnTableTextJustify = new PopupMenuButtonD((AppD) ev.getApplication(),
				justifyIcons, 1, -1, new Dimension(getIconWidth(), iconHeight),
				SelectionTable.MODE_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				if (tableText != null) {
					this.setVisible(true);
					String justification = tableText.getJustification();
					EuclidianStyleBarD.this.specialJustification = null;
					if ("c".equals(justification)) {
						btnTableTextJustify.setSelectedIndex(1);
					} else if ("r".equals(justification)) {
						btnTableTextJustify.setSelectedIndex(2);
					} else if ("l".equals(justification)) {
						btnTableTextJustify.setSelectedIndex(0); // left align
					} else {
						btnTableTextJustify.setSelectedIndex(0);
						EuclidianStyleBarD.this.specialJustification = justification;
					}
				} else {
					this.setVisible(false);
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};

		btnTableTextJustify.addActionListener(this);
		btnTableTextJustify.setKeepVisible(false);

		// ==============================
		// bracket style popup

		ImageIcon[] bracketIcons = new ImageIcon[EuclidianStyleBarStatic.bracketArray.length];
		for (int i = 0; i < bracketIcons.length; i++) {
			bracketIcons[i] = GeoGebraIconD.createStringIcon(
					EuclidianStyleBarStatic.bracketArray[i], app.getPlainFont(),
					true, false, true,
					new Dimension(getIconWidth(30) + 4, iconHeight + 4),
					Color.BLACK, null);
		}

		btnTableTextBracket = new PopupMenuButtonD((AppD) ev.getApplication(),
				bracketIcons, 2, -1,
				new Dimension(getIconWidth(30) + 4, iconHeight + 4),
				SelectionTable.MODE_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				if (tableText != null) {
					this.setVisible(true);
					String s = tableText.getOpenSymbol() + " "
							+ tableText.getCloseSymbol();
					int index = 0;
					for (int i = 0; i < EuclidianStyleBarStatic.bracketArray.length; i++) {
						if (s.equals(EuclidianStyleBarStatic.bracketArray[i])) {
							index = i;
							break;
						}
					}
					// System.out.println("index" + index);
					btnTableTextBracket.setSelectedIndex(index);

				} else {
					this.setVisible(false);
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};

		btnTableTextBracket.addActionListener(this);
		btnTableTextBracket.setKeepVisible(false);

		// ====================================
		// vertical grid lines toggle button
		btnTableTextLinesV = new MyToggleButtonD(
				GeoGebraIconD.createVGridIcon(iconDimension), iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				if (tableText != null) {
					setVisible(true);
					setSelected(tableText.isVerticalLines());
				} else {
					setVisible(false);
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};
		btnTableTextLinesV.addActionListener(this);

		// ====================================
		// horizontal grid lines toggle button
		btnTableTextLinesH = new MyToggleButtonD(
				GeoGebraIconD.createHGridIcon(iconDimension), iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				if (tableText != null) {
					setVisible(true);
					setSelected(tableText.isHorizontalLines());
				} else {
					setVisible(false);
				}
			}

			/*
			 * @Override public Point getToolTipLocation(MouseEvent e) { return
			 * new Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
			 */
		};
		btnTableTextLinesH.addActionListener(this);
	}

	// =====================================================
	// Event Handlers
	// =====================================================

	@Override
	public void updateGUI() {

		if (isIniting) {
			return;
		}

		updatePreferredSize();

		btnPointCapture.removeActionListener(this);
		updateButtonPointCapture(ev.getPointCapturingMode());
		btnPointCapture.addActionListener(this);

		btnShowAxes.removeActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addActionListener(this);

		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		btnShowGrid.addActionListener(this);

		btnStandardView.removeActionListener(this);
		btnStandardView.setSelected(false);
		btnStandardView.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		needUndo = false;

		ArrayList<GeoElement> targetGeos = new ArrayList<>();
		targetGeos.addAll(ec.getJustCreatedGeos());
		if (!EuclidianConstants.isMoveOrSelectionMode(mode)) {
			targetGeos.addAll(defaultGeos);
			Previewable p = ev.getPreviewDrawable();
			if (p != null) {
				GeoElement geo = p.getGeoElement();
				if (geo != null) {
					targetGeos.add(geo);
				}
			}
		} else {
			targetGeos.addAll(app.getSelectionManager().getSelectedGeos());
		}

		processSource(source, targetGeos);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}

	}

	/**
	 * process the action performed
	 * 
	 * @param source
	 *            toggle / popup button
	 * @param targetGeos
	 *            geos
	 */
	protected void processSource(Object source,
			ArrayList<GeoElement> targetGeos) {

		if ((source instanceof JButton)
				&& (EuclidianStyleBarStatic.processSourceCommon(
						((JButton) source).getActionCommand(), targetGeos, ev))) {
			return;
		} else if (source == btnColor) {
			if (EuclidianView.isPenMode(mode)) {
				ec.getPen().setPenColor((btnColor.getSelectedColor()));
				// btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
			} else {
				GColor color = btnColor.getSelectedColor();
				float alpha = btnColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyColor(targetGeos, color,
						alpha, app);
				// btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
				// btnPointStyle.setFgColor((Color)btnColor.getSelectedValue());
			}
		}

		else if (source == btnBgColor) {
			if (btnBgColor.getSelectedIndex() >= 0) {
				GColor color = btnBgColor.getSelectedColor();
				float alpha = btnBgColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyBgColor(targetGeos,
						color, alpha);
			}
		}

		else if (source == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				GColor color = btnTextColor.getSelectedColor();
				needUndo = EuclidianStyleBarStatic.applyTextColor(targetGeos,
						color);
				// btnTextColor.setFgColor((Color)btnTextColor.getSelectedValue());
				// btnItalic.setForeground((Color)btnTextColor.getSelectedValue());
				// btnBold.setForeground((Color)btnTextColor.getSelectedValue());
			}
		} else if (source == btnLineStyle) {
			if (btnLineStyle.getSelectedValue() != null) {
				if (EuclidianView.isPenMode(mode)) {
					ec.getPen().setPenLineStyle(EuclidianView
							.getLineType(btnLineStyle.getSelectedIndex()));
					ec.getPen().setPenSize(btnLineStyle.getSliderValue());
				} else {
					int selectedIndex = btnLineStyle.getSelectedIndex();
					int lineSize = btnLineStyle.getSliderValue();
					needUndo = EuclidianStyleBarStatic.applyLineStyle(
							targetGeos, selectedIndex, lineSize);
				}

			}
		} else if (source == btnPointStyle) {
			if (btnPointStyle.getSelectedValue() != null) {
				int pointStyleSelIndex = btnPointStyle.getSelectedIndex();
				int pointSize = btnPointStyle.getSliderValue();
				needUndo = EuclidianStyleBarStatic.applyPointStyle(targetGeos,
						pointStyleSelIndex, pointSize);
			}
		} else if (source == btnBold) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos,
					GFont.ITALIC,
					btnBold.isSelected() ? GFont.BOLD : GFont.PLAIN);
		} else if (source == btnItalic) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos,
					GFont.BOLD,
					btnItalic.isSelected() ? GFont.ITALIC : GFont.PLAIN);
		} else if (source == btnTextSize) {
			needUndo = EuclidianStyleBarStatic.applyTextSize(targetGeos,
					btnTextSize.getSelectedIndex());
		} else if (source == btnAngleInterval) {
			needUndo = EuclidianStyleBarStatic.applyAngleInterval(targetGeos,
					btnAngleInterval.getSelectedIndex());
		} else if (source == btnLabelStyle) {
			needUndo = EuclidianStyleBarStatic.applyCaptionStyle(targetGeos,
					mode, btnLabelStyle.getSelectedIndex());
		}

		else if (source == btnTableTextJustify || source == btnTableTextLinesH
				|| source == btnTableTextLinesV
				|| source == btnTableTextBracket) {
			if (source == btnTableTextJustify) {
				specialJustification = null;
			}
			String[] justifyArray = { "l", "c", "r" };
			EuclidianStyleBarStatic.applyTableTextFormat(targetGeos,
					specialJustification != null ? specialJustification
							: justifyArray[btnTableTextJustify
									.getSelectedIndex()],
					btnTableTextLinesH.isSelected(),
					btnTableTextLinesV.isSelected(),
					btnTableTextBracket.getSelectedIndex(), app);
		}

		else if (source == btnFixPosition) {
			needUndo = EuclidianStyleBarStatic.applyFixPosition(targetGeos,
					btnFixPosition.isSelected(), ev) != null;
		}

		else if (source == btnFixObject) {
			needUndo = EuclidianStyleBarStatic.applyFixObject(targetGeos,
					btnFixObject.isSelected(), ev) != null;
			btnFixObject.update(targetGeos.toArray());
		}

		else {
			for (int i = 0; i < 3; i++) {
				if (source == btnDeleteSize[i]) {
					setDelSize(i);
				}
			}
		}
	}

	private void setDelSize(int s) {
		ev.getSettings().setDeleteToolSize(EuclidianSettings.DELETE_SIZES[s]);
		for (int i = 0; i < 3; i++) {
			btnDeleteSize[i].setSelected(i == s);
			btnDeleteSize[i].setEnabled(i != s);
		}
	}

	@Override
	public void updateButtonPointCapture(int mode1) {
		if (mode1 == 3 || mode1 == 0)
		 {
			btnPointCapture.setSelectedIndex(3 - mode1); // swap 0 and 3
		} else {
			btnPointCapture.setSelectedIndex(mode1);
		}
	}

	// ==============================================
	// Apply Styles
	// ==============================================

	/**
	 * Set labels with localized strings.
	 */
	@Override
	public void setLabels() {

		initGUI();
		updateStyleBar();

		btnShowGrid.setToolTipText(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxes.setToolTipText(loc.getPlainTooltip("stylebar.Axes"));
		btnStandardView
				.setToolTipText(loc.getPlainTooltip("stylebar.ViewDefault"));
		btnPointCapture.setToolTipText(loc.getPlainTooltip("stylebar.Capture"));

		btnAngleInterval.setToolTipText(loc.getPlainTooltip("AngleBetween"));

		btnLabelStyle.setToolTipText(loc.getPlainTooltip("stylebar.Label"));

		btnColor.setToolTipText(loc.getPlainTooltip("stylebar.Color"));
		btnBgColor.setToolTipText(loc.getPlainTooltip("stylebar.BgColor"));

		btnLineStyle.setToolTipText(loc.getPlainTooltip("stylebar.LineStyle"));
		btnPointStyle
				.setToolTipText(loc.getPlainTooltip("stylebar.PointStyle"));

		btnTextColor.setToolTipText(loc.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(loc.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
		btnTableTextJustify
				.setToolTipText(loc.getPlainTooltip("stylebar.Align"));
		btnTableTextBracket
				.setToolTipText(loc.getPlainTooltip("stylebar.Bracket"));
		btnTableTextLinesV
				.setToolTipText(loc.getPlainTooltip("stylebar.VerticalLine"));
		btnTableTextLinesH
				.setToolTipText(loc.getPlainTooltip("stylebar.HorizontalLine"));

		btnFixPosition
				.setToolTipText(loc.getPlainTooltip("AbsoluteScreenLocation"));
		btnFixObject.setToolTipText(loc.getPlainTooltip("FixObject"));

		btnDeleteSize[0].setToolTipText(loc.getPlainTooltip("Small"));
		btnDeleteSize[1].setToolTipText(loc.getPlainTooltip("Medium"));
		btnDeleteSize[2].setToolTipText(loc.getPlainTooltip("Large"));
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return btnPointCapture.getSelectedIndex();
	}

	@Override
	public void hidePopups() {
		// not needed in Desktop
	}

	protected PopupMenuButtonD getBtnPointStyle() {
		return btnPointStyle;
	}

	protected void selectPointStyle(int idx) {
		btnPointStyle.setSelectedIndex(idx);
	}

	@Override
	public void reinit() {
		updatePreferredSize();
		createButtons();
		createColorButton();
		createBgColorButton();
		createTextButtons();
		createTableTextButtons();
		setActionCommands();

		addButtons();
		setLabels();
	}

}
