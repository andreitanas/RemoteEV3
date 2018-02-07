package lego.ev3.core;

import java.util.Dictionary;

/// <summary>
/// Arguments for PortsChanged event
/// </summary>
public final class BrickChangedEventArgs
{
    /// <summary>
    /// A map of all ports on the EV3 brick
    /// </summary>
    public Dictionary<Enums.InputPort, Port> Ports;

    /// <summary>
    /// Buttons on the face of the LEGO EV3 brick
    /// </summary>
    public BrickButtons Buttons;

    public BrickChangedEventArgs(Dictionary<Enums.InputPort, Port> ports, BrickButtons buttons) {
        this.Ports = ports;
        this.Buttons = buttons;
    }
}