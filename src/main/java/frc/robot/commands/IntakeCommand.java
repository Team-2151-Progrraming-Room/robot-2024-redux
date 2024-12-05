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

    public IntakeCommand(IntakeSubsystem intakeSub, LedSubsystem ledSub, BooleanSupplier speedSupplier) {
    
        m_stabilizeIntakeSpeedCheck = speedSupplier;

        m_intakeSubsystem      = intakeSub;
        m_ledSubsystem          = ledSub;

        addRequirements(intakeSub);
        addRequirements(ledSub);
    }

    public Command getIntakeCommand() {
    return Commands.sequence(
        Commands.parallel(m_intakeSubsystem.intakeMotorOnCommand(), m_ledIntakeRunningCommand),

        Commands.until(intakeSubstem.checkGamePieceLoadedCommand()),

        Commands.parallel(
            m_intakeSubsystem.intakeMotorOffCommand(),
            m_ledSubSystem.LedIntakeLoadedCommand()
        ),

        Commands.until(!intakeSubstem.checkGamePieceLoadedCommand()),

        m_ledSubSystem.LedIntakeUnloadedCommand()
    );
    }

}
