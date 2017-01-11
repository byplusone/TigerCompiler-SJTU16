package tiger.Absyn;
import tiger.Symbol.Symbol;
public class RecordTy extends Ty {
   public FieldList fields;
   public RecordTy(int p, FieldList f) {pos=p; fields=f;}
}   
