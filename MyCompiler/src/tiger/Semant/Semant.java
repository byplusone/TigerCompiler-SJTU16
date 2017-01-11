package tiger.Semant;

import tiger.Absyn.FieldList;
import tiger.errormsg.*; 
import tiger.Translate.Level;
import tiger.Types.*;
import tiger.Util.BoolList;
import tiger.Symbol.Symbol;

public class Semant {
	private Env env;
	private tiger.Translate.Translate trans;
	private tiger.Translate.Level level = null;
	private java.util.Stack<tiger.Temp.Label> loopStack = new java.util.Stack<tiger.Temp.Label>(); //实现循环的堆栈
	
	public Semant(tiger.Translate.Translate t, ErrorMsg err)
	{
		trans = t;
		level = new Level(t.frame);
		level = new Level(level, Symbol.symbol("main"), null);
		env = new Env(err, level);
	}
	public tiger.Translate.Frag transProg(tiger.Absyn.Exp e)
	{
		ExpTy et = transExp(e);
		if(ErrorMsg.anyErrors)
		{
			System.out.println("Semantic Error Detected");
			return null;
		}
		trans.procEntryExit (level, et.exp, false); 
		level = level.parent;
		return trans.getResult();
	}
	public ExpTy transVar(tiger.Absyn.Var e)
	{
		if (e instanceof tiger.Absyn.SimpleVar) return transVar((tiger.Absyn.SimpleVar)e);
		if (e instanceof tiger.Absyn.SubscriptVar) return transVar((tiger.Absyn.SubscriptVar)e);
		if (e instanceof tiger.Absyn.FieldVar) return transVar((tiger.Absyn.FieldVar)e);
		env.errorMsg.error(e.pos, "Unknow Var!");
		return null;
	}
	//调用重载函数检查表达式
	public ExpTy transExp(tiger.Absyn.Exp e)
	{
		if (e instanceof tiger.Absyn.IntExp) return transExp((tiger.Absyn.IntExp)e);
		if (e instanceof tiger.Absyn.StringExp) return transExp((tiger.Absyn.StringExp)e);
		if (e instanceof tiger.Absyn.NilExp) return transExp((tiger.Absyn.NilExp)e);
		if (e instanceof tiger.Absyn.VarExp) return transExp((tiger.Absyn.VarExp)e);
		if (e instanceof tiger.Absyn.OpExp) return transExp((tiger.Absyn.OpExp)e);
		if (e instanceof tiger.Absyn.AssignExp) return transExp((tiger.Absyn.AssignExp)e);
		if (e instanceof tiger.Absyn.CallExp) return transExp((tiger.Absyn.CallExp)e);
		if (e instanceof tiger.Absyn.RecordExp) return transExp((tiger.Absyn.RecordExp)e);
		if (e instanceof tiger.Absyn.ArrayExp) return transExp((tiger.Absyn.ArrayExp)e);
		if (e instanceof tiger.Absyn.IfExp) return transExp((tiger.Absyn.IfExp)e);
		if (e instanceof tiger.Absyn.WhileExp) return transExp((tiger.Absyn.WhileExp)e);
		if (e instanceof tiger.Absyn.ForExp) return transExp((tiger.Absyn.ForExp)e);
		if (e instanceof tiger.Absyn.BreakExp) return transExp((tiger.Absyn.BreakExp)e);
		if (e instanceof tiger.Absyn.LetExp) return transExp((tiger.Absyn.LetExp)e);
		if (e instanceof tiger.Absyn.SeqExp) return transExp((tiger.Absyn.SeqExp)e);
		return null;
	}
	//调用重载函数检查声明
	public tiger.Translate.Exp transDec(tiger.Absyn.Dec e)
	{
		if (e instanceof tiger.Absyn.VarDec) return transDec((tiger.Absyn.VarDec)e);
		if (e instanceof tiger.Absyn.TypeDec) return transDec((tiger.Absyn.TypeDec)e);
		if (e instanceof tiger.Absyn.FunctionDec) return transDec((tiger.Absyn.FunctionDec)e);
		env.errorMsg.error(e.pos, "Unknow Dec!");
		return null;
	}
	//调用重载函数检查返回类型Type类
	public Type transTy(tiger.Absyn.Ty e)
	{
		if (e instanceof tiger.Absyn.ArrayTy) return transTy((tiger.Absyn.ArrayTy)e);
		if (e instanceof tiger.Absyn.RecordTy) return transTy((tiger.Absyn.RecordTy)e);
		if (e instanceof tiger.Absyn.NameTy) return transTy((tiger.Absyn.NameTy)e);
		env.errorMsg.error(e.pos, "Unknow Ty!");
		return null;
	}

