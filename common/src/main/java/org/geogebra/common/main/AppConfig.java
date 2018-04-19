package org.geogebra.common.main;

import org.geogebra.common.io.layout.DockPanelData;

public interface AppConfig {

	public void adjust(DockPanelData dp);

	public String getAVTitle();

	public int getLineDisplayStyle();

	public String getAppTitle();

	public String getTutorialKey();

}
