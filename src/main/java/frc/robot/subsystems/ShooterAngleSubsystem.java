package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.util.sendable.SendableBuilder;



// SparkMax imports - these come from REV Robotics

// import com.revrobotics.CANSparkMax;
// import com.revrobotics.RelativeEncoder;
// import com.revrobotics.SparkPIDController;
// import com.revrobotics.CANSparkLowLevel.MotorType;


// our robot constants

import frc.robot.Robot;
import frc.robot.Constants.ShooterConstants;
// import frc.robot.Constants.ShooterAngleConstants;

import frc.robot.Utilities;

import java.util.function.DoubleSupplier;



// the first comment in the definition is picked up and displayed as information about this objecty when you
// hover over it in the editor

public class ShooterAngleSubsystem extends SubsystemBase {

/**
 * The ShooterAngle subsystem provides all of the methods for aiming the shooter
 */
    
    // private final CANSparkMax m_shooterAngleMotor = new CANSparkMax(ShooterAngleConstants.kShooterAngleMotor_CANID, MotorType.kBrushless);

    // private RelativeEncoder m_shooterAngleEncoder;

    // if we had a variable angled shooter, we'd probably handle it sort of like this
    //
    // same sort of table lookup process that we have in the shooter subsystem
    //
    // we've got a table that maps distance to the target to an appropriate shooter angle
    //
    // we'd take that same distance and map it to a shooter angle - the tables don't need to be the same size - we're going to interpolate the values
    // so more values, the more specific the shooter position of speed will be but it it's linear, we don't need that many values
    //
    // all of this would need to be worked out emperically through actual robot testing to work out exactly how our
    // shooter works in terms of speds and angles for different distances

    private double[][] m_shooterAngleTable = { {5.0, 45.0}, {7.5, 40},  {8.5, 35.0}, {9.5, 31.0}, {10.5, 28.0}, {ShooterConstants.kMaxShootRange.in(Meters), 25.0} };

    private double m_shooterAngleDegreesTarget = 0.0;;

    private double m_targetRange               = 0.0;



    public ShooterAngleSubsystem() {

        // m_shooterAngleMotor.restoreFactoryDefaults();

        // m_shooterAngleMotor.stopMotor();                     // just a safety thing - they should be stopped on instantiation

        // m_shooterAngleEncoder = m_shooterAngleMotor.getEncoder();

        if ( ! Robot.isReal()) {                        // setup things for the simulation as needed
        
        }
    }



    // set the shooter angle based on the target distance
    //
    // we don't actually do anything here since the 2024 robot has a fixed shooter angle so this is
    // sort of "simulated"

    public void setShooterAngleByRange(double range) {

        m_targetRange = range;
        
        System.out.println("setShooterAngleByrange(" + range + ") = " + Utilities.lookupByValue(m_targetRange, m_shooterAngleTable));
  
        setShooterAngleDegrees(Utilities.lookupByValue(m_targetRange, m_shooterAngleTable));
    }



    private void setShooterAngleDegrees(double angle) {

        m_shooterAngleDegreesTarget = angle;
    }



    private double getShooterAngleDegrees() {

      return m_shooterAngleDegreesTarget + 0.1;      // we return the angle + .1 degrees - no mechanism is perfect - this is
                                                     // essentially "simulating" the shooter angle to be close to what we asked for
    }



    public boolean atShooterAngle() {

      if (MathUtil.isNear(m_shooterAngleDegreesTarget, getShooterAngleDegrees(), ShooterConstants.kShooterAngleTolerance)) {
        return true;
      }
      
      return false;
    }



    // this gets called from the periodic routine
    //
    // all dashboard updates happen here
    //
    // keeps periodic() cleaner

    private void updateDashboard() {

          SmartDashboard.putNumber("Shooter Angle", Math.round(getShooterAngleDegrees()));
          SmartDashboard.putBoolean("At Shooter Angle", atShooterAngle());
    }



    /* Commands *************************************************************************
     ************************************************************************************/



     public Command setShooterAngleByRangeCommand(DoubleSupplier rangeSupplier) {

    return runOnce(
        () -> {
          setShooterAngleByRange(rangeSupplier.getAsDouble());      // starts a PID controller for angle
        });
  }



  public Command stabilizeShooterAngleCommand() {

    return run(() -> atShooterAngle());     // run until it returns true
  }


  
  /* Periodics *******************************************************************************
   *******************************************************************************************/
  
  @Override
  public void periodic() {

    updateDashboard();
  }



  @Override
  public void simulationPeriodic() {

  }



/* Sendables ********************************************************************************
 ********************************************************************************************/

  @Override
  public void initSendable(SendableBuilder builder) {

    super.initSendable(builder);

    builder.addDoubleProperty("Shooter Angle (send)", () -> getShooterAngleDegrees(), null); 
    builder.addBooleanProperty("At Shooter Angle (send)", () -> atShooterAngle(), null);

  }
}