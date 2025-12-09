/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
