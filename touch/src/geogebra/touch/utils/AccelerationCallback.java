package geogebra.touch.utils;



public interface AccelerationCallback {
	public void onSuccess(double x, double y, double z);

	public void onFailure();
}
