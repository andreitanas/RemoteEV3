package lego.ev3.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * see https://legoev3.codeplex.com/SourceControl/latest#Lego.Ev3.Core/Brick.cs
 */

/// <summary>
/// Main EV3 brick interface
/// </summary>
public class Brick implements ICommunication.IReportReceiver {
    /// <summary>
    /// Width of LCD screen
    /// </summary>
    public short LcdWidth = 178;

    /// <summary>
    /// Height of LCD screen
    /// </summary>
    public short LcdHeight = 128;

    /// <summary>
    /// Height of status bar
    /// </summary>
    public short TopLineHeight = 10;

    private final SynchronizationContext _context = SynchronizationContext.Current;
    private final ICommunication _comm;
    private final boolean _alwaysSendEvents;
    private final DirectCommand _directCommand;
    private final SystemCommand _systemCommand;
    private final Command _batchCommand;

    /// <summary>
    /// Input and output ports on LEGO EV3 brick
    /// </summary>
    public Dictionary<Enums.InputPort, Port> Ports;

    /// <summary>
    /// Buttons on the face of the LEGO EV3 brick
    /// </summary>
    public BrickButtons Buttons;

    /// <summary>
    /// Send "direct commands" to the EV3 brick.  These commands are executed instantly and are not batched.
    /// </summary>
    public DirectCommand getDirectCommand() {
        return _directCommand;
    }

    /// <summary>
    /// Send "system commands" to the EV3 brick.  These commands are executed instantly and are not batched.
    /// </summary>
    public SystemCommand getSystemCommand() {
        return _systemCommand;
    }

    /// <summary>
    /// Send a batch command of multiple direct commands at once.  Call the <see cref="Command.Initialize"/> method with the proper <see cref="CommandType"/> to set the type of command the batch should be executed as.
    /// </summary>
    public Command getBatchCommand() {
        return _batchCommand;
    }

    /// <summary>
    /// Event that is fired when a port is changed
    /// </summary>
    public BrickChangedListener brickChangeListener;

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="comm">Object implementing the <see cref="ICommunication"/> interface for talking to the brick</param>
    public Brick(ICommunication comm) throws ArgumentException {
        this(comm, false);
    }

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="comm">Object implementing the <see cref="ICommunication"/> interface for talking to the brick</param>
    /// <param name="alwaysSendEvents">Send events when data changes, or at every poll</param>
    public Brick(ICommunication comm, boolean alwaysSendEvents) throws ArgumentException {
        _directCommand = new DirectCommand(this);
        _systemCommand = new SystemCommand(this);
        _batchCommand = new Command(this);

        Buttons = new BrickButtons();

        _alwaysSendEvents = alwaysSendEvents;

        int index = 0;

        _comm = comm;
        _comm.SetReportReceiver(this);

        Ports = new Hashtable<Enums.InputPort, Port>();

        for (Enums.InputPort i : Enums.InputPort.values()) {
            Port port = new Port();
            port.InputPort = i;
            port.Index = index++;
            port.setName(String.valueOf(i));
        }
    }

    /// <summary>
    /// Connect to the EV3 brick.
    /// </summary>
    /// <returns></returns>
    public void Connect() throws ArgumentException {
        _comm.Connect();
        _directCommand.StopMotor(OutputPort.All, false);
    }

    /// <summary>
    /// Disconnect from the EV3 brick
    /// </summary>
    public void Disconnect() {
        _comm.Disconnect();
    }

    @Override
    public void ReceiveReport(byte[] data) {
        ResponseManager.HandleResponse(data);
    }

    void SendCommand(Command c) {
        _comm.Write(c.ToBytes());
        if (c.CommandType == Enums.CommandType.DirectReply || c.CommandType == Enums.CommandType.SystemReply)
            ResponseManager.WaitForResponse(c.Response);
    }

    private void PollSensors() throws ArgumentException {
        boolean changed = false;
        final int responseSize = 11;
        int index = 0;

        Command c = new Command(Enums.CommandType.DirectReply, (short)((8 * responseSize) + 6), 0);

        for (Enums.InputPort i : Enums.InputPort.values()) {
            Port p = Ports.get(i);
            index = p.Index * responseSize;

            c.GetTypeMode(p.InputPort, (byte)index, (byte)(index + 1));
            c.ReadySI(p.InputPort, p.getMode(), (byte)(index + 2));
            c.ReadyRaw(p.InputPort, p.getMode(), (byte)(index + 6));
            c.ReadyPercent(p.InputPort, p.getMode(), (byte)(index + 10));
        }

        index += responseSize;

        c.IsBrickButtonPressed(Enums.BrickButton.Back, (byte)(index + 0));
        c.IsBrickButtonPressed(Enums.BrickButton.Left, (byte)(index + 1));
        c.IsBrickButtonPressed(Enums.BrickButton.Up, (byte)(index + 2));
        c.IsBrickButtonPressed(Enums.BrickButton.Right, (byte)(index + 3));
        c.IsBrickButtonPressed(Enums.BrickButton.Down, (byte)(index + 4));
        c.IsBrickButtonPressed(Enums.BrickButton.Enter, (byte)(index + 5));

        SendCommand(c);
        if (c.Response.Data == null)
            return;

        for (Enums.InputPort i : Enums.InputPort.values()) {
            Port p = Ports.get(i);

            int type = c.Response.Data[(p.Index * responseSize) + 0];
            byte mode = c.Response.Data[(p.Index * responseSize) + 1];
            float siValue = ByteBuffer.wrap(c.Response.Data, (p.Index * responseSize) + 2, 4)
                    .order(ByteOrder.LITTLE_ENDIAN).getFloat();
            int rawValue = ByteBuffer.wrap(c.Response.Data, (p.Index * responseSize) + 6, 4)
                    .order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte percentValue = c.Response.Data[(p.Index * responseSize) + 10];

            if ((byte)p.getType().getValue() != type || Math.abs(p.getSIValue() - siValue) > 0.01f ||
                    p.getRawValue() != rawValue || p.getPercentValue() != percentValue)
                changed = true;

            p.setType(Enums.DeviceType.fromValue(type));

            p.setSIValue(siValue);
            p.setRawValue(rawValue);
            p.setPercentValue(percentValue);
        }

        if (Buttons.Back != (c.Response.Data[index + 0] == 1) ||
                Buttons.Left != (c.Response.Data[index + 1] == 1) ||
                Buttons.Up != (c.Response.Data[index + 2] == 1) ||
                Buttons.Right != (c.Response.Data[index + 3] == 1) ||
                Buttons.Down != (c.Response.Data[index + 4] == 1) ||
                Buttons.Enter != (c.Response.Data[index + 5] == 1))
            changed = true;

        Buttons.Back = (c.Response.Data[index + 0] == 1);
        Buttons.Left = (c.Response.Data[index + 1] == 1);
        Buttons.Up = (c.Response.Data[index + 2] == 1);
        Buttons.Right = (c.Response.Data[index + 3] == 1);
        Buttons.Down = (c.Response.Data[index + 4] == 1);
        Buttons.Enter = (c.Response.Data[index + 5] == 1);

        if (changed || _alwaysSendEvents)
            OnBrickChanged(new BrickChangedEventArgs(this.Ports, this.Buttons));
    }

    interface BrickChangedListener {
        void OnBrickChanged(BrickChangedEventArgs eventArgs);
    }

    public void setBrickChangedListener(BrickChangedListener listener) {
        this.brickChangeListener = listener;
    }

    private void OnBrickChanged(BrickChangedEventArgs e) {
        if (brickChangeListener != null)
            brickChangeListener.OnBrickChanged(e);
    }
}