package tiger.Semant;

public class FunEntry extends Entry{
	tiger.Translate.Level level;
	tiger.Temp.Label label;
	tiger.Types.RECORD formals;
	tiger.Types.Type result;
	public FunEntry(tiger.Translate.Level l,  tiger.Temp.Label lab,tiger.Types.RECORD f,tiger.Types.Type r){
		formals=f;result=r;level=l; label=lab;}
}
