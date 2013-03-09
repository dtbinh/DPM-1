package odometer;

import java.util.Stack;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Timer;
import lejos.util.TimerListener;
import main.Constants;
import sensors.LightSampler;

public class OdometryCorrection implements TimerListener{
	
	private Odometer odometer;
	private TwoWheeledRobot robot;
	private LightSampler leftLs, rightLs;
	private NXTRegulatedMotor leftMotor;
	private Stack<Double> lineX, lineY, tachoCountX, tachoCountY;
	private Stack<SensorSide> sensorSideX, sensorSideY;
	public enum SensorSide{LEFT, RIGHT};
	public enum LineType{X, Y};
	private double x, y, theta, length;
	private boolean filter;
	
	private long startTimeX, startTimeY, endTimeX, endTimeY;
	private long endTimeLight;
	
	public static long xCor = 0;
	public static long yCor = 0;
	public static int tCor = 0;
	public static int lsValue = Constants.DARK_LINE_VALUE;
	public static int rsValue = Constants.DARK_LINE_VALUE;
	
	
	private Timer correctionTimer;
	
	public OdometryCorrection(Odometer odometer, LightSensor ls1, LightSensor ls2){
		this.odometer = odometer;
		this.robot = odometer.getTwoWheeledRobot();
		this.leftLs = new LightSampler(ls1);
		this.rightLs = new LightSampler(ls2);
		this.leftMotor = Motor.A;
		lineX = new Stack<Double>();
		tachoCountX = new Stack<Double>();
		lineY = new Stack<Double>();
		tachoCountY = new Stack<Double>();
		sensorSideX = new Stack<SensorSide>();
		sensorSideY = new Stack<SensorSide>();
		filter = true;
		correctionTimer = new Timer(Constants.ODOMETER_CORRECTION_TIMEOUT, this);
		//resetInternalTimer();
	}

	@Override
	public void timedOut() {
		/*
		 * If a line is detected, determine which line was detected.
		 * Store the line value, the tachometer reading and which sensor it was
		 * that detected the line.
		 * Start the respective line refresh counter
		 * If another line is detected determine if 
		 */
		if(System.currentTimeMillis() > endTimeX){ //if timer runs out flush memory for x values

			resetInternalXTimer();
			
		}
		if(System.currentTimeMillis() > endTimeY){ //if timer runs out flush memory for y values
			resetInternalYTimer();
			
		}
		if(!robot.isRotating()){
			//if left sensor detects a line
			if(leftLs.isDarkLine() && (System.currentTimeMillis() > endTimeLight)){
				Sound.beep();
				LineType lineType = determineLineType();
				//save the time
				//store the tacoCount
				double taco1 = leftMotor.getTachoCount();
				switch(lineType){
				case X:
					if(sensorSideX.empty()){ //if its the first time detecting the line add it to the stack
						xCor++;
						sensorSideX.addElement(SensorSide.LEFT); //add the sensor which detected the line
						tachoCountX.push(new Double(taco1)); //save the tachometer count
						lineX.push(x); //save the value of the line
						resetInternalXTimer(); // start the x timer
					}
					else if(!isSameSensor(sensorSideX, SensorSide.LEFT) && isSameLine(lineType)){ //if its the second time and not the same sensor
						tachoCountX.push(new Double(taco1)); //store the tachometer value
						double diff = Math.abs(tachoCountX.pop() - tachoCountX.pop()); //get the difference in tachometer readings
						calculateAndSetTheta(diff); //calculate and set theta
						resetInternalXTimer(); //reset the x timer
						
					}
					else{
						//do nothing because it was the same sensor
					}
					setX();
					break;
				case Y:
					if(sensorSideY.empty()){ //if its the first time detecting the line add it to the stack
						sensorSideY.addElement(SensorSide.LEFT); //add the sensor which detected the line
						tachoCountY.push(new Double(taco1)); //save the tachometer count
						lineX.push(y); //save the value of the line
						resetInternalYTimer(); // start the y timer
					}
					else if(!isSameSensor(sensorSideY, SensorSide.LEFT) && isSameLine(lineType)){ //if its the second time and not the same sensor
						tachoCountY.push(new Double(taco1)); //store the tachometer value
						double diff = Math.abs(tachoCountY.pop() - tachoCountY.pop()); //get the difference in tachometer readings
						calculateAndSetTheta(diff); //calculate and set theta
						resetInternalYTimer(); //reset the y timer
						
					}
					else{
						//do nothing because it was the same sensor
					}
					setY();
					break;
				}
				
				
							
			}
			
			
			
			//if right sensor detects a line
			//if(isDarkLine(rsValue, this.rightLightValues)){
			//if(rsValue < Constants.DARK_LINE_VALUE){
			if(rightLs.isDarkLine() && (System.currentTimeMillis() > endTimeLight)){
				Sound.beep();
				LineType lineType = determineLineType();
				//save the time
				//store the tacoCount
				double taco1 = leftMotor.getTachoCount();
				switch(lineType){
				case X:
					if(sensorSideX.empty()){ //if its the first time detecting the line add it to the stack
						sensorSideX.addElement(SensorSide.RIGHT); //add the sensor which detected the line
						tachoCountX.push(new Double(taco1)); //save the tachometer count
						lineX.push(x); //save the value of the line
						resetInternalXTimer(); // start the x timer
					}
					else if(!isSameSensor(sensorSideX, SensorSide.RIGHT)){ //&& isSameLine(lineType)){ //if its the second time and not the same sensor
						tachoCountX.push(new Double(taco1)); //store the tachometer value
						double diff = Math.abs(tachoCountX.pop() - tachoCountX.pop()); //get the difference in tachometer readings
						calculateAndSetTheta(diff); //calculate and set theta
						resetInternalXTimer(); //reset the x timer
						
					}
					else{
						//do nothing because it was the same sensor
					}
					setX();
					break;
				case Y:
					setY();
					if(sensorSideY.empty()){ //if its the first time detecting the line add it to the stack
						sensorSideY.addElement(SensorSide.RIGHT); //add the sensor which detected the line
						tachoCountY.push(new Double(taco1)); //save the tachometer count
						lineX.push(y); //save the value of the line
						resetInternalYTimer(); // start the y timer
					}
					//else if(!isSameSensor(sensorSideY, SensorSide.RIGHT)){//&& isSameLine(lineType)){ //if its the second time and not the same sensor
					else{
						tachoCountY.push(new Double(taco1)); //store the tachometer value
						double diff = Math.abs(tachoCountY.pop() - tachoCountY.pop()); //get the difference in tachometer readings
						calculateAndSetTheta(diff); //calculate and set theta
						resetInternalYTimer(); //reset the y timer
						
					//}
					//else{
						//do nothing because it was the same sensor
					}
					
					break;
				}
				
				
			}
		}
		
		lsValue = leftLs.getLightValue();
		rsValue = rightLs.getLightValue();
		
	}//timeout

