package com.joebotics.simmer.client.util;

import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.elcomp.chips.*;
import com.joebotics.simmer.client.elcomp.sensors.*;

public class CircuitElementFactory {

    public static AbstractCircuitElement constructElement(String n, int x1, int y1) {

        if (n == "GroundElm")
            return  new GroundElm(x1, y1);

        if (n == "ResistorElm")
            return  new ResistorElm(x1, y1);

        if (n == "RailElm")
            return  new RailElm(x1, y1);

        if (n == "SwitchElm")
            return  new SwitchElm(x1, y1);

        if (n == "Switch2Elm")
            return  new Switch2Elm(x1, y1);

        if (n == "NTransistorElm")
            return  new NTransistorElm(x1, y1);

        if (n == "PTransistorElm")
            return  new PTransistorElm(x1, y1);

        if (n == "WireElm")
            return  new WireElm(x1, y1);

        if (n == "CapacitorElm")
            return  new CapacitorElm(x1, y1);

        if (n == "InductorElm")
            return  new InductorElm(x1, y1);

        if (n == "DCVoltageElm")
            return  new DCVoltageElm(x1, y1);

        if (n == "VarRailElm")
            return  new VarRailElm(x1, y1);

        if (n == "PotElm")
            return  new PotElm(x1, y1);

        if (n == "OutputElm")
            return  new OutputElm(x1, y1);

        if (n == "CurrentElm")
            return  new CurrentElm(x1, y1);

        if (n == "ProbeElm")
            return  new ProbeElm(x1, y1);

        if (n == "DiodeElm")
            return  new DiodeElm(x1, y1);

        if (n == "ZenerElm")
            return  new ZenerElm(x1, y1);

        if (n == "ACVoltageElm")
            return  new ACVoltageElm(x1, y1);

        if (n == "ACRailElm")
            return  new ACRailElm(x1, y1);

        if (n == "SquareRailElm")
            return  new SquareRailElm(x1, y1);

        if (n == "SweepElm")
            return  new SweepElm(x1, y1);

        if (n == "LEDElm")
            return  new LEDElm(x1, y1);

        if (n == "AntennaElm")
            return  new AntennaElm(x1, y1);

        if (n == "LogicInputElm")
            return  new LogicInputElm(x1, y1);

        if (n == "LogicOutputElm")
            return  new LogicOutputElm(x1, y1);

        if (n == "TransformerElm")
            return  new TransformerElm(x1, y1);

        if (n == "TappedTransformerElm")
            return  new TappedTransformerElm(x1, y1);

        if (n == "TransLineElm")
            return  new TransLineElm(x1, y1);

        if (n == "RelayElm")
            return  new RelayElm(x1, y1);

        if (n == "MemristorElm")
            return  new MemristorElm(x1, y1);

        if (n == "SparkGapElm")
            return  new SparkGapElm(x1, y1);

        if (n == "ClockElm")
            return  new ClockElm(x1, y1);

        if (n == "AMElm")
            return  new AMElm(x1, y1);

        if (n == "FMElm")
            return  new FMElm(x1, y1);

        if (n == "LampElm")
            return  new LampElm(x1, y1);

        if (n == "PushSwitchElm")
            return  new PushSwitchElm(x1, y1);

        if (n == "OpAmpElm")
            return  new OpAmpElm(x1, y1);

        if (n == "OpAmpSwapElm")
            return  new OpAmpSwapElm(x1, y1);

        if (n == "NMosfetElm")
            return  new NMosfetElm(x1, y1);

        if (n == "PMosfetElm")
            return  new PMosfetElm(x1, y1);

        if (n == "NJfetElm")
            return  new NJfetElm(x1, y1);

        if (n == "PJfetElm")
            return  new PJfetElm(x1, y1);

        if (n == "AnalogSwitchElm")
            return  new AnalogSwitchElm(x1, y1);

        if (n == "AnalogSwitch2Elm")
            return  new AnalogSwitch2Elm(x1, y1);

        if (n == "SchmittElm")
            return  new SchmittElm(x1, y1);

        if (n == "InvertingSchmittElm")
            return  new InvertingSchmittElm(x1, y1);

        if (n == "TriStateElm")
            return  new TriStateElm(x1, y1);

        if (n == "SCRElm")
            return  new SCRElm(x1, y1);

        if (n == "DiacElm")
            return  new DiacElm(x1, y1);

        if (n == "TriacElm")
            return  new TriacElm(x1, y1);

        if (n == "TriodeElm")
            return  new TriodeElm(x1, y1);

        if (n == "TunnelDiodeElm")
            return  new TunnelDiodeElm(x1, y1);

        if (n == "CC2Elm")
            return  new CC2Elm(x1, y1);

        if (n == "CC2NegElm")
            return  new CC2NegElm(x1, y1);

        if (n == "InverterElm")
            return  new InverterElm(x1, y1);

        if (n == "NandGateElm")
            return  new NandGateElm(x1, y1);

        if (n == "NorGateElm")
            return  new NorGateElm(x1, y1);

        if (n == "AndGateElm")
            return  new AndGateElm(x1, y1);

        if (n == "OrGateElm")
            return  new OrGateElm(x1, y1);

        if (n == "XorGateElm")
            return  new XorGateElm(x1, y1);

        if (n == "ScriptElm")
            return  new ScriptElm(x1, y1);

        if (n == "DFlipFlopElm")
            return  new DFlipFlopElm(x1, y1);

        if (n == "JKFlipFlopElm")
            return  new JKFlipFlopElm(x1, y1);

        if (n == "SevenSegElm")
            return  new SevenSegElm(x1, y1);

        if (n == "MultiplexerElm")
            return  new MultiplexerElm(x1, y1);

        if (n == "DeMultiplexerElm")
            return  new DeMultiplexerElm(x1, y1);

        if (n == "SipoShiftElm")
            return  new SipoShiftElm(x1, y1);

        if (n == "PisoShiftElm")
            return  new PisoShiftElm(x1, y1);

        if (n == "PhaseCompElm")
            return  new PhaseCompElm(x1, y1);

        if (n == "CounterElm")
            return  new CounterElm(x1, y1);

        if (n == "DecadeElm")
            return  new DecadeElm(x1, y1);

        if (n == "TimerElm")
            return  new TimerElm(x1, y1);

        if (n == "DACElm")
            return  new DACElm(x1, y1);

        if (n == "ADCElm")
            return  new ADCElm(x1, y1);

        if (n == "LatchElm")
            return  new LatchElm(x1, y1);

        if (n == "SeqGenElm")
            return  new SeqGenElm(x1, y1);

        if (n == "VCOElm")
            return  new VCOElm(x1, y1);

        if (n == "BoxElm")
            return  new BoxElm(x1, y1);

        if (n == "TextElm")
            return  new TextElm(x1, y1);

        if (n == "TFlipFlopElm")
            return  new TFlipFlopElm(x1, y1);

        if (n == "SevenSegDecoderElm")
            return  new SevenSegDecoderElm(x1, y1);

        if (n == "FullAdderElm")
            return  new FullAdderElm(x1, y1);

        if (n == "HalfAdderElm")
            return  new HalfAdderElm(x1, y1);

        if (n == "MonostableElm")
            return  new MonostableElm(x1, y1);

        if (n == "GpioInputElm")
            return  new GpioInputElm(x1, y1);

        if (n == "GpioOutputElm")
            return  new GpioOutputElm(x1, y1);

        if (n == "KY_002")
            return  new KY002Elm(x1, y1);

        if (n == "KY_003")
            return  new KY003Elm(x1, y1);

        if (n == "KY_004")
            return  new KY004Elm(x1, y1);

        if (n == "KY_005")
            return  new KY005Elm(x1, y1);

        if (n == "KY_006")
            return  new KY006Elm(x1, y1);

        if (n == "KY_008")
            return  new KY008Elm(x1, y1);

        if (n == "KY_009")
            return  new KY009Elm(x1, y1);

        if (n == "KY_010")
            return  new KY010Elm(x1, y1);

        if (n == "KY_012")
            return  new KY012Elm(x1, y1);

        if (n == "KY_013")
            return  new KY013Elm(x1, y1);

        if (n == "KY_016")
            return  new KY016Elm(x1, y1);

        if (n == "KY_017")
            return new KY017Elm(x1, y1);

        if (n == "KY_020")
            return new KY020Elm(x1, y1);

        if (n == "KY_021")
            return new KY021Elm(x1, y1);

        if (n == "KY_024")
            return new KY024Elm(x1, y1);

        if (n == "KY_031")
            return new KY031Elm(x1, y1);

        if (n == "KY_033")
            return new KY033Elm(x1, y1);

        if (n == "SG90")
            return new SG90(x1, y1);
        return null;
    }

