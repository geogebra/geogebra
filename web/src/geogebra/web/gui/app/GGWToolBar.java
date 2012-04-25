package geogebra.web.gui.app;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.toolbar.ToolBar;
import geogebra.web.gui.toolbar.images.MyIconResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite {

	private static GGWToolBarUiBinder uiBinder = GWT
	        .create(GGWToolBarUiBinder.class);

	interface GGWToolBarUiBinder extends UiBinder<VerticalPanel, GGWToolBar> {
	}

	static private MyIconResourceBundle myIconResourceBundle = GWT
	        .create(MyIconResourceBundle.class);

	static public MyIconResourceBundle getMyIconResourceBundle() {
		return myIconResourceBundle;
	}

	private VerticalPanel toolbarPanel = new VerticalPanel(); // just dummy!
	private VerticalPanel toolbars;
	private AbstractApplication app;
	public ToolBar toolBar;
	@UiField
	VerticalPanel toolBarPanel;

	/**
	 * Create a new GGWToolBar object
	 */
	public GGWToolBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Initialisation of the GGWToolbar.
	 * 
	 * @param app
	 */
	public void init(AbstractApplication app) {

		this.app = app;
		toolbars = new VerticalPanel();
		toolBar = new ToolBar();

		toolBarPanel.add(toolBar);
		toolBarPanel.setSize("100%", "100%");
		toolBar.init((geogebra.web.main.Application) app);
		addToolbar(toolBar);
		buildGui();
	}

	/**
	 * Build the toolbar GUI
	 */
	public void buildGui() {
		toolbarPanel = new VerticalPanel();
		updateToolbarPanel();

		// setActiveToolbar(activeToolbar);

	}

	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		AbstractApplication.debug("Implementation needed - just finishing");

		toolbarPanel.clear();
		
		for(Widget toolbar : toolbars) {
			if(toolbar != null) {
				((ToolBar)toolbar).buildGui();
				//TODO
				//toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
				toolBarPanel.add(toolbar);
			}
		}
		
		//TODO
		//toolbarPanel.show(Integer.toString(activeToolbar));
		//toolbarPanel.setVisible(true);
		
	}

	/**
	 * Adds a toolbar to this container. Use updateToolbarPanel() to update the
	 * GUI after all toolbar changes were made.
	 * 
	 * @param toolbar
	 */
	public void addToolbar(ToolBar toolbar) {
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
	public void removeToolbar(ToolBar toolbar) {
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
	public static String getImageHtml(int mode) {

//		String modeText = app.getKernel().getModeText(mode);
//		// bugfix for Turkish locale added Locale.US
//		String iconName = "mode_" +StringUtil.toLowerCase(modeText)
//				+ "_32";
//	
		
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
			return myIconResourceBundle.mode_point_32().getHTML();
		}

		switch (mode) {

		case EuclidianConstants.MODE_ANGLE:
			return myIconResourceBundle.mode_angle_32().getHTML();

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return myIconResourceBundle.mode_anglefixed_32().getHTML();

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return myIconResourceBundle.mode_angularbisector_32().getHTML();

		case EuclidianConstants.MODE_AREA:
			return myIconResourceBundle.mode_area_32().getHTML();

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return myIconResourceBundle.mode_attachdetachpoint_32().getHTML();

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return myIconResourceBundle.mode_buttonaction_32().getHTML();

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return myIconResourceBundle.mode_circle2_32().getHTML();

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return myIconResourceBundle.mode_circle3_32().getHTML();

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.mode_circlearc3_32().getHTML();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return myIconResourceBundle.mode_circlepointradius_32().getHTML();

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.mode_circlesector3_32().getHTML();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.mode_circumcirclearc3_32().getHTML();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.mode_circumcirclesector3_32().getHTML();

		case EuclidianConstants.MODE_COMPASSES:
			return myIconResourceBundle.mode_compasses_32().getHTML();

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return myIconResourceBundle.mode_complexnumber_32().getHTML();

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return myIconResourceBundle.mode_conic5_32().getHTML();

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return myIconResourceBundle.mode_copyvisualstyle_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return myIconResourceBundle.mode_countcells_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return myIconResourceBundle.mode_createlist_32().getHTML();

		case EuclidianConstants.MODE_CREATE_LIST:
			return myIconResourceBundle.mode_createlistgraphicsview_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return myIconResourceBundle.mode_createlistofpoints_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return myIconResourceBundle.mode_creatematrix_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return myIconResourceBundle.mode_createpolyline_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return myIconResourceBundle.mode_createtable_32().getHTML();

		case EuclidianConstants.MODE_DELETE:
			return myIconResourceBundle.mode_delete_32().getHTML();

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return myIconResourceBundle.mode_derivative_32().getHTML();

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return myIconResourceBundle.mode_dilatefrompoint_32().getHTML();

		case EuclidianConstants.MODE_DISTANCE:
			return myIconResourceBundle.mode_distance_32().getHTML();

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return myIconResourceBundle.mode_ellipse3_32().getHTML();

		case EuclidianConstants.MODE_CAS_EVALUATE:
			return myIconResourceBundle.mode_evaluate_32().getHTML();

		case EuclidianConstants.MODE_CAS_EXPAND:
			return myIconResourceBundle.mode_expand_32().getHTML();

		case EuclidianConstants.MODE_CAS_FACTOR:
			return myIconResourceBundle.mode_factor_32().getHTML();

		case EuclidianConstants.MODE_FITLINE:
			return myIconResourceBundle.mode_fitline_32().getHTML();

		case EuclidianConstants.MODE_FREEHAND:
			return myIconResourceBundle.mode_freehand_32().getHTML();

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return myIconResourceBundle.mode_functioninspector_32().getHTML();

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return myIconResourceBundle.mode_hyperbola3_32().getHTML();

		case EuclidianConstants.MODE_IMAGE:
			return myIconResourceBundle.mode_image_32().getHTML();

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return myIconResourceBundle.mode_integral_32().getHTML();

		case EuclidianConstants.MODE_INTERSECT:
			return myIconResourceBundle.mode_intersect_32().getHTML();

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return myIconResourceBundle.mode_intersectioncurve_32().getHTML();

		case EuclidianConstants.MODE_JOIN:
			return myIconResourceBundle.mode_join_32().getHTML();

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return myIconResourceBundle.mode_keepinput_32().getHTML();

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return myIconResourceBundle.mode_linebisector_32().getHTML();

		case EuclidianConstants.MODE_LOCUS:
			return myIconResourceBundle.mode_locus_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return myIconResourceBundle.mode_maxcells_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return myIconResourceBundle.mode_meancells_32().getHTML();

		case EuclidianConstants.MODE_MIDPOINT:
			return myIconResourceBundle.mode_midpoint_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return myIconResourceBundle.mode_mincells_32().getHTML();

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return myIconResourceBundle.mode_mirroratcircle_32().getHTML();

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return myIconResourceBundle.mode_mirroratline_32().getHTML();

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return myIconResourceBundle.mode_mirroratpoint_32().getHTML();

		case EuclidianConstants.MODE_MOVE:
			return myIconResourceBundle.mode_move_32().getHTML();

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return myIconResourceBundle.mode_moverotate_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return myIconResourceBundle.mode_multivarstats_32().getHTML();

		case EuclidianConstants.MODE_CAS_NUMERIC:
			return myIconResourceBundle.mode_numeric_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return myIconResourceBundle.mode_onevarstats_32().getHTML();

		case EuclidianConstants.MODE_ORTHOGONAL:
			return myIconResourceBundle.mode_orthogonal_32().getHTML();

		case EuclidianConstants.MODE_PARABOLA:
			return myIconResourceBundle.mode_parabola_32().getHTML();

		case EuclidianConstants.MODE_PARALLEL:
			return myIconResourceBundle.mode_parallel_32().getHTML();

		case EuclidianConstants.MODE_PEN:
			return myIconResourceBundle.mode_pen_32().getHTML();

		case EuclidianConstants.MODE_POINT:
			return myIconResourceBundle.mode_point_32().getHTML();

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return myIconResourceBundle.mode_pointonobject_32().getHTML();

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return myIconResourceBundle.mode_polardiameter_32().getHTML();

		case EuclidianConstants.MODE_POLYGON:
			return myIconResourceBundle.mode_polygon_32().getHTML();

		case EuclidianConstants.MODE_POLYLINE:
			return myIconResourceBundle.mode_polyline_32().getHTML();

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return myIconResourceBundle.mode_probabilitycalculator_32().getHTML();

		case EuclidianConstants.MODE_RAY:
			return myIconResourceBundle.mode_ray_32().getHTML();

		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			return myIconResourceBundle.mode_recordtospreadsheet_32().getHTML();

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return myIconResourceBundle.mode_regularpolygon_32().getHTML();

		case EuclidianConstants.MODE_RELATION:
			return myIconResourceBundle.mode_relation_32().getHTML();

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return myIconResourceBundle.mode_rigidpolygon_32().getHTML();

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return myIconResourceBundle.mode_rotatebyangle_32().getHTML();

		case EuclidianConstants.MODE_SEGMENT:
			return myIconResourceBundle.mode_segment_32().getHTML();

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return myIconResourceBundle.mode_segmentfixed_32().getHTML();

		case EuclidianConstants.MODE_SEMICIRCLE:
			return myIconResourceBundle.mode_semicircle_32().getHTML();

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return myIconResourceBundle.mode_showcheckbox_32().getHTML();

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return myIconResourceBundle.mode_showhidelabel_32().getHTML();

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return myIconResourceBundle.mode_showhideobject_32().getHTML();

		case EuclidianConstants.MODE_SLIDER:
			return myIconResourceBundle.mode_slider_32().getHTML();

		case EuclidianConstants.MODE_SLOPE:
			return myIconResourceBundle.mode_slope_32().getHTML();

		case EuclidianConstants.MODE_CAS_SOLVE:
			return myIconResourceBundle.mode_solve_32().getHTML();

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return myIconResourceBundle.mode_substitute_32().getHTML();

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return myIconResourceBundle.mode_sumcells_32().getHTML();

		case EuclidianConstants.MODE_TANGENTS:
			return myIconResourceBundle.mode_tangent_32().getHTML();

		case EuclidianConstants.MODE_TEXT:
			return myIconResourceBundle.mode_text_32().getHTML();

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return myIconResourceBundle.mode_textfieldaction_32().getHTML();

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return myIconResourceBundle.mode_translatebyvector_32().getHTML();

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return myIconResourceBundle.mode_translateview_32().getHTML();
			
		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return myIconResourceBundle.mode_twovarstats_32().getHTML();

		case EuclidianConstants.MODE_VECTOR:
			return myIconResourceBundle.mode_vector_32().getHTML();

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return myIconResourceBundle.mode_vectorfrompoint_32().getHTML();

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return myIconResourceBundle.mode_vectorpolygon_32().getHTML();

		case EuclidianConstants.MODE_VISUAL_STYLE:
			return myIconResourceBundle.mode_visualstyle_32().getHTML();

		case EuclidianConstants.MODE_ZOOM_IN:
			return myIconResourceBundle.mode_zoomin_32().getHTML();

		case EuclidianConstants.MODE_ZOOM_OUT:
			return myIconResourceBundle.mode_zoomout_32().getHTML();

		default:
			return "";
		}

	}
}
