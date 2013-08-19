package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.web.Web;
import geogebra.web.Web.GuiToLoad;

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
	private final AppWeb app;
	private final FileManagerT fm;
	private final FlowPanel content;
	private final TabletGUI tabletGUI;
	private DockLayoutPanel contentPanel;
	private final WorksheetHeader header;

	public WorksheetGUI(final AppWeb app) {
		this.setStyleName("worksheetgui");
		this.fm = ((TouchApp) app).getFileManager();
		this.content = new FlowPanel();
		this.content.setStyleName("worksheet");
		this.header = TouchEntryPoint.getLookAndFeel().buildWorksheetHeader(
				this);
		this.app = app;
		this.tabletGUI = (TabletGUI) ((TouchApp) app).getTouchGui();

		this.instructionsPost = new Label();
		this.instructionsPre = new Label();
		this.instructionsPre.setStyleName("instructionsPre");
		this.instructionsPost.setStyleName("instructionsPost");
		onResize();
	}

	public FlowPanel getContent() {
		return this.content;
	}
	
	@Override
	public void onResize(){
		this.content.setHeight((Window.getClientHeight()-TouchEntryPoint.getLookAndFeel().getTabletHeaderHeight())+"px");
		//this.content.getElement().getStyle().setOverflow(Overflow.AUTO);
	}

	public DockLayoutPanel getContentPanel() {
		return this.contentPanel;
	}

	public void loadWorksheet(final Material m) {
		this.header.setMaterial(m);
		this.contentPanel = this.tabletGUI.getContentPanel();

		if (m.getId() > 0) {
			this.content.add(this.instructionsPre);
			this.content.add(this.frame);
			this.content.add(this.instructionsPost);
			this.setContentWidget(this.content);

			final Element article = DOM.createElement("article");
			article.setClassName("geogebraweb");
			article.setAttribute("data-param-ggbBase64", "");
			article.setAttribute("data-param-width", m.getWidth() + "");
			article.setAttribute("data-param-height", m.getHeight() + "");
			// right click makes no sense with Touch
			article.setAttribute("data-param-enableRightClick", "false");
			// label drags too hardd with Touch
			article.setAttribute("data-param-enableLabelDrags", "false");
			article.setAttribute("data-param-enableShiftDragZoom",
					m.getShiftDragZoom());
			article.setAttribute("data-param-showMenuBar", m.getShowMenu());
			article.setAttribute("data-param-showToolBar", m.getShowToolbar());
			article.setAttribute("data-param-showAlgebraInput",
					m.getShowInputbar());
			article.setAttribute("data-param-showResetIcon",
					m.getShowResetIcon());
			// no security issues here
			article.setAttribute("data-param-useBrowserForJS", "true");
			final Element div = this.frame.getElement();
			final int cc = div.getChildCount();
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

			this.instructionsPre.getElement().setInnerHTML(
					m.getInstructionsPre());
			this.instructionsPost.getElement().setInnerHTML(
					m.getInstructionsPost());
		} else {
			TouchEntryPoint.allowEditing(false);
			this.fm.getMaterial(m, this.app);
			this.setContentWidget(this.contentPanel);
		}
		App.debug("loading" + m.getTitle());
	}

	public void setLabels() {
		this.header.setLabels();
	}
}
