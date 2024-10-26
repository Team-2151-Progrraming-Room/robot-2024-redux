package frc.robot.subsystems;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.AnalogGyro;
import static edu.wpi.first.units.Units.*;


// simulation-related

import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;


import edu.wpi.first.wpilibj2.command.SubsystemBase;



// SparkMax imports - these come from REV Robotics

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;


// our robot constants

import frc.robot.Robot;
import frc.robot.Constants.RobotConstants;
import frc.robot.Constants.DrivetrainConstants;



// the first comment in the definition is picked up and displayed as information about this objecty when you
// hover over it in the editor

public class DriveSubsystem extends SubsystemBase {

/**
 * The Drivetrain subsystem provides all of the methods for driving the robot
 */
    
    private final CANSparkMax m_leftFrontMotor  = new CANSparkMax(DrivetrainConstants.kDriveLeftFront_CANID,  MotorType.kBrushless);
    private final CANSparkMax m_leftRearMotor   = new CANSparkMax(DrivetrainConstants.kDriveLeftRear_CANID,   MotorType.kBrushless);
    private final CANSparkMax m_rightFrontMotor = new CANSparkMax(DrivetrainConstants.kDriveRightFront_CANID, MotorType.kBrushless);
    private final CANSparkMax m_rightRearMotor  = new CANSparkMax(DrivetrainConstants.kDriveRightRear_CANID, MotorType.kBrushless);

    // we're setup to use direect encoders rather than the SparkMax encoders to stay clear of the SparkMax controllers
    // during simulation

    // private final RelativeEncoder m_leftEncoder  = m_leftFrontMotor.getEncoder();
    // private final RelativeEncoder m_rightEncoder = m_leftFrontMotor.getEncoder();

    private final DifferentialDrive m_diffDrive = new DifferentialDrive(m_leftFrontMotor, m_rightFrontMotor);    // setup motor following later


    // for now, just use the typical values for various jitters from the example in the doc - would measure this in real life using 'sysinfo' on the
    // actual robot as part of our characterization process (very important for auto path following to map a "perfect" theoretical robot into
    // an actual robot that has internal friction and inertia)

    private final DifferentialDrivetrainSim m_diffDriveSim = new DifferentialDrivetrainSim(DCMotor.getNEO(DrivetrainConstants.kNumOfMotorsPerSide),
                                                                                           DrivetrainConstants.kDrivetrainGearRatio,
                                                                                           RobotConstants.kRobotInertia,
                                                                                           RobotConstants.kRobotMass.in(Kilograms),
                                                                                           DrivetrainConstants.kDrivetrainWheelDiameter.in(Meters) / 2,
                                                                                           DrivetrainConstants.kDrivetrainTrack.in(Meters),
                                                                                           VecBuilder.fill(0.001, 0.001, 0.001, 0.1,
                                                                                                           0.1, 0.005, 0.005));


    // setup some quadrature encoders to instantiate the simulated encoders from - can't seem to do that from the SparkMax built-in encoder

    private final Encoder m_leftEncoder  = new Encoder(DrivetrainConstants.kLeftEncoderADio,  DrivetrainConstants.kLeftEncoderBDio);
    private final Encoder m_rightEncoder = new Encoder(DrivetrainConstants.kRightEncoderADio, DrivetrainConstants.kRightEncoderBDio);

    private final EncoderSim m_leftEncoderSim  = new EncoderSim(m_leftEncoder);
    private final EncoderSim m_rightEncoderSim = new EncoderSim(m_rightEncoder);

    // for now, we'll create an analog gyro to associated with our simulated gyro

    private AnalogGyro m_gyro       = new AnalogGyro(1);
    private AnalogGyroSim m_gyroSim = new AnalogGyroSim(m_gyro);

    private Field2d m_field = new Field2d();

    private DifferentialDriveOdometry m_odometry = new DifferentialDriveOdometry(m_gyro.getRotation2d(),
                                                                                 m_leftEncoder.getDistance(),
                                                                                 m_rightEncoder.getDistance(),
                                                                                 // 1M from wall, midfield, pointing towards other alliance
                                                                                 new Pose2d(1.35, 5.55, new Rotation2d(Math.PI)));


    private Rotation2d m_rotation      = new Rotation2d(0.0);                // simulation starting rotation - 0.0 is downfield to other alliance wall

    private Pose2d m_pose              = new Pose2d(6.6, 1.9, m_rotation);     // simulation starting pose - x, y, rotation


    private boolean m_driveTypeErrorPrinted = false;



