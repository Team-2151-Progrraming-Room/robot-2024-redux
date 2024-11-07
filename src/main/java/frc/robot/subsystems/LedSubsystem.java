package frc.robot.subsystems;


import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


// our robot constants

import frc.robot.Constants.LedConstants;



public class LedSubsystem extends SubsystemBase {

/**
 * The LED subsystem provides all of the methods for driving the LEDs on the robot
 */

    private AddressableLED m_led;

    private AddressableLEDBuffer m_ledBuffer;



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
