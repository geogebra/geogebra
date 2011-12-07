package geogebra.adapters;

public class FactoryAdapter extends geogebra.common.adapters.FactoryAdapter {

	public FactoryAdapter() {
		//prototype = this;//Well, I'm not sure it will work with a free (new FactoryAdapter()); //Arpad Fekete
	}

	public geogebra.common.adapters.Complex newComplex(double r, double i) {
		return new Complex(r, i);
	}
}
