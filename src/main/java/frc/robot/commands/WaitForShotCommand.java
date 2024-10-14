// Wait for Shot
//
// All we do is watch the sensor on the intake - if it clears, the note is gone and isFinished()
// should return true - otherwise we continue by returning false
//
// for now, we just return true from isFinished() and print a message in the initialize() routine to remind
// we need to tie this together.  There are also some other code sections in initialize that need to be
// uncommented once we go live.
//
// Most of the "shooting" command depend on the Shooter subsystem - this depends on the Intake subsystem
// since that's who own the note detection sensor
//
// we might not need to make this a seperate command and we could possibly do this with a .until condition
// sort of like OurCommand.until( ! m_intakeSubsystem.getNoteLoadedStatus())
//
// That's a made up method right now but it's based on the assumption that getNoteLoadedStatus() returns true when
// a note is detected a note is detected and false when a note is no longer detected.  It would be useful to detect
// a note having been successfully loaded from the intake as well as a load being "unloaded" (shot) by the shooter.

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
