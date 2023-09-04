package com.example.demo;

import javafx.scene.paint.Color;

import java.util.ArrayList;

//Ai which solves the cube using beginners method. This method is very heavy on switch statements however since the
//algorithm runs pretty linearly this doesn't matter too much. It wills still execute quickly
//method relies on moving pieces to certain places using pre-known move sequences called 'algorithms' until they are in the correct place
public class Beginners extends Thread{
    Cube cube;
    CubeGUI cubeGUI;
    Color[] colours;
    float duration;

    final int[][] edgeIndexes = {{1,33},{3,25},{5,17},{7,9},{35,15},{11,23},{19,31},{27,39},{41,21},{43,29},{45,37},{47,13}};
    final int[][] cornerIndexes = {{0,8,34},{2,32,26},{4,24,18},{6,16,10},{40,12,22},{42,20,30},{44,28,38},{46,36,14}};
    public Beginners(Cube cube, CubeGUI cubeGUI, Color[] colours, float duration){
        this.cube = cube;
        this.cubeGUI = cubeGUI;
        this.colours = colours;
        this.duration = duration;
    }

    static void performMove(Cube cube, int[] move){
        switch (move[1]) {
            case 1 -> cube.clockwise(move[0]);
            case 3 -> cube.antiClockwise(move[0]);
            case 2 -> cube.doubleTwist(move[0]);
        }
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
    static String solveEdge(Cube cube, int whiteIndex, int colour, String sequence){
        //if white sticker is on either the left, front, right or back faces
        if(whiteIndex >= 8 && whiteIndex <40){
            switch(whiteIndex%8){
                //when white sticker on top side of face
                case 1 -> {
                    switch((((whiteIndex/8)-(colour-1))%4+4)%4){
                    //white sticker is on the same side as the side we want to insert onto
                    case 0 -> {return performMoves(cube,"U L F' L'",sequence,colour-1);}
                    //one side to the right
                    case 1 -> {return performMoves(cube,"R' F R",sequence,colour-1);}
                    //other side
                    case 2 -> {return performMoves(cube,"U R' F R",sequence,colour-1);}
                    //one side to the left
                    case 3 -> {return performMoves(cube,"L F' L'",sequence,colour-1);}
                }}
                //when white sticker on right side of face
                case 3 -> {return performMoves(cube,"R U R'",sequence,whiteIndex/8);}
                //when white sticker on bottom side of face
                case 5 -> {return performMoves(cube,"F2",sequence,whiteIndex/8);}
                //when white sticker on left side of face
                case 7 -> {return performMoves(cube,"L' U' L",sequence,whiteIndex/8);}
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
    static String whiteCross(Cube cube, int[][] edgeIndexes){
        String sequence = "";
        //iterates through each edge from the green edge to the red edge solving each one at a time
        for(int i = 1; i<=4; i++){
            while (cube.getColour(5, 2*i-1) != 6 || cube.getColour((i%4)+1, 5) != 2+(i%4)) {
                //loops through edges until we find the matching edge
                for (int[] edge : edgeIndexes) {
                    if (cube.getColour(edge[0]/8,edge[0]%8) == 6 && cube.getColour(edge[1]/8,edge[1]%8) == 2+(i%4)) {
                        //despite the name this doesn't always solve the edge. Mostly it is used to move the edge into a position where it can then be solved
                        //this then repeats until the edge is solved
                        sequence = solveEdge(cube, edge[0], 2+(i%4), sequence);
                        break;
                    } else if (cube.getColour(edge[0]/8,edge[0]%8) == 2+(i%4) && cube.getColour(edge[1]/8,edge[1]%8) == 6) {
                        sequence = solveEdge(cube, edge[1], 2+(i%4), sequence);
                        break;
                    }
                }
            }
        }
        return sequence;
    }

    static String solveCorner(Cube cube, int whiteIndex, int colourOne, String sequence){
        //if white sticker on bottom
        if(whiteIndex>=40){
            return performMoves(cube,"L' U' L", sequence, (((whiteIndex+2)%8)/2)+1);
        }
        //if white sticker on top
        if(whiteIndex<8){
            return performMoves(cube,"R U' R' F R' F' R", sequence, ((8-whiteIndex)/2));
        }
        //if white is on left, right, front or back
        switch (whiteIndex%8){
            //if white sticker in top left
            case 0 -> {
                switch ((((colourOne-1)-(whiteIndex/8))%4+4)%4){
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
                switch ((((colourOne-1)-(whiteIndex/8))%4+4)%4){
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
    static String doCorners(Cube cube, String sequence, int[][] cornerIndexes){
        //iterates over the four bottom corners solving each one sequentially
        for(int i = 0;i<4;i++){
            //we only actually have to check two of the sides for the corners. The third one can be determined just from these.
            while(cube.getColour(5,2*i)!=6 || cube.getColour(i+1,4)!=2+i){
                for(int[] corner:cornerIndexes){
                    if((cube.getColour(corner[0]/8,corner[0]%8)==6)&&(cube.getColour(corner[1]/8,corner[1]%8)==2+i)){
                        sequence = solveCorner(cube,corner[0],2+i,sequence);
                        break;
                    } else if ((cube.getColour(corner[1]/8,corner[1]%8)==6)&&(cube.getColour(corner[2]/8,corner[2]%8)==2+i)){
                        sequence = solveCorner(cube,corner[1],2+i,sequence);
                        break;
                    } else if ((cube.getColour(corner[2]/8,corner[2]%8)==6)&&(cube.getColour(corner[0]/8,corner[0]%8)==2+i)) {
                        sequence = solveCorner(cube,corner[2],2+i,sequence);
                        break;
                    }
                }
                //System.out.println(sequence);
            }
        }
        return sequence;
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
    static String insertEdges(Cube cube, String sequence, int[][]edgeIndexes){
        for(int i = 1;i<=4;i++){
            while(cube.getColour((i%4)+1,7)!=(i%4)+2 || cube.getColour(i,3)!=1+i){
                for(int[] edge:edgeIndexes){
                    if (cube.getColour(edge[0]/8,edge[0]%8) == 1+i && cube.getColour(edge[1]/8,edge[1]%8) == (i%4)+2){
                        sequence = insertEdge(cube, edge[0],edge[1],1+i,sequence);
                        break;
                    } else if (cube.getColour(edge[0]/8,edge[0]%8) == (i%4)+2 && cube.getColour(edge[1]/8,edge[1]%8) == 1+i){
                        sequence = insertEdge(cube, edge[1],edge[0],1+i,sequence);
                        break;
                    }
                }
            }
        }
        return sequence;
    }

    static String yellowCross(Cube cube, String sequence){
        //if not solved
        while((cube.sides[0]&0x0F0F0F0F)!=0x01010101){
            System.out.println("test");
            //if not in one of the three cases keep making U moves until it is
            /*while((cube.sides[0]&0x0F0F0F0F)!=0&&(cube.sides[0]&0x0F0F0F0F)!=0x01000001&&(cube.sides[0]&0x0F0F0F0F)!=0x00010001){
                System.out.println(Long.toHexString(cube.sides[0]&0x0F0F0F0F));
                sequence = performMoves(cube,"U",sequence,2);
            }*/
            //checks for dot case
            for(int i=1;i<8;i+=2){
                //if any edges are yellow check whether it is a line or a corner. If neither perform a U move
                if(cube.getColour(0,i)==1){
                    while(cube.getColour(0,7)!=1||(cube.getColour(0,1)!=1&&cube.getColour(0,3)!=1)) {
                    sequence = performMoves(cube,"U",sequence,2);}
                }
            }
            //perform an algorithm to move it into the next case. This repeats until the cross is solved
            sequence = performMoves(cube,"F R U R' U' F'",sequence,2);
        }
        return sequence;
    }

    //permutes the edges of the cross so that they are all in order
    static String permuteCross(Cube cube, String sequence){
        ArrayList<Integer> solvedEdges = new ArrayList<>();
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
                    case 1 ->{
                        sequence=performMoves(cube,"U2",sequence,2);
                    } case 2 -> {sequence=performMoves(cube,"U'",sequence,2);}
                    case 4 -> {sequence=performMoves(cube,"U",sequence,2);}
                }
                return performMoves(cube,"R U R' U R U2 R'",sequence,2);
            }
            //if none of the edges are solved perform a naive sune algorithm to flip two edges and try again
            sequence = performMoves(cube,"R U R' U R U2 R'",sequence,2);
        }
    }

    //permutes the corners of the yellow side until they are in the right position
    static String permuteCorners(Cube cube, String sequence, int[][] cornerIndexes){
        ArrayList<Integer> permutedCorners = new ArrayList<>();
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
            System.out.println(permutedCorners);
            //if every corner is already in correct place just return sequence without doing anything
            if (permutedCorners.size() == 4) {
                return sequence;
            }
            //if only one edge is in the right place, move to the front top left corner and perform algorithm
            if (permutedCorners.size() == 1) {
                switch(permutedCorners.get(0)){
                    case 0 -> sequence = performMoves(cube,"U'",sequence,2);
                    case 1 -> sequence = performMoves(cube,"U2",sequence,2);
                    case 2 -> sequence = performMoves(cube,"U",sequence,2);
                }
                System.out.println("algorithm performed");
                sequence = performMoves(cube,"R U' L' U R' U' L",sequence,2);
            } else { //if none of the edges are in the right place perform a naive performance of the algorithm and then repeat the process
                sequence=performMoves(cube,"R U' L' U R' U' L",sequence,2);
            }
            permutedCorners.clear();
        }

    }

    static String orientCorners(Cube cube, String sequence){
        //for every single corner
        for(int i = 0;i<4;i++){
            //while not orientated correctly
            while(cube.getColour(0,4)!=1){
                sequence = performMoves(cube, "R' D' R D R' D' R D",sequence,2);
            }
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
    //calls each step sequentially, gradually adding on to the solution
    static String solve(Cube cube,int[][] cornerIndexes, int[][] edgeIndexes){
        String solution = whiteCross(cube, edgeIndexes);
        solution = doCorners(cube,solution, cornerIndexes);
        solution = insertEdges(cube, solution, edgeIndexes);
        solution = yellowCross(cube,solution);
        solution = permuteCross(cube,solution);
        solution = permuteCorners(cube,solution, cornerIndexes);
        return orientCorners(cube,solution);
    }

    @Override
    public void run(){
        String solution = solve(cube.clone(),cornerIndexes,edgeIndexes);
        System.out.println(solution);
        for(String move:solution.split(" ")){
            switch (move) {
                case "U" -> {
                    cube.clockwise(0);
                    cubeGUI.rotateY(-1,-1,duration, cube, colours);}
                case "U'" -> {
                    cube.antiClockwise(0);
                    cubeGUI.rotateY(-1,1,duration, cube, colours);
                }
                case "U2" -> {
                    cube.doubleTwist(0);
                    cubeGUI.rotateY(-1,-2,duration, cube, colours);
                }
                case "L" -> {
                    cube.clockwise(1);
                    cubeGUI.rotateX(-1,-1,duration, cube, colours);
                }
                case "L'" -> {
                    cube.antiClockwise(1);
                    cubeGUI.rotateX(-1,1,duration, cube, colours);
                }
                case "L2" -> {
                    cube.doubleTwist(1);
                    cubeGUI.rotateX(-1,-2,duration, cube, colours);
                }
                case "F" -> {
                    cube.clockwise(2);
                    cubeGUI.rotateZ(-1,-1,duration, cube, colours);
                }
                case "F'" -> {
                    cube.antiClockwise(2);
                    cubeGUI.rotateZ(-1,1,duration, cube, colours);
                }
                case "F2" -> {
                    cube.doubleTwist(2);
                    cubeGUI.rotateZ(-1,-2,duration, cube, colours);
                }
                case "R" -> {
                    cube.clockwise(3);
                    cubeGUI.rotateX(1,1,duration, cube, colours);
                }
                case "R'" -> {
                    cube.antiClockwise(3);
                    cubeGUI.rotateX(1,-1,duration, cube, colours);
                }
                case "R2" -> {
                    cube.doubleTwist(3);
                    cubeGUI.rotateX(1,2,duration, cube, colours);
                }
                case "B" -> {
                    cube.clockwise(4);
                    cubeGUI.rotateZ(1,1,duration, cube, colours);
                }
                case "B'" -> {
                    cube.antiClockwise(4);
                    cubeGUI.rotateZ(1,-1,duration, cube, colours);
                }
                case "B2" -> {
                    cube.doubleTwist(4);
                    cubeGUI.rotateZ(1,2,duration, cube, colours);
                }
                case "D" -> {
                    cube.clockwise(5);
                    cubeGUI.rotateY(1,1,duration, cube, colours);
                }
                case "D'" -> {
                    cube.antiClockwise(5);
                    cubeGUI.rotateY(1,-1,duration, cube, colours);
                }
                case "D2" -> {
                    cube.doubleTwist(5);
                    cubeGUI.rotateY(1,2,duration, cube, colours);
                }
            }
            try {
                Thread.sleep(Math.round(duration)+54);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