    public DriveSubsystem() {

        // in general, it's a good idea to set the controller options to WHAT WE WANT
        //
        // like every time
        //
        // it's OK to use the various configuration tools to come up with the settings we want but once we have
        // those settings, we want make sure those settings are in use every time
        //
        // to get there, we'll reset the controllers to factory defaults and then explicitly set what we need

        m_leftFrontMotor.restoreFactoryDefaults();
        m_leftRearMotor.restoreFactoryDefaults();
        m_rightFrontMotor.restoreFactoryDefaults();
        m_rightRearMotor.restoreFactoryDefaults();

        // since the motors are facing in the opposite direction, one side needs to be reversed
        //
        // if we consider the "left" side as we look from the rear of the robot (like we were driving it),
        // then when the left is turning "forward", the right side needs to turn "backwards" on a differential
        // drivetrain
        //
        // we only need to set the right front motor as the rear motors will all be set to follow their
        // the front motors on their respective side

        m_rightFrontMotor.setInverted(true);


        // since we have two motors on each side, we'll make the rear motors follow the fronts
        //
        // that way we only send speed comnmands to the front motors and the rears will do the same
        //
        // this makes multiple motors useable with the differential drive class since it excpects only
        // a single motor controller for each "side"

        m_leftRearMotor.follow(m_leftFrontMotor);
        m_rightRearMotor.follow(m_rightFrontMotor);
        
        m_leftFrontMotor.stopMotor();                     // just a safety thing - they should be stopped on instantiation
        m_rightFrontMotor.stopMotor();

        m_leftEncoder.setDistancePerPulse(DrivetrainConstants.kDrivetrainWheelDiameter.in(Meters) * Math.PI / DrivetrainConstants.kDrivetrainEncoderCPR);
        m_rightEncoder.setDistancePerPulse(m_leftEncoder.getDistancePerPulse());    // get what we set; they always should be the same

        if ( ! Robot.isReal()) {                          // setup things for the simulation as needed
        
          // we'll just tell it we have a specific pulse count per rev of the wheel and calculate a distance for each pulse
          // calculate the wheel circuference and divide but the number of pulses per rotation

          m_leftEncoderSim.setDistancePerPulse(DrivetrainConstants.kDrivetrainWheelDiameter.in(Meters) * Math.PI / DrivetrainConstants.kDrivetrainEncoderCPR);
          m_rightEncoderSim.setDistancePerPulse(m_leftEncoderSim.getDistancePerPulse());    // get what we set; they always should be the same

          m_leftEncoderSim.setCount(0);
          m_rightEncoderSim.setCount(0);

          SmartDashboard.putData("Field", m_field);     // display the field overhead view
          m_field.setRobotPose(m_pose);                     // show the starting pose on the field
        }
    }



    public void drive(double val1, double val2, int driveType) {

      switch(driveType) {
        
        case DrivetrainConstants.kDriveArcade:

          // invert the side to side joystick
          m_diffDrive.arcadeDrive(val1, -val2, DrivetrainConstants.kDriveSqInputs);
          break;

        case DrivetrainConstants.kDriveTank:

          m_diffDrive.tankDrive(val1, val2, DrivetrainConstants.kDriveSqInputs);
          break;

        case DrivetrainConstants.kDriveCurvature:

          // curvatureDrive controls the rate of turning related to the robot speed
          //
          // it does not have a square input flag but I still think it's valuable so we'll manually apply it here

          if (val1 < 0.0) {
            val1 *= val1 * -1.0;    // maintain the sign as negative
          } else {
            val1 *= val1;
          }

          if (val2 < 0.0) {
            val2 *= val2 * -1.0;
          } else {
            val2 *= val2;
          }

          // enable turn in place
          m_diffDrive.curvatureDrive(val1, val2, true);
          break;

        default:

          // only need to print the message once when we encounter this error
          //
          // in the event of an invalid drive type, we will basically ignore this call
          if (m_driveTypeErrorPrinted != true) {

            String errMsg = "Drivetrain: Drive: invalid drive type " + driveType;
            System.err.println(errMsg);

            m_driveTypeErrorPrinted = true;
          }
          break;
        }
    }




  // be able to get the current speeds for logging and dashboard use

  public double getLeftSpeed() {

    return m_leftFrontMotor.get();
  }


  public double getRightSpeed() {

    return m_rightFrontMotor.get();
  }


