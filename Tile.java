package mousegame;

/**
 *
 * @author Adam
 */
public class Tile {
    private byte xpos; //x coordinate of the tile
    private byte ypos; //y coordinate of the tile
    private boolean valid; //is the tile a valid move
    private boolean cheese; //does the tile contain the cheese
    private double info; //information value of the tile
    
    public Tile(byte y, byte x, boolean v, boolean c){
        xpos = x;
        ypos = y;
        valid = v;
        cheese = c;
        info = 0.20;
    }
    
    public Tile getTile(){
        return this;
    }
    
    public byte getXpos(){
        return this.xpos;
    }
    
    public byte getYpos(){
        return this.ypos;
    }
    
    public boolean isCheese(){
        return this.cheese;
    }
    
    public double getInfo(){
        return this.info;
    }
    
    public boolean isValid(){
        return this.valid;
    }
    
    public void setInfo(double info){
        this.info = info;
    }
    
    @Override
    public String toString(){
        String str = ("Tile: " + this.xpos + ", " + this.ypos);
        return str;
    }
    
    /**
     * Compares two tiles to see if they are the same by comparing positions in the game board
     * @param t tile to compare
     * @return true or false
     */
    public Boolean isEqual(Tile t){
        boolean bool; //stores the boolean value of the function
        if(t == null){ //used to prevent null pointer exceptions
            bool = false;
        }
        else if (this.xpos == t.xpos && this.ypos == t.ypos) bool = true;
        else bool = false;
        return bool;
    }
}
