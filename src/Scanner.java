import java.util.*;


public class Scanner {
    private boolean isFinish=false;

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    private int pos=0;

    private int flag;

    private Stack<Integer> recordstack=new Stack<>();

    private Stack<Integer> record1Stack=new Stack<>();

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    private Word word;

    private String content;

    private char[] list;

    private int before;

    private int before2;

    private int before3;

    private int temp1;

    private int temp;

    private char[] token=new char[100000];

    private int pos_token;

    private char ch;

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void record1(){
        record1Stack.push(pos);
    }

    public void goBackFromWhile(){
        pos=record1Stack.pop();

    }

    public void record(){
        recordstack.push(pos);
    }

    public void goBackBaseTemp(){
        pos=recordstack.pop();

    }

    public Scanner(String content) {
        this.content = content;
        this.list=content.toCharArray();
    }

    public int covert(String content){
        int number=0;
        String [] HighLetter = {"A","B","C","D","E","F"};
        content=content.substring(2);
     //   System.out.println(content);
        Map<String,Integer> map = new HashMap<>();
        for(int i = 0;i <= 9;i++){
            map.put(i+"",i);
        }
        for(int j= 10;j<HighLetter.length+10;j++){
            map.put(HighLetter[j-10],j);
        }
        String[] str = new String[content.length()];
        for(int i = 0; i < str.length; i++){
            str[i] = content.substring(i,i+1);
        }
        for(int i = 0; i < str.length; i++){
            number += map.get(str[i])*Math.pow(16,str.length-1-i);
        }
        return number;
    }

    public char getchar(){
        if(pos<content.length()){
          //  System.out.println(list);
            ch=list[pos];
            pos++;


        }
        if(pos==content.length()){
            isFinish=true;
        }
        return ch;
    }

    public void getNbc(){
        while((ch==' '||ch=='\t'||ch=='\n')&&pos<content.length()-1) {
         //   System.out.println(" ia getnbc");
            ch = list[pos];
            pos++;
        }

        if(pos==content.length()){
       //     System.out.println("i am in getnbc");
            isFinish=true;
        }
    }

    public boolean isLetter(){
        if(ch>='a'&&ch<='z'||ch>='A'&&ch<='Z'||ch=='_'){
            return true;
        }
        return false;
    }

    public boolean isDigit(){
        return ch>='0'&& ch<='9';
    }

    public boolean isOctal(){
        return ch>='0'&& ch<='7';
    }

    public boolean isHex(){
        return (ch>='0'&&ch<='9')||(ch>='a'&&ch<='f')||(ch>='A'&&ch<='F');
    }
    public boolean isEqual(){
        return ch=='=';
    }

    public void catToken(){
        token[pos_token]=ch;
        pos_token++;
        token[pos_token]='\0';
    }

    public int isSpecial(){
     //   System.out.println(word.specialList.toString());
        for (int i = 0; i < word.specialList.length; i++) {
            if(((String.valueOf(token)).substring(0,pos_token)).equals(word.specialList[i])){
                return i;
            }
        }
        return -1;
    }

