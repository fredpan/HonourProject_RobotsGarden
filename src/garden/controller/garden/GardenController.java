package garden.controller.garden;

import garden.Algorithm.caculate2D;
import garden.model.Robot;
import garden.model.RobotGraphicalDisplay;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The controller of the garden.fxml
 * The Garden is the window that contains all the robots
 */
public class GardenController extends VBox {

    /**
     * The size (radius) of the robot that displays on the screen. The default value is 21.
     */
    private static double ROBOT_SIZE = 9;

    private List<Robot> robots = Collections.synchronizedList(new ArrayList<>());

    /**
     * The garden instance
     */
    @FXML
    private Pane garden;

    public GardenController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../view/garden.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        robotsInitBooster();

    }

    public void updateGarden() {
        garden.getChildren().removeAll(garden.getChildren());//remove all the element
        List<Circle> robotsPosition = new ArrayList<>();
        List<Circle> robotsBodyAndBorder = new ArrayList<>();
//        List<Circle> robotsBody = new ArrayList<>();
//        List<Circle> robotsBorder = new ArrayList<>();
        List<Circle> robotsVision = new ArrayList<>();
        for (Robot robot : robots) {
            RobotGraphicalDisplay robotGraphicalDisplay = robot.getGraphicalDisplay();
            Circle robotPosition = robotGraphicalDisplay.getRobotPosition();
            Circle robotBody = robotGraphicalDisplay.getRobotBody();
            Circle robotBorder = robotGraphicalDisplay.getRobotBorder();
            Circle robotVision = robotGraphicalDisplay.getRobotVision();
            if (robotGraphicalDisplay.isVisionVisible()) {
                robotsVision.add(robotVision);
            }
            robotsBodyAndBorder.add(robotBorder);
            robotsBodyAndBorder.add(robotBody);
//            robotsBody.add(robotBody);
//            robotsBorder.add(robotBorder);
            robotsPosition.add(robotPosition);
        }
        //ensure the vision is always under the border
        garden.getChildren().addAll(robotsVision);


        //todo how to display?
//        //ensure the border is always under the body
//        garden.getChildren().addAll(robotsBorder);
//        //ensure the body is always under the position
//        garden.getChildren().addAll(robotsBody);
        //ensure the body is always under the position
        garden.getChildren().addAll(robotsBodyAndBorder);


        //ensure the position is always in front of anything
        garden.getChildren().addAll(robotsPosition);
    }

    /**
     * Get the size of the robots that display on the screen
     *
     * @return size of the robots
     */
    public static double getRobotSize() {
        return ROBOT_SIZE;
    }

    /**
     * Set the size of the robots that display on the screen
     *
     * @param robotSize size of the robots. Throw IllegalArgumentException if the size is not bigger than 0.
     */
    public static void setRobotSize(double robotSize) {
        if (robotSize <= 0) {
            throw new IllegalArgumentException("The robot size must bigger than 0");
        }
        ROBOT_SIZE = robotSize;
    }

    /**
     * get the list of robots.
     * @return the list of the robots
     */
    public List<Robot> getRobots() {
        return robots;
    }

    /**
     * Set the list of robots.
     * Note, one should avoid to use this method, instead, change the content of the robots directly.
     * @param robots the new list of the robots
     */
    public void setRobots(ArrayList<Robot> robots) {
        this.robots = robots;
    }

    /**
     * Init the robots: add on click to the pane(garden) so where ever click the pane, a new robots will be created.
     */
    private void robotsInitBooster() {
        //set onClickListener for creating robots
        garden.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {// add listener for left click
                    Robot robot = robotGenerator("New Robot", event.getX(), event.getY());
                    //adding to the graph
                    updateGarden();//using this method for insert in order to ensure the robot position is always overlapped the robot body and the robot body is always in front of the robot vision.
                }
            }
        });
    }

    /**
     * @param tag todo
     * @param x
     * @param y   for now, generate and register the listener
     * @return
     */
    public Robot robotGenerator(String tag, double x, double y) {
        Robot robot = new Robot(new RobotGraphicalDisplay());
        robot.moveTo(x, y);
        robot.setTag(tag);
        addOnClickListenerToRobot(robot);
        robots.add(robot);//add the robot into the robots list
        robot.setAlgorithm(new caculate2D(robot));
        return robot;
    }

    private void addOnClickListenerToRobot(Robot robot) {
        RobotGraphicalDisplay robotGraphicalDisplay = robot.getGraphicalDisplay();
        //set onClickListener for opening robot setting & displaying vision range
        robotGraphicalDisplay.getRobotBody().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {// for each of the btn that has added event, add one right click listener for it.
                    RobotSettingHelper robotSettingHelper = new RobotSettingHelper(robot, garden.getScene().getWindow());
                } else if (event.getButton() == MouseButton.MIDDLE) {
                    robot.getGraphicalDisplay().toggleVisionVisible();
                    updateGarden();
                }
            }
        });
    }

    public Pane getGarden() {
        return garden;
    }
}
