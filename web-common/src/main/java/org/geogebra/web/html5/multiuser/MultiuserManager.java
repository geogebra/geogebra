package org.geogebra.web.html5.multiuser;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;

public final class MultiuserManager implements EventListener {

	public static final MultiuserManager INSTANCE = new MultiuserManager();

	private final HashMap<String, User> activeInteractions = new HashMap<>();

	private MultiuserManager() {
		// singleton class
	}

	/**
	 * Add a multiuser interaction coming from another user
	 * @param app application
	 * @param clientId id of the client that changed this object
	 * @param user name of the user that changed this object
	 * @param color color associated with the user
	 * @param label label of the changed object
	 * @param implicit whether the geo was interacted with (add, update) without explicit selection
	 */
	public void addSelection(App app, String clientId, String user, GColor color, String label,
			boolean implicit) {
		GColor withAlpha = GColor.newColor(adjustColor(color.getRed()),
				adjustColor(color.getGreen()), adjustColor(color.getBlue()), 127);
		User currentUser = activeInteractions
				.computeIfAbsent(clientId, k -> new User(user, withAlpha));
		app.getEventDispatcher().removeEventListener(this);
		app.getEventDispatcher().addEventListener(this);
		// TODO this removeSelection is not propagated to other users. Markers get
		// inconsistent, if two users select the same object.
		for (Map.Entry<String, User> entry : activeInteractions.entrySet()) {
			if (!entry.getKey().equals(clientId)) {
				entry.getValue().removeSelection(label);
			}
		}
		if (implicit) {
			currentUser.addInteraction(app.getActiveEuclidianView(), label);
		} else {
			currentUser.addSelection(app.getActiveEuclidianView(), label);
		}
	}

	private int adjustColor(int component) {
		return Math.max(2 * component - 255, 0);
	}

	/**
	 * Deselect objects associated with given user
	 * @param app application
	 * @param clientId client ID
	 */
	public void deselect(App app, String clientId) {
		User currentUser = activeInteractions.get(clientId);
		if (currentUser != null) {
			currentUser.deselectAll(app.getActiveEuclidianView());
		}
	}

	/**
	 * Paint the boxes showing which objects were recently changed by
	 * other users. Also updates the tooltips.
	 * @param view euclidian view
	 * @param graphics canvas to paint on
	 */
	public void paintInteractionBoxes(EuclidianView view, GGraphics2D graphics) {
		graphics.setStroke(AwtFactory.getPrototype()
				.newBasicStroke(6, GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND));
		for (User user : activeInteractions.values()) {
			user.paintInteractionBoxes(view, graphics);
		}
	}

	/**
	 * @param view view
	 * @param graphics canvas to paint on
	 */
	public void paintInteractionBackgrounds(EuclidianView view, GGraphics2D graphics) {
		for (User user : activeInteractions.values()) {
			user.paintInteractionBackgrounds(view, graphics);
		}
	}

	@Override
	public void sendEvent(Event evt) {
		if (evt.type == EventType.RENAME) {
			for (User user : activeInteractions.values()) {
				user.rename(evt.target);
			}
		} else if (evt.type == EventType.REMOVE) {
			for (User user : activeInteractions.values()) {
				user.removeSelection(evt.target.getLabelSimple());
			}
		}
	}
}
