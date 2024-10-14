// High-level shooter commands

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;


public class ShootCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public ShootCommand(ShooterSubsystem Shooter, Measure<Distance> range) {
    m_subsystem = Shooter;

    addRequirements(Shooter);
  }


  @Override
  public void initialize() {

    m_subsystem.setShooterAngleByRange(Meters.of(10.0));
    System.out.println("PASS TARGET RANGE TO \".setShooterAngleByRange()\" in \"SetShooterAngleCommand\"");
  }


  @Override
  public void execute() {}


  @Override
  public void end(boolean interrupted) {}


  @Override
  public boolean isFinished() {
    return true;
  }
}
