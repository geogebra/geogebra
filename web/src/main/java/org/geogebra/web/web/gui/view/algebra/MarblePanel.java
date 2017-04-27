package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author Zbynek
 */
public class MarblePanel extends FlowPanel {
	private Marble marble;
	private boolean selected = false;
	/** warning triangle / help button */
	private ToggleButton btnHelpToggle;
	ToggleButton btnPlus;
	/** av item */
	RadioTreeItem item;
	ContextMenuPlus cmPlus=null;

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
		
		if (item.app.has(Feature.AV_PLUS) && item.getAV().isInputActive()) {
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
	 * @param selected
	 *            whether this should be highlighted
	 */
	public void setHighlighted(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Update marble visibility and highlighting
	 */
	public void update() {
		marble.setEnabled(shouldShowMarble());

		marble.setChecked(item.geo != null && item.geo.isEuclidianVisible());

		setHighlighted(selected);
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
	public boolean isHit(int x, int y) {
		return x > getAbsoluteLeft()
				&& x < getAbsoluteLeft() + getOffsetWidth()
				&& y < getAbsoluteTop() + getOffsetHeight();
	}

	/**
	 * @param warning
	 *            whether warning triangle should be visible
	 */
	public void updateIcons(boolean warning) {	
		ToggleButton btn = null;
		String img = GuiResources.INSTANCE.icon_help().getSafeUri().asString();
		if (item.app.has(Feature.AV_PLUS) && item.isInputTreeItem() ){
			initPlus();
			img = GuiResources.INSTANCE.algebra_new().getSafeUri().asString();
			btn = btnPlus;
		} else {
			initHelpToggle();
			btn = btnHelpToggle;
		}

		if (warning) {
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
		btn.getUpFace().setImage(new NoDragImage(
				warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning().getSafeUri()
						.asString()	: img,24));
		// new
		// Image(AppResources.INSTANCE.inputhelp_left_20x20().getSafeUri().asString()),
		btn.getDownFace().setImage(new NoDragImage(
				warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning().getSafeUri()
						.asString() : img,
				24));

	}


	private void initHelpToggle() {
		if (btnHelpToggle == null) {
			btnHelpToggle = new ToggleButton();
			ClickStartHandler.init(btnHelpToggle,
					new ClickStartHandler(true, true) {

						@Override
						public void onClickStart(int x, int y,
								PointerEventType type) {
							item.preventBlur();
							item.requestFocus();
							if (item.showCurrentError()) {

								return;
							}
							// getBtnHelpToggle()
							// .setDown(!getBtnHelpToggle().isDown());
							if (getBtnHelpToggle().isDown()) {
								item.app.hideKeyboard();
								Scheduler.get().scheduleDeferred(
										new Scheduler.ScheduledCommand() {
											@Override
											public void execute() {
												item.setShowInputHelpPanel(
														true);
												((InputBarHelpPanelW) item.app
														.getGuiManager()
														.getInputHelpPanel())
																.focusCommand(
																		item.getCommand());
											}

										});
							} else {
								item.setShowInputHelpPanel(false);
							}
						}
					});

			// when clicked, this steals focus
			// => we need to push focus to parent item
			btnHelpToggle.addFocusHandler(new FocusHandler() {

				public void onFocus(FocusEvent event) {
					item.setFocus(true, true);
					event.preventDefault();
					event.stopPropagation();

				}
			});
		}
	}
	
	public void initPlus() {
		if (btnPlus == null) {
			btnPlus = new ToggleButton();
			add(btnPlus);
			btnPlus.addStyleName("avPlusButton");
			ClickStartHandler.init(btnPlus, new ClickStartHandler() {
				
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					if (cmPlus == null) {
						cmPlus = new ContextMenuPlus(item.app);
					}
					cmPlus.show(btnPlus.getAbsoluteLeft(), btnPlus.getAbsoluteTop());
				}
				
			});
		}
		
		btnPlus.getUpFace().setImage(new NoDragImage(
				GuiResources.INSTANCE.algebra_new().getSafeUri()
							.asString(),
			24));

		NoDragImage hoverImg = new NoDragImage(
				GuiResources.INSTANCE.algebra_new_hover().getSafeUri()
				.asString(), 24);
		
		btnPlus.getUpHoveringFace().setImage(hoverImg);

			}
 
	/**
	 * @return help button
	 */
	public ToggleButton getBtnHelpToggle() {
		return btnHelpToggle;
	}

}