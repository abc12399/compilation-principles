import org.ietf.jgss.Oid;

import javax.swing.*;
import java.io.*;

import java.net.MalformedURLException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Stack;

public class Main {

    public Word word;
    public Scanner scanner;
    public int number;

    public String Out;
    public ArrayList<Var> varList;

    public ArrayList<String> funcList;
    public int varNum;
    public int orderNum;

    public int Varpos;

    public int waiting;

    public int searchFuncPos;
    public int Operate(int m,int n,char x){
        if(x=='+'){
            return m+n;
        }
        else if(x=='-'){
            return m-n;
        }
        else if(x=='*'){
            return m*n;
        }
        else if(x=='%'){
            return m%n;
        }
        else {
            return m/n;
        }

    }

    public int flag=0;
    public void error(){
        System.out.println(word.getWord());
        System.out.println(Out);
        System.exit(-1);
    }


    public void Number(){
        if(word.getType().equals("Number")){
            number= Integer.valueOf(word.getWord());
            word= scanner.scan();
            Var var =new Var();
            var.setValue(number);
            var.setType("value");
            varList.add(var);
            varNum++;
        }
        return ;
    }
    public void Lval(){
        if (!search(word.getWord())){
            error();
        }
        else{
            Ident();
        }
    }
    public void PrimaryExp(){
        if(word.getWord().equals("(")){
            //    System.out.println("(");
            word= scanner.scan();
            Exp();
            if(word.getWord().equals(")")){
                //  System.out.println(")");
                word= scanner.scan();
                //   System.out.println("primary:"+num);
                //    System.out.println(word.getWord());
                return;
            }
            error();
            return;
        }
        else if(word.getType().equals("Ident")){
            Lval();
            int waiting=Varpos;
            Var var=new Var();
            var.setOrder(orderNum);

            String s1="\t";
            s1+=var.getOrderUse();
            s1+=" = load i32, i32* ";

            s1+=varList.get(waiting).getOrderUse();

            s1+="\n";
            Out+=s1;
            varList.add(var);
            varNum++;
            orderNum++;
        }
        else{

            Number();
        }
    }
    public boolean searchFunList(String s){
        for (int i=0;i<funcList.size();i++){
            if(s.equals(funcList.get(i))){
                searchFuncPos=i;
                return true;
            }
        }
        return false;

    }
    public void UnaryExp(){
        //System.out.println("tttt");
        if(word.getWord().equals("+")||word.getWord().equals("-")){
            while(word.getWord().equals("+")||word.getWord().equals("-")){

                if(word.getWord().equals("-")){
                    flag+=1;
                }
                word= scanner.scan();
            }
            PrimaryExp();
            //   System.out.println("flag:"+flag+"num:"+num);
            if(flag%2==0){
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = add i32 0";
                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;
            }
            else{
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = sub i32 0";
                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;
            }
        }
        else if(word.getType().equals("Ident")){
            if(!searchFunList(word.getWord())){
                Ident();
                if(word.getWord().equals("(")){
                    scanner.goBack2();
                    word= scanner.scan();
                    funcList.add(word.getWord());
                    Ident();
                    word= scanner.scan();
                    if(word.getWord().equals(")")){
                        if(funcList.get(funcList.size()-1).equals("putint")||funcList.get(funcList.size()-1).equals("putch")||funcList.get(funcList.size()-1).equals("putarray")){
                            error();
                        }
                        String s1="";
                        s1+="declare i32 @";
                        s1+=funcList.get(funcList.size()-1);
                        s1+="()\n";
                        Out=s1+Out;

                        Var var=new Var();
                        var.setOrder(orderNum);
                        varList.add(var);
                        orderNum++;
                        varNum++;
                        String s2="\t";
                        s2+=var.getOrderUse();
                        s2+=" = call i32 @";
                        s2+=funcList.get(funcList.size()-1);
                        s2+="()\n";
                        Out+=s2;
                        word= scanner.scan();

                    }
                    else{
                        if(funcList.get(funcList.size()-1).equals("getint")||funcList.get(funcList.size()-1).equals("getch")||funcList.get(funcList.size()-1).equals("getarray")){
                            error();
                        }
                        String s1="";
                        s1+="declare void @";
                        s1+=funcList.get(funcList.size()-1);
                        s1+="(i32)\n";
                        Out=s1+Out;
                        ArrayList<Integer> waitnum=new ArrayList<>();
                        Exp();
                        waitnum.add(varNum-1);
                        while(word.getWord().equals(",")){
                            word= scanner.scan();
                            Exp();
                            waitnum.add(varNum-1);
                        }
                        for (int i = 0; i < waitnum.size(); i++) {
                            String s2="\tcall void @";
                            s2+=funcList.get(funcList.size()-1);
                            s2+="(i32 ";
                            if(varList.get(waitnum.get(i)).getType().equals("value")){
                                s2+=varList.get(waitnum.get(i)).getValue();
                            }
                            else{
                                s2+=varList.get(waitnum.get(i)).getOrderUse();
                            }
                            if(i==waitnum.size()-1){
                                s2+=")\n";
                            }
                            else{
                                s2+=", ";
                            }
                            Out+=s2;
                        }
                        if(word.getWord().equals(")")){
                            word= scanner.scan();
                            return;
                        }
                        error();
                    }
                }
                else{
                    scanner.goBack2();
                    word= scanner.scan();
                    PrimaryExp();
                }

            }
            else{
                Ident();
                if(word.getWord().equals("(")){
                    word= scanner.scan();
                    if(word.getWord().equals(")")){
                        if(funcList.get(searchFuncPos).equals("putint")||funcList.get(searchFuncPos).equals("putch")||funcList.get(searchFuncPos).equals("putarray")){
                            error();
                        }
                        Var var=new Var();
                        var.setOrder(orderNum);
                        varList.add(var);
                        orderNum++;
                        varNum++;
                        String s2="\t";
                        s2+=var.getOrderUse();
                        s2+=" = call i32 @";
                        s2+=funcList.get(searchFuncPos);
                        s2+="()\n";
                        Out+=s2;
                        word= scanner.scan();
                        return;
                    }
                    else{
                        if(funcList.get(searchFuncPos).equals("getint")||funcList.get(searchFuncPos).equals("getch")||funcList.get(searchFuncPos).equals("getarray")){
                            error();
                        }
                        ArrayList<Integer> waitnum=new ArrayList<>();
                        System.out.println("2345");
                        System.out.println(word.getWord());
                        Exp();
                       // System.out.println(word.getWord());
                        waitnum.add(varNum-1);
                      //  System.out.println(waitnum);
                        while(word.getWord().equals(",")){
                            word= scanner.scan();
                            Exp();
                            waitnum.add(varNum-1);
                        }
                        System.out.println();

                        for (int i = 0; i < waitnum.size(); i++) {
                            System.out.println("fddgddddddddd");
                            String s2="\tcall void @";
                            s2+=funcList.get(searchFuncPos);
                            s2+="(i32 ";
                            if(varList.get(waitnum.get(i)).getType().equals("value")){
                                s2+=varList.get(waitnum.get(i)).getValue();
                            }
                            else{
                                s2+=varList.get(waitnum.get(i)).getOrderUse();
                            }

                            if(i==waitnum.size()-1){
                                s2+=")\n";
                            }
                            else{
                                s2+=", ";
                            }
                            Out+=s2;
                        }
                        if(word.getWord().equals(")")){
                            word= scanner.scan();
                            return;
                        }
                    }
                }
                else{
                    scanner.goBack2();
                    word= scanner.scan();
                    PrimaryExp();
                }
            }
        }
        else{
            PrimaryExp();
        }

    }
    public void MulExp(){
        UnaryExp();
        while(word.getWord().equals("*")||word.getWord().equals("/")||word.getWord().equals("%")){
            int calnum=varNum-1;
            //   System.out.println("in *");
            char[] arr=word.getWord().toCharArray();

            word= scanner.scan();

            UnaryExp();
            if(arr[0]=='/'){
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = sdiv i32 ";
                if(varList.get(calnum).getType().equals("value")){
                    s1+=varList.get(calnum).getValue();
                }
                else{
                    s1+=varList.get(calnum).getOrderUse();
                }

                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;
            }
            else if(arr[0] == '*'){
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = mul i32 ";
                if(varList.get(calnum).getType().equals("value")){
                    s1+=varList.get(calnum).getValue();
                }
                else{
                    s1+=varList.get(calnum).getOrderUse();
                }

                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;
            }
            else{
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = srem i32 ";
                if(varList.get(calnum).getType().equals("value")){
                    s1+=varList.get(calnum).getValue();
                }
                else{
                    s1+=varList.get(calnum).getOrderUse();
                }

                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;
            }

        }
        // System.out.println("sum"+sum);
        return;
    }
    public void AddExp(){
        MulExp();
        while(word.getWord().equals("+")||word.getWord().equals("-")){
            int calnum=varNum-1;
            int temp=0;
            while (word.getWord().equals("+")||word.getWord().equals("-")){
                if(word.getWord().equals("-")){
                    temp++;
                }
                word= scanner.scan();
            }
            MulExp();
            //这里 Varnum-1是最新的数
            if(temp%2==0){
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = add i32 ";
                if(varList.get(calnum).getType().equals("value")){
                    s1+=varList.get(calnum).getValue();
                }
                else{
                    s1+=varList.get(calnum).getOrderUse();
                }

                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }

                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;
            }
            else{
                Var var=new Var();
                var.setOrder(orderNum);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = sub i32 ";
                if(varList.get(calnum).getType().equals("value")){
                    s1+=varList.get(calnum).getValue();
                }
                else{
                    s1+=varList.get(calnum).getOrderUse();
                }
                s1+=", ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+="\n";
                Out+=s1;
                varList.add(var);
                varNum++;
                orderNum++;
            }
        }
    }
    public void Exp(){
        AddExp();
    }
    public void Stmt(){
        if(word.getWord().equals("return")){
            word= scanner.scan();
            Exp();
            String s="\tret ";
            s+="i32 ";
            if(varList.get(varNum-1).getType().equals("value")){
                s+=varList.get(varNum-1).getValue();
            }
            else {
                s+=varList.get(varNum-1).getOrderUse();
            }
            s+="\n";
            Out+=s;
            if(word.getWord().equals(";")){
                word= scanner.scan();
                return;
            }
            error();
        }
        else if(word.getType().equals("Ident")){
            word= scanner.scan();
            if(word.getWord().equals("=")){
                scanner.goBack2();
                word= scanner.scan();

                if(!search(word.getWord())){
                    error();
                }
                else {
                    Lval();
                    int waiting=Varpos;
                    if(varList.get(waiting).getType().equals("Const")){
                        error();
                    }
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        Exp();
                    //    System.out.println(word.getWord());
                        String s1="\t";
                        s1+="store i32 ";
                        if(varList.get(varNum-1).getType().equals("value")){
                            s1+=varList.get(varNum-1).getValue();
                        }
                        else{
                            s1+=varList.get(varNum-1).getOrderUse();
                        }
                        s1+=", i32* ";
                        s1+=varList.get(waiting).getOrderUse();
                        s1+="\n";
                        Out+=s1;
                    }

                }
            }
            else{
                scanner.goBack2();
                word= scanner.scan();
                Exp();
            }
        }
        else {
            Exp();
            if(word.getWord().equals(";")){
                word= scanner.scan();
                return;
            }
            error();
        }
    }
    public void FuncType(){
        if(word.getWord().equals("int")){
            word= scanner.scan();
            return;
        }
        else{
            error();
        }
    }
    public void Ident_Main(){
        if(word.getWord().equals("main")){
            word=scanner.scan();
            return;
        }
        error();
    }
    public void Ident(){
        if(word.getType().equals("Ident")){
            word= scanner.scan();
            return;
        }
        else {
            error();
        }
    }
    public boolean search(String str){
        for (int i = varList.size()-1; i>=0; i--) {
            if(str.equals(varList.get(i).getWord())){
                Varpos=i;
                return true;
            }
        }
        return false;
    }
    public void InitVal(){
        Exp();
    }
    public void VarDef(){
        if(!search(word.getWord())&&word.getType().equals("Ident")){
            Var var=new Var();
            var.setWord(word.getWord());
            var.setOrder(orderNum);
            varList.add(var);
            Varpos=varNum;
            varNum++;
            orderNum++;
            String s="\t";
            s+=var.getOrderUse();
            s+=" = alloca i32\n";
            Out+=s;
        }
        else{
            error();
        }
        Ident();
        if(word.getWord().equals("=")){
            word= scanner.scan();
            waiting=Varpos;
            InitVal();

            //这里 定义 int a=1; 把1 存储在a中
            String s1="\tstore i32 ";
        //    System.out.println(varNum);
        //    System.out.println(varList.get(varNum-1).getType());
            if(varList.get(varNum-1).getType().equals("value")){
                s1+=varList.get(varNum-1).getValue();
            }
            else{
                s1+=varList.get(varNum-1).getOrderUse();
            }
            s1+=", i32* ";
            s1+=varList.get(waiting).getOrderUse();
            s1+="\n";
            Out+=s1;

            return;
        }
    }
    public void Btype(){
        if (word.getWord().equals("int")){
            word= scanner.scan();
            return;
        }
        else {
            error();
        }
    }
    public void VarDecl(){
        Btype();
        VarDef();
        while(!word.getWord().equals(";")){
            if(word.getWord().equals(",")){
                word= scanner.scan();
                VarDef();
            }
            else {
                error();
            }
        }
        word=scanner.scan();
        return;
    }
    public void ConstExp(){
        AddExp();
    }
    public void ConstInitVal(){
        ConstExp();

    }
    public void ConstDef(){
        if(!search(word.getWord())&&word.getType().equals("Ident")){
            Var var=new Var();
            var.setWord(word.getWord());
            var.setOrder(orderNum);
            var.setType("Const");
            varList.add(var);
            Varpos=varNum;
            varNum++;
            orderNum++;
            String s="\t";
            s+=var.getOrderUse();
            s+=" = alloca i32\n";
            Out+=s;

            Ident();
            if(word.getWord().equals("=")){
                word= scanner.scan();
                waiting=Varpos;
                ConstInitVal();

                //这里 定义 int a=1; 把1 存储在a中
                String s1="\tstore i32 ";
                System.out.println(varList);
                System.out.println(varList.get(varNum-1).getType());
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+=", i32* ";
                s1+=varList.get(waiting).getOrderUse();
                s1+="\n";
                Out+=s1;

                return;
            }
            error();
        }
        else{
            error();
        }

    }
    public void ConstDecl(){
        if(word.getWord().equals("const")){
            word= scanner.scan();
            Btype();
            ConstDef();
            while(!word.getWord().equals(";")){
                if(word.getWord().equals(",")){
                    word= scanner.scan();
                    ConstDef();
                }
            }
            word= scanner.scan();
            return;
        }
        error();
    }

    public void Decl(){
        if(word.getWord().equals("const")){
            ConstDecl();
        }
        else{
            VarDecl();
        }
    }
    public void BlockItem(){
        if(word.getWord().equals("const")||word.getWord().equals("int")){
            Decl();
        }
        else{
            Stmt();
        }
        return;
    }

    public void Block(){
        if (word.getWord().equals("{")){
            Out+="{";
            Out+="\n";
            word=scanner.scan();

            while(!word.getWord().equals("}")){
                BlockItem();
            }

            if(word.getWord().equals("}")){
                //这里理论上来说是最后的位置
                //暂时不scan
                Out+="}";
                return;
            }
            error();
        }
    }
    public void FuncDef(){
        Out+="define dso_local i32 @main()";
        FuncType();
        String temp="main";
        funcList.add(temp);
        Var var=new Var();
        var.setWord(word.getWord());
        var.setOrder(varNum);
        varList.add(var);
        varNum++;orderNum++;

        Ident();
        if(word.getWord().equals("(")){
            word= scanner.scan();
            if(word.getWord().equals(")")){
                word= scanner.scan();
                Block();
                return;
            }
            error();
        }
        else{
            error();
        }

    }
    public void CompUnit(){
        FuncDef();
        return;
    }
    public static void main(String[] args) {
        System.out.println("123");

        String path=args[0];
        String output=args[1];


        String filecontent="";

        File source=new File(path);
        File output_source=new File(output);
        PrintWriter pw=null;
        try {
            Reader in =new FileReader(source);
            try {
                FileWriter fw=new FileWriter(output_source);
                pw=new PrintWriter(fw);
            } catch (IOException e) {
                System.exit(-1);
                e.printStackTrace();
            }
            BufferedReader bufferReader =new BufferedReader(in);
            String str=null;
            while (true){
                try {
                    if (!((str=bufferReader.readLine())!=null)) break;
                } catch (IOException e) {
                    System.exit(-1);
                    e.printStackTrace();
                }
                filecontent+=str;
                filecontent+='\n';
            }

        } catch (FileNotFoundException e) {
            System.exit(-1);
            e.printStackTrace();
        }

        Main main=new Main();
        main.scanner =new Scanner(filecontent);
        main.word= main.scanner.scan();

        main.varList=new ArrayList<>();
        main.funcList=new ArrayList<>();
        main.Out="";

        main.varNum=0;
        main.orderNum=0;
        main.CompUnit();
   //     System.out.println(main.Out);
        pw.print(main.Out);
        pw.flush();
        pw.close();


    }
}