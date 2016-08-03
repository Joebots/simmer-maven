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

// GWT conversion (c) 2015 by Iain Sharp
// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.*;
import com.joebotics.simmer.client.breadboard.CircuitLibrary;
import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.impl.*;
import com.joebotics.simmer.client.gui.util.*;
import com.joebotics.simmer.client.util.*;
import com.joebotics.simmer.client.util.HintTypeEnum.HintType;
import com.joebotics.simmer.client.util.MouseModeEnum.MouseMode;

import java.util.Random;
import java.util.Vector;
	//MouseDownHandler, MouseWheelHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, ClickHandler, DoubleClickHandler, ContextMenuHandler, Event.NativePreviewHandler
public class Simmer
{

//	public static final double				freqMult			= Math.PI * 2 * 4;
	private static String					muString			= "u";
	public static final String				ohmString			= "ohm";
	private final SimmerController simmerController = new SimmerController(this);

	private int								circuitBottom;
	private double							circuitMatrix[][], circuitRightSide[], origRightSide[], origMatrix[][];
	private int								circuitMatrixSize, circuitMatrixFullSize;
	private boolean							circuitNeedsMap;
	private boolean							circuitNonLinear;
	private int								circuitPermute[];
	private final int						FASTTIMER = 40;
	private int								gridMask;
	private int								gridRound;
	private int								gridSize;
	private SwitchElm heldSwitchElm;
	private HintType						hintType			= HintType.HINT_UNSET;
	private HintType						hintItem1			= HintType.HINT_UNSET;
	private HintType						hintItem2			= HintType.HINT_UNSET;
	private int								draggingPost;
	private int								dragX, dragY, initDragX, initDragY;
	private boolean							dumpMatrix;
	private boolean							converged;
	
	private static AboutBox aboutBox;
	private static EditDialog editDialog;
	private static ExportAsLocalFileDialog	exportAsLocalFileDialog;
	private static ExportAsTextDialog		exportAsTextDialog;
	private static ExportAsUrlDialog		exportAsUrlDialog;
	private static ImportFromTextDialog importFromTextDialog;
	private static ScrollValuePopup			scrollValuePopup;
	private Context2d						backcontext;
	private Canvas							backcv;
	private Rectangle						circuitArea;
	private RowInfo							circuitRowInfo[];
	
	private PopupPanel						contextPanel;

	private String							ctrlMetaKey;
	private Canvas							cv;
	private Context2d						cvcontext;

	private Vector<AbstractCircuitElement>	elmList;

    private CheckboxMenuItem                conventionCheckItem;
    private CheckboxMenuItem				euroResistorCheckItem;
    private CheckboxMenuItem				dotsCheckItem;
    private CheckboxMenuItem				showValuesCheckItem;
    private CheckboxMenuItem				smallGridCheckItem;
    private CheckboxMenuItem				voltsCheckItem;

	private MenuBar							fileMenuBar;
	private boolean							isMac;
	private MouseMode						mouseMode			= MouseMode.SELECT;
	private int								mousePost			= -1;
	private int								scopeColCount[];
	private int								scopeCount;
	private long							lastTime			= 0, lastFrameTime, lastIterTime, secTime = 0;
	private int								menuScope			= -1;
	private String							mouseModeStr		= "Select";
	private int								scopeSelected		= -1;
	private int								subIterations;
	private double							t;
	private MouseMode						tempMouseMode		= MouseMode.SELECT;

	public AbstractCircuitElement			mouseElm			= null;
	private DockLayoutPanel					layoutPanel;
	private LoadFile loadFileInput;
	private MenuBar							mainMenuBar;

	private Vector<CircuitNode>				nodeList;
	private MenuBar							menuBar;

	private AbstractCircuitElement			menuElm;

	private long							mytime				= 0;
	private MenuBar							optionsMenuBar;

	private AbstractCircuitElement			plotXElm, plotYElm;

	private Random							random;

	private Rectangle						selectedArea;
	private String							shortcuts[];

	private Scrollbar						speedBar;
	private String							startCircuit		= null;
	private String							startCircuitText	= null;
	private String							startLabel			= null;
	private AbstractCircuitElement			stopElm;
	private String							stopMessage;
	private final Timer						timer				= new Timer() {
																	public void run() {
																		updateCircuit();
																	}
																};
	private double							timeStep;
	private MenuBar							transScopeMenuBar;
	private Vector<String>					undoStack, redoStack;
	private AbstractCircuitElement			voltageSources[];

	public AbstractCircuitElement			dragElm;
	public boolean							analyzeFlag;
	public boolean							dragging;
	public boolean							mouseDragging;

	public static ScrollValuePopup getScrollValuePopup() {
		return scrollValuePopup;
	}

	public void init() {

		boolean printable = false;
		boolean convention = true;
		boolean euro = false;

		AbstractCircuitElement.initClass(this);
		QueryParameters qp = new QueryParameters();

		try {
			String cct = qp.getValue("cct");
			if (cct != null)
				startCircuitText = cct.replace("%24", "$");

			startCircuit = qp.getValue("startCircuit");
			startLabel = qp.getValue("startLabel");
			euro = qp.getBooleanValue("euroResistors", true);
			printable = qp.getBooleanValue("whiteBackground", false);
			convention = qp.getBooleanValue("conventionalCurrent", true);
		} catch (Exception e) {
		}

		String os = Navigator.getPlatform();
		isMac = (os.toLowerCase().contains("mac"));
		ctrlMetaKey = (isMac) ? "Cmd" : "Ctrl";

		// dumpTypes = new Class[300];
		shortcuts = new String[127];

		// these characters are reserved
		// IES - removal of scopes
		/*
		 * dumpTypes[(int)'o'] = Scope.class; dumpTypes[(int)'h'] = Scope.class;
		 * dumpTypes[(int)'$'] = Scope.class; dumpTypes[(int)'%'] = Scope.class;
		 * dumpTypes[(int)'?'] = Scope.class; dumpTypes[(int)'B'] = Scope.class;
		 */

		// main.setLayout(new CircuitLayout());
		layoutPanel = new DockLayoutPanel(Unit.PX);

		MenuBar drawMenuBar = new MenuBar(true);
		drawMenuBar.setAutoOpen(true);

		menuBar = new MenuBar();
		menuBar.addItem(MessageI18N.getLocale("File"), new FileMenu());
		menuBar.addItem(MessageI18N.getLocale("Edit"), buildEditMenu());
		menuBar.addItem(MessageI18N.getLocale("Draw"), drawMenuBar);
		menuBar.addItem(MessageI18N.getLocale("Scopes"), new ScopesMenu());
		menuBar.addItem(MessageI18N.getLocale("Options"), buildOptionsMenu());
		verticalPanel = new VerticalPanel();

//		optionsMenuBar = m = new MenuBar(true);

		getVoltsCheckItem().setState(true);
		getShowValuesCheckItem().setState(true);
		getEuroResistorCheckItem().setState(euro);
		getPrintableCheckItem().setState(printable);
		getConventionCheckItem().setState(convention);

		mainMenuBar = new DrawMenu(this, true);
		mainMenuBar.setAutoOpen(true);
		//buildDrawMenu(mainMenuBar);

		// popup
		//buildDrawMenu(drawMenuBar);

		layoutPanel.addNorth(menuBar, Display.MENUBARHEIGHT);
		layoutPanel.addEast(verticalPanel, Display.VERTICALPANELWIDTH);
//        SideBar sideBar = new SideBar(this);
//        layoutPanel.addEast(sideBar, Display.VERTICALPANELWIDTH);

		RootLayoutPanel.get().add(layoutPanel);
		cv = Canvas.createIfSupported();
		if (cv == null) {
			RootPanel.get().add(new Label(MessageI18N.getLocale("Not_working._You_need_a_browser_that_supports_the_CANVAS_element.")));
			return;
		}
		layoutPanel.add(cv);

		cvcontext = cv.getContext2d();
		backcv = Canvas.createIfSupported();
		backcontext = backcv.getContext2d();
		setCanvasSize();
		createSideBar();
		setGrid();

		elmList = new Vector<AbstractCircuitElement>();
		// setupList = new Vector();
		undoStack = new Vector<String>();
		redoStack = new Vector<String>();

		scopes = new Scope[20];
		scopeColCount = new int[20];
		scopeCount = 0;

		random = new Random();
		// cv.setBackground(Color.black);
		// cv.setForeground(Color.lightGray);

		// element popup menu
		elmMenuBar = buildElementPopupMenu();
		// main.add(elmMenu);

		transScopeMenuBar = buildScopeMenu(true);
		scopeMenuBar = buildScopeMenu(false);

		if (startCircuitText != null) {
			getSetupList(false);
			readSetup(startCircuitText, false);
		} else {
			readSetup(null, 0, false, false);
			if (stopMessage == null && startCircuit != null) {
				getSetupList(false);
				readSetupFile(startCircuit, startLabel, true);
			} else
				getSetupList(true);
		}

		editMenu.enableUndoRedo();
		setiFrameHeight();
		bindEventHandlers();
		// setup timer
		timer.scheduleRepeating(FASTTIMER);
		NativeJavascriptWrapper.EventBus();

	}

	public static EditDialog getEditDialog() {
		return editDialog;
	}

	public static String getMuString() {
		return muString;
	}

