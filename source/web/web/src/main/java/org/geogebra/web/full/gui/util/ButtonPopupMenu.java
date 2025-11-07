package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.FocusPanel;
import org.gwtproject.user.client.ui.Panel;

public class ButtonPopupMenu extends GPopupPanel implements HasKeyboardPopup {
	
	private final FocusPanel container;
	private final FlowPanel panel;

	/**
	 * @param root
	 *            root for popup
	 * @param app
	 *            application
	 */
	public ButtonPopupMenu(Panel root, App app) {
		super(root, app);
		container = new FocusPanel();
		panel = new FlowPanel();
		container.add(panel);
		container.addStyleName("ButtonPopupMenu");
		container.addKeyUpHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
				hide();
			}
		});
		add(container);
	}
	
	public FlowPanel getPanel() {
		return panel;
	}

	public FocusPanel getFocusPanel() {
		return container;
	}

}
