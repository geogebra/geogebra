package org.geogebra.common.main.settings.config;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.properties.factory.NotesPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * Config for Mebis Notes and GGB Notes
 *
 */
public class AppConfigNotes extends AppConfigDefault {

	public AppConfigNotes() {
		super(GeoGebraConstants.NOTES_APPCODE);
	}

	@Override
	public String getPreferencesKey() {
		return "_notes";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.NOTES + "";
	}

	@Override
	public String getAppTitle() {
		return "Notes";
	}

	@Override
	public String getTutorialKey() {
		return "notes_tutorials";
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.NOTES;
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.NOTES;
	}

	@Override
	public boolean disableTraceCM() {
		return true;
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}

	@Override
	public boolean showAxesOnFileNew() {
		return false;
	}

	@Override
	public boolean showGridOnFileNew() {
		return false;
	}

	@Override
	public boolean shouldKeepRatioEuclidian() {
		return true;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new NotesPropertiesFactory();
	}
}
