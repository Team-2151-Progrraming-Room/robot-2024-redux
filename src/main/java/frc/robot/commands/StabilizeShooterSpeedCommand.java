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



public class StabilizeShooterSpeedCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;


  
  public StabilizeShooterSpeedCommand(ShooterSubsystem Shooter) {
    m_subsystem = Shooter;

    addRequirements(Shooter);
  }


  @Override
  public void initialize() {}


  @Override
  public void execute() {}


  @Override
  public void end(boolean interrupted) {}


  @Override
  public boolean isFinished() {
    return m_subsystem.atShooterSpeed();
  }
}
