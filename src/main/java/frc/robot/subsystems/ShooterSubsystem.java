package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.util.sendable.SendableBuilder;



// SparkMax imports - these come from REV Robotics

import com.revrobotics.*;
import com.revrobotics.CANSparkLowLevel.MotorType;


// our robot constants

import frc.robot.Robot;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Utilities;



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

    // we've got a table that maps distance to the target to an appropriate shooter speed
    //
    // we'd take that same distance and map it to a shooter angle in the ShooterAngleSubsystem
    //
    // we use the value passed if we find it, otherwise we use the value just below up to the next value
    //
    // table is arranged so the distance, in meters, is the first entry and the speed or angle is the second entry
    //
    // note that the tables could be different sizes if the speed or angle covered different ranges
    //
    // all of this would need to be worked out emperically through actual robot testing to work out exactly how our
    // shooter works in terms of speeds and angles for different distances

    private double[][] m_shooterSpeedTable = { {4.0, 750}, {5.5, 1000}, {7.0, 1400}, {9.5, 1800}, {11.0, 2200}, {ShooterConstants.kMaxShootRange.in(Meters), 2500} };

    private double m_shooterRpmTarget          = 0.0;



    public ShooterSubsystem() {

        m_shooterMotor.restoreFactoryDefaults();
        m_kickerMotor.restoreFactoryDefaults();

        m_shooterMotor.stopMotor();                     // just a safety thing - they should be stopped on instantiation
        m_kickerMotor.stopMotor();
   
        m_shooterMotor.setSmartCurrentLimit(ShooterConstants.kShooterMotorCurrentLimit);        
        m_kickerMotor.setSmartCurrentLimit(ShooterConstants.kKickerMotorCurrentLimit);

        m_shooterPidController = m_shooterMotor.getPIDController();

        m_shooterPidController.setP(ShooterConstants.kShooterPidP);
        m_shooterPidController.setI(ShooterConstants.kShooterPidI);
        m_shooterPidController.setD(ShooterConstants.kShooterPidD);
        m_shooterPidController.setFF(ShooterConstants.kShooterPidFF);
        m_shooterPidController.setIZone(ShooterConstants.kShooterPidIzone);
        m_shooterPidController.setOutputRange(ShooterConstants.kShooterPidOutputMin, ShooterConstants.kShooterPidOutputMax);
        m_shooterPidController.setReference(m_shooterRpmTarget, CANSparkMax.ControlType.kVelocity);

        m_shooterEncoder = m_shooterMotor.getEncoder();

        if ( ! Robot.isReal()) {                        // setup things for the simulation as needed
        
        }
    }



    // set the shooter speed based on the target distance
    public void setShooterSpeedByRange(double range) {

      System.out.println("setShooterSpeedbyRange(" + range + ") = " + Utilities.lookupByValue(range, m_shooterSpeedTable));

      m_shooterRpmTarget = Utilities.lookupByValue(range, m_shooterSpeedTable);

      m_shooterPidController.setReference(m_shooterRpmTarget, CANSparkMax.ControlType.kVelocity);
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



    // this gets called from the periodic routine
    //
    // all dashboard updates happen here
    //
    // keeps periodic() cleaner

    private void updateDashboard() {

    }



  /* Commands *************************************************************************
   ************************************************************************************/

  public Command setShooterSpeedByRangeCommand(DoubleSupplier range) {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          setShooterSpeedByRange(range.getAsDouble());      // starts a PID controller for speed
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

    builder.addDoubleProperty("Shooter RPM (send)", () -> getShooterVelocity(), null);
  }
}