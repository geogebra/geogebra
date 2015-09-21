package org.geogebra.web.html5.css;

import org.geogebra.web.html5.util.LessResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResourcesSimple extends ClientBundle {

	GuiResourcesSimple INSTANCE = GWT.create(GuiResourcesSimple.class);

	@Source("org/geogebra/web/resources/js/zipjs/deflate.js")
	TextResource deflateJs();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle.png")
	ImageResource icons_play_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle_hover.png")
	ImageResource icons_play_circle_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle.png")
	ImageResource icons_play_pause_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle_hover.png")
	ImageResource icons_play_pause_circle_hover();

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

	@Source("org/geogebra/web/resources/js/domvas.js")
	TextResource domvas();

	@Source("org/geogebra/web/resources/js/WebMIDIAPIWrapper.js")
	TextResource webMidiAPIWrapperJs();

	@Source("org/geogebra/web/resources/js/midi/jasmid.js")
	TextResource jasmidJs();

	@Source("org/geogebra/web/resources/css/mathquillggb.css")
	TextResource mathquillggbCss();

	@Source("org/geogebra/web/resources/css/web-styles.css")
	LessResource modernStyle();

	// New less resources for GEOGEBRA EXAM
	@Source("org/geogebra/web/exam/css/exam.ltr.less")
	LessResource examStyleLTR();

	@Source("org/geogebra/web/exam/css/exam.rtl.less")
	LessResource examStyleRTL();

	// New less resources
	@Source("org/geogebra/web/resources/css/general.ltr.less")
	LessResource generalStyleLTR();

	@Source("org/geogebra/web/resources/css/general.rtl.less")
	LessResource generalStyleRTL();

	@Source("org/geogebra/web/resources/css/headerbar.ltr.less")
	LessResource headerbarStyleLTR();

	@Source("org/geogebra/web/resources/css/headerbar.rtl.less")
	LessResource headerbarStyleRTL();


	// don't include these anywhere!
	// they are only here, because otherwise they are not compiled on browser
	// refresh and then I cannot see the changes!
	// ---------------------------------------------------------------
	// @Source("org/geogebra/web/resources/css/functions.less")
	// LessResource styleFunctions();
	//
	// @Source("org/geogebra/web/resources/css/directions.less")
	// LessResource styleDirections();
	//
	// @Source("org/geogebra/web/resources/css/definitions.less")
	// LessResource styleDefinitions();
	//
	// @Source("org/geogebra/web/resources/css/general.less")
	// LessResource generalStyle();
	//
	// @Source("org/geogebra/web/resources/css/av.less")
	// LessResource avStyle();
	//
	// @Source("org/geogebra/web/resources/css/av.ltr.less")
	// LessResource avStyleLTR();
	//
	// @Source("org/geogebra/web/resources/css/av.rtl.less")
	// LessResource avStyleRTL();

	// @Source("org/geogebra/web/resources/css/headerbar.less")
	// LessResource headerbarStyle();

	// EXAM
	// @Source("org/geogebra/web/exam/css/exam.less")
	// LessResource examStyle();

	// don't include files above
	// -----------------------------------------------------------------

	@Source("org/geogebra/web/resources/css/jquery-ui.css")
	TextResource jqueryStyle();

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
