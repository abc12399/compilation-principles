import java.io.*;
//
//public class Test {
//    public static void main(String[] args) {
//
//        String path=args[0];
//        //String path="src/a.txt";
//      //  System.out.println(path);
//
//        String filecontent="";
//
//        File source=new File(path);
//        try {
//            Reader in =new FileReader(source);
//            BufferedReader bufferReader =new BufferedReader(in);
//            String str=null;
//            while (true){
//                try {
//                    if (!((str=bufferReader.readLine())!=null)) break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                filecontent+=str;
//                filecontent+='\n';
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        //file has already be read to filecontent
//        //System.out.print(filecontent);
//
//        //next scan filecontent
//
//        Scanner scanner =new Scanner(filecontent);
//        while(!scanner.isFinish()){
//            Word word=scanner.scan();
//            if(word.getType().equals("Number")||word.getType().equals("Ident")){
//                System.out.println(word.getType()+"("+word.getWord()+")");
//            }
//            else{
//                if(word.getType().equals("Err")){
//                    System.out.println(word.getType());
//                    break;
//                }
//                else if(word.getType().equals("\n")){
//                    break;
//                }
//                System.out.println(word.getType());
//            }
//        }
//
//
//    }
//}