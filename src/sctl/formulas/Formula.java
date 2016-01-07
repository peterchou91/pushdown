package sctl.formulas;


public abstract class Formula {
	public abstract Formula nnf();//·­Òë³Énegative normal form
	public abstract Formula neg();//nnfµÄ¸¨Öúº¯Êý
	public abstract Formula replaceVar(String v, String s);
}
