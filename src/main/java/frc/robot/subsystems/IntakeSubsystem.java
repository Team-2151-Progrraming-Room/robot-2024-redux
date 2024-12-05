package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.util.sendable.SendableBuilder;

// SparkMax imports - these come from REV Robotics

import com.revrobotics.*;
import com.revrobotics.CANSparkLowLevel.MotorType;


// our robot constants

import frc.robot.Robot;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Utilities;



import java.util.function.DoubleSupplier;


public class IntakeSubsystem extends SubsystemBase{

    private final CANSparkMax m_intakeMotor = new CANSparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);

    //limit switch sensor that currently doesn't exist
    private final DigitalInput gamepieceLoadedLimitSwitch = new DigitalInput(0);

    public IntakeSubsystem(){
        m_IntakeMotor.restoreFactoryDefaults();

        m_intakeMotor.stopMotor();

        m_intakeMotor.setSmartCurrentLimit(IntakeConstants.kIntakeMotorCurrentLimit); 
    }

    public void intakeMotorOn() {

        m_intakeMotor.set(IntakeConstants.kIntakeSpeed);
      }
  
      public void intakeMotorOff() {
  
        m_intakeMotor.stopMotor();
      }

      public boolean checkGamePieceLoaded(){
        if (gamepieceLoadedLimitSwitch.get();) {
          return true;
        }
        
        return false;
      }

      private void updateDashboard(){

      }



      //Commands
      public Command intakeMotorOnCommand() {

        return runOnce(
            () -> {
              intakeMotorOn();
            });
      }
    
      public Command intakeMotorOffCommand() {
    
        return runOnce(
            () -> {
              intakeMotorOff();
            });
      }

      public Command checkGamePieceLoadedCommand() {

        return run(() -> checkGamePieceLoaded());
      }



      //periodics
      @Override
  public void periodic() {

    updateDashboard();
  }

  @Override
  public void simulationPeriodic() {

  }
}
