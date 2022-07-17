package com.joebotics.simmer.client;

import java.util.List;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.GraphicElm;
import com.joebotics.simmer.client.gui.util.Display;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Rectangle;
import com.joebotics.simmer.client.util.CircuitElementFactory;
import com.joebotics.simmer.client.util.MathUtil;
import com.joebotics.simmer.client.util.MouseModeEnum;

public class CirciutElmDragHelper {
    private final Simmer simmer;
    private boolean dragStarted = false;
    private int dragX, dragY, initDragX, initDragY;

    public CirciutElmDragHelper(Simmer simmer) {
        this.simmer = simmer;
    }

    public void startDrag(Point p) {
        simmer.setMouseDragging(true);
        if (dragStarted == false) {
            dragStarted = true;
            simmer.getMainMenuBar().getEditMenu().pushUndo();
        }
        // IES - Grab resize handles in select mode if they are far enough apart
        // and you are on top of them
        if (simmer.getTempMouseMode() == MouseModeEnum.MouseMode.SELECT && simmer.getMouseElm() != null
                && MathUtil.distanceSq(simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1(),
                        simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) >= 256
                && (MathUtil.distanceSq(p.getX(), p.getY(), simmer.getMouseElm().getX1(),
                        simmer.getMouseElm().getY1()) <= Display.POSTGRABSQ
                        || MathUtil.distanceSq(p.getX(), p.getY(), simmer.getMouseElm().getX2(),
                                simmer.getMouseElm().getY2()) <= Display.POSTGRABSQ)
                && !simmer.anySelectedButMouse())
            simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_POST);

        if (simmer.getTempMouseMode() != MouseModeEnum.MouseMode.SELECT
                && simmer.getTempMouseMode() != MouseModeEnum.MouseMode.DRAG_SELECTED)
            simmer.getMainMenuBar().getEditMenu().doSelectNone();

        simmer.getMainMenuBar().getEditMenu().pushUndo();
        
        initDragX = p.getX();
        initDragY = p.getY();
        
        if (simmer.getTempMouseMode() == MouseModeEnum.MouseMode.DRAG_SELECTED && simmer.getMouseElm() instanceof GraphicElm) {
            dragX = p.getX();
            dragY = p.getY();
        } else {
            dragX = simmer.snapGrid(p.getX());
            dragY = simmer.snapGrid(p.getY());
        }
        if (simmer.getTempMouseMode() != MouseModeEnum.MouseMode.ADD_ELM) {
            return;
        }

        int x0 = simmer.snapGrid(p.getX());
        int y0 = simmer.snapGrid(p.getY());
        if (!simmer.getCircuitArea().contains(x0, y0))
            return;

