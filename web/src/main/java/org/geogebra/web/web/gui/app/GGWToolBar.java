package org.geogebra.web.web.gui.app;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.toolbar.ToolBarW;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.gui.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite implements RequiresResize,
        ToolBarInterface {

	private static final int MENU_ICONS_WIDTH = 200;
	private static final int UNDO_ICONS_WIDTH = 90;
	static protected ToolbarResources myIconResourceBundle = ((ImageFactory) GWT
	        .create(ImageFactory.class)).getToolbarResources();

	static public ToolbarResources getMyIconResourceBundle() {
		return myIconResourceBundle;
	}

	private ArrayList<ToolBarW> toolbars;
	AppW app;
	ToolBarW toolBar;
	//panel which contains the toolbar and undo-redo buttons.
	FlowPanel toolBarPanel;
	//panel for toolbar (without undo-redo buttons)
	FlowPanel toolBPanel;
	boolean inited = false;
	private Integer activeToolbar = -1;
	private boolean menuBarShowing = false;
	
	private FlowPanel rightButtonPanel;
	private StandardButton openSearchButton;
	private StandardButton openMenuButton;
	// private PushButton openSearchButton, openMenuButton;
	StandardButton undoButton;
	private StandardButton redoButton;
	private boolean redoPossible = false;

	/**
	 * Create a new GGWToolBar object
	 */
	public GGWToolBar() {
		toolBarPanel = new FlowPanel();
		toolBarPanel.addStyleName("ggbtoolbarpanel");
		//this makes it draggable on SMART board
		toolBarPanel.addStyleName("smart-nb-draggable");

		//For app we set this also in GGWFrameLayoutPanel, but for applets we must set it here 
		toolBarPanel.setHeight(GLookAndFeelI.TOOLBAR_HEIGHT+"px");
		initWidget(toolBarPanel);
	}

	public boolean isInited() {
		return inited;
	}
	
	public void setActiveToolbar(Integer viewID){
		if (activeToolbar == viewID){
			return;
		}
		activeToolbar = viewID;
		for(ToolBarW bar:toolbars){
			bar.setActiveView(viewID);
		}
	}

	/**
	 * Initialization of the GGWToolbar.
	 * 
	 * @param app1 application
	 */
	public void init(AppW app1) {

		this.inited = true;
		this.app = app1;
		toolbars = new ArrayList<ToolBarW>();
		toolBar = new ToolBarW(this);
		toolBPanel = new FlowPanel();
		toolBarPanel.add(toolBar);
		toolBarPanel.add(toolBPanel);
		toolBarPanel.addStyleName("toolbarPanel");
		if (app.isExam()) {
			toolBarPanel.addStyleName("toolbarPanelExam");
		}
		toolBPanel.setStyleName("toolBPanel");
		
		//toolBarPanel.setSize("100%", "100%");
		toolBar.init(app1);
		addToolbar(toolBar);
		
		
		//Adds the Open and Options Button for SMART
		
		addRightButtonPanel();
		
	}

//	/**
//	 * Build the toolbar GUI
//	 */
//	public void buildGui() {
//
//	}

	//undo-redo buttons
	private void addUndoPanel(){
		PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		//Image redoImage = new Image(GuiResources.INSTANCE.button_redo());
		// redoButton = new StandardButton(pr.button_redo(), null, 32);
		//redoButton.getElement().appendChild(redoImage.getElement());
		// redoButton.addFastClickHandler(new FastClickHandler(){
		// @Override
		// public void onClick(Widget source) {
		// app.getGuiManager().redo();
		// app.hideKeyboard();
		// }
		// });

		redoButton = new StandardButton(
pr.menu_header_redo(), null, 32);
		redoButton.getUpHoveringFace().setImage(
				getImage(pr.menu_header_redo_hover(), 32));

		redoButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				app.getGuiManager().redo();
				app.hideKeyboard();
			}
		});

		redoButton.addStyleName("redoButton");
		//redoButton.getElement().addClassName("button");
		redoButton.setTitle("Redo");
		redoButton.getElement().getStyle().setOverflow(Overflow.HIDDEN);

		//Image undoImage = new Image(GuiResources.INSTANCE.button_undo());
		// undoButton = new StandardButton(pr.button_undo(), null, 32);
		//undoButton.getElement().appendChild(undoImage.getElement());
		// undoButton.addFastClickHandler(new FastClickHandler(){
		// @Override
		// public void onClick(Widget source) {
		// app.getGuiManager().undo();
		// app.hideKeyboard();
		// }
		// });

		undoButton = new StandardButton(
pr.menu_header_undo(), null, 32);
		undoButton.getUpHoveringFace().setImage(
				getImage(pr.menu_header_undo_hover(), 32));

		undoButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				app.getGuiManager().undo();
				app.hideKeyboard();
			}
		});

		undoButton.addStyleName("undoButton");
		//undoButton.getElement().addClassName("button");
		undoButton.setTitle("Undo");
		//toolBarPanel.add(redoButton);
		updateUndoActions();
		rightButtonPanel.add(undoButton);
		rightButtonPanel.add(redoButton);
			
	}

	// timer for GeoGebraExam
	private FlowPanel getTimer() {
		final Label timer = new Label();
		timer.getElement().setClassName("timer");
		timer.getElement().setId("timer");
		timer.getElement().setPropertyBoolean("started", false);

		// https://groups.google.com/forum/#!msg/google-web-toolkit/VrF3KD1iLh4/-y4hkIDt5BUJ
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			@Override
			public void execute(double timestamp) {

				if (app.getExam().isCheating()) {
					getElement().getStyle().setBackgroundColor("red");
					getElement().getStyle().setColor("white");
				}

				timer.setText(app.getExam().timeToString(
						System.currentTimeMillis()));

				AnimationScheduler.get().requestAnimationFrame(this);
			}
		});
		visibilityEventMain();

		FlowPanel fp = new FlowPanel();
		fp.add(timer);
		Image info = new Image(GuiResourcesSimple.INSTANCE.dialog_info()
				.getSafeUri());
		fp.add(info);
		info.getElement().getStyle().setMarginLeft(5, Unit.PX);
		fp.addDomHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				app.showMessage(app.getExam().getLog());

			}
		}, ClickEvent.getType());
		return fp;

	}

	private void startCheating() {
		if (app.getExam() != null) {
			app.getExam().startCheating();
		}
	}

	private void stopCheating() {
		if (app.getExam() != null) {
			app.getExam().stopCheating();
		}
	}

	private native void visibilityEventMain() /*-{
		// wrapper to call the appropriate function from visibility.js
		var that = this;
		var startCheating = function() {
			that.@org.geogebra.web.web.gui.app.GGWToolBar::startCheating()()
		};
		var stopCheating = function() {
			that.@org.geogebra.web.web.gui.app.GGWToolBar::stopCheating()()
		};
		$wnd.visibilityEventMain(startCheating, stopCheating);
		// Suggested by Zbynek (Hero of the Day, 2015-01-22)
		$wnd.onblur = function(event) {
			// Borrowed from http://www.quirksmode.org/js/events_properties.html
			var e = event ? event : $wnd.event;
			var targ;
			if (e.target) {
				targ = e.target;
			} else if (e.srcElement) {
				targ = e.srcElement;
			}
			if (targ.nodeType == 3) { // defeat Safari bug
				targ = targ.parentNode;
			}
			console.log("Checking cheating: Type = " + e.type + ", Target = "
					+ targ + ", " + targ.id + "CurrentTarget = "
					+ e.currentTarget + ", " + e.currentTarget.id);
			// The focusout event should not be caught:
			if (e.type == "blur") {
				startCheating();
			}
		};
		$wnd.onfocus = stopCheating;
	}-*/;
	

	// Undo, redo, open, menu (and exam mode)
	private void addRightButtonPanel(){



		PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		this.rightButtonPanel = new FlowPanel();
		this.rightButtonPanel.setStyleName("rightButtonPanel");
		updateActionPanel();
		toolBarPanel.add(rightButtonPanel);
	}

	public void updateActionPanel() {
		rightButtonPanel.clear();
		boolean exam = app.isExam();
		setStyleName("examToolbar", exam);
		if (exam) {
			// We directly read the parameters to show the intention.
			// It may be possible that 3D is not supported from technical
			// reasons (e.g. the graphics card is problematic), but in such
			// cases we don't want to show that here.

			if (!app.getExam().isCASAllowed()) {
				Label nocas = new Label("CAS");
				nocas.getElement().getStyle()
				        .setTextDecoration(TextDecoration.LINE_THROUGH);
				nocas.getElement().setClassName("timer");
				rightButtonPanel.add(nocas);
			}
			if (!app.getExam().is3DAllowed()) {
				Label no3d = new Label("3D");
				no3d.getElement().getStyle()
				        .setTextDecoration(TextDecoration.LINE_THROUGH);
				no3d.getElement().setClassName("timer");
				rightButtonPanel.add(no3d);
			}
			rightButtonPanel.add(getTimer());
		}

		if(app.getLAF().undoRedoSupported()){
			addUndoPanel();
		}
		if(app.getArticleElement().getDataParamShowMenuBar(false) || 
				app.getArticleElement().getDataParamApp()){
			PerspectiveResources pr = ((ImageFactory) GWT
					.create(ImageFactory.class)).getPerspectiveResources();
		this.menuBarShowing = true;
			// openMenuButton = new StandardButton(pr.button_open_menu(), null,
			// 32);
			//
			// openMenuButton.addFastClickHandler(new FastClickHandler() {
			// @Override
			// public void onClick(Widget source) {
			// app.hideKeyboard();
			// app.closePopups();
			// GGWToolBar.this.app.toggleMenu();
			// }

			openMenuButton = new StandardButton(pr.menu_header_open_menu(),
					null, 32);

			openMenuButton.getUpHoveringFace().setImage(
					getImage(pr.menu_header_open_menu_hover(), 32));

			openMenuButton.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick(Widget source) {
					app.hideKeyboard();
					app.closePopups();
					GGWToolBar.this.app.toggleMenu();
				}
			});

		openMenuButton.addDomHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
	            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
	            	GGWToolBar.this.app.toggleMenu();
	            }
	            if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT){
	            	GGWToolBar.this.selectMenuButton(0);
	            }
	            if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT){
	            	GGWToolBar.this.toolBar.selectMenu(0);
	            }
            }
		}, KeyUpEvent.getType());

		if (!exam && app.enableFileFeatures()) {
				// openSearchButton = new
				// StandardButton(pr.button_open_search(),
				// null, 32);
				// openSearchButton.addFastClickHandler(new FastClickHandler() {
				// @Override
				// public void onClick(Widget source) {
				// app.openSearch(null);
				// }
				// });

				openSearchButton = new StandardButton(
						pr.menu_header_open_search(), null, 32);
				openSearchButton.getUpFace().setImage(
						getImage(pr.menu_header_open_search(), 32));
				openSearchButton.getUpHoveringFace().setImage(
						getImage(pr.menu_header_open_search_hover(), 32));

				openSearchButton.addFastClickHandler(new FastClickHandler() {
					@Override
					public void onClick(Widget source) {
						app.openSearch(null);
					}
				});
		
			openSearchButton.addDomHandler(new KeyUpHandler(){
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
						app.openSearch(null);
					}
					if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT){
						GGWToolBar.this.selectMenuButton(1);
					}
					if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT){
						GGWToolBar.this.toolBar.selectMenu(-1);
					}
				}
			}, KeyUpEvent.getType());
		
			this.rightButtonPanel.add(openSearchButton);
		}
		this.rightButtonPanel.add(openMenuButton);
		}

	}

	public NoDragImage getImage(ResourcePrototype uri, int width) {
		return new NoDragImage(GGWToolBar.safeURI(uri), width);
	}

	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		toolBPanel.clear();
		for(ToolBarW toolbar : toolbars) {
			if(toolbar != null) {
				toolbar.buildGui();
				//TODO
				//toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
				toolBPanel.add(toolbar);
			}
		}
		
		//TODO
		//toolbarPanel.show(Integer.toString(activeToolbar));
		onResize();
		toolBPanel.setVisible(true);
	}

	/**
	 * Adds a toolbar to this container. Use updateToolbarPanel() to update the
	 * GUI after all toolbar changes were made.
	 * 
	 * @param toolbar
	 */
	public void addToolbar(ToolBarW toolbar) {
		toolbars.add(toolbar);
	}

	/**
	 * Removes a toolbar from this container. Use {@link #updateToolbarPanel()}
	 * to update the GUI after all toolbar changes were made. If the removed
	 * toolbar was the active toolbar as well the active toolbar is changed to
	 * the general (but again, {@link #updateToolbarPanel()} has to be called
	 * for a visible effect).
	 * 
	 * @param toolbar
	 */
	public void removeToolbar(ToolBarW toolbar) {
		toolbars.remove(toolbar);
		/*AGif(getViewId(toolbar) == activeToolbar) {
			activeToolbar = -1;
		}*/
	}

	/**
	 * Gets an HTML fragment that displays the image belonging to mode
	 * given in parameter
	 * 
	 * @param mode
	 * @return HTML fragment
	 */
	public String getImageHtml(int mode){
		String url = getImageURL(mode);
		return (url.length()>0) ? "<img src=\""+url+"\" width=\"32\">" : "";
	}

	public String getImageURL(int mode) {
		return getImageURL(mode, app);
	}
	
	public static String getImageURL(int mode, AppW app) {
		

//		String modeText = app.getKernel().getModeText(mode);
//		// bugfix for Turkish locale added Locale.US
//		String iconName = "mode_" +StringUtil.toLowerCase(modeText)
//				+ "_32";
//

		// macro
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = app.getKernel().getMacro(macroID);
				String iconName = macro.getIconFileName();
				if (iconName == null || iconName.length()==0) {
					// default icon
					return safeURI(myIconResourceBundle.mode_tool_32());
				}
				// use image as icon
				Image img = new NoDragImage(app.getImageManager().getExternalImageSrc(iconName),32);
				return img.getUrl();
			} catch (Exception e) {
				App.debug("macro does not exist: ID = " + macroID);
				return "";
			}
		}
		
		return safeURI(getImageURLNotMacro(mode));
		
	}
		
	public static String safeURI(ResourcePrototype res) {
	    if(res instanceof ImageResource){
	    	return ((ImageResource)res).getSafeUri().asString();
	    }
	    if(res instanceof SVGResource){
	    	return ((SVGResource)res).getSafeUri().asString();
	    }
	    return "";
    }

	protected static ResourcePrototype getImageURLNotMacro(int mode) {
		switch (mode) {

		case EuclidianConstants.MODE_ANGLE:
			return myIconResourceBundle.mode_angle_32();

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return myIconResourceBundle.mode_anglefixed_32();

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return myIconResourceBundle.mode_angularbisector_32();

		case EuclidianConstants.MODE_AREA:
			return myIconResourceBundle.mode_area_32();

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return myIconResourceBundle.mode_attachdetachpoint_32();

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return myIconResourceBundle.mode_buttonaction_32();

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return myIconResourceBundle.mode_circle2_32();

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return myIconResourceBundle.mode_circle3_32();

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.mode_circlearc3_32();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return myIconResourceBundle.mode_circlepointradius_32();

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.mode_circlesector3_32();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.mode_circumcirclearc3_32();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.mode_circumcirclesector3_32();

		case EuclidianConstants.MODE_COMPASSES:
			return myIconResourceBundle.mode_compasses_32();

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return myIconResourceBundle.mode_complexnumber_32();

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return myIconResourceBundle.mode_conic5_32();

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return myIconResourceBundle.mode_copyvisualstyle_32();

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return myIconResourceBundle.mode_countcells_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return myIconResourceBundle.mode_createlist_32();

		case EuclidianConstants.MODE_CREATE_LIST:
			return myIconResourceBundle.mode_createlist_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return myIconResourceBundle.mode_createlistofpoints_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return myIconResourceBundle.mode_creatematrix_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return myIconResourceBundle.mode_createpolyline_32();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return myIconResourceBundle.mode_createtable_32();

		case EuclidianConstants.MODE_DELETE:
			return myIconResourceBundle.mode_delete_32();

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return myIconResourceBundle.mode_derivative_32();

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return myIconResourceBundle.mode_dilatefrompoint_32();

		case EuclidianConstants.MODE_DISTANCE:
			return myIconResourceBundle.mode_distance_32();

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return myIconResourceBundle.mode_ellipse3_32();

		case EuclidianConstants.MODE_CAS_EVALUATE:
			return myIconResourceBundle.mode_evaluate_32();

		case EuclidianConstants.MODE_CAS_EXPAND:
			return myIconResourceBundle.mode_expand_32();

		case EuclidianConstants.MODE_CAS_FACTOR:
			return myIconResourceBundle.mode_factor_32();

		case EuclidianConstants.MODE_FITLINE:
			return myIconResourceBundle.mode_fitline_32();

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return myIconResourceBundle.mode_freehandshape_32();

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return myIconResourceBundle.mode_functioninspector_32();

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return myIconResourceBundle.mode_hyperbola3_32();

		case EuclidianConstants.MODE_IMAGE:
			return myIconResourceBundle.mode_image_32();

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return myIconResourceBundle.mode_integral_32();

		case EuclidianConstants.MODE_INTERSECT:
			return myIconResourceBundle.mode_intersect_32();

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return myIconResourceBundle.mode_intersectioncurve_32();

		case EuclidianConstants.MODE_JOIN:
			return myIconResourceBundle.mode_join_32();

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return myIconResourceBundle.mode_keepinput_32();

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return myIconResourceBundle.mode_linebisector_32();

		case EuclidianConstants.MODE_LOCUS:
			return myIconResourceBundle.mode_locus_32();

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return myIconResourceBundle.mode_maxcells_32();

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return myIconResourceBundle.mode_meancells_32();

		case EuclidianConstants.MODE_MIDPOINT:
			return myIconResourceBundle.mode_midpoint_32();

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return myIconResourceBundle.mode_mincells_32();

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return myIconResourceBundle.mode_mirroratcircle_32();

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return myIconResourceBundle.mode_mirroratline_32();

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return myIconResourceBundle.mode_mirroratpoint_32();

		case EuclidianConstants.MODE_MOVE:
			return myIconResourceBundle.mode_move_32();

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return myIconResourceBundle.mode_moverotate_32();

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return myIconResourceBundle.mode_multivarstats_32();
			
		case EuclidianConstants.MODE_CAS_NUMERIC:
			return myIconResourceBundle.mode_numeric_32();
			
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
			return myIconResourceBundle.mode_nsolve_32();

		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return myIconResourceBundle.mode_onevarstats_32();

		case EuclidianConstants.MODE_ORTHOGONAL:
			return myIconResourceBundle.mode_orthogonal_32();

		case EuclidianConstants.MODE_PARABOLA:
			return myIconResourceBundle.mode_parabola_32();

		case EuclidianConstants.MODE_PARALLEL:
			return myIconResourceBundle.mode_parallel_32();

		case EuclidianConstants.MODE_PEN:
			return myIconResourceBundle.mode_pen_32();

		case EuclidianConstants.MODE_POINT:
			return myIconResourceBundle.mode_point_32();

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return myIconResourceBundle.mode_pointonobject_32();

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return myIconResourceBundle.mode_polardiameter_32();

		case EuclidianConstants.MODE_POLYGON:
			return myIconResourceBundle.mode_polygon_32();

		case EuclidianConstants.MODE_POLYLINE:
			return myIconResourceBundle.mode_polyline_32();

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return myIconResourceBundle.mode_probabilitycalculator_32();

		case EuclidianConstants.MODE_RAY:
			return myIconResourceBundle.mode_ray_32();

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return myIconResourceBundle.mode_regularpolygon_32();

		case EuclidianConstants.MODE_RELATION:
			return myIconResourceBundle.mode_relation_32();

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return myIconResourceBundle.mode_rigidpolygon_32();

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return myIconResourceBundle.mode_rotatebyangle_32();

		case EuclidianConstants.MODE_SEGMENT:
			return myIconResourceBundle.mode_segment_32();

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return myIconResourceBundle.mode_segmentfixed_32();

		case EuclidianConstants.MODE_SEMICIRCLE:
			return myIconResourceBundle.mode_semicircle_32();

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return myIconResourceBundle.mode_showcheckbox_32();

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return myIconResourceBundle.mode_showhidelabel_32();

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return myIconResourceBundle.mode_showhideobject_32();

		case EuclidianConstants.MODE_SLIDER:
			return myIconResourceBundle.mode_slider_32();

		case EuclidianConstants.MODE_SLOPE:
			return myIconResourceBundle.mode_slope_32();

		case EuclidianConstants.MODE_CAS_SOLVE:
			return myIconResourceBundle.mode_solve_32();

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return myIconResourceBundle.mode_substitute_32();

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return myIconResourceBundle.mode_sumcells_32();

		case EuclidianConstants.MODE_TANGENTS:
			return myIconResourceBundle.mode_tangent_32();

		case EuclidianConstants.MODE_TEXT:
			return myIconResourceBundle.mode_text_32();

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return myIconResourceBundle.mode_textfieldaction_32();

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return myIconResourceBundle.mode_translatebyvector_32();

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return myIconResourceBundle.mode_translateview_32();
			
		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return myIconResourceBundle.mode_twovarstats_32();

		case EuclidianConstants.MODE_VECTOR:
			return myIconResourceBundle.mode_vector_32();

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return myIconResourceBundle.mode_vectorfrompoint_32();

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return myIconResourceBundle.mode_vectorpolygon_32();

		case EuclidianConstants.MODE_ZOOM_IN:
			return myIconResourceBundle.mode_zoomin_32();

		case EuclidianConstants.MODE_ZOOM_OUT:
			return myIconResourceBundle.mode_zoomout_32();
			
			
			
			
			/*
			 * 3D
			 */
			
		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return myIconResourceBundle.mode_circleaxispoint_32();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return myIconResourceBundle.mode_circlepointradiusdirection_32();

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			return myIconResourceBundle.mode_cone_32();

		case EuclidianConstants.MODE_CONIFY:
			return myIconResourceBundle.mode_conify_32();

		case EuclidianConstants.MODE_CUBE:
			return myIconResourceBundle.mode_cube_32();

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			return myIconResourceBundle.mode_cylinder_32();

		case EuclidianConstants.MODE_EXTRUSION:
			return myIconResourceBundle.mode_extrusion_32();

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			return myIconResourceBundle.mode_mirroratplane_32();

		case EuclidianConstants.MODE_NET:
			return myIconResourceBundle.mode_net_32();

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return myIconResourceBundle.mode_orthogonalplane_32();

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return myIconResourceBundle.mode_parallelplane_32();

		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return myIconResourceBundle.mode_planethreepoint_32();

		case EuclidianConstants.MODE_PLANE:
			return myIconResourceBundle.mode_plane_32();

		case EuclidianConstants.MODE_PRISM:
			return myIconResourceBundle.mode_prism_32();

		case EuclidianConstants.MODE_PYRAMID:
			return myIconResourceBundle.mode_pyramid_32();
			
		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return myIconResourceBundle.mode_rotatearoundline_32();

		case EuclidianConstants.MODE_ROTATEVIEW:
			return myIconResourceBundle.mode_rotateview_32();

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return myIconResourceBundle.mode_sphere2_32();

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return myIconResourceBundle.mode_spherepointradius_32();

		case EuclidianConstants.MODE_TETRAHEDRON:
			return myIconResourceBundle.mode_tetrahedron_32();

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return myIconResourceBundle.mode_viewinfrontof_32();

		case EuclidianConstants.MODE_VOLUME:
			return myIconResourceBundle.mode_volume_32();
			
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			return myIconResourceBundle.mode_orthogonalthreed_32();
			
		

		default:
			return AppResources.INSTANCE.empty();
		}

	}
	
	/**
	 * @return tool bar
	 */
	public ToolBarW getToolBar(){
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
	 * @param toolbar
	 * @return The ID of the dock panel associated with the passed toolbar or -1
	 */
	private static int getViewId(ToolBarW toolbar) {
		return (toolbar.getDockPanel() != null ? toolbar.getDockPanel()
				.getViewId() : -1);
	}
	
	@Override
	protected void onAttach(){
		super.onAttach();
		// gwt sets openSearcButton's tabindex to 0 at onAttach (see
		// FocusWidget.onAttach())
		// but we don't want to select openSearchButton with tab, so tabindex will
		// be set back to -1 after attach all time.
		if(this.openSearchButton != null){
			this.openSearchButton.setTabIndex(-1);
		}
		if(this.openMenuButton != null){
			this.openMenuButton.setTabIndex(-1);
		}
	}

	public void selectMenuButton(int index) {
		deselectButtons();

		// MyToggleButton2 focused = index == 0 ? this.openSearchButton
		// : this.openMenuButton;
		// if(focused != null){
		// focused.setFocus(true);
		// focused.getElement().addClassName("selectedButton");
		// }

		if (index == 0) {
			this.openSearchButton.getElement().addClassName("selectedButton");
		} else {
			this.openMenuButton.getElement().addClassName("selectedButton");
		}
	    
    }

	public void deselectButtons() {
		this.openSearchButton.getElement().removeClassName("selectedButton");
		this.openMenuButton.getElement().removeClassName("selectedButton");
    }

	public void attachMenubar() {
		if(!this.menuBarShowing){
			this.rightButtonPanel.removeFromParent();
			this.addRightButtonPanel();
		}
    }

	public void updateUndoActions() {
		if(undoButton != null){
			this.undoButton.setEnabled(app.getKernel().undoPossible());
		}
		if(this.redoButton != null){
			this.redoButton.setEnabled(app.getKernel().redoPossible());
		}
	}

	@Override
    public void onResize() {
		 if(toolbars.get(0).getGroupCount() < 0){ 
	 	        return; 
         }
		 
		int maxButtons = getMaxButtons();
		if (maxButtons > 0) {
			toolbars.get(0).setMaxButtons(maxButtons);
		}

	}

	private int getMaxButtons() {
		int extraButtons = 0;
		 if(app.getLAF().undoRedoSupported()){
			extraButtons = 90;
		 }
		 if(app.showMenuBar()){
			extraButtons += 90;
		 }
		if (app.isExam()) {
			extraButtons += 30;
			if (!app.getExam().is3DAllowed()) {
				extraButtons += 55;
			}
			if (!app.getExam().isCASAllowed()) {
				extraButtons += 55;
			}
		}
		return ((int) app.getWidth() - extraButtons - 15) / 45;
	}

	/**
	 * @return the Element object of the open menu button
	 */
	public Element getOpenMenuButtonElement() {
		return openMenuButton.getElement();
	}

	public static void set1rstMode(AppW app) {
		if (app.getToolbar() == null) return;
		if (((GGWToolBar)app.getToolbar()).getToolBar() == null) return;
		
		app.setMode(((GGWToolBar)app.getToolbar()).
				getToolBar().
getFirstMode(),
		        ModeSetter.DOCK_PANEL);
	    
    }

}
