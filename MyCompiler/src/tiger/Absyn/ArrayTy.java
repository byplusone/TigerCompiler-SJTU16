package tiger.Absyn;
import tiger.Symbol.Symbol;
public class ArrayTy extends Ty {
   public Symbol typ;
   public ArrayTy(int p, Symbol t) {pos=p; typ=t;}
}
