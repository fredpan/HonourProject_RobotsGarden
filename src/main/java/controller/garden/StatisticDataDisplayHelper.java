package controller.garden;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Robot;
import model.StatisticData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatisticDataDisplayHelper extends VBox {

    @FXML
    private Text statisticDataDisplay;

    public StatisticDataDisplayHelper(GardenController gardenController, Robot robot) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(gardenController.getCoordinateSystem().getScene().getWindow());
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/robot_statistic_display.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            Parent parent = fxmlLoader.load();
            Scene dialogScene = new Scene(parent, 200, 400);
            dialog.setScene(dialogScene);
            dialog.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String displayInfo = "";
        System.out.println(robot);
        HashMap<String, StatisticData> statisticDataList = gardenController.getControlPanelFacade().getStatisticDataByRobotTag(robot.getTag());
//        System.out.println(statisticDataList.get(0));
        for (Map.Entry<String, StatisticData> statisticData : statisticDataList.entrySet()) {
            displayInfo = displayInfo + "\n" + statisticData.getKey() + ": " + statisticData.getValue().display();
        }
        statisticDataDisplay.setText(displayInfo);
    }
}
