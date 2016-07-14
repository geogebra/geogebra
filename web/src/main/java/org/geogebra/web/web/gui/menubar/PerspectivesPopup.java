package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.util.debug.Log;
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
 * Apps Picker Dialog for new Start screen (GGB-992)
 */
public class PerspectivesPopup {
	/** dialog */
	private DialogBoxW box;
	/** application */
	AppW app;

	private static int activePerspective;

	public PerspectivesPopup(AppW app) {
		this.app = app;
	}

	public void showPerspectivesPopup() {
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
		ArrayList<ResourcePrototype> icons = new ArrayList<ResourcePrototype>();
		PerspectiveResources pr = ((ImageFactory) GWT
				.create(ImageFactory.class)).getPerspectiveResources();
		icons.add(pr.menu_icon_algebra());
		icons.add(pr.menu_icon_cas());
		icons.add(pr.menu_icon_geometry());
		icons.add(pr.menu_icon_graphics3D());
		icons.add(pr.menu_icon_spreadsheet());
		icons.add(pr.menu_icon_probability());
		icons.add(pr.menu_icon_exam());
		icons.add(GuiResources.INSTANCE.icon_help());

		box = new DialogBoxW(true, true, null, app.getPanel());
		box.setGlassEnabled(false);

		FlowPanel contentPanel = new FlowPanel();
		contentPanel.removeStyleName("dialogContent");
		contentPanel.addStyleName("perspectivesMainPanel");

		box.setWidget(contentPanel);
		box.addStyleName("perspectivesBox");
		box.getCaption().setText(app.getMenu("CreateYourOwn"));
		box.getCaption().asWidget().addStyleName("perspectivesCaption");

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			if (defaultPerspectives[i] == null) {
				continue;
			}
			final int index = i;
			final int defID = defaultPerspectives[i].getDefaultID();
			HorizontalPanel rowPanel = addPerspectiveRow(icons.get(i),
					defaultPerspectives[i].getId(), index, defID);
			if (activePerspective == index) {
				Log.debug("activePerspective: " + activePerspective);
				rowPanel.addStyleName("perspectiveHighlighted");
			} else {
				rowPanel.removeStyleName("perspectiveHighlighted");
			}
			contentPanel.add(rowPanel);
		}

		// add exam mode
		HorizontalPanel examRow = addPerspectiveRow(icons.get(6), "exam_menu_entry", -1, 7);
		contentPanel.add(examRow);

		// add link to tutorials
		HorizontalPanel tutorialsRow = addPerspectiveRow(icons.get(7),
				"Tutorials", -2, 8);
		tutorialsRow.addStyleName("upperBoarder");
		contentPanel.add(tutorialsRow);

		box.show();
	}

	final static String[] tutorials = new String[] { "graphing/", "graphing/", "geometry/", "spreadsheet/", "cas/",
			"3d/", "probability/", "exam/", "" };

	private HorizontalPanel addPerspectiveRow(ResourcePrototype icon,
			String menuID, final int index, final int defID) {
		HorizontalPanel rowPanel = new HorizontalPanel();
		HorizontalPanel perspective = new HorizontalPanel();

		// icon
		perspective.add(new Image(ImgResourceHelper.safeURI(icon)));
		// perspective label
		perspective.add(new Label(app.getMenu(menuID)));
		rowPanel.setStyleName("perspectivesRow");
		rowPanel.add(perspective);

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
					ToolTipManagerW.openWindow(URL);
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
						((AppWFull) app).showStartTooltip(defID);
					}
				} else if (index == -1) {
					if (app.getLAF().supportsFullscreen()) {
						ExamUtil.toggleFullscreen(true);
					}
					app.setExam(new ExamEnvironment());
					((AppWFull) app).examWelcome();
					// activePerspective = -1;
				} else if (index == -2) {
					String URL = GeoGebraConstants.QUICKSTART_URL + tutorials[defID]
							+ app.getLocalization().getLocaleStr() + "/";
					// TODO check if online
					ToolTipManagerW.openWindow(URL);
				}
				box.hide();
			}
		}, ClickEvent.getType());

		return rowPanel;
	}

	public int getActivePerspective() {
		return activePerspective;
	}

	public static void setActivePerspective(int index) {
		activePerspective = index;
	}
}
