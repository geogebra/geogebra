package org.geogebra.web.web.gui.menubar;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.user.client.ui.MenuItem;

public interface MenuBarI {

	void hide();

	public MenuItem addItem(@IsSafeHtml String text, boolean asHTML,
			ScheduledCommand cmd);

}
