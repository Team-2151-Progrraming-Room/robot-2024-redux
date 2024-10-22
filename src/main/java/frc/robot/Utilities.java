
package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what
 * you are doing, do not modify this file except to change the parameter class to the startRobot
 * call.
 */
public class Utilities {
    
    // using the passed value, lookup and return the secondary value associated with it
    //
    // if the passed value is less than the first value in the array, return the value associated with the first value
    //
    // if the passed value is greater than the last value, return the valeu associated with the last value
    //
    // otherwise return the value associasted with the passed value if equal to or greater than  but not equal to the next value
    //
    // values must be in increasing order in regards to the first value
    //
    // we use this to lookup speeds and angles based on distances
    //
    // this routine could get pretty smart - it could find a range in the table closest to the current range and use that or
    // we could get even smarter and actually interpolate a speed or angle based on the spacing of the various values and
    // calculate speeds or angles for the "in betweens"

    public static double lookupByValue(double inValue, double[][] array) {
      // lookup values from an order list

      int valueIndex = 0;
      boolean found = false;

      if (inValue <= array[0][0]) {                 // less than the first?
        return array[0][1];
      }

      if (inValue >= array[array.length - 1][0]) {  // greater than the last?
        return array[array.length - 1][1];
      }

      while (valueIndex < (array.length - 1) && ! found) {  // find it then
        if (inValue >= array[valueIndex][0] && inValue < array[valueIndex + 1][0]) {
          return array[valueIndex][1];
        }

        valueIndex++;
      }

      // we should never get here but if we do, return 0
      return 0.0;
    }
}
