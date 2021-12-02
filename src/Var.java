import java.util.ArrayList;

public class Var {
    private int order;
    private String type="";
    private String orderUse="";
    private String word="";
    private int value;
    private int blocknum;

    private int blocklvel;

    private ArrayList<Integer> paramList;

    public ArrayList<Integer> getParamList() {
        return paramList;
    }

    public void setParamList(ArrayList<Integer> paramList) {
        this.paramList = paramList;
    }

    public int[] arr_value;

    public int getBlocklvel() {
        return blocklvel;
    }

    public void setBlocklvel(int blocklvel) {
        this.blocklvel = blocklvel;
    }

    public int getBlocknum() {
        return blocknum;
    }

    public void setBlocknum(int blocknum) {
        this.blocknum = blocknum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderUse() {
        return orderUse;
    }

    public void setOrderUse(String orderUse) {
        this.orderUse = orderUse;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
        this.orderUse="%"+this.order;

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}