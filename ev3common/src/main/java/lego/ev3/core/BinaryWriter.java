package lego.ev3.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrei Tanas on 14-11-27.
 */
public class BinaryWriter {
    private List<Byte> buffer = new ArrayList<Byte>();

    public void Write(byte value) {
        buffer.add(value);
    }

    public void Write(short value) {
        buffer.add((byte)(value & 0xff));
        buffer.add((byte)(value >> 8));
    }

    public void Write(int value) {
        buffer.add((byte)(value & 0xff));
        buffer.add((byte)((value >> 8) & 0xff));
        buffer.add((byte)((value >> 16) & 0xff));
        buffer.add((byte)((value >> 24) & 0xff));
    }

    public void Write(byte[] value) {
        for (byte b : value)
            buffer.add(b);
    }

    public void Write(byte[] value, int index, int count) {
        for (int i = 0; i < count; i++)
            buffer.add(value[index + i]);
    }

    public byte[] ToArray() {
        byte[] array = new byte[buffer.size()];
        for (int i  = 0; i < array.length; i++)
            array[i] = buffer.get(i);
        return array;
    }
}