	//a 整数表达式，字符串表达式，nil表达式，变量表达式
	private ExpTy transExp(tiger.Absyn.IntExp e)
	{
		return new ExpTy(trans.transIntExp(e.value), new INT());
	}
	private ExpTy transExp(tiger.Absyn.StringExp e)
	{
		return new ExpTy(trans.transStringExp(e.value), new STRING());
	}
	private ExpTy transExp(tiger.Absyn.NilExp e)
	{
		return new ExpTy(trans.transNilExp(), new NIL());
	}
	private ExpTy transExp(tiger.Absyn.VarExp e)
	{
		return transVar(e.var);
	}
	//b 检查算数表达式 c测试相等不等表达式
	private ExpTy transExp(tiger.Absyn.OpExp e)
	{
		ExpTy el = transExp(e.left);
		ExpTy er = transExp(e.right);
		if (el == null || er == null)
		{
			env.errorMsg.error(e.pos, "Lack of operator!");
			return null;
		}
		if (e.oper == tiger.Absyn.OpExp.EQ || e.oper == tiger.Absyn.OpExp.NE) 
		{
			if (el.ty.actual() instanceof NIL && er.ty.actual() instanceof NIL)
			{
				env.errorMsg.error(e.pos, "NIL cannot compare with NIL");
				return null;
			}
			if (el.ty.actual() instanceof VOID || er.ty.actual() instanceof VOID)//左右任一个不能为void类型
			{
				env.errorMsg.error(e.pos, "Cannot compare VOID");
				return null;
			}
			if (el.ty.actual() instanceof NIL && er.ty.actual() instanceof RECORD)//可以一个为nil一个为record
				return new ExpTy(trans.transOpExp(e.oper, transExp(e.left).exp, transExp(e.right).exp), new INT());
			if (el.ty.actual() instanceof RECORD && er.ty.actual() instanceof NIL)//同上
				return new ExpTy(trans.transOpExp(e.oper, transExp(e.left).exp, transExp(e.right).exp), new INT());
			if (el.ty.coerceTo(er.ty))//其他情况下左右类型必须一致
			{
				if (el.ty.actual() instanceof STRING && e.oper == tiger.Absyn.OpExp.EQ)
				{
					return new ExpTy(trans.transStringRelExp(level, e.oper, transExp(e.left).exp, transExp(e.right).exp), new INT());
				}
				return new ExpTy(trans.transOpExp(e.oper, transExp(e.left).exp, transExp(e.right).exp), new INT());
			}

			env.errorMsg.error(e.pos, "Two operator should have the same type");
			return null;
		}
		if (e.oper > tiger.Absyn.OpExp.NE)//关系运算符必须左右全为INT或STRING
		{
			if (el.ty.actual() instanceof INT && er.ty.actual() instanceof INT)
				return new ExpTy(trans.transOpExp(e.oper, transExp(e.left).exp, transExp(e.right).exp), new INT());
			if (el.ty.actual() instanceof STRING && er.ty.actual() instanceof STRING)
				return new ExpTy(trans.transOpExp(e.oper, transExp(e.left).exp, transExp(e.right).exp), new STRING());
			env.errorMsg.error(e.pos, "cannot compare two operator with different type");
			return null;
		}
		if (e.oper < tiger.Absyn.OpExp.EQ)
		{	
			if (el.ty.actual() instanceof INT && er.ty.actual() instanceof INT)
				return new ExpTy(trans.transOpExp(e.oper, transExp(e.left).exp, transExp(e.right).exp), new INT());
			env.errorMsg.error(e.pos, "Two operator should have the same type");
			return null;
		}

		return new ExpTy(trans.transOpExp(e.oper, el.exp, er.exp), new INT());
	}
	private ExpTy transExp(tiger.Absyn.AssignExp e)//赋值运算符
	{
		int pos=e.pos;
		tiger.Absyn.Var var=e.var;
		tiger.Absyn.Exp exp=e.exp;
		ExpTy er = transExp(exp);
		if (er.ty.actual() instanceof VOID)//不能是void类型
		{
			env.errorMsg.error(pos, "cannot assign a var with nil");
			return null;
		}
		if (var instanceof tiger.Absyn.SimpleVar)
		{
			tiger.Absyn.SimpleVar ev = (tiger.Absyn.SimpleVar)var;
			Entry x= (Entry)(env.vEnv.get(ev.name));
			if (x instanceof VarEntry && ((VarEntry)x).isFor)//循环变量不可被赋值
			{
				env.errorMsg.error(pos, "loopvar cannot be assigned");
				return null;
			}
		}
		ExpTy vr = transVar(var);
		if (!er.ty.coerceTo(vr.ty))//无法进行强制转换
		{
				env.errorMsg.error(pos, er.ty.actual().toString()+"type value cannot be assigned as "+vr.ty.actual().toString()+" type");
				return null;	
		}
		return new ExpTy(trans.transAssignExp(vr.exp, er.exp), new VOID());
		
	}
	private ExpTy transExp(tiger.Absyn.CallExp e)//调用表达式
	{
		/*如果在 vEnv 里查不到函数名或者它不是 FuncEntry (包括 StdFuncEntry) 则报错:函数未定义
			然后逐个检查形参和实参是否匹配(用 AssignExp 的方法检查),遇到不匹配则报错.
			在遍历形参链表的时候,可能遇到链表空或有剩余的情况,此时分别报告实参过多或不足的错误
		* */
		Object x = env.vEnv.get(e.func);
		tiger.Absyn.ExpList ex =e.args;
		FunEntry fe = (FunEntry)x;
		RECORD rc = fe.formals;
		while (ex != null)
		{
			if (rc == null)
			{
				env.errorMsg.error(e.pos, "different from parameter type");
				return null;
			}
			ex = ex.tail;
			rc = rc.tail;
		}
		java.util.ArrayList<tiger.Translate.Exp> arrl = new java.util.ArrayList<tiger.Translate.Exp>();
		for (tiger.Absyn.ExpList i = e.args; i != null; i = i.tail)
			arrl.add(transExp(i.head).exp);
		if (x instanceof StdFuncEntry)
		{
			StdFuncEntry sf = (StdFuncEntry)x;
			return new ExpTy(trans.transStdCallExp(level, sf.label, arrl), sf.result);
		}
		return new ExpTy(trans.transCallExp(level, fe.level, fe.label, arrl), fe.result);
	}
	private ExpTy transExp(tiger.Absyn.RecordExp e)
	{
		/*先在 tEnv 中查找类型是否存在,若否或非记录类型报告未知记录类型错误
		然后逐个检查记录表达式和记录类型域的名字是否相同
		然后逐个检查记录表达式和记录类型域的类型是否匹配 (用 AssignExp 方法检查)
		在遍历记录类型链表的时候,可能遇到链表空或有剩余的情况,此时分别报告域过多或不足的错误
		* */
		Type t =(Type)env.tEnv.get(e.typ);
		if (t == null || !(t.actual() instanceof RECORD))
		{
			env.errorMsg.error(e.pos, "this type doesn't exist in this field");
			return null;
		}
		tiger.Absyn.FieldExpList fe = e.fields;
		RECORD rc = (RECORD)(t.actual());
		if (fe == null && rc != null)
		{
			env.errorMsg.error(e.pos, "the types in this field isn't consistent");
			return null;
		}
		
		while (fe != null)
		{	
			ExpTy ie = transExp(fe.init);
			fe = fe.tail;
			rc = rc.tail;
		}	
		java.util.ArrayList<tiger.Translate.Exp> arrl = new java.util.ArrayList<tiger.Translate.Exp>();
		for (tiger.Absyn.FieldExpList i = e.fields; i != null; i = i.tail)
			arrl.add(transExp(i.init).exp);
		return new ExpTy(trans.transRecordExp(level, arrl), t.actual()); 
	}
	private ExpTy transExp(tiger.Absyn.ArrayExp e)
	{
		/*
		先在 tEnv 中查找类型是否存在,若否或非数组类型报告未知记录类型错误
		再检查数组范围是否为整数,若否报错
		再用 AssignExp 的方法检查 tEnv 中的数组类型和实际类型是否匹配,若否报告类型匹配错误
		* */
		Type ty = (Type)env.tEnv.get(e.typ);
		if (ty == null || !(ty.actual() instanceof ARRAY))
		{
			env.errorMsg.error(e.pos, "this array doesn't exist");
			return null;
		}
		ExpTy size = transExp(e.size);
		if (!(size.ty.actual() instanceof INT))
		{
			env.errorMsg.error(e.pos, "the length of array should be INT");
			return null;
		}	

		ARRAY ar = (ARRAY)ty.actual();
		ExpTy ini = transExp(e.init);
		if (!ini.ty.coerceTo(ar.element))
		{
			env.errorMsg.error(e.pos, "the type of initial value isn't consistent with the type of the array");
			return null;
		}
		return new ExpTy(trans.transArrayExp(level, ini.exp, size.exp), new ARRAY(ar.element));			
	}
	private ExpTy transExp(tiger.Absyn.IfExp e)
	{
		ExpTy testET = transExp(e.test);//翻译控制条件
		ExpTy thenET = transExp(e.thenclause);//翻译条件为真时运行的程序
		ExpTy elseET = transExp(e.elseclause);//翻译条件为假时运行的程序
		//控制条件必须为int类型的表达式,不然则报错
		if (e.test == null || testET == null || !(testET.ty.actual() instanceof INT))
		{
			env.errorMsg.error(e.pos, "the expression in IF cannot be INT");
			return null;
		}
		//若没有false分支,则if语句不应有返回值
		if (e.elseclause == null && (!(thenET.ty.actual() instanceof VOID)))
		{
			env.errorMsg.error(e.pos, "there shouldn't be return value");
			return null;
		}
		//若没有假分支,则将假分支作为空语句翻译
		if (elseET == null)
			return new ExpTy(trans.transIfThenElseExp(testET.exp, thenET.exp, trans.transNoOp()), thenET.ty);
		return new ExpTy(trans.transIfThenElseExp(testET.exp, thenET.exp, elseET.exp), thenET.ty);
	}
	private ExpTy transExp(tiger.Absyn.WhileExp e)
	{
		//翻译while循环语句
		ExpTy transt = transExp(e.test);//翻译循环条件
		if (transt == null)	{
			env.errorMsg.error(e.pos, "loop should have an condition");
			return null;
		}
		//循环条件必须为整数类型
		if (!(transt.ty.actual() instanceof INT))
		{
			env.errorMsg.error(e.pos, "the expression of a loop cannot be INT");
			return null;
		}

		tiger.Temp.Label out = new tiger.Temp.Label();
		//循环出口的标记
		loopStack.push(out);//将循环压栈一遍处理循环嵌套
		ExpTy bdy = transExp(e.body);//翻译循环体
		loopStack.pop();//将当前循环弹出栈
		
		if (bdy == null){env.errorMsg.error(e.pos, "loop should have an condition");	return null;}
		//while循环无返回值
		if (!(bdy.ty.actual() instanceof VOID))
		{
			env.errorMsg.error(e.pos, "no return value for while loop");
			return null;
		}
		
		return new ExpTy(trans.transWhileExp(transt.exp, bdy.exp, out), new VOID());
	}
	private ExpTy transExp(tiger.Absyn.ForExp e)
	{
		//翻译for循环
		boolean flag = false;//标记循环体是否为空
		//循环变量必须是整数类型
		if (!(transExp(e.hi).ty.actual() instanceof INT) || !(transExp(e.var.init).ty.actual() instanceof INT))
		{
			env.errorMsg.error(e.pos, "loopvar must be INT");
		}
		//由于需要为循环变量分配存储空间,故需要新开始一个作用域
		env.vEnv.beginScope();
		tiger.Temp.Label label = new tiger.Temp.Label();//定义循环的入口
		loopStack.push(label);
		//循环入栈
		tiger.Translate.Access acc = level.allocLocal(true);
		//为循环变量分配空间
		env.vEnv.put(e.var.name, new VarEntry(new INT(), acc, true));
		//将循环变量加入变量符号表
		ExpTy body = transExp(e.body);
		//翻译循环体
		ExpTy high = transExp(e.hi);
		//翻译循环变量的最终值表达式
		ExpTy low = transExp(e.var.init);
		//翻译循环变量的初始值表达式
		if (body == null)	flag = true;
		loopStack.pop();
		//循环弹出栈
		env.vEnv.endScope();
		//结束当前的定义域
		
		if (flag){
			env.errorMsg.error(e.pos, "nothing in the loop");
			return null;
		}
	
		return new ExpTy(trans.transForExp(level, acc, low.exp, high.exp, body.exp, label), new VOID());
	}
	private ExpTy transExp(tiger.Absyn.BreakExp e)
	{
		//翻译break语句
		//若break语句不在循环内使用则报错
		if (loopStack.isEmpty())
		{
			env.errorMsg.error(e.pos, "break should be in a loop");
			return null;
		}
		return new ExpTy(trans.transBreakExp(loopStack.peek()), new VOID());//传入当前的循环
	}
	private ExpTy transExp(tiger.Absyn.LetExp e)
	{
		//翻译let-in-end语句
		tiger.Translate.Exp ex = null;
		//let-in之间新开一个定义域
		env.vEnv.beginScope();
		env.tEnv.beginScope();	
		ExpTy td = transDecList(e.decs);
		//翻译类型\变量\函数申明语句
		if (td != null)
			ex = td.exp;
		ExpTy tb = transExp(e.body);
		//翻译in-end之间的程序
		if (tb == null)
			ex = trans.combine2Stm(ex, null);
		else if (tb.ty.actual() instanceof VOID)
			ex = trans.combine2Stm(ex, tb.exp);
		else 
			ex = trans.combine2Exp(ex, tb.exp);
		//将两部分连接在一起
				
		env.tEnv.endScope();
		env.vEnv.endScope();
		//结束定义域
		return new ExpTy(ex, tb.ty);
	}
	private ExpTy transDecList(tiger.Absyn.DecList e)
	{
		//翻译申明列表
		tiger.Translate.Exp ex = null;
		for (tiger.Absyn.DecList i = e; i!= null; i = i.tail)
			ex = trans.combine2Stm(ex, transDec(i.head));

		return new ExpTy(ex, new VOID());
	}
	private ExpTy transExp(tiger.Absyn.SeqExp e)
	{
		//翻译表达式序列
		tiger.Translate.Exp ex = null;
		for (tiger.Absyn.ExpList t = e.list; t != null; t = t.tail)
		{
			ExpTy x = transExp(t.head);

			if (t.tail == null)
			{	
				
				if (x.ty.actual() instanceof VOID)
					ex = trans.combine2Stm(ex, x.exp);
				else 
				{
					ex = trans.combine2Exp(ex, x.exp);
				}
				return new ExpTy(ex, x.ty);
			}
			ex = trans.combine2Stm(ex, x.exp);	
		}
		env.errorMsg.error(e.pos, "SeqExp has Errors!");
		return null;
	}
	private ExpTy transVar(tiger.Absyn.SimpleVar e)
	{
		//翻译简单变量(右值)
		Entry ex = (Entry)env.vEnv.get(e.name);
		//查找入口符号表,找不到则报错
		if (ex == null || !(ex instanceof VarEntry))
		{
			env.errorMsg.error(e.pos, "undefined var");
			return null;
		}
		VarEntry evx = (VarEntry)ex;
		return new ExpTy(trans.transSimpleVar(evx.acc, level), evx.Ty);
	}
	private ExpTy transVar(tiger.Absyn.SubscriptVar e)
	{
		//翻译数组变量(右值)
		//数组下标必须为整数,不然则报错
		if (!(transExp(e.index).ty.actual() instanceof INT))
		{
			env.errorMsg.error(e.pos, "index should be INT");
			return null;
		}		
		ExpTy ev = transVar(e.var);
		//翻译数组入口
		ExpTy ei = transExp(e.index);
		//翻译数组下标的表达式
		//若入口为空则报错
		if (ev == null || !(ev.ty.actual() instanceof ARRAY))
		{
			env.errorMsg.error(e.pos, "array doesn't exist");
			return null;
		}
		ARRAY ae = (ARRAY)(ev.ty.actual());
		return new ExpTy(trans.transSubscriptVar(ev.exp, ei.exp), ae.element);
	}
	private ExpTy transVar(tiger.Absyn.FieldVar e)
	{
		//翻译域变量(右值)
		ExpTy et = transVar(e.var);
		//若除去域部分后不是记录类型,则报错
		if (!(et.ty.actual() instanceof RECORD))
		{
			env.errorMsg.error(e.pos, "this field is not a record");
			return null;
		}
		//逐个查找记录的域,如果没有一个匹配当前域变量的域,则报错
		RECORD rc = (RECORD)(et.ty.actual());
		int count = 1;
		while (rc != null)
		{
			if (rc.fieldName == e.field)
			{
				return new ExpTy(trans.transFieldVar(et.exp, count), rc.fieldType);
			}
			count++;
			rc = rc.tail;
		}
		env.errorMsg.error(e.pos, "there is no such field for this var");
		return null;
	}
	private Type transTy(tiger.Absyn.NameTy e)
	{
		//翻译未知类型  NameTy
		if (e == null)
			return new VOID();
		
		Type t =(Type)env.tEnv.get(e.name);
		//检查入口符号表,若找不到则报错
		if (t == null)
		{
			env.errorMsg.error(e.pos, "undefined type");
			return null;
		}
		return t.actual();
	}
	private ARRAY transTy(tiger.Absyn.ArrayTy e)
	{
		Type t = (Type)env.tEnv.get(e.typ);
		//检查入口符号表,若找不到则报错
		if (t == null)
		{
			env.errorMsg.error(e.pos, "undefined type");
			return null;
		}
		return new ARRAY(t);
	}
	private RECORD transTy(tiger.Absyn.RecordTy e)
	{
		RECORD rc = new RECORD(),  r = new RECORD();
		if (e == null || e.fields == null)
		{
			rc.gen(null, null, null);
			return rc;
		}
		//检查该记录类型每个域的类型在 tEnv中是否存在,若否,则报告未知类型错误
		FieldList fl = e.fields;
		boolean first = true;
		while (fl != null)
		{
			/*if (env.tEnv.get(fl.typ) == null)
			{
				env.errorMsg.error(e.pos, "域类型不存在");
				return null;
			}*/
			
			rc.gen(fl.name, (Type)env.tEnv.get(fl.typ), new RECORD());
			if (first)
			{
				r = rc;
				first = false;
			}
			if (fl.tail == null)
				rc.tail = null;
			rc = rc.tail;
			fl = fl.tail;
		}		
		
		return r;
	}
	private tiger.Translate.Exp transDec(tiger.Absyn.VarDec e)
	{
		//翻译变量定义
		ExpTy et = transExp(e.init);
		//翻译初始值
		//处记录类型外,其他变量定义必需赋初始值
		if (et == null )	
		{
			env.errorMsg.error(e.pos,"var should have an initial value");
			 return null;
		}
		//若初始值与变量类型不匹配则报错
		//if (e.typ != null && !(transExp(e.init).ty.coerceTo((Type)env.tEnv.get(e.typ.name))))
		//{
			//env.errorMsg.error(e.pos,"初始值与变量类型不匹配");
			//return null;
		//}
		//初始值不能为nil
		if (e.typ == null && e.init instanceof tiger.Absyn.NilExp)
		{
			env.errorMsg.error(e.pos, "initial value cannot be nil");
			return null;
		}
		if (e.init == null )
		{
			env.errorMsg.error(e.pos, "var should have an initial value");
			return null;
		}
		tiger.Translate.Access acc = level.allocLocal(true);
		//为变量分配空间
		if (e.typ != null)
		{
			env.vEnv.put(e.name, new VarEntry((Type)env.tEnv.get(e.typ.name), acc));
		}
		//将变量加入入口符号表,分简略申明与长申明两种,简略申明不用写明变量类型,其类型由初始值定义
		else
		{
			env.vEnv.put(e.name, new VarEntry(transExp(e.init).ty, acc));
		}
		return trans.transAssignExp(trans.transSimpleVar(acc, level), et.exp);
	}
	private tiger.Translate.Exp transDec(tiger.Absyn.TypeDec e)
	{
		//翻译类型申明语句
		java.util.HashSet<Symbol> hs = new java.util.HashSet<Symbol>();
		//采用哈希表注意检查是否有重复定义,注意变量定义若有重复则直接覆盖,而类型定义若重复则报错
		for (tiger.Absyn.TypeDec i = e; i != null; i = i.next)
		{
			if (hs.contains(i.name))
			{ 
				env.errorMsg.error(e.pos, "re-defined in a block");
				return null;
			}
			hs.add(i.name);
		}

		for (tiger.Absyn.TypeDec i = e; i != null; i = i.next)
		{
			env.tEnv.put(i.name, new NAME(i.name));
			((NAME)env.tEnv.get(i.name)).bind(transTy(i.ty));
			NAME field = (NAME)env.tEnv.get(i.name);
			if(field.isLoop() == true) 
				{
				env.errorMsg.error(i.pos, "circular define");
				return null;
				}
		}	
	//将类型放入类型符号表
	for (tiger.Absyn.TypeDec i = e; i != null; i = i.next)
		env.tEnv.put(i.name, transTy(i.ty));
		return trans.transNoOp();
	}
	
