package geogebra.kernel.discrete.tsp.gui;

import geogebra.kernel.discrete.tsp.model.Edge;
import geogebra.kernel.discrete.tsp.model.Node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.JComponent;

/**
 * ã‚°ãƒ©ãƒ•ã�®ãƒ‡ãƒ¢ã‚’è¡¨ç¤ºã�™ã‚‹ãƒ‘ãƒ�ãƒ«ã�§ã�™ã€‚
 * @author ma38su 
 */
public class DemoPanel extends JComponent {

	/**
	 * è¾ºã�®æŽ¥ç¶šåˆ¶ç´„
	 */
	private boolean[][] connect;

	/**
	 * è¾ºã�®é�žæŽ¥ç¶šåˆ¶ç´„
	 */
	private boolean[][] disconnect;
	
	public void switchNodeIndexView() {
		this.isNodeIndexViwe = !this.isNodeIndexViwe;
	}
	/**
	 * è¾ºã�®æŽ¥ç¶šçŠ¶æ³�
	 */
	private boolean[][] edges;

	/**
	 * ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³ã�®é–“éš”
	 */
	private int interval = 0;

	/**
	 * è¾ºã�®æŽ¥ç¶šåˆ¶ç´„ã�®è¡¨ç¤ºãƒ•ãƒ©ã‚°
	 */
	private boolean isConnectView = false;

	/**
	 * è¾ºã�®é�žæŽ¥ç¶šåˆ¶ç´„ã�®è¡¨ç¤ºãƒ•ãƒ©ã‚°
	 */
	private boolean isDisconnectView = false;
	
	/**
	 * è¾ºã�®è¡¨ç¤ºãƒ•ãƒ©ã‚°
	 */
	private boolean isEdgeView = true;

	/**
	 * é ‚ç‚¹ç•ªå�·ã�®è¡¨ç¤ºãƒ•ãƒ©ã‚°
	 */
	private boolean isNodeIndexViwe = true;
	
	/**
	 * ãƒ•ãƒ¬ãƒ¼ãƒ ã�¨ãƒ‘ãƒ�ãƒ«ã�®ãƒžãƒ¼ã‚¸ãƒ³
	 */
	private final int MARGIN = 5;

	/**
	 * é ‚ç‚¹ã�®ãƒªã‚¹ãƒˆ
	 */
	private List<Node> nodes;

	/**
	 * é ‚ç‚¹æ•°ã�¨å·¡å›žè·¯ã�®ã‚³ã‚¹ãƒˆã�®å¤‰æ›´ã‚’é€šçŸ¥ã�™ã‚‹ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ
	 */
	private Observable observable;

	/**
	 * ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã‚¤ãƒ¡ãƒ¼ã‚¸
	 */
	private Image offs;

	/**
	 * å·¡å›žè·¯
	 */
	private List<Node> route;

	private Rectangle screen;

	/**
	 * ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿
	 * @param observable å·¡å›žè·¯ã‚³ã‚¹ãƒˆã�¨ã€�é ‚ç‚¹æ•°ã�®å¤‰æ›´ã‚’é€šçŸ¥ã�™ã‚‹ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ
	 */
	public DemoPanel(Observable observable) {
		this.observable = observable;
		this.nodes = new ArrayList<Node>();
		this.route = new ArrayList<Node>();
		this.edges = null;
	}
	
	/**
	 * é ‚ç‚¹ã‚’è¿½åŠ ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param x Xåº§æ¨™
	 * @param y Yåº§æ¨™
	 */
	public void add(int x, int y) {
		if (this.screen.contains(x, y)) {
			this.nodes.add(new Node(x, y));
			this.observable.notifyObservers(this.nodes.size());
			this.repaint();
		}
	}

	/**
	 * ãƒ‘ãƒ�ãƒ«ã‚’åˆ�æœŸåŒ–ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 */
	public void clear() {
		this.route.clear();
		this.disconnect = null;
		this.connect = null;
		this.edges = null;
		this.observable.notifyObservers(this.route);
		this.nodes.clear();
		this.observable.notifyObservers(this.nodes.size());
		this.repaint();
	}

