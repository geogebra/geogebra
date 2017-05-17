package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.NoDragImage;
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Apps Picker Dialog for new Start screen (GGB-992)
 */
public class PerspectivesPopup {
	/** dialog */
	private DialogBoxW box;
	/** application */
	final AppWFull app;
	private FlowPanel contentPanel;



	/**
	 * @param app
	 *            application
	 */
	public PerspectivesPopup(final AppWFull app) {
		this.app = app;
		box = new DialogBoxW(true, false, null, app.getPanel()){
			@Override
			public void setPopupPosition(int left, int top) {
				super.setPopupPosition(left,
						Math.max(0, (int) (app.getHeight() / 2 - 250)));
			}
		};
		
		if (app.has(Feature.DIALOGS_OVERLAP_KEYBOARD)) {
			box.setOverlapFeature(true);
		}
		
		box.setGlassEnabled(false);

		this.contentPanel = new FlowPanel();
		contentPanel.removeStyleName("dialogContent");
		contentPanel.addStyleName("perspectivesMainPanel");

		box.setWidget(contentPanel);
		box.addStyleName("perspectivesBox");

		box.getCaption().asWidget().addStyleName("perspectivesCaption");
		

	}

	/**
	 * Show the popup!
	 */
	public void showPerspectivesPopup() {
		setLabels();
		box.show();
	}

	private void setLabels() {
		PerspectiveResources pr = ((ImageFactory) GWT
				.create(ImageFactory.class)).getPerspectiveResources();
		contentPanel.clear();
		addPerspective(0, pr.menu_icon_algebra24());
		addPerspective(1, pr.menu_icon_geometry24());
		if (app.supportsView(App.VIEW_EUCLIDIAN3D)) {
			addPerspective(4, pr.menu_icon_graphics3D24());
		}
		if (app.supportsView(App.VIEW_CAS)) {
			addPerspective(3, pr.menu_icon_cas24());
		}

		addPerspective(2, pr.menu_icon_spreadsheet24());
		addPerspective(5, pr.menu_icon_probability24());


		// add exam mode
		if (app.getLAF().examSupported(app.has(Feature.EXAM_TABLET))) {
			HorizontalPanel examRow = addPerspectiveRow(pr.menu_icon_exam24(),
					"exam_menu_entry", -1, 7);
			contentPanel.add(examRow);
		}

		if (app.has(Feature.STORE_IMAGES_ON_APPS_PICKER)) {

			// separator
			SimplePanel separator = new SimplePanel();
			separator.addStyleName("separatorDiv");

			// creating play store icon
			NoDragImage playStoreIcon = new NoDragImage(ImgResourceHelper
					.safeURI(GuiResources.INSTANCE.google_play_badge()), 111);
			Anchor link_playstore = new Anchor(playStoreIcon.toString(), true,
					"https://play.google.com/store/apps/details?id=org.geogebra.android");
			link_playstore.addStyleName("linkPlaystore");

			// creating app store icon
			NoDragImage appStoreIcon = new NoDragImage(ImgResourceHelper
					.safeURI(GuiResources.INSTANCE.app_store()), 112);
			Anchor link_appstore = new Anchor(appStoreIcon.toString(), true,
					"https://itunes.apple.com/us/app/geogebra-graphing-calculator/id1146717204");

			// holder panel
			FlowPanel holderPanel = new FlowPanel();
			holderPanel.addStyleName("storeIconHolder");
			holderPanel.add(new SimplePanel()); // separator
			holderPanel.add(link_playstore);
			holderPanel.add(link_appstore);
			contentPanel.add(holderPanel);

		} else {
			// add link to tutorials
			HorizontalPanel tutorialsRow = addPerspectiveRow(
					GuiResources.INSTANCE.icon_help(), "Tutorials", -2, 8);
			tutorialsRow.addStyleName("upperBorder");
			contentPanel.add(tutorialsRow);

		}

		box.getCaption()
				.setText(app.getLocalization().getMenu("CreateYourOwn"));

	}

	private void addPerspective(int i, ResourcePrototype icon) {
		if (Layout.getDefaultPerspectives(i) == null) {
			return;
		}
		final int index = i;
		final int defID = Layout.getDefaultPerspectives(i).getDefaultID();
		HorizontalPanel rowPanel = addPerspectiveRow(icon,
				Layout.getDefaultPerspectives(i).getId(), index, defID);
		if (app.getActivePerspective() == index) {
			rowPanel.addStyleName("perspectiveHighlighted");
		} else {
			rowPanel.removeStyleName("perspectiveHighlighted");
		}
		contentPanel.add(rowPanel);

	}

	/** tutorial links */
	final static String[] tutorials = new String[] { "graphing/", "graphing/", "geometry/", "spreadsheet/", "cas/",
			"3d/", "probability/", "exam/", "" };

	private HorizontalPanel addPerspectiveRow(ResourcePrototype icon,
			String menuID, final int index, final int defID) {
		HorizontalPanel rowPanel = new HorizontalPanel();
		// HorizontalPanel perspective = new HorizontalPanel();

		// icon
		rowPanel.add(new Image(ImgResourceHelper.safeURI(icon)));
		// perspective label
		Label label = new Label(app.getLocalization().getMenu(menuID));
		label.addStyleName("perspectivesLabel");
		rowPanel.add(label);
		rowPanel.setStyleName("perspectivesRow");

		// help button
		if (index != -2) {
			Image helpBtn = new Image(GuiResources.INSTANCE.icon_help());
			helpBtn.addStyleName("perspectivesHelp");
			helpBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String URL = GeoGebraConstants.QUICKSTART_URL + tutorials[defID]
							+ app.getLocalization().getLocaleStr() + "/";
					// TODO check if online
					app.getFileManager().open(URL);
					event.stopPropagation();
				}
			});
			rowPanel.add(helpBtn);
		}

		rowPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (index >= 0) {
					PerspectivesMenuW.setPerspective(app, index);
					if (!(app.isExam() && app.getExam().getStart() >= 0)) {
						app.showStartTooltip(defID);
					}
				} else if (index == -1) {
					app.getLAF().toggleFullscreen(true);
					app.setNewExam();
					app.examWelcome();
					// activePerspective = -1;
				} else if (index == -2) {
					String URL = GeoGebraConstants.QUICKSTART_URL + tutorials[defID]
							+ app.getLocalization().getLocaleStr() + "/";
					// TODO check if online
					app.getFileManager().open(URL);
				}
				closePerspectivesPopup();
			}
		}, ClickEvent.getType());

		return rowPanel;
	}

	/**
	 * Close the popup
	 */
	public void closePerspectivesPopup() {
		box.hide();
	}

	/**
	 * @return whether popup is showing
	 */
	public boolean isShowing() {
		return box.isShowing();
	}

}
