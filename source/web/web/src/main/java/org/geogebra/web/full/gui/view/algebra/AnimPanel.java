package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.util.DataTest;
import org.geogebra.web.html5.util.HasDataTest;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Animation panel for points and sliders
 *
 */
public class AnimPanel extends FlowPanel implements ClickHandler, HasDataTest {

	/** Size of play button in pixels */
	public static final int PLAY_BUTTON_SIZE = 24;

	/**
	 * Animation speeds
	 */
	final static double[] ANIM_SPEEDS = { 0.05, 0.1, 0.15, 0.2, 0.35, 0.75, 1,
			1.5, 2, 3.5, 4, 5, 6, 7, 10, 15, 20 };
	private final RadioTreeItem radioTreeItem;
	private StandardButton btnSpeedDown;
	private StandardButton btnSpeedUp;
	private ToggleButton btnPlay;
	private boolean play = false;
	private double speed = 1; // currently displayed speed
	private FlowPanel speedPanel;
	private final AnimPanelListener listener;
	private Label lblSpeedValue;

	/**
	 * Callback for play button
	 */
	public interface AnimPanelListener {

		/**
		 * Run this when animation was started / stopped
		 *
		 * @param play
		 *            whether animation was started
		 */
		void onPlay(boolean play);
	}

	/**
	 * @param radioTreeItem
	 *            parent item
	 * @param listener
	 *            listener
	 */
	public AnimPanel(RadioTreeItem radioTreeItem, AnimPanelListener listener) {
		super();
		this.radioTreeItem = radioTreeItem;
		this.listener = listener;

		buildGui();
	}

	private void buildGui() {
		createPlayButton();
		btnPlay.addStyleName("playOnly");
		buildSpeedPanel();
		add(speedPanel);
		add(btnPlay);
		ClickStartHandler.initDefaults(btnPlay, false, true);
	}

	private void buildSpeedPanel() {
		speedPanel = new FlowPanel();
		speedPanel.addStyleName("speedPanel-hidden");
		btnSpeedDown = new StandardButton(
				MaterialDesignResources.INSTANCE.speed_down_black(), PLAY_BUTTON_SIZE);
		btnSpeedDown.setStyleName("flatButton");

		btnSpeedUp = new StandardButton(
				MaterialDesignResources.INSTANCE.speed_up_black(), PLAY_BUTTON_SIZE);
		btnSpeedUp.setStyleName("flatButton");

		btnSpeedDown.addFastClickHandler((e) -> {
			radioTreeItem.getController().stopEdit();
			speedDown();
		});
		btnSpeedUp.addFastClickHandler((e) -> {
			radioTreeItem.getController().stopEdit();
			speedUp();
		});
		lblSpeedValue = new Label("");
		lblSpeedValue.addStyleName("value");
		lblSpeedValue.addClickHandler(this);
		lblSpeedValue.addMouseUpHandler(DomEvent::stopPropagation);

		setSpeedText(this.radioTreeItem.geo.getAnimationSpeed());

		btnSpeedUp.setTabIndex(-1);
		btnSpeedDown.setTabIndex(-1);

		speedPanel.add(btnSpeedDown);
		speedPanel.add(lblSpeedValue);
		speedPanel.add(btnSpeedUp);
		ClickStartHandler.initDefaults(btnSpeedUp, false, true);
		ClickStartHandler.initDefaults(btnSpeedDown, false, true);
		ClickStartHandler.initDefaults(lblSpeedValue, true, true);
	}

	private void createPlayButton() {
		btnPlay = new ToggleButton(GuiResourcesSimple.INSTANCE.play_circle(),
				GuiResourcesSimple.INSTANCE.pause_circle());
		btnPlay.setTabIndex(-1);
		btnPlay.setStyleName("avPlayButton");
		btnPlay.addFastClickHandler((event) -> {
				getController().stopEdit();

				boolean value = !isGeoAnimating();

				getGeo().setAnimating(value);
				setPlay(value);
				getGeo().updateRepaint();

				setAnimating(getGeo().isAnimating());
		});
	}

	/** @return tree item controller */
	protected RadioTreeItemController getController() {
		return radioTreeItem.getController();
	}

	/**
	 * @return geo element
	 */
	protected GeoElement getGeo() {
		return this.radioTreeItem.geo;
	}

	/**
	 * Set animating flag of underlying geo
	 *
	 * @param value
	 *            whether animation is on
	 */
	void setAnimating(boolean value) {
		if (!(getGeo().isAnimatable())) {
			return;
		}
		getGeo().setAnimating(value);
		getGeo().getKernel().getAnimationManager().startAnimation();
	}

	/**
	 * @param value
	 *            whether animation is playing
	 */
	void setPlay(boolean value) {
		play = value;
		if (play) {
			speedPanel.addStyleName("speedPanel");
			speedPanel.removeStyleName("speedPanel-hidden");
		} else {
			speedPanel.addStyleName("speedPanel-hidden");
			speedPanel.removeStyleName("speedPanel");
		}

		if (listener != null) {
			listener.onPlay(play);
		}
	}

	/**
	 * @param value
	 *            whether play buttons should be visible
	 */
	public void showPlay(boolean value) {
		btnPlay.setVisible(value);
	}

	private void setSpeed(double newSpeed) {
		speed = newSpeed;
		getGeo().setAnimationSpeed(speed);
		getGeo().getKernel().notifyUpdateVisualStyle(getGeo(), GProperty.COMBINED);
		setSpeedText(speed);
	}

	private void setSpeedText(double speed) {
		String speedStr = speed + " " + Unicode.MULTIPLY;
		lblSpeedValue.setText(speedStr);
	}

	@Override
	public void onClick(ClickEvent event) {
		radioTreeItem.getController().stopEdit();
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			return;
		}
		Object source = event.getSource();
		if (source == lblSpeedValue) {
			event.stopPropagation();
		}
	}

	private void speedUp() {
		for (double animSpeed : ANIM_SPEEDS) {
			if (animSpeed > speed) {
				setSpeed(animSpeed);
				return;
			}
		}
	}

	private void speedDown() {
		for (int speedIndex = ANIM_SPEEDS.length - 1; speedIndex >= 0; speedIndex--) {
			if (ANIM_SPEEDS[speedIndex] < speed) {
				setSpeed(ANIM_SPEEDS[speedIndex]);
				return;
			}
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
			btnPlay.setSelected(v);
		}
		if (visible && getGeo().getAnimationSpeed() != speed) {
			speed = getGeo().getAnimationSpeed();
			setSpeedText(speed);
		}
		setVisible(visible);
	}

	/**
	 * @return whether geo is animating
	 */
	boolean isGeoAnimating() {
		return this.radioTreeItem.geo.isAnimating()
				&& this.radioTreeItem.kernel.getAnimationManager().isRunning();
	}

	/**
	 * Update alt text according to localization
	 *
	 * @param loc
	 *            localization
	 */
	public void setLabels(Localization loc) {
		AriaHelper.setLabel(btnPlay, loc.getMenu("Play"));
	}

	@Override
	public void updateDataTest(int index) {
		DataTest.ALGEBRA_ITEM_PLAY_BUTTON.applyWithIndex(btnPlay, index);
	}
}