package geogebra.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle {
	
	GuiResources INSTANCE = GWT.create(GuiResources.class);
	
	//@Source("geogebra/resources/images/ggb4-splash-h120.png")
	//ImageResource getGeoGebraWebSplash();
	
	@Source("geogebra/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();
	
	@Source("geogebra/resources/images/nav_play.png")
	ImageResource navPlay();
	
	@Source("geogebra/resources/images/nav_pause.png")
	ImageResource navPause();
	
	@Source("geogebra/resources/images/view-refresh.png")
	ImageResource viewRefresh();
	
	@Source("geogebra/resources/images/spacer.png")
	ImageResource spacer();
	
	@Source("icons/png/web/algebra-view-tree-open.png")
	ImageResource algebra_tree_open();
	
	@Source("icons/png/web/algebra-view-tree-closed.png")
	ImageResource algebra_tree_closed();
	
	@Source("geogebra/resources/images/triangle-down.png")
	ImageResource triangle_down();

	@Source("geogebra/resources/images/splash-ggb4.svg")
	TextResource ggb4Splash();
	
	@Source("geogebra/resources/js/zipjs/dataview.js")
	TextResource dataViewJs();
	
	@Source("geogebra/resources/js/zipjs/zip-3.js")
	TextResource zipJs();
	
	@Source("geogebra/pub/js/zipjs/deflate.js")
	TextResource deflateJs();

	@Source("geogebra/pub/js/zipjs/inflate.js")
	TextResource inflateJs();
	
	@Source("geogebra/resources/js/zipjs/base64.js")
	TextResource base64Js();
	
	@Source("geogebra/resources/js/zipjs/arraybuffer.js")
	TextResource arrayBufferJs();

	
	@Source("geogebra/resources/css/mathquillggb.css")
	TextResource mathquillggbCss();
	
	@Source("geogebra/resources/default-preferences.xml")
	TextResource preferencesXML();
	
	@Source("geogebra/resources/js/jquery-1.7.2.min.js")
	TextResource jQueryJs();

	@Source("geogebra/resources/js/mathquillggb.js")
	TextResource mathquillggbJs();

	@Source("geogebra/pub/js/properties_keys_en.js")
	TextResource propertiesKeysJS();
	
	@Source("geogebra/resources/images/spinner.html")
	TextResource ggbSpinnerHtml();
	
	@Source("geogebra/resources/images/ggbSplash.html")
	TextResource ggbSplashHtml();

	@Source("geogebra/resources/css/clean-2.css")
	TextResource style();
	
	@Source("geogebra/resources/css/web-styles.css")
	TextResource modernStyle();
	
	//SMART TOOLBAR: Open menu and open search
	@Source("icons/png/web/menu-button-open-search.png")
	ImageResource button_open_search();
	
	@Source("icons/png/web/menu-button-open-menu.png")
	ImageResource button_open_menu();
	
	//SMART MENUBAR: Icons
	@Source("icons/png/web/menu_icons/menu-file.png")
	ImageResource menu_icon_file();
	
	@Source("icons/png/web/menu_icons/menu-file-new.png")
	ImageResource menu_icon_file_new();
	
	@Source("icons/png/web/menu_icons/menu-file-open.png")
	ImageResource menu_icon_file_open();
	
	@Source("icons/png/web/menu_icons/menu-file-share.png")
	ImageResource menu_icon_file_share();
	
	@Source("icons/png/web/menu_icons/menu-edit.png")
	ImageResource menu_icon_edit();
	
	@Source("icons/png/web/menu_icons/menu-view.png")
	ImageResource menu_icon_view();
	
	@Source("icons/png/web/menu_icons/menu-help.png")
	ImageResource menu_icon_help();
	
	@Source("icons/png/web/menu_icons/menu-options.png")
	ImageResource menu_icon_options();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-algebra.png")
	ImageResource menu_icon_perspectives_algebra();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-basic-geometry.png")
	ImageResource menu_icon_perspectives_basic_geometry();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-geometry.png")
	ImageResource menu_icon_perspectives_geometry();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-spreadsheet.png")
	ImageResource menu_icon_perspectives_spreadsheet();
	
	@Source("icons/png/web/menu_icons/menu-perspectives-cas.png")
	ImageResource menu_icon_perspectives_cas();
	
	//SUBMENUS
	@Source("icons/png/web/arrow-submenu-right.png")
	ImageResource arrow_submenu_right();
	
}

