package org.geogebra.web.full.gui.view;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Contains the real views and also the "extensions"
 */
public class Views {
	/** Properties of a view type */
	public enum ViewType {
		/**
		 * Algebra
		 */
		ALGEBRA(App.VIEW_ALGEBRA, "AlgebraWindow", SvgPerspectiveResources.INSTANCE
				.menu_icon_algebra_transparent()),
		/**
		 * Graphics
		 */
		GRAPHICS(App.VIEW_EUCLIDIAN, "DrawingPad", SvgPerspectiveResources.INSTANCE
						.menu_icon_geometry_transparent()),
		/**
		 * Graphics 2
		 */
		GRAPHICS_2(App.VIEW_EUCLIDIAN2, "DrawingPad2", SvgPerspectiveResources.INSTANCE
				.menu_icon_graphics2_transparent()),
		/**
		 * 3D Graphics
		 */
		GRAPHICS_3D(App.VIEW_EUCLIDIAN3D, "GraphicsView3D",
				SvgPerspectiveResources.INSTANCE.menu_icon_graphics3D_transparent()),
		/**
		 * Spreadsheet
		 */
		SPREADSHEET(App.VIEW_SPREADSHEET, "Spreadsheet", SvgPerspectiveResources.INSTANCE
				.menu_icon_spreadsheet_transparent()),
		/**
		 * CAS
		 */
		CAS(App.VIEW_CAS, "CAS", SvgPerspectiveResources.INSTANCE.menu_icon_cas_transparent()),
		/**
		 * 
		 */
		PROBABILITY(App.VIEW_PROBABILITY_CALCULATOR, "ProbabilityCalculator",
				SvgPerspectiveResources.INSTANCE.menu_icon_probability_transparent()),
		/**
		 * Construction Protocol
		 */
		CONSTRUCTION_PROTOCOL(App.VIEW_CONSTRUCTION_PROTOCOL,
				"ConstructionProtocol", SvgPerspectiveResources.INSTANCE
						.menu_icon_construction_protocol_transparent());

		private int id;
		private String key;
		private ResourcePrototype resourceType;

		ViewType(int viewID, String key, ResourcePrototype resourceType) {
			this.id = viewID;
			this.key = key;
			this.resourceType = resourceType;
		}

		/**
		 * @return view id
		 */
		public int getID() {
			return id;
		}

		/**
		 * @return name of the view
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @return {@link ResourcePrototype}
		 */
		public ResourcePrototype getIcon() {
			return resourceType;
		}
	}

	final private static ArrayList<ViewType> BASIC_VIEWS = new ArrayList<>();
	final private static ArrayList<ViewType> EXTENSIONS = new ArrayList<>();

	static {
		BASIC_VIEWS.add(ViewType.ALGEBRA);
		BASIC_VIEWS.add(ViewType.CAS);
		BASIC_VIEWS.add(ViewType.GRAPHICS);
		BASIC_VIEWS.add(ViewType.GRAPHICS_2);
		BASIC_VIEWS.add(ViewType.GRAPHICS_3D);
		BASIC_VIEWS.add(ViewType.SPREADSHEET);
		BASIC_VIEWS.add(ViewType.PROBABILITY);
		EXTENSIONS.add(ViewType.CONSTRUCTION_PROTOCOL);
	}

	/**
	 * @return list of "real" views
	 */
	public static ArrayList<ViewType> getViews() {
		return BASIC_VIEWS;
	}

	/**
	 * @return list of "view-extensions"
	 */
	public static ArrayList<ViewType> getViewExtensions() {
		return EXTENSIONS;
	}

	/**
	 * @return list of the "real" views and the "view-extension"
	 */
	public static ArrayList<ViewType> getAll() {
		ArrayList<ViewType> all = new ArrayList<>();
		all.addAll(BASIC_VIEWS);
		all.addAll(EXTENSIONS);
		return all;
	}

	/**
	 * 
	 * @return number of real views + view-extensions
	 */
	public static int numOfViews() {
		return BASIC_VIEWS.size() + EXTENSIONS.size();
	}
}