	public static void setEditDialog(EditDialog editDialog) {
		Simmer.editDialog = editDialog;
	}

	private void analyzeCircuit() {
		calcCircuitBottom();

		if (elmList.isEmpty())
			return;

		stopMessage = null;
		stopElm = null;
		int i, j;
		int vscount = 0;
		setNodeList(new Vector<CircuitNode>());
		boolean gotGround = false;
		boolean gotRail = false;
		AbstractCircuitElement volt = null;

		// look for voltage or ground element
		for (i = 0; i != elmList.size(); i++) {
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
			getNodeList().addElement(cn);
		} else {
			// otherwise allocate extra node for ground
			CircuitNode cn = new CircuitNode();
			cn.x = cn.y = -1;
			getNodeList().addElement(cn);
		}

		// allocate nodes and voltage sources
		for (i = 0; i != elmList.size(); i++) {
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
					cn.links.addElement(cnl);
					ce.setNode(j, getNodeList().size());
					getNodeList().addElement(cn);
				} else {
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.setNum(j);
					cnl.setElm(ce);
					getCircuitNode(k).links.addElement(cnl);
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
				cn.links.addElement(cnl);
				ce.setNode(cnl.getNum(), getNodeList().size());
				getNodeList().addElement(cn);
			}
			vscount += ivs;
		}
		voltageSources = new AbstractCircuitElement[vscount];
		vscount = 0;
		circuitNonLinear = false;

		// determine if circuit is nonlinear
		for (i = 0; i != elmList.size(); i++) {
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
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			ce.stamp();
		}

