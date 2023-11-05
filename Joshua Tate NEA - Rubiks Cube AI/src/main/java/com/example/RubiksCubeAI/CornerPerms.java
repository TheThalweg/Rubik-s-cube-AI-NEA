package com.example.RubiksCubeAI;
import java.util.ArrayList;
import java.util.HashSet;
//This class is specifically used to calculate the reachable permutations within that the cube can reach, of which there should be 96
//Since this takes very little time I have chosen just to run this whenever you run the program instead of storing the permutations in a file
public class CornerPerms {
    HashSet<Long> perms = new HashSet<>();
    public CornerPerms(){}
    //A hashing algorithm which given any cube will produce a unique long number which represents the permutations of all the corners
    static long cubeToPerm(Cube cube){
        long top = cube.sides[0]&0xF0F0F0F0;
        long bottom = cube.sides[5]&0xF0F0F0F0;
        long left = cube.sides[1]&0xF0F0F0F0;
        long right = cube.sides[3]&0xF0F0F0F0;
        return(top<<32|bottom<<28|left|right>>4);
    }
    static void DLS(Node cube, int depth,HashSet<Long> perms){
        if(depth==0){
            //for any state reachable by only 180 degree twists add the corner permutation to the set
            long perm = cubeToPerm(cube.getState());
            perms.add(perm);
        } else {
            //only bother searching children if there are still permutations to be found
            if(perms.size()<96){
            ArrayList<Node> children= cube.getChildren(new int[][]{{0,2},{1,2},{2,2},{3,2},{4,2},{5,2}},depth==1);
            for (Node child : children) {
                DLS(child, depth - 1, perms);}
            }
        }
    }

    //basically just the same as the IDDFS method in the Thistle class
    public HashSet<Long> calculatePerms(Node cube){
        //On the first call the depth is set to 0. This checks whether the given cube is already a solution
        int depth = 0;
        //Initiates while loop which continuously increments the depth until a solution is found
        while(true){
            //calls depth limited search
            DLS(cube,depth,perms);
            //once perms is 96 permutations long we can just return the set, since we know there can't be anymore
            if(perms.size()>=96){return perms;}
            depth++;
        }
    }
    }

