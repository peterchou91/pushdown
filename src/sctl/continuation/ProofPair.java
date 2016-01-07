package sctl.continuation;

public class ProofPair {
	private String parent;
	private String child;
	
	public ProofPair(String parent, String child) {
		this.parent = parent;
		this.child = child;
	}
	
	public String parent() {
		return parent;
	}
	
	public String child() {
		return child;
	}

//	@Override
//	public boolean equals(Object o) {
//		ProofPair pp = (ProofPair)o;
//		if(this.parent ==  pp.parent && this.child == pp.child) {
//			return true;
//		} else {
//			return false;
//		}
//		
//	}
	
}
