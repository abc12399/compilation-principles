import org.ietf.jgss.Oid;

import javax.swing.*;
import java.io.*;

import java.net.MalformedURLException;
import java.rmi.server.ExportException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Condition;

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

    public int blocknum;

    public int belong;

    public int check;
    public ArrayList<Var> blocklist;

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
                System.out.println(")");
                word= scanner.scan();
                System.out.println("primary:"+num);
                System.out.println(word.getWord());
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
                if(varList.get(Varpos).getType().equals("value")){
                    word=scanner.scan();
                    System.out.println("aaaaaaaaaaaaaaaaa"+varList.get(Varpos).getValue());
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
        //System.out.println("tttt");
        System.out.println("xzxxx"+word.getWord());
        while(word.getWord().equals("+")||word.getWord().equals("-")){

            if(word.getWord().equals("-")){
                flag+=1;
            }
            word= scanner.scan();

        }

        int num=constPrimaryExp();

        System.out.println("flag:"+flag+"num:"+num);
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
        System.out.println("xzx"+sum);
        System.out.println(word.getWord());
        while(word.getWord().equals("*")||word.getWord().equals("/")||word.getWord().equals("%")){
            System.out.println("in *");
            char[] arr=word.getWord().toCharArray();

            word= scanner.scan();

            int num=constUnaryExp();

            if(num==0&&arr[0]=='/'){
                error();
            }
            else{
                sum=Operate(sum,num,arr[0]);
                System.out.println(sum);
            }

        }
        System.out.println("sum"+sum);
        return sum;
    }
    public int constAddExp(){

        int sum=constMulExp();

        if (word.getWord().equals("+")||word.getWord().equals("-")){
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
    public void Lval(){
        if (!search(word.getWord())){
            error();
        }
        else {
            if(check==0 && ! word.getType().equals("Const")){
                error();
            }
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
            if(varList.get(waiting).getType().equals("value")){
                Var var =new Var();
                var.setValue(varList.get(waiting).getValue());
                var.setType("value");
                varList.add(var);
                varNum++;
            }
            else {
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
        flag=0;
        int z=0;
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
            System.out.println("gvgvg"+flag);
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
    public void RelExp(){
        System.out.println("dfs");

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
        while (word.getWord().equals("&&")){
            int andnum=varNum-1;
            word= scanner.scan();
            EqExp();
            fillIn(" =  and i1 ",andnum);

        }
    }
    public void LorExp(){
        // System.out.println("1");

        LAndExp();

        while(word.getWord().equals("||")){
            int ornum=varNum-1;
            word= scanner.scan();
            LAndExp();
            fillIn(" =  or i1 ",ornum);
        }
    }
    public void Cond(){
        LorExp();
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
        else if(word.getWord().equals("{")){
            Block();
        }
        else if(word.getWord().equals("if")){
            word= scanner.scan();
            if(word.getWord().equals("(")){
                //  System.out.println("afdsf");
                word= scanner.scan();
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
                    System.out.println(word.getWord()+"fda54444444");
                    Stmt();
                    int temp=Out.indexOf(store1);
                    if(temp==0){
                        block1=Out.substring(store1.length());
                    }
                    else {
                        block1=Out.substring(temp+store1.length());
                        store1=Out.substring(0,temp+store1.length());
                    }
                    System.out.println(block1);
                    //这里要加跳转到Out



                    block1=s1+block1;
                    // System.out.println(block1);
                    System.out.println(word.getWord());
                    if(word.getWord().equals("else")){
                        //这里继续
                        word=scanner.scan();
                        String store2=Out;
                        String s2="\n";
                        Var var2=new Var();
                        var2.setOrder(orderNum);
                        to2=orderNum;
                        System.out.println("to2"+orderNum);
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
                            store2=Out.substring(0,temp+store2.length());

                        }

                        System.out.println(block2);
                        System.out.println("there" );

                        //System.out.println(block2);
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
                Var varout=new Var();
                varout.setOrder(orderNum);
                varList.add(varout);
                varNum++;
                orderNum++;
                String goout="";
                goout+="\tbr label "+varout.getOrderUse();
                goout+="\n";
                String gotos="";
                gotos+="\tbr i1 %"+from;

                gotos+=",label %"+to1;

                gotos+=", label %"+to2;
                gotos+="\n";

                assert block1 != null;
                if (!block1.contains("ret")){
                    block1+=goout;
                }
                if(!block2.contains("ret")){
                    block2+=goout;
                }

                Out=store1+gotos+block1+block2+varout.getOrder()+":\n";

            }

        }
        else if(word.getWord().equals("while")){
            word= scanner.scan();
            int skiptowhile=orderNum;
            if(word.getWord().equals("(")){
                //  System.out.println("afdsf");
                word= scanner.scan();
                Out+=orderNum;
                Out+=":\n";
                Cond();
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
                    System.out.println(word.getWord()+"fda54444444");
                    Stmt();
                    int temp=Out.indexOf(store1);
                    if(temp==0){
                        block1=Out.substring(store1.length());
                    }
                    else {
                        block1=Out.substring(temp+store1.length());
                        store1=Out.substring(0,temp+store1.length());
                    }
                    System.out.println(block1);
                    //这里要加跳转到Out
                    block1=s1+block1;
                    // System.out.println(block1);
                    System.out.println(word.getWord());

                }
                Var varout=new Var();
                varout.setOrder(orderNum);
                varList.add(varout);
                varNum++;
                orderNum++;
                String goToWhile="";
                goToWhile+="\tbr label "+skiptowhile;
                goToWhile+="\n";
                String gotos="";
                gotos+="\tbr i1 %"+from;

                gotos+=",label %"+to1;

                gotos+=", label %"+varout.getOrder();
                gotos+="\n";

                assert block1 != null;
                if (!block1.contains("ret")){
                    block1+=goToWhile;
                }
                Out=store1+gotos+block1+varout.getOrder()+":\n";

            }

        }
        else if(word.getType().equals("Ident")){
            System.out.println(word.getWord());
            word= scanner.scan();
            if(word.getWord().equals("=")){
                scanner.goBack2();
                word= scanner.scan();

                if(!search(word.getWord())){
                    error();
                }
                else {
                    Lval();
                    System.out.println(word.getWord()+"lval");
                    int waiting=Varpos;
                    if(varList.get(waiting).getType().equals("Const")){
                        error();
                    }
                    if(word.getWord().equals("=")){
                        word= scanner.scan();
                        Exp();
                        System.out.println("exp "+word.getWord());
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
                System.out.println(word.getWord());
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
                if(varList.get(i).getBlocknum()<=blocknum){
                    Varpos=i;
                    return true;
                }

            }
        }
        return false;
    }
    public void InitVal(){
        Exp();
    }
    public void VarDef(){
        if(check==1){
            if((!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum)&&word.getType().equals("Ident")){
                Var var=new Var();
                var.setWord(word.getWord());
                var.setBlocknum(blocknum);
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
        else{
            String s1="";
            int num=0;
            if((!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum)&&word.getType().equals("Ident")){
                Var var=new Var();
                var.setWord(word.getWord());
                var.setBlocknum(blocknum);
                var.setOrderUse("@"+word.getWord());

                varList.add(var);
                Varpos=varNum;
                varNum++;

                s1+=varList.get(Varpos).getOrderUse();
                s1+=" = dso_local global i32 ";
            }
            else{
                error();
            }
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

    public int ConstExp(){
        return constAddExp();
    }

    public int ConstInitVal(){
        return ConstExp();
    }
    public void ConstDef(){
        if(!search(word.getWord())||varList.get(Varpos).getBlocknum()<blocknum&&word.getType().equals("Ident")){
            Var var=new Var();
            var.setWord(word.getWord());
            var.setBlocknum(blocknum);
            var.setType("value");

            Varpos=varNum;
            varNum++;
            Ident();
            if(word.getWord().equals("=")){
                word= scanner.scan();
                waiting=Varpos;
                int value=ConstInitVal();

                var.setValue(value);
                varList.add(var);

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
            System.out.println(word.getWord());
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
                System.out.println(word.getWord());
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
    public void FuncDef(){
        Out+="define dso_local i32 @main(){";
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

                Out+="}";
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
        while(!word.getWord().equals("main")){
            scanner.goBack2();
            word= scanner.scan();
            Decl();
            word= scanner.scan();
        }
        scanner.goBack2();
        word=scanner.scan();
        check=1;
        FuncDef();
        return;
    }
    public static void main(String[] args) {
//        String path=args[0];
//        String output=args[1];
        String path="a.txt";
        String output="b.txt";

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
        main.CompUnit();

        System.out.println(main.Out);
        pw.print(main.Out);
        pw.flush();
        pw.close();


    }
}