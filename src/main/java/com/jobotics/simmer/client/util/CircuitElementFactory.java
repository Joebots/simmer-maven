package com.jobotics.simmer.client.util;

import com.jobotics.simmer.client.elcomp.ACRailElm;
import com.jobotics.simmer.client.elcomp.ACVoltageElm;
import com.jobotics.simmer.client.elcomp.AMElm;
import com.jobotics.simmer.client.elcomp.AbstractCircuitElement;
import com.jobotics.simmer.client.elcomp.AnalogSwitch2Elm;
import com.jobotics.simmer.client.elcomp.AnalogSwitchElm;
import com.jobotics.simmer.client.elcomp.AndGateElm;
import com.jobotics.simmer.client.elcomp.AntennaElm;
import com.jobotics.simmer.client.elcomp.CapacitorElm;
import com.jobotics.simmer.client.elcomp.ClockElm;
import com.jobotics.simmer.client.elcomp.CurrentElm;
import com.jobotics.simmer.client.elcomp.DCVoltageElm;
import com.jobotics.simmer.client.elcomp.DiacElm;
import com.jobotics.simmer.client.elcomp.DiodeElm;
import com.jobotics.simmer.client.elcomp.FMElm;
import com.jobotics.simmer.client.elcomp.GroundElm;
import com.jobotics.simmer.client.elcomp.InductorElm;
import com.jobotics.simmer.client.elcomp.InverterElm;
import com.jobotics.simmer.client.elcomp.InvertingSchmittElm;
import com.jobotics.simmer.client.elcomp.JfetElm;
import com.jobotics.simmer.client.elcomp.LEDElm;
import com.jobotics.simmer.client.elcomp.LampElm;
import com.jobotics.simmer.client.elcomp.LogicInputElm;
import com.jobotics.simmer.client.elcomp.LogicOutputElm;
import com.jobotics.simmer.client.elcomp.MemristorElm;
import com.jobotics.simmer.client.elcomp.MosfetElm;
import com.jobotics.simmer.client.elcomp.NJfetElm;
import com.jobotics.simmer.client.elcomp.NMosfetElm;
import com.jobotics.simmer.client.elcomp.NTransistorElm;
import com.jobotics.simmer.client.elcomp.NandGateElm;
import com.jobotics.simmer.client.elcomp.NorGateElm;
import com.jobotics.simmer.client.elcomp.OpAmpElm;
import com.jobotics.simmer.client.elcomp.OpAmpSwapElm;
import com.jobotics.simmer.client.elcomp.OrGateElm;
import com.jobotics.simmer.client.elcomp.OutputElm;
import com.jobotics.simmer.client.elcomp.PJfetElm;
import com.jobotics.simmer.client.elcomp.PMosfetElm;
import com.jobotics.simmer.client.elcomp.PTransistorElm;
import com.jobotics.simmer.client.elcomp.PotElm;
import com.jobotics.simmer.client.elcomp.ProbeElm;
import com.jobotics.simmer.client.elcomp.PushSwitchElm;
import com.jobotics.simmer.client.elcomp.RailElm;
import com.jobotics.simmer.client.elcomp.RelayElm;
import com.jobotics.simmer.client.elcomp.ResistorElm;
import com.jobotics.simmer.client.elcomp.SCRElm;
import com.jobotics.simmer.client.elcomp.SchmittElm;
import com.jobotics.simmer.client.elcomp.SparkGapElm;
import com.jobotics.simmer.client.elcomp.SquareRailElm;
import com.jobotics.simmer.client.elcomp.SweepElm;
import com.jobotics.simmer.client.elcomp.Switch2Elm;
import com.jobotics.simmer.client.elcomp.SwitchElm;
import com.jobotics.simmer.client.elcomp.TappedTransformerElm;
import com.jobotics.simmer.client.elcomp.TextElm;
import com.jobotics.simmer.client.elcomp.TransLineElm;
import com.jobotics.simmer.client.elcomp.TransformerElm;
import com.jobotics.simmer.client.elcomp.TransistorElm;
import com.jobotics.simmer.client.elcomp.TriStateElm;
import com.jobotics.simmer.client.elcomp.TriacElm;
import com.jobotics.simmer.client.elcomp.TriodeElm;
import com.jobotics.simmer.client.elcomp.TunnelDiodeElm;
import com.jobotics.simmer.client.elcomp.VCOElm;
import com.jobotics.simmer.client.elcomp.VarRailElm;
import com.jobotics.simmer.client.elcomp.VoltageElm;
import com.jobotics.simmer.client.elcomp.WireElm;
import com.jobotics.simmer.client.elcomp.XorGateElm;
import com.jobotics.simmer.client.elcomp.ZenerElm;
import com.jobotics.simmer.client.elcomp.chips.ADCElm;
import com.jobotics.simmer.client.elcomp.chips.CC2Elm;
import com.jobotics.simmer.client.elcomp.chips.CC2NegElm;
import com.jobotics.simmer.client.elcomp.chips.CounterElm;
import com.jobotics.simmer.client.elcomp.chips.DACElm;
import com.jobotics.simmer.client.elcomp.chips.DFlipFlopElm;
import com.jobotics.simmer.client.elcomp.chips.DeMultiplexerElm;
import com.jobotics.simmer.client.elcomp.chips.DecadeElm;
import com.jobotics.simmer.client.elcomp.chips.FullAdderElm;
import com.jobotics.simmer.client.elcomp.chips.HalfAdderElm;
import com.jobotics.simmer.client.elcomp.chips.JKFlipFlopElm;
import com.jobotics.simmer.client.elcomp.chips.LatchElm;
import com.jobotics.simmer.client.elcomp.chips.MonostableElm;
import com.jobotics.simmer.client.elcomp.chips.MultiplexerElm;
import com.jobotics.simmer.client.elcomp.chips.PhaseCompElm;
import com.jobotics.simmer.client.elcomp.chips.PisoShiftElm;
import com.jobotics.simmer.client.elcomp.chips.SeqGenElm;
import com.jobotics.simmer.client.elcomp.chips.SevenSegDecoderElm;
import com.jobotics.simmer.client.elcomp.chips.SevenSegElm;
import com.jobotics.simmer.client.elcomp.chips.SipoShiftElm;
import com.jobotics.simmer.client.elcomp.chips.TFlipFlopElm;
import com.jobotics.simmer.client.elcomp.chips.TimerElm;
import com.jobotics.simmer.client.gui.impl.BoxElm;

