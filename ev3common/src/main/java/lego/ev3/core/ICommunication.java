package lego.ev3.core;

/// <summary>
/// Interface for communicating with the EV3 brick
/// </summary>
public interface ICommunication {
    /// <summary>
    /// Called when a full report is ready to parse and process.
    /// </summary>
    interface IReportReceiver {
        void ReceiveReport(byte[] data);
    }

    void SetReportReceiver(IReportReceiver receiver);

    /// <summary>
    /// Connect to the EV3 brick.
    /// </summary>
    void Connect();

    /// <summary>
    /// Disconnect from the EV3 brick.
    /// </summary>
    void Disconnect();

    /// <summary>
    /// Write a report to the EV3 brick.
    /// </summary>
    /// <param name="data"></param>
    void Write(byte[] data);
}