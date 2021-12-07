import java.util.ArrayList;

public class Word {
    private String word;//

    private String type;

    public String[] typelist={"Ident","Number","If","Else","While","Break","Continue","Return","Assign","Semicolon","LPar","RPar","LBrace","RBrace","Plus",
            "Mult","Div","Lt","Gt","Eq","Err"};

    public String[] specialList={"if","else","while","break","continue","return"};

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}