	/**
	 * é ‚ç‚¹ã‚’è¿”ã�™ãƒ¡ã‚½ãƒƒãƒ‰
	 * @return é ‚ç‚¹ã�®ãƒªã‚¹ãƒˆ
	 */
	public List<Node> getNodes() {
		return this.nodes;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (this.offs == null || this.getWidth() != this.offs.getWidth(null) || this.getHeight() != this.offs.getHeight(null)) {
			this.offs = this.createImage(this.getWidth(), this.getHeight());
			this.screen = new Rectangle(this.MARGIN, this.MARGIN, this.getWidth() - this.MARGIN * 2, this.getHeight() - this.MARGIN * 2);
		}
		Graphics2D g2 = (Graphics2D) this.offs.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fill(this.screen);
		g2.setClip(this.screen);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.BLACK);
		synchronized (this.route) {
			if (!this.route.isEmpty()) {
				Node n0 = this.route.get(this.route.size() - 1);
				for (Node node : this.route) {
					Edge entry = new Edge(n0, node);
					entry.draw(g2);
					n0 = node;
				}
			}
		}

		try {
			Stroke stroke = g2.getStroke();
			if (this.isDisconnectView) {
				if (this.disconnect != null) {
					g2.setColor(Color.RED);
					g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{5, 5}, 0));
					for (int i = 0; i < this.disconnect.length; i++) {
						// this.nodes.size()ã� ã�¨é ‚ç‚¹ã�Œå¢—ã�ˆã�Ÿã�¨ã��ã�«ä¾‹å¤–ã�Œèµ·ã�“ã‚‹å�¯èƒ½æ€§ã�Œã�‚ã‚‹ã€‚
						for (int j = i + 1; j < this.disconnect[i].length; j++) {
							if (this.disconnect[i][j]) {
								Edge edge = new Edge(this.nodes.get(i), this.nodes.get(j));
								edge.draw(g2);
							}
						}
					}
					g2.setStroke(stroke);
				}
			}
			if (this.edges != null && this.isEdgeView) {
				g2.setColor(Color.DARK_GRAY);
				for (int i = 0; i < this.edges.length; i++) {
					// this.nodes.size()ã� ã�¨é ‚ç‚¹ã�Œå¢—ã�ˆã�Ÿã�¨ã��ã�«ä¾‹å¤–ã�Œèµ·ã�“ã‚‹å�¯èƒ½æ€§ã�Œã�‚ã‚‹ã€‚
					for (int j = i + 1; j < this.edges[i].length; j++) {
						if (this.edges[i][j]) {
							Edge edge = new Edge(this.nodes.get(i), this.nodes.get(j));
							edge.draw(g2);
						}
					}
				}
			}
			
			if (this.isConnectView) {
				if (this.connect != null) {
					g2.setColor(Color.BLUE);
					g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{5, 5}, 0));
					for (int i = 0; i < this.connect.length; i++) {
						// this.nodes.size()ã� ã�¨é ‚ç‚¹ã�Œå¢—ã�ˆã�Ÿã�¨ã��ã�«ä¾‹å¤–ã�Œèµ·ã�“ã‚‹å�¯èƒ½æ€§ã�Œã�‚ã‚‹ã€‚
						for (int j = i + 1; j < this.connect[i].length; j++) {
							if (this.connect[i][j]) {
								Edge edge = new Edge(this.nodes.get(i), this.nodes.get(j));
								edge.draw(g2);
							}
						}
					}
					g2.setStroke(stroke);
				}
			}

			for (int i = 0; i < this.nodes.size(); i++) {
				int connection = 0;
				Node node = this.nodes.get(i);
				if (this.edges != null) {
					for (int j = 0; j < this.edges.length; j++) {
						if (i < this.edges.length && (this.edges[i][j] || this.edges[j][i])) {
							connection++;
						}
					}
				}
				if (connection == 0) {
					node.draw(g2, Color.GRAY);
				} else if (connection % 2 == 0) {
					node.draw(g2, Color.YELLOW);
				} else {
					node.draw(g2, Color.RED);
				}
			}
			
			if (this.isNodeIndexViwe) {
				for (int i = 0; i < this.nodes.size(); i++) {
					this.nodes.get(i).draw(g2, i + 1);
				}
			}
			
			g2.setClip(0, 0, this.getWidth(), this.getHeight());
			g2.setColor(Color.BLACK);
			g2.draw(this.screen);
			g.drawImage(this.offs, 0, 0, null);
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			this.repaint();
		}
	}

	/**
	 * ãƒ‘ãƒ�ãƒ«ã�«è¡¨ç¤ºã�™ã‚‹è¾ºã‚’è¨­å®šã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param edges è¾ºé…�åˆ—
	 */
	public void set(boolean[][] edges) {
		synchronized (this.route) {
			this.route.clear();
		}
		this.observable.notifyObservers(this.route);
		this.edges = edges;
		this.repaint();
		synchronized (this) {
			if (this.interval > 0) {
				try {
					this.wait(this.interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * ãƒ‘ãƒ�ãƒ«ã�«è¡¨ç¤ºã�™ã‚‹æŽ¥ç¶šã€�é�žæŽ¥ç¶šã‚’è¨­å®šã�™ã‚‹ã�Ÿã‚�ã�®ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param connect æŽ¥ç¶šåˆ¶ç´„
	 * @param disconnect é�žæŽ¥ç¶šåˆ¶ç´„
	 */
	public void set(boolean[][] connect, boolean[][] disconnect) {
		this.connect = connect;
		this.disconnect = disconnect;
	}

	/**
	 * å·¡å›žè³‚ã‚’è¨­å®šã�—ã�¾ã�™ã€‚
	 * @param route å·¡å›žè³‚ã‚’ç¤ºã�™é ‚ç‚¹ã�®ãƒªã‚¹ãƒˆ
	 */
	public void set(List<Node> route) {
		this.edges = null;
		synchronized (this.route) {
			this.route.clear();
			synchronized (route) {
				this.route.addAll(route);
			}
		}
		this.repaint();
		synchronized (this) {
			if (this.interval > 0) {
				try {
					this.wait(this.interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * å·¡å›žè·¯ã�®ã‚³ã‚¹ãƒˆã‚’è¨­å®šã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param cost å·¡å›žè·¯ã�®ã‚³ã‚¹ãƒˆ
	 */
	public void setCost(Double cost) {
		this.observable.notifyObservers(cost);
	}

	/**
	 * ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³ã�®é–“éš”ã‚’è¨­å®šã�—ã�¾ã�™ã€‚
	 * @param ms ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³ã�®é–“éš”ï¼ˆmsï¼‰
	 */
	public void setInterval(int ms) {
		synchronized (this) {
			this.interval = ms;
		}
	}

	/**
	 * ãƒ‘ãƒ�ãƒ«ã�«è¡¨ç¤ºã�™ã‚‹é ‚ç‚¹ã‚’è¨­å®šã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param nodes
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
		this.observable.notifyObservers(this.nodes.size());
		this.repaint();
	}

	/**
	 * æŽ¥ç¶šåˆ¶ç´„ã�®è¡¨ç¤ºã‚’åˆ‡æ›¿ã�ˆã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 */
	public void switchConnectViwe() {
		this.isConnectView = !this.isConnectView;
	}

	/**
	 * é�žæŽ¥ç¶šåˆ¶ç´„ã�®è¡¨ç¤ºã‚’åˆ‡æ›¿ã�ˆã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 */
	public void switchDisconnectView() {
		this.isDisconnectView = !this.isDisconnectView;
	}
	
	/**
	 * è¾ºã�®è¡¨ç¤ºã‚’åˆ‡æ›¿ã�ˆã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 */
	public void switchEdgeView() {
		this.isEdgeView = !this.isEdgeView;
	}
}
