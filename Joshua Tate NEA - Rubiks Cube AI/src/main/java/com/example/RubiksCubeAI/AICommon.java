package com.example.RubiksCubeAI;

import javafx.scene.paint.Color;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

//This is a class that Beginner and CFOP inherit from. It contains a bunch of helper functions along with the cross section of the solve since that is identical between the two methods
public class AICommon {

    Cube cube;
    Color[] colours;
    float duration;


/*I could have used heaps algorithm for this but since its only 24 permutations I opted just to type them by hand
This 2d array stores all possible side order permutations. The AI will solve each stage in all these orders and then select which ever completes it first as the
most optimal (using the algorithms I chose) solution*/
    final int[][] sideOrders = {
            {1,2,3,4},{1,2,4,3},{1,3,2,4},{1,3,4,2},{1,4,2,3},{1,4,3,2},
            {2,1,3,4},{2,1,4,3},{2,3,1,4},{2,3,4,1},{2,4,1,3},{2,4,3,1},
            {3,1,2,4},{3,1,4,2},{3,2,1,4},{3,2,4,1},{3,4,1,2},{3,4,2,1},
            {4,1,2,3},{4,1,3,2},{4,2,1,3},{4,2,3,1},{4,3,1,2},{4,3,2,1}
    };
   // final int[][] sideOrders = {{1,3,2,4}};
    final int[][] edgeIndexes = {{1,33},{3,25},{5,17},{7,9},{35,15},{11,23},{19,31},{27,39},{41,21},{43,29},{45,37},{47,13}};
    final int[][] cornerIndexes = {{0,8,34},{2,32,26},{4,24,18},{6,16,10},{40,12,22},{42,20,30},{44,28,38},{46,36,14}};

