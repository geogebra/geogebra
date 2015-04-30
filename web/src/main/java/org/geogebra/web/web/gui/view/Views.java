package org.geogebra.web.web.gui.view;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.PerspectiveResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Contains the real views and also the "extensions"
 */
public class Views {

	public enum ViewType {
		/**
		 * 
		 */
		ALGEBRA(App.VIEW_ALGEBRA, "AlgebraWindow", perspectiveResources
				.menu_icon_algebra()),
		/**
		 * 
		 */
		GRAPHICS(App.VIEW_EUCLIDIAN, "DrawingPad", perspectiveResources
				.menu_icon_graphics()),
		/**
		 * 
		 */
		GRAPHICS_2(App.VIEW_EUCLIDIAN2, "DrawingPad2", perspectiveResources
				.menu_icon_graphics2()),
		/**
		 * 
		 */
		GRAPHICS_3D(App.VIEW_EUCLIDIAN3D, "GraphicsView3D",
				perspectiveResources.menu_icon_graphics3D()),
		/**
		 * 
		 */
		SPREADSHEET(App.VIEW_SPREADSHEET, "Spreadsheet", perspectiveResources
				.menu_icon_spreadsheet()),
		/**
		 * 
		 */
		CAS(App.VIEW_CAS, "CAS", perspectiveResources.menu_icon_cas()),
		/**
		 * 
		 */
		PROBABILITY(App.VIEW_PROBABILITY_CALCULATOR, "ProbabilityCalculator",
				perspectiveResources.menu_icon_probability()),
		/**
		 * 
		 */
		CONSTRUCTION_PROTOCOL(App.VIEW_CONSTRUCTION_PROTOCOL,
				"ConstructionProtocol", perspectiveResources
						.menu_icon_construction_protocol());

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

	static PerspectiveResources perspectiveResources = ((ImageFactory) GWT
			.create(ImageFactory.class)).getPerspectiveResources();
	private static ArrayList<ViewType> views = new ArrayList<ViewType>();
	private static ArrayList<ViewType> extensions = new ArrayList<ViewType>();
	static {
		views.add(ViewType.ALGEBRA);
		views.add(ViewType.GRAPHICS);
		views.add(ViewType.GRAPHICS_2);
		views.add(ViewType.GRAPHICS_3D);
		views.add(ViewType.SPREADSHEET);
		views.add(ViewType.CAS);
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
		ArrayList<ViewType> all = new ArrayList<ViewType>();
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