module com.example.wytwornia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.wytwornia to javafx.fxml;
    exports com.example.wytwornia;
}