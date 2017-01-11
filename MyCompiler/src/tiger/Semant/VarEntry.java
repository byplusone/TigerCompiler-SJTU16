package tiger.Semant;
import tiger.Types.*;
public class VarEntry extends Entry{
	Type Ty;
	tiger.Translate.Access acc;
	boolean isFor;
	
	public VarEntry(Type ty, tiger.Translate.Access acc){ Ty = ty; this.acc = acc; this.isFor=false; }
	public VarEntry(Type ty, tiger.Translate.Access acc, boolean isf){ Ty = ty; this.acc = acc; this.isFor=isf; }
}