  public void driveInputs(double leftY, double rightY, double rightX) {
    
    leftY  *= -1;     // invert since it returns negative for forward
    rightY *= -1;

    switch (DrivetrainConstants.kDriveType) {

      case DrivetrainConstants.kDriveArcade:    // both are handled the same
      case DrivetrainConstants.kDriveCurvature:

        drive(leftY, rightX, DrivetrainConstants.kDriveType);
        break;

      case DrivetrainConstants.kDriveTank:

        drive(leftY, rightY, DrivetrainConstants.kDriveType);
        break;
    }
  }


/* DISABLED AND NOT COMPILED FOR NOW

  // collision detection at the edge of the field
  //
  // for now, this is really rudimentary as we just use the middle of the robot width
  // (which assumes a square robot) and we don't take into account the rotation of the
  // robot (If it's rotated, the corners will hit first) - it does include a bumper
  // width for fun
  //
  // using this will help keep the robot on the playing field during similation
  //
  // enhancing this check to take the robot rotation into account would be an interesting
  // addition if someone was interested in doing that.  While we were at it, that could even
  // be extended to take the non-rectangular field edges coming from the speaker and the
  // loading station for each alliance.
  //
  // the details here are that it takes "inPose" as the incoming pose (where the
  // robot thinks it "wants" to be), the returned pose is where the robot "should" be taking the field
  // perimeter into acount.
  //
  // if there is no collision with the edge, inPose is returned
  //
  // if there is a collision with the edge(s) of the field, the returned pose is the pose that keeps the
  // robot on the field
  //
  // WARNING:  Every time a collision is detected, a new pose is returned which will need to be reaped by
  // the garbage collector - in general, this approach doesn't seem like the best way and maybe we shoudl just not
  // drive the robot off the field as there is more to it than just the pose - need to consider the impact on
  // odometry for example - do we need to "back out" or otherwise adjust the odometry readings?

  private Pose2d collisionCheck(Pose2d inPose) {

    double x = inPose.getX();
    double y = inPose.getY();

    Rotation2d rotation = inPose.getRotation();

    final double halfRobotWidth = RobotConstants.kRobotWidth.in(Meters) / 2 + RobotConstants.kBumperWidth.in(Meters);

    boolean collisionDetected = false;


    if (x < halfRobotWidth) {     // check for x collision at each end of the field
      x = halfRobotWidth;
      collisionDetected = true;
    } else {
      if (x < (FieldConstants.kFieldXMax.in(Meters) - halfRobotWidth)) {
        x = FieldConstants.kFieldXMax.in(Meters) - halfRobotWidth;
        collisionDetected = true;
      }
    }

    if (y < halfRobotWidth) {     // check for y collisions
      y = halfRobotWidth;
      collisionDetected = true;
    } else {
      if (y > (FieldConstants.kFieldYMax.in(Meters) - halfRobotWidth)) {
        y = FieldConstants.kFieldYMax.in(Meters) - halfRobotWidth;
        collisionDetected =  true;    // y collision
      }
    }

    // if collisionDetected is true, there was a collision detected and the values
    // x and y reflect the updated robot locations we want to be at
    //
    // since we can't just "set" x and y values (or rotation values either) in a pose,
    // we need to create a new pose and return that - that creates a new object which the
    // garbage collector needs to cleanup at some point.  if that happens, the program will
    // so this isn't something we want to do in actual teleop mode.  We probably won't see
    // any pauses in short runs but if this is run for a "long" time (don't ask me what
    // "long" is right now), eventually enough pose objects will be created and no longer
    // used that they'll need to be collected.  if you want to know more about this topic
    // including memory usage monitoring, see the FRC doc for more details.

    if ( ! collisionDetected) {         // no collision - just return inPose
      return inPose;
    }

    return new Pose2d(x, y, rotation);  // return a new pose that keeps us on the field
  }
*/



/* Periodics ************************************************************************************
 ************************************************************************************************/

  @Override
  public void periodic() {

    if ( ! Robot.isReal()) {

      m_odometry.update(m_gyro.getRotation2d(),
                        m_leftEncoder.getDistance(),
                        m_rightEncoder.getDistance());

      // show the robot on the fidl in its latest pose
      m_field.setRobotPose(m_odometry.getPoseMeters());
    }
  }



  @Override
  public void simulationPeriodic() {

    // the inclusion of robot voltage maps the -1 to 1 "speed" value into voltage which setInputs uses
    // direction gets handled as part of the sign of the speed
    m_diffDriveSim.setInputs(getLeftSpeed() * RobotController.getInputVoltage(),
                             getRightSpeed() * RobotController.getInputVoltage()); // invert right side
    
    // Advance the model by 20 ms. Note that if you are running this
    // subsystem in a separate thread or have changed the nominal timestep
    // of TimedRobot, this value needs to match it.
    m_diffDriveSim.update(0.02);

    // Update all of our sensors.
    m_leftEncoderSim.setDistance(m_diffDriveSim.getLeftPositionMeters());
    m_leftEncoderSim.setRate(m_diffDriveSim.getLeftVelocityMetersPerSecond());

    m_rightEncoderSim.setDistance(m_diffDriveSim.getRightPositionMeters());
    m_rightEncoderSim.setRate(m_diffDriveSim.getRightVelocityMetersPerSecond());

    m_gyroSim.setAngle(-m_diffDriveSim.getHeading().getDegrees());
  }
}
