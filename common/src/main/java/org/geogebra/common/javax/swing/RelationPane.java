package org.geogebra.common.javax.swing;

import org.geogebra.common.gui.util.RelationMore;
import org.geogebra.common.main.App;

/**
 * Common interface for the Relation Tool information window.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public interface RelationPane {

	/**
	 * One row in the information window.
	 * 
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public class RelationRow {
		/**
		 * The information shown in the left column.
		 */
		public String info;
		/**
		 * The action to be started when the user clicks on "More...".
		 */
		public RelationMore callback;
	}

	/**
	 * Shows the information window.
	 * 
	 * @param title
	 *            The title of the window
	 * @param relations
	 *            The pieces of information to be shown
	 * @param app
	 *            GeoGebra Application
	 */
	public abstract void showDialog(String title, RelationRow[] relations,
			App app);

	/**
	 * Updates a row containing information and probably a button.
	 * 
	 * @param row
	 *            The row to be updated
	 * @param relation
	 *            The new relation in the row
	 */
	public abstract void updateRow(int row, RelationRow relation);

}
