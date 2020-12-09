package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Toolbar for mow
 * 
 * @author csilla
 *
 */
public class ToolbarMow extends FlowPanel
		implements  SetLabels {
	private final AppW appW;
	private final HeaderMow header;
	private FlowPanel toolbarPanel;
	private FlowPanel toolbarPanelContent;

	private boolean isOpen = true;

	private PenSubMenu penPanel;
	private ToolsSubMenu toolsPanel;
	private MediaSubMenu mediaPanel;
	private TabIds currentTab;
	private final NotesLayout notesLayout;

	/**
	 * Tab ids.
	 */
	enum TabIds {
		/** tab one */
		PEN,

		/** tab two */
		TOOLS,

		/** tab three */
		MEDIA
	}

	/**
	 * constructor
	 * 
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolbarMow(AppW app, NotesLayout layout) {
		this.appW = app;
		header = new HeaderMow(this, appW);
		notesLayout = layout;
		add(header);
		initGui();
	}

	/**
	 * @return true if toolbar is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            true if toolbar is open
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	private void initGui() {
		addStyleName("toolbarMow");
		toolbarPanel = new FlowPanel();
		toolbarPanel.addStyleName("toolbarMowPanel");
		add(toolbarPanel);

		createPanels();
		// setMode(appW.getMode());
		setLabels();
	}

	private void createPanels() {
		toolbarPanelContent = new FlowPanel();
		toolbarPanelContent.addStyleName("mowSubmenuScrollPanel");
		toolbarPanelContent.addStyleName("slideLeft");
		currentTab = TabIds.PEN;
		penPanel = new PenSubMenu(appW);
		toolsPanel = new ToolsSubMenu(appW);
		mediaPanel = new MediaSubMenu(appW);
		toolbarPanelContent.add(penPanel);
		toolbarPanelContent.add(toolsPanel);
		toolbarPanelContent.add(mediaPanel);
		toolbarPanel.add(toolbarPanelContent);
		updateAriaHidden();
	}

	/**
	 * rebuild media tools panel
	 */
	public void updateMediaPanel() {
		mediaPanel.clear();
		mediaPanel.createContentPanel();
	}

	/**
	 * @param tab
	 *            id of tab
	 */
	public void tabSwitch(TabIds tab) {
		if (tab != currentTab) {
			currentTab = tab;
			toolbarPanelContent.removeStyleName("slideLeft");
			toolbarPanelContent.removeStyleName("slideCenter");
			toolbarPanelContent.removeStyleName("slideRight");
			switch (tab) {
			case PEN:
				toolbarPanelContent.addStyleName("slideLeft");
				break;
			case TOOLS:
				toolbarPanelContent.addStyleName("slideCenter");
				break;
			case MEDIA:
				toolbarPanelContent.addStyleName("slideRight");
				break;
			default:
				toolbarPanelContent.addStyleName("slideLeft");
				break;
			}
			appW.setMode(getCurrentPanel().getFirstMode());
			updateAriaHidden();
		}
	}

	private void updateAriaHidden() {
		penPanel.setAriaHidden(currentTab != TabIds.PEN);
		toolsPanel.setAriaHidden(currentTab != TabIds.TOOLS);
		mediaPanel.setAriaHidden(currentTab != TabIds.MEDIA);
	}

	@Override
	public void setLabels() {
		header.setLabels();
		penPanel.setLabels();
		toolsPanel.setLabels();
		mediaPanel.setLabels();
	}

	private SubMenuPanel getCurrentPanel() {
		switch (currentTab) {
		case PEN:
			return penPanel;
		case TOOLS:
			return toolsPanel;
		case MEDIA:
			return mediaPanel;
		default:
			return penPanel;
		}
	}

	/**
	 * @param mode
	 *            id of tool
	 */
	public void setMode(int mode) {
		if (((AppWFull) appW).getZoomPanelMow() != null
				&& mode != EuclidianConstants.MODE_TRANSLATEVIEW) {
			((AppWFull) appW).getZoomPanelMow().getDragPadBtn()
					.removeStyleName("selected");
		}
		getCurrentPanel().setMode(mode);
	}

	/**
	 * Toggle toolbar visibility
	 */
	public void onOpenClose() {
		notesLayout.deselectDragButton();
		setStyleName(
				isOpen() ? "hideMowToolbarPanel"
						: "showMowToolbarPanel");

		setOpen(!isOpen());
		addStyleName("toolbarMow");
		notesLayout.updateFloatingButtonsPosition();
	}
}
