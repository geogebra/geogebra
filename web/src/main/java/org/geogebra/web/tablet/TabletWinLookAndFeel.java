package org.geogebra.web.tablet;

import org.geogebra.common.main.App;

public class TabletWinLookAndFeel extends TabletLookAndFeel implements TabletLookAndFeelI {
	public TabletWinLookAndFeel(){
		App.debug("WIN");
	}
	@Override
	public boolean exportSupported() {
		return true;
	}
	
	@Override
	public boolean supportsLocalSave() {
		return true;
	}

}
