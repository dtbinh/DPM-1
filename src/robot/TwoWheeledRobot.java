package robot;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import main.Constants;

/**
 * Controls the wheel motors of the robot.
 * 
 * Implements core navigation functionality
 * @author charles
 *
 */
public class TwoWheeledRobot {
	
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	private int forwardSpeed = Constants.FORWARD_SPEED;
	private double rotationSpeed = Constants.ROTATE_SPEED;
	private double currentX = 0;
	private double currentY = 0;
	private double xTarget = 0;
	private double yTarget = 0;
	private double theta= 0;
	private boolean isRotating;
	private double currentTachoCount;
	private double previousTachoCount;
	private Odometer odometer;
	
	/**
	 * Sets all controllable attributes of the class
	 * @param leftMotor Left Motor
	 * @param rightMotor Right Motor
	 */
	public TwoWheeledRobot(Odometer odo, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.odometer = odo;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = Constants.WHEEL_RADIUS;
		this.rightRadius = Constants.WHEEL_RADIUS;
		this.width = Constants.WIDTH;
		isRotating = false;
		
		
	}
	
	/**
	 * Converts a distance into the number degreess the wheel needds to turn to order to
	 * move the given distance
	 * @param radius The wheel radius
	 * @param distance The distance need to be moved
	 * @return The number of degrees of rotation of the wheels needed to achieve the required distance
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Converts an angle in degrees the wheel needs to rotate in order face a
	 * given heading 
	 * @param radius The wheel radius of the robot
	 * @param width The wheel center of the robot
	 * @param angle The angle which the robot to should turn to face
	 * @return The number of degrees the wheel needs to rotate
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	
	}
	
	/**
	 * Turns robot by the given number of degrees anti-clockwise
	 * @param theta The angle in degrees which the robot should rotate by
	 */
	public void turnToImmediate(double theta){
		isRotating = true;
		leftMotor.setSpeed(Constants.ROTATE_SPEED);
		rightMotor.setSpeed(Constants.ROTATE_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.rotate(convertAngle(leftRadius,width,theta),true);
		rightMotor.rotate(-convertAngle(rightRadius,width,theta),false);
	}
	
	/**
	 * Turns robot by the given number of degrees anti-clockwise
	 * @param theta The angle in degrees which the robot should rotate by
	 * @param immediateReturn True if the method should return immediately
	 */
	public void turnToImmediate(double theta, boolean immediateReturn){
		isRotating = true;
		leftMotor.setSpeed(Constants.ROTATE_SPEED);
		rightMotor.setSpeed(Constants.ROTATE_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.rotate(convertAngle(leftRadius,width,theta),true);
		rightMotor.rotate(-convertAngle(rightRadius,width,theta),immediateReturn);
	}
	
	/**
	 * Travels to a specified position, accurate to a pre-calculated distance. This operation cannot be interrupted.
	 * @param xTarget The x value of the target position
	 * @param yTarget The y value of the target position
	 */
	public void travelTo(int xTarget, int yTarget){
		turnToFace(xTarget, yTarget);
		double distance = calculatedCorrectedDistance(xTarget, yTarget);
		divider(distance, xTarget, yTarget);
			
	}

	/**
	 * Corrects the distance to move the robot forward by to get form one square to another
	 * as the distance between tiles is not 30cm exact.
	 * @param xTarget The x ordinate of the targets location
	 * @param yTarget The y ordinate of the targets location
	 * @return The corrected distance that the robot must travel in oder to reach its intended destination
	 */
	public double calculatedCorrectedDistance(int xTarget, int yTarget) {
		double distance =  calculateDistance(xTarget, yTarget);
		return (distance/Constants.TILE_DISTANCE_TRUNCATED)*Constants.TILE_DISTANCE;
	}

	/**
	 * Corrects the robots heading if it is off the intended target
	 * @param xTarget The x ordinate of the target destination
	 * @param yTarget The y ordinate of the target destination
	 * @param distance The distance the robot needs to move in order to get to its target
	 */
	public void divider(double distance, int xTarget, int yTarget) {
		if(distance <= 60){
			moveForwardBy(distance);
		}
		else if(distance > 60 && distance < 90){
			double multiplier = distance/Constants.TILE_DISTANCE;
			for(int i = 1; i < multiplier-1; i++){
				Sound.beepSequenceUp();
				turnToFace(xTarget, yTarget);
				moveForwardBy(Constants.TILE_DISTANCE);
			}
			turnToFace(xTarget,yTarget);
			moveForwardBy(calculatedCorrectedDistance(xTarget, yTarget));
		}
		
		//moveForwardBy(distance);
		//turnToFace(xTarget,yTarget);
		//moveForwardBy(calculateDistance(xTarget,yTarget));
	}
	
	/**
	 * Travels to a specified position, accurate to the {@link Odometer}. This operation cannot be interrupted.
	 * @param xTarget The x value of the target position
	 * @param yTarget The y value of the target position
	 */
	public void travleToActual(int xTarget, int yTarget){
		turnToFace(xTarget, yTarget);
		moveForwardDefault();
		while((Math.abs(xTarget-odometer.getX())  > 1) && (Math.abs(yTarget - odometer.getY()) > 1)){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		stopMotors();
	}
	
	/**
	 * Calculates the distance between the robot and its target position
	 * @param x The x value of the target position
	 * @param y the y value of the target position
	 * @return The distance between the robot and its target position
	 */
	public double calculateDistance(double x, double y){
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
		double x2 = Math.pow(deltaX, 2);
		double y2 = Math.pow(deltaY, 2);
		
		return Math.sqrt(y2 +x2);
	}
	
	
	/**
	 * Rotates the robot to face in the direction of a given target position
	 * @param xTarget The x value of the target position 
	 * @param yTarget the y value of the target position
	 */
	public void turnToFace(int xTarget, int yTarget){
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		double deltaX = xTarget -currentX;
		double deltaY = yTarget - currentY;
		
		double angle = 90- Math.toDegrees(Math.atan2(deltaY, deltaX));
		double robotAngle = odometer.getTheta();
		double theta = 0;
		double deltaTheta = angle - robotAngle;
		if(Math.abs(deltaTheta) <= 180){
			theta = deltaTheta;
		}
		else if(deltaTheta < -180){
			theta = deltaTheta + 360;
		}
		else if(deltaTheta > 180){
			theta = deltaTheta -360;
		}
		
		turnToImmediate(theta);
	}
	
	/**
	 * Turns the robot to face a given heading
	 * @param angle The heading the robot should to rotate towards as measured from the positive y axis
	 * in a clockwise direction
	 */
	public void turnTo(double angle){
			
			double robotAngle = odometer.getTheta();
			double theta = 0;
			double deltaTheta = angle - robotAngle;
			if(Math.abs(deltaTheta) <= 180){
				theta = deltaTheta;
			}
			else if(deltaTheta < -180){
				theta = deltaTheta + 360;
			}
			else if(deltaTheta > 180){
				theta = deltaTheta -360;
			}
			
			turnToImmediate(theta);
		}
	
	
	
	
	/**
	 * Moves the robot forward  in a straight line by a given distance. This method cannot be interrupted.
	 * @param distance The distance to moved in centimetres
	 */
	public void moveForwardBy(double distance){
		isRotating = false;
		rightMotor.setSpeed(Constants.FORWARD_SPEED);
		leftMotor.setSpeed(Constants.FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.rotate(convertDistance(leftRadius,distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
			
	}

	/**
	 * Moves the robot forward  in a straight line by a given distance. This method can be interrupted.
	 * @param distance The distance to moved in centimetres
	 * @param immediateReturn whether the method should return immediately
	 */
	public void moveForwardBy(double distance, boolean immediateReturn){
		isRotating = false;
		rightMotor.setSpeed(Constants.FORWARD_SPEED);
		leftMotor.setSpeed(Constants.FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.rotate(convertDistance(leftRadius,distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), immediateReturn);
	}
	
	
	/**
	 * Sets the robots forward speed to the given value
	 * @param speed The speed at which the robot should move forward at
	 */
	 public void setForwardSpeed(int speed) {
		 forwardSpeed = speed;
		 isRotating = false;
		 leftMotor.setSpeed(speed);
		 rightMotor.setSpeed(speed);
		 leftMotor.forward();
		 rightMotor.forward();
	}
	 
	 /**
	  * Moves the motors forward at the current speed setting
	  */
	 public void moveForward(){
		 leftMotor.setSpeed(forwardSpeed);
		 rightMotor.setSpeed(forwardSpeed);
		 leftMotor.forward();
		 rightMotor.forward();	 
	 }
	 
	 /**
	  * Moves the robot backwards
	  */
	 public void moveBackward(){
		 leftMotor.setSpeed(forwardSpeed);
		 rightMotor.setSpeed(forwardSpeed);
		 leftMotor.backward();
		 rightMotor.backward();
	 }
	 
	 /**
	  * Stops the robots right motor
	  */
	 public void stopRightMotor(){
		 leftMotor.stop();
	 }
	 
	 /**
	  * Stops the robot right motor
	  * @param immediateReturn The return type. True if method should immediately return
	  */
	 public void stopRightMotor(boolean immediateReturn){
		 leftMotor.stop(immediateReturn);
	 }
	 
	 /**
	  * Stops the robots left motor
	  */
	 public void stopLeftMotor(){
		 rightMotor.stop();
	 }
	 
	 /**
	  * Stops the robot left motor
	  * @param immediateReturn The return type. True if method should immediately return
	  */
	 public void stopLeftMotor(boolean immediateReturn){
		 leftMotor.stop(immediateReturn);
	 }
	 
	 /**
	  * Moves the motors forward at the default current speed setting
	  */
	 public void moveForwardDefault(){
		 leftMotor.setSpeed(Constants.FORWARD_SPEED);
		 rightMotor.setSpeed(Constants.FORWARD_SPEED);
		 leftMotor.forward();
		 rightMotor.forward();
		 
	 }

	 /**
	  * Sets the speed at which the robot should rotate at.
	  * @param speed The speed at which the robot should rotate at
	  */
	 public void setRotationSpeed(double speed){
		 isRotating = true;
		 rightMotor.setSpeed((int)speed);
		 leftMotor.setSpeed((int)speed);
		 if (speed>0){
			 leftMotor.forward();
			 rightMotor.backward();
		 }
		 else{
			 leftMotor.backward();
			 rightMotor.forward();
		 }
	 }
	 
	 /**
	  * Floats the robots motors
	  */
	 public void motorFloat(){
		 leftMotor.flt(true);
		 rightMotor.flt();
	 }
	 
	 /**
	  * Stops the robots wheels
	  */
	 public void stopMotors(){
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		leftMotor.stop(true);
		rightMotor.stop(false);
		 
	 }
	 
	 /**
	  * Checks if the robot is rotating
	  * @return True if the robot is rotating
	  */
	 public boolean isRotating(){
		 return this.isRotating;
	 }
	 
	 /**
	  * Checks if the robot is moving
	  * @return True of the robot is moving
	  */
	 public boolean isMoving(){
		 return (leftMotor.isMoving() && !leftMotor.isStalled() && rightMotor.isMoving() && !rightMotor.isStalled());
	 }
	 
	 /**
	  * Gets the {@link Odometer} used by the robot 
	  * @return An {@link Odometer} 
	  */
	 public Odometer getOdometer(){
		 return this.odometer;
	 }
	 
	 /**
	  * Returns the robots current x position
	  * @return The robots x position
	  */
	 public double getX(){
		 return this.odometer.getX();
	 }
	 
	 /**
	  * Returns the robots current y position
	  * @return The robots y position
	  */
	 public double getY(){
		 return this.odometer.getY();
	 }
	 
	 /**
	  * Returns the robots current heading
	  * @return The heading of the robot
	  */
	 public double getTheta(){
		 return this.odometer.getTheta();
	 }
	 
	 /**
	  * Checks if the robot is navigating
	  * @return True if the robot is still navigating to a given point
	  */
	 public boolean isNavigating(){
			double deltaX = xTarget - odometer.getX();
			double deltaY = yTarget - odometer.getY();
			if(Math.abs(deltaX) < 5 && Math.abs(deltaY) < 5){
				return false;
			}
			
			return true;
	}
	 
	 /**
	  * Gets the left motor of the robot
	  * @return The left motor of the robot
	  */
	public NXTRegulatedMotor getLeftMotor(){
		return this.leftMotor;
	}
	
	/**
	  * Gets the right motor of the robot
	  * @return The right motor of the robot
	  */
	public NXTRegulatedMotor getRightMotor(){
		return this.rightMotor;
	}
}

	 
	 	
	 
	 
	
	









	
	

