package geogebra.html5.factories;

import geogebra.common.factories.Factory;
import geogebra.common.javax.swing.RelationPane;

public class FactoryW extends Factory {

	@Override
	public RelationPane newRelationPane() {
		return new geogebra.html5.javax.swing.RelationPaneW();
	}

}
