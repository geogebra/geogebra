package geogebra.touch.gui;

import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.ui.SVGResource.Validated;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface AndroidResources extends ClientBundle {
	
	public static AndroidResources INSTANCE = GWT.create(AndroidResources.class);

	@Source("icons/svg/android/android_delete.svg")
	@Validated(validated = false)
	SVGResource android_delete();

	@Source("icons/svg/android/android_edit.svg")
	@Validated(validated = false)
	SVGResource android_edit();

	@Source("icons/svg/android/android_fx_input_icon.svg")
	@Validated(validated = false)
	SVGResource android_fx_input_icon();

	@Source("icons/svg/android/android_menu_back.svg")
	@Validated(validated = false)
	SVGResource android_menu_back();

	@Source("icons/svg/android/android_menu_cancel.svg")
	@Validated(validated = false)
	SVGResource android_menu_cancel();

	@Source("icons/svg/android/android_menu_edit_redo.svg")
	@Validated(validated = false)
	SVGResource android_menu_edit_redo();

	@Source("icons/svg/android/android_menu_edit_undo.svg")
	@Validated(validated = false)
	SVGResource android_menu_edit_undo();

	@Source("icons/svg/android/android_menu_new_file.svg")
	@Validated(validated = false)
	SVGResource android_menu_new_file();

	@Source("icons/svg/android/android_menu_ok.svg")
	@Validated(validated = false)
	SVGResource android_menu_ok();

	@Source("icons/svg/android/android_menu_save_file.svg")
	@Validated(validated = false)
	SVGResource android_menu_save_file();

	@Source("icons/svg/android/android_menu_search.svg")
	@Validated(validated = false)
	SVGResource android_menu_search();
	
	@Source("icons/svg/android/android_menu_home.svg")
	@Validated(validated = false)
	SVGResource android_menu_home();

	@Source("icons/svg/android/android_open_viewer.svg")
	@Validated(validated = false)
	SVGResource android_open_viewer();

}
