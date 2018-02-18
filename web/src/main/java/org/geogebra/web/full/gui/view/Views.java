package org.geogebra.web.full.gui.view;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;

import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Contains the real views and also the "extensions"
 */
public class Views {
	/** Properties of a view type */
	public static enum ViewType {
		/**
		 * Algebra
		 */
		ALGEBRA(App.VIEW_ALGEBRA, "AlgebraWindow", SvgPerspectiveResources.INSTANCE
				.menu_icon_algebra24()),
		/**
		 * Graphics
		 */
		GRAPHICS(App.VIEW_EUCLIDIAN, "DrawingPad", SvgPerspectiveResources.INSTANCE
				.menu_icon_graphics24()),
		/**
		 * Graphics 2
		 */
		GRAPHICS_2(App.VIEW_EUCLIDIAN2, "DrawingPad2", SvgPerspectiveResources.INSTANCE
				.menu_icon_graphics224()),
		/**
		 * 3D Graphics
		 */
		GRAPHICS_3D(App.VIEW_EUCLIDIAN3D, "GraphicsView3D",
				SvgPerspectiveResources.INSTANCE.menu_icon_graphics3D24()),
		/**
		 * Spreadsheet
		 */
		SPREADSHEET(App.VIEW_SPREADSHEET, "Spreadsheet", SvgPerspectiveResources.INSTANCE
				.menu_icon_spreadsheet24()),
		/**
		 * CAS
		 */
		CAS(App.VIEW_CAS, "CAS", SvgPerspectiveResources.INSTANCE.menu_icon_cas24()),
		/**
		 * 
		 */
		PROBABILITY(App.VIEW_PROBABILITY_CALCULATOR, "ProbabilityCalculator",
				SvgPerspectiveResources.INSTANCE.menu_icon_probability24()),
		/**
		 * Construction Protocol
		 */
		CONSTRUCTION_PROTOCOL(App.VIEW_CONSTRUCTION_PROTOCOL,
				"ConstructionProtocol", SvgPerspectiveResources.INSTANCE
						.menu_icon_construction_protocol24());

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

	final private static ArrayList<ViewType> views = new ArrayList<>();
	final private static ArrayList<ViewType> extensions = new ArrayList<>();
	static {
		views.add(ViewType.ALGEBRA);
		views.add(ViewType.CAS);
		views.add(ViewType.GRAPHICS);
		views.add(ViewType.GRAPHICS_2);
		views.add(ViewType.GRAPHICS_3D);
		views.add(ViewType.SPREADSHEET);
		views.add(ViewType.PROBABILITY);
		extensions.add(ViewType.CONSTRUCTION_PROTOCOL);
	}

	/**
	 * @return list of "real" views
	 */
	public static ArrayList<ViewType> getViews() {
		return views;
	}

	/**
	 * @return list of "view-extensions"
	 */
	public static ArrayList<ViewType> getViewExtensions() {
		return extensions;
	}

	/**
	 * @return list of the "real" views and the "view-extension"
	 */
	public static ArrayList<ViewType> getAll() {
		ArrayList<ViewType> all = new ArrayList<>();
		all.addAll(views);
		all.addAll(extensions);
		return all;
	}

	/**
	 * 
	 * @return number of real views + view-extensions
	 */
	public static int numOfViews() {
		return views.size() + extensions.size();
	}
}