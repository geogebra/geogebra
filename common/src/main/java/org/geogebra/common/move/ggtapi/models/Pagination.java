package org.geogebra.common.move.ggtapi.models;

public class Pagination {
	public final int from;
	public final int to;
	public final int total;

	/**
	 * @param from page start
	 * @param to page end
	 * @param total total number of resources
	 */
	public Pagination(int from, int to, int total) {
		this.from = from;
		this.to = to;
		this.total = total;
	}

}
