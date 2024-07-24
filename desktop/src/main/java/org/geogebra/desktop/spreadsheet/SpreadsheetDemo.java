package org.geogebra.desktop.spreadsheet;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.OverlayLayout;
import javax.swing.border.BevelBorder;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.kernel.GeoElementCellRendererFactory;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidian.CursorMap;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.gui.spreadsheet.AwtReTeXGraphicsBridgeD;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;

public class SpreadsheetDemo {

	/**
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame("spreadsheet");
			Dimension preferredSize = new Dimension(800, 600);
			frame.setPreferredSize(preferredSize);
			AppCommon appCommon = new AppCommon(new LocalizationCommon(3), new AwtFactoryD());
			KernelTabularDataAdapter adapter = new KernelTabularDataAdapter(
					appCommon.getSettings().getSpreadsheet(), appCommon.getKernel());
			Spreadsheet spreadsheet = new Spreadsheet(adapter,
					new GeoElementCellRendererFactory(new AwtReTeXGraphicsBridgeD()), null);

			FactoryProviderDesktop.setInstance(new FactoryProviderDesktop());
			spreadsheet.setWidthForColumns(60, 0, 10);
			spreadsheet.setHeightForRows(20, 0, 10);

			spreadsheet.setWidthForColumns(90, 2, 4);
			spreadsheet.setHeightForRows(40, 3, 5);
			SpreadsheetPanel spreadsheetPanel = new SpreadsheetPanel(spreadsheet, appCommon, frame);
			appCommon.getKernel().attach(adapter);
			/*appCommon.getGgbApi().evalCommand(String.join("\n", "C4=7", "C5=8",
					"A1=4", "B2=true", "B3=Button()", "B4=sqrt(x)"));*/
			appCommon.setXML(readDemoFile(), true);
			spreadsheetPanel.setPreferredSize(preferredSize);
			initParentPanel(frame, spreadsheetPanel);
			spreadsheet.setViewport(spreadsheetPanel.getViewport());

