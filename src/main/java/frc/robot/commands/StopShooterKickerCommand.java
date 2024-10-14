// StopShooterKickerCommand
//
// All we have to do is stop the shooter kicker in the intialization routine and return true
// from as finished because we are finished after that

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;



public class StopShooterKickerCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;



  public StopShooterKickerCommand(ShooterSubsystem Shooter) {
    m_subsystem = Shooter;

    addRequirements(Shooter);
  }


  @Override
  public void initialize() {
    m_subsystem.kickerMotorOff();
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
