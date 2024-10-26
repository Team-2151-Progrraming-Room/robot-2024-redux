// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;


/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

  public static class RobotConstants {

    public static final Measure<Mass> kRobotMass = Pounds.of(100.0);

    public static final double kRobotInertia     = 7.0;   // not measured for this robot - typical 3-8 jKgM^2 range

    public static final Measure<Distance> kRobotWidth  = Inches.of(30); // for simmulation crash detection
    public static final Measure<Distance> kRobotLength = Inches.of(30);
    public static final Measure<Distance> kBumperWidth = Inches.of(6);  // how think are the bumpers?

    public static final Measure<Voltage> kRobotNomVoltage = Volts.of(12.0);  // nominal voltage

    public static final double kNeoMaxRpm = 5700;   // used for FF scaling for shooter and other applications
  }



  public static class FieldConstants {

    public static final Measure<Distance> kFieldXMax = Feet.of(53).plus(Inches.of(3));         // length from alliance wall to alliance wall
    public static final Measure<Distance> kFieldYMax = Feet.of(26).plus(Inches.of(3));         // width of field
  }



  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }



  public static class DrivetrainConstants {

    public static final int kNumOfMotorsPerSide = 2;  // 4 motor mirrors drivetrain

    // if the motor will be controlled via a CANbus connected controller, we need to know the
    // CANbus ID to use for the various constructors
    //
    // IMPORTANT:  This not only needs to be set in the code but the actual motor controller
    // will need to be configured to the correct CANbus address so it will respond to the code
    // as expected - this is done outside of the robot code and is typically done by a
    // vendor supplied tool specific to the brand of controller being used (REV Hardware Client
    // for REV Robotics SparkMax controllers for example)
    
    public static final int kDriveLeftFront_CANID  = 10;
    public static final int kDriveLeftRear_CANID   = 11;
    public static final int kDriveRightFront_CANID = 12;
    public static final int kDriveRightRear_CANID  = 13;

    public static final int kLeftEncoderADio  = 0;      // DIO channels on RoboRIO (should be able to do this using SparkMAX encoder but having simulation trouble)
    public static final int kLeftEncoderBDio  = 1;
    public static final int kRightEncoderADio = 2;
    public static final int kRightEncoderBDio = 3;

    public static final int kDriveArcade       = 0;
    public static final int kDriveTank         = 1;
    public static final int kDriveCurvature    = 2;

    public static final int kDriveType         = kDriveArcade;

    public static final boolean kDriveSqInputs = true;

    // physical drivetrain constants

    public static final Measure<Distance> kDrivetrainTrack         = Inches.of(29);   // close to the width of the robot - using a typical 30" width
    public static final Measure<Distance> kDrivetrainWheelbase     = Inches.of(24);   // wheel base tends to be smaller than track
    public static final Measure<Distance> kDrivetrainWheelDiameter = Inches.of(6);    // 6in wheel diameter
    public static final double kDrivetrainGearRatio                = 10.71;                     // typical kitbot gear ratio

    public static final int kDrivetrainEncoderCPR                  = 360;                       // typical quadrature encoder used on a kitbot chassis
                                                                                                // AndyMark E4T encoder
  }



  public static class IntakeConstants {

  }



  public static class ShooterConstants {
 
    public static final int kKickerMotor_CANID      = 20;
    public static final int kShooterMotor_CANID     = 21;
    
    public static final int kKickerSpeed            = 500;

    public static final double kShooterPidP         = 0.00006;       // tuner as needed
    public static final double kShooterPidI         = 0.0000005;
    public static final double kShooterPidD         = 0.0;
    public static final double kShooterPidFF        = 0.00003;
    public static final double kShooterPidIzone     = 0.0;
    public static final double kShooterPidOutputMin = 0.0;       // don't need to run in reverse (negative value)
    public static final double kShooterPidOutputMax = 1.0;

    public static final double kShooterSpeedTolerance = 25.0;    // +/- tolerance in RPM
    public static final double kShooterAngleTolerance = 2.0;     // +/- tolerance in degrees

    public static final Measure<Distance> kMinShootRange = Meters.of(1.0);
    public static final Measure<Distance> kMaxShootRange = Meters.of(12.0);


    // both of the below are timesouts for the shooting related commands

    public static final Measure<Time> kShooterStabilizeTime = Seconds.of(3);         // how long we wait for the shooter motor to get up to speed

    public static final Measure<Time> kKickerRunTime        = Seconds.of(2);         // how long the kicker motor runs during the shooting sequence
  }



   public static class ShooterAngleConstants {
 
    public static final int kShooterAngleMotor_CANID      = 22;
   }
}
