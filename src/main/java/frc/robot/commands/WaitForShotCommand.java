// Wait for Shot
//
// All we do is watch the sensor on the intake - if it clears, the note is gone and isFinished()
// should return true - otherwise we continue bvy returning false

//import frc.robot.subsystems.IntakeSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class WaitForShotCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  // private final Intake Subsystem m_subsystem;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public WaitForShotCommand(/* IntakeSubsystem Shooter */) {
    // m_subsystem = Intake;
    // Use addRequirements() here to declare subsystem dependencies.
    //addRequirements(Intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("WaitForShotCommand:  Tie to Intake subsystem when available!!!");
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
