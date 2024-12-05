package frc.robot.commands;


import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;


//Super Simple Command that is going to be used to show off source control.
public class SpinInCircleCommand extends Command{
    private final DriveSubsystem m_driveSubsystem;
    private final double spinFactor;

    public SpinInCircleCommand(DriveSubsystem subsystem, double spinRate){
        m_driveSubsystem = subsystem;
        spinFactor = spinRate;
        addRequirements(subsystem);
    }
    

    @Override
    public void execute(){
        m_driveSubsystem.driveInputs(0,0,spinFactor);
    }

    public void end(boolean interrupted){
        System.out.println("Spin Command has finished!");
    }
}  
