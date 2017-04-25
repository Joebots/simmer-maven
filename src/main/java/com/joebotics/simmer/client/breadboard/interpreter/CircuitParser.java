package com.joebotics.simmer.client.breadboard.interpreter;

import com.joebotics.simmer.client.elcomp.CircuitNode;
import com.joebotics.simmer.client.elcomp.CircuitNodeLink;

import java.util.List;
import java.util.logging.Logger;


public class CircuitParser {

	private static final Logger lager = Logger.getLogger(CircuitParser.class.getName());

	private List<CircuitNode> circuitNodeList;
	private CircuitParserListener circuitParserListener;

	public CircuitParser(List<CircuitNode> circuitNodeList, CircuitParserListener circuitParserListener){
		this.circuitNodeList = circuitNodeList;
		this.circuitParserListener = circuitParserListener;
	}

	public void analyze(){

		lager.fine("analyze()");

		circuitParserListener.onStart();

		for( CircuitNode n : circuitNodeList ){

			lager.fine("analyze::CircuitNode:" + n);
			circuitParserListener.onCircuitNode(n);

			for( CircuitNodeLink cnl : n.links ){
				circuitParserListener.onCircuitNodeLink(n, cnl);
				lager.fine("analyze::CircuitNodeLink:" + cnl);
			}
		}

		circuitParserListener.onEnd();
	}

	public CircuitParserListener getCircuitParserListener() {
		return circuitParserListener;
	}
}