        simmer.setDragElm(CircuitElementFactory.constructElement(simmer.getMouseModeStr(), x0, y0));
    }

    public void doDrag(Point p) {
        if (simmer.isMouseDragging()) {
            mouseDragged(p);
        } else {
            //simmer.setDragX(simmer.snapGrid(p.getX()));
            //simmer.setDragY(simmer.snapGrid(p.getY()));
            simmer.setDraggingPost(-1);
            simmer.setPlotXElm(null);
            simmer.setPlotYElm(null);
        }
    }

    public void stopDrag() {
        dragStarted = false;
        simmer.setMouseDragging(false);
        simmer.setTempMouseMode(simmer.getMouseMode());
        simmer.setSelectedArea(null);
        simmer.setDragging(false);
        boolean circuitChanged = false;

        if (simmer.getHeldSwitchElm() != null) {
            simmer.getHeldSwitchElm().mouseUp();
            simmer.setHeldSwitchElm(null);
            circuitChanged = true;
        }

        if (simmer.getDragElm() != null) {
            // if the element is zero size then don't create it
            // IES - and disable any previous selection
            if (simmer.getDragElm().getX1() == simmer.getDragElm().getX2()
                    && simmer.getDragElm().getY1() == simmer.getDragElm().getY2()) {
                simmer.getDragElm().delete();

                if (simmer.getMouseMode() == MouseModeEnum.MouseMode.SELECT
                        || simmer.getMouseMode() == MouseModeEnum.MouseMode.DRAG_SELECTED)
                    simmer.getMainMenuBar().getEditMenu().doSelectNone();

            } else {
                simmer.getElmList().add(simmer.getDragElm());
                // fire component added

                circuitChanged = true;
            }
            simmer.setDragElm(null);
        }

        if (circuitChanged) {
            simmer.needAnalyze();
        }

        if (simmer.getDragElm() != null) {
            simmer.getDragElm().delete();
        }
        simmer.setDragElm(null);
    }

    private void mouseDragged(Point p) {
        Rectangle circuitArea = simmer.getCircuitArea();
        AbstractCircuitElement dragElm = simmer.getDragElm();
        AbstractCircuitElement mouseElm = simmer.getMouseElm();

        if (!circuitArea.contains(p.getX(), p.getY())) {
            return;
        }

        if (dragElm != null) {
            dragElm.drag(p.getX(), p.getY());
        }

        boolean success = true;
        switch (simmer.getTempMouseMode()) {
        case DRAG_ALL:
            dragAll(simmer.snapGrid(p.getX()), simmer.snapGrid(p.getY()));
            break;

        case DRAG_ROW:
            dragRow(simmer.snapGrid(p.getX()), simmer.snapGrid(p.getY()));
            break;

        case DRAG_COLUMN:
            dragColumn(simmer.snapGrid(p.getX()), simmer.snapGrid(p.getY()));
            break;

        case DRAG_POST:
            if (mouseElm != null)
                dragPost(simmer.snapGrid(p.getX()), simmer.snapGrid(p.getY()));
            break;

        case SELECT:
            if (mouseElm == null)
                selectArea(p.getX(), p.getY());
            else {
                simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_SELECTED);
                success = dragSelected(p.getX(), p.getY());
            }

            break;

        case DRAG_SELECTED:
            success = dragSelected(p.getX(), p.getY());
            break;

        case ADD_ELM:
            break;
        }

        simmer.setDragging(true);

        if (success) {
            if (simmer.getTempMouseMode() == MouseModeEnum.MouseMode.DRAG_SELECTED && mouseElm instanceof GraphicElm) {
                dragX = p.getX();
                dragY = p.getY();
            } else {
                dragX = simmer.snapGrid(p.getX());
                dragY = simmer.snapGrid(p.getY());
            }
        }
    }

    private boolean dragSelected(int x, int y) {
        boolean me = false;
        AbstractCircuitElement mouseElm = simmer.getMouseElm();
        List<AbstractCircuitElement> elmList = simmer.getElmList();

        if (mouseElm != null && !mouseElm.isSelected())
            mouseElm.setSelected(me = true);

        // snap grid, unless we're only dragging text elements
        int i;
        for (i = 0; i != elmList.size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.isSelected() && !(ce instanceof GraphicElm))
                break;
        }
        if (i != elmList.size()) {
            x = simmer.snapGrid(x);
            y = simmer.snapGrid(y);
        }

        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0) {
            // don't leave mouseElm selected if we selected it above
            if (me)
                mouseElm.setSelected(false);
            return false;
        }
        boolean allowed = true;

        // check if moves are allowed
        for (i = 0; allowed && i != elmList.size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.isSelected() && !ce.allowMove(dx, dy))
                allowed = false;
        }

        if (allowed) {
            for (i = 0; i != elmList.size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);

                if (ce.isSelected())
                    ce.move(dx, dy);
            }

            simmer.needAnalyze();
        }

        // don't leave mouseElm selected if we selected it above
        if (me)
            mouseElm.setSelected(false);

        return allowed;
    }

    private void dragRow(int x, int y) {
        int dy = y - dragY;
        if (dy == 0)
            return;
        int i;
        for (i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.getY1() == dragY)
                ce.movePoint(0, 0, dy);
            if (ce.getY2() == dragY)
                ce.movePoint(1, 0, dy);
        }

        removeZeroLengthElements();
    }

    private void dragPost(int x, int y) {
        int draggingPost = simmer.getDraggingPost();
        AbstractCircuitElement mouseElm = simmer.getMouseElm();

        if (draggingPost == -1) {
            draggingPost = (MathUtil.distanceSq(mouseElm.getX1(), mouseElm.getY1(), x, y) > MathUtil
                    .distanceSq(mouseElm.getX2(), mouseElm.getY2(), x, y)) ? 1 : 0;
        }
        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0)
            return;
        mouseElm.movePoint(draggingPost, dx, dy);
        simmer.needAnalyze();
    }

    private void dragColumn(int x, int y) {
        int dx = x - dragX;
        if (dx == 0)
            return;
        int i;
        for (i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.getX1() == dragX)
                ce.movePoint(0, dx, 0);
            if (ce.getX2() == dragX)
                ce.movePoint(1, dx, 0);
        }
        removeZeroLengthElements();
    }

    private void dragAll(int x, int y) {
        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0)
            return;
        int i;
        for (i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            ce.move(dx, dy);
        }
        removeZeroLengthElements();
    }

    private void selectArea(int x, int y) {
        int x1 = MathUtil.min(x, initDragX);
        int x2 = MathUtil.max(x, initDragX);
        int y1 = MathUtil.min(y, initDragY);
        int y2 = MathUtil.max(y, initDragY);
        // selectedArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);

        simmer.setSelectedArea(new Rectangle(x1, y1, x2 - x1, y2 - y1));
        for (int i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            ce.selectRect(simmer.getSelectedArea());
        }
    }

    private void removeZeroLengthElements() {
        int i;
        // boolean changed = false;
        for (i = simmer.getElmList().size() - 1; i >= 0; i--) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.getX1() == ce.getX2() && ce.getY1() == ce.getY2()) {
                simmer.getElmList().remove(i);
                // fire component removed event
                // {source: simmer, component: elmList.getElementAt(i)}
                ce.delete();
                // changed = true;
            }
        }
        simmer.needAnalyze();
    }
}
