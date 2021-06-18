package org.geogebra.common.javax.swing;

import org.geogebra.common.kernel.Relation;
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
		private Relation callback;

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
		public Relation getCallback() {
			return callback;
		}

		/**
		 * @param callback
		 *            The action to be started when the user clicks on
		 *            "More...".
		 */
		public void setCallback(Relation callback) {
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
}
