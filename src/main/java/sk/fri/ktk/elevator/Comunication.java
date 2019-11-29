
package sk.fri.ktk.elevator;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.util.Pair;
import org.apache.commons.lang3.SystemUtils;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.AckPacket;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

import java.util.*;
import java.util.stream.Collectors;


public class Comunication implements SerialPortDataListener {
    private EventBus eventBus;
    //private SerialPort serialPort;
    private com.fazecast.jSerialComm.SerialPort serialPort;
    private Protocol protocol;
    Random rand = new Random();
    final SerialSender serialSender;

    public class SerialSender extends Thread {

        public static final int MAX_NUM_OF_RETRANSMISSION = 8;
        public static final int ACK_RETRANSMIT_WAIT_TIME = 5; //30ms

        private Vector<SerialCommPacket> outQueue = new Vector();
        private Vector<Pair<SerialCommPacket, Date>> ackQueue = new Vector();

        public SerialSender(String name) {
            super(name);
            outQueue.clear();
            ackQueue.clear();
        }

        @Override
        public void run() {
            while (true) {
                try {

                    SerialCommPacket outputMessage = getOutputMessage();

                    for (int i = 1; i <= MAX_NUM_OF_RETRANSMISSION; i++) {
                        if (serialPort == null || !serialPort.isOpen()) {
                            Singleton.logSystem.warning("No serial port is opened!");
                            break;
                        }

                        byte[] serialData = Protocol.getSerialData(outputMessage).array();

                        // Link noise simulation

                        if (Singleton.getInstance().settings.isLINK_NOISE && !outputMessage.isAck()) {
                            for (int b = 0; b < serialData.length; b++) {
                                boolean val;
                                boolean bl = val = rand.nextInt((int) (1.0 / Singleton.getInstance().settings.propabilityRate)) == 0;
                                if (val) {
                                    serialData[b] = (byte) (serialData[b] ^ (byte) rand.nextInt(254));
                                    Singleton.logElevator.warning("Protocol: Noise on TX line");
                                }
                            }
                        }

                        serialPort.writeBytes(serialData, serialData.length);
                        Singleton.logElevator.info("Tx: " + outputMessage.toString());
                        Singleton.getInstance().SerialLoggerTX(serialData);

                        if (outputMessage.isAck())
                            break; // If output packet is ack, we are not waiting for ack replay from MCU

                        SerialCommPacket ackMessage = getAckMessage(ACK_RETRANSMIT_WAIT_TIME);// wait time
                        if (ackMessage != null) break;
                        if (i < MAX_NUM_OF_RETRANSMISSION)
                            Singleton.logElevator.warning("Tx: No Ack received resending: " + i + "/" + MAX_NUM_OF_RETRANSMISSION);
                        else
                            Singleton.logElevator.warning("Tx: No Ack received. Packet was not delivered !: Num of attempts" + i + "/" + MAX_NUM_OF_RETRANSMISSION);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized void putOutputPacket(SerialCommPacket serialCommPacket) throws InterruptedException {

            if (serialCommPacket.isAck() && !outQueue.isEmpty()) {
//                outQueue.insertElementAt(serialCommPacket, 0);
                outQueue.add(0, serialCommPacket);
                Singleton.logElevator.info("Sending ACK");
            } else {
                outQueue.add(serialCommPacket);
            }
            notify();
        }

        synchronized void putAckPacket(SerialCommPacket serialCommPacket) throws InterruptedException {
            ackQueue.add(new Pair<>(serialCommPacket, new Date()));
            notify();
        }

        // Called by Consumer
        public synchronized SerialCommPacket getOutputMessage() throws InterruptedException {
            while (outQueue.size() == 0) {
                wait();//By executing wait() from a synchronized block, a thread gives up its hold on the lock and goes to sleep.
            }

            return outQueue.remove(0);
        }

        public synchronized SerialCommPacket getAckMessage(long delay) throws InterruptedException {
            Date currentDate = new Date();
            ackQueue.removeIf(ackPair -> ((currentDate.getTime() - ackPair.getValue().getTime()) >= 2L));

            if (ackQueue.size() == 0) {
                wait(delay);//By executing wait() from a synchronized block, a thread gives up its hold on the lock and goes to sleep.
            }

            if (ackQueue.size() != 0)
                return ackQueue.remove(0).getKey();
            else
                return null;

        }


    }

    public Comunication(EventBus eventBus) {
        serialSender = new SerialSender("SenderThread");
        serialSender.setPriority(Thread.MAX_PRIORITY);
        serialSender.start();

        this.eventBus = eventBus;
        eventBus.register(this);
        this.protocol = new Protocol();

    }

    public List<String> getPorts() {
        SerialPort[] commPorts = SerialPort.getCommPorts();

        return Arrays.stream(commPorts)
                .map(serialPort1 -> {
                    return SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC ? "/dev/" + serialPort1.getSystemPortName() : serialPort1.getSystemPortName();
                })
                .collect(Collectors.toList());
    }

    public boolean openConnection(String com) {
        Singleton.logSystem.info("Trying to open port: " + com);

        if (this.serialPort != null && this.serialPort.isOpen()) {
            this.serialPort.closePort();
        }


        this.serialPort = SerialPort.getCommPort(com);

        if (this.serialPort.openPort()) {

            this.serialPort.setComPortParameters(Singleton.getInstance().settings.NEW_BAUD_RATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

            serialSender.ackQueue.clear();
            serialSender.outQueue.clear();

            serialPort.addDataListener(this);
            Singleton.logSystem.info("Port :" + com + " is open! Speed: "+ Singleton.getInstance().settings.NEW_BAUD_RATE +" [bits/s] RxTimeout[mS]: "+Singleton.getInstance().getRxTimeOut());

            return true;
        } else {
            return false;
        }

    }

    @Subscribe
    public void PacketArrived(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getAddress().equals(SerialCommPacket.NET_MASK) || serialCommPacket.getAddress().equals(SerialCommPacket.SERIAL_LINK)) {
            this.sendToSerialPort(serialCommPacket);
        }
    }

    private void sendToSerialPort(SerialCommPacket serialCommPacket) {
//        if (this.serialPort == null || !this.serialPort.isOpen()) {
//            Singleton.logSystem.warning("No serial port is opened!");
//            return;
//        }
//
//
//        byte[] serialData = Protocol.getSerialData(serialCommPacket).array();
//        this.serialPort.writeBytes(serialData, serialData.length);
//
//        Singleton.logElevator.info("Tx: " + serialCommPacket.toString());
//        Singleton.getInstance().SerialLoggerTX(serialData);

        try {
            serialSender.putOutputPacket(serialCommPacket);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public boolean closePort() {
        if (this.serialPort != null /*&& this.serialPort.isOpen()*/) {
            Singleton.logSystem.info("Serial port is closed!");
            serialPort.removeDataListener();
            return this.serialPort.closePort();
        }
        return false;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            byte[] receivedData = serialPortEvent.getReceivedData();
            Singleton.getInstance().SerialLoggerRX(receivedData);
            for (byte c : receivedData) {

                // Link noise simulation
                if (Singleton.getInstance().settings.isLINK_NOISE) {
                    boolean val;
                    boolean bl = val = this.rand.nextInt((int) (1.0 / Singleton.getInstance().settings.propabilityRate)) == 0;
                    if (val) {
                        c = (byte) (c ^ (byte) this.rand.nextInt(254));
                        Singleton.logElevator.warning("Protocol: Noise on RX line");
                    }
                }

                Protocol.ReturnValue returnValue = this.protocol.newChar(c);
                if (!returnValue.commCompleted) {
                    if (returnValue.lastSTATE == Protocol.STATE.CRC) {
                        Singleton.logElevator.warning("Rx: " + this.protocol.getSerialCommPacket().toString());
                    }
                    continue;
                }

                if(!protocol.getSerialCommPacket().isAck()) {
                    Singleton.logElevator.info("Rx: " + this.protocol.getSerialCommPacket().toString());
                }else{
                    Singleton.logElevator.info("Rx: ACK. addr: " + Integer.toHexString(this.protocol.getSerialCommPacket().getAddress()));

                }

                if (!this.protocol.getSerialCommPacket().isAck()) {
                    SerialCommPacket ackSerialCommPacket = new AckPacket(this.protocol.getSerialCommPacket().getAddress());
//                ackPacket.setAddress(0);
//                ackPacket.setSenderAddr(0);
                    this.sendToSerialPort(ackSerialCommPacket);
                    this.eventBus.post((Object) this.protocol.getSerialCommPacket());
                } else {
                    try {
                        serialSender.putAckPacket(this.protocol.getSerialCommPacket());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            //Singleton.serialLog.info("Rx: " + Packet.bytesToHex(receivedData));


        }

    }
}

