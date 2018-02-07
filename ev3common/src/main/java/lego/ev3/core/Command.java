package lego.ev3.core;

import java.nio.charset.Charset;

/**
 * see https://legoev3.codeplex.com/SourceControl/latest#Lego.Ev3.Core/Command.cs
 */

/// <summary>
/// Command or chain of commands to be written to the EV3 brick
/// </summary>
public final class Command {
    private BinaryWriter _writer;
    private Brick _brick;

    public Enums.CommandType CommandType;

    public Response Response;

    public Command(Brick brick) throws ArgumentException {
        this(Enums.CommandType.DirectNoReply);
        _brick = brick;
    }

    public Command(Enums.CommandType commandType) throws ArgumentException {
        this(commandType, (short)0, 0);
    }

    public Command(Enums.CommandType commandType, short globalSize, int localSize) throws ArgumentException {
        Initialize(commandType, globalSize, localSize);
    }

    /// <summary>
    /// Start a new command of a specific type
    /// </summary>
    /// <param name="commandType">The type of the command to start</param>
    public void Initialize(Enums.CommandType commandType) throws ArgumentException {
        Initialize(commandType, (short)0, 0);
    }

    /// <summary>
    /// Start a new command of a speicifc type with a global and/or local buffer on the EV3 brick
    /// </summary>
    /// <param name="commandType">The type of the command to start</param>
    /// <param name="globalSize">The size of the global buffer in bytes (maximum of 1024 bytes)</param>
    /// <param name="localSize">The size of the local buffer in bytes (maximum of 64 bytes)</param>
    public void Initialize(Enums.CommandType commandType, short globalSize, int localSize) throws ArgumentException {
        if (globalSize > 1024)
            throw new ArgumentException("Global buffer must be less than 1024 bytes", "globalSize");
        if (localSize > 64)
            throw new ArgumentException("Local buffer must be less than 64 bytes", "localSize");

        _writer = new BinaryWriter();
        Response = ResponseManager.CreateResponse();

        CommandType = commandType;

        // 2 bytes (this gets filled in later when the user calls ToBytes())
        _writer.Write((short)0xffff);

        // 2 bytes
        _writer.Write(Response.Sequence);

        // 1 byte
        _writer.Write((byte)commandType.getValue());

        if (commandType == Enums.CommandType.DirectReply || commandType == Enums.CommandType.DirectNoReply) {
            // 2 bytes (llllllgg gggggggg)
            _writer.Write((byte)globalSize); // lower bits of globalSize
            _writer.Write((byte)((localSize << 2) | (globalSize >> 8) & 0x03)); // upper bits of globalSize + localSize
        }
    }

    public void AddOpcode(Enums.Opcode opcode) {
        // 1 or 2 bytes (opcode + subcmd, if applicable)
        // I combined opcode + sub into short where applicable, so we need to pull them back apart here
        if (opcode.getValue() > Enums.Opcode.Tst.getValue())
            _writer.Write((byte)((short)opcode.getValue() >> 8));
        _writer.Write((byte)opcode.getValue());
    }

    public void AddOpcode(Enums.SystemOpcode opcode) {
        _writer.Write((byte)opcode.getValue());
    }

    public void AddGlobalIndex(byte index) {
        // 0xe1 = global index, long format, 1 byte
        _writer.Write((byte)(0xe1));
        _writer.Write(index);
    }

    public void AddParameter(byte parameter) {
        // 0x81 = long format, 1 byte
        _writer.Write((byte)Enums.ArgumentSize.Byte.getValue());
        _writer.Write(parameter);
    }

    public void AddParameter(short parameter) {
        // 0x82 = long format, 2 bytes
        _writer.Write((byte)Enums.ArgumentSize.Short.getValue());
        _writer.Write(parameter);
    }

    public void AddParameter(int parameter) {
        // 0x83 = long format, 4 bytes
        _writer.Write((byte)Enums.ArgumentSize.Int.getValue());
        _writer.Write(parameter);
    }

