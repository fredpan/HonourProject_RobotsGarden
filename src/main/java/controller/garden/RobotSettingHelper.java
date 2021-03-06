package controller.garden;


import controller.controlpanel.ControlPanelFacade;
import core.AlgorithmClassLoader;
import core.AlgorithmLoadingHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Algorithm;
import model.Robot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

public class RobotSettingHelper extends VBox {

    public static final double DEFAULT_ROBOT_VISION = 100;
    public static final double DEFAULT_ROBOT_Radius = 10;
    public static final double DEFAULT_ROBOT_UNIT = Double.POSITIVE_INFINITY;
    public static final String DEFAULT_COLOR_TEXT = "Select an color for robots: ";
    public static final String DEFAULT_VISION_TEXT = "Select an vision for robots: ";
    public static final String DEFAULT_UNIT_TEXT = "Select an unit for robots: ";
    private static final String WARING_TEXT = "Warning information";

    @FXML
    private ComboBox<Algorithm> algorithmSelection;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private TextField inputVision;
    @FXML
    private TextField inputUnit;
    @FXML
    private Text colorText;
    @FXML
    private Text visionText;
    @FXML
    private Text unitText;
    @FXML
    private RadioButton visionCheck;
    @FXML
    private RadioButton unitCheck;
    @FXML
    private RadioButton unitRandom;
    @FXML
    private RadioButton unitCustomize;
    @FXML
    private Text warning;
    @FXML
    private HBox settingPage;
    @FXML
    private Button Confirm;
    @FXML
    private Button Cancel;

    private ControlPanelFacade controlPanelFacade;

    private AlgorithmLoadingHelper algorithmLoadingHelper = new AlgorithmLoadingHelper();

    private Algorithm selectedAlgorithm;

    private double selectedRobotVision;

    private Paint selectedRobotColor = Color.BLACK;

    private double selectedRobotUnit;

    private List<Robot> robots;

    private Robot robot;

    private boolean selectRandom = false;

    private final Stage dialog;

    public RobotSettingHelper(GardenController gardenController,Robot robot) {


        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(gardenController.getCoordinateSystem().getScene().getWindow());
        FXMLLoader fxmlLoader;
        this.robots = robots;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/robot_setting.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            Parent parent = fxmlLoader.load();
            Scene dialogScene = new Scene(parent);
            dialog.setScene(dialogScene);
            dialog.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initNodesText(robot);
        visionCheckListener();
        unitCheckListener();
        inputVisionListener();
        inputUnitListener();
        colorPickerListener();
        algorithmSelectionInit();
        algorithmSelectionListener();
        confirmListener();
        cancelListener();
    }

    private void initNodesText(Robot robot) {
        this.robot = robot;
        settingPage.setVisible(true);
        warning.setText(WARING_TEXT);
        inputVision.setText(DEFAULT_ROBOT_VISION + "");
        selectedRobotVision = DEFAULT_ROBOT_VISION;
        inputUnit.setText("");
        inputUnit.setDisable(true);
        selectedRobotUnit = DEFAULT_ROBOT_UNIT;
        colorText.setText(DEFAULT_COLOR_TEXT);
        visionText.setText(DEFAULT_VISION_TEXT);
        unitText.setText(DEFAULT_UNIT_TEXT);
        unitCheck.setUserData("Infinity");
        unitRandom.setUserData("Random");
        unitCustomize.setUserData("Customize");
        if(robot.getVision()<Math.sqrt(800*800*2)){
            inputVision.setText(robot.getVision() + "");
            selectedRobotVision = robot.getVision();
        }else {
            inputVision.setText("");
            inputVision.setDisable(true);
            visionCheck.setSelected(true);
        }
        if(robot.isRandomUnit()){
            unitRandom.setSelected(true);

        }else if(robot.getStep()<Double.POSITIVE_INFINITY){

                inputUnit.setDisable(false);
                inputUnit.setText(robot.getStep()+"");
                selectedRobotUnit = robot.getStep();
                unitCustomize.setSelected(true);


        }else{
            unitCheck.setSelected(true);
        }


        selectedRobotColor = robot.getGraphicalDisplay().getRobotBody().getFill();
        colorPicker.setValue(Color.valueOf(selectedRobotColor+""));
        selectedAlgorithm = robot.getAlgorithm();




    }

    private void inputVisionListener() {

        inputVision.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    selectedRobotVision = Integer.valueOf(inputVision.getText());//todo VISON: must press return to trigger, better way? use int or double?
                } catch (NumberFormatException e) {
                    warning.setText("Robot Vision must be an int");
                }

            }
        });

    }

    private void inputUnitListener(){
        inputUnit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try{
                    selectedRobotUnit = Double.valueOf(inputUnit.getText());
                }catch (NumberFormatException e){
                    warning.setText("Robot unit number must be an int or double");
                }
            }
        });
    }

    //todo Vison: Duplicated CODE HERE?

