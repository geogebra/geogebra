package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
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
	private MyToggleButton btnHelpToggle;
	/** plus button (new expression / text, ...) */
	MyToggleButton btnPlus;
	/** av item */
	RadioTreeItem item;
	/** plus menu */
	ContextMenuAVPlus cmPlus = null;

	/** No PLUS menu */
	boolean noPlus = false;
	
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
		MyToggleButton btn = null;
		String tooltip = "";
		boolean textInput = item.getController().isInputAsText();
		noPlus = !StringUtil.emptyTrim(item.getText());
		if (textInput && !item.isInputTreeItem()
				&& item.getController().isEditing()) {
			addStyleName("text");
		} else {
			removeStyleName("text");
		}

		String img = GuiResources.INSTANCE.icon_help().getSafeUri().asString();
		if (item.isInputTreeItem()) {
			initPlus();
			img = StringUtil.emptyTrim(item.getText())
					? 
					MaterialDesignResources.INSTANCE.add_black().getSafeUri()
							.asString()
				: AppResources.INSTANCE.empty().getSafeUri().asString();
			btn = btnPlus;
			if (warning) {
				tooltip = "";
			} else {
				tooltip = noPlus ? " "
						: item.app.getLocalization().getMenu("AddItem");
			}
		} else {
			initHelpToggle();
			btn = btnHelpToggle;
			tooltip = warning ? ""
					: item.app.getLocalization().getMenu("InputHelp");
		}
		
		btn.setTitle(tooltip);
		btn.setAltText(tooltip);
		if (warning && !textInput) {
			remove(marble);
			add(btn);
			addStyleName("error");
			removeStyleName("help");
		}
		else if (item.getController().isEditing() || item.geo == null) {
			remove(marble);
			add(btn);
			removeStyleName("error");
			addStyleName("error");

		} else {
			add(marble);
			marble.setEnabled(shouldShowMarble());
			remove(btn);
			removeStyleName("error");
		}
		AriaHelper.setLabel(marble,
				item.loc.getMenu("ShowHideObject"));
		if (!textInput) {
			if (warning) {
				NoDragImage warnIcon = new NoDragImage(
						GuiResourcesSimple.INSTANCE.icon_dialog_warning().getSafeUri()
						.asString());
				
				btn.getUpFace().setImage(warnIcon);
				btn.getUpHoveringFace().setImage(warnIcon);
				btn.getDownFace().setImage(warnIcon);
				btn.getDownHoveringFace().setImage(warnIcon);
					
			} else {
				NoDragImage ndi = new NoDragImage(img, 24);
				btn.getUpFace().setImage(ndi);
				btn.getDownFace().setImage(ndi);
				
				if (btn == btnPlus && !noPlus) {
					NoDragImage hover = new NoDragImage(
							MaterialDesignResources.INSTANCE.add_purple()
									.getSafeUri().asString(),
							24);
					btn.getUpHoveringFace().setImage(hover);
					btn.getDownHoveringFace().setImage(hover);
				} else {
					btn.getUpHoveringFace().setImage(ndi);
					btn.getDownHoveringFace().setImage(ndi);
					
				}
			}
		}
	}

	private void initHelpToggle() {
		if (btnHelpToggle == null) {
			btnHelpToggle = new MyToggleButton(item.app);
			ClickStartHandler.init(btnHelpToggle,
					new ClickStartHandler(true, true) {

						@Override
						public void onClickStart(int x, int y,
								PointerEventType type) {

							if (checkError(item)) {
								return;
							}

							getBtnHelpToggle()
									.setDown(!getBtnHelpToggle().isDown());

							if (getBtnHelpToggle().isDown()) {
								item.app.hideKeyboard();
								item.clearPreviewAndSuggestions();
								showDeferred(item);
							} else {
								item.setShowInputHelpPanel(false);
							}
						}
					});

			// when clicked, this steals focus
			// => we need to push focus to parent item
			btnHelpToggle.addFocusHandler(new FocusHandler() {

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
			btnPlus.setTitle(item.app.getLocalization().getMenu("AddItem"));
			add(btnPlus);
			if (item.app.isUnbundled()) {
				btnPlus.addStyleName("flatButton");
			}
			
			ClickStartHandler.init(btnPlus, new ClickStartHandler(true, true) {
				
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					onPlusPressed();
				}
			});
		}
		
		btnPlus.getUpFace().setImage(new NoDragImage(
				MaterialDesignResources.INSTANCE.add_black().getSafeUri()
							.asString(),
			24));

		NoDragImage hoverImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.add_purple().getSafeUri()
				.asString(), 24);
		
		btnPlus.getUpHoveringFace().setImage(hoverImg);
		btnPlus.getDownHoveringFace().setImage(hoverImg);
		btnPlus.setIgnoreTab();
		btnPlus.addKeyDownHandler(this);
		btnPlus.setAltText(btnPlus.getTitle());
		AriaHelper.setHidden(btnPlus, true);
	}

	/**
	 * @return help button
	 */
	@Override
	public MyToggleButton getBtnHelpToggle() {
		return btnHelpToggle;
	}

	@Override
	public void setLabels() {
		if (cmPlus != null) {
			cmPlus.setLabels();
		}
	}

	/**
	 * @return the Plus menu button.
	 */
	@Override
	public MyToggleButton getBtnPlus() {
		return btnPlus;
	}

	/** Plus button handler */
	void onPlusPressed() {
		if (noPlus) {
			return;
		}

		if (cmPlus == null) {
			cmPlus = new ContextMenuAVPlus(item);
		}
		item.cancelEditing();
		cmPlus.show(btnPlus.getAbsoluteLeft() + 16,
				btnPlus.getAbsoluteTop() + 6);
	}

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title, item.app);
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
}
