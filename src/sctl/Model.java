package sctl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import sctl.continuation.Continuation;
import sctl.continuation.ProofPair;
import sctl.formulas.AtomicFml;
import sctl.formulas.BottomFml;
import sctl.formulas.EuFml;
import sctl.formulas.Formula;
import sctl.formulas.TopFml;
import sctl.paint.listener.ProofEdgeInfo;
import sctl.paint.listener.ProofNodeInfo;
import sctl.paint.stateGraphViewer.StateGraphVisualizer;
import sctl.paint.treeViewer.TreeVisualizer;

public class Model {
	public HashSet<String> states = new HashSet<String>();
	public HashSet<AtomicFml> atoms = new HashSet<AtomicFml>();
	public HashMap<String, HashSet<String>> transitions = new HashMap<String, HashSet<String>>();
	public HashSet<Formula> specs = new HashSet<Formula>();

	private FileWriter fw;

	public HashMap<String, Formula> fmls = new HashMap<String, Formula>();
	public ArrayList<ProofPair> proof_tree = new ArrayList<ProofPair>();
	public ArrayList<ProofPair> counter_example = new ArrayList<ProofPair>();

	private TreeVisualizer proofViewer;
	private StateGraphVisualizer stateViewer;

	public Model() {
		try {
			fw = new FileWriter("./result.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ArrayList<String> al;
	}

	public void setProofVisualizer(TreeVisualizer tv) {
		proofViewer = tv;
	}
	public void setStateGraphViewer(StateGraphVisualizer sgv) {
		stateViewer = sgv;
	}

	private void addFml(int id, Formula f) {
		fmls.put(String.valueOf(id), f);
	}

	public boolean isAtomicTrue(AtomicFml af) {
		for (AtomicFml f : this.atoms) {
			if (f.equals(af)) {
				return true;
			}
		}
		return false;
	}

	public boolean prove(Continuation cont) {
		while (!cont.isBasic()) {
			// System.out.println(cont.fml);
			// System.out.println(cont);
			HashSet<ProofPair> proof = cont.getTruePairs();
			if (proof != null) {
				for (ProofPair pp : proof) {
					this.proof_tree.add(pp);
				}
			}
			HashSet<ProofPair> counter = cont.getFalsePairs();
			if (counter != null) {
				for (ProofPair pp : counter) {
					this.counter_example.add(pp);
				}
			}
			Formula fml = cont.fml;
			switch (fml.getClass().getName()) { 
			case "sctl.formulas.AtomicFml":
				// System.out.println("proving atomic");
				AtomicFml af = (AtomicFml) fml;
				this.addFml(cont.index, af);
				if (this.isAtomicTrue(af)) {
					cont = cont.contl;
				} else {
					cont = cont.contr;
				}
				break;
			case "sctl.formulas.EuFml":
				// System.out.println("proving eu");
				EuFml euf = (EuFml) fml;
				this.addFml(cont.index, euf);
				HashSet<String> gamma = cont.states;
				if (gamma.contains(euf.state)) {
					cont = cont.contr;
				} else {
					Formula phi1 = euf.fml1.replaceVar(euf.x, euf.state);
					Formula phi2 = euf.fml2.replaceVar(euf.y, euf.state);
					Continuation contt1 = cont.contl.newContinuation();
					Continuation contt2 = cont.contl.newContinuation();
					Continuation contf1 = cont.contr.newContinuation();
					Continuation contf2 = cont.contr.newContinuation();

					contt1.addTruePair(new ProofPair(String.valueOf(cont.index), String.valueOf(cont.index + 2)));
					contt2.addTruePair(new ProofPair(String.valueOf(cont.index), String.valueOf(cont.index + 1)));
					contf1.addFalsePair(new ProofPair(String.valueOf(cont.index), String.valueOf(cont.index + 2)));
					contf1.addFalsePair(new ProofPair(String.valueOf(cont.index), String.valueOf(cont.index + 1)));

					HashSet<String> nextStates = this.transitions.get(euf.state);
					ArrayList<Formula> fmls = new ArrayList<Formula>();
					for (String s : nextStates) {
						fmls.add(new EuFml(euf.x, euf.y, euf.fml1, euf.fml2, s));
					}
					// nextStates.add(euf.state);
					HashSet<String> states = cont.states;
					states.add(euf.state);
					cont = new Continuation(false, new HashSet<String>(), phi2, contt1,
							new Continuation(false, new HashSet<String>(), phi1,
									makeOrCont(cont.index, fmls, states, contt2, contf2), contf1, cont.index + 1,
									new HashSet<ProofPair>(), new HashSet<ProofPair>()),
							cont.index + 2, new HashSet<ProofPair>(), new HashSet<ProofPair>());
				}
				break;
			}

		}
		// cont.addTruePair(cont.contl.truePair);
		// cont.addFalsePair(cont.contr.falsePair);
		HashSet<ProofPair> proof = cont.getTruePairs();
		if (proof != null) {
			for (ProofPair pp : proof) {
				this.proof_tree.add(pp);
			}
		}
		HashSet<ProofPair> counter = cont.getFalsePairs();
		if (counter != null) {
			for (ProofPair pp : counter) {
				this.counter_example.add(pp);
			}
		}
		if ("sctl.formulas.TopFml".equals(cont.fml.getClass().getName())) {
			return true;
		} else {
			return false;
		}
	}

	// public Continuation makeAndCont(ArrayList<Formula> fmls,
	// HashSet<String> gamma, Continuation contl, Continuation contr) {
	// if (fmls.size() == 1) {
	// return new Continuation(false, (HashSet<String>) gamma.clone(),
	// fmls.get(0), contl, contr);
	// } else {
	// Formula f = fmls.remove(0);
	// return new Continuation(false, (HashSet<String>) gamma.clone(), f,
	// makeAndCont(fmls, gamma, contl, contr), contr);
	// }
	// }

	private Continuation makeOrCont(int index, ArrayList<Formula> fmls, HashSet<String> nextStates, Continuation contt2,
			Continuation contf2) {
		HashSet<String> states = (HashSet<String>) nextStates.clone();
		int i = index + 3;
		Continuation t2 = contt2;
		Continuation f2 = contf2;
		int j = 0;
		for (Formula fml : fmls) {
			f2.falsePairs.add(new ProofPair(String.valueOf(index), String.valueOf(i + j)));
			j++;
		}
		Continuation cont = f2;
		j = 0;
		for (Formula fml : fmls) {
			HashSet<ProofPair> tps = (HashSet<ProofPair>) t2.truePairs.clone();
			tps.add(new ProofPair(String.valueOf(index), String.valueOf(i + j)));
			cont = new Continuation(false, states, fml, t2.newTruePairs(tps), cont, i + j, new HashSet<ProofPair>(),
					new HashSet<ProofPair>());
			j++;
		}
		return cont;
	}

	public void doProve() {
		System.out.println("*******PROOF START******");

		for (Formula fml : specs) {
			System.out.println("proving " + fml);
			try {
				fw.append("proving " + fml + "\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
			Formula fml2 = fml;
			long startTime = System.currentTimeMillis(); // start time
			boolean result = prove(new Continuation(false, new HashSet<String>(), fml2,
					new Continuation(true, null, new TopFml(), null, null, -1, new HashSet<ProofPair>(),
							new HashSet<ProofPair>()),
					new Continuation(true, null, new BottomFml(), null, null, -1, new HashSet<ProofPair>(),
							new HashSet<ProofPair>()),
					0, new HashSet<ProofPair>(), new HashSet<ProofPair>()));
			long endTime = System.currentTimeMillis();
			System.out.println("*The result of the proof of is: " + result + " in " + (endTime - startTime) + "ms.");
			updateProofTree(result);
			// System.out.println("*The time used is "
			// + ((endTime - startTime) / 1000.0) + " s");
			try {
				fw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("*******PROOF DONE*******");
	}

	public void updateProofTree(boolean result) {
		ArrayList<ProofPair> tmp_pps = new ArrayList<ProofPair>();
		LinkedList<String> quene = new LinkedList<String>();
		quene.addFirst(String.valueOf(0));
		String str = quene.getFirst();
		while (str != null) {
			for (ProofPair pp : proof_tree) {
				if (pp.parent().equals(str)) {
					// System.out.println(str+"->"+pp.child());
					try {
						fw.write(str + "->" + pp.child() + "\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					tmp_pps.add(new ProofPair(str, pp.child()));
					// System.out.println(str+": "+this.fmls.get(str));
					try {
						fw.write(str + ": " + this.fmls.get(str) + "\r\n");
						fw.write(pp.child() + ": " + this.fmls.get(pp.child()) + "\r\n");
						fw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// System.out.println(pp.child()+":
					// "+this.fmls.get(pp.child()));
					quene.addLast(pp.child());
				}
			}
			str = quene.removeFirst();
			if (quene.isEmpty()) {
				break;
			} else {
				str = quene.getFirst();
			}
		}
		this.proof_tree = tmp_pps;
	}

	public void showProofTree() {
		if (this.proofViewer == null) {
			System.out.println("Error, no visualizer initialized.");
		} else {
			ProofPair pp0 = proof_tree.get(0);
			proofViewer.addNode(new ProofNodeInfo(true, pp0.parent(), fmls.get(pp0.parent())));
			proofViewer.addNode(new ProofNodeInfo(false, pp0.child(), fmls.get(pp0.child())));
			proofViewer.addEdge(new ProofEdgeInfo(pp0.parent(), pp0.child()));
			for (int i = 1; i < proof_tree.size(); i++) {
				ProofPair pp = proof_tree.get(i);
				proofViewer.addNode(new ProofNodeInfo(false, pp.parent(), fmls.get(pp.parent())));
				proofViewer.addNode(new ProofNodeInfo(false, pp.child(), fmls.get(pp.child())));
				proofViewer.addEdge(new ProofEdgeInfo(pp.parent(), pp.child()));
			}
		}
		proofViewer.updateLayout();
		proofViewer.show();
	}
	
	public void showStateGraph() {
		for(String sf : transitions.keySet()) {
			this.stateViewer.addState(sf, sf, false);
			for(String st : transitions.get(sf)) {
				this.stateViewer.addState(st, st, false);
				this.stateViewer.addEdge(sf, st);
			}
		}
		this.stateViewer.updateLayout();
		this.stateViewer.show();
	}
}
