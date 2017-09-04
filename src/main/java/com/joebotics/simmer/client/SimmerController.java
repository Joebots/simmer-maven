package com.joebotics.simmer.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.*;
import com.joebotics.simmer.client.gui.dialog.AboutBox;
import com.joebotics.simmer.client.gui.dialog.ImportFromTextDialog;
import com.joebotics.simmer.client.gui.menu.ScrollValuePopup;
import com.joebotics.simmer.client.gui.util.Display;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Rectangle;
import com.joebotics.simmer.client.util.CircuitElementFactory;
import com.joebotics.simmer.client.util.MathUtil;
import com.joebotics.simmer.client.util.MessageI18N;
import com.joebotics.simmer.client.util.MouseModeEnum;

import java.util.List;

public class SimmerController implements MouseDownHandler, MouseWheelHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler,
    TouchCancelHandler, TouchEndHandler, TouchMoveHandler, TouchStartHandler,
    ClickHandler, DoubleClickHandler,
    ContextMenuHandler, Event.NativePreviewHandler {
    private final Simmer simmer;
    private boolean dragStarted = false;

    public SimmerController(Simmer simmer) {
        this.simmer = simmer;
    }// public void mouseClicked(MouseEvent e) {

    public void onClick(ClickEvent e) {
        e.preventDefault();

        if ((e.getNativeButton() == NativeEvent.BUTTON_MIDDLE))
            scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), 0);
    }

    public void onContextMenu(ContextMenuEvent e) {

        e.preventDefault();
        int x, y;
        simmer.setSelectedCircuitElement(simmer.getMouseElm());
        simmer.setMenuScope(-1);

        if (simmer.getScopeSelected() != -1) {

            MenuBar m = simmer.getScope(simmer.getScopeSelected()).getMenu();
            simmer.setMenuScope(simmer.getScopeSelected());

            if (m != null) {
                simmer.setContextPanel(new PopupPanel(true));
                simmer.getContextPanel().add(m);
                y = Math.max(0, Math.min(e.getNativeEvent().getClientY(), simmer.getCv().getCoordinateSpaceHeight() - 400));
                simmer.getContextPanel().setPopupPosition(e.getNativeEvent().getClientX(), y);
                simmer.getContextPanel().show();
            }
        } else if (simmer.getMouseElm() != null) {
            simmer.getElmScopeMenuItem().setEnabled(simmer.getMouseElm().canViewInScope());
            simmer.getElmEditMenuItem().setEnabled(simmer.getMouseElm().getEditInfo(0) != null);
            simmer.setContextPanel(new PopupPanel(true));
            simmer.getContextPanel().add(simmer.getElmMenuBar());
            simmer.getContextPanel().setPopupPosition(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY());
            simmer.getContextPanel().show();
        } else {
            simmer.doMainMenuChecks();
            simmer.setContextPanel(new PopupPanel(true));
            simmer.getContextPanel().add(simmer.getPopupDrawMenu());
            x = Math.max(0, Math.min(e.getNativeEvent().getClientX(), simmer.getCv().getCoordinateSpaceWidth() - 400));
            y = Math.max(0, Math.min(e.getNativeEvent().getClientY(), simmer.getCv().getCoordinateSpaceHeight() - 450));
            simmer.getContextPanel().setPopupPosition(x, y);
            simmer.getContextPanel().show();
        }
    }

    public void onDoubleClick(DoubleClickEvent e) {
        e.preventDefault();

        // if (!didSwitch && mouseElm != null)
        if (simmer.getMouseElm() != null)
            simmer.getMainMenuBar().getEditMenu().doEdit(simmer.getMouseElm());
    }

    public void onMouseDown(MouseDownEvent e) {
        e.preventDefault();

        // IES - hack to only handle left button events in the web version.
        if (e.getNativeButton() != NativeEvent.BUTTON_LEFT)
            return;

        simmer.setMouseDragging(true);

        if( dragStarted == false ){
            dragStarted = true;
            simmer.getMainMenuBar().getEditMenu().pushUndo();
        }

        if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            // // left mouse
            simmer.setTempMouseMode(simmer.getMouseMode());
            // if ((ex & MouseEvent.ALT_DOWN_MASK) != 0 &&
            // (ex & MouseEvent.META_DOWN_MASK) != 0)
            if (e.isAltKeyDown() && e.isMetaKeyDown())
                simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_COLUMN);
                // else if ((ex & MouseEvent.ALT_DOWN_MASK) != 0 &&
                // (ex & MouseEvent.SHIFT_DOWN_MASK) != 0)
            else if (e.isAltKeyDown() && e.isShiftKeyDown())
                simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_ROW);
                // else if ((ex & MouseEvent.SHIFT_DOWN_MASK) != 0)
            else if (e.isShiftKeyDown())
                simmer.setTempMouseMode(MouseModeEnum.MouseMode.SELECT);
                // else if ((ex & MouseEvent.ALT_DOWN_MASK) != 0)
            else if (e.isAltKeyDown())
                simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_ALL);
            else if (e.isControlKeyDown() || e.isMetaKeyDown())
                simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_POST);
        }

        // IES - Grab resize handles in select mode if they are far enough apart
        // and you are on top of them
        if (simmer.getTempMouseMode() == MouseModeEnum.MouseMode.SELECT
                && simmer.getMouseElm() != null
                && MathUtil.distanceSq(simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1(), simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) >= 256
                && (MathUtil.distanceSq(e.getX(), e.getY(), simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1()) <= Display.POSTGRABSQ || MathUtil.distanceSq(e.getX(), e.getY(), simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) <= Display.POSTGRABSQ)
                && !simmer.anySelectedButMouse())
            simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_POST);

        if (simmer.getTempMouseMode() != MouseModeEnum.MouseMode.SELECT && simmer.getTempMouseMode() != MouseModeEnum.MouseMode.DRAG_SELECTED)
            simmer.getMainMenuBar().getEditMenu().doSelectNone();

        if (simmer.doSwitch(e.getX(), e.getY())) {
            return;
        }

        simmer.getMainMenuBar().getEditMenu().pushUndo();
        simmer.setInitDragX(e.getX());
        simmer.setInitDragY(e.getY());
        simmer.setDragging(true);

        if (simmer.getTempMouseMode() != MouseModeEnum.MouseMode.ADD_ELM) {
            return;
        }

        int x0 = snapGrid(e.getX());
        int y0 = snapGrid(e.getY());
        if (!simmer.getCircuitArea().contains(x0, y0))
            return;

        simmer.setDragElm(CircuitElementFactory.constructElement(simmer.getMouseModeStr(), x0, y0));
    }

    public native void log(String message) /*-{
        $wnd.console.log(message);
    }-*/;

    public int snapGrid(int x) {
        return (x + simmer.getGridRound()) & simmer.getGridMask();
    }

    public boolean dragSelected(int x, int y) {
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
            x = snapGrid(x);
            y = snapGrid(y);
        }

        int dx = x - simmer.getDragX();
        int dy = y - simmer.getDragY();
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

    public void dragRow(int x, int y) {
        int dy = y - simmer.getDragY();
        if (dy == 0)
            return;
        int i;
        for (i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.getY1() == simmer.getDragY())
                ce.movePoint(0, 0, dy);
            if (ce.getY2() == simmer.getDragY())
                ce.movePoint(1, 0, dy);
        }
        
        removeZeroLengthElements();
    }

    public void selectArea(int x, int y) {
        int initDragX = simmer.getInitDragX();
        int initDragY = simmer.getInitDragY();

        int x1 = MathUtil.min(x, initDragX);
        int x2 = MathUtil.max(x, initDragX);
        int y1 = MathUtil.min(y, initDragY);
        int y2 = MathUtil.max(y, initDragY);
//        selectedArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);

        simmer.setSelectedArea(new Rectangle(x1, y1, x2-x1, y2-y1));
        for (int i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            ce.selectRect(simmer.getSelectedArea());
        }
    }

    public void dragPost(int x, int y) {
        int draggingPost = simmer.getDraggingPost();
        AbstractCircuitElement mouseElm  = simmer.getMouseElm();

        if (draggingPost == -1) {
            draggingPost = (MathUtil.distanceSq(mouseElm.getX1(), mouseElm.getY1(), x, y) > MathUtil.distanceSq(mouseElm.getX2(), mouseElm.getY2(), x, y)) ? 1 : 0;
        }
        int dx = x - simmer.getDragX();
        int dy = y - simmer.getDragY();
        if (dx == 0 && dy == 0)
            return;
        mouseElm.movePoint(draggingPost, dx, dy);
        simmer.needAnalyze();
    }

    public void dragColumn(int x, int y) {
        int dragX = simmer.getDragX();
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

    public void dragAll(int x, int y) {
        int dragX = simmer.getDragX();
        int dragY = simmer.getDragY();

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

    public void mouseDragged(MouseMoveEvent e) {
        // ignore right mouse button with no modifiers (needed on PC)
        if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            if (!(e.isMetaKeyDown() || e.isShiftKeyDown() || e.isControlKeyDown() || e.isAltKeyDown()))
                return;
        }

        Rectangle circuitArea = simmer.getCircuitArea();
        AbstractCircuitElement dragElm = simmer.getDragElm();
        AbstractCircuitElement mouseElm = simmer.getMouseElm();

        if (!circuitArea.contains(e.getX(), e.getY()))
            return;

        if (dragElm != null)
            dragElm.drag(e.getX(), e.getY());

        boolean success = true;
        switch (simmer.getTempMouseMode()) {

            case DRAG_ALL:
                dragAll(snapGrid(e.getX()), snapGrid(e.getY()));
                break;

            case DRAG_ROW:
                dragRow(snapGrid(e.getX()), snapGrid(e.getY()));
                break;

            case DRAG_COLUMN:
                dragColumn(snapGrid(e.getX()), snapGrid(e.getY()));
                break;

            case DRAG_POST:
                if (mouseElm != null)
                    dragPost(snapGrid(e.getX()), snapGrid(e.getY()));
                break;

            case SELECT:
                if (mouseElm == null)
                    selectArea(e.getX(), e.getY());
                else {
                    simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_SELECTED);
                    success = dragSelected(e.getX(), e.getY());
                }

                break;

            case DRAG_SELECTED:
                success = dragSelected(e.getX(), e.getY());
                break;

            case ADD_ELM:
                break;
        }

        simmer.setDragging(true);

        if (success) {
            if (simmer.getTempMouseMode() == MouseModeEnum.MouseMode.DRAG_SELECTED && mouseElm instanceof GraphicElm) {
                simmer.setDragX(e.getX());
                simmer.setDragY(e.getY());
            } else {
                simmer.setDragX(snapGrid(e.getX()));
                simmer.setDragY(snapGrid(e.getY()));
            }
        }
    }

    public void onMouseMove(MouseMoveEvent e) {
        e.preventDefault();

        if (simmer.isMouseDragging()) {
            mouseDragged(e);
            return;
        }

        AbstractCircuitElement newMouseElm = null;
        int x = e.getX();
        int y = e.getY();
        simmer.setDragX(snapGrid(x));
        simmer.setDragY(snapGrid(y));
        simmer.setDraggingPost(-1);
        int i;

        simmer.setMousePost(-1);
        simmer.setPlotXElm(null);
        simmer.setPlotYElm(null);

        if (simmer.getMouseElm() != null && (MathUtil.distanceSq(x, y, simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1()) <= Display.POSTGRABSQ || MathUtil.distanceSq(x, y, simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) <= Display.POSTGRABSQ)) {
            newMouseElm = simmer.getMouseElm();
        } else {
            int bestDist = 100000;
            int bestArea = 100000;
            for (i = 0; i != simmer.getElmList().size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);
                if (ce.getBoundingBox().contains(x, y)) {
                    int j;
                    int area = ce.getBoundingBox().width * ce.getBoundingBox().height;
                    int jn = ce.getPostCount();

                    if (jn > 2)
                        jn = 2;

                    for (j = 0; j != jn; j++) {
                        Point pt = ce.getPost(j);
                        int dist = MathUtil.distanceSq(x, y, pt.getX(), pt.getY());

                        // if multiple elements have overlapping bounding boxes,
                        // we prefer selecting elements that have posts close
                        // to the mouse pointer and that have a small bounding
                        // box area.
                        if (dist <= bestDist && area <= bestArea) {
                            bestDist = dist;
                            bestArea = area;
                            newMouseElm = ce;
                        }
                    }
                    if (ce.getPostCount() == 0)
                        newMouseElm = ce;
                }
            } // for
        }
        simmer.setScopeSelected(-1);
        if (newMouseElm == null) {
            for (i = 0; i != simmer.getScopeCount(); i++) {
                Scope s = simmer.getScopes()[i];
                if (s.getRect().contains(x, y)) {
                    newMouseElm = s.getElm();
                    if (s.isPlotXY()) {
                        simmer.setPlotXElm(s.getElm());
                        simmer.setPlotYElm(s.getyElm());
                    }
                    simmer.setScopeSelected(i);
                }
            }
            // // the mouse pointer was not in any of the bounding boxes, but we
            // // might still be close to a post
            for (i = 0; i != simmer.getElmList().size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);
                if (simmer.getMouseMode() == MouseModeEnum.MouseMode.DRAG_POST) {
                    if (MathUtil.distanceSq(ce.getX1(), ce.getY1(), x, y) < 26) {
                        newMouseElm = ce;
                        break;
                    }
                    if (MathUtil.distanceSq(ce.getX2(), ce.getY2(), x, y) < 26) {
                        newMouseElm = ce;
                        break;
                    }
                }
                int j;
                int jn = ce.getPostCount();
                for (j = 0; j != jn; j++) {
                    Point pt = ce.getPost(j);
                    // int dist = distanceSq(x, y, pt.x, pt.y);
                    if (MathUtil.distanceSq(pt.getX(), pt.getY(), x, y) < 26) {
                        newMouseElm = ce;
                        simmer.setMousePost(j);
                        break;
                    }
                }
            }
        } else {
            simmer.setMousePost(-1);
            // look for post close to the mouse pointer
            for (i = 0; i != newMouseElm.getPostCount(); i++) {
                Point pt = newMouseElm.getPost(i);
                if (MathUtil.distanceSq(pt.getX(), pt.getY(), x, y) < 26)
                    simmer.setMousePost(i);
            }
        }
        // if (mouseElm != origMouse)
        // cv.repaint();
        simmer.setMouseElm(newMouseElm);
    }

    public void onMouseOut(MouseOutEvent e) {
        simmer.setScopeSelected(-1);
        simmer.setMouseElm(null);
        simmer.setPlotXElm(null);
        simmer.setPlotYElm(null);
    }

    public void onMouseUp(MouseUpEvent e) {
        e.preventDefault();

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
            if (simmer.getDragElm().getX1() == simmer.getDragElm().getX2() && simmer.getDragElm().getY1() == simmer.getDragElm().getY2()) {
                simmer.getDragElm().delete();

                if (simmer.getMouseMode() == MouseModeEnum.MouseMode.SELECT || simmer.getMouseMode() == MouseModeEnum.MouseMode.DRAG_SELECTED)
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

        if (simmer.getDragElm() != null){
            simmer.getDragElm().delete();
        }

        simmer.setDragElm(null);
        // cv.repaint();
    }

    public void onMouseWheel(MouseWheelEvent e) {
        e.preventDefault();
        scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), e.getDeltaY());
        if (simmer.getMouseElm() instanceof MouseWheelHandler)
            ((MouseWheelHandler) simmer.getMouseElm()).onMouseWheel(e);
    }

    public void onPreviewNativeEvent(Event.NativePreviewEvent e) {
        int cc = e.getNativeEvent().getCharCode();
        int t = e.getTypeInt();
        int code = e.getNativeEvent().getKeyCode();

        if (dialogIsShowing()) {
            if (Simmer.getScrollValuePopup() != null && Simmer.getScrollValuePopup().isShowing() && (t & Event.ONKEYDOWN) != 0) {
                if (code == KeyCodes.KEY_ESCAPE || code == KeyCodes.KEY_SPACE)
                    Simmer.getScrollValuePopup().close(false);
                if (code == KeyCodes.KEY_ENTER)
                    Simmer.getScrollValuePopup().close(true);
            }
            if (simmer.getEditDialog() != null && simmer.getEditDialog().isShowing() && (t & Event.ONKEYDOWN) != 0) {
                if (code == KeyCodes.KEY_ENTER) {
                    simmer.getEditDialog().apply();
                    simmer.getEditDialog().close();
                }
            }
            return;
        }
        if ((t & Event.ONKEYDOWN) != 0) {

            if (code == KeyCodes.KEY_BACKSPACE || code == KeyCodes.KEY_DELETE) {
                simmer.getMainMenuBar().getEditMenu().doDelete();
                e.cancel();
            }

            if (code == KeyCodes.KEY_ESCAPE) {
                simmer.setMouseMode(MouseModeEnum.MouseMode.SELECT);
                simmer.setMouseModeStr(MessageI18N.getMessage("Select"));
                simmer.setTempMouseMode(simmer.getMouseMode());
                e.cancel();
            }

            if (e.getNativeEvent().getCtrlKey() || e.getNativeEvent().getMetaKey()) {
                if (code == KeyCodes.KEY_C) {
                    menuPerformed("key", "copy");
                    e.cancel();
                }
                if (code == KeyCodes.KEY_X) {
                    menuPerformed("key", "cut");
                    e.cancel();
                }
                if (code == KeyCodes.KEY_V) {
                    menuPerformed("key", "paste");
                    e.cancel();
                }
                if (code == KeyCodes.KEY_Z) {
                    menuPerformed("key", "undo");
                    e.cancel();
                }
                if (code == KeyCodes.KEY_Y) {
                    menuPerformed("key", "redo");
                    e.cancel();
                }
                if (code == KeyCodes.KEY_A) {
                    menuPerformed("key", "selectAll");
                    e.cancel();
                }
            }
        }
        if ((t & Event.ONKEYPRESS) != 0) {
            if (cc > 32 && cc < 127) {
                String c = simmer.getShortcuts()[cc];
                e.cancel();

                if (c == null)
                    return;

                simmer.setMouseMode(MouseModeEnum.MouseMode.ADD_ELM);
                simmer.setMouseModeStr(c);
                simmer.setTempMouseMode(simmer.getMouseMode());
            }
            if (cc == 32) {
                simmer.setMouseMode(MouseModeEnum.MouseMode.SELECT);
                simmer.setMouseModeStr(MessageI18N.getMessage("Select"));
                simmer.setTempMouseMode(simmer.getMouseMode());
                e.cancel();
            }
        }
    }

    public void menuPerformed(String menu, String item) {

        log("menu: " + menu + "\titem: " + item);

        if (item == "about")
            simmer.setAboutBox(new AboutBox(Launcher.versionString));

        if (item == "importfromlocalfile") {
            simmer.getMainMenuBar().getEditMenu().pushUndo();
            simmer.getLoadFileInput().click();
        }

        if (item == "importfromtext") {
            simmer.setImportFromTextDialog( new ImportFromTextDialog(simmer) );
        }

        if (item == "exportasurl") {
            simmer.getFileOps().doExportAsUrl();
        }

        if (item == "exportaslocalfile")
            simmer.getFileOps().doExportAsLocalFile();

        if (item == "exportastext")
            simmer.getFileOps().doExportAsText();

//        if ((menu == "elm" || menu == "scopepop") && simmer.getContextPanel() != null)
//            simmer.getContextPanel().hide();

        if (menu == "options" && item == "other")
            simmer.getMainMenuBar().getEditMenu().doEdit(new EditOptions(simmer));

        if (item == "undo")
            simmer.getMainMenuBar().getEditMenu().doUndo();

        if (item == "redo")
            simmer.getMainMenuBar().getEditMenu().doRedo();

        if (item == "cut") {
            if (menu != "elm")
                simmer.setMouseElm(null);

            simmer.getMainMenuBar().getEditMenu().doCut();
        }
        if (item == "copy") {
            if (menu != "elm")
                simmer.setMouseElm(null);

            simmer.getMainMenuBar().getEditMenu().doCopy();
        }

        if (item == "paste")
            simmer.getMainMenuBar().getEditMenu().doPaste();

        if (item == "selectAll")
            simmer.getMainMenuBar().getEditMenu().doSelectAll();

        if (item == "selectNone")
            simmer.getMainMenuBar().getEditMenu().doSelectNone();

        if (item == "centrecircuit") {
            simmer.getMainMenuBar().getEditMenu().pushUndo();
            simmer.centreCircuit();
        }
        if (item == "stackAll")
            simmer.stackAll();

        if (item == "unstackAll")
            simmer.unstackAll();

        if (menu == "elm" && item == "edit")
            simmer.getMainMenuBar().getEditMenu().doEdit(simmer.getSelectedCircuitElement());

        if (item == "delete") {
            if (menu == "elm")
                simmer.setSelectedCircuitElement(null);

            simmer.getMainMenuBar().getEditMenu().doDelete();
        }

        if (item == "viewInScope" && simmer.getSelectedCircuitElement() != null) {
            int i;
            for (i = 0; i != simmer.getScopeCount(); i++){
                if (simmer.getScope(i).getElm() == null){
                    break;
                }
            }

            if (i == simmer.getScopeCount()) {

                if (simmer.getScopeCount() == simmer.getScopes().length)
                    return;

                simmer.setScopeCount(simmer.getScopeCount()+1);
                simmer.getScopes()[i] = new Scope(simmer);
                simmer.getScopes()[i].setPosition(i);
                // handleResize();
            }

            simmer.getScope(i).setElm(simmer.getSelectedCircuitElement());
        }
        if (menu == "scopepop") {
            simmer.getMainMenuBar().getEditMenu().pushUndo();
            if (item == "remove")
                simmer.getScope(simmer.getMenuScope()).setElm(null);
            if (item == "speed2")
                simmer.getScope(simmer.getMenuScope()).speedUp();
            if (item == "speed1/2")
                simmer.getScope(simmer.getMenuScope()).slowDown();
            if (item == "scale")
                simmer.getScope(simmer.getMenuScope()).adjustScale(.5);
            if (item == "maxscale")
                simmer.getScope(simmer.getMenuScope()).adjustScale(1e-50);
            if (item == "stack")
                simmer.stackScope(simmer.getMenuScope());
            if (item == "unstack")
                simmer.unstackScope(simmer.getMenuScope());
            if (item == "selecty")
                simmer.getScope(simmer.getMenuScope()).selectY();
            if (item == "reset")
                simmer.getScope(simmer.getMenuScope()).resetGraph();
            if (item.indexOf("show") == 0 || item == "plotxy")
                simmer.getScope(simmer.getMenuScope()).handleMenu(item);
            // cv.repaint();
        }
        if (menu == "circuits" && item.indexOf("setup ") == 0) {
            simmer.getMainMenuBar().getEditMenu().pushUndo();
            simmer.getFileOps().readSetupFile(item.substring(6), "", true);
        }

        // IES: Moved from itemStateChanged()
        if (menu == "main") {
            if (simmer.getContextPanel() != null)
                simmer.getContextPanel().hide();
            // MenuItem mmi = (MenuItem) mi;
            // int prevMouseMode = mouseMode;
            simmer.setMouseMode(MouseModeEnum.MouseMode.ADD_ELM);
            String s = item;
            if (s.length() > 0)
                simmer.setMouseModeStr(s);
            if (s.compareTo("DragAll") == 0)
                simmer.setMouseMode(MouseModeEnum.MouseMode.DRAG_ALL);
            else if (s.compareTo("DragRow") == 0)
                simmer.setMouseMode(MouseModeEnum.MouseMode.DRAG_ROW);
            else if (s.compareTo("DragColumn") == 0)
                simmer.setMouseMode(MouseModeEnum.MouseMode.DRAG_COLUMN);
            else if (s.compareTo("DragSelected") == 0)
                simmer.setMouseMode(MouseModeEnum.MouseMode.DRAG_SELECTED);
            else if (s.compareTo("DragPost") == 0)
                simmer.setMouseMode(MouseModeEnum.MouseMode.DRAG_POST);
            else if (s.compareTo("Select") == 0)
                simmer.setMouseMode(MouseModeEnum.MouseMode.SELECT);

            //tempMouseMode = mouseMode;
            simmer.setTempMouseMode(simmer.getMouseMode());
        }
    }

    protected void scrollValues(int x, int y, int deltay) {
        AbstractCircuitElement mouseElm = simmer.getMouseElm();

        if (mouseElm != null && !dialogIsShowing()){
            if (mouseElm instanceof ResistorElm || mouseElm instanceof CapacitorElm || mouseElm instanceof InductorElm) {
                simmer.setScrollValuePopup(new ScrollValuePopup(x, y, deltay, mouseElm, simmer));
            }
        }
    }

    public boolean dialogIsShowing() {
        if (simmer.getEditDialog() != null && simmer.getEditDialog().isShowing())
            return true;
        if (simmer.getExportAsLocalFileDialog() != null && simmer.getExportAsLocalFileDialog().isShowing())
            return true;
        if (simmer.getExportAsTextDialog() != null && simmer.getExportAsTextDialog().isShowing())
            return true;
        if (simmer.getExportAsLocalFileDialog() != null && simmer.getExportAsLocalFileDialog().isShowing())
            return true;
        if (simmer.getContextPanel() != null && simmer.getContextPanel().isShowing())
            return true;
        if (simmer.getScrollValuePopup() != null && simmer.getScrollValuePopup().isShowing())
            return true;
        if (simmer.getAboutBox() != null && simmer.getAboutBox().isShowing())
            return true;
        if (simmer.getImportFromTextDialog() != null && simmer.getImportFromTextDialog().isShowing())
            return true;
        return false;
    }

    public void removeZeroLengthElements() {
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

    @Override
    public void onTouchStart(TouchStartEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTouchCancel(TouchCancelEvent event) {
        // TODO Auto-generated method stub
        
    }
}