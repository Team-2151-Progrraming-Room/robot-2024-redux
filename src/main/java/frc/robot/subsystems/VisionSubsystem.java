// Vision "subsystem"
//
// "Subsystem" is in quotes because this isn't really a traditional "subsystem" as it doesn't control any robot
// hardware and is basically useable by any code that wants to use it at any time without contention
//
// It basically provides access to the information collected by the actual vision components (typically via
// NetworkTables) and presents it in a manner that is more generally useable
//
// This might evolve quite a bit as things move ahead depending on how we integrate vision into the robot.





package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.*;
import frc.robot.Constants.ShooterConstants;



public class VisionSubsystem extends SubsystemBase {

    private final static double kRangeTolerance = 1;                                         // we can generate ranges between min - this and max + this
                                                                                             // to go slightly outside the shootable range
    private final static double kMinRange = ShooterConstants.kMinShootRange.in(Meters);      // used for generating random ranges between these values
    private final static double kMaxRange = ShooterConstants.kMaxShootRange.in(Meters);      // We can generate ranges less or more than the min/max



    public VisionSubsystem() {

        // nothing to do right now...
    }


    public Boolean isTargetWithinRange() {

        double range = getRangeToTarget();
        
        if (range >= ShooterConstants.kMinShootRange.in(Meters) && range <= ShooterConstants.kMaxShootRange.in(Meters)) {
            return true;
        }

        return false;              // out of range
    }



    public double getRangeToTarget() {

        // for now we just generate a random range - when we have actual vision-based ranging, we'll pickup the
        // range to the target from the vision system (most likely through network tables entries)
        //
        // the generated range runs between the min and max values and can go the tolerance value above or below
        // the min to max band so we get some out of range random values
        //
        // this makes our effective random range values range from (min - tolerance) to (max + tolerance)
        //
        // we need to do this because the random() function returns a random double between 0.0 and 1.0

        double randomRange = Math.random() * (kMaxRange - kMinRange + 2 * kRangeTolerance) + (kMinRange - kRangeTolerance);

        return randomRange;
    }  
}