package com.joebotics.simmer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.dialog.ExportAsLocalFileDialog;
import com.joebotics.simmer.client.gui.dialog.ExportAsTextDialog;
import com.joebotics.simmer.client.gui.dialog.ExportAsUrlDialog;
import com.joebotics.simmer.client.gui.Scope;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.CircuitElementFactory;
import com.joebotics.simmer.client.util.HintTypeEnum;
import com.joebotics.simmer.client.util.MessageI18N;
import com.joebotics.simmer.client.util.StringTokenizer;

public class FileOps {
    private Simmer simmer;

    public FileOps(Simmer simmer) {
        this.simmer = simmer;
    }// this is the file generation logic!  :)

    public void doExportAsLocalFile() {
        String dump = dumpCircuit();
        simmer.setExportAsLocalFileDialog(new ExportAsLocalFileDialog(dump));
        simmer.getExportAsLocalFileDialog().show();
    }

    public void doExportAsText() {
        String dump = dumpCircuit();
        simmer.setExportAsTextDialog(new ExportAsTextDialog(dump));
        simmer.getExportAsTextDialog().show();
    }

    public void doExportAsUrl() {
        String start[] = Window.Location.getHref().split("\\?");
        String dump = dumpCircuit();
        dump = dump.replace(' ', '+');
        dump = start[0] + "?cct=" + URL.encode(dump);
        simmer.setExportAsUrlDialog(new ExportAsUrlDialog(dump));
        simmer.getExportAsUrlDialog().show();
    }

    public String dumpCircuit() {
        int i;
        int f = (simmer.getMainMenuBar().getOptionsMenuBar().getDotsCheckItem().getState()) ? 1 : 0;
        f |= (simmer.getMainMenuBar().getOptionsMenuBar().getSmallGridCheckItem().getState()) ? 2 : 0;
        f |= (simmer.getMainMenuBar().getOptionsMenuBar().getVoltsCheckItem().getState()) ? 0 : 4;
        f |= (simmer.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState()) ? 8 : 0;
        f |= (simmer.getMainMenuBar().getOptionsMenuBar().getShowValuesCheckItem().getState()) ? 0 : 16;
        // 32 = linear scale in afilter
        String dump = "$ " + f + " " + simmer.getTimeStep() + " " + simmer.getIterCount() + " " + simmer.getSidePanel().getCurrentBar().getValue() + " " + AbstractCircuitElement.voltageRange + " " + simmer.getSidePanel().getPowerBar().getValue() + "\n";

        log("elmList.size=" + simmer.getElmList().size() + "\tscope count:" + simmer.getScopeCount());

        for (i = 0; i != simmer.getElmList().size(); i++)
            dump += simmer.getElm(i).dump() + "\n";

        for (i = 0; i != simmer.getScopeCount(); i++) {
            String d = simmer.getScopes()[i].dump();
            if (d != null)
                dump += d + "\n";
        }
        if (simmer.getHintType() != HintTypeEnum.HintType.HINT_UNSET)
            dump += "h " + simmer.getHintType() + " " + simmer.getHintItem1() + " " + simmer.getHintItem2() + "\n";
        return dump;
    }

    protected void processSetupList(byte b[], int len, final boolean openDefault) {
        MenuBar currentMenuBar;
        MenuBar stack[] = new MenuBar[6];
        int stackptr = 0;
        currentMenuBar = new MenuBar(true);
        currentMenuBar.setAutoOpen(true);
        simmer.getMainMenuBar().addItem(MessageI18N.getMessage("Circuits"), currentMenuBar);
        stack[stackptr++] = currentMenuBar;
        int p;
        for (p = 0; p < len; ) {
            int l;
            for (l = 0; l != len - p; l++)
                if (b[l + p] == '\n') {
                    l++;
                    break;
                }
            String line = new String(b, p, l - 1);
            if (line.charAt(0) == '#')
                ;
            else if (line.charAt(0) == '+') {
                // MenuBar n = new Menu(line.substring(1));
                MenuBar n = new MenuBar(true);
                n.setAutoOpen(true);
                currentMenuBar.addItem(line.substring(1), n);
                currentMenuBar = stack[stackptr++] = n;
            } else if (line.charAt(0) == '-') {
                currentMenuBar = stack[--stackptr - 1];
            } else {
                int i = line.indexOf(' ');
                if (i > 0) {
                    String title = line.substring(i + 1);
                    boolean first = false;
                    if (line.charAt(0) == '>')
                        first = true;
                    String file = line.substring(first ? 1 : 0, i);
                    // menu.add(getMenuItem(title, MessageI18N.getMessage("setup_") + file));
                    currentMenuBar.addItem(new MenuItem(title, new MenuCommand("circuits", "setup " + file)));
                    if (first && simmer.getStartCircuit() == null) {
                        simmer.setStartCircuit(file);
                        simmer.setStartLabel(title);
                        if (openDefault && simmer.getStopMessage() == null)
                            readSetupFile(simmer.getStartCircuit(), simmer.getStartLabel(), true);
                    }
                }
            }
            p += l;
        }
    }

