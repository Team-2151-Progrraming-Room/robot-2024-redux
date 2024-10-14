// Set Shooter Angle
//
// All we do is start the shooter towards the correct angle according to the range
//
// The shooter angle would be managed by a PID loop with no other intervention after starting it
//
// After that, isFinished() just returns true because we are

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;



public class SetShooterAngleCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;

  private Measure<Distance> m_range;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public SetShooterAngleCommand(ShooterSubsystem Shooter, Measure<Distance> range) {

    m_subsystem = Shooter;

    m_range = range;

    addRequirements(Shooter);
  }


  @Override
  public void initialize() {

    m_subsystem.setShooterAngleByRange(m_range);
    System.out.println("PASS TARGET RANGE TO \".setShooterAngleByRange(" + m_range.in(Meters) + " (meters))\" in \"SetShooterAngleCommand\"");
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
