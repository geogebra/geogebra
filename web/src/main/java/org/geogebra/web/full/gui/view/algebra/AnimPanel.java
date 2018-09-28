package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Animation panel for points and sliders
 *
 */
public class AnimPanel extends FlowPanel implements ClickHandler {

	/** Size of play button in pixels */
	public static final int PLAY_BUTTON_SIZE = 24;

	/**
	 * Animation speeds
	 */
	final static double[] ANIM_SPEEDS = { 0.05, 0.1, 0.15, 0.2, 0.35, 0.75, 1,
			1.5, 2, 3.5, 4, 5, 6, 7, 10, 15, 20 };
	private final RadioTreeItem radioTreeItem;
	private MyToggleButtonW btnSpeedDown;
	private PushButton btnSpeedValue;
	private MyToggleButtonW btnSpeedUp;
	private MyToggleButtonW btnPlay;
	private boolean speedButtons = false;
	private boolean play = false;
	private int speedIndex = 6;
	private boolean playOnly;
	private FlowPanel speedPanel;
	private AnimPanelListener listener = null;
	private Label lblSpeedValue;
	private List<CustomButton> buttons = new ArrayList<>();

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
	 */
	public AnimPanel(RadioTreeItem radioTreeItem) {
		this(radioTreeItem, null);
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
		addStyleName("elemRow");
		playOnly = true;
		
		if (playOnly) {
			buildPlayOnly();
		} else {
			buildPlayWithSpeedButtons();
		}
	}
	
	private void buildPlayOnly() {
		createPlayButton();
		btnPlay.addStyleName("playOnly");
		buildSpeedPanel();
		add(speedPanel);
		add(btnPlay);
		ClickStartHandler.initDefaults(btnPlay, true, true);

	}

	private void buildPlayWithSpeedButtons() {
		btnSpeedDown = new MyToggleButtonW(
				GuiResources.INSTANCE.icons_play_rewind());
		btnSpeedDown.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.icons_play_rewind_hover()));

		btnSpeedDown.setStyleName("avSpeedButton");
		btnSpeedDown.addStyleName("slideIn");

		btnSpeedUp = new MyToggleButtonW(
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

	private void buildSpeedPanel() {
		speedPanel = new FlowPanel();
		speedPanel.addStyleName("speedPanel-hidden");
		btnSpeedDown = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.speed_down_black(), PLAY_BUTTON_SIZE));

		btnSpeedDown.setStyleName("flatButton");

		btnSpeedUp = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.speed_up_black(), PLAY_BUTTON_SIZE));

		btnSpeedUp.setStyleName("flatButton");

		btnSpeedDown.addClickHandler(this);
		btnSpeedUp.addClickHandler(this);
		lblSpeedValue = new Label("");
		lblSpeedValue.addStyleName("value");
		lblSpeedValue.addClickHandler(this);
		lblSpeedValue.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				event.stopPropagation();
			}
		});

		setSpeedText(this.radioTreeItem.geo.getAnimationSpeed());

		btnSpeedUp.setIgnoreTab();
		btnSpeedDown.setIgnoreTab();

		speedPanel.add(btnSpeedDown);
		speedPanel.add(lblSpeedValue);
		speedPanel.add(btnSpeedUp);
		showSpeedValue(false);
		ClickStartHandler.initDefaults(btnSpeedUp, false, true);
		ClickStartHandler.initDefaults(btnSpeedDown, false, true);
		ClickStartHandler.initDefaults(lblSpeedValue, true, true);
		buttons.add(btnSpeedUp);
		buttons.add(btnSpeedDown);
	}

	private void createPlayButton() {
		if (playOnly) {
			btnPlay = new MyToggleButtonW(
					new ImageResourcePrototype(null,
							SharedResources.INSTANCE.play_black()
									.getSafeUri(),
							0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE, false,
							false),
					new ImageResourcePrototype(null,
							SharedResources.INSTANCE.pause_black()
									.getSafeUri(),
							0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE, false, false));
			btnPlay.getUpHoveringFace()
					.setImage(new Image(
					new ImageResourcePrototype(null,
									SharedResources.INSTANCE.play_purple()
									.getSafeUri(),
							0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE, false, false)));
			btnPlay.getDownHoveringFace().setImage(
					new Image(new ImageResourcePrototype(null,
							SharedResources.INSTANCE.pause_purple()
									.getSafeUri(),
							0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE, false, false)));
			btnPlay.setIgnoreTab();
		} else {
			btnPlay = new MyToggleButtonW(
				GuiResourcesSimple.INSTANCE.icons_play_circle(),
				GuiResourcesSimple.INSTANCE.icons_play_pause_circle());
			btnPlay.getUpHoveringFace().setImage(
				new Image(GuiResourcesSimple.INSTANCE
						.icons_play_circle_hover()));
			btnPlay.getDownHoveringFace()
				.setImage(new Image(GuiResourcesSimple.INSTANCE
						.icons_play_pause_circle_hover()));
		}
		btnPlay.setStyleName("avPlayButton");

		ClickStartHandler.init(btnPlay, new ClickStartHandler() {
			@Override
			public boolean onClickStart(int x, int y, PointerEventType type,
					boolean right) {
				if (right) {
					return true;
				}
				getController().stopEdit();

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
		buttons.add(btnPlay);

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
		if (playOnly) {
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
			return;
		}
		
		showSpeedButtons(false);

		if (value) {
			showSpeedValue(true);
		} else {
			showSpeedValue(false);

		}
	}

	private void showSpeedValue(boolean value) {
		if (playOnly) {
			return;
		}
		
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
		if (playOnly) {
			return;
		}
		
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
		double speed = ANIM_SPEEDS[this.speedIndex];
		this.radioTreeItem.geo.setAnimationSpeed(speed);
		setSpeedText(speed);
	}

	private void setSpeedText(double speed) {
		String speedStr = speed + " " + Unicode.MULTIPLY;
		if (playOnly) {
			lblSpeedValue.setText(speedStr);
			return;
		}

		btnSpeedValue.getUpFace().setText(speedStr);
		btnSpeedValue.getUpHoveringFace().setText(speedStr);
		btnSpeedValue.getDownFace().setText(speedStr);
		btnSpeedValue.getDownHoveringFace().setText(speedStr);
		btnSpeedValue.setText(speedStr);
	}

	@Override
	public void onClick(ClickEvent event) {
		radioTreeItem.getController().stopEdit();
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			return;
		}
		Object source = event.getSource();
		if (source == btnSpeedDown) {
			speedDown();
			// this.radioTreeItem.selectItem(true);
		} else if (source == btnSpeedUp) {
			speedUp();
			// this.radioTreeItem.selectItem(true);
		} else if (!playOnly && source == btnSpeedValue) {
			showSpeedButtons(!speedButtons);
		} else if (source == lblSpeedValue) {
			event.stopPropagation();
		}
	}

	private void speedUp() {
		if (this.speedIndex < ANIM_SPEEDS.length - 1) {
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
		return this.radioTreeItem.geo.isAnimating()
				&& this.radioTreeItem.kernel.getAnimatonManager().isRunning();
	}

	/**
	 * Reset UI
	 */
	public void reset() {
		if (playOnly) {
			// speedPanel.addStyleName("speedPanel-hidden");
			// speedPanel.removeStyleName("speedPanel");
			return;
		}
		showSpeedButtons(false);
		showSpeedValue(isGeoAnimating());
	}
	
	/**
	 * @return play button
	 */
	MyToggleButtonW getPlayButton() {
		return btnPlay;
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
}