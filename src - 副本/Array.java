public class Array {
    private int dimension;
    private String word;
    private int x;
    private int y;
    private String type;
    private int blocknum;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    //标志是否初始化
    private int flag;

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

    private String baseptr;

    public String getBaseptr() {
        return baseptr;
    }

    public void setBaseptr(String baseptr) {
        this.baseptr = baseptr;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}