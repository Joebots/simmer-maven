package com.joebotics.simmer.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.EditOptions;
import com.joebotics.simmer.client.gui.Scope;
import com.joebotics.simmer.client.gui.dialog.AboutBox;
import com.joebotics.simmer.client.gui.dialog.ImportFromTextDialog;
import com.joebotics.simmer.client.gui.menu.ScrollValuePopup;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.MessageI18N;
import com.joebotics.simmer.client.util.MouseModeEnum;

public class SimmerController implements MouseDownHandler, MouseWheelHandler, MouseMoveHandler, MouseUpHandler,
        MouseOutHandler, TouchCancelHandler, TouchEndHandler, TouchMoveHandler, TouchStartHandler, ClickHandler,
        DoubleClickHandler, ContextMenuHandler, Event.NativePreviewHandler {
    private final Simmer simmer;
    private final CircuitElementFinder finder;
    private final CirciutElmDragHelper dragHelper;

    public SimmerController(Simmer simmer) {
        this.simmer = simmer;
        this.finder = new CircuitElementFinder(simmer);
        this.dragHelper = new CirciutElmDragHelper(simmer);
    }

    public void onClick(ClickEvent e) {
        e.preventDefault();

        Point p = new Point(e.getX(), e.getY());
        AbstractCircuitElement element = simmer.getMouseElm();

        if(element != null) {
            element.click(e);
        }

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
                y = Math.max(0,
                        Math.min(e.getNativeEvent().getClientY(), simmer.getCv().getCoordinateSpaceHeight() - 400));
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

    @Override
    public void onDoubleClick(DoubleClickEvent e) {
        e.preventDefault();

        // if (!didSwitch && mouseElm != null)
        if (simmer.getMouseElm() != null)
            simmer.getMainMenuBar().getEditMenu().doEdit(simmer.getMouseElm());
    }

    @Override
    public void onMouseDown(MouseDownEvent e) {
        e.preventDefault();
        Point p = new Point(e.getX(), e.getY());

        // IES - hack to only handle left button events in the web version.
        if (e.getNativeButton() != NativeEvent.BUTTON_LEFT)
            return;

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
        dragHelper.startDrag(p);
    }

    @Override
    public void onMouseMove(MouseMoveEvent e) {
        e.preventDefault();
        Point p = new Point(e.getX(), e.getY());

        // ignore right mouse button with no modifiers (needed on PC)
        if (simmer.isMouseDragging()) {
            if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT
                    && !(e.isMetaKeyDown() || e.isShiftKeyDown() || e.isControlKeyDown() || e.isAltKeyDown())) {
                return;
            }
            dragHelper.doDrag(p);
        } else {
            finder.selectElement(p);
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent e) {
        simmer.setScopeSelected(-1);
        simmer.setMouseElm(null);
        simmer.setPlotXElm(null);
        simmer.setPlotYElm(null);
    }

    @Override
    public void onMouseUp(MouseUpEvent e) {
        e.preventDefault();
        if (!simmer.isDragging()) {
            doSwitch();
        }
        dragHelper.stopDrag();
    }

    @Override
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
            if (Simmer.getScrollValuePopup() != null && Simmer.getScrollValuePopup().isShowing()
                    && (t & Event.ONKEYDOWN) != 0) {
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

        if (item == "about")
            simmer.setAboutBox(new AboutBox(Launcher.versionString));

        if (item == "importfromlocalfile") {
            simmer.getMainMenuBar().getEditMenu().pushUndo();
            simmer.getLoadFileInput().click();
        }

        if (item == "importfromtext") {
            simmer.setImportFromTextDialog(new ImportFromTextDialog(simmer));
        }

        if (item == "exportasurl") {
            simmer.getFileOps().doExportAsUrl();
        }

        if (item == "exportaslocalfile")
            simmer.getFileOps().doExportAsLocalFile();

        if (item == "exportastext")
            simmer.getFileOps().doExportAsText();

        // if ((menu == "elm" || menu == "scopepop") && simmer.getContextPanel()
        // != null)
        // simmer.getContextPanel().hide();

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
            for (i = 0; i != simmer.getScopeCount(); i++) {
                if (simmer.getScope(i).getElm() == null) {
                    break;
                }
            }

            if (i == simmer.getScopeCount()) {

                if (simmer.getScopeCount() == simmer.getScopes().length)
                    return;

                simmer.setScopeCount(simmer.getScopeCount() + 1);
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

            // tempMouseMode = mouseMode;
            simmer.setTempMouseMode(simmer.getMouseMode());
        }
    }

    protected void scrollValues(int x, int y, int deltay) {
        AbstractCircuitElement mouseElm = simmer.getMouseElm();

        if (mouseElm != null && !dialogIsShowing()) {
            if (mouseElm instanceof ResistorElm || mouseElm instanceof CapacitorElm
                    || mouseElm instanceof InductorElm) {
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
    
    private void doSwitch() {
        AbstractCircuitElement mouseElm = simmer.getMouseElm();
        if (mouseElm != null && mouseElm instanceof SwitchElm) {
            SwitchElm se = (SwitchElm) mouseElm;
            se.toggle();

            if (se.isMomentary()) {
                simmer.setHeldSwitchElm(se);
            }
            simmer.needAnalyze();
        }
    }

    @Override
    public void onTouchStart(TouchStartEvent e) {
        e.preventDefault();
        Touch touch = e.getTouches().get(0);
        Point p = new Point(touch.getClientX(), touch.getClientY());
        finder.selectElement(p);
        dragHelper.startDrag(p);
    }

    @Override
    public void onTouchMove(TouchMoveEvent e) {
        e.preventDefault();
        Touch touch = e.getTouches().get(0);
        Point p = new Point(touch.getClientX(), touch.getClientY());
        dragHelper.doDrag(p);
    }

    @Override
    public void onTouchEnd(TouchEndEvent e) {
        e.preventDefault();
        if (!simmer.isDragging()) {
            doSwitch();
        }
        dragHelper.stopDrag();
    }

    @Override
    public void onTouchCancel(TouchCancelEvent e) {
        e.preventDefault();
        dragHelper.stopDrag();
    }
}
