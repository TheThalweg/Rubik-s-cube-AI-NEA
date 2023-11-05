package com.example.RubiksCubeAI;

public class Cube {
    /*The cube representation works as follows. Each side of the cube is stored as a 32-bit integer, where each nibble
    of 4 bits represents one piece of the cube. For ease of understanding these numbers are generally shown in hexadecimal
    where each character represents a colour. The colours are as follows:
    1 - Yellow
    2 - Red
    3 - Green
    4 - Orange
    5 - Blue
    6 - White
    These values are also the values used for the positions of sides in all arrays (Except they are zero indexed)

    Adjacency list which is used to find which sides are next to any given side along with the array that stores the states
    of individual sides. There is also an array which stores the slices used to find a layer adjacent to a certain side.

    See above values to see which positions in the array correspond to which side*/
    final int[][] adjacency = {{4,3,2,1},{0,2,5,4},{0,3,5,1},{0,4,5,2},{0,1,5,3},{1,2,3,4}};
    int[] sides = {0x11111111,0x22222222,0x33333333,0x44444444,0x55555555,0x66666666};

    /* To explain further: 0 represents up, 1 - right, 2 - down, 3 - left.
    For example if you were to turn the front side (the 3rd item in the array), you would have to change the bottom slice
    on the top side, the left slice on the right side, the top slice on the bottom side and the right slice on the left side.
     */
    final int[][] moveSlices = {{0,0,0,0},{3,3,3,1},{2,3,0,1},{1,3,1,1},{0,3,2,1},{2,2,2,2}};

    //This is a container for all the bitmasks used, including top slice, right slice, bottom slice, left slice, edges and corners
    final static int[] bitMaskDic = {0xFFF00000,0x00FFF000,0x0000FFF0,0xF00000FF,0x0F0F0F0F,0xF0F0F0F0};

    //Constructor used to set initial state of the cube (initial scramble)
    public Cube() {}

    private Cube(int[] sides) {
        this.sides = sides.clone();
    }
    //deep clones the cube (This is used for the IDDFS)
    public Cube clone(){
        return new Cube(this.sides);
    }
    //gets colour at given index for given side by shifting colour to the right and then bitwise ending with 0000000F
    public int getColour(int side, int index){
        return sides[side] >> (7-index)*4 & 0x0000000F;
    }
    //Static method used to roll an integer number a certain amount of bits to the right.
    static int singleFace(int side,int amountToBeTurned){
        int rightSide = side >> (8*amountToBeTurned);
        int leftSide = side << (32 - 8*amountToBeTurned);
        return rightSide | leftSide;
    }

    //Static method which is used to overwrite a slice of a face with a slice from another face
    static int singleSliceManipulation(int stateOne, int stateTwo, int sliceOne, int sliceTwo) {
        stateOne = stateOne & ~(bitMaskDic[sliceOne]);
        /*Here we need to perform a bitwise rotation of the second state in order to make it match up with stateOne.
        This is needed to allow for a correct bitwise or at the end of the function.*/
        stateTwo = singleFace(stateTwo & bitMaskDic[sliceTwo],sliceOne-sliceTwo);
        return stateOne | stateTwo;
    }

    //Each of these methods alter the states of the cube for a clockwise, anticlockwise and 180-degree turn
    public void clockwise(int side) {
        //retrieves adjacent sides for a given side inputted as a parameter
        int[] adjacentSides = adjacency[side];
        //Stores temporary state for the 4th side, so it is still useful once overwritten
        int temp = sides[adjacentSides[3]];
        int[] slices = moveSlices[side];
        //loops over sides 4 to 1, moving pieces from side 3 to 4 etc.
        for (int i=3;i>0;i--){
            sides[adjacentSides[i]] = singleSliceManipulation(sides[adjacentSides[i]],sides[adjacentSides[i-1]],slices[i],slices[i-1]);
        }
        //Moves pieces over from the temporary side 4 to the first side
        sides[adjacentSides[0]] = singleSliceManipulation(sides[adjacentSides[0]],temp,slices[0],slices[3]);
        //Performs a bitwise rotation on the main side
        sides[side] = singleFace(sides[side],1);
    }
    //Basically the same as clockwise except the other way around. Also, final rotation is done 3 times.
    public void antiClockwise(int side) {
        int[] adjacentSides = adjacency[side];
        int temp = sides[adjacentSides[0]];
        int[] slices = moveSlices[side];
        for (int i=0;i<3;i++){
            sides[adjacentSides[i]] = singleSliceManipulation(sides[adjacentSides[i]],sides[adjacentSides[i+1]],slices[i],slices[i+1]);
        }
        sides[adjacentSides[3]] = singleSliceManipulation(sides[adjacentSides[3]],temp,slices[3],slices[0]);
        sides[side] = singleFace(sides[side],3);
    }
    //Similar to previous methods but everything is done manually as using a for loop is unnecessary, and it requires two temporary variables
    public void doubleTwist(int side) {
        int[] adjacentSides = adjacency[side];
        int temp = sides[adjacentSides[0]];
        int[] slices = moveSlices[side];
        sides[adjacentSides[0]] = singleSliceManipulation(sides[adjacentSides[0]],sides[adjacentSides[2]],slices[0],slices[2]);
        sides[adjacentSides[2]] = singleSliceManipulation(sides[adjacentSides[2]],temp,slices[2],slices[0]);
        temp = sides[adjacentSides[1]];
        sides[adjacentSides[1]] = singleSliceManipulation(sides[adjacentSides[1]],sides[adjacentSides[3]],slices[1],slices[3]);
        sides[adjacentSides[3]] = singleSliceManipulation(sides[adjacentSides[3]],temp,slices[3],slices[1]);
        sides[side] = singleFace(sides[side],2);
    }
}