    public static AbstractCircuitElement createCircuitElement(int tint, int x1, int y1, int x2, int y2, int f,
            StringTokenizer st) {
        if (tint == 'g')
            return  new GroundElm(x1, y1, x2, y2, f, st);

        if (tint == 'r')
            return  new ResistorElm(x1, y1, x2, y2, f, st);

        if (tint == 'R')
            return  new RailElm(x1, y1, x2, y2, f, st);

        if (tint == 's')
            return  new SwitchElm(x1, y1, x2, y2, f, st);

        if (tint == 'S')
            return  new Switch2Elm(x1, y1, x2, y2, f, st);

        if (tint == 't')
            return  new TransistorElm(x1, y1, x2, y2, f, st);

        if (tint == 'w')
            return  new WireElm(x1, y1, x2, y2, f, st);

        if (tint == 'c')
            return  new CapacitorElm(x1, y1, x2, y2, f, st);

        if (tint == 'l')
            return  new InductorElm(x1, y1, x2, y2, f, st);

        if (tint == 'v')
            return  new VoltageElm(x1, y1, x2, y2, f, st);

        if (tint == 172)
            return  new VarRailElm(x1, y1, x2, y2, f, st);

        if (tint == 174)
            return  new PotElm(x1, y1, x2, y2, f, st);

        if (tint == 'O')
            return  new OutputElm(x1, y1, x2, y2, f, st);

        if (tint == 'i')
            return  new CurrentElm(x1, y1, x2, y2, f, st);

        if (tint == 'p')
            return  new ProbeElm(x1, y1, x2, y2, f, st);

        if (tint == 'd')
            return  new DiodeElm(x1, y1, x2, y2, f, st);

        if (tint == 'z')
            return  new ZenerElm(x1, y1, x2, y2, f, st);

        if (tint == 170)
            return  new SweepElm(x1, y1, x2, y2, f, st);

        if (tint == 162)
            return  new LEDElm(x1, y1, x2, y2, f, st);

        if (tint == 'A')
            return  new AntennaElm(x1, y1, x2, y2, f, st);

        if (tint == 'L')
            return  new LogicInputElm(x1, y1, x2, y2, f, st);

        if (tint == 'M')
            return  new LogicOutputElm(x1, y1, x2, y2, f, st);

        if (tint == 'T')
            return  new TransformerElm(x1, y1, x2, y2, f, st);

        if (tint == 169)
            return  new TappedTransformerElm(x1, y1, x2, y2, f, st);

        if (tint == 171)
            return  new TransLineElm(x1, y1, x2, y2, f, st);

        if (tint == 178)
            return  new RelayElm(x1, y1, x2, y2, f, st);

        if (tint == 'm')
            return  new MemristorElm(x1, y1, x2, y2, f, st);

        if (tint == 187)
            return  new SparkGapElm(x1, y1, x2, y2, f, st);

        if (tint == 200)
            return  new AMElm(x1, y1, x2, y2, f, st);

        if (tint == 201)
            return  new FMElm(x1, y1, x2, y2, f, st);

        if (tint == 181)
            return  new LampElm(x1, y1, x2, y2, f, st);

        if (tint == 'a')
            return  new OpAmpElm(x1, y1, x2, y2, f, st);

        if (tint == 'f')
            return  new MosfetElm(x1, y1, x2, y2, f, st);

        if (tint == 'j')
            return  new JfetElm(x1, y1, x2, y2, f, st);

        if (tint == 159)
            return  new AnalogSwitchElm(x1, y1, x2, y2, f, st);

        if (tint == 160)
            return  new AnalogSwitch2Elm(x1, y1, x2, y2, f, st);

        if (tint == 180)
            return  new TriStateElm(x1, y1, x2, y2, f, st);

        if (tint == 182)
            return  new SchmittElm(x1, y1, x2, y2, f, st);

        if (tint == 183)
            return  new InvertingSchmittElm(x1, y1, x2, y2, f, st);

        if (tint == 177)
            return  new SCRElm(x1, y1, x2, y2, f, st);

        if (tint == 203)
            return  new DiacElm(x1, y1, x2, y2, f, st);

        if (tint == 206)
            return  new TriacElm(x1, y1, x2, y2, f, st);

        if (tint == 173)
            return  new TriodeElm(x1, y1, x2, y2, f, st);

        if (tint == 175)
            return  new TunnelDiodeElm(x1, y1, x2, y2, f, st);

        if (tint == 179)
            return  new CC2Elm(x1, y1, x2, y2, f, st);

        if (tint == 'I')
            return  new InverterElm(x1, y1, x2, y2, f, st);

        if (tint == 151)
            return  new NandGateElm(x1, y1, x2, y2, f, st);

        if (tint == 153)
            return  new NorGateElm(x1, y1, x2, y2, f, st);

        if (tint == 150)
            return  new AndGateElm(x1, y1, x2, y2, f, st);

        if (tint == 152)
            return  new OrGateElm(x1, y1, x2, y2, f, st);

        if (tint == 154)
            return  new XorGateElm(x1, y1, x2, y2, f, st);

        if (tint == 155)
            return  new DFlipFlopElm(x1, y1, x2, y2, f, st);

        if (tint == 156)
            return  new JKFlipFlopElm(x1, y1, x2, y2, f, st);

        if (tint == 157)
            return  new SevenSegElm(x1, y1, x2, y2, f, st);

        if (tint == 184)
            return  new MultiplexerElm(x1, y1, x2, y2, f, st);

        if (tint == 185)
            return  new DeMultiplexerElm(x1, y1, x2, y2, f, st);

        if (tint == 189)
            return  new SipoShiftElm(x1, y1, x2, y2, f, st);

        if (tint == 186)
            return  new PisoShiftElm(x1, y1, x2, y2, f, st);

        if (tint == 161)
            return  new PhaseCompElm(x1, y1, x2, y2, f, st);

        if (tint == 164)
            return  new CounterElm(x1, y1, x2, y2, f, st);

        if (tint == 163)
            return  new DecadeElm(x1, y1, x2, y2, f, st);

        if (tint == 165)
            return  new TimerElm(x1, y1, x2, y2, f, st);

        if (tint == 166)
            return  new DACElm(x1, y1, x2, y2, f, st);

        if (tint == 167)
            return  new ADCElm(x1, y1, x2, y2, f, st);

        if (tint == 168)
            return  new LatchElm(x1, y1, x2, y2, f, st);

        if (tint == 188)
            return  new SeqGenElm(x1, y1, x2, y2, f, st);

        if (tint == 158)
            return  new VCOElm(x1, y1, x2, y2, f, st);

        if (tint == 'b')
            return  new BoxElm(x1, y1, x2, y2, f, st);

        if (tint == 'x')
            return  new TextElm(x1, y1, x2, y2, f, st);

        if (tint == 193)
            return  new TFlipFlopElm(x1, y1, x2, y2, f, st);

        if (tint == 197)
            return  new SevenSegDecoderElm(x1, y1, x2, y2, f, st);

        if (tint == 196)
            return  new FullAdderElm(x1, y1, x2, y2, f, st);

        if (tint == 195)
            return  new HalfAdderElm(x1, y1, x2, y2, f, st);

        if (tint == 194)
            return  new MonostableElm(x1, y1, x2, y2, f, st);

        if (tint == 300)
            return  new ScriptElm(x1, y1, x2, y2, f, st);

        if (tint == 400)
            return  new GpioOutputElm(x1, y1, x2, y2, f, st);

        if (tint == 401)
            return  new GpioInputElm(x1, y1, x2, y2, f, st);

        // Sensors
        if (tint == 502)
            return  new KY002Elm(x1, y1, x2, y2, f, st);

        if (tint == 503)
            return  new KY003Elm(x1, y1, x2, y2, f, st);

        if (tint == 504)
            return  new KY004Elm(x1, y1, x2, y2, f, st);

        if (tint == 505)
            return  new KY005Elm(x1, y1, x2, y2, f, st);

        if (tint == 506)
            return  new KY006Elm(x1, y1, x2, y2, f, st);

        if (tint == 508)
            return  new KY008Elm(x1, y1, x2, y2, f, st);

        if (tint == 509)
            return  new KY009Elm(x1, y1, x2, y2, f, st);

        if (tint == 512)
            return  new KY012Elm(x1, y1, x2, y2, f, st);

        if (tint == 513)
            return  new KY013Elm(x1, y1, x2, y2, f, st);

        if (tint == 516)
            return  new KY016Elm(x1, y1, x2, y2, f, st);

        if (tint == 590)
            return  new SG90(x1, y1, x2, y2, f, st);
        return null;
    }
}
