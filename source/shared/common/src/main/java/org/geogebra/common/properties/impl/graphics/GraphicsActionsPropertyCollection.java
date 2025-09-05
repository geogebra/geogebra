package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.aliases.ActionableIconPropertyCollection;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class GraphicsActionsPropertyCollection
		extends AbstractPropertyCollection<ActionableIconProperty>
		implements ActionableIconPropertyCollection {

	/**
	 * Constructs a GraphicsActionsPropertyCollection.
	 * @param app app
	 * @param localization localization
	 */
	public GraphicsActionsPropertyCollection(App app, Localization localization,
			EuclidianViewInterfaceCommon view) {
		super(localization, "");
		ArrayList<ActionableIconProperty> properties = new ArrayList<>();
		properties.add(new StandardViewAction(localization, view));
		properties.add(new ShowAllObjectsAction(localization, app.getConfig(), view));
		setProperties(properties.toArray(new ActionableIconProperty[0]));
	}
}