//    private void inputVisionListener() {
//        inputVision.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    selectedRobotVision = Integer.valueOf(inputVision.getText());//todo VISON: must press return to trigger, beeter way? use int or double?
//                } catch (NumberFormatException e) {
//                    warning.setText("Robot Vision must be an int");
//                }
//
//            }
//        });
//    }
//
//    private void inputUnitListener(){
//        inputUnit.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try{
//                    selectedRobotUnit = Double.valueOf(inputUnit.getText());
//                }catch (NumberFormatException e){
//                    warning.setText("Robot unit number must be an int or double");
//                }
//            }
//        });
//    }

    private void visionCheckListener(){
        visionCheck.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(visionCheck.isSelected()){
                    selectedRobotVision = Math.sqrt(800*800*2);
                    //inputVision.setText("");
                    inputVision.setDisable(true);

                }else{
                    inputVision.setDisable(false);
                    inputVision.setText(robot.getVision()+"");
                    selectedRobotVision = robot.getVision();
                }
            }
        });
    }

    private void unitCheckListener(){
        ToggleGroup group = new ToggleGroup();
        unitCheck.setToggleGroup(group);
        unitRandom.setToggleGroup(group);
        unitCustomize.setToggleGroup(group);


        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(group.getSelectedToggle().getUserData().toString().equals("Infinity")){
                    inputUnit.setText("");
                    inputUnit.setDisable(true);
                    selectedRobotUnit = robot.getStep();
                    selectRandom = false;
                }else if(group.getSelectedToggle().getUserData().toString().equals("Customize")){
                    inputUnit.setText("");
                    inputUnit.setDisable(false);
                    selectRandom = false;

                }else if(group.getSelectedToggle().getUserData().toString().equals("Random")){
                    inputUnit.setText("");
                    inputUnit.setDisable(true);
                    selectRandom = true;

                }


            }
        });

    }


    private void algorithmSelectionInit() {
        ObservableList<Algorithm> value = FXCollections.observableArrayList();
        List<Algorithm> allAlgInfo = null;
        try {
            allAlgInfo = algorithmLoadingHelper.getAlgorithmList();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        value.addAll(allAlgInfo);
 //       System.out.println("!!"+value.get(0).getClass().getName());
        algorithmSelection.setItems(value);
//        AlgorithmLoadingHelper algorithmLoadingHelper = new AlgorithmLoadingHelper();
//        String algorithmFileName = "";
//        try {
//            List<Algorithm> algorithmList = algorithmLoadingHelper.getAlgorithmList();
//            for (Algorithm algName : algorithmList) {
//                if (algName.algorithmName().equals(selectedAlgorithm.algorithmName())) {
//                    algorithmFileName = algName.getClass().getSimpleName();//todo VISON: What is it used for?
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
        Iterator<Algorithm> iterator = algorithmSelection.getItems().iterator();
        Algorithm isSelectedOne = null;
        while (iterator.hasNext()) {
            Algorithm currAlg = iterator.next();
           if (currAlg.getClass().getSimpleName().equals(robot.getAlgorithm().getClass().getSimpleName())) {
               isSelectedOne = currAlg;
               break;

           }
        }

        algorithmSelection.setConverter(new StringConverter<Algorithm>() {
            @Override
            public String toString(Algorithm object) {
                return object.algorithmName();
            }

            @Override
            public Algorithm fromString(String string) {
                try {
                    return AlgorithmClassLoader.getAlgorithmInstanceByName(string);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Algorithm not found");
                }
            }
        });

        if (isSelectedOne != null) {
            algorithmSelection.getSelectionModel().select(isSelectedOne);//todo VISON: display issue?

        }
//        System.out.println(selectedAlgorithm);
//        System.out.println(algorithmFileName+"~~~~~");
//        System.out.println(algorithmSelection.getSelectionModel().getSelectedIndex());

    }

    private void algorithmSelectionListener() {
        algorithmSelection.valueProperty().addListener(
                (obs, oldVal, newVal) -> selectedAlgorithm = newVal
        );
    }

    private void colorPickerListener() {
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedRobotColor = colorPicker.getValue();

            }
        });
    }

    private void confirmListener(){
        Confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                robot.getGraphicalDisplay().setRobotVision(selectedRobotVision);
                algorithmLoadingHelper.assignAlgorithmToRobot(robot, selectedAlgorithm.getClass().getSimpleName());
                robot.setUnit(selectedRobotUnit);
                robot.getGraphicalDisplay().setColor(selectedRobotColor);
                robot.setRandomUnit(selectRandom);
                dialog.close();

            }
        });

    }

    private void cancelListener(){
        Cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialog.close();
            }
        });


    }



}