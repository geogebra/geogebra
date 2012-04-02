package geogebra.common.gui;

import geogebra.common.io.layout.Perspective;
import geogebra.common.main.settings.LayoutSettings;

import java.util.ArrayList;

/**
 * @author gabor
 * 
 * Abstract class for Web and Desktop Layout
 *
 */
public abstract class Layout {
	
	protected ArrayList<Perspective> perspectives;
	
	/**
	 * Layout settings.
	 */
	protected LayoutSettings settings;
	
	/**
	 * An array with the default perspectives.
	 */
	public static Perspective[] defaultPerspectives;
	
	/**
	 * Set a list of perspectives as the perspectives of this user and
	 * apply the "tmp" perspective if one was found.
	 * 
	 * @param perspectives
	 */
	public void setPerspectives(ArrayList<Perspective> perspectives) {
		boolean foundTmp = false;
		
		if(perspectives != null) {
			this.perspectives = perspectives;
			
			for(Perspective perspective : perspectives) {
				if(perspective.getId().equals("tmp")) {
					perspectives.remove(perspective);
					applyPerspective(perspective);
					foundTmp = true;
					break;
				}
			}
		} else {
			this.perspectives = new ArrayList<Perspective>();
		}
		
		if(!foundTmp) {
			applyPerspective(defaultPerspectives[0]);
		}
	}

	abstract protected void applyPerspective(Perspective perspective);

}
