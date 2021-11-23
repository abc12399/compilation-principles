import javax.swing.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Stack;

public class Main {

    public Word word;
    public Scanner scanner;
    public int number;

    public String Out;
    public ArrayList<Var> varList;
    public int VarNum;

    public int Varpos;

    public int waiting;

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
    public void FuncType(){
        if(word.getWord().equals("int")){
            word= scanner.scan();
            return;
        }
        else{
            error();
        }
    }

    public int number(){
        if(word.getType().equals("Number")){
            number= Integer.valueOf(word.getWord());
            word= scanner.scan();
            return number;
        }
        error();
        return 0;
    }

    public int PrimaryExp(){
        if(word.getWord().equals("(")){
        //    System.out.println("(");
            word= scanner.scan();

            int num=Exp();
            if(word.getWord().equals(")")){
              //  System.out.println(")");
                word= scanner.scan();
             //   System.out.println("primary:"+num);
            //    System.out.println(word.getWord());
                return num;
            }
            error();
            return 0;
        }
        else if(word.getType().equals("Ident")){
            System.out.println("in pri");
            Lval();
            System.out.println("afet");
            System.out.println(varList.get(Varpos).getValue());
            return varList.get(Varpos).getValue();
        }
        else{
            return number();
        }
    }

    public int UnaryExp(){
        //System.out.println("tttt");
        if(word.getType().equals("Ident")){
            Ident();
            word=scanner.scan();
            if(word.getWord().equals("(")){
                varList.get(Varpos).setType("Func");
                word=scanner.scan();
                if (word.getWord().equals(")")){
                    String s="\t";
                    s+=;
                    s+=", i32* %";
                    s+=VarNum;
                    s+="\n";
                    Out+=s;
                }
            }
            else{
                return varList.get(Varpos).getValue();
            }
        }
        else{
            while(word.getWord().equals("+")||word.getWord().equals("-")){

                if(word.getWord().equals("-")){
                    flag+=1;
                }
                word= scanner.scan();
            }
            int num=PrimaryExp();
            //   System.out.println("flag:"+flag+"num:"+num);
            if(flag%2==0){
                flag=0;
                return num;
            }
            else{
                flag=0;
                return num*(-1);
            }
        }
    }
    public int MulExp(){
        int sum=UnaryExp();
        while(word.getWord().equals("*")||word.getWord().equals("/")||word.getWord().equals("%")){
         //   System.out.println("in *");
            char[] arr=word.getWord().toCharArray();

            word= scanner.scan();

            int num=UnaryExp();
            System.out.println("here /"+num);
            if(num==0&&arr[0]=='/'){
                error();
            }
            else{
                sum=Operate(sum,num,arr[0]);
            }

        }
       // System.out.println("sum"+sum);
        return sum;
    }
    public int AddExp(){
        int sum=MulExp();
        while(word.getWord().equals("+")||word.getWord().equals("-")){
            int temp=0;
            while (word.getWord().equals("+")||word.getWord().equals("-")){
                if(word.getWord().equals("-")){
                    temp++;
                }
                word= scanner.scan();
            }
            int num=MulExp();
            if(temp%2==0){
                sum=Operate(sum,num,'+');
            }
            else{
                sum=Operate(sum,num,'-');
            }
        }
        return sum;
    }
    public int Exp(){
        int num=AddExp();
        return num;
    }

