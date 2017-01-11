package tiger.Translate;
import tiger.Temp.Label;


class Ex extends Exp {
  tiger.Tree.Expr exp;
  Ex(tiger.Tree.Expr e) {
    exp = e;
  }

  tiger.Tree.Expr unEx() {
    return exp;
  }

  tiger.Tree.Stm unNx() {
    return new tiger.Tree.EXP(exp);
  }

  tiger.Tree.Stm unCx(Label t, Label f) {
    if (exp instanceof tiger.Tree.CONST) {
      tiger.Tree.CONST c = (tiger.Tree.CONST)exp;
      if (c.value == 0)
        return new tiger.Tree.JUMP(f);
      else
        return new tiger.Tree.JUMP(t);
    }
    return new tiger.Tree.CJUMP(tiger.Tree.CJUMP.NE, exp, new tiger.Tree.CONST(0), t, f);
  }

}
