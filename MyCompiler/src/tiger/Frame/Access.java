package tiger.Frame;
import tiger.Tree.Expr;

public abstract class Access {
    public abstract Expr exp(Expr framePtr); //以 fp 为起始地址返回变量
    public abstract Expr expFromStack(Expr stackPtr); //以 sp 为起始地址返回变量
}