	private tiger.Translate.Exp transDec(tiger.Absyn.FunctionDec e)
	{
		//翻译函数申明
		java.util.HashSet<Symbol> hs = new java.util.HashSet<Symbol>();
		ExpTy et = null;
		//检查重复申明,分为普通函数与标准库函数
		for (tiger.Absyn.FunctionDec i = e; i != null; i = i.next)
		{
			if (hs.contains(i.name))
			{
				env.errorMsg.error(e.pos, "re-defined in a block");
				return null;
			}
			if (env.stdFuncSet.contains(i.name))
			{
				env.errorMsg.error(e.pos, "have the same name with standard function");
				return null;
			}
			
			tiger.Absyn.RecordTy rt = new tiger.Absyn.RecordTy(i.pos, i.params);
			RECORD  r = transTy(rt);
			if ( r == null){
				env.errorMsg.error(e.pos, "Record is empty!");	
				return null;
			}
			//后检查参数列表,与记录类型RecordTy的检查完全相同,得到 RECORD 类型的形参列表
			BoolList bl = null;
			for (FieldList f = i.params; f != null; f = f.tail)
			{
				bl = new BoolList(true, bl);
			}
			level = new Level(level, i.name, bl);
			env.vEnv.put(i.name, new FunEntry(level, new tiger.Temp.Label(i.name), r, transTy(i.result)));
			env.vEnv.beginScope();
			tiger.Translate.AccessList al = level.formals.tail;
			for (RECORD j = r; j!= null; j = j.tail)
			{
				if (j.fieldName != null)
				{
					env.vEnv.put(j.fieldName, new VarEntry(j.fieldType, al.head));
					al = al.tail;
				}
			}			
			et = transExp(i.body);
			
			//翻译函数体
			if (et == null)
			{	env.vEnv.endScope();	
			env.errorMsg.error(e.pos, "Body is empty.");
				return null;	}
			//着检查函数返回值,如果没有返回值则设置成 void 
			//判断是否为void,若不为void则要将返回值存入$v0寄存器
			if (!(et.ty.actual() instanceof VOID)) 
				trans.procEntryExit(level, et.exp, true);
			else 
				trans.procEntryExit(level, et.exp, false);
			
			env.vEnv.endScope();
			level = level.parent;
			//回到原来的层
			hs.add(i.name);
		}
		return trans.transNoOp();
	}
}
