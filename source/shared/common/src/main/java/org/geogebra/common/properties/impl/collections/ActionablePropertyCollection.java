package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionableProperty;

public class ActionablePropertyCollection<T extends ActionableProperty> extends
		AbstractPropertyCollection<ActionableProperty> {
	/**
	 * Constructs an ActionablePropertyCollection.
	 * @param localization localization
	 * @param properties list of {@link ActionableProperty} properties
	 */
	public ActionablePropertyCollection(Localization localization,
			List<T> properties) {
		super(localization, "");
		setProperties(properties.toArray(new ActionableProperty[0]));
	}

	@Override
	public T[] getProperties() {
		return (T[]) super.getProperties();
	}
}