public class CircuitElementFactory {
	

	public static AbstractCircuitElement constructElement(String n, int x1, int y1) {
		
		if (n == "GroundElm")
			return (AbstractCircuitElement) new GroundElm(x1, y1);

		if (n == "ResistorElm")
			return (AbstractCircuitElement) new ResistorElm(x1, y1);
		
		if (n == "RailElm")
			return (AbstractCircuitElement) new RailElm(x1, y1);
		
		if (n == "SwitchElm")
			return (AbstractCircuitElement) new SwitchElm(x1, y1);
		
		if (n == "Switch2Elm")
			return (AbstractCircuitElement) new Switch2Elm(x1, y1);
		
		if (n == "NTransistorElm")
			return (AbstractCircuitElement) new NTransistorElm(x1, y1);
		
		if (n == "PTransistorElm")
			return (AbstractCircuitElement) new PTransistorElm(x1, y1);
		
		if (n == "WireElm")
			return (AbstractCircuitElement) new WireElm(x1, y1);
		
		if (n == "CapacitorElm")
			return (AbstractCircuitElement) new CapacitorElm(x1, y1);
		
		if (n == "InductorElm")
			return (AbstractCircuitElement) new InductorElm(x1, y1);
		
		if (n == "DCVoltageElm")
			return (AbstractCircuitElement) new DCVoltageElm(x1, y1);
		
		if (n == "VarRailElm")
			return (AbstractCircuitElement) new VarRailElm(x1, y1);
		
		if (n == "PotElm")
			return (AbstractCircuitElement) new PotElm(x1, y1);
		
		if (n == "OutputElm")
			return (AbstractCircuitElement) new OutputElm(x1, y1);
		
		if (n == "CurrentElm")
			return (AbstractCircuitElement) new CurrentElm(x1, y1);
		
		if (n == "ProbeElm")
			return (AbstractCircuitElement) new ProbeElm(x1, y1);
		
		if (n == "DiodeElm")
			return (AbstractCircuitElement) new DiodeElm(x1, y1);
		
		if (n == "ZenerElm")
			return (AbstractCircuitElement) new ZenerElm(x1, y1);
		
		if (n == "ACVoltageElm")
			return (AbstractCircuitElement) new ACVoltageElm(x1, y1);
		
		if (n == "ACRailElm")
			return (AbstractCircuitElement) new ACRailElm(x1, y1);
		
		if (n == "SquareRailElm")
			return (AbstractCircuitElement) new SquareRailElm(x1, y1);
		
		if (n == "SweepElm")
			return (AbstractCircuitElement) new SweepElm(x1, y1);
		
		if (n == "LEDElm")
			return (AbstractCircuitElement) new LEDElm(x1, y1);
		
		if (n == "AntennaElm")
			return (AbstractCircuitElement) new AntennaElm(x1, y1);
		
		if (n == "LogicInputElm")
			return (AbstractCircuitElement) new LogicInputElm(x1, y1);
		
		if (n == "LogicOutputElm")
			return (AbstractCircuitElement) new LogicOutputElm(x1, y1);
		
		if (n == "TransformerElm")
			return (AbstractCircuitElement) new TransformerElm(x1, y1);
		
		if (n == "TappedTransformerElm")
			return (AbstractCircuitElement) new TappedTransformerElm(x1, y1);
		
		if (n == "TransLineElm")
			return (AbstractCircuitElement) new TransLineElm(x1, y1);
		
		if (n == "RelayElm")
			return (AbstractCircuitElement) new RelayElm(x1, y1);
		
		if (n == "MemristorElm")
			return (AbstractCircuitElement) new MemristorElm(x1, y1);
		
		if (n == "SparkGapElm")
			return (AbstractCircuitElement) new SparkGapElm(x1, y1);
		
		if (n == "ClockElm")
			return (AbstractCircuitElement) new ClockElm(x1, y1);
		
		if (n == "AMElm")
			return (AbstractCircuitElement) new AMElm(x1, y1);
		
		if (n == "FMElm")
			return (AbstractCircuitElement) new FMElm(x1, y1);
		
		if (n == "LampElm")
			return (AbstractCircuitElement) new LampElm(x1, y1);
		
		if (n == "PushSwitchElm")
			return (AbstractCircuitElement) new PushSwitchElm(x1, y1);
		
		if (n == "OpAmpElm")
			return (AbstractCircuitElement) new OpAmpElm(x1, y1);
		
		if (n == "OpAmpSwapElm")
			return (AbstractCircuitElement) new OpAmpSwapElm(x1, y1);
		
		if (n == "NMosfetElm")
			return (AbstractCircuitElement) new NMosfetElm(x1, y1);
		
		if (n == "PMosfetElm")
			return (AbstractCircuitElement) new PMosfetElm(x1, y1);
		
		if (n == "NJfetElm")
			return (AbstractCircuitElement) new NJfetElm(x1, y1);
		
		if (n == "PJfetElm")
			return (AbstractCircuitElement) new PJfetElm(x1, y1);
		
		if (n == "AnalogSwitchElm")
			return (AbstractCircuitElement) new AnalogSwitchElm(x1, y1);
		
		if (n == "AnalogSwitch2Elm")
			return (AbstractCircuitElement) new AnalogSwitch2Elm(x1, y1);
		
		if (n == "SchmittElm")
			return (AbstractCircuitElement) new SchmittElm(x1, y1);
		
		if (n == "InvertingSchmittElm")
			return (AbstractCircuitElement) new InvertingSchmittElm(x1, y1);
		
		if (n == "TriStateElm")
			return (AbstractCircuitElement) new TriStateElm(x1, y1);
		
		if (n == "SCRElm")
			return (AbstractCircuitElement) new SCRElm(x1, y1);
		
		if (n == "DiacElm")
			return (AbstractCircuitElement) new DiacElm(x1, y1);
		
		if (n == "TriacElm")
			return (AbstractCircuitElement) new TriacElm(x1, y1);
		
		if (n == "TriodeElm")
			return (AbstractCircuitElement) new TriodeElm(x1, y1);
		
		if (n == "TunnelDiodeElm")
			return (AbstractCircuitElement) new TunnelDiodeElm(x1, y1);
		
		if (n == "CC2Elm")
			return (AbstractCircuitElement) new CC2Elm(x1, y1);
		
		if (n == "CC2NegElm")
			return (AbstractCircuitElement) new CC2NegElm(x1, y1);
		
		if (n == "InverterElm")
			return (AbstractCircuitElement) new InverterElm(x1, y1);
		
		if (n == "NandGateElm")
			return (AbstractCircuitElement) new NandGateElm(x1, y1);
		
		if (n == "NorGateElm")
			return (AbstractCircuitElement) new NorGateElm(x1, y1);
		
		if (n == "AndGateElm")
			return (AbstractCircuitElement) new AndGateElm(x1, y1);
		
		if (n == "OrGateElm")
			return (AbstractCircuitElement) new OrGateElm(x1, y1);
		
		if (n == "XorGateElm")
			return (AbstractCircuitElement) new XorGateElm(x1, y1);
		
		if (n == "DFlipFlopElm")
			return (AbstractCircuitElement) new DFlipFlopElm(x1, y1);
		
		if (n == "JKFlipFlopElm")
			return (AbstractCircuitElement) new JKFlipFlopElm(x1, y1);
		
		if (n == "SevenSegElm")
			return (AbstractCircuitElement) new SevenSegElm(x1, y1);
		
		if (n == "MultiplexerElm")
			return (AbstractCircuitElement) new MultiplexerElm(x1, y1);
		
		if (n == "DeMultiplexerElm")
			return (AbstractCircuitElement) new DeMultiplexerElm(x1, y1);
		
		if (n == "SipoShiftElm")
			return (AbstractCircuitElement) new SipoShiftElm(x1, y1);
		
		if (n == "PisoShiftElm")
			return (AbstractCircuitElement) new PisoShiftElm(x1, y1);
		
		if (n == "PhaseCompElm")
			return (AbstractCircuitElement) new PhaseCompElm(x1, y1);
		
		if (n == "CounterElm")
			return (AbstractCircuitElement) new CounterElm(x1, y1);
		
		if (n == "DecadeElm")
			return (AbstractCircuitElement) new DecadeElm(x1, y1);
		
		if (n == "TimerElm")
			return (AbstractCircuitElement) new TimerElm(x1, y1);
		
		if (n == "DACElm")
			return (AbstractCircuitElement) new DACElm(x1, y1);
		
		if (n == "ADCElm")
			return (AbstractCircuitElement) new ADCElm(x1, y1);
		
		if (n == "LatchElm")
			return (AbstractCircuitElement) new LatchElm(x1, y1);
		
		if (n == "SeqGenElm")
			return (AbstractCircuitElement) new SeqGenElm(x1, y1);
		
		if (n == "VCOElm")
			return (AbstractCircuitElement) new VCOElm(x1, y1);
		
		if (n == "BoxElm")
			return (AbstractCircuitElement) new BoxElm(x1, y1);
		
		if (n == "TextElm")
			return (AbstractCircuitElement) new TextElm(x1, y1);
		
		if (n == "TFlipFlopElm")
			return (AbstractCircuitElement) new TFlipFlopElm(x1, y1);
		
		if (n == "SevenSegDecoderElm")
			return (AbstractCircuitElement) new SevenSegDecoderElm(x1, y1);
		
		if (n == "FullAdderElm")
			return (AbstractCircuitElement) new FullAdderElm(x1, y1);
		
		if (n == "HalfAdderElm")
			return (AbstractCircuitElement) new HalfAdderElm(x1, y1);
		
		if (n == "MonostableElm")
			return (AbstractCircuitElement) new MonostableElm(x1, y1);

		return null;
	}

