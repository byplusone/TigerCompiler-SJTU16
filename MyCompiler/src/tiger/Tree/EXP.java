package tiger.Tree;
import tiger.Temp.Temp;
import tiger.Temp.Label;
public class EXP extends Stm {
  public Expr exp;
  public EXP(Expr e) {exp=e;}
  public ExpList kids() {return new ExpList(exp,null);}
  public Stm build(ExpList kids) {
    return new EXP(kids.head);
  }
}

/*
package Tree;
import Temp.Temp;
import Temp.Label;
public class Exp extends Stm {
  public Exp exp;
  public Exp(Exp e) {exp=e;}
  public ExpList kids() {return new ExpList(exp,null);}
  public Stm build(ExpList kids) {
    return new Exp(kids.head);
  }
}
*/