	/**
	 * Resets the internal line refresh timer for x
	 */
	public void resetInternalXTimer() {
		lineX.clear();
		tachoCountX.clear();
		sensorSideX.clear();
		startTimeX = System.currentTimeMillis();
		endTimeX = startTimeX + Constants.TIME_REFRESH_CONSTANT;
	}
	
	/**
	 * Resets the internal line refresh timer for y
	 */
	public void resetInternalYTimer() {
		lineY.clear();
		tachoCountY.clear();
		sensorSideY.clear();
		startTimeY = System.currentTimeMillis();
		endTimeY = startTimeY + Constants.TIME_REFRESH_CONSTANT;
	}

	/**
	 * Sets the odometer x and y values based on the line detected
	 * and sets the value of theta
	 */
	/*Needs to be corrected to account for approach at angle*/
	private void setOdometerValues() {
		//get odometer values of x and y
		
		// check if the line is x or y
		if(Math.abs(x % 30) < 1){
			//correct the x value
			x = ((int)(x/30))* 30;
			xCor++;
			odometer.setX(x);
			//lineCheck(x);
		}
		//if the line is y
		if(Math.abs(y % 30) < 1){
			//correct the y value
			y = ((int)(y/30))* 30;
			yCor++;
			odometer.setY(y);
			//lineCheck(y);
		}
	}

	public boolean isSameSensor(Stack<SensorSide> storedSensorSide, SensorSide sensorSide){
		return (storedSensorSide.peek().equals(sensorSide));
	}
	
	
	public LineType determineLineType(){
		x = odometer.getX();
		y = odometer.getY();
		if(Math.abs(x % 300) == 0){
			return LineType.X;
		}
		return LineType.Y;
	}
	/**
	 * Checks the line to determine if the same line has been crossed or not
	 * @param value
	 */
	private void lineCheck() {
		
		
		
	}

	/**
	 * Calculates theta based on the difference in the tachometer readings
	 * @param diff The difference in the tachometer readings
	 */
	public void calculateAndSetTheta(double diff) {
		double side = convertTacoToLength(diff);
		//calculate theta
		theta = Math.toDegrees(Math.atan(Constants.WIDTH/ side));
		//set theta
		odometer.setTheta(theta);
		tCor++;
	}

	/**
	 * Converts the tachometer reading into a distance
	 * @param diff The difference in the tachometer count
	 * @return The distance moved
	 */
	public double convertTacoToLength(double diff) {
		length =  (diff/360)*2*Math.PI*Constants.WHEEL_RADIUS;
		return length;
	}

	/**
	 * Starts the odometry correction timer and the internal light samplers
	 */
	public void startCorrectionTimer(){
		endTimeLight = System.currentTimeMillis() + Constants.LIGHT_CALIBRATION_TIME; //set the amount of calibration time
		this.leftLs.startCorrectionTimer(); //start the left light sampler
		this.rightLs.startCorrectionTimer(); //start the right light sampler
		correctionTimer.start();
	}
	
	/**
	 * Stops the odometry correction timer and the internal light samplers
	 */
	public void stopCorrectionTimer(){
		this.leftLs.stopCorrectionTimer(); //stop the left light sampler
		this.rightLs.stopCorrectionTimer(); //stop the right light sampler
		correctionTimer.stop();		
	}
	
	public boolean isSameLine(LineType lineType){
		switch(lineType){
			case X:
				return Math.abs(lineX.peek() - x) < Constants.MAX_LINE_CORRECTION_BANDWIDTH;
			case Y:
				return Math.abs(lineY.peek() - y) < Constants.MAX_LINE_CORRECTION_BANDWIDTH;
		}
		return false;
	}
	
	/**
	 * Sets the x value of the odometer
	 */
	public void setX(){
		if(Math.abs(x % 30) < 1){
			//correct the x value
			x = ((int)(x/30))* 30;
			xCor++;
			double value = length*Math.sin(Math.toRadians(theta));
			value = value/2;
			odometer.setX( x);
			//lineCheck(x);
		}
	}
	
	/**
	 * Sets the y value of the odometer
	 */
	public void setY(){
		yCor++;
		if(Math.abs(y % 30) < 1){
			//correct the y value
			y = ((int)(y/30))* 30;
			yCor++;
			double value = length*Math.sin(Math.toRadians(theta));
			value = value/2;
			odometer.setY(y);
			//lineCheck(x);
		}
	}
	
}
