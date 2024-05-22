package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class PerspectivesPopup {
	protected GPopupMenuW wrappedPopup;
	final AppWFull app;

	/**
	 * constructor
	 * @param app see {@link AppWFull}
	 */
	public PerspectivesPopup(final AppWFull app) {
		this.app = app;
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("perspectivePopup");
		buildGUI();
		addResizeHandler();
	}

	private void addResizeHandler() {
		if (wrappedPopup.isMenuShown()) {
			app.addWindowResizeListener(() ->
					wrappedPopup.showAtPoint((int) (app.getWidth() - 280),
							(int) ((app.getHeight() - 426) / 2)));
		}
	}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addHeader();
		wrappedPopup.addVerticalSeparator();

		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		addPerspectiveItem(pr.menu_icon_algebra_transparent(), 0);
		addPerspectiveItem(pr.menu_icon_geometry_transparent(), 1);

		if (app.supportsView(App.VIEW_EUCLIDIAN3D)) {
			addPerspectiveItem(pr.menu_icon_graphics3D_transparent(), 4);
		}

		if (app.supportsView(App.VIEW_CAS)) {
			addPerspectiveItem(pr.menu_icon_cas_transparent(), 3);
		}

		addPerspectiveItem(pr.menu_icon_spreadsheet_transparent(), 2);
		addPerspectiveItem(pr.menu_icon_probability_transparent(), 5);

		if (app.getLAF().examSupported()) {
			addPerspectiveItem(pr.menu_icon_exam_transparent(), -1);
		}

		if (!app.isOffline()) {
			wrappedPopup.addVerticalSeparator();
			addDownloadItem();
		}
	}

	private void addHeader() {
		AriaMenuItem headerMenuItem = new AriaMenuItem();
		headerMenuItem.addStyleName("headerItem");

		FlowPanel headerPanel = new FlowPanel();
		headerPanel.addStyleName("headerPanel");

		Label geogebraText = new Label(app.getLocalization().getMenu("CreateYourOwn"));
		headerPanel.add(geogebraText);

		StandardButton helpButton = new StandardButton(SharedResources.INSTANCE.icon_help_black(),
				null, 24, 24);
		helpButton.addStyleName("helpBtn");
		helpButton.addFastClickHandler(source -> {
			Browser.openWindow(app.getLocalization()
					.getTutorialURL(app.getConfig()));
			wrappedPopup.hide();
		});
		headerPanel.add(helpButton);

		headerMenuItem.setWidget(headerPanel);
		wrappedPopup.addItem(headerMenuItem);
	}

	private void addDownloadItem() {
		AriaMenuItem downloadMenuItem = new AriaMenuItem();
		FlowPanel download = new FlowPanel();
		download.addStyleName("downloadItem");

		download.add(new NoDragImage(GuiResources.INSTANCE.get_app(), 24));
		download.add(new Label(app.getLocalization().getMenu("Download")));

		downloadMenuItem.setWidget(download);
		downloadMenuItem.setScheduledCommand(
				() -> Browser.openWindow("https://www.geogebra.org/download"));
		wrappedPopup.addItem(downloadMenuItem);
	}

	private void addPerspectiveItem(SVGResource img, int perspectiveID) {
		Perspective perspective = app.getLayout().getDefaultPerspectives(perspectiveID);
		String text = perspective != null ? perspective.getId() : "exam_menu_entry";
		AriaMenuItem mi = new AriaMenuItem(MainMenu.getMenuBarHtml(img,
						app.getLocalization().getMenu(text)),
				true,
				() -> {
					if (perspective != null) {
						PerspectivesMenuW.setPerspective(app, perspective);
						if (!GlobalScope.examController.isExamActive()) {
							app.showStartTooltip(perspective);
						}
					} else {
						app.getLAF().toggleFullscreen(true);
						app.showExamWelcomeMessage();
					}
					wrappedPopup.hide();
				});
		wrappedPopup.addItem(mi);
	}

	/**
	 * show popup
	 */
	public void show() {
		wrappedPopup.showAtPoint((int) (app.getWidth() - 280),
				(int) ((app.getHeight() - 426) / 2));
	}

	public boolean isShowing() {
		return wrappedPopup.isMenuShown();
	}
}
