module hr.java.waterusage.waterusagejavafxaplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;



    opens hr.java.waterUsageJavaFxApplication to javafx.fxml;
    exports hr.java.waterUsageJavaFxApplication;
}