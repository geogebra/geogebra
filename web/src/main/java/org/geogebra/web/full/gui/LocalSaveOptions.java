package org.geogebra.web.full.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.util.MimeType;
import org.geogebra.common.util.StringUtil;

import elemental2.core.JsArray;
import jsinterop.base.JsPropertyMap;

/**
 * Class to build the proper options object for FileSystemAPI save dialog.
 * It gives a default file name and sets the extension filter depending on the app.
 *
 */
public class LocalSaveOptions {

	private final App app;
	private final MimeType mimeType;

	/**
	 *
	 * @param app {@link App}
	 */
	public LocalSaveOptions(App app) {
		this.app = app;
		mimeType = MimeType.forApplication(app);
	}

	/**
	 *
	 * @return the save options as a property map.
	 */
	public JsPropertyMap<Object> asPropertyMap() {
		JsPropertyMap<Object> propertyMap = JsPropertyMap.of();
		propertyMap.set("suggestedName", getSuggestedName());
		propertyMap.set("types", getAcceptedMimeTypes());
		// propertyMap.set("excludeAcceptAllOption", "true"); // it does not seem to work.
		return propertyMap;
	}

	private String getSuggestedName() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		return (StringUtil.empty(consTitle)
				? app.getLocalization().getMenu("Untitled")
				: consTitle
		) + mimeType.dotExtension();
	}

	private JsArray<Object> getAcceptedMimeTypes() {
		JsPropertyMap<String> mimeTypes = JsPropertyMap.of();
		mimeTypes.set(mimeType.type(), mimeType.dotExtension());
		JsPropertyMap<Object> types = JsPropertyMap.of();
		types.set("description", mimeType.getDescription(app.getLocalization()));
		types.set("accept", mimeTypes);
		return JsArray.of(types);
	}

}
