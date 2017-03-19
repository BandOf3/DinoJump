package kpi.ua.dinojump.entities;

import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import kpi.ua.dinojump.Runner;


public class Horizon extends BaseEntity {

    private static class config {
        public static double BG_CLOUD_SPEED = 0.2;
        public static double CLOUD_FREQUENCY = .5;
        public static int MAX_CLOUDS = 6;
    }

    private Point dimensions;
    private double cloudSpeed;
    private List<Cloud> clouds;
    private double cloudFrequency;
    private double gapCoefficient;
    public HorizonLine horizonLine;
    private List<Obstacle> obstacles;
    private List<Integer> obstacleHistory;

    public Horizon(Point dimension, double gapCoefficient) {
        this.dimensions = dimension;
        this.gapCoefficient = gapCoefficient;
        this.cloudFrequency = config.CLOUD_FREQUENCY;
        this.clouds = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.obstacleHistory = new ArrayList<>();
        this.cloudSpeed = config.BG_CLOUD_SPEED;
        this.init();
    }

    private void init() {
        this.addCloud();
        this.horizonLine = new HorizonLine(Runner.spritePos.HORIZON);
    }

    private void addCloud() {
        this.clouds.add(new Cloud(Runner.spritePos.CLOUD,
                this.dimensions.x));
    }

    public void update(Object... args) {
        long deltaTime = 0;
        double currentSpeed = 0;
        boolean updateObstacles = false;
        if (args.length > 0) {
            deltaTime = (long) args[0];
            if (args.length > 1) {
                currentSpeed = (double) args[1];
                if (args.length > 2) {
                    updateObstacles = (boolean) args[2];
                }
            }
        }
        this.horizonLine.update(deltaTime, currentSpeed);
        this.updateClouds(deltaTime, currentSpeed);
        if (updateObstacles) {
            this.updateObstacles(deltaTime, currentSpeed);
        }
    }

    public void draw(Canvas canvas) {
        for (Cloud c : clouds) {
            c.draw(canvas);
        }

        horizonLine.draw(canvas);
        for (Obstacle o : obstacles) {
            o.draw(canvas);
        }
    }

    private void updateObstacles(long deltaTime, double currentSpeed) {
        // Obstacles, move to Horizon layer.
        int delObs = -1;

        for (int i = 0; i < this.obstacles.size(); i++) {
            Obstacle obstacle = this.obstacles.get(i);
            obstacle.update(deltaTime, currentSpeed);
            // Clean up existing obstacles.
            if (obstacle.remove) {
                delObs = i;
            }
        }
        if (delObs >= 0 && delObs < obstacles.size())
            obstacles.remove(delObs);
        if (!this.obstacles.isEmpty()) {
            Obstacle lastObstacle = this.obstacles.get(this.obstacles.size() - 1);
            if (lastObstacle != null && !lastObstacle.followingObstacleCreated &&
                    lastObstacle.isVisible() &&
                    (lastObstacle.xPos + lastObstacle.width + lastObstacle.gap) <
                            this.dimensions.x) {
                this.addNewObstacle(currentSpeed);
                lastObstacle.followingObstacleCreated = true;
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
        if (this.duplicateObstacleCheck(obstacleType.type) ||
                currentSpeed < obstacleType.minSpeed) {
            this.addNewObstacle(currentSpeed);
        } else {
            this.obstacles.add(new Obstacle(obstacleType, this.dimensions, this.gapCoefficient, currentSpeed));
            this.obstacleHistory.add(obstacleType.type);
            if (this.obstacleHistory.size() > 1) {
                obstacleHistory.remove(0);
            }
        }
    }

    public boolean duplicateObstacleCheck(int nextObstacleType) {
        int duplicateCount = 0;
        for (int i = 0; i < this.obstacleHistory.size(); i++) {
            duplicateCount = this.obstacleHistory.get(i) == nextObstacleType ?
                    duplicateCount + 1 : 0;
        }
        return duplicateCount >= Runner.config.MAX_OBSTACLE_DUPLICATION;
    }

    private void updateClouds(long deltaTime, double speed) {
        double cloudSpeed = this.cloudSpeed / 1000 * deltaTime * speed;
        int numClouds = clouds.size();
        if (numClouds > 0) {
            for (Cloud c : clouds) {
                c.update(deltaTime, cloudSpeed);
            }
            Cloud lastCloud = clouds.get(numClouds - 1);
            if (numClouds < config.MAX_CLOUDS &&
                    (this.dimensions.x - lastCloud.xPos) > lastCloud.cloudGap &&
                    this.cloudFrequency > Math.random()) {
                this.addCloud();
            }
            List<Cloud> newClouds = new ArrayList(clouds);
            for (int i = 0; i < newClouds.size(); i++) {
                if (newClouds.get(i).remove) {
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
    }
}
