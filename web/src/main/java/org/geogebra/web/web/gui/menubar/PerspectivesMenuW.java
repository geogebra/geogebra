package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamUtil;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

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
	    icons.add(pr.menu_icon_algebra());
	    icons.add(pr.menu_icon_geometry());
	    icons.add(pr.menu_icon_spreadsheet());
	    icons.add(pr.menu_icon_cas());
	    icons.add(pr.menu_icon_graphics3D());
	    icons.add(pr.menu_icon_probability());
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
							setPerspective(index);
							if (!(app.isExam() && app.getExam().getStart() >= 0)) {
								((AppWFull) app).showStartTooltip(defID);
							}
						}
			});			
		}
		// this is enabled always
	}

	final String[] tutorials = new String[] { "graphing", "graphing", "geometry", "spreadsheet", "cas", "3d",
			"probability", "exam" };
	private DialogBoxW box;

	public void showPerspectivesPopup() {
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
		ArrayList<ResourcePrototype> icons = new ArrayList<ResourcePrototype>();
		PerspectiveResources pr = ((ImageFactory) GWT.create(ImageFactory.class)).getPerspectiveResources();
		icons.add(pr.menu_icon_algebra());
		icons.add(pr.menu_icon_geometry());
		icons.add(pr.menu_icon_spreadsheet());
		icons.add(pr.menu_icon_cas());
		icons.add(pr.menu_icon_graphics3D());
		icons.add(pr.menu_icon_probability());
		icons.add(pr.menu_icon_exam());

		box = new DialogBoxW(true, true, null, app.getPanel());
		box.setGlassEnabled(false);

		FlowPanel contentPanel = new FlowPanel();
		contentPanel.removeStyleName("dialogContent");
		contentPanel.addStyleName("perspectivesMainPanel");

		box.setWidget(contentPanel);
		box.addStyleName("perspectivesBox");
		box.getCaption().setText(app.getMenu("CreateYourOwn"));
		box.center();

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			if (defaultPerspectives[i] == null) {
				continue;
			}
			final int index = i;
			final int defID = defaultPerspectives[i].getDefaultID();
			HorizontalPanel rowPanel = addPerspectiveRow(icons.get(i), defaultPerspectives[i].getId(), index, defID);
			contentPanel.add(rowPanel);

			if (i % 2 == 1) {
				rowPanel.addStyleName("perspectivesMargin");
			}
		}
		// add exam mode
		HorizontalPanel examRow = addPerspectiveRow(icons.get(6), "exam_menu_enter", -1, 7);
		contentPanel.add(examRow);

		// add link to tutorials

	}

	private HorizontalPanel addPerspectiveRow(ResourcePrototype icon, String menuID, final int index, final int defID) {
		HorizontalPanel rowPanel = new HorizontalPanel();
		HorizontalPanel perspective = new HorizontalPanel();

		// icon
		perspective.add(new Image(ImgResourceHelper.safeURI(icon)));
		// perspective label
		perspective.add(new Label(app.getMenu(menuID)));
		// help button
		Image helpBtn = new Image(GuiResources.INSTANCE.icon_help());

		rowPanel.setStyleName("perspectivesRow");
		helpBtn.addStyleName("perspectivesHelp");

		rowPanel.add(perspective);
		rowPanel.add(helpBtn);

		rowPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (index >= 0) {
					setPerspective(index);
					if (!(app.isExam() && app.getExam().getStart() >= 0)) {
						((AppWFull) app).showStartTooltip(defID);
					}
				} else if (index == -1) {
					ExamUtil.toggleFullscreen(true);
					app.setExam(new ExamEnvironment());
					((AppWFull) app).examWelcome();
				}
				box.hide();
			}
		}, ClickEvent.getType());

		helpBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String URL = GeoGebraConstants.QUICKSTART_URL + tutorials[defID] + "/"
						+ app.getLocalization().getLocaleStr() + "/";
				// TODO check if online
				ToolTipManagerW.openWindow(URL);
				event.stopPropagation();
			}
		});

		return rowPanel;
	}
	

	/**
	 * @param index
	 *            perspective index
	 */
	void setPerspective(int index) {
		app.persistWidthAndHeight();
		boolean changed = layout
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
