package lego.ev3.core;

/// <summary>
/// An input or output port on the EV3 brick
/// </summary>
public class Port implements INotifyPropertyChanged {
    int Index;
    Enums.InputPort InputPort;

    private String _name;
    private Enums.DeviceType _type;
    private byte _mode;
    private float _siValue;
    private int _rawValue;
    private byte _percentValue;
    private final SynchronizationContext _context;

    /// <summary>
    /// Constructor
    /// </summary>
    public Port() {
        _context = SynchronizationContext.Current;
    }

    /// <summary>
    /// Name of port.
    /// </summary>
    public String getName() {
        return _name;
    }

    public void setName(String value) {
        _name = value;
        OnPropertyChanged();
    }

    /// <summary>
    /// Device plugged into port.
    /// </summary>
    public Enums.DeviceType getType() {
        return _type;
    }

    public void setType(Enums.DeviceType value) {
        _type = value;
        OnPropertyChanged();
    }

    /// <summary>
    /// Device mode.  Some devices work in multiple modes.
    /// </summary>
    public byte getMode() {
        return _mode;
    }

    private void setMode(byte value) {
        _mode = value;
        OnPropertyChanged();
    }

    /// <summary>
    /// Current International System of Units value associated with the Port.
    /// </summary>
    public float getSIValue() {
        return _siValue;
    }

    public void setSIValue(float value) {
        _siValue = value;
        OnPropertyChanged();
    }

    /// <summary>
    /// Raw value associated with the Port.
    /// </summary>
    public int getRawValue() {
        return _rawValue;
    }

    public void setRawValue(int value) {
        _rawValue = value;
        OnPropertyChanged();
    }

    /// <summary>
    /// Percentage value associated with the Port.
    /// </summary>
    public byte getPercentValue() {
        return _percentValue;
    }

    public void setPercentValue(byte value) {
        _percentValue = value;
        OnPropertyChanged();
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(byte mode) {
        setMode(mode);
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.TouchMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.NxtLightMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.NxtSoundMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.NxtUltrasonicMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.NxtTemperatureMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.MotorMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.ColorMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.UltrasonicMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.GyroscopeMode mode) {
        setMode((byte)mode.ordinal());
    }

    /// <summary>
    /// Set the connected sensor's mode
    /// </summary>
    /// <param name="mode">The requested mode.</param>
    public void SetMode(Enums.InfraredMode mode) {
        setMode((byte)mode.ordinal());
    }

    @Override
    public void OnPropertyChanged() {

    }

/// <summary>
/// INotifyProperty event
/// </summary>
//public event PropertyChangedEventHandler PropertyChanged;
//
//private void OnPropertyChanged([CallerMemberName] string propertyName = null)
//        {
//        PropertyChangedEventHandler handler = PropertyChanged;
//        if(handler != null)
//        {
//        if(_context == SynchronizationContext.Current)
//        handler(this, new PropertyChangedEventArgs(propertyName));
//        else
//        _context.Post(delegate { handler(this, new PropertyChangedEventArgs(propertyName)); }, null);
//        }
//        }
}