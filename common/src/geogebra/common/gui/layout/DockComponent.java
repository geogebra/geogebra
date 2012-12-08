package geogebra.common.gui.layout;

/**
 * interface for DockSplitPane and DockPanel
 * @author mathieu
 *
 */
public interface DockComponent {
	


	/**
	 * minimum half size of a panel
	 */
	public static final int MIN_SIZE = 100;
	
	/**
	 * 
	 * @param prefix prefix to add
	 * @return string description with prefix
	 */
	public String toString(String prefix);
	
	/**
	 * Update resize weight
	 * @return true if it contains a panel that takes new space (currently if contains an euclidian view)
	 */
	public boolean updateResizeWeight();
	
	/**
	 * save divider location (recursively)
	 */
	public void saveDividerLocation();
	
	/**
	 * update divider location (recursively)
	 * @param size new size of the component
	 * @param orientation orientation of the parent split
	 */
	public void updateDividerLocation(int size, int orientation);
	
	/**
	 * set visibility of all DockPanel sub components
	 * @param visible flag
	 */
	public void setDockPanelsVisible(boolean visible);

}
