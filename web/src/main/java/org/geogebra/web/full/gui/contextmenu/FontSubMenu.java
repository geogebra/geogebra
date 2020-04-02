package org.geogebra.web.full.gui.contextmenu;

import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.web.html5.gui.laf.FontFamily;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * Submenu for Font item in 3-dot menu of inline text
 *
 * @author Laszlo
 *
 */
public class FontSubMenu extends AriaMenuBar {

	private final List<FontFamily> fonts;
	private List<DrawInlineText> inlines;
	private final AppW app;
	private AriaMenuItem highlighted;

	/**
	 * @param app the application
	 * @param inlines to format text.
	 */
	public FontSubMenu(AppW app, List<DrawInlineText> inlines) {
		this.app = app;
		this.fonts = app.getVendorSettings().getTextToolFonts();
		this.inlines = inlines;
		createItems();
	}

	private void createItems() {
		for (final FontFamily font : fonts) {
			ScheduledCommand command = new ScheduledCommand() {
				@Override
				public void execute() {
					setFontName(font.cssName());
					app.storeUndoInfo();
				}
			};

			AriaMenuItem item = new AriaMenuItem(font.displayName(), false, command);
			addItem(item);
		}
	}

	private void setFontName(String cssName) {
		for (DrawInlineText drawInlineText: inlines) {
			drawInlineText.getTextController().format("font", cssName);
		}
	}

	@Override
	public void stylePopup(Widget widget) {
		highlightCurrent();
	}

	private void highlightCurrent() {
		if (inlines.isEmpty()) {
			return;
		}

		String font = inlines.get(0).getFormat("font", "");
		for (FontFamily family : fonts) {
			if (font.equals(family.cssName())) {
				highlightItem(fonts.indexOf(family));
				return;
			}
		}
		unselect();
	}

	private void highlightItem(int index) {
		if (highlighted != null) {
			highlighted.removeStyleName("highlighted");
		}

		if (index < 0) {
			return;
		}

		highlighted = getItemAt(index);
		highlighted.addStyleName("highlighted");
	}
}
