package org.geogebra.web.full.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class of submenus of MOWToorbar
 * 
 * @author Laszlo Gal, Alicia Hofstaetter
 *
 */
public abstract class SubMenuPanel extends FlowPanel
		implements FastClickHandler {
	/**
	 * panel row
	 */
	protected FlowPanel panelRow;

	/** app **/
	AppW app;
	// private boolean info;
	/**
	 * Scrollpanel to larger toolbars like 'Tools'
	 */
	ScrollPanel scrollPanel;

	/**
	 * Here goes the toolbar contents ie the buttons
	 */
	FlowPanel contentPanel;

	/**
	 * group panel
	 */
	protected static class GroupPanel extends FlowPanel {
		private static final int BUTTON_WIDTH = 46;

		/**
		 * constructor
		 */
		public GroupPanel() {
			addStyleName("groupPanel");
		}

		/**
		 * @param columns
		 *            nr of cloumns
		 */
		public void setColumns(int columns) {
			setWidth(((columns) * BUTTON_WIDTH) + "px");
		}
	}

	/**
	 * The info (help) panel for the selected tool.
	 */
	// FlowPanel infoPanel;

	/**
	 * icon of the tool for
	 */
	// NoDragImage infoImage;

	/** '?' image */
	// NoDragImage questionMark;

	/** the brief info itself */
	// HTML infoLabel;

	/** link to more info. */
	// String infoURL;

	/**
	 * 
	 * @param app
	 *            GGB application.
	 */
	public SubMenuPanel(AppW app/* , boolean info */) {
		this.app = app;
		// this.info = info;
		createGUI();
	}

	/**
	 * Makes the content panel and the info panel, if needed.
	 */
	protected void createGUI() {
		addStyleName("mowSubMenu");
		createContentPanel();
		/*
		 * if (hasInfo()) { createInfoPanel();
		 * add(LayoutUtilW.panelRow(scrollPanel, infoPanel)); } else {
		 */
		add(scrollPanel);
		// }
	}

	/**
	 * Creates the scrollable panel of contents.
	 */
	protected void createContentPanel() {
		scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("mowSubMenuContent");
		contentPanel = new FlowPanel();
		scrollPanel.add(contentPanel);
	}

	/**
	 * @param mowToolsDefString
	 *            mow toolbar definition
	 */
	public void createPanelRow(String mowToolsDefString) {
		panelRow = new FlowPanel();
		panelRow.addStyleName("panelRow");
		addModesToToolbar(panelRow, mowToolsDefString);
		contentPanel.add(panelRow);
	}

	/**
	 * Creates the info panel.
	 */
	/*
	 * protected void createInfoPanel() { infoPanel = new FlowPanel();
	 * infoPanel.addStyleName("mowSubMenuInfo"); }
	 */

	/**
	 * 
	 * @param toolbarString
	 *            GGB toolbar definition string.
	 * @return The vector of modes.
	 */
	protected Vector<ToolbarItem> getToolbarVec(String toolbarString) {
		Vector<ToolbarItem> toolbarVec;
		try {
			toolbarVec = ToolBar.parseToolbarString(toolbarString);
		} catch (Exception e) {
			toolbarVec = ToolBar.parseToolbarString(ToolBar.getAllTools(app));
		}
		return toolbarVec;
	}

	/**
	 * Adds tool buttons to content panel depending on a toolbar definition
	 * string.
	 * 
	 * @param toolbarString
	 *            The toolbar definition string
	 */
	protected void addModesToToolbar(String toolbarString) {
		addModesToToolbar(contentPanel, toolbarString);
	}

	/**
	 * Adds tool buttons to an arbitrary panel depending on a toolbar definition
	 * string.
	 * 
	 * @param panel
	 *            The panel to add mode buttons.
	 * @param toolbarString
	 *            The toolbar definition string
	 */
	protected void addModesToToolbar(FlowPanel panel, String toolbarString) {
		Vector<ToolbarItem> toolbarVec = getToolbarVec(toolbarString);
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();
			addModeMenu(panel, menu);
		}
	}

	/**
	 * Add buttons to a panel depending a mode vector. add modes to a group so
	 * they get grouped in the toolbox
	 * 
	 * @param panel
	 *            The panel to add the mode buttons.
	 * @param menu
	 *            The vector of modes to add buttons.
	 */
	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		int col = 0;
		GroupPanel group = new GroupPanel();
		for (Integer mode : menu) {
			if (app.isModeValid(mode)) {
				StandardButton btn = createButton(mode);
				group.add(btn);
				col++;
			}
		}
		group.setColumns(col / 2 + col % 2);
		panel.add(group);
	}

	/**
	 * Creates a toolbar button from a mode.
	 * 
	 * @param mode
	 *            The mode the button will stand for.
	 * @return Newly created toolbar button to set its mode.
	 */
	protected StandardButton createButton(int mode) {
		NoDragImage im = new NoDragImage(AppResources.INSTANCE.empty(), 32);
		GGWToolBar.getImageResource(mode, app, im);
		// opacity hack: old icons don't need opacity, new ones do
		if (imageNeedsOpacity(mode)) {
			im.addStyleName("opacityFixForOldIcons");
		}
		StandardButton button = new StandardButton(null, "", 32, app);
		button.getUpFace().setImage(im);
		button.setBtnImage(im);
		button.addFastClickHandler(this);
		button.addStyleName("mowToolButton");
		if (modeAvailable(mode)) {
			button.addStyleName("inactiveToolButton");
		}
		String altText = app.getLocalization()
				.getMenu(EuclidianConstants.getModeText(mode)) + ". "
				+ app.getToolHelp(mode);
		button.setTitle(app.getLocalization()
				.getMenu(EuclidianConstants.getModeText(mode)));
		button.setAltText(altText);
		button.getElement().setAttribute("mode", mode + "");
		button.getElement().setId("mode" + mode);
		return button;
	}

	/**
	 * 
	 * @return true if submenu has info panel.
	 */
	/*
	 * public boolean hasInfo() { return info; }
	 */

	/**
	 * 
	 * @param info
	 *            true if submenu should have info panel, false if not.
	 */
	/*
	 * public void setInfo(boolean info) { this.info = info; }
	 */

	private boolean modeAvailable(int mode) {
		return (mode == EuclidianConstants.MODE_VIDEO
				&& !app.has(Feature.MOW_VIDEO_TOOL))
				|| (mode == EuclidianConstants.MODE_AUDIO
						&& !app.has(Feature.MOW_AUDIO_TOOL))
				|| (mode == EuclidianConstants.MODE_CAMERA
						&& !app.has(Feature.MOW_IMAGE_DIALOG_UNBUNDLED))
				|| (mode == EuclidianConstants.MODE_PDF
						&& !app.has(Feature.MOW_PDF_TOOL))
				|| (mode == EuclidianConstants.MODE_GEOGEBRA
						&& !app.has(Feature.MOW_GEOGEBRA_TOOL));
	}

	/**
	 * Initializes the submenu when it is opened from MOWToolbar.
	 */
	public void onOpen() {
		int mode = app.getMode();
		setMode(mode);
	}

	@Override
	public void onClick(Widget source) {
		int pos = scrollPanel.getHorizontalScrollPosition();
		int mode = Integer.parseInt(source.getElement().getAttribute("mode"));
		if (mode == EuclidianConstants.MODE_IMAGE) {
			// set css before file picker
			setMode(mode);
		}
		app.setMode(mode);
		scrollPanel.setHorizontalScrollPosition(pos);
		closeFloatingMenus();
		/*
		 * if (hasInfo()) { infoPanel.clear(); showToolTip(mode); }
		 */
	}

	/**
	 * Closes burger menu and page control panel
	 */
	public void closeFloatingMenus() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		if (app.has(Feature.MOW_MULTI_PAGE)) {
			((AppWFull) app).getAppletFrame().getPageControlPanel().close();
		}
	}

	// @Override
	/*
	 * public void onClick(ClickEvent event) { if (event.getSource() ==
	 * questionMark) { app.getFileManager().open(infoURL); } }
	 */

	/**
	 * Add tooltips to info panel
	 * 
	 * @param mode
	 *            The mode of the tool that needs info.
	 */
	/*
	 * protected void showToolTip(int mode) { if (mode >= 0) { infoImage = new
	 * NoDragImage(GGWToolBar.getImageURL(mode, app));
	 * infoImage.addStyleName("mowToolButton"); // opacity hack: old icons don't
	 * need opacity, new ones do if (imageNeedsOpacity(mode)) {
	 * infoImage.addStyleName("opacityFixForOldIcons"); } infoLabel = new
	 * HTML(app.getToolTooltipHTML(mode));
	 * infoLabel.addStyleName("mowInfoLabel"); infoURL =
	 * app.getGuiManager().getTooltipURL(mode); questionMark = new
	 * NoDragImage(ImgResourceHelper.safeURI(GGWToolBar.getMyIconResourceBundle(
	 * ).help_32())); infoPanel.add(infoImage); infoPanel.add(infoLabel);
	 * 
	 * boolean online = app.getNetworkOperation() == null ||
	 * app.getNetworkOperation().isOnline(); if (infoURL != null &&
	 * infoURL.length() > 0 && online) { questionMark.addClickHandler(this);
	 * questionMark.addStyleName("mowQuestionMark");
	 * infoPanel.add(questionMark); } } }
	 */

	/**
	 * Decide if icon needs opacity or not. New icons are black with opacity.
	 * Old icons don't need opacity so they get a style fix.
	 * 
	 * @param mode
	 *            app mode
	 * @return true if icon needs a style fix
	 */
	protected boolean imageNeedsOpacity(int mode) {
		if ((mode < 101 && mode != EuclidianConstants.MODE_TEXT
				&& mode != EuclidianConstants.MODE_IMAGE
				&& mode != EuclidianConstants.MODE_PEN
				&& mode != EuclidianConstants.MODE_SELECT)
				|| (mode > 110 && mode != EuclidianConstants.MODE_VIDEO
						&& mode != EuclidianConstants.MODE_AUDIO
						&& mode != EuclidianConstants.MODE_GEOGEBRA
						&& mode != EuclidianConstants.MODE_CAMERA
						&& mode != EuclidianConstants.MODE_HIGHLIGHTER
						&& mode != EuclidianConstants.MODE_PDF)) {
			return true;
		}
		return false;
	}

	/**
	 * Set css to selected for given mode
	 * 
	 * @param mode
	 *            The mode to select
	 */
	public void setMode(int mode) {
		for (int i = 0; i < panelRow.getWidgetCount(); i++) {
			FlowPanel w = (FlowPanel) panelRow.getWidget(i);
			for (int j = 0; j < w.getWidgetCount(); j++) {
				int modeID = Integer.parseInt(
						w.getWidget(j).getElement().getAttribute("mode"));
				if (modeID != mode) {
					w.getWidget(j).getElement().setAttribute("selected",
							"false");
				} else {
					w.getWidget(j).getElement().setAttribute("selected",
							"true");
				}
			}
		}
	}

	/**
	 * @return first mode; to be selected once this submenu is opened
	 */
	public abstract int getFirstMode();
}
