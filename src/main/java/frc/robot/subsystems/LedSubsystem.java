package frc.robot.subsystems;


import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


// our robot constants

import frc.robot.Constants.LedConstants;
import frc.robot.commands.LedBounceCommand;



public class LedSubsystem extends SubsystemBase {

/**
 * The LED subsystem provides all of the methods for driving the LEDs on the robot
 */

    private AddressableLED m_led;

    private AddressableLEDBuffer m_ledBuffer;

    // all part of the pre-shoot sequence
    private static final int m_hueRange = LedConstants.kLedShooterSpinupStartH - LedConstants.kLedShooterSpinupEndH;
    private static final int m_hueStep  = m_hueRange / LedConstants.kNumOfLeds;
    private static final int m_offsetStep = LedConstants.kNumOfLeds / 20 + 1;       // march through al of them in about 20 cycles (1 sec)
    private int m_shootSpinupCurrentHue;
    private int m_shootSpinupLedCount;


    public LedSubsystem() {

        m_led = new AddressableLED(LedConstants.kLedPwmPort);

        m_ledBuffer = new AddressableLEDBuffer(LedConstants.kNumOfLeds);
        m_led.setLength(LedConstants.kNumOfLeds);

        // setLedsMaroon();
   
        m_led.start();
    }
    


    public int getNumOfLeds() {
        return m_ledBuffer.getLength();
    }



    // all the various color and effect routines do not ned to do a .setData() call
    //
    // the periodic routine for the LedSubsystem will do it for you

    public void setLedsOff() {

        for (int i = 0 ; i < m_ledBuffer.getLength() ; i++) {
   
            m_ledBuffer.setRGB(i, 0, 0, 0);
        }
    }



    public void setLedsWhite() {

        for (int i = 0 ; i < m_ledBuffer.getLength() ; i++) {
   
            m_ledBuffer.setRGB(i, 180, 180, 180);
        }
    }



    public void setLedsMaroon() {

        for (int i = 0 ; i < m_ledBuffer.getLength() ; i++) {
   
            m_ledBuffer.setHSV(i, 2, 64, 22);       // determined with google colo picker at https://www.google.com/search?q=color+picker
        }
    }



    public void setAllLedsRGB(int red, int green, int blue) {

/**
 * sets all of the LEDs in the string to the same passed RGB colors
 */ 

        for (int i = 0 ; i < m_ledBuffer.getLength() ; i++) {
   
            m_ledBuffer.setRGB(i, red, green, blue);
        }
    }



    public void setRangeLedsRGB(int index, int count, int red, int green, int blue) {

/**
 * sets a subset of the LEDs in the string to the same passed RGB colors starting at the 0-based index and setting count number of LEDs
 */ 
 
        for (int i = 0 ; i < count ; i++) {
   
            m_ledBuffer.setRGB(index + i, red, green, blue);
        }
    }



    public void setLedRGB(int index, int red, int green, int blue) {

/**
 * sets a specific LED in the string based on the 0-based index to the same passed RGB colors
 */ 
 
        m_ledBuffer.setRGB(index, red, green, blue);
    }



    public void setAllLedsHSV(int hue, int sat, int value) {

/**
 * sets all of the LEDs in the string to the same passed HSV colors
 */ 

        for (int i = 0 ; i < m_ledBuffer.getLength() ; i++) {
   
            m_ledBuffer.setHSV(i, hue, sat, value);
        }
    }



    public void setRangeLedsHSV(int index, int count, int hue, int sat, int value) {

/**
 * sets a subset of the LEDs in the string to the same passed HSV colors starting at the 0-based index and setting count number of LEDs
 */ 
 
        for (int i = 0 ; i < count ; i++) {
   
            m_ledBuffer.setHSV(index + i, hue, sat, value);
        }
    }



    public void setLedHSV(int index, int hue, int sat, int value) {

    /**
     * sets a specific LED in the string based on the 0-based index to the same passed HSV colors
     */ 
 
        m_ledBuffer.setHSV(index, hue, sat, value);
    }



    public boolean ledPostShootSequence() {

    /**
     * turns off one random LED each time through as part of the post shootiung LED sequence
     */

        int offLed = (int)(Math.random() * LedConstants.kNumOfLeds);       // pick a random LED to turn off (0 to kNumOfLeds - 1)

        setLedHSV(offLed, LedConstants.kLedGeneralBackgroundH, LedConstants.kLedGeneralBackgroundS, LedConstants.kLedGeneralBackgroundV);

        return false;       // this gets call from a wait(x).until() call with this being part of the until() decorator
    }



    private void initLedShootSequence() {

        m_shootSpinupCurrentHue  = LedConstants.kLedShooterSpinupStartH;

        m_shootSpinupLedCount = 1;

        setAllLedsHSV(0, 0, 0);     // turn all the LEDs off - the pre-shoot seq will turn them back on
    }



    public boolean ledPreShootSequence() {

        setRangeLedsHSV(0, m_shootSpinupLedCount, m_shootSpinupCurrentHue, 255, 255);

        m_shootSpinupCurrentHue -= m_hueStep;               // hue goes backwards from orange-ish to yellow red-ish
        m_shootSpinupLedCount   += m_offsetStep;

        // get setup for the next run
        if (m_shootSpinupCurrentHue < LedConstants.kLedShooterSpinupEndH) {
            m_shootSpinupCurrentHue = LedConstants.kLedShooterSpinupEndH;
        }

        if (m_shootSpinupLedCount > LedConstants.kNumOfLeds) {
            m_shootSpinupLedCount = LedConstants.kNumOfLeds;
        }

        return false;               // keep it going until the timeout
    }



/* Commands *************************************************************************
 ************************************************************************************/



  public Command LedIntakeLoadedCommand() {
    /**
     * Set the pattern indicating the intake has loaded a note
     */

    return runOnce(
        () -> {
          setAllLedsHSV(LedConstants.kLedIntakePrimaryH, LedConstants.kLedIntakePrimaryS, LedConstants.kLedIntakePrimaryV);
        });
  }



   public Command LedIntakeCancelledCommand() {
    /**
     * Set the LEDs to the background color indicating the intake has been cancelled
     */

    return runOnce(
        () -> {
          setAllLedsHSV(LedConstants.kLedIntakeBackgroundH, LedConstants.kLedIntakeBackgroundS, LedConstants.kLedIntakeBackgroundV);
        });
  }



  public Command LedShootCommand() {
    /**
     * Set the LEDs to the shoot color
     */

    return runOnce(
        () -> {
          setAllLedsHSV(LedConstants.kLedShooterShotH, LedConstants.kLedShooterShotS, LedConstants.kLedShooterShotV);
        });
  }



  public Command LedPostShootCleanupCommand() {
    /**
     * Set the LEDs to the post-shoot color
     */

    return runOnce(
        () -> {
          setAllLedsHSV(LedConstants.kLedShooterBackgroundH, LedConstants.kLedShooterBackgroundS, LedConstants.kLedShooterBackgroundV);
        });
  }



  public Command LedPreShootInitCommand() {
    /**
     * setup for the shooting spinup
     */

     return runOnce(
             () -> {
          initLedShootSequence();
        });
  }


  


/* Periodics ************************************************************************************
 ************************************************************************************************/

  @Override
  public void periodic() {

    m_led.setData(m_ledBuffer);
 }



  @Override
  public void simulationPeriodic() {

  }
}
