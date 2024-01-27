package com.example.RubiksCubeAI;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import static java.util.Map.entry;

/*Similar to the beginners method this implementation won't produce a solution as well as a human can. This is because this method is designed to be solved
intuitively by humans, and therefore it is difficult to implement through a computer. There are many shortcuts humans can take which are very time-consuming
to code in (e.g. using empty slots to reduce U turns). For the purpose of simplicity I have ignored many of these shortcuts as they aren't worth the extra hassle.
What I have done is calculated all possible orders in which you can solve the F2L cases in the hopes that the quickest solve naively implements some of these shortcuts
just by luck*/
public class CFOP extends AICommon implements Runnable {

    //This is the dictionary that contains all the algorithms for the PLL sections. The computer recognises which case it has (given by the binary hash on the left),
    //and then performs the move algorithm given on the right. There are 21 cases in total + the solved case (10110101111). All hashes are missing leading 0s.
    private final Map<Short,String> permutationAlgs = Map.ofEntries(
            entry((short)0b10011110001101,"R2 U2 R U2 R2 U2 R2 U2 R U2 R2"), //H perm
            entry((short)0b10010110111100,"R2 U' R' U' R U R U R U' R"),//Ua perm
            entry((short)0b11010110001110,"R' U R' U' R' U' R' U R U R2"),//Ub perm
            entry((short)0b11011010011100,"R' U' R2 U R U R' U' R U R U' R U' R'"),//Z perm
            entry((short)0b1101001111100,"R' F R' B2 R F' R' B2 R2"),//Aa perm
            entry((short)0b10111101001001,"R2 B2 R F R' B2 R F' R"),//Ab perm
            entry((short)0b11110010010110,"R B' R' F R B R' F' R B R' F R B' R' F'"),//E perm
            entry((short)0b11101011010100,"R' U R U' R2 F' U' F U R F R' F' R2"),//F perm
            entry((short)0b11011011001001,"R2 U R' U R' U' R U' R2 D U' R' U R D'"),//Ga perm
            entry((short)0b10011111011000,"R' U' R U D' R2 U R' U R U' R U' R2 D"),//Gb perm
            entry((short)0b1111101101000,"R2 F2 R U2 R U2 R' F R U R' U' R' F R2"),//Gc perm
            entry((short)0b10010011111001,"R U R' U' D R2 U' R U' R' U R' U R2 D'"),//Gd perm
            entry((short)0b10111111010,"F2 L' U' L F2 R' D R' D' R2"),//Ja perm
            entry((short)0b11011011011,"R U R' F' R U R' U' R' F R2 U' R' U'"),//Jb perm <3
            entry((short)0b10110110000111,"R U R' U R U R' F' R U R' U' R' F R2 U' R' U2 R U' R'"),//Na perm
            entry((short)0b111110100101,"R' U L' U2 R U' L R' U L' U2 R U' L"),//Nb perm
            entry((short)0b11010111101000,"R U' R' U' R U R D R' U' R D' R' U2 R'"),//Ra perm
            entry((short)0b01101111100100,"R' U2 R U2 R' F R U R' U' R' F' R2"),//Rb perm
            entry((short)0b10010111001011,"R U R' U' R' F R2 U' R' U' R U R' F'"),//T perm
            entry((short)0b10111110010100,"R' U R' U' R D' R' D R' U D' R2 U' R2 D R2"),//V perm
            entry((short)0b01111110000110,"F R U' R' U' R U R' F' R U R' U' R' F R F'"),//Y perm
            entry((short)0b10110101111,"")//Completed cube
    );
    //This is the dictionary that contains all the algorithms for the OLL sections. The computer recognises which case it has (given by the binary hash on the left),
    //and then performs the move algorithm given on the right. There are 57 cases in total + the solved case (111111110000). All hashes are missing leading 0s.
    private final Map<Short,String> orientationAlgs = Map.<Short, String>ofEntries(
            entry((short) 0b1010, "R U2 R2 F R F' U2 R' F R F'"),
            entry((short) 0b1001, "F R U R' U' F' B U L U' L' B'"),
            entry((short) 0b100000000000, "F U R U' R' F' U F R U R' U' F'"),
            entry((short) 0b101011, "F U R U' R' F' U' F R U R' U' F'"),
            entry((short) 0b110000010000, "R' F2 L F L' F R"),
            entry((short) 0b11100001110, "L F2 R' F' R F' L'"),
            entry((short) 0b10000110000, "L F R' F R F2 L'"),
            entry((short) 0b10110001101, "R' F' L F' L' F2 R"),
            entry((short) 0b10010011101, "R U R' U' R' F R2 U R' U' F'"),
            entry((short) 0b1001010000, "R U R' U R' F R F' R U2 R'"),
            entry((short) 0b101100000, "L' R2 B R' B R B2 R' B L R'"),
            entry((short) 0b1101001110, "F R U R' U' F' U F R U R' U' F'"),
            entry((short) 0b100110000, "F U R U' R2 F' R U R U' R'"),
            entry((short) 0b110011101, "R' F R U R' F' R F U' F'"),
            entry((short) 0b100100010000, "R' F' R L' U' L U R' F R"),
            entry((short) 0b1100011110, "L F L' R U R' U' L F' L'"),
            entry((short) 0b100010000001, "R U R' U R' F R F' U2 R' F R F'"),
            entry((short) 0b10100001, "F R U R' U F' U2 F' L F L'"),
            entry((short) 0b101000000010, "R' U2 F R U R' U' F2 U2 F R"),
            entry((short) 0b101010100000, "L' R B R B R' B' L2 R2 F R F' L'"),
            entry((short) 0b10101011010, "R U R' U R U' R' U R U2 R'"),
            entry((short) 0b10101011001, "R U2 R2 U' R2 U' R2 U2 R"),
            entry((short) 0b111101010100, "R2 D R' U2 R D' R' U2 R'"),
            entry((short) 0b11111010100, "L F R' F' L' F R F'"),
            entry((short) 0b110111010100, "R' F R B' R' F' R B"),
            entry((short) 0b11101011110, "R U2 R' U' R U' R'"),
            entry((short) 0b10101110000, "R U R' U R U2 R'"),
            entry((short) 0b111010110000, "L F R' F' L' R U R U' R'"),
            entry((short) 0b11010010100, "R U R' U' R U' R' F' U' F R U R'"),
            entry((short) 0b10010111000, "F R' F R2 U' R' U' R U R' F2"),
            entry((short) 0b11110000100, "R' U' F U R U' R' F' R"),
            entry((short) 0b1111000100, "R U B' U' R' U R B R'"),
            entry((short) 0b1110010100, "R U R' U' R' F R F'"),
            entry((short) 0b110111000, "R U R' U' B' R' F R F' B"),
            entry((short) 0b100111000100, "R U2 R2 F R F' R U2 R'"),
            entry((short) 0b100011010100, "R' U' R U' R' U R U R B' R' B"),
            entry((short) 0b110010010100, "F R U' R' U' R U R' F'"),
            entry((short) 0b11000110010, "R U R' U R U' R' U' R' F R F'"),
            entry((short) 0b1100110010, "L F' L' U' L U F U' L'"),
            entry((short) 0b100110010001, "R' F R U R' U' F' U R"),
            entry((short) 0b10010110001, "R U R' U R U2 R' F R U R' U' F'"),
            entry((short) 0b101001010100, "R' U' R U' R' U2 R F R U R' U' F'"),
            entry((short) 0b11110001000, "F' U' L' U L F"),
            entry((short) 0b110000110010, "F U R U' R' F'"),
            entry((short) 0b1110011000, "F R U R' U' F'"),
            entry((short) 0b110001100010, "R' U' R' F R F' U R"),
            entry((short) 0b10100000110, "F' L' U' L U L' U' L U F"),
            entry((short) 0b10000011001, "F R U R' U' R U R' U' F'"),
            entry((short) 0b1010110, "R B' R2 F R2 B R2 F' R"),
            entry((short) 0b10000010110, "R' F R2 B' R2 F' R2 B R'"),
            entry((short) 0b100010110, "F U R U' R' U R U' R' F'"),
            entry((short) 0b10001000110, "R' U' R U' R' U F' U F R"),
            entry((short) 0b10000011010, "R' F' L F' L' F L F' L' F2 R"),
            entry((short) 0b10100001010, "L F R' F R F' R' F R F2 L'"),
            entry((short) 0b10001001010, "R U2 R2 U' R U' R' U2 F R F'"),
            entry((short) 0b100011010, "L F L' U R U' R' U R U' R' L F' L'"),
            entry((short) 0b101110110000, "R U R' U' L R' F R F' L'"),
            entry((short) 0b111111110000, "") //This is the solved state so no algorithm needs to be performed
    );

