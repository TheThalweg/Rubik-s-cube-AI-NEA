package com.example.RubiksCubeAI;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

public class CubeController implements Initializable {
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
    float duration;
    private static final double windowX = 1920;
    private static final double windowY = 880;
    private static final double CAMERA_X = -windowX/2;
    private static final double CAMERA_Y = -windowY/2;
    private static final double CAMERA_Z = -100;
    private double mouseOldX, mouseOldY;

    /*This is used to store solutions so that they can be accessed by the GUI. the AIs add to this queue when they solve something
    and the GUI retrieves the solution to be added to the label at the bottom of the screen
    public BlockingQueue<String> solutions = new LinkedBlockingQueue<>();*/
    public String[] solutionContainer = new String[1];
    private final CyclicBarrier barrier = new CyclicBarrier(2);

    private Cube cube;

    private Cube simplified;
    private final Color[] colours = {Color.YELLOW, Color.RED, Color.GREEN, Color.ORANGE,Color.BLUE,Color.WHITE};

    //keeps track of completed moves. Used for back button.
    private final Stack<String> completedMoves = new Stack<>();
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

    //dictates which AI is used
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
        Group group = new Group();
        group.getChildren().add(cubeGUI.getModel());
        SubScene subScene = new SubScene(group, windowX, windowY, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.web("#151515"));
        cubePane.getChildren().add(subScene);
        duration = 120;
        subScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        subScene.setOnMouseDragged(event -> {
            double rotX = event.getSceneY() - mouseOldY;
            double rotY = event.getSceneX() - mouseOldX;
            camera.getTransforms().addAll(new Rotate(-rotX, - CAMERA_X, - CAMERA_Y,  - CAMERA_Z, Rotate.X_AXIS), new Rotate(rotY,  - CAMERA_X,  - CAMERA_Y, - CAMERA_Z, Rotate.Y_AXIS));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        CornerPerms cornerPerms = new CornerPerms();
        perms = cornerPerms.calculatePerms(new Node(new Cube(),null,null));
    }

    public void setAI(boolean doCFOP, boolean doBeginners) {
        this.doCFOP = doCFOP;
        this.doBeginners = doBeginners;
    }

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
                completedMoves.add(move); //adds move to completedMove stack
                animateMove(move);
                movePos++;
                displaySolution();
                if(movePos==moves.length-1){
                    switchVisibility();
                }
                try {
                    Thread.sleep(Math.round(duration) + 27);
                } catch (InterruptedException e) {
                    setRotationTracker();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        animationThread.start();
    }

    public void switchVisibility(){
        if(playButton.isVisible()){
        playButton.setVisible(false);
        pauseButton.setVisible(true);
        }else{playButton.setVisible(true);
            pauseButton.setVisible(false);}
    }

    @FXML
    public void forward() throws InterruptedException {
        pauseAnimation();
        Thread.sleep(1);
        try{
        String move = solution.split(" ")[movePos+1];
        switch (rotationTracker){
            case 1-> move = move.charAt(0)+"'";
            case 2-> move = move.charAt(0)+"2";
            case 3-> move = move.charAt(0)+"";
        }
        animateMove(move);
        completedMoves.add(move);
        movePos++;
        setRotationTracker();
        displaySolution();} catch (RuntimeException ignored){}
    }

    @FXML
    public void pauseAnimation(){
        if(pauseButton.isVisible()){
        switchVisibility();}
        if (animationThread != null) {
            animationThread.interrupt();
            animationThread = null;
        }
    }
    @FXML
    public void resumeAnimation(){
        if (animationThread == null) {
            animateMoves();
        }
    }

    static String invertMove(String lastMove){
        if(lastMove.length()==1){return lastMove+"'";}
        if(lastMove.charAt(1)=='2'){return lastMove;}
        return Character.toString(lastMove.charAt(0));
    }

    public void setRotationTracker(){
        //sets the rotation tracker to the value dictated by the move we are currently on
        try {
            if (solution.split(" ")[movePos + 1].length() == 1) {
                rotationTracker = 3;
            } else {
                if (solution.split(" ")[movePos + 1].charAt(1) == '2') {
                    rotationTracker = 2;
                } else {
                    rotationTracker = 1;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored){}
    }

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
    @FXML
    public void back(){
        pauseAnimation();
        try {
            String lastMove = completedMoves.pop(); //gets last move from stack
            animateMove(invertMove(lastMove));
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
            }else{solveCube();}
        } catch (RuntimeException ignored){} catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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

    @FXML
    public void homeButton() throws IOException {
        AudioClip click = new AudioClip(Objects.requireNonNull(getClass().getResource("/click.mp3")).toExternalForm());
        click.play();
        //resets AI choice back to normal
        doCFOP = false;
        doBeginners = false;
        pauseAnimation();
        //Stops the Thistle Thread from running if it is in the process of calculating
        if(!(doBeginners||doCFOP)){
            if(thistle!=null){
                thistle.interrupt();
                thistle = null;
            }
        }
        FXMLLoader ScrambleMenuLoader = new FXMLLoader(getClass().getResource("Scramble-Menu.fxml"));
        Parent root = ScrambleMenuLoader.load();
        // Get the Scene from the SceneHolder and change its root node
        Scene scene = SceneHolder.getInstance().getScene();
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.show();
    }

    //the purpose of this method is to determine whether we need to recalculate a solution if a user has deviated off the given solution
    private boolean recalculate(String moveType){
        if(moveType.length()==1){rotationTracker++;} //if a clockwise move add 1. else subtract 1.
        else{rotationTracker --;}
        try {
            if(moveType.charAt(0)!=solution.split(" ")[movePos+1].charAt(0)){return true;}//i.e. if the user has deviated, recalculate
        } catch (ArrayIndexOutOfBoundsException e){return true;}
        if((rotationTracker+4)%4==0){//if move has completed increment movePos and change the solution to show the new current move. Then return false
            movePos++;
            try{
            if(solution.split(" ")[movePos+1].length()==1){
                rotationTracker=3; //one clockwise move needed to complete
            }else{if(solution.split(" ")[movePos+1].charAt(1)=='2'){rotationTracker=2;}
            else{rotationTracker=1;}}
            displaySolution();}
            catch (ArrayIndexOutOfBoundsException ignored){}
            return false;
        }
        return false;
    }

    public void handlePress(KeyEvent e) throws BrokenBarrierException, InterruptedException {
            switch (e.getCode()) {
                //Creates event handlers for keyboard inputs. When shift is being held the boolean variable prime is set to true.
                //This allows for anti-clockwise moves to be performed.
                case SHIFT -> prime.set(true);
                //for each case if prime is true then an anticlockwise turn is made, otherwise a clockwise move is made
                //the gui cube is then updated using updateGUI
                case W -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(0);
                        cubeGUI.rotateY(-1,1,duration, cube, colours);
                        completedMoves.add("U'");
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
                        completedMoves.add("U");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("U")){
                            solveCube();
                            movePos=0;}
                    }
                }
                case A -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(1);
                        cubeGUI.rotateX(-1,1,duration, cube, colours);
                        completedMoves.add("L'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("L'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(1);
                        cubeGUI.rotateX(-1,-1,duration, cube, colours);
                        completedMoves.add("L");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("L")){
                            solveCube();
                            movePos=0;}
                    }
                }
                case S -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(2);
                        cubeGUI.rotateZ(-1,1,duration, cube, colours);
                        completedMoves.add("F'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("F'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(2);
                        cubeGUI.rotateZ(-1,-1,duration, cube, colours);
                        completedMoves.add("F");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("F")){
                            solveCube();
                            movePos=0;}
                    }
                }
                case D -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(3);
                        cubeGUI.rotateX(1,-1,duration, cube, colours);
                        completedMoves.add("R'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("R'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(3);
                        cubeGUI.rotateX(1,1,duration, cube, colours);
                        completedMoves.add("R");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("R")){
                            solveCube();
                            movePos=0;}
                    }

                }
                case F -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(4);
                        cubeGUI.rotateZ(1,-1,duration, cube, colours);
                        completedMoves.add("B'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("B'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(4);
                        cubeGUI.rotateZ(1,1,duration, cube, colours);
                        completedMoves.add("B");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("B")){
                            solveCube();
                            movePos=0;}
                    }
                }
                case X -> {
                    AudioClip singleTurn = new AudioClip(Objects.requireNonNull(getClass().getResource("/SingleTurn.wav")).toExternalForm());
                    singleTurn.play();
                    if (prime.get()) {
                        cube.antiClockwise(5);
                        cubeGUI.rotateY(1,-1,duration, cube, colours);
                        completedMoves.add("D'");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("D'")){
                            solveCube();
                            movePos=0;}
                    } else {
                        cube.clockwise(5);
                        cubeGUI.rotateY(1,1,duration, cube, colours);
                        completedMoves.add("D");
                        pauseAnimation();
                        Thread.sleep(1);
                        if(recalculate("D")){
                        solveCube();
                        movePos=0;}
                    }
                }
            }
    }


    public void handleRelease(KeyEvent e){
        if(e.getCode() == KeyCode.SHIFT){
            prime.set(false);
        }
    }

    //updates entire cube (To be applied after scrambles)
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
    public void getSolution(){
        Task<String> getSolutionTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                barrier.await();  // Wait until the CFOP/beginners thread has finished
                return solutionContainer[0];
            }
        };

        getSolutionTask.setOnSucceeded(event -> {
            solution = getSolutionTask.getValue();
            if(solution.split(" ")[1].length()==1){rotationTracker=3;}
            else{if(solution.split(" ")[1].charAt(1)=='2'){rotationTracker=2;}
            else{rotationTracker=1;}}
            // Update the GUI with the solution here
            displaySolution();
        });
        new Thread(getSolutionTask).start();
    }
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
    public void solveCFOP() {
        Thread CFOP = new Thread(new CFOP(cube,  colours, duration, solutionContainer, barrier));
        CFOP.start();
        getSolution();
    }

    public void solveBeginners() {
        Thread beginners = new Thread(new Beginners(cube, colours, duration, solutionContainer, barrier));
        beginners.start();
        getSolution();
    }

    public void solveThistle() {
        thistle = new Thread(new Thistle(cube,perms, simplified, moveDisplay, colours, duration,solutionContainer,barrier));
        thistle.start();
        getSolution();
    }

    public void solveCube() throws BrokenBarrierException, InterruptedException {
        if(doCFOP){solveCFOP();}
        else if(doBeginners){solveBeginners();}
        else{solveThistle();}
    }
}
