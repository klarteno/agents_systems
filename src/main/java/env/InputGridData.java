package env;

import java.io.Serializable;
import java.util.Map;

public class InputGridData implements Serializable {
    private static final long serialVersionUID = 765764534241652904L;

    public final int height;
    public final int width;
    public final int nbAgs;
    public final char[][] data;
    public final Map<Character, String> colors;


    public InputGridData(int height, int width, int nbAgs, char[][] data, Map<Character, String> colors) {
        super();

        this.height = height;
        this.width= width;
        this.nbAgs = nbAgs;
        this.data = data; 
        this.colors = colors;
    }
}
