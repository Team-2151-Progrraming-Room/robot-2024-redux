// Spin Up Shooter
//
// All we do is start the shooter spinning according to the range
//
// The shooter speed is managed by a PID loop with no other intervention after starting it
//
// After that, isFinished() just returns true because we are

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class SpinUpShooterCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public SpinUpShooterCommand(ShooterSubsystem Shooter) {
    m_subsystem = Shooter;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(Shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_subsystem.setShooterSpeedByMetersDistance(10.0);
    System.out.println("PASS TARGET RANGE TO \".setShooterSpeedByMetersDistance()\" in \"SpinUpShooterCommand\"");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
