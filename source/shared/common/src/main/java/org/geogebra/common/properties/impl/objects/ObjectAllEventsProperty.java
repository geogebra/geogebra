package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractProperty;

public class ObjectAllEventsProperty extends AbstractProperty {
	private final ArrayList<ObjectEventProperty> props;

	/**
	 * @param loc localization
	 * @param props properties to wrap
	 */
	public ObjectAllEventsProperty(Localization loc, ArrayList<ObjectEventProperty> props) {
		super(loc, "");
		this.props = props;
	}

	public ArrayList<ObjectEventProperty> getProps() {
		return props;
	}
}
