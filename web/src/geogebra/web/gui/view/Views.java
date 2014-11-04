package geogebra.web.gui.view;

import geogebra.common.main.App;
import geogebra.web.gui.ImageFactory;
import geogebra.web.gui.images.PerspectiveResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Views {
	public static int[] ids = new int[]{App.VIEW_ALGEBRA, App.VIEW_SPREADSHEET, App.VIEW_CAS,App.VIEW_EUCLIDIAN, App.VIEW_EUCLIDIAN2, App.VIEW_EUCLIDIAN3D, App.VIEW_CONSTRUCTION_PROTOCOL, App.VIEW_PROBABILITY_CALCULATOR};
	private static PerspectiveResources perspectiveResources = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources(); 
	public static ResourcePrototype[] icons = new ResourcePrototype[]{
		perspectiveResources.view_algebra24(),
		perspectiveResources.view_spreadsheet24(),
		perspectiveResources.view_cas24(),
		perspectiveResources.view_graphics24(),
		perspectiveResources.view_graphics224(),
		perspectiveResources.view_graphics3D24(),
		perspectiveResources.view_constructionprotocol24(),
		perspectiveResources.view_probability24()
	};
	public static ResourcePrototype[] menuIcons = new ResourcePrototype[]{
		perspectiveResources.menu_icon_algebra(),
		perspectiveResources.menu_icon_spreadsheet(),
		perspectiveResources.menu_icon_cas(),
		perspectiveResources.menu_icon_graphics(),
		perspectiveResources.menu_icon_graphics2(),
		perspectiveResources.menu_icon_graphics3D(),
		perspectiveResources.menu_icon_construction_protocol(),
		perspectiveResources.menu_icon_probability()
	};
	public static String[] keys = new String[]{
		"AlgebraWindow",
		"Spreadsheet",
		"CAS",
		"DrawingPad",
		"DrawingPad2",
		"GraphicsView3D",
		"ConstructionProtocol",
		"ProbabilityCalculator"
	};
}
