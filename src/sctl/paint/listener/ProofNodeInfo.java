package sctl.paint.listener;

import sctl.formulas.Formula;

public class ProofNodeInfo implements NodeInfo {
	private boolean root;
	private String id;
	private Formula fml;
	
	public ProofNodeInfo(boolean root, String id, Formula fml) {
		this.root = root;
		this.id = id;
		this.fml = fml;
	}
	
	public boolean isRoot() {
		return root;
	}

	public String getId() {
		return id;
	}

	public Formula getFml() {
		return fml;
	}

}
