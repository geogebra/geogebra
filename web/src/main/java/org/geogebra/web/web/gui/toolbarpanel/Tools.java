package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Category;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.web.css.ToolbarSvgResources;

import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel {

	/**
	 * Tool categories
	 */
	ToolCategorization mToolCategorization;
	/**
	 * application
	 */
	AppW app;
	/**
	 * move button
	 */
	StandardButton moveButton;

	/**
	 * @param appl
	 *            application
	 */
	public Tools(AppW appl) {
		app = appl;
		this.addStyleName("toolsPanel");
		buildGui();
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		app.setMode(EuclidianConstants.MODE_MOVE);
		clearSelectionStyle();
		if (moveButton != null) {
			moveButton.getElement().setAttribute("selected", "true");
		}
	}

	/**
	 * Changes visual settings of selected mode.
	 * 
	 * @param mode
	 *            the mode will be selected
	 */
	public void setMode(int mode) {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					if ((mode + "").equals(panelTools.getWidget(j).getElement()
							.getAttribute("mode"))) {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "true");

					} else {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "false");
					}
				}
			}
		}

	}

	/**
	 * Clears visual selection of all tools.
	 */
	public void clearSelectionStyle() {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					panelTools.getWidget(j).getElement()
							.setAttribute("selected", "false");
				}
			}
		}
	}

	/**
	 * Builds the panel of tools.
	 */
	public void buildGui() {
		this.clear();

		mToolCategorization = new ToolCategorization(app,
				app.getSettings().getToolbarSettings().getType(), app.getSettings().getToolbarSettings().getToolsetLevel(), false);
		mToolCategorization.resetTools();
		ArrayList<ToolCategorization.Category> categories = mToolCategorization
				.getCategories();

		for (int i = 0; i < categories.size(); i++) {
			add(new CategoryPanel(categories.get(i)));
		}
		
		setMoveMode();
	}

	private class CategoryPanel extends FlowPanel {
		private Category category;
		private FlowPanel toolsPanel;
		private ToolbarSvgResources toolSvgRes = ToolbarSvgResources.INSTANCE;

		public CategoryPanel(ToolCategorization.Category cat) {
			super();
			category = cat;
			initGui();
		}

		private void initGui() {
			add(new Label(mToolCategorization.getLocalizedHeader(category)));

			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			ArrayList<Integer> tools = mToolCategorization.getTools(
					mToolCategorization.getCategories().indexOf(category));

			for (int i = 0; i < tools.size(); i++) {
				StandardButton btn = getButton(tools.get(i));
				toolsPanel.add(btn);
				if (tools.get(i) == EuclidianConstants.MODE_MOVE) {
					moveButton = btn;
				}
			}
			add(toolsPanel);
		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		/**
		 * @param mode
		 *            app mode
		 * @return toolbar icon resource
		 */
		private SVGResource getSvgImageForTool(int mode) {
			switch (mode) {

			case EuclidianConstants.MODE_ANGLE:
				return toolSvgRes.mode_angle_32();

			case EuclidianConstants.MODE_ANGLE_FIXED:
				return toolSvgRes.mode_anglefixed_32();

			case EuclidianConstants.MODE_ANGULAR_BISECTOR:
				return toolSvgRes.mode_angularbisector_32();

			case EuclidianConstants.MODE_AREA:
				return toolSvgRes.mode_area_32();

			case EuclidianConstants.MODE_ATTACH_DETACH:
				return toolSvgRes.mode_attachdetachpoint_32();

			case EuclidianConstants.MODE_BUTTON_ACTION:
				return toolSvgRes.mode_buttonaction_32();

			case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
				return toolSvgRes.mode_circle2_32();

			case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
				return toolSvgRes.mode_circle3_32();

			case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
				return toolSvgRes.mode_circlearc3_32();

			case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
				return toolSvgRes.mode_circlepointradius_32();

			case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
				return toolSvgRes.mode_circlesector3_32();

			case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
				return toolSvgRes.mode_circumcirclearc3_32();

			case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
				return toolSvgRes.mode_circumcirclesector3_32();

			case EuclidianConstants.MODE_COMPASSES:
				return toolSvgRes.mode_compasses_32();

			case EuclidianConstants.MODE_COMPLEX_NUMBER:
				return toolSvgRes.mode_complexnumber_32();

			case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
				return toolSvgRes.mode_conic5_32();

			case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
				return toolSvgRes.mode_copyvisualstyle_32();

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_COUNT: return
			 * toolSvgRes.mode_countcells_32();
			 */

			case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
				return toolSvgRes.mode_createlist_32();

			case EuclidianConstants.MODE_CREATE_LIST:
				return toolSvgRes.mode_createlist_32();

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			 * return toolSvgRes.mode_createlistofpoints_32();
			 * 
			 * case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX: return
			 * toolSvgRes.mode_creatematrix_32();
			 * 
			 * case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE: return
			 * toolSvgRes.mode_createpolyline_32();
			 * 
			 * case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT: return
			 * toolSvgRes.mode_createtable_32();
			 */

			case EuclidianConstants.MODE_DELETE:
				return toolSvgRes.mode_delete_32();

			/*
			 * case EuclidianConstants.MODE_CAS_DERIVATIVE: return
			 * toolSvgRes.mode_derivative_32();
			 */

			case EuclidianConstants.MODE_DILATE_FROM_POINT:
				return toolSvgRes.mode_dilatefrompoint_32();

			case EuclidianConstants.MODE_DISTANCE:
				return toolSvgRes.mode_distance_32();

			case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
				return toolSvgRes.mode_ellipse3_32();

			/*
			 * case EuclidianConstants.MODE_CAS_EVALUATE: return
			 * toolSvgRes.mode_evaluate_32();
			 * 
			 * case EuclidianConstants.MODE_CAS_EXPAND: return
			 * toolSvgRes.mode_expand_32();
			 */

			case EuclidianConstants.MODE_EXTREMUM:
				return toolSvgRes.mode_extremum_32();

			/*
			 * case EuclidianConstants.MODE_CAS_FACTOR: return
			 * toolSvgRes.mode_factor_32();
			 */

			case EuclidianConstants.MODE_FITLINE:
				return toolSvgRes.mode_fitline_32();

			case EuclidianConstants.MODE_FREEHAND_SHAPE:
				return toolSvgRes.mode_freehandshape_32();

			case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
				return toolSvgRes.mode_functioninspector_32();

			case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
				return toolSvgRes.mode_hyperbola3_32();

			case EuclidianConstants.MODE_IMAGE:
				return toolSvgRes.mode_image_32();

			/*
			 * case EuclidianConstants.MODE_CAS_INTEGRAL: return
			 * toolSvgRes.mode_integral_32();
			 */

			case EuclidianConstants.MODE_INTERSECT:
				return toolSvgRes.mode_intersect_32();

			/*
			 * case EuclidianConstants.MODE_INTERSECTION_CURVE: return
			 * toolSvgRes.mode_intersectioncurve_32();
			 */

			case EuclidianConstants.MODE_JOIN:
				return toolSvgRes.mode_join_32();

			/*
			 * case EuclidianConstants.MODE_CAS_KEEP_INPUT: return
			 * toolSvgRes.mode_keepinput_32();
			 */

			case EuclidianConstants.MODE_LINE_BISECTOR:
				return toolSvgRes.mode_linebisector_32();

			case EuclidianConstants.MODE_LOCUS:
				return toolSvgRes.mode_locus_32();

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_MAX: return
			 * toolSvgRes.mode_maxcells_32();
			 * 
			 * case EuclidianConstants.MODE_SPREADSHEET_AVERAGE: return
			 * toolSvgRes.mode_meancells_32();
			 */

			case EuclidianConstants.MODE_MIDPOINT:
				return toolSvgRes.mode_midpoint_32();

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_MIN: return
			 * toolSvgRes.mode_mincells_32();
			 */
			case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
				return toolSvgRes.mode_mirroratcircle_32();

			case EuclidianConstants.MODE_MIRROR_AT_LINE:
				return toolSvgRes.mode_mirroratline_32();

			case EuclidianConstants.MODE_MIRROR_AT_POINT:
				return toolSvgRes.mode_mirroratpoint_32();

			case EuclidianConstants.MODE_MOVE:
				return toolSvgRes.mode_move_32();

			case EuclidianConstants.MODE_SELECT:
				return toolSvgRes.mode_select_32();

			case EuclidianConstants.MODE_MOVE_ROTATE:
				return toolSvgRes.mode_moverotate_32();

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS: return
			 * toolSvgRes.mode_multivarstats_32();
			 */

			/*
			 * case EuclidianConstants.MODE_CAS_NUMERIC: return
			 * toolSvgRes.mode_numeric_32();
			 */

			/*
			 * case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE: return
			 * toolSvgRes.mode_nsolve_32();
			 */

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS: return
			 * toolSvgRes.mode_onevarstats_32();
			 */

			case EuclidianConstants.MODE_ORTHOGONAL:
				return toolSvgRes.mode_orthogonal_32();

			case EuclidianConstants.MODE_PARABOLA:
				return toolSvgRes.mode_parabola_32();

			case EuclidianConstants.MODE_PARALLEL:
				return toolSvgRes.mode_parallel_32();

			case EuclidianConstants.MODE_PEN:
				return toolSvgRes.mode_pen_32();

			case EuclidianConstants.MODE_POINT:
				return toolSvgRes.mode_point_32();

			case EuclidianConstants.MODE_POINT_ON_OBJECT:
				return toolSvgRes.mode_pointonobject_32();

			case EuclidianConstants.MODE_POLAR_DIAMETER:
				return toolSvgRes.mode_polardiameter_32();

			case EuclidianConstants.MODE_POLYGON:
				return toolSvgRes.mode_polygon_32();

			case EuclidianConstants.MODE_POLYLINE:
				return toolSvgRes.mode_polyline_32();

			case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
				return toolSvgRes.mode_probabilitycalculator_32();

			case EuclidianConstants.MODE_RAY:
				return toolSvgRes.mode_ray_32();

			case EuclidianConstants.MODE_REGULAR_POLYGON:
				return toolSvgRes.mode_regularpolygon_32();

			case EuclidianConstants.MODE_RELATION:
				return toolSvgRes.mode_relation_32();

			case EuclidianConstants.MODE_RIGID_POLYGON:
				return toolSvgRes.mode_rigidpolygon_32();

			case EuclidianConstants.MODE_ROOTS:
				return toolSvgRes.mode_roots_32();

			case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
				return toolSvgRes.mode_rotatebyangle_32();

			case EuclidianConstants.MODE_SEGMENT:
				return toolSvgRes.mode_segment_32();

			case EuclidianConstants.MODE_SEGMENT_FIXED:
				return toolSvgRes.mode_segmentfixed_32();

			case EuclidianConstants.MODE_SEMICIRCLE:
				return toolSvgRes.mode_semicircle_32();

			case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
				return toolSvgRes.mode_showcheckbox_32();

			case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
				return toolSvgRes.mode_showhidelabel_32();

			case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
				return toolSvgRes.mode_showhideobject_32();

			case EuclidianConstants.MODE_SLIDER:
				return toolSvgRes.mode_slider_32();

			case EuclidianConstants.MODE_SLOPE:
				return toolSvgRes.mode_slope_32();

			/*
			 * case EuclidianConstants.MODE_CAS_SOLVE: return
			 * toolSvgRes.mode_solve_32();
			 * 
			 * case EuclidianConstants.MODE_CAS_SUBSTITUTE: return
			 * toolSvgRes.mode_substitute_32();
			 * 
			 * case EuclidianConstants.MODE_SPREADSHEET_SUM: return
			 * toolSvgRes.mode_sumcells_32();
			 */

			case EuclidianConstants.MODE_TANGENTS:
				return toolSvgRes.mode_tangent_32();

			case EuclidianConstants.MODE_TEXT:
				return toolSvgRes.mode_text_32();

			case EuclidianConstants.MODE_TEXTFIELD_ACTION:

				return toolSvgRes.mode_textfieldaction_32();

			case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
				return toolSvgRes.mode_translatebyvector_32();

			case EuclidianConstants.MODE_TRANSLATEVIEW:
				return toolSvgRes.mode_translateview_32();

			/*
			 * case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS: return
			 * toolSvgRes.mode_twovarstats_32();
			 */

			case EuclidianConstants.MODE_VECTOR:
				return toolSvgRes.mode_vector_32();

			case EuclidianConstants.MODE_VECTOR_FROM_POINT:
				return toolSvgRes.mode_vectorfrompoint_32();

			case EuclidianConstants.MODE_VECTOR_POLYGON:
				return toolSvgRes.mode_vectorpolygon_32();

			case EuclidianConstants.MODE_ZOOM_IN:
				return toolSvgRes.mode_zoomin_32();

			case EuclidianConstants.MODE_ZOOM_OUT:
				return toolSvgRes.mode_zoomout_32();

			/*
			 * 3D
			 */

			/*
			 * case EuclidianConstants.MODE_CIRCLE_AXIS_POINT: return
			 * toolSvgRes.mode_circleaxispoint_32();
			 * 
			 * case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			 * return toolSvgRes .mode_circlepointradiusdirection_32();
			 * 
			 * case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS: return
			 * toolSvgRes.mode_cone_32();
			 * 
			 * case EuclidianConstants.MODE_CONIFY: return
			 * toolSvgRes.mode_conify_32();
			 * 
			 * case EuclidianConstants.MODE_CUBE: return
			 * toolSvgRes.mode_cube_32();
			 * 
			 * case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS: return
			 * toolSvgRes.mode_cylinder_32();
			 * 
			 * case EuclidianConstants.MODE_EXTRUSION: return
			 * toolSvgRes.mode_extrusion_32();
			 * 
			 * case EuclidianConstants.MODE_MIRROR_AT_PLANE: return
			 * toolSvgRes.mode_mirroratplane_32();
			 * 
			 * case EuclidianConstants.MODE_NET: return
			 * toolSvgRes.mode_net_32();
			 * 
			 * case EuclidianConstants.MODE_ORTHOGONAL_PLANE: return
			 * toolSvgRes.mode_orthogonalplane_32();
			 * 
			 * case EuclidianConstants.MODE_PARALLEL_PLANE: return
			 * toolSvgRes.mode_parallelplane_32();
			 * 
			 * case EuclidianConstants.MODE_PLANE_THREE_POINTS: return
			 * toolSvgRes.mode_planethreepoint_32();
			 * 
			 * case EuclidianConstants.MODE_PLANE: return
			 * toolSvgRes.mode_plane_32();
			 * 
			 * case EuclidianConstants.MODE_PRISM: return
			 * toolSvgRes.mode_prism_32();
			 * 
			 * case EuclidianConstants.MODE_PYRAMID: return
			 * toolSvgRes.mode_pyramid_32();
			 * 
			 * case EuclidianConstants.MODE_ROTATE_AROUND_LINE: return
			 * toolSvgRes.mode_rotatearoundline_32();
			 * 
			 * case EuclidianConstants.MODE_ROTATEVIEW: return
			 * toolSvgRes.mode_rotateview_32();
			 * 
			 * case EuclidianConstants.MODE_SPHERE_TWO_POINTS: return
			 * toolSvgRes.mode_sphere2_32();
			 * 
			 * case EuclidianConstants.MODE_SPHERE_POINT_RADIUS: return
			 * toolSvgRes.mode_spherepointradius_32();
			 * 
			 * case EuclidianConstants.MODE_TETRAHEDRON: return
			 * toolSvgRes.mode_tetrahedron_32();
			 * 
			 * case EuclidianConstants.MODE_VIEW_IN_FRONT_OF: return
			 * toolSvgRes.mode_viewinfrontof_32();
			 * 
			 * case EuclidianConstants.MODE_VOLUME: return
			 * toolSvgRes.mode_volume_32();
			 * 
			 * case EuclidianConstants.MODE_ORTHOGONAL_THREE_D: return
			 * toolSvgRes.mode_orthogonalthreed_32();
			 */

			/** WHITEBOARD TOOLS */
			/*
			 * case EuclidianConstants.MODE_SHAPE_LINE: return
			 * toolSvgRes.mode_shape_line_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_TRIANGLE: return
			 * toolSvgRes.mode_shape_triangle_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_SQUARE: return
			 * toolSvgRes.mode_shape_square_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_RECTANGLE: return
			 * toolSvgRes.mode_shape_rectangle_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES: return
			 * toolSvgRes .mode_shape_rectangle_round_edges_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_POLYGON: return
			 * toolSvgRes.mode_shape_polygon_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_FREEFORM: return
			 * toolSvgRes.mode_shape_freeform_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_CIRCLE: return
			 * toolSvgRes.mode_shape_circle_32();
			 * 
			 * case EuclidianConstants.MODE_SHAPE_ELLIPSE: return
			 * toolSvgRes.mode_shape_ellipse_32();
			 * 
			 * case EuclidianConstants.MODE_ERASER: return
			 * toolSvgRes.mode_eraser_32();
			 */

			/*
			 * case EuclidianConstants.MODE_HIGHLIGHTER: return
			 * toolSvgRes.mode_highlighter_32();
			 */
			/*
			 * case EuclidianConstants.MODE_PEN_PANEL: return
			 * toolSvgRes.pen_panel_24();
			 * 
			 * case EuclidianConstants.MODE_TOOLS_PANEL: return
			 * toolSvgRes.tools_panel_24();
			 * 
			 * case EuclidianConstants.MODE_MEDIA_PANEL: return
			 * toolSvgRes.media_panel_24();
			 * 
			 * case EuclidianConstants.MODE_VIDEO: return
			 * toolSvgRes.mode_video_32();
			 * 
			 * case EuclidianConstants.MODE_AUDIO: return
			 * toolSvgRes.mode_audio_32();
			 * 
			 * case EuclidianConstants.MODE_GEOGEBRA: return
			 * toolSvgRes.mode_geogebra_32();
			 */

			default:
				return toolSvgRes.mode_angle_32();
			}

		}

		private StandardButton getButton(final int mode) {
			int size = (mode == EuclidianConstants.MODE_DELETE
					|| mode == EuclidianConstants.MODE_IMAGE) ? 24 : 32;

			final StandardButton btn = new StandardButton(
					new ImageResourcePrototype(null,
							getSvgImageForTool(mode).getSafeUri(), 0, 0, size,
							size,
							false, false),
					app);
			btn.setTitle(app.getLocalization()
					.getMenu(EuclidianConstants.getModeText(mode)));

			if (mode == EuclidianConstants.MODE_DELETE
					|| mode == EuclidianConstants.MODE_IMAGE) {
				btn.addStyleName("plusPadding");
			}
			btn.getElement().setAttribute("mode", mode + "");

			btn.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					app.setMode(mode);
					if (!Browser.isMobile()) {
						ToolTipManagerW.sharedInstance().setBlockToolTip(false);
						ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
							app.getToolTooltipHTML(mode),
							app.getGuiManager().getTooltipURL(mode),
							ToolTipLinkType.Help, app,
							app.getAppletFrame().isKeyboardShowing());
						ToolTipManagerW.sharedInstance().setBlockToolTip(true);
					}
					app.updateDynamicStyleBars();
				}

			});

			return btn;
		}
	}

}
