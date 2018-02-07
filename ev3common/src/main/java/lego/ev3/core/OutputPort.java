package lego.ev3.core;

/// <summary>
/// Ports which can send output
/// </summary>
public class OutputPort {
    /// <summary>
    /// Port A
    /// </summary>
    public static OutputPort A = new OutputPort(0x01);
    /// <summary>
    /// Port B
    /// </summary>
    public static OutputPort B = new OutputPort(0x02);
    /// <summary>
    /// Port C
    /// </summary>
    public static OutputPort C = new OutputPort(0x04);
    /// <summary>
    /// Port D
    /// </summary>
    public static OutputPort D = new OutputPort(0x08);
    /// <summary>
    /// Ports A),B),C and D simultaneously
    /// </summary>
    public static OutputPort All = new OutputPort(0x0f);
    /// <summary>
    /// None of the ports
    /// </summary>
    public static OutputPort None = new OutputPort(0);

    private int value;
    public OutputPort(int i) { value = i;}
    int getValue() { return value; }

    public OutputPort Also(OutputPort other) {
        return new OutputPort(value | other.value);
    }
}