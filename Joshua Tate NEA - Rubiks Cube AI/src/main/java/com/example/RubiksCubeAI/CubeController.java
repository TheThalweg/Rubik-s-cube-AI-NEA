package com.example.RubiksCubeAI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.LinkedList;

//Custom implementation of stack that limits how big the stack can get (to avoid memory problems)
class LimitedStack<String> {
    private final LinkedList<String> stack = new LinkedList<>();

    //Adds item to stack. If stack size is greater than 1000, remove the first added item to the list
    public void push(String item) {
        if (stack.size() >= 1000) {
            stack.removeFirst();
        }
        stack.addLast(item);
    }

    //Pops item from stack
    public String pop() {
        if (!stack.isEmpty()) {
            return stack.removeLast();
        }
        return null;
    }
}

//Main controller which controls the cube scene display
public class CubeController implements Initializable {

    /*FXML attributes refer to different parts of the scene. cubePane refers to the 3D cube, moveDisplay refers to the solution at the bottom
    and play/pauseButton refers to the animation button. Defining these attributes here allows the controller to interact with them */
    @FXML
    private Pane cubePane;
    @FXML
    private Label moveDisplay;
    @FXML
    private ImageView playButton;
    @FXML
    private ImageView pauseButton;

    private CubeGUI cubeGUI;
    private String solution;


    AtomicBoolean prime = new AtomicBoolean(false);
    float duration = 120;

    //some values for the cubeGUI subScene
    private static final double windowX = 1920;
    private static final double windowY = 880;
    private static final double CAMERA_X = -windowX/2;
    private static final double CAMERA_Y = -windowY/2;
    private static final double CAMERA_Z = -100;
    private double mouseOldX, mouseOldY;

    private Cube cube;
    private Cube simplified;
    private final Color[] colours = {Color.YELLOW, Color.RED, Color.GREEN, Color.ORANGE,Color.BLUE,Color.WHITE};

    //keeps track of completed moves. Used for back button.
    private final LimitedStack<String> completedMoves = new LimitedStack<>();
    private int movePos = 0; //keeps track of where we are in the solution
    private volatile Thread animationThread;
    private volatile Thread thistle;
    private int rotationTracker = 0; /*This is a variable that keeps track of which rotation of a face we are at.
    The reason we need this is so that we only calculate new solutions whenever the user performs a move that isn't
    part of the original provided solution. However, since only 90 degree moves are possible with the keybindings we need
    a way to calculate how far we are through a move in case the solution needs a 180 degree move.
    Only once that move is completed do we move onto the next move.*/

    public void setCube(Cube cube) {
        this.cube = cube;
    }
    public void setSimplified(Cube simplified){
        this.simplified = simplified;
    }
    private HashSet<Long> perms;

