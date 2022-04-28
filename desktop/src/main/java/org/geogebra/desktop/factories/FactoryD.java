package org.geogebra.desktop.factories;

import org.geogebra.common.factories.Factory;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.desktop.javax.swing.RelationPaneD;

public class FactoryD extends Factory {

	@Override
	public RelationPane newRelationPane(String subTitle) {
		return new RelationPaneD();
	}

}
