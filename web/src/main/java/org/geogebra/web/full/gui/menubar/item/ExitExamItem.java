package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exit exam item
 */
public class ExitExamItem extends MenuItem<AppWFull> {

	/**
	 * Creates a new ExitExam menu item.
	 */
	public ExitExamItem() {
		super("exam_menu_exit",
				MaterialDesignResources.INSTANCE.signout_black(),
				new ExitExamAction());
	}
}
