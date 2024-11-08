// High-level shooter commands

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;


import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;

import frc.robot.Constants.ShooterConstants;




public class RobotCommands {

  /*
  public static Command ShootCommand(ShooterSubsystem shooter, Measure<Distance> range) {

    return Commands.sequence(
              
              shooter.setShooterRangeCommand(range),

              Commands.parallel(
                shooter.stabilizeShooterSpeedCommand(),
                shooter.stabilizeShooterAngleCommand()
              ).withTimeout(ShooterConstants.kShooterStabilizeTime.in(Seconds)),

              shooter.shootNoteCommand()
    );
  }
  */
}
