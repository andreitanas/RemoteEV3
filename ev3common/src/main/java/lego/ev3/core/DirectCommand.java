package lego.ev3.core;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/// <summary>
/// Direct commands for the EV3 brick
/// </summary>
public class DirectCommand {
    private final Brick _brick;

    DirectCommand(Brick brick) {
        _brick = brick;
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified power.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100 to 100).</param>
    /// <returns></returns>
    public void TurnMotorAtPower(OutputPort ports, int power) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TurnMotorAtPower(ports, power);
        c.StartMotor(ports);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Turn the specified motor at the specified speed.
    /// </summary>
    /// <param name="ports">Port or ports to apply the command to.</param>
    /// <param name="speed">The speed to apply to the specified motors (-100 to 100).</param>
    public void TurnMotorAtSpeed(OutputPort ports, int speed) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TurnMotorAtSpeed(ports, speed);
        c.StartMotor(ports);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified power for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100 to 100).</param>
    /// <param name="steps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtPower(OutputPort ports, int power, int steps, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StepMotorAtPower(ports, power, 0, steps, 0, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified power for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100 to 100).</param>
    /// <param name="rampUpSteps"></param>
    /// <param name="constantSteps"></param>
    /// <param name="rampDownSteps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtPower(OutputPort ports, int power, int rampUpSteps, int constantSteps, int rampDownSteps, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StepMotorAtPower(ports, power, rampUpSteps, constantSteps, rampDownSteps, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified speed for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The speed at which to turn the motor (-100 to 100).</param>
    /// <param name="steps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtSpeed(OutputPort ports, int speed, int steps, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StepMotorAtSpeed(ports, speed, 0, steps, 0, brake);
        _brick.SendCommand(c);
    }


    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified speed for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The speed at which to turn the motor (-100 to 100).</param>
    /// <param name="rampUpSteps"></param>
    /// <param name="constantSteps"></param>
    /// <param name="rampDownSteps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtSpeed(OutputPort ports, int speed, int rampUpSteps, int constantSteps, int rampDownSteps, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StepMotorAtSpeed(ports, speed, rampUpSteps, constantSteps, rampDownSteps, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified power for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100 to 100).</param>
    /// <param name="milliseconds">Number of milliseconds to run at constant power.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtPowerForTime(OutputPort ports, int power, int milliseconds, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TurnMotorAtPowerForTime(ports, power, 0, milliseconds, 0, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified power for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100 to 100).</param>
    /// <param name="msRampUp">Number of milliseconds to get up to power.</param>
    /// <param name="msConstant">Number of milliseconds to run at constant power.</param>
    /// <param name="msRampDown">Number of milliseconds to power down to a stop.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtPowerForTime(OutputPort ports, int power, int msRampUp, int msConstant, int msRampDown, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TurnMotorAtPowerForTime(ports, power, msRampUp, msConstant, msRampDown, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified speed for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The power at which to turn the motor (-100 to 100).</param>
    /// <param name="milliseconds">Number of milliseconds to run at constant speed.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtSpeedForTime(OutputPort ports, int speed, int milliseconds, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TurnMotorAtSpeedForTime(ports, speed, 0, milliseconds, 0, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified speed for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The power at which to turn the motor (-100 to 100).</param>
    /// <param name="msRampUp">Number of milliseconds to get up to speed.</param>
    /// <param name="msConstant">Number of milliseconds to run at constant speed.</param>
    /// <param name="msRampDown">Number of milliseconds to slow down to a stop.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtSpeedForTime(OutputPort ports, int speed, int msRampUp, int msConstant, int msRampDown, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TurnMotorAtSpeedForTime(ports, speed, msRampUp, msConstant, msRampDown, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Set the polarity (direction) of a motor.
    /// </summary>
    /// <param name="ports">Port or ports to change polarity</param>
    /// <param name="polarity">The new polarity (direction) value</param>
    /// <returns></returns>
    public void SetMotorPolarity(OutputPort ports, Enums.Polarity polarity) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.SetMotorPolarity(ports, polarity);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Start motors on the specified ports.
    /// </summary>
    /// <param name="ports">The port or ports to which the stop command will be sent.</param>
    /// <returns></returns>
    public void StartMotor(OutputPort ports) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StartMotor(ports);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Synchronize stepping of motors.
    /// </summary>
    /// <param name="ports">The port or ports to which the stop command will be sent.</param>
    /// <param name="speed">Speed to turn the motor(s).</param>
    /// <param name="turnRatio">The turn ratio to apply.</param>
    /// <param name="step">The number of steps to turn the motor(s).</param>
    /// <param name="brake">Brake or coast at the end.</param>
    /// <returns></returns>
    public void StepMotorSync(OutputPort ports, int speed, short turnRatio, int step, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StepMotorSync(ports, speed, turnRatio, step, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Synchronize timing of motors.
    /// </summary>
    /// <param name="ports">The port or ports to which the stop command will be sent.</param>
    /// <param name="speed">Speed to turn the motor(s).</param>
    /// <param name="turnRatio">The turn ratio to apply.</param>
    /// <param name="time">The time to turn the motor(s).</param>
    /// <param name="brake">Brake or coast at the end.</param>
    /// <returns></returns>
    public void TimeMotorSync(OutputPort ports, int speed, short turnRatio, int time, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.TimeMotorSync(ports, speed, turnRatio, time, brake);
        _brick.SendCommand(c);
    }


    /// <summary>
    /// Stops motors on the specified ports.
    /// </summary>
    /// <param name="ports">The port or ports to which the stop command will be sent.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void StopMotor(OutputPort ports, boolean brake) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.StopMotor(ports, brake);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Resets all ports and devices to defaults.
    /// </summary>
    /// <returns></returns>
    public void ClearAllDevices() throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.ClearAllDevices();
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Clears changes on specified port
    /// </summary>
    ///	<param name="port">The port to clear</param>
    /// <returns></returns>
    public void ClearChanges(Enums.InputPort port) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.ClearChanges(port);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Plays a tone of the specified frequency for the specified time.
    /// </summary>
    /// <param name="volume">Volume of tone (0-100).</param>
    /// <param name="frequency">Frequency of tone, in hertz.</param>
    /// <param name="duration">Duration to play tone, in milliseconds.</param>
    /// <returns></returns>
    public void PlayTone(int volume, short frequency, short duration) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.PlayTone(volume, frequency, duration);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Play a sound file stored on the EV3 brick
    /// </summary>
    /// <param name="volume">Volume of the sound (0-100)</param>
    /// <param name="filename">Filename of sound stored on brick, without the .RSF extension</param>
    /// <returns></returns>
    public void PlaySound(int volume, String filename) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.PlaySound(volume, filename);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Return the current version number of the firmware running on the EV3 brick.
    /// </summary>
    /// <returns>Current firmware version.</returns>
    public String GetFirmwareVersion() throws ArgumentException, UnsupportedEncodingException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)0x10, 0);
        c.GetFirwmareVersion(0x10, 0);
        _brick.SendCommand(c);
        if (c.Response.Data == null)
            return null;

        int index = c.Response.Data.length;
        for (int i = 0; i < c.Response.Data.length; i++) {
            if (c.Response.Data[i] == 0) {
                index = i;
                break;
            }
        }

        return new String(c.Response.Data, 0, index, "UTF-8");
    }

    /// <summary>
    /// Returns whether the specified BrickButton is pressed
    /// </summary>
    /// <param name="button">Button on the face of the EV3 brick</param>
    /// <returns>Whether or not the button is pressed</returns>
    public boolean IsBrickButtonPressed(Enums.BrickButton button) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)1, 0);
        c.IsBrickButtonPressed(button, 0);
        _brick.SendCommand(c);
        return false;
    }

    /// <summary>
    /// Set EV3 brick LED pattern
    /// </summary>
    /// <param name="ledPattern">Pattern to display on LED</param>
    /// <returns></returns>
    public void SetLedPattern(Enums.LedPattern ledPattern) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.SetLedPattern(ledPattern);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw a line on the EV3 LCD screen
    /// </summary>
    /// <param name="color">Color of the line</param>
    /// <param name="x0">X start</param>
    /// <param name="y0">Y start</param>
    /// <param name="x1">X end</param>
    /// <param name="y1">Y end</param>
    /// <returns></returns>
    public void DrawLine(Enums.Color color, short x0, short y0, short x1, short y1) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawLine(color, x0, y0, x1, y1);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw a single pixel
    /// </summary>
    /// <param name="color">Color of the pixel</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <returns></returns>
    public void DrawPixel(Enums.Color color, short x, short y) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawPixel(color, x, y);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw a rectangle
    /// </summary>
    /// <param name="color">Color of the rectangle</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="width">Width of rectangle</param>
    /// <param name="height">Height of rectangle</param>
    /// <param name="filled">Filled or empty</param>
    /// <returns></returns>
    public void DrawRectangle(Enums.Color color, short x, short y, short width, short height, boolean filled) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawRectangle(color, x, y, width, height, filled);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw a filled rectangle, inverting the pixels underneath it
    /// </summary>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="width">Width of the rectangle</param>
    /// <param name="height">Height of the rectangle</param>
    /// <returns></returns>
    public void DrawInverseRectangle(short x, short y, short width, short height) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawInverseRectangle(x, y, width, height);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw a circle
    /// </summary>
    /// <param name="color">Color of the circle</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="radius">Radius of the circle</param>
    /// <param name="filled">Filled or empty</param>
    /// <returns></returns>
    public void DrawCircle(Enums.Color color, short x, short y, short radius, boolean filled) {
    }

    /// <summary>
    /// Write a string to the screen
    /// </summary>
    /// <param name="color">Color of the text</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="text">Text to draw</param>
    /// <returns></returns>
    public void DrawText(Enums.Color color, short x, short y, String text) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawCircle(color, x, y, (short)0, false);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw a dotted line
    /// </summary>
    /// <param name="color">Color of dotted line</param>
    /// <param name="x0">X start</param>
    /// <param name="y0">Y start</param>
    /// <param name="x1">X end</param>
    /// <param name="y1">Y end</param>
    /// <param name="onPixels">Number of pixels the line is drawn</param>
    /// <param name="offPixels">Number of pixels the line is empty</param>
    /// <returns></returns>
    public void DrawDottedLine(Enums.Color color, short x0, short y0, short x1, short y1, short onPixels, short offPixels) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawDottedLine(color, x0, y0, x1, y1, onPixels, offPixels);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Fills the width of the screen between the provided Y coordinates
    /// </summary>
    /// <param name="color">Color of the fill</param>
    /// <param name="y0">Y start</param>
    /// <param name="y1">Y end</param>
    /// <returns></returns>
    public void DrawFillWindow(Enums.Color color, short y0, short y1) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawFillWindow(color, y0, y1);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Draw an image to the LCD screen
    /// </summary>
    /// <param name="color">Color of the image pixels</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="devicePath">Path to the image on the EV3 brick</param>
    /// <returns></returns>
    public void DrawImage(Enums.Color color, short x, short y, String devicePath) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.DrawImage(color, x, y, devicePath);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Enable or disable the top status bar
    /// </summary>
    /// <param name="enabled">Enabled or disabled</param>
    /// <returns></returns>
    public void EnableTopLine(boolean enabled) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.EnableTopLine(enabled);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Select the font for text drawing
    /// </summary>
    /// <param name="fontType">Type of font to use</param>
    /// <returns></returns>
    public void SelectFont(Enums.FontType fontType) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.SelectFont(fontType);
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Clear the entire screen
    /// </summary>
    /// <returns></returns>
    public void CleanUI() throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.CleanUI();
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Refresh the EV3 LCD screen
    /// </summary>
    /// <returns></returns>
    public void UpdateUI() throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectNoReply);
        c.UpdateUI();
        _brick.SendCommand(c);
    }

    /// <summary>
    /// Get the type and mode of the device attached to the specified port
    /// </summary>
    /// <param name="port">The input port to query</param>
    /// <returns>2 bytes, index 0 being the type, index 1 being the mode</returns>
    public byte[] GetTypeMode(Enums.InputPort port) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)2, 0);
        c.GetTypeMode(port, 0, 1);
        _brick.SendCommand(c);
        return c.Response.Data;
    }

    /// <summary>
    /// Read the SI value from the specified port in the specified mode
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode used to read the data</param>
    /// <returns>The SI value</returns>
    public float ReadySI(Enums.InputPort port, int mode) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)4, 0);
        c.ReadySI(port, mode, 0);
        _brick.SendCommand(c);
        return ByteBuffer.wrap(c.Response.Data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    /// <summary>
    /// Read the raw value from the specified port in the specified mode
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode used to read the data</param>
    /// <returns>The Raw value</returns>
    public int ReadyRaw(Enums.InputPort port, int mode) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)4, 0);
        c.ReadyRaw(port, mode, 0);
        _brick.SendCommand(c);
        return ByteBuffer.wrap(c.Response.Data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /// <summary>
    /// Read the percent value from the specified port in the specified mode
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode used to read the data</param>
    /// <returns>The percentage value</returns>
    public int ReadyPercent(Enums.InputPort port, int mode) throws ArgumentException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)4, 0);
        c.ReadyPercent(port, mode, 0);
        _brick.SendCommand(c);
        return ByteBuffer.wrap(c.Response.Data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /// <summary>
    /// Get the name of the device attached to the specified port
    /// </summary>
    /// <param name="port">Port to query</param>
    /// <returns>The name of the device</returns>
    public String GetDeviceName(Enums.InputPort port) throws ArgumentException, UnsupportedEncodingException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)0x7f, 0);
        c.GetDeviceName(port, 0x7f, 0);
        _brick.SendCommand(c);
        int index = c.Response.Data.length;
        for (int i = 0; i < c.Response.Data.length; i++) {
            if (c.Response.Data[i] == 0) {
                index = i;
                break;
            }
        }

        return new String(c.Response.Data, 0, index, "UTF-8");
    }

    /// <summary>
    /// Get the mode of the device attached to the specified port
    /// </summary>
    /// <param name="port">Port to query</param>
    /// <param name="mode">Mode of the name to get</param>
    /// <returns>The name of the mode</returns>
    public String GetModeName(Enums.InputPort port, int mode) throws ArgumentException, UnsupportedEncodingException {
        Command c = new Command(Enums.CommandType.DirectReply, (short)0x7f, 0);
        c.GetModeName(port, mode, 0x7f, 0);
        _brick.SendCommand(c);
        int index = c.Response.Data.length;
        for (int i = 0; i < c.Response.Data.length; i++) {
            if (c.Response.Data[i] == 0) {
                index = i;
                break;
            }
        }

        return new String(c.Response.Data, 0, index, "UTF-8");
    }
}