package frc.robot.subsystems;

import edu.wpi.first.math.*;
import edu.wpi.first.math.MathUtil;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;

import javax.naming.directory.InvalidSearchFilterException;

// simulation-related

import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;



// SparkMax imports - these come from REV Robotics

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel.MotorType;


// our robot constants

import frc.robot.Robot;
import frc.robot.Constants.RobotConstants;
import frc.robot.Constants.ShooterConstants;



// the first comment in the definition is picked up and displayed as information about this objecty when you
// hover over it in the editor

public class ShooterSubsystem extends SubsystemBase {

/**
 * The Shooter subsystem provides all of the methods for shooting the note one loaded
 */
    
    private final CANSparkMax m_shooterMotor = new CANSparkMax(ShooterConstants.kShooterMotor_CANID, MotorType.kBrushless);
    private final CANSparkMax m_kickerMotor  = new CANSparkMax(ShooterConstants.kKickerMotor_CANID,  MotorType.kBrushless);


    private SparkPIDController m_shooterPidController;
    private double             m_shooterPidP;
    private double             m_shooterPidI;
    private double             m_shooterPidD;
    private double             m_shooterPidIzone;
    private double             m_shooterPidFF;
    private double             m_shooterPidOutputMax;
    private double             m_shooterPidOutputMin;
    private double             m_shooterPidSetPoint = 0;
    
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
    private double[][] m_shooterSpeedTable = { {5.0, 1000.0}, {10.0, 1500.0},           {15.0, 2000.0},             {20.0, 2500.0} };
    private double[][] m_shooterAngleTable = { {5.0, 45.0},   {10.0, 40}, {12.0, 35.0}, {15.0, 31.0}, {17.0, 28.0}, {20.0, 25.0} };

    private double m_shooterAngleDegreesTarget;
    private double m_shooterAngleDegreesTolerance = 1.0;   // degree tolerance that is OK

    private double m_shooterRpmTarget;
    private double m_shooterRpmTolerance = 20;        // + / - this RPM is OK




    public ShooterSubsystem() {

        m_shooterMotor.restoreFactoryDefaults();
        m_kickerMotor.restoreFactoryDefaults();

        m_shooterMotor.stopMotor();                     // just a safety thing - they should be stopped on instantiation
        m_kickerMotor.stopMotor();


        m_shooterPidController = m_shooterMotor.getPIDController();

        m_shooterPidController.setP(ShooterConstants.kPidP);
        m_shooterPidController.setI(ShooterConstants.kPidI);
        m_shooterPidController.setD(ShooterConstants.kPidD);
        m_shooterPidController.setIZone(ShooterConstants.kPidIzone);
        m_shooterPidController.setFF(ShooterConstants.kPidFF);
        m_shooterPidController.setOutputRange(ShooterConstants.kPidOutputMin, ShooterConstants.kPidOutputMax);

        m_shooterPidSetPoint = 0;

        // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("Shooter Set Point", m_shooterPidSetPoint);
        SmartDashboard.putNumber("Shooter Actual RPM", 0.0);
        SmartDashboard.putNumber("Shooter P Gain", ShooterConstants.kPidP);
        SmartDashboard.putNumber("Shooter I Gain", ShooterConstants.kPidI);
        SmartDashboard.putNumber("Shooter D Gain", ShooterConstants.kPidD);
        SmartDashboard.putNumber("Shooter I Zone", ShooterConstants.kPidIzone);
        SmartDashboard.putNumber("Shooter Feed Forward", ShooterConstants.kPidFF);
        SmartDashboard.putNumber("Shooter Max Output", ShooterConstants.kPidOutputMax);
        SmartDashboard.putNumber("Shooter Min Output", ShooterConstants.kPidOutputMin);

        m_shooterEncoder = m_shooterMotor.getEncoder();

        setShooterSpeedByMetersDistance(1.0);
        setShooterSpeedByMetersDistance(12.0);
        setShooterSpeedByMetersDistance(22.0);

        if ( ! Robot.isReal()) {                        // setup things for the simulation as needed
        
        }
    }


    // set the shooter for the passed distance
    //
    // this includes setting the shooter speed and the angle
    //
    // for the 2024 robot, this doesn't matter and won't really do anything but if we could set the angle,
    // we'd do something like this

    public void setShooterMetersDistance(double distance) {

      setShooterSpeedByMetersDistance(distance);
      setShooterAngleByMetersDistance(distance);;

    }