    public void readHint(StringTokenizer st) {
        simmer.setHintType(simmer.getHintType().getHintFromValue(new Integer(st.nextToken()).intValue()));
        simmer.setHintItem1(simmer.getHintType().getHintFromValue(new Integer(st.nextToken()).intValue()));
        simmer.setHintItem2(simmer.getHintType().getHintFromValue(new Integer(st.nextToken()).intValue()));
    }

    public void readOptions(StringTokenizer st) {
        int flags = new Integer(st.nextToken()).intValue();
        // IES - remove inteaction
        simmer.getMainMenuBar().getOptionsMenuBar().getDotsCheckItem().setState((flags & 1) != 0);
        simmer.getMainMenuBar().getOptionsMenuBar().getSmallGridCheckItem().setState((flags & 2) != 0);
        simmer.getMainMenuBar().getOptionsMenuBar().getVoltsCheckItem().setState((flags & 4) == 0);
        simmer.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().setState((flags & 8) == 8);
        simmer.getMainMenuBar().getOptionsMenuBar().getShowValuesCheckItem().setState((flags & 16) == 0);
        simmer.setTimeStep(new Double(st.nextToken()).doubleValue());
        double sp = new Double(st.nextToken()).doubleValue();
        int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
        // int sp2 = (int) (Math.log(sp)*24+1.5);
        simmer.getSidePanel().getSpeedBar().setValue(sp2);
        simmer.getSidePanel().getCurrentBar().setValue(new Integer(st.nextToken()).intValue());
        AbstractCircuitElement.voltageRange = new Double(st.nextToken()).doubleValue();

        try {
            simmer.getSidePanel().getPowerBar().setValue(new Integer(st.nextToken()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        simmer.setGrid();
    }

    public native void log(String message) /*-{
        $wnd.console.log(message);
    }-*/;

    public void getSetupList(final boolean openDefault) {

        String url = GWT.getModuleBaseURL();
        url = url.substring(0,url.indexOf("circuitjs1"));
        url = url +  "setuplist.txt" + "?v=" + Math.random();
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    GWT.log(MessageI18N.getMessage("File_Error_Response"), exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    // processing goes here
                    if (response.getStatusCode() == Response.SC_OK) {
                        String text = response.getText();
                        processSetupList(text.getBytes(), text.length(), openDefault);
                        // end or processing
                    } else
                        GWT.log(MessageI18N.getMessage("Bad_file_server_response") + response.getStatusText());
                }
            });
        } catch (RequestException e) {
            GWT.log(MessageI18N.getMessage("failed_file_reading"), e);
        }

        String s = "";
        if( s != null && s.isEmpty() && Character.isUpperCase(s.charAt(0))){

        }
    }

