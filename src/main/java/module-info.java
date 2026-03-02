module org.gletchick.lab2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    requires static lombok;

    opens org.gletchick.lab2.db to javafx.base;

    opens org.gletchick.lab2.ui to javafx.fxml;

    exports org.gletchick.lab2;
    exports org.gletchick.lab2.ui;
    opens org.gletchick.lab2.model to javafx.base;
    opens org.gletchick.lab2.repository to javafx.base;
}