package org.geogebra.web.html5.multiuser;

import java.util.HashMap;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.main.App;

public final class MultiuserManager {

	public static final MultiuserManager INSTANCE = new MultiuserManager();

	private final HashMap<String, User> activeInteractions = new HashMap<>();

	private MultiuserManager() {
		// singleton class
	}

	/**
	 * Add a multiuser interaction coming from another user
	 * @param app application
	 * @param user user that changed this object
	 * @param color color associated with the user
	 * @param label label of the changed object
	 * @param newGeo if the geo was added
	 */
	public void addSelection(App app, String user, GColor color, String label, boolean newGeo) {
		User currentUser = activeInteractions
				.computeIfAbsent(user, k -> new User(user, color));
		for (User u : activeInteractions.values()) {
			if (u != currentUser) {
				u.removeSelection(label);
			}
		}
		currentUser.addSelection(app.getActiveEuclidianView(), label, newGeo);
	}

	/**
	 * Deselect objects associated with given user
	 * @param app application
	 * @param user user ID
	 */
	public void deselect(App app, String user) {
		User currentUser = activeInteractions.get(user);
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
}
