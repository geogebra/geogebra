package geogebra.main;

import geogebra.common.gui.GuiManager;
import geogebra.gui.GuiManagerD;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.layout.DockBar;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.toolbar.ToolbarContainer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;

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
		
		GuiManagerD guiManager = (GuiManagerD) app.getGuiManager();
		
		// initialize toolbar panel even if it's not used (hack)
		((GuiManagerD) app.getGuiManager()).getToolbarPanelContainer();

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
			eastPanel.add(toolBarContainer, app.borderEast());
			if (showToolBarHelp && helpPanel != null) {
				northPanel.add(helpPanel, BorderLayout.NORTH);
			}
			break;
		case SwingConstants.WEST:
			westPanel.add(toolBarContainer, app.borderWest());
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

	public static void initInputBar(AppD app, boolean showInputTop, JPanel northPanel, JPanel southPanel) {
		if (showInputTop) {
			northPanel.add(( (GuiManagerD) app.getGuiManager()).getAlgebraInput(),
					BorderLayout.SOUTH);
		} else {
			southPanel.add(( (GuiManagerD) app.getGuiManager()).getAlgebraInput(),
					BorderLayout.SOUTH);
		}
		((AlgebraInput)( (GuiManagerD) app.getGuiManager()).getAlgebraInput()).updateOrientation(showInputTop);
	}

	public static JPanel getMenuBarPanel(AppD appD, JPanel applicationPanel) {
		JPanel menuBarPanel = new JPanel(new BorderLayout());
		menuBarPanel.add(( (GuiManagerD) appD.getGuiManager()).getMenuBar(),
				BorderLayout.NORTH);
		menuBarPanel.add(applicationPanel, BorderLayout.CENTER);
		return menuBarPanel;
	}

	public static GuiManager newGuiManager(AppD appD) {
		return new GuiManagerD(appD);
	}

	public static void loadFile(AppD app, File currentFile, boolean b) {
		((GuiManagerD) app.getGuiManager()).loadFile(currentFile, false);
	}

	public static void setActiveView(AppD app, int view) {
		((LayoutD) app.getGuiManager().getLayout()).getDockManager().setFocusedPanel(view);
	}

	public static boolean inExternalWindow(AppD app, Component eventPane) {
		return ((LayoutD) app.getGuiManager().getLayout()).inExternalWindow(eventPane);
	}

	public static Component getRootComponent(AppD app) {
		return ((LayoutD) app.getGuiManager().getLayout()).getRootComponent();
	}

	public static void newLayout(AppD app) {
		app.guiManager.setLayout(new geogebra.gui.layout.LayoutD());
	}

	public static DockBarInterface newDockBar(AppD app) {
		return new DockBar(app);
	}

}
