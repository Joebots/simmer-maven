package com.joebotics.simmer.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.Bgpio;
import com.joebotics.simmer.client.gui.Scope;
import com.joebotics.simmer.client.gui.dialog.ExportAsLocalFileDialog;
import com.joebotics.simmer.client.gui.dialog.ExportAsTextDialog;
import com.joebotics.simmer.client.gui.dialog.ExportAsUrlDialog;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.util.*;

public class FileOps {
    private Simmer simmer;

    private static Logger lager = Logger.getLogger(FileOps.class.getName());

    public FileOps(Simmer simmer) {
        this.simmer = simmer;
    }// this is the file generation logic! :)

    public String getCircuitUrl() {
        String dataAsBase64 = Base64Util.encodeString(dumpCircuit());
        return "data:application/octet-stream;name=document.pdf;base64," + dataAsBase64;
    }

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
        int f = (simmer.getOptions().getBoolean(OptionKey.SHOW_CURRENT)) ? 1 : 0;
        f |= (simmer.getOptions().getBoolean(OptionKey.SMALL_GRID)) ? 2 : 0;
        f |= (simmer.getOptions().getBoolean(OptionKey.SHOW_VOLTAGE)) ? 0 : 4;
        f |= (simmer.getOptions().getBoolean(OptionKey.SHOW_POWER)) ? 8 : 0;
        f |= (simmer.getOptions().getBoolean(OptionKey.SHOW_VALUES)) ? 0 : 16;
        // 32 = linear scale in afilter
        String dump = "$ " + f + " " + simmer.getCanvasContainer().getTimeStep() + " " + simmer.getCanvasContainer().getIterCount() + " "
                + simmer.getCanvasContainer().getOptions().getInteger(OptionKey.CURRENT_SPEED) + " " + AbstractCircuitElement.voltageRange
                + " " + simmer.getCanvasContainer().getSidePanel().getPowerBar().getValue() + "\n";

        for (i = 0; i != simmer.getCanvasContainer().getElmList().size(); i++)
            dump += simmer.getCanvasContainer().getElm(i).dump() + "\n";

