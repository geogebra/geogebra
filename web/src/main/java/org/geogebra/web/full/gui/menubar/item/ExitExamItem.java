package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.view.algebra.MenuItem;

/**
 * Exit exam action
 */
public class ExitExamItem extends MenuItem<Void> {

	public ExitExamItem() {
		super("exam_menu_exit",
				MaterialDesignResources.INSTANCE.signout_black(),
				new ExitExamAction());
	}
}
