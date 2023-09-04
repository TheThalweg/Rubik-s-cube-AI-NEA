package com.example.demo;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
/*
Class for the Thistlethwaite algorithm AI. Works by moving cube into consecutive stages of which the AI can solve the cube with fewer moves.
Movements between these groups are done using IDDFS, goal states are located inside the Node class.

From my experience the median time to solve using this method is around 30 seconds, and it solves it in around 30 moves.
That being said the run time and move length can vary greatly depending on the scramble. The best I have achieved using this program
was a 26 move solve calculated in under a second, whilst the worst I have achieved is a ten minute solve due to an extremely unlucky solution
of 39 moves. In general the results are skewed towards the lower end meaning most of the time the solves are well under a minute but if you do
get unlucky it can take a long time to calculate owing to the nature of the IDDFS algorithm which gets exponentially larger based on search depth.
*/
//extends from thread class to allow for multithreading (Means GUI is still usable whilst the AI is calculating and performing move)
public class Thistle extends Thread{
    Cube initial;
    Cube simplified;
    HashSet<Long> perms;
    CubeGUI cubeGUI;
    Color[] colours;
    float duration;
    //first num indicates side, second num indicates type of move e.g. clockwise, anticlockwise, 180 degree twist
    //first row indicates moves for first group, second row indicates moves for second group etc.
    int[][][] possibleMoves =
            {{{0,0},{0,1},{0,2},{1,0},{1,1},{1,2},{2,0},{2,1},{2,2},{3,0},{3,1},{3,2},{4,0},{4,1},{4,2},{5,0},{5,1},{5,2}},
            {{0,0},{0,1},{0,2},{1,0},{1,1},{1,2},{2,2},{3,0},{3,1},{3,2},{4,2},{5,0},{5,1},{5,2}},
            {{0,0},{0,1},{0,2},{1,2},{2,2},{3,2},{4,2},{5,0},{5,1},{5,2}},
            {{0,2},{1,2},{2,2},{3,2},{4,2},{5,2}}
    };
    //constructor for thistle class
    public Thistle(Cube initial, HashSet<Long> perms, Cube simplified, CubeGUI cubeGUI,Color[] colours, float duration){
        this.initial = initial;
        this.perms = perms;
        this.simplified = simplified;
        this.cubeGUI = cubeGUI;
        this.colours = colours;
        this.duration = duration;
    }

    /*subroutine for depth limited search. It takes in a parameter depth and recursively calls itself,
     decreasing depth each time until depth = 0 at which point it checks if it is the goal state.
     If it is then it returns the node at the goal state. If it isn’t then it returns null.*/
    static Node DLS(Node cube, int depth, int stage, HashSet<Long> perms, int[][][] possibleMoves, Cube initial, int fullDepth){
        Node found;
        if(depth==0){
            //System.out.println(Arrays.toString(cube.getMove()));
            if(cube.isGoal(stage, perms, initial)){
                return cube;
            } else {return null;}
        } else {
            //#checks all the children of the node. The children can be received by applying the transition model algorithms
            // previously discussed for all possible moves.
            ArrayList<Node> children= cube.getChildren(possibleMoves[stage-1],depth==fullDepth);
            //cube.getState().displayCube();
            for (Node child : children) {
                //System.out.println(Arrays.toString(child.getMove()));
                found = DLS(child, depth - 1, stage, perms, possibleMoves, initial, fullDepth);
                if (found!=null){
                    return found;
                }
            }
            return null;
        }
    }

