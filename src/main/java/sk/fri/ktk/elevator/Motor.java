/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  javafx.application.Platform
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.CabineMovePacket;
import sk.fri.ktk.elevator.Packets.MotorPacket;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class Motor
        extends Element {
    public static final byte MOTOR_STOP = 1;
    public static final byte MOTOR_RUN = 2;
    public static final byte MOTOR_INFO = 3;
    public static final int PERIOD = 50;
    public static final double TIMECONSTANT = 0.9;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture scheduledFuture;
    CurrentState currentState = new CurrentState();
    Runnable motorSimulation = new Runnable() {
        private double aDistance;
        double last_speed = 0.0;

        @Override
        public void run() {
            if (Singleton.getInstance().emergencyBreak) {
                Motor.this.currentState.setMaxSpeed(0);
            }

            double newMaxSpeed = Motor.this.currentState.getMaxSpeed();
            double currentSpeed = Motor.this.currentState.getCurrentSpeed();

            currentSpeed = TIMECONSTANT * currentSpeed + (1.0 - TIMECONSTANT) * newMaxSpeed;

            if (Math.abs(Motor.this.currentState.getCurrentSpeed()) > Math.abs(currentSpeed) && Math.abs(currentSpeed) < 2) {
                currentSpeed = 0.0;
            }
            Motor.this.currentState.setCurrentSpeed(currentSpeed);
            Motor.this.eventBus.post(new MotorPacket(currentSpeed, Motor.this.address));


//            this.aDistance = Math.round(currentSpeed * (double) Singleton.SIMULATION_RESOLUTION / 1000.0);
//
//            CurrentState currentState = Motor.this.currentState;
//            currentState.distance = Math.round(currentState.distance + this.aDistance);

            if (this.last_speed != currentSpeed) {
                Platform.runLater(() -> Motor.this.ui.updateUI(null));
            } else {
                scheduledFuture.cancel(true);
            }
            this.last_speed = currentSpeed;

//            if (Math.abs(this.aDistance) > 0.1) {

//            Motor.this.eventBus.post(new MotorPacket(currentSpeed, Motor.this.address));
//            }

        }
    };

    @Subscribe
    public void cabineMove(CabineMovePacket cabineMovePacket) {
        this.currentState.distance = cabineMovePacket.getPosition();

    }

    public Motor(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
        this.setSuperClassName(this.getClass().getSimpleName());
        scheduledFuture = this.scheduler.scheduleAtFixedRate(this.motorSimulation, 0L, Singleton.SIMULATION_RESOLUTION, TimeUnit.MILLISECONDS);

    }

    public CurrentState getCurrentState() {
        return this.currentState;
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() == null) {
            return;
        }
        serialCommPacket.getData().rewind();
        switch (serialCommPacket.getData().get()) {
            case 1: { //STOP
                this.currentState.setMaxSpeed(0);
                Singleton.logElevator.info("Motor stop");
                break;
            }
            case 2: { //NEW SPEED
                int anInt = serialCommPacket.getData().getInt();
                if (Math.abs((double) Math.signum(anInt) - Math.signum(this.currentState.getCurrentSpeed())) == 2.0) {
                    anInt = 0;
                }
                if (anInt > 100) {
                    anInt = 100;
                }
                if (anInt < -100) {
                    anInt = -100;
                }
                Singleton.logElevator.info("Motor set new speed: " + anInt);
                this.currentState.setMaxSpeed(anInt);
                break;
            }
            case 3: { //SEND DINSTANCE
                SerialCommPacket serialCommPacketSend = new SerialCommPacket();
                serialCommPacketSend.setAddress(SerialCommPacket.SERIAL_LINK);
                serialCommPacketSend.setSenderAddr(this.address);
                serialCommPacketSend.setData(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) this.currentState.distance));
                this.eventBus.post(serialCommPacketSend);
            }
            case 4: { //SEND CURRENT MOTOR SPEED
                SerialCommPacket serialCommPacketSend = new SerialCommPacket();
                serialCommPacketSend.setAddress(SerialCommPacket.SERIAL_LINK);
                serialCommPacketSend.setSenderAddr(this.address);
                serialCommPacketSend.setData(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat((float) this.currentState.getCurrentSpeed()));
                this.eventBus.post(serialCommPacketSend);
            }
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Motor</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Motor moves the elevator cabin up and down according to the given commands. The range of motor speed is from -100 to 100. A negative value represents the down movement and vice-versa. Motor also has encoder element that counts motor movement.</p>\n<p>Commands:</p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Command&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data In&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data Out&nbsp;(response)</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x" + Integer.toHexString(1) + "</td>\n<td style=\"text-align: center;\">&nbsp;stop</td>\n<td style=\"text-align: center;\">null</td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x" + Integer.toHexString(2) + "</td>\n<td style=\"text-align: center;\">movement</td>\n<td style=\"text-align: center;\">Type: 32bit sign integer (range <-100,100>) </td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x" + Integer.toHexString(3) + "</td>\n<td style=\"text-align: center;\">Send motor encoder count</td>\n<td style=\"text-align: center;\">null </td>\n<td style=\"text-align: center;\">Type: double </td>\n</tr>\n</tbody>\n</table>";
    }

    public class CurrentState {
        int maxSpeed = 0;
        private double currentSpeed = 0.0;
        private double distance = 0.0;

        public synchronized double getCurrentSpeed() {
            return this.currentSpeed;
        }

        public synchronized void setCurrentSpeed(double currentSpeed) {
            this.currentSpeed = currentSpeed;
        }

        public synchronized double getMaxSpeed() {
            return this.maxSpeed;
        }

        public synchronized void setMaxSpeed(int maxSpeed) {
            this.maxSpeed = maxSpeed;
            if (scheduledFuture.isDone())
                scheduledFuture = scheduler.scheduleAtFixedRate(motorSimulation, 0L, Singleton.SIMULATION_RESOLUTION, TimeUnit.MILLISECONDS);
        }
    }

}

