import java.util.ArrayList;

public class Scanner {
    private boolean isFinish=false;

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    private int pos=0;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    private Word word;

    private String content;

    private char[] list;

    private char[] token=new char[255];

    private int pos_token;

    private char ch;

    public Scanner(String content) {
        this.content = content;
        this.list=content.toCharArray();
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
        while((ch==' '||ch=='\t'||ch=='\n')&&pos<list.length-1) {
            ch = list[pos];
            pos++;
        }
        if(pos==content.length()){
            isFinish=true;
        }
    }

    public boolean isLetter(){
        if(ch>='a'&&ch<='z'||ch>='A'&&ch<='Z'){
            return true;
        }
        return false;
    }

    public boolean isDigit(){
        return ch>='0'&& ch<='9';
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

    public Word scan(){
        token=new char[255];
        pos_token=0;
        word=new Word();
        getchar();
        getNbc();
        if(isLetter()){
            while (isLetter()||isDigit()){
                //System.out.println(ch);
                catToken();
                getchar();
            }
            pos--;
            token[pos_token]='\0';

            if(isSpecial()!=-1){
                word.setType(word.typelist[isSpecial()+2]);
              //  System.out.println("aaaaaaaaaaaa");
            }else{
                word.setType(word.typelist[0]);
                word.setWord((String.valueOf(token)).substring(0,pos_token));
            }
            return word;
        }
        else if(isDigit()){
            while(isDigit()){
                catToken();
                getchar();
            }
            pos--;
            token[pos_token]='\0';
           // System.out.println(pos_token);
           // System.out.println(token);
            word.setWord((String.valueOf(token)).substring(0,pos_token));
            word.setType(word.typelist[1]);
            return word;
        }
        else if(isEqual()){
            char ch1=getchar();
            if(ch1 =='='){
                word.setType("Eq");
                return word;
            }
            else {
                pos--;
                word.setType("Assign");
                return word;
            }
        }
        else if(ch=='\u0000'){
            word.setType("");
            word.setWord("");
            return word;
        }
        else{
            switch (ch) {
                case ';':
                    word.setType("Semicolon");
                    return word;
                case '(':
                    word.setType("LPar");
                    return word;
                case ')':
                    word.setType("RPar");
                    return word;
                case '{':
                    word.setType("LBrace");
                    return word;
                case '}':
                    word.setType("RBrace");
                    return word;
                case '+':
                    word.setType("Plus");
                    return word;
                case '*':
                    word.setType("Mult");
                    return word;
                case '/':
                    word.setType("Div");
                    return word;
                case '<':
                    word.setType("Lt");
                    return word;
                case '>':
                    word.setType("Gt");
                    return word;
                default:
                 //   System.out.println(ch);
                 //   System.out.println("1");
                    catToken();
                   // System.out.println(token);
                    word.setType("Err");
                    return word;
            }
        }



    }
}
