// Default command runs the drivetrain
//
// Other commands can override it as needed for things like targetting asnd other automation

package frc.robot.commands;

import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;


public class DefaultDriveCommand extends Command {
  private final DriveSubsystem m_drive;
  private final DoubleSupplier m_leftY;
  private final DoubleSupplier m_rightY;
  private final DoubleSupplier m_rightX;

  /**
   * @param subsystem The drive subsystem this command will run on.
   * @param leftY Left Y axis
   * @param rightY Right Y axis
   * @param rightX Right X axis
   * 
   * That way we can get all pertinent control input and handle all drive modes for experimentation by the drive team
   */
  public DefaultDriveCommand(DriveSubsystem subsystem, DoubleSupplier leftY, DoubleSupplier rightY, DoubleSupplier rightX) {
    m_drive = subsystem;

    m_leftY = leftY;
    m_rightY = rightY;
    m_rightX = rightX;

    addRequirements(m_drive);
  }

  @Override
  public void execute() {
    m_drive.driveInputs(m_leftY.getAsDouble(), m_rightY.getAsDouble(), m_rightX.getAsDouble());
  }
}

