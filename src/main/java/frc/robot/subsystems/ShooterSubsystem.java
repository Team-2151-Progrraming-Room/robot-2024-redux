package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;

import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.util.sendable.SendableBuilder;



// SparkMax imports - these come from REV Robotics

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;


// our robot constants

import frc.robot.Robot;
import frc.robot.Constants.ShooterConstants;


import java.util.function.DoubleSupplier;



// the first comment in the definition is picked up and displayed as information about this objecty when you
// hover over it in the editor

public class ShooterSubsystem extends SubsystemBase {

/**
 * The Shooter subsystem provides all of the methods for shooting the note one loaded
 */
    
    private final CANSparkMax m_shooterMotor = new CANSparkMax(ShooterConstants.kShooterMotor_CANID, MotorType.kBrushless);
    private final CANSparkMax m_kickerMotor  = new CANSparkMax(ShooterConstants.kKickerMotor_CANID,  MotorType.kBrushless);

    private SparkPIDController m_shooterPidController;

    private RelativeEncoder m_shooterEncoder;

    // if we had a variable angled shooter, we'd probably handle it sort of like this
    //
    // we've got a table that maps distance to the target to an appropriate shooter speed
    //
    // we'd take that same distance and map it to a shooter angle - the tables don't need to be the same size - we're going to interpolate the values
    // so more values, the more specific the shooter position of speed will be but it it's linear, we don't need that many values
    //
    // we use the value passed if we find it, otherwise we use the value just below up to the next value
    //
    // table is arranged so the distance, in meters, is the first entry and the speed or angle is the second entry
    //
    // note that in this example, the tables could be different sizes if the speed or angle covered different ranges
    //
    // the tables could have different distance entries if speed and angle don't map use or need the same range values
    // for changes in angle and speed
    //
    // all of this would need to be worked out emperically through actual robot testing to work out exactly how our
    // shooter works in terms of speds and angles for different distances

    private double[][] m_shooterSpeedTable = { {5.0, 1000.0}, {7.5, 1500.0},              {9.5, 2000.0},                {ShooterConstants.kMaxShootRange.in(Meters), 2500.0} };
    private double[][] m_shooterAngleTable = { {5.0, 45.0},   {7.5, 40},     {8.5, 35.0}, {9.5, 31.0},   {10.5, 28.0},  {ShooterConstants.kMaxShootRange.in(Meters), 25.0} };

    private double m_shooterAngleDegreesTarget = 0.0;;

    private double m_shooterRpmTarget          = 0.0;

    private double m_targetRange               = 0.0;



    public ShooterSubsystem() {

        m_shooterMotor.restoreFactoryDefaults();
        m_kickerMotor.restoreFactoryDefaults();

        m_shooterMotor.stopMotor();                     // just a safety thing - they should be stopped on instantiation
        m_kickerMotor.stopMotor();


        m_shooterPidController = m_shooterMotor.getPIDController();

        m_shooterPidController.setP(ShooterConstants.kShooterPidP);
        m_shooterPidController.setI(ShooterConstants.kShooterPidI);
        m_shooterPidController.setD(ShooterConstants.kShooterPidD);
        m_shooterPidController.setFF(ShooterConstants.kShooterPidFF);
        m_shooterPidController.setIZone(ShooterConstants.kShooterPidIzone);
        m_shooterPidController.setOutputRange(ShooterConstants.kShooterPidOutputMin, ShooterConstants.kShooterPidOutputMax);
        m_shooterPidController.setReference(m_shooterRpmTarget, CANSparkMax.ControlType.kVelocity);

        /* // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("Shooter Set Point", m_shooterRpmTarget);
        SmartDashboard.putNumber("Shooter Actual RPM", 0.0);
        SmartDashboard.putNumber("Shooter P Gain", ShooterConstants.kShooterPidP);
        SmartDashboard.putNumber("Shooter I Gain", ShooterConstants.kShooterPidI);
        SmartDashboard.putNumber("Shooter D Gain", ShooterConstants.kShooterPidD);
        SmartDashboard.putNumber("Shooter I Zone", ShooterConstants.kShooterPidIzone);
        SmartDashboard.putNumber("Shooter Feed Forward", ShooterConstants.kShooterPidFF);
        SmartDashboard.putNumber("Shooter Max Output", ShooterConstants.kShooterPidOutputMax);
        SmartDashboard.putNumber("Shooter Min Output", ShooterConstants.kShooterPidOutputMin);
        */

        m_shooterEncoder = m_shooterMotor.getEncoder();

        if ( ! Robot.isReal()) {                        // setup things for the simulation as needed
        
        }
    }



    // set the shooter for the passed distance
    //
    // this includes setting the shooter speed and the angle
    //
    // for the 2024 robot, this doesn't matter and won't really do anything in terms of shooting angle
    // but if we could set the angle, we'd probably do something like this

    public void setShooterRange(double range) {

      m_targetRange = range;

      setShooterSpeedByRange(range);
      setShooterAngleByRange(range);
    }



    // set the shooter speed based on the target distance
    public void setShooterSpeedByRange(double range) {

      System.out.println("setShooterSpeedbyRange(" + range + ") = " + lookupByValue(range, m_shooterSpeedTable));

      m_shooterRpmTarget = lookupByValue(range, m_shooterSpeedTable);

      m_shooterPidController.setReference(m_shooterRpmTarget, CANSparkMax.ControlType.kVelocity);
    }

    

