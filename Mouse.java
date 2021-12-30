package mousegame;
import java.util.Random;
import java.util.ArrayList;
/**
 *
 * @author Adam
 */
public class Mouse {
    private boolean hasCheese; //if the mouse gets the cheese this becomes true
    private int totalMoves; //stores the total moves the mouse has made
    private byte xpos; //x coordinate of the mouse in the gameboard
    private byte ypos; //y coordinate of the mouse in the gameboard
    private double fitness; //the fitness level of the mouse
    final private double prevMod = 0.1; //modifies the information values of the previous tile
    final private double isInMod = 0.5; //modifies the information value of a tile already in the movesMade arraylist
    final private double nullMod = -1.0; //used to keep the mouse from moving to a null tile, filler to be used with the infoModified array
    private double gamma = 0.5; //learning rate of the mouse; aids with random searching
    private double gammaDecay = 0.85; //decay rate of gamma, keeps the mouse from searching forever 
    private Random rand = new Random(); //used for mutation
    private Tile current; //current tile the mouse is on
    private Tile previous; //previous tile the mouse was on
    private Tile maxInfo; //tile that has the largest info value
    private Tile[] possibleMoves = new Tile[4]; //stores all possible moves
    private double[] infoModified = new double[4];//stores the modified info of the tiles, info values are modified based on certain criteria
    private ArrayList<Tile> movesMade; //stores all the moves made by the mouse

    public Mouse() {
    }
    
    /**
     * Constructor for a mouse object
     * @param x starting x coordinate
     * @param y starting y coordinate
     */
    public Mouse(byte x, byte y){
        hasCheese = false;
        totalMoves = 0;
        xpos = x;
        ypos = y;
        current = null;
        previous = null;
        maxInfo = null;
        movesMade = new ArrayList<>();
    }
    
    /**
     * Used to see if the mouse has the cheese
     * @return true if the mouse has the cheese, false otherwise
     */
    public boolean returnCheese(){
        return hasCheese;
    }
    
    /**
     * Used to get the amount of moves a mouse made to reach the cheese
     * @return moveList.size()
     */
    public int getMoveCount(){
        return totalMoves;
    }
    
    /**
     * Returns the fitness of the mouse
     * @return fitness
     */
    public double getFitness(){
        return fitness;
    }
    
    /**
     * Returns a Tile with the matching x and y coordinates that are inputted
     * @param x x coordinate to look for
     * @param y y coordinate to look for
     * @param board board where the tiles are stored
     * @return Tile
     */
    public Tile getTile(byte x, byte y, Tile[][] board){
        Tile t = null;
        for(int i = 0; i < board[0].length; i++){
            for(int k = 0; k < board[1].length; k++){
                if(x == board[i][k].getXpos() && y == board[i][k].getYpos()){
                    t = board[i][k]; 
                }
            }
        }
        return t;
    }
    
    /**
    * Used to create an array of all the moves a mouse has made
    * @return the current Tile the mouse is on
    */
    public Tile getCurrentTile(){
        return current;  
    }

    public ArrayList<Tile> getMovesMade(){
        return movesMade;
    }
    
    /**
     * Checks all possible moves the mouse can make and assigns them to the possibleMoves array.
     * @param board Board containing the Tile objects
     */
    public void checkMoves(Tile[][] board) {
        if (current == null){
            current = getTile(xpos, ypos, board);
        }
        int i = 0;
        //Makes sure the mouse can't move out of bounds, prevents InvalidPointerException
        if (xpos + 1 >= board[0].length){
            possibleMoves[i] = null;
        }
        //Fetches the tile on the right if it's within the board
        else{
            possibleMoves[i] = getTile((byte)(xpos+1), ypos, board);
        }
        i++;
        /*
        All others repeat for the other three possible tiles
        */
        if(xpos - 1 < 0){
            possibleMoves[i] = null;
        }
        else{
            possibleMoves[i] = getTile((byte)(xpos-1), ypos, board);
        }
        i++;
        if(ypos + 1 >= board[0].length){
            possibleMoves[i] = null;
        }
        else{
            possibleMoves[i] = getTile(xpos, (byte)(ypos+1), board);
        }
        i++;
        if(ypos - 1 < 0){
            possibleMoves[i] = null;
        }
        else{
            possibleMoves[i] = getTile(xpos, (byte)(ypos-1), board);
        }  
    }
    
