package geogebra.common.kernel.prover;

import java.math.BigInteger;

public class FreeVariable implements Comparable{
	private static int n=0;
	private final int id;
	private BigInteger value;
	
	public FreeVariable(){
		n++;
		id=n;
	}
	
	protected FreeVariable(FreeVariable fv){
		id=fv.getId();
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

	public int compareTo(Object o) {
		if (o instanceof FreeVariable) {
			if (id < ((FreeVariable) o).getId()) {
				return 1;
			}
			if (id > ((FreeVariable) o).getId()) {
				return -1;
			}
			return 0;
		}
		return 0;
	}
	
	public boolean equals(Object o){
		if (o instanceof FreeVariable){
			return id==((FreeVariable)o).id;
		} 
		return super.equals(o);
	}
	
}
