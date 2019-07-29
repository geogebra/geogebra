package org.geogebra.common.gui.toolcategorization;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ToolbarSettings;

/**
 * categorization of tools
 */
public class ToolCategorization {

	private ToolsetLevel level;
	private ArrayList<Category> customizedCategories;
	private ArrayList<ArrayList<Integer>> toolsLists;
	private TreeSet<Integer> availableTools;
	private App app;
	private ToolbarSettings settings;
	private boolean isPhoneApp;

	/**
	 * levels of toolset
	 * 
	 * @author csilla
	 *
	 */
	public enum ToolsetLevel {
		/**
		 * full list of tools
		 */
		ADVANCED("ToolsetLevel.Advanced"),
		/**
		 * non-empty construction
		 */
		STANDARD("ToolsetLevel.Standard"),
		/**
		 * for empty construction
		 */
		EMPTY_CONSTRUCTION("ToolsetLevel.Empty");

		private final String level;

		ToolsetLevel(String level) {
			this.level = level;
		}

		/**
		 * @return level
		 */
		public String getLevel() {
			return level;
		}
	}

	/**
	 * @author csilla
	 * 
	 *         app types
	 *
	 */
	public enum AppType {
		/**
		 * graphing calculator
		 */
		GEOMETRY_CALC,
		/**
		 * geometry
		 */
		GRAPHING_CALCULATOR,
		/**
		 * 3D graphing calculator
		 */
		GRAPHER_3D
	}

	/**
	 * @author csilla
	 * 
	 *         category names
	 *
	 */
    public enum Category {
        // from Geometry & Graphing Calculator
		/**
		 * basic
		 */
		BASIC("ToolCategory.BasicTools"),
		/**
		 * edit
		 */
		EDIT("ToolCategory.Edit"),
		/**
		 * media
		 */
		MEDIA("ToolCategory.Media"),
		/**
		 * construct
		 */
		CONSTRUCT("ToolCategory.Construct"),
		/**
		 * measure
		 */
		MEASURE("ToolCategory.Measure"),
		/**
		 * points
		 */
		POINTS("ToolCategory.Points"),
		/**
		 * lines
		 */
		LINES("ToolCategory.Lines"),
		/**
		 * polygons
		 */
		POLYGONS("ToolCategory.Polygons"),
		/**
		 * circles
		 */
		CIRCLES("ToolCategory.Circles"),
		/**
		 * curves
		 */
		CURVES("ToolCategory.Curves"),
		/**
		 * conics
		 */
		CONICS("ToolCategory.Conics"),
		/**
		 * transformation
		 */
		TRANSFORM("ToolCategory.Transform"),
		/**
		 * special lines
		 */
        SPECIAL_LINES("ToolCategory.SpecialLines"),
		/**
		 * others
		 */
		OTHERS("ToolCategory.Others"),
		/**
		 * lines and polygons
		 */
        // specific to 3D Grapher
		LINES_AND_POLYGONS("ToolCategory.LinesAndPolygons"),
		/**
		 * solids
		 */
		SOLIDS("ToolCategory.Solids"),
		/**
		 * planes
		 */
		PLANES("ToolCategory.Planes"),
		/**
		 * select and format
		 */
		SELECT_AND_FORMAT("ToolCategory.SelectAndFormat");
        private final String header;

        Category(String header) {
            this.header = header;
        }

		/**
		 * @return header of category
		 */
        public String getHeader() {
            return header;
        }
    }

    /**
	 * Creates a tool categorization for the give type
	 *
	 * @param app
	 *            App (for localization)
	 * 
	 * @param settings
	 *            defines which set of tools to use, depending on app type and
	 *            phone flag
	 */
	public ToolCategorization(App app, ToolbarSettings settings) {
        this.app = app;
		this.level = settings.getToolsetLevel();
		toolsLists = new ArrayList<>();
		customizedCategories = new ArrayList<>();
		this.settings = settings;
		isPhoneApp = settings.isPhoneApp();
    }

