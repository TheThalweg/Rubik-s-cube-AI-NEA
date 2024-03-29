package com.example.RubiksCubeAI;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Objects;


/*Ai which solves the cube using beginners method. This method is very heavy on switch statements however since the
algorithm runs pretty linearly this doesn't matter too much. It wills still execute quickly
method relies on moving pieces to certain places using pre-known move sequences called 'algorithms' until they are in the correct place*/
public class Beginners extends AICommon implements Runnable{
    public Beginners(Cube cube, Color[] colours, CubeController cubeController) {
        super(cube, colours,  cubeController);
    }
    static String solveCorner(Cube cube, int whiteIndex, int colourOne, String sequence){
        //if white sticker on bottom
        if(whiteIndex>=40){
            return performMoves(cube,"L' U' L", sequence, (((whiteIndex+2)%8)/2)+1);
        }
        //if white sticker on top
        if(whiteIndex<8){
            //i.e. if the corner below that is solved perform the algorithm that preserves that corner
            if(cube.getColour(5,(6-(whiteIndex)))==6&&cube.getColour((8-whiteIndex)/2,4)==(8-whiteIndex)/2+1){
                return performMoves(cube,"R U' R' F R' F' R", sequence, ((8-whiteIndex)/2));
            } else { //if the corner below isn't solved we don't have to bother preserving it
                return performMoves(cube,"R U' R'", sequence, ((8-whiteIndex)/2));
            }

        }
        //if white is on left, right, front or back
        final int whitePos = (((colourOne - 1) - (whiteIndex / 8)) % 4 + 4) % 4;
        switch (whiteIndex%8){
            //if white sticker in top left

            case 0 -> {
                switch (whitePos){
                    //white sticker is in top left of the side it needs to go into (goes into bottom right corner)
                    case 0 -> {
                        return performMoves(cube, "U' R U R'", sequence, colourOne-1);
                    } //white sticker is on face to the left
                    case 1 -> {
                        return performMoves(cube, "F' U2 F", sequence, colourOne - 1);
                    } //white sticker is on the opposite side
                    case 2 -> {
                        return performMoves(cube, "F' U F", sequence, colourOne - 1);
                    } //white sticker is on the face to the right
                    case 3 -> {
                        return performMoves(cube, "R U R'", sequence, colourOne - 1);
                    }
                }
            } //if sticker is in top right
            case 2 -> {
                switch (whitePos){
                    //white sticker is in the top right of the side it needs to go into (goes into bottom right corner)
                    case 0->{
                        return performMoves(cube, "F' U' F", sequence, colourOne - 1);
                    }//white sticker is on the face to the left
                    case 1 -> {
                        return performMoves(cube, "R U' R'", sequence, colourOne - 1);
                    } //white sticker is on opposite side
                    case 2 -> {
                        return performMoves(cube,"R U2 R'", sequence, colourOne-1);
                    } //white sticker is on the face to the right
                    case 3 -> {
                        return performMoves(cube,"U' R U2 R'", sequence, colourOne-1);
                    }
                }
            } //if sticker is on the bottom right
            case 4 -> {
                return performMoves(cube, "R U' R'", sequence, whiteIndex/8);
            } //if sticker is on the bottom left
            case 6 -> {
                return performMoves(cube, "L' U L", sequence, whiteIndex/8);
            }
        }
        return null;
    }
    static String doCorners(Cube originalCube, Cube cube, String originalSequence, int[][] cornerIndexes, int[][] sideOrders){
        ArrayList<String> sequences = new ArrayList<>();
        ////iterates over each side order permutation
        for(int[] permutation: sideOrders) {
            Cube clone = cube.clone();
            String sequence = originalSequence;
            for (int i:permutation) {
                //we only actually have to check two of the sides for the corners. The third one can be determined just from these.
                while (clone.getColour(5, 2 * (i-1)) != 6 || clone.getColour(i, 4) != 1 + i) {
                    for (int[] corner : cornerIndexes) {
                        if ((clone.getColour(corner[0] / 8, corner[0] % 8) == 6) && (clone.getColour(corner[1] / 8, corner[1] % 8) == 1 + i)) {
                            sequence = solveCorner(clone, corner[0], 1 + i, sequence);
                            break;
                        } else if ((clone.getColour(corner[1] / 8, corner[1] % 8) == 6) && (clone.getColour(corner[2] / 8, corner[2] % 8) == 1 + i)) {
                            sequence = solveCorner(clone, corner[1], 1 + i, sequence);
                            break;
                        } else if ((clone.getColour(corner[2] / 8, corner[2] % 8) == 6) && (clone.getColour(corner[0] / 8, corner[0] % 8) == 1 + i)) {
                            sequence = solveCorner(clone, corner[2], 1 + i, sequence);
                            break;
                        }
                    }
                }
            }
            assert sequence != null;
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

    //logic for how to insert a given edge
    static String insertEdge(Cube cube, int edgeOne, int edgeTwo, int colourOne, String sequence){
        //if edge is stuck in the right edge slot when it shouldn't be there
        if(edgeOne/8>0&&edgeOne%8==3){
            return performMoves(cube,"R U' R' F R' F' R", sequence, edgeOne/8);
        } //if edge is stuck in the left edge slot when it shouldn't be there
        else if(edgeOne/8>0&&edgeOne%8==7){
            return performMoves(cube,"L' U L F' L F L'", sequence, edgeOne/8);
        }
        //if the second edge is on top
        if(edgeTwo/8==0){
            switch((((colourOne-1)-edgeOne/8)+4)%4){
                //same side as colourOne
                case 0 ->{
                    return performMoves(cube, "U R U' R' F R' F' R", sequence, colourOne-1);
                } //on the side to the left etc.
                case 1 ->{
                    return performMoves(cube, "R U' R' F R' F' R", sequence, colourOne-1);
                } case 2 ->{
                    return performMoves(cube, "U' R U' R' F R' F' R", sequence, colourOne-1);
                } case 3 ->{
                    return performMoves(cube, "U2 R U' R' F R' F' R", sequence, colourOne-1);
                }
            }
        }
        //if the first edge is on top
        switch ((((colourOne-1)-edgeTwo/8)+4)%4){
            //you get the drill by now
            case 0 -> {return performMoves(cube,"U2 R' F R F' R U R'", sequence, colourOne-1);}
            case 1 -> {return performMoves(cube,"U R' F R F' R U R'", sequence, colourOne-1);}
            case 2 -> {return performMoves(cube,"R' F R F' R U R'", sequence, colourOne-1);}
            case 3 -> {return performMoves(cube,"U' R' F R F' R U R'", sequence, colourOne-1);}
        }
        return null;
    }

    // Runs through all the edges and inserts them into their relevant slots. Simulate all insertion orders and pick whichever one is fastest.
    static String insertEdges(Cube originalCube, Cube cube, String originalSequence, int[][]edgeIndexes, int[][]sideOrders){
        ArrayList<String> sequences = new ArrayList<>();
        for(int[] permutation: sideOrders) {
            Cube clone = cube.clone();
            String sequence = originalSequence;
            for(int i : permutation){
                while(clone.getColour((i%4)+1,7)!=(i%4)+2 || clone.getColour(i,3)!=1+i){
                    for(int[] edge:edgeIndexes){
                        if (clone.getColour(edge[0]/8,edge[0]%8) == 1+i && clone.getColour(edge[1]/8,edge[1]%8) == (i%4)+2){
                            sequence = insertEdge(clone, edge[0],edge[1],1+i,sequence);
                            break;
                        } else if (clone.getColour(edge[0]/8,edge[0]%8) == (i%4)+2 && clone.getColour(edge[1]/8,edge[1]%8) == 1+i){
                            sequence = insertEdge(clone, edge[1],edge[0],1+i,sequence);
                            break;
                        }
                    }
                }
            }
            assert sequence != null;
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

    // Solves the yellow cross
    static String yellowCross(Cube cube, String sequence){
        int errorChecker = 0; //Used in case the AI has made an error for whatever reason. Just makes sure the thread doesn't run indefinitely
        //if not solved
        while((cube.sides[0]&0x0F0F0F0F)!=0x01010101){
            //checks for dot case
            for(int i=1;i<8;i+=2){
                //if any edges are yellow check whether it is a line or a corner. If neither perform a U move
                if(cube.getColour(0,i)==1){
                    while(cube.getColour(0,7)!=1||(cube.getColour(0,1)!=1&&cube.getColour(0,3)!=1)) {
                        errorChecker++;
                        if(errorChecker>12){
                            System.out.println("Oh dear");
                            return null;}
                    sequence = performMoves(cube,"U",sequence,2);}
                }
            }
            //perform an algorithm to move it into the next case. This repeats until the cross is solved
            sequence = performMoves(cube,"F R U R' U' F'",sequence,2);
            errorChecker++;
            if(errorChecker>12){
                System.out.println("Oh dear");
                return null;}
        }
        return sequence;
    }

    //permutes the edges of the cross so that they are all in order
    static String permuteCross(Cube cube, String sequence){
        ArrayList<Integer> solvedEdges = new ArrayList<>();
        int errorChecker = 0;
        while (true) {
            //counts up which edges are in the right place. (one number means two consecutive edges are in the right place)
            for (int i = 1; i <= 4; i++) {
                if ((cube.getColour(i, 1) - 1) % 4 + 1 == cube.getColour((i % 4) + 1, 1) - 1) {
                    solvedEdges.add(i);
                }
            }
            //if all the edges already in correct place
            if(solvedEdges.size()==4){return sequence;}
            if(solvedEdges.size()==1){
                //first we need to line up the two correct edges
                switch (solvedEdges.get(0)){
                    case 1 -> sequence=performMoves(cube,"U2",sequence,2);
                    case 2 -> sequence=performMoves(cube,"U'",sequence,2);
                    case 4 -> sequence=performMoves(cube,"U",sequence,2);
                }
                return performMoves(cube,"R U R' U R U2 R'",sequence,2);
            }
            //if none of the edges are solved perform a naive sune algorithm to flip two edges and try again
            sequence = performMoves(cube,"R U R' U R U2 R'",sequence,2);
            errorChecker++;
            if(errorChecker>2){
                System.out.println("Come on man");
                return null;
            }
        }
    }

    //permutes the corners of the yellow side until they are in the right position
    static String permuteCorners(Cube cube, String sequence, int[][] cornerIndexes){
        ArrayList<Integer> permutedCorners = new ArrayList<>();
        int errorChecker = 0;
        while(true) {
            /*((4 - i) % 4 + 1 + cube.getColour(2,1))%4 +2) is a way to retrieve the correct orientation colour given how much the U face is turned and the index of the corner
            I won't bother to try to explain the maths but basically the i value gives the corner index and the getColour value gives the rotation of the U face.
            Given these values you can use that formula to find the desired colour that we want the sticker clockwise of the yellow sticker to be
             (I arrived at that formula through trial and error until I found a formula that worked for every possible situation)*/
            for (int i = 0; i < 4; i++) {
                if ((cube.getColour(cornerIndexes[i][0] / 8, cornerIndexes[i][0] % 8) == 1) && (cube.getColour(cornerIndexes[i][1] / 8, cornerIndexes[i][1] % 8) == ((4 - i) % 4 + 1 + cube.getColour(2,1))%4 +2) ||
                        (cube.getColour(cornerIndexes[i][1] / 8, cornerIndexes[i][1] % 8) == 1) && (cube.getColour(cornerIndexes[i][2] / 8, cornerIndexes[i][2] % 8) == ((4 - i) % 4 + 1 + cube.getColour(2,1))%4 +2) ||
                        (cube.getColour(cornerIndexes[i][2] / 8, cornerIndexes[i][2] % 8) == 1) && (cube.getColour(cornerIndexes[i][0] / 8, cornerIndexes[i][0] % 8) == ((4 - i) % 4 + 1 + cube.getColour(2,1))%4 +2)) {
                    permutedCorners.add(i);
                }
            }
            //if every corner is already in correct place just return sequence without doing anything
            if (permutedCorners.size() == 4) {
                return sequence;
            }
            //if only one edge is in the right place, move to the front top left corner and perform algorithm
            if (permutedCorners.size() == 1) {
                //Performs U rotations to move edge into right position, so it can be slotted in using the algorithm
                switch(permutedCorners.get(0)){
                    case 0 -> sequence = performMoves(cube,"U'",sequence,2);
                    case 1 -> sequence = performMoves(cube,"U2",sequence,2);
                    case 2 -> sequence = performMoves(cube,"U",sequence,2);
                }
            }
            //Algorithm to insert edge. If none of the edges are in the right place perform a naive performance of the algorithm and then repeat the process
            sequence = performMoves(cube,"R U' L' U R' U' L",sequence,2);
            permutedCorners.clear();
            errorChecker ++;
            if(errorChecker>3){return null;}
        }

    }

    // Orients the corners to create a solved cube
    static String orientCorners(Cube cube, String sequence){
        //for every single corner
        int errorChecker = 0;
        for(int i = 0;i<4;i++){
            //while not orientated correctly
            while(cube.getColour(0,4)!=1){
                sequence = performMoves(cube, "R' D' R D R' D' R D",sequence,2);
                errorChecker++;
                if(errorChecker>4){return null;}
            }
            errorChecker = 0;
            sequence = performMoves(cube, "U",sequence,2);
        }
        //final rotation to fix the top layer
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
        //just to get rid of compiler error
        return sequence;
    }

    // Calls each step sequentially, gradually adding on to the solution
    static String solve(Cube cube,int[][] cornerIndexes, int[][] edgeIndexes, int[][] sideOrders){
        Cube original = cube.clone();
        Cube cornersDone = cube.clone();
        String solution = whiteCross(cube, edgeIndexes, sideOrders);
        solution = doCorners(cornersDone,cube,solution, cornerIndexes, sideOrders);
        solution = insertEdges(original,cornersDone, solution, edgeIndexes,sideOrders);
        solution = yellowCross(original,solution);
        solution = permuteCross(original,solution);
        solution = permuteCorners(original,solution, cornerIndexes);
        return cancelMoves(Objects.requireNonNull(orientCorners(original, solution)));
    }

    // Main function called on beginners.start()
    @Override
    public void run(){
        String solution = solve(cube.clone(),cornerIndexes,edgeIndexes, sideOrders); // Solves the cube and retrieves solution
        cubeController.setSolution(solution); // Sets the solution in cubeController to the new solution
    }
}
