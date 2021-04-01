package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
	private MyToggleButtonW btnSpeedUp;
	private MyToggleButtonW btnPlay;
	private boolean play = false;
	private int speedIndex = 6;
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
		addStyleName("elemRow");

		buildGui();
	}
	
	private void buildGui() {
		createPlayButton();
		btnPlay.addStyleName("playOnly");
		buildSpeedPanel();
		add(speedPanel);
		add(btnPlay);
		ClickStartHandler.initDefaults(btnPlay, true, true);
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
		lblSpeedValue.addMouseUpHandler(DomEvent::stopPropagation);

		setSpeedText(this.radioTreeItem.geo.getAnimationSpeed());

		btnSpeedUp.setIgnoreTab();
		btnSpeedDown.setIgnoreTab();

		speedPanel.add(btnSpeedDown);
		speedPanel.add(lblSpeedValue);
		speedPanel.add(btnSpeedUp);
		ClickStartHandler.initDefaults(btnSpeedUp, false, true);
		ClickStartHandler.initDefaults(btnSpeedDown, false, true);
		ClickStartHandler.initDefaults(lblSpeedValue, true, true);
	}

	private void createPlayButton() {
		String hoverColor = GeoGebraColorConstants.GEOGEBRA_ACCENT.toString();
		SVGResource play = GuiResourcesSimple.INSTANCE.play_circle();
		SVGResource pause = GuiResourcesSimple.INSTANCE.pause_circle();

		btnPlay = new MyToggleButtonW(
				new Image(play.getSafeUri(), 0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE),
				new Image(pause.getSafeUri(), 0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE)
		);

		btnPlay.getUpHoveringFace().setImage(new Image(play.withFill(hoverColor).getSafeUri(),
				0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE));
		btnPlay.getDownHoveringFace().setImage(new Image(pause.withFill(hoverColor).getSafeUri(),
				0, 0, PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE));

		btnPlay.setIgnoreTab();
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
		getGeo().getKernel().getAnimatonManager().startAnimation();
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

	private void setSpeed() {
		double speed = ANIM_SPEEDS[this.speedIndex];
		this.radioTreeItem.geo.setAnimationSpeed(speed);
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
		if (source == btnSpeedDown) {
			speedDown();
		} else if (source == btnSpeedUp) {
			speedUp();
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
	 * Update alt text according to localization
	 * 
	 * @param loc
	 *            localization
	 */
	public void setLabels(Localization loc) {
		AriaHelper.setLabel(btnPlay, loc.getMenu("Play"));
	}
}