    //Constructor (inherits from AICommon)
    public CFOP(Cube cube, Color[] colours, CubeController cubeController) {
        super(cube, colours, cubeController);
    }

    /*This is the main function that is used to solve the f2l. It is implemented with a lot of nested switch statements (not particularly elegant, but it is the best and easiest solution to code)
    based on factors such as the edge and corner indexes and orientations
    Don't worry if you don't understand what all the move algorithms mean they are just cubing algorithms that can be used in certain situations */
    static String solvePair(Cube cube, String sequence, int cornerIndex,
                            int cornerOrientation, int edgeIndex, int edgeOrientation, int colourOne){
        //if corners are at the bottom
        if(cornerIndex>3){
            //if edges are in the top layer we can do some algorithms to immediately create a pair in the top layer
            if(edgeIndex < 4){
                switch (cornerOrientation){
                    //if white is facing down; 11-edgeIndex-cornerIndex%4 is a way to retrieve where the edge is relative to the corner in the bottom right
                    case 1 -> {switch((11-edgeIndex-cornerIndex)%4){
                        //same side as each other. From then on edge is one more side to the right
                        case 0 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U2 F' U F",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"U R U' R'",sequence,cornerIndex-3);}
                            }
                        case 1 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U' F' U F",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"U2 R U' R'",sequence,cornerIndex-3);}
                            }
                        case 2 -> {
                            //if first colour on top
                            if(edgeOrientation==1){return performMoves(cube,"F' U F",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"U' R U' R'",sequence,cornerIndex-3);}
                            }
                        case 3 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U F' U F",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"R U' R'",sequence,cornerIndex-3);}
                            }
                        }
                    }
                    //if white is on the front face (when corner is held in bottom right)
                    case 2 -> {switch((11-edgeIndex-cornerIndex)%4){
                        case 0 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U' R U' R'",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"U2 R U' R'",sequence,cornerIndex-3);}
                        } case 1 -> {
                            if(edgeOrientation==1){return performMoves(cube,"R U' R'",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"U' R U' R'",sequence,cornerIndex-3);}
                        } case 2 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U R U' R'",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"R U' R'",sequence,cornerIndex-3);}
                        } case 3 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U2 R U' R'",sequence,cornerIndex-3);}
                            else{return performMoves(cube,"U R U' R'",sequence,cornerIndex-3);}
                            }
                        }
                    }
                    //if white on the right face
                    case 3 -> {switch((11-edgeIndex-cornerIndex)%4){
                        case 0 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U' R U' R'",sequence,cornerIndex-3);}
                            else {return performMoves(cube,"R U R'",sequence,cornerIndex-3);}
                        } case 1 -> {
                            if(edgeOrientation==1){return performMoves(cube,"R U' R'",sequence,cornerIndex-3);}
                            else {return performMoves(cube,"U R U R'",sequence,cornerIndex-3);}
                        } case 2 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U R U' R'",sequence,cornerIndex-3);}
                            else {return performMoves(cube,"U2 R U R'",sequence,cornerIndex-3);}
                        } case 3 -> {
                            if(edgeOrientation==1){return performMoves(cube,"U2 R U' R'",sequence,cornerIndex-3);}
                            else {return performMoves(cube,"U' R U R'",sequence,cornerIndex-3);}
                            }
                        }
                    }
                }
            }
            //if edges aren't in top layer just move the corner into the top layer, we can deal with the resulting position afterwards
            else {
                return performMoves(cube,"R U R'",sequence,cornerIndex-3);
                }
            }
        //If corner is in top layer then perform these algorithms
        else {
            //if edge isn't in top layer perform these algorithms
            if (edgeIndex > 3) {
                //first we need to move the corner over the edge
                switch((12-edgeIndex-cornerIndex)%4){
                    case 1 -> {return performMoves(cube, "U", sequence, 2);}
                    case 2 -> {return performMoves(cube, "U2", sequence, 2);}
                    case 3 -> {return performMoves(cube, "U'", sequence, 2);}
                }
                switch (cornerOrientation) {
                    //if white layer on top
                    case 1 -> {
                        if(edgeOrientation==1){return performMoves(cube, "U R U' R' U R U' R'", sequence, 4 - cornerIndex);}
                        else{return performMoves(cube, "R U' R'", sequence, 4 - cornerIndex);}
                    } //if white on right
                    case 2 -> {
                        if(edgeOrientation==1){return performMoves(cube, "U R U R'", sequence, 4 - cornerIndex);}
                        else{return performMoves(cube, "U F' U' F", sequence, 4 - cornerIndex);}
                    } //if white on front
                    case 3 -> {if(edgeOrientation==1){return performMoves(cube, "U' R U' R'", sequence, 4 - cornerIndex);}
                    else{return performMoves(cube, "U' R U R'", sequence, 4 - cornerIndex);}}
                }
            }
            //in all these algorithms they will be performed with the corner over the desired slot. U move cancellations can be done in post.
            switch ((9 - cornerIndex - colourOne) % 4) {
                case 1 -> {return performMoves(cube, "U", sequence, 2);}
                case 2 -> {return performMoves(cube, "U2", sequence, 2);}
                case 3 -> {return performMoves(cube, "U'", sequence, 2);}
            }
            switch (cornerOrientation) {
                //if white layer is on top
                case 1 -> {
                    switch ((4 + cornerIndex - edgeIndex) % 4) {
                        //if edge is one to the left of the corner
                        case 0 -> {
                            if (edgeOrientation == 1) {
                                return performMoves(cube, "U R U' R' U' R U' R' U R U' R'", sequence, 4 - cornerIndex);
                            } else {
                                return performMoves(cube, "F' U2 F U F' U' F", sequence, 4 - cornerIndex);
                            }
                        } //if edge is one to the right of the corner
                        case 1 -> {
                            if (edgeOrientation == 1) {
                                return performMoves(cube, "R U2 R' U' R U R'", sequence, 4 - cornerIndex);
                            } else {
                                return performMoves(cube, "U' F' U F U F' U F U' F' U F", sequence, 4 - cornerIndex);
                            }
                        } //if edge is two to the right of the corner
                        case 2 -> {
                            if (edgeOrientation == 1) {
                                return performMoves(cube, "U R U2 R' U R U' R'", sequence, 4 - cornerIndex);
                            } else {
                                return performMoves(cube, "U2 F' U' F U' F' U F", sequence, 4 - cornerIndex);
                            }
                        } //if edge is two the left of the corner
                        case 3 -> {
                            if (edgeOrientation == 1) {
                                return performMoves(cube, "U2 R U R' U R U' R'", sequence, 4 - cornerIndex);
                            } else {
                                return performMoves(cube, "U' F' U2 F U' F' U F", sequence, 4 - cornerIndex);
                            }
                        }
                    }
                } //If white corner is on right
                case 2 -> {
                    switch ((4 + cornerIndex - edgeIndex) % 4) {
                        //same as previously...
                        case 0 -> {
                            if(edgeOrientation==1){return performMoves(cube, "R U' R' U R U' R' U2 R U' R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "U' F' U F", sequence, 4 - cornerIndex);}
                        } case 1 -> {
                            if(edgeOrientation==1){return performMoves(cube, "U' R U' R' U R U R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "R U' R' U2 F' U' F", sequence, 4 - cornerIndex);}
                        } case 2 -> {
                            if(edgeOrientation==1){return performMoves(cube, "R U R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "U F' U2 F U F' U2 F", sequence, 4 - cornerIndex);}
                        } case 3 -> {
                            if(edgeOrientation==1){return performMoves(cube, "U' R U R' U R U R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "U F' U' F U F' U2 F", sequence, 4 - cornerIndex);}
                        }
                    }
                } //If white corner is in front
                case 3 -> {
                    switch ((4 + cornerIndex - edgeIndex) % 4) {
                        case 0 -> {
                            if(edgeOrientation==1){return performMoves(cube, "F' U F U2 R U R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "U F' U F U' F' U' F", sequence, 4 - cornerIndex);}
                        } case 1 -> {
                            if(edgeOrientation==1){return performMoves(cube, "U R U' R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "F' U F U' F' U F U2 F' U F", sequence, 4 - cornerIndex);}
                        } case 2 -> {
                            if(edgeOrientation==1){return performMoves(cube, "U' R U R' U2 R U' R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "U F' U' F U' F' U' F", sequence, 4 - cornerIndex);}
                        } case 3 -> {
                            if(edgeOrientation==1){return performMoves(cube, "U' R U2 R' U2 R U' R'", sequence, 4 - cornerIndex);}
                            else {return performMoves(cube, "F' U' F", sequence, 4 - cornerIndex);}
                        }
                    }
                }
            }
        }
        return null;
    }

    //functions F2L and findAndSolveEdges are each used to find the indexes and orientations of the desired edge and corner piece. From there it is solved
    static String findAndSolveEdges(Cube cube, String sequence, int[][] edgeIndexes, int cornerIndex, int cornerOrientation, int colourOne, int colourTwo){
        int edgeIndex = 0;
        for(int[] edge: edgeIndexes){
            if (cube.getColour(edge[0]/8,edge[0]%8) == colourOne && cube.getColour(edge[1]/8,edge[1]%8) == colourTwo){
                return solvePair(cube, sequence, cornerIndex, cornerOrientation,edgeIndex,1, colourOne);
            } else if (cube.getColour(edge[1]/8,edge[1]%8) == colourOne && cube.getColour(edge[0]/8,edge[0]%8) == colourTwo){
                return solvePair(cube, sequence, cornerIndex, cornerOrientation,edgeIndex,2, colourOne);
            }
            edgeIndex ++;
        }
        return sequence;
    }

    static String F2L(Cube originalCube, Cube cube, String originalSequence, int[][] cornerIndexes, int[][] edgeIndexes, int[][] sideOrders){
        ArrayList<String> sequences = new ArrayList<>();
        for(int[] permutation: sideOrders){
            Cube clone = cube.clone();
            String sequence = originalSequence;
            //iterates over the four corner edge pairs solving each one sequentially
            for(int i:permutation){
                //Checks whether the pair has been solved
                while (clone.getColour(5, 2 * (i-1)) != 6 || clone.getColour(i , 4) != 1 + i || clone.getColour((i % 4) + 1, 7) != (i % 4) + 2 || clone.getColour(i, 3) != 1 + i) {
                    int cornerIndex = 0;
                    for (int[] corner : cornerIndexes) {
                        if ((clone.getColour(corner[0] / 8, corner[0] % 8) == 6) && (clone.getColour(corner[1] / 8, corner[1] % 8) == 1 + i)) {
                            sequence = findAndSolveEdges(clone, sequence, edgeIndexes, cornerIndex, 1, 1 + i, i % 4 + 2);
                            break;
                        } else if ((clone.getColour(corner[1] / 8, corner[1] % 8) == 6) && (clone.getColour(corner[2] / 8, corner[2] % 8) == 1 + i)) {
                            sequence = findAndSolveEdges(clone, sequence, edgeIndexes, cornerIndex, 2, 1 + i, i % 4 + 2);
                            break;
                        } else if ((clone.getColour(corner[2] / 8, corner[2] % 8) == 6) && (clone.getColour(corner[0] / 8, corner[0] % 8) == 1 + i)) {
                            sequence = findAndSolveEdges(clone, sequence, edgeIndexes, cornerIndex, 3, 1 + i, i % 4 + 2);
                            break;
                        }
                        cornerIndex++;
                    }
                }
            }
            sequences.add(cancelMoves(sequence));
        }
        int moveLength = 999; //set to arbitrarily high value
        String bestSequence = "";
        //searches for the shortest solution
        for(String crossSequence:sequences){
            if(crossSequence.split(" ").length < moveLength){
                bestSequence = crossSequence;
                moveLength = crossSequence.split(" ").length;
            }
        }
        return performMoves(originalCube,bestSequence,"",2);
    }
    /*Hashes a code from the current cube orientation. Achieved by setting any values by recording a 16-bit short integer
    where a 1 represents the presence of a yellow sticker and 0 representing a different colour. The sticker value are checked for the 8
    non-center stickers on the top, and 4 stickers on the side (only need two stickers of corner to figure out orientation, and only one sticker for the edge
    Thus you need 2*4+1*4=12 bits necessary; technically you can shave off more, but it's fine as 12 bits)*/
    static short getOLLHash(Cube cube){
        int hash = 0;
        for(int i = 0;i<8;i++){
            hash = hash<<1;
            if(cube.getColour(0,i)==1){hash|=1;}//changes last bit to 1
        }
        for(int i = 1;i<5;i++){
            hash = hash<<1;
            if(cube.getColour(i,0)==1){hash|=1;}//changes last bit to 1
        }
        return (short)hash;
    }

    // Solves the OLL section of the method
    static String OLL(Cube cube, String sequence, Map<Short,String> orientationAlgs){
        int errorChecker = 0; /*This is a tracker I put in for the scenario where there has been an error.
        It makes sure that the thread doesn't run indefinitely which would be a big hit to performance.
        Instead, if the user makes 4 U rotations the program returns a null solution which whilst it isn't ideal
        it is better than the alternative. In practice this program is extremely difficult to debug due to the shear amount of states
        the cube can be in (43 quintillion). Therefore, it is practically impossible to fully test the program. In theory the AI should work every time,
        but I put this in just in case.*/
        //performs U rotations until the top face matches one of the orientations (this deals with rotations)
        while(!orientationAlgs.containsKey(getOLLHash(cube))){
            sequence = performMoves(cube,"U",sequence,2);
            errorChecker++;
            if(errorChecker>=4){
                System.out.println("I have made a severe and continuous lapse of my judgement");
                return null;
            }
        }
        return performMoves(cube,orientationAlgs.get(getOLLHash(cube)),sequence,2);
    }

    /*hashing algorithm which converts a permutation of the last layer into a unique 16-bit integer.
    It functions by first setting the first sticker of the red face as the 'default' colour. From then on the other colour codes are
    derived from their centers position relative to the default colour. e.g. if the first sticker on the red side is green, all green stickers are 0, orange is 1,
    blue is 2 and red is 3. From there we just cycle right and record the values found. We only need to keep track of the first 2 stickers of each side to create a
    unique hash. Since each colour can be encoded with 2 bits this generates a 2*8=16-bit hash*/
    static short getPLLHash(Cube cube){
        int defaultColour = cube.getColour(1,0);
        int hash = 0;
        //to get colour offset we can add perform this calculation: (colour + 4 - default colour)%4
        //for loop cycles through sides from red to blue
        for(int i = 1;i<5;i++){
            hash=hash<<2;
            hash|=((cube.getColour(i,0) + 4 - defaultColour)%4);
            hash=hash<<2;
            hash|=((cube.getColour(i,1) + 4 - defaultColour)%4);
        }
        return (short)hash;
    }

    // Solves the PLL section of the method
    static String PLL(Cube cube, String sequence, Map<Short,String> permutationAlgs){
        int errorChecker = 0;
        while(!permutationAlgs.containsKey(getPLLHash(cube))){
            sequence = performMoves(cube,"U",sequence,2);
            errorChecker++;
            if(errorChecker>=4){
                System.out.println("I have made a severe and continuous lapse of my judgement");
                return null;
            }
        }
        sequence = performMoves(cube,permutationAlgs.get(getPLLHash(cube)),sequence,2);
        //fixes the top layer after algorithm is complete
        switch (cube.getColour(2,1)){
            case 2 -> {
                return performMoves(cube, "U",sequence,2);
            } case 3 -> {
                return sequence;
            } case 4 -> {
                return performMoves(cube, "U'",sequence,2);
            } case 5 -> {
                return performMoves(cube, "U2",sequence,2);
            }
        }
        return sequence;
    }

    // Solves the cube by sequentially calling the different stages of the method
    static String solve (Cube cube,int[][] cornerIndexes, int[][] edgeIndexes, Map<Short,String> orientationAlgs, Map<Short,String> permutationAlgs, int[][] sideOrders) {
        Cube original = cube.clone();
        String solution = whiteCross(cube, edgeIndexes, sideOrders);
        solution = F2L(original,cube,solution, cornerIndexes,edgeIndexes, sideOrders);
        solution = OLL(original,cancelMoves(solution),orientationAlgs);
        return cancelMoves(Objects.requireNonNull(PLL(original, solution, permutationAlgs)));
    }

    //Main function that is called on CFOP.start()
    @Override
    public void run(){
        String solution = solve(cube.clone(),cornerIndexes,edgeIndexes, orientationAlgs, permutationAlgs, sideOrders); // Gets solution
        cubeController.setSolution(solution); // Sets the solution in cubeController to the new solution
    }

}
