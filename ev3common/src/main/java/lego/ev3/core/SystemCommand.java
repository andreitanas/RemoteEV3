package lego.ev3.core;

import java.io.IOException;
import java.io.RandomAccessFile;

/// <summary>
/// Direct commands for the EV3 brick
/// </summary>
public class SystemCommand
{
    private final Brick _brick;

    SystemCommand(Brick brick)
    {
        _brick = brick;
    }

    /// <summary>
    /// Write a file to the EV3 brick
    /// </summary>
    /// <param name="data">Data to write.</param>
    /// <param name="devicePath">Destination path on the brick.</param>
    /// <returns></returns>
    ///	<remarks>devicePath is relative from "lms2012/sys" on the EV3 brick.  Destination folders are automatically created if provided in the path.  The path must start with "apps", "prjs", or "tools".</remarks>
    public void WriteFile(final byte[] data, String devicePath) throws ArgumentException, IOException {
        final int chunkSize = 960;

        Command commandBegin = new Command(Enums.CommandType.SystemReply);
        commandBegin.AddOpcode(Enums.SystemOpcode.BeginDownload);
        commandBegin.AddRawParameter(data.length);
        commandBegin.AddRawParameter(devicePath);

        _brick.SendCommand(commandBegin);
        if(commandBegin.Response.SystemReplyStatus != Enums.SystemReplyStatus.Success)
            throw new IOException("Could not begin file save: " + commandBegin.Response.SystemReplyStatus);

        byte handle = commandBegin.Response.Data[0];
        int sizeSent = 0;

        while(sizeSent < data.length)
        {
            Command commandContinue = new Command(Enums.CommandType.SystemReply);
            commandContinue.AddOpcode(Enums.SystemOpcode.ContinueDownload);
            commandContinue.AddRawParameter(handle);
            int sizeToSend = Math.min(chunkSize, data.length - sizeSent);
            commandContinue.AddRawParameter(data, sizeSent, sizeToSend);
            sizeSent += sizeToSend;

            _brick.SendCommand(commandContinue);
            if(commandContinue.Response.SystemReplyStatus != Enums.SystemReplyStatus.Success &&
                    (commandContinue.Response.SystemReplyStatus != Enums.SystemReplyStatus.EndOfFile && sizeSent == data.length))
                throw new IOException("Error saving file: " + commandContinue.Response.SystemReplyStatus);
        }

        //Command commandClose = new Command(CommandType.SystemReply);
        //commandClose.AddOpcode(SystemOpcode.CloseFileHandle);
        //commandClose.AddRawParameter(handle);
        //await _brick.SendCommandAsyncInternal(commandClose);
        //if(commandClose.Response.SystemReplyStatus != SystemReplyStatus.Success)
        //	throw new Exception("Could not close handle: " + commandClose.Response.SystemReplyStatus);
    }

    /// <summary>
    /// Copy a local file to the EV3 brick
    /// </summary>
    /// <param name="localPath">Source path on the computer.</param>
    /// <param name="devicePath">Destination path on the brick.</param>
    /// <returns></returns>
    ///	<remarks>devicePath is relative from "lms2012/sys" on the EV3 brick.  Destination folders are automatically created if provided in the path.  The path must start with "apps", "prjs", or "tools".</remarks>
    public void CopyFile(String localPath, String devicePath) throws IOException, ArgumentException {
        byte[] data = GetFileContents(localPath);
        WriteFile(data, devicePath);
    }

    /// <summary>
    /// Create a directory on the EV3 brick
    /// </summary>
    /// <param name="devicePath">Destination path on the brick.</param>
    /// <returns></returns>
    ///	<remarks>devicePath is relative from "lms2012/sys" on the EV3 brick.  Destination folders are automatically created if provided in the path.  The path must start with "apps", "prjs", or "tools".</remarks>
    public void CreateDirectory(String devicePath) throws ArgumentException, IOException {
        Response r = ResponseManager.CreateResponse();
        Command c = new Command(Enums.CommandType.SystemReply);
        c.CreateDirectory(devicePath);
        _brick.SendCommand(c);
        if(r.SystemReplyStatus != Enums.SystemReplyStatus.Success)
            throw new IOException("Error creating directory: " + r.SystemReplyStatus);
    }

    /// <summary>
    /// Delete file from the EV3 brick
    /// </summary>
    /// <param name="devicePath">Destination path on the brick.</param>
    /// <returns></returns>
    /// <remarks>devicePath is relative from "lms2012/sys" on the EV3 brick.  The path must start with "apps", "prjs", or "tools".</remarks>
    public void DeleteFile(String devicePath) throws ArgumentException, IOException {
        Response r = ResponseManager.CreateResponse();
        Command c = new Command(Enums.CommandType.SystemReply);
        c.DeleteFile(devicePath);
        _brick.SendCommand(c);
        if(r.SystemReplyStatus != Enums.SystemReplyStatus.Success)
            throw new IOException("Error deleting file: " + r.SystemReplyStatus);
    }

    private byte[] GetFileContents(String localPath) throws IOException {
        RandomAccessFile f = new RandomAccessFile(localPath, "r");
        try {
            byte[] data = new byte[(int)f.length()];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
