package com.example.RubiksCubeAI;

import javafx.scene.Scene;

public class SceneHolder {
    private static SceneHolder instance = null;
    private Scene scene;

    private SceneHolder() {}

    // Returns instance of SceneHolder
    public static SceneHolder getInstance() {
        if (instance == null) {
            instance = new SceneHolder();
        }
        return instance;
    }

    // Used to set scene and to get scene
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
