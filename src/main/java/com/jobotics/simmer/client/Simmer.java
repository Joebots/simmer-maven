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

package com.jobotics.simmer.client;

// GWT conversion (c) 2015 by Iain Sharp
// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

import static com.google.gwt.event.dom.client.KeyCodes.KEY_A;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_BACKSPACE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_C;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_DELETE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ESCAPE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_SPACE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_V;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_X;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_Y;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_Z;

import java.util.Random;
import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.jobotics.simmer.client.elcomp.AbstractCircuitElement;
import com.jobotics.simmer.client.elcomp.CapacitorElm;
import com.jobotics.simmer.client.elcomp.CircuitNode;
import com.jobotics.simmer.client.elcomp.CircuitNodeLink;
import com.jobotics.simmer.client.elcomp.CurrentElm;
import com.jobotics.simmer.client.elcomp.GroundElm;
import com.jobotics.simmer.client.elcomp.InductorElm;
import com.jobotics.simmer.client.elcomp.RailElm;
import com.jobotics.simmer.client.elcomp.ResistorElm;
import com.jobotics.simmer.client.elcomp.SwitchElm;
import com.jobotics.simmer.client.elcomp.VoltageElm;
import com.jobotics.simmer.client.elcomp.WireElm;
import com.jobotics.simmer.client.gui.impl.AboutBox;
import com.jobotics.simmer.client.gui.impl.Checkbox;
import com.jobotics.simmer.client.gui.impl.CheckboxAlignedMenuItem;
import com.jobotics.simmer.client.gui.impl.CheckboxMenuItem;
import com.jobotics.simmer.client.gui.impl.EditDialog;
import com.jobotics.simmer.client.gui.impl.EditOptions;
import com.jobotics.simmer.client.gui.impl.Editable;
import com.jobotics.simmer.client.gui.impl.ExportAsLocalFileDialog;
import com.jobotics.simmer.client.gui.impl.ExportAsTextDialog;
import com.jobotics.simmer.client.gui.impl.ExportAsUrlDialog;
import com.jobotics.simmer.client.gui.impl.GraphicElm;
import com.jobotics.simmer.client.gui.impl.ImportFromTextDialog;
import com.jobotics.simmer.client.gui.impl.Scope;
import com.jobotics.simmer.client.gui.impl.ScrollValuePopup;
import com.jobotics.simmer.client.gui.impl.Scrollbar;
import com.jobotics.simmer.client.gui.util.Color;
import com.jobotics.simmer.client.gui.util.Display;
import com.jobotics.simmer.client.gui.util.Font;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.LoadFile;
import com.jobotics.simmer.client.gui.util.MenuCommand;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.gui.util.Rectangle;
import com.jobotics.simmer.client.gui.util.RowInfo;
import com.jobotics.simmer.client.util.CircuitElementFactory;
import com.jobotics.simmer.client.util.FindPathInfo;
import com.jobotics.simmer.client.util.HintTypeEnum.HintType;
import com.jobotics.simmer.client.util.MouseModeEnum.MouseMode;
import com.jobotics.simmer.client.util.QueryParameters;
import com.jobotics.simmer.client.util.StringTokenizer;

public class Simmer implements MouseDownHandler, MouseWheelHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, ClickHandler, DoubleClickHandler, ContextMenuHandler, NativePreviewHandler
{

	public static final double				freqMult			= Math.PI * 2 * 4;
//	private static final int				HINT_3DB_C			= 3;
//	private static final int				HINT_3DB_L			= 5;
//	private static final int				HINT_LC				= 1;
//	private static final int				HINT_RC				= 2;
//	private static final int				HINT_TWINT			= 4;
//	private static final int				infoWidth			= 120;
//	private static final int				MENUBARHEIGHT		= 30;
//	private static final int				MODE_ADD_ELM		= 0;
//	private static final int				MODE_DRAG_ALL		= 1;
//	private static final int				MODE_DRAG_POST		= 5;
//	public static final int					MODE_DRAG_COLUMN	= 3;
//	public static final int					MODE_DRAG_ROW		= 2;
//	private static final int				MODE_DRAG_SELECTED	= 4;
//	private static final int				MODE_SELECT			= 6;
	private static String					muString			= "u";
//	private static final double				pi					= 3.14159265358979323846;
//	private static final int				POSTGRABSQ			= 16;
//	public static final int					sourceRadius		= 7;
//	public static final int					VERTICALPANELWIDTH	= 166;
	public static final String				ohmString			= "ohm";
	// private static final int resct = 6;

	private int								circuitBottom;
	private double							circuitMatrix[][], circuitRightSide[], origRightSide[], origMatrix[][];
	private int								circuitMatrixSize, circuitMatrixFullSize;
	private boolean							circuitNeedsMap;
	private boolean							circuitNonLinear;
	private int								circuitPermute[];
	private final int						FASTTIMER = 40;
	// private int framerate = 0, steprate = 0;
	// private int frames = 0;
	private int								gridMask;
	private int								gridRound;
	private int								gridSize;
	private SwitchElm						heldSwitchElm;
//	private int								hintType			= -1, hintItem1, hintItem2;
	private HintType						hintType			= HintType.HINT_UNSET;
	private HintType						hintItem1			= HintType.HINT_UNSET;
	private HintType						hintItem2			= HintType.HINT_UNSET;
	private int								draggingPost;
	private int								dragX, dragY, initDragX, initDragY;
	private boolean							dumpMatrix;
	private boolean							converged;
	
	// Button dumpMatrixButton;
	private static AboutBox					aboutBox;
	private static EditDialog				editDialog;
	private static ExportAsLocalFileDialog	exportAsLocalFileDialog;
	private static ExportAsTextDialog		exportAsTextDialog;
	private static ExportAsUrlDialog		exportAsUrlDialog;
	private static ImportFromTextDialog		importFromTextDialog;
	private static ScrollValuePopup			scrollValuePopup;
//	private MenuItem						aboutItem;
	private Context2d						backcontext;
	private Canvas							backcv;
	private Rectangle						circuitArea;
	private RowInfo							circuitRowInfo[];
	private String							clipboard;
	
	// Class addingClass;
	private PopupPanel						contextPanel;
	private CheckboxMenuItem				conventionCheckItem;
	private String							ctrlMetaKey;
	private Scrollbar						currentBar;
	private Canvas							cv;
	private Context2d						cvcontext;

	// private boolean didSwitch = false;
	private CheckboxMenuItem				dotsCheckItem;

	// Vector setupList;
	private Vector<AbstractCircuitElement>	elmList;

	private MenuItem						elmCopyMenuItem;
	private MenuItem						elmCutMenuItem;
	private MenuItem						elmDeleteMenuItem;
	private MenuItem						elmEditMenuItem;
	private MenuBar							elmMenuBar;
	private MenuItem						elmScopeMenuItem;
	// private CheckboxMenuItem conductanceCheckItem;
	private CheckboxMenuItem				euroResistorCheckItem;
	private MenuBar							fileMenuBar;
	private Frame							iFrame;
	private MenuItem						importFromLocalFileItem, importFromTextItem, exportAsUrlItem, exportAsLocalFileItem, exportAsTextItem;
	// boolean useBufferedImage;
	private boolean							isMac;
	private MouseMode						mouseMode			= MouseMode.SELECT;
	private int								mousePost			= -1;
	private int								scopeColCount[];
	// public boolean useFrame;
	private int								scopeCount;
	private long							lastTime			= 0, lastFrameTime, lastIterTime, secTime = 0;
	private int								menuScope			= -1;
	private String							mouseModeStr		= "Select";
	private int								scopeSelected		= -1;
	private int								subIterations;
	private double							t;
	private MouseMode						tempMouseMode		= MouseMode.SELECT;

	private AbstractCircuitElement			mouseElm			= null;
	private DockLayoutPanel					layoutPanel;
	private LoadFile						loadFileInput;
	private MenuBar							mainMenuBar;
	private Vector<String>					mainMenuItemNames	= new Vector<String>();
	private Vector<CheckboxMenuItem>		mainMenuItems		= new Vector<CheckboxMenuItem>();
	private Vector<CircuitNode>				nodeList;
	private MenuBar							menuBar;

	private AbstractCircuitElement			menuElm;

	// private long mydrawtime = 0;
	// private long myframes = 1;
	// private long myruntime = 0;
	private long							mytime				= 0;
	private MenuBar							optionsMenuBar;
	// private int pause = 10;
	private AbstractCircuitElement			plotXElm, plotYElm;
	private Scrollbar						powerBar;
	private CheckboxMenuItem				powerCheckItem;
	private Label							powerLabel;
	private CheckboxMenuItem				printableCheckItem;
	private Random							random;
	// static Container main;
	// IES - remove interaction
	// Label titleLabel;
	private Button							resetButton;
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
	private Rectangle						selectedArea;
	// private int selectedSource;
	// Class dumpTypes[], shortcuts[];
	private String							shortcuts[];
	// private boolean shown = false;
	private CheckboxMenuItem				showValuesCheckItem;
	private CheckboxMenuItem				smallGridCheckItem;
	private Scrollbar						speedBar;
	private String							startCircuit		= null;
	private String							startCircuitText	= null;
	private String							startLabel			= null;
	// private int steps = 0;
	private AbstractCircuitElement			stopElm;
	private String							stopMessage;
	private Checkbox						stoppedCheck;
	// String baseURL = "http://www.falstad.com/circuit/";
	private final Timer						timer				= new Timer() {
																	public void run() {
																		updateCircuit();
																	}
																};
	private double							timeStep;
	private MenuBar							transScopeMenuBar;
	@SuppressWarnings("unused")
	private MenuItem						undoItem, redoItem, cutItem, copyItem, pasteItem, selectAllItem, optionsItem;
	private Vector<String>					undoStack, redoStack;
	private VerticalPanel					verticalPanel;
	// private int voltageSourceCount;
	private AbstractCircuitElement			voltageSources[];
	private CheckboxMenuItem				voltsCheckItem;
	
	public AbstractCircuitElement			dragElm;
	public boolean							analyzeFlag;
	public boolean							dragging;
	public boolean							mouseDragging;


	public static EditDialog getEditDialog() {
		return editDialog;
	}

	public static String getMuString() {
		return muString;
	}

	public static void setEditDialog(EditDialog editDialog) {
		Simmer.editDialog = editDialog;
	}

	public static void setMuString(String muString) {
		Simmer.muString = muString;
	}

	public void addWidgetToVerticalPanel(Widget w) {
		if (iFrame != null) {
			int i = verticalPanel.getWidgetIndex(iFrame);
			verticalPanel.insert(w, i);
			setiFrameHeight();
		} else
			verticalPanel.add(w);
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

		// System.out.println("ac1");
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
			cn.x = (int) pt.x;
			cn.y = (int) pt.y;
			getNodeList().addElement(cn);
		} else {
			// otherwise allocate extra node for ground
			CircuitNode cn = new CircuitNode();
			cn.x = cn.y = -1;
			getNodeList().addElement(cn);
		}
		// System.out.println("ac2");

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
					if (pt.x == cn.x && pt.y == cn.y)
						break;
				}
				if (k == getNodeList().size()) {
					CircuitNode cn = new CircuitNode();
					cn.x = (int) pt.x;
					cn.y = (int) pt.y;
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
		// System.out.println("ac3");

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
		// System.out.println("ac4");

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
					System.out.println("node " + i + " unconnected");
					stampResistor(0, i, 1e8);
					closure[i] = true;
					changed = true;
					break;
				}
		}
		// System.out.println("ac5");

		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			// look for inductors with no current path
			if (ce instanceof InductorElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1), getNodeList().size(), getElmList());
				// first try findPath with maximum depth of 5, to avoid
				// slowdowns
				if (!fpi.findPath(ce.getNode(0), 5) && !fpi.findPath(ce.getNode(0))) {
					System.out.println(ce + " no path");
					ce.reset();
				}
			}
			// look for current sources with no current path
			if (ce instanceof CurrentElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1), getNodeList().size(), getElmList());
				if (!fpi.findPath(ce.getNode(0))) {
					stop("No path for current source!", ce);
					return;
				}
			}
			// look for voltage source loops
			// IES
			if ((ce instanceof VoltageElm && ce.getPostCount() == 2) || ce instanceof WireElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce, ce.getNode(1), getNodeList().size(), getElmList());
				if (fpi.findPath(ce.getNode(0))) {
					stop("Voltage source/wire loop with no resistance!", ce);
					return;
				}
			}
			// look for shorted caps, or caps w/ voltage but no R
			if (ce instanceof CapacitorElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce, ce.getNode(1), getNodeList().size(), getElmList());
				if (fpi.findPath(ce.getNode(0))) {
					System.out.println(ce + " shorted");
					ce.reset();
				} else {
					fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1), getNodeList().size(), getElmList());
					if (fpi.findPath(ce.getNode(0))) {
						stop("Capacitor loop with no resistance!", ce);
						return;
					}
				}
			}
		}
		// System.out.println("ac6");

		// simplify the matrix; this speeds things up quite a bit
		for (i = 0; i != matrixSize; i++) {
			int qm = -1, qp = -1;
			double qv = 0;
			RowInfo re = circuitRowInfo[i];
			/*
			 * System.out.println("row " + i + " " + re.lsChanges + " " +
			 * re.rsChanges + " " + re.dropRow);
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
			// System.out.println("line " + i + " " + qp + " " + qm + " " + j);
			/*
			 * if (qp != -1 && circuitRowInfo[qp].lsChanges) {
			 * System.out.println("lschanges"); continue; } if (qm != -1 &&
			 * circuitRowInfo[qm].lsChanges) { System.out.println("lschanges");
			 * continue; }
			 */
			if (j == matrixSize) {
				if (qp == -1) {
					stop("Matrix error", null);
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
						 * System.out.println("following equal chain from " + i
						 * + " " + qp + " to " + elt.nodeEq);
						 */
						qp = elt.getNodeEq();
						elt = circuitRowInfo[qp];
					}
					if (elt.getType() == RowInfo.ROW_EQUAL) {
						// break equal chains
						// System.out.println("Break equal chain");
						elt.setType(RowInfo.ROW_NORMAL);
						continue;
					}
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						System.out.println("type already " + elt.getType() + " for " + qp + "!");
						continue;
					}
					elt.setType(RowInfo.ROW_CONST);
					elt.setValue((circuitRightSide[i] + rsadd) / qv);
					circuitRowInfo[i].setDropRow(true);
					// System.out.println(qp + " * " + qv + " = const " +
					// elt.value);
					i = -1; // start over from scratch
				} else if (circuitRightSide[i] + rsadd == 0) {
					// we found a row with only two nonzero entries, and one
					// is the negative of the other; the values are equal
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						// System.out.println("swapping");
						int qq = qm;
						qm = qp;
						qp = qq;
						elt = circuitRowInfo[qp];
						if (elt.getType() != RowInfo.ROW_NORMAL) {
							// we should follow the chain here, but this
							// hardly ever happens so it's not worth worrying
							// about
							System.out.println("swap failed");
							continue;
						}
					}
					elt.setType(RowInfo.ROW_EQUAL);
					elt.setNodeEq(qm);
					circuitRowInfo[i].setDropRow(true);
					// System.out.println(qp + " = " + qm);
				}
			}
		}

		// find size of new matrix
		int nn = 0;
		for (i = 0; i != matrixSize; i++) {
			RowInfo elt = circuitRowInfo[i];
			if (elt.getType() == RowInfo.ROW_NORMAL) {
				elt.setMapCol(nn++);
				// System.out.println("col " + i + " maps to " + elt.mapCol);
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
					// System.out.println(i + " = [late]const " + elt.value);
				} else {
					elt.setMapCol(e2.getMapCol());
					// System.out.println(i + " maps to: " + e2.mapCol);
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
			// System.out.println("Row " + i + " maps to " + ii);
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
				stop("Singular matrix!", null);
				return;
			}
		}
	}

	private boolean anySelectedButMouse() {
		for (int i = 0; i != elmList.size(); i++)
			if (getElm(i) != mouseElm && getElm(i).isSelected())
				return true;
		return false;
	}
	
	private void buildDrawMenu(MenuBar mainMenuBar) {
		mainMenuBar.addItem(getClassCheckItem("Add Wire", "WireElm"));
		mainMenuBar.addItem(getClassCheckItem("Add Resistor", "ResistorElm"));

		// Passive Components
		MenuBar passMenuBar = new MenuBar(true);
		passMenuBar.addItem(getClassCheckItem("Add Capacitor", "CapacitorElm"));
		passMenuBar.addItem(getClassCheckItem("Add Inductor", "InductorElm"));
		passMenuBar.addItem(getClassCheckItem("Add Switch", "SwitchElm"));
		passMenuBar.addItem(getClassCheckItem("Add Push Switch", "PushSwitchElm"));
		passMenuBar.addItem(getClassCheckItem("Add SPDT Switch", "Switch2Elm"));
		passMenuBar.addItem(getClassCheckItem("Add Potentiometer", "PotElm"));
		passMenuBar.addItem(getClassCheckItem("Add Transformer", "TransformerElm"));
		passMenuBar.addItem(getClassCheckItem("Add Tapped Transformer", "TappedTransformerElm"));
		passMenuBar.addItem(getClassCheckItem("Add Transmission Line", "TransLineElm"));
		passMenuBar.addItem(getClassCheckItem("Add Relay", "RelayElm"));
		passMenuBar.addItem(getClassCheckItem("Add Memristor", "MemristorElm"));
		passMenuBar.addItem(getClassCheckItem("Add Spark Gap", "SparkGapElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Passive Components"), passMenuBar);

		// Inputs and Sources
		MenuBar inputMenuBar = new MenuBar(true);
		inputMenuBar.addItem(getClassCheckItem("Add Ground", "GroundElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Voltage Source (2-terminal)", "DCVoltageElm"));
		inputMenuBar.addItem(getClassCheckItem("Add A/C Voltage Source (2-terminal)", "ACVoltageElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Voltage Source (1-terminal)", "RailElm"));
		inputMenuBar.addItem(getClassCheckItem("Add A/C Voltage Source (1-terminal)", "ACRailElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Square Wave Source (1-terminal)", "SquareRailElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Clock", "ClockElm"));
		inputMenuBar.addItem(getClassCheckItem("Add A/C Sweep", "SweepElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Variable Voltage", "VarRailElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Antenna", "AntennaElm"));
		inputMenuBar.addItem(getClassCheckItem("Add AM Source", "AMElm"));
		inputMenuBar.addItem(getClassCheckItem("Add FM Source", "FMElm"));
		inputMenuBar.addItem(getClassCheckItem("Add Current Source", "CurrentElm"));

		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Inputs and Sources"), inputMenuBar);

		// Outputs and Labels
		MenuBar outputMenuBar = new MenuBar(true);
		outputMenuBar.addItem(getClassCheckItem("Add Analog Output", "OutputElm"));
		outputMenuBar.addItem(getClassCheckItem("Add LED", "LEDElm"));
		outputMenuBar.addItem(getClassCheckItem("Add Lamp (beta)", "LampElm"));
		outputMenuBar.addItem(getClassCheckItem("Add Text", "TextElm"));
		outputMenuBar.addItem(getClassCheckItem("Add Box", "BoxElm"));
		outputMenuBar.addItem(getClassCheckItem("Add Scope Probe", "ProbeElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Outputs and Labels"), outputMenuBar);
		
		// Active Components
		MenuBar activeMenuBar = new MenuBar(true);
		activeMenuBar.addItem(getClassCheckItem("Add Diode", "DiodeElm"));
		activeMenuBar.addItem(getClassCheckItem("Add Zener Diode", "ZenerElm"));
		activeMenuBar.addItem(getClassCheckItem("Add Transistor (bipolar, NPN)", "NTransistorElm"));
		activeMenuBar.addItem(getClassCheckItem("Add Transistor (bipolar, PNP)", "PTransistorElm"));
		activeMenuBar.addItem(getClassCheckItem("Add MOSFET (N-Channel)", "NMosfetElm"));
		activeMenuBar.addItem(getClassCheckItem("Add MOSFET (P-Channel)", "PMosfetElm"));
		activeMenuBar.addItem(getClassCheckItem("Add JFET (N-Channel)", "NJfetElm"));
		activeMenuBar.addItem(getClassCheckItem("Add JFET (P-Channel)", "PJfetElm"));
		activeMenuBar.addItem(getClassCheckItem("Add SCR", "SCRElm"));
		// activeMenuBar.addItem(getClassCheckItem("Add Varactor/Varicap",
		// "VaractorElm"));
		activeMenuBar.addItem(getClassCheckItem("Add Tunnel Diode", "TunnelDiodeElm"));
		activeMenuBar.addItem(getClassCheckItem("Add Triode", "TriodeElm"));
		// activeMenuBar.addItem(getClassCheckItem("Add Diac", "DiacElm"));
		// activeMenuBar.addItem(getClassCheckItem("Add Triac", "TriacElm"));
		// activeMenuBar.addItem(getClassCheckItem("Add Photoresistor",
		// "PhotoResistorElm"));
		// activeMenuBar.addItem(getClassCheckItem("Add Thermistor",
		// "ThermistorElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Active Components"), activeMenuBar);
		
		// Active Building Blocks
		MenuBar activeBlocMenuBar = new MenuBar(true);
		activeBlocMenuBar.addItem(getClassCheckItem("Add Op Amp (- on top)", "OpAmpElm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add Op Amp (+ on top)", "OpAmpSwapElm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add Analog Switch (SPST)", "AnalogSwitchElm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add Analog Switch (SPDT)", "AnalogSwitch2Elm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add Tristate Buffer", "TriStateElm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add Schmitt Trigger", "SchmittElm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add Schmitt Trigger (Inverting)", "InvertingSchmittElm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add CCII+", "CC2Elm"));
		activeBlocMenuBar.addItem(getClassCheckItem("Add CCII-", "CC2NegElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Active Building Blocks"), activeBlocMenuBar);
		
		// Logic Gates, Input and Output
		MenuBar gateMenuBar = new MenuBar(true);
		gateMenuBar.addItem(getClassCheckItem("Add Logic Input", "LogicInputElm"));
		gateMenuBar.addItem(getClassCheckItem("Add Logic Output", "LogicOutputElm"));
		gateMenuBar.addItem(getClassCheckItem("Add Inverter", "InverterElm"));
		gateMenuBar.addItem(getClassCheckItem("Add NAND Gate", "NandGateElm"));
		gateMenuBar.addItem(getClassCheckItem("Add NOR Gate", "NorGateElm"));
		gateMenuBar.addItem(getClassCheckItem("Add AND Gate", "AndGateElm"));
		gateMenuBar.addItem(getClassCheckItem("Add OR Gate", "OrGateElm"));
		gateMenuBar.addItem(getClassCheckItem("Add XOR Gate", "XorGateElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Logic Gates, Input and Output"), gateMenuBar);

		// Digital Chips
		MenuBar chipMenuBar = new MenuBar(true);
		chipMenuBar.addItem(getClassCheckItem("Add D Flip-Flop", "DFlipFlopElm"));
		chipMenuBar.addItem(getClassCheckItem("Add JK Flip-Flop", "JKFlipFlopElm"));
		chipMenuBar.addItem(getClassCheckItem("Add T Flip-Flop", "TFlipFlopElm"));
		chipMenuBar.addItem(getClassCheckItem("Add 7 Segment LED", "SevenSegElm"));
		chipMenuBar.addItem(getClassCheckItem("Add 7 Segment Decoder", "SevenSegDecoderElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Multiplexer", "MultiplexerElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Demultiplexer", "DeMultiplexerElm"));
		chipMenuBar.addItem(getClassCheckItem("Add SIPO shift register", "SipoShiftElm"));
		chipMenuBar.addItem(getClassCheckItem("Add PISO shift register", "PisoShiftElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Counter", "CounterElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Decade Counter", "DecadeElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Latch", "LatchElm"));
		// chipMenuBar.addItem(getClassCheckItem("Add Static RAM", "SRAMElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Sequence generator", "SeqGenElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Full Adder", "FullAdderElm"));
		chipMenuBar.addItem(getClassCheckItem("Add Half Adder", "HalfAdderElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Digital Chips"), chipMenuBar);
		
		// Analog and Hybrid Chips
		MenuBar achipMenuBar = new MenuBar(true);
		achipMenuBar.addItem(getClassCheckItem("Add 555 Timer", "TimerElm"));
		achipMenuBar.addItem(getClassCheckItem("Add Phase Comparator", "PhaseCompElm"));
		achipMenuBar.addItem(getClassCheckItem("Add DAC", "DACElm"));
		achipMenuBar.addItem(getClassCheckItem("Add ADC", "ADCElm"));
		achipMenuBar.addItem(getClassCheckItem("Add VCO", "VCOElm"));
		achipMenuBar.addItem(getClassCheckItem("Add Monostable", "MonostableElm"));
		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Analog and Hybrid Chips"), achipMenuBar);
		
		// Drag
		MenuBar otherMenuBar = new MenuBar(true);
		CheckboxMenuItem mi;
		otherMenuBar.addItem(mi = getClassCheckItem("Drag All", "DragAll"));
		mi.addShortcut("(Alt-drag)");
		otherMenuBar.addItem(mi = getClassCheckItem("Drag Row", "DragRow"));
		mi.addShortcut("(S-right)");
		otherMenuBar.addItem(getClassCheckItem("Drag Column", "DragColumn"));
		otherMenuBar.addItem(getClassCheckItem("Drag Selected", "DragSelected"));
		otherMenuBar.addItem(mi = getClassCheckItem("Drag Post", "DragPost"));
		mi.addShortcut("(" + ctrlMetaKey + "-drag)");

		mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>Drag"), otherMenuBar);

		mainMenuBar.addItem(mi = getClassCheckItem("Select/Drag Sel", "Select"));
		mi.addShortcut("(space or Shift-drag)");
	}

	private MenuBar buildScopeMenu(boolean t) {
		MenuBar m = new MenuBar(true);
		m.addItem(new CheckboxAlignedMenuItem("Remove", new MenuCommand("scopepop", "remove")));
		m.addItem(new CheckboxAlignedMenuItem("Speed 2x", new MenuCommand("scopepop", "speed2")));
		m.addItem(new CheckboxAlignedMenuItem("Speed 1/2x", new MenuCommand("scopepop", "speed1/2")));
		m.addItem(new CheckboxAlignedMenuItem("Scale 2x", new MenuCommand("scopepop", "scale")));
		m.addItem(new CheckboxAlignedMenuItem("Max Scale", new MenuCommand("scopepop", "maxscale")));
		m.addItem(new CheckboxAlignedMenuItem("Stack", new MenuCommand("scopepop", "stack")));
		m.addItem(new CheckboxAlignedMenuItem("Unstack", new MenuCommand("scopepop", "unstack")));
		m.addItem(new CheckboxAlignedMenuItem("Reset", new MenuCommand("scopepop", "reset")));
		if (t) {
			m.addItem(scopeIbMenuItem = new CheckboxMenuItem("Show Ib", new MenuCommand("scopepop", "showib")));
			m.addItem(scopeIcMenuItem = new CheckboxMenuItem("Show Ic", new MenuCommand("scopepop", "showic")));
			m.addItem(scopeIeMenuItem = new CheckboxMenuItem("Show Ie", new MenuCommand("scopepop", "showie")));
			m.addItem(scopeVbeMenuItem = new CheckboxMenuItem("Show Vbe", new MenuCommand("scopepop", "showvbe")));
			m.addItem(scopeVbcMenuItem = new CheckboxMenuItem("Show Vbc", new MenuCommand("scopepop", "showvbc")));
			m.addItem(scopeVceMenuItem = new CheckboxMenuItem("Show Vce", new MenuCommand("scopepop", "showvce")));
			m.addItem(scopeVceIcMenuItem = new CheckboxMenuItem("Show Vce vs Ic", new MenuCommand("scopepop", "showvcevsic")));
		} else {
			m.addItem(scopeVMenuItem = new CheckboxMenuItem("Show Voltage", new MenuCommand("scopepop", "showvoltage")));
			m.addItem(scopeIMenuItem = new CheckboxMenuItem("Show Current", new MenuCommand("scopepop", "showcurrent")));
			m.addItem(scopePowerMenuItem = new CheckboxMenuItem("Show Power Consumed", new MenuCommand("scopepop", "showpower")));
			m.addItem(scopeScaleMenuItem = new CheckboxMenuItem("Show Scale", new MenuCommand("scopepop", "showscale")));
			m.addItem(scopeMaxMenuItem = new CheckboxMenuItem("Show Peak Value", new MenuCommand("scopepop", "showpeak")));
			m.addItem(scopeMinMenuItem = new CheckboxMenuItem("Show Negative Peak Value", new MenuCommand("scopepop", "shownegpeak")));
			m.addItem(scopeFreqMenuItem = new CheckboxMenuItem("Show Frequency", new MenuCommand("scopepop", "showfreq")));
			m.addItem(scopeVIMenuItem = new CheckboxMenuItem("Show V vs I", new MenuCommand("scopepop", "showvvsi")));
			m.addItem(scopeXYMenuItem = new CheckboxMenuItem("Plot X/Y", new MenuCommand("scopepop", "plotxy")));
			m.addItem(scopeSelectYMenuItem = new CheckboxAlignedMenuItem("Select Y", new MenuCommand("scopepop", "selecty")));
			m.addItem(scopeResistMenuItem = new CheckboxMenuItem("Show Resistance", new MenuCommand("scopepop", "showresistance")));
		}
		return m;
	}
	
	@SuppressWarnings("unused")
	private MenuBar buildEditMenu1(){
		
		elmMenuBar = new MenuBar(true);
		elmMenuBar.addItem(elmEditMenuItem = new MenuItem("Edit", new MenuCommand("elm", "edit")));
		elmMenuBar.addItem(elmScopeMenuItem = new MenuItem("View in Scope", new MenuCommand("elm", "viewInScope")));
		elmMenuBar.addItem(elmCutMenuItem = new MenuItem("Cut", new MenuCommand("elm", "cut")));
		elmMenuBar.addItem(elmCopyMenuItem = new MenuItem("Copy", new MenuCommand("elm", "copy")));
		elmMenuBar.addItem(elmDeleteMenuItem = new MenuItem("Delete", new MenuCommand("elm", "delete")));
		
		return elmMenuBar;
	}
	
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

	private void centreCircuit() {
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
				minx = min(ce.getX(), min(ce.getX2(), minx));
				maxx = max(ce.getX(), max(ce.getX2(), maxx));
			}
			miny = min(ce.getY(), min(ce.getY2(), miny));
			maxy = max(ce.getY(), max(ce.getY2(), maxy));
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

	private void clearSelection() {
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			ce.setSelected(false);
		}
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

	private boolean dialogIsShowing() {
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

	private int distanceSq(int x1, int y1, int x2, int y2) {
		x2 -= x1;
		y2 -= y1;
		return x2 * x2 + y2 * y2;
	}

	private void doCopy() {
		int i;
		clipboard = "";
		setMenuSelection();
		for (i = elmList.size() - 1; i >= 0; i--) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.isSelected())
				clipboard += ce.dump() + "\n";
		}
		enablePaste();
	}

	private void doCut() {
		int i;
		pushUndo();
		setMenuSelection();
		clipboard = "";
		for (i = elmList.size() - 1; i >= 0; i--) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.isSelected()) {
				clipboard += ce.dump() + "\n";
				ce.delete();
				elmList.removeElementAt(i);
			}
		}
		enablePaste();
		needAnalyze();
	}

	private void doDelete() {
		int i;
		pushUndo();
		setMenuSelection();
		boolean hasDeleted = false;

		for (i = elmList.size() - 1; i >= 0; i--) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.isSelected()) {
				ce.delete();
				elmList.removeElementAt(i);
				hasDeleted = true;
			}
		}

		if (!hasDeleted) {
			for (i = elmList.size() - 1; i >= 0; i--) {
				AbstractCircuitElement ce = getElm(i);
				if (ce == mouseElm) {
					ce.delete();
					elmList.removeElementAt(i);
					hasDeleted = true;
					setMouseElm(null);
					break;
				}
			}
		}

		if (hasDeleted)
			needAnalyze();
	}

	private void doEdit(Editable eable) {
		clearSelection();
		pushUndo();
		if (getEditDialog() != null) {
			// requestFocus();
			getEditDialog().setVisible(false);
			setEditDialog(null);
		}
		setEditDialog(new EditDialog(eable, this));
		getEditDialog().show();
	}

	private void doExportAsLocalFile() {
		String dump = dumpCircuit();
		exportAsLocalFileDialog = new ExportAsLocalFileDialog(dump);
		exportAsLocalFileDialog.show();
	}

	private void doExportAsText() {
		String dump = dumpCircuit();
		exportAsTextDialog = new ExportAsTextDialog(dump);
		exportAsTextDialog.show();
	}

	private void doExportAsUrl() {
		String start[] = Location.getHref().split("\\?");
		String dump = dumpCircuit();
		dump = dump.replace(' ', '+');
		dump = start[0] + "?cct=" + URL.encode(dump);
		exportAsUrlDialog = new ExportAsUrlDialog(dump);
		exportAsUrlDialog.show();
	}

	private void doMainMenuChecks() {
		int c = mainMenuItems.size();
		int i;
		for (i = 0; i < c; i++)
			mainMenuItems.get(i).setState(mainMenuItemNames.get(i) == mouseModeStr);
	}

	private void doPaste() {
		pushUndo();
		clearSelection();
		int i;
		Rectangle oldbb = null;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			Rectangle bb = ce.getBoundingBox();
			if (oldbb != null)
				oldbb = oldbb.union(bb);
			else
				oldbb = bb;
		}
		int oldsz = elmList.size();
		readSetup(clipboard, true, false);

		// select new items
		Rectangle newbb = null;
		for (i = oldsz; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
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
			int spacew = circuitArea.width - oldbb.width - newbb.width;
			int spaceh = circuitArea.height - oldbb.height - newbb.height;
			if (spacew > spaceh)
				dx = snapGrid(oldbb.x + oldbb.width - newbb.x + gridSize);
			else
				dy = snapGrid(oldbb.y + oldbb.height - newbb.y + gridSize);
			for (i = oldsz; i != elmList.size(); i++) {
				AbstractCircuitElement ce = getElm(i);
				ce.move(dx, dy);
			}
			// center circuit
			// handleResize();
		}
		needAnalyze();
	}

	private void doRedo() {
		if (redoStack.size() == 0)
			return;
		undoStack.add(dumpCircuit());
		String s = redoStack.remove(redoStack.size() - 1);
		readSetup(s, false);
		enableUndoRedo();
	}

	private void doSelectAll() {
		int i;
		for (i = 0; i != elmList.size(); i++) {
			AbstractCircuitElement ce = getElm(i);
			ce.setSelected(true);
		}
	}

	private boolean doSwitch(int x, int y) {
		if (mouseElm == null || !(mouseElm instanceof SwitchElm))
			return false;
		SwitchElm se = (SwitchElm) mouseElm;
		se.toggle();
		if (se.isMomentary())
			heldSwitchElm = se;
		needAnalyze();
		return true;
	}

	private void doUndo() {
		if (undoStack.size() == 0)
			return;
		redoStack.add(dumpCircuit());
		String s = undoStack.remove(undoStack.size() - 1);
		readSetup(s, false);
		enableUndoRedo();
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
			if (ce.getX() == dragX)
				ce.movePoint(0, dx, 0);
			if (ce.getX2() == dragX)
				ce.movePoint(1, dx, 0);
		}
		removeZeroLengthElements();
	}

	private void dragPost(int x, int y) {
		if (draggingPost == -1) {
			draggingPost = (distanceSq(mouseElm.getX(), mouseElm.getY(), x, y) > distanceSq(mouseElm.getX2(), mouseElm.getY2(), x, y)) ? 1 : 0;
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
			if (ce.getY() == dragY)
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

	private String dumpCircuit() {
		int i;
		int f = (getDotsCheckItem().getState()) ? 1 : 0;
		f |= (getSmallGridCheckItem().getState()) ? 2 : 0;
		f |= (getVoltsCheckItem().getState()) ? 0 : 4;
		f |= (getPowerCheckItem().getState()) ? 8 : 0;
		f |= (getShowValuesCheckItem().getState()) ? 0 : 16;
		// 32 = linear scale in afilter
		String dump = "$ " + f + " " + getTimeStep() + " " + getIterCount() + " " + currentBar.getValue() + " " + AbstractCircuitElement.voltageRange + " " + powerBar.getValue()
				+ "\n";

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

	private void enablePaste() {
		pasteItem.setEnabled(clipboard.length() > 0);
	}

	private void enableUndoRedo() {
		redoItem.setEnabled(redoStack.size() > 0);
		undoItem.setEnabled(undoStack.size() > 0);
	}

	public CircuitNode getCircuitNode(int n) {
		if (n >= getNodeList().size())
			return null;
		return getNodeList().elementAt(n);
	}

	private CheckboxMenuItem getClassCheckItem(String s, String t) {
		// try {
		// Class c = Class.forName(t);
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
		// else
		// GWT.log("Coudn't create class: "+t);
		// } catch (Exception ee) {
		// ee.printStackTrace();
		// }
		if (shortcut == "")
			mi = new CheckboxMenuItem(s);
		else
			mi = new CheckboxMenuItem(s, shortcut);
		
		mi.setScheduledCommand(new MenuCommand("main", t));
		mainMenuItems.add(mi);
		mainMenuItemNames.add(t);
		return mi;
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

	public MouseMode getMouseMode() {
		return mouseMode;
	}

	public Vector<CircuitNode> getNodeList() {
		return nodeList;
	}

	public CheckboxMenuItem getPowerCheckItem() {
		return powerCheckItem;
	}

	public CheckboxMenuItem getPrintableCheckItem() {
		return printableCheckItem;
	}

	public int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}

	public CheckboxMenuItem getScopeFreqMenuItem() {
		return scopeFreqMenuItem;
	}

	public CheckboxMenuItem getScopeIbMenuItem() {
		return scopeIbMenuItem;
	}

	public CheckboxMenuItem getScopeIcMenuItem() {
		return scopeIcMenuItem;
	}

	public CheckboxMenuItem getScopeIeMenuItem() {
		return scopeIeMenuItem;
	}

	public CheckboxMenuItem getScopeIMenuItem() {
		return scopeIMenuItem;
	}

	public CheckboxMenuItem getScopeMaxMenuItem() {
		return scopeMaxMenuItem;
	}

	public MenuBar getScopeMenuBar() {
		return scopeMenuBar;
	}

	public CheckboxMenuItem getScopeMinMenuItem() {
		return scopeMinMenuItem;
	}

	public CheckboxMenuItem getScopePowerMenuItem() {
		return scopePowerMenuItem;
	}

	public CheckboxMenuItem getScopeResistMenuItem() {
		return scopeResistMenuItem;
	}

	public CheckboxMenuItem getScopeScaleMenuItem() {
		return scopeScaleMenuItem;
	}

	public int getScopeSelected() {
		return scopeSelected;
	}

	public MenuItem getScopeSelectYMenuItem() {
		return scopeSelectYMenuItem;
	}

	public CheckboxMenuItem getScopeVbcMenuItem() {
		return scopeVbcMenuItem;
	}

	public CheckboxMenuItem getScopeVbeMenuItem() {
		return scopeVbeMenuItem;
	}

	public CheckboxMenuItem getScopeVceIcMenuItem() {
		return scopeVceIcMenuItem;
	}

	public CheckboxMenuItem getScopeVceMenuItem() {
		return scopeVceMenuItem;
	}

	public CheckboxMenuItem getScopeVIMenuItem() {
		return scopeVIMenuItem;
	}

	public CheckboxMenuItem getScopeVMenuItem() {
		return scopeVMenuItem;
	}

	public CheckboxMenuItem getScopeXYMenuItem() {
		return scopeXYMenuItem;
	}

	private void getSetupList(final boolean openDefault) {

		String url = GWT.getModuleBaseURL();
		url = url.substring(0,url.indexOf("circuitjs1"));
		url = url +  "setuplist.txt" + "?v=" + random.nextInt();
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// processing goes here
					if (response.getStatusCode() == Response.SC_OK) {
						String text = response.getText();
						processSetupList(text.getBytes(), text.length(), openDefault);
						// end or processing
					} else
						GWT.log("Bad file server response:" + response.getStatusText());
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}
		
		String s = "";
		if( s != null && s.isEmpty() && Character.isUpperCase(s.charAt(0))){
			
		}
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

	public MenuBar getTransScopeMenuBar() {
		return transScopeMenuBar;
	}

	public CheckboxMenuItem getVoltsCheckItem() {
		return voltsCheckItem;
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
		menuBar.addItem("File", buildFileMenu());
		menuBar.addItem("Edit", buildEditMenu());
		menuBar.addItem("Draw", drawMenuBar);
		menuBar.addItem("Scopes", buildScopesMenu());
		menuBar.addItem("Options", buildOptionsMenu());
		verticalPanel = new VerticalPanel();

//		optionsMenuBar = m = new MenuBar(true);
		
		getVoltsCheckItem().setState(true);
		getShowValuesCheckItem().setState(true);
		getEuroResistorCheckItem().setState(euro);
		getPrintableCheckItem().setState(printable);
		getConventionCheckItem().setState(convention);
		
		mainMenuBar = new MenuBar(true);
		mainMenuBar.setAutoOpen(true);
		buildDrawMenu(mainMenuBar);
		
		// popup
		buildDrawMenu(drawMenuBar);

		layoutPanel.addNorth(menuBar, Display.MENUBARHEIGHT);
		layoutPanel.addEast(verticalPanel, Display.VERTICALPANELWIDTH);
		RootLayoutPanel.get().add(layoutPanel);
		cv = Canvas.createIfSupported();
		if (cv == null) {
			RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
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
		elmMenuBar = new MenuBar(true);
		elmMenuBar.addItem(elmEditMenuItem = new MenuItem("Edit", new MenuCommand("elm", "edit")));
		elmMenuBar.addItem(elmScopeMenuItem = new MenuItem("View in Scope", new MenuCommand("elm", "viewInScope")));
		elmMenuBar.addItem(elmCutMenuItem = new MenuItem("Cut", new MenuCommand("elm", "cut")));
		elmMenuBar.addItem(elmCopyMenuItem = new MenuItem("Copy", new MenuCommand("elm", "copy")));
		elmMenuBar.addItem(elmDeleteMenuItem = new MenuItem("Delete", new MenuCommand("elm", "delete")));
		// main.add(elmMenu);

		scopeMenuBar = buildScopeMenu(false);
		transScopeMenuBar = buildScopeMenu(true);

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
		
		enableUndoRedo();
		setiFrameHeight();
		bindEventHandlers();
		// setup timer
		timer.scheduleRepeating(FASTTIMER);

	}
	
	private MenuBar buildEditMenu() {
		MenuBar m;
		m = new MenuBar(true);
		final String edithtml = "<div style=\"display:inline-block;width:80px;\">";
		String sn = edithtml + "Undo</div>Ctrl-Z";
		m.addItem(undoItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "undo")));
		// undoItem.setShortcut(new MenuShortcut(KeyEvent.VK_Z));
		sn = edithtml + "Redo</div>Ctrl-Y";
		m.addItem(redoItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "redo")));
		// redoItem.setShortcut(new MenuShortcut(KeyEvent.VK_Z, true));
		m.addSeparator();
		// m.addItem(cutItem = new MenuItem("Cut", new
		// MenuCommand("edit","cut")));
		sn = edithtml + "Cut</div>Ctrl-X";
		m.addItem(cutItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "cut")));
		// cutItem.setShortcut(new MenuShortcut(KeyEvent.VK_X));
		sn = edithtml + "Copy</div>Ctrl-C";
		m.addItem(copyItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "copy")));
		sn = edithtml + "Paste</div>Ctrl-V";
		m.addItem(pasteItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "paste")));
		// pasteItem.setShortcut(new MenuShortcut(KeyEvent.VK_V));
		pasteItem.setEnabled(false);
		m.addSeparator();
		sn = edithtml + "Select All</div>Ctrl-A";
		m.addItem(selectAllItem = new MenuItem(SafeHtmlUtils.fromTrustedString(sn), new MenuCommand("edit", "selectAll")));
		// selectAllItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
		m.addItem(new MenuItem("Centre Circuit", new MenuCommand("edit", "centrecircuit")));
		return m;
	}

	private MenuBar buildFileMenu() {
		MenuBar fileMenuBar = new MenuBar(true);
		MenuItem importFromLocalFileItem = new MenuItem("Import From Local File", new MenuCommand("file", "importfromlocalfile"));
		importFromLocalFileItem.setEnabled(LoadFile.isSupported());
		fileMenuBar.addItem(importFromLocalFileItem);
		MenuItem importFromTextItem = new MenuItem("Import From Text", new MenuCommand("file", "importfromtext"));
		fileMenuBar.addItem(importFromTextItem);
		MenuItem exportAsUrlItem = new MenuItem("Export as Link", new MenuCommand("file", "exportasurl"));
		fileMenuBar.addItem(exportAsUrlItem);
		MenuItem exportAsLocalFileItem = new MenuItem("Export as Local File", new MenuCommand("file", "exportaslocalfile"));
		exportAsLocalFileItem.setEnabled(ExportAsLocalFileDialog.downloadIsSupported());
		fileMenuBar.addItem(exportAsLocalFileItem);
		MenuItem exportAsTextItem = new MenuItem("Export as Text", new MenuCommand("file", "exportastext"));
		fileMenuBar.addItem(exportAsTextItem);
		fileMenuBar.addSeparator();
		MenuItem aboutItem = new MenuItem("About", (Command) null);
		fileMenuBar.addItem(aboutItem);
		aboutItem.setScheduledCommand(new MenuCommand("file", "about"));
		return fileMenuBar;
	}

	private MenuBar buildScopesMenu() {
		MenuBar m = new MenuBar(true);
		m.addItem(new MenuItem("Stack All", new MenuCommand("scopes", "stackAll")));
		m.addItem(new MenuItem("Unstack All", new MenuCommand("scopes", "unstackAll")));
		return m;
	}

	private MenuBar buildOptionsMenu() {
		
		MenuBar optionsMenuBar = new MenuBar(true);
		optionsMenuBar.addItem(setDotsCheckItem(new CheckboxMenuItem("Show Current")));
		getDotsCheckItem().setState(true);
		optionsMenuBar.addItem(setVoltsCheckItem(new CheckboxMenuItem("Show Voltage", new Command() {
			public void execute() {
				if (getVoltsCheckItem().getState())
					getPowerCheckItem().setState(false);
				setPowerBarEnable();
			}
		})));
		optionsMenuBar.addItem(setPowerCheckItem(new CheckboxMenuItem("Show Power", new Command() {
			public void execute() {
				if (getPowerCheckItem().getState())
					getVoltsCheckItem().setState(false);
				setPowerBarEnable();
			}
		})));
		optionsMenuBar.addItem(setShowValuesCheckItem(new CheckboxMenuItem("Show Values")));
		// m.add(conductanceCheckItem = getCheckItem("Show Conductance"));
		optionsMenuBar.addItem(setSmallGridCheckItem(new CheckboxMenuItem("Small Grid", new Command() {
			public void execute() {
				setGrid();
			}
		})));
		optionsMenuBar.addItem(setEuroResistorCheckItem(new CheckboxMenuItem("European Resistors")));
		optionsMenuBar.addItem(setPrintableCheckItem(new CheckboxMenuItem("White Background", new Command() {
			public void execute() {
				int i;
				for (i = 0; i < scopeCount; i++)
					scopes[i].setRect(scopes[i].getRect());
			}
		})));
		optionsMenuBar.addItem(setConventionCheckItem(new CheckboxMenuItem("Conventional Current Motion")));
		optionsMenuBar.addItem(optionsItem = new CheckboxAlignedMenuItem("Other Options...", new MenuCommand("options", "other")));
		
		return optionsMenuBar;
	}

	private void bindEventHandlers() {
		cv.addMouseDownHandler(this);
		cv.addMouseMoveHandler(this);
		cv.addMouseUpHandler(this);
		cv.addClickHandler(this);
		cv.addDoubleClickHandler(this);
		cv.addDomHandler(this, ContextMenuEvent.getType());
		menuBar.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doMainMenuChecks();
			}
		}, ClickEvent.getType());
		Event.addNativePreviewHandler(this);
		cv.addMouseWheelHandler(this);
	}

	private void createSideBar() {
		verticalPanel.add(resetButton = new Button("Reset"));
		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				resetAction();
			}
		});
		// dumpMatrixButton = new Button("Dump Matrix");
		// main.add(dumpMatrixButton);// IES for debugging
		setStoppedCheck(new Checkbox("Stopped"));
		verticalPanel.add(getStoppedCheck());

		if (LoadFile.isSupported())
			verticalPanel.add(loadFileInput = new LoadFile(this));

		Label l;
		verticalPanel.add(l = new Label("Simulation Speed"));
		l.addStyleName("topSpace");

		// was max of 140
		verticalPanel.add(speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260));

		verticalPanel.add(l = new Label("Current Speed"));
		l.addStyleName("topSpace");
		currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
		verticalPanel.add(currentBar);
		verticalPanel.add(powerLabel = new Label("Power Brightness"));
		powerLabel.addStyleName("topSpace");
		verticalPanel.add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100));
		setPowerBarEnable();
		verticalPanel.add(iFrame = new Frame("iframe.html"));
		iFrame.setWidth(Display.VERTICALPANELWIDTH + "px");
		iFrame.setHeight("100 px");
		iFrame.getElement().setAttribute("scrolling", "no");
	}

	private void loadFileFromURL(String url, final boolean centre) {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						String text = response.getText();
						readSetup(text.getBytes(), text.length(), false, centre);
					} else
						GWT.log("Bad file server response:" + response.getStatusText());
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
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
				System.out.println("avoided zero");
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

	// IES - remove interaction
	public void menuPerformed(String menu, String item) {
		if (item == "about")
			aboutBox = new AboutBox(Launcher.versionString);
		if (item == "importfromlocalfile") {
			pushUndo();
			loadFileInput.click();
		}
		if (item == "importfromtext") {
			importFromTextDialog = new ImportFromTextDialog(this);
		}
		if (item == "exportasurl") {
			doExportAsUrl();
		}
		if (item == "exportaslocalfile")
			doExportAsLocalFile();
		if (item == "exportastext")
			doExportAsText();
		if ((menu == "elm" || menu == "scopepop") && contextPanel != null)
			contextPanel.hide();
		if (menu == "options" && item == "other")
			doEdit(new EditOptions(this));
		// public void actionPerformed(ActionEvent e) {
		// String ac = e.getActionCommand();
		// if (e.getSource() == resetButton) {
		// int i;
		//
		// // on IE, drawImage() stops working inexplicably every once in
		// // a while. Recreating it fixes the problem, so we do that here.
		// dbimage = main.createImage(winSize.width, winSize.height);
		//
		// for (i = 0; i != elmList.size(); i++)
		// getElm(i).reset();
		// // IES - removal of scopes
		// // for (i = 0; i != scopeCount; i++)
		// // scopes[i].resetGraph();
		// analyzeFlag = true;
		// t = 0;
		// stoppedCheck.setState(false);
		// cv.repaint();
		// }
		// if (e.getSource() == dumpMatrixButton)
		// dumpMatrix = true;
		// IES - remove import export
		// if (e.getSource() == exportItem)
		// doExport(false);
		// if (e.getSource() == optionsItem)
		// doEdit(new EditOptions(this));
		// if (e.getSource() == importItem)
		// doImport();
		// if (e.getSource() == exportLinkItem)
		// doExport(true);
		if (item == "undo")
			doUndo();
		if (item == "redo")
			doRedo();
		if (item == "cut") {
			if (menu != "elm")
				menuElm = null;
			doCut();
		}
		if (item == "copy") {
			if (menu != "elm")
				menuElm = null;
			doCopy();
		}

		if (item == "paste")
			doPaste();

		if (item == "selectAll")
			doSelectAll();

		if (item == "centrecircuit") {
			pushUndo();
			centreCircuit();
		}
		if (item == "stackAll")
			stackAll();
		if (item == "unstackAll")
			unstackAll();
		if (menu == "elm" && item == "edit")
			doEdit(menuElm);
		if (item == "delete") {
			if (menu == "elm")
				menuElm = null;
			doDelete();
		}

		if (item == "viewInScope" && menuElm != null) {
			int i;
			for (i = 0; i != scopeCount; i++)
				if (scopes[i].getElm() == null)
					break;
			if (i == scopeCount) {
				if (scopeCount == scopes.length)
					return;
				scopeCount++;
				scopes[i] = new Scope(this);
				scopes[i].setPosition(i);
				// handleResize();
			}
			scopes[i].setElm(menuElm);
		}
		if (menu == "scopepop") {
			pushUndo();
			if (item == "remove")
				scopes[menuScope].setElm(null);
			if (item == "speed2")
				scopes[menuScope].speedUp();
			if (item == "speed1/2")
				scopes[menuScope].slowDown();
			if (item == "scale")
				scopes[menuScope].adjustScale(.5);
			if (item == "maxscale")
				scopes[menuScope].adjustScale(1e-50);
			if (item == "stack")
				stackScope(menuScope);
			if (item == "unstack")
				unstackScope(menuScope);
			if (item == "selecty")
				scopes[menuScope].selectY();
			if (item == "reset")
				scopes[menuScope].resetGraph();
			if (item.indexOf("show") == 0 || item == "plotxy")
				scopes[menuScope].handleMenu(item);
			// cv.repaint();
		}
		if (menu == "circuits" && item.indexOf("setup ") == 0) {
			pushUndo();
			readSetupFile(item.substring(6), "", true);
		}

		// IES: Moved from itemStateChanged()
		if (menu == "main") {
			if (contextPanel != null)
				contextPanel.hide();
			// MenuItem mmi = (MenuItem) mi;
			// int prevMouseMode = mouseMode;
			setMouseMode(MouseMode.ADD_ELM);
			String s = item;
			if (s.length() > 0)
				mouseModeStr = s;
			if (s.compareTo("DragAll") == 0)
				setMouseMode(MouseMode.DRAG_ALL);
			else if (s.compareTo("DragRow") == 0)
				setMouseMode(MouseMode.DRAG_ROW);
			else if (s.compareTo("DragColumn") == 0)
				setMouseMode(MouseMode.DRAG_COLUMN);
			else if (s.compareTo("DragSelected") == 0)
				setMouseMode(MouseMode.DRAG_SELECTED);
			else if (s.compareTo("DragPost") == 0)
				setMouseMode(MouseMode.DRAG_POST);
			else if (s.compareTo("Select") == 0)
				setMouseMode(MouseMode.SELECT);

			tempMouseMode = mouseMode;
		}
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

	// public void mouseClicked(MouseEvent e) {
	public void onClick(ClickEvent e) {
		e.preventDefault();

		if ((e.getNativeButton() == NativeEvent.BUTTON_MIDDLE))
			scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), 0);
	}

	public void onContextMenu(ContextMenuEvent e) {
		e.preventDefault();
		int x, y;
		menuElm = mouseElm;
		menuScope = -1;
		if (scopeSelected != -1) {
			MenuBar m = scopes[scopeSelected].getMenu();
			menuScope = scopeSelected;
			if (m != null) {
				contextPanel = new PopupPanel(true);
				contextPanel.add(m);
				y = Math.max(0, Math.min(e.getNativeEvent().getClientY(), cv.getCoordinateSpaceHeight() - 400));
				contextPanel.setPopupPosition(e.getNativeEvent().getClientX(), y);
				contextPanel.show();
			}
		} else if (mouseElm != null) {
			elmScopeMenuItem.setEnabled(mouseElm.canViewInScope());
			elmEditMenuItem.setEnabled(mouseElm.getEditInfo(0) != null);
			contextPanel = new PopupPanel(true);
			contextPanel.add(elmMenuBar);
			contextPanel.setPopupPosition(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY());
			contextPanel.show();
		} else {
			doMainMenuChecks();
			contextPanel = new PopupPanel(true);
			contextPanel.add(mainMenuBar);
			x = Math.max(0, Math.min(e.getNativeEvent().getClientX(), cv.getCoordinateSpaceWidth() - 400));
			y = Math.max(0, Math.min(e.getNativeEvent().getClientY(), cv.getCoordinateSpaceHeight() - 450));
			contextPanel.setPopupPosition(x, y);
			contextPanel.show();
		}
	}

	public void onDoubleClick(DoubleClickEvent e) {
		e.preventDefault();

		// if (!didSwitch && mouseElm != null)
		if (mouseElm != null)
			doEdit(mouseElm);
	}

	public void onMouseDown(MouseDownEvent e) {
		e.preventDefault();

		// IES - hack to only handle left button events in the web version.
		if (e.getNativeButton() != NativeEvent.BUTTON_LEFT)
			return;

		mouseDragging = true;

		if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
			// // left mouse
			tempMouseMode = mouseMode;
			// if ((ex & MouseEvent.ALT_DOWN_MASK) != 0 &&
			// (ex & MouseEvent.META_DOWN_MASK) != 0)
			if (e.isAltKeyDown() && e.isMetaKeyDown())
				tempMouseMode = MouseMode.DRAG_COLUMN;
			// else if ((ex & MouseEvent.ALT_DOWN_MASK) != 0 &&
			// (ex & MouseEvent.SHIFT_DOWN_MASK) != 0)
			else if (e.isAltKeyDown() && e.isShiftKeyDown())
				tempMouseMode = MouseMode.DRAG_ROW;
			// else if ((ex & MouseEvent.SHIFT_DOWN_MASK) != 0)
			else if (e.isShiftKeyDown())
				tempMouseMode = MouseMode.SELECT;
			// else if ((ex & MouseEvent.ALT_DOWN_MASK) != 0)
			else if (e.isAltKeyDown())
				tempMouseMode = MouseMode.DRAG_ALL;
			else if (e.isControlKeyDown() || e.isMetaKeyDown())
				tempMouseMode = MouseMode.DRAG_POST;
		}

		// IES - Grab resize handles in select mode if they are far enough apart
		// and you are on top of them
		if (tempMouseMode == MouseMode.SELECT
				&& mouseElm != null
				&& distanceSq(mouseElm.getX(), mouseElm.getY(), mouseElm.getX2(), mouseElm.getY2()) >= 256
				&& (distanceSq(e.getX(), e.getY(), mouseElm.getX(), mouseElm.getY()) <= Display.POSTGRABSQ || distanceSq(e.getX(), e.getY(), mouseElm.getX2(), mouseElm.getY2()) <= Display.POSTGRABSQ)
				&& !anySelectedButMouse())
			tempMouseMode = MouseMode.DRAG_POST;

		if (tempMouseMode != MouseMode.SELECT && tempMouseMode != MouseMode.DRAG_SELECTED)
			clearSelection();

		if (doSwitch(e.getX(), e.getY())) {
			return;
		}

		pushUndo();
		initDragX = e.getX();
		initDragY = e.getY();
		dragging = true;
		if (tempMouseMode != MouseMode.ADD_ELM)
			return;

		int x0 = snapGrid(e.getX());
		int y0 = snapGrid(e.getY());
		if (!circuitArea.contains(x0, y0))
			return;

		dragElm = CircuitElementFactory.constructElement(mouseModeStr, x0, y0);
	}

	public void onMouseMove(MouseMoveEvent e) {
		e.preventDefault();
		if (mouseDragging) {
			mouseDragged(e);
			return;
		}
		// The following is in the original, but seems not to work/be needed for
		// GWT
		// if (e.getNativeButton()==NativeEvent.BUTTON_LEFT)
		// return;
		AbstractCircuitElement newMouseElm = null;
		int x = e.getX();
		int y = e.getY();
		dragX = snapGrid(x);
		dragY = snapGrid(y);
		draggingPost = -1;
		int i;
		// AbstractCircuitElement origMouse = mouseElm;

		mousePost = -1;
		plotXElm = plotYElm = null;
		if (mouseElm != null && (distanceSq(x, y, mouseElm.getX(), mouseElm.getY()) <= Display.POSTGRABSQ || distanceSq(x, y, mouseElm.getX2(), mouseElm.getY2()) <= Display.POSTGRABSQ)) {
			newMouseElm = mouseElm;
		} else {
			int bestDist = 100000;
			int bestArea = 100000;
			for (i = 0; i != elmList.size(); i++) {
				AbstractCircuitElement ce = getElm(i);
				if (ce.getBoundingBox().contains(x, y)) {
					int j;
					int area = ce.getBoundingBox().width * ce.getBoundingBox().height;
					int jn = ce.getPostCount();
					if (jn > 2)
						jn = 2;
					for (j = 0; j != jn; j++) {
						Point pt = ce.getPost(j);
						int dist = distanceSq(x, y, pt.x, pt.y);

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
		scopeSelected = -1;
		if (newMouseElm == null) {
			for (i = 0; i != scopeCount; i++) {
				Scope s = scopes[i];
				if (s.getRect().contains(x, y)) {
					newMouseElm = s.getElm();
					if (s.isPlotXY()) {
						plotXElm = s.getElm();
						plotYElm = s.getyElm();
					}
					scopeSelected = i;
				}
			}
			// // the mouse pointer was not in any of the bounding boxes, but we
			// // might still be close to a post
			for (i = 0; i != elmList.size(); i++) {
				AbstractCircuitElement ce = getElm(i);
				if (mouseMode == MouseMode.DRAG_POST) {
					if (distanceSq(ce.getX(), ce.getY(), x, y) < 26) {
						newMouseElm = ce;
						break;
					}
					if (distanceSq(ce.getX2(), ce.getY2(), x, y) < 26) {
						newMouseElm = ce;
						break;
					}
				}
				int j;
				int jn = ce.getPostCount();
				for (j = 0; j != jn; j++) {
					Point pt = ce.getPost(j);
					// int dist = distanceSq(x, y, pt.x, pt.y);
					if (distanceSq(pt.x, pt.y, x, y) < 26) {
						newMouseElm = ce;
						mousePost = j;
						break;
					}
				}
			}
		} else {
			mousePost = -1;
			// look for post close to the mouse pointer
			for (i = 0; i != newMouseElm.getPostCount(); i++) {
				Point pt = newMouseElm.getPost(i);
				if (distanceSq(pt.x, pt.y, x, y) < 26)
					mousePost = i;
			}
		}
		// if (mouseElm != origMouse)
		// cv.repaint();
		setMouseElm(newMouseElm);
	}

	public void onMouseOut(MouseOutEvent e) {
		scopeSelected = -1;
		mouseElm = plotXElm = plotYElm = null;
	}

	public void onMouseUp(MouseUpEvent e) {
		e.preventDefault();
		mouseDragging = false;
		tempMouseMode = mouseMode;
		selectedArea = null;
		dragging = false;
		boolean circuitChanged = false;
		if (heldSwitchElm != null) {
			heldSwitchElm.mouseUp();
			heldSwitchElm = null;
			circuitChanged = true;
		}
		if (dragElm != null) {
			// if the element is zero size then don't create it
			// IES - and disable any previous selection
			if (dragElm.getX() == dragElm.getX2() && dragElm.getY() == dragElm.getY2()) {
				dragElm.delete();
				if (mouseMode == MouseMode.SELECT || mouseMode == MouseMode.DRAG_SELECTED)
					clearSelection();
			} else {
				elmList.addElement(dragElm);
				circuitChanged = true;
			}
			dragElm = null;
		}
		if (circuitChanged)
			needAnalyze();
		if (dragElm != null)
			dragElm.delete();
		dragElm = null;
		// cv.repaint();
	}

	public void onMouseWheel(MouseWheelEvent e) {
		e.preventDefault();
		scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), e.getDeltaY());
		if (mouseElm instanceof MouseWheelHandler)
			((MouseWheelHandler) mouseElm).onMouseWheel(e);
	}

	public void onPreviewNativeEvent(NativePreviewEvent e) {
		int cc = e.getNativeEvent().getCharCode();
		int t = e.getTypeInt();
		int code = e.getNativeEvent().getKeyCode();
		if (dialogIsShowing()) {
			if (scrollValuePopup != null && scrollValuePopup.isShowing() && (t & Event.ONKEYDOWN) != 0) {
				if (code == KEY_ESCAPE || code == KEY_SPACE)
					scrollValuePopup.close(false);
				if (code == KEY_ENTER)
					scrollValuePopup.close(true);
			}
			if (getEditDialog() != null && getEditDialog().isShowing() && (t & Event.ONKEYDOWN) != 0) {
				if (code == KEY_ESCAPE)
					getEditDialog().closeDialog();
				if (code == KEY_ENTER) {
					getEditDialog().apply();
					getEditDialog().closeDialog();
				}
			}
			return;
		}
		if ((t & Event.ONKEYDOWN) != 0) {

			if (code == KEY_BACKSPACE || code == KEY_DELETE) {
				doDelete();
				e.cancel();
			}
			if (code == KEY_ESCAPE) {
				setMouseMode(MouseMode.SELECT);
				mouseModeStr = "Select";
				tempMouseMode = mouseMode;
				e.cancel();
			}
			if (e.getNativeEvent().getCtrlKey() || e.getNativeEvent().getMetaKey()) {
				if (code == KEY_C) {
					menuPerformed("key", "copy");
					e.cancel();
				}
				if (code == KEY_X) {
					menuPerformed("key", "cut");
					e.cancel();
				}
				if (code == KEY_V) {
					menuPerformed("key", "paste");
					e.cancel();
				}
				if (code == KEY_Z) {
					menuPerformed("key", "undo");
					e.cancel();
				}
				if (code == KEY_Y) {
					menuPerformed("key", "redo");
					e.cancel();
				}
				if (code == KEY_A) {
					menuPerformed("key", "selectAll");
					e.cancel();
				}
			}
		}
		if ((t & Event.ONKEYPRESS) != 0) {
			if (cc > 32 && cc < 127) {
				String c = shortcuts[cc];
				e.cancel();
				if (c == null)
					return;
				setMouseMode(MouseMode.ADD_ELM);
				mouseModeStr = c;
				tempMouseMode = mouseMode;
			}
			if (cc == 32) {
				setMouseMode(MouseMode.SELECT);
				mouseModeStr = "Select";
				tempMouseMode = mouseMode;
				e.cancel();
			}
		}
	}

	private void processSetupList(byte b[], int len, final boolean openDefault) {
		MenuBar currentMenuBar;
		MenuBar stack[] = new MenuBar[6];
		int stackptr = 0;
		currentMenuBar = new MenuBar(true);
		currentMenuBar.setAutoOpen(true);
		menuBar.addItem("Circuits", currentMenuBar);
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
					// menu.add(getMenuItem(title, "setup " + file));
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

	public void pushUndo() {
		redoStack.removeAllElements();
		String s = dumpCircuit();
		if (undoStack.size() > 0 && s.compareTo(undoStack.lastElement()) == 0)
			return;
		undoStack.add(s);
		enableUndoRedo();
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
		int p;
		for (p = 0; p < len;) {
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
						System.out.println("unrecognized dump type: " + type);
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

	private void readSetup(String text, boolean retain, boolean centre) {
		readSetup(text.getBytes(), text.length(), retain, centre);
	}

	private void readSetupFile(String str, String title, boolean centre) {
		t = 0;
		System.out.println(str);
		// try {
		// TODO: Maybe think about some better approach to cache management!
		String url = GWT.getModuleBaseURL();
		url = url.substring(0,url.indexOf("circuitjs1"));
		url = url+ "circuits/" + str + "?v=" + random.nextInt();
		loadFileFromURL(url, centre);
	}

	public void removeWidgetFromVerticalPanel(Widget w) {
		verticalPanel.remove(w);
		if (iFrame != null)
			setiFrameHeight();
	}

	public void removeZeroLengthElements() {
		int i;
		// boolean changed = false;
		for (i = elmList.size() - 1; i >= 0; i--) {
			AbstractCircuitElement ce = getElm(i);
			if (ce.getX() == ce.getX2() && ce.getY() == ce.getY2()) {
				elmList.removeElementAt(i);
				ce.delete();
				// changed = true;
			}
		}
		needAnalyze();
	}

	private void resetAction() {
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
							stop("nan/infinite matrix!", null);
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
						stop("Singular matrix!", null);
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
					 * System.out.println(j + " " + res + " " + ri.type + " " +
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
						// System.out.println("setting vsrc " + ji + " to " +
						// res);
						voltageSources[ji].setCurrent(ji, res);
					}
				}
				if (!circuitNonLinear)
					break;
			}
			if (subiter > 5)
				System.out.print("converged after " + subiter + " iterations\n");
			if (subiter == subiterCount) {
				stop("Convergence failed!", null);
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
		// System.out.println((System.currentTimeMillis()-lastFrameTime)/(double)
		// iter);
	}

	private void scrollValues(int x, int y, int deltay) {
		if (mouseElm != null && !dialogIsShowing())
			if (mouseElm instanceof ResistorElm || mouseElm instanceof CapacitorElm || mouseElm instanceof InductorElm) {
				scrollValuePopup = new ScrollValuePopup(x, y, deltay, mouseElm, this);
			}
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

	private void setGrid() {
		gridSize = (getSmallGridCheckItem().getState()) ? 8 : 16;
		gridMask = ~(gridSize - 1);
		gridRound = gridSize / 2 - 1;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
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
		// GWT.log("Root OH="+RootLayoutPanel.get().getOffsetHeight());
		// GWT.log("iF top="+iFrame.getAbsoluteTop() );
		// GWT.log("RP top="+RootLayoutPanel.get().getAbsoluteTop());
		// GWT.log("ih="+ih);
		// GWT.log("if left="+iFrame.getAbsoluteLeft());
		if (ih < 0)
			ih = 0;
		iFrame.setHeight(ih + "px");
	}

	private void setMenuSelection() {
		if (menuElm != null) {
			if (menuElm.isSelected())
				return;
			clearSelection();
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

	private void setMouseMode(MouseMode addElm) {
		mouseMode = addElm;
		if (addElm == MouseMode.ADD_ELM) {
			cv.addStyleName("cursorCross");
			cv.removeStyleName("cursorPointer");
		} else {
			cv.addStyleName("cursorPointer");
			cv.removeStyleName("cursorCross");
		}
	}

	private void setNodeList(Vector<CircuitNode> nodeList) {
		this.nodeList = nodeList;
	}

	private void setPowerBarEnable() {
		if (getPowerCheckItem().getState()) {
			powerLabel.setStyleName("disabled", false);
			powerBar.enable();
		} else {
			powerLabel.setStyleName("disabled", true);
			powerBar.disable();
		}
	}

	private CheckboxMenuItem setPowerCheckItem(CheckboxMenuItem powerCheckItem) {
		this.powerCheckItem = powerCheckItem;
		return powerCheckItem;
	}

	private CheckboxMenuItem setPrintableCheckItem(CheckboxMenuItem printableCheckItem) {
		this.printableCheckItem = printableCheckItem;
		return printableCheckItem;
	}

	public void setScopeFreqMenuItem(CheckboxMenuItem scopeFreqMenuItem) {
		this.scopeFreqMenuItem = scopeFreqMenuItem;
	}

	public void setScopeIbMenuItem(CheckboxMenuItem scopeIbMenuItem) {
		this.scopeIbMenuItem = scopeIbMenuItem;
	}

	public void setScopeIcMenuItem(CheckboxMenuItem scopeIcMenuItem) {
		this.scopeIcMenuItem = scopeIcMenuItem;
	}

	public void setScopeIeMenuItem(CheckboxMenuItem scopeIeMenuItem) {
		this.scopeIeMenuItem = scopeIeMenuItem;
	}

	public void setScopeIMenuItem(CheckboxMenuItem scopeIMenuItem) {
		this.scopeIMenuItem = scopeIMenuItem;
	}

	public void setScopeMaxMenuItem(CheckboxMenuItem scopeMaxMenuItem) {
		this.scopeMaxMenuItem = scopeMaxMenuItem;
	}

	public void setScopeMenuBar(MenuBar scopeMenuBar) {
		this.scopeMenuBar = scopeMenuBar;
	}

	public void setScopeMinMenuItem(CheckboxMenuItem scopeMinMenuItem) {
		this.scopeMinMenuItem = scopeMinMenuItem;
	}

	public void setScopePowerMenuItem(CheckboxMenuItem scopePowerMenuItem) {
		this.scopePowerMenuItem = scopePowerMenuItem;
	}

	public void setScopeResistMenuItem(CheckboxMenuItem scopeResistMenuItem) {
		this.scopeResistMenuItem = scopeResistMenuItem;
	}

	public void setScopeScaleMenuItem(CheckboxMenuItem scopeScaleMenuItem) {
		this.scopeScaleMenuItem = scopeScaleMenuItem;
	}

	public void setScopeSelected(int scopeSelected) {
		this.scopeSelected = scopeSelected;
	}

	public void setScopeSelectYMenuItem(MenuItem scopeSelectYMenuItem) {
		this.scopeSelectYMenuItem = scopeSelectYMenuItem;
	}

	public void setScopeVbcMenuItem(CheckboxMenuItem scopeVbcMenuItem) {
		this.scopeVbcMenuItem = scopeVbcMenuItem;
	}

	public void setScopeVbeMenuItem(CheckboxMenuItem scopeVbeMenuItem) {
		this.scopeVbeMenuItem = scopeVbeMenuItem;
	}

	public void setScopeVceIcMenuItem(CheckboxMenuItem scopeVceIcMenuItem) {
		this.scopeVceIcMenuItem = scopeVceIcMenuItem;
	}

	public void setScopeVceMenuItem(CheckboxMenuItem scopeVceMenuItem) {
		this.scopeVceMenuItem = scopeVceMenuItem;
	}

	public void setScopeVIMenuItem(CheckboxMenuItem scopeVIMenuItem) {
		this.scopeVIMenuItem = scopeVIMenuItem;
	}

	public void setScopeVMenuItem(CheckboxMenuItem scopeVMenuItem) {
		this.scopeVMenuItem = scopeVMenuItem;
	}

	public void setScopeXYMenuItem(CheckboxMenuItem scopeXYMenuItem) {
		this.scopeXYMenuItem = scopeXYMenuItem;
	}

	protected CheckboxMenuItem setShowValuesCheckItem(CheckboxMenuItem showValuesCheckItem) {
		this.showValuesCheckItem = showValuesCheckItem;
		return showValuesCheckItem;
	}

	protected CheckboxMenuItem setSmallGridCheckItem(CheckboxMenuItem smallGridCheckItem) {
		this.smallGridCheckItem = smallGridCheckItem;
		return smallGridCheckItem;
	}

	protected void setStoppedCheck(Checkbox stoppedCheck) {
		this.stoppedCheck = stoppedCheck;
	}

	public void setT(double t) {
		this.t = t;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	public void setTransScopeMenuBar(MenuBar transScopeMenuBar) {
		this.transScopeMenuBar = transScopeMenuBar;
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

	protected CheckboxMenuItem setVoltsCheckItem(CheckboxMenuItem voltsCheckItem) {
		this.voltsCheckItem = voltsCheckItem;
		return voltsCheckItem;
	}

	public int snapGrid(int x) {
		return (x + gridRound) & gridMask;
	}

	private void stackAll() {
		int i;
		for (i = 0; i != scopeCount; i++) {
			scopes[i].setPosition(0);
			scopes[i].setShowMax(scopes[i].setShowMin(false));
		}
	}

	private void stackScope(int s) {
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
					// System.out.println("Stamping constant " + i + " " + j +
					// " " + x);
					circuitRightSide[i] -= x * ri.getValue();
					return;
				}
				j = ri.getMapCol();
				// System.out.println("stamping " + i + " " + j + " " + x);
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
			System.out.print("bad resistance " + r + " " + r0 + "\n");
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
		// System.out.println("rschanges true " + (i-1));
		if (i > 0)
			circuitRowInfo[i - 1].setRsChanges(true);
	}

	// stamp value x on the right side of row i, representing an
	// independent current source flowing into node i
	public void stampRightSide(int i, double x) {
		if (i > 0) {
			if (circuitNeedsMap) {
				i = circuitRowInfo[i - 1].getMapRow();
				// System.out.println("stamping " + i + " " + x);
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

	private void unstackAll() {
		int i;
		for (i = 0; i != scopeCount; i++) {
			scopes[i].setPosition(i);
			scopes[i].setShowMax(true);
		}
	}

	private void unstackScope(int s) {
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
		// long myrunstarttime;
		// long mydrawstarttime;
		AbstractCircuitElement realMouseElm;
		// if (winSize == null || winSize.width == 0)
		// return;
		mystarttime = System.currentTimeMillis();
		if (analyzeFlag) {
			analyzeCircuit();
			analyzeFlag = false;
		}
		// if (editDialog != null && editDialog.elm instanceof
		// AbstractCircuitElement)
		// mouseElm = (AbstractCircuitElement) (editDialog.elm);
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
		// myrunstarttime = System.currentTimeMillis();
		if (!getStoppedCheck().getState()) {
			try {
				runCircuit();
			} catch (Exception e) {
				e.printStackTrace();
				analyzeFlag = true;
				// cv.repaint();
				return;
			}
			// myruntime += System.currentTimeMillis() - myrunstarttime;
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
		AbstractCircuitElement.powerMult = Math.exp(powerBar.getValue() / 4.762 - 7);

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
					g.fillOval(ce.getX() - 3, ce.getY() - 3, 7, 7);
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
		if (dragElm != null && (dragElm.getX() != dragElm.getX2() || dragElm.getY() != dragElm.getY2())) {
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
				info[i++] = badnodes + ((badnodes == 1) ? " bad connection" : " bad connections");

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
		// g.drawString("Framerate: " +
		// AbstractCircuitElement.showFormat.format(framerate),
		// 10, 10);
		// g.drawString("Steprate: " +
		// AbstractCircuitElement.showFormat.format(steprate),
		// 10, 30);
		// g.drawString("Steprate/iter: " +
		// AbstractCircuitElement.showFormat.format(steprate/getIterCount()),
		// 10, 50);
		// g.drawString("iterc: " +
		// AbstractCircuitElement.showFormat.format(getIterCount()), 10, 70);
		// g.drawString("Frames: "+ frames,10,90);
		// g.drawString("ms per frame (other): "+
		// AbstractCircuitElement.showFormat.format((mytime-myruntime-mydrawtime)/myframes),10,110);
		// g.drawString("ms per frame (simmer): "+
		// AbstractCircuitElement.showFormat.format((myruntime)/myframes),10,130);
		// g.drawString("ms per frame (draw): "+
		// AbstractCircuitElement.showFormat.format((mydrawtime)/myframes),10,150);

		cvcontext.drawImage(backcontext.getCanvas(), 0.0, 0.0);
		// IES - remove interaction and delay
		// if (!stoppedCheck.getState() && circuitMatrix != null) {
		// Limit to 50 fps (thanks to Jurgen Klotzer for this)
		// long delay = 1000/50 - (System.currentTimeMillis() - lastFrameTime);
		// realg.drawString("delay: " + delay, 10, 110);
		// if (delay > 0) {
		// try {
		// Thread.sleep(delay);
		// } catch (InterruptedException e) {
		// }
		// }

		// cv.repaint(0);
		// }
		lastFrameTime = lastTime;
		mytime = mytime + System.currentTimeMillis() - mystarttime;
		// myframes++;
	}

	public void updateVoltageSource(int n1, int n2, int vs, double v) {
		int vn = getNodeList().size() + vs;
		stampRightSide(vn, v);
	}

	public boolean isConverged() {
		return converged;
	}

	public void setConverged(boolean converged) {
		this.converged = converged;
	}

	public int getSubIterations() {
		return subIterations;
	}

	public void setSubIterations(int subIterations) {
		this.subIterations = subIterations;
	}

	public AbstractCircuitElement getPlotXElm() {
		return plotXElm;
	}

	public void setPlotXElm(AbstractCircuitElement plotXElm) {
		this.plotXElm = plotXElm;
	}

	public AbstractCircuitElement getPlotYElm() {
		return plotYElm;
	}

	public void setPlotYElm(AbstractCircuitElement plotYElm) {
		this.plotYElm = plotYElm;
	}

}
