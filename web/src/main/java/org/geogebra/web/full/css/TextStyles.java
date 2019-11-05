package org.geogebra.web.full.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.geogebra.web.resources.SassResource;

/**
 * Styles related to text.
 */
public interface TextStyles extends ClientBundle {
	TextStyles INSTANCE = GWT.create(TextStyles.class);

	/** Headline 6 style name */
	String HEADLINE_6 = "headline6";

	/** Subtitle 1 style name */
	String SUBTITLE_1 = "subtitle1";

	/** Subtitle 1 styled link style name */
	String SUBTITLE_1_LINK = "subtitle1-link";

	@Source("org/geogebra/web/resources/scss/text-styles.scss")
	SassResource textStyles();
}
