package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.web.Web;
import geogebra.web.Web.GuiToLoad;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Label;

public class WorksheetGUI extends HeaderPanel {

	private final Label instructionsPost, instructionsPre;
	private final FlowPanel frame = new FlowPanel();
	// private final WorksheetHeaderPanel header;
	private final AppWeb app;
	private final FileManagerM fm;
	private final FlowPanel content;
	TabletGUI tabletGUI;
	private DockLayoutPanel contentPanel;
	private WorksheetHeader header;

	public WorksheetGUI(AppWeb app, TabletGUI tabletGUI) {
		this.setStyleName("worksheetgui");
		this.fm = ((TouchApp) app).getFileManager();
		this.content = new FlowPanel();
		this.header = TouchEntryPoint.getLookAndFeel().buildWorksheetHeader(
				this, tabletGUI);
		this.app = app;
		this.tabletGUI = tabletGUI;

		this.instructionsPost = new Label();
		this.instructionsPre = new Label();
		this.instructionsPre.setStyleName("instructionsPre");
		this.instructionsPost.setStyleName("instructionsPost");
	}

	public FlowPanel getContent() {
		return this.content;
	}

	public DockLayoutPanel getContentPanel() {
		return this.contentPanel;
	}

	public void loadWorksheet(Material m) {
		this.header.setMaterial(m);
		this.contentPanel = this.tabletGUI.getContentPanel();

		if (m.getId() > 0) {
			this.content.add(this.instructionsPre);
			this.content.add(this.frame);
			this.content.add(this.instructionsPost);
			this.setContentWidget(this.content);

			// do not change allowEditing here -- we do not show any part of
			// TabletGUI
			Element article = DOM.createElement("article");
			article.setClassName("geogebraweb");
			article.setAttribute("data-param-ggbBase64", "");
			article.setAttribute("data-param-width", m.getWidth() + "");
			article.setAttribute("data-param-height", m.getHeight() + "");
			// right click makes no sense with Touch
			article.setAttribute("data-param-enableRightClick", "false");
			// label drags too hardd with Touch
			article.setAttribute("data-param-enableLabelDrags", "false");
			// TODO
			article.setAttribute("data-param-enableShiftDragZoom", "false");
			// ???
			article.setAttribute("data-param-showMenuBar", "false");
			// TODO
			article.setAttribute("data-param-showToolBar", "false");
			// TODO
			article.setAttribute("data-param-showAlgebraInput", "false");
			// TODO
			article.setAttribute("data-param-showResetIcon", "true");
			// no security issues here
			article.setAttribute("data-param-useBrowserForJS", "true");
			Element div = this.frame.getElement();
			int cc = div.getChildCount();
			for (int i = cc - 1; i >= 0; i--) {
				div.removeChild(div.getChild(i));
			}
			this.frame.getElement().appendChild(article);
			this.frame.setPixelSize(m.getWidth() + 2, m.getHeight() + 2);
			Web.currentGUI = GuiToLoad.VIEWER;
			Web.panelForApplets = this.frame;
			Web.urlToOpen = "http://www.geogebratube.org/files/material-"
					+ m.getId() + ".ggb";
			Web.loadAppletAsync();

			this.instructionsPre.setText(m.getInstructionsPre());
			this.instructionsPost.setText(m.getInstructionsPost());
		} else {
			TouchEntryPoint.allowEditing(false);
			this.fm.getMaterial(m, this.app);
			this.setContentWidget(this.contentPanel);
			this.updateViewSize();
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				WorksheetGUI.this.tabletGUI
						.updateViewSizes(WorksheetGUI.this.tabletGUI
								.isAlgebraShowing());
			}
		});
		App.debug("loading" + m.getTitle());
	}

	public void setLabels() {
		this.header.setLabels();
	}

	private void updateViewSize() {
		this.contentPanel.setPixelSize(Window.getClientWidth(),
				Window.getClientHeight()
						- TouchEntryPoint.getLookAndFeel().getAppBarHeight());
	}
}
