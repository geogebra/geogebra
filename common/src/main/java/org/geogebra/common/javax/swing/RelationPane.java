package org.geogebra.common.javax.swing;

import org.geogebra.common.gui.util.RelationMore;
import org.geogebra.common.main.App;

/**
 * Common interface for the Relation Tool information window.
 * 
 * @author Zoltan Kovacs
 *
 */
public interface RelationPane {

	/**
	 * One row in the information window.
	 * 
	 * @author Zoltan Kovacs
	 *
	 */
	public class RelationRow {

		private String info;
		private RelationMore callback;

		/**
		 * @return The information shown in the left column.
		 */
		public String getInfo() {
			return info;
		}

		/**
		 * @param info
		 *            The information shown in the left column.
		 */
		public void setInfo(String info) {
			this.info = info;
		}

		/**
		 * @return The action to be started when the user clicks on "More...".
		 */
		public RelationMore getCallback() {
			return callback;
		}

		/**
		 * @param callback
		 *            The action to be started when the user clicks on
		 *            "More...".
		 */
		public void setCallback(RelationMore callback) {
			this.callback = callback;
		}
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
