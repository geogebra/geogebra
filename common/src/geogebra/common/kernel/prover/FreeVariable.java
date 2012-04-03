package geogebra.common.kernel.prover;

import java.math.BigInteger;

public class FreeVariable {
	private static int n=0;
	private final int id;
	private BigInteger value;
	
	public FreeVariable(){
		n++;
		id=n;
	}
	
	public int getId() {
		return id;
	}

	public String toString(){
		return "freevar"+id;
	}
	
	public BigInteger getValue() {
		return value;
	}
	
	public void setValue(BigInteger value) {
		this.value = value;
	}
	
}
