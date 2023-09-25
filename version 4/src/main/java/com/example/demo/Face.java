package com.example.demo;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

//You can think of the face class as the colour stickers on a given cube. Each cubie will have 1-3 stickers depending on what part of the cube it is.
public class Face {
    int orientation; //1 means top, 2 means left, 3 means front, 4 means right, 5 means back, 6 means bottom
    Box box = null;

    //sets the orientation of the face according to the inputted orientation
    public Face(int orientation){
        this.orientation = orientation;
    }

    Box model = null;

    //returns the model of the box. This model is decided according to the orientation of the box, and it's inputted colour
    public Box getModel(Color colour){
        if(model!=null){return model;}
        switch (orientation){
            //if the orientation is at the bottom create a box with dimensions of 95x1x95 (basically a flat horizontal side) and translate it to the bottom of the cubie
            case 6:
                box = new Box(95,1,95);
                box.setTranslateY(50);
                break;
            case 1:
                box = new Box(95,1,95);
                box.setTranslateY(-50);
                break;
            case 2:
                box = new Box(1,95,95);
                box.setTranslateX(-50);
                break;
            case 4:
                box = new Box(1,95,95);
                box.setTranslateX(50);
                break;
            case 3:
                box = new Box(95,95,1);
                box.setTranslateZ(-50);
                break;
            case 5:
                box = new Box(95,95,1);
                box.setTranslateZ(50);
                break;
            default:
                return null;
            }
            //sets the colour of the box according to the colour passed in as the parameter
            box.setMaterial(new PhongMaterial(colour));
            model = box;
            return box;
        }
    //sets the colour of the box according to the colour passed in as the parameter
    public void setColour(Color colour){
        box.setMaterial(new PhongMaterial(colour));
    }
    }

