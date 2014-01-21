package geogebra.touch.gui.laf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

interface AndroidResources extends DefaultResources {

	static AndroidResources INSTANCE = GWT.create(AndroidResources.class);

	// Dialogs

	@Override
	@Source("icons/svg/android/arrow_go_next.svg")
	ImageResource arrow_go_next();

	@Override
	@Source("icons/svg/android/arrow_go_previous.svg")
	ImageResource arrow_go_previous();

	@Override
	@Source("icons/svg/android/menu_back.svg")
	ImageResource back();

	@Override
	@Source("icons/svg/android/button_cancel.svg")
	ImageResource dialog_cancel();

	// Header

	@Override
	@Source("icons/svg/android/button_trashcan.svg")
	ImageResource dialog_trash();

	@Override
	@Source("icons/svg/android/document_edit.svg")
	ImageResource document_edit();

	@Override
	@Source("icons/svg/android/document-new.svg")
	ImageResource document_new();

	@Override
	@Source("icons/svg/android/document-open.svg")
	ImageResource document_open();

	@Override
	@Source("icons/svg/android/document-save.svg")
	ImageResource document_save();

	// GeoGebraTube View

	@Override
	@Source("icons/svg/android/document_share.svg")
	ImageResource document_share();

	@Override
	@Source("icons/svg/android/document_viewer.svg")
	ImageResource document_viewer();

	@Override
	@Source("icons/svg/android/icon_fx.svg")
	ImageResource icon_fx();

	@Override
	@Source("icons/svg/android/icon_question.svg")
	ImageResource icon_question();

	@Override
	@Source("icons/svg/android/icon_warning.svg")
	ImageResource icon_warning();

	@Override
	@Source("icons/svg/android/menu_edit_redo.svg")
	ImageResource redo();

	@Override
	@Source("icons/svg/android/document-open.svg")
	ImageResource search();

	@Override
	@Source("icons/svg/android/menu_edit_undo.svg")
	ImageResource undo();

}
