package org.geogebra.web.full.html5;

import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.main.App;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.ContextMenuFactory;
import org.geogebra.web.html5.main.AppW;

public class MenuFactory extends ContextMenuFactory {
	public MenuFactory(AppW app) {
		super();
	}

	@Override
	public GPopupMenuW newPopupMenu(AppW app) {
		return new GPopupMenuWMock(app);
	}

	@Override
	public InlineTextToolbar newInlineTextToolbar(List<DrawInlineText> inlines, App app) {
		return new InlineTextToolbarMock(inlines, app);
	}
}
