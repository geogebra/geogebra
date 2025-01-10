package org.geogebra.common.factories;

import org.geogebra.common.javax.swing.RelationPane;

public abstract class Factory {
	private static final Object lock = new Object();
	private static volatile Factory prototype;

	public abstract RelationPane newRelationPane(String subTitle);

	/**
	 * @return might return null. Use App.getFactory()
	 */
	public static Factory getPrototype() {
		return prototype;
	}

	/**
	 * @param p prototype
	 */
	public static void setPrototype(Factory p) {
		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

}
