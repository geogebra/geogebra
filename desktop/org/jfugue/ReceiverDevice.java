package org.jfugue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;

/**
 * Represents an attached MIDI device, such as a keyboard.  This class
 * uses javax.sound.MidiDevice, but is not derived from javax.sound.MidiDevice.
 * 
 * @author David Koelle
 * @version 3.0
 */
public class ReceiverDevice 
{
    private Receiver receiver;
    
    public ReceiverDevice(MidiDevice.Info info) throws MidiUnavailableException
    {
        MidiDevice device = null;

        device = MidiSystem.getMidiDevice(info);
        if (!(device.isOpen())) {
          device.open();
        }

        this.receiver = device.getReceiver();
    }
    
    /**
     * Send the given sequence to the MIDI device - use this to send MIDI files
     * to your keyboard!
     *  
     * @param sequence The sequence to send to the MIDI device
     */
    public void sendSequence(Sequence sequence)
    {
        TimeEventManager tem = new TimeEventManager();
        
        // ============================================== 
        // made null because getEvents does not exist
        // TODO must fix this if we ever need it
        MidiEvent[] events = null;   // = tem.getEvents(sequence);
        // ==============================================
        
        long elapsedTime = 0;
        for (int i = 0; i < events.length; i++) {
            MidiEvent event = events[i];
            MidiMessage message = event.getMessage();
            
            long timestamp = event.getTick();
            long deltaTime = timestamp - elapsedTime;
            elapsedTime = timestamp;

            if (deltaTime < 500) {
                System.out.print("sleeping for "+deltaTime+"...");
                try {
                    Thread.sleep((int)(deltaTime * 1.25));
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                System.out.println("awake");
            }

            receiver.send(message, -1);
        }
    }
}