    // set the shooter angle based on the target distance
    //
    // we don't actually do anything here since the 2024 robot has a fixed shooter angle so this is
    // sort of "simulated"
    public void setShooterAngleByRange(double range) {

      System.out.println("setShooterAngleByrange(" + range + ") = " + lookupByValue(range, m_shooterAngleTable));

      setShooterAngleDegrees(lookupByValue(range, m_shooterAngleTable));
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



    // symmetry with the kickerMotorOff to cleanly end the shooting sequence command

    public void shooterMotorOff() {

      m_shooterRpmTarget = 0.0;

      m_shooterPidController.setReference(m_shooterRpmTarget, CANSparkMax.ControlType.kVelocity);
    }



    public double getShooterVelocity() {

      return m_shooterEncoder.getVelocity();      // return the RPM - not power percentage
    }



    public boolean atShooterSpeed() {

      if (MathUtil.isNear(m_shooterRpmTarget, getShooterVelocity(), ShooterConstants.kShooterSpeedTolerance)) {
        return true;
      }
      
      return false;
    }



    // we can handle the kicker speed here - once we know what it should be, we'll just set
    // the constant and run it at that

    public void kickerMotorOn() {

      m_kickerMotor.set(ShooterConstants.kKickerSpeed);
    }



    public void kickerMotorOff() {

      m_kickerMotor.stopMotor();
    }



  /* Utility Functions *************************************************************************
   ************************************************************************************/

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

    private double lookupByValue(double inValue, double[][] array) {

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



    // this gets called from the periodic routine
    //
    // all dashboard updates happen here
    //
    // keeps periodic() cleaner

    private void updateDashboard() {

          SmartDashboard.putNumber("Shooter RPM", Math.round(getShooterVelocity()));
          SmartDashboard.putNumber("Target RPM", Math.round(getShooterVelocity()));
          SmartDashboard.putNumber("Shooter Angle", Math.round(getShooterAngleDegrees()));
          SmartDashboard.putNumber("Target Angle", Math.round(getShooterVelocity()));
          SmartDashboard.putNumber("Shooter Range (meters)", m_targetRange);
          SmartDashboard.putNumber("Shooter Actual RPM", Math.round(getShooterVelocity()));
          SmartDashboard.putBoolean("At Shooter Speed", atShooterSpeed());
          SmartDashboard.putBoolean("At Shooter Angle", atShooterAngle());
    }



  /* Commands *************************************************************************
   ************************************************************************************/

  public Command ShootCommand(ShooterSubsystem shooter, DoubleSupplier rangeSupplier) {
    
    
    double range = rangeSupplier.getAsDouble();
    
    return Commands.sequence(
              
    // right now we don't do any robot alignment to the target nor do we prevent the driver
    // from moving the robot once it's aligned
    //
    // if we wanted a totally automated shooting sequence, we'd need to incorporate those aspects

              setShooterSpeedCommand(range),
/*

              setShooterAngleCommand(range),
              setShooterByRangeCommand(range),

              stabilizeShooterSpeedCommand(),
              stabilizeShooterAngleCommand(),
              
              kickerMotorOnCommand(),

              // for now, we're just letting the kicker run for some period of time
              //
              // all we want is to feed the note into the shooter so it actually gets launched
              //
              // doing this is generally fine but we could optimize this by watching the note sensor and only
              // running it until the note clears the sensor (at which point we assume it has been launched)
              //
              // this would make this command slightly more responsive and not *have* to run for the entire
              // kKickerRunTime period
    */
              Commands.waitSeconds(ShooterConstants.kKickerRunTime.in(Seconds))
    /*
              shooterMotorOffCommand(),
              kickerMotorOffCommand()
    */
    );
  }



  public Command setShooterByRangeCommand(double range) {

    return Commands.parallel(
      setShooterSpeedCommand(range),
      setShooterAngleCommand(range)
    );
  }



  public Command shootNoteCommand() {

    return Commands.sequence(
      kickerMotorOnCommand(),

      Commands.waitSeconds(ShooterConstants.kKickerRunTime.in(Seconds)),

      shooterMotorOffCommand(),
      kickerMotorOffCommand()
    );
  }


  public Command setShooterSpeedCommand(double range) {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          setShooterSpeedByRange(range);      // starts a PID controller for speed
        });
  }



  public Command setShooterAngleCommand(double range) {

    return runOnce(
        () -> {
          setShooterAngleByRange(range);      // would start a PID controller for angle (if we could set the angle)
        });
  }



  public Command kickerMotorOnCommand() {

    return runOnce(
        () -> {
          kickerMotorOn();
        });
  }



  public Command kickerMotorOffCommand() {

    return runOnce(
        () -> {
          kickerMotorOff();
        });
  }



  public Command shooterMotorOffCommand() {

    return runOnce(
        () -> {
          shooterMotorOff();
        });
  }



  public Command stabilizeShooterAngleCommand() {

    return run(() -> atShooterAngle());
  }
  


  public Command stabilizeShooterSpeedCommand() {

    return run(() -> atShooterSpeed());
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
    // Publish the solenoid state to telemetry.
    builder.addDoubleProperty("Shooter RPM (send)", () -> getShooterVelocity(), null);
    builder.addDoubleProperty("Shooter Angle (send)", () -> getShooterAngleDegrees(), null); 

  }
}