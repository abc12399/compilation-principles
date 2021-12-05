import org.ietf.jgss.Oid;

import javax.swing.*;
import java.io.*;

import java.net.MalformedURLException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Condition;

public class Main {

    public Word word;
    public Scanner scanner;
    public int number;

    public int i;
    public int j;
    public String Out;
    public ArrayList<Var> varList;

    public ArrayList<String> funcList;

    public ArrayList<Integer> paramList;

    public ArrayList<Array> arrays;
    public int varNum;
    public int orderNum;

    public String funcValStart;
    public int[] a;
    public int Varpos;

    public int waiting;

    public int searchFuncPos;

    public int blocknum;

    public int belong;

    public int check;

    public int nowarr;

    public int arrtag;
    public Stack<Var> continueStack;
    public Stack<Var> breakStack;
    public ArrayList<Var> blocklist;

    public int pass;
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
    public int constNumber(){
        if(word.getType().equals("Number")){
            number= Integer.valueOf(word.getWord());
            word= scanner.scan();
            return number;
        }
        error();
        return 0;
    }

    public int constPrimaryExp(){

        if(word.getWord().equals("(")){
            word= scanner.scan();

            int num=constAddExp();
            if(word.getWord().equals(")")){
                word= scanner.scan();
                return num;
            }
            error();
            return 0;
        }
        else if(word.getType().equals("Ident")){
            if(!search(word.getWord())){
                error();
            }
            else{
                System.out.println(varList.get(Varpos).getType());
                if(varList.get(Varpos).getType().equals("value")){
                    word=scanner.scan();
                    return varList.get(Varpos).getValue();
                }
                else {
                    error();
                }

            }
        }
        else{
            return constNumber();
        }
        return 0;
    }

