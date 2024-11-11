
package frc.robot.commands;

import frc.robot.subsystems.LedSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.LedConstants;

public class LedIntakeRunningCommand extends Command {

    private final LedSubsystem m_ledSubsystem;

    private static final int m_paceFactor              = 2;     // the execute routines runs every 20ms so this is a factor to slow us down
    private static int       m_paceCount;
    private static int       m_intakeLedIndex;


    public LedIntakeRunningCommand(LedSubsystem subsystem) {
        m_ledSubsystem = subsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(subsystem);
    }



    @Override
    public void initialize() {

        m_paceCount       = m_paceFactor;
        m_intakeLedIndex  = 0;

        m_ledSubsystem.setAllLedsHSV(LedConstants.kLedIntakeBackgroundH, LedConstants.kLedIntakeBackgroundS, LedConstants.kLedIntakeBackgroundV);
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

        // we just run the Intake a color from one end to the other wrapping at the end
        //
        // we assume m_intakeIndex is in the right place for the last execution as it points to
        // the start of the non-background string
        //
        // the pattern is <shadow> <bright * 3> <shadow)

        // reset the first LED and then start marching through
        m_ledSubsystem.setLedHSV(m_intakeLedIndex, LedConstants.kLedIntakeBackgroundH, LedConstants.kLedIntakeBackgroundS, LedConstants.kLedIntakeBackgroundV);

        int ledCount = m_ledSubsystem.getNumOfLeds();       // we'll use this more than once so lets cache it

        // this is where the multi-LED string starts this go around so update the class-wide index starting variable
        
        m_intakeLedIndex = nextLedIndexWithWrap(m_intakeLedIndex, ledCount);

        m_ledSubsystem.setLedHSV(m_intakeLedIndex, LedConstants.kLedIntakeShadowH, LedConstants.kLedIntakeShadowS, LedConstants.kLedIntakeShadowV);

        // when we are dealing with the LEDs which are part of the pattern, we need to find the next with wrap
        // if needed but we don't want to update the class-wide variable

        int localIntakeIndex = nextLedIndexWithWrap(m_intakeLedIndex, ledCount);    // we use a local var to step through where we want to set colors

        m_ledSubsystem.setLedHSV(localIntakeIndex, LedConstants.kLedIntakePrimaryH, LedConstants.kLedIntakePrimaryS, LedConstants.kLedIntakePrimaryV);

        localIntakeIndex = nextLedIndexWithWrap(localIntakeIndex, ledCount);

        m_ledSubsystem.setLedHSV(localIntakeIndex, LedConstants.kLedIntakePrimaryH, LedConstants.kLedIntakePrimaryS, LedConstants.kLedIntakePrimaryV);

        localIntakeIndex = nextLedIndexWithWrap(localIntakeIndex, ledCount);

        m_ledSubsystem.setLedHSV(localIntakeIndex, LedConstants.kLedIntakePrimaryH, LedConstants.kLedIntakePrimaryS, LedConstants.kLedIntakePrimaryV);

        localIntakeIndex = nextLedIndexWithWrap(localIntakeIndex, ledCount);

        m_ledSubsystem.setLedHSV(m_intakeLedIndex, LedConstants.kLedIntakeShadowH, LedConstants.kLedIntakeShadowS, LedConstants.kLedIntakeShadowV);
    }

    

    private int nextLedIndexWithWrap(int index, int ledCount) {

        /**
         * nextLedIndexWithWrap
         * 
         * return the next LED index but wrap back to the start if we hit the last LED
         */

         index++;

         if (index >= ledCount) {
            return 0;
         } else {
            return index;
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
