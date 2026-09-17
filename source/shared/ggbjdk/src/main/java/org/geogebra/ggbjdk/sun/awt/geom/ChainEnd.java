/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.ggbjdk.sun.awt.geom;

import com.google.j2objc.annotations.Weak;

final class ChainEnd {
	CurveLink head;
	CurveLink tail;

	@Weak
	ChainEnd partner;

	int etag;

	public ChainEnd(CurveLink first, ChainEnd partner) {
		this.head = first;
		this.tail = first;
		this.partner = partner;
		this.etag = first.getEdgeTag();
	}

	public CurveLink getChain() {
		return head;
	}

	public void setOtherEnd(ChainEnd partner) {
		this.partner = partner;
	}

	public ChainEnd getPartner() {
		return partner;
	}

	/*
	 * Returns head of a complete chain to be added to subcurves
	 * or null if the links did not complete such a chain.
	 */
	public CurveLink linkTo(ChainEnd that) {
		if (etag == AreaOp.ETAG_IGNORE || that.etag == AreaOp.ETAG_IGNORE) {
			throw new RuntimeException("ChainEnd linked more than once!");
		}
		if (etag == that.etag) {
			throw new RuntimeException("Linking chains of the same type!");
		}
		ChainEnd enter, exit;
		// assert(partner.etag != that.partner.etag);
		if (etag == AreaOp.ETAG_ENTER) {
			enter = this;
			exit = that;
		} else {
			enter = that;
			exit = this;
		}
		// Now make sure these ChainEnds are not linked to any others...
		etag = AreaOp.ETAG_IGNORE;
		that.etag = AreaOp.ETAG_IGNORE;
		// Now link everything up...
		enter.tail.setNext(exit.head);
		enter.tail = exit.tail;
		if (partner == that) {
			// Curve has closed on itself...
			return enter.head;
		}
		// Link this chain into one end of the chain formed by the partners
		ChainEnd otherenter = exit.partner;
		ChainEnd otherexit = enter.partner;
		otherenter.partner = otherexit;
		otherexit.partner = otherenter;
		if (enter.head.getYTop() < otherenter.head.getYTop()) {
			enter.tail.setNext(otherenter.head);
			otherenter.head = enter.head;
		} else {
			otherexit.tail.setNext(enter.head);
			otherexit.tail = enter.tail;
		}
		return null;
	}

	public void addLink(CurveLink newlink) {
		if (etag == AreaOp.ETAG_ENTER) {
			tail.setNext(newlink);
			tail = newlink;
		} else {
			newlink.setNext(head);
			head = newlink;
		}
	}

	public double getX() {
		if (etag == AreaOp.ETAG_ENTER) {
			return tail.getXBot();
		}
		return head.getXBot();
	}
}
