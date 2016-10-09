package se.mah.ae5929.brosgeodata.main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Zarokhan on 2016-10-07.
 * My bound service
 * Bound service allows cross communication between service and activity/controller
 * Good because allows for controller to access service public methods
 */
public class TCPConnectionService extends Service {

    public static final String IP = "195.178.227.53";
    public static final int PORT = 7117;
    private static final String TAG = TCPConnectionService.class.getName();
    private final IBinder mBro = new TCPConnectionBinder();

    private RunOnThread thread;
    private Receive receive;
    private Buffer<String> receiveBuffer;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private InetAddress address;
    private Exception exception;

    /* Service override methods & Binder class */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        thread = new RunOnThread();
        receiveBuffer = new Buffer<String>();
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }
    // Service binder class
    public class TCPConnectionBinder extends Binder {
        public TCPConnectionService getService(){
            return TCPConnectionService.this;
        }
    }
    // Service bind call
    @Override
    public IBinder onBind(Intent intent) {
        return mBro;
    }

    /* My own methods */

    public void connect() {
        thread.start();
        thread.execute(new Connect());
        Log.d(TAG, "connect");
    }

    public void disconnect() {
        thread.execute(new Disconnect());
        Log.d(TAG, "disconnect");
    }

    public void send(Expression exp) {
        thread.execute(new Send(exp));
        Log.d(TAG, "send");
    }

    public String receive() throws InterruptedException {
        return receiveBuffer.get();
    }

    public Exception getException() {
        Exception result = exception;
        exception = null;
        return result;
    }

    /* My own classes */
    private class Receive extends Thread {
        public void run() {
            Log.d(TAG, "Receiveing");
            String result = null;
            try {
                while (result != null) {
                    result = (String) input.readObject();
                    receiveBuffer.put(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Connect implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "Connecting");
            try {
                address = InetAddress.getByName(IP);
                socket = new Socket(address, PORT);
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                receiveBuffer.put("CONNECTED");
                receive = new Receive();
                receive.start();
            } catch (Exception e) {
                exception = e;
                receiveBuffer.put("EXCEPTION");
            }
        }
    }

    private class Disconnect implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "Disconnecting");
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (socket != null)
                    socket.close();
                thread.stop();
                receiveBuffer.put("CLOSED");
            } catch (Exception e) {
                exception = e;
                receiveBuffer.put("EXCEPTION");
            }
        }
    }

    private class Send implements Runnable {
        private Expression exp;

        public Send(Expression exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            Log.d(TAG, "Sending");
            try {
                output.writeObject(exp);
                output.flush();
            } catch (Exception e) {
                exception = e;
                receiveBuffer.put("EXCEPTION");
            }
        }
    }
}