    public int power(int i, int j) {
        int temp = 1;
        for(int k=0;k<j;k++){
            temp *= i;
        }
        return temp;
    }
    public void goBack(){
        pos=before;
    }
    public void goBack2(){
        pos=before2;
    }
    public void goBack3(){
        pos=before3;
    }
    public void setAflag(){
        flag=pos;
    }
    public void fromTheflag(){
        pos=flag;
    }
    public Word scan(){
        before3=before2;
        before2=before;
        before=pos;
        token=new char[100000];
        pos_token=0;
        word=new Word();
      //  System.out.println(pos);
       // System.out.println("I am before getchar");
        getchar();
     //   System.out.println(ch);
        getNbc();
    //    System.out.println(pos);
        while(ch=='/'){
            getchar();
            if(ch=='/'){
                while(ch!='\n'){
                    getchar();

                }
                getchar();
                getNbc();

            }
            else if(ch=='*'){
                while(ch!='\0'&&pos<list.length){
                    getchar();
                    if(ch=='*'){
                        getchar();
                        if(ch=='/'){
                            getchar();
                            getNbc();
                            break;
                        }
                    }
                }
                if(pos== list.length){

                    System.exit(-1);
                }
            }
            else{
                pos--;
                ch=list[pos-1];
             //   System.out.println("here");
            //    System.out.println(ch);
                break;
            }
        }
     //   System.out.println("i am behind ///");
    //    System.out.println(pos);
        if(isLetter()){
            while (isLetter()||isDigit()){

                catToken();
                getchar();
            }
            pos--;
            token[pos_token]='\0';

            if(isSpecial()!=-1){
                word.setWord((String.valueOf(token)).substring(0,pos_token));
                word.setType(word.typelist[isSpecial()+2]);
              //  System.out.println("aaaaaaaaaaaa");
            }else{
                word.setType(word.typelist[0]);
                word.setWord((String.valueOf(token)).substring(0,pos_token));
            }
            return word;
        }
        else if(isDigit()){
          //  System.out.println(pos);
        //    System.out.println("I am in isDigit");
            if(ch=='1'||ch=='2'||ch=='3'||ch=='4'||ch=='5'||ch=='6'||ch=='7'||ch=='8'||ch=='9'){
                while(isDigit()){
                    catToken();
                    getchar();
                    getNbc();
                }
                pos--;
                token[pos_token]='\0';
                // System.out.println(pos_token);
                // System.out.println(token);
                word.setWord((String.valueOf(token)).substring(0,pos_token));
                word.setType(word.typelist[1]);
           //     System.out.println(pos);
                return word;
            }
            else if(ch=='0'){
                catToken();
                getchar();
                if(ch=='x'||ch=='X'){
                    catToken();
                    getchar();
                    if(isHex()){
                        while(isHex()){
                            catToken();
                            getchar();
                        }
                        pos--;
                        token[pos_token]='\0';
                        // System.out.println(pos_token);
                        // System.out.println(token);
                        String s=(String.valueOf(token)).substring(0,pos_token);
                        s=s.toUpperCase();
                    //    System.out.println(s);
                        int num=covert(s);
                        word.setWord((String.valueOf(num)));
                        word.setType(word.typelist[1]);
                        return word;
                    }
                    else {
                        pos--;
                        token[pos_token]='\0';
                        // System.out.println(pos_token);
                        // System.out.println(token);
                        word.setWord((String.valueOf(token)).substring(0,pos_token));
                        word.setType("Err");
                        return word;
                    }
                }
                else{
                    while(isOctal()){
                        catToken();
                        getchar();
                    }
                    pos--;
                    token[pos_token]='\0';
                    // System.out.println(pos_token);
                    // System.out.println(token);
                    String s=(String.valueOf(token)).substring(0,pos_token);
                    char[] str=s.toCharArray();
                    int num=0;
                    for (int i = str.length-1; i >=0 ; i--) {
                        num+=(str[i]-'0')*power(8,(str.length-1-i));
                    }
                    word.setWord(String.valueOf(num));
                    word.setType(word.typelist[1]);
                    return word;

                }
            }

        }
        else if(isEqual()){
       //     System.out.println("22225");
            char ch1=getchar();
            if(ch1 =='='){
                word.setType("Eq");
                word.setWord("==");
                return word;
            }
            else {
                pos--;
                word.setWord("=");
                word.setType("Assign");
                return word;
            }
        }
        else if(ch=='|'){
            char ch1=getchar();
            if(ch1=='|'){
                word.setType("||");
                word.setWord("||");
                return word;
            }
            else{
                pos--;
                word.setWord("Err");
                word.setType("Err");
                return word;
            }
        }
        else if(ch=='&'){
            char ch1=getchar();
            if(ch1=='&'){
                word.setType("&&");
                word.setWord("&&");
                return word;
            }
            else{
                pos--;
                word.setWord("Err");
                word.setType("Err");
                return word;
            }
        }
        else if(ch=='<'){
            char ch1=getchar();
            if(ch1=='='){
                word.setType("<=");
                word.setWord("<=");
                return word;
            }
            else{
                pos--;
                word.setWord("<");
                word.setType("<");
                return word;
            }
        }
        else if(ch=='>'){
            char ch1=getchar();
            if(ch1=='='){
                word.setType(">=");
                word.setWord(">=");
                return word;
            }
            else{
                pos--;
                word.setWord(">");
                word.setType(">");
                return word;
            }
        }
        else if(ch=='!'){
            char ch1=getchar();
            if(ch1=='='){
                word.setType("!=");
                word.setWord("!=");
                return word;
            }
            else {
                pos--;
                word.setWord("!");
                word.setType("!");
                return word;
            }
        }
        else{
         //   System.out.println(ch);
            switch (ch) {
                case ';':
                    word.setWord(";");
                    word.setType("Semicolon");
                    return word;
                case '(':
                    word.setWord("(");
                    word.setType("LPar");
                    return word;
                case ')':
                    word.setWord(")");
                    word.setType("RPar");
                    return word;
                case '{':
                    word.setWord("{");
                    word.setType("LBrace");
                    return word;
                case '}':
                    word.setWord("}");
                    word.setType("RBrace");
                    return word;
                case '+':
                    word.setWord("+");
                    word.setType("Plus");
                    return word;
                case '-':
                    word.setWord("-");
                    word.setType("Red");
                    return word;
                case '*':
                    word.setWord("*");
                    word.setType("Mult");
                    return word;
                case '/':
                    word.setWord("/");
                    word.setType("Div");
                    return word;
                case '\n':
                    word.setWord("\n");
                    word.setType("\n");
                    return word;
                case '%':
                    word.setWord("%");
                    word.setType("%");
                    return word;
                case ',':
                    word.setWord(",");
                    word.setType(",");
                    return word;
                case '[':
                    word.setWord("[");
                    word.setType("[");
                    return word;
                case ']':
                    word.setWord("]");
                    word.setType("]");
                    return word;
                default:
                    catToken();
                 //   System.out.println(token);

                    word.setWord("Err");
                    word.setType("Err");
                    return word;
            }

        }
        return null;
    }
}
