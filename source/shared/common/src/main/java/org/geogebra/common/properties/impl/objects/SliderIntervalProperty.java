package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * A slider interval property (collection). Contains min, max and step properties, operating on a
 * list of slider {@code GeoElement}s.
 */
public class SliderIntervalProperty extends AbstractPropertyCollection<StringProperty> {

	/**
	 * Creates a slider interval property collection. Contains min, max and step properties.
	 * @param propertiesFactory factory
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements geo elements
	 * @throws NotApplicablePropertyException if this property is not applicable to any of the
	 * geo elements
	 */
	public SliderIntervalProperty(GeoElementPropertiesFactory propertiesFactory,
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Interval");

		setProperties(new StringProperty[]{
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new MinProperty(processor, localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new MaxProperty(processor, localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new AnimationStepProperty(processor, localization,
								element, true),
						StringPropertyListFacade::new),
		});
	}

	public StringProperty getMinProperty() {
		return getProperties()[0];
	}

	public StringProperty getMaxProperty() {
		return getProperties()[1];
	}

	public StringProperty getStepProperty() {
		return getProperties()[2];
	}
}
