package org.geogebra.desktop.factories;

import org.geogebra.common.factories.Factory;
import org.geogebra.common.javax.swing.RelationPane;

public class FactoryD extends Factory {

	@Override
	public RelationPane newRelationPane() {
		return new org.geogebra.desktop.javax.swing.RelationPaneD();
	}

}
