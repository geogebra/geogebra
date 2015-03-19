package geogebra.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResourcesSimple extends ClientBundle {

	GuiResourcesSimple INSTANCE = GWT.create(GuiResourcesSimple.class);

	@Source("geogebra/pub/js/zipjs/deflate.js")
	TextResource deflateJs();

	@Source("geogebra/resources/images/nav_play.png")
	ImageResource navPlay();

	@Source("geogebra/resources/images/nav_pause.png")
	ImageResource navPause();

	@Source("geogebra/resources/js/mathquillggb.js")
	TextResource mathquillggbJs();

	@Source("geogebra/pub/js/properties_keys_en.js")
	TextResource propertiesKeysJS();

	@Source("geogebra/resources/js/jquery-1.7.2.min.js")
	TextResource jQueryJs();

	@Source("geogebra/pub/js/zipjs/inflate.js")
	TextResource inflateJs();

	@Source("geogebra/resources/js/zipjs/base64.js")
	TextResource base64Js();

	@Source("geogebra/resources/js/zipjs/arraybuffer.js")
	TextResource arrayBufferJs();

	@Source("geogebra/resources/js/gif.js")
	TextResource gifJs();

	@Source("geogebra/resources/js/zipjs/dataview.js")
	TextResource dataViewJs();

	@Source("geogebra/resources/js/zipjs/zip-3.js")
	TextResource zipJs();

	@Source("geogebra/resources/js/visibility.js")
	TextResource visibilityJs();

	@Source("geogebra/resources/css/mathquillggb.css")
	TextResource mathquillggbCss();

	@Source("geogebra/resources/css/web-styles.css")
	TextResource modernStyle();

	@Source("icons/png/view_refresh.png")
	ImageResource viewRefresh();

	@Source("geogebra/resources/images/spinner.html")
	TextResource ggbSpinnerHtml();

	@Source("geogebra/resources/images/ggbSplash.html")
	TextResource ggbSplashHtml();

	// INFO, WARNING, QUESTION, ERROR
	@Source("icons/png/web/dialog-error.png")
	ImageResource dialog_error();

	@Source("icons/png/web/dialog-info.png")
	ImageResource dialog_info();

	@Source("icons/png/web/dialog-question.png")
	ImageResource dialog_question();

	@Source("icons/png/web/dialog-warning.png")
	ImageResource dialog_warning();

	@Source("icons/png/web/question-mark.png")
	ImageResource questionMark();

	// TODO we need another picture
	@Source("icons/png/android/document_viewer.png")
	ImageResource viewSaved();

	@Source("geogebra/resources/js/realsenseinfo-2.0.js")
	TextResource realsenseinfoJs();

	@Source("geogebra/resources/js/realsense-2.0.js")
	TextResource realsenseJs();

	@Source("geogebra/resources/js/promise-1.0.0.min.js")
	TextResource promiseJs();
}
