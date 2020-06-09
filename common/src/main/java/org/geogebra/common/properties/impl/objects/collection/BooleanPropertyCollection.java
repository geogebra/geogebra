package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.BooleanProperty;

/**
 * Handles a collection of BooleanProperty objects as a single BooleanProperty.
 */
public class BooleanPropertyCollection
		extends AbstractPropertyCollection<BooleanProperty, Boolean> implements BooleanProperty {

	/**
	 * @param propertyCollection properties to handle
	 */
	public BooleanPropertyCollection(Collection<? extends BooleanProperty> propertyCollection) {
		super(propertyCollection.toArray(new BooleanProperty[0]));
	}

	@Override
	public boolean getValue() {
		return getFirstProperty().getValue();
	}

	@Override
	protected void setPropertyValue(BooleanProperty property, Boolean value) {
		property.setValue(value);
	}

	@Override
	public void setValue(boolean value) {
		setProperties(value);
	}
}
