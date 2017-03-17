package org.geogebra.web.web.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class of submenus of MOWToorbar
 * 
 * @author Laszlo Gal, Alicia Hofstaetter
 *
 */
public abstract class SubMenuPanel extends FlowPanel
		implements ClickHandler, FastClickHandler {

	/** app **/
	AppW app;
	private boolean info;
	/**
	 * Scrollpanel to larger toolbars like 'Tools'
	 */
	ScrollPanel scrollPanel;

	/**
	 * Here goes the toolbar contents ie the buttons
	 */
	FlowPanel contentPanel;

	/**
	 * The info (help) panel for the selected tool.
	 */
	FlowPanel infoPanel;

	/**
	 * icon of the tool for
	 */
	NoDragImage infoImage;

	/** '?' image */
	NoDragImage questionMark;

	/** the brief info itself */
	HTML infoLabel;

	/** link to more info. */
	String infoURL;

	/**
	 * 
	 * @param app
	 *            GGB application.
	 * @param info
	 *            true if submenu should have an info panel.
	 */
	public SubMenuPanel(AppW app, boolean info) {
		this.app=app;
		this.info = info;
		createGUI();
	}

	/**
	 * Makes the content panel and the info panel, if needed.
	 */
	protected void createGUI() {
		addStyleName("mowSubMenu");
		createContentPanel();
		if (hasInfo()) {
			createInfoPanel();
			add(LayoutUtilW.panelRow(scrollPanel, infoPanel));
		} else {
			add(scrollPanel);
		}
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
	 * Creates the info panel.
	 */
	protected void createInfoPanel() {
		infoPanel = new FlowPanel();
		infoPanel.addStyleName("mowSubMenuInfo");
	}

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
	 * Add buttons to a panel depending a mode vector.
	 * 
	 * @param panel
	 *            The panel to add the mode buttons.
	 * @param menu
	 *            The vector of modes to add buttons.
	 */
	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		if (app.isModeValid(menu.get(0).intValue())) {
			panel.add(createButton(menu.get(0).intValue()));
		}
	}

	/**
	 * Creates a toolbar button from a mode.
	 * 
	 * @param mode
	 *            The mode the button will stand for.
	 * @return Newly created toolbar button to set its mode.
	 */
	protected StandardButton createButton(int mode) {
		NoDragImage im = new NoDragImage(GGWToolBar.getImageURL(mode, app));
		// opacity hack: old icons don't need opacity, new ones do
		if (imageNeedsOpacity(mode)) {
			im.addStyleName("opacityFixForOldIcons");
		}
		StandardButton button = new StandardButton(null, "", 32);
		button.getUpFace().setImage(im);
		button.addFastClickHandler(this);

		button.addStyleName("mowToolButton");
		button.getElement().setAttribute("mode", mode + "");
		button.getElement().setId("mode" + mode);
		return button;
	}

	/**
	 * 
	 * @return true if submenu has info panel.
	 */
	public boolean hasInfo() {
		return info;
	}

	/**
	 * 
	 * @param info
	 *            true if submenu should have info panel, false if not.
	 */
	public void setInfo(boolean info) {
		this.info = info;
	}

	/**
	 * Initializes the submenu when it is opened from MOWToolbar.
	 */
	public void onOpen() {
		int mode = app.getMode();
		setMode(mode);
	}

	/**
	 * reset
	 */
	public void reset() {
		deselectAllCSS();
		infoPanel.clear();
	}

	@Override
	public void onClick(Widget source) {
		int pos = scrollPanel.getHorizontalScrollPosition();
		int mode = Integer.parseInt(source.getElement().getAttribute("mode"));
		setCSStoSelected(source);
		app.setMode(mode);
		if (hasInfo()) {
			infoPanel.clear();
			showToolTip(mode);
		}
		scrollPanel.setHorizontalScrollPosition(pos);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == questionMark) {
			app.getFileManager().open(infoURL);
		}
	}

	/**
	 * Sets CSS of active tool to selected
	 * 
	 * @param source
	 *            The widget to select.
	 */
	public void setCSStoSelected(Widget source) {

		FlowPanel parent = (FlowPanel) source.getParent();
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			Widget w = parent.getWidget(i);
			if (w != source) {
				w.getElement().setAttribute("selected", "false");
			} else {
				w.getElement().setAttribute("selected", "true");
			}
		}
	}

	/**
	 * unselect all tools
	 */
	public void deselectAllCSS() {
		// override in subclasses
	}


	/**
	 * Add tooltips to info panel
	 * 
	 * @param mode
	 *            The mode of the tool that needs info.
	 */
	protected void showToolTip(int mode) {
		if (mode >= 0) {
			infoImage = new NoDragImage(GGWToolBar.getImageURL(mode, app));
			infoImage.addStyleName("mowToolButton");
			// opacity hack: old icons don't need opacity, new ones do
			if (imageNeedsOpacity(mode)) {
				infoImage.addStyleName("opacityFixForOldIconsSelected");
			} else {
				infoImage.addStyleName("selected");
			}
			infoLabel = new HTML(app.getToolTooltipHTML(mode));
			infoLabel.addStyleName("mowInfoLabel");
			infoURL = app.getGuiManager().getTooltipURL(mode);
			questionMark = new NoDragImage(ImgResourceHelper.safeURI(GGWToolBar.getMyIconResourceBundle().help_32()));
			infoPanel.add(infoImage);
			infoPanel.add(infoLabel);

			boolean online = app.getNetworkOperation() == null || app.getNetworkOperation().isOnline();
			if (infoURL != null && infoURL.length() > 0 && online) {
				questionMark.addClickHandler(this);
				questionMark.addStyleName("mowQuestionMark");
				infoPanel.add(questionMark);
			}
		}
	}

	/**
	 * Decide if icon needs opacity or not. New icons are black with opacity.
	 * Old icons don't need opacity so they get a style fix.
	 * 
	 * @param mode
	 *            app mode
	 * @return true if icon needs a style fix
	 */
	protected boolean imageNeedsOpacity(int mode) {
		if ((mode < 101 && mode != EuclidianConstants.MODE_TEXT && mode != EuclidianConstants.MODE_IMAGE
				&& mode != EuclidianConstants.MODE_PEN && mode != EuclidianConstants.MODE_FREEHAND_SHAPE)
				|| (mode > 110 && mode != EuclidianConstants.MODE_VIDEO && mode != EuclidianConstants.MODE_AUDIO
						&& mode != EuclidianConstants.MODE_GEOGEBRA)) {
			return true;
		}
		return false;
	}

	/**
	 * Select tool and show its info.
	 * 
	 * @param mode
	 *            The mode to select and display info from.
	 */
	public void setMode(int mode) {
		reset();
		Element btn = DOM.getElementById("mode" + mode);
		if (btn != null) {
			btn.setAttribute("selected", "true");
			showToolTip(mode);
		}
	}

	/**
	 * @return first mode; to be selected once this submenu is opened
	 */
	public abstract int getFirstMode();

}
