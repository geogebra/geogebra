package geogebra.main;

import geogebra.gui.GuiManagerD;
import geogebra.gui.toolbar.ToolbarContainer;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 
 * move some methods out of App so that minimal applets work
 * 
 * @author michael
 *
 */
public class AppD2 {

	public static void initToolbar(AppD app, int toolbarPosition, boolean showToolBarHelp, JPanel northPanel, JPanel eastPanel, JPanel southPanel, JPanel westPanel) {
		
		GuiManagerD guiManager = app.getGuiManagerD();
		
		ToolbarContainer toolBarContainer = (ToolbarContainer) guiManager.getToolbarPanelContainer();
		JComponent helpPanel = toolBarContainer.getToolbarHelpPanel();
		toolBarContainer.setOrientation(toolbarPosition);

		// TODO handle xml for new field toolbarPosition vs. old showToolBarTop 
		
		
		//showToolBarTop = false;
		//if (showToolBarTop) {
			northPanel.add(guiManager.getToolbarPanelContainer(),
					BorderLayout.NORTH);
		//} else {
		//	southPanel.add(guiManager.getToolbarPanelContainer(),
		//			BorderLayout.NORTH);
		//}

		switch (toolbarPosition) {
		case SwingConstants.NORTH:
			northPanel.add(toolBarContainer, BorderLayout.NORTH);
			break;
		case SwingConstants.SOUTH:
			southPanel.add(toolBarContainer, BorderLayout.NORTH);
			break;
		case SwingConstants.EAST:
			eastPanel.add(toolBarContainer, BorderLayout.EAST);
			if (showToolBarHelp && helpPanel != null) {
				northPanel.add(helpPanel, BorderLayout.NORTH);
			}
			break;
		case SwingConstants.WEST:
			westPanel.add(toolBarContainer, BorderLayout.WEST);
			if (showToolBarHelp && helpPanel != null) {
				northPanel.add(helpPanel, BorderLayout.NORTH);
			}
			break;
		}

		northPanel.revalidate();
		southPanel.revalidate();
		westPanel.revalidate();
		eastPanel.revalidate();
		toolBarContainer.buildGui();
	}

}
