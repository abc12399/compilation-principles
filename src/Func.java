import java.util.ArrayList;

public class Func {
    private String word;
    private String type;
    private ArrayList<Integer> paramList;
    private int pos;

    public String getWord() {
        return word;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
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

    public ArrayList<Integer> getParamList() {
        return paramList;
    }

    public void setParamList(ArrayList<Integer> paramList) {
        this.paramList = paramList;
    }
}