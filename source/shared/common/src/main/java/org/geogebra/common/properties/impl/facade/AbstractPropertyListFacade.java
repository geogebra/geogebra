package org.geogebra.common.properties.impl.facade;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.properties.Property;

/**
 * Base class for properties that hold a list of properties of type <code>P</code>
 * and act as a property of that type.
 * @param <P> hosted property type
 */
public abstract class AbstractPropertyListFacade<P extends Property> implements Property {

	protected final List<P> properties;

	protected AbstractPropertyListFacade(List<P> properties) {
		if (properties.isEmpty()) {
			throw new IllegalArgumentException("Properties must have at least a single property");
		}
		this.properties = properties;
	}

	@Override
	public String getName() {
		return getFirstProperty().getName();
	}

	@Override
	public @Nonnull String getRawName() {
		return getFirstProperty().getRawName();
	}

	@Override
	public boolean isEnabled() {
		boolean isEnabled = true;
		for (Property property : properties) {
			isEnabled = isEnabled && property.isEnabled();
		}
		return isEnabled;
	}

	@Override
	public boolean isFrozen() {
		return getFirstProperty().isFrozen();
	}

	@Override
	public void setFrozen(boolean frozen) {
		// ignore
	}

	@Override
	public boolean isAvailable() {
		return getFirstProperty().isAvailable();
	}

	public P getFirstProperty() {
		return properties.get(0);
	}

	public List<P> getPropertyList() {
		return properties;
	}
}
