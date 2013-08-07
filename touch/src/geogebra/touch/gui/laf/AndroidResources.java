package geogebra.touch.gui.laf;

import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.SVGResource.Validated;

import com.google.gwt.core.client.GWT;

interface AndroidResources extends DefaultResources {

	static AndroidResources INSTANCE = GWT.create(AndroidResources.class);

	// Dialogs

	@Override
	@Source("icons/svg/android/arrow_go_next.svg")
	@Validated(validated = false)
	SVGResource arrow_go_next();

	@Override
	@Source("icons/svg/android/arrow_go_previous.svg")
	@Validated(validated = false)
	SVGResource arrow_go_previous();

	@Override
	@Source("icons/svg/android/menu_back.svg")
	@Validated(validated = false)
	SVGResource back();

	@Override
	@Source("icons/svg/android/button_cancel.svg")
	@Validated(validated = false)
	SVGResource dialog_cancel();

	// Header

	@Override
	@Source("icons/svg/android/button_ok.svg")
	@Validated(validated = false)
	SVGResource dialog_ok();

	@Override
	@Source("icons/svg/android/button_trashcan.svg")
	@Validated(validated = false)
	SVGResource dialog_trash();

	@Override
	@Source("icons/svg/android/document_edit.svg")
	@Validated(validated = false)
	SVGResource document_edit();

	@Override
	@Source("icons/svg/android/document-new.svg")
	@Validated(validated = false)
	SVGResource document_new();

	@Override
	@Source("icons/svg/android/document-open.svg")
	@Validated(validated = false)
	SVGResource document_open();

	@Override
	@Source("icons/svg/android/document-save.svg")
	@Validated(validated = false)
	SVGResource document_save();

	// GeoGebraTube View

	@Override
	@Source("icons/svg/android/document_share.svg")
	@Validated(validated = false)
	SVGResource document_share();

	@Override
	@Source("icons/svg/android/document_viewer.svg")
	@Validated(validated = false)
	SVGResource document_viewer();

	@Override
	@Source("icons/svg/android/icon_fx.svg")
	@Validated(validated = false)
	SVGResource icon_fx();

	@Override
	@Source("icons/svg/android/icon_question.svg")
	@Validated(validated = false)
	SVGResource icon_question();

	@Override
	@Source("icons/svg/android/icon_warning.svg")
	@Validated(validated = false)
	SVGResource icon_warning();

	@Override
	@Source("icons/svg/android/menu_edit_redo.svg")
	@Validated(validated = false)
	SVGResource redo();

	@Override
	@Source("icons/svg/android/document-open.svg")
	@Validated(validated = false)
	SVGResource search();

	@Override
	@Source("icons/svg/android/menu_edit_undo.svg")
	@Validated(validated = false)
	SVGResource undo();

}
