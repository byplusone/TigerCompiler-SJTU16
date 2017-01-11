package tiger.Mips;

import tiger.Assem.Instr;
import tiger.Assem.InstrList;
import tiger.Assem.OPER;
import tiger.Temp.LabelList;
import tiger.Temp.Temp;
import tiger.Temp.TempList;
import tiger.Temp.TempMap;
import tiger.Tree.BINOP;
import tiger.Tree.CALL;
import tiger.Tree.CJUMP;
import tiger.Tree.CONST;
import tiger.Tree.Expr;
import tiger.Tree.Expr;
import tiger.Tree.ExpList;
import tiger.Tree.JUMP;
import tiger.Tree.LABEL;
import tiger.Tree.MEM;
import tiger.Tree.MOVE;
import tiger.Tree.NAME;
import tiger.Tree.Stm;
import tiger.Tree.StmList;
import tiger.Tree.TEMP;

public class Codegen {

    MipsFrame frame;

    public Codegen(MipsFrame f) {
        frame = f;
    }

    static Instr OPER(String a, TempList d, TempList s) {
        return new OPER(a, d, s);
    }


    private InstrList ilist = null, last = null;

    private void emit(Instr inst) {
        if (last != null) {
            last = last.tail = new InstrList(inst, null);
        } else {
            if (ilist != null) {
                throw new Error("Codegen.emit");
            }
            last = ilist = new InstrList(inst, null);
        }
    }

    InstrList codegen(Stm s) {
        munchStm(s);
        InstrList l = ilist;
        ilist = last = null;
        return l;
    }

    static Instr MOVE(String a, Temp d, Temp s) {
        return new tiger.Assem.MOVE(a, d, s);
    }

    static TempList L(Temp h) {
        return new TempList(h, null);
    }

    static TempList L(Temp h, TempList t) {
        return new TempList(h, t);
    }

    public void munchStms(StmList slist) {
        StmList list = slist;
        for (; list != null; list = list.tail) {
            munchStm(list.head);
        }
    }

    void munchStm(Stm s) {
        if (s instanceof MOVE) {
            munchStm((MOVE) s);
        } else if (s instanceof tiger.Tree.EXP) {
            munchStm((tiger.Tree.EXP) s);
        } else if (s instanceof JUMP) {
            munchStm((JUMP) s);
        } else if (s instanceof CJUMP) {
            munchStm((CJUMP) s);
        } else if (s instanceof LABEL) {
            munchStm((LABEL) s);
        } else {
            throw new Error("Codegen.munchStm");
        }
    }

    public void munchStm(CJUMP j) {
    	String oper = null;
    	switch (j.relop) {
    		case CJUMP.EQ:
    			oper = "beq";
    			break;
    		case CJUMP.NE:
    			oper = "bne";
    			break;
    		case CJUMP.GT:
    			oper = "bgt";
    			break;
    		case CJUMP.GE:
    			oper = "bge";
    			break;
    		case CJUMP.LT:
    			oper = "blt";
    			break;
    		case CJUMP.LE:
    			oper = "ble";
    			break;
    	}
    	Temp t1 = munchExp(j.left);
    	Temp t2 = munchExp(j.right);
    	emit(new OPER(oper + " `s0, `s1, `j0", null, new TempList(t1,
    	new TempList(t2, null)), new LabelList(j.iftrue, new LabelList(j.iffalse, null))));
    }
    
    public void munchStm(MOVE s) {
    	tiger.Tree.Expr dst = s.dst;
    	tiger.Tree.Expr src = s.src;
    	if (dst instanceof MEM) {
    	MEM dst1 = (MEM) dst;
    	if (dst1.exp instanceof BINOP
    	&& ((BINOP) dst1.exp).binop == tiger.Tree.BINOP.PLUS
    	&& ((BINOP) dst1.exp).right instanceof CONST) {
    	Temp t1 = munchExp(src);
    	Temp t2 = munchExp(((BINOP) dst1.exp).left);
    	emit(new OPER("sw `s0, "
    			+ ((CONST) ((BINOP) dst1.exp).right).value + "(`s1)",
    			null, new TempList(t1, new TempList(t2, null))));
    	} else if (dst1.exp instanceof BINOP
    			&& ((BINOP) dst1.exp).binop == tiger.Tree.BINOP.PLUS
    			&& ((BINOP) dst1.exp).left instanceof CONST) {
    			Temp t1 = munchExp(src);
    			Temp t2 = munchExp(((BINOP) dst1.exp).right);
    			emit(new OPER("sw `s0, "
    			+ ((CONST) ((BINOP) dst1.exp).left).value + "(`s1)",
    			null, new TempList(t1, new TempList(t2, null))));
    			} else if (dst1.exp instanceof CONST) {
    			Temp t1 = munchExp(src);
    			emit(new OPER("sw `s0, " + ((CONST) dst1.exp).value, null,
    			new TempList(t1, null)));
    			} else {
    			Temp t1 = munchExp(src);
    			Temp t2 = munchExp(dst1.exp);
    			emit(new OPER("sw `s0, (`s1)", null, new TempList(t1,
    			new TempList(t2, null))));
    			}
    			} else if (dst instanceof TEMP)
    			if (src instanceof CONST) {
    			emit(new OPER("li `d0, " + ((CONST) src).value, new TempList(
    			((TEMP) dst).temp, null), null));
    			} else {
    			Temp t1 = munchExp(src);
    			emit(new OPER("move `d0, `s0", new TempList(((TEMP) dst).temp,
    			null), new TempList(t1, null)));
    			}
    			}

    void munchStm(tiger.Tree.EXP s) {
        munchExp(s.exp);
    }

    public void munchStm(JUMP j) {
    	emit(new OPER("j " + j.targets.head, null, null, j.targets));
    	}
 
