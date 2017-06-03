package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.dialog.EditDialog;
import com.joebotics.simmer.client.gui.Editable;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.gui.util.Rectangle;
import com.joebotics.simmer.client.util.MessageI18N;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.gwt.user.client.Window.*;

/**
 * Created by joe on 7/16/16.
 */
public class EditMenu extends MenuBar{

    private List<String> undoStack = new ArrayList<>();
    private List<String> redoStack = new ArrayList<>();
    private List<AbstractCircuitElement> selected = new ArrayList<>();
    private String clipboard;
    private Simmer simmer;

    private static final Logger lager = Logger.getLogger(EditMenu.class.getName());

    private MenuItem undoItem, redoItem, cutItem, copyItem, pasteItem, selectAllItem, optionsItem;

    public EditMenu(Simmer simmer){
        super(true);
        this.simmer = simmer;
        MenuBar m = this;

        final String edithtml = "<div style=\"display:inline-block;width:80px;\">";

        String sn = edithtml + "Undo</div>Ctrl-Z";
        m.addItem(undoItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "undo")));

        sn = edithtml + "Redo</div>Ctrl-Y";
        m.addItem(redoItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "redo")));
        m.addSeparator();

        sn = edithtml + "Cut</div>Ctrl-X";
        m.addItem(cutItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "cut")));
        sn = edithtml + "Copy</div>Ctrl-C";
        m.addItem(copyItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "copy")));
        sn = edithtml + "Paste</div>Ctrl-V";
        m.addItem(pasteItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "paste")));
        m.addSeparator();

        sn = edithtml + "Select All</div>Ctrl-A";
        m.addItem(selectAllItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "selectAll")));
        sn = edithtml + "Select None</div>";
        m.addItem( new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "selectNone")));
        m.addItem(new MenuItem(MessageI18N.getMessage("Centre_Circuit"), new MenuCommand("edit", "centrecircuit")));

        pasteItem.setEnabled(false);
    }

    private void enablePaste() {
        pasteItem.setEnabled(clipboard.length() > 0);
    }

    public void enableUndoRedo() {
        redoItem.setEnabled(redoStack.size() > 0);
        undoItem.setEnabled(undoStack.size() > 0);
    }

    public void pushUndo() {

        redoStack.clear();
        String circuit = simmer.getFileOps().dumpCircuit();

        if( undoStack.isEmpty() )
            undoStack.add(circuit);

        if(circuitHasChanged()){
            undoStack.add(circuit);
            enableUndoRedo();
        }
    }

    public boolean circuitHasChanged(){
        String circuit = simmer.getFileOps().dumpCircuit();
        return !undoStack.contains(circuit);
    }

    public String peekUndo(){
        return undoStack.isEmpty()?"":undoStack.get(undoStack.size()-1);
    }

    public void doUndo() {

        if (undoStack.size() == 0)
            return;

        redoStack.add(simmer.getFileOps().dumpCircuit());
        String s = undoStack.remove(undoStack.size() - 1);
        simmer.getFileOps().readSetup(s, false);
        enableUndoRedo();
    }

    public void doRedo() {
        String circuit = simmer.getFileOps().dumpCircuit();

        if (redoStack.size() == 0)
            return;

        undoStack.add(circuit);
        String s = redoStack.remove(redoStack.size() - 1);
        simmer.getFileOps().readSetup(s, false);
        enableUndoRedo();
    }

    public void doCopy() {
        int i;
        clipboard = "";
        setMenuSelection();
        for (i = simmer.getElmList().size() - 1; i >= 0; i--) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.isSelected())
                clipboard += ce.dump() + "\n";
        }
        enablePaste();
    }

    public void doCut() {
        int i;
        pushUndo();
        setMenuSelection();
        clipboard = "";
        for (i = simmer.getElmList().size() - 1; i >= 0; i--) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.isSelected()) {
                clipboard += ce.dump() + "\n";
                ce.delete();
                simmer.getElmList().remove(i);
            }
        }
        enablePaste();
        simmer.needAnalyze();
    }

    public void doDelete() {
        int i;
        pushUndo();
        setMenuSelection();
        boolean hasDeleted = false;

        for (i = simmer.getElmList().size() - 1; i >= 0; i--) {
            AbstractCircuitElement ce = simmer.getElm(i);
            if (ce.isSelected()) {
                ce.delete();
                simmer.getElmList().remove(i);
                hasDeleted = true;
            }
        }

        if (!hasDeleted) {
            for (i = simmer.getElmList().size() - 1; i >= 0; i--) {
                AbstractCircuitElement ce = simmer.getElm(i);
                if (ce == simmer.getMouseElm()) {
                    ce.delete();
                    simmer.getElmList().remove(i);
                    hasDeleted = true;
                    simmer.setMouseElm(null);
                    break;
                }
            }
        }

        if (hasDeleted)
            simmer.needAnalyze();
    }

    public void doEdit(Editable eable) {
        doSelectNone();
        pushUndo();

        if (simmer.getEditDialog() != null) {
            // requestFocus();
            simmer.getEditDialog().setVisible(false);
            simmer.setEditDialog(null);
        }

        simmer.setEditDialog(new EditDialog(eable, simmer));
        simmer.getEditDialog().show();
    }

    public void doPaste() {
        pushUndo();
        doSelectNone();
        int i;
        Rectangle oldbb = null;
        for (i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            Rectangle bb = ce.getBoundingBox();
            if (oldbb != null)
                oldbb = oldbb.union(bb);
            else
                oldbb = bb;
        }
        int oldsz = simmer.getElmList().size();
        simmer.getFileOps().readSetup(clipboard, true, false);

        // select new items
        Rectangle newbb = null;

        for (i = oldsz; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            ce.setSelected(true);
            Rectangle bb = ce.getBoundingBox();
            if (newbb != null)
                newbb = newbb.union(bb);
            else
                newbb = bb;
        }

        if (oldbb != null && newbb != null && oldbb.intersects(newbb)) {

            // find a place for new items
            int dx = 0, dy = 0;
            int spacew = simmer.getCircuitArea().width - oldbb.width - newbb.width;
            int spaceh = simmer.getCircuitArea().height - oldbb.height - newbb.height;
            if (spacew > spaceh)
                dx = simmer.getSimmerController().snapGrid(oldbb.x + oldbb.width - newbb.x + simmer.getGridSize());
            else
                dy = simmer.getSimmerController().snapGrid(oldbb.y + oldbb.height - newbb.y + simmer.getGridSize());
            for (i = oldsz; i != simmer.getElmList().size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);
                ce.move(dx, dy);
            }
            // center circuit
            // handleResize();
        }

        simmer.needAnalyze();
    }

    public void doSelectAll() {
        for (int i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            ce.setSelected(true);
        }
    }

    public void doSelectNone() {
        for (int i = 0; i != simmer.getElmList().size(); i++) {
            AbstractCircuitElement ce = simmer.getElm(i);
            ce.setSelected(false);
        }
    }

    public void setMenuSelection() {

        AbstractCircuitElement menuElm = simmer.getSelectedCircuitElement();

        if (menuElm != null) {

            if (menuElm.isSelected())
                return;

            this.doSelectNone();
            menuElm.setSelected(true);
        }
    }
}
