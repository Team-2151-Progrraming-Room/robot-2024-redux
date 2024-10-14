// StartShooterKickerCommand
//
// All we have to do is start the shooter kicked in the intialization routine and return true
// from as finished because we are finished after that

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;



public class StartShooterKickerCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;



  public StartShooterKickerCommand(ShooterSubsystem Shooter) {
    m_subsystem = Shooter;

    addRequirements(Shooter);
  }


  @Override
  public void initialize() {
    m_subsystem.kickerMotorOn();
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