    public int constUnaryExp(){
        while(word.getWord().equals("+")||word.getWord().equals("-")){

            if(word.getWord().equals("-")){
                flag+=1;
            }
            word= scanner.scan();

        }

        int num=constPrimaryExp();

        if(flag%2==0){

            flag=0;
            return num;
        }
        else{
            flag=0;
            return num*(-1);
        }

    }
    public int constMulExp(){

        int sum=constUnaryExp();
        while(word.getWord().equals("*")||word.getWord().equals("/")||word.getWord().equals("%")){
            char[] arr=word.getWord().toCharArray();

            word= scanner.scan();

            int num=constUnaryExp();

            if(num==0&&arr[0]=='/'){
                error();
            }
            else{
                sum=Operate(sum,num,arr[0]);
            }

        }
        System.out.println(sum);
        return sum;
    }
    public int constAddExp(){

        int sum=constMulExp();
        System.out.println(word.getWord()+"Safa");
        while(word.getWord().equals("+")||word.getWord().equals("-")){
            int temp=0;
            while (word.getWord().equals("+")||word.getWord().equals("-")){
                if(word.getWord().equals("-")){
                    temp++;
                }
                word= scanner.scan();
            }

            int num=constMulExp();
            if(temp%2==0){
                sum=Operate(sum,num,'+');
            }
            else{
                sum=Operate(sum,num,'-');
            }

        }
        return sum;
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
    public int searchArr(String str){
        for (int k = arrays.size()-1; k >=0; k--) {
            if(str.equals(arrays.get(k).getWord())){
                if(arrays.get(k).getBlocknum()<=blocknum){
                    return k;
                }
            }
        }
        return -1;
    }
    public void Lval(){
        if (!search(word.getWord())){
            error();
        }
        else {
            //如果能找到已经定义的val
            //如果这是在全局变量的定义过程，那么lval必须是Const
            if(check==0 && ! word.getType().equals("Const")){
                error();
            }
            Ident();

            if(word.getWord().equals("[")){
                scanner.goBack2();
                word= scanner.scan();
                String arrname=word.getWord();
                word=scanner.scan();

                int count=0;int x=0,y=1;
                while (word.getWord().equals("[")){
                    count++;
                    word= scanner.scan();
                    Exp();
                    if(count==1){
                        x=varNum-1;
                    }
                    else{
                        y=varNum-1;
                    }
                    if(word.getWord().equals("]")){
                        word= scanner.scan();
                    }
                }
                int t=searchArr(arrname);
                if(t==-1){
                    error();
                }
//                if(count!=arrays.get(t).getDimension()){
//                    error();
//                }
                if(count==1&&arrays.get(t).getDimension()==1){
                    x=varNum-1;
                }
                if(count==2&&arrays.get(t).getDimension()==2){
                  //  doFillofArray(x);
                    y=varNum-1;
                }
                Var var=new Var();
                String s1="";
                var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                s1="\t";
                s1+=var.getOrderUse();
                s1+=" = mul i32 ";
                if(varList.get(x).getType().equals("value")){
                    s1+=varList.get(x).getValue();
                }
                else{
                    s1+=varList.get(x).getOrderUse();
                }

                s1+=", ";
                s1+=arrays.get(t).getY();
                s1+="\n";
                Out+=s1;
                varNum++;
                orderNum++;


                if(count==2){
                    var=new Var();
                    var.setOrder(orderNum);
                    varList.add(var);
                    s1="\t";
                    s1+=var.getOrderUse();
                    s1+=" = add i32 ";
                    if(varList.get(y).getType().equals("value")){
                        s1+=varList.get(y).getValue();
                    }
                    else{
                        s1+=varList.get(y).getOrderUse();
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
                int waiting=varNum-1;
                {

                    if(arrays.get(t).getFlag()!=1){
                    //    arrays.get(t).setFlag(1);
                        x=arrays.get(t).getX();
                        y=arrays.get(t).getY();
                        var=new Var();
                        var.setOrder(orderNum);
                        var.setBlocknum(blocknum);
                        varList.add(var);
                        varNum++;
                        orderNum++;
                        String s="\t";

                        s+=var.getOrderUse();
                        s+=" = getelementptr [";
                        s+=x*y;
                        s+=" x i32], [";
                        s+=x*y;
                        s+=" x i32]* ";
                        s+=arrays.get(t).getBaseptr();
                        s+=", i32 0, i32 0\n";
                        Out+=s;
                     //   arrays.get(t).setBaseptr(var.getOrderUse());
                    }
                    var=new Var();
                    var.setOrder(orderNum);
                    var.setBlocknum(blocknum);
                    if(count==2){
                        var.setCalDimension(2);
                    }
                    else {
                        var.setCalDimension(1);
                    }
                    varNum++;
                    orderNum++;
                    varList.add(var);
                    s1="\t";
                    s1+=var.getOrderUse();
                    s1+=" = getelementptr i32, i32* ";
                    if(arrays.get(t).getBaseptr().contains("@")){
                        s1+=("%"+(orderNum-2));
                    }
                    else{
                        s1+=arrays.get(t).getBaseptr();
                    }

                    s1+=", i32 ";
                    if(varList.get(waiting).getType().equals("value")){
                        s1+=varList.get(waiting).getValue();
                    }
                    else{
                        s1+=varList.get(waiting).getOrderUse();
                    }
                    s1+="\n";
                    Out+=s1;
                }
                Varpos=varNum-1;
            }


        }

    }

    public void doFillofArray(int t){
        if(varList.get(t).getType().equals("value")){
            Var var =new Var();
            var.setValue(varList.get(t).getValue());
            var.setType("value");
            varList.add(var);
            varNum++;
        }
        else {
            Var var=new Var();
            var.setOrder(orderNum);

            String s1="\t";
            s1+=var.getOrderUse();
          //  s1+="987654321";
            s1+=" = load i32, i32* ";
            s1+=varList.get(t).getOrderUse();
            s1+="\n";
            Out+=s1;
            varList.add(var);
            varNum++;
            orderNum++;
        }
    }
    public void PrimaryExp(){
        if(word.getWord().equals("(")){
            word= scanner.scan();
            Exp();
            if(word.getWord().equals(")")){

                word= scanner.scan();
                return;
            }
            error();
            return;
        }
        else if(word.getType().equals("Ident")){
            String testArr=word.getWord();
            Lval();
            int waiting=Varpos;

            if(varList.get(waiting).getType().equals("value")){
                Var var =new Var();
                var.setValue(varList.get(waiting).getValue());
                var.setType("value");
                varList.add(var);
                varNum++;
            }
            else {
                int t=searchArr(varList.get(waiting).getWord());
                if(searchArr(varList.get(waiting).getWord())!=-1){

                    int x=0;int y=0;
                    Var var;
                    if(arrays.get(t).getFlag()!=1){
                        //    arrays.get(t).setFlag(1);
                        x=arrays.get(t).getX();
                        y=arrays.get(t).getY();
                        var=new Var();
                        var.setOrder(orderNum);
                        var.setBlocknum(blocknum);
                        varList.add(var);
                        varNum++;
                        orderNum++;
                        String s="\t";

                        s+=var.getOrderUse();
                        s+=" = getelementptr [";
                        s+=x*y;
                        s+=" x i32], [";
                        s+=x*y;
                        s+=" x i32]* ";
                        s+=arrays.get(t).getBaseptr();
                        s+=", i32 0, i32 0\n";
                        Out+=s;
                        //   arrays.get(t).setBaseptr(var.getOrderUse());
                    }

                    var=new Var();
                    var.setOrder(orderNum);
                    var.setBlocknum(blocknum);
                    varNum++;
                    orderNum++;
                    varList.add(var);
                    String s1;
                    s1="\t";
                    s1+=var.getOrderUse();
                    s1+=" = getelementptr i32, i32* ";
                    if(arrays.get(t).getBaseptr().contains("@")){
                        s1+=("%"+(orderNum-2));
                    }
                    else{
                        s1+=arrays.get(t).getBaseptr();
                    }

                    s1+=", i32 0";
//
//                    if(varList.get().getType().equals("value")){
//                        s1+=varList.get().getValue();
//                    }
//                    else{
//                        s1+=varList.get().getOrderUse();
//                    }
                    s1+="\n";
                    Out+=s1;
                }
                else{
                    int q=searchArr(testArr);

                    Var var=new Var();
                    var.setOrder(orderNum);
                    String s1="\t";
                    s1+=var.getOrderUse();
                //    System.out.println("12345678910");
                    //     s1+="12345678910";
                    s1+=" = load i32, i32* ";

                    s1+=varList.get(waiting).getOrderUse();
                    if(q!=-1){
                        var.setCalDimension(arrays.get(q).getDimension()-varList.get(waiting).getCalDimension());
                    }
                    s1+="\n";
                    Out+=s1;
                    varList.add(var);
                    varNum++;
                    orderNum++;
                }

            }

        }
        else{
            Number();
        }
    }
    public int searchFunList(String s){
        for (int i=0;i<funcList.size();i++){
            if(s.equals(funcList.get(i))){
                searchFuncPos=i;
                return i;
            }
        }
        return -1;

    }
    public void UnaryExp(){
        //System.out.println("tttt");
        flag=0;
        int z=0;
        while(word.getWord().equals("+")){
            word= scanner.scan();
        }
        if(word.getWord().equals("+")||word.getWord().equals("-")||word.getWord().equals("!")){
            while(word.getWord().equals("+")||word.getWord().equals("-")||word.getWord().equals("!")){
                if(word.getWord().equals("-")){
                    flag+=1;
                }
                if(word.getWord().equals("!")){
                    z++;
                }
                word= scanner.scan();
            }

            PrimaryExp();
            for (int i = 0; i < z; i++) {
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = icmp eq i32 ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+=", 0\n";
                varNum++;
                orderNum++;
                Out+=s1;

                Var var1=new Var();
                var1.setOrder(orderNum);
                varList.add(var1);
                String s2="\t";
                s2+=var1.getOrderUse();
                s2+=" = zext i1 ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s2+=varList.get(varNum-1).getValue();
                }
                else{
                    s2+=varList.get(varNum-1).getOrderUse();
                }
                s2+=" to i32\n";
                Out+=s2;
                varNum++;
                orderNum++;

            }
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

            if(z!=0){
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                String s1="\t";
                s1+=var.getOrderUse();
                s1+=" = icmp ne i32 ";
                if(varList.get(varNum-1).getType().equals("value")){
                    s1+=varList.get(varNum-1).getValue();
                }
                else{
                    s1+=varList.get(varNum-1).getOrderUse();
                }
                s1+=", 0\n";
                varNum++;
                orderNum++;
                Out+=s1;

            }
            flag=0;
        }
        else if(word.getType().equals("Ident")){
            System.out.println(funcList);
            System.out.println(word.getWord());
            Ident();
            if(word.getWord().equals("(")){
                scanner.goBack2();
                word= scanner.scan();
                if(searchFunList(word.getWord())==-1){
                    Ident();
                    if(word.getWord().equals("(")){
                        scanner.goBack2();
                        word= scanner.scan();
                        funcList.add(word.getWord());
                        System.out.println(funcList);
                        Ident();
                        word= scanner.scan();
                        if(word.getWord().equals(")")){
                            if(funcList.get(funcList.size()-1).equals("putint")||
                                    funcList.get(funcList.size()-1).equals("putch")||
                                    funcList.get(funcList.size()-1).equals("putarray")){
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
                            if(funcList.get(funcList.size()-1).equals("getint")||funcList.get(funcList.size()-1).equals("getch")){
                                error();
                            }
                            if(funcList.get(funcList.size()-1).equals("getarray")){
                                String s1="";
                                s1+="declare i32 @";
                                s1+=funcList.get(funcList.size()-1);
                                s1+="(i32*)\n";
                                Out=s1+Out;
                                Lval();
                                int t=varNum-1;
                                if(t==-1){
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
                                s2+=funcList.get(funcList.size()-1);
                                s2+="(i32* ";
                                s2+=varList.get(t).getOrderUse();
                                s2+=")\n";
                                Out+=s2;

                                if(word.getWord().equals(")")){
                                    word= scanner.scan();
                                    return;
                                }
                            }
                            else if(funcList.get(funcList.size()-1).equals("putarray")){
                                String s1="";
                                s1+="declare void @";
                                s1+="putarray";
                                s1+="(i32, i32*)\n";
                                Out=s1+Out;
                                //Lval();
                                int t=varNum-1;

                                ArrayList<Integer> waitnum=new ArrayList<>();

                                Exp();

                                waitnum.add(varNum-1);
                                while(word.getWord().equals(",")){
                                    word= scanner.scan();
                                    Exp();
                                    waitnum.add(varNum-1);
                                }
                                String s2="\tcall void @";
                                s2+="putarray";
                                s2+="(";
                                for (int i = 0; i < waitnum.size(); i++) {
                                    if(i==0) {
                                        s2 += "i32 ";
                                    }else{
                                        s2+="i32*";
                                    }

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

                                }
                                Out+=s2;
                                if(word.getWord().equals(")")){
                                    word= scanner.scan();
                                    return;
                                }
                                error();
                            }
                            else{
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
                    }
                    else{
                        scanner.goBack2();
                        word= scanner.scan();
                        PrimaryExp();
                    }

                }
                else{
                    int using=searchFunList(word.getWord());
                    Ident();
                    if(word.getWord().equals("(")){
                        word= scanner.scan();

                        if(word.getWord().equals(")")){
                            if(funcList.get(searchFuncPos).equals("putint")||funcList.get(searchFuncPos).equals("putch")||funcList.get(searchFuncPos).equals("putarray")){
                                error();
                            }

                            search(funcList.get(searchFuncPos));
                            if(varList.get(Varpos).getType().equals("void")){
                                String s2="\t";
                                s2+="call void @";
                                s2+=funcList.get(searchFuncPos);
                                s2+="()\n";
                                Out+=s2;
                            }
                            else{
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
                            }

                            word= scanner.scan();
                            return;
                        }
                        else{
                            if(funcList.get(searchFuncPos).equals("getint")||funcList.get(searchFuncPos).equals("getch")){
                                error();
                            }
                            if(funcList.get(searchFuncPos).equals("getarray")){
                                Lval();
                                int t=varNum-1;
                                Var var=new Var();
                                var.setOrder(orderNum);
                                varList.add(var);
                                orderNum++;
                                varNum++;
                                String s2="\t";
                                s2+=var.getOrderUse();
                                s2+=" = call i32 @";
                                s2+=funcList.get(funcList.size()-1);
                                s2+="(i32* ";
                                s2+=varList.get(t).getOrderUse();
                                s2+=")\n";
                                Out+=s2;

                                if(word.getWord().equals(")")){
                                    word= scanner.scan();
                                    return;
                                }
                            }
                            else if (funcList.get(searchFuncPos).equals("putarray")){
                                ArrayList<Integer> waitnum=new ArrayList<>();
                                System.out.println("4"+word.getWord());

                                Exp();
                                waitnum.add(varNum-1);

                                while(word.getWord().equals(",")){
                                    word= scanner.scan();
                                    Exp();
                                    waitnum.add(varNum-1);
                                }
                                System.out.println(waitnum);

                                String s2="\tcall void @";
                                s2+=funcList.get(searchFuncPos);
                                s2+="(";
                                for (int i = 0; i < waitnum.size(); i++) {

                                    if(i==0){
                                        s2+="i32 ";
                                    }
                                    else{
                                        s2+="i32* ";
                                    }
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

                                }
                                Out+=s2;
                                if(word.getWord().equals(")")){
                                    word= scanner.scan();
                                    return;
                                }
                            }
                            else if(funcList.get(searchFuncPos).equals("putint")||funcList.get(searchFuncPos).equals("putch")){
                                Exp();
                                int p=varNum-1;
                                String s2="\tcall void @";
                                s2+=funcList.get(using);
                                s2+="(i32 ";

                                if(varList.get(p).getType().equals("value")){
                                    s2+=varList.get(p).getValue();
                                }
                                else{
                                    s2+=varList.get(p).getOrderUse();
                                }
                                s2+=")\n";

                                Out+=s2;
                                if(word.getWord().equals(")")){
                                    word= scanner.scan();
                                    return;
                                }
                            }
                            else{
                                String waitingfunc=funcList.get(searchFuncPos);

                                ArrayList<Integer> waitnum=new ArrayList<>();
                                System.out.println("4"+word.getWord());

                                Exp();
                                System.out.println("ttttt"+word.getWord());
                                waitnum.add(varNum-1);

                                while(word.getWord().equals(",")){
                                    word= scanner.scan();
                                    Exp();
                                    waitnum.add(varNum-1);
                                }
                                System.out.println(funcList.get(searchFuncPos));
                                search(waitingfunc);
                                System.out.println(waitnum.size());
                                System.out.println(varList.get(Varpos).getParamList().size());

                                if(varList.get(Varpos).getParamList().size()!=waitnum.size()){
                                    error();
                                }
                                String s2;
                                if(varList.get(Varpos).getType().equals("void")){
                                    s2="\tcall void @";
                                }
                                else{
                                    Var var =new Var();
                                    var.setOrder(orderNum);
                                    var.setBlocknum(blocknum);
                                    orderNum++;
                                    varNum++;
                                    varList.add(var);
                                    s2=("\t"+var.getOrderUse());
                                    s2+=" = ";
                                    s2+="call i32 @";
                                }

                                s2+=funcList.get(using);
                                System.out.println(s2+"23");
                                s2+="(";
                                for (int i = 0; i < waitnum.size(); i++) {

                                    if(varList.get(Varpos).getParamList().get(i)==1){
//                                    if(varList.get(waitnum.get(i)).getCalDimension()!=1){
//                                        error();
//                                    }
                                        s2+="i32* ";
                                    }
                                    else if(varList.get(Varpos).getParamList().get(i)==2){
                                        s2+="i32 ";
                                    }
                                    else{
//                                    if(varList.get(waitnum.get(i)).getCalDimension()!=2){
//                                        error();
//                                    }
                                        s2+="i32* ";
                                    }

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

                                }
                                Out+=s2;
                                if(word.getWord().equals(")")){
                                    word= scanner.scan();
                                    return;
                                }
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
                scanner.goBack2();
                word= scanner.scan();
                PrimaryExp();
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
    public void RelExp(){
        AddExp();
        while (word.getWord().equals("<")||word.getWord().equals(">")||word.getWord().equals(">=")||word.getWord().equals("<=")){
            int relnum=varNum-1;
            if(word.getWord().equals("<")){
                word= scanner.scan();
                AddExp();
                fillIn(" =  icmp slt i32 ",relnum);
            }
            else if(word.getWord().equals(">")){
                word= scanner.scan();
                AddExp();
                fillIn(" =  icmp sgt i32 ",relnum);
            }
            else if(word.getWord().equals(">=")){
                word= scanner.scan();
                AddExp();
                fillIn(" =  icmp sge i32 ",relnum);
            }
            else{
                word= scanner.scan();
                AddExp();
                fillIn(" =  icmp sle i32 ",relnum);
            }
        }
    }
    public void fillIn(String str,int num){
        Var var=new Var();
        var.setOrder(orderNum);
        varList.add(var);
        String s1="\t";
        s1+=var.getOrderUse();
        s1+=str;
        if(varList.get(num).getType().equals("value")){
            s1+=varList.get(num).getValue();
        }
        else{
            s1+=varList.get(num).getOrderUse();
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
    public void EqExp(){
        RelExp();
        while (word.getWord().equals("==")||word.getWord().equals("!=")){
            int eqnum=varNum-1;
            if(word.getWord().equals("==")){
                word= scanner.scan();
                RelExp();
                fillIn(" =  icmp eq i32 ",eqnum);
            }
            else{
                word= scanner.scan();
                RelExp();
                fillIn(" =  icmp ne i32 ",eqnum);
            }

        }
    }
    public void LAndExp(){
        EqExp();
        judge();
        while (word.getWord().equals("&&")){
            int andnum=varNum-1;
            word= scanner.scan();
            EqExp();
            judge();
            fillIn(" =  and i1 ",andnum);

        }
    }
    public void judge(){
        Out=Out.substring(0,Out.length()-1);
        int z=Out.lastIndexOf("\n");
        String s_temp=Out.substring(z);
        if(!s_temp.contains("icmp") &&!s_temp.contains("or")&&!s_temp.contains("and")){
            Out+="\n";
            Var temp=new Var();
            temp.setOrder(orderNum);
            varList.add(temp);
            String s1="\t";
            s1+=temp.getOrderUse();
            s1+=" = icmp ne i32 ";
            s1+=varList.get(varNum-1).getOrderUse();
            s1+=", 0";
            s1+="\n";
            Out+=s1;
            varNum++;
            orderNum++;
        }
        else{
            Out+="\n";
        }
    }
    public void LorExp(){
        // System.out.println("1");

        LAndExp();
        judge();
        while(word.getWord().equals("||")){
            int ornum=varNum-1;
            word= scanner.scan();
            LAndExp();
            judge();
            fillIn(" =  or i1 ",ornum);
        }
    }
    public void Cond(){
        LorExp();
    }
    public void Stmt(){
        if(word.getWord().equals("return")){
            word= scanner.scan();
            if(word.getWord().equals(";")){
                Out+="\tret void\n";
            }
            else{
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
        }
        else if(word.getWord().equals("{")){
            Block();
        }
        else if(word.getWord().equals("if")){
            word= scanner.scan();
            if(word.getWord().equals("(")){
                word= scanner.scan();
                System.out.println("hahaha"+word.getWord());
                Cond();
                //这里得到%cond 改跳转了 br %cond %true
                String block1 = null,block2=null;
                int from,to1 = 0,to2 = 0,out;
                String store1=null;
                //br i1 label t01 label to2
                from=orderNum-1;
                if(word.getWord().equals(")")){
                    store1=Out;
                    word= scanner.scan();
                    String s1="\n";
                    Var var=new Var();
                    var.setOrder(orderNum);
                    to1=orderNum;
                    var.setType("to1");
                    varList.add(var);
                    varNum++;
                    orderNum++;
                    s1+=var.getOrder();
                    s1+=":";
                    //这里继续生成%cond+1块 定义 from to1 to2 out
                    Stmt();
                    int temp=Out.indexOf(store1);
                    if(temp==0){
                        block1=Out.substring(store1.length());
                    }
                    else {
                        block1=Out.substring(temp+store1.length());
                    }
                    //这里要加跳转到Out



                    block1=s1+block1;
                    System.out.println("6666666666666 "+word.getWord());
                    if(word.getWord().equals("else")){
                        //这里继续
                        word=scanner.scan();
                        String store2=Out;
                        String s2="\n";
                        Var var2=new Var();
                        var2.setOrder(orderNum);
                        to2=orderNum;
                        var2.setType("to2");
                        varList.add(var2);
                        varNum++;
                        orderNum++;
                        s2+=var2.getOrder();
                        s2+=":\n";
                        Stmt();
                        //加跳转

                        temp=Out.indexOf(store2);
                        if(temp==0){
                            block2=Out.substring(store2.length()+1);
                        }
                        else {
                            block2=Out.substring(temp+store2.length());
                        }

                        if(block2.charAt(0) != '\t'){
                            block2=s2+"\t"+block2;
                        }
                        else{
                            block2=s2+block2;
                        }

                    }
                    else{

                        String sss=word.getWord();
                        String store2=Out;
                        String s2="\n";
                        Var var2=new Var();
                        var2.setOrder(orderNum);
                        to2=orderNum;
                        var2.setType("to2");
                        varList.add(var2);
                        varNum++;
                        orderNum++;
                        s2+=var2.getOrder();
                        s2+=":\n";
                        block2="" ;
                        block2=s2+block2;

                    }

                }
                else{
                    error();
                }
                Var varout=new Var();
                varout.setOrder(orderNum);

                String goout="";
                goout+="\tbr label "+varout.getOrderUse();
                goout+="\n";
                String gotos="";
                gotos+="\tbr i1 %"+from;

                gotos+=",label %"+to1;

                gotos+=", label %"+to2;
                gotos+="\n";

              //  assert block1 != null;
              //  System.out.println(block1);
                if(block1==null){
                    block1="";
                    block1+=goout;
                }
                System.out.println(block1);
                if (!block1.contains("ret")&&
                        (!block1.contains("br label")||
                        (block1.contains("br label")&&block1.lastIndexOf("br label")<block1.length()-15))){
                    block1+=goout;
                }
                if(block2==null){
                    block2="";
                    block2+=goout;
                }
                if(!block2.contains("ret")&&
                        (!block2.contains("br label")||
                                (block2.contains("br label")&& block2.lastIndexOf("br label")<block2.length()-15))){
                    block2+=goout;
                }
                if(store1!=null){
                    int declare=Out.indexOf("define dso_local");
                    String declares=Out.substring(0,declare);
                    int s_declare=store1.indexOf("define dso_local");
                    String s_main=store1.substring(s_declare);
                    store1=declares+s_main;
                }
                else{
                    store1="";
                }

                if(block1.contains("ret")&&block2.contains("ret")){
                    Out=store1+gotos+block1+block2+"\n";
                }
                else{
                    varList.add(varout);
                    varNum++;
                    orderNum++;
                    Out=store1+gotos+block1+block2+varout.getOrder()+":\n";
                }


            }

        }
        else if(word.getWord().equals("while")){
            int tag=0;
            word= scanner.scan();
            System.out.println(word.getWord()+"888");
            if(word.getWord().equals("(")){
                int skiptowhile=orderNum;
                Out+="\tbr label %"+skiptowhile+"\n";
                word= scanner.scan();
                Var var=new Var();
                var.setOrder(orderNum);
                varList.add(var);
                continueStack.push(var);
                Out+=orderNum;
                Out+=":\n";
                orderNum++;
                varNum++;
                System.out.println(word.getWord()+"666");
                if(word.getType().equals("Number")){
                    int v=constNumber();
                    if(word.getWord().equals(")")){
                        Var temp=new Var();
                        temp.setOrder(orderNum);
                        varList.add(temp);
                        String s1="\t";
                        s1+=temp.getOrderUse();
                        s1+=" = icmp ne i32 "+v;

                        s1+=", 0";
                        s1+="\n";
                        Out+=s1;
                        varNum++;
                        orderNum++;
                    }
                    else{
                        scanner.goBack2();
                        word=scanner.scan();
                        Cond();
                    }
                }
                else{
                    System.out.println(word.getWord()+"321");
                    Cond();

                }
                //这里得到%cond 改跳转了 br %cond %true
                String block1 = null,block2=null;
                int from,to1 = 0,to2 = 0;
                String store1=null;
                //br i1 label t01 label to2
                from=orderNum-1;
                if(word.getWord().equals(")")){
                    store1=Out;
                    word= scanner.scan();
                    String s1="\n";
                    Var wvar=new Var();
                    wvar.setOrder(orderNum);
                    to1=orderNum;
                    wvar.setType("to1");
                    varList.add(wvar);
                    varNum++;
                    orderNum++;
                    s1+=wvar.getOrder();
                    s1+=":";
                    if(word.getWord().equals("continue")){
                        tag=1;
                    }
                    if(word.getWord().equals("break")){
                        tag=2;
                    }
                    Stmt();


                    continueStack.pop();
                    int temp=Out.indexOf(store1);
                    if(temp==0){
                        block1=Out.substring(store1.length());
                    }
                    else {
                        block1=Out.substring(temp+store1.length());
                        store1=Out.substring(0,temp+store1.length());
                    }
                    //这里要加跳转到Out
                    block1=s1+block1;

                    while(block1.indexOf("break")!=-1){
                        int pos=block1.indexOf("break");
                        String front=block1.substring(0,pos);
                        String behind=block1.substring(pos+5);
                        block1=front+"%"+orderNum+behind;
                    }

                }
                Var varout=new Var();
                varout.setOrder(orderNum);
                varList.add(varout);
                varNum++;
                orderNum++;
                String goToWhile="";
                goToWhile+="\tbr label %"+skiptowhile;
                goToWhile+="\n";
                String gotos="";
                gotos+="\tbr i1 %"+from;

                gotos+=",label %"+to1;

                gotos+=", label %"+varout.getOrder();
                gotos+="\n";

                assert block1 != null;
                if (!block1.contains("ret")&&(!block1.contains("br label")||(block1.contains("br label")&&block1.lastIndexOf("br label")<block1.length()-15))){
                    block1+=goToWhile;
                }
                if(!block1.contains(goToWhile)){
                    block1+=goToWhile;
                }
                Out=store1+gotos+block1+varout.getOrder()+":\n";

            }

        }
        else if(word.getWord().equals("break")){
            word=scanner.scan();
            String s1="";
            s1+="\tbr label ";
            s1+="break\n";
            Out+=s1;
        }
        else if(word.getWord().equals("continue")){
            word=scanner.scan();
            if(continueStack.empty()){
                error();
            }
            String s1="";
            s1+="\tbr label ";
            s1+=continueStack.peek().getOrderUse();
            s1+="\n";
            Out+=s1;
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
                        if(word.getWord().equals(";")){
                            word=scanner.scan();
                            return;
                        }
                        error();
                    }

                }

            }
            else if(word.getWord().equals("[")){
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
                        if(word.getWord().equals(";")){
                            word=scanner.scan();
                            return;
                        }
                        error();
                    }

                }
            }
            else{
                scanner.goBack2();
                word= scanner.scan();
                Exp();
                if(word.getWord().equals(";")){
                    word= scanner.scan();
                    System.out.println("88888888"+word.getWord());
                    return;
                }

                error();



            }
        }
        else {
            if(!word.getWord().equals(";")){
                Exp();
            }
            if(word.getWord().equals(";")){
                word= scanner.scan();
                return;
            }
            error();
        }
    }
    public void FuncType(){
        if(word.getWord().equals("int")||word.getWord().equals("void")){
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
                if(varList.get(i).getBlocknum()<=blocknum){
                    Varpos=i;
                    return true;
                }

            }
        }
        return false;
    }
    public void globalInitVal(){
        if (word.getWord().equals("{")){
            word=scanner.scan();
            if(!word.getWord().equals("}")){
                globalInitVal();
                while (word.getWord().equals(",")){
                    word= scanner.scan();
                    globalInitVal();
                }

                if(word.getWord().equals("}")){
                    word= scanner.scan();
                    i++;
                    j=0;
                }
            }
            else{
                word=scanner.scan();
                return;
            }
        }
        else{
            Exp();
            int nownum=varNum-1;
            System.out.println(nowarr);
            System.out.println(i+" "+j);
            if(arrtag==1){

                int p=i*arrays.get(arrays.size()-1).getY()+j;
                a[p]= varList.get(nownum).getValue();

                j++;
            }


        }
    }
    public void InitVal(){
        System.out.println("ccccccccccc"+word.getWord());
        if (word.getWord().equals("{")){
            word=scanner.scan();
            if(!word.getWord().equals("}")){
                InitVal();
                while (word.getWord().equals(",")){
                    word= scanner.scan();
                    InitVal();
                }

                if(word.getWord().equals("}")){
                    word= scanner.scan();
                    i++;
                    j=0;
                }
            }
            else{
                word= scanner.scan();
                return;
            }
        }
        else{
            Exp();
            int nownum=varNum-1;
            System.out.println(nowarr);
            System.out.println(i+" "+j);
            if(arrtag==1){
                Var var=new Var();
                if(arrays.get(arrays.size()-1).getFlag()!=1){
                 //   arrays.get(arrays.size()-1).setFlag(1);
                    int x=arrays.get(arrays.size()-1).getX();
                    int y=arrays.get(arrays.size()-1).getY();
                    var=new Var();
                    var.setOrder(orderNum);
                    var.setBlocknum(blocknum);
                    varList.add(var);
                    varNum++;
                    orderNum++;

                    String s="\t";
                    s+=var.getOrderUse();
                    s+=" = getelementptr [";
                    s+=x*y;
                    s+=" x i32], [";
                    s+=x*y;
                    s+=" x i32]* ";
                    s+=arrays.get(arrays.size()-1).getBaseptr();
                    s+=", i32 0, i32 0\n";
                    Out+=s;
                   // arrays.get(arrays.size()-1).setBaseptr(var.getOrderUse());
                }
                var=new Var();
                var.setBlocknum(blocknum);
                var.setOrder(orderNum);
                varList.add(var);
                Varpos=varNum;
                varNum++;
                orderNum++;
                String s="\t";
                s+=var.getOrderUse();
                s+=" = getelementptr i32, i32* ";
                if(arrays.get(arrays.size()-1).getBaseptr().contains("@")){
                    s+=("%"+(orderNum-2));
                }
                else{
                    s+=arrays.get(arrays.size()-1).getBaseptr();
                }

                s+=", i32 ";
                s+=i*arrays.get(arrays.size()-1).getY()+j;
                s+="\n";

                s+="\tstore i32 ";
                if(varList.get(nownum).getType().equals("value")){
                    s+=varList.get(nownum).getValue();
                }
                else{
                    s+=varList.get(nownum).getOrderUse();
                }
                s+=", i32* ";
                s+=var.getOrderUse();
                s+="\n";
                Out+=s;

                j++;
            }


        }

    }
    public void VarDef(){
        if(check==1){
            if((!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum)&&word.getType().equals("Ident")){
                String str=word.getWord();
                word= scanner.scan();
                System.out.println("fd"+word.getWord());
                int x = 0,y = 1;
                int arrnum=0;
                if(word.getWord().equals("[")){
                    arrtag=1;
                    while(word.getWord().equals("[")){
                        arrnum++;
                        word=scanner.scan();
                        if(arrnum==1){
                            x=ConstExp();
                        }
                        else {
                            y=ConstExp();
                        }
                        if(word.getWord().equals("]")){
                            word= scanner.scan();
                        }
                        else {
                            error();
                        }
                    }

                    System.out.println("22222222222222222");
                    Array arr=new Array();
                    arr.setDimension(arrnum);
                    arr.setWord(str);
                    arr.setX(x);
                    arr.setY(y);
                    {
                        Var var=new Var();

                        var.setBlocknum(blocknum);
                        var.setOrder(orderNum);
                        varList.add(var);
                        Varpos=varNum;
                        varNum++;
                        orderNum++;
                        String s="\t";
                        s+=var.getOrderUse();

                        s+=" = alloca [";
                        s+=x*y;
                        s+=" x i32]\n";
                        Out+=s;


                        var=new Var();
                        var.setBlocknum(blocknum);
                        var.setOrder(orderNum);
                        var.setWord(str);
                        varList.add(var);
                        //baseptr
                        nowarr=varNum;
                        arr.setBaseptr(varList.get(nowarr).getOrderUse());
                        arr.setBlocknum(blocknum);
                        arr.setFlag(1);
                        arrays.add(arr);

                        Varpos=varNum;
                        varNum++;
                        orderNum++;
                        s="\t";
                        s+=var.getOrderUse();
                        s+=" = getelementptr [";
                        s+=x*y;
                        s+=" x i32], [";
                        s+=x*y;
                        s+=" x i32]* %";
                        s+=orderNum-2;
                        s+=", i32 0, i32 0\n";

                        s+="\tcall void @memset(i32* %";
                        s+=orderNum-1;
                        s+=", i32 0, i32 ";
                        s+=x*y*4;
                        s+=")\n";
                        Out+=s;
                    }

                }
                else{
                    scanner.goBack2();
                    word=scanner.scan();

                    Var var=new Var();
                    var.setWord(word.getWord());
                    var.setBlocknum(blocknum);
                    var.setOrder(orderNum);

                    varList.add(var);
                    Varpos=varNum;
                    varNum++;
                    orderNum++;
                    Ident();
                    String s="\t";
                    s+=var.getOrderUse();

                    s+=" = alloca i32\n";
                    Out+=s;
                }
            }
            else{
                error();
            }
            if(word.getWord().equals("=")){
                word= scanner.scan();
                waiting=Varpos;
                InitVal();
                //这里 定义 int a=1; 把1 存储在a中
                if(arrtag==0){
                    String s1="\tstore i32 ";
                    if(varList.get(varNum-1).getType().equals("value")){
                        s1+=varList.get(varNum-1).getValue();
                    }
                    else{
                        s1+=varList.get(varNum-1).getOrderUse();
                    }
                    s1+=", i32* ";
                    s1+=varList.get(Varpos).getOrderUse();
                    s1+="\n";
                    Out+=s1;
                }

            }
            i=0;
            j=0;
        }
        else{
            String s1="";
            int num=0;
            int arrnum=0;
            if((!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum)&&word.getType().equals("Ident")){
                String str=word.getWord();
                word= scanner.scan();
                System.out.println("fd"+word.getWord());
                int x = 0,y = 1;
                if(word.getWord().equals("[")){
                    arrtag=1;
                    while(word.getWord().equals("[")){
                        arrnum++;
                        word=scanner.scan();
                        if(arrnum==1){
                            x=ConstExp();
                        }
                        else {
                            y=ConstExp();
                        }
                        if(word.getWord().equals("]")){
                            word= scanner.scan();
                        }
                        else {
                            error();
                        }
                    }

                    Array arr=new Array();
                    arr.setDimension(arrnum);
                    arr.setWord(str);
                    if(arrnum==2){
                        arr.setX(x);
                        arr.setY(y);
                    }
                    else {
                        arr.setX(x);
                        arr.setY(y);
                    }
                    String s="";
                    {
                        Var var=new Var();
                        var.setWord(str);
                        var.setBlocknum(blocknum);
                        var.setOrderUse("@"+str);

                        varList.add(var);

                        //baseptr
                        nowarr=varNum;
                        arr.setBaseptr(varList.get(nowarr).getOrderUse());
                        arr.setBlocknum(blocknum);
                        arrays.add(arr);

                        Varpos=varNum;
                        varNum++;
                        s="";
                        s+=var.getOrderUse();
                        s+=" =  dso_local global [";
                        s+=x*y;
                        s+=" x i32] ";

                    }
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        waiting=Varpos;
                        s+="[";
                        a=new int[arr.getY()*arr.getX()];
                        globalInitVal();
                        for (int k = 0; k < a.length-1; k++) {
                            s+="i32 ";
                            s+=a[k];
                            s+=",";
                        }
                        s+="i32 ";
                        s+=a[a.length-1];
                        s+="]\n";

                        //这里 定义 int a=1; 把1 存储在a中
                    }
                    else {
                        s+="zeroinitializer\n";
                    }
                    Out+=s;

                }
                else{
                    scanner.goBack2();
                    word=scanner.scan();
                    Var var=new Var();
                    var.setWord(word.getWord());
                    var.setBlocknum(blocknum);
                    var.setOrderUse("@"+word.getWord());

                    varList.add(var);
                    Varpos=varNum;
                    varNum++;

                    s1+=varList.get(Varpos).getOrderUse();
                    s1+=" = dso_local global i32 ";

                    Ident();
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        waiting=Varpos;
                        num=ConstInitVal();
                        //这里 定义 int a=1; 把1 存储在a中



                    }
                    s1+=num;
                    s1+="\n";
                    Out=s1+Out;
                }

            }
            else{
                error();
            }


        }
        arrtag=0;
        i=0;
        j=0;
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

    public int ConstExp(){
        return constAddExp();
    }

    public int ConstInitVal() {
        if (word.getWord().equals("{")) {
            word = scanner.scan();
            if (!word.getWord().equals("}")) {
                ConstInitVal();
                while (word.getWord().equals(",")) {
                    word = scanner.scan();
                    ConstInitVal();
                }
                if (word.getWord().equals("}")) {
                    word = scanner.scan();
                }
                error();
            }
        }
        else{
            return ConstExp();
        }
        return 0;

    }
    public void ConstDef(){
        if(check==1){
            if(!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum&&word.getType().equals("Ident")){
                Var var=new Var();
                var.setWord(word.getWord());
                var.setBlocknum(blocknum);
                var.setType("value");
                varList.add(var);
                Varpos=varNum;
                varNum++;
                Ident();

                int x = 0,y = 1;
                int arrnum=0;
                int waiting=Varpos;
                if(word.getWord().equals("[")){
                    arrtag=1;
                    while(word.getWord().equals("[")){
                        arrnum++;
                        word=scanner.scan();
                        if(arrnum==1){
                            x=ConstExp();
                        }
                        else {
                            y=ConstExp();
                        }
                        if(word.getWord().equals("]")){
                            word= scanner.scan();
                        }
                        else {
                            error();
                        }
                    }

                    Array arr=new Array();
                    arr.setDimension(arrnum);
                    arr.setWord(var.getWord());
                    arr.setType("Const");
                    if(arrnum==2){
                        arr.setX(x);
                        arr.setY(y);
                    }
                    else {
                        arr.setX(x);
                        arr.setY(y);
                    }
                    {


                    }
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        a=new int[arr.getY()*arr.getX()];
                        globalInitVal();
                        for (int k = 0; k < a.length-1; k++) {

                            varList.get(waiting).arr_value[k]=a[k];
                        }
                        //这里 定义 int a=1; 把1 存储在a中
                    }

                }
                else{
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        int value=ConstInitVal();
                        var.setValue(value);
                        varList.add(var);

                        return;

                    }
                    error();
                }
            }
            else{
                error();
            }
        }
        else{
            Var var=new Var();
            String s1="";
            int num=0;
            int arrnum=0;
            if((!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum)&&word.getType().equals("Ident")){
                String str=word.getWord();
                word= scanner.scan();
                int x = 0,y = 1;
                if(word.getWord().equals("[")){
                    arrtag=1;
                    while(word.getWord().equals("[")){
                        arrnum++;
                        word=scanner.scan();
                        if(arrnum==1){
                            x=ConstExp();
                        }
                        else {
                            y=ConstExp();
                        }
                        if(word.getWord().equals("]")){
                            word= scanner.scan();
                        }
                        else {
                            error();
                        }
                    }

                    Array arr=new Array();
                    arr.setDimension(arrnum);
                    arr.setWord(str);
                    if(arrnum==2){
                        arr.setX(x);
                        arr.setY(y);
                    }
                    else {
                        arr.setX(x);
                        arr.setY(y);
                    }
                    String s="";
                    {

                        var.setWord(str);
                        var.setBlocknum(blocknum);
                        var.setOrderUse("@"+str);

                        varList.add(var);

                        //baseptr
                        nowarr=varNum;
                        arr.setBaseptr(varList.get(nowarr).getOrderUse());
                        arr.setBlocknum(blocknum);
                        arrays.add(arr);

                        Varpos=varNum;
                        varNum++;
                        s="";
                        s+=var.getOrderUse();
                        s+=" =  dso_local constant [";
                        s+=x*y;
                        s+=" x i32] ";

                    }
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        waiting=Varpos;
                        s+="[";
                        a=new int[arr.getY()*arr.getX()];
                        globalInitVal();
                        for (int k = 0; k < a.length-1; k++) {
                            s+="i32 ";
                            s+=a[k];
                            s+=",";
                        }
                        var.arr_value=a;
                        s+="i32 ";
                        s+=a[a.length-1];
                        s+="]\n";

                        //这里 定义 int a=1; 把1 存储在a中
                    }
                    else {
                        s+="zeroinitializer\n";
                    }
                    Out+=s;

                }
                else{
                    scanner.goBack2();
                    word=scanner.scan();
                    var=new Var();
                    var.setWord(word.getWord());
                    var.setType("value");
                    var.setBlocknum(blocknum);
                    var.setOrderUse("@"+word.getWord());

                    varList.add(var);
                    Varpos=varNum;
                    varNum++;


                    Ident();
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        waiting=Varpos;
                        num=ConstInitVal();
                        varList.get(waiting).setValue(num);
                        //这里 定义 int a=1; 把1 存储在a中
                    }

                }

            }
            else{
                error();
            }


        }
        arrtag=0;

        i=0;j=0;
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
            belong=varNum;
            blocknum++;
            Out+="\n";
            word=scanner.scan();

            while(!word.getWord().equals("}")){
                BlockItem();
            }

            if(word.getWord().equals("}")){
                for (int i=varList.size()-1;i>=0;i--){
                    if(varList.get(i).getBlocknum()==blocknum){
                        varList.get(i).setBlocknum(100000);
                    }
                    if(varList.get(i).getBlocknum()==blocknum){
                        break;
                    }
                }
                blocknum--;
                word=scanner.scan();
                return;
            }
            error();
        }
    }
    public int FuncFParam(){
        Btype();
        String w=word.getWord();
        Ident();
        System.out.println("abcd"+word.getWord());
        if(word.getWord().equals("[")){

            Out+="i32* ";
            Var var=new Var();
            var.setOrder(orderNum);
            var.setWord(w);
            var.setBlocknum(blocknum);
            varList.add(var);
            orderNum++;
            varNum++;
            Out+=var.getOrderUse();



            word= scanner.scan();

            int cou=1;

            if(word.getWord().equals("]")){
                word= scanner.scan();

                if (word.getWord().equals("[")){
                    word= scanner.scan();
                    cou++;
                    int num=constAddExp();
                    pass=num;
                    System.out.println(word.getWord()+"44444");
                    if (word.getWord().equals("]")){
                        word= scanner.scan();
                    }
                    else{
                        error();
                    }
                    return 3;
                }


            }
            else {
                error();
            }
            return 1;
        }
        else {
            Out+="i32 ";
            Var var=new Var();
            var.setOrder(orderNum);
            var.setWord(w);
            var.setBlocknum(blocknum);
            varList.add(var);
            orderNum++;
            varNum++;
            Out+=var.getOrderUse();

            return 2;
        }
    }
    public void FuncFParams(int t){
        paramList=new ArrayList<>();

        int i=1;
        int a=FuncFParam();
        paramList.add(a);
        while(word.getWord().equals(",")){
            Out+=",";
            word= scanner.scan();
            int b=FuncFParam();
            paramList.add(b);
        }
        varList.get(t).setParamList(paramList);
        Var var=new Var();
        var.setWord("null");
        var.setOrder(orderNum);
        varList.add(var);
        orderNum++;
        varNum++;
    }
    public int searchOrder(int t){
        for (int i = varList.size()-1; i >=0; i--) {
            if(varList.get(i).getOrder()==0){
                for (int k = i; k < varList.size(); k++) {
                    if(varList.get(k).getOrder()==t){
                        return k;
                    }
                }
            }
        }
        return -1;
    }
    public void FuncDef(){
        Out+="define dso_local ";
        String functype=word.getWord();
        FuncType();
        String funcname=word.getWord();
        funcList.add(funcname);
        if(functype.equals("int")){
            Out+="i32 ";
        }
        else{
            Out+="void ";
        }

        Var var=new Var();
        var.setWord(funcname);
        var.setType(functype);
      //  var.setOrder(orderNum);
        var.setBlocknum(blocknum);
        varList.add(var);
        varNum++;

        Out+=("@"+funcname+"(");

        Ident();
        int t=varNum-1;
        if(word.getWord().equals("(")){
            blocknum++;
            word= scanner.scan();
            if(!word.getWord().equals(")")){

                FuncFParams(t);

                int p=varNum-2;

                int l=varList.get(t).getParamList().size();
                for (int k = 0; k <l; k++) {
                    if(varList.get(t).getParamList().get(k)==1){
                        int x=searchOrder(k);
                        String w=varList.get(x).getWord();
                        System.out.println(w+"4444444444444444444");
                        Array array=new Array();
                        array.setWord(w);
                        Var var1=new Var();
                        var1.setOrder(orderNum);
                        var1.setBlocknum(blocknum);
                        var1.setWord(w);
                        varList.add(var1);
                        orderNum++;varNum++;
                        funcValStart+=("\t"+var1.getOrderUse()+" = alloca i32*\n");
                        funcValStart+=("\tstore i32* %"+(k)+", i32* * "+var1.getOrderUse()+"\n");

                        Var var2=new Var();
                        var2.setOrder(orderNum);
                        var2.setBlocknum(blocknum);
                        varList.add(var2);
                        orderNum++;varNum++;
                        funcValStart+=("\t"+var2.getOrderUse()+" = load i32* , i32* * "+var1.getOrderUse()+"\n");
                        array.setBaseptr(var2.getOrderUse());
                        array.setFlag(1);
                        array.setDimension(1);
                        array.setY(1);
                        arrays.add(array);
                    }
                    else if(varList.get(t).getParamList().get(k)==2){
                        int x=searchOrder(k);
                        String w=varList.get(x).getWord();

                        Var var1=new Var();
                        var1.setOrder(orderNum);
                        var1.setWord(w);
                        var1.setBlocknum(blocknum);
                        varList.add(var1);
                        orderNum++;varNum++;
                        funcValStart+=("\t"+var1.getOrderUse()+" = alloca i32\n");
                        funcValStart+=("\tstore i32 %"+(k)+", i32* "+var1.getOrderUse()+"\n");
                    }
                    else{
                        int x=searchOrder(k);
                        String w=varList.get(x).getWord();
                        Array array=new Array();
                        array.setWord(w);
                        Var var1=new Var();
                        var1.setWord(w);
                        var1.setOrder(orderNum);
                        var1.setBlocknum(blocknum);
                        varList.add(var1);
                        orderNum++;varNum++;
                        funcValStart+=("\t"+var1.getOrderUse()+" = alloca i32*\n");
                        funcValStart+=("\tstore i32* %"+(k)+", i32* * "+var1.getOrderUse()+"\n");

                        Var var2=new Var();
                        var2.setOrder(orderNum);
                        var2.setBlocknum(blocknum);
                        varList.add(var2);
                        orderNum++;varNum++;
                        funcValStart+=("\t"+var2.getOrderUse()+" = load i32* , i32* * "+var1.getOrderUse());
                        array.setBaseptr(var2.getOrderUse());
                        array.setFlag(1);
                        array.setDimension(2);
                        array.setY(pass);
                        arrays.add(array);
                    }
                }


            }
            else{
                var=new Var();
                var.setWord("null");
                var.setOrder(orderNum);
                varList.add(var);
                orderNum++;
                varNum++;

            }
            if(word.getWord().equals(")")){
                blocknum--;
                Out+="){\n";
                Out+=funcValStart;
                funcValStart="";
                word= scanner.scan();
                Block();
                System.out.println("end "+word.getWord());
                if(functype.equals("void")){
                    Out+="\tret void\n";
                }
                Out+="}\n";

                if(word.getType().equals("Ident")){
                    orderNum=0;
                    CompUnit();
                }
                return;
            }

            error();
        }
        else{
            error();
        }

    }
    public void CompUnit(){
        word= scanner.scan();
        word= scanner.scan();
        while (!word.getWord().equals("(")){
            scanner.goBack3();
            word= scanner.scan();
            Decl();
            word= scanner.scan();
            word= scanner.scan();
        }

        scanner.goBack3();
        word=scanner.scan();
        check=1;
        FuncDef();
        for (int i=0;i<varList.size();i++){
            if (varList.get(i).getWord().equals("get")){
                System.out.println(varList.get(i).getOrderUse()+" "+varList.get(i).getBlocknum());
            }
        }
    }
    public static void main(String[] args) {
        String path=args[0];
        String output=args[1];
//        String path="a.txt";
//        String output="b.txt";

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
        System.out.println(filecontent);
        main.scanner =new Scanner(filecontent);
        main.word= main.scanner.scan();

        main.varList=new ArrayList<>();
        main.funcList=new ArrayList<>();
        main.Out="";

        main.varNum=0;
        main.orderNum=0;
        main.blocknum=0;
        main.check=0;
        main.a=new int[]{0};
        main.arrtag=0;
        main.continueStack=new Stack<>();
        main.breakStack=new Stack<>();
        main.arrays=new ArrayList<>();
        main.paramList=new ArrayList<>();
        main.i=0;main.j=0;
        main.funcValStart="";
        main.CompUnit();
        main.Out="declare void @memset(i32*  ,i32 ,i32 )\n"+main.Out;
        System.out.println(main.Out);
        pw.print(main.Out);
        pw.flush();
        pw.close();


    }
}