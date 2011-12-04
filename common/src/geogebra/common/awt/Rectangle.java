package geogebra.common.awt;

public interface Rectangle {

	int width = 0;
	int height = 0;

	void setBounds(int xLabel, int i, int width, int height);

	void setLocation(int xLabel, int i);

	void setBounds(Rectangle rectangle);

	boolean contains(int x, int y);

}