    /**
     * @return categories used
     */
    public ArrayList<Category> getCategories() {
        return customizedCategories;
    }

    /**
     * @return toolsList
     */
    public ArrayList<ArrayList<Integer>> getToolsLists() {
        return toolsLists;
    }

    /**
     * @param category tools category
     * @return localized header for this category
     */
    public String getLocalizedHeader(Category category) {
		return app.getLocalization().getMenu(category.getHeader());
    }

    /**
     * categoryId is the rank of the category over the categories list
     *
     * @param categoryId category id
     * @return list of tools for that category
     */
    public ArrayList<Integer> getTools(int categoryId) {
        return toolsLists.get(categoryId);
    }

    /**
     * reset tools & categories list
     */
    public void resetTools() {
        resetTools(null);
    }

    /**
     * reset tools & categories list, keeping only tools present in toolbarDef
     *
     * @param toolbarDef toolbar definition
     */
    public void resetTools(String toolbarDef) {
        if (toolbarDef == null) {
            this.availableTools = null;
        } else {
            this.availableTools = ToolBar.toSet(toolbarDef);
        }
        toolsLists.clear();
        customizedCategories.clear();
        buildTools();
    }

    private void buildTools() {
        Category category;
        ArrayList<Integer> tools;
		AppType type = settings.getType();
        switch (type) {
            case GEOMETRY_CALC:
			category = Category.BASIC;
			tools = new ArrayList<>();
			addToList(tools, EuclidianConstants.MODE_MOVE);
			addToList(tools, EuclidianConstants.MODE_POINT);
			addToList(tools, EuclidianConstants.MODE_SEGMENT);
			addToList(tools, EuclidianConstants.MODE_JOIN);
			addToList(tools, EuclidianConstants.MODE_POLYGON);
			addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
			storeIfNotEmpty(category, tools);
			if (!level.equals(ToolsetLevel.EMPTY_CONSTRUCTION)) {
				buildGeometryCalculatorCommonTools();
			}
			break;

            case GRAPHING_CALCULATOR:
            default:
			category = Category.BASIC;
			tools = new ArrayList<>();
			addToList(tools, EuclidianConstants.MODE_MOVE);
			addToList(tools, EuclidianConstants.MODE_POINT);
			addToList(tools, EuclidianConstants.MODE_SLIDER);
			addToList(tools, EuclidianConstants.MODE_INTERSECT);
			addToList(tools, EuclidianConstants.MODE_EXTREMUM);
			addToList(tools, EuclidianConstants.MODE_ROOTS);
			addToList(tools, EuclidianConstants.MODE_FITLINE);
			if (!isPhoneApp) {
                   // addToList(tools, EuclidianConstants.MODE_FUNCTION_INSPECTOR);
                }
                storeIfNotEmpty(category, tools);
			buildGraphingCalculatorCommonTools();
                break;

            case GRAPHER_3D:
			category = Category.BASIC;
			tools = new ArrayList<>();
                addToList(tools, EuclidianConstants.MODE_MOVE);
                addToList(tools, EuclidianConstants.MODE_POINT);
                addToList(tools, EuclidianConstants.MODE_PYRAMID);
                addToList(tools, EuclidianConstants.MODE_CUBE);
                addToList(tools, EuclidianConstants.MODE_SPHERE_TWO_POINTS);
                addToList(tools, EuclidianConstants.MODE_PLANE_THREE_POINTS);
                addToList(tools, EuclidianConstants.MODE_INTERSECTION_CURVE);
                addToList(tools, EuclidianConstants.MODE_NET);
                storeIfNotEmpty(category, tools);
				build3DGrapherCommonTools();
                break;
        }
    }

	private void buildGraphingCalculatorCommonTools() {
		if (level.equals(ToolsetLevel.ADVANCED)) {
			buildGraphingAdvancedCommonTools();
		} else {
			buildGraphingStandardCommonTools();
		}
	}

