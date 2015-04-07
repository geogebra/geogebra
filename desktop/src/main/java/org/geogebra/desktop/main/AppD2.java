package org.geogebra.desktop.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.inputbar.AlgebraInput;
import org.geogebra.desktop.gui.layout.DockBar;
import org.geogebra.desktop.gui.toolbar.ToolbarContainer;

/**
 * 
 * move some methods out of App so that minimal applets work
 * 
 * @author michael
 * 
 */
public class AppD2 {

	private static GuiManagerD getGuiManager(AppD app) {
		return (GuiManagerD) app.getGuiManager();
	}

	public static void initToolbar(AppD app, int toolbarPosition,
			boolean showToolBarHelp, JPanel northPanel, JPanel eastPanel,
			JPanel southPanel, JPanel westPanel) {

		GuiManagerD guiManager = getGuiManager(app);
		LocalizationD loc = app.getLocalization();
		// initialize toolbar panel even if it's not used (hack)
		guiManager.getToolbarPanelContainer();

		ToolbarContainer toolBarContainer = (ToolbarContainer) guiManager
				.getToolbarPanelContainer();
		JComponent helpPanel = toolBarContainer.getToolbarHelpPanel();
		toolBarContainer.setOrientation(toolbarPosition);
		ToolbarContainer.setShowHelp(showToolBarHelp);

		switch (toolbarPosition) {
		case SwingConstants.NORTH:
			northPanel.add(toolBarContainer, BorderLayout.NORTH);
			break;
		case SwingConstants.SOUTH:
			southPanel.add(toolBarContainer, BorderLayout.NORTH);
			break;
		case SwingConstants.EAST:
			eastPanel.add(toolBarContainer, loc.borderEast());
			if (showToolBarHelp && helpPanel != null) {
				northPanel.add(helpPanel, BorderLayout.NORTH);
			}
			break;
		case SwingConstants.WEST:
			westPanel.add(toolBarContainer, loc.borderWest());
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
		helpPanel.revalidate();
	}

	public static void initInputBar(AppD app, boolean showInputTop,
			JPanel northPanel, JPanel southPanel) {
		GuiManagerD gui = (GuiManagerD) app.getGuiManager();
		if (showInputTop) {
			northPanel.add(gui.getAlgebraInput(), BorderLayout.SOUTH);
		} else {
			southPanel.add(gui.getAlgebraInput(), BorderLayout.SOUTH);
		}
		((AlgebraInput) gui.getAlgebraInput()).updateOrientation(showInputTop);
	}

	public static JPanel getMenuBarPanel(AppD appD, JPanel applicationPanel) {
		JPanel menuBarPanel = new JPanel(new BorderLayout());
		menuBarPanel.add(appD.getGuiManager().getMenuBar(), BorderLayout.NORTH);
		menuBarPanel.add(applicationPanel, BorderLayout.CENTER);
		return menuBarPanel;
	}

	public static GuiManagerD newGuiManager(AppD appD) {
		return new GuiManagerD(appD);
	}

	public static void loadFile(AppD app, File currentFile, boolean b) {
		app.getGuiManager().loadFile(currentFile, false);
	}

	public static void setActiveView(AppD app, int view) {
		getGuiManager(app).getLayout().getDockManager().setFocusedPanel(view);
	}

	public static boolean inExternalWindow(AppD app, Component eventPane) {
		return getGuiManager(app).getLayout().inExternalWindow(eventPane);
	}

	public static Component getRootComponent(AppD app) {
		return getGuiManager(app).getLayout().getRootComponent();
	}

	public static void newLayout(AppD app) {
		app.guiManager.setLayout(new org.geogebra.desktop.gui.layout.LayoutD(app));
	}

	public static DockBarInterface newDockBar(AppD app) {
		return new DockBar(app);
	}

}
