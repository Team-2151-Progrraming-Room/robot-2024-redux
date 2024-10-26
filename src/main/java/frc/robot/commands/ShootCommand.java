// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.ShooterAngleSubsystem;

import frc.robot.Constants.ShooterConstants;

import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;

import java.util.function.DoubleSupplier;




public class ShootCommand extends Command {
    
    ShooterSubsystem        m_shooterSubsystem;

    ShooterAngleSubsystem   m_shooterAngleSubsystem;

    DoubleSupplier          m_rangeSupplier;

    public ShootCommand(ShooterSubsystem shooterSub, ShooterAngleSubsystem shooterAngleSub, DoubleSupplier rangeSupplier) {
    
        m_rangeSupplier = rangeSupplier;            // the actual ranmging call gets made very late in the low level shooting routines

        m_shooterSubsystem      = shooterSub;
        m_shooterAngleSubsystem = shooterAngleSub;

        addRequirements(shooterSub);
        addRequirements(shooterAngleSub);
    }



    public Command getShootCommand() {

        return Commands.sequence(
              
        // right now we don't do any robot alignment to the target nor do we prevent the driver
        // from moving the robot once it's aligned
        //
        // if we wanted a totally automated shooting sequence, we'd need to incorporate those aspects

            Commands.print("Shooting sequence starting"),

            Commands.print("Setting range..."),

            Commands.parallel(
            // for each of the range-related commands, we pass the range supplier function which is called in the low level routines
            // to get the range not at the time the command is created but when it is actually run
            m_shooterSubsystem.setShooterSpeedByRangeCommand(m_rangeSupplier),
            m_shooterAngleSubsystem.setShooterAngleByRangeCommand(m_rangeSupplier)
            ),

            Commands.print("Ranges set"),

            Commands.print("Stabilizing..."),

            // eventually we'll run the stabilization routines in parallel with a timeout of kShooterStabilizeTime

//              m_shooterSubsystem.stabilizeShooterSpeedCommand(),
            m_shooterAngleSubsystem.stabilizeShooterAngleCommand(),
            
            Commands.print("Stabilized"),

            m_shooterSubsystem.kickerMotorOnCommand(),

            Commands.print("Kicking..."),


            // for now, we're just letting the kicker run for some period of time
            //
            // all we want is to feed the note into the shooter so it actually gets launched
            //
            // doing this is generally fine but we could optimize this by watching the note sensor and only
            // running it until the note clears the sensor (at which point we assume it has been launched)
            //
            // this would make this command slightly more responsive and not *have* to run for the entire
            // kKickerRunTime period

            Commands.waitSeconds(ShooterConstants.kKickerRunTime.in(Seconds)),

            Commands.print("Shutting down..."),

            m_shooterSubsystem.shooterMotorOffCommand(),
            m_shooterSubsystem.kickerMotorOffCommand(),

            Commands.print("Shooting sequence completed")

    );
  }
}

