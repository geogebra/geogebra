package geogebra.web.gui.app;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.App;
import geogebra.html5.gui.ToolbarResources;
import geogebra.web.WebStatic;
import geogebra.web.WebStatic.GuiToLoad;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.toolbar.ToolBarW;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite {

	private static GGWToolBarUiBinder uiBinder = GWT
	        .create(GGWToolBarUiBinder.class);

	interface GGWToolBarUiBinder extends UiBinder<HorizontalPanel, GGWToolBar> {
	}

	static private ToolbarResources myIconResourceBundle = GWT
	        .create(ToolbarResources.class);

	static public ToolbarResources getMyIconResourceBundle() {
		return myIconResourceBundle;
	}

	private ArrayList<ToolBarW> toolbars;
	App app;
	static private ToolBarW toolBar;
	@UiField
	HorizontalPanel toolBarPanel;
	boolean inited = false;

	/**
	 * Create a new GGWToolBar object
	 */
	public GGWToolBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public boolean isInited() {
		return inited;
	}
	
	public void setActiveToolbar(Integer viewID){
		for(ToolBarW bar:toolbars){
			bar.setActiveView(viewID);
		}
	}

	/**
	 * Initialisation of the GGWToolbar.
	 * 
	 * @param app1 application
	 */
	public void init(App app1) {

		this.inited = true;
		this.app = app1;
		toolbars = new ArrayList<ToolBarW>();
		toolBar = new ToolBarW();

		toolBarPanel.add(toolBar);
		toolBarPanel.addStyleName("toolbarPanel");
		
		//toolBarPanel.setSize("100%", "100%");
		toolBar.init((geogebra.web.main.AppW) app);
		addToolbar(toolBar);
		buildGui();
	}

	/**
	 * Build the toolbar GUI
	 */
	public void buildGui() {
		
		if(!WebStatic.currentGUI.equals(GuiToLoad.APP)){
			updateToolbarPanel();  //currently it's needed for applet, maybe later this will be unnecessary
		}
		// setActiveToolbar(activeToolbar);
		
		//undo-redo buttons

		Image redoImage = new Image(AppResources.INSTANCE.edit_redo());
		Button redoButton = new Button();
		redoButton.getElement().appendChild(redoImage.getElement());
		redoButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				app.getGuiManager().redo();
            }
		});
		redoButton.setStyleName("redoButton");
		redoButton.setTitle("Redo");
	
		Image undoImage = new Image(AppResources.INSTANCE.edit_undo());
		Button undoButton = new Button();
		undoButton.getElement().appendChild(undoImage.getElement());
		undoButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				app.getGuiManager().undo();
            }
		});
		undoButton.setStyleName("undoButton");
		undoButton.setTitle("Undo");
		//toolBarPanel.add(redoButton);
		
		HorizontalPanel undoPanel = new HorizontalPanel();
		undoPanel.addStyleName("undoPanel");
		undoPanel.add(undoButton);
		undoPanel.add(redoButton);
		toolBarPanel.add(undoPanel);
	}

	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		toolBarPanel.clear();
		for(Widget toolbar : toolbars) {
			if(toolbar != null) {
				((ToolBarW)toolbar).buildGui();
				//TODO
				//toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
				toolBarPanel.add(toolbar);
			}
		}
		
		//TODO
		//toolbarPanel.show(Integer.toString(activeToolbar));

		toolBarPanel.setVisible(true);
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
	public static String getImageHtml(int mode){
		String url = getImageURL(mode);
		return (url.length()>0) ? "<img src=\""+url+"\">" : "";
	}
	
	
	public static String getImageURL(int mode) {

//		String modeText = app.getKernel().getModeText(mode);
//		// bugfix for Turkish locale added Locale.US
//		String iconName = "mode_" +StringUtil.toLowerCase(modeText)
//				+ "";
//	
		
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
			return myIconResourceBundle.new_point().getSafeUri().asString();
		}

		switch (mode) {

		case EuclidianConstants.MODE_ANGLE:
			return myIconResourceBundle.angle().getSafeUri().asString();

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return myIconResourceBundle.angle_fixed().getSafeUri().asString();

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return myIconResourceBundle.angle_bisector().getSafeUri().asString();

		case EuclidianConstants.MODE_AREA:
			return myIconResourceBundle.area().getSafeUri().asString();

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return myIconResourceBundle.attach_detach_point().getSafeUri().asString();

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return myIconResourceBundle.insert_button().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return myIconResourceBundle.circle_with_center_through_point().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return myIconResourceBundle.circle_through_three_points().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.circular_arc_with_center_between_two_points().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return myIconResourceBundle.circle_with_center_and_radius().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.circular_sector_with_center_between_two_points().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.circumcircular_arc_through_three_points().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.circumcircular_sector_through_three_points().getSafeUri().asString();

		case EuclidianConstants.MODE_COMPASSES:
			return myIconResourceBundle.compasses().getSafeUri().asString();

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return myIconResourceBundle.complex_number().getSafeUri().asString();

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return myIconResourceBundle.conic_through_5_points().getSafeUri().asString();

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return myIconResourceBundle.copy_visual_style().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return myIconResourceBundle.count().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
		case EuclidianConstants.MODE_CREATE_LIST:
			return myIconResourceBundle.create_list().getSafeUri().asString();//they seem to be the same

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return myIconResourceBundle.create_point_list().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return myIconResourceBundle.create_matrix().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return myIconResourceBundle.create_polyline().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return myIconResourceBundle.create_table().getSafeUri().asString();

		case EuclidianConstants.MODE_DELETE:
			return myIconResourceBundle.delete_object().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return myIconResourceBundle.derivative().getSafeUri().asString();

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return myIconResourceBundle.dilate_object_from_point_by_factor().getSafeUri().asString();

		case EuclidianConstants.MODE_DISTANCE:
			return myIconResourceBundle.distance_or_length().getSafeUri().asString();

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return myIconResourceBundle.ellipse().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_EVALUATE:
			return myIconResourceBundle.evaluate().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_EXPAND:
			return myIconResourceBundle.expand().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_FACTOR:
			return myIconResourceBundle.factor().getSafeUri().asString();

		case EuclidianConstants.MODE_FITLINE:
			return myIconResourceBundle.best_fit_line().getSafeUri().asString();

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return myIconResourceBundle.freehand_shape().getSafeUri().asString();

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return myIconResourceBundle.function_inspector().getSafeUri().asString();

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return myIconResourceBundle.hyperbola().getSafeUri().asString();

		case EuclidianConstants.MODE_IMAGE:
			return myIconResourceBundle.insert_image().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return myIconResourceBundle.integral().getSafeUri().asString();
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			//return myIconResourceBundle.intersectioncurve().getSafeUri().asString(); TODO svg icon missing
			
		case EuclidianConstants.MODE_INTERSECT:
			return myIconResourceBundle.intersect_two_objects().getSafeUri().asString();

		

		case EuclidianConstants.MODE_JOIN:
			return myIconResourceBundle.line_through_two_points().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return myIconResourceBundle.keep_input().getSafeUri().asString();

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return myIconResourceBundle.perpendicular_bisector().getSafeUri().asString();

		case EuclidianConstants.MODE_LOCUS:
			return myIconResourceBundle.locus().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return myIconResourceBundle.max().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return myIconResourceBundle.mean().getSafeUri().asString();

		case EuclidianConstants.MODE_MIDPOINT:
			return myIconResourceBundle.midpoint_or_center().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return myIconResourceBundle.min().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return myIconResourceBundle.reflect_object_about_circle().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return myIconResourceBundle.reflect_object_about_line().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return myIconResourceBundle.reflect_object_about_point().getSafeUri().asString();

		case EuclidianConstants.MODE_MOVE:
			return myIconResourceBundle.move().getSafeUri().asString();

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return myIconResourceBundle.rotate_around_point().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return myIconResourceBundle.multiple_variable().getSafeUri().asString();
			
		case EuclidianConstants.MODE_CAS_NUMERIC:
			return myIconResourceBundle.numeric().getSafeUri().asString();
			
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
			return myIconResourceBundle.nsolve().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return myIconResourceBundle.one_variable().getSafeUri().asString();

		case EuclidianConstants.MODE_ORTHOGONAL:
			return myIconResourceBundle.perpendicular_line().getSafeUri().asString();

		case EuclidianConstants.MODE_PARABOLA:
			return myIconResourceBundle.parabola().getSafeUri().asString();

		case EuclidianConstants.MODE_PARALLEL:
			return myIconResourceBundle.parallel_line().getSafeUri().asString();

		case EuclidianConstants.MODE_PEN:
			return myIconResourceBundle.pen().getSafeUri().asString();

		case EuclidianConstants.MODE_POINT:
			return myIconResourceBundle.new_point().getSafeUri().asString();

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return myIconResourceBundle.point_on_object().getSafeUri().asString();

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return myIconResourceBundle.polar_or_diameter_line().getSafeUri().asString();

		case EuclidianConstants.MODE_POLYGON:
			return myIconResourceBundle.polygon().getSafeUri().asString();

		case EuclidianConstants.MODE_POLYLINE:
			return myIconResourceBundle.polyline_between_points().getSafeUri().asString();

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return myIconResourceBundle.probability_calculator().getSafeUri().asString();

		case EuclidianConstants.MODE_RAY:
			return myIconResourceBundle.ray_through_two_points().getSafeUri().asString();

		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			return myIconResourceBundle.record_to_spreadsheet().getSafeUri().asString();

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return myIconResourceBundle.regular_polygon().getSafeUri().asString();

		case EuclidianConstants.MODE_RELATION:
			return myIconResourceBundle.relation_between_two_objects().getSafeUri().asString();

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return myIconResourceBundle.rigid_polygon().getSafeUri().asString();

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return myIconResourceBundle.rotate_object_about_point_by_angle().getSafeUri().asString();

		case EuclidianConstants.MODE_SEGMENT:
			return myIconResourceBundle.segment_between_two_points().getSafeUri().asString();

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return myIconResourceBundle.segment_with_given_length_from_point().getSafeUri().asString();

		case EuclidianConstants.MODE_SEMICIRCLE:
			return myIconResourceBundle.semicircle().getSafeUri().asString();

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return myIconResourceBundle.checkbox_to_show_hide_objects().getSafeUri().asString();

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return myIconResourceBundle.label().getSafeUri().asString();

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return myIconResourceBundle.show_hide_object().getSafeUri().asString();

		case EuclidianConstants.MODE_SLIDER:
			return myIconResourceBundle.slider().getSafeUri().asString();

		case EuclidianConstants.MODE_SLOPE:
			return myIconResourceBundle.slope().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_SOLVE:
			return myIconResourceBundle.solve().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return myIconResourceBundle.substitute().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return myIconResourceBundle.sum().getSafeUri().asString();

		case EuclidianConstants.MODE_TANGENTS:
			return myIconResourceBundle.tangents().getSafeUri().asString();

		case EuclidianConstants.MODE_TEXT:
			return myIconResourceBundle.insert_text().getSafeUri().asString();

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return myIconResourceBundle.insert_input_box().getSafeUri().asString();

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return myIconResourceBundle.translate_object_by_vector().getSafeUri().asString();

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return myIconResourceBundle.move_graphics_view().getSafeUri().asString();
			
		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return myIconResourceBundle.two_variable().getSafeUri().asString(); 

		case EuclidianConstants.MODE_VECTOR:
			return myIconResourceBundle.vector_between_two_points().getSafeUri().asString();

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return myIconResourceBundle.vector_from_point().getSafeUri().asString();

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return myIconResourceBundle.vector_polygon().getSafeUri().asString();

		case EuclidianConstants.MODE_ZOOM_IN:
			return myIconResourceBundle.zoom_in().getSafeUri().asString();

		case EuclidianConstants.MODE_ZOOM_OUT:
			return myIconResourceBundle.zoom_out().getSafeUri().asString();

		default:
			return "";
		}

	}
	
	/**
	 * @return tool bar
	 */
	public static ToolBarW getToolBar(){
		return toolBar;
	}
}
