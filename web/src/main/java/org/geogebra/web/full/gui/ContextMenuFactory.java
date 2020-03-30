package org.geogebra.web.full.gui;

import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.main.App;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.main.AppW;

/**
 * Factory to create popup menus.
 * @author laszlo
 */
public class ContextMenuFactory {

	/**
	 * @return list box, to be mocked
	 */
	public GPopupMenuW newPopupMenu(AppW app) {return new GPopupMenuW(app);}

	/**
	 *
	 * @param inlines the drawable texts.
	 * @param app the application.
	 * @return toolbar for texts, sub/superscript, list styles.
	 */
	public InlineTextToolbar newInlineTextToolbar(List<DrawInlineText> inlines, App app) {
		return new InlineTextToolbar(inlines, app);
	}
}
