package com.joebotics.simmer.client.util;

import java.util.List;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.CapacitorElm;
import com.joebotics.simmer.client.elcomp.CurrentElm;
import com.joebotics.simmer.client.elcomp.InductorElm;
import com.joebotics.simmer.client.elcomp.VoltageElm;

public class FindPathInfo {
	public static final int		CAP_V	= 4;
	public static final int		INDUCT	= 1;
	public static final int		SHORT	= 3;
	public static final int		VOLTAGE	= 2;
	int						dest;
	AbstractCircuitElement firstElm;
	int						type;
	boolean					used[];
	List<AbstractCircuitElement> elementList = null;

	public FindPathInfo(int t, AbstractCircuitElement e, int d, int nodeListSize, List<AbstractCircuitElement> elmList) {
		dest = d;
		type = t;
		firstElm = e;
		used = new boolean[nodeListSize];
		elementList = elmList;
	}

	public boolean findPath(int n1) {
		return findPath(n1, -1);
	}

	public boolean findPath(int n1, int depth) {
		
		CircuitElementUtil ceUtil = new CircuitElementUtil(elementList);
		
		if (n1 == dest)
			return true;
		
		if (depth-- == 0)
			return false;
		
		if (used[n1]) {
			// System.out.println("used " + n1);
			return false;
		}
		
		used[n1] = true;
		int i;
		
		for (i = 0; i != elementList.size(); i++) {
			AbstractCircuitElement ce = ceUtil.getElm(i);
			
			if (ce == firstElm)
				continue;
			
			if (type == INDUCT) {
				if (ce instanceof CurrentElm)
					continue;
			}
			if (type == VOLTAGE) {

				if (!(ce.isWire() || ce instanceof VoltageElm))
					continue;
			}
			if (type == SHORT && !ce.isWire())
				continue;
			
			if (type == CAP_V) {
				if (!(ce.isWire() || ce instanceof CapacitorElm || ce instanceof VoltageElm))
					continue;
			}
			
			if (n1 == 0) {
				// look for posts which have a ground connection;
				// our path can go through ground
				int j;
				for (j = 0; j != ce.getPostCount(); j++)
					if (ce.hasGroundConnection(j) && findPath(ce.getNode(j), depth)) {
						used[n1] = false;
						return true;
					}
			}
			
			int j;
			for (j = 0; j != ce.getPostCount(); j++) {
				// System.out.println(ce + " " + ce.getNode(j));
				if (ce.getNode(j) == n1)
					break;
			}
			
			if (j == ce.getPostCount())
				continue;
			
			if (ce.hasGroundConnection(j) && findPath(0, depth)) {
				// System.out.println(ce + " has ground");
				used[n1] = false;
				return true;
			}
			
			if (type == INDUCT && ce instanceof InductorElm) {
				double c = ce.getCurrent();
				if (j == 0)
					c = -c;
				// System.out.println("matching " + c + " to " +
				// firstElm.getCurrent());
				// System.out.println(ce + " " + firstElm);
				if (Math.abs(c - firstElm.getCurrent()) > 1e-10)
					continue;
			}
			
			int k;
			for (k = 0; k != ce.getPostCount(); k++) {
				if (j == k)
					continue;
				// System.out.println(ce + " " + ce.getNode(j) + "-" +
				// ce.getNode(k));
				if (ce.getConnection(j, k) && findPath(ce.getNode(k), depth)) {
					// System.out.println("got findpath " + n1);
					used[n1] = false;
					return true;
				}
				// System.out.println("back on findpath " + n1);
			}
		}
		
		used[n1] = false;
		// System.out.println(n1 + " failed");
		return false;
	}
}