		// determine nodes that are unconnected
		boolean closure[] = new boolean[getNodeList().size()];
		boolean changed = true;
		closure[0] = true;
		while (changed) {
			changed = false;
			for (i = 0; i != elmList.size(); i++) {
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
					System.out.println(MessageI18N.getLocale("node_") + i + MessageI18N.getLocale("_unconnected"));
					stampResistor(0, i, 1e8);
					closure[i] = true;
					changed = true;
					break;
				}
		}
	
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			// look for inductors with no current path
			if (ce instanceof InductorElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1), getNodeList().size(), getElmList());
				// first try findPath with maximum depth of 5, to avoid
				// slowdowns
				if (!fpi.findPath(ce.getNode(0), 5) && !fpi.findPath(ce.getNode(0))) {
					System.out.println(ce + MessageI18N.getLocale("_no_path"));
					ce.reset();
				}
			}
			// look for current sources with no current path
			if (ce instanceof CurrentElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1), getNodeList().size(), getElmList());
				if (!fpi.findPath(ce.getNode(0))) {
					stop(MessageI18N.getLocale("No_path_for_current_source!"), ce);

					// fire circuit broken event here
					// {source: simmer, component: ce, message: "No_path_for_current_source"}
					return;
				}
			}
			// look for voltage source loops
			// IES
			if ((ce instanceof VoltageElm && ce.getPostCount() == 2) || ce instanceof WireElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce, ce.getNode(1), getNodeList().size(), getElmList());
				if (fpi.findPath(ce.getNode(0))) {
					stop(MessageI18N.getLocale("Voltage_source/wire_loop_with_no_resistance!"), ce);

					// fire circuit broken event here
					// {source: simmer, component: ce, message: "Voltage_source/wire_loop_with_no_resistance!"}
					return;
				}
			}
			// look for shorted caps, or caps w/ voltage but no R
			if (ce instanceof CapacitorElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce, ce.getNode(1), getNodeList().size(), getElmList());
				if (fpi.findPath(ce.getNode(0))) {
					System.out.println(ce + MessageI18N.getLocale("_shorted"));
					ce.reset();
				} else {
					fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1), getNodeList().size(), getElmList());
					if (fpi.findPath(ce.getNode(0))) {
						stop(MessageI18N.getLocale("Capacitor_loop_with_no_resistance!"), ce);

						// fire circuit broken event here
						// {source: simmer, component: ce, message: "Capacitor_loop_with_no_resistance!"}
						return;
					}
				}
			}
		}
		// System.out.println(MessageI18N.getLocale("ac6"));

		// simplify the matrix; this speeds things up quite a bit
		for (i = 0; i != matrixSize; i++) {
			int qm = -1, qp = -1;
			double qv = 0;
			RowInfo re = circuitRowInfo[i];
			/*
			 * System.out.println(MessageI18N.getLocale("row_") + i + "_" + re.lsChanges + "_" +
			 * re.rsChanges + "_" + re.dropRow);
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
			// System.out.println(MessageI18N.getLocale("line_") + i + "_" + qp + "_" + qm + "_" + j);
			/*
			 * if (qp != -1 && circuitRowInfo[qp].lsChanges) {
			 * System.out.println(MessageI18N.getLocale("lschanges")); continue; } if (qm != -1 &&
			 * circuitRowInfo[qm].lsChanges) { System.out.println(MessageI18N.getLocale("lschanges"));
			 * continue; }
			 */
			if (j == matrixSize) {
				if (qp == -1) {
					stop(MessageI18N.getLocale("Matrix_error"), null);

					// fire circuit broken event here
					// {source: simmer, component: ce, message: "Matrix_error"}
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
						 * System.out.println(MessageI18N.getLocale("following_equal_chain_from_") + i
						 * + "_" + qp + MessageI18N.getLocale("_to_") + elt.nodeEq);
						 */
						qp = elt.getNodeEq();
						elt = circuitRowInfo[qp];
					}
					if (elt.getType() == RowInfo.ROW_EQUAL) {
						// break equal chains
						// System.out.println(MessageI18N.getLocale("Break_equal_chain"));
						elt.setType(RowInfo.ROW_NORMAL);
						continue;
					}
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						System.out.println(MessageI18N.getLocale("type_already_") + elt.getType() + MessageI18N.getLocale("_for_") + qp + "!");
						continue;
					}
					elt.setType(RowInfo.ROW_CONST);
					elt.setValue((circuitRightSide[i] + rsadd) / qv);
					circuitRowInfo[i].setDropRow(true);
					// System.out.println(qp + MessageI18N.getLocale("_*_") + qv + MessageI18N.getLocale("_=_const_") +
					// elt.value);
					i = -1; // start over from scratch
				} else if (circuitRightSide[i] + rsadd == 0) {
					// we found a row with only two nonzero entries, and one
					// is the negative of the other; the values are equal
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						// System.out.println(MessageI18N.getLocale("swapping"));
						int qq = qm;
						qm = qp;
						qp = qq;
						elt = circuitRowInfo[qp];
						if (elt.getType() != RowInfo.ROW_NORMAL) {
							// we should follow the chain here, but this
							// hardly ever happens so it's not worth worrying
							// about
							System.out.println(MessageI18N.getLocale("swap_failed"));
							continue;
						}
					}
					elt.setType(RowInfo.ROW_EQUAL);
					elt.setNodeEq(qm);
					circuitRowInfo[i].setDropRow(true);
					// System.out.println(qp + MessageI18N.getLocale("_=_") + qm);
				}
			}
		}

		// find size of new matrix
		int nn = 0;
		for (i = 0; i != matrixSize; i++) {
			RowInfo elt = circuitRowInfo[i];
			if (elt.getType() == RowInfo.ROW_NORMAL) {
				elt.setMapCol(nn++);
				// System.out.println(MessageI18N.getLocale("col_") + i + MessageI18N.getLocale("_maps_to_") + elt.mapCol);
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
					// System.out.println(i + MessageI18N.getLocale("_=_[late]const_") + elt.value);
				} else {
					elt.setMapCol(e2.getMapCol());
					// System.out.println(i + MessageI18N.getLocale("_maps_to:_") + e2.mapCol);
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
			// System.out.println(MessageI18N.getLocale("Row_") + i + MessageI18N.getLocale("_maps_to_") + ii);
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

		// if a matrix is linear, we can do the lu_factor here instead of
		// needing to do it every frame
		if (!circuitNonLinear) {
			if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
				stop(MessageI18N.getLocale("Singular_matrix!"), null);

				// fire circuit broken event here?
				// {source: simmer, component: ce, message: "Singular_matrix!"}
				return;
			}
		}

		// fire circuit working event here
		CircuitLibrary circuit = createCircuitLibrary(elmList);
		NativeJavascriptWrapper.fire("closed_circuit_signal", circuit);
	}

	protected boolean anySelectedButMouse() {
		for (int i = 0; i != elmList.size(); i++)
			if (getElm(i) != mouseElm && getElm(i).isSelected())
				return true;
		return false;
	}

    private Vector<String>					mainMenuItemNames	= new Vector<String>();
    private Vector<CheckboxMenuItem>		mainMenuItems		= new Vector<CheckboxMenuItem>();

	private void buildDrawMenu(MenuBar mainMenuBar) {
		mainMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Wire"), "WireElm"));
		mainMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Resistor"), "ResistorElm"));

		// Passive Components
		MenuBar passMenuBar = new MenuBar(true);
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Capacitor"), "CapacitorElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Inductor"), "InductorElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Switch"), "SwitchElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Push_Switch"), "PushSwitchElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_SPDT_Switch"), "Switch2Elm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Potentiometer"), "PotElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Transformer"), "TransformerElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Tapped_Transformer"), "TappedTransformerElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Transmission_Line"), "TransLineElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Relay"), "RelayElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Memristor"), "MemristorElm"));
		passMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Spark_Gap"), "SparkGapElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Passive_Components")), passMenuBar);

		// Inputs and Sources
		MenuBar inputMenuBar = new MenuBar(true);
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Ground"), "GroundElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Voltage_Source_(2-terminal)"), "DCVoltageElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_A/C_Voltage_Source_(2-terminal)"), "ACVoltageElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Voltage_Source_(1-terminal)"), "RailElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_A/C_Voltage_Source_(1-terminal)"), "ACRailElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Square_Wave_Source_(1-terminal)"), "SquareRailElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Clock"), "ClockElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_A/C_Sweep"), "SweepElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Variable_Voltage"), "VarRailElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Antenna"), "AntennaElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_AM_Source"), "AMElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_FM_Source"), "FMElm"));
		inputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Current_Source"), "CurrentElm"));

		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Inputs_and_Sources")), inputMenuBar);

		// Outputs and Labels
		MenuBar outputMenuBar = new MenuBar(true);
		outputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Analog_Output"), "OutputElm"));
		outputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_LED"), "LEDElm"));
		outputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Lamp_(beta)"), "LampElm"));
		outputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Text"), "TextElm"));
		outputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Box"), "BoxElm"));
		outputMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Scope_Probe"), "ProbeElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Outputs_and_Labels")), outputMenuBar);
		
		// Active Components
		MenuBar activeMenuBar = new MenuBar(true);
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Diode"), "DiodeElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Zener_Diode"), "ZenerElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Transistor_(bipolar_NPN)"), "NTransistorElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Transistor_(bipolar_PNP)"), "PTransistorElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_MOSFET_(N-Channel)"), "NMosfetElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_MOSFET_(P-Channel)"), "PMosfetElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_JFET_(N-Channel)"), "NJfetElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_JFET_(P-Channel)"), "PJfetElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_SCR"), "SCRElm"));
		// activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Varactor/Varicap"),
		// MessageI18N.getLocale("VaractorElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Tunnel_Diode"), "TunnelDiodeElm"));
		activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Triode"), "TriodeElm"));
		// activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Diac"), MessageI18N.getLocale("DiacElm"));
		// activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Triac"), MessageI18N.getLocale("TriacElm"));
		// activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Photoresistor"),
		// MessageI18N.getLocale("PhotoResistorElm"));
		// activeMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Thermistor"),
		// MessageI18N.getLocale("ThermistorElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Active_Components")), activeMenuBar);
		
		// Active Building Blocks
		MenuBar activeBlocMenuBar = new MenuBar(true);
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Op_Amp_(-_on_top)"), "OpAmpElm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Op_Amp_(+_on_top)"), "OpAmpSwapElm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Analog_Switch_(SPST)"), "AnalogSwitchElm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Analog_Switch_(SPDT)"), "AnalogSwitch2Elm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Tristate_Buffer"), "TriStateElm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Schmitt_Trigger"), "SchmittElm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Schmitt_Trigger_(Inverting)"), "InvertingSchmittElm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_CCII+"), "CC2Elm"));
		activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_CCII-"), "CC2NegElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Active_Building_Blocks")), activeBlocMenuBar);
		
		// Logic Gates, Input and Output
		MenuBar gateMenuBar = new MenuBar(true);
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Logic_Input"), "LogicInputElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Logic_Output"), "LogicOutputElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Inverter"), "InverterElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_NAND_Gate"), "NandGateElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_NOR_Gate"), "NorGateElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_AND_Gate"), "AndGateElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_OR_Gate"), "OrGateElm"));
		gateMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_XOR_Gate"), "XorGateElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Logic_Gates_Input_and_Output")), gateMenuBar);

		// Digital Chips
		MenuBar chipMenuBar = new MenuBar(true);
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_D_Flip-Flop"), "DFlipFlopElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_JK_Flip-Flop"), "JKFlipFlopElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_T_Flip-Flop"), "TFlipFlopElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_7_Segment_LED"), "SevenSegElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_7_Segment_Decoder"), "SevenSegDecoderElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Multiplexer"), "MultiplexerElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Demultiplexer"), "DeMultiplexerElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_SIPO_shift_register"), "SipoShiftElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_PISO_shift_register"), "PisoShiftElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Counter"), "CounterElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Decade_Counter"), "DecadeElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Latch"), "LatchElm"));
		// chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Static_RAM"), MessageI18N.getLocale("SRAMElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Sequence_generator"), "SeqGenElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Full_Adder"), "FullAdderElm"));
		chipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Half_Adder"), "HalfAdderElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Digital_Chips")), chipMenuBar);
		
		// Analog and Hybrid Chips
		MenuBar achipMenuBar = new MenuBar(true);
		achipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_555_Timer"), "TimerElm"));
		achipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Phase_Comparator"), "PhaseCompElm"));
		achipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_DAC"), "DACElm"));
		achipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_ADC"), "ADCElm"));
		achipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_VCO"), "VCOElm"));
		achipMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Add_Monostable"), "MonostableElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Analog_and_Hybrid_Chips")), achipMenuBar);
		
		// Drag
		MenuBar otherMenuBar = new MenuBar(true);
		CheckboxMenuItem mi;
		otherMenuBar.addItem(mi = getClassCheckItem(MessageI18N.getLocale("Drag_All"), "DragAll"));
		mi.addShortcut("(Alt-drag)");
		otherMenuBar.addItem(mi = getClassCheckItem(MessageI18N.getLocale("Drag_Row"), "DragRow"));
		mi.addShortcut("(S-right)");
		otherMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Drag_Column"), "DragColumn"));
		otherMenuBar.addItem(getClassCheckItem(MessageI18N.getLocale("Drag_Selected"), "DragSelected"));
		otherMenuBar.addItem(mi = getClassCheckItem(MessageI18N.getLocale("Drag_Post"), "DragPost"));
		mi.addShortcut("(" + ctrlMetaKey + "-drag)");

		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getLocale("Drag")), otherMenuBar);

		mainMenuBar.addItem(mi = getClassCheckItem("Select/Drag Sel", "Select"));
		mi.addShortcut("(space or Shift-drag)");
	}

    private MenuBar							elmMenuBar;
    private MenuItem						elmCopyMenuItem;
    private MenuItem						elmCutMenuItem;
    private MenuItem						elmDeleteMenuItem;
    private MenuItem						elmEditMenuItem;
    private MenuItem						elmScopeMenuItem;
	private MenuBar buildElementPopupMenu(){
		MenuBar elmMenuBar = new MenuBar(true);

		elmMenuBar.addItem(elmEditMenuItem = new MenuItem(MessageI18N.getLocale("Edit"), new MenuCommand("elm", "edit")));
		elmMenuBar.addItem(elmScopeMenuItem = new MenuItem(MessageI18N.getLocale("View_in_Scope"), new MenuCommand("elm", "viewInScope")));
		elmMenuBar.addItem(elmCutMenuItem = new MenuItem(MessageI18N.getLocale("Cut"), new MenuCommand("elm", "cut")));
		elmMenuBar.addItem(elmCopyMenuItem = new MenuItem(MessageI18N.getLocale("Copy"), new MenuCommand("elm", "copy")));
		elmMenuBar.addItem(elmDeleteMenuItem = new MenuItem(MessageI18N.getLocale("Delete"), new MenuCommand("elm", "delete")));

		return elmMenuBar;
	}
    /** Scope.java **/

    private CheckboxMenuItem				scopeFreqMenuItem;
    private CheckboxMenuItem				scopeIbMenuItem;
    private CheckboxMenuItem				scopeIcMenuItem;
    private CheckboxMenuItem				scopeIeMenuItem;
    private CheckboxMenuItem				scopeIMenuItem;
    private CheckboxMenuItem				scopeMaxMenuItem;
    private MenuBar							scopeMenuBar;
    private CheckboxMenuItem				scopeMinMenuItem;
    private CheckboxMenuItem				scopePowerMenuItem;

    private CheckboxMenuItem				scopeResistMenuItem;
    private Scope							scopes[];
    private CheckboxMenuItem				scopeScaleMenuItem;
    private MenuItem						scopeSelectYMenuItem;

    private CheckboxMenuItem				scopeVbcMenuItem;
    private CheckboxMenuItem				scopeVbeMenuItem;

    private CheckboxMenuItem				scopeVceIcMenuItem;
    private CheckboxMenuItem				scopeVceMenuItem;

    private CheckboxMenuItem				scopeVIMenuItem;
    private CheckboxMenuItem				scopeVMenuItem;
    private CheckboxMenuItem				scopeXYMenuItem;

	private MenuBar buildScopeMenu(boolean t) {
		MenuBar m = new MenuBar(true);
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Remove"), new MenuCommand("scopepop", "remove")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Speed_2x"), new MenuCommand("scopepop", "speed2")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Speed_1/2x"), new MenuCommand("scopepop", "speed1/2")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Scale_2x"), new MenuCommand("scopepop", "scale")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Max_Scale"), new MenuCommand("scopepop", "maxscale")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Stack"), new MenuCommand("scopepop", "stack")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Unstack"), new MenuCommand("scopepop", "unstack")));
		m.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Reset"), new MenuCommand("scopepop", "reset")));
		if (t) {
			m.addItem(scopeIbMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Ib"), new MenuCommand("scopepop", "showib")));
			m.addItem(scopeIcMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Ic"), new MenuCommand("scopepop", "showic")));
			m.addItem(scopeIeMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Ie"), new MenuCommand("scopepop", "showie")));
			m.addItem(scopeVbeMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Vbe"), new MenuCommand("scopepop", "showvbe")));
			m.addItem(scopeVbcMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Vbc"), new MenuCommand("scopepop", "showvbc")));
			m.addItem(scopeVceMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Vce"), new MenuCommand("scopepop", "showvce")));
			m.addItem(scopeVceIcMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Vce_vs_Ic"), new MenuCommand("scopepop", "showvcevsic")));
		} else {
			m.addItem(scopeVMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Voltage"), new MenuCommand("scopepop", "showvoltage")));
			m.addItem(scopeIMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Current"), new MenuCommand("scopepop", "showcurrent")));
			m.addItem(scopePowerMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Power_Consumed"), new MenuCommand("scopepop", "showpower")));
			m.addItem(scopeScaleMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Scale"), new MenuCommand("scopepop", "showscale")));
			m.addItem(scopeMaxMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Peak_Value"), new MenuCommand("scopepop", "showpeak")));
			m.addItem(scopeMinMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Negative_Peak_Value"), new MenuCommand("scopepop", "shownegpeak")));
			m.addItem(scopeFreqMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Frequency"), new MenuCommand("scopepop", "showfreq")));
			m.addItem(scopeVIMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_V_vs_I"), new MenuCommand("scopepop", "showvvsi")));
			m.addItem(scopeXYMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Plot_X/Y"), new MenuCommand("scopepop", "plotxy")));
			m.addItem(scopeSelectYMenuItem = new CheckboxAlignedMenuItem(MessageI18N.getLocale("Select_Y"), new MenuCommand("scopepop", "selecty")));
			m.addItem(scopeResistMenuItem = new CheckboxMenuItem(MessageI18N.getLocale("Show_Resistance"), new MenuCommand("scopepop", "showresistance")));
		}
		return m;
	}
	// end scope.java

	private void calcCircuitBottom() {
		int i;
		circuitBottom = 0;
		for (i = 0; i != elmList.size(); i++) {
			Rectangle rect = getElm(i).getBoundingBox();
			int bottom = rect.height + rect.y;
			if (bottom > circuitBottom)
				circuitBottom = bottom;
		}
	}

	protected void centreCircuit() {
		// void handleResize() {
		// winSize = cv.getSize();
		// if (winSize.width == 0)
		// return;
		// dbimage = main.createImage(winSize.width, winSize.height);
		// int h = winSize.height / 5;
		/*
		 * if (h < 128 && winSize.height > 300) h = 128;
		 */
		// circuitArea = new Rectangle(0, 0, winSize.width, winSize.height-h);
		int i;
		int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			// centered text causes problems when trying to center the circuit,
			// so we special-case it here
			if (!ce.isCenteredText()) {
				minx = min(ce.getX1(), min(ce.getX2(), minx));
				maxx = max(ce.getX1(), max(ce.getX2(), maxx));
			}
			miny = min(ce.getY1(), min(ce.getY2(), miny));
			maxy = max(ce.getY1(), max(ce.getY2(), maxy));
		}
		// center circuit; we don't use snapGrid() because that rounds
		int dx = gridMask & ((circuitArea.width - (maxx - minx)) / 2 - minx);
		int dy = gridMask & ((circuitArea.height - (maxy - miny)) / 2 - miny);
		if (dx + minx < 0)
			dx = gridMask & (-minx);
		if (dy + miny < 0)
			dy = gridMask & (-miny);
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			ce.move(dx, dy);
		}
		// after moving elements, need this to avoid singular matrix probs
		needAnalyze();
		circuitBottom = 0;
	}

	public void createNewLoadFile() {
		// This is a hack to fix what IMHO is a bug in the <INPUT FILE element
		// reloading the same file doesn't create a change event so importing
		// the same file twice
		// doesn't work unless you destroy the original input element and
		// replace it with a new one
		int idx = verticalPanel.getWidgetIndex(loadFileInput);
		LoadFile newlf = new LoadFile(this);
		verticalPanel.insert(newlf, idx);
		verticalPanel.remove(idx + 1);
		loadFileInput = newlf;
	}

	protected boolean dialogIsShowing() {
		if (getEditDialog() != null && getEditDialog().isShowing())
			return true;
		if (exportAsUrlDialog != null && exportAsUrlDialog.isShowing())
			return true;
		if (exportAsTextDialog != null && exportAsTextDialog.isShowing())
			return true;
		if (exportAsLocalFileDialog != null && exportAsLocalFileDialog.isShowing())
			return true;
		if (contextPanel != null && contextPanel.isShowing())
			return true;
		if (scrollValuePopup != null && scrollValuePopup.isShowing())
			return true;
		if (aboutBox != null && aboutBox.isShowing())
			return true;
		if (importFromTextDialog != null && importFromTextDialog.isShowing())
			return true;
		return false;
	}

	protected int distanceSq(int x1, int y1, int x2, int y2) {
		x2 -= x1;
		y2 -= y1;
		return x2 * x2 + y2 * y2;
	}

	protected void doMainMenuChecks() {
		int c = mainMenuItems.size();
		int i;
		for (i = 0; i < c; i++)
			mainMenuItems.get(i).setState(mainMenuItemNames.get(i) == mouseModeStr);
	}

	protected boolean doSwitch(int x, int y) {
		if (mouseElm == null || !(mouseElm instanceof SwitchElm))
			return false;
		SwitchElm se = (SwitchElm) mouseElm;
		se.toggle();
		if (se.isMomentary())
			heldSwitchElm = se;
		needAnalyze();
		return true;
	}

	private void dragAll(int x, int y) {
		int dx = x - dragX;
		int dy = y - dragY;
		if (dx == 0 && dy == 0)
			return;
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			ce.move(dx, dy);
		}
		removeZeroLengthElements();
	}

	private void dragColumn(int x, int y) {
		int dx = x - dragX;
		if (dx == 0)
			return;
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.getX1() == dragX)
				ce.movePoint(0, dx, 0);
			if (ce.getX2() == dragX)
				ce.movePoint(1, dx, 0);
		}
		removeZeroLengthElements();
	}

	private void dragPost(int x, int y) {
		if (draggingPost == -1) {
			draggingPost = (distanceSq(mouseElm.getX1(), mouseElm.getY1(), x, y) > distanceSq(mouseElm.getX2(), mouseElm.getY2(), x, y)) ? 1 : 0;
		}
		int dx = x - dragX;
		int dy = y - dragY;
		if (dx == 0 && dy == 0)
			return;
		mouseElm.movePoint(draggingPost, dx, dy);
		needAnalyze();
	}

	private void dragRow(int x, int y) {
		int dy = y - dragY;
		if (dy == 0)
			return;
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.getY1() == dragY)
				ce.movePoint(0, 0, dy);
			if (ce.getY2() == dragY)
				ce.movePoint(1, 0, dy);
		}
		removeZeroLengthElements();
	}

	private boolean dragSelected(int x, int y) {
		boolean me = false;
		if (mouseElm != null && !mouseElm.isSelected())
			mouseElm.setSelected(me = true);

		// snap grid, unless we're only dragging text elements
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.isSelected() && !(ce instanceof GraphicElm))
				break;
		}
		if (i != elmList.size()) {
			x = snapGrid(x);
			y = snapGrid(y);
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
			AbstractCircuitElement ce = getElm(i);
			if (ce.isSelected() && !ce.allowMove(dx, dy))
				allowed = false;
		}

		if (allowed) {
			for (i = 0; i != elmList.size(); i++) {
				AbstractCircuitElement ce = getElm(i);
				if (ce.isSelected())
					ce.move(dx, dy);
			}
			needAnalyze();
		}

		// don't leave mouseElm selected if we selected it above
		if (me)
			mouseElm.setSelected(false);

		return allowed;
	}

	// this is the file generation logic!  :)
    protected void doExportAsLocalFile() {
        String dump = dumpCircuit();
        exportAsLocalFileDialog = new ExportAsLocalFileDialog(dump);
        exportAsLocalFileDialog.show();
    }

    protected void doExportAsText() {
        String dump = dumpCircuit();
        exportAsTextDialog = new ExportAsTextDialog(dump);
        exportAsTextDialog.show();
    }

    protected void doExportAsUrl() {
        String start[] = Location.getHref().split("\\?");
        String dump = dumpCircuit();
        dump = dump.replace(' ', '+');
        dump = start[0] + "?cct=" + URL.encode(dump);
        exportAsUrlDialog = new ExportAsUrlDialog(dump);
        exportAsUrlDialog.show();
    }

	public String dumpCircuit() {
		int i;
		int f = (getDotsCheckItem().getState()) ? 1 : 0;
		f |= (getSmallGridCheckItem().getState()) ? 2 : 0;
		f |= (getVoltsCheckItem().getState()) ? 0 : 4;
		f |= (getPowerCheckItem().getState()) ? 8 : 0;
		f |= (getShowValuesCheckItem().getState()) ? 0 : 16;
		// 32 = linear scale in afilter
		String dump = "$ " + f + " " + getTimeStep() + " " + getIterCount() + " " + currentBar.getValue() + " " + AbstractCircuitElement.voltageRange + " " + powerBar.getValue() + "\n";

		for (i = 0; i != elmList.size(); i++)
			dump += getElm(i).dump() + "\n";

		for (i = 0; i != scopeCount; i++) {
			String d = scopes[i].dump();
			if (d != null)
				dump += d + "\n";
		}
		if (hintType != HintType.HINT_UNSET)
			dump += "h " + hintType + " " + hintItem1 + " " + hintItem2 + "\n";
		return dump;
	}
	// end file generation logic

	public CircuitNode getCircuitNode(int n) {
		if (n >= getNodeList().size())
			return null;
		return getNodeList().elementAt(n);
	}

	private CheckboxMenuItem getClassCheckItem(String s, String t) {
		String shortcut = "";
		AbstractCircuitElement elm = CircuitElementFactory.constructElement(t, 0, 0);
		CheckboxMenuItem mi;

		// register(c, elm);
		if (elm != null) {
			if (elm.needsShortcut()) {
				shortcut += (char) elm.getShortcut();
				shortcuts[elm.getShortcut()] = t;
			}
			elm.delete();
		}

		if (shortcut == "")
			mi = new CheckboxMenuItem(s);
		else
			mi = new CheckboxMenuItem(s, shortcut);
		
		mi.setScheduledCommand(new MenuCommand("main", t));
		mainMenuItems.add(mi);
		mainMenuItemNames.add(t);
		return mi;
	}

	private String getHint() {
		AbstractCircuitElement c1 = getElm(hintItem1.getValue());
		AbstractCircuitElement c2 = getElm(hintItem2.getValue());

		if (c1 == null || c2 == null)
			return null;

		if (hintType == HintType.HINT_LC) {
			if (!(c1 instanceof InductorElm))
				return null;

			if (!(c2 instanceof CapacitorElm))
				return null;

			InductorElm ie = (InductorElm) c1;
			CapacitorElm ce = (CapacitorElm) c2;

			return "res.f = " + AbstractCircuitElement.getUnitText(1 / (2 * Math.PI * Math.sqrt(ie.getInductance() * ce.getCapacitance())), "Hz");
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

			return "f.3db = " + AbstractCircuitElement.getUnitText(1 / (2 * Math.PI * re.getResistance() * ce.getCapacitance()), "Hz");
		}
		if (hintType == HintType.HINT_3DB_L) {
			if (!(c1 instanceof ResistorElm))
				return null;

			if (!(c2 instanceof InductorElm))
				return null;

			ResistorElm re = (ResistorElm) c1;
			InductorElm ie = (InductorElm) c2;

			return "f.3db = " + AbstractCircuitElement.getUnitText(re.getResistance() / (2 * Math.PI * ie.getInductance()), "Hz");
		}
		if (hintType == HintType.HINT_TWINT) {
			if (!(c1 instanceof ResistorElm))
				return null;

			if (!(c2 instanceof CapacitorElm))
				return null;

			ResistorElm re = (ResistorElm) c1;
			CapacitorElm ce = (CapacitorElm) c2;

			return "fc = " + AbstractCircuitElement.getUnitText(1 / (2 * Math.PI * re.getResistance() * ce.getCapacitance()), "Hz");
		}
		return null;
	}

	private double getIterCount() {
		if (speedBar.getValue() == 0)
			return 0;

		return .1 * Math.exp((speedBar.getValue() - 61) / 24.);

	}

	public int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}

	private void getSetupList(final boolean openDefault) {

		String url = GWT.getModuleBaseURL();
		url = url.substring(0,url.indexOf("circuitjs1"));
		url = url +  "setuplist.txt" + "?v=" + random.nextInt();
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log(MessageI18N.getLocale("File_Error_Response"), exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// processing goes here
					if (response.getStatusCode() == Response.SC_OK) {
						String text = response.getText();
						processSetupList(text.getBytes(), text.length(), openDefault);
						// end or processing
					} else
						GWT.log(MessageI18N.getLocale("Bad_file_server_response") + response.getStatusText());
				}
			});
		} catch (RequestException e) {
			GWT.log(MessageI18N.getLocale("failed_file_reading"), e);
		}
		
		String s = "";
		if( s != null && s.isEmpty() && Character.isUpperCase(s.charAt(0))){
			
		}
	}


	/** Options Menu **/
    private CheckboxMenuItem				powerCheckItem;

    public CheckboxMenuItem getPowerCheckItem() {
        return powerCheckItem;
    }

    private CheckboxMenuItem setPowerCheckItem(CheckboxMenuItem powerCheckItem) {
        this.powerCheckItem = powerCheckItem;
        return powerCheckItem;
    }

    public void setPowerBarEnable() {
        if (getPowerCheckItem().getState()) {
            powerLabel.setStyleName("disabled", false);
            powerBar.enable();
        } else {
            powerLabel.setStyleName("disabled", true);
            powerBar.disable();
        }
    }

    private void enableItems() {
        // if (powerCheckItem.getState()) {
        // powerBar.enable();
        // powerLabel.enable();
        // } else {
        // powerBar.disable();
        // powerLabel.disable();
        // }
        // enableUndoRedo();
    }


    private MenuBar buildOptionsMenu() {
		
		MenuBar optionsMenuBar = new MenuBar(true);
		optionsMenuBar.addItem(setDotsCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Current"))));
		getDotsCheckItem().setState(true);
		optionsMenuBar.addItem(setVoltsCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Voltage"), new Command() {
			public void execute() {
				if (getVoltsCheckItem().getState())
					getPowerCheckItem().setState(false);
				setPowerBarEnable();
			}
		})));
		optionsMenuBar.addItem(setPowerCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Power"), new Command() {
			public void execute() {
				if (getPowerCheckItem().getState())
					getVoltsCheckItem().setState(false);
				setPowerBarEnable();
			}
		})));
		optionsMenuBar.addItem(setShowValuesCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Values"))));
		// m.add(conductanceCheckItem = getCheckItem(MessageI18N.getLocale("Show_Conductance")));
		optionsMenuBar.addItem(setSmallGridCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Small_Grid"), new Command() {
			public void execute() {
				setGrid();
			}
		})));
		optionsMenuBar.addItem(setEuroResistorCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("European_Resistors"))));
		optionsMenuBar.addItem(setPrintableCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("White_Background"), new Command() {
			public void execute() {
				for (int i = 0; i < scopeCount; i++)
					scopes[i].setRect(scopes[i].getRect());
			}
		})));
		optionsMenuBar.addItem(setConventionCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Conventional_Current_Motion"))));
		optionsMenuBar.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Other_Options..."), new MenuCommand("options", "other")));
		
		return optionsMenuBar;
	}
	/** end options menu **/


	private void bindEventHandlers() {
		cv.addMouseDownHandler(simmerController);
		cv.addMouseMoveHandler(simmerController);
		cv.addMouseUpHandler(simmerController);
		cv.addClickHandler(simmerController);
		cv.addDoubleClickHandler(simmerController);
		cv.addDomHandler(simmerController, ContextMenuEvent.getType());
		menuBar.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doMainMenuChecks();
			}
		}, ClickEvent.getType());
		Event.addNativePreviewHandler(simmerController);
		cv.addMouseWheelHandler(simmerController);
	}

	/** sidebar **/
    private Scrollbar						powerBar;
    private Label							powerLabel;
    private CheckboxMenuItem				printableCheckItem;
    private VerticalPanel					verticalPanel;
    private Button							resetButton;
    private Scrollbar                       currentBar;
    private Checkbox						stoppedCheck;
    private Frame							iFrame;

    protected void setStoppedCheck(Checkbox stoppedCheck) {
        this.stoppedCheck = stoppedCheck;
    }

    public void setiFrameHeight() {
        if (iFrame == null)
            return;
        int i;
        int cumheight = 0;
        for (i = 0; i < verticalPanel.getWidgetIndex(iFrame); i++) {
            if (verticalPanel.getWidget(i) != loadFileInput) {
                cumheight = cumheight + verticalPanel.getWidget(i).getOffsetHeight();
                if (verticalPanel.getWidget(i).getStyleName().contains("topSpace"))
                    cumheight += 12;
            }
        }
        // int
        // ih=RootLayoutPanel.get().getOffsetHeight()-(iFrame.getAbsoluteTop()-RootLayoutPanel.get().getAbsoluteTop());
        int ih = RootLayoutPanel.get().getOffsetHeight() - Display.MENUBARHEIGHT - cumheight;
        // GWT.log(MessageI18N.getLocale("Root_OH=")+RootLayoutPanel.get().getOffsetHeight());
        // GWT.log(MessageI18N.getLocale("iF_top=")+iFrame.getAbsoluteTop() );
        // GWT.log(MessageI18N.getLocale("RP_top=")+RootLayoutPanel.get().getAbsoluteTop());
        // GWT.log(MessageI18N.getLocale("ih=")+ih);
        // GWT.log(MessageI18N.getLocale("if_left=")+iFrame.getAbsoluteLeft());
        if (ih < 0)
            ih = 0;
        iFrame.setHeight(ih + "px");
    }

    public void removeWidgetFromVerticalPanel(Widget w) {
        verticalPanel.remove(w);
        if (iFrame != null)
            setiFrameHeight();
    }

    public void addWidgetToVerticalPanel(Widget w) {
        if (iFrame != null) {
            int i = verticalPanel.getWidgetIndex(iFrame);
            verticalPanel.insert(w, i);
            setiFrameHeight();
        } else
            verticalPanel.add(w);
    }

    private void createSideBar() {
		verticalPanel.add(resetButton = new Button(MessageI18N.getLocale("Reset")));
		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				resetAction();
			}
		});
		// dumpMatrixButton = new Button(MessageI18N.getLocale("Dump_Matrix"));
		// main.add(dumpMatrixButton);// IES for debugging
		setStoppedCheck(new Checkbox(MessageI18N.getLocale("Stopped")));
		verticalPanel.add(getStoppedCheck());

		if (LoadFile.isSupported())
			verticalPanel.add(loadFileInput = new LoadFile(this));

		Label l;
		verticalPanel.add(l = new Label(MessageI18N.getLocale("Simulation_Speed")));
		l.addStyleName(MessageI18N.getLocale("topSpace"));

		// was max of 140
		verticalPanel.add(speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260));

		verticalPanel.add(l = new Label(MessageI18N.getLocale("Current_Speed")));
		l.addStyleName("topSpace");
		currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
		verticalPanel.add(currentBar);
		verticalPanel.add(powerLabel = new Label(MessageI18N.getLocale("Power_Brightness")));
		powerLabel.addStyleName("topSpace");
		verticalPanel.add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100));
		setPowerBarEnable();
		verticalPanel.add(iFrame = new Frame("iframe.html"));
		iFrame.setWidth(Display.VERTICALPANELWIDTH + "px");
		iFrame.setHeight("100 px");
		iFrame.getElement().setAttribute("scrolling","no");
	}
    /** end sidebar **/

	private void loadFileFromURL(String url, final boolean centre) {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log(MessageI18N.getLocale("File_Error_Response"), exception);
				}

				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						String text = response.getText();
						readSetup(text.getBytes(), text.length(), false, centre);
					} else
						GWT.log(MessageI18N.getLocale("Bad_file_server_response") + response.getStatusText());
				}
			});
		} catch (RequestException e) {
			GWT.log(MessageI18N.getLocale("failed_file_reading"), e);
		}

	}

	public int locateElm(AbstractCircuitElement elm) {
		int i;
		for (i = 0; i != elmList.size(); i++)
			if (elm == elmList.elementAt(i))
				return i;
		return -1;
	}

	// factors a matrix into upper and lower triangular matrices by
	// gaussian elimination. On entry, a[0..n-1][0..n-1] is the
	// matrix to be factored. ipvt[] returns an integer vector of pivot
	// indices, used in the lu_solve() routine.
	private boolean lu_factor(double a[][], int n, int ipvt[]) {
		double scaleFactors[];
		int i, j, k;

		scaleFactors = new double[n];

		// divide each row by its largest element, keeping track of the
		// scaling factors
		for (i = 0; i != n; i++) {
			double largest = 0;
			for (j = 0; j != n; j++) {
				double x = Math.abs(a[i][j]);
				if (x > largest)
					largest = x;
			}
			// if all zeros, it's a singular matrix
			if (largest == 0)
				return false;
			scaleFactors[i] = 1.0 / largest;
		}

		// use Crout's method; loop through the columns
		for (j = 0; j != n; j++) {

			// calculate upper triangular elements for this column
			for (i = 0; i != j; i++) {
				double q = a[i][j];
				for (k = 0; k != i; k++)
					q -= a[i][k] * a[k][j];
				a[i][j] = q;
			}

			// calculate lower triangular elements for this column
			double largest = 0;
			int largestRow = -1;
			for (i = j; i != n; i++) {
				double q = a[i][j];
				for (k = 0; k != j; k++)
					q -= a[i][k] * a[k][j];
				a[i][j] = q;
				double x = Math.abs(q);
				if (x >= largest) {
					largest = x;
					largestRow = i;
				}
			}

			// pivoting
			if (j != largestRow) {
				double x;
				for (k = 0; k != n; k++) {
					x = a[largestRow][k];
					a[largestRow][k] = a[j][k];
					a[j][k] = x;
				}
				scaleFactors[largestRow] = scaleFactors[j];
			}

			// keep track of row interchanges
			ipvt[j] = largestRow;

			// avoid zeros
			if (a[j][j] == 0.0) {
				System.out.println(MessageI18N.getLocale("avoided_zero"));
				a[j][j] = 1e-18;
			}

			if (j != n - 1) {
				double mult = 1.0 / a[j][j];
				for (i = j + 1; i != n; i++)
					a[i][j] *= mult;
			}
		}

		return true;
	}

	// Solves the set of n linear equations using a LU factorization
	// previously performed by lu_factor. On input, b[0..n-1] is the right
	// hand side of the equations, and on output, contains the solution.
	private void lu_solve(double a[][], int n, int ipvt[], double b[]) {
		int i;

		// find first nonzero b element
		for (i = 0; i != n; i++) {
			int row = ipvt[i];

			double swap = b[row];
			b[row] = b[i];
			b[i] = swap;
			if (swap != 0)
				break;
		}

		int bi = i++;
		for (; i < n; i++) {
			int row = ipvt[i];
			int j;
			double tot = b[row];

			b[row] = b[i];
			// forward substitution using the lower triangular matrix
			for (j = bi; j < i; j++)
				tot -= a[i][j] * b[j];
			b[i] = tot;
		}
		for (i = n - 1; i >= 0; i--) {
			double tot = b[i];

			// back-substitution using the upper triangular matrix
			int j;
			for (j = i + 1; j != n; j++)
				tot -= a[i][j] * b[j];
			b[i] = tot / a[i][i];
		}
	}

	int max(int a, int b) {
		return (a > b) ? a : b;
	}

	int min(int a, int b) {
		return (a < b) ? a : b;
	}

	public void mouseDragged(MouseMoveEvent e) {
		// ignore right mouse button with no modifiers (needed on PC)
		if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			if (!(e.isMetaKeyDown() || e.isShiftKeyDown() || e.isControlKeyDown() || e.isAltKeyDown()))
				return;
		}

		if (!circuitArea.contains(e.getX(), e.getY()))
			return;
		if (dragElm != null)
			dragElm.drag(e.getX(), e.getY());

		boolean success = true;
		switch (tempMouseMode) {
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
					tempMouseMode = MouseMode.DRAG_SELECTED;
					success = dragSelected(e.getX(), e.getY());
				}
				break;
			case DRAG_SELECTED:
				success = dragSelected(e.getX(), e.getY());
				break;
			case ADD_ELM:
				break;
		}
		dragging = true;
		if (success) {
			if (tempMouseMode == MouseMode.DRAG_SELECTED && mouseElm instanceof GraphicElm) {
				dragX = e.getX();
				dragY = e.getY();
			} else {
				dragX = snapGrid(e.getX());
				dragY = snapGrid(e.getY());
			}
		}
		// cv.repaint(pause);
	}

	public void needAnalyze() {
		analyzeFlag = true;
		// cv.repaint();
	}

	private void processSetupList(byte b[], int len, final boolean openDefault) {
		MenuBar currentMenuBar;
		MenuBar stack[] = new MenuBar[6];
		int stackptr = 0;
		currentMenuBar = new MenuBar(true);
		currentMenuBar.setAutoOpen(true);
		menuBar.addItem(MessageI18N.getLocale("Circuits"), currentMenuBar);
		stack[stackptr++] = currentMenuBar;
		int p;
		for (p = 0; p < len;) {
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
					// menu.add(getMenuItem(title, MessageI18N.getLocale("setup_") + file));
					currentMenuBar.addItem(new MenuItem(title, new MenuCommand("circuits", "setup " + file)));
					if (first && startCircuit == null) {
						startCircuit = file;
						startLabel = title;
						if (openDefault && stopMessage == null)
							readSetupFile(startCircuit, startLabel, true);
					}
				}
			}
			p += l;
		}
	}

	private void readHint(StringTokenizer st) {
		hintType = hintType.getHintFromValue(new Integer(st.nextToken()).intValue());
		hintItem1 = hintType.getHintFromValue(new Integer(st.nextToken()).intValue());
		hintItem2 = hintType.getHintFromValue(new Integer(st.nextToken()).intValue());
	}

	private void readOptions(StringTokenizer st) {
		int flags = new Integer(st.nextToken()).intValue();
		// IES - remove inteaction
		getDotsCheckItem().setState((flags & 1) != 0);
		getSmallGridCheckItem().setState((flags & 2) != 0);
		getVoltsCheckItem().setState((flags & 4) == 0);
		getPowerCheckItem().setState((flags & 8) == 8);
		getShowValuesCheckItem().setState((flags & 16) == 0);
		setTimeStep(new Double(st.nextToken()).doubleValue());
		double sp = new Double(st.nextToken()).doubleValue();
		int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
		// int sp2 = (int) (Math.log(sp)*24+1.5);
		speedBar.setValue(sp2);
		currentBar.setValue(new Integer(st.nextToken()).intValue());
		AbstractCircuitElement.voltageRange = new Double(st.nextToken()).doubleValue();

		try {
			powerBar.setValue(new Integer(st.nextToken()).intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setGrid();
	}

	private void readSetup(byte b[], int len, boolean retain, boolean centre) {
		int i;
		if (!retain) {
			for (i = 0; i != elmList.size(); i++) {
				AbstractCircuitElement ce = getElm(i);
				ce.delete();
			}
			elmList.removeAllElements();
			hintType = HintType.HINT_UNSET;
			setTimeStep(5e-6);
			getDotsCheckItem().setState(false);
			getSmallGridCheckItem().setState(false);
			getPowerCheckItem().setState(false);
			getVoltsCheckItem().setState(true);
			getShowValuesCheckItem().setState(true);
			setGrid();
			speedBar.setValue(117); // 57
			currentBar.setValue(50);
			powerBar.setValue(50);
			AbstractCircuitElement.voltageRange = 5;
			scopeCount = 0;
		}
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
			StringTokenizer st = new StringTokenizer(line, " +\t\n\r\f");
			while (st.hasMoreTokens()) {
				String type = st.nextToken();
				int tint = type.charAt(0);
				try {
					if (tint == 'o') {
						Scope sc = new Scope(this);
						sc.setPosition(scopeCount);
						sc.undump(st);
						scopes[scopeCount++] = sc;
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
						System.out.println(MessageI18N.getLocale("unrecognized_dump_type_") + type);
						break;
					}
					newce.setPoints();
					elmList.addElement(newce);
					// } catch (java.lang.reflect.InvocationTargetException ee)
					// {
					// ee.getTargetException().printStackTrace();
					// break;
				} catch (Exception ee) {
					ee.printStackTrace();
					break;
				}
				break;
			}
			p += l;

		}
		setPowerBarEnable();
		enableItems();
		// if (!retain)
		// handleResize(); // for scopes
		needAnalyze();
		if (centre)
			centreCircuit();
	}

	public void readSetup(String text, boolean centre) {
		readSetup(text, false, centre);
	}

	public void readSetup(String text, boolean retain, boolean centre) {
		readSetup(text.getBytes(), text.length(), retain, centre);
	}

	public void readSetupFile(String str, String title, boolean centre) {
		t = 0;
		System.out.println(str);
		// try {
		// TODO: Maybe think about some better approach to cache management!
		String url = GWT.getModuleBaseURL();
		url = url.substring(0,url.indexOf("circuitjs1"));
		url = url+ "circuits/" + str + "?v=" + random.nextInt();
		loadFileFromURL(url, centre);
	}

	public void removeZeroLengthElements() {
		int i;
		// boolean changed = false;
		for (i = elmList.size() - 1; i >= 0; i--) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.getX1() == ce.getX2() && ce.getY1() == ce.getY2()) {
				elmList.removeElementAt(i);
				// fire component removed event
				// {source: simmer, component: elmList.getElementAt(i)}
				ce.delete();
				// changed = true;
			}
		}
		needAnalyze();
	}

	public void resetAction() {
		int i;
		for (i = 0; i != elmList.size(); i++)
			getElm(i).reset();

		for (i = 0; i != scopeCount; i++)
			scopes[i].resetGraph();

        // TODO: Will need to do IE bug fix here?
		analyzeFlag = true;
		t = 0;
		getStoppedCheck().setState(false);
	}

	private void runCircuit() {
		if (circuitMatrix == null || elmList.size() == 0) {
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
			for (i = 0; i != elmList.size(); i++) {
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
				for (i = 0; i != elmList.size(); i++) {
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
							stop(MessageI18N.getLocale("nan/infinite_matrix!"), null);

							// fire circuit broken event here
							// {source: simmer, component: ce, message: "nan/infinite_matrix!"}
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
					if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
						stop(MessageI18N.getLocale("Singular_matrix!"), null);

						// fire circuit broken event here
						// {source: simmer, component: ce, message: "Singular_matrix!"}
						return;
					}
				}
				lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute, circuitRightSide);

				for (j = 0; j != circuitMatrixFullSize; j++) {
					RowInfo ri = circuitRowInfo[j];
					double res = 0;
					if (ri.getType() == RowInfo.ROW_CONST)
						res = ri.getValue();
					else
						res = circuitRightSide[ri.getMapCol()];
					/*
					 * System.out.println(j + "_" + res + "_" + ri.type + "_" +
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
							CircuitNodeLink cnl = (CircuitNodeLink) cn.links.elementAt(k);
							cnl.getElm().setNodeVoltage(cnl.getNum(), res);
						}
					} else {
						int ji = j - (getNodeList().size() - 1);
						// System.out.println(MessageI18N.getLocale("setting_vsrc_") + ji + MessageI18N.getLocale("_to_") +
						// res);
						voltageSources[ji].setCurrent(ji, res);
					}
				}
				if (!circuitNonLinear)
					break;
			}
			if (subiter > 5)
				System.out.print(MessageI18N.getLocale("converged_after_") + subiter + MessageI18N.getLocale("_iterations")+"\n");
			if (subiter == subiterCount) {
				stop(MessageI18N.getLocale("Convergence_failed!"), null);
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

	private void selectArea(int x, int y) {
		int x1 = min(x, initDragX);
		int x2 = max(x, initDragX);
		int y1 = min(y, initDragY);
		int y2 = max(y, initDragY);
		selectedArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			ce.selectRect(selectedArea);
		}
	}

	public void setCanvasSize() {
		int width, height;
		width = (int) RootLayoutPanel.get().getOffsetWidth();
		height = (int) RootLayoutPanel.get().getOffsetHeight();
		height = height - Display.MENUBARHEIGHT;
		width = width - Display.VERTICALPANELWIDTH;
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
		/*
		 * if (h < 128 && winSize.height > 300) h = 128;
		 */
		circuitArea = new Rectangle(0, 0, width, height - h);

	}

	private void setGrid() {
		gridSize = (getSmallGridCheckItem().getState()) ? 8 : 16;
		gridMask = ~(gridSize - 1);
		gridRound = gridSize / 2 - 1;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public void setMenuSelection() {
		if (menuElm != null) {
			if (menuElm.isSelected())
				return;
			editMenu.doSelectNone();
			menuElm.setSelected(true);
		}
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
			pos = max(scopes[i].getPosition(), pos);
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

	public int snapGrid(int x) {
		return (x + gridRound) & gridMask;
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
					// System.out.println(MessageI18N.getLocale("Stamping_constant_") + i + "_" + j +
					// "_" + x);
					circuitRightSide[i] -= x * ri.getValue();
					return;
				}
				j = ri.getMapCol();
				// System.out.println(MessageI18N.getLocale("stamping_") + i + "_" + j + "_" + x);
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
			System.out.print(MessageI18N.getLocale("bad_resistance_") + r + "_" + r0 + "\n");
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
		// System.out.println(MessageI18N.getLocale("rschanges_true_") + (i-1));
		if (i > 0)
			circuitRowInfo[i - 1].setRsChanges(true);
	}

	// stamp value x on the right side of row i, representing an
	// independent current source flowing into node i
	public void stampRightSide(int i, double x) {
		if (i > 0) {
			if (circuitNeedsMap) {
				i = circuitRowInfo[i - 1].getMapRow();
				// System.out.println(MessageI18N.getLocale("stamping_") + i + "_" + x);
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
		stopMessage = s;
		circuitMatrix = null;
		stopElm = ce;
		getStoppedCheck().setState(true);
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
		if (getPrintableCheckItem().getState()) {
			AbstractCircuitElement.whiteColor = Color.black;
			AbstractCircuitElement.lightGrayColor = Color.black;
			g.setColor(Color.white);
		} else {
			AbstractCircuitElement.whiteColor = Color.white;
			AbstractCircuitElement.lightGrayColor = Color.lightGray;
			g.setColor(Color.black);
		}
		g.fillRect(0, 0, g.getContext().getCanvas().getWidth(), g.getContext().getCanvas().getHeight());
		if (!getStoppedCheck().getState()) {
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
		if (!getStoppedCheck().getState()) {

			if (lastTime != 0) {
				int inc = (int) (sysTime - lastTime);
				double c = currentBar.getValue();
				c = java.lang.Math.exp(c / 3.5 - 14.2);
				AbstractCircuitElement.currentMult = 1.7 * inc * c;
				if (!getConventionCheckItem().getState())
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
		AbstractCircuitElement.powerMult = Math.exp((powerBar.getValue() / 4.762) - 7);

		int i;
		// Font oldfont = g.getFont();
		Font oldfont = AbstractCircuitElement.unitsFont;
		g.setFont(oldfont);
		// mydrawstarttime = System.currentTimeMillis();
		for (i = 0; i != elmList.size(); i++) {
			if (getPowerCheckItem().getState())
				g.setColor(Color.gray);
			/*
			 * else if (conductanceCheckItem.getState())
			 * g.setColor(Color.white);
			 */
			getElm(i).draw(g);
		}
		// mydrawtime += System.currentTimeMillis() - mydrawstarttime;
		if (tempMouseMode == MouseMode.DRAG_ROW || tempMouseMode == MouseMode.DRAG_COLUMN || tempMouseMode == MouseMode.DRAG_POST || tempMouseMode == MouseMode.DRAG_SELECTED)
			for (i = 0; i != elmList.size(); i++) {

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
		if (tempMouseMode ==MouseMode.SELECT && mouseElm != null) {
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
					CircuitNodeLink cnl = cn.links.elementAt(0);
					for (j = 0; j != elmList.size(); j++) { // TODO: (hausen)
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
			x = max(x, cv.getCoordinateSpaceWidth() * 2 / 3);
			// x=cv.getCoordinateSpaceWidth()*2/3;

			// count lines of data
			for (i = 0; info[i] != null; i++)
				;
			if (badnodes > 0)
				info[i++] = badnodes + ((badnodes == 1) ? MessageI18N.getLocale("_bad_connection") : MessageI18N.getLocale("_bad_connections"));

			// find where to show data; below circuit, not too high unless we
			// need it
			// int ybase = winSize.height-15*i-5;
			int ybase = cv.getCoordinateSpaceHeight() - 15 * i - 5;
			ybase = min(ybase, circuitArea.height);
			ybase = max(ybase, circuitBottom);
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
		mytime = mytime + System.currentTimeMillis() - mystarttime;
		// myframes++;
	}

	public void updateVoltageSource(int n1, int n2, int vs, double v) {
		int vn = getNodeList().size() + vs;
		stampRightSide(vn, v);
	}
   
	private CircuitLibrary createCircuitLibrary(Vector<AbstractCircuitElement>	elmList){
		CircuitLibrary circuit = new CircuitLibrary();
		//loop through the elmlist creating components and connection. 
		//all the properties of connection cannot be filled because we have to have 
		//all the components first found.
//		for(int i=0; i < elmList.size();i++){
//			AbstractCircuitElement elm = elmList.get(i);
//			if (!elm.isWire()){
//				CircuitComponent component = new CircuitComponent();
//				double uuid=Math.random();
//				component.setUUID(uuid);
//				component.setTypeClassName(elm.getClass().getName());
//				component.setBoundedBox(elm.getBoundingBox());
//				component.setTypeClass(elm.getClass());
//				circuit.put(uuid, component);
//			}
//			else {
//				Connection connection = new Connection();
//				double uuid=Math.random();
//				connection.setUUID(uuid);
//
//				circuit.put(uuid, connection);
//			}
//
//		}
		//now for each connetion find a componenet that intersects its bounding box
//		for (Map.Entry<Double, Identifiable> entry : circuit.entrySet()) {
//			if (entry.getValue() instanceof CircuitComponent) continue;
//			Connection connection =(Connection) entry.getValue();
//			int i=0;
//			for (Map.Entry<Double, Identifiable> insideEntry : circuit.entrySet()){
//				if (i==2) break;
//				if (insideEntry.getValue() instanceof CircuitComponent) {
//					if (connection.getBoundedBox().intersects(insideEntry.getValue().getBoundedBox())){
//						if (i==0){
//							connection.setSide1UUID(insideEntry.getKey());
//													}
//						else {
//							connection.setSide1UUID(insideEntry.getKey());
//						}
//						i++;
//					}
//				}
//			}
//		}
		
		return circuit;
	}

	public SimmerController getSimmerController(){
		return this.simmerController;
	}

	public LoadFile getLoadFileInput(){
		return this.loadFileInput;
	}

	public void setImportFromTextDialog(ImportFromTextDialog dialog){
		this.importFromTextDialog = dialog;
	}

	public void setIFrame(Frame iFrame){
		this.iFrame = iFrame;
	}

	public MouseMode getMouseMode() {
		return mouseMode;
	}

	public Vector<CircuitNode> getNodeList() {
		return nodeList;
	}

	private CheckboxMenuItem setConventionCheckItem(CheckboxMenuItem conventionCheckItem) {
		this.conventionCheckItem = conventionCheckItem;
		return conventionCheckItem;
	}

	private CheckboxMenuItem setDotsCheckItem(CheckboxMenuItem dotsCheckItem) {
		this.dotsCheckItem = dotsCheckItem;
		return dotsCheckItem;
	}

	// public void setElmList(Vector<AbstractCircuitElement> elmList) {
	// this.elmList = elmList;
	// }

	private CheckboxMenuItem setEuroResistorCheckItem(CheckboxMenuItem euroResistorCheckItem) {
		this.euroResistorCheckItem = euroResistorCheckItem;
		return euroResistorCheckItem;
	}

	public CheckboxMenuItem getPrintableCheckItem() {
		return printableCheckItem;
	}

	private CheckboxMenuItem setPrintableCheckItem(CheckboxMenuItem printableCheckItem) {
		this.printableCheckItem = printableCheckItem;
		return printableCheckItem;
	}

	protected CheckboxMenuItem setShowValuesCheckItem(CheckboxMenuItem showValuesCheckItem) {
		this.showValuesCheckItem = showValuesCheckItem;
		return showValuesCheckItem;
	}

	protected CheckboxMenuItem setSmallGridCheckItem(CheckboxMenuItem smallGridCheckItem) {
		this.smallGridCheckItem = smallGridCheckItem;
		return smallGridCheckItem;
	}

	private MenuBar buildEditMenu() {
		editMenu = new EditMenu(this);
		return editMenu;
	}

	public CheckboxMenuItem getShowValuesCheckItem() {
		return showValuesCheckItem;
	}

	public CheckboxMenuItem getSmallGridCheckItem() {
		return smallGridCheckItem;
	}

	public Checkbox getStoppedCheck() {
		return stoppedCheck;
	}

	public double getT() {
		return t;
	}

	public double getTimeStep() {
		return timeStep;
	}

	public CheckboxMenuItem getVoltsCheckItem() {
		return voltsCheckItem;
	}

	private EditMenu editMenu;

	public EditMenu getEditMenu(){
		return editMenu;
	};

	public int getScopeCount(){
		return this.scopeCount;
	}

	public Scope[] getScopes(){
		return scopes;
	}

	public Scope getScope(int idx){
		return scopes[idx];
	}


	protected void setAboutBox(AboutBox aboutBox){
		this.aboutBox = aboutBox;
	}

	public int getScopeSelected(){
		return scopeSelected;
	}

	public String[] getShortcuts(){
		return this.shortcuts;
	}

	protected void setScrollValuePopup(ScrollValuePopup popup){
		Simmer.scrollValuePopup = popup;
	}

	protected AbstractCircuitElement getMouseElm(){
		return mouseElm;
	}

	protected AbstractCircuitElement getDragElm(){
		return this.dragElm;
	}

	protected void setMouseDragging(boolean state){
		this.mouseDragging = state;
	}

	protected boolean isMouseDragging(){
		return this.mouseDragging;
	}

	protected void setDragging(boolean state){
		this.dragging = state;
	}

	protected void setDragElm(AbstractCircuitElement elm){
		this.dragElm = elm;
	}

	protected void setPlotXElm(AbstractCircuitElement elm){
		this.plotXElm = elm;
	}

	protected void setPlotYElm(AbstractCircuitElement elm){
		this.plotYElm = elm;
	}

	protected void setScopeSelected(int nbr){
		this.scopeSelected = nbr;
	}

	public Rectangle getCircuitArea(){
		return circuitArea;
	}


	private void setNodeList(Vector<CircuitNode> nodeList) {
		this.nodeList = nodeList;
	}

	public void setT(double t) {
		this.t = t;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	protected CheckboxMenuItem setVoltsCheckItem(CheckboxMenuItem voltsCheckItem) {
		this.voltsCheckItem = voltsCheckItem;
		return voltsCheckItem;
	}

	public MenuItem getElmScopeMenuItem() {
		return elmScopeMenuItem;
	}

	public int getMousePost() {
		return mousePost;
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

	public int getDragY() {
		return dragY;
	}

	public int getMenuScope() {
		return menuScope;
	}

	public void setScopeCount(int cnt){
		this.scopeCount = cnt;
	}

	public MenuItem getElmEditMenuItem() {
		return elmEditMenuItem;
	}

	public int getInitDragY() {
		return initDragY;
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

	public int getInitDragX() {
		return initDragX;
	}

	public MenuBar getElmMenuBar() {
		return elmMenuBar;
	}

	public MenuBar getMainMenuBar() {
		return mainMenuBar;
	}

	public AbstractCircuitElement getMenuElm() {
		return menuElm;
	}

	public PopupPanel getContextPanel() {
		return contextPanel;
	}

	public int getDragX() {
		return dragX;
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

	public void setDragY(int dragY) {
		this.dragY = dragY;
	}

	public void setMenuScope(int menuScope) {
		this.menuScope = menuScope;
	}

	public void setInitDragY(int initDragY) {
		this.initDragY = initDragY;
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

	public void setInitDragX(int initDragX) {
		this.initDragX = initDragX;
	}

	public void setMenuElm(AbstractCircuitElement menuElm) {
		this.menuElm = menuElm;
	}

	public void setContextPanel(PopupPanel contextPanel) {
		this.contextPanel = contextPanel;
	}

	public void setDragX(int dragX) {
		this.dragX = dragX;
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

	public CheckboxMenuItem getConventionCheckItem() {
		return conventionCheckItem;
	}

	public CheckboxMenuItem getDotsCheckItem() {
		return dotsCheckItem;
	}

	public AbstractCircuitElement getElm(int n) {
		if (n >= elmList.size())
			return null;

		return elmList.elementAt(n);
	}

	public Vector<AbstractCircuitElement> getElmList() {
		return elmList;
	}

	public CheckboxMenuItem getEuroResistorCheckItem() {
		return euroResistorCheckItem;
	}

	public int getGridSize() {
		return gridSize;
	}
}
