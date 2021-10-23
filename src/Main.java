import java.io.*;
import java.util.ArrayList;

public class Main {

    public Word word;
    public Scanner scanner;
    public int number;

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

    public void number(){
        if(word.getType().equals("Number")){
            number= Integer.valueOf(word.getWord());
       //     System.out.println(number);
            word= scanner.scan();
            return;
        }
        error();
    }
    public void Stmt(){
        if(word.getWord().equals("return")){
            word= scanner.scan();
            number();
            if(word.getWord().equals(";")){
                word= scanner.scan();
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
                e.printStackTrace();
            }
            BufferedReader bufferReader =new BufferedReader(in);
            String str=null;
            while (true){
                try {
                    if (!((str=bufferReader.readLine())!=null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filecontent+=str;
                filecontent+='\n';
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("1111");
        Main main=new Main();
        main.scanner =new Scanner(filecontent);
        main.word= main.scanner.scan();

        main.Funcdef();
        System.out.println("sdfa");
        System.out.println(main.number);
        pw.print("define dso_local i32 @main(){\n" +
                "    ret i32 "+ main.number+"\n" +
                "}");
        pw.flush();
        pw.close();


    }
}