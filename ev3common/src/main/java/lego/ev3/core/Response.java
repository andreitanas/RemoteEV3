package lego.ev3.core;

/**
 * Created by Andrei Tanas on 14-11-27.
 */
class Response {
    public Enums.ReplyType ReplyType;
    public short Sequence;
    public Object Event;
    public byte[] Data;
    public Enums.SystemOpcode SystemCommand;
    public Enums.SystemReplyStatus SystemReplyStatus;

    public Response(short sequence) {
        Sequence = sequence;
        Event = new Object();
    }
}