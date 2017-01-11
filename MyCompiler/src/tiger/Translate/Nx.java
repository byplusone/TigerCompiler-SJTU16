package tiger.Translate;
import tiger.Temp.Label;
import tiger.Tree.Stm;

public class Nx extends Exp {
  Stm stm;
  Nx(Stm stm) { this.stm = stm; }
  tiger.Tree.Expr unEx() { return null;}
  Stm unNx() {return stm;} 
  Stm unCx(Label t, Label f) { return null;}
}
