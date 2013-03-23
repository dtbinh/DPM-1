/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
*/
package main;

import java.io.DataInputStream;
import java.io.IOException;

import communication.PlayerRole;
import communication.StartCorner;
import communication.Transmission;

import lejos.nxt.LCD;

/**
 * Static parsers for parsing data off the communication channel
 * 
 * The order of data is defined in the Server's Transmission class
 */

public class ParseTransmission {


	public static Transmission parse (DataInputStream dis) {
		Transmission trans = null;
		try {
			
			while (dis.available() <= 0)
				Thread.sleep(10); // spin waiting for data
			
			trans = new Transmission();
			trans.role = PlayerRole.lookupRole(dis.readInt());
			ignore(dis);
			trans.startingCorner = StartCorner.lookupCorner(dis.readInt());
			ignore(dis);
			trans.bx = Constants.TILE_DISTANCE*dis.readInt();
			ignore(dis);
			trans.by = Constants.TILE_DISTANCE*dis.readInt();
			ignore(dis);
			trans.w1 = Constants.TILE_DISTANCE*dis.readInt();
			ignore(dis);
			trans.w2 = Constants.TILE_DISTANCE*dis.readInt();
			ignore(dis);
			trans.d1 = Constants.TILE_DISTANCE*dis.readInt();
			
			return trans;
		} catch (IOException e) {
			// failed to read transmitted data
			LCD.drawString("IO Ex", 0, 7);
			return trans;
		} catch (InterruptedException e) {
			return trans;
		}
		
	}
	
	public static void ignore(DataInputStream dis) throws IOException {
		dis.readChar();
	}
	
}