    //dictates which AI is used (No need to store to dictate the Thistle AI. If neither of the booleans are true, pick Thistle)
    private boolean doCFOP = false;
    private boolean doBeginners = false;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        //creates camera for the scene
        cubeGUI = new CubeGUI();
        cubeGUI.initialize();
        Camera camera = new PerspectiveCamera();
        camera.translateXProperty().set(CAMERA_X);
        camera.translateYProperty().set(CAMERA_Y);
        camera.translateZProperty().set(CAMERA_Z);
        //sets the render distance for the cameras. Any objects between these numbers will be rendered
        camera.setNearClip(0.1);
        camera.setFarClip(1000);
        //creates subScene with the cubeGUI and the camera and adds that to the cubePane
        Group group = new Group();
        group.getChildren().add(cubeGUI.getModel());
        SubScene subScene = new SubScene(group, windowX, windowY, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.web("#151515"));
        cubePane.getChildren().add(subScene);
        //All the logic for rotating the cube using mouse drag
        //when you click the mouse, get the position of the pointer
        subScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        //As you rotate the mouse calculate the offset from when you originally clicked the mouse and apply a rotation to the camera according to that offset
        //Cube isn't actually rotating, the camera is just orbiting around the cube.
        subScene.setOnMouseDragged(event -> {
            double rotX = event.getSceneY() - mouseOldY;
            double rotY = event.getSceneX() - mouseOldX;
            camera.getTransforms().addAll(new Rotate(-rotX, - CAMERA_X, - CAMERA_Y,  - CAMERA_Z, Rotate.X_AXIS), new Rotate(rotY,  - CAMERA_X,  - CAMERA_Y, - CAMERA_Z, Rotate.Y_AXIS));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        //calculates the allowed corner permutations for the Group 3 checks of the thistlethwaite AI
        CornerPerms cornerPerms = new CornerPerms();
        perms = cornerPerms.calculatePerms(new Node(new Cube(),null,null));
    }

    //setter to set the type of AI that will be used for the solve
    public void setAI(boolean doCFOP, boolean doBeginners) {
        this.doCFOP = doCFOP;
        this.doBeginners = doBeginners;
    }

    //animates the move according to whatever move is passed in. Plays a sound effect depending on whether it is a 180-degree or a 90-degree turn.
    public void animateMove(String move){
        if(move.length()==1){
            AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
            singleTurn.play();
        } else {
            AudioClip doubleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/DoubleTurn.wav")).toExternalForm());
            doubleTurn.play();
        }
        switch (move) {
            case "U" -> {
                cube.clockwise(0);
                cubeGUI.rotateY(-1, -1, duration, cube, colours);
            }
            case "U'" -> {
                cube.antiClockwise(0);
                cubeGUI.rotateY(-1, 1, duration, cube, colours);
            }
            case "U2" -> {
                cube.doubleTwist(0);
                cubeGUI.rotateY(-1, -2, duration, cube, colours);
            }
            case "L" -> {
                cube.clockwise(1);
                cubeGUI.rotateX(-1, -1, duration, cube, colours);
            }
            case "L'" -> {
                cube.antiClockwise(1);
                cubeGUI.rotateX(-1, 1, duration, cube, colours);
            }
            case "L2" -> {
                cube.doubleTwist(1);
                cubeGUI.rotateX(-1, -2, duration, cube, colours);
            }
            case "F" -> {
                cube.clockwise(2);
                cubeGUI.rotateZ(-1, -1, duration, cube, colours);
            }
            case "F'" -> {
                cube.antiClockwise(2);
                cubeGUI.rotateZ(-1, 1, duration, cube, colours);
            }
            case "F2" -> {
                cube.doubleTwist(2);
                cubeGUI.rotateZ(-1, -2, duration, cube, colours);
            }
            case "R" -> {
                cube.clockwise(3);
                cubeGUI.rotateX(1, 1, duration, cube, colours);
            }
            case "R'" -> {
                cube.antiClockwise(3);
                cubeGUI.rotateX(1, -1, duration, cube, colours);
            }
            case "R2" -> {
                cube.doubleTwist(3);
                cubeGUI.rotateX(1, 2, duration, cube, colours);
            }
            case "B" -> {
                cube.clockwise(4);
                cubeGUI.rotateZ(1, 1, duration, cube, colours);
            }
            case "B'" -> {
                cube.antiClockwise(4);
                cubeGUI.rotateZ(1, -1, duration, cube, colours);
            }
            case "B2" -> {
                cube.doubleTwist(4);
                cubeGUI.rotateZ(1, 2, duration, cube, colours);
            }
            case "D" -> {
                cube.clockwise(5);
                cubeGUI.rotateY(1, 1, duration, cube, colours);
            }
            case "D'" -> {
                cube.antiClockwise(5);
                cubeGUI.rotateY(1, -1, duration, cube, colours);
            }
            case "D2" -> {
                cube.doubleTwist(5);
                cubeGUI.rotateY(1, 2, duration, cube, colours);
            }
        }
    }

    //Runs through the solution, starting at the point dictated by the movePos counter and plays the move
    public void animateMoves() {
        //we do the animation on a separate thread so that the pause in animation doesn't freeze the main GUI program
        animationThread = new Thread(() -> {
            switchVisibility();
            String[] moves = solution.split(" ");
            int startPos = movePos + 1;
            for (int i = movePos+1; i < moves.length; i++) {
                String move = moves[i];
                //additional piece of logic to handle cases where user is halfway through move and then click animate button
                if(i == startPos){//if first move of animation check how far through that move the user is
                    switch (rotationTracker){
                        case 1-> move = move.charAt(0)+"'";
                        case 2-> move = move.charAt(0)+"2";
                        case 3-> move = move.charAt(0)+"";
                    }
                }
                completedMoves.push(move); //adds move to completedMove stack
                animateMove(move);
                movePos++;
                displaySolution();
                //if we have reached the end of the solution, switch the visibility of the animateButton.
                if(movePos==moves.length-1){
                    switchVisibility();
                }
                try {
                    Thread.sleep(Math.round(duration) + 27);
                } catch (InterruptedException e) {
                    /*if anything interrupts the animation e.g. pressing back or forward or the user makes a move using a keybinding
                    set the rotationTracker to the move we are currently on*/
                    setRotationTracker();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        //starts the thread if there is a solution
        if(solution != null){animationThread.start();}
    }

    //switches between the playButton and the pauseButton
    public void switchVisibility(){
        if(playButton.isVisible()){
        playButton.setVisible(false);
        pauseButton.setVisible(true);
        }else{playButton.setVisible(true);
            pauseButton.setVisible(false);}
    }

    //Allows the user to move forward according the move dictated by the solution
    @FXML
    public void forward() throws InterruptedException {
        pauseAnimation();
        Thread.sleep(1); //fixes desync with animationThread
        try{
        String move = solution.split(" ")[movePos+1];
        //alters move according to the rotationTracker (For when the user is only halfway through making a move using keybindings, and then they click the forward button)
        switch (rotationTracker){
            case 1-> move = move.charAt(0)+"'";
            case 2-> move = move.charAt(0)+"2";
            case 3-> move = move.charAt(0)+"";
        }
        //animates move and adds it onto stack
        animateMove(move);
        completedMoves.push(move);
        movePos++;
        setRotationTracker();
        //change the solution at the bottom to show updated movePos
        displaySolution();} catch (RuntimeException ignored){}
    }

    //used to pause the animation Thread. Is executed whenever the user interacts with the program using a button.
    @FXML
    public void pauseAnimation(){
        if(pauseButton.isVisible()){
        switchVisibility();}
        if (animationThread != null) {
            animationThread.interrupt();
            animationThread = null;
        }
    }

    //starts/resumes the animation
    @FXML
    public void resumeAnimation(){
        if (animationThread == null) {
            animateMoves();
        }
    }

    // inverts the move so that the undo button plays the inverse of the last completed move
    static String invertMove(String lastMove){
        if(lastMove.length()==1){return lastMove+"'";}
        if(lastMove.charAt(1)=='2'){return lastMove;}
        return Character.toString(lastMove.charAt(0));
    }

    public void setRotationTracker(){
        //sets the rotation tracker to the value dictated by the move we are currently on
        try {
            rotationTracker = getValue(solution.split(" ")[movePos + 1]);
        } catch (ArrayIndexOutOfBoundsException ignored){}
    }

    // returns the value of the move passed in. 90 degree CW moves are 1, 180 degree moves are 2, 90 degree ACW moves are 3
    static int getValue(String move){
        if (move.length() == 1) {
            return 3;
        } else {
            if (move.charAt(1) == '2') {
                return 2;
            } else {
                return 1;
            }
        }
    }

    // used to go backwards. Unlike the forward button, this doesn't go backwards in the solution, it just undoes the most recent moves
    @FXML
    public void back(){
        pauseAnimation();
        try {
            String lastMove = completedMoves.pop(); //gets last move from stack
            animateMove(invertMove(lastMove));
            //dictates logic for setting the rotationTracker whilst moving back through the moves
            if(movePos>0){
                if(lastMove.charAt(0)==solution.split(" ")[movePos].charAt(0)){
                movePos--;
                displaySolution();
                rotationTracker = getValue(lastMove);
                }
                else{
                    if(lastMove.length()==1){rotationTracker--;}
                    else{rotationTracker++;}
                }
            }else{solveCube();} //if at starts of solution recalculate the solution based on the new cubeState
        } catch (RuntimeException ignored){} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Exits the program
    @FXML
    public void exit(){
        pauseAnimation();
        //Stops the Thistle Thread from running if it is in the process of calculating
        if(!(doBeginners||doCFOP)){
            if(thistle!=null){
                thistle.interrupt();
                thistle = null;
            }
        }
        Platform.exit();
    }

    // Returns to the main menu
    @FXML
    public void homeButton() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        // resets AI choice back to normal
        doCFOP = false;
        doBeginners = false;
        solution = null; //resets solution
        pauseAnimation();
        // Stops the Thistle Thread from running if it is in the process of calculating
        if(!(doBeginners||doCFOP)){
            if(thistle!=null){
                thistle.interrupt();
                thistle = null;
            }
        }
        // The following code is used to load up the main menu
        FXMLLoader ScrambleMenuLoader = new FXMLLoader(getClass().getResource("Scramble-Menu.fxml"));
        Parent root = ScrambleMenuLoader.load();
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setOnKeyPressed(event->{});
        scene.setOnKeyReleased(event->{});
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
    }

    // the purpose of this method is to determine whether we need to recalculate a solution if a user has deviated off the given solution
    private boolean recalculate(String moveType){
        if(moveType.length()==1){rotationTracker++;} // if a clockwise move add 1. else subtract 1.
        else{rotationTracker --;}
        try {
            if(moveType.charAt(0)!=solution.split(" ")[movePos+1].charAt(0)){return true;}// if the user has deviated, recalculate
        } catch (ArrayIndexOutOfBoundsException e){return true;}
        if((rotationTracker+4)%4==0){// if move has completed increment movePos and change the solution to show the new current move. Then return false
            movePos++;
            try{rotationTracker = getValue(solution.split(" ")[movePos + 1]);
            displaySolution();}
            catch (ArrayIndexOutOfBoundsException ignored){}
            return false;
        }
        return false;
    }

    // Handles keybindings. Allows users to make moves not necessarily dictated by the solution.
    public void handlePress(KeyEvent e) throws InterruptedException {
            switch (e.getCode()) {
                // Creates event handlers for keyboard inputs. When shift is being held the boolean variable prime is set to true.
                // This allows for anti-clockwise moves to be performed.
                case SHIFT -> prime.set(true);
                // for each case if prime is true then an anticlockwise turn is made, otherwise a clockwise move is made
                // the gui cube is then updated using rotate

                // rotates the top face
                case W -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    // if prime is true perform anticlockwise moves, otherwise perform clockwise moves
                    if (prime.get()) {
                        cube.antiClockwise(0);
                        cubeGUI.rotateY(-1,1,duration, cube, colours);
                        completedMoves.push("U'");
                        pauseAnimation();
                        Thread.sleep(1); //There was a bug where recalculate was running before the animation set the new rotationTracker
                        //since animateMoves is done on another Thread. Adding in this slight delay is the easiest fix to this problem.
                        //(delay is imperceptible by humans)
                        if(recalculate("U'")){
                            solveCube();//Whenever the user interacts with the cube in a way not dictated by the solution, recalculate solution
                            movePos=0;}
                    } else {
                        cube.clockwise(0);
                        cubeGUI.rotateY(-1,-1,duration, cube, colours);
                        completedMoves.push("U");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("U")){
                            solveCube();
                            movePos=0;}
                    }
                }
                // rotates the left face
                case A -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(1);
                        cubeGUI.rotateX(-1,1,duration, cube, colours);
                        completedMoves.push("L'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("L'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(1);
                        cubeGUI.rotateX(-1,-1,duration, cube, colours);
                        completedMoves.push("L");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("L")){
                            solveCube();
                            movePos=0;}
                    }
                }
                // rotates the front face
                case S -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(2);
                        cubeGUI.rotateZ(-1,1,duration, cube, colours);
                        completedMoves.push("F'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("F'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(2);
                        cubeGUI.rotateZ(-1,-1,duration, cube, colours);
                        completedMoves.push("F");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("F")){
                            solveCube();
                            movePos=0;}
                    }
                }
                // rotates the right face
                case D -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(3);
                        cubeGUI.rotateX(1,-1,duration, cube, colours);
                        completedMoves.push("R'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("R'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(3);
                        cubeGUI.rotateX(1,1,duration, cube, colours);
                        completedMoves.push("R");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("R")){
                            solveCube();
                            movePos=0;}
                    }

                }
                // rotates the back face
                case F -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(4);
                        cubeGUI.rotateZ(1,-1,duration, cube, colours);
                        completedMoves.push("B'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("B'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(4);
                        cubeGUI.rotateZ(1,1,duration, cube, colours);
                        completedMoves.push("B");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("B")){
                            solveCube();
                            movePos=0;}
                    }
                }
                // rotates the bottom face
                case X -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(5);
                        cubeGUI.rotateY(1,-1,duration, cube, colours);
                        completedMoves.push("D'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("D'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(5);
                        cubeGUI.rotateY(1,1,duration, cube, colours);
                        completedMoves.push("D");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("D")){
                        solveCube();
                        movePos=0;}
                    }
                }
            }
    }

    // sets the prime variable to false once the user lets go of shift
    public void handleRelease(KeyEvent e){
        if(e.getCode() == KeyCode.SHIFT){
            prime.set(false);
        }
    }

    // updates entire cube (To be applied after scrambles)
    public void updateFull(Cube cube, Color[] colours){
        cubeGUI.cubies[0][0][2].alterFace(1, colours[cube.getColour(0, 0) - 1]);
        cubeGUI.cubies[1][0][2].alterFace(1, colours[cube.getColour(0, 1) - 1]);
        cubeGUI.cubies[2][0][2].alterFace(1, colours[cube.getColour(0, 2) - 1]);
        cubeGUI.cubies[2][0][1].alterFace(1, colours[cube.getColour(0, 3) - 1]);
        cubeGUI.cubies[2][0][0].alterFace(1, colours[cube.getColour(0, 4) - 1]);
        cubeGUI.cubies[1][0][0].alterFace(1, colours[cube.getColour(0, 5) - 1]);
        cubeGUI.cubies[0][0][0].alterFace(1, colours[cube.getColour(0, 6) - 1]);
        cubeGUI.cubies[0][0][1].alterFace(1, colours[cube.getColour(0, 7) - 1]);
        cubeGUI.cubies[0][0][2].alterFace(2, colours[cube.getColour(1, 0) - 1]);
        cubeGUI.cubies[0][0][1].alterFace(2, colours[cube.getColour(1, 1) - 1]);
        cubeGUI.cubies[0][0][0].alterFace(2, colours[cube.getColour(1, 2) - 1]);
        cubeGUI.cubies[0][1][0].alterFace(2, colours[cube.getColour(1, 3) - 1]);
        cubeGUI.cubies[0][2][0].alterFace(2, colours[cube.getColour(1, 4) - 1]);
        cubeGUI.cubies[0][2][1].alterFace(2, colours[cube.getColour(1, 5) - 1]);
        cubeGUI.cubies[0][2][2].alterFace(2, colours[cube.getColour(1, 6) - 1]);
        cubeGUI.cubies[0][1][2].alterFace(2, colours[cube.getColour(1, 7) - 1]);
        cubeGUI.cubies[0][0][0].alterFace(3, colours[cube.getColour(2, 0) - 1]);
        cubeGUI.cubies[1][0][0].alterFace(3, colours[cube.getColour(2, 1) - 1]);
        cubeGUI.cubies[2][0][0].alterFace(3, colours[cube.getColour(2, 2) - 1]);
        cubeGUI.cubies[2][1][0].alterFace(3, colours[cube.getColour(2, 3) - 1]);
        cubeGUI.cubies[2][2][0].alterFace(3, colours[cube.getColour(2, 4) - 1]);
        cubeGUI.cubies[1][2][0].alterFace(3, colours[cube.getColour(2, 5) - 1]);
        cubeGUI.cubies[0][2][0].alterFace(3, colours[cube.getColour(2, 6) - 1]);
        cubeGUI.cubies[0][1][0].alterFace(3, colours[cube.getColour(2, 7) - 1]);
        cubeGUI.cubies[2][0][0].alterFace(4, colours[cube.getColour(3, 0) - 1]);
        cubeGUI.cubies[2][0][1].alterFace(4, colours[cube.getColour(3, 1) - 1]);
        cubeGUI.cubies[2][0][2].alterFace(4, colours[cube.getColour(3, 2) - 1]);
        cubeGUI.cubies[2][1][2].alterFace(4, colours[cube.getColour(3, 3) - 1]);
        cubeGUI.cubies[2][2][2].alterFace(4, colours[cube.getColour(3, 4) - 1]);
        cubeGUI.cubies[2][2][1].alterFace(4, colours[cube.getColour(3, 5) - 1]);
        cubeGUI.cubies[2][2][0].alterFace(4, colours[cube.getColour(3, 6) - 1]);
        cubeGUI.cubies[2][1][0].alterFace(4, colours[cube.getColour(3, 7) - 1]);
        cubeGUI.cubies[2][0][2].alterFace(5, colours[cube.getColour(4, 0) - 1]);
        cubeGUI.cubies[1][0][2].alterFace(5, colours[cube.getColour(4, 1) - 1]);
        cubeGUI.cubies[0][0][2].alterFace(5, colours[cube.getColour(4, 2) - 1]);
        cubeGUI.cubies[0][1][2].alterFace(5, colours[cube.getColour(4, 3) - 1]);
        cubeGUI.cubies[0][2][2].alterFace(5, colours[cube.getColour(4, 4) - 1]);
        cubeGUI.cubies[1][2][2].alterFace(5, colours[cube.getColour(4, 5) - 1]);
        cubeGUI.cubies[2][2][2].alterFace(5, colours[cube.getColour(4, 6) - 1]);
        cubeGUI.cubies[2][1][2].alterFace(5, colours[cube.getColour(4, 7) - 1]);
        cubeGUI.cubies[0][2][0].alterFace(6, colours[cube.getColour(5, 0) - 1]);
        cubeGUI.cubies[1][2][0].alterFace(6, colours[cube.getColour(5, 1) - 1]);
        cubeGUI.cubies[2][2][0].alterFace(6, colours[cube.getColour(5, 2) - 1]);
        cubeGUI.cubies[2][2][1].alterFace(6, colours[cube.getColour(5, 3) - 1]);
        cubeGUI.cubies[2][2][2].alterFace(6, colours[cube.getColour(5, 4) - 1]);
        cubeGUI.cubies[1][2][2].alterFace(6, colours[cube.getColour(5, 5) - 1]);
        cubeGUI.cubies[0][2][2].alterFace(6, colours[cube.getColour(5, 6) - 1]);
        cubeGUI.cubies[0][2][1].alterFace(6, colours[cube.getColour(5, 7) - 1]);
    }

    // sets the solution to the passed in argument (is called at the end of an AI thread)
    public void setSolution(String solution){
        try {
            this.solution = solution;
            if (this.solution.split(" ")[1].length() == 1) {
                rotationTracker = 3;
            } else {
                if (this.solution.split(" ")[1].charAt(1) == '2') {
                    rotationTracker = 2;
                } else {
                    rotationTracker = 1;
                }
            }
            // Update the GUI with the solution here
            displaySolution();
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    // Used to update the solution at the bottom of the screen. Highlights the current move for the user.
    public void displaySolution() {
        Platform.runLater(() -> {
            TextFlow textFlow = new TextFlow();
            textFlow.setTextAlignment(TextAlignment.CENTER); // Center the text
            textFlow.setPrefWidth(moveDisplay.getWidth()); // Set the preferred width to the width of the label
            textFlow.setLineSpacing(5.0); // Add some line spacing
            String[] moves = solution.split(" ");
            for (int i = 0; i < moves.length; i++) {
                Text text = new Text(moves[i] + " ");
                text.setFont(Font.font("Verdana", 29)); // Set the font size to 29px
                if (i-1 == movePos) {
                    text.setFill(Color.GREEN); // Highlight the current move in green
                } else if (i-1 < movePos) {
                    text.setFill(Color.GRAY); // Set the color of previous moves to gray
                } else {text.setFill(Color.ORANGE); // Set the color of future moves to orange
                }
                textFlow.getChildren().add(text);
            }
            VBox vbox = new VBox(textFlow); // Wrap the TextFlow with a VBox
            vbox.setAlignment(Pos.CENTER); // Center the content vertically
            moveDisplay.setGraphic(vbox); // Replace 'myLabel' with the ID of your label
        });
    }

    // Solves the cube using the CFOP AI
    public void solveCFOP() {
        Thread CFOP = new Thread(new CFOP(cube,  colours, this));
        CFOP.start();
    }

    // Solves the cube using the Beginners AI
    public void solveBeginners() {
        Thread beginners = new Thread(new Beginners(cube, colours,  this));
        beginners.start();
    }

    // Solves the cube using the Thistlethwaite AI
    public void solveThistle() {
        thistle = new Thread(new Thistle(cube,perms, simplified, moveDisplay, colours,this));
        thistle.start();
    }

    // Solves the cube using whatever AI is dictated by the doCFOP and doBeginners booleans
    public void solveCube() throws InterruptedException {
        if(doCFOP){solveCFOP();}
        else if(doBeginners){solveBeginners();}
        else{solveThistle();}
    }
}
