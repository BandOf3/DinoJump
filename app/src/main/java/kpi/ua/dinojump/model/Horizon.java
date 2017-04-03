package kpi.ua.dinojump.model;

import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import kpi.ua.dinojump.Constants;


public class Horizon extends BaseEntity {

    private final static double BG_CLOUD_SPEED = 0.2;
    private final static double CLOUD_FREQUENCY = .5;
    private final static int MAX_CLOUDS = 6;

    private Point dimensions;
    private double cloudSpeed;
    private List<Cloud> clouds;
    private double cloudFrequency;
    private double gapCoefficient;
    private HorizonLine horizonLine;
    private List<Obstacle> obstacles;
    private List<Integer> obstacleHistory;

    public Horizon(Point dimension, double gapCoefficient) {
        this.dimensions = dimension;
        this.gapCoefficient = gapCoefficient;
        this.cloudFrequency = CLOUD_FREQUENCY;
        this.clouds = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.obstacleHistory = new ArrayList<>();
        this.cloudSpeed = BG_CLOUD_SPEED;
        this.init();
    }

    private void init() {
        this.addCloud();
        this.horizonLine = new HorizonLine(Constants.HORIZON);
    }

    private void addCloud() {
        this.clouds.add(new Cloud(Constants.CLOUD,
                this.dimensions.x));
    }

    public void update(long deltaTime, double currentSpeed, boolean updateObstacles) {
        this.horizonLine.update(deltaTime, currentSpeed);
        this.updateClouds(deltaTime, currentSpeed);
        if (updateObstacles) {
            this.updateObstacles(deltaTime, currentSpeed);
        }
    }

    public void draw(Canvas canvas) {
        for (Cloud cloud : clouds) {
            cloud.draw(canvas);
        }
        horizonLine.draw(canvas);
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas);
        }
    }

    private void updateObstacles(long deltaTime, double currentSpeed) {
        // Obstacles, move to Horizon layer.
        int delObs = -1;

        for (int i = 0; i < this.obstacles.size(); i++) {
            Obstacle obstacle = this.obstacles.get(i);
            obstacle.update(deltaTime, currentSpeed);
            // Clean up existing obstacles.
            if (obstacle.isRemove()) {
                delObs = i;
            }
        }
        if (delObs >= 0 && delObs < obstacles.size())
            obstacles.remove(delObs);
        if (!this.obstacles.isEmpty()) {
            Obstacle lastObstacle = this.obstacles.get(this.obstacles.size() - 1);
            if (lastObstacle != null && !lastObstacle.followingObstacleWasCreated() &&
                    lastObstacle.isVisible() &&
                    (lastObstacle.getXPosition() + lastObstacle.getWidth() + lastObstacle.getGap()) <
                            this.dimensions.x) {
                this.addNewObstacle(currentSpeed);
                lastObstacle.setFollowingObstacleCreated(true);
            }
        } else {
            this.addNewObstacle(currentSpeed);
        }
    }

    private void addNewObstacle(double currentSpeed) {
        int obstacleTypeIndex = (int) getRandomNum(0, Obstacle.types.length - 1);
        Obstacle.types.ObstacleTypes obstacleType = Obstacle.types.getObstacleTypes(obstacleTypeIndex);
        // Check for multiples of the same type of obstacle.
        // Also check obstacle is available at current speed.
        if (this.duplicateObstacleCheck(obstacleType.type) || currentSpeed < obstacleType.minSpeed) {
            this.addNewObstacle(currentSpeed);
        } else {
            this.obstacles.add(new Obstacle(obstacleType, dimensions, gapCoefficient, currentSpeed));
            this.obstacleHistory.add(obstacleType.type);
            if (this.obstacleHistory.size() > 1) {
                obstacleHistory.remove(0);
            }
        }
    }

    private boolean duplicateObstacleCheck(int nextObstacleType) {
        int duplicateCount = 0;
        for (int i = 0; i < this.obstacleHistory.size(); i++) {
            duplicateCount = this.obstacleHistory.get(i) == nextObstacleType ? duplicateCount + 1 : 0;
        }
        return duplicateCount >= Constants.MAX_OBSTACLE_DUPLICATION;
    }

    private void updateClouds(long deltaTime, double speed) {
        double cloudSpeed = this.cloudSpeed / 1000 * deltaTime * speed;
        int numClouds = clouds.size();
        if (numClouds > 0) {
            for (Cloud cloud : clouds) {
                cloud.update(cloudSpeed);
            }
            Cloud lastCloud = clouds.get(numClouds - 1);
            if (numClouds < MAX_CLOUDS &&
                    (this.dimensions.x - lastCloud.getXPosition()) > lastCloud.getCloudGap() &&
                    this.cloudFrequency > Math.random()) {
                this.addCloud();
            }
            List<Cloud> newClouds = new ArrayList<>(clouds);
            for (int i = 0; i < newClouds.size(); i++) {
                if (newClouds.get(i).removeItemFromScreen()) {
                    clouds.remove(i);
                    break;
                }
            }
        }
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void reset() {
        this.horizonLine.reset();
        this.obstacles.clear();
    }
}
