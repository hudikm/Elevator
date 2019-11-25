/*
 * Decompiled with CFR 0.145.
 */
package sk.fri.ktk.elevator.Packets;

public class MotorPacket {

    private double speed;
    private int motorAddress;

    public MotorPacket(double speed, int motorAddress) {
        this.speed = speed;
        this.motorAddress = motorAddress;
    }

    public double getSpeed() {
        return speed;

    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getMotorAddress() {
        return this.motorAddress;
    }

    public void setMotorAddress(int motorAddress) {
        this.motorAddress = motorAddress;
    }

}

