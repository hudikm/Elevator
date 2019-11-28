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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


public class Singleton {

    public static final long SIMULATION_RESOLUTION = 50L;
    public static final int DELAY_MS = 5;
    public static final Logger logSystem = Logger.getLogger("SystemLog");
    public static final Logger logElevator = Logger.getLogger("ElevatorLog");
    public static final Logger serialLog = Logger.getLogger("SerialLog");
    private static final EventBus eventBus = new EventBus();
    private static final List<Integer> serialSpeedList = new ArrayList<>();

    private static Singleton ourInstance = new Singleton();

    static {
        serialSpeedList.add(9600);
        serialSpeedList.add(19200);
        serialSpeedList.add(38400);
        serialSpeedList.add(57600);
        serialSpeedList.add(115200);
        serialSpeedList.add(230400);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings settings = new Settings();

    private ByteBuffer RxBuffer = ByteBuffer.allocate(1000);
    private Timer timerRx;
    private ByteBuffer TxBuffer = ByteBuffer.allocate(1000);
    private Timer timerTx;

    private Singleton() {
    }

    public static List<Integer> getSerialSpeedList() {
        return serialSpeedList;
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static Singleton getInstance() {
        return ourInstance;
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

    public boolean isDEBUG_MODE() {
        return settings.isDEBUG_MODE();
    }

    public void setDEBUG_MODE(boolean DEBUG_MODE) {
        settings.setDEBUG_MODE(DEBUG_MODE);
    }

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
}

