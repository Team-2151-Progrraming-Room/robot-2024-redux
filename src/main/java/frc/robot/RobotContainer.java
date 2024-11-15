// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.ShootCommand;
import frc.robot.commands.LedBounceCommand;
import frc.robot.commands.LedIntakeRunningCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.ShooterAngleSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.LedSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.DoubleSupplier; 
import java.util.function.BooleanSupplier; 


/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem           = new ExampleSubsystem();

  public  final DriveSubsystem m_drivetrainSubsystem          = new DriveSubsystem();

  private final ShooterSubsystem m_shooterSubsystem           = new ShooterSubsystem();

  private final ShooterAngleSubsystem m_shooterAngleSubsystem = new ShooterAngleSubsystem();
    
  public final LedSubsystem m_ledSubsystem                    = new LedSubsystem();

  public final CommandXboxController m_driverController       = new CommandXboxController(OperatorConstants.kDriverControllerPort);


  public final VisionSubsystem m_vision                       = new VisionSubsystem();

  DoubleSupplier m_dynamicRange                               = () -> m_vision.getRangeToTarget();
  BooleanSupplier m_dynamicAtShootSpeed                       = () -> m_shooterSubsystem.atShooterSpeed();
  BooleanSupplier m_dynamicAtShootAngle                       = () -> m_shooterAngleSubsystem.atShooterAngle();

  public final Command m_shootCommand                         = new ShootCommand(m_shooterSubsystem, m_shooterAngleSubsystem, m_ledSubsystem,
                                                                                 m_dynamicRange, m_dynamicAtShootSpeed, m_dynamicAtShootAngle).getShootCommand();

  public final Command m_ledBounceCommand                     = new LedBounceCommand(m_ledSubsystem);
  public final Command m_ledIntakeRunningCommand              = new LedIntakeRunningCommand(m_ledSubsystem);




  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    // we support all drive modes - any inversion of the joysticks happens in the drivetrain code
    m_drivetrainSubsystem.setDefaultCommand(
        Commands.run(
            () -> m_drivetrainSubsystem.driveInputs(m_driverController.getLeftY(),
                                                    m_driverController.getRightY(),
                                                    m_driverController.getRightX()),
                                                    m_drivetrainSubsystem));

    m_ledSubsystem.setDefaultCommand(m_ledBounceCommand);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {

    m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());

    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());

    // schedule the shoot command when the Xbox controller's Y button is pressed
    m_driverController.y().onTrue(m_shootCommand);

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
