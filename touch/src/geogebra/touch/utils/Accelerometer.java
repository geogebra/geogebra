package geogebra.touch.utils;

public class Accelerometer {
	
	private boolean supported;
	private AccelerationCallback clb;
	
	public Accelerometer() {
		this.supported = checkSupport();
	}

	private void attachNativeEvent() {
		
	}

	private native boolean checkSupport() /*-{
		return "DeviceMotionEvent" in $wnd;
	}-*/;

	public void watchAcceleration(
			AccelerationCallback accelerationCallback) {
		if (this.supported) {
			this.clb = accelerationCallback;
			attachEvent(accelerationCallback);
		}
	}

	private native void attachEvent(AccelerationCallback clb) /*-{
		$wnd.addEventListener('devicemotion', function(e) {
			var acc = e.acceleration;
			clb.@geogebra.touch.utils.AccelerationCallback::onSuccess(DDD)(acc.x, acc.y, acc.z);
		});
	}-*/;

	/*TODO: can be done for specific events later*/
	public void clearWatches() {
		removeEvent(this.clb);
	}

	private native void removeEvent(AccelerationCallback clb) /*-{
		$wnd.removeEventListener("devicemotion", clb);
	}-*/;

}
