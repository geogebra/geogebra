package org.geogebra.web.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResourcesSimple extends ClientBundle {

	GuiResourcesSimple INSTANCE = GWT.create(GuiResourcesSimple.class);

	@Source("org/geogebra/web/resources/js/zipjs/deflate.js")
	TextResource deflateJs();

	@Source("org/geogebra/web/resources/images/nav_play.png")
	ImageResource navPlay();

	@Source("org/geogebra/web/resources/images/nav_pause.png")
	ImageResource navPause();

	@Source("org/geogebra/web/resources/js/mathquillggb.js")
	TextResource mathquillggbJs();

	@Source("org/geogebra/web/pub/js/properties_keys_en.js")
	TextResource propertiesKeysJS();

	@Source("org/geogebra/web/resources/js/jquery-1.7.2.min.js")
	TextResource jQueryJs();

	@Source("org/geogebra/web/resources/js/zipjs/inflate.js")
	TextResource inflateJs();

	@Source("org/geogebra/web/resources/js/zipjs/base64.js")
	TextResource base64Js();

	@Source("org/geogebra/web/resources/js/zipjs/arraybuffer.js")
	TextResource arrayBufferJs();

	@Source("org/geogebra/web/resources/js/gif.js")
	TextResource gifJs();

	@Source("org/geogebra/web/resources/js/zipjs/dataview.js")
	TextResource dataViewJs();

	@Source("org/geogebra/web/resources/js/zipjs/zip-3.js")
	TextResource zipJs();

	@Source("org/geogebra/web/resources/js/visibility.js")
	TextResource visibilityJs();

	@Source("org/geogebra/web/resources/js/jquery-ui.js")
	TextResource jqueryUI();

	@Source("org/geogebra/web/resources/js/WebMIDIAPIWrapper.js")
	TextResource webMidiAPIWrapperJs();

	@Source("org/geogebra/web/resources/js/midi/jasmid.js")
	TextResource jasmidJs();

	@Source("org/geogebra/web/resources/css/mathquillggb.css")
	TextResource mathquillggbCss();

	@Source("org/geogebra/web/resources/css/web-styles.css")
	TextResource modernStyle();

	@Source("org/geogebra/web/resources/css/jquery-ui.css")
	TextResource jqueryStyle();

	@Source("org/geogebra/web/resources/css/keyboard-styles.css")
	TextResource keyboardStyle();

	@Source("org/geogebra/common/icons/png/view_refresh.png")
	ImageResource viewRefresh();

	@Source("org/geogebra/web/resources/images/spinner.html")
	TextResource ggbSpinnerHtml();

	@Source("org/geogebra/web/resources/images/ggbSplash.html")
	TextResource ggbSplashHtml();

	// INFO, WARNING, QUESTION, ERROR
	@Source("org/geogebra/common/icons/png/web/dialog-error.png")
	ImageResource dialog_error();

	@Source("org/geogebra/common/icons/png/web/dialog-info.png")
	ImageResource dialog_info();

	@Source("org/geogebra/common/icons/png/web/dialog-question.png")
	ImageResource dialog_question();

	@Source("org/geogebra/common/icons/png/web/dialog-warning.png")
	ImageResource dialog_warning();

	@Source("org/geogebra/common/icons/png/web/question-mark.png")
	ImageResource questionMark();

	// TODO we need another picture
	@Source("org/geogebra/common/icons/png/android/document_viewer.png")
	ImageResource viewSaved();

	@Source("org/geogebra/web/resources/js/realsenseinfo-2.0.js")
	TextResource realsenseinfoJs();

	@Source("org/geogebra/web/resources/js/realsense-2.0.js")
	TextResource realsenseJs();

	@Source("org/geogebra/web/resources/js/promise-1.0.0.min.js")
	TextResource promiseJs();
}
