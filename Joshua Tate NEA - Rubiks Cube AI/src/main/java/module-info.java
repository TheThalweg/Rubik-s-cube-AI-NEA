module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.media;

    opens com.example.RubiksCubeAI to javafx.fxml;
    exports com.example.RubiksCubeAI;
}