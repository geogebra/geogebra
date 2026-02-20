/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import static org.geogebra.common.main.settings.AlgebraStyle.DEFINITION;
import static org.geogebra.common.main.settings.AlgebraStyle.DEFINITION_AND_VALUE;
import static org.geogebra.common.main.settings.AlgebraStyle.LINEAR_NOTATION;

import java.util.function.Supplier;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.util.IndexTextBuilder;
import org.gwtproject.user.client.ui.UIObject;

/**
 * Supplies accessible content text and role descriptions for an algebra view item.
 *
 * <p>This class provides two accessibility resources based on a geo element
 * and its {@link AlgebraStyle}:
 * <ul>
 *   <li>a supplier of localized content text suitable for screen readers</li>
 *   <li>a localized role description that describes the type of content</li>
 * </ul>
 * The content supplier returns either a combined definition/value representation
 * or a single representation, depending on the algebra style.</p>
 */
public class AccessibleContentProvider {
	private final GeoElement geo;
	private final Localization loc;
	private final AlgebraStyle style;

	/**
	 * Creates a provider for accessible content based on the given geo and algebra style.
	 *
	 * @param geo the geo element for which accessibility content is generated
	 * @param style the algebra style determining the type of representation
	 */
	public AccessibleContentProvider(GeoElement geo, AlgebraStyle style) {
		this.geo = geo;
		loc = geo.getApp().getLocalization();
		this.style = style;
	}

	/**
	 * Returns a supplier of accessible text for the associated geo element.
	 *
	 * @return a supplier providing localized, unescaped content text
	 */
	public Supplier<String> getContentSupplier() {
		if (DEFINITION_AND_VALUE.equals(style) || LINEAR_NOTATION.equals(style)) {
			return this::getOutputWithLabel;
		}
		return this::getInput;
	}

	private String getOutputWithLabel() {
		return geo.getNameAndDefinition(StringTemplate.defaultTemplate);
	}

	private String getInput() {
		return buildDefinition();
	}

	private String buildDefinition() {
		IndexTextBuilder builder = new IndexTextBuilder();
		AlgebraItem.buildPlainTextItemSimple(geo, builder,
				style, StringTemplate.defaultTemplate);
		return builder.toString();
	}

	/**
	 * Returns a localized accessibility role description based on the current algebra style.
	 *
	 * <p>This description is suitable for use with
	 * {@link org.geogebra.web.html5.gui.util.AriaHelper#setRoleDescription(UIObject, String)}
	 * to provide assistive technologies with a meaningful semantic label.</p>
	 *
	 * @return the localized role description string for the current style
	 */
	public String getRoleDescription() {
		String roleDescription = switch (style) {
			case UNDEFINED -> "undefined";
			case DEFINITION, VALUE, DESCRIPTION -> style.getTranslationKey();
			case DEFINITION_AND_VALUE, LINEAR_NOTATION -> DEFINITION.getTranslationKey();
		};
		return loc.getMenu(roleDescription);
	}
}
