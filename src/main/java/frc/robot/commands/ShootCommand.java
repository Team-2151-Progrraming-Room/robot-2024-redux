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

            Commands.parallel(

                // wait until the angle and shooter speed have stabilized with a timeout
                //
                // these commands will keep running until things stabilize or the timeout expires
                //
                // they should happen before the timeout during normal operation
                //
                // if they don't reach the target speed or angle, the probably should abort the command because it means
                // something went wrong - what, who knows?  Can the robot recover - who knows?
                //
                // right now the command just get cancelled when the timeout happens and the rest of the sequence continues

                m_shooterSubsystem.stabilizeShooterSpeedCommand(),
                m_shooterAngleSubsystem.stabilizeShooterAngleCommand()

            ).withTimeout(ShooterConstants.kShooterStabilizeTime.in(Seconds)),
            
            Commands.print("Stabilized"),

            m_shooterSubsystem.kickerMotorOnCommand(),

            Commands.print("Kicking..."),

            // for now, we're just letting the kicker run for some period of time
            //
            // all we want is to feed the note into the shooter so it actually gets launched
            //
            // doing this based on some amount of time is generally fine but we could optimize this by watching the
            // note sensor and only running it until the note clears the sensor (at which point we assume it has been launched)
            //
            // this would make this command slightly more responsive and not *have* to run for the entire
            // kKickerRunTime period and would reduce the run time of this command to the minimum it took
            // to spin up and set the shooter abngle and then to actually get the note clear of the robot.
            //
            // Note that we're assuming that once we the note sensor we can stop but we might actually need to have a
            // "short" delay so we don't stop things too soon - the routine to check the note will be running
            // at a 50Hz frequency.  This could result in us seeing the note sensor go clear and if the note doesn't
            // complete shooting within 20ms, we could in fact end up *not* actually completely shooting the note
            // but shutting down the shooter motor prematurely and either just "plopping" out the note as a result of
            // a winding down shooter motor or possibly even jamming the shooter with a partially shot note)
            //
            // That would not be "good".
            //
            // How would we deal with that?  Probably through some empirical testing.

            Commands.waitSeconds(ShooterConstants.kKickerRunTime.in(Seconds)),

            Commands.print("Note shot"),

            Commands.print("Shutting down..."),

            // since thes ecommands are both in the shooterSubsystem, we can't run them in parallel and need
            // to run them sequentially
            //
            // since they are basically one-shot operatiosn of setting a motor to 0, it's not like they will take
            // a lot of time anyway - they will basically complete "instantly"

            m_shooterSubsystem.shooterMotorOffCommand(),
            m_shooterSubsystem.kickerMotorOffCommand(),

            Commands.print("Shooting sequence completed")

            // at this point the motors for the shooter and the kicker would be off
            //
            // the angle is still at whatever it was at - we could make a command to lower the shooter to do
            // something like fit under the stage or just leave it so we don't have to move as much for the
            // next shot
            //
            // this would be something where we might work with the strategy / drive department to see how
            // we could best fit our game play plan
    );
  }
}

