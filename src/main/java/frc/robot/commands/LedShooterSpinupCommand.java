
package frc.robot.commands;

import frc.robot.subsystems.LedSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.LedConstants;

public class LedShooterSpinupCommand extends Command {

    private final LedSubsystem m_ledSubsystem;

    private static final int m_paceFactor  = 2;     // the execute routines runs every 20ms so this is a factor to slow us down
    private static int       m_paceCount;

    private static int       m_spinupHue   = LedConstants.kLedShooterSpinupStartH;  // starting color


    public LedShooterSpinupCommand(LedSubsystem subsystem) {
        m_ledSubsystem = subsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(subsystem);
    }



    // Called when the command is initially scheduled.
    @Override
    public void initialize() {

        m_paceCount       = m_paceFactor;

        m_ledSubsystem.setAllLedsHSV(LedConstants.kLedShooterBackgroundH, LedConstants.kLedShooterBackgroundS, LedConstants.kLedShooterBackgroundV);
    }



    @Override
    public void execute() {

        // slow us down

        m_paceCount--;

        if (m_paceCount > 0 ) {
            return;                     // don't do anyting - yet...
        }

        m_paceCount = m_paceFactor;     // reset counter and do our thing


        m_ledSubsystem.setAllLedsHSV(m_spinupHue, LedConstants.kLedShooterSpinupS, LedConstants.kLedShooterSpinupV);

        if (m_spinupHue > LedConstants.kLedShooterSpinupEndH) {
            m_spinupHue--;          // yellow to orangish-red
        }
    }



    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {}



    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