    //subroutine Iterative deepening depth first search
    static Node IDDFS(Node cube,int stage, HashSet<Long> perms, int[][][] possibleMoves, Cube initial){
        //On the first call the depth is set to 0. This checks whether the given cube is already a solution
        int depth = 0;
        Node found;
        //Initiates while loop which continuously increments the depth until a solution is found
        while(true){
            /*found is the node returned after the algorithm is finished.
            If this isn’t null it means that the algorithm has found a solution and the node's history can be checked to find the path of moves.
            if it is null, it means no solution was found at that depth and the depth needs to be incremented by 1.*/
            found = DLS(cube,depth,stage, perms, possibleMoves, initial, depth);
            if(found!=null){return found;}
            System.out.println(depth);
            System.out.println(found);
            depth++;
            //if(depth > 2){return null;}
        }
    }
    //A recursive algorithm which visits each node's parent and adds the move to get from the parent to the child onto the list called 'sequence'
    static ArrayList<int[]> getSequence(Node parent, int[] move, ArrayList<int[]> sequence){
        //handles situation where we are given a solved cube (rare).
        if(parent == null){return sequence;}
        sequence.add(move);
        if(parent.getParent()==null){return sequence;}
        return getSequence(parent.getParent(),parent.getMove(),sequence);
    }
    /*Used to get from a simplified cube to an unsimplified cube. We use the simplified cube to calculate the first 3 and a bit stages of the algorithm
    From then on we use the proper cube. This function just retrieves the sequence of moves from the simplified node, applies it to the
    initial cube to get the end state and changes the end state of the final node to this new state*/
    static Node unsimplify(Cube cube, Node simplified){
        ArrayList<int[]> sequence = new ArrayList<>();
        sequence = getSequence(simplified.getParent(), simplified.getMove(),sequence);
        for(int i = sequence.size();i>0;i--){
            switch (sequence.get(i - 1)[1]) {
                case 0 -> cube.clockwise(sequence.get(i - 1)[0]);
                case 1 -> cube.antiClockwise(sequence.get(i - 1)[0]);
                case 2 -> cube.doubleTwist(sequence.get(i - 1)[0]);
            }
        }
        //We could theoretically create an entirely new node branch for the full cube,
        // but it's easier just to change the final state of the simplified node
        simplified.setState(cube);
        return simplified;
    }

    //Solves the cube by applying four IDDFS sequentially, decreasing the moveset each time
    //for the first 3 stages we use a simplified cube as it makes goal calculation MUCH quicker
    static Node solve(Cube simplified, HashSet<Long> perms, int[][][] possibleMoves, Cube initial){
        Node stageOne = IDDFS(new Node(simplified,null,null),1, perms, possibleMoves, initial);
        System.out.println("stage one complete");
        Node stageTwo = IDDFS(stageOne,2, perms, possibleMoves, initial);
        System.out.println("stage two complete");
        Node stageThree = IDDFS(stageTwo,3, perms, possibleMoves, initial);
        System.out.println("stage three complete");
        return IDDFS(unsimplify(initial.clone(),stageThree),4, perms, possibleMoves, initial);
    }

    //this is the method that will be run when we call start (Allows for multithreading)
    @Override
    public void run(){
        Node solved = solve(simplified, perms, possibleMoves, initial);
        ArrayList<int[]> solution = new ArrayList<>();
        while(solved.getParent() != null){
            solution.add(solved.getMove());
            solved = solved.getParent();
        }
        //cancels down moves from separate groups that are identical
        for(int i = solution.size()-1;i>0;i--){
            if(solution.get(i)[0]==solution.get(i-1)[0]){
                //the only situation where cancellations can occur is when a 90 degree move is next to a 180 degree move
                //(Due to the way the thistlethwaite algorithm works with respect to the group move sets)
                //thus if there is a cancellation just switch a clockwise move into an anticlockwise move and vice versa
                //solution.get(i)[1] = (solution.get(i)[1] + 1) % 2;
                solution.set(i, new int[]{solution.get(i)[0], (solution.get(i)[1] + 1) % 2});
                solution.remove(i-1);
                i--;
            }
        }
        int direction = 0;
        //runs backwards though the solution and performs the moves to the cubeGUI
        for(int i = solution.size();i>0;i--){
            System.out.println(Arrays.toString(solution.get(i-1)));
            switch (solution.get(i - 1)[1]) {
                case 0 -> {
                    initial.clockwise(solution.get(i - 1)[0]);
                    direction = 1;
                }
                case 1 -> {
                    initial.antiClockwise(solution.get(i - 1)[0]);
                    direction = -1;
                }
                case 2 -> {
                    initial.doubleTwist(solution.get(i - 1)[0]);
                    direction = 2;
                }
            }
            switch (solution.get(i - 1)[0]) {
                case 0 -> cubeGUI.rotateY(-1, -1*direction, duration, initial, colours);
                case 1 -> cubeGUI.rotateX(-1, -1*direction, duration, initial, colours);
                case 2 -> cubeGUI.rotateZ(-1, -1*direction, duration, initial, colours);
                case 3 -> {
                    direction *= -1;
                    cubeGUI.rotateX(1, -1*direction, duration, initial, colours);
                }
                case 4 -> {
                    direction *= -1;
                    cubeGUI.rotateZ(1, -1*direction, duration, initial, colours);
                }
                case 5 -> {
                    direction *= -1;
                    cubeGUI.rotateY(1, -1*direction, duration, initial, colours);
                }
            }
            //adds in delay
            try {
                Thread.sleep(Math.round(duration)+50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(solution.size());
    }
}
