package main;

/**
 * This class contains the constants used in the code.
 * @author charles
 *
 */
public final class Constants {

	/**
	 * Number of intersection points to the right of the bottom left corner
	 */
	public static final int GOALX = 5;
	
	/**
	 * Number of intersection points in front of the bottom left corner
	 */
	public static final int GOALY = 10;
	/** 
	 * Distance between the tiles
	 */
	public static final int TILE_DISTANCE = 30;
	/**
	 * The radius of the robots wheels
	 */
	public static final double WHEEL_RADIUS = 2.775;
	/**
	 * The wheel to wheel radius of the robot
	 */
	public static final double WIDTH = 17; //wheel center
	/**
	 * The default {@link Odometer} timeout period
	 */
	public static final int ODOMETER_DEFAULT_PERIOD = 25;
	/**
	 * The rotation speed of the robot {@link USLocalizer}
	 */
	public static final int US_LOCALIZATION_ROTATE_SPEED = 100;
	/**
	 * The default rotation speed of the robot
	 */
	public static final int ROTATE_SPEED = 150;
	/**
	 * The default forawrd speed of the robot
	 */
	public static final int FORWARD_SPEED = 150;
	/**
	 * The default light value for detecting a dark line
	 */
	public static final int DARK_LINE_VALUE = 44;
	/**
	 * Minimum percentage difference needed between the light average and the current light value
	 * in order for the system to detect a black line in the {@link LightSampler}
	 */
	public static final double LIGHT_VALUE_PERCENTAGE = 0.10;
	/**
	 * The distance between the sensor and the center of the robot needed for light localization
	 */
	public static final double LIGHT_DIST = 12; //distance between light sensor and center of robot
	/**
	 * The number of light samples to be averaged when by the {@link LightSampler}
	 */
	public static final int LIGHT_SAMPLE_SIZE = 20;
	/**
	 * The number of milliseconds after which the the {@link LightSampler} {@code timedOut} function called again 
	 */
	public static final int LIGHT_SAMPLER_REFRESH = 20;
	public static final long LIGHT_CALIBRATION_TIME = 10;
	/**
	 * The number of consecutive dark lines detected before the {@link LightSampler} determines that it is traveling on a path that it thinks is a dark. Once this number is reaached the {@code isDarkLine}
	 * method will return false.
	 */
	public static final int CONSECUTIVE_DARK_LINES = 5; //number of consecutive dark lines allowed
	public static final int WALL_DIST = 60; //for ultrasonic sensor
	public static final int US_ANGLE_OFFSET = -25;
	public static final int ODOMETER_CORRECTION_TIMEOUT = 20; //number of miliseconds after which correction is called again
	public static final double ODOMETRY_CORRECTION_MAX_ERROR_ALLOWANCE = WIDTH/2;
	public static final double MAX_LINE_CORRECTION_BANDWIDTH = 1; //difference between the two 'same' values by each light sensor
	public static final long TIME_REFRESH_CONSTANT = 1500; //amount of time passed before line latched is removed from stack
	public static final String SLAVE_NAME = "Group34";
	public static final int WALL_FOLLWER_REFRESH = 50; //the amount of times passed before the us sensor is re-pinged
	public static final int WALL_FOLLOWER_BANDWIDTH = 3;
	public static final int WALL_FOLLOWER_BANDCENTER = 28; 
	public static final int WALL_FOLLOWER_DELTA = 25; //incremental increase/decrease to wheels speed.
	public static final int WALL_FOLLOWER_FILTER_OUT = 20; //number of readings before considered no wall present
	public static final int WALL_FOLLOWER_MAX_SPEED = 200; //max speed to move wheel at
	public static final int WALL_FOLLOWER_MIN_SPEED = 60;  //min speed to move wheel at
	public static final int ObstacleDist = 25; 
	public static final int motorLow = 170;
	public static final int clearPathDist=80;

}
