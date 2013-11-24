package geogebra.common.gui.util;

import geogebra.common.javax.swing.RelationPane;
import geogebra.common.kernel.geos.GeoElement;

/**
 * An object for storing a GeoGebra command (Prove or ProveDetails)
 * and a check (AreParallel, ArePerpendicular etc.).
 * It is used in the new Relation Tool.
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public interface RelationMore {
	
	/**
	 * the command to run (e.g. Prove)
	 * FIXME: Create an interface for the two commands instead.
	 */
	String command = null;
	/**
	 * the check to be done (e.g. AreParallel)
	 * FIXME: Create an interface for the checks instead.
	 */
	String check = null;
	/**
	 * This should be an array of GeoElements here, FIXME.
	 * They are the inputs for the check.
	 */
	GeoElement a = null;
	GeoElement b = null;
	
	/**
	 * Run the action (when clicking the "More..." button)
	 * @param table the Relation Tool window
	 * @param row the row to be updated after the action is finished
	 */
	public void action(RelationPane table, int row);
}
