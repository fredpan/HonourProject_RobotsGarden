package algorithms.src.fakedalgorithmhelper;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Algorithm;
import model.DisplayAdapter;

import java.util.Random;

public class mAdapter extends DisplayAdapter<Circle> {

    private Random random = new Random();

    private Algorithm algorithm;

    public mAdapter() {

        //create a new AdditionalAdapter and inited it with shape Circle and TAG "Random move this robot".
        super(new Circle(27, Color.GRAY), "Random move this robot");
        //make this adapter's shape invisible.
        setVisible(false);
    }

    //Customized method to pass the algorithm into its adapter
    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }


    //Core method, to move the robot to a random position
    @Override
    public void update() {
        moveTo(algorithm.getRobot().getPositionX(), algorithm.getRobot().getPositionY());
        algorithm.getRobot().moveTo(random.nextInt(150), random.nextInt(150));
    }
}
