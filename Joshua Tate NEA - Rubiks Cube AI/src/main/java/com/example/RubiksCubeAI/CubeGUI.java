package com.example.RubiksCubeAI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class CubeGUI {
    //3d array to be filled with cubies.
    Cubie[][][] cubies = new Cubie[3][3][3];
    private Group model;

    //initialises a solved cube state
    public void initialize(){
        Color[] colours = {Color.YELLOW, Color.RED, Color.GREEN, Color.ORANGE,Color.BLUE,Color.WHITE};
        for(Cubie[][] i:cubies){
            for(Cubie[] j:i){
                for(Cubie cubie:j){
                    if(cubie.z==-1){cubie.setupFace(3,colours[2]);}
                    else if(cubie.z==1){cubie.setupFace(5,colours[4]);}
                    if(cubie.y==-1){cubie.setupFace(1,colours[0]);}
                    else if(cubie.y==1){cubie.setupFace(6,colours[5]);}
                    if(cubie.x==-1){cubie.setupFace(2,colours[1]);}
                    else if(cubie.x==1){cubie.setupFace(4,colours[3]);}
                }
            }
        }
    }

    static void updateGui(CubeGUI cubeGUI,int sideIndex, Cube cube,Color[] colours){
        //The easiest way to do this is just using a large switch statement which manually updates each side based on the new state of the cube
        //since this function isn't called when updating the GUI it doesn't need to be super quick
        switch (sideIndex) {
            //top side
            case 0 -> {
                //updates all the faces on the top side
                cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(1, colours[cube.getColour(0, 1) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(1, colours[cube.getColour(0, 3) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(1, colours[cube.getColour(0, 5) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(1, colours[cube.getColour(0, 7) - 1]);
                //updates the faces on the sides of the top side
                cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(5, colours[cube.getColour(4, 1) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(4, colours[cube.getColour(3, 1) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(3, colours[cube.getColour(2, 1) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(2, colours[cube.getColour(1, 1) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
            }
            case 1 -> {
                //updates all the faces on the left side
                cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(2, colours[cube.getColour(1, 1) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(2, colours[cube.getColour(1, 3) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(2, colours[cube.getColour(1, 5) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(2, colours[cube.getColour(1, 7) - 1]);
                //updates all the faces on the sides of the left side
                cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
                cubeGUI.cubies[0][0][1].alterFace(1, colours[cube.getColour(0, 7) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(3, colours[cube.getColour(2, 7) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(6, colours[cube.getColour(5, 7) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(5, colours[cube.getColour(4, 3) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
            }
            case 2 -> {
                //updates all the faces on the front side
                cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(3, colours[cube.getColour(2, 1) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(3, colours[cube.getColour(2, 3) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(3, colours[cube.getColour(2, 5) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(3, colours[cube.getColour(2, 7) - 1]);
                //updates all the faces on the sides of the front side
                cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
                cubeGUI.cubies[1][0][0].alterFace(1, colours[cube.getColour(0, 5) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(4, colours[cube.getColour(3, 7) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(6, colours[cube.getColour(5, 1) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
                cubeGUI.cubies[0][1][0].alterFace(2, colours[cube.getColour(1, 3) - 1]);
                cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
            }
            case 3 -> {
                //updates all the faces on the right side
                cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(4, colours[cube.getColour(3, 1) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(4, colours[cube.getColour(3, 3) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(4, colours[cube.getColour(3, 5) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(4, colours[cube.getColour(3, 7) - 1]);
                //updates all the faces on the sides of the right side
                cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
                cubeGUI.cubies[2][0][1].alterFace(1, colours[cube.getColour(0, 3) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(5, colours[cube.getColour(4, 7) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(6, colours[cube.getColour(5, 3) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
                cubeGUI.cubies[2][1][0].alterFace(3, colours[cube.getColour(2, 3) - 1]);
                cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
            }
            case 4 -> {
                //updates all the faces on the back side
                cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(5, colours[cube.getColour(4, 1) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(5, colours[cube.getColour(4, 3) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(5, colours[cube.getColour(4, 5) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(5, colours[cube.getColour(4, 7) - 1]);
                //updates all the faces on the sides of the back side
                cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
                cubeGUI.cubies[1][0][2].alterFace(1, colours[cube.getColour(0, 1) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
                cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
                cubeGUI.cubies[0][1][2].alterFace(2, colours[cube.getColour(1, 7) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(6, colours[cube.getColour(5, 5) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
                cubeGUI.cubies[2][1][2].alterFace(4, colours[cube.getColour(3, 3) - 1]);
                cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
            }
            case 5 -> {
                //updates all the faces on the bottom side
                cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(6, colours[cube.getColour(5, 1) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(6, colours[cube.getColour(5, 3) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(6, colours[cube.getColour(5, 5) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(6, colours[cube.getColour(5, 7) - 1]);
                //updates all the faces on the side of the bottom side
                cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
                cubeGUI.cubies[1][2][0].alterFace(3, colours[cube.getColour(2, 5) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
                cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
                cubeGUI.cubies[2][2][1].alterFace(4, colours[cube.getColour(3, 5) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
                cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
                cubeGUI.cubies[1][2][2].alterFace(5, colours[cube.getColour(4, 5) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
                cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
                cubeGUI.cubies[0][2][1].alterFace(2, colours[cube.getColour(1, 5) - 1]);
                cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
            }
        }
    }
    public void rotateZ(int index, int direction, float duration, Cube cube, Color[] colours) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(duration/18),e->{
        for(Cubie[][] i:cubies){
            for(Cubie[] j:i){
                for(Cubie cubie:j){
                    if(cubie.z==index){
                        cubie.getModel().getTransforms().add(new Rotate(-5*direction,-100*cubie.x,-100*cubie.y,-100*cubie.z,Rotate.Z_AXIS));
                    }
                }
            }
        }
        }));
        animation.setCycleCount(18);
        //remove the transformations after the animation. This means that you can perform as many moves as you want without slowing down the system.
        //afterwards we just update all the sides according to the bitboards
        animation.setOnFinished(e-> {for(Cubie[][] i:cubies){
                    for(Cubie[] j:i){
                        for(Cubie cubie:j){
                            if(cubie.z==index){
                                //removes last 18 transformations
                                cubie.getModel().getTransforms().remove(cubie.getModel().getTransforms().size()-18,cubie.getModel().getTransforms().size());
                                updateGui(this, 3 + index, cube, colours);
                            }
                        }
                    }
                }
        });
        animation.play();
    }

    public void rotateY(int index, int direction, float duration, Cube cube, Color[] colours){
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(duration/18),e->{
            for(Cubie[][] i:cubies){
                for(Cubie[] j:i){
                    for(Cubie cubie:j){
                        if(cubie.y==index){
                            cubie.getModel().getTransforms().add(new Rotate(-5*direction,-100*cubie.x,-100*cubie.y,-100*cubie.z,Rotate.Y_AXIS));
                        }
                    }
                }
            }
        }));
        animation.setCycleCount(18);
        //remove the transformations after the animation. This means that you can perform as many moves as you want without slowing down the system.
        //afterwards we just update all the sides according to the bitboards
        animation.setOnFinished(e-> {for(Cubie[][] i:cubies){
            for(Cubie[] j:i){
                for(Cubie cubie:j){
                    if(cubie.y==index){
                        //removes last 18 transformations
                        cubie.getModel().getTransforms().remove(cubie.getModel().getTransforms().size()-18,cubie.getModel().getTransforms().size());
                        if(index==-1){updateGui(this, 0, cube, colours);}
                        else{updateGui(this, 5, cube, colours);}
                    }
                }
            }
        }
        });
        animation.play();
    }
    public void rotateX(int index, int direction, float duration, Cube cube, Color[] colours) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(duration/18),e->{
            for(Cubie[][] i:cubies){
                for(Cubie[] j:i){
                    for(Cubie cubie:j){
                        if(cubie.x==index){
                            cubie.getModel().getTransforms().add(new Rotate(-5*direction,-100*cubie.x,-100*cubie.y,-100*cubie.z,Rotate.X_AXIS));
                        }
                    }
                }
            }
        }));
        animation.setCycleCount(18);
        //remove the transformations after the animation. This means that you can perform as many moves as you want without slowing down the system.
        //afterwards we just update all the sides according to the bitboards
        animation.setOnFinished(e-> {for(Cubie[][] i:cubies){
            for(Cubie[] j:i){
                for(Cubie cubie:j){
                    if(cubie.x==index){
                        //removes last 18 transformations
                        cubie.getModel().getTransforms().remove(cubie.getModel().getTransforms().size()-18,cubie.getModel().getTransforms().size());
                        updateGui(this, 2+index, cube, colours);
                    }
                }
            }
        }
        });
        animation.play();
    }
    //constructor class which forms a 3d array of 27 cubies each constructed with their x,y and z coordinates
    public CubeGUI(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    cubies[i][j][k]=new Cubie(i-1,j-1,k-1);
                }
            }
        }
    }
    //gets model for entire cube
    public Group getModel(){
        if(model!=null){return model;}
        Group root = new Group();
        this.model = root;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                for (int k = 0; k < 3; k++){
                    //get children returns the set of the groups children before adding onto that set the cubie at index[x][y][z] (or i,j,k in this sense)
                    root.getChildren().add(this.cubies[i][j][k].getModel());
                }
            }
        }
        return root;
    }
}
