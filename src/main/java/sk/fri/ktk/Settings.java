package sk.fri.ktk;

public class Settings {
    public int NEW_BAUD_RATE = 230400;
    public boolean isLINK_NOISE = true;
    public double propabilityRate = 0.01;
    public boolean emergencyBreak = false;
    public boolean sensorSwitch = false;
    public boolean watchDog = false;
    public String lastOpenComport;
    public boolean DEBUG_MODE = false;

    public Settings() {
    }

    public boolean isDEBUG_MODE() {
        return this.DEBUG_MODE;
    }

    public void setDEBUG_MODE(boolean DEBUG_MODE) {
        this.DEBUG_MODE = DEBUG_MODE;
    }
}