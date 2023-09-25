package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


public class Cubie {
    int x,y,z;
    private Group model;
    Face[] faces = new Face[6];
    Box[] boxes = new Box[6];
    //constructor which creates the cubie with the passed in x,y,z coordinates
    public Cubie(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    //initially creates a face at the start of the program. Parameters are orientation for which side the face will be on, and colour for what colour it will be.
    public void setupFace(int orientation, Color colour){
        faces[orientation-1] = new Face(orientation);
        boxes[orientation-1] = faces[orientation-1].getModel(colour);
    }
    //changes the colour of a given face according to the parameters, orientation which decides which face it is on and the colour it will be changed to.
    public void alterFace(int orientation, Color colour){
        faces[orientation-1].setColour(colour);
    }

    //returns the model for the entire cubie, included the black base along with any faces that it has.
    public Group getModel(){
        if (model!=null){return model;}
        Box base = new Box(100,100,100);
        base.setMaterial(new PhongMaterial(Color.BLACK));
        Group group = new Group(base);
       for(Box box:boxes){
            if(box!=null){
                group.getChildren().add(box);
            }
        }
        group.setTranslateX(x*100);
        group.setTranslateY(y*100);
        group.setTranslateZ(z*100);
        model = group;
        return group;

    }
}
