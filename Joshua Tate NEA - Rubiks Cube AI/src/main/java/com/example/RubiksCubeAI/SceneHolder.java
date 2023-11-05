package com.example.RubiksCubeAI;

import javafx.scene.Scene;

public class SceneHolder {
    private static SceneHolder instance = null;
    private Scene scene;

    private SceneHolder() {}

    public static SceneHolder getInstance() {
        if (instance == null) {
            instance = new SceneHolder();
        }
        return instance;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
