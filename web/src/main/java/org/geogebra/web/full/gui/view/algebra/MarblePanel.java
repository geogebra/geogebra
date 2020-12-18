package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Zbynek
 */
public class MarblePanel extends FlowPanel
		implements KeyDownHandler, AlgebraItemHeader {
	
	private Marble marble;
	/** warning triangle / help button */
	private MyToggleButton btnWarning;
	/** plus button (new expression / text, ...) */
	MyToggleButton btnPlus;
	/** av item */
	RadioTreeItem item;
	/** plus menu */
	ContextMenuAVPlus cmPlus = null;

	/**
	 * @param item
	 *            AV item
	 */
	public MarblePanel(RadioTreeItem item) {
		this.item = item;
		marble = new Marble(item);
		marble.setStyleName("marble");
		marble.setEnabled(shouldShowMarble());

		addStyleName("marblePanel");
		
		if (item.getAV().isInputActive() && item.getGeo() == null) {
			addStyleName("plus");
			initPlus();
			return;
		}

		if (item.getGeo() != null) {
			marble.setChecked(item.geo.isEuclidianVisible());
			add(marble);
		} else {
			updateIcons(false);
		}
		update();
	}

	/**
	 * Update marble visibility and highlighting
	 */
	@Override
	public void update() {
		marble.setEnabled(shouldShowMarble());
		marble.setChecked(item.geo != null && item.geo.isEuclidianVisible());
	}

	private boolean shouldShowMarble() {
		return item.geo != null && item.geo.isEuclidianShowable()
				&& (!item.getApplication().isExam()
						|| item.getApplication().enableGraphing());
	}

	/**
	 * @param x
	 *            pointer x-coord
	 * @param y
	 *            pointer y-coord
	 * @return whether pointer is over this
	 */
	@Override
	public boolean isHit(int x, int y) {
		return x > getAbsoluteLeft()
				&& x < getAbsoluteLeft() + getOffsetWidth()
				&& y < getAbsoluteTop() + getOffsetHeight();
	}

	/**
	 * @param warning
	 *            whether warning triangle should be visible
	 */
	@Override
	public void updateIcons(boolean warning) {
		boolean textInput = item.getController().isInputAsText();
		if (textInput && !item.isInputTreeItem()
				&& item.getController().isEditing()) {
			addStyleName("text");
		} else {
			removeStyleName("text");
		}

		clear();
		if (warning && !textInput) {
			initHelpToggle();
			add(btnWarning);
			addStyleName("error");
		} else if (item.geo == null) {
			initPlus();
			add(btnPlus);
			removeStyleName("error");
		} else {
			add(marble);
			marble.setEnabled(shouldShowMarble());
			removeStyleName("error");
		}
		AriaHelper.setLabel(marble,
				item.loc.getMenu("ShowHideObject"));
	}

	private void initHelpToggle() {
		if (btnWarning == null) {
			btnWarning = new MyToggleButton(item.app);
			NoDragImage warnIcon = new NoDragImage(
					GuiResourcesSimple.INSTANCE.icon_dialog_warning().getSafeUri()
							.asString());

			btnWarning.getUpFace().setImage(warnIcon);
			btnWarning.getUpHoveringFace().setImage(warnIcon);
			btnWarning.getDownFace().setImage(warnIcon);
			btnWarning.getDownHoveringFace().setImage(warnIcon);
			// when clicked, this steals focus
			// => we need to push focus to parent item
			btnWarning.addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					item.setFocus(true);
					event.preventDefault();
					event.stopPropagation();
				}
			});
		}
	}

	/**
	 * Create plus button if it doesn't exist, update the image
	 */
	public void initPlus() {
		if (btnPlus == null) {
			btnPlus = new MyToggleButton(item.app);
			NoDragImage plus = new NoDragImage(
					MaterialDesignResources.INSTANCE.add_black(), 24);
			NoDragImage hover = new NoDragImage(
					MaterialDesignResources.INSTANCE.add_purple(), 24);
			btnPlus.getUpHoveringFace().setImage(hover);
			btnPlus.getDownHoveringFace().setImage(hover);
			btnPlus.getUpFace().setImage(plus);
			btnPlus.getDownFace().setImage(plus);

			add(btnPlus);
			if (item.app.isUnbundled()) {
				btnPlus.addStyleName("flatButton");
			}
			
			ClickStartHandler.init(btnPlus, new ClickStartHandler(true, true) {
				
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					item.preventBlur();
					onPlusPressed();
				}
			});
		}
		String tooltip = item.app.getLocalization().getMenu("AddItem");
		btnPlus.setTitle(tooltip);
		btnPlus.setAltText(tooltip);
		btnPlus.addKeyDownHandler(this);
		AriaHelper.setHidden(btnPlus, true);
	}

	@Override
	public void setLabels() {
		if (cmPlus != null) {
			cmPlus.setLabels();
		}
	}

	/** Plus button handler */
	void onPlusPressed() {
		if (cmPlus == null) {
			cmPlus = new ContextMenuAVPlus(item);
		}
		item.cancelEditing();
		cmPlus.show(btnPlus.getAbsoluteLeft() + 16,
				btnPlus.getAbsoluteTop() + 6);
	}

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title);
	}

	/**
	 * Shows the help panel
	 * 
	 * @param item
	 *            item to show the help in
	 */
	public static void showDeferred(final RadioTreeItem item) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				item.setFocus(true);
				item.setShowInputHelpPanel(true);
				item.app.getGuiManager().getInputHelpPanel()
						.focusCommand(item.getCommand());
			}

		});
	}

	/**
	 * Check if item shows error.
	 * 
	 * @param item
	 *            to check
	 * 
	 * @return if there is an error or not.
	 */
	public static boolean checkError(final RadioTreeItem item) {
		item.preventBlur();
		item.requestFocus();
		return item.showCurrentError();
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		Object source = event.getSource();
		int key = event.getNativeKeyCode();
		if (source == btnPlus) {
			if (key == KeyCodes.KEY_ENTER || key == KeyCodes.KEY_SPACE) {
				onPlusPressed();
			}
		}
	}

	@Override
	public void setIndex(int itemCount) {
		// index not visible
	}

	@Override
	public void setError(String title) {
		if (btnWarning != null) {
			btnWarning.setAltText(title);
			btnWarning.setTitle(title);
		}
	}
}
