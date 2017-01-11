package tiger.Types;

public class RECORD extends Type {
   public tiger.Symbol.Symbol fieldName;
   public Type fieldType;
   public RECORD tail;
   public RECORD(tiger.Symbol.Symbol n, Type t, RECORD x) {
      gen(n, t, x);
   }
   public RECORD(){
      gen(null, null, null);
   }
   public void gen(tiger.Symbol.Symbol n, Type t, RECORD x) {
      fieldName=n; fieldType=t; tail=x;
   }
   static public boolean isNull(RECORD r)
   {
      if (r == null || (r.fieldName == null && r.fieldType == null && r.tail == null))
         return true;
      return false;
   }
   public boolean coerceTo(Type t) {
	return this==t.actual();
   }
}
   

