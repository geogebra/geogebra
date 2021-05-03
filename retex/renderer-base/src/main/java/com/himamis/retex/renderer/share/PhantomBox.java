package com.himamis.retex.renderer.share;

public class PhantomBox extends StrutBox {

	private final Box box;

	PhantomBox(Box box, final double w, final double h, final double d,
			final double s) {
		super(w, h, d, s);
		this.box = box;
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position);
		box.inspect(handler, position);
	}
}