	private void buildGraphingStandardCommonTools() {
		Category category;
		ArrayList<Integer> tools;

		category = Category.EDIT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SELECT);
		addToList(tools, EuclidianConstants.MODE_TRANSLATEVIEW);
		addToList(tools, EuclidianConstants.MODE_DELETE);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		storeIfNotEmpty(category, tools);

		if (!isPhoneApp) {
			category = Category.MEDIA;
			tools = new ArrayList<>();
			addToList(tools, EuclidianConstants.MODE_IMAGE);
			addToList(tools, EuclidianConstants.MODE_TEXT);
			storeIfNotEmpty(category, tools);
		}

		category = Category.MEASURE;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ANGLE);
		addToList(tools, EuclidianConstants.MODE_DISTANCE);
		addToList(tools, EuclidianConstants.MODE_AREA);
		storeIfNotEmpty(category, tools);

		category = Category.TRANSFORM;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_LINE);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_POINT);
		addToList(tools, EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		storeIfNotEmpty(category, tools);

		category = Category.CONSTRUCT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_MIDPOINT);
		addToList(tools, EuclidianConstants.MODE_ORTHOGONAL);
		addToList(tools, EuclidianConstants.MODE_LINE_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_PARALLEL);
		addToList(tools, EuclidianConstants.MODE_ANGULAR_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_TANGENTS);
		storeIfNotEmpty(category, tools);

		category = Category.LINES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SEGMENT);
		addToList(tools, EuclidianConstants.MODE_JOIN);
		addToList(tools, EuclidianConstants.MODE_RAY);
		addToList(tools, EuclidianConstants.MODE_VECTOR);
		storeIfNotEmpty(category, tools);

		category = Category.CIRCLES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		addToList(tools, EuclidianConstants.MODE_COMPASSES);
		addToList(tools, EuclidianConstants.MODE_SEMICIRCLE);
		storeIfNotEmpty(category, tools);
	}

	private void buildGraphingAdvancedCommonTools() {
        Category category;
        ArrayList<Integer> tools;

        category = Category.EDIT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SELECT);
		addToList(tools, EuclidianConstants.MODE_TRANSLATEVIEW);
		addToList(tools, EuclidianConstants.MODE_DELETE);
        addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_LABEL);
        addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		addToList(tools, EuclidianConstants.MODE_COPY_VISUAL_STYLE);
        storeIfNotEmpty(category, tools);

		if (!isPhoneApp) {
			category = Category.MEDIA;
			tools = new ArrayList<>();
			addToList(tools, EuclidianConstants.MODE_IMAGE);
			addToList(tools, EuclidianConstants.MODE_TEXT);
			storeIfNotEmpty(category, tools);
		}

		category = Category.MEASURE;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ANGLE);
		addToList(tools, EuclidianConstants.MODE_DISTANCE);
		addToList(tools, EuclidianConstants.MODE_AREA);
		addToList(tools, EuclidianConstants.MODE_ANGLE_FIXED);
		addToList(tools, EuclidianConstants.MODE_SLOPE);
		storeIfNotEmpty(category, tools);

		category = Category.POINTS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_POINT);
		addToList(tools, EuclidianConstants.MODE_INTERSECT);
		addToList(tools, EuclidianConstants.MODE_POINT_ON_OBJECT);
		addToList(tools, EuclidianConstants.MODE_ATTACH_DETACH);
		addToList(tools, EuclidianConstants.MODE_EXTREMUM);
		addToList(tools, EuclidianConstants.MODE_ROOTS);
		addToList(tools, EuclidianConstants.MODE_COMPLEX_NUMBER);
		addToList(tools, EuclidianConstants.MODE_CREATE_LIST);
		storeIfNotEmpty(category, tools);

		category = Category.CONSTRUCT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_MIDPOINT);
		addToList(tools, EuclidianConstants.MODE_ORTHOGONAL);
		addToList(tools, EuclidianConstants.MODE_LINE_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_PARALLEL);
		addToList(tools, EuclidianConstants.MODE_ANGULAR_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_TANGENTS);
		addToList(tools, EuclidianConstants.MODE_LOCUS);
		storeIfNotEmpty(category, tools);

        category = Category.LINES;
		tools = new ArrayList<>();
        addToList(tools, EuclidianConstants.MODE_SEGMENT);
        addToList(tools, EuclidianConstants.MODE_JOIN);
        addToList(tools, EuclidianConstants.MODE_RAY);
        addToList(tools, EuclidianConstants.MODE_VECTOR);
		addToList(tools, EuclidianConstants.MODE_SEGMENT_FIXED);
		addToList(tools, EuclidianConstants.MODE_VECTOR_FROM_POINT);
		addToList(tools, EuclidianConstants.MODE_POLAR_DIAMETER);
		addToList(tools, EuclidianConstants.MODE_POLYLINE);
        storeIfNotEmpty(category, tools);

		category = Category.POLYGONS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_POLYGON);
		addToList(tools, EuclidianConstants.MODE_REGULAR_POLYGON);
		addToList(tools, EuclidianConstants.MODE_VECTOR_POLYGON);
		addToList(tools, EuclidianConstants.MODE_RIGID_POLYGON);
		storeIfNotEmpty(category, tools);

        category = Category.CIRCLES;
		tools = new ArrayList<>();
        addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		addToList(tools, EuclidianConstants.MODE_COMPASSES);
		addToList(tools, EuclidianConstants.MODE_SEMICIRCLE);
        addToList(tools, EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
        addToList(tools, EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
        addToList(tools, EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
        addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
        addToList(tools, EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
        addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
        storeIfNotEmpty(category, tools);

		category = Category.CONICS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		addToList(tools, EuclidianConstants.MODE_PARABOLA);
		addToList(tools, EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		storeIfNotEmpty(category, tools);

		category = Category.TRANSFORM;
		tools = new ArrayList<>();
        addToList(tools, EuclidianConstants.MODE_MIRROR_AT_LINE);
        addToList(tools, EuclidianConstants.MODE_MIRROR_AT_POINT);
        addToList(tools, EuclidianConstants.MODE_ROTATE_BY_ANGLE);
        addToList(tools, EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
        addToList(tools, EuclidianConstants.MODE_DILATE_FROM_POINT);
        addToList(tools, EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
        storeIfNotEmpty(category, tools);

        category = Category.OTHERS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_PEN);
        addToList(tools, EuclidianConstants.MODE_FREEHAND_SHAPE);
        addToList(tools, EuclidianConstants.MODE_RELATION);
        if (!isPhoneApp) {
			// addToList(tools, EuclidianConstants.MODE_FUNCTION_INSPECTOR);
            addToList(tools, EuclidianConstants.MODE_BUTTON_ACTION);
            addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
            addToList(tools, EuclidianConstants.MODE_TEXTFIELD_ACTION);
			// addToList(tools, EuclidianConstants.MODE_CREATE_LIST);
        }
        storeIfNotEmpty(category, tools);
    }

	private void buildGeometryCalculatorCommonTools() {
		if (level.equals(ToolsetLevel.ADVANCED)) {
			buildGeometryAdvancedCommonTools();
		} else {
			buildGeometryStandardCommonTools();
		}
	}

	private void buildGeometryStandardCommonTools() {
		Category category;
		ArrayList<Integer> tools;

		category = Category.EDIT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SELECT);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		addToList(tools, EuclidianConstants.MODE_DELETE);
		storeIfNotEmpty(category, tools);

		category = Category.CONSTRUCT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_MIDPOINT);
		addToList(tools, EuclidianConstants.MODE_ORTHOGONAL);
		addToList(tools, EuclidianConstants.MODE_LINE_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_PARALLEL);
		addToList(tools, EuclidianConstants.MODE_ANGULAR_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_TANGENTS);
		storeIfNotEmpty(category, tools);

		category = Category.MEASURE;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ANGLE);
		addToList(tools, EuclidianConstants.MODE_ANGLE_FIXED);
		addToList(tools, EuclidianConstants.MODE_DISTANCE);
		addToList(tools, EuclidianConstants.MODE_AREA);
		storeIfNotEmpty(category, tools);

		category = Category.LINES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SEGMENT);
		addToList(tools, EuclidianConstants.MODE_SEGMENT_FIXED);
		addToList(tools, EuclidianConstants.MODE_JOIN);
		addToList(tools, EuclidianConstants.MODE_RAY);
		addToList(tools, EuclidianConstants.MODE_VECTOR);
		storeIfNotEmpty(category, tools);

		category = Category.CIRCLES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		addToList(tools, EuclidianConstants.MODE_COMPASSES);
		addToList(tools, EuclidianConstants.MODE_SEMICIRCLE);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		storeIfNotEmpty(category, tools);

		category = Category.POLYGONS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_POLYGON);
		addToList(tools, EuclidianConstants.MODE_REGULAR_POLYGON);
		storeIfNotEmpty(category, tools);

		category = Category.TRANSFORM;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		addToList(tools, EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_LINE);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_POINT);
		addToList(tools, EuclidianConstants.MODE_DILATE_FROM_POINT);
		storeIfNotEmpty(category, tools);

		if (!isPhoneApp) {
			category = Category.MEDIA;
			tools = new ArrayList<>();
			addToList(tools, EuclidianConstants.MODE_IMAGE);
			addToList(tools, EuclidianConstants.MODE_TEXT);
			storeIfNotEmpty(category, tools);
		}
	}

	private void buildGeometryAdvancedCommonTools() {
		Category category;
		ArrayList<Integer> tools;

		category = Category.EDIT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SELECT);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		addToList(tools, EuclidianConstants.MODE_DELETE);
		addToList(tools, EuclidianConstants.MODE_TRANSLATEVIEW);
		addToList(tools, EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		storeIfNotEmpty(category, tools);

		category = Category.CONSTRUCT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_MIDPOINT);
		addToList(tools, EuclidianConstants.MODE_ORTHOGONAL);
		addToList(tools, EuclidianConstants.MODE_LINE_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_PARALLEL);
		addToList(tools, EuclidianConstants.MODE_ANGULAR_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_TANGENTS);
		addToList(tools, EuclidianConstants.MODE_LOCUS);
		storeIfNotEmpty(category, tools);

		category = Category.MEASURE;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ANGLE);
		addToList(tools, EuclidianConstants.MODE_ANGLE_FIXED);
		addToList(tools, EuclidianConstants.MODE_DISTANCE);
		addToList(tools, EuclidianConstants.MODE_AREA);
		addToList(tools, EuclidianConstants.MODE_SLIDER);
		addToList(tools, EuclidianConstants.MODE_SLOPE);
		storeIfNotEmpty(category, tools);

		category = Category.POINTS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_POINT);
		addToList(tools, EuclidianConstants.MODE_INTERSECT);
		addToList(tools, EuclidianConstants.MODE_POINT_ON_OBJECT);
		addToList(tools, EuclidianConstants.MODE_ATTACH_DETACH);
		addToList(tools, EuclidianConstants.MODE_EXTREMUM);
		addToList(tools, EuclidianConstants.MODE_ROOTS);
		storeIfNotEmpty(category, tools);

		category = Category.LINES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SEGMENT);
		addToList(tools, EuclidianConstants.MODE_SEGMENT_FIXED);
		addToList(tools, EuclidianConstants.MODE_JOIN);
		addToList(tools, EuclidianConstants.MODE_RAY);
		addToList(tools, EuclidianConstants.MODE_VECTOR);
		addToList(tools, EuclidianConstants.MODE_VECTOR_FROM_POINT);
		addToList(tools, EuclidianConstants.MODE_POLAR_DIAMETER);
		addToList(tools, EuclidianConstants.MODE_POLYLINE);
		addToList(tools, EuclidianConstants.MODE_FITLINE);
		storeIfNotEmpty(category, tools);

		category = Category.CIRCLES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		addToList(tools, EuclidianConstants.MODE_COMPASSES);
		addToList(tools, EuclidianConstants.MODE_SEMICIRCLE);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		addToList(tools,
				EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		storeIfNotEmpty(category, tools);

		category = Category.POLYGONS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_POLYGON);
		addToList(tools, EuclidianConstants.MODE_REGULAR_POLYGON);
		addToList(tools, EuclidianConstants.MODE_VECTOR_POLYGON);
		addToList(tools, EuclidianConstants.MODE_RIGID_POLYGON);
		storeIfNotEmpty(category, tools);

		category = Category.CONICS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		addToList(tools, EuclidianConstants.MODE_PARABOLA);
		addToList(tools, EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		storeIfNotEmpty(category, tools);

		category = Category.TRANSFORM;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		addToList(tools, EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_LINE);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_POINT);
		addToList(tools, EuclidianConstants.MODE_DILATE_FROM_POINT);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		storeIfNotEmpty(category, tools);

		if (!isPhoneApp) {
			category = Category.MEDIA;
			tools = new ArrayList<>();
			addToList(tools, EuclidianConstants.MODE_IMAGE);
			addToList(tools, EuclidianConstants.MODE_TEXT);
			storeIfNotEmpty(category, tools);
		}

		category = Category.OTHERS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_PEN);
		addToList(tools, EuclidianConstants.MODE_FREEHAND_SHAPE);
		addToList(tools, EuclidianConstants.MODE_RELATION);
		if (!isPhoneApp) {
			// addToList(tools, EuclidianConstants.MODE_FUNCTION_INSPECTOR);
			addToList(tools, EuclidianConstants.MODE_BUTTON_ACTION);
			addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
			addToList(tools, EuclidianConstants.MODE_TEXTFIELD_ACTION);
			// addToList(tools, EuclidianConstants.MODE_CREATE_LIST);
		}
		storeIfNotEmpty(category, tools);
	}

	private void build3DGrapherCommonTools() {
		if (level.equals(ToolsetLevel.ADVANCED)) {
			build3DGrapherAdvancedCommonTools();
		}
	}

	private void build3DGrapherAdvancedCommonTools() {
		Category category;
		ArrayList<Integer> tools;

		category = Category.EDIT;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		addToList(tools, EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		addToList(tools, EuclidianConstants.MODE_DELETE);
		addToList(tools, EuclidianConstants.MODE_VIEW_IN_FRONT_OF);
		storeIfNotEmpty(category, tools);

		category = Category.POINTS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_POINT);
		addToList(tools, EuclidianConstants.MODE_INTERSECT);
		addToList(tools, EuclidianConstants.MODE_MIDPOINT);
		addToList(tools, EuclidianConstants.MODE_POINT_ON_OBJECT);
		addToList(tools, EuclidianConstants.MODE_ATTACH_DETACH);
		storeIfNotEmpty(category, tools);

		category = Category.LINES_AND_POLYGONS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_SEGMENT);
		addToList(tools, EuclidianConstants.MODE_SEGMENT_FIXED);
		addToList(tools, EuclidianConstants.MODE_JOIN);
		addToList(tools, EuclidianConstants.MODE_RAY);
		addToList(tools, EuclidianConstants.MODE_VECTOR);
		addToList(tools, EuclidianConstants.MODE_POLYGON);
		addToList(tools, EuclidianConstants.MODE_REGULAR_POLYGON);
		addToList(tools, EuclidianConstants.MODE_ORTHOGONAL_THREE_D);
		addToList(tools, EuclidianConstants.MODE_PARALLEL);
		addToList(tools, EuclidianConstants.MODE_ANGULAR_BISECTOR);
		addToList(tools, EuclidianConstants.MODE_TANGENTS);
		storeIfNotEmpty(category, tools);

		category = Category.SOLIDS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_PYRAMID);
		addToList(tools, EuclidianConstants.MODE_PRISM);
		addToList(tools, EuclidianConstants.MODE_TETRAHEDRON);
		addToList(tools, EuclidianConstants.MODE_CUBE);
		addToList(tools, EuclidianConstants.MODE_SPHERE_TWO_POINTS);
		addToList(tools, EuclidianConstants.MODE_SPHERE_POINT_RADIUS);
		addToList(tools, EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS);
		addToList(tools, EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS);
		addToList(tools, EuclidianConstants.MODE_CONIFY);
		addToList(tools, EuclidianConstants.MODE_EXTRUSION);
		addToList(tools, EuclidianConstants.MODE_NET);
		storeIfNotEmpty(category, tools);

		category = Category.PLANES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_PLANE_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_PLANE);
		addToList(tools, EuclidianConstants.MODE_PARALLEL_PLANE);
		addToList(tools, EuclidianConstants.MODE_ORTHOGONAL_PLANE);
		storeIfNotEmpty(category, tools);

		category = Category.CIRCLES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_CIRCLE_AXIS_POINT);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
		storeIfNotEmpty(category, tools);

		category = Category.CURVES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		addToList(tools, EuclidianConstants.MODE_PARABOLA);
		addToList(tools, EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		addToList(tools, EuclidianConstants.MODE_LOCUS);
		addToList(tools, EuclidianConstants.MODE_INTERSECTION_CURVE);
		storeIfNotEmpty(category, tools);

		category = Category.TRANSFORM;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_PLANE);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_POINT);
		addToList(tools, EuclidianConstants.MODE_ROTATE_AROUND_LINE);
		addToList(tools, EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		addToList(tools, EuclidianConstants.MODE_DILATE_FROM_POINT);
		addToList(tools, EuclidianConstants.MODE_MIRROR_AT_LINE);
		storeIfNotEmpty(category, tools);

		category = Category.MEASURE;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ANGLE);
		addToList(tools, EuclidianConstants.MODE_DISTANCE);
		addToList(tools, EuclidianConstants.MODE_AREA);
		addToList(tools, EuclidianConstants.MODE_VOLUME);
		storeIfNotEmpty(category, tools);

		category = Category.OTHERS;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_ROTATEVIEW);
		addToList(tools, EuclidianConstants.MODE_TRANSLATEVIEW);
		addToList(tools, EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		if (!isPhoneApp) {
			addToList(tools, EuclidianConstants.MODE_TEXT);
		}
		storeIfNotEmpty(category, tools);

		category = Category.SPECIAL_LINES;
		tools = new ArrayList<>();
		addToList(tools, EuclidianConstants.MODE_VECTOR_FROM_POINT);
		addToList(tools, EuclidianConstants.MODE_POLYLINE);
//                addToList(tools, EuclidianConstants.MODE_FITLINE);
		addToList(tools, EuclidianConstants.MODE_POLAR_DIAMETER);
		storeIfNotEmpty(category, tools);
	}

    private void storeIfNotEmpty(Category category, ArrayList<Integer> tools) {
        if (!tools.isEmpty()) {
            customizedCategories.add(category);
            toolsLists.add(tools);
        }
    }

    final private void addToList(ArrayList<Integer> toolList, int mode) {
		if (availableTools == null || availableTools.contains(mode)
				|| (app.getSettings().getToolbarSettings().getType()
						.equals(AppType.GRAPHER_3D)
						&& mode == EuclidianConstants.MODE_POLYLINE)) {
            toolList.add(mode);
        }
    }
}
