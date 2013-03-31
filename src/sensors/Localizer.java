package sensors;

import communication.Decoder;
import communication.StartCorner;

import robot.Odometer;
import robot.TwoWheeledRobot;
import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
import main.Constants;

public class Localizer {
	private LightSampler leftLight, rightLight;
	private UltrasonicSensor usLeft;
	private TwoWheeledRobot robot;
	private Odometer odometer;
	private StartCorner startCorner;
	private int fieldSize;
	
	public Localizer(TwoWheeledRobot robot, UltrasonicSensor usLeft, LightSampler leftLight, LightSampler rightLight){
		this.robot = robot;
		this.odometer = robot.getOdometer();
		this.usLeft = usLeft;
		this.leftLight = leftLight;
		this.rightLight = rightLight;
		startCorner = Decoder.startCorner;
		fieldSize = Constants.PLAYING_FEILD.length * Constants.TILE_DISTANCE;
		
	}
	
	public void dolocalize(){
		//new LCDInfo(odo);
				boolean loacalized = false;
				//if facing towards the wall initially rotate until facing no wall
				if(usLeft.getDistance() < Constants.WALL_DIST){
					boolean wrongDirection = true;
					robot.setRotationSpeed(Constants.ROTATE_SPEED);
					while(wrongDirection){
						if(usLeft.getDistance() > Constants.WALL_DIST){
							robot.stopMotors();
							wrongDirection = false;
						}
					}
				}
				//if facing away from the wall
				if(usLeft.getDistance()  > Constants.WALL_DIST){
					while(!loacalized){
						robot.setRotationSpeed(Constants.ROTATE_SPEED);
						//rotate clockwise till you see a wall
						if(usLeft.getDistance()  < 60){
							//patBot.turnToImmediate(-55);
							robot.stopMotors();
							robot.moveForward(); //move forawrd until you hit a line
							boolean light = false;
							while(!light){
								if(rightLight.isDarkLine()){
									Motor.B.stop(true);
									Motor.A.stop();
									Motor.A.setSpeed(-Constants.ROTATE_SPEED);
									Motor.A.backward();
									boolean light2 = false;
									while(!light2){
										if(leftLight.isDarkLine()){
											Motor.A.stop();
											odometer.setTheta(0);
											double odoX = odometer.getX();
											double odoY = odometer.getY();
											int xValue = (int)(odoX/30) *30;
											int yValue = (int)(odoY/30) *30;
											switch(startCorner){	
											case BOTTOM_LEFT:			
												odometer.setX(xValue);
												break;
											case BOTTOM_RIGHT:
												odometer.setY(yValue);
												break;
											case NULL: //for the null case assume bottom left
												odometer.setX(xValue);
												break;
											case TOP_LEFT:
												odometer.setY(fieldSize - (yValue + 2*Constants.TILE_DISTANCE));
												break;
											case TOP_RIGHT:
												odometer.setX(fieldSize - (xValue + 2*Constants.TILE_DISTANCE));
												break;
											}
											
											light2 = true;
											light = true;
											break;
										}
									}
								}
								
							}
							robot.turnTo(-90);
							robot.moveForward();
							boolean there = false;
							while(!there){
								if(leftLight.isDarkLine()){
									robot.stopMotors();
									boolean last = false;
									Motor.B.setSpeed(-Constants.ROTATE_SPEED);
									Motor.B.backward();
									while(!last){
										if(rightLight.isDarkLine()){
											Motor.B.stop();
											last = true;
											break;
										}
									}
									double odoY = odometer.getY();
									double odoX = odometer.getX();
									double yValue = (int)(odoY/30) *Constants.TILE_DISTANCE;
									double xValue = (int)(odoX/30) *Constants.TILE_DISTANCE;
									switch(startCorner){
									case BOTTOM_LEFT:
										odometer.setY(yValue);
										odometer.setTheta(0);
										robot.turnTo(0);
										robot.travelTo(60, 0);
										robot.travelTo(60, 30);
										break;
									case BOTTOM_RIGHT:
										odometer.setX(fieldSize - (xValue + 2*Constants.TILE_DISTANCE));
										odometer.setTheta(270);
										robot.travelTo(270, 0);
										robot.travelTo(270, 30);
										break;
									case NULL: //for null case assume bottom right
										odometer.setY(yValue);
										odometer.setTheta(0);
										robot.turnTo(0);
										robot.travelTo(60, 0);
										robot.travelTo(60, 30);
										break;
									case TOP_LEFT:
										odometer.setX(xValue);
										odometer.setTheta(90);
										robot.travelTo(60, 330);
										robot.travelTo(60, 300);
										break;
									case TOP_RIGHT:
										odometer.setY(fieldSize - (yValue + 2*Constants.TILE_DISTANCE));
										odometer.setTheta(180);
										robot.travelTo(270, 330);
										robot.travelTo(270, 300);
										break;
									default:
										break;
									
									}
									
									//odo.setY(0);
									there = true;
									
									//dometer.setX(0);
									loacalized = true;
									break;
								}
							}
							
							
						}
						
					}
				}
	}
}
