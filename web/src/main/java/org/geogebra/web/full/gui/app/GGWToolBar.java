package org.geogebra.web.full.gui.app;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.toolbar.ToolBarW;
import org.geogebra.web.full.gui.toolbar.ToolbarSubmenuP;
import org.geogebra.web.full.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.animation.client.AnimationScheduler;
import org.gwtproject.animation.client.AnimationScheduler.AnimationCallback;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.TextDecoration;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.resources.client.impl.ImageResourcePrototype;
import org.gwtproject.safehtml.shared.UriUtils;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

/**
 * Toolbar for web, includes ToolbarW, undo panel and search / menu
 */
public class GGWToolBar extends Composite
		implements RequiresResize, ToolBarInterface, SetLabels {

	private ArrayList<ToolBarW> toolbars;
	/** application */
	AppW app;
	/** toolbar */
	ToolBarW toolBar;
	/** panel which contains the toolbar and undo-redo buttons. */
	FlowPanel toolBarPanel;
	/** panel for toolbar (without undo-redo buttons) */
	ScrollPanel toolBPanel;
	// ScrollPanel ;
	/** panel for mobile submenu view */
	FlowPanel submenuPanel;
	private ScrollPanel submenuScrollPanel;
	private boolean inited = false;
	private Integer activeToolbar = -1;
	private boolean menuBarShowing = false;

	private FlowPanel rightButtonPanel;
	private @CheckForNull StandardButton openSearchButton;
	private @CheckForNull StandardButton openMenuButton;
	/** undo button */
	StandardButton undoButton;
	private StandardButton redoButton;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * Create a new GGWToolBar object
	 */
	public GGWToolBar() {
		toolBarPanel = new FlowPanel();
		toolBarPanel.addStyleName("ggbtoolbarpanel");
		// this makes it draggable on SMART board
		toolBarPanel.addStyleName("smart-nb-draggable");
		// For app we set this also in GGWFrameLayoutPanel, but for applets we
		// must set it here
		toolBarPanel.setHeight(GLookAndFeelI.TOOLBAR_HEIGHT + "px");
		initWidget(toolBarPanel);
	}

	/**
	 * @return whether init was alreadz called
	 */
	public boolean isInited() {
		return inited;
	}

	/**
	 * @param viewID
	 *            view ID
	 */
	public void setActiveToolbar(Integer viewID) {
		if (activeToolbar.equals(viewID)) {
			return;
		}
		activeToolbar = viewID;
		for (ToolBarW bar : toolbars) {
			bar.setActiveView(viewID);
			bar.closeAllSubmenu();
		}
	}

	/**
	 * Initialization of the GGWToolbar.
	 * 
	 * @param app1
	 *            application
	 */
	public void init(AppW app1) {

		this.inited = true;
		this.app = app1;
		toolbars = new ArrayList<>();

		submenuScrollPanel = new ScrollPanel();
		submenuPanel = new FlowPanel();
		submenuPanel.addStyleName("submenuPanel");
		submenuScrollPanel.addStyleName("submenuScrollPanel");
		submenuScrollPanel.add(submenuPanel);

		toolBarPanel.add(submenuScrollPanel);

		toolBar = new ToolBarW(this, submenuPanel);

		updateClassname(app.getToolbarPosition());
		toolBPanel = new ScrollPanel();
		toolBarPanel.add(toolBar);
		toolBarPanel.add(toolBPanel);

		toolBarPanel.addStyleName("toolbarPanel");

		if (!examController.isIdle()) {
			toolBarPanel.addStyleName("toolbarPanelExam");
		}
		toolBPanel.setStyleName("toolBPanel");
		toolBPanel.addStyleName("overflow");

		// toolBarPanel.setSize("100%", "100%");
		toolBar.init(app1);
		addToolbar(toolBar);

		// Adds the Open and Options Button for SMART

		addRightButtonPanel();

	}

	/**
	 * Update class name for south/north toolbar
	 * 
	 * @param toolbarPosition
	 *            SwingConstants.SOUTH or SwingConstants.SOUTH
	 */
	public void updateClassname(int toolbarPosition) {
		if (toolbarPosition == SwingConstants.SOUTH) {
			removeStyleName("toolbarPanelNorth");
			addStyleName("toolbarPanelSouth");
		} else {
			removeStyleName("toolbarPanelSouth");
			addStyleName("toolbarPanelNorth");
		}

	}

	// undo-redo buttons
	private void addUndoPanel() {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;

		redoButton = new StandardButton(pr.menu_header_redo(), null, 32);
		redoButton.addStyleName("redoButton");

		redoButton.addClickHandler(app.getGlobalHandlers(), source -> {
			app.getGuiManager().redo();
			app.hideKeyboard();
		});

		undoButton = new StandardButton(pr.menu_header_undo(), null, 32);
		undoButton.addStyleName("undoButton");

		undoButton.addClickHandler(app.getGlobalHandlers(), source -> {
			app.getGuiManager().undo();
			app.hideKeyboard();
		});

		updateUndoActions();
		rightButtonPanel.add(undoButton);
		rightButtonPanel.add(redoButton);
		setLabels();
	}

	@Override
	public void setLabels() {
		Localization loc = app.getLocalization();
		if (redoButton != null) {
			redoButton.setTitle(loc.getMenu("Redo"));
		}
		if (undoButton != null) {
			undoButton.setTitle(loc.getMenu("Undo"));
		}

	}

	// timer for GeoGebraExam
	private FlowPanel getTimer() {
		final Label timerLabel = new Label();
		timerLabel.getElement().setClassName("timer");
		timerLabel.getElement().setId("timer");
		timerLabel.getElement().setPropertyBoolean("started", false);

		// https://groups.google.com/forum/#!msg/google-web-toolkit/VrF3KD1iLh4/-y4hkIDt5BUJ
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				if (examController.isExamActive()
						|| examController.getState() == ExamState.PREPARING) {
					if (examController.isCheating()) {
						ExamUtil.makeRed(getElement(), true);
						makeTimerWhite(Js.uncheckedCast(getElement()));
					}

					timerLabel.setText(examController.getDurationFormatted(app.getLocalization()));
					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}

		});
		// check and log window resize and focus on window
		new ExamUtil(app).addVisibilityAndBlurHandlers();

		FlowPanel fp = new FlowPanel();
		fp.add(timerLabel);
		Image info = new Image(
				GuiResourcesSimple.INSTANCE.dialog_info().getSafeUri().asString());
		info.setStyleName("examInfo");
		fp.add(info);

		// clicking on info button
		fp.addDomHandler(event -> {
			new ExamLogAndExitDialog(app, true, null, null, "OK").show();
		}, ClickEvent.getType());
		return fp;
	}

	/**
	 * @param element
	 *            element to be changed to red timer text elements get changed
	 *            to white
	 */
	private void makeTimerWhite(elemental2.dom.Element element) {
		HTMLCollection<elemental2.dom.Element> timerElements = element
				.getElementsByClassName("rightButtonPanel")
				.getAt(0)
				.getElementsByClassName("timer");

		for (int i = 0; i < timerElements.length; i++) {
			((HTMLElement) timerElements.getAt(i)).style
					.setProperty("color", "white", "important");
		}
	}

	// Undo, redo, open, menu (and exam mode)
	private void addRightButtonPanel() {

		this.rightButtonPanel = new FlowPanel();
		this.rightButtonPanel.setStyleName("rightButtonPanel");

		updateActionPanel();
		toolBarPanel.add(rightButtonPanel);
	}

	/**
	 * Updates the toolbar to match current settings (exam / no exam)
	 */
	public void updateActionPanel() {
		rightButtonPanel.clear();
		boolean exam = !examController.isIdle();
		setStyleName("examToolbar", exam);
		if (exam) {
			// We directly read the parameters to show the intention.
			// It may be possible that 3D is not supported from technical
			// reasons (e.g. the graphics card is problematic), but in such
			// cases we don't want to show that here.
			boolean supportsCAS = app.getSettings().getCasSettings().isEnabled();
			if (!supportsCAS) {
				Label nocas = new Label("CAS");
				nocas.getElement().getStyle()
						.setTextDecoration(TextDecoration.LINE_THROUGH);
				nocas.getElement().setClassName("timer");
				// do not add CAS to toolBar for tablet exam apps
				rightButtonPanel.add(nocas);
			}
			if (!app.getSettings().getEuclidian(-1).isEnabled()) {
				Label no3d = new Label("3D");
				no3d.getElement().getStyle()
						.setTextDecoration(TextDecoration.LINE_THROUGH);
				no3d.getElement().setClassName("timer");
				// do not add 3D to toolBar for tablet exam apps
				rightButtonPanel.add(no3d);
			}
			rightButtonPanel.add(getTimer());
		}
		if (app.getUndoRedoMode() == UndoRedoMode.GUI) {
			addUndoPanel();
		}
		this.menuBarShowing = false;
		if (app.getAppletParameters().getDataParamShowMenuBar(false)
				|| app.getAppletParameters().getDataParamApp()) {
			initMenuButton();

			if (!exam && app.enableOnlineFileFeatures()) {
				initOpenSearchButton();
			}

			if (!exam) {
				ExamUtil.makeRed(getElement(), false);
			}
			this.rightButtonPanel.add(openMenuButton);
		}
	}

	private void initMenuButton() {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		this.menuBarShowing = true;

		StandardButton openMenuButton = new StandardButton(pr.menu_header_open_menu(), null, 32);

		openMenuButton.addFastClickHandler(source -> {
			app.hideKeyboard();
			app.closePopups();
			app.toggleMenu();
		});

		openMenuButton.addDomHandler(event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				app.toggleMenu();
			}
			if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
				selectMenuButton(0);
			}
			if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
				toolBar.selectMenu(0);
			}
		}, KeyUpEvent.getType());

		this.openMenuButton = openMenuButton;
	}

	private void initOpenSearchButton() {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		StandardButton openSearchButton = new StandardButton(pr.menu_header_open_search(),
				null, 32, 32);

		openSearchButton.addFastClickHandler(source -> app.openSearch(null));

		openSearchButton.addDomHandler(event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				app.openSearch(null);
			}
			if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
				selectMenuButton(1);
			}
			if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
				toolBar.selectMenu(-1);
			}
		}, KeyUpEvent.getType());
		this.openSearchButton = openSearchButton;
		this.rightButtonPanel.add(openSearchButton);
	}

	/**
	 * @param uri
	 *            image URI
	 * @param width
	 *            size
	 * @return image wrapped in no-dragging widget
	 */
	public NoDragImage getImage(ResourcePrototype uri, int width) {
		return new NoDragImage(uri, width, width);
	}

	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		toolBPanel.clear();
		for (ToolBarW toolbar : toolbars) {
			if (toolbar != null) {
				toolbar.buildGui();
				toolBPanel.add(toolbar);
			}
		}

		onResize();
		toolBPanel.setVisible(true);
	}

	/**
	 * Adds a toolbar to this container. Use updateToolbarPanel() to update the
	 * GUI after all toolbar changes were made.
	 * 
	 * @param toolbar
	 *            toolbar
	 */
	public void addToolbar(ToolBarW toolbar) {
		toolbars.add(toolbar);
	}

	/**
	 * @param mode
	 *            app mode
	 * @param app
	 *            application
	 * @param target
	 *            icon for macro or builtin mode
	 */
	public static void getImageResource(final int mode, final AppW app,
			final HasResource target) {
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = app.getKernel().getMacro(macroID);
				String iconName = macro.getIconFileName();
				if (iconName == null || iconName.length() == 0) {
					// default icon
					target.setResource(ToolbarSvgResourcesSync.INSTANCE.mode_tool_32());
					return;
				}
				// use image as icon
				String src = app.getImageManager()
						.getExternalImageSrc(iconName);

				target.setResource(new ImageResourcePrototype("",
						UriUtils.fromTrustedString(src), 0,
						0, 32, 32, false, false));
				return;
			} catch (Exception e) {
				Log.debug("macro does not exist: ID = " + macroID);
				target.setResource(
						ToolbarSvgResourcesSync.INSTANCE.mode_tool_32());
				return;
			}
		}
		GWT.runAsync(GGWToolBar.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// failed loading toolbar
			}

			@Override
			public void onSuccess() {
				target.setResource(getImageURLNotMacro(
						ToolbarSvgResources.INSTANCE, mode, app));
			}
		});

	}

	/**
	 * @param resourceBundle
	 *            PNG or SVG bundle
	 * @param mode
	 *            app mode
	 * @param app
	 *            see {@link AppW}
	 * @return toolbar icon resource
	 */
	public static ResourcePrototype getImageURLNotMacro(
			ToolbarResources resourceBundle, int mode, AppW app) {
		switch (mode) {
		case EuclidianConstants.MODE_ANGLE:
			return resourceBundle.mode_angle_32();

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return resourceBundle.mode_anglefixed_32();

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return resourceBundle.mode_angularbisector_32();

		case EuclidianConstants.MODE_AREA:
			return resourceBundle.mode_area_32();

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return resourceBundle.mode_attachdetachpoint_32();

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return resourceBundle.mode_buttonaction_32();

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return resourceBundle.mode_circle2_32();

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return resourceBundle.mode_circle3_32();

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return resourceBundle.mode_circlearc3_32();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return resourceBundle.mode_circlepointradius_32();

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return resourceBundle.mode_circlesector3_32();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return resourceBundle.mode_circumcirclearc3_32();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return resourceBundle.mode_circumcirclesector3_32();

		case EuclidianConstants.MODE_COMPASSES:
			return resourceBundle.mode_compasses_32();

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return resourceBundle.mode_complexnumber_32();

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return resourceBundle.mode_conic5_32();

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return resourceBundle.mode_copyvisualstyle_32();

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return resourceBundle.mode_countcells_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return resourceBundle.mode_createlist_32();

		case EuclidianConstants.MODE_CREATE_LIST:
			return resourceBundle.mode_createlist_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return resourceBundle.mode_createlistofpoints_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return resourceBundle.mode_creatematrix_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return resourceBundle.mode_createpolyline_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
				return resourceBundle.mode_createtable_32();

		case EuclidianConstants.MODE_DELETE:
			return resourceBundle.mode_eraser_32();

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return resourceBundle.mode_derivative_32();

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return resourceBundle.mode_dilatefrompoint_32();

		case EuclidianConstants.MODE_DISTANCE:
			return resourceBundle.mode_distance_32();

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return resourceBundle.mode_ellipse3_32();

		case EuclidianConstants.MODE_CAS_EVALUATE:
			return resourceBundle.mode_evaluate_32();

		case EuclidianConstants.MODE_CAS_EXPAND:
			return resourceBundle.mode_expand_32();

		case EuclidianConstants.MODE_EXTREMUM:
			return resourceBundle.mode_extremum_32();

		case EuclidianConstants.MODE_CAS_FACTOR:
			return resourceBundle.mode_factor_32();

		case EuclidianConstants.MODE_FITLINE:
			return resourceBundle.mode_fitline_32();

		case EuclidianConstants.MODE_FREEHAND_FUNCTION:
			return resourceBundle.mode_freehandshape_32();

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return resourceBundle.mode_freehandshape_32();

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return resourceBundle.mode_functioninspector_32();

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return resourceBundle.mode_hyperbola3_32();

		case EuclidianConstants.MODE_IMAGE:
			return app.isWhiteboardActive() ? resourceBundle.mode_image_mow_32()
					: resourceBundle.mode_image_32();

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return resourceBundle.mode_integral_32();

		case EuclidianConstants.MODE_INTERSECT:
			return resourceBundle.mode_intersect_32();

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return resourceBundle.mode_intersectioncurve_32();

		case EuclidianConstants.MODE_JOIN:
			return resourceBundle.mode_join_32();

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return resourceBundle.mode_keepinput_32();

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return resourceBundle.mode_linebisector_32();

		case EuclidianConstants.MODE_LOCUS:
			return resourceBundle.mode_locus_32();

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return resourceBundle.mode_maxcells_32();

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return resourceBundle.mode_meancells_32();

		case EuclidianConstants.MODE_MIDPOINT:
			return resourceBundle.mode_midpoint_32();

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return resourceBundle.mode_mincells_32();

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return resourceBundle.mode_mirroratcircle_32();

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return resourceBundle.mode_mirroratline_32();

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return resourceBundle.mode_mirroratpoint_32();

		case EuclidianConstants.MODE_MOVE:
			return resourceBundle.mode_move_32();

		case EuclidianConstants.MODE_SELECT:
			return resourceBundle.mode_select_32();

		case EuclidianConstants.MODE_SELECT_MOW:
			return resourceBundle.mode_select_32();

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return resourceBundle.mode_moverotate_32();

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return resourceBundle.mode_multivarstats_32();

		case EuclidianConstants.MODE_CAS_NUMERIC:
			return resourceBundle.mode_numeric_32();

		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
			return resourceBundle.mode_nsolve_32();

		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return resourceBundle.mode_onevarstats_32();

		case EuclidianConstants.MODE_ORTHOGONAL:
			return resourceBundle.mode_orthogonal_32();

		case EuclidianConstants.MODE_PARABOLA:
			return resourceBundle.mode_parabola_32();

		case EuclidianConstants.MODE_PARALLEL:
			return resourceBundle.mode_parallel_32();

		case EuclidianConstants.MODE_PEN:
			return resourceBundle.mode_pen();

		case EuclidianConstants.MODE_POINT:
			return resourceBundle.mode_point_32();

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return resourceBundle.mode_pointonobject_32();

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return resourceBundle.mode_polardiameter_32();

		case EuclidianConstants.MODE_POLYGON:
			return resourceBundle.mode_polygon_32();

		case EuclidianConstants.MODE_POLYLINE:
			return resourceBundle.mode_polyline_32();

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return resourceBundle.mode_probabilitycalculator_32();

		case EuclidianConstants.MODE_RAY:
			return resourceBundle.mode_ray_32();

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return resourceBundle.mode_regularpolygon_32();

		case EuclidianConstants.MODE_RELATION:
			return resourceBundle.mode_relation_32();

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return resourceBundle.mode_rigidpolygon_32();

		case EuclidianConstants.MODE_ROOTS:
			return resourceBundle.mode_roots_32();

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return resourceBundle.mode_rotatebyangle_32();

		case EuclidianConstants.MODE_SEGMENT:
			return resourceBundle.mode_segment_32();

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return resourceBundle.mode_segmentfixed_32();

		case EuclidianConstants.MODE_SEMICIRCLE:
			return resourceBundle.mode_semicircle_32();

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return ToolbarSvgResourcesSync.INSTANCE.mode_showcheckbox_32();

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return ToolbarSvgResourcesSync.INSTANCE.mode_showhidelabel_32();

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return resourceBundle.mode_showhideobject_32();

		case EuclidianConstants.MODE_SLIDER:
			return ToolbarSvgResourcesSync.INSTANCE.mode_slider_32();

		case EuclidianConstants.MODE_SLOPE:
			return resourceBundle.mode_slope_32();

		case EuclidianConstants.MODE_CAS_SOLVE:
			return resourceBundle.mode_solve_32();

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return resourceBundle.mode_substitute_32();

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return resourceBundle.mode_sumcells_32();

		case EuclidianConstants.MODE_TANGENTS:
			return resourceBundle.mode_tangent_32();

		case EuclidianConstants.MODE_TEXT:
			return resourceBundle.mode_text_32();

		case EuclidianConstants.MODE_MEDIA_TEXT:
			return resourceBundle.mode_media_text();

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return resourceBundle.mode_textfieldaction_32();

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return resourceBundle.mode_translatebyvector_32();

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return resourceBundle.mode_translateview_32();

		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return resourceBundle.mode_twovarstats_32();

		case EuclidianConstants.MODE_GRASPABLE_MATH:
			return resourceBundle.mode_graspablemath_32();

		case EuclidianConstants.MODE_VECTOR:
			return resourceBundle.mode_vector_32();

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return resourceBundle.mode_vectorfrompoint_32();

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return resourceBundle.mode_vectorpolygon_32();

		case EuclidianConstants.MODE_ZOOM_IN:
			return resourceBundle.mode_zoomin_32();

		case EuclidianConstants.MODE_ZOOM_OUT:
			return resourceBundle.mode_zoomout_32();

		/*
		 * 3D
		 */

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return resourceBundle.mode_circleaxispoint_32();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return resourceBundle.mode_circlepointradiusdirection_32();

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			return resourceBundle.mode_cone_32();

		case EuclidianConstants.MODE_CONIFY:
			return resourceBundle.mode_conify_32();

		case EuclidianConstants.MODE_CUBE:
			return resourceBundle.mode_cube_32();

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			return resourceBundle.mode_cylinder_32();

		case EuclidianConstants.MODE_EXTRUSION:
			return resourceBundle.mode_extrusion_32();

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			return resourceBundle.mode_mirroratplane_32();

		case EuclidianConstants.MODE_NET:
			return resourceBundle.mode_net_32();

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return resourceBundle.mode_orthogonalplane_32();

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return resourceBundle.mode_parallelplane_32();

		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return resourceBundle.mode_planethreepoint_32();

		case EuclidianConstants.MODE_PLANE:
			return resourceBundle.mode_plane_32();

		case EuclidianConstants.MODE_PRISM:
			return resourceBundle.mode_prism_32();

		case EuclidianConstants.MODE_PYRAMID:
			return resourceBundle.mode_pyramid_32();

		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return resourceBundle.mode_rotatearoundline_32();

		case EuclidianConstants.MODE_ROTATEVIEW:
			return resourceBundle.mode_rotateview_32();

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return resourceBundle.mode_sphere2_32();

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return resourceBundle.mode_spherepointradius_32();

		case EuclidianConstants.MODE_TETRAHEDRON:
			return resourceBundle.mode_tetrahedron_32();

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return resourceBundle.mode_viewinfrontof_32();

		case EuclidianConstants.MODE_VOLUME:
			return resourceBundle.mode_volume_32();

		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			return resourceBundle.mode_orthogonalthreed_32();

		case EuclidianConstants.MODE_SURFACE_OF_REVOLUTION:
			return resourceBundle.mode_surface_of_revolution();

		/* WHITEBOARD TOOLS */
		case EuclidianConstants.MODE_SHAPE_LINE:
			return resourceBundle.mode_shape_line_32();

		case EuclidianConstants.MODE_SHAPE_TRIANGLE:
			return resourceBundle.mode_shape_triangle_32();

		case EuclidianConstants.MODE_SHAPE_SQUARE:
			return resourceBundle.mode_shape_square_32();

		case EuclidianConstants.MODE_SHAPE_RECTANGLE:
			return resourceBundle.mode_shape_rectangle_32();

		case EuclidianConstants.MODE_SHAPE_PENTAGON:
			return resourceBundle.mode_shape_pentagon_32();

		case EuclidianConstants.MODE_SHAPE_FREEFORM:
			return resourceBundle.mode_shape_freeform_32();

		case EuclidianConstants.MODE_SHAPE_CIRCLE:
			return resourceBundle.mode_shape_circle_32();

		case EuclidianConstants.MODE_SHAPE_ELLIPSE:
			return resourceBundle.mode_shape_ellipse_32();

		case EuclidianConstants.MODE_ERASER:
			return resourceBundle.mode_eraser_32();

		case EuclidianConstants.MODE_HIGHLIGHTER:
			return resourceBundle.mode_highlighter_32();

		case EuclidianConstants.MODE_VIDEO:
			return resourceBundle.mode_video_32();

		case EuclidianConstants.MODE_CAMERA:
			return resourceBundle.mode_camera_32();

		case EuclidianConstants.MODE_AUDIO:
			return resourceBundle.mode_audio_32();

		case EuclidianConstants.MODE_CALCULATOR:
			return resourceBundle.mode_calculator();

		case EuclidianConstants.MODE_EXTENSION:
			return ToolbarSvgResourcesSync.INSTANCE.mode_extension();

		case EuclidianConstants.MODE_H5P:
			return resourceBundle.mode_h5p();

		case EuclidianConstants.MODE_PDF:
			return resourceBundle.mode_pdf_32();

		case EuclidianConstants.MODE_MASK:
			return resourceBundle.mode_mask();

		case EuclidianConstants.MODE_TABLE:
			return resourceBundle.mode_table();

		case EuclidianConstants.MODE_EQUATION:
			return resourceBundle.mode_equation();

		case EuclidianConstants.MODE_MIND_MAP:
			return resourceBundle.mode_mindmap();

		case EuclidianConstants.MODE_RULER:
			return resourceBundle.mode_ruler();

		case EuclidianConstants.MODE_PROTRACTOR:
			return resourceBundle.mode_protractor();

		case EuclidianConstants.MODE_TRIANGLE_PROTRACTOR:
			return resourceBundle.mode_triangle_protractor();

		default:
			return AppResources.INSTANCE.empty();
		}

	}

	/**
	 * @return tool bar
	 */
	public ToolBarW getToolBar() {
		return toolBar;
	}

	/**
	 * Select a mode.
	 * 
	 * @param mode
	 *            new mode
	 * @return -1 //mode that was actually selected
	 */
	@Override
	public int setMode(int mode, ModeSetter ms) {
		return toolbars.get(0).setMode(mode, ms);
	}

	/**
	 * @param index
	 *            0 for open, 1 for menu
	 */
	public void selectMenuButton(int index) {
		deselectButtons();

		if (index == 0 && this.openSearchButton != null) {
			this.openSearchButton.getElement().addClassName("selectedButton");
		} else if (this.openMenuButton != null) {
			this.openMenuButton.getElement().addClassName("selectedButton");
		}

	}

	/**
	 * Deselect both menu and open
	 */
	public void deselectButtons() {
		if (this.openMenuButton != null && openSearchButton != null) {
			this.openSearchButton.getElement().removeClassName("selectedButton");
			this.openMenuButton.getElement().removeClassName("selectedButton");
		}
	}

	/**
	 * Reset the right panel to include menubar; checks if menubar is already
	 * attached
	 */
	public void attachMenubar() {
		if (!this.menuBarShowing) {
			this.rightButtonPanel.removeFromParent();
			this.addRightButtonPanel();
		}
	}

	/**
	 * Update enabled/disabled for undo and redo
	 */
	public void updateUndoActions() {
		if (undoButton != null) {
			this.undoButton.setEnabled(app.getKernel().undoPossible());
		}
		if (this.redoButton != null) {
			this.redoButton.setEnabled(app.getKernel().redoPossible());
		}
	}

	@Override
	public void onResize() {
		setToolbarWidth(
				app.getWidth() <= 0 ? app.getAppletWidth() : app.getWidth());
	}

	/**
	 * @param width
	 *            pixel width
	 */
	public void setToolbarWidth(double width) {
		if (toolbars.get(0).getGroupCount() < 0) {
			return;
		}

		int maxButtons = getMaxButtons((int) width);
		if (maxButtons > 0) {
			toolbars.get(0).setMaxButtons(maxButtons);
		}
		if (toolBar.isMobileToolbar()) {
			int tbwidth = Math.max(toolBar.getToolbarVecSize() * 45, 45);
			toolBar.setWidth(tbwidth + "px");
			toolBPanel.setWidth(maxButtons * 45 + "px");
			toolBPanel.removeStyleName("overflow");
			toolBPanel.addStyleName("toolBPanelMobile");
			rightButtonPanel.addStyleName("rightButtonPanelMobile");
			setSubmenuDimensions(width);
		} else {
			toolBar.setWidth("");
			toolBPanel.setWidth("");
			toolBPanel.removeStyleName("toolBPanelMobile");
			toolBPanel.addStyleName("overflow");
			rightButtonPanel.removeStyleName("rightButtonPanelMobile");
		}
	}

	/**
	 * @param appWidth
	 *            total width
	 * @return number of icons we can fit in toolbar
	 */
	public int getMaxButtons(int appWidth) {
		int extraButtons = 0;
		if (app.getUndoRedoMode() == UndoRedoMode.GUI) {
			extraButtons = 95;
		}
		if (app.showMenuBar()) {
			extraButtons += 90;
		}
		if (!examController.isIdle()) {
			extraButtons += 95;
			if (!app.getSettings().getEuclidian(-1).isEnabled()) {
				extraButtons += 55;
			}
			if (!app.getSettings().getCasSettings().isEnabled()) {
				extraButtons += 55;
			}
		}
		int max = (appWidth - extraButtons - 20) / 45;
		if (max > 1) {
			return max;
		}
		// make sure toolbar is always visible
		return 2;
	}

	/**
	 * sets the with of the submenu dynamically on resize
	 * 
	 * @param width
	 *            toolbar width
	 */
	public void setSubmenuDimensions(double width) {
		if (toolBar.isMobileToolbar() && !toolBar.isVisible()) {
			int maxButtons = getMaxButtons((int) width);
			int submenuButtonCount = 0;
			if (submenuPanel.getWidgetCount() != 0) {
				submenuButtonCount = ((ToolbarSubmenuP) submenuPanel
						.getWidget(0)).getButtonCount();
			}
			submenuScrollPanel.setWidth((maxButtons - 1) * 45 + "px");
			submenuPanel.setWidth(submenuButtonCount * 45 + "px");
		}
	}

	/**
	 * @return the Element object of the open menu button
	 */
	public @CheckForNull Element getOpenMenuButtonElement() {
		return openMenuButton == null ? null : openMenuButton.getElement();
	}

	/**
	 * @param app
	 *            application
	 */
	public static void set1rstMode(AppW app) {
		if (app.isWhiteboardActive()) {
			app.setMode(EuclidianConstants.MODE_PEN, ModeSetter.DOCK_PANEL);
		} else {
			if (app.getToolbar() == null
					|| ((GGWToolBar) app.getToolbar()).getToolBar() == null) {
				return;
			}
			app.setMode(
					((GGWToolBar) app.getToolbar()).getToolBar().getFirstMode(),
					ModeSetter.DOCK_PANEL);
		}
	}

	@Override
	public void closeAllSubmenu() {
		toolBar.closeAllSubmenu();
	}

	/**
	 * @return toolbar panel
	 */
	public FlowPanel getToolBarPanel() {
		return toolBarPanel;
	}

	@Override
	public boolean isMobileToolbar() {
		return toolBar != null && toolBar.isMobileToolbar();
	}

	@Override
	public boolean isShown() {
		return isVisible() && isAttached();
	}
}
