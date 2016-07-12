package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Web implementation of FileMenu
 */
public class PerspectivesMenuW extends GMenuBar {
	
	/** Application */
	AppW app;
	private Layout layout;
	
	/**
	 * @param app application
	 */
	public PerspectivesMenuW(AppW app) {
	    super(true);
	    this.app = app;
	    this.layout = app.getGuiManager().getLayout();
	    addStyleName("GeoGebraMenuBar");
		initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {

		
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
	    ArrayList<ResourcePrototype> icons = new ArrayList<ResourcePrototype>();
	    PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		if (app.has(Feature.NEW_START_SCREEN)) {
			icons.add(pr.menu_icon_algebra());
			icons.add(pr.menu_icon_cas());
			icons.add(pr.menu_icon_geometry());
			icons.add(pr.menu_icon_graphics3D());
			icons.add(pr.menu_icon_spreadsheet());
			icons.add(pr.menu_icon_probability());
			icons.add(pr.menu_icon_exam());
		} else {
			icons.add(pr.menu_icon_algebra());
			icons.add(pr.menu_icon_geometry());
			icons.add(pr.menu_icon_spreadsheet());
			icons.add(pr.menu_icon_cas());
			icons.add(pr.menu_icon_graphics3D());
			icons.add(pr.menu_icon_probability());
		}
		for (int i = 0; i < defaultPerspectives.length; ++i) {
			if(defaultPerspectives[i] == null){
				continue;
			}
			final int index = i;
			final int defID = defaultPerspectives[i].getDefaultID();
			addItem(MainMenu.getMenuBarHtml(
					ImgResourceHelper.safeURI(icons.get(i)),
					app.getMenu(defaultPerspectives[i].getId()), true),true,new MenuCommand(app) {
						
						@Override
						public void doExecute() {
							setPerspective(app, index);
							if (!(app.isExam() && app.getExam().getStart() >= 0)) {
								((AppWFull) app).showStartTooltip(defID);
							}
						}
			});			
		}
		// this is enabled always

	}

	/**
	 * Apps Picker Dialog for new Start screen (GGB-992)
	 */
	final static String[] tutorials = new String[] { "graphing/", "graphing/",
			"geometry/", "spreadsheet/", "cas/", "3d/",
			"probability/", "exam/", "" };
	private DialogBoxW box;

	


	/**
	 * @param index
	 *            perspective index
	 */
	static void setPerspective(AppW app, int index) {
		app.persistWidthAndHeight();
		boolean changed = app.getGuiManager().getLayout()
				.applyPerspective(Layout.defaultPerspectives[index]);
		app.updateViewSizes();
		app.getGuiManager().updateMenubar();
		// app.getToolbar().closeAllSubmenu();
		if (app.getTubeId() < 1 && app.getArticleElement().getDataParamApp()) {
			Browser.changeUrl(Perspective.perspectiveSlugs[index]);
		}
		if (changed) {
			app.storeUndoInfo();
		}
	}
	

}
