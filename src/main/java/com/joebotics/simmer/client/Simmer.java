/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.joebotics.simmer.client;

import java.util.List;
import java.util.logging.Logger;

// GWT conversion (c) 2015 by Iain Sharp
// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.*;
import com.joebotics.simmer.client.breadboard.interpreter.BreadboardCircuitParserListener;
import com.joebotics.simmer.client.breadboard.interpreter.CircuitParser;
import com.joebotics.simmer.client.breadboard.interpreter.CircuitParserListener;
import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.event.SimmerEventBus;
import com.joebotics.simmer.client.gui.MainPanel;
import com.joebotics.simmer.client.gui.Scope;
import com.joebotics.simmer.client.gui.SidePanel;
import com.joebotics.simmer.client.gui.dialog.*;
import com.joebotics.simmer.client.gui.menu.*;
import com.joebotics.simmer.client.gui.util.*;
import com.joebotics.simmer.client.integration.JSEventBusProxy;
import com.joebotics.simmer.client.integration.SimmerEvents;
import com.joebotics.simmer.client.model.FootprintManager;
import com.joebotics.simmer.client.model.GpioManager;
import com.joebotics.simmer.client.util.*;
import com.joebotics.simmer.client.util.HintTypeEnum.HintType;
import com.joebotics.simmer.client.util.MouseModeEnum.MouseMode;

public class Simmer {
    public static final String muString = "u";
    public static final String ohmString = "ohm";

    private final SimmerController simmerController = new SimmerController(this);
    private final FileOps fileOps = new FileOps(this);
    private SidePanel sidePanel = new SidePanel(this);

    private double circuitMatrix[][], circuitRightSide[], origRightSide[], origMatrix[][];
    private int circuitMatrixSize, circuitMatrixFullSize;
    private int circuitPermute[];
    private int circuitBottom;
    private boolean circuitNeedsMap;
    private boolean circuitNonLinear;

    private int gridMask;
    private int gridRound;
    private int gridSize;

    private final int FASTTIMER = 40;
    private SwitchElm heldSwitchElm;

    private HintType hintType = HintType.HINT_UNSET;
    private int hintItem1;
    private int hintItem2;

    private int draggingPost;

    private boolean dumpMatrix;
    private boolean converged;

    private Context2d backcontext;
    private Canvas backcv;
    private Canvas cv;
    private Rectangle circuitArea;
    private RowInfo circuitRowInfo[];

    private PopupPanel contextPanel;
    private String ctrlMetaKey;
    private Context2d cvcontext;

    private String mouseModeStr = "Select";
    private int mousePost = -1;
    private int scopeColCount[];
    private int scopeCount;
    private int menuScope = -1;
    private int scopeSelected = -1;
    private int subIterations;
    private double t;

    private boolean isMac;
    private long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    private LoadFile loadFileInput;

    private static AboutBox aboutBox;
    private static EditDialog editDialog;
    private static ExportAsLocalFileDialog exportAsLocalFileDialog;
    private static ExportAsTextDialog exportAsTextDialog;
    private static ExportAsUrlDialog exportAsUrlDialog;
    private static ImportFromTextDialog importFromTextDialog;
    private static ScrollValuePopup scrollValuePopup;

    private MouseMode mouseMode = MouseMode.SELECT;
    private MouseMode tempMouseMode = MouseMode.SELECT;
    // private DockLayoutPanel layoutPanel;
    private DrawMenu popupDrawMenu;

    private AbstractCircuitElement selectedCircuitElement;
    private AbstractCircuitElement plotXElm, plotYElm;
    private AbstractCircuitElement stopElm;
    private AbstractCircuitElement voltageSources[];
    private AbstractCircuitElement dragElm;
    private AbstractCircuitElement mouseElm;

    private Rectangle selectedArea;
    private String shortcuts[];
    private TreeNode<CircuitLinkInfo> circuitsTree = null;
    private String startCircuit = null;
    private String startCircuitText = null;
    private String startLabel = null;
    private String stopMessage;

    private double timeStep;
    private boolean analyzeFlag;
    private boolean dragging;
    private boolean mouseDragging;

    private ElementPopupMenu elmMenuBar;
    private Scope scopes[];

    private CircuitModel circuitModel;
    private Options options;
    private GpioManager gpioManager;
    private String blocklyXml;
    private SimmerEventBus eventBus;
    private FootprintManager footprintManager;

    private static Simmer instance;

    private static final Logger lager = Logger.getLogger(Simmer.class.getName());

    private Simmer() {
        gpioManager = new GpioManager();
        footprintManager = new FootprintManager();
        eventBus = new SimmerEventBus();
    }

    public static final Simmer getInstance() {
        if (instance == null)
            instance = new Simmer();

        return instance;
    }

    public ImportFromTextDialog getImportFromTextDialog() {
        return importFromTextDialog;
    }

    public AboutBox getAboutBox() {
        return aboutBox;
    }

    private MainMenuBar mainMenuBar;

    public void init() {

        boolean printable = false;
        boolean convention = true;
        boolean euro = false;

        AbstractCircuitElement.initClass(this);
        QueryParameters qp = new QueryParameters();

        Timer timer = new Timer() {
            public void run() {
                updateCircuit();
            }
        };

        try {
            String cct = qp.getValue("cct");
            if (cct != null)
                startCircuitText = cct.replace("%24", "$");

            startCircuit = qp.getValue("startCircuit");
            startLabel = qp.getValue("startLabel");
            euro = qp.getBooleanValue("euroResistors", false);
            printable = qp.getBooleanValue("whiteBackground", false);
            convention = qp.getBooleanValue("conventionalCurrent", true);
        } catch (Exception e) {
            // log(e);
        }

        // HACK FOR DEMO
        startCircuit = "demo-powering-an-led.txt";

        String os = Navigator.getPlatform();
        isMac = (os.toLowerCase().contains("mac"));
        ctrlMetaKey = (isMac) ? "Cmd" : "Ctrl";

        // dumpTypes = new Class[300];
        shortcuts = new String[127];
        options = new Options(Dictionary.getDictionary("SimmerOptions"));

        // these characters are reserved
        // IES - removal of scopes
        /*
         * dumpTypes[(int)'o'] = Scope.class; dumpTypes[(int)'h'] = Scope.class;
         * dumpTypes[(int)'$'] = Scope.class; dumpTypes[(int)'%'] = Scope.class;
         * dumpTypes[(int)'?'] = Scope.class; dumpTypes[(int)'B'] = Scope.class;
         */

        // main.setLayout(new CircuitLayout());
        MainPanel mainPanel = new MainPanel();
        editDialog = mainPanel.getEditDialog();
        // layoutPanel = new DockLayoutPanel(Unit.PX);
        sidePanel = new SidePanel(this);
        popupDrawMenu = new DrawMenu(this, true);
        mainMenuBar = new MainMenuBar(this);

        RootLayoutPanel.get().add(mainPanel);
        cv = mainPanel.getCanvas();
        if (cv == null) {
            // fire circuit broken event here
            // {source: simmer, component: ce, message:
            // "Voltage_source/wire_loop_with_no_resistance!"}
            String message = MessageI18N
                    .getMessage("Not_working._You_need_a_browser_that_supports_the_CANVAS_element.");
            JSEventBusProxy.fireError(SimmerEvents.SYSTEM_ERROR, message);
            RootPanel.get().add(new Label(message));
            return;
        }

        cvcontext = cv.getContext2d();
        backcv = Canvas.createIfSupported();
        backcontext = backcv.getContext2d();
        setCanvasSize();
        sidePanel.createSideBar();
        setGrid();

        circuitModel = new CircuitModel();

        scopes = new Scope[20];
        scopeColCount = new int[20];
        scopeCount = 0;

        // element popup menu
        elmMenuBar = new ElementPopupMenu();

        if (startCircuitText != null) {

            fileOps.getSetupList(false);
            fileOps.readSetup(startCircuitText, false);

        } else {

            fileOps.readSetup(null, 0, "blank.txt", false, false);

            if (stopMessage == null && startCircuit != null) {
                fileOps.getSetupList(false);
                fileOps.readSetupFile(startCircuit, true);
            } else
                fileOps.getSetupList(true);
        }

        mainMenuBar.getEditMenu().enableUndoRedo();
        sidePanel.setiFrameHeight();
        bindEventHandlers();

        // setup timer
        timer.scheduleRepeating(FASTTIMER);
        JSEventBusProxy.init();
    }

