package com.joebotics.simmer.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.CapacitorElm;
import com.joebotics.simmer.client.elcomp.InductorElm;
import com.joebotics.simmer.client.elcomp.ResistorElm;
import com.joebotics.simmer.client.gui.impl.*;
import com.joebotics.simmer.client.gui.util.Display;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.CircuitElementFactory;
import com.joebotics.simmer.client.util.MessageI18N;
import com.joebotics.simmer.client.util.MouseModeEnum;

public class SimmerController implements MouseDownHandler, MouseWheelHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, ClickHandler, DoubleClickHandler, ContextMenuHandler, Event.NativePreviewHandler {
    private final Simmer simmer;

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
        simmer.setMenuElm(simmer.getMouseElm());
        simmer.setMenuScope(-1);

        if (simmer.getScopeSelected() != -1) {
            MenuBar m = simmer.getScopes()[simmer.getScopeSelected()].getMenu();

            Window.alert(simmer.getScopeSelected() + " " + m);

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
            simmer.getContextPanel().add(simmer.getMainMenuBar());
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
            simmer.getEditMenu().doEdit(simmer.getMouseElm());
    }

    public void onMouseDown(MouseDownEvent e) {
        e.preventDefault();

        // IES - hack to only handle left button events in the web version.
        if (e.getNativeButton() != NativeEvent.BUTTON_LEFT)
            return;

        simmer.setMouseDragging(true);

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
                && simmer.distanceSq(simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1(), simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) >= 256
                && (simmer.distanceSq(e.getX(), e.getY(), simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1()) <= Display.POSTGRABSQ || simmer.distanceSq(e.getX(), e.getY(), simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) <= Display.POSTGRABSQ)
                && !simmer.anySelectedButMouse())
            simmer.setTempMouseMode(MouseModeEnum.MouseMode.DRAG_POST);

        if (simmer.getTempMouseMode() != MouseModeEnum.MouseMode.SELECT && simmer.getTempMouseMode() != MouseModeEnum.MouseMode.DRAG_SELECTED)
            simmer.getEditMenu().doSelectNone();

        if (simmer.doSwitch(e.getX(), e.getY())) {
            return;
        }

        simmer.getEditMenu().pushUndo();
        simmer.setInitDragX(e.getX());
        simmer.setInitDragY(e.getY());
        simmer.setDragging(true);
        if (simmer.getTempMouseMode() != MouseModeEnum.MouseMode.ADD_ELM) {
            return;
        }

        int x0 = simmer.snapGrid(e.getX());
        int y0 = simmer.snapGrid(e.getY());
        if (!simmer.getCircuitArea().contains(x0, y0))
            return;

