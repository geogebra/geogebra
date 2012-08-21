package geogebra.main;

/**
 * move DockBar out of App so that minimal applets work
 * 
 * @author michael
 *
 */
public interface DockBarInterface {

	boolean isEastOrientation();

	void setVisible(boolean b);

	void update();

	void setLabels();

	void showPopup();

	boolean isShowButtonBar();

	void setEastOrientation(boolean selected);

	void setShowButtonBar(boolean selected);

}
