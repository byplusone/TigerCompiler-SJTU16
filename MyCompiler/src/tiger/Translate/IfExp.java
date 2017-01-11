package tiger.Translate;
import tiger.Temp.Label;
import tiger.Tree.*;


public class IfExp extends Exp {
  private Exp test; //测试条件
  private Exp e1; //then 子句
  private Exp e2; //else 子句
  IfExp(Exp test, Exp e1, Exp e2) {
    this.test = test;
    this.e1 = e1;
    this.e2 = e2;
  }
//if-else 这样翻译成有返回值的表达式:
//if (test) goto T else goto F
//LABEL T: r=e1
//goto JOIN
//LABEL F: r=e2
//LABEL JOIN: return r
tiger.Tree.Expr unEx() {
  tiger.Temp.Temp r = new tiger.Temp.Temp();
  tiger.Temp.Label join = new tiger.Temp.Label();
  tiger.Temp.Label t = new tiger.Temp.Label();
  tiger.Temp.Label f = new tiger.Temp.Label();
    return new ESEQ(new SEQ(test.unCx(t, f),
            new SEQ(new LABEL(t),
                    new SEQ(new MOVE(new TEMP(r), e1.unEx()),
                            new SEQ(new JUMP(join),
                                    new SEQ(new LABEL(f),
                                            new SEQ(new MOVE(new TEMP(r), e2.unEx()),
                                                    new LABEL(join))))))),
            new TEMP(r));
  }

  Stm unNx() {
    tiger.Temp.Label join = new tiger.Temp.Label();
    tiger.Temp.Label t = new tiger.Temp.Label();
    tiger.Temp.Label f = new tiger.Temp.Label();
    if (e2 == null)
      return new SEQ(test.unCx(t, join),new SEQ(new LABEL(t),
              new SEQ(e1.unNx(),
                      new LABEL(join))));
    else
      return new SEQ(test.unCx(t, f),
              new SEQ(new LABEL(t),
                      new SEQ(e1.unNx(),
                              new SEQ(new JUMP(join),
                                      new SEQ(new LABEL(f),
                                              new SEQ(e2.unNx(),
                                                      new LABEL(join)))))));
  }

  Stm unCx(Label t, Label f) {
    return new CJUMP(CJUMP.NE, unEx(), new CONST(0), t, f);
  }
}