    public String[] solutionContainer;
    public CyclicBarrier barrier;
    public AICommon(Cube cube, Color[] colours, float duration, String[] solutionContainer, CyclicBarrier barrier){
        this.cube = cube;
        this.colours = colours;
        this.duration = duration;
        this.solutionContainer = solutionContainer;
        this.barrier = barrier;
    }
    static String moveShift(String move, int shift){
        StringBuilder moveBuilder = new StringBuilder(move);
        char[] faceOrder = {'L','F','R','B'};
        for(int i = 0;i<=3;i++){
            if(move.charAt(0)==faceOrder[i]){
                moveBuilder.setCharAt(0,faceOrder[((((i+shift)%4)+4)%4)]);
            }
        }
        return moveBuilder.toString();
    }
    //When given an algorithm such as 'R U  R'' it will perform those moves according to the given front face
    //Throughout the solve, white will be on bottom, so we only need to consider 4 situations of which face is to the front
    static String performMoves(Cube cube, String moves, String sequence, int frontFace){
        StringBuilder sequenceBuilder = new StringBuilder(sequence);
        for(String move:moves.split(" ")){
            switch(move){
                case "U" ->{cube.clockwise(0);
                    sequenceBuilder.append(" ").append(move);}
                case "U'" ->{cube.antiClockwise(0);
                    sequenceBuilder.append(" ").append(move);}
                case "U2" ->{cube.doubleTwist(0);
                    sequenceBuilder.append(" ").append(move);}
                //some math to account for the front face when applying the move
                case "L" ->{cube.clockwise(((((frontFace-2)%4)+4)%4)+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "L'" ->{cube.antiClockwise(((((frontFace-2)%4)+4)%4)+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "L2" ->{cube.doubleTwist(((((frontFace-2)%4)+4)%4)+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "F" ->{cube.clockwise((frontFace));
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "F'" ->{cube.antiClockwise(frontFace);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "F2" ->{cube.doubleTwist(frontFace);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "R" ->{cube.clockwise((frontFace)%4+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "R'" ->{cube.antiClockwise((frontFace)%4+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "R2" ->{cube.doubleTwist((frontFace)%4+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "B" ->{cube.clockwise((frontFace+1)%4+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "B'" ->{cube.antiClockwise((frontFace+1)%4+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "B2" ->{cube.doubleTwist((frontFace+1)%4+1);
                    sequenceBuilder.append(" ").append(moveShift(move,(frontFace-2)));}
                case "D" ->{cube.clockwise(5);
                    sequenceBuilder.append(" ").append(move);}
                case "D'" ->{cube.antiClockwise(5);
                    sequenceBuilder.append(" ").append(move);}
                case "D2" ->{cube.doubleTwist(5);
                    sequenceBuilder.append(" ").append(move);}
            }
        }
        return sequenceBuilder.toString();
    }
    //solves the edge given a white edge with its corresponding colour
    static String solveEdge(Cube cube, int whiteIndex, int colour, String sequence, int offset){
        //if white sticker is on either the left, front, right or back faces
        if(whiteIndex >= 8 && whiteIndex <40){
            final int disturbedColourRightFace = (4 + whiteIndex / 8 - offset) % 4 + 2;
            final int disturbedColourSameFace = (whiteIndex / 8 - offset + 3) % 4 + 2;
            switch(whiteIndex%8){
                //when white sticker on top side of face
                case 1 -> {
                    switch((((whiteIndex/8)-(colour-1))%4+4)%4){
                        //white sticker is on the same side as the side we want to insert onto
                        case 0 -> {if (cube.getColour((whiteIndex/8)%4+1,5)== disturbedColourRightFace &&cube.getColour(5,(((whiteIndex/8)%4+2)*2-1)%8)==6)
                        {return performMoves(cube,"U' R' F R",sequence,colour-1);}
                        else{return performMoves(cube,"U R' F'",sequence,colour-1);}} //Checks whether the left edge has been solved yet. If it has set perform L'.
                        //one side to the right
                        case 1 -> {if(cube.getColour(whiteIndex/8,5)== disturbedColourSameFace &&cube.getColour(5,(((whiteIndex/8)+2)%4)*2+1)==6){
                            return performMoves(cube,"R' F R",sequence,colour-1);}
                        else {
                            return performMoves(cube,"R' F",sequence,colour-1);}}
                        //other side
                        case 2 -> {if(cube.getColour((whiteIndex/8)%4+1,5)== disturbedColourRightFace &&cube.getColour(5,(((whiteIndex/8)%4+2)*2-1)%8)==6){return performMoves(cube,"U L F' L'",sequence,colour-1);}
                        else {return performMoves(cube,"U L F'",sequence,colour-1);}}
                        //one side to the left
                        case 3 -> {if(cube.getColour(whiteIndex/8,5)== disturbedColourSameFace &&cube.getColour(5,(((whiteIndex/8)+2)%4)*2+1)==6){return performMoves(cube,"L F' L'",sequence,colour-1);}
                        else {return performMoves(cube,"L F'",sequence,colour-1);}}
                    }}
                //when white sticker on right side of face
                case 3 -> {if(cube.getColour((whiteIndex/8)%4+1,5)== disturbedColourRightFace &&cube.getColour(5,((whiteIndex/8)*2-1))==6){return performMoves(cube,"R U R'",sequence,whiteIndex/8);}
                else{return performMoves(cube,"R",sequence,whiteIndex/8);}}
                //when white sticker on bottom side of face
                case 5 -> {return performMoves(cube,"F2",sequence,whiteIndex/8);}
                //when white sticker on left side of face
                case 7 -> {if (cube.getColour(((whiteIndex/8)+2)%4+1,5)==((whiteIndex/8-offset)+6)%4+2&&cube.getColour(5,(((whiteIndex/8)%4+2)*2-1)%8)==6){return performMoves(cube,"L' U' L",sequence,whiteIndex/8);}
                else{return performMoves(cube,"L'",sequence,whiteIndex/8);}}
            }
        }
        //if white sticker on bottom side
        if(whiteIndex>=40){
            return performMoves(cube,"F2",sequence,((((whiteIndex+2)%8)+1)/2));
        }
        //if white sticker on top side
        switch((((((9-whiteIndex)/2)-(colour - 1))%4)+4)%4){
            //if colour sticker on the same face as it's center
            case 0 -> {return performMoves(cube,"F2",sequence,(colour-1));}
            case 1 -> {return performMoves(cube,"U F2",sequence,(colour-1));}
            case 2 -> {return performMoves(cube,"U2 F2",sequence,(colour-1));}
            case 3 -> {return performMoves(cube,"U' F2",sequence,(colour-1));}
        }
        return null;
    }

    //This solution is by no means the shortest but since the human method of solving the cross is highly intuition based
    //it is difficult to code in all the scenarios. Usually there would be shortcuts but there would be too many scenarios to code in
    static String whiteCross(Cube cube, int[][] edgeIndexes, int[][] sideOrders){
        String sequence;
        ArrayList<String> sequences = new ArrayList<>();
        for(int offset = 0; offset<4;offset++){
            //iterates through each edge from the green edge to the red edge solving each one at a time
            for(int[] permutation: sideOrders){
                Cube clone = cube.clone();
                sequence = "";
                for(int i:permutation){
                    while (clone.getColour(5, 2*((i+offset-1)%4)+1) != 6 || clone.getColour(((i+offset)%4)+1, 5) != 2+(i%4)) {
                        //loops through edges until we find the matching edge
                        for (int[] edge : edgeIndexes) {
                            if (clone.getColour(edge[0]/8,edge[0]%8) == 6 && clone.getColour(edge[1]/8,edge[1]%8) == 2+(i%4)) {
                                //despite the name this doesn't always solve the edge. Mostly it is used to move the edge into a position where it can then be solved
                                //this then repeats until the edge is solved
                                sequence = solveEdge(clone, edge[0], 2+((i+offset)%4), sequence, offset);
                                break;
                            } else if (clone.getColour(edge[0]/8,edge[0]%8) == 2+(i%4) && clone.getColour(edge[1]/8,edge[1]%8) == 6) {
                                sequence = solveEdge(clone, edge[1], 2+((i+offset)%4), sequence, offset);
                                break;
                            }
                        }
                    }
                }
                //deals with bottom layer offset
                switch (offset){
                    case 1 -> sequence=performMoves(clone, "D'",sequence,2);
                    case 2 -> sequence=performMoves(clone, "D2",sequence,2);
                    case 3 -> sequence=performMoves(clone, "D",sequence,2);
                }
                assert sequence != null;
                sequences.add(cancelMoves(sequence));
            }
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
        return performMoves(cube,bestSequence,"",2);
    }

    //calculates how much the move rotates the side clockwise
    static int moveValue(String move){
        if(move.length() == 1){return 1;} //e.g. if U
        if(move.charAt(1)=='2'){return 2;} //e.g. if U2
        return 3; //e.g. if U'
    }

    //The goal of this function is to cancel down all the moves. Since the AI uses a lot of U moves to get into certain positions this just removes them along with cancelling
    //down some other things if possible
    static String cancelMoves(String sequence){
        Stack<String> cancelledMoves = new Stack<>();
        for(String move:sequence.split(" ")){
            if(Objects.equals(move, "")){continue;}
            if(cancelledMoves.size()==0){cancelledMoves.push(move);continue;}//avoids error due to empty stack
            String lastMove = cancelledMoves.pop();
            if((move.charAt(0)==lastMove.charAt(0))){
                switch ((moveValue(move)+moveValue(lastMove))%4){
                    case 1 -> cancelledMoves.push(String.valueOf(move.charAt(0)));
                    case 2 -> cancelledMoves.push(move.charAt(0)+"2");
                    case 3 -> cancelledMoves.push(move.charAt(0)+"'");
                }
            } else {cancelledMoves.push(lastMove); cancelledMoves.push(move);}
        }
        StringBuilder newSequence = new StringBuilder();
        for(int i=cancelledMoves.size();i>0;i--){
            newSequence.insert(0, " "+cancelledMoves.pop());
        }
        return newSequence.toString();
    }
}
