package com.joebotics.simmer.client;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.Scope;
import com.joebotics.simmer.client.gui.util.Display;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.MathUtil;
import com.joebotics.simmer.client.util.MouseModeEnum;

public class CircuitElementFinder {
    private final Simmer simmer;

    public CircuitElementFinder(Simmer simmer) {
        this.simmer = simmer;
    }

    public AbstractCircuitElement selectElement(Point p) {
        AbstractCircuitElement element = findCoveredElement(p);
        if (element == null) {
            element = findNextElement(p);
        }
        if (element == null) {
            element = selectScope(p);
        } else {
            int post = findPost(element, p);
            if (post != -1) {
                simmer.setMousePost(post);
            }
        }
        simmer.setMouseElm(element);
        return element;
    }

    private AbstractCircuitElement findCoveredElement(Point p) {
        if (simmer.getMouseElm() != null && (MathUtil.distanceSq(p.getX(), p.getY(), simmer.getMouseElm().getX1(),
                simmer.getMouseElm().getY1()) <= Display.POSTGRABSQ
                || MathUtil.distanceSq(p.getX(), p.getY(), simmer.getMouseElm().getX2(),
                        simmer.getMouseElm().getY2()) <= Display.POSTGRABSQ)) {
            return simmer.getMouseElm();
        } else {
            int bestDist = 100000;
            int bestArea = 100000;
            for (int i = 0; i != simmer.getElmList().size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);
                if (ce.getBoundingBox().contains(p.getX(), p.getY())) {
                    int j;
                    int area = ce.getBoundingBox().width * ce.getBoundingBox().height;
                    int jn = ce.getPostCount();

                    if (jn > 2)
                        jn = 2;

                    for (j = 0; j != jn; j++) {
                        Point pt = ce.getPost(j);
                        int dist = MathUtil.distanceSq(p.getX(), p.getY(), pt.getX(), pt.getY());

                        // if multiple elements have overlapping bounding boxes,
                        // we prefer selecting elements that have posts close
                        // to the mouse pointer and that have a small bounding
                        // box area.
                        if (dist <= bestDist && area <= bestArea) {
                            bestDist = dist;
                            bestArea = area;
                            return ce;
                        }
                    }
                    if (ce.getPostCount() == 0)
                        return ce;
                }
            }
        }
        return null;
    }

    private AbstractCircuitElement findNextElement(Point p) {
        // // the mouse pointer was not in any of the bounding boxes, but we
        // // might still be close to a post
        for (int i = 0; i < simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (simmer.getMouseMode() == MouseModeEnum.MouseMode.DRAG_POST) {
                if (MathUtil.distanceSq(ce.getX1(), ce.getY1(), p.getX(), p.getY()) < 26) {
                    return ce;
                }
                if (MathUtil.distanceSq(ce.getX2(), ce.getY2(), p.getX(), p.getY()) < 26) {
                    return ce;
                }
            }
            for (int j = 0; j < ce.getPostCount(); j++) {
                Point pt = ce.getPost(j);
                // int dist = distanceSq(x, y, pt.x, pt.y);
                if (MathUtil.distanceSq(pt.getX(), pt.getY(), p.getX(), p.getY()) < 26) {
                    return ce;
                }
            }
        }
        return null;
    }

    private AbstractCircuitElement selectScope(Point p) {
        simmer.setScopeSelected(-1);
        for (int i = 0; i != simmer.getScopeCount(); i++) {
            Scope s = simmer.getScopes()[i];
            if (s.getRect().contains(p.getX(), p.getY())) {
                if (s.isPlotXY()) {
                    simmer.setPlotXElm(s.getElm());
                    simmer.setPlotYElm(s.getyElm());
                }
                simmer.setScopeSelected(i);
                return s.getElm();
            }
        }
        return null;
    }

    private int findPost(AbstractCircuitElement element, Point p) {
        if (element != null) {
            simmer.setMousePost(-1);
            // look for post close to the mouse pointer
            for (int i = 0; i != element.getPostCount(); i++) {
                Point pt = element.getPost(i);
                if (MathUtil.distanceSq(pt.getX(), pt.getY(), p.getX(), p.getY()) < 26) {
                    return i;
                }
            }
        }
        return -1;
    }
}