	public static AbstractCircuitElement createCircuitElement(int tint, int x1, int y1, int x2, int y2, int f, StringTokenizer st) {
		if (tint == 'g')
			return (AbstractCircuitElement) new GroundElm(x1, y1, x2, y2, f, st);
		if (tint == 'r')
			return (AbstractCircuitElement) new ResistorElm(x1, y1, x2, y2, f, st);
		if (tint == 'R')
			return (AbstractCircuitElement) new RailElm(x1, y1, x2, y2, f, st);
		if (tint == 's')
			return (AbstractCircuitElement) new SwitchElm(x1, y1, x2, y2, f, st);
		if (tint == 'S')
			return (AbstractCircuitElement) new Switch2Elm(x1, y1, x2, y2, f, st);
		if (tint == 't')
			return (AbstractCircuitElement) new TransistorElm(x1, y1, x2, y2, f, st);
		if (tint == 'w')
			return (AbstractCircuitElement) new WireElm(x1, y1, x2, y2, f, st);
		if (tint == 'c')
			return (AbstractCircuitElement) new CapacitorElm(x1, y1, x2, y2, f, st);
		if (tint == 'l')
			return (AbstractCircuitElement) new InductorElm(x1, y1, x2, y2, f, st);
		if (tint == 'v')
			return (AbstractCircuitElement) new VoltageElm(x1, y1, x2, y2, f, st);
		if (tint == 172)
			return (AbstractCircuitElement) new VarRailElm(x1, y1, x2, y2, f, st);
		if (tint == 174)
			return (AbstractCircuitElement) new PotElm(x1, y1, x2, y2, f, st);
		if (tint == 'O')
			return (AbstractCircuitElement) new OutputElm(x1, y1, x2, y2, f, st);
		if (tint == 'i')
			return (AbstractCircuitElement) new CurrentElm(x1, y1, x2, y2, f, st);
		if (tint == 'p')
			return (AbstractCircuitElement) new ProbeElm(x1, y1, x2, y2, f, st);
		if (tint == 'd')
			return (AbstractCircuitElement) new DiodeElm(x1, y1, x2, y2, f, st);
		if (tint == 'z')
			return (AbstractCircuitElement) new ZenerElm(x1, y1, x2, y2, f, st);
		if (tint == 170)
			return (AbstractCircuitElement) new SweepElm(x1, y1, x2, y2, f, st);
		if (tint == 162)
			return (AbstractCircuitElement) new LEDElm(x1, y1, x2, y2, f, st);
		if (tint == 'A')
			return (AbstractCircuitElement) new AntennaElm(x1, y1, x2, y2, f, st);
		if (tint == 'L')
			return (AbstractCircuitElement) new LogicInputElm(x1, y1, x2, y2, f, st);
		if (tint == 'M')
			return (AbstractCircuitElement) new LogicOutputElm(x1, y1, x2, y2, f, st);
		if (tint == 'T')
			return (AbstractCircuitElement) new TransformerElm(x1, y1, x2, y2, f, st);
		if (tint == 169)
			return (AbstractCircuitElement) new TappedTransformerElm(x1, y1, x2, y2, f, st);
		if (tint == 171)
			return (AbstractCircuitElement) new TransLineElm(x1, y1, x2, y2, f, st);
		if (tint == 178)
			return (AbstractCircuitElement) new RelayElm(x1, y1, x2, y2, f, st);
		if (tint == 'm')
			return (AbstractCircuitElement) new MemristorElm(x1, y1, x2, y2, f, st);
		if (tint == 187)
			return (AbstractCircuitElement) new SparkGapElm(x1, y1, x2, y2, f, st);
		if (tint == 200)
			return (AbstractCircuitElement) new AMElm(x1, y1, x2, y2, f, st);
		if (tint == 201)
			return (AbstractCircuitElement) new FMElm(x1, y1, x2, y2, f, st);
		if (tint == 181)
			return (AbstractCircuitElement) new LampElm(x1, y1, x2, y2, f, st);
		if (tint == 'a')
			return (AbstractCircuitElement) new OpAmpElm(x1, y1, x2, y2, f, st);
		if (tint == 'f')
			return (AbstractCircuitElement) new MosfetElm(x1, y1, x2, y2, f, st);
		if (tint == 'j')
			return (AbstractCircuitElement) new JfetElm(x1, y1, x2, y2, f, st);
		if (tint == 159)
			return (AbstractCircuitElement) new AnalogSwitchElm(x1, y1, x2, y2, f, st);
		if (tint == 160)
			return (AbstractCircuitElement) new AnalogSwitch2Elm(x1, y1, x2, y2, f, st);
		if (tint == 180)
			return (AbstractCircuitElement) new TriStateElm(x1, y1, x2, y2, f, st);
		if (tint == 182)
			return (AbstractCircuitElement) new SchmittElm(x1, y1, x2, y2, f, st);
		if (tint == 183)
			return (AbstractCircuitElement) new InvertingSchmittElm(x1, y1, x2, y2, f, st);
		if (tint == 177)
			return (AbstractCircuitElement) new SCRElm(x1, y1, x2, y2, f, st);
		if (tint == 203)
			return (AbstractCircuitElement) new DiacElm(x1, y1, x2, y2, f, st);
		if (tint == 206)
			return (AbstractCircuitElement) new TriacElm(x1, y1, x2, y2, f, st);
		if (tint == 173)
			return (AbstractCircuitElement) new TriodeElm(x1, y1, x2, y2, f, st);
		if (tint == 175)
			return (AbstractCircuitElement) new TunnelDiodeElm(x1, y1, x2, y2, f, st);
		if (tint == 179)
			return (AbstractCircuitElement) new CC2Elm(x1, y1, x2, y2, f, st);
		if (tint == 'I')
			return (AbstractCircuitElement) new InverterElm(x1, y1, x2, y2, f, st);
		if (tint == 151)
			return (AbstractCircuitElement) new NandGateElm(x1, y1, x2, y2, f, st);
		if (tint == 153)
			return (AbstractCircuitElement) new NorGateElm(x1, y1, x2, y2, f, st);
		if (tint == 150)
			return (AbstractCircuitElement) new AndGateElm(x1, y1, x2, y2, f, st);
		if (tint == 152)
			return (AbstractCircuitElement) new OrGateElm(x1, y1, x2, y2, f, st);
		if (tint == 154)
			return (AbstractCircuitElement) new XorGateElm(x1, y1, x2, y2, f, st);
		if (tint == 155)
			return (AbstractCircuitElement) new DFlipFlopElm(x1, y1, x2, y2, f, st);
		if (tint == 156)
			return (AbstractCircuitElement) new JKFlipFlopElm(x1, y1, x2, y2, f, st);
		if (tint == 157)
			return (AbstractCircuitElement) new SevenSegElm(x1, y1, x2, y2, f, st);
		if (tint == 184)
			return (AbstractCircuitElement) new MultiplexerElm(x1, y1, x2, y2, f, st);
		if (tint == 185)
			return (AbstractCircuitElement) new DeMultiplexerElm(x1, y1, x2, y2, f, st);
		if (tint == 189)
			return (AbstractCircuitElement) new SipoShiftElm(x1, y1, x2, y2, f, st);
		if (tint == 186)
			return (AbstractCircuitElement) new PisoShiftElm(x1, y1, x2, y2, f, st);
		if (tint == 161)
			return (AbstractCircuitElement) new PhaseCompElm(x1, y1, x2, y2, f, st);
		if (tint == 164)
			return (AbstractCircuitElement) new CounterElm(x1, y1, x2, y2, f, st);
		if (tint == 163)
			return (AbstractCircuitElement) new DecadeElm(x1, y1, x2, y2, f, st);
		if (tint == 165)
			return (AbstractCircuitElement) new TimerElm(x1, y1, x2, y2, f, st);
		if (tint == 166)
			return (AbstractCircuitElement) new DACElm(x1, y1, x2, y2, f, st);
		if (tint == 167)
			return (AbstractCircuitElement) new ADCElm(x1, y1, x2, y2, f, st);
		if (tint == 168)
			return (AbstractCircuitElement) new LatchElm(x1, y1, x2, y2, f, st);
		if (tint == 188)
			return (AbstractCircuitElement) new SeqGenElm(x1, y1, x2, y2, f, st);
		if (tint == 158)
			return (AbstractCircuitElement) new VCOElm(x1, y1, x2, y2, f, st);
		if (tint == 'b')
			return (AbstractCircuitElement) new BoxElm(x1, y1, x2, y2, f, st);
		if (tint == 'x')
			return (AbstractCircuitElement) new TextElm(x1, y1, x2, y2, f, st);
		if (tint == 193)
			return (AbstractCircuitElement) new TFlipFlopElm(x1, y1, x2, y2, f, st);
		if (tint == 197)
			return (AbstractCircuitElement) new SevenSegDecoderElm(x1, y1, x2, y2, f, st);
		if (tint == 196)
			return (AbstractCircuitElement) new FullAdderElm(x1, y1, x2, y2, f, st);
		if (tint == 195)
			return (AbstractCircuitElement) new HalfAdderElm(x1, y1, x2, y2, f, st);
		if (tint == 194)
			return (AbstractCircuitElement) new MonostableElm(x1, y1, x2, y2, f, st);

		return null;
	}
}