			frame.setVisible(true);
			frame.setSize(preferredSize);
		} catch (Throwable t) {
			Log.debug(t);
		}
	}

	private static String readDemoFile() throws URISyntaxException, IOException {
		return Files.readString(Paths.get(SpreadsheetDemo.class
				.getResource("spreadsheet.xml").toURI()), StandardCharsets.UTF_8);
	}

	private static void initParentPanel(JFrame frame, SpreadsheetPanel spreadsheetPanel) {
		JScrollBar verticalScrollBar = new JScrollBar();
		JScrollBar horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

		JPanel topBar = new JPanel();
		topBar.setBackground(Color.lightGray);
		topBar.setPreferredSize(new Dimension(800, 30));
		topBar.add(new JLabel("(Click here to clear selection)"));
		topBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				spreadsheetPanel.spreadsheet.clearSelection();
				frame.repaint();
			}
		});

		JPanel spreadsheetContainer = new JPanel();
		spreadsheetContainer.setLayout(new BoxLayout(spreadsheetContainer, BoxLayout.X_AXIS));
		spreadsheetContainer.add(spreadsheetPanel);
		spreadsheetContainer.add(verticalScrollBar);
		scrollPanel.add(topBar);
		scrollPanel.add(spreadsheetContainer);
		scrollPanel.add(horizontalScrollBar);

		Container contentPane = frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(800, 600));
		contentPane.setLayout(new OverlayLayout(contentPane));
		contentPane.add(scrollPanel);
		spreadsheetPanel.editorOverlay = new JPanel();
		spreadsheetPanel.editorOverlay.setPreferredSize(new Dimension(800, 600));
		spreadsheetPanel.editorOverlay.setLayout(null);
		contentPane.add(spreadsheetPanel.editorOverlay);
		spreadsheetPanel.editorOverlay.add(spreadsheetPanel.editorBox);

		verticalScrollBar.addAdjustmentListener(evt -> {
			spreadsheetPanel.scrollY = evt.getValue() * 10;
			spreadsheetPanel.spreadsheet.setViewport(spreadsheetPanel.getViewport());
			frame.repaint();
		});
		horizontalScrollBar.addAdjustmentListener(evt -> {
			spreadsheetPanel.scrollX = evt.getValue() * 10;
			spreadsheetPanel.spreadsheet.setViewport(spreadsheetPanel.getViewport());
			frame.repaint();
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private static class SpreadsheetPanel extends JPanel {
		private final Spreadsheet spreadsheet;
		private final MathFieldD mathField;
		private final Box editorBox = Box.createHorizontalBox();
		private final JPopupMenu contextMenu = new JPopupMenu();
		public JPanel editorOverlay;

		private int scrollX;
		private int scrollY;

		public SpreadsheetPanel(Spreadsheet spreadsheet, AppCommon app, JFrame frame) {
			this.spreadsheet = spreadsheet;
			this.mathField = new MathFieldD(new SyntaxAdapterImpl(app.getKernel()),
					editorBox::repaint);
			editorBox.setBorder(new BevelBorder(BevelBorder.RAISED));
			editorBox.add(mathField);
			mathField.setBounds(0, 0, 200, 200);
			editorBox.setAlignmentX(0);
			editorBox.setAlignmentY(0);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					spreadsheet.handlePointerUp(event.getX(), event.getY(),
							getModifiers(event));
					repaint();
				}

				@Override
				public void mousePressed(MouseEvent event) {
					spreadsheet.handlePointerDown(event.getX(), event.getY(),
							getModifiers(event));
					repaint();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					MouseCursor cursor = spreadsheet.getCursor(e.getX(), e.getY());
					setCursor(CursorMap.get(cursor));
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					spreadsheet.handlePointerMove(e.getX(), e.getY(), getModifiers(e));
					repaint();
				}
			});
			setFocusable(true);
			addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					// key press only
				}

				@Override
				public void keyPressed(KeyEvent e) {
					spreadsheet.handleKeyPressed(e.getKeyCode(),
							e.getKeyChar() + "", getModifiers(e));
                    repaint();
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// key press only
				}
			});

			spreadsheet.setControlsDelegate(new SpreadsheetControlsDelegate() {

				private final SpreadsheetCellEditor editor = new DesktopSpreadsheetCellEditor(frame,
						app);

				private ClipboardInterface clipboard = new ClipboardD();

				@Override
				public SpreadsheetCellEditor getCellEditor() {
					return editor;
				}

				@Override
				public void showContextMenu(List<ContextMenuItem> items, GPoint position) {
					contextMenu.show(editorOverlay, position.x, position.y);
					contextMenu.removeAll();
					for (ContextMenuItem item: items) {
						String localizationKey = item.getLocalizationKey();
						JMenuItem btn = new JMenuItem(localizationKey);
						btn.setAction(new AbstractAction(localizationKey) {
							@Override
							public void actionPerformed(ActionEvent e) {
								item.performAction();
							}
						});
						contextMenu.add(btn);
					}
					contextMenu.setVisible(true);
					frame.revalidate();
				}

				@Override
				public void hideContextMenu() {
					contextMenu.setVisible(false);
				}

				@Override
				public ClipboardInterface getClipboard() {
					return clipboard;
				}
			});
		}

		private Modifiers getModifiers(MouseEvent event) {
			return new Modifiers(event.isAltDown(), event.isControlDown(), event.isShiftDown(),
					event.getButton() == 3);
		}

		private Modifiers getModifiers(KeyEvent event) {
			return new Modifiers(event.isAltDown(),
					event.isControlDown() || event.isMetaDown(), // looks like Meta == Cmd on Mac
					event.isShiftDown(), false);
		}

		public Rectangle getViewport() {
			return new Rectangle(scrollX, scrollX + 500, scrollY, scrollY + 400);
		}

		@Override
		public void paint(Graphics graphics) {
			super.paint(graphics);
			GGraphics2DD graphics1 = new GGraphics2DD((Graphics2D) graphics);
			spreadsheet.draw(graphics1);
		}

		private class DesktopSpreadsheetCellEditor implements SpreadsheetCellEditor {

			private final JFrame frame;
			private final AppCommon app;

			DesktopSpreadsheetCellEditor(JFrame frame, AppCommon app) {
				this.frame = frame;
				this.app = app;
			}

			@Override
			public void show(Rectangle editorBounds, Rectangle viewport, int textAlignment) {
				if (!frame.getContentPane().isAncestorOf(editorBox)) {
					frame.getContentPane().add(editorBox);
				}
				Point locationInWindow = getParent().getLocation();
				editorBox.setBounds((int) editorBounds.getMinX() + (int)locationInWindow.x,
						(int) editorBounds.getMinY() + (int)locationInWindow.y,
						(int) editorBounds.getWidth(), (int) editorBounds.getHeight());
				mathField.setBounds(0, 0,
						(int) editorBounds.getWidth(), (int) editorBounds.getHeight());
				editorBox.setVisible(true);
				mathField.requestViewFocus();
			}

			@Override
			public void hide() {
				editorBox.setVisible(false);
				requestFocus();
				frame.getContentPane().repaint();
			}

			@Override
			public @Nonnull MathFieldInternal getMathField() {
				return mathField.getInternal();
			}

			@Override
			public @Nonnull DefaultSpreadsheetCellProcessor getCellProcessor() {
				return new DefaultSpreadsheetCellProcessor(
						app.getKernel().getAlgebraProcessor(),
						app.getDefaultErrorHandler());
			}

			@Override
			public @Nonnull SpreadsheetCellDataSerializer getCellDataSerializer() {
				return new DefaultSpreadsheetCellDataSerializer();
			}
		}
	}
}
