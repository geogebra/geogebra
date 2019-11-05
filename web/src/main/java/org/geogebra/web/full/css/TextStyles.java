package org.geogebra.web.full.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.geogebra.web.resources.SassResource;

/**
 * Styles related to text.
 */
public interface TextStyles extends ClientBundle {
	TextStyles INSTANCE = GWT.create(TextStyles.class);

	@Source("org/geogebra/web/resources/scss/text-styles.scss")
	SassResource textStyles();
}
