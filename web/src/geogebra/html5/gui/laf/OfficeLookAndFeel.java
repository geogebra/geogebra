package geogebra.html5.gui.laf;


/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class OfficeLookAndFeel extends SmartLookAndFeel{
	
	@Override
    public boolean undoRedoSupported() {
	    return true;
    }
	
	@Override
    public boolean isSmart() {
		return false;
	}

	@Override
    public String getType() {
	    return "office";
    }

	
	@Override
    public String getLoginListener() {
	    return null;
    }
}