    /**
     * Moves the mouse to the next tile
     */
    public void move(){
        totalMoves++; 
        //checks to see if any of the tiles in possible moves have the cheese
        for (int k = 0; k < possibleMoves.length; k++) {
            if(possibleMoves[k] != null && possibleMoves[k].isValid()){
                //System.out.println("PossibleMoves: " + possibleMoves[k].toString());
                //above statement prevents trying to call methods on null objects in the array
                if (possibleMoves[k].isCheese()) {//if a Tile has cheese == true
                    //moves the mouse to the cheese and performs the appropriate actions
                    //System.out.println("Current: "+ current);
                    //System.out.println("Previous: "+ previous);
                    maxInfo = possibleMoves[k];
                    ypos = maxInfo.getYpos();
                    xpos = maxInfo.getXpos();
                    previous = current;
                    current = maxInfo;
                    hasCheese = true;
                    movesMade.add(previous);
                    movesMade.add(current);
                    System.out.println("Cheese has been found");
                }
            }
        }
        //if cheese has not been found it goes on normally
        if (!hasCheese){
            //For loop checks the possible moves then assigns a modifier depending on the status of that tile
            for(int i = 0; i <possibleMoves.length; i++){
                if(possibleMoves[i] == null){ //A null object gives a negative modifier
                    infoModified[i] = nullMod;
                }
                else if (!possibleMoves[i].isValid()){//used if the tile is not a valid move
                    infoModified[i] = nullMod;
                }
                else if(possibleMoves[i].isEqual(previous)){//if it's the previous tile
                    infoModified[i] = possibleMoves[i].getInfo() * prevMod;
                }
                else if(isIn(possibleMoves[i])){ //If it's a move the mouse has already made it is lowered
                    infoModified[i] = possibleMoves[i].getInfo() * isInMod;
                }
                else{ //If it's a new tile
                    infoModified[i] = possibleMoves[i].getInfo();
                }
                //System.out.println("Info Modified: " + infoModified[i]);
            }
            
            int largest = 0; //index of the largest number
            for(int i = 0; i < possibleMoves.length; i++){
                //System.out.println("Largest: " + largest);
                if (possibleMoves[largest] == null){
                    largest++; //gets largest out of the null
                }
                else if (possibleMoves[i] == null){
                    //do nothing, i will increment automatically
                }
                else{
                    if (infoModified[largest] == infoModified[i]){
                        if (gamma >= mutator()){
                            largest = i;
                        }
                    }
                    else if(infoModified[i] > infoModified[largest]){
                        largest = i;
                    }
                }
            }
            //System.out.println("Largest after for loop: " + largest);
            gamma *= gammaDecay;
            maxInfo = possibleMoves[largest];
            //System.out.println("MaxInfo: " + maxInfo.toString());
            //moves the mouse to maxInfo and performs the appropriate actions
            try{
            ypos = maxInfo.getYpos();
            xpos = maxInfo.getXpos();
            previous = current;
            movesMade.add(previous);
            current = maxInfo;
            }
            catch(Exception e){
                System.out.println("Found a problem");
                hasCheese = true;
            }
            //System.out.println("Current tile: "+ previous);
            //System.out.println("Moving to: "+ maxInfo);
           
        }
    }
    
    /**
     * Creates a random boolean used for mutation
     * @return boolean
     */
    public boolean randomBoolean(){
        return rand.nextBoolean();
    }
    
    /**
     * Used for mutating the information value of a Tile during comparison
     * @return One or a zero
     */
    public double mutator(){
        double m = rand.nextInt(10);
        return (1/m);      
    }
    
    /**
     * Checks to see if the passed in tile is in the movesMade Array list for the mouse.
     * @param t Tile to be checked for
     * @return Returns a boolean.
     */
    public boolean isIn(Tile t){
        boolean isIn = false;
        for(Tile k : movesMade){
            if(k.isEqual(t)){
                isIn = true;
            }
        }
        return isIn;
    }
}