    public EditDialog getEditDialog() {
        return editDialog;
    }

    public static String getMuString() {
        return muString;
    }

    private void analyzeCircuit() {
        calcCircuitBottom();

        if (getElmList().isEmpty())
            return;

        stopMessage = null;
        stopElm = null;
        int i, j;
        int vscount = 0;
        circuitModel.resetNodeList();
        boolean gotGround = false;
        boolean gotRail = false;
        AbstractCircuitElement volt = null;

        // look for voltage or ground element
        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            if (ce instanceof GroundElm) {
                gotGround = true;
                break;
            }
            if (ce instanceof RailElm)
                gotRail = true;

            if (volt == null && ce instanceof VoltageElm)
                volt = ce;
        }

        // if no ground, and no rails, then the voltage elm's first terminal
        // is ground
        if (!gotGround && volt != null && !gotRail) {
            CircuitNode cn = new CircuitNode();
            Point pt = volt.getPost(0);
            cn.x = (int) pt.getX();
            cn.y = (int) pt.getY();
            getNodeList().add(cn);

        } else {
            // otherwise allocate extra node for ground
            CircuitNode cn = new CircuitNode();
            cn.x = cn.y = -1;
            getNodeList().add(cn);
        }

        // allocate nodes and voltage sources
        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            int inodes = ce.getInternalNodeCount();
            int ivs = ce.getVoltageSourceCount();
            int posts = ce.getPostCount();

