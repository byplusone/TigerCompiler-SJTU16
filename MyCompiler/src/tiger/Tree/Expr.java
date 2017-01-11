package tiger.Tree;

abstract public class Expr {
    abstract public ExpList kids();

    abstract public Expr build(ExpList kids);

    public static boolean isBINOP(Expr exp){
        if(exp instanceof BINOP)
            return true;
        else
            return false;
    }
    public static boolean isCONST(Expr exp){
        if(exp instanceof CONST)
            return true;
        else
            return false;
    }
    public static boolean isTEMP(Expr exp){
        if(exp instanceof TEMP)
            return true;
        else
            return false;
    }
}