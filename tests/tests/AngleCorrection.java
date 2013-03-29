package tests;

import navigation.Navigation;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import robot.Odometer;
import robot.OdometryCorrection;
import robot.TwoWheeledRobot;
import sensors.LightSampler;
import utilities.OdoLCD;

public class AngleCorrection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Odometer odo = new Odometer (true);
		TwoWheeledRobot patBot = new TwoWheeledRobot(odo, Motor.A, Motor.B);
		LightSensor lsLeft = new LightSensor(SensorPort.S1);
		LightSensor lsRight = new LightSensor(SensorPort.S2);
		LightSampler leftLight = new LightSampler(lsLeft);
		LightSampler rightLight = new LightSampler(lsRight);
		new OdoLCD(odo);
		OdometryCorrection odoCorrect = new OdometryCorrection(patBot, leftLight, rightLight);
		Button.waitForAnyPress();
		odoCorrect.startCorrectionTimer();
		patBot.moveForwardBy(30);
		//nav.turnTo(180);
		//.moveForwardBy(60);
		//nav.turnTo(0);
		patBot.turnToImmediate(90);
		patBot.moveForwardBy(30);
		patBot.turnTo(0);
		Button.waitForAnyPress();
		System.exit(0);
		

	}

}
