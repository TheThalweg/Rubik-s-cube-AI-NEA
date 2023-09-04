package com.example.demo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Node {
    private Cube state;
    private final Node parent;
    private final int[] move;
    public Cube getState() {
        return state;
    }
    public Node getParent(){
        return parent;
    }
    public void setState(Cube state){this.state=state;}
    public int[] getMove(){
        return move;
    }

    public Node(Cube state, Node parent, int[] move){
        this.state = state;
        this.parent = parent;
        this.move = move;
    }

    //returns all the children of the node
    public ArrayList<Node> getChildren(int[][] possibleMoves, boolean isFirst){
        ArrayList<Node> children = new ArrayList<>();
        boolean prioritise = false;
        for(int[] possibleMove:possibleMoves){
            /*ignores any moves that were identical to previous moves. (No point of a U move after a previous U move)
            Exception is when we are considering the first move of a group, in which case we actually want to prioritise moves
            that are identical to the previous move as it could allow for cancellation of moves, which would result in a solution with fewer moves
            E.g. without this prioritisation a scramble of R U B would result in a solution of around 20 moves. This is because once the program
            performs B to get into group 2 it doesn't consider B2 for the first move of group 2,sending the program down a rabbit hole of correct but inefficient moves,
            ignoring an extremely quick 3 move solution that could have been achieved otherwise.

            From experience, this makes longer solves take a bit longer to calculate, especially if there are no cancellations,
            however it makes up for this due to the fact that shorter solutions are more common, making the program more consistent.
            In general the average solve length is around 30 with this method as opposed to 34 with the other method*/
            if(move!=null){if(possibleMove[0]==move[0]){if(!isFirst){continue;}else{prioritise = true;}}
                if(move[0]-possibleMove[0]==2||move[0]-possibleMove[0]==6){continue;}
            }
            //creates a child of the current node
            Node child = new Node(this.state.clone(),this, possibleMove);
            switch (possibleMove[1]) {
                case 0 -> child.state.clockwise(possibleMove[0]);
                case 1 -> child.state.antiClockwise(possibleMove[0]);
                case 2 -> child.state.doubleTwist(possibleMove[0]);
            }
            if(prioritise){children.add(0,child);prioritise = false;}
            else{children.add(child);}
        }
        return children;
    }

    //goal test to check whether all the edges are fixed
    static boolean goalOne(Cube cube){
        return ((cube.getColour(0,1)!=2)&&
                (cube.getColour(0,3)!=2)&&
                (cube.getColour(0,5)!=2)&&
                (cube.getColour(0,7)!=2)&&
                (cube.getColour(5,1)!=2)&&
                (cube.getColour(5,3)!=2)&&
                (cube.getColour(5,5)!=2)&&
                (cube.getColour(5,7)!=2)&&
                (cube.getColour(1,1)!=1)&&
                (cube.getColour(2,1)!=1)&&
                (cube.getColour(3,1)!=1)&&
                (cube.getColour(4,1)!=1)&&
                (cube.getColour(1,5)!=1)&&
                (cube.getColour(2,5)!=1)&&
                (cube.getColour(3,5)!=1)&&
                (cube.getColour(4,5)!=1)&&
                (cube.getColour(2,3)!=2)&&
                (cube.getColour(2,7)!=2)&&
                (cube.getColour(4,3)!=2)&&
                (cube.getColour(4,7)!=2)&&
                (cube.getColour(1,3)!=1)&&
                (cube.getColour(1,7)!=1)&&
                (cube.getColour(3,3)!=1)&&
                (cube.getColour(3,7)!=1)
                );

    }
    //A hashing algorithm which given any cube will produce a unique long number which represents the permutations of all the corners
    static long cubeToPerm(Cube cube) {
        long top = cube.sides[0]&0xF0F0F0F0;
        long bottom = cube.sides[5]&0xF0F0F0F0;
        long left = cube.sides[1]&0xF0F0F0F0;
        long right = cube.sides[3]&0xF0F0F0F0;
        return(top<<32|bottom<<28|left|right>>4);
    }
    //A recursive algorithm which visits each node's parent and adds the move to get from the parent to the child onto the list called 'sequence'
    static ArrayList<int[]> getSequence(Node parent, int[] move, ArrayList<int[]> sequence){
        if(parent==null){return sequence;}
        sequence.add(move);
        if(parent.getParent()==null){return sequence;}
        return getSequence(parent.getParent(),parent.getMove(),sequence);
    }

    /*starts off by treating the cube as if all the opposite colours are the same. This allows us to check whether all the cubes are in their correct orbits
    and if all the edges are in their correct slices by just checking whether the cube is 'solved' (we don't need to check the top and bottom side since that
    was checked in the previous stage of the algorithm). Once we have confirmed that we retrieve the sequence of moves that lead up to that point, apply it to the
    original cube and use that unsimplified version to check whether the current permutation of corners is an allowed permutation (if it is in the perms set then
    it is allowed). This may seem long but the use of simplified cubes massively cuts down on computational costs as although unsimplifying the cube is long, it is
    of O(n) (linear) time meaning it is worth it since a vast majority of states won't even pass the first check which can be calculated near instantly*/
    static boolean goalThree(Cube cube, HashSet<Long> perms, Cube initial, Node parent, int[] move){
        ArrayList<int[]> sequence = new ArrayList<>();
        if(cube.sides[1]==0x22222222 && cube.sides[3]==0x22222222 && cube.sides[2]==0x33333333 && cube.sides[4]==0x33333333){
            sequence = getSequence(parent,move,sequence);
            for(int i = sequence.size();i>0;i--){
                switch (sequence.get(i - 1)[1]) {
                    case 0 -> initial.clockwise(sequence.get(i - 1)[0]);
                    case 1 -> initial.antiClockwise(sequence.get(i - 1)[0]);
                    case 2 -> initial.doubleTwist(sequence.get(i - 1)[0]);
                }
            }
            sequence.clear();
            return perms.contains(cubeToPerm(initial));
        }
        return false;
    }
    public boolean isGoal(int stage, HashSet<Long> perms, Cube initial){
        return switch (stage) {
            case 1 -> goalOne(state);
            //goal test to check whether all the corners are fixed and whether the middle edge pieces are in the correct slice
            //(same as top and bottom sides are all yellow or white)
            case 2 -> state.sides[0] == 0x11111111 && state.sides[5] == 0x11111111;
            case 3 -> goalThree(state, perms, initial.clone(), parent, move);
            //checks whether the cube is solved
            case 4 -> Arrays.equals(state.sides, new int[]{0x11111111, 0x22222222, 0x33333333, 0x44444444, 0x55555555, 0x66666666});
            default -> return false;
        };
    }
}
