package sctl;

import java.io.FileReader;
import java.io.IOException;

import lexer_parser.Parser;
import sctl.paint.stateGraphViewer.StateGraphVisualizer;
import sctl.paint.treeViewer.TreeVisualizer;

public class TestModel {
	public static void main(String args[]) throws IOException {
		Parser yyparser = new Parser(new FileReader(
				"./src/lexer_parser/river.sctl"));
		yyparser.parse();
		System.out.println("parser done!!!");
//		yyparser.m.print();
		yyparser.m.doProve();
		yyparser.m.setProofVisualizer(new TreeVisualizer("Proof Visualizer"));
		yyparser.m.showProofTree();
		yyparser.m.setStateGraphViewer(new StateGraphVisualizer("State Graph"));
		yyparser.m.showStateGraph();
	}
}
