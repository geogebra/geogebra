package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.util.MyToggleButton2;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Animation panel for points and sliders
 *
 */
public class AnimPanel extends FlowPanel implements ClickHandler {
	/**
	 * Animation speeds
	 */
	final static double animSpeeds[] = { 0.05, 0.1, 0.15, 0.2, 0.35, 0.75, 1,
			1.5, 2, 3.5, 4, 5, 6, 7, 10, 15, 20 };
	private final RadioTreeItem radioTreeItem;
	private MyToggleButton2 btnSpeedDown;
	private PushButton btnSpeedValue;
	private MyToggleButton2 btnSpeedUp;
	private MyToggleButton2 btnPlay;
	private boolean speedButtons = false;
	private boolean play = false;
	private int speedIndex = 6;

	/**
	 * @param radioTreeItem
	 *            parent item
	 */
	public AnimPanel(RadioTreeItem radioTreeItem) {
		super();
		this.radioTreeItem = radioTreeItem;
		addStyleName("elemRow");

		btnSpeedDown = new MyToggleButton2(
				GuiResources.INSTANCE.icons_play_rewind());
		btnSpeedDown.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.icons_play_rewind_hover()));

		btnSpeedDown.setStyleName("avSpeedButton");
		btnSpeedDown.addStyleName("slideIn");

		btnSpeedUp = new MyToggleButton2(
				GuiResources.INSTANCE.icons_play_fastforward());
		btnSpeedUp.getUpHoveringFace().setImage(new Image(
				GuiResources.INSTANCE.icons_play_fastforward_hover()));

		btnSpeedUp.setStyleName("avSpeedButton");
		btnSpeedUp.addStyleName("slideIn");
		// btnSpeedUp.removeStyleName("MyToggleButton");

		btnSpeedDown.addClickHandler(this);
		btnSpeedUp.addClickHandler(this);
		btnSpeedValue = new PushButton("");
		btnSpeedValue.addStyleName("speedValue");
		btnSpeedValue.addStyleName("slideIn");
		btnSpeedValue.addClickHandler(this);
		setSpeedText(this.radioTreeItem.geo.getAnimationSpeed());
		createPlayButton();
		add(btnSpeedDown);
		add(btnSpeedValue);
		add(btnSpeedUp);
		add(btnPlay);
		showSpeedValue(false);
	}

	private void doStyle() {
		btnSpeedDown.setStyleName("avSpeedButton");
		btnSpeedDown.addStyleName("slideIn");

		btnSpeedUp.setStyleName("avSpeedButton");
		btnSpeedUp.addStyleName("slideIn");

		btnSpeedValue.addStyleName("speedValue");
		btnSpeedValue.addStyleName("slideIn");

	}

	private void createPlayButton() {
		btnPlay = new MyToggleButton2(
				GuiResourcesSimple.INSTANCE.icons_play_circle(),
				GuiResourcesSimple.INSTANCE.icons_play_pause_circle());
		btnPlay.getUpHoveringFace().setImage(
				new Image(GuiResourcesSimple.INSTANCE
						.icons_play_circle_hover()));
		btnPlay.getDownHoveringFace()
				.setImage(new Image(GuiResourcesSimple.INSTANCE
						.icons_play_pause_circle_hover()));
		btnPlay.setStyleName("avPlayButton");

		ClickStartHandler.init(btnPlay, new ClickStartHandler() {
			@Override
			public boolean onClickStart(int x, int y, PointerEventType type,
					boolean right) {
				if (right) {
					return true;
				}

				boolean value = !isGeoAnimating();

				getGeo().setAnimating(value);
				setPlay(value);
				getGeo().updateRepaint();

				AnimPanel.this.setAnimating(
						getGeo().isAnimating());
				return true;
			}

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onClickStart(x, y, type, false);
			}
		});

	}

	/**
	 * @return geo element
	 */
	protected GeoElement getGeo() {
		return this.radioTreeItem.geo;
	}

	/**
	 * Set aniating flag of underlying geo
	 * 
	 * @param value
	 *            whether animation is on
	 */
	void setAnimating(boolean value) {
		if (!(getGeo().isAnimatable())) {
			return;
		}
		getGeo().setAnimating(value);
		getGeo().getKernel().getAnimatonManager().startAnimation();
	}

	/**
	 * @param value
	 *            whether animation is playing
	 */
	void setPlay(boolean value) {
		play = value;
		showSpeedButtons(false);

		if (value) {
			showSpeedValue(true);
		} else {
			showSpeedValue(false);

		}
	}

	private void showSpeedValue(boolean value) {
		setSpeedText(this.radioTreeItem.geo.getAnimationSpeed());
		if (value) {
			btnSpeedValue.removeStyleName("hidden");
		} else {
			btnSpeedValue.addStyleName("hidden");
			showSpeedButtons(false);
		}
	}

	/**
	 * @param value
	 *            whether play buttons should be visible
	 */
	public void showPlay(boolean value) {
		btnPlay.setVisible(value);
	}

	private void showSpeedButtons(boolean value) {
		if (value) {
			setSpeedText(this.radioTreeItem.geo.getAnimationSpeed());
			btnSpeedUp.removeStyleName("hidden");
			btnSpeedDown.removeStyleName("hidden");
		} else {
			btnSpeedUp.addStyleName("hidden");
			btnSpeedDown.addStyleName("hidden");
		}
		speedButtons = value;
	}

	private void setSpeed() {
		double speed = animSpeeds[this.speedIndex];
		this.radioTreeItem.geo.setAnimationSpeed(speed);
		setSpeedText(speed);
	}

	private void setSpeedText(double speed) {
		String speedStr = speed + " " + Unicode.MULTIPLY;
		btnSpeedValue.getUpFace().setText(speedStr);
		btnSpeedValue.getUpHoveringFace().setText(speedStr);
		btnSpeedValue.getDownFace().setText(speedStr);
		btnSpeedValue.getDownHoveringFace().setText(speedStr);
		btnSpeedValue.setText(speedStr);
	}

	public void onClick(ClickEvent event) {
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			return;
		}
		Object source = event.getSource();
		if (source == btnSpeedDown) {
			speedDown();
			this.radioTreeItem.selectItem(true);
		} else if (source == btnSpeedUp) {
			speedUp();
			this.radioTreeItem.selectItem(true);
		} else if (source == btnSpeedValue) {
			showSpeedButtons(!speedButtons);
		}
	}

	private void speedUp() {
		if (this.speedIndex < animSpeeds.length - 1) {
			this.speedIndex++;
			setSpeed();
		}
	}

	private void speedDown() {
		if (this.speedIndex > 0) {
			this.speedIndex--;
			setSpeed();

		}
	}

	/**
	 * Update UI
	 */
	public void update() {
		boolean visible = this.radioTreeItem.geo != null
				&& this.radioTreeItem.geo.isAnimatable();
		if (isGeoAnimating() != play || !isVisible()) {
			boolean v = isGeoAnimating();
			setPlay(v);
			btnPlay.setDown(v);
		}
		setVisible(visible);
	}

	/**
	 * @return whether geo is animating
	 */
	boolean isGeoAnimating() {
		return this.radioTreeItem.geo.isAnimating() && this.radioTreeItem.kernel.getAnimatonManager().isRunning();
	}

	/**
	 * Reset UI
	 */
	public void reset() {
		showSpeedButtons(false);
		showSpeedValue(isGeoAnimating());
	}
}