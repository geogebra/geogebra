package geogebra.common.adapters;

public interface RealMatrix {
    double[] getRow(int r) throws RuntimeException;
	void setRow(int r, double[] arr) throws RuntimeException;
    double[] getColumn(int c) throws RuntimeException;
    void setColumn(int c, double[] arr) throws RuntimeException;
}
