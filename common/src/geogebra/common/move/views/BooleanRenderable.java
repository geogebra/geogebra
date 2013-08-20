package geogebra.common.move.views;

/**
 * @author gabor
 * must be implemented by views, that added to the view list of BaseView
 */
public interface BooleanRenderable {
	/**
	 * renders the given view
	 */
	public void render(boolean b);
}