    public void AddParameter(String s) {
        // 0x84 = long format, null terminated string
        _writer.Write((byte)Enums.ArgumentSize.String.getValue());
        byte[] bytes = Charset.forName("UTF-8").encode(s).array();
        _writer.Write(bytes);
        _writer.Write((byte)0x00);
    }

    // Raw methods below don't get format specifier added prior to the data itself...these are used in system commands (only?)
    public void AddRawParameter(byte parameter) {
        _writer.Write(parameter);
    }

    public void AddRawParameter(short parameter) {
        _writer.Write(parameter);
    }

    public void AddRawParameter(int parameter) {
        _writer.Write(parameter);
    }

    public void AddRawParameter(String s) {
        byte[] bytes = Charset.forName("UTF-8").encode(s).array();
        _writer.Write(bytes);
        _writer.Write((byte)0x00);
    }

    public void AddRawParameter(byte[] data, int index, int count) {
        _writer.Write(data, index, count);
    }

    public byte[] ToBytes() {
        byte[] buff = _writer.ToArray();

        // size of data, not including the 2 size bytes
        short size = (short)(buff.length - 2);

        // little-endian
        buff[0] = (byte)size;
        buff[1] = (byte)(size >> 8);

        return buff;
    }

    /// <summary>
    /// Start the motor(s) based on previous commands
    /// </summary>
    /// <param name="ports">Port or ports to apply the command to.</param>
    public void StartMotor(OutputPort ports) {
        AddOpcode(Enums.Opcode.OutputStart);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
    }

