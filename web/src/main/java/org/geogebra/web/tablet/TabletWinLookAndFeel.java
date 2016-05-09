package org.geogebra.web.tablet;

/**
 * LAF for Windows Store app
 *
 */
public class TabletWinLookAndFeel extends TabletLookAndFeel implements TabletLookAndFeelI {
	/**
	 * Creates new LAF for Windows Store app
	 */
	public TabletWinLookAndFeel(){
		//
	}
	@Override
	public boolean exportSupported() {
		return true;
	}
	
	@Override
	public boolean supportsLocalSave() {
		return true;
	}

	@Override
	public String getFrameStyleName() {
		return "TabletWin";
	}

}