    public void readSetup(byte b[], int len, boolean retain, boolean centre) {

//        log("readSetup " + b.length );
        int i;
        if (!retain) {
            for (i = 0; i != simmer.getElmList().size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);
                ce.delete();
            }
            simmer.getElmList().removeAllElements();
            simmer.setHintType(HintTypeEnum.HintType.HINT_UNSET);
            simmer.setTimeStep(5e-6);
            simmer.getMainMenuBar().getOptionsMenuBar().getDotsCheckItem().setState(false);
            simmer.getMainMenuBar().getOptionsMenuBar().getSmallGridCheckItem().setState(false);
            simmer.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().setState(false);
            simmer.getMainMenuBar().getOptionsMenuBar().getVoltsCheckItem().setState(true);
            simmer.getMainMenuBar().getOptionsMenuBar().getShowValuesCheckItem().setState(true);
            simmer.setGrid();
            simmer.getSidePanel().getSpeedBar().setValue(117); // 57
            simmer.getSidePanel().getCurrentBar().setValue(50);
            simmer.getSidePanel().getPowerBar().setValue(50);
            AbstractCircuitElement.voltageRange = 5;
            simmer.setScopeCount(0);
        }
        // cv.repaint();
        for (int p = 0; p < len; ) {
            int l;
            int linelen = len - p; // IES - changed to allow the last line to
            // not end with a delim.
            for (l = 0; l != len - p; l++)
                if (b[l + p] == '\n' || b[l + p] == '\r') {
                    linelen = l++;
                    if (l + p < b.length && b[l + p] == '\n')
                        l++;
                    break;
                }
            String line = new String(b, p, linelen);
            StringTokenizer st = new StringTokenizer(line, " +\t\n\r\f");
            while (st.hasMoreTokens()) {
                String type = st.nextToken();
                int tint = type.charAt(0);
                try {
                    if (tint == 'o') {
                        Scope sc = new Scope(simmer);
                        sc.setPosition(simmer.getScopeCount());
                        sc.undump(st);
                        simmer.getScopes()[simmer.getScopeCount()] = sc;
                        simmer.setScopeCount(simmer.getScopeCount() + 1);
                        break;
                    }
                    if (tint == 'h') {
                        readHint(st);
                        break;
                    }
                    if (tint == '$') {
                        readOptions(st);
                        break;
                    }
                    if (tint == '%' || tint == '?' || tint == 'B') {
                        // ignore afilter-specific stuff
                        break;
                    }
                    if (tint >= '0' && tint <= '9')
                        tint = new Integer(type).intValue();

                    int x1 = new Integer(st.nextToken()).intValue();
                    int y1 = new Integer(st.nextToken()).intValue();
                    int x2 = new Integer(st.nextToken()).intValue();
                    int y2 = new Integer(st.nextToken()).intValue();
                    int f = new Integer(st.nextToken()).intValue();

                    AbstractCircuitElement newce = CircuitElementFactory.createCircuitElement(tint, x1, y1, x2, y2, f, st);

                    if (newce == null) {
                        System.out.println(MessageI18N.getMessage("unrecognized_dump_type_") + type);
                        break;
                    }
                    newce.setPoints();
                    simmer.getElmList().addElement(newce);

                } catch (Exception ee) {
                    ee.printStackTrace();
                    break;
                }
                break;
            }
            p += l;

        }
        simmer.getSidePanel().setPowerBarEnable();
        simmer.enableItems();
        // if (!retain)
        // handleResize(); // for scopes
        simmer.needAnalyze();

        if (centre)
            simmer.centreCircuit();
    }

    public void readSetup(String text, boolean centre) {
        readSetup(text, false, centre);
    }

    public void readSetup(String text, boolean retain, boolean centre) {
        readSetup(text.getBytes(), text.length(), retain, centre);
    }

    protected void readSetupFile(String str, String title, boolean centre) {
        simmer.setT(0);
        // try {
        // TODO: Maybe think about some better approach to cache management!
        String url = GWT.getModuleBaseURL();
        url = url.substring(0, url.indexOf("circuitjs1"));
        url = url + "circuits/" + str + "?v=" + Math.random();
        loadFileFromURL(url, centre);
    }

    public void createNewLoadFile() {
        // This is a hack to fix what IMHO is a bug in the <INPUT FILE element
        // reloading the same file doesn't create a change event so importing
        // the same file twice
        // doesn't work unless you destroy the original input element and
        // replace it with a new one
        int idx = simmer.getSidePanel().getWidgetIndex(simmer.getLoadFileInput());
        LoadFile newlf = new LoadFile(simmer);
        simmer.getSidePanel().insert(newlf, idx);
        simmer.getSidePanel().remove(idx + 1);
        simmer.setLoadFileInput(newlf);
    }

    public void loadFileFromURL(String url, final boolean centre) {
        log("loadFileFromUrl:" + url );
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    GWT.log(MessageI18N.getMessage("File_Error_Response"), exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == Response.SC_OK) {
                        String text = response.getText();
                        readSetup(text.getBytes(), text.length(), false, centre);
                    } else
                        GWT.log(MessageI18N.getMessage("Bad_file_server_response") + response.getStatusText());
                }
            });
        } catch (RequestException e) {
            GWT.log(MessageI18N.getMessage("failed_file_reading"), e);
        }

    }
}