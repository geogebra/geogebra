package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.Factory;
import org.geogebra.common.javax.swing.RelationPane;

public class FactoryW extends Factory {

	@Override
	public RelationPane newRelationPane() {
		return new org.geogebra.web.html5.javax.swing.RelationPaneW();
	}

}
