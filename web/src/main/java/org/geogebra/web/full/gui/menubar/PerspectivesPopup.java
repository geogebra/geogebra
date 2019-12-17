package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
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
		box = new DialogBoxW(true, false, null, app.getPanel(), app) {
			@Override
			public void setPopupPosition(int left, int top) {
				super.setPopupPosition(left,
						Math.max(0, (int) (app.getHeight() / 2 - 250)));
			}
		};

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
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		contentPanel.clear();
		addPerspective(0, pr.menu_icon_algebra_transparent());
		addPerspective(1, pr.menu_icon_geometry_transparent());
		if (app.supportsView(App.VIEW_EUCLIDIAN3D)) {
			addPerspective(4, pr.menu_icon_graphics3D_transparent());
		}
		if (app.supportsView(App.VIEW_CAS)) {
			addPerspective(3, pr.menu_icon_cas_transparent());
		}

		addPerspective(2, pr.menu_icon_spreadsheet_transparent());
		addPerspective(5, pr.menu_icon_probability_transparent());

		// add exam mode
		if (app.getLAF().examSupported()) {
			HorizontalPanel examRow = addPerspectiveRow(pr.menu_icon_exam_transparent(),
					"exam_menu_entry", -1, 7);
			contentPanel.add(examRow);
		}

		if (!app.getLAF().isTablet()) {

			// separator
			SimplePanel separator = new SimplePanel();
			separator.addStyleName("separatorDiv");

			// creating play store icon
			SVGResource res = GuiResources.INSTANCE.get_app();
			
			NoDragImage ndg = new NoDragImage(res.getSafeUri().asString(), 24);
			ndg.addStyleName("downloadimg");
			Anchor link = new Anchor("",
					"https://www.geogebra.org/download");
			link.addStyleName("linkDownload");
			link.setTarget("_blank");
			link.getElement().appendChild(ndg.getElement());
			InlineLabel linktext = new InlineLabel(
					app.getLocalization().getMenu("Download"));
			linktext.addStyleName("downloadlink");
			link.getElement().appendChild(
					linktext
							.getElement());

			// holder panel
			FlowPanel holderPanel = new FlowPanel();
			holderPanel.addStyleName("storeIconHolder");
			holderPanel.add(separator); // separator
			holderPanel.add(link);
			contentPanel.add(holderPanel);

		}

		box.getCaption()
				.setText(app.getLocalization().getMenu("CreateYourOwn"));
		AnchorElement helpLink = DOM.createAnchor().cast();
		helpLink.setHref(app.getLocalization()
				.getTutorialURL(app.getConfig()));
		helpLink.setTarget("_blank");
		NoDragImage helpBtn = new NoDragImage(GuiResources.INSTANCE.icon_help(),
				24);
		helpBtn.addStyleName("perspectivesHelp");
		helpLink.appendChild(helpBtn.getElement());
		box.getCaption().asWidget().getElement()
				.appendChild(helpLink);
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

	private HorizontalPanel addPerspectiveRow(ResourcePrototype icon,
			String menuID, final int index, final int defID) {
		HorizontalPanel rowPanel = new HorizontalPanel();
		// HorizontalPanel perspective = new HorizontalPanel();

		// icon
		rowPanel.add(new NoDragImage(icon, 24, 24));
		// perspective label
		Label label = new Label(app.getLocalization().getMenu(menuID));
		label.addStyleName("perspectivesLabel");
		rowPanel.add(label);
		rowPanel.setStyleName("perspectivesRow");

		// help button

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
					String URL = app.getLocalization()
							.getTutorialURL(app.getConfig());
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