    /// <summary>
    /// Turns the specified motor at the specified power
    /// </summary>
    /// <param name="ports">Port or ports to apply the command to.</param>
    /// <param name="power">The amount of power to apply to the specified motors (-100% to 100%).</param>
    public void TurnMotorAtPower(OutputPort ports, int power) throws ArgumentException {
        if (power < -100 || power > 100)
            throw new ArgumentException("Power must be between -100 and 100 inclusive.", "power");

        AddOpcode(Enums.Opcode.OutputPower);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)power);    // power
    }

    /// <summary>
    /// Turn the specified motor at the specified speed.
    /// </summary>
    /// <param name="ports">Port or ports to apply the command to.</param>
    /// <param name="speed">The speed to apply to the specified motors (-100% to 100%).</param>
    public void TurnMotorAtSpeed(OutputPort ports, int speed) throws ArgumentException {
        if (speed < -100 || speed > 100)
            throw new ArgumentException("Speed must be between -100 and 100 inclusive.", "speed");

        AddOpcode(Enums.Opcode.OutputSpeed);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)speed);        // speed
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified power for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100% to 100%).</param>
    /// <param name="steps">The number of steps to turn the motor.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtPower(OutputPort ports, int power, int steps, boolean brake) throws ArgumentException {
        StepMotorAtPower(ports, power, 0, steps, 10, brake);
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified power for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100% to 100%).</param>
    /// <param name="rampUpSteps"></param>
    /// <param name="constantSteps"></param>
    /// <param name="rampDownSteps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtPower(OutputPort ports, int power, int rampUpSteps, int constantSteps, int rampDownSteps, boolean brake) throws ArgumentException {
        if (power < -100 || power > 100)
            throw new ArgumentException("Power must be between -100 and 100 inclusive.", "power");

        AddOpcode(Enums.Opcode.OutputStepPower);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)power);            // power
        AddParameter(rampUpSteps);    // step1
        AddParameter(constantSteps);    // step2
        AddParameter(rampDownSteps);    // step3
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified speed for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The speed at which to turn the motor (-100% to 100%).</param>
    /// <param name="steps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtSpeed(OutputPort ports, int speed, int steps, boolean brake) throws ArgumentException {
        StepMotorAtSpeed(ports, speed, 0, steps, 0, brake);
    }

    /// <summary>
    /// Step the motor connected to the specified port or ports at the specified speed for the specified number of steps.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The speed at which to turn the motor (-100% to 100%).</param>
    /// <param name="rampUpSteps"></param>
    /// <param name="constantSteps"></param>
    /// <param name="rampDownSteps"></param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    public void StepMotorAtSpeed(OutputPort ports, int speed, int rampUpSteps, int constantSteps, int rampDownSteps, boolean brake) throws ArgumentException {
        if (speed < -100 || speed > 100)
            throw new ArgumentException("Speed must be between -100 and 100 inclusive.", "speed");

        AddOpcode(Enums.Opcode.OutputStepSpeed);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)speed);            // speed
        AddParameter(rampUpSteps);    // step1
        AddParameter(constantSteps);    // step2
        AddParameter(rampDownSteps);    // step3
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified power for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100% to 100%).</param>
    /// <param name="milliseconds">Number of milliseconds to run at constant power.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtPowerForTime(OutputPort ports, int power, int milliseconds, boolean brake) throws ArgumentException {
        TurnMotorAtPowerForTime(ports, power, 0, milliseconds, 0, brake);
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified power for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="power">The power at which to turn the motor (-100% to 100%).</param>
    /// <param name="msRampUp">Number of milliseconds to get up to power.</param>
    /// <param name="msConstant">Number of milliseconds to run at constant power.</param>
    /// <param name="msRampDown">Number of milliseconds to power down to a stop.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtPowerForTime(OutputPort ports, int power, int msRampUp, int msConstant, int msRampDown, boolean brake) throws ArgumentException {
        if (power < -100 || power > 100)
            throw new ArgumentException("Power must be between -100 and 100 inclusive.", "power");

        AddOpcode(Enums.Opcode.OutputTimePower);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)power);    // power
        AddParameter(msRampUp);        // step1
        AddParameter(msConstant);    // step2
        AddParameter(msRampDown);    // step3
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified speed for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The power at which to turn the motor (-100% to 100%).</param>
    /// <param name="milliseconds">Number of milliseconds to run at constant speed.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtSpeedForTime(OutputPort ports, int speed, int milliseconds, boolean brake) throws ArgumentException {
        TurnMotorAtSpeedForTime(ports, speed, 0, milliseconds, 0, brake);
    }

    /// <summary>
    /// Turn the motor connected to the specified port or ports at the specified speed for the specified times.
    /// </summary>
    /// <param name="ports">A specific port or Ports.All.</param>
    /// <param name="speed">The power at which to turn the motor (-100% to 100%).</param>
    /// <param name="msRampUp">Number of milliseconds to get up to speed.</param>
    /// <param name="msConstant">Number of milliseconds to run at constant speed.</param>
    /// <param name="msRampDown">Number of milliseconds to slow down to a stop.</param>
    /// <param name="brake">Apply brake to motor at end of routine.</param>
    /// <returns></returns>
    public void TurnMotorAtSpeedForTime(OutputPort ports, int speed, int msRampUp, int msConstant, int msRampDown, boolean brake) throws ArgumentException {
        if (speed < -100 || speed > 100)
            throw new ArgumentException("Speed must be between -100 and 100 inclusive.", "speed");

        AddOpcode(Enums.Opcode.OutputTimeSpeed);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)speed);            // power
        AddParameter(msRampUp);        // step1
        AddParameter(msConstant);        // step2
        AddParameter(msRampDown);        // step3
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Append the Set Polarity command to an existing Command object
    /// </summary>
    /// <param name="ports">Port or ports to change polarity</param>
    /// <param name="polarity">The new polarity (direction) value</param>
    public void SetMotorPolarity(OutputPort ports, Enums.Polarity polarity) {
        AddOpcode(Enums.Opcode.OutputPolarity);
        AddParameter((byte)0x00);
        AddParameter((byte)ports.getValue());
        AddParameter((byte)polarity.getValue());
    }

    /// <summary>
    /// Synchronize stepping of motors.
    /// </summary>
    /// <param name="ports">The port or ports to which the stop command will be sent.</param>
    /// <param name="speed">Speed to turn the motor(s). (-100 to 100)</param>
    /// <param name="turnRatio">The turn ratio to apply. (-200 to 200)</param>
    /// <param name="step">The number of steps to turn the motor(s).</param>
    /// <param name="brake">Brake or coast at the end.</param>
    /// <returns></returns>
    public void StepMotorSync(OutputPort ports, int speed, short turnRatio, int step, boolean brake) throws ArgumentException {
        if (speed < -100 || speed > 100)
            throw new ArgumentException("Speed must be between -100 and 100", "speed");

        if (turnRatio < -200 || turnRatio > 200)
            throw new ArgumentException("Turn ratio must be between -200 and 200", "turnRatio");

        AddOpcode(Enums.Opcode.OutputStepSync);
        AddParameter((byte)0x00);
        AddParameter((byte)ports.getValue());
        AddParameter((byte)speed);
        AddParameter(turnRatio);
        AddParameter(step);
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Synchronize timing of motors.
    /// </summary>
    /// <param name="ports">The port or ports to which the stop command will be sent.</param>
    /// <param name="speed">Speed to turn the motor(s). (-100 to 100)</param>
    /// <param name="turnRatio">The turn ratio to apply. (-200 to 200)</param>
    /// <param name="time">The time to turn the motor(s).</param>
    /// <param name="brake">Brake or coast at the end.</param>
    /// <returns></returns>
    public void TimeMotorSync(OutputPort ports, int speed, short turnRatio, int time, boolean brake) throws ArgumentException {
        if (speed < -100 || speed > 100)
            throw new ArgumentException("Speed must be between -100 and 100", "speed");

        if (turnRatio < -200 || turnRatio > 200)
            throw new ArgumentException("Turn ratio must be between -200 and 200", "turnRatio");

        AddOpcode(Enums.Opcode.OutputTimeSync);
        AddParameter((byte)0x00);
        AddParameter((byte)ports.getValue());
        AddParameter((byte)speed);
        AddParameter(turnRatio);
        AddParameter(time);
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Append the Stop Motor command to an existing Command object
    /// </summary>
    /// <param name="ports">Port or ports to stop</param>
    /// <param name="brake">Apply the brake at the end of the command</param>
    public void StopMotor(OutputPort ports, boolean brake) {
        AddOpcode(Enums.Opcode.OutputStop);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)ports.getValue());    // ports
        AddParameter((byte)(brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    }

    /// <summary>
    /// Append the Clear All Devices command to an existing Command object
    /// </summary>
    public void ClearAllDevices() {
        AddOpcode(Enums.Opcode.InputDevice_ClearAll);
        AddParameter((byte)0x00);            // layer
    }

    /// <summary>
    /// Append the Clear Changes command to an existing Command object
    /// </summary>
    public void ClearChanges(Enums.InputPort port) {
        AddOpcode(Enums.Opcode.InputDevice_ClearChanges);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)port.getValue());            // port
    }

    /// <summary>
    /// Append the Play Tone command to an existing Command object
    /// </summary>
    /// <param name="volume">Volme to play the tone (0-100)</param>
    /// <param name="frequency">Frequency of the tone in hertz</param>
    /// <param name="duration">Duration of the tone in milliseconds</param>
    public void PlayTone(int volume, short frequency, short duration) throws ArgumentException {
        if (volume < 0 || volume > 100)
            throw new ArgumentException("Volume must be between 0 and 100", "volume");

        AddOpcode(Enums.Opcode.Sound_Tone);
        AddParameter((byte)volume);        // volume
        AddParameter(frequency);    // frequency
        AddParameter(duration);    // duration (ms)
    }

    /// <summary>
    /// Append the Play Sound command to an existing Command object
    /// </summary>
    /// <param name="volume">Volume to play the sound</param>
    /// <param name="filename">Filename on the Brick of the sound to play</param>
    public void PlaySound(int volume, String filename) {
        AddOpcode(Enums.Opcode.Sound_Play);
        AddParameter((byte)volume);
        AddParameter(filename);
    }

    /// <summary>
    /// Append the Get Firmware Version command to an existing Command object
    /// </summary>
    /// <param name="maxLength">Maximum length of string to be returned</param>
    /// <param name="index">Index at which the data should be returned inside of the global buffer</param>
    public void GetFirwmareVersion(int maxLength, int index) throws ArgumentException {
        if (maxLength > 0xff)
            throw new ArgumentException("String length cannot be greater than 255 bytes", "maxLength");
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.UIRead_GetFirmware);
        AddParameter((byte)maxLength);        // global buffer size
        AddGlobalIndex((byte)index);        // index where buffer begins
    }

    /// <summary>
    /// Add the Is Brick Pressed command to an existing Command object
    /// </summary>
    /// <param name="button">Button to check</param>
    /// <param name="index">Index at which the data should be returned inside of the global buffer</param>
    public void IsBrickButtonPressed(Enums.BrickButton button, int index) throws ArgumentException {
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.UIButton_Pressed);
        AddParameter((byte)button.ordinal());
        AddGlobalIndex((byte)index);
    }

    /// <summary>
    /// Append the Set LED Pattern command to an existing Command object
    /// </summary>
    /// <param name="ledPattern">The LED pattern to display</param>
    public void SetLedPattern(Enums.LedPattern ledPattern) {
        AddOpcode(Enums.Opcode.UIWrite_LED);
        AddParameter((byte)ledPattern.ordinal());
    }

    /// <summary>
    /// Append the Clean UI command to an existing Command object
    /// </summary>
    public void CleanUI() {
        AddOpcode(Enums.Opcode.UIDraw_Clean);
    }

    /// <summary>
    /// Append the Draw Line command to an existing Command object
    /// </summary>
    /// <param name="color">Color of the line</param>
    /// <param name="x0">X start</param>
    /// <param name="y0">Y start</param>
    /// <param name="x1">X end</param>
    /// <param name="y1">Y end</param>
    public void DrawLine(Enums.Color color, short x0, short y0, short x1, short y1) {
        AddOpcode(Enums.Opcode.UIDraw_Line);
        AddParameter((byte)color.ordinal());
        AddParameter(x0);
        AddParameter(y0);
        AddParameter(x1);
        AddParameter(y1);
    }

    /// <summary>
    /// Append the Draw Pixel command to an existing Command object
    /// </summary>
    /// <param name="color">Color of the pixel</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    public void DrawPixel(Enums.Color color, short x, short y) {
        AddOpcode(Enums.Opcode.UIDraw_Pixel);
        AddParameter((byte)color.ordinal());
        AddParameter(x);
        AddParameter(y);
    }

    /// <summary>
    /// Append the Draw Rectangle command to an existing Command object
    /// </summary>
    /// <param name="color">Color of the rectangle</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="width">Width of rectangle</param>
    /// <param name="height">Height of the rectangle</param>
    /// <param name="filled">Draw a filled or empty rectangle</param>
    public void DrawRectangle(Enums.Color color, short x, short y, short width, short height, boolean filled) {
        AddOpcode(filled ? Enums.Opcode.UIDraw_FillRect : Enums.Opcode.UIDraw_Rect);
        AddParameter((byte)color.ordinal());
        AddParameter(x);
        AddParameter(y);
        AddParameter(width);
        AddParameter(height);
    }

    /// <summary>
    /// Append the Draw Inverse Rectangle command to an existing Command object
    /// </summary>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="width">Width of rectangle</param>
    /// <param name="height">Height of rectangle</param>
    public void DrawInverseRectangle(short x, short y, short width, short height) {
        AddOpcode(Enums.Opcode.UIDraw_InverseRect);
        AddParameter(x);
        AddParameter(y);
        AddParameter(width);
        AddParameter(height);
    }

    /// <summary>
    /// Append the Draw Circle command to an existing Command object
    /// </summary>
    /// <param name="color">Color of the circle</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="radius">Radius of circle</param>
    /// <param name="filled">Draw a filled or empty circle</param>
    public void DrawCircle(Enums.Color color, short x, short y, short radius, boolean filled) {
        AddOpcode(filled ? Enums.Opcode.UIDraw_FillCircle : Enums.Opcode.UIDraw_Circle);
        AddParameter((byte)color.ordinal());
        AddParameter(x);
        AddParameter(y);
        AddParameter(radius);
    }

    /// <summary>
    /// Append the Draw Text command to an existing Command object
    /// </summary>
    /// <param name="color">Color of the text</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="text">Text to draw</param>
    public void DrawText(Enums.Color color, short x, short y, String text) {
        AddOpcode(Enums.Opcode.UIDraw_Text);
        AddParameter((byte)color.ordinal());
        AddParameter(x);
        AddParameter(y);
        AddParameter(text);
    }

    /// <summary>
    /// Append the Draw Fill Window command to an existing Command object
    /// </summary>
    /// <param name="color">The color to fill</param>
    /// <param name="y0">Y start</param>
    /// <param name="y1">Y end</param>
    public void DrawFillWindow(Enums.Color color, short y0, short y1) {
        AddOpcode(Enums.Opcode.UIDraw_FillWindow);
        AddParameter((byte)color.ordinal());
        AddParameter(y0);
        AddParameter(y1);
    }

    /// <summary>
    /// Append the Draw Image command to an existing Command object
    /// </summary>
    /// <param name="color">The color of the image to draw</param>
    /// <param name="x">X position</param>
    /// <param name="y">Y position</param>
    /// <param name="devicePath">Filename on the brick of the image to draw</param>
    public void DrawImage(Enums.Color color, short x, short y, String devicePath) {
        AddOpcode(Enums.Opcode.UIDraw_BmpFile);
        AddParameter((byte)color.ordinal());
        AddParameter(x);
        AddParameter(y);
        AddParameter(devicePath);
    }

    /// <summary>
    /// Append the Select Font command to an existing Command object
    /// </summary>
    /// <param name="fontType">The font to select</param>
    public void SelectFont(Enums.FontType fontType) {
        AddOpcode(Enums.Opcode.UIDraw_SelectFont);
        AddParameter((byte)fontType.ordinal());
    }

    /// <summary>
    /// Append the Enable Top Line command to an existing Command object
    /// </summary>
    /// <param name="enabled">Enable/disable the top status bar line</param>
    public void EnableTopLine(boolean enabled) {
        AddOpcode(Enums.Opcode.UIDraw_Topline);
        AddParameter((byte)(enabled ? 1 : 0));
    }

    /// <summary>
    /// Append the Draw Dotted Line command to an existing Command object
    /// </summary>
    /// <param name="color">The color of the line</param>
    /// <param name="x0">X start</param>
    /// <param name="y0">Y start</param>
    /// <param name="x1">X end</param>
    /// <param name="y1">Y end</param>
    /// <param name="onPixels">Number of pixels the line is on</param>
    /// <param name="offPixels">Number of pixels the line is off</param>
    public void DrawDottedLine(Enums.Color color, short x0, short y0, short x1, short y1, short onPixels, short offPixels) {
        AddOpcode(Enums.Opcode.UIDraw_DotLine);
        AddParameter((byte)color.ordinal());
        AddParameter(x0);
        AddParameter(y0);
        AddParameter(x1);
        AddParameter(y1);
        AddParameter(onPixels);
        AddParameter(offPixels);
    }

    /// <summary>
    /// Append the Update UI command to an existing Command object
    /// </summary>
    public void UpdateUI() {
        AddOpcode(Enums.Opcode.UIDraw_Update);
    }

    /// <summary>
    /// Append the Delete File command to an existing Command object
    /// </summary>
    /// <param name="devicePath">Filename on the brick to delete</param>
    public void DeleteFile(String devicePath) {
        AddOpcode(Enums.SystemOpcode.DeleteFile);
        AddRawParameter(devicePath);
    }

    /// <summary>
    /// Append the Create Directory command to an existing Command object
    /// </summary>
    /// <param name="devicePath">Directory name on the brick to create</param>
    public void CreateDirectory(String devicePath) {
        AddOpcode(Enums.SystemOpcode.CreateDirectory);
        AddRawParameter(devicePath);
    }

    /// <summary>
    /// Append the Get Type/Mode command to an existing Command object
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="typeIndex">The index to hold the Type value in the global buffer</param>
    /// <param name="modeIndex">The index to hold the Mode value in the global buffer</param>
    public void GetTypeMode(Enums.InputPort port, int typeIndex, int modeIndex) throws ArgumentException {
        if (typeIndex > 1024)
            throw new ArgumentException("Index for Type cannot be greater than 1024", "typeIndex");
        if (modeIndex > 1024)
            throw new ArgumentException("Index for Mode cannot be greater than 1024", "modeIndex");

        AddOpcode(Enums.Opcode.InputDevice_GetTypeMode);
        AddParameter((byte)0x00);            // layer
        AddParameter((byte)port.getValue());    // port
        AddGlobalIndex((byte)typeIndex);    // index for type
        AddGlobalIndex((byte)modeIndex);    // index for mode
    }

    /// <summary>
    /// Append the Ready SI command to an existing Command object
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode to read the data as</param>
    /// <param name="index">The index to hold the return value in the global buffer</param>
    public void ReadySI(Enums.InputPort port, int mode, int index) throws ArgumentException {
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.InputDevice_ReadySI);
        AddParameter((byte)0x00);                // layer
        AddParameter((byte)port.getValue());        // port
        AddParameter((byte)0x00);                // type
        AddParameter((byte)mode);                // mode
        AddParameter((byte)0x01);                // # values
        AddGlobalIndex((byte)index);            // index for return data
    }

    /// <summary>
    /// Append the Ready Raw command to an existing Command object
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode to query the value as</param>
    /// <param name="index">The index in the global buffer to hold the return value</param>
    public void ReadyRaw(Enums.InputPort port, int mode, int index) throws ArgumentException {
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.InputDevice_ReadyRaw);
        AddParameter((byte)0x00);                // layer
        AddParameter((byte)port.getValue());        // port
        AddParameter((byte)0x00);                // type
        AddParameter((byte)mode);                // mode
        AddParameter((byte)0x01);                // # values
        AddGlobalIndex((byte)index);            // index for return data
    }

    /// <summary>
    /// Append the Ready Percent command to an existing Command object
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode to query the value as</param>
    /// <param name="index">The index in the global buffer to hold the return value</param>
    public void ReadyPercent(Enums.InputPort port, int mode, int index) throws ArgumentException {
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.InputDevice_ReadyPct);
        AddParameter((byte)0x00);                // layer
        AddParameter((byte)port.getValue());        // port
        AddParameter((byte)0x00);                // type
        AddParameter((byte)mode);                // mode
        AddParameter((byte)0x01);                // # values
        AddGlobalIndex((byte)index);            // index for return data
    }

    /// <summary>
    /// Append the Get Device Name command to an existing Command object
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="bufferSize">Size of the buffer to hold the returned data</param>
    /// <param name="index">Index to the position of the returned data in the global buffer</param>
    public void GetDeviceName(Enums.InputPort port, int bufferSize, int index) throws ArgumentException {
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.InputDevice_GetDeviceName);
        AddParameter((byte)0x00);
        AddParameter((byte)port.getValue());
        AddParameter((byte)bufferSize);
        AddGlobalIndex((byte)index);
    }

    /// <summary>
    /// Append the Get Mode Name command to an existing Command object
    /// </summary>
    /// <param name="port">The port to query</param>
    /// <param name="mode">The mode of the name to get</param>
    /// <param name="bufferSize">Size of the buffer to hold the returned data</param>
    /// <param name="index">Index to the position of the returned data in the global buffer</param>
    public void GetModeName(Enums.InputPort port, int mode, int bufferSize, int index) throws ArgumentException {
        if (index > 1024)
            throw new ArgumentException("Index cannot be greater than 1024", "index");

        AddOpcode(Enums.Opcode.InputDevice_GetModeName);
        AddParameter((byte)0x00);
        AddParameter((byte)port.getValue());
        AddParameter((byte)mode);
        AddParameter((byte)bufferSize);
        AddGlobalIndex((byte)index);
    }

    /// <summary>
    /// End and send a Command to the EV3 brick.
    /// </summary>
    /// <returns>A byte array containing the response from the brick, if any.</returns>
    byte[] SendCommand() throws ArgumentException {
        _brick.SendCommand(this);
        byte[] response = Response.Data;
        Initialize(Enums.CommandType.DirectNoReply);
        return response;
    }
}