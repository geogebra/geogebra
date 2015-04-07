package org.geogebra.common.gui.util;

import org.geogebra.common.javax.swing.RelationPane;

/**
 * An object for storing a GeoGebra command (Prove or ProveDetails) and a check
 * (AreParallel, ArePerpendicular etc.). It is used in the new Relation Tool.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public interface RelationMore {

	/**
	 * Run the action (when clicking the "More..." button)
	 * 
	 * @param table
	 *            the Relation Tool window
	 * @param row
	 *            the row to be updated after the action is finished
	 */
	public void action(RelationPane table, int row);
}