        simmer.setDragElm(CircuitElementFactory.constructElement(simmer.getMouseModeStr(), x0, y0));
    }

    public void onMouseMove(MouseMoveEvent e) {
        e.preventDefault();
        if (simmer.isMouseDragging()) {
            simmer.mouseDragged(e);
            return;
        }
        // The following is in the original, but seems not to work/be needed for
        // GWT
        // if (e.getNativeButton()==NativeEvent.BUTTON_LEFT)
        // return;
        AbstractCircuitElement newMouseElm = null;
        int x = e.getX();
        int y = e.getY();
        simmer.setDragX(simmer.snapGrid(x));
        simmer.setDragY(simmer.snapGrid(y));
        simmer.setDraggingPost(-1);
        int i;
        // AbstractCircuitElement origMouse = mouseElm;

        simmer.setMousePost(-1);
        simmer.setPlotXElm(null);
        simmer.setPlotYElm(null);

        if (simmer.getMouseElm() != null && (simmer.distanceSq(x, y, simmer.getMouseElm().getX1(), simmer.getMouseElm().getY1()) <= Display.POSTGRABSQ || simmer.distanceSq(x, y, simmer.getMouseElm().getX2(), simmer.getMouseElm().getY2()) <= Display.POSTGRABSQ)) {
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
                        int dist = simmer.distanceSq(x, y, pt.getX(), pt.getY());

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
                    if (simmer.distanceSq(ce.getX1(), ce.getY1(), x, y) < 26) {
                        newMouseElm = ce;
                        break;
                    }
                    if (simmer.distanceSq(ce.getX2(), ce.getY2(), x, y) < 26) {
                        newMouseElm = ce;
                        break;
                    }
                }
                int j;
                int jn = ce.getPostCount();
                for (j = 0; j != jn; j++) {
                    Point pt = ce.getPost(j);
                    // int dist = distanceSq(x, y, pt.x, pt.y);
                    if (simmer.distanceSq(pt.getX(), pt.getY(), x, y) < 26) {
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
                if (simmer.distanceSq(pt.getX(), pt.getY(), x, y) < 26)
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
                    simmer.getEditMenu().doSelectNone();
            } else {
                simmer.getElmList().addElement(simmer.getDragElm());
                // fire component added

                circuitChanged = true;
            }
            simmer.setDragElm(null);
        }
        if (circuitChanged)
            simmer.needAnalyze();
        if (simmer.getDragElm() != null)
            simmer.getDragElm().delete();
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
        if (simmer.dialogIsShowing()) {
            if (Simmer.getScrollValuePopup() != null && Simmer.getScrollValuePopup().isShowing() && (t & Event.ONKEYDOWN) != 0) {
                if (code == KeyCodes.KEY_ESCAPE || code == KeyCodes.KEY_SPACE)
                    Simmer.getScrollValuePopup().close(false);
                if (code == KeyCodes.KEY_ENTER)
                    Simmer.getScrollValuePopup().close(true);
            }
            if (Simmer.getEditDialog() != null && Simmer.getEditDialog().isShowing() && (t & Event.ONKEYDOWN) != 0) {
                if (code == KeyCodes.KEY_ESCAPE)
                    Simmer.getEditDialog().closeDialog();
                if (code == KeyCodes.KEY_ENTER) {
                    Simmer.getEditDialog().apply();
                    Simmer.getEditDialog().closeDialog();
                }
            }
            return;
        }
        if ((t & Event.ONKEYDOWN) != 0) {

            if (code == KeyCodes.KEY_BACKSPACE || code == KeyCodes.KEY_DELETE) {
                simmer.getEditMenu().doDelete();
                e.cancel();
            }
            if (code == KeyCodes.KEY_ESCAPE) {
                simmer.setMouseMode(MouseModeEnum.MouseMode.SELECT);
                simmer.setMouseModeStr(MessageI18N.getLocale("Select"));
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
                simmer.setMouseModeStr(MessageI18N.getLocale("Select"));
                simmer.setTempMouseMode(simmer.getMouseMode());
                e.cancel();
            }
        }
    }

    public void menuPerformed(String menu, String item) {

        Window.alert("menu: " + menu + "\titem: " + item);

        if (item == "about")
            simmer.setAboutBox(new AboutBox(Launcher.versionString));

        if (item == "importfromlocalfile") {
            simmer.getEditMenu().pushUndo();
            simmer.getLoadFileInput().click();
        }

        if (item == "importfromtext") {
            simmer.setImportFromTextDialog( new ImportFromTextDialog(simmer) );
        }

        if (item == "exportasurl") {
            simmer.doExportAsUrl();
        }

        if (item == "exportaslocalfile")
            simmer.doExportAsLocalFile();

        if (item == "exportastext")
            simmer.doExportAsText();

        if ((menu == "elm" || menu == "scopepop") && simmer.getContextPanel() != null)
            simmer.getContextPanel().hide();

        if (menu == "options" && item == "other")
            simmer.getEditMenu().doEdit(new EditOptions(simmer));

        if (item == "undo")
            simmer.getEditMenu().doUndo();

        if (item == "redo")
            simmer.getEditMenu().doRedo();

        if (item == "cut") {
            if (menu != "elm")
                simmer.setMouseElm(null);

            simmer.getEditMenu().doCut();
        }
        if (item == "copy") {
            if (menu != "elm")
                simmer.setMouseElm(null);

            simmer.getEditMenu().doCopy();
        }

        if (item == "paste")
            simmer.getEditMenu().doPaste();

        if (item == "selectAll")
            simmer.getEditMenu().doSelectAll();

        if (item == "selectNone")
            simmer.getEditMenu().doSelectNone();

        if (item == "centrecircuit") {
            simmer.getEditMenu().pushUndo();
            simmer.centreCircuit();
        }
        if (item == "stackAll")
            simmer.stackAll();
        if (item == "unstackAll")
            simmer.unstackAll();
        if (menu == "elm" && item == "edit")
            simmer.getEditMenu().doEdit(simmer.getMenuElm());
        if (item == "delete") {
            if (menu == "elm")
                simmer.setMenuElm(null);

            simmer.getEditMenu().doDelete();
        }

        if (item == "viewInScope" && simmer.getMenuElm() != null) {
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

            simmer.getScope(i).setElm(simmer.getMenuElm());
        }
        if (menu == "scopepop") {
            simmer.getEditMenu().pushUndo();
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
            simmer.getEditMenu().pushUndo();
            simmer.readSetupFile(item.substring(6), "", true);
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

        if (mouseElm != null && !simmer.dialogIsShowing()){
            if (mouseElm instanceof ResistorElm || mouseElm instanceof CapacitorElm || mouseElm instanceof InductorElm) {
                simmer.setScrollValuePopup(new ScrollValuePopup(x, y, deltay, mouseElm, simmer));
            }
        }
    }

}