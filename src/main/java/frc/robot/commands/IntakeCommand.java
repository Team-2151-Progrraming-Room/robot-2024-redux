package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.LedSubsystem;
import frc.robot.subsystems.ShooterAngleSubsystem;

import frc.robot.Constants.*;

import static edu.wpi.first.units.Units.*;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class IntakeCommand extends Command {
    IntakeSubsystem        m_intakeSubsystem;

    LedSubsystem            m_ledSubsystem;

    BooleanSupplier         m_stabilizeIntakeSpeedCheck;

    public final Command m_ledIntakeRunningCommand              = new LedIntakeRunningCommand(m_ledSubsystem);

    public IntakeCommand(IntakeSubsystem intakeSub, LedSubsystem ledSub) {
    

        m_intakeSubsystem      = intakeSub;
        m_ledSubsystem          = ledSub;

        addRequirements(intakeSub);
        addRequirements(ledSub);
    }

    public Command getIntakeCommand() {
    return Commands.sequence(
        // the first thing that will happen when this command is called is the intake motor will begin to spin
        Commands.parallel(m_intakeSubsystem.intakeMotorOnCommand(), m_ledIntakeRunningCommand),

        //the motor will continue to spin until the sensor which senses whether there
        //is a game piece or not detects the gamepiece within the robot
        Commands.until(intakeSubstem.checkGamePieceLoadedCommand()),

        //when this occurs, the intake motor will turn off automatically,
        //and the LED strand will simultaneously show that a piece is loaded
        Commands.parallel(
            m_intakeSubsystem.intakeMotorOffCommand(),
            m_ledSubSystem.LedIntakeLoadedCommand()
        )
    );
    }


}
