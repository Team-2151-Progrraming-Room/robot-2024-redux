// Stabilze Shooter Speed
//
// All we do is check whether we are within the shooter speed tolerance using the
// atShooterspeed() routine in the Shooter subsystem and return that from the isFinished() routine
//
// The shooter speed is managed by a PID loop with no other intervention after starting it
//
// No other processing for this command is needed

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class StabilizeShooterSpeedCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public StabilizeShooterSpeedCommand(ShooterSubsystem Shooter) {
    m_subsystem = Shooter;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(Shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_subsystem.atShooterSpeed();
  }
}
