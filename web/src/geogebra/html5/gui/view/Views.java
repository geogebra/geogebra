package geogebra.html5.gui.view;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.resources.client.ImageResource;

public class Views {
	public static int[] ids = new int[]{App.VIEW_ALGEBRA, App.VIEW_SPREADSHEET, App.VIEW_EUCLIDIAN2, App.VIEW_EUCLIDIAN3D, App.VIEW_CONSTRUCTION_PROTOCOL};
	public static ImageResource[] icons = new ImageResource[]{
		AppResources.INSTANCE.view_algebra24(),
		AppResources.INSTANCE.view_spreadsheet24(),
		AppResources.INSTANCE.view_cas24(),
		AppResources.INSTANCE.view_graphics24(),
		AppResources.INSTANCE.view_graphics224(),
		AppResources.INSTANCE.view_graphics224(),//TODO !!!
		AppResources.INSTANCE.view_constructionprotocol24(),
	};
	public static String[] keys = new String[]{
		"AlgebraWindow",
		"Spreadsheet",
		"CAS",
		"DrawingPad",
		"DrawingPad2",
		"GraphicsView3D",//TODO !!!
		"ConstructionProtocol",
	};
}