        for (i = 0; i != simmer.getCanvasContainer().getScopeCount(); i++) {
            String d = simmer.getCanvasContainer().getScopes()[i].dump();
            if (d != null)
                dump += d + "\n";
        }
        if (simmer.getCanvasContainer().getHintType() != HintTypeEnum.HintType.HINT_UNSET)
            dump += "h " + simmer.getCanvasContainer().getHintType() + " " + simmer.getCanvasContainer().getHintItem1() + " " + simmer.getCanvasContainer().getHintItem2() + "\n";
        // Blockly blocks
        if (simmer.getBlocklyXml() != null) {
            dump += "& " + Base64Util.encodeString(simmer.getBlocklyXml());
        }
        return dump;
    }

    protected TreeNode<CircuitLinkInfo> parseNode(Node parent){

        // cats and subcats
        if(parent.getNodeName().equalsIgnoreCase("category")) {
            CircuitLinkInfo inf = new CircuitLinkInfo(parent.getAttributes().getNamedItem("name").getNodeValue());
            TreeNode<CircuitLinkInfo> result = new TreeNode<>(inf);

            NodeList entries = parent.getChildNodes();

            // recurse
            for(int i=0; i<entries.getLength(); i++){

                if(entries.item(i) != null && entries.item(i).getNodeType() == Node.ELEMENT_NODE)
                    result.addChild(parseNode(entries.item(i)).getData());
            }

            return result;
        }
        // entries
        else if(parent != null && parent.getAttributes() != null){
            CircuitLinkInfo inf = new CircuitLinkInfo(parent.getAttributes().getNamedItem("name").getNodeValue(), parent.getAttributes().getNamedItem("value").getNodeValue());
            return new TreeNode<>(inf);
        }

        return null;
    }

    public void readHint(StringTokenizer st) {
        simmer.setHintType(simmer.getHintType().getHintFromValue(new Integer(st.nextToken()).intValue()));
        simmer.setHintItem1(new Integer(st.nextToken()).intValue());
        simmer.setHintItem2(new Integer(st.nextToken()).intValue());
    }

    public void readOptions(StringTokenizer st) {
        int flags = new Integer(st.nextToken()).intValue();
        // IES - remove inteaction
        simmer.getOptions().setValue(OptionKey.SHOW_CURRENT, (flags & 1) != 0);
        simmer.getOptions().setValue(OptionKey.SMALL_GRID, (flags & 2) != 0);
        simmer.getOptions().setValue(OptionKey.SHOW_VOLTAGE, (flags & 4) == 0);
        simmer.getOptions().setValue(OptionKey.SHOW_POWER, (flags & 8) == 8);
        simmer.getOptions().setValue(OptionKey.SHOW_VALUES, (flags & 16) == 0);
        simmer.setTimeStep(new Double(st.nextToken()).doubleValue());
        double sp = new Double(st.nextToken()).doubleValue();
        int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
        // int sp2 = (int) (Math.log(sp)*24+1.5);
        simmer.getOptions().setValue(OptionKey.SIMULATION_SPEED, sp2);
        simmer.getOptions().setValue(OptionKey.CURRENT_SPEED, new Integer(st.nextToken()).intValue());
        AbstractCircuitElement.voltageRange = new Double(st.nextToken()).doubleValue();

        try {
            simmer.getSidePanel().getPowerBar().setValue(new Integer(st.nextToken()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        simmer.setGrid();
    }

    public void readBlocks(StringTokenizer st) {
        String encoded = st.nextToken();
        if (encoded != null) {
            try {
                String xmlText = Base64Util.decodeString(encoded);
                Bgpio.clearBlocks();
                simmer.setBlocklyXml(xmlText);
            } catch (Exception e) {
                GWT.log("Error", e);
            }
        }
    }

    public void loadSetupList(boolean openDefault){
        loadSetupList("setuplist.txt", openDefault);
    }

    public void loadSetupList(String url, boolean openDefault) {
//        String url = "setuplist.txt" + "?v=" + Math.random();
//        String url = "circuits/catalog.xml" + "?v=" + Math.random();
        String resource = url +  "?v=" + Math.random();
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, resource);

        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    GWT.log(MessageI18N.getMessage("File_Error_Response"), exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    // processing goes here
                    if (response.getStatusCode() == Response.SC_OK) {
                        String text = response.getText();
                        processCircuitCatalog(text.getBytes(), text.length(), openDefault, url);
                        // end or processing
                    } else
                        GWT.log(MessageI18N.getMessage("Bad_file_server_response") + response.getStatusText());
                }
            });
        } catch (RequestException e) {
            GWT.log(MessageI18N.getMessage("failed_file_reading"), e);
        }
    }

    protected TreeNode<CircuitLinkInfo> processXmlCircuitCatalog(String xml){

        String bytes = new String(xml);
        Document document = XMLParser.parse(bytes);
        NodeList rootCategories = document.getElementsByTagName("category");
        TreeNode<CircuitLinkInfo> catalog = new TreeNode<>( new CircuitLinkInfo("categories") );

        for(int i=0; i<rootCategories.getLength(); i++){
            Node categoryel = rootCategories.item(i);
//            Element categoryel = (Element)rootCategories.item(i);
            TreeNode<CircuitLinkInfo> category = parseNode(categoryel);
            catalog.addChild(category.data);
        }

        return catalog;
    }

    protected void processCircuitCatalog(byte b[], int len, final boolean openDefault, String url) {
        TreeNode<CircuitLinkInfo> catalog = url.contains(".xml")
                                ?processXmlCircuitCatalog(new String(b))
                                :processLegacyCircuitCatalog(b, len, openDefault);

        simmer.setCircuitsTree(catalog);
    }

    protected TreeNode<CircuitLinkInfo> processLegacyCircuitCatalog(byte b[], int len, final boolean openDefault) {

        TreeNode<CircuitLinkInfo> stack[] = new TreeNode[6];
        int stackptr = 0;
        TreeNode<CircuitLinkInfo> circuitsTree = new TreeNode(null);
        TreeNode<CircuitLinkInfo> currentNode = circuitsTree;
        stack[stackptr++] = currentNode;
        int p;
        for (p = 0; p < len;) {
            int l;
            for (l = 0; l != len - p; l++)
                if (b[l + p] == '\n') {
                    l++;
                    break;
                }
            String line = new String(b, p, l - 1);
            switch (line.charAt(0)) {
            case '#':
                // Commented out record
                break;
            case '+':
                TreeNode n = currentNode.addChild(new CircuitLinkInfo(line.substring(1)));
                currentNode = stack[stackptr++] = n;
                break;
            case '-':
                currentNode = stack[--stackptr - 1];
                break;
            default:
                int i = line.indexOf(' ');
                if (i > 0) {
                    String title = line.substring(i + 1);
                    boolean first = false;
                    if (line.charAt(0) == '>')
                        first = true;
                    String file = line.substring(first ? 1 : 0, i);
                    currentNode.addChild(new CircuitLinkInfo(title, file));
                    if (first && simmer.getStartCircuit() == null) {
                        simmer.setStartCircuit(file);
                        simmer.setStartLabel(title);
                        if (openDefault && simmer.getStopMessage() == null)
                            readSetupFile(simmer.getStartCircuit(), true);
                    }
                }
            }
            p += l;
        }

        return circuitsTree;
    }

    public void readSetup(byte b[], int len, String title, boolean retain, boolean centre) {
        int i;
        if (!retain) {
            for (i = 0; i != simmer.getCanvasContainer().getElmList().size(); i++) {
                AbstractCircuitElement ce = simmer.getElm(i);
                ce.delete();
            }
            simmer.getCanvasContainer().getElmList().clear();
            simmer.setHintType(HintTypeEnum.HintType.HINT_UNSET);
            simmer.setTimeStep(5e-6);
            simmer.setGrid();
            simmer.getCanvasContainer().getSidePanel().getPowerBar().setValue(50);
            AbstractCircuitElement.voltageRange = 5;
            simmer.setScopeCount(0);
            Bgpio.clearBlocks();
            simmer.setBlocklyXml(null);
            simmer.consoleLog("HELLO "+simmer.getCanvasContainer().getCircuitModel());
            simmer.getCanvasContainer().getCircuitModel().setTitle(null);
        }
        simmer.getCanvasContainer().getCircuitModel().setTitle(title);
        // cv.repaint();
        for (int p = 0; p < len;) {
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
            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f");
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
                    if (tint == '&') {
                        readBlocks(st);
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

                    AbstractCircuitElement newce = CircuitElementFactory.createCircuitElement(tint, x1, y1, x2, y2, f,
                            st);
                    if (newce == null) {
                        System.out.println(MessageI18N.getMessage("unrecognized_dump_type_") + type);
                        break;
                    }
                    newce.setPoints();
                    simmer.getElmList().add(newce);

                } catch (Exception ee) {
                    ee.printStackTrace();
                    break;
                }
                break;
            }
            p += l;

        }
        simmer.getCanvasContainer().getSidePanel().setPowerBarEnable();
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
        readSetup(text, "my circuit.txt", retain, centre);
    }

    public void readSetup(String text, String title, boolean centre) {
        readSetup(text, title, false, centre);
    }

    public void readSetup(String text, String title, boolean retain, boolean centre) {
        readSetup(text.getBytes(), text.length(), title, retain, centre);
    }

    public void readSetupFile(String str, boolean centre) {
        simmer.setT(0);
        //String url = GWT.getHostPageBaseURL();
        String url = "circuits/" + str + "?v=" + Math.random();
        loadFileFromURL(url, str, centre);
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

    public void loadFileFromURL(String url, String title, final boolean centre) {
        lager.info("loadFileFromUrl:" + url);

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    GWT.log(MessageI18N.getMessage("File_Error_Response"), exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == Response.SC_OK) {
                        String text = response.getText();
                        readSetup(text.getBytes(), text.length(), title,false, centre);
                    } else
                        lager.info(MessageI18N.getMessage("Bad_file_server_response") + response.getStatusText());
                }
            });
        } catch (RequestException e) {
            GWT.log(MessageI18N.getMessage("failed_file_reading"), e);
        }

    }
}
