
package frc.robot.commands;

import frc.robot.subsystems.LedSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class LedBounceCommand extends Command {

    private final LedSubsystem m_ledSubsystem;

    private static final int[] m_primaryBounceColor    = {255, 0, 0 };
    private static final int[] m_shadowBounceColor     = {30, 0, 0};
    private static final int[] m_backgroundBounceColor = {20, 20, 20};

    private static final int m_paceFactor              = 4;     // the execute routines runs every 20ms so this is a factor to slow us down
    private static int       m_paceCount;
    private static int       m_bounceIndex;
    private static int       m_bounceDirection;


    public LedBounceCommand(LedSubsystem subsystem) {
        m_ledSubsystem = subsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(subsystem);
    }



    // Called when the command is initially scheduled.
    @Override
    public void initialize() {

        m_paceCount       = m_paceFactor;
        m_bounceIndex     = 0;
        m_bounceDirection = 1;

        m_ledSubsystem.setAllLedsRGB(m_backgroundBounceColor[0],
                                     m_backgroundBounceColor[1],
                                     m_backgroundBounceColor[2]);

        m_ledSubsystem.setLedRGB(0, m_shadowBounceColor[0],
                                    m_shadowBounceColor[1],
                                    m_shadowBounceColor[2]);
                                
        m_ledSubsystem.setLedRGB(1, m_primaryBounceColor[0],
                                    m_primaryBounceColor[1],
                                    m_primaryBounceColor[2]);
                                
        m_ledSubsystem.setLedRGB(2, m_shadowBounceColor[0],
                                    m_shadowBounceColor[1],
                                    m_shadowBounceColor[2]);
    }



    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        // slow us down

        m_paceCount--;

        if (m_paceCount > 0 ) {
            return;                     // don't do anyting - yet...
        }

        m_paceCount = m_paceFactor;     // reset counter and do our thing

        // we just bounce a color back and forth from one end to the other

        // reset from what we're showing now

        m_ledSubsystem.setRangeLedsRGB(m_bounceIndex, 3,
                                       m_backgroundBounceColor[0],
                                       m_backgroundBounceColor[1],
                                       m_backgroundBounceColor[2]);

        m_bounceIndex += m_bounceDirection;     // handles either direction because direction changes sign

        if (m_bounceIndex < 0) {
            m_bounceIndex     = 1;
            m_bounceDirection = 1;
        }

        if (m_bounceIndex > (m_ledSubsystem.getNumOfLeds() - 3)) {
            m_bounceIndex     = m_ledSubsystem.getNumOfLeds() - 3 - 1;
            m_bounceDirection = -1; 
        }
        
        // set bounce colors
        
        m_ledSubsystem.setLedRGB(m_bounceIndex,
                                 m_shadowBounceColor[0],
                                 m_shadowBounceColor[1],
                                 m_shadowBounceColor[2]);

        m_ledSubsystem.setLedRGB(m_bounceIndex + 1,
                                 m_primaryBounceColor[0],
                                 m_primaryBounceColor[1],
                                 m_primaryBounceColor[2]);

        m_ledSubsystem.setLedRGB(m_bounceIndex + 2,
                                 m_shadowBounceColor[0],
                                 m_shadowBounceColor[1],
                                 m_shadowBounceColor[2]);
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
