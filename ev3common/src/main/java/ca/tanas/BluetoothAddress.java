package ca.tanas;

/**
 * Created by andreitanas on 14-11-25.
 */
public class BluetoothAddress
{
    public String Name;
    public String Address;

    public BluetoothAddress(String name, String address)
    {
        Name = name;
        Address = address;
    }

    @Override
    public String toString()
    {
        if (Address == null)
            return "Disabled";
        return String.format("%s (%s)", Name, Address);
    }

    @Override
    public boolean equals(Object o)
    {
        return Address != null &&
                o.getClass() == this.getClass() &&
                Address.equals(((BluetoothAddress)o).Address);
    }
}