    // set the shooter speed based on the target distance
    public void setShooterSpeedByMetersDistance(double distance) {

      System.out.println("setShooterSpeed(" + distance + ") = " + lookupByValue(distance, m_shooterSpeedTable));

    }

    

    // set / get the shooter angle based on the target distance
    public void setShooterAngleByMetersDistance(double distance) {

    }



    private void setShooterDegreesAngle(double angle) {

      m_shooterAngleDegreesTarget = lookupByValue(angle, m_shooterAngleTable);
    }



    private double getShooterAngleDegrees() {

      return m_shooterAngleDegreesTarget + 0.1;      // we return the angle + .1 degrees - no mechanism is perfect
    }



    public boolean atShooterAngle() {

      if (MathUtil.isNear(m_shooterAngleDegreesTarget, getShooterAngleDegrees(), m_shooterAngleDegreesTolerance)) {
        return true;      // we're always at the angle for now
      }
      
      return false;
    }



    // set the shooter speed in RPM
    //
    // positive RPM means use the PID controller on the SparkMAX to set and maintain the target speed
    //
    // 0 means stop the shooter motor

    private void setShooterSpeed(double speed) {      // only used internally

     if (speed == 0) {
        m_shooterMotor.stopMotor();
        return;
     }

     m_shooterMotor.set(speed);
    }



    // symmetry with the kickerMotorOff to cleanly end the shooting sequence command

    public void shooterMotorOff() {

      setShooterSpeed(0.0);
    }



    public double getShooterVelocity() {

      return m_shooterEncoder.getVelocity();      // return the RPM - not power percentage
    }



    public boolean atShooterSpeed() {

      if (MathUtil.isNear(m_shooterPidSetPoint, getShooterVelocity(), m_shooterRpmTolerance)) {
        return true;
      }
      
      return false;
  }



    // we cvan handle the kicker speed here - once we know what it should be, we'll just set
    // the constant and run it at that

    public void kickerMotorOn() {

      m_kickerMotor.set(ShooterConstants.kKickerSpeed);
    }



    public void kickerMotorOff() {

      m_kickerMotor.stopMotor();
    }


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



  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }



  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }



  @Override
  public void periodic() {


    // read PID coefficients from SmartDashboard
    m_shooterPidSetPoint  = SmartDashboard.getNumber("Shooter Set Point", 0);

    double p   = SmartDashboard.getNumber("Shooter P Gain", 0);
    double i   = SmartDashboard.getNumber("Shooter I Gain", 0);
    double d   = SmartDashboard.getNumber("Shooter D Gain", 0);
    double iz  = SmartDashboard.getNumber("Shooter I Zone", 0);
    double ff  = SmartDashboard.getNumber("Shooter Feed Forward", 0);
    double max = SmartDashboard.getNumber("Shooter Max Output", 0);
    double min = SmartDashboard.getNumber("Shooter Min Output", 0);

    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((p != m_shooterPidP)) { m_shooterPidController.setP(p); m_shooterPidP = p; }

    if((i != m_shooterPidI)) { m_shooterPidController.setI(i); m_shooterPidI = i; }

    if((d != m_shooterPidD)) { m_shooterPidController.setD(d); m_shooterPidD = d; }

    if((iz != m_shooterPidIzone)) { m_shooterPidController.setIZone(iz); m_shooterPidIzone = iz; }

    if((ff != m_shooterPidFF)) { m_shooterPidController.setFF(ff); m_shooterPidFF = ff; }
    
    if((max != m_shooterPidOutputMax) || (min != m_shooterPidOutputMin)) { 
      m_shooterPidController.setOutputRange(min, max); 
      m_shooterPidOutputMin = min;
      m_shooterPidOutputMax = max; 
    }

    m_shooterPidController.setReference(m_shooterPidSetPoint, CANSparkMax.ControlType.kVelocity);

    SmartDashboard.putNumber("Shooter P Gain", p);
    SmartDashboard.putNumber("Shooter I Gain", i);
    SmartDashboard.putNumber("Shooter D Gain", d);
    SmartDashboard.putNumber("Shooter I Zone", iz);
    SmartDashboard.putNumber("Shooter Feed Forward", ff);
    SmartDashboard.putNumber("Shooter Max Output", max);
    SmartDashboard.putNumber("Shooter Min Output", min);
    SmartDashboard.putNumber("Shooter Set Point", m_shooterPidSetPoint);
    SmartDashboard.putNumber("Shooter Actual RPM", Math.round(getShooterVelocity()));
  }



  @Override
  public void simulationPeriodic() {

  }
}