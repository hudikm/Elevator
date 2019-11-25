/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk;

import com.google.common.eventbus.EventBus;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


public class Singleton {
    public static final long SIMULATION_RESOLUTION = 50L;
    private static final EventBus eventBus = new EventBus();
    public static final int DELAY_MS = 5;
    public static final int NEW_BAUD_RATE = 230400;

    private static Singleton ourInstance = new Singleton();

    private boolean DEBUG_MODE = false;
    public boolean isLINK_NOISE = true;
    public double propabilityRate = 0.01;
    public boolean emergencyBreak = false;
    public boolean sensorSwitch = false;
    public String lastOpenComport;

    public static final Logger logSystem = Logger.getLogger("SystemLog");
    public static final Logger logElevator = Logger.getLogger("ElevatorLog");
    public static final Logger serialLog = Logger.getLogger("SerialLog");

    private Singleton() {
    }


    private ByteBuffer RxBuffer = ByteBuffer.allocate(1000);
    private Timer timerRx;

    private ByteBuffer TxBuffer = ByteBuffer.allocate(1000);
    private Timer timerTx;


    private class SerialLogTaskRX extends TimerTask {
        public void run() {
            synchronized (RxBuffer) {
                if (RxBuffer.position() == 0) return;
                byte[] outBuff = new byte[RxBuffer.capacity() - RxBuffer.remaining()];
                RxBuffer.rewind();
                RxBuffer.get(outBuff);
                RxBuffer.rewind();
                Singleton.serialLog.info("Rx: " + SerialCommPacket.bytesToHex(outBuff));

            }
        }
    }

    private class SerialLogTaskTX extends TimerTask {
        public void run() {
            synchronized (TxBuffer) {
                if (TxBuffer.position() == 0) return;
                byte[] outBuff = new byte[TxBuffer.capacity() - TxBuffer.remaining()];
                TxBuffer.rewind();
                TxBuffer.get(outBuff);
                TxBuffer.rewind();
                Singleton.serialLog.info("Tx: " + SerialCommPacket.bytesToHex(outBuff));

            }
        }
    }

    public void SerialLoggerRX(byte[] log) {
        SerialLogTaskTX serialLogTaskTX = null;
        synchronized (TxBuffer) {
            if (TxBuffer.position() != 0) {
                if (timerTx != null) timerTx.cancel();
                serialLogTaskTX = new SerialLogTaskTX();
            }
        }
        if (serialLogTaskTX != null)
            serialLogTaskTX.run();

        if (timerRx != null)
            timerRx.cancel();
        timerRx = new Timer("Timer");
        synchronized (RxBuffer) {
            RxBuffer.put(log);
            timerRx.schedule(new SerialLogTaskRX(), DELAY_MS);
        }

    }

    public void SerialLoggerTX(byte[] log) {
        SerialLogTaskRX serialLogTaskRX = null;
        synchronized (RxBuffer) {
            if (RxBuffer.position() != 0) {
                if (timerRx != null) timerRx.cancel();
                serialLogTaskRX = new SerialLogTaskRX();
            }
        }
        if (serialLogTaskRX != null)
            serialLogTaskRX.run();

        if (timerTx != null)
            timerTx.cancel();
        timerTx = new Timer("TimerT");
        synchronized (TxBuffer) {
            TxBuffer.put(log);
            timerTx.schedule(new SerialLogTaskTX(), 50);
        }

    }


    public static EventBus getEventBus() {
        return eventBus;
    }

    public static Singleton getInstance() {
        return ourInstance;
    }

    public boolean isDEBUG_MODE() {
        return this.DEBUG_MODE;
    }

    public void setDEBUG_MODE(boolean DEBUG_MODE) {
        this.DEBUG_MODE = DEBUG_MODE;
    }
}