    public void munchStm(LABEL l) {
    	emit(new tiger.Assem.LABEL(l.label.toString() + ":", l.label));
    	}

    Temp munchExp(Expr s) {
        if (s instanceof CONST) {
            return munchExp((CONST) s);
        } else if (s instanceof NAME) {
            return munchExp((NAME) s);
        } else if (s instanceof TEMP) {
            return munchExp((TEMP) s);
        } else if (s instanceof BINOP) {
            return munchExp((BINOP) s);
        } else if (s instanceof MEM) {
            return munchExp((MEM) s);
        } else if (s instanceof CALL) {
            return munchExp((CALL) s);
        } else {
            throw new Error("Codegen.munchExp");
        }
    }

    public Temp munchExp(CONST e) {
    	Temp ret = new Temp();
    	emit(new OPER("li `d0, " + e.value, new TempList(ret, null), null));
    	return ret;
    }

    public Temp munchExp(NAME t) {
    	Temp ret = new Temp();
    	emit(new OPER("la `d0, " + t.label, new TempList(ret, null), null));
    	return ret;
    }

    public Temp munchExp(TEMP t) {
    	return t.temp;
    }
    
    private static String[] BINOP = new String[10];

    static {
        BINOP[tiger.Tree.BINOP.PLUS] = "add";
        BINOP[tiger.Tree.BINOP.MINUS] = "sub";
        BINOP[tiger.Tree.BINOP.MUL] = "mulo";
        BINOP[tiger.Tree.BINOP.DIV] = "div";
        BINOP[tiger.Tree.BINOP.AND] = "and";
        BINOP[tiger.Tree.BINOP.OR] = "or";
        BINOP[tiger.Tree.BINOP.LSHIFT] = "sll";
        BINOP[tiger.Tree.BINOP.RSHIFT] = "srl";
        BINOP[tiger.Tree.BINOP.ARSHIFT] = "sra";
        BINOP[tiger.Tree.BINOP.XOR] = "xor";
    }
	
    public Temp munchExp(BINOP e) {
    	Temp ret = new Temp();
    	String oper = null;
    	switch (e.binop) {
    		case tiger.Tree.BINOP.PLUS:
    			oper = "add";
    			break;
    		case tiger.Tree.BINOP.MINUS:
    			oper = "sub";
    			break;
    		case tiger.Tree.BINOP.MUL:
    			oper = "mul";
    			break;
    		case tiger.Tree.BINOP.DIV:
    			oper = "div";
    			break;
    	}
    	if (e.right instanceof CONST) {
    	Temp t1 = munchExp(e.left);
    	emit(new OPER(oper + " `d0, `s0, " + ((CONST) e.right).value,
    	new TempList(ret, null), new TempList(t1, null)));
    	} else if (e.left instanceof CONST) {
    		Temp t1 = munchExp(e.right);
    		emit(new OPER(oper + " `d0, `s0, " + ((CONST) e.left).value,
    		new TempList(ret, null), new TempList(t1, null)));
    		} else {
    			Temp t1 = munchExp(e.left);
    			Temp t2 = munchExp(e.right);
    			emit(new OPER(oper + " `d0, `s0, `s1", new TempList(ret, null),new TempList(t1, new TempList(t2, null))));
    		}
    		return ret;
    }

    public Temp munchExp(MEM e) {
    	Temp ret = new Temp();
    	if (e.exp instanceof BINOP && ((BINOP) e.exp).binop == tiger.Tree.BINOP.PLUS
    	&& ((BINOP) e.exp).right instanceof CONST) {
    	Temp t1 = munchExp(((BINOP) e.exp).left);
    	emit(new OPER("lw `d0, " + ((CONST) ((BINOP) e.exp).right).value
    	+ "(`s0)", new TempList(ret, null), new TempList(t1, null)));

    	} else if (e.exp instanceof BINOP && ((BINOP) e.exp).binop == tiger.Tree.BINOP.PLUS
    	&& ((BINOP) e.exp).left instanceof CONST) {
    		Temp t1 = munchExp(((BINOP) e.exp).right);
    		emit(new OPER("lw `d0, " + ((CONST) ((BINOP) e.exp).left).value
    		+ "(`s0)", new TempList(ret, null), new TempList(t1, null)));

    		} else if (e.exp instanceof CONST) {
    		emit(new OPER("lw `d0, " + ((CONST) e.exp).value, new TempList(ret,
    		null), null));
    		} else {
    		Temp t1 = munchExp(e.exp);
    		emit(new OPER("lw `d0, (`s0)", new TempList(ret, null),
    		new TempList(t1, null)));
    		}
    		return ret;
    }
    	

    public Temp munchExp(CALL c) {
    	TempList list = null;
    	int i = 0;
    	for (tiger.Tree.ExpList a = c.args; a != null; a = a.tail, ++i) {
    	Temp t = null;
    	if (a.head instanceof CONST)
    	emit(new OPER("li $a" + i + ", " + ((CONST) a.head).value,
    	null, null));
    	else {
    	t = munchExp(a.head);
    	emit(new OPER("move $a" + i + ", `s0", null, new TempList(t,
    	null)));
    	}
    	if (t != null)
    	list = new TempList(t, list);
    	}
    	emit(new OPER("jal " + ((NAME) c.func).label, MipsFrame.calldefs, list));
    	return MipsFrame.V0;
    }


    public String format(InstrList is, TempMap f) {
        String s = "";
        s = is.head.toString() + "";
        System.out.println(is.head);
        System.out.println(is.head.format(f));
        if (is.tail == null) {
            return s + is.head.format(f);
        } else {
            return s + is.head.format(f) + "" + format(is.tail, f);
        }
    }
}