    public void Lval(){
        if(!search(word.getWord())){
            error();
        }
        Ident();
    }
    public void Stmt(){
        if(word.getWord().equals("return")){
            word= scanner.scan();
            int num=Exp();

            String s="\tstore i32 ";
            s+=num;
            s+=", i32* %";
            s+=VarNum;
            s+="\n";
            Out+=s;

            if(word.getWord().equals(";")){
                String s1="\tret i32 %";
                s1+=VarNum;
                s1+="\n";
                Out+=s1;

                word= scanner.scan();
                return;
            }
            error();
        }
        else{
            if(word.getType().equals("Ident")){
                Lval();
                if(word.getWord().equals("=")){

                    waiting=Varpos;
                    if(varList.get(waiting).getType().equals("Const")){
                        error();
                    }
                    word= scanner.scan();
                    int num=Exp();
                    varList.get(waiting).setValue(num);


                    if(word.getWord().equals(";")){
                        word= scanner.scan();
                        return;
                    }
                    error();
                }
                else{
                    //注意 这里有一种exp仅仅为Lval的情况 需要额外处理
                    return;
                }
            }
            else{
                if(!word.getWord().equals(";")){
                    Exp();
                }
                else{
                    word= scanner.scan();
                    return;
                }
            }
        }

    }



    public int ConstExp(){
        int num=AddExp();
        return num;
    }

    public void ConstInitVal(){
        int num=ConstExp();
        varList.get(waiting).setValue(num);
        String s="\tstore i32 ";
        s+=num;
        s+=", i32* ";
        s+=varList.get(waiting).getOrderUse();
        s+="\n";
        Out+=s;
    }
    public void ConstDef(){
        System.out.println(word.getWord()+"constdef");
        Ident();
        varList.get(Varpos).setType("Const");
        System.out.println(word.getWord()+"ds ");
        waiting=Varpos;
        if(word.getWord().equals("=")){
            word= scanner.scan();
            ConstInitVal();
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
    public void ConstDecl(){
        if(word.getWord().equals("const")){
            word= scanner.scan();
            System.out.println(word.getWord()+"这里应该是int");
            Btype();
            System.out.println(word.getWord()+"这里应该是a");
            ConstDef();
            System.out.println(word.getWord()+"zhel");
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

    public void InitVal(){
        System.out.println("11");
        int num=Exp();
        System.out.println("sa"+num);
        System.out.println(varList.get(waiting).getWord());
        varList.get(waiting).setValue(num);
        String s="\tstore i32 ";
        s+=num;
        s+=", i32* ";
        s+=varList.get(waiting).getOrderUse();
        s+="\n";
        Out+=s;
    }


    public boolean search(String str){
        for (int i = 0; i <varList.size(); i++) {
            if(str.equals(varList.get(i).getWord())){
                Varpos=i;
                return true;
            }
        }
        return false;
    }

    public void Ident(){
        if(word.getType().equals("Ident")){
            System.out.println(word.getWord()+"inident");
            if(!search(word.getWord())){
                Var var=new Var();
                var.setWord(word.getWord());
                var.setOrder(VarNum);

                varList.add(var);
                Varpos=VarNum;
                VarNum++;

                String s="\t";
                s+=var.getOrderUse();
                s+=" = alloca i32\n";
                Out+=s;
            }
            System.out.println(word.getWord());
            word= scanner.scan();
            return;
        }
        else {
            error();
        }
    }

    public void VarDef(){
        Ident();
        if(word.getWord().equals("=")){
            word= scanner.scan();
            waiting=Varpos;
            InitVal();
            return;
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
    public void Ident_Main(){
        if(word.getWord().equals("main")){
            Var var=new Var();
            var.setWord("main");
            varList.add(var);

            VarNum++;
            word=scanner.scan();
            return;
        }
    }
    public void Funcdef(){
        Out+="define dso_local i32 @main()";
        FuncType();
        Ident_Main();
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
        Funcdef();
        return;
    }
    public static void main(String[] args) {
        System.out.println("123");

//        String path=args[0];
  //      String output=args[1];

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
        main.scanner =new Scanner(filecontent);
        main.word= main.scanner.scan();

        main.varList=new ArrayList<>();
        main.Out="";
        main.CompUnit();

        System.out.println(main.number);
        System.out.println("1");

        System.out.println(main.Out);








//        if(main.flag%2==0){
//
//        }

        pw.flush();
        pw.close();


    }
}