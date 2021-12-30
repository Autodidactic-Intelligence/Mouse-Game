package mousegame;
import java.util.Scanner;
import java.util.ArrayList; 
/**
 *
 * @author Adam
 */
public class MouseGame {
    private Scanner input = new Scanner(System.in);
    private static Mouse[] mice = new Mouse[4]; //aray of Mouse objects that undergo testing
    private static int shortestIndex; //index in mice that has the lowest move count
    private static int secondShortestIndex; //index in mice that has the 2nd lowest move count   
    static Tile[][] gameBoard; //gameBoard of all tiles
    
    /**
     * Generates the board the mouse will traverse through.
     * @Param x: how many tiles along the x axis
     * @Param y: how many tiles along the y axis
     */
    public static void generateBoard(byte x, byte y){
        gameBoard = new Tile[x][y];
        boolean isCheese = false;
        for (int i = 0; i < x; i++){
            for (int k = 0; k < y; k++){
                if (i == (x-1) && k == (y-1)){
                    isCheese = true;
                }
                Tile T = new Tile((byte)i, (byte)k, true, isCheese);
                gameBoard[i][k] = T;
            }
        }
    }
  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        generateBoard((byte) 12, (byte) 12);      
        int goAgain = 1;
        //int numGenerations = 100;
        int generation = 1;
        Scanner input = new Scanner(System.in);
        while(goAgain == 1){
            System.out.println("Generation: " + generation);
            System.out.println("------------------------");
            for(int iterator = 0; iterator < mice.length; iterator++){
                mice[iterator] = (new Mouse((byte)0, (byte)0));
                while(!mice[iterator].returnCheese()){
                    mice[iterator].checkMoves(gameBoard);
                    mice[iterator].move();
                }
                System.out.println("The mouse took " + (mice[iterator].getMoveCount()) + " steps to reach the cheese.");
//                System.out.println("The Mouse got the cheese! " + mouse1.returnCheese());           
                System.out.println();
            }
            getShortest(); 
            System.out.println("The highest scoring mouse is number: " + (shortestIndex +1));
            System.out.println("The 2nd highest scoring mouse is number: " + (secondShortestIndex+1));
            double avgLength = 0;
            for(Mouse m : mice){
                avgLength += m.getMoveCount();
            }
            avgLength /= 4;
            System.out.println("Average length for generation " + generation + " is: " + avgLength);
            ArrayList<Tile> finalList = crossover();

            System.out.println("Final list size: "+ finalList.size());

            adjustInfo(finalList);
            System.gc();
            System.out.println("Run again: 0 to end the game 1 to continue");
            goAgain = input.nextInt();
            for(Mouse m : mice){
                m = null;
            }
            generation++;
        }
        System.gc();
    }
    
    /**
     * Gets the index of the 2 quickest mice in the mice array
     */
    public static void getShortest(){
        int[] lengths = new int[mice.length]; //stores the length of all the mouse's movesMade ArrayList
        for (int i = 0; i < mice.length; i++){
            lengths[i] = mice[i].getMoveCount();
        }     
        shortestIndex = 0;
        for (int i = 0; i < lengths.length-1; i++){
            if (lengths[shortestIndex] >= lengths[i+1]){
                shortestIndex = (i+1);
            }
        }
        if(shortestIndex == 0)  secondShortestIndex = 1;
        else secondShortestIndex = 0;
        for (int i = 0; i < lengths.length-1; i++){
            if (lengths[secondShortestIndex] > lengths[i+1] && shortestIndex != i+1){
                secondShortestIndex = (i+1);
            }
        }
    }
    
    /**
     * Takes the 2 shortest movesMade arrayLists of Mouse objects and splices them to create a potentially shorter array 
     * @return ArrayList 
     * Splices the two arrays together based off of tiles that are in both arrays, these tiles are referred to as same tiles.
     */
    public static ArrayList<Tile> crossover(){
        ArrayList<Tile> shortList = mice[shortestIndex].getMovesMade(); //List of moves by the quickest mouse
        ArrayList<Tile> secShortList = mice[secondShortestIndex].getMovesMade(); //list of moves by the second quickest mouse
        ArrayList<Tile> same = new ArrayList<>(); //Holds tiles that are both in shortList & secshortList
        ArrayList<Tile> finalList = new ArrayList<>(); //FinalList of moves to be passed on
        ArrayList<Integer> shortIndex = new ArrayList<>(); //Index of the identical tiles for the shortList
        ArrayList<Integer> secShortIndex = new ArrayList<>(); //Index of the identical tiles for secShortList
        int sDistance; //distance between same tiles for the shortList
        int ssDistance; //distance between same tiles for the secShortList
        Tile comparing; //Tile to compare for
        boolean isIn; //If a tile is in the same arrayList already
        
        
        for (int i = 0; i < shortList.size(); i++){
            comparing = shortList.get(i); //Tile to compare against
            for (int k = 0; k < secShortList.size(); k++){ 
                if (comparing.isEqual(secShortList.get(k))){ //If the tile is the same as that in the secShortList It gets added to the same arrayList
                    isIn = false; //used to prevent duplicate tiles from being entered
                    for (int j = 0; j < same.size(); j++){ //Logic to determine if a tile is already present in that array
                        if (comparing.isEqual(same.get(j))){
                            isIn = true;
                        }
                    }
                    if (!isIn){ //If that tile, comparing, is not in the same arrayList it gets added and indexes of those tiles in both mouse move lists
                                //are added to their respective lists
                        same.add(comparing);
                        //System.out.println("Comparing tile:" + comparing.toString());
                        shortIndex.add(i);
                        secShortIndex.add(k);
                        //System.out.println("I & K: " +i + " " + k);
                    }
                }
            }
        }
        for (int i = 0; i < same.size() - 1; i++){ //minus 1 is here as this code looks at I and the index after I.
            sDistance = shortIndex.get(i+1) - shortIndex.get(i);
            ssDistance = secShortIndex.get(i+1) - secShortIndex.get(i);
            //get the distance between a same tile in both arraylists and the next same tile, same being a tile present in both arraylists 
            if (ssDistance < sDistance){ //if the distance is shorter for the second quickest mouse it adds it's moves to the new array
                for (int k = secShortIndex.get(i); k <= secShortIndex.get(i+1); k++){
                    finalList.add(secShortList.get(k));
                }
            }
            else {//if not it adds the quickest mouse tiles to the array
                for (int k = shortIndex.get(i); k <= shortIndex.get(i+1); k++){
                    finalList.add(shortList.get(k));
                }
            }
        }
        return finalList; //returns the array
    }
    
    /**
     * Removes duplicates in the array passed to it
     * @param arr array with duplicates
     * @return cleaned array
     * Looks for loops, or cycles, in the path and cuts them out
     */
    public static Tile[] removeLoops(ArrayList<Tile> arr){
        int upTo = 0;//index to go up to for loop removal
        int from = 0; //index to start from, both are initiliazed as the IDE doesnt like having them uninitialized
        System.out.println("crossed array size: " + arr.size());
        //starts at the first tile and iterates over all remaining tiles to try and find the same tile
        for (int i = 0; i < arr.size() -1; i++){
            for (int k = i+1; k < arr.size(); k++){
                if(arr.get(i).isEqual(arr.get(k))){ 
                    //if two tiles are the same it removes the 1st instance of that tile and all tiles between it and the 2nd instance,
                    //not including the 2nd instance.
                    upTo = k;
                    from = i;
                    for (int b = from; b < upTo; b++){
                        arr.remove(from);
                    }
                }
            }
        }
//        System.out.println("I: " + from);
//        System.out.println("K: " + upTo);
        Tile[] cleanArray = new Tile[arr.size()];
        for(int i = 0; i < arr.size(); i++){
            cleanArray[i] = arr.get(i);
            //System.out.println("Cleaned array: " + cleanArray[i]);
        }
        System.out.println(cleanArray.length);
        return cleanArray;
    }
    
    /**
     * Adjusts the information value of a tile based on the reward
     * @param arr Final ArrayList
     */
    public static void adjustInfo(ArrayList<Tile> arr){
        int reward  = 40; //reward amount, needs to be changed based on gameboard size
        double newInfo; //new information value of the tile
        double rewardPerTile = (reward/arr.size()); //calculates the reward per tile
        Tile[] finalTiles = removeLoops(arr); //Removes all duplicate tiles
        for(Tile t : finalTiles){ //Assigns the reward for each tile to the tiles
            newInfo = t.getInfo() + rewardPerTile;
            t.setInfo(newInfo);
        }
    }
}