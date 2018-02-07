package lego.ev3.core;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by Andrei Tanas on 14-11-27.
 */
class ResponseManager {
    private static int _nextSequence = 0x0001;
    static Dictionary<Integer, Response> Responses = new Hashtable<Integer, Response>();

    private static short GetSequenceNumber() {
        if (_nextSequence == 65535)
            _nextSequence = 0;

        return (short)_nextSequence++;
    }

    static Response CreateResponse() {
        short sequence = GetSequenceNumber();

        Response r = new Response(sequence);
        Responses.put((int)sequence, r);
        return r;
    }

    static void WaitForResponse(Response r) {
        try {
            synchronized (r.Event) {
                r.Event.wait(1000);
            }
            Responses.remove(r.Sequence);
        } catch (InterruptedException e) {
            r.ReplyType = Enums.ReplyType.DirectReplyError;
        }
    }

    static void HandleResponse(byte[] report) {
        if (report == null || report.length < 3)
            return;

        short sequence = (short)(report[0] | (report[1] << 8));
        int replyType = report[2];

        //System.Diagnostics.Debug.WriteLine("Size: " + report.Length + ", Sequence: " + sequence + ", Type: " + (ReplyType)replyType + ", Report: " + BitConverter.ToString(report));

        if (sequence > 0) {
            Response r = Responses.get(sequence);

            r.ReplyType = Enums.ReplyType.fromValue(replyType);

            if (r.ReplyType == Enums.ReplyType.DirectReply || r.ReplyType == Enums.ReplyType.DirectReplyError) {
                r.Data = new byte[report.length - 3];
                System.arraycopy(report, 3, r.Data, 0, report.length - 3);
            } else if (r.ReplyType == Enums.ReplyType.SystemReply || r.ReplyType == Enums.ReplyType.SystemReplyError) {
                r.SystemCommand = Enums.SystemOpcode.fromValue(report[3]);

                r.SystemReplyStatus = Enums.SystemReplyStatus.fromValue(report[4]);

                r.Data = new byte[report.length - 5];
                System.arraycopy(report, 5, r.Data, 0, report.length - 5);
            }

            synchronized (r.Event) {
                r.Event.notify();
            }
        }
    }
}