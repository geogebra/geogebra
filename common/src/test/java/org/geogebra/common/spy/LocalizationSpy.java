package org.geogebra.common.spy;

import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.Locale;

class LocalizationSpy extends Localization {

	@Override
	public String getCommand(String key) {
		return null;
	}

	@Override
	public String getMenu(String key) {
		return null;
	}

	@Override
	public String getError(String key) {
		return null;
	}

	@Override
	public String getSymbol(int key) {
		return null;
	}

	@Override
	public String reverseGetColor(String colorName) {
		return null;
	}

	@Override
	public String getColor(String key) {
		return null;
	}

	@Override
	public String getLanguage() {
		return null;
	}

	@Override
	public String getSymbolTooltip(int key) {
		return null;
	}

	@Override
	public void initCommand() {

	}

	@Override
	protected boolean isCommandChanged() {
		return false;
	}

	@Override
	protected void setCommandChanged(boolean b) {

	}

	@Override
	protected boolean isCommandNull() {
		return false;
	}

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		return null;
	}
}
