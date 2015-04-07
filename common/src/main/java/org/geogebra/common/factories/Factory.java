package org.geogebra.common.factories;

import org.geogebra.common.javax.swing.RelationPane;

public abstract class Factory {
	private static Factory prototype;

	public abstract RelationPane newRelationPane();

	/**
	 * @return might return null. Use App.getFactory()
	 */
	public static Factory getPrototype() {
		return prototype;
	}

	public static void setPrototype(Factory ret) {
		prototype = ret;
	}

}
