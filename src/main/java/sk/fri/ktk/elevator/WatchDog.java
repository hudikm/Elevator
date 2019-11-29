/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.EmergencyPaket;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class WatchDog
extends Element
implements Runnable {
    private static final long TIMEOUT_TIME = 1500L;
    public static final int RESET_CMD = 1;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private boolean isClean = true;
    private boolean fired = false;
    private boolean enabled = false;

    public WatchDog(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
    }

    public boolean isFired() {
        return this.fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.startWatchdog();
            this.isClean = true;
        } else {
            this.stopWatchDog();
        }
    }

    private void startWatchdog() {
        this.scheduledFuture = this.scheduler.scheduleAtFixedRate(this, 0L, 2000L, TimeUnit.MILLISECONDS);
    }

    private void stopWatchDog() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
    }

    public synchronized boolean isClean() {
        return this.isClean;
    }

    public synchronized void setClean(boolean clean) {
        this.isClean = clean;
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() != null) {
            serialCommPacket.getData().rewind();
            if (serialCommPacket.getData().get() == 1) {
                if (this.isFired()) {
                    this.setFired(false);
                    this.setEnabled(false);
                    this.setEnabled(true);
                    this.getUi().updateUI(this);
                    Singleton.logElevator.fine("Watchdog: restart" );
                }
            } else {
                this.setClean(true);
                Singleton.logElevator.fine("Watchdog: reset timer!");
            }
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>WATCHDOG TIMER</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Watchdog timer is an electronic timer that is used to detect and recover from computer malfunctions. During normal operation, the computer regularly resets the watchdog timer to prevent it from elapsing, or \"timing out\".  If, due to a hardware fault or program error, the computer fails to reset the watchdog, the timer will elapse and generate a timeout signal. The timeout signal is used to initiate corrective action or actions. The corrective actions typically include placing the computer system in a safe state and restoring normal system operation. </p>\n<h4>TimeOut time:</h4>\n<p>" + 2000L + " ms</p>\n<p>Commands:</p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Command&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data In&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data Out&nbsp;(response)</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x01</td>\n<td style=\"text-align: center;\">Reset watchdog timer after \"timeing out\"(restore watchdog timer to initial state))</td>\n<td style=\"text-align: center;\">Byte </td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> else </td>\n<td style=\"text-align: center;\"> Regularly resets the watchdog timer to prevent it from elapsing. </td>\n<td style=\"text-align: center;\">Byte </td>\n</tr>\n</tbody>\n</table>";
    }

    @Override
    public void run() {
        if (this.enabled) {
            if (!this.isClean) {
                this.setFired(true);
                Singleton.getInstance().settings.emergencyBreak = true;
                this.eventBus.post((Object)new EmergencyPaket());
                this.getUi().updateUI(this);
            }
            this.setClean(false);
        }
    }
}

