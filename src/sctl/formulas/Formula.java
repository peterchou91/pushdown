package sctl.formulas;


public abstract class Formula {
	public abstract Formula nnf();//�����negative normal form
	public abstract Formula neg();//nnf�ĸ�������
	public abstract Formula replaceVar(String v, String s);
}
