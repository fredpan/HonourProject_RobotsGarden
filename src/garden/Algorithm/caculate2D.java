package garden.Algorithm;

import com.sun.xml.internal.bind.v2.TODO;
import garden.core.Algorithm;
import garden.model.Robot;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class caculate2D extends Algorithm{


    private int iter;

    private int todo;

    private ArrayList<Robot> state;

    private int range;

    public caculate2D(Robot robot){
        super(robot);
//        this.iter = iter;
//        this.todo = todo;
//        this.state = state;
//        this.range = range;
        this.state = new ArrayList<>(robot.getSensor().getAllVisibleRobotsInLocalScale());
        robot.getSensor().getVision();

    }



    /**
     * Compute the next generation of robots' positions
     * @param  iter current iteration
     * @param todo how many left to generate
     * @param  state current state of robots' positions
     * @param  range vision v of all robots
     */

    public Boolean generate( ArrayList<Robot> state, int range){
        if(todo==0){
            return true;
        }

        //ArrayList<Robot> uniques = getUniqueRobots(state);
        ArrayList newState = new ArrayList();
        for(Robot robot:state){
            ArrayList<Point> visibles = findRobotsVisible(robot,uniques,range);
            //Todo robot need faulty type.
            if(visibles.size()<2){
                newState.add(robot);
                continue;
            }
            Disc C = miniDisc(visibles);
            ArrayList rs = new ArrayList();
            for(Point p:visibles){
                if(p.getX()!=robot.getPositionX()|| p.getY()!= robot.getPositionY()){
                    rs.add(p);
                }
            }
            Point currRobot  = new Point();
            currRobot.setLocation(robot.getPositionX(),robot.getPositionY());
            Point connectedCenter= getConnectedCenter(range,C.getCenter(),currRobot,rs);
            //Todo disscussion




        }












       return generate(iter+1,todo-1,newState,range);
    }

    public Point generateOneRobot (ArrayList<Robot>visibles , int range){
        Point result = new Point();
        ArrayList newState = new ArrayList();
        if (visibles.size()<2){
            result.setLocation(0.0,0.0);
            return result;
        }
        Disc C = miniDisc(visibles);
        ArrayList rs = new ArrayList();
        for(Point p:visibles){
            if(p.getX()!=robot.getPositionX()|| p.getY()!= robot.getPositionY()){
                rs.add(p);
            }
        }
        Point currRobot  = new Point();
        currRobot.setLocation(robot.getPositionX(),robot.getPositionY());
        Point connectedCenter= getConnectedCenter(range,C.getCenter(),currRobot,rs);








    }

















    /**
     * From a given list, only return robots that are at distinct coordinates
     * We realized that if 2 or more robots have the same coordinates and end up in `findDiscWith3Points`, we get a resulting disc that is wrong (infinite disc).
     * Therefore, removing duplicates is necessary.
     *
     * Since then, we restricted even more: duplicates now also include robots that are within a distance of 1e-10
     *
     * @param  robots  a list of all robots
     * @return {Array}
     */
    public ArrayList getUniqueRobots(ArrayList<Robot> robots){
        ArrayList uniques = new ArrayList();

        for(Robot robot:robots){
            ArrayList robotsWithin = findRobotsVisible(robot,uniques,1e-10);
            if (robotsWithin.size()==0){
                uniques.add(robot);

            }

        }
        return uniques;

    }

    /**
     * Retuns the robots visible to a reference robot within a certain range
     *
     * @param  comparedRobot point of reference
     * @param  state current state of robots' positionss
     * @param  range vision v of all robots
     * @return {Array}
     */

    private ArrayList findRobotsVisible(Robot comparedRobot,ArrayList<Robot> state,double range){
        ArrayList visible  = new ArrayList();

        for(Robot robot:state ){
            double distanceSquared = Math.pow((comparedRobot.getPositionX()-robot.getPositionX()),2)+
                    Math.pow((comparedRobot.getPositionY()-robot.getPositionY()),2);
            if(distanceSquared <= range*range){
                visible.add(robot);
            }
        }

      return visible;
    }

    /**
     * Based on `Smallest Enclosing Disc` in "Computational Geometry - Algorithms and Applications - Third Edition"
     *
     * @param  P set of points {x, y}
     * @return {Disc} smallest disc containing all points in `P`
     */

    public Disc miniDisc(ArrayList<Point> P){
        Collections.shuffle(P);
        Point p1 = P.get(0);
        Point p2 = P.get(1);
        Disc D2 = new Disc(p1,p2);

        for(int i= 2;i<P.size();i++){
            Point pi = P.get(i);
            if(!D2.contains(pi)){
                D2 =miniDiscWithPoint(new ArrayList<Point>(P.subList(0,i)),pi);
            }
        }
        return D2;
    }


    /**
     * Based on `Smallest Enclosing Disc` in "Computational Geometry - Algorithms and Applications - Third Edition"
     *
     * @param  P set of points {x, y}
     * @param  q point {x, y} on the boundary of the new disc
     * @return {Disc} smallest enclosing disc for P with q on its boundary
     */

    private Disc miniDiscWithPoint(ArrayList<Point> P, Point q){
        Point p1 = P.get(0);
        Disc D1 = new Disc(p1,q);

        for(int i=1; i<P.size();i++){
            Point pi = P.get(i);
            if(!D1.contains(pi)){
                D1 = miniDiscWith2Points(new ArrayList<Point>(P.subList(0,i)),pi,q);
            }

        }
        return D1;

    }



    /**
     * Based on `Smallest Enclosing Disc` in "Computational Geometry - Algorithms and Applications - Third Edition"
     *
     * @param  P set of points {x, y}
     * @param q1 point {x, y} on the boundary of the new disc
     * @param q2 point {x, y} on the boundary of the new disc
     * @return {Disc} smallest enclosing disc for P with q1 and q2 on its boundary
     */
    private Disc miniDiscWith2Points(ArrayList<Point> P,Point q1,Point q2){
        Disc D0 = new Disc(q1,q2);
        for (Point pk : P){
            if(!D0.contains(pk)){
                D0 = new Disc(q1,q2,pk);
            }
        }


    return D0;
    }


    /**
     * Apply the algorithm to make sure that the next position (ci) of the current robot (Ri) stills maintains connectivity
     * with its neighbours (R)
     *
     * @param  V
     * @param  ci
     * @param  Ri
     * @param  R
     */

    public Point getConnectedCenter(int V,Point ci, Point Ri,ArrayList<Point> R){
        Vector Vgoal = new Vector(Ri,ci);
        if(Vgoal.getNorm()==0){
            return Ri;
        }

        ArrayList<Double> test = new ArrayList<Double>();
        for(Point Rj:R){
            Vector Vrirj = new Vector(Ri,Rj);
            double dj = Vrirj.getNorm();
            ArrayList cosAndsin = Vrirj.getCosAndSin(Vgoal);
            double lj = (dj/2*(double)cosAndsin.get(0))+Math.sqrt((V/2)*(V/2)-Math.pow((dj/2*(double)cosAndsin.get(1)),2));
             test.add(lj);
        }
        double limit = 0;
        for(double num:test){
            if(num<limit){
                limit = num;
            }
        }
        double D = Math.min(Vgoal.getNorm(),limit);
        return Vgoal.resize(D).getEnd();

    }


    @Override
    public Point next() {
        return null;
    }
}