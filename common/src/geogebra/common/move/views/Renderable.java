package geogebra.common.move.views;

/**
 * @author gabor
 * must be implemented by views, that added to the view list of BaseView
 */
public interface Renderable {
	/**
	 * renders the given view
	 */
	public void render();
}
