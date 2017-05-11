package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * Item action bar
 *
 */
public class ItemControls extends FlowPanel {
	/**
	 * 
	 */
	final RadioTreeItem radioTreeItem;


	/** Deletes the whole item */
	protected PushButton btnDelete;

	/** opens context menu */
	protected ToggleButton btnMore;


	/** animation controls */
	protected AnimPanel animPanel = null;

	private ContextMenuMore cmMore = null;


	private boolean playOnly;
	
	/**
	 * @param radioTreeItem
	 *            parent item
	 */
	public ItemControls(RadioTreeItem radioTreeItem) {
		this.radioTreeItem = radioTreeItem;
		playOnly = radioTreeItem.app.has(Feature.AV_PLAY_ONLY);
		addStyleName("AlgebraViewObjectStylebar");
		addStyleName("smallStylebar");
		buildGUI();
		if (hasMoreMenu()) {
			add(getMoreButton());
		}
	}

	/**
	 * Gets (and creates if there is not yet) the delete button which geo
	 * item can be removed with from AV.
	 * 
	 * @return The "X" button.
	 */
	public PushButton getDeleteButton() {
		if (btnDelete == null) {
			btnDelete = new PushButton(
					new Image(GuiResources.INSTANCE.algebra_delete()));
			btnDelete.getUpHoveringFace().setImage(new Image(
					GuiResources.INSTANCE.algebra_delete_hover()));
			btnDelete.addStyleName("XButton");
			btnDelete.addStyleName("shown");
			btnDelete.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					if (event
							.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
						return;
					}
					event.stopPropagation();
					getController().removeGeo();
				}
			});
		}
		return btnDelete;

	}

	/**
	 * 
	 * @return The more button which opens the context menu.
	 */
	public ToggleButton getMoreButton() {
		if (btnMore == null) {
			btnMore = new ToggleButton(
					new Image(GuiResources.INSTANCE.more_vert_black()));
			btnMore.getUpHoveringFace().setImage(new Image(
					GuiResources.INSTANCE.more_vert_purple()));
			btnMore.addStyleName("XButton");
			btnMore.addStyleName("shown");
			ClickStartHandler.init(btnMore, new ClickStartHandler() {
			
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					showMoreContexMenu();
				}
				
			});
		}
		return btnMore;

	}

	/**
	 * Show the More context menu
	 */
	protected void showMoreContexMenu() {
		if (cmMore == null) {
			cmMore = new ContextMenuMore(radioTreeItem);
		}
		radioTreeItem.cancelEditing();
		cmMore.show(btnMore.getAbsoluteLeft(), btnMore.getAbsoluteTop() - 8);

	}

	/**
	 * @param value
	 *            whether to show animation panel
	 */
	public void showAnimPanel(boolean value) {
		if (hasAnimPanel()) {
			animPanel.setVisible(value);
		}
	}

	private void buildGUI() {
		radioTreeItem.setFirst(radioTreeItem.first);
		clear();
		buildAnimPanel();
		

		if (!hasMoreMenu()) {
			add(getDeleteButton());
		}
	}
	
	private void buildAnimPanel() {
		if (radioTreeItem.geo.isAnimatable()) {
			if (animPanel == null) {
				createAnimPanel();
			}
			
			add(animPanel);
			reset();
			updateAnimPanel();
			showAnimPanel(true);
		} else {
			showAnimPanel(false);
		}
		
	}
	private boolean hasMoreMenu() {
		return radioTreeItem.app.has(Feature.AV_MORE_MENU);
	}

	/**
	 * 
	 */
	protected void createAnimPanel() {
		animPanel = radioTreeItem.geo.isAnimatable()
				? new AnimPanel(radioTreeItem)
				: null;

	}

	/**
	 * Update animation panel
	 */
	public void updateAnimPanel() {
		if (hasAnimPanel()) {
			animPanel.update();
		}
	}

	/**
	 * @return animation panel
	 */
	public AnimPanel getAnimPanel() {
		return animPanel;
	}

	/**
	 * @param showX
	 *            whether to show x button
	 * @return whether this is shown (when single geo selected)
	 */
	public boolean update(boolean showX) {
		radioTreeItem.setFirst(radioTreeItem.first);

		if (radioTreeItem.geo == null) {
			return false;
		}
		boolean ret = false;
		if (getController().selectionCtrl.isSingleGeo()
				|| getController().selectionCtrl.isEmpty()) {
			radioTreeItem.setFirst(radioTreeItem.first);
			clear();
			if (radioTreeItem.geo.isAnimatable()) {
				if (animPanel == null) {
					createAnimPanel();
				}

				add(animPanel);
			}

			if (radioTreeItem.getPButton() != null) {
				add(radioTreeItem.getPButton());
			}
			if (showX) {
				add(hasMoreMenu() ? getMoreButton(): getDeleteButton());
			}

			setVisible(true);

			if (!getController().isEditing()) {
				radioTreeItem.maybeSetPButtonVisibility(false);
			}

			radioTreeItem.getAV().setActiveTreeItem(radioTreeItem);
			ret = true;
		} else {
			radioTreeItem.getAV().removeCloseButton();
		}

		updateAnimPanel();
		return ret;
	}

	/**
	 * Remove animation panel
	 */
	public void removeAnimPanel() {
		if (hasAnimPanel()) {
			remove(animPanel);
		}
	}

	/**
	 * Reset animation panel
	 */
	public void reset() {
		if (hasAnimPanel()) {
			animPanel.reset();
		}
	}

	/**
	 * @return whether animation panel exists
	 */
	public boolean hasAnimPanel() {
		return animPanel != null;
	}

	@Override
	public void setVisible(boolean b) {
		if (getController().isEditing()) {
			return;
		}
		super.setVisible(b);
	}

	/**
	 * Called when item selected, shows the x button in edit mode
	 * 
	 * @param value
	 *            whether this is the only selected item
	 */
	public void show(boolean value) {
		if (radioTreeItem.app.has(Feature.AV_MORE_MENU)) {
			super.setVisible(true);
			return;
		}
			if (!radioTreeItem.app.has(Feature.AV_SINGLE_TAP_EDIT)) {
			return;
		}

		boolean b = value || getController().isEditing();

		if (value && isVisible()) {
			return;
		}

		setVisible(b);

		if (value) {
			buildGUI();
			Log.debug("Control repositionig. width: " + getOffsetWidth());
		}
	}

	/**
	 * Update position
	 */
	public void reposition() {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				// ScrollPanel algebraPanel = ((AlgebraDockPanelW)
				// radioTreeItem.app
				// .getGuiManager().getLayout().getDockManager()
				// .getPanel(App.VIEW_ALGEBRA)).getAbsolutePanel();
				// int scrollPos = algebraPanel.getHorizontalScrollPosition();

				// extra margin if vertical scrollbar is visible.
				// int sw = Browser.isTabletBrowser() ? 0
				// : RadioTreeItem.BROWSER_SCROLLBAR_WIDTH;
				// int margin = radioTreeItem.getAV().getOffsetHeight()
				// + getOffsetHeight() > algebraPanel.getOffsetHeight()
				// ? sw : 0;

				int right = 0;
				int itemWidth = radioTreeItem.getOffsetWidth();
				int avWidth = radioTreeItem.getAV().getOffsetWidth();
				if (avWidth < itemWidth) {
					right = itemWidth - avWidth;
				}
				getElement().getStyle().setRight(right, Unit.PX);

			}
		});
	}
	


	/**
	 * @return controller
	 */
	RadioTreeItemController getController() {
		return this.radioTreeItem.getController();
	}
}