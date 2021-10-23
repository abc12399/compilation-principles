import java.io.*;

import java.util.Stack;

public class Main {

    public Word word;
    public Scanner scanner;
    public int number;



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
    public void Ident(){
        if(word.getWord().equals("main")){
            word= scanner.scan();
            return;
        }
        else {
            error();
        }
    }

    public int number(){
        if(word.getType().equals("Number")){
            number= Integer.valueOf(word.getWord());
          //  System.out.println(word.getWord());
          //  System.out.println("555");
       //     System.out.println(number);
            word= scanner.scan();
          //  System.out.println("llll");
            System.out.println(word.getWord());
            return number;
        }
        error();
        return 0;
    }

    public int PrimaryExp(){
      //  System.out.println("asd");
      //  System.out.println(word.getWord());
        if(word.getWord().equals("(")){
           // System.out.println("aa");
            word= scanner.scan();
            System.out.println(word.getWord()+" ");
            int num=Exp();
           // System.out.println("tre");
           // System.out.println(num);
          //  System.out.println(word.getWord());
            if(word.getWord().equals(")")){
                word= scanner.scan();
                System.out.println(word.getWord()+" ");
                return num;
            }
            error();
            return 0;
        }
        else{

            return number();
        }
    }

    public int UnaryExp(){
        //System.out.println("tttt");
        while(word.getWord().equals("+")||word.getWord().equals("-")){
          //  System.out.println("aaaaaaa");
            if(word.getWord().equals("-")){
                flag+=1;
            }
            word= scanner.scan();
            System.out.println(word.getWord()+" ");
        }

        int num=PrimaryExp();

        if(flag%2==0){

            flag=0;
            return num;
        }
        else{
           // System.out.println("b");
            flag=0;
            return num*(-1);
        }

    }
    public int MulExp(){
       // System.out.println("mul");
       // System.out.println(word.getWord());
        int sum=UnaryExp();
        while(word.getWord().equals("*")||word.getWord().equals("/")||word.getWord().equals("%")){
           // System.out.println("in *");
            char[] arr=word.getWord().toCharArray();
         //   System.out.println(arr[0]);
            word= scanner.scan();
            System.out.println(word.getWord()+" ");
            int num=UnaryExp();
            if(sum==0&&arr[0]=='/'){
                error();
            }
            else{
                sum=Operate(sum,num,arr[0]);
            }

        }
        return sum;
    }
    public int Exp(){
      //  System.out.println("exp");
        int sum=MulExp();
        while(word.getWord().equals("+")||word.getWord().equals("-")){
            char[] arr=word.getWord().toCharArray();
            word= scanner.scan();
            System.out.println(word.getWord()+" ");
            int num=MulExp();
            sum=Operate(sum,num,arr[0]);
        }
        return sum;
    }
    public void Stmt(){
        if(word.getWord().equals("return")){
            word= scanner.scan();
            System.out.println(word.getWord()+" ");
            //lab1
            //number();

            //lab2
            number=Exp();


            if(word.getWord().equals(";")){
                word= scanner.scan();
                System.out.println(word.getWord()+" ");
              //  System.out.println("1");
                return;
            }
            error();
        }
        error();
    }
    public void Block(){
        if (word.getWord().equals("{")){
            word=scanner.scan();
            System.out.println(word.getWord()+" ");
            Stmt();
            if(word.getWord().equals("}")){

                return;
            }
            error();
        }
    }
    public void Funcdef(){
        FuncType();
        Ident();
        if(word.getWord().equals("(")){
            word= scanner.scan();
            System.out.println(word.getWord()+" ");
            if(word.getWord().equals(")")){
                word= scanner.scan();
                System.out.println(word.getWord()+" ");
                Block();
                return;
            }
            error();
        }
        else{
            error();
        }

    }

    public static void main(String[] args) {
        String path=args[0];
        String output=args[1];
        //String path="src/a.txt";
        //  System.out.println(path);

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

      //  System.out.println("1111");
        Main main=new Main();
        main.scanner =new Scanner(filecontent);
        main.word= main.scanner.scan();


        main.Funcdef();
        System.out.println(main.number);
//        System.out.println("sdfa");
//        System.out.println(main.number);

        //lab1
//        pw.print("define dso_local i32 @main(){\n" +
//                "    ret i32 "+ main.number+"\n" +
//                "}");

        if(main.flag%2==0){
            pw.print("define dso_local i32 @main(){\n" +
                "    ret i32 "+ main.number+"\n" +
                "}");
        }
        else {
            if(main.number==0){
                pw.print("define dso_local i32 @main(){\n" +
                        "    ret i32 "+0+"\n" +
                        "}");
            }
            else{
                pw.print("define dso_local i32 @main(){\n" +
                        "    ret i32 "+ "-"+main.number+"\n" +
                        "}");
            }

        }

        pw.flush();
        pw.close();


    }
}