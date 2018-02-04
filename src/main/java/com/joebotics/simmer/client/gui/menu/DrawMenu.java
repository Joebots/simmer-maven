package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.widget.CheckboxMenuItem;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.CircuitElementFactory;
import com.joebotics.simmer.client.util.MessageI18N;

import java.util.Vector;

/**
 * @deprecated Functionality is moved to the {@code SchematicDialog} class
 * Created by joe on 7/18/16.
 */
public class DrawMenu extends MenuBar {

    private Vector<String> mainMenuItemNames = new Vector<String>();
    private Vector<CheckboxMenuItem> mainMenuItems = new Vector<CheckboxMenuItem>();
    private Simmer simmer;

    public DrawMenu(Simmer simmer, boolean vertical) {
        super(vertical);
        setAutoOpen(true);
        this.simmer = simmer;
        this.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Wire"), "WireElm"));
        this.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Resistor"), "ResistorElm"));

        // Passive Components
        MenuBar passMenuBar = new MenuBar(true);
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Capacitor"), "CapacitorElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Inductor"), "InductorElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Switch"), "SwitchElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Push_Switch"), "PushSwitchElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_SPDT_Switch"), "Switch2Elm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Potentiometer"), "PotElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Transformer"), "TransformerElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Tapped_Transformer"), "TappedTransformerElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Transmission_Line"), "TransLineElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Relay"), "RelayElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Memristor"), "MemristorElm"));
        passMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Spark_Gap"), "SparkGapElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Passive_Components")), passMenuBar);

        // Inputs and Sources
        MenuBar inputMenuBar = new MenuBar(true);
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Ground"), "GroundElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Voltage_Source_(2-terminal)"), "DCVoltageElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_A/C_Voltage_Source_(2-terminal)"), "ACVoltageElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Voltage_Source_(1-terminal)"), "RailElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_A/C_Voltage_Source_(1-terminal)"), "ACRailElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Square_Wave_Source_(1-terminal)"), "SquareRailElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Clock"), "ClockElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_A/C_Sweep"), "SweepElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Variable_Voltage"), "VarRailElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Antenna"), "AntennaElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_AM_Source"), "AMElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_FM_Source"), "FMElm"));
        inputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Current_Source"), "CurrentElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Inputs_and_Sources")), inputMenuBar);

        // Outputs and Labels
        MenuBar outputMenuBar = new MenuBar(true);
        outputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Analog_Output"), "OutputElm"));
        outputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_LED"), "LEDElm"));
        outputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Lamp_(beta)"), "LampElm"));
        outputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Text"), "TextElm"));
        outputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Box"), "BoxElm"));
        outputMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Scope_Probe"), "ProbeElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Outputs_and_Labels")), outputMenuBar);

        // Active Components
        MenuBar activeMenuBar = new MenuBar(true);
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Diode"), "DiodeElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Zener_Diode"), "ZenerElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Transistor_(bipolar_NPN)"), "NTransistorElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Transistor_(bipolar_PNP)"), "PTransistorElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_MOSFET_(N-Channel)"), "NMosfetElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_MOSFET_(P-Channel)"), "PMosfetElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_JFET_(N-Channel)"), "NJfetElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_JFET_(P-Channel)"), "PJfetElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_SCR"), "SCRElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Tunnel_Diode"), "TunnelDiodeElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Triode"), "TriodeElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Varactor/Varicap"), "VaractorElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Diac"), "DiacElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Triac"), "TriacElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Photoresistor"), "PhotoResistorElm"));
        activeMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Thermistor"), "ThermistorElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Active_Components")), activeMenuBar);

//        // Active Building Blocks
        MenuBar activeBlocMenuBar = new MenuBar(true);
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Op_Amp_(-_on_top)"), "OpAmpElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Op_Amp_(+_on_top)"), "OpAmpSwapElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Analog_Switch_(SPST)"), "AnalogSwitchElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Analog_Switch_(SPDT)"), "AnalogSwitch2Elm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Tristate_Buffer"), "TriStateElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Schmitt_Trigger"), "SchmittElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Schmitt_Trigger_(Inverting)"), "InvertingSchmittElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_CCII+"), "CC2Elm"));
        activeBlocMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_CCII-"), "CC2NegElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Active_Building_Blocks")), activeBlocMenuBar);
//
//        // Logic Gates, Input and Output
        MenuBar gateMenuBar = new MenuBar(true);
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Logic_Input"), "LogicInputElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Logic_Output"), "LogicOutputElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Inverter"), "InverterElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_NAND_Gate"), "NandGateElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_NOR_Gate"), "NorGateElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_AND_Gate"), "AndGateElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_OR_Gate"), "OrGateElm"));
        gateMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_XOR_Gate"), "XorGateElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Logic_Gates_Input_and_Output")), gateMenuBar);

        // Digital Chips
        MenuBar chipMenuBar = new MenuBar(true);
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_D_Flip-Flop"), "DFlipFlopElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_JK_Flip-Flop"), "JKFlipFlopElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_T_Flip-Flop"), "TFlipFlopElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_7_Segment_LED"), "SevenSegElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_7_Segment_Decoder"), "SevenSegDecoderElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Multiplexer"), "MultiplexerElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Demultiplexer"), "DeMultiplexerElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_SIPO_shift_register"), "SipoShiftElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_PISO_shift_register"), "PisoShiftElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Counter"), "CounterElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Decade_Counter"), "DecadeElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Latch"), "LatchElm"));

        // missing i18n key
        //chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Static_RAM"), "SRAMElm");

        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Sequence_generator"), "SeqGenElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Full_Adder"), "FullAdderElm"));
        chipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Half_Adder"), "HalfAdderElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Digital_Chips")), chipMenuBar);

        // Analog and Hybrid Chips
        MenuBar achipMenuBar = new MenuBar(true);
        achipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_555_Timer"), "TimerElm"));
        achipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Phase_Comparator"), "PhaseCompElm"));
        achipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_DAC"), "DACElm"));
        achipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_ADC"), "ADCElm"));
        achipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_VCO"), "VCOElm"));
        achipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Monostable"), "MonostableElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Analog_and_Hybrid_Chips")), achipMenuBar);
        
        // Programmable
        MenuBar pchipMenuBar = new MenuBar(true);
        pchipMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Add_Script"), "ScriptElm"));
        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Programmable_Chips")), pchipMenuBar);
        
        // Drag
        MenuBar otherMenuBar = new MenuBar(true);
        CheckboxMenuItem mi;
        otherMenuBar.addItem(mi = getClassCheckItem(MessageI18N.getMessage("Drag_All"), "DragAll"));
        mi.addShortcut("(Alt-drag)");
        otherMenuBar.addItem(mi = getClassCheckItem(MessageI18N.getMessage("Drag_Row"), "DragRow"));
        mi.addShortcut("(S-right)");
        otherMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Drag_Column"), "DragColumn"));
        otherMenuBar.addItem(getClassCheckItem(MessageI18N.getMessage("Drag_Selected"), "DragSelected"));
        otherMenuBar.addItem(mi = getClassCheckItem(MessageI18N.getMessage("Drag_Post"), "DragPost"));

        String os = Window.Navigator.getPlatform();
        boolean isMac = (os.toLowerCase().contains("mac"));
        mi.addShortcut("(" + ((isMac) ? "Cmd" : "Ctrl") + "-drag)");

        this.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + "&nbsp;</div>" + MessageI18N.getMessage("Drag")), otherMenuBar);

        this.addItem(mi = getClassCheckItem("Select/Drag Sel", "Select"));
        mi.addShortcut("(space or Shift-drag)");
    }

    private CheckboxMenuItem getClassCheckItem(String s, String t) {
        String shortcut = "";
        AbstractCircuitElement elm = CircuitElementFactory.constructElement(t, 0, 0);
        CheckboxMenuItem mi;

        // register(c, elm);
        if (elm != null) {
            if (elm.needsShortcut()) {
                shortcut += (char) elm.getShortcut();
                simmer.getShortcuts()[elm.getShortcut()] = t;
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

    public Vector<String> getMainMenuItemNames() {
        return mainMenuItemNames;
    }

    public Vector<CheckboxMenuItem> getMainMenuItems() {
        return mainMenuItems;
    }
}