            // allocate a node for each post and match posts to nodes
            for (j = 0; j != posts; j++) {
                Point pt = ce.getPost(j);
                int k;
                for (k = 0; k != getNodeList().size(); k++) {
                    CircuitNode cn = getCircuitNode(k);
                    if (pt.getX() == cn.x && pt.getY() == cn.y)
                        break;
                }
                if (k == getNodeList().size()) {
                    CircuitNode cn = new CircuitNode();
                    cn.x = (int) pt.getX();
                    cn.y = (int) pt.getY();
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.setNum(j);
                    cnl.setElm(ce);
                    cn.links.add(cnl);
                    ce.setNode(j, getNodeList().size());
                    getNodeList().add(cn);
                } else {
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.setNum(j);
                    cnl.setElm(ce);
                    getCircuitNode(k).links.add(cnl);
                    ce.setNode(j, k);
                    // if it's the ground node, make sure the node voltage is 0,
                    // cause it may not get set later
                    if (k == 0)
                        ce.setNodeVoltage(j, 0);
                }
            }
            for (j = 0; j != inodes; j++) {
                CircuitNode cn = new CircuitNode();
                cn.x = cn.y = -1;
                cn.internal = true;
                CircuitNodeLink cnl = new CircuitNodeLink();
                cnl.setNum(j + posts);
                cnl.setElm(ce);
                cn.links.add(cnl);
                ce.setNode(cnl.getNum(), getNodeList().size());
                getNodeList().add(cn);
            }
            vscount += ivs;
        }
        voltageSources = new AbstractCircuitElement[vscount];
        vscount = 0;
        circuitNonLinear = false;

        // determine if circuit is nonlinear
        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            if (ce.nonLinear())
                circuitNonLinear = true;
            int ivs = ce.getVoltageSourceCount();
            for (j = 0; j != ivs; j++) {
                voltageSources[vscount] = ce;
                ce.setVoltageSource(j, vscount++);
            }
        }
        // voltageSourceCount = vscount;

        int matrixSize = getNodeList().size() - 1 + vscount;
        circuitMatrix = new double[matrixSize][matrixSize];
        circuitRightSide = new double[matrixSize];
        origMatrix = new double[matrixSize][matrixSize];
        origRightSide = new double[matrixSize];
        circuitMatrixSize = circuitMatrixFullSize = matrixSize;
        circuitRowInfo = new RowInfo[matrixSize];
        circuitPermute = new int[matrixSize];

        // int vs = 0;
        for (i = 0; i != matrixSize; i++)
            circuitRowInfo[i] = new RowInfo();

        circuitNeedsMap = false;

        // stamp linear circuit elements
        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            ce.stamp();
        }

        // determine nodes that are unconnected
        boolean closure[] = new boolean[getNodeList().size()];
        boolean changed = true;
        closure[0] = true;
        while (changed) {
            changed = false;

            for (i = 0; i != getElmList().size(); i++) {
                AbstractCircuitElement ce = getElm(i);

                // loop through all ce's nodes to see if they are connected
                // to other nodes not in closure
                for (j = 0; j < ce.getPostCount(); j++) {

                    if (!closure[ce.getNode(j)]) {
                        if (ce.hasGroundConnection(j))
                            closure[ce.getNode(j)] = changed = true;

                        continue;
                    }
                    int k;
                    for (k = 0; k != ce.getPostCount(); k++) {
                        if (j == k)
                            continue;
                        int kn = ce.getNode(k);
                        if (ce.getConnection(j, k) && !closure[kn]) {
                            closure[kn] = true;
                            changed = true;
                        }
                    }
                }
            }
            if (changed)
                continue;

            // connect unconnected nodes
            for (i = 0; i != getNodeList().size(); i++)
                if (!closure[i] && !getCircuitNode(i).internal) {
                    String message = MessageI18N.getMessage("node_") + " " + i + " "
                            + MessageI18N.getMessage("_unconnected");
                    lager.info(message);
                    JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN, message, getCircuitNode(i));
                    stampResistor(0, i, 1e8);
                    closure[i] = true;
                    changed = true;
                    break;
                }
        }

        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            // look for inductors with no current path
            if (ce instanceof InductorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1), getNodeList().size(),
                        getElmList());

                // first try findPath with maximum depth of 5, to avoid
                // slowdowns
                if (!fpi.findPath(ce.getNode(0), 5) && !fpi.findPath(ce.getNode(0))) {
                    lager.info(ce + MessageI18N.getMessage("_no_path"));
                    ce.reset();
                }
            }
            // look for current sources with no current path
            if (ce instanceof CurrentElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1), getNodeList().size(),
                        getElmList());
                if (!fpi.findPath(ce.getNode(0))) {
                    String message = MessageI18N.getMessage("No_path_for_current_source!");
                    stop(message, ce);
                    // fire circuit broken event here
                    // {source: simmer, component: ce, message:
                    // "No_path_for_current_source"}
                    JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_NO_PATH_FOR_CURRENT_SOURCE, message, ce);
                    return;
                }
            }
            // look for voltage source loops
            // IES
            if ((ce instanceof VoltageElm && ce.getPostCount() == 2) || ce instanceof WireElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce, ce.getNode(1), getNodeList().size(),
                        getElmList());
                if (fpi.findPath(ce.getNode(0))) {
                    String message = MessageI18N.getMessage("Voltage_source/wire_loop_with_no_resistance!");
                    stop(message, ce);
                    // fire circuit broken event here
                    // {source: simmer, component: ce, message:
                    // "Voltage_source/wire_loop_with_no_resistance!"}
                    JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_VOLTAGE_SOURCE_LOOP, message, ce);

                    return;
                }
            }
            // look for shorted caps, or caps w/ voltage but no R
            if (ce instanceof CapacitorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce, ce.getNode(1), getNodeList().size(),
                        getElmList());
                if (fpi.findPath(ce.getNode(0))) {
                    lager.fine(ce + MessageI18N.getMessage("_shorted"));
                    ce.reset();
                } else {
                    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1), getNodeList().size(), getElmList());
                    if (fpi.findPath(ce.getNode(0))) {
                        String message = MessageI18N.getMessage("Capacitor_loop_with_no_resistance!");
                        stop(message, ce);
                        // fire circuit broken event here
                        // {source: simmer, component: ce, message:
                        // "Capacitor_loop_with_no_resistance!"}
                        JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_CAPACITOR_LOOP, message, ce);
                        return;
                    }
                }
            }
        }

        // simplify the matrix; this speeds things up quite a bit
        for (i = 0; i != matrixSize; i++) {
            int qm = -1, qp = -1;
            double qv = 0;
            RowInfo re = circuitRowInfo[i];
            /*
             * lager.fine(MessageI18N.getMessage("row_") + i + "_" +
             * re.lsChanges + "_" + re.rsChanges + "_" + re.dropRow);
             */
            if (re.isLsChanges() || re.isDropRow() || re.isRsChanges())
                continue;

            double rsadd = 0;

            // look for rows that can be removed
            for (j = 0; j != matrixSize; j++) {
                double q = circuitMatrix[i][j];

                if (circuitRowInfo[j].getType() == RowInfo.ROW_CONST) {
                    // keep a running total of const values that have been
                    // removed already
                    rsadd -= circuitRowInfo[j].getValue() * q;
                    continue;
                }

                if (q == 0)
                    continue;

                if (qp == -1) {
                    qp = j;
                    qv = q;
                    continue;
                }

                if (qm == -1 && q == -qv) {
                    qm = j;
                    continue;
                }

                break;
            }
            // lager.fine(MessageI18N.getMessage("line_") + i + "_" + qp + "_" +
            // qm + "_" + j);
            /*
             * if (qp != -1 && circuitRowInfo[qp].lsChanges) {
             * lager.fine(MessageI18N.getMessage("lschanges")); continue; } if
             * (qm != -1 && circuitRowInfo[qm].lsChanges) {
             * lager.fine(MessageI18N.getMessage("lschanges")); continue; }
             */
            if (j == matrixSize) {
                if (qp == -1) {
                    String message = MessageI18N.getMessage("Matrix_error");
                    stop(message, null);
                    // fire circuit broken event here
                    // {source: simmer, component: ce, message: "Matrix_error"}
                    JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_MATRIX_ERROR, message);
                    return;
                }
                RowInfo elt = circuitRowInfo[qp];
                if (qm == -1) {
                    // we found a row with only one nonzero entry; that value
                    // is a constant
                    int k;
                    for (k = 0; elt.getType() == RowInfo.ROW_EQUAL && k < 100; k++) {
                        // follow the chain
                        /*
                         * lager.fine(MessageI18N.getMessage(
                         * "following_equal_chain_from_") + i + "_" + qp +
                         * MessageI18N.getMessage("_to_") + elt.nodeEq);
                         */
                        qp = elt.getNodeEq();
                        elt = circuitRowInfo[qp];
                    }
                    if (elt.getType() == RowInfo.ROW_EQUAL) {
                        // break equal chains
                        // lager.fine(MessageI18N.getMessage("Break_equal_chain"));
                        elt.setType(RowInfo.ROW_NORMAL);
                        continue;
                    }
                    if (elt.getType() != RowInfo.ROW_NORMAL) {
                        lager.fine(MessageI18N.getMessage("type_already_") + elt.getType()
                                + MessageI18N.getMessage("_for_") + qp + "!");
                        continue;
                    }
                    elt.setType(RowInfo.ROW_CONST);
                    elt.setValue((circuitRightSide[i] + rsadd) / qv);
                    circuitRowInfo[i].setDropRow(true);
                    // lager.fine(qp + MessageI18N.getMessage("_*_") + qv +
                    // MessageI18N.getMessage("_=_const_") +
                    // elt.value);
                    i = -1; // start over from scratch
                } else if (circuitRightSide[i] + rsadd == 0) {
                    // we found a row with only two nonzero entries, and one
                    // is the negative of the other; the values are equal
                    if (elt.getType() != RowInfo.ROW_NORMAL) {
                        // lager.fine(MessageI18N.getMessage("swapping"));
                        int qq = qm;
                        qm = qp;
                        qp = qq;
                        elt = circuitRowInfo[qp];
                        if (elt.getType() != RowInfo.ROW_NORMAL) {
                            // we should follow the chain here, but this
                            // hardly ever happens so it's not worth worrying
                            // about
                            lager.fine(MessageI18N.getMessage("swap_failed"));
                            continue;
                        }
                    }
                    elt.setType(RowInfo.ROW_EQUAL);
                    elt.setNodeEq(qm);
                    circuitRowInfo[i].setDropRow(true);
                    // lager.fine(qp + MessageI18N.getMessage("_=_") + qm);
                }
            }
        }

        // find size of new matrix
        int nn = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.getType() == RowInfo.ROW_NORMAL) {
                elt.setMapCol(nn++);
                // lager.fine(MessageI18N.getMessage("col_") + i +
                // MessageI18N.getMessage("_maps_to_") + elt.mapCol);
                continue;
            }
            if (elt.getType() == RowInfo.ROW_EQUAL) {
                RowInfo e2 = null;
                // resolve chains of equality; 100 max steps to avoid loops
                for (j = 0; j != 100; j++) {

                    e2 = circuitRowInfo[elt.getNodeEq()];

                    if (e2.getType() != RowInfo.ROW_EQUAL)
                        break;

                    if (i == e2.getNodeEq())
                        break;

                    elt.setNodeEq(e2.getNodeEq());
                }
            }
            if (elt.getType() == RowInfo.ROW_CONST)
                elt.setMapCol(-1);
        }
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.getType() == RowInfo.ROW_EQUAL) {
                RowInfo e2 = circuitRowInfo[elt.getNodeEq()];
                if (e2.getType() == RowInfo.ROW_CONST) {

                    // if something is equal to a const, it's a const
                    elt.setType(e2.getType());
                    elt.setValue(e2.getValue());
                    elt.setMapCol(-1);
                    // lager.fine(i + MessageI18N.getMessage("_=_[late]const_")
                    // + elt.value);
                } else {
                    elt.setMapCol(e2.getMapCol());
                    // lager.fine(i + MessageI18N.getMessage("_maps_to:_") +
                    // e2.mapCol);
                }
            }
        }

        // make the new, simplified matrix
        int newsize = nn;
        double newmatx[][] = new double[newsize][newsize];
        double newrs[] = new double[newsize];
        int ii = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo rri = circuitRowInfo[i];
            if (rri.isDropRow()) {
                rri.setMapRow(-1);
                continue;
            }
            newrs[ii] = circuitRightSide[i];
            rri.setMapRow(ii);
            // lager.fine(MessageI18N.getMessage("Row_") + i +
            // MessageI18N.getMessage("_maps_to_") + ii);
            for (j = 0; j != matrixSize; j++) {
                RowInfo ri = circuitRowInfo[j];
                if (ri.getType() == RowInfo.ROW_CONST)
                    newrs[ii] -= ri.getValue() * circuitMatrix[i][j];
                else
                    newmatx[ii][ri.getMapCol()] += circuitMatrix[i][j];
            }
            ii++;
        }

        circuitMatrix = newmatx;
        circuitRightSide = newrs;
        matrixSize = circuitMatrixSize = newsize;

        for (i = 0; i != matrixSize; i++)
            origRightSide[i] = circuitRightSide[i];

        for (i = 0; i != matrixSize; i++)
            for (j = 0; j != matrixSize; j++)
                origMatrix[i][j] = circuitMatrix[i][j];

        circuitNeedsMap = true;

        if (!circuitNonLinear) {
            if (!MathUtil.lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
                String message = MessageI18N.getMessage("Singular_matrix");
                stop(message, null);
                // fire circuit broken event here?
                // {source: simmer, component: ce, message: "Singular_matrix!"}
                JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_SINGULAR_MATRIX, message);
                return;
            }
        }

        // fire circuit working event here
        BreadboardCircuitParserListener bi = new BreadboardCircuitParserListener();
        CircuitParser cl = new CircuitParser(getNodeList(), bi);
        cl.analyze();

        // this is a hack to ensure bounds are returned properly
        // but with a little work can be used to throttle events
        // sent through the bus to the js code.
        class TimerHandler extends Timer {

            // private CircuitParserListener listener;

            public TimerHandler(CircuitParserListener listener) {
                // this.listener = listener;
            }

            private JSONObject last;

            public void run() {
                JSONObject jsonObject = circuitModel.toJSONObject();
                if (getMainMenuBar().getEditMenu().circuitHasChanged())
                    JSEventBusProxy.fireEvent(SimmerEvents.CIRCUIT_WORKING, jsonObject);
            }
        }

        TimerHandler th = new TimerHandler(bi);
        th.schedule(1);
    }

    protected boolean anySelectedButMouse() {
        for (int i = 0; i != getElmList().size(); i++)
            if (getElm(i) != mouseElm && getElm(i).isSelected())
                return true;
        return false;
    }

    private void calcCircuitBottom() {
        int i;
        circuitBottom = 0;
        for (i = 0; i != getElmList().size(); i++) {
            Rectangle rect = getElm(i).getBoundingBox();
            int bottom = rect.height + rect.y;
            if (bottom > circuitBottom)
                circuitBottom = bottom;
        }
    }

    protected void centreCircuit() {
        int i;
        int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            // centered text causes problems when trying to center the circuit,
            // so we special-case it here
            if (!ce.isCenteredText()) {
                minx = MathUtil.min(ce.getX1(), MathUtil.min(ce.getX2(), minx));
                maxx = MathUtil.max(ce.getX1(), MathUtil.max(ce.getX2(), maxx));
            }
            miny = MathUtil.min(ce.getY1(), MathUtil.min(ce.getY2(), miny));
            maxy = MathUtil.max(ce.getY1(), MathUtil.max(ce.getY2(), maxy));
        }
        // center circuit; we don't use snapGrid() because that rounds
        int dx = gridMask & ((circuitArea.width - (maxx - minx)) / 2 - minx);
        int dy = gridMask & ((circuitArea.height - (maxy - miny)) / 2 - miny);

        if (dx + minx < 0)
            dx = gridMask & (-minx);

        if (dy + miny < 0)
            dy = gridMask & (-miny);

        for (i = 0; i != getElmList().size(); i++) {
            AbstractCircuitElement ce = getElm(i);
            ce.move(dx, dy);
        }
        // after moving elements, need this to avoid singular matrix probs
        needAnalyze();
        circuitBottom = 0;
    }

    protected void doMainMenuChecks() {
        int c = mainMenuBar.getDrawMenu().getMainMenuItems().size();
        for (int i = 0; i < c; i++)
            mainMenuBar.getDrawMenu().getMainMenuItems().get(i)
                    .setState(mainMenuBar.getDrawMenu().getMainMenuItemNames().get(i) == mouseModeStr);
    }

    public CircuitNode getCircuitNode(int n) {
        if (n >= getNodeList().size())
            return null;
        return getNodeList().get(n);
    }

    private String getHint() {
        AbstractCircuitElement c1 = getElm(hintItem1);
        AbstractCircuitElement c2 = getElm(hintItem2);

        if (c1 == null || c2 == null)
            return null;

        if (hintType == HintType.HINT_LC) {
            if (!(c1 instanceof InductorElm))
                return null;

            if (!(c2 instanceof CapacitorElm))
                return null;

            InductorElm ie = (InductorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;

            return "res.f = " + AbstractCircuitElement
                    .getUnitText(1 / (2 * Math.PI * Math.sqrt(ie.getInductance() * ce.getCapacitance())), "Hz");
        }
        if (hintType == HintType.HINT_RC) {
            if (!(c1 instanceof ResistorElm))
                return null;

            if (!(c2 instanceof CapacitorElm))
                return null;

            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;

            return "RC = " + AbstractCircuitElement.getUnitText(re.getResistance() * ce.getCapacitance(), "s");
        }
        if (hintType == HintType.HINT_3DB_C) {
            if (!(c1 instanceof ResistorElm))
                return null;

            if (!(c2 instanceof CapacitorElm))
                return null;

            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;

            return "f.3db = " + AbstractCircuitElement
                    .getUnitText(1 / (2 * Math.PI * re.getResistance() * ce.getCapacitance()), "Hz");
        }
        if (hintType == HintType.HINT_3DB_L) {
            if (!(c1 instanceof ResistorElm))
                return null;

            if (!(c2 instanceof InductorElm))
                return null;

            ResistorElm re = (ResistorElm) c1;
            InductorElm ie = (InductorElm) c2;

            return "f.3db = "
                    + AbstractCircuitElement.getUnitText(re.getResistance() / (2 * Math.PI * ie.getInductance()), "Hz");
        }
        if (hintType == HintType.HINT_TWINT) {
            if (!(c1 instanceof ResistorElm))
                return null;

            if (!(c2 instanceof CapacitorElm))
                return null;

            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;

            return "fc = " + AbstractCircuitElement
                    .getUnitText(1 / (2 * Math.PI * re.getResistance() * ce.getCapacitance()), "Hz");
        }
        return null;
    }

    public double getIterCount() {
        Integer speed = options.getInteger(OptionKey.SIMULATION_SPEED);
        if (speed == 0)
            return 0;
        return .1 * Math.exp((speed - 61) / 24.);
    }

    public void enableItems() {
        // if (powerCheckItem.getState()) {
        // powerBar.enable();
        // powerLabel.enable();
        // } else {
        // powerBar.disable();
        // powerLabel.disable();
        // }
        // enableUndoRedo();
    }

    /** end options menu **/
    private void bindEventHandlers() {
        cv.sinkEvents(Event.MOUSEEVENTS | Event.TOUCHEVENTS | Event.KEYEVENTS);
        cv.addMouseDownHandler(simmerController);
        cv.addMouseMoveHandler(simmerController);
        cv.addMouseUpHandler(simmerController);
        cv.addClickHandler(simmerController);
        cv.addDoubleClickHandler(simmerController);
        cv.addTouchStartHandler(simmerController);
        cv.addTouchMoveHandler(simmerController);
        cv.addTouchEndHandler(simmerController);
        cv.addTouchCancelHandler(simmerController);
        cv.addDomHandler(simmerController, ContextMenuEvent.getType());
        mainMenuBar.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doMainMenuChecks();
            }
        }, ClickEvent.getType());
        Event.addNativePreviewHandler(simmerController);
        cv.addMouseWheelHandler(simmerController);
    }

    public int locateElm(AbstractCircuitElement elm) {
        for (int i = 0; i != getElmList().size(); i++)
            if (elm == getElmList().get(i))
                return i;
        return -1;
    }

    public void needAnalyze() {
        analyzeFlag = true;
    }

    public void resetAction() {
        int i;
        for (i = 0; i != getElmList().size(); i++)
            getElm(i).reset();

        for (i = 0; i != scopeCount; i++)
            scopes[i].resetGraph();

        // TODO: Will need to do IE bug fix here?
        analyzeFlag = true;
        t = 0;
        sidePanel.getStoppedCheck().setState(false);
    }

    private void runCircuit() {
        if (circuitMatrix == null || getElmList().size() == 0) {
            circuitMatrix = null;
            return;
        }
        int iter;
        // int maxIter = getIterCount();
        boolean debugprint = dumpMatrix;
        dumpMatrix = false;
        long steprate = (long) (160 * getIterCount());
        long tm = System.currentTimeMillis();
        long lit = lastIterTime;
        if (1000 >= steprate * (tm - lastIterTime))
            return;
        for (iter = 1;; iter++) {
            int i, j, k, subiter;
            for (i = 0; i != getElmList().size(); i++) {
                AbstractCircuitElement ce = getElm(i);
                ce.startIteration();
            }
            // steps++;
            final int subiterCount = 5000;
            for (subiter = 0; subiter != subiterCount; subiter++) {
                converged = true;
                subIterations = subiter;
                for (i = 0; i != circuitMatrixSize; i++)
                    circuitRightSide[i] = origRightSide[i];
                if (circuitNonLinear) {
                    for (i = 0; i != circuitMatrixSize; i++)
                        for (j = 0; j != circuitMatrixSize; j++)
                            circuitMatrix[i][j] = origMatrix[i][j];
                }
                for (i = 0; i != getElmList().size(); i++) {
                    AbstractCircuitElement ce = getElm(i);
                    ce.doStep();
                }
                if (stopMessage != null)
                    return;

                boolean printit = debugprint;
                debugprint = false;
                for (j = 0; j != circuitMatrixSize; j++) {
                    for (i = 0; i != circuitMatrixSize; i++) {
                        double x = circuitMatrix[i][j];
                        if (Double.isNaN(x) || Double.isInfinite(x)) {
                            String message = MessageI18N.getMessage("nan/infinite_matrix!");
                            stop(message, null);
                            // fire circuit broken event here
                            // {source: simmer, component: ce, message:
                            // "nan/infinite_matrix!"}
                            JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_NAN, message);
                            return;
                        }
                    }
                }
                if (printit) {
                    for (j = 0; j != circuitMatrixSize; j++) {
                        for (i = 0; i != circuitMatrixSize; i++)
                            System.out.print(circuitMatrix[j][i] + ",");
                        System.out.print("  " + circuitRightSide[j] + "\n");
                    }
                    System.out.print("\n");
                }
                if (circuitNonLinear) {
                    if (converged && subiter > 0)
                        break;
                    if (!MathUtil.lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
                        String message = MessageI18N.getMessage("Singular_matrix!");
                        stop(message, null);
                        // fire circuit broken event here
                        // {source: simmer, component: ce, message:
                        // "Singular_matrix!"}
                        JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_BROKEN_SINGULAR_MATRIX, message);
                        return;
                    }
                }
                MathUtil.lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute, circuitRightSide);

                for (j = 0; j != circuitMatrixFullSize; j++) {
                    RowInfo ri = circuitRowInfo[j];
                    double res = 0;
                    if (ri.getType() == RowInfo.ROW_CONST)
                        res = ri.getValue();
                    else
                        res = circuitRightSide[ri.getMapCol()];
                    /*
                     * lager.fine(j + "_" + res + "_" + ri.type + "_" +
                     * ri.mapCol);
                     */
                    if (Double.isNaN(res)) {
                        converged = false;
                        // debugprint = true;
                        break;
                    }
                    if (j < getNodeList().size() - 1) {
                        CircuitNode cn = getCircuitNode(j + 1);
                        for (k = 0; k != cn.links.size(); k++) {
                            CircuitNodeLink cnl = (CircuitNodeLink) cn.links.get(k);
                            cnl.getElm().setNodeVoltage(cnl.getNum(), res);
                        }
                    } else {
                        int ji = j - (getNodeList().size() - 1);
                        // lager.fine(MessageI18N.getMessage("setting_vsrc_") +
                        // ji + MessageI18N.getMessage("_to_") +
                        // res);
                        voltageSources[ji].setCurrent(ji, res);
                    }
                }
                if (!circuitNonLinear)
                    break;
            }
            if (subiter > 5)
                System.out.print(MessageI18N.getMessage("converged_after_") + subiter
                        + MessageI18N.getMessage("_iterations") + "\n");
            if (subiter == subiterCount) {
                stop(MessageI18N.getMessage("Convergence_failed!"), null);
                break;
            }
            t += getTimeStep();
            for (i = 0; i != scopeCount; i++)
                scopes[i].timeStep();
            tm = System.currentTimeMillis();
            lit = tm;
            if (iter * 1000 >= steprate * (tm - lastIterTime) || (tm - lastFrameTime > 500))
                break;
        } // for (iter = 1; ; iter++)
        lastIterTime = lit;
    }

    public void setCanvasSize() {
        int width, height;
        width = RootLayoutPanel.get().getOffsetWidth();
        height = RootLayoutPanel.get().getOffsetHeight();
        // height = height - Display.MENUBARHEIGHT;
        width = width - Display.BREADBOARD_WIDTH;
        if (cv != null) {
            cv.setWidth(width + "PX");
            cv.setHeight(height + "PX");
            cv.setCoordinateSpaceWidth(width);
            cv.setCoordinateSpaceHeight(height);
        }

        if (backcv != null) {
            backcv.setWidth(width + "PX");
            backcv.setHeight(height + "PX");
            backcv.setCoordinateSpaceWidth(width);
            backcv.setCoordinateSpaceHeight(height);
        }

        int h = height / 5;
        circuitArea = new Rectangle(0, 0, width, height - h);
    }

    public void setGrid() {
        gridSize = options.getBoolean(OptionKey.SMALL_GRID) ? 8 : 16;
        gridMask = ~(gridSize - 1);
        gridRound = gridSize / 2 - 1;
    }

    public void setMouseElm(AbstractCircuitElement ce) {
        if (ce != mouseElm) {
            if (mouseElm != null)
                mouseElm.setMouseElm(false);

            if (ce != null)
                ce.setMouseElm(true);

            mouseElm = ce;
        }
    }

    protected void setMouseMode(MouseMode addElm) {
        mouseMode = addElm;
        if (addElm == MouseMode.ADD_ELM) {
            cv.addStyleName("cursorCross");
            cv.removeStyleName("cursorPointer");
        } else {
            cv.addStyleName("cursorPointer");
            cv.removeStyleName("cursorCross");
        }
    }

    private void setupScopes() {
        int i;

        // check scopes to make sure the elements still exist, and remove
        // unused scopes/columns
        int pos = -1;
        for (i = 0; i < scopeCount; i++) {

            if (locateElm(scopes[i].getElm()) < 0)
                scopes[i].setElm(null);

            if (scopes[i].getElm() == null) {
                int j;
                for (j = i; j != scopeCount; j++)
                    scopes[j] = scopes[j + 1];
                scopeCount--;
                i--;
                continue;
            }

            if (scopes[i].getPosition() > pos + 1)
                scopes[i].setPosition(pos + 1);

            pos = scopes[i].getPosition();
        }

        while (scopeCount > 0 && scopes[scopeCount - 1].getElm() == null)
            scopeCount--;

        int h = cv.getCoordinateSpaceHeight() - circuitArea.height;
        pos = 0;

        for (i = 0; i != scopeCount; i++)
            scopeColCount[i] = 0;

        for (i = 0; i != scopeCount; i++) {
            pos = MathUtil.max(scopes[i].getPosition(), pos);
            scopeColCount[scopes[i].getPosition()]++;
        }

        int colct = pos + 1;
        int iw = Display.INFOWIDTH;

        if (colct <= 2)
            iw = iw * 3 / 2;

        int w = (cv.getCoordinateSpaceWidth() - iw) / colct;
        int marg = 10;

        if (w < marg * 2)
            w = marg * 2;

        pos = -1;
        int colh = 0;
        int row = 0;
        int speed = 0;

        for (i = 0; i != scopeCount; i++) {

            Scope s = scopes[i];

            if (s.getPosition() > pos) {
                pos = s.getPosition();
                colh = h / scopeColCount[pos];
                row = 0;
                speed = s.getSpeed();
            }

            if (s.getSpeed() != speed) {
                s.setSpeed(speed);
                s.resetGraph();
            }

            Rectangle r = new Rectangle(pos * w, cv.getCoordinateSpaceHeight() - h + colh * row, w - marg, colh);
            row++;

            if (!r.equals(s.getRect()))
                s.setRect(r);
        }
    }

    protected void stackAll() {
        int i;
        for (i = 0; i != scopeCount; i++) {
            scopes[i].setPosition(0);
            scopes[i].setShowMax(scopes[i].setShowMin(false));
        }
    }

    protected void stackScope(int s) {
        if (s == 0) {
            if (scopeCount < 2)
                return;
            s = 1;
        }
        if (scopes[s].getPosition() == scopes[s - 1].getPosition())
            return;
        scopes[s].setPosition(scopes[s - 1].getPosition());
        for (s++; s < scopeCount; s++)
            scopes[s].setPosition(scopes[s].getPosition() - 1);
    }

    // stamp a current source from n1 to n2 depending on current through vs
    public void stampCCCS(int n1, int n2, int vs, double gain) {
        int vn = getNodeList().size() + vs;
        stampMatrix(n1, vn, gain);
        stampMatrix(n2, vn, -gain);
    }

    public void stampConductance(int n1, int n2, double r0) {
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    public void stampCurrentSource(int n1, int n2, double i) {
        stampRightSide(n1, -i);
        stampRightSide(n2, i);
    }

    // stamp value x in row i, column j, meaning that a voltage change
    // of dv in node j will increase the current into node i by x dv.
    // (Unless i or j is a voltage source node.)
    public void stampMatrix(int i, int j, double x) {
        if (i > 0 && j > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].getMapRow();
                RowInfo ri = circuitRowInfo[j - 1];
                if (ri.getType() == RowInfo.ROW_CONST) {
                    // lager.fine(MessageI18N.getMessage("Stamping_constant_") +
                    // i + "_" + j +
                    // "_" + x);
                    circuitRightSide[i] -= x * ri.getValue();
                    return;
                }
                j = ri.getMapCol();
                // lager.fine(MessageI18N.getMessage("stamping_") + i + "_" + j
                // + "_" + x);
            } else {
                i--;
                j--;
            }
            circuitMatrix[i][j] += x;
        }
    }

    // indicate that the values on the left side of row i change in doStep()
    public void stampNonLinear(int i) {
        if (i > 0)
            circuitRowInfo[i - 1].setLsChanges(true);
    }

    public void stampResistor(int n1, int n2, double r) {
        double r0 = 1 / r;
        if (Double.isNaN(r0) || Double.isInfinite(r0)) {
            System.out.print(MessageI18N.getMessage("bad_resistance_") + r + "_" + r0 + "\n");
            int a = 0;
            a /= a;
        }
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    // indicate that the value on the right side of row i changes in doStep()
    public void stampRightSide(int i) {
        // lager.fine(MessageI18N.getMessage("rschanges_true_") + (i-1));
        if (i > 0)
            circuitRowInfo[i - 1].setRsChanges(true);
    }

    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
    public void stampRightSide(int i, double x) {
        if (i > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].getMapRow();
                // lager.fine(MessageI18N.getMessage("stamping_") + i + "_" +
                // x);
            } else
                i--;
            circuitRightSide[i] += x;
        }
    }

    // current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
    public void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
        stampMatrix(cn1, vn1, g);
        stampMatrix(cn2, vn2, g);
        stampMatrix(cn1, vn2, -g);
        stampMatrix(cn2, vn1, -g);
    }

    // control voltage source vs with voltage from n1 to n2 (must
    // also call stampVoltageSource())
    public void stampVCVS(int n1, int n2, double coef, int vs) {
        int vn = getNodeList().size() + vs;
        stampMatrix(vn, n1, coef);
        stampMatrix(vn, n2, -coef);
    }

    // use this if the amount of voltage is going to be updated in doStep()
    public void stampVoltageSource(int n1, int n2, int vs) {
        int vn = getNodeList().size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    // stamp independent voltage source #vs, from n1 to n2, amount v
    public void stampVoltageSource(int n1, int n2, int vs, double v) {
        int vn = getNodeList().size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn, v);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    public void stop(String s, AbstractCircuitElement ce) {
        JSEventBusProxy.fireError(SimmerEvents.CIRCUIT_STOPPED, s, ce);
        stopMessage = s;
        circuitMatrix = null;
        stopElm = ce;
        sidePanel.getStoppedCheck().setState(true);
        analyzeFlag = false;
        // cv.repaint();
    }

    protected void unstackAll() {
        int i;
        for (i = 0; i != scopeCount; i++) {
            scopes[i].setPosition(i);
            scopes[i].setShowMax(true);
        }
    }

    protected void unstackScope(int s) {
        if (s == 0) {
            if (scopeCount < 2)
                return;
            s = 1;
        }
        if (scopes[s].getPosition() != scopes[s - 1].getPosition())
            return;
        for (; s < scopeCount; s++)
            scopes[s].setPosition(scopes[s].getPosition() + 1);
    }

    public void updateCircuit() {
        long mystarttime;
        AbstractCircuitElement realMouseElm;
        mystarttime = System.currentTimeMillis();
        if (analyzeFlag) {
            analyzeCircuit();
            analyzeFlag = false;
        }

        realMouseElm = mouseElm;
        if (mouseElm == null)
            mouseElm = stopElm;

        setupScopes();
        // Graphics2D g = null; // hausen: changed to Graphics2D
        // g = (Graphics2D)dbimage.getGraphics();
        // g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics g = new Graphics(backcontext);
        AbstractCircuitElement.selectColor = Color.cyan;

        if (options.getBoolean(OptionKey.WHITE_BACKGROUND)) {
            AbstractCircuitElement.whiteColor = Color.black;
            AbstractCircuitElement.lightGrayColor = Color.black;
            g.setColor(Color.white);
        } else {
            AbstractCircuitElement.whiteColor = Color.white;
            AbstractCircuitElement.lightGrayColor = Color.lightGray;
            g.setColor(Color.black);
        }

        g.fillRect(0, 0, g.getContext().getCanvas().getWidth(), g.getContext().getCanvas().getHeight());

        if (!sidePanel.getStoppedCheck().getState()) {
            try {
                runCircuit();
            } catch (Exception e) {
                e.printStackTrace();
                analyzeFlag = true;
                // cv.repaint();
                return;
            }
        }

        long sysTime = System.currentTimeMillis();

        if (!sidePanel.getStoppedCheck().getState()) {

            if (lastTime != 0) {
                int inc = (int) (sysTime - lastTime);
                double c = options.getInteger(OptionKey.CURRENT_SPEED);
                c = java.lang.Math.exp(c / 3.5 - 14.2);
                AbstractCircuitElement.currentMult = 1.7 * inc * c;
                if (!options.getBoolean(OptionKey.CONVENTIONAL_CURRENT_MOTION))
                    AbstractCircuitElement.currentMult = -AbstractCircuitElement.currentMult;
            }

            lastTime = sysTime;
        } else
            lastTime = 0;

        if (sysTime - secTime >= 1000) {
            // framerate = frames;
            // steprate = steps;
            // frames = 0;
            // steps = 0;
            secTime = sysTime;
        }
        AbstractCircuitElement.powerMult = Math.exp((sidePanel.getPowerBar().getValue() / 4.762) - 7);

        int i;
        Font oldfont = AbstractCircuitElement.unitsFont;
        g.setFont(oldfont);

        for (i = 0; i != getElmList().size(); i++) {
            if (options.getBoolean(OptionKey.SHOW_POWER))
                g.setColor(Color.gray);
            /*
             * else if (conductanceCheckItem.getState())
             * g.setColor(Color.white);
             */
            getElm(i).draw(g);
        }
        // mydrawtime += System.currentTimeMillis() - mydrawstarttime;
        if (tempMouseMode == MouseMode.DRAG_ROW || tempMouseMode == MouseMode.DRAG_COLUMN
                || tempMouseMode == MouseMode.DRAG_POST || tempMouseMode == MouseMode.DRAG_SELECTED)
            for (i = 0; i != getElmList().size(); i++) {

                AbstractCircuitElement ce = getElm(i);
                // ce.drawPost(g, ce.x , ce.y );
                // ce.drawPost(g, ce.x2, ce.y2);
                if (ce != mouseElm || tempMouseMode != MouseMode.DRAG_POST) {
                    g.setColor(Color.gray);
                    g.fillOval(ce.getX1() - 3, ce.getY1() - 3, 7, 7);
                    g.fillOval(ce.getX2() - 3, ce.getY2() - 3, 7, 7);
                } else {
                    ce.drawHandles(g, Color.cyan);
                }
            }
        if (tempMouseMode == MouseMode.SELECT && mouseElm != null) {
            mouseElm.drawHandles(g, Color.cyan);
        }
        int badnodes = 0;
        // find bad connections, nodes not connected to other elements which
        // intersect other elements' bounding boxes
        // debugged by hausen: nullPointerException
        if (getNodeList() != null)
            for (i = 0; i != getNodeList().size(); i++) {
                CircuitNode cn = getCircuitNode(i);
                if (!cn.internal && cn.links.size() == 1) {

                    int bb = 0, j;
                    CircuitNodeLink cnl = cn.links.get(0);

                    for (j = 0; j != getElmList().size(); j++) { // TODO:
                                                                 // (hausen)
                        // see if this
                        // change does not
                        // break stuff
                        AbstractCircuitElement ce = getElm(j);

                        if (ce instanceof GraphicElm)
                            continue;

                        if (cnl.getElm() != ce && getElm(j).getBoundingBox().contains(cn.x, cn.y))
                            bb++;
                    }
                    if (bb > 0) {
                        g.setColor(Color.red);
                        g.fillOval(cn.x - 3, cn.y - 3, 7, 7);
                        badnodes++;
                    }
                }
            }
        /*
         * if (mouseElm != null) { g.setFont(oldfont); g.drawString("+",
         * mouseElm.x+10, mouseElm.y); }
         */
        if (dragElm != null && (dragElm.getX1() != dragElm.getX2() || dragElm.getY1() != dragElm.getY2())) {

            dragElm.draw(g);
            dragElm.drawHandles(g, Color.cyan);
        }

        g.setFont(oldfont);
        int ct = scopeCount;

        if (stopMessage != null)
            ct = 0;

        for (i = 0; i != ct; i++)
            scopes[i].draw(g);

        g.setColor(AbstractCircuitElement.whiteColor);

        if (stopMessage != null) {
            g.drawString(stopMessage, 10, circuitArea.height - 10);

        } else {

            if (circuitBottom == 0)
                calcCircuitBottom();

            String info[] = new String[10];

            if (mouseElm != null) {
                if (mousePost == -1)
                    mouseElm.getInfo(info);
                else
                    info[0] = "V = " + AbstractCircuitElement.getUnitText(mouseElm.getPostVoltage(mousePost), "V");
            } else {
                info[0] = "t = " + AbstractCircuitElement.getUnitText(t, "s");
                // AbstractCircuitElement.showFormat.setMinimumFractionDigits(0);
            }

            if (hintType != HintType.HINT_UNSET) {
                for (i = 0; info[i] != null; i++)
                    ;
                String s = getHint();
                if (s == null)
                    hintType = HintType.HINT_UNSET;
                else
                    info[i] = s;
            }

            int x = 0;
            if (ct != 0)
                x = scopes[ct - 1].rightEdge() + 20;
            x = MathUtil.max(x, cv.getCoordinateSpaceWidth() * 2 / 3);
            // x=cv.getCoordinateSpaceWidth()*2/3;

            // count lines of data
            for (i = 0; info[i] != null; i++)
                ;

            if (badnodes > 0)
                info[i++] = badnodes + ((badnodes == 1) ? MessageI18N.getMessage("_bad_connection")
                        : MessageI18N.getMessage("_bad_connections"));

            // find where to show data; below circuit, not too high unless we
            // need it
            // int ybase = winSize.height-15*i-5;
            int ybase = cv.getCoordinateSpaceHeight() - 15 * i - 5;
            ybase = MathUtil.min(ybase, circuitArea.height);
            ybase = MathUtil.max(ybase, circuitBottom);

            for (i = 0; info[i] != null; i++)
                g.drawString(info[i], x, ybase + 15 * (i + 1));
        }
        if (selectedArea != null) {
            g.setColor(AbstractCircuitElement.selectColor);
            g.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
        }
        mouseElm = realMouseElm;
        // frames++;

        g.setColor(Color.white);

        cvcontext.drawImage(backcontext.getCanvas(), 0.0, 0.0);
        lastFrameTime = lastTime;
        // myframes++;
    }

    public int snapGrid(int x) {
        return (x + getGridRound()) & getGridMask();
    }

    public void updateVoltageSource(int n1, int n2, int vs, double v) {
        int vn = getNodeList().size() + vs;
        stampRightSide(vn, v);
    }

    public SimmerController getSimmerController() {
        return this.simmerController;
    }

    public LoadFile getLoadFileInput() {
        return this.loadFileInput;
    }

    public void setImportFromTextDialog(ImportFromTextDialog dialog) {
        this.importFromTextDialog = dialog;
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public CircuitModel getCircuitModel() {
        return circuitModel;
    }

    public List<CircuitNode> getNodeList() {
        return circuitModel.getNodeList();
    }

    public double getT() {
        return t;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public int getScopeCount() {
        return this.scopeCount;
    }

    public Scope[] getScopes() {
        return scopes;
    }

    public Scope getScope(int idx) {
        return scopes[idx];
    }

    protected void setAboutBox(AboutBox aboutBox) {
        this.aboutBox = aboutBox;
    }

    public int getScopeSelected() {
        return scopeSelected;
    }

    public String[] getShortcuts() {
        return this.shortcuts;
    }

    protected void setScrollValuePopup(ScrollValuePopup popup) {
        Simmer.scrollValuePopup = popup;
    }

    public AbstractCircuitElement getMouseElm() {
        return mouseElm;
    }

    public AbstractCircuitElement getDragElm() {
        return this.dragElm;
    }

    protected void setMouseDragging(boolean state) {
        this.mouseDragging = state;
    }

    protected boolean isMouseDragging() {
        return this.mouseDragging;
    }

    protected void setDragging(boolean state) {
        this.dragging = state;
    }
    
    protected boolean isDragging() {
        return this.dragging;
    }

    protected void setDragElm(AbstractCircuitElement elm) {
        this.dragElm = elm;
    }

    protected void setPlotXElm(AbstractCircuitElement elm) {
        this.plotXElm = elm;
    }

    protected void setPlotYElm(AbstractCircuitElement elm) {
        this.plotYElm = elm;
    }

    protected void setScopeSelected(int nbr) {
        this.scopeSelected = nbr;
    }

    public Rectangle getCircuitArea() {
        return circuitArea;
    }

    public void setT(double t) {
        this.t = t;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public SwitchElm getHeldSwitchElm() {
        return heldSwitchElm;
    }

    public Canvas getCv() {
        return cv;
    }

    public Rectangle getSelectedArea() {
        return selectedArea;
    }

    public int getMenuScope() {
        return menuScope;
    }

    public void setScopeCount(int cnt) {
        this.scopeCount = cnt;
    }

    public ElementPopupMenu getElmMenuBar() {
        return elmMenuBar;
    }

    public MenuItem getElmEditMenuItem() {
        return elmMenuBar.getElmEditMenuItem();
    }

    public MenuItem getElmScopeMenuItem() {
        return elmMenuBar.getElmScopeMenuItem();
    }

    public int getDraggingPost() {
        return draggingPost;
    }

    public String getMouseModeStr() {
        return mouseModeStr;
    }

    public MouseMode getTempMouseMode() {
        return tempMouseMode;
    }

    public MenuBar getPopupDrawMenu() {
        return popupDrawMenu;
    }

    public AbstractCircuitElement getSelectedCircuitElement() {
        return selectedCircuitElement;
    }

    public PopupPanel getContextPanel() {
        return contextPanel;
    }

    public void setMousePost(int mousePost) {
        this.mousePost = mousePost;
    }

    public void setHeldSwitchElm(SwitchElm heldSwitchElm) {
        this.heldSwitchElm = heldSwitchElm;
    }

    public void setSelectedArea(Rectangle selectedArea) {
        this.selectedArea = selectedArea;
    }

    public void setMenuScope(int menuScope) {
        this.menuScope = menuScope;
    }

    public void setDraggingPost(int draggingPost) {
        this.draggingPost = draggingPost;
    }

    public void setMouseModeStr(String mouseModeStr) {
        this.mouseModeStr = mouseModeStr;
    }

    public void setTempMouseMode(MouseMode tempMouseMode) {
        this.tempMouseMode = tempMouseMode;
    }

    public void setSelectedCircuitElement(AbstractCircuitElement selectedCircuitElement) {
        this.selectedCircuitElement = selectedCircuitElement;
    }

    public void setContextPanel(PopupPanel contextPanel) {
        this.contextPanel = contextPanel;
    }

    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public int getSubIterations() {
        return subIterations;
    }

    public AbstractCircuitElement getPlotXElm() {
        return plotXElm;
    }

    public AbstractCircuitElement getPlotYElm() {
        return plotYElm;
    }

    public AbstractCircuitElement getElm(int n) {
        if (n >= getElmList().size())
            return null;

        return getElmList().get(n);
    }

    public List<AbstractCircuitElement> getElmList() {
        return circuitModel.getElmList();
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setHintType(HintType hintType) {
        this.hintType = hintType;
    }

    public HintType getHintType() {
        return hintType;
    }

    public int getHintItem1() {
        return hintItem1;
    }

    public int getHintItem2() {
        return hintItem2;
    }

    @Deprecated
    public MainMenuBar getMainMenuBar() {
        return mainMenuBar;
    }

    public String getStartCircuit() {
        return startCircuit;
    }

    public void setStartCircuit(String startCircuit) {
        this.startCircuit = startCircuit;
    }

    public void setStartLabel(String startLabel) {
        this.startLabel = startLabel;
    }

    public String getStopMessage() {
        return stopMessage;
    }

    public String getStartLabel() {
        return startLabel;
    }

    public FileOps getFileOps() {
        return fileOps;
    }

    public void setHintItem2(int hintItem2) {
        this.hintItem2 = hintItem2;
    }

    public void setHintItem1(int hintItem1) {
        this.hintItem1 = hintItem1;
    }

    public static ScrollValuePopup getScrollValuePopup() {
        return scrollValuePopup;
    }

    public ExportAsUrlDialog getExportAsUrlDialog() {
        return exportAsUrlDialog;
    }

    public void setExportAsUrlDialog(ExportAsUrlDialog exportAsUrlDialog) {
        Simmer.exportAsUrlDialog = exportAsUrlDialog;
    }

    public void setExportAsTextDialog(ExportAsTextDialog exportAsTextDialog) {
        Simmer.exportAsTextDialog = exportAsTextDialog;
    }

    public ExportAsTextDialog getExportAsTextDialog() {
        return exportAsTextDialog;
    }

    public ExportAsLocalFileDialog getExportAsLocalFileDialog() {
        return exportAsLocalFileDialog;
    }

    public void setExportAsLocalFileDialog(ExportAsLocalFileDialog exportAsLocalFileDialog) {
        Simmer.exportAsLocalFileDialog = exportAsLocalFileDialog;
    }

    public void setAnalyzeFlag(boolean analyzeFlag) {
        this.analyzeFlag = analyzeFlag;
    }

    public void setLoadFileInput(LoadFile loadFileInput) {
        this.loadFileInput = loadFileInput;
    }

    public int getGridRound() {
        return gridRound;
    }

    public int getGridMask() {
        return gridMask;
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public Options getOptions() {
        return options;
    }

    public TreeNode<CircuitLinkInfo> getCircuitsTree() {
        return circuitsTree;
    }

    public void setCircuitsTree(TreeNode<CircuitLinkInfo> circuitsTree) {
        this.circuitsTree = circuitsTree;
    }

    public GpioManager getGpioManager() {
        return gpioManager;
    }

    public FootprintManager getFootprintManager() {
        return footprintManager;
    }

    public String getBlocklyXml() {
        return blocklyXml;
    }

    public void setBlocklyXml(String blocklyXml) {
        this.blocklyXml = blocklyXml;
    }

    public SimmerEventBus getEventBus() {
        return eventBus;
    }
}
