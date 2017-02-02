package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
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

public class ItemControls extends FlowPanel {
	/**
	 * 
	 */
	final RadioTreeItem radioTreeItem;


	/** Deletes the whole item */
	protected PushButton btnDelete;


	/** animation controls */
	protected AnimPanel animPanel;

	/**
	 * @param radioTreeItem
	 *            parent item
	 */
	public ItemControls(RadioTreeItem radioTreeItem) {
		this.radioTreeItem = radioTreeItem;
		addStyleName("AlgebraViewObjectStylebar");
		addStyleName("smallStylebar");
		setVisible(false);
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

	public void showAnimPanel(boolean value) {
		if (hasAnimPanel()) {
			animPanel.setVisible(value);
		}
	}

	/**
	 * Show animation panel
	 */
	public void showAnimPanel() {
		showAnimPanel(true);
	}

	public void buildGUI() {
		radioTreeItem.setFirst(radioTreeItem.first);
		clear();
		if (radioTreeItem.geo.isAnimatable()) {
			if (animPanel == null) {
				createAnimPanel();
			}

			add(animPanel);
			reset();
			updateAnimPanel();
			showAnimPanel(true);
		} else {
			hideAnimPanel();
		}

		add(getDeleteButton());

	}

	public void hideAnimPanel() {
		showAnimPanel(false);
	}

	protected void createAnimPanel() {
		animPanel = radioTreeItem.geo.isAnimatable()
				? new AnimPanel(radioTreeItem)
				: null;

	}

	public void updateAnimPanel() {
		if (hasAnimPanel()) {
			animPanel.update();
		}
	}

	public AnimPanel getAnimPanel() {
		return animPanel;
	}

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
				add(getDeleteButton());
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

	public void removeAnimPanel() {
		if (hasAnimPanel()) {
			remove(animPanel);
		}
	}

	public void reset() {
		if (hasAnimPanel()) {
			animPanel.reset();
		}
	}

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

	public void show(boolean value) {
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

	RadioTreeItemController getController() {
		return this.radioTreeItem.getController();
	}
}