package tiger.Semant;

import tiger.Translate.Exp;
import tiger.Types.Type;
public class ExpTy {
Exp exp;
Type ty;
ExpTy(Exp exp, Type ty) {
this.exp = exp;
this.ty = ty;
}
}