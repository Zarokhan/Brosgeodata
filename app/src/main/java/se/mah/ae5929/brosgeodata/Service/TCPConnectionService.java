package se.mah.ae5929.brosgeodata.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    private DataInputStream input;
    private DataOutputStream output;
    private InetAddress address;
    private Exception exception;
    private boolean isConnected;

    /* Service override methods & Binder class */
    @Override
    public void onCreate() {
        thread = new RunOnThread();
        receiveBuffer = new Buffer<String>();
        connect();
        Log.d(TAG, "onStartCommand");
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

    public void send(String jsonMessage) {
        thread.execute(new Send(jsonMessage));
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

    public boolean isConnected() {
        return isConnected;
    }

    /* My own classes */
    private class Receive extends Thread {
        public void run() {
            Log.d(TAG, "Receiveing");
            String result = null;
            try {
                while (result != null) {
                    result = input.readUTF();
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
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.flush();
                receiveBuffer.put("CONNECTED");
                receive = new Receive();
                receive.start();
                isConnected = true;
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
                isConnected = false;
            } catch (Exception e) {
                exception = e;
                receiveBuffer.put("EXCEPTION");
            }
        }
    }

    private class Send implements Runnable {
        String jsonMessage = null;

        public Send(String jsonMessage) {
            this.jsonMessage = jsonMessage;
        }

        @Override
        public void run() {
            Log.d(TAG, "Sending");
            try {
                //output.writeObject(exp);
                output.writeUTF(jsonMessage);
                output.flush();
            } catch (Exception e) {
                exception = e;
                receiveBuffer.put("EXCEPTION");
            }
        }
    }
}
