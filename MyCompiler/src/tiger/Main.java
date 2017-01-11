package tiger;
import tiger.Absyn.Exp;
import tiger.Absyn.Print;
import tiger.FindEscape.FindEscape;
import tiger.RegAlloc.RegAlloc;
import tiger.Semant.Semant;
import tiger.Temp.CombineMap;
import tiger.Temp.Temp;
import tiger.Temp.TempMap;
import tiger.Translate.DataFrag;
import tiger.Translate.Frag;
import tiger.Translate.ProcFrag;
import tiger.Translate.Translate;
import tiger.Parser.*;
import tiger.errormsg.ErrorMsg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

public class Main {
    static tiger.Frame.Frame frame = new tiger.Mips.MipsFrame();

    static void prStmList(tiger.Tree.Print print, tiger.Tree.StmList stms) {
        for(tiger.Tree.StmList l = stms; l!=null; l=l.tail)
            print.prStm(l.head);
    }

    static tiger.Assem.InstrList codegen(tiger.Frame.Frame f, tiger.Tree.StmList stms) {
        tiger.Assem.InstrList first=null, last=null;
        for(tiger.Tree.StmList s=stms; s!=null; s=s.tail) {
            tiger.Assem.InstrList i = f.codegen(s.head);
            if(i == null) continue;
            if (last==null) {first=last=i;}
            else {while (last.tail!=null) last=last.tail;
                last=last.tail=i;
            }
        }
        return f.procEntryExit2(first);
    }

    static public void main(String argv[]) {
        try{
            if (argv.length == 0)
                throw new RuntimeException("no input file");

            String yourname = File.separator+argv[0];;
            String filename="testcases"+ File.separator+argv[0];
            FileReader FileIn = new FileReader(filename);
            if(!argv[0].matches(".+\\.tig") )throw new RuntimeException("Wrong Input File");
            PrintStream outs = new PrintStream(argv[0].substring(0, argv[0].length()-3) + "s");
            PrintStream outabs = new PrintStream(argv[0].substring(0, argv[0].length() - 3) + "abs");
			PrintStream outir = new PrintStream(argv[0].substring(0, argv[0].length() - 3) + "ir");
            ErrorMsg errorMsg = new ErrorMsg(filename);

            System.out.println("*****************Generating " + yourname + ".abs*****************");

            Parse p = new Parse(errorMsg);

            Exp result = p.parse(filename);

            tiger.Translate.Translate translate = new tiger.Translate.Translate(frame);

            Semant semant = new Semant(translate, p.errorMsg);

            tiger.Absyn.Print print = new tiger.Absyn.Print(outabs);
            print.prExp(result, 0);

            FindEscape findEscape = new FindEscape();
            findEscape.findEscape(result);

            //Semant semant = new Semant(mipsFrame);
            Frag frags = semant.transProg(result);


            System.out.println("*****************Generating " + yourname + ".ir*****************");
            System.out.println("*****************Generating " + yourname + ".s*****************");
            int count = 0;
            outs.println(".globl main");
            for (Frag it = frags; it != null; it = it.next, count++) {
                outir.println("---------------Frag "+ count + "-----------------");
                outs.println("###############Frag " + count + " ################");
                if (it instanceof ProcFrag)
                    emitProc(null, outir, outs, (ProcFrag)it);
                else if(it instanceof DataFrag){
                    //outir.println(((DataFrag) it).data);
                    outs.println(".data\n" + ((DataFrag) it).data);
                }
            }
            outs.println();
            BufferedReader runtime = new BufferedReader(new FileReader("runtime.s"));
            while (runtime.ready())
                outs.println(runtime.readLine());
            System.out.println("*****************Compile Success*****************");
            System.exit(0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    static void emitProc(PrintStream outabs, PrintStream outir, PrintStream outs, tiger.Translate.ProcFrag f) {
        java.io.PrintStream debug = outir;
        PrintStream debug2 = outs;
        tiger.Temp.TempMap tempmap= new tiger.Temp.CombineMap(f.frame,new tiger.Temp.DefaultMap());
        tiger.Tree.Print print = new tiger.Tree.Print(debug, tempmap);
        debug.println("# Before canonicalization: ");
        print.prStm(f.body);
        debug.print("# After canonicalization: ");
        tiger.Tree.StmList stms = tiger.Canon.Canon.linearize(f.body);
        prStmList(print,stms);
        debug.println("# Basic Blocks: ");
        tiger.Canon.BasicBlocks b = new tiger.Canon.BasicBlocks(stms);
        for(tiger.Canon.StmListList l = b.blocks; l!=null; l=l.tail) {
            debug.println("#");
            prStmList(print,l.head);
        }
        print.prStm(new tiger.Tree.LABEL(b.done));
        debug.println("# Trace Scheduled: ");
        tiger.Tree.StmList traced = (new tiger.Canon.TraceSchedule(b)).stms;
        prStmList(print,traced);

        tiger.Assem.InstrList instrs= codegen(f.frame,traced);
        debug2.println("# Instructions: ");
//        for(tiger.Assem.InstrList p=instrs; p!=null; p=p.tail)
//            debug2.print(p.head.format(tempmap));

        instrs = frame.procEntryExit2(instrs);

        RegAlloc regAlloc = new RegAlloc(f.frame,instrs);
        instrs = f.frame.procEntryExit3(instrs);
        TempMap tempMap = new CombineMap(f.frame, regAlloc);

        outs.print(".text"+"\n");
        for (tiger.Assem.InstrList p = instrs; p!=null; p = p.tail)
            outs.println(p.head.format(tempMap));
    }

}



class NullOutputStream extends java.io.OutputStream {
    public void write(int b) {}
}
