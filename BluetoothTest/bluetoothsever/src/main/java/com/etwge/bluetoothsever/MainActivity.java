package com.etwge.bluetoothsever;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_ENABLE_BT = 0x1001;
	private static final String NAME = "XGateServer";
	private static final UUID MY_UUID = UUID.fromString("466bf7fe-3fd7-4274-81f7-8638e2fc1203");

	BluetoothAdapter mBluetoothAdapter;
	ConnectedThread mConnectedThread;
	AcceptThread mAcceptThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initBluetooth();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
		}
	}

	private void initData() {
		mAcceptThread = new AcceptThread();
		mAcceptThread.start();

	}

	private void initView() {
		TextView uuid = findViewById(R.id.text_uuid);
		uuid.setText("uuid:" + MY_UUID);
		Log.i(TAG, "uid:" + MY_UUID);
	}

	private void initBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Device does not support Bluetooth");
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client code
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mmServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				// If a connection was accepted
				if (socket != null) {
					// Do work to manage the connection (in a separate thread)
					manageConnectedSocket(socket);
					try {
						mmServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) { }
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream     mmInStream;
		private final OutputStream    mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024];  // buffer store for the stream
			int bytes; // bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					// Send the obtained bytes to the UI activity
//					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//							.sendToTarget();
					final String str = new String(buffer, 0, bytes, "utf-8");
					Log.i(TAG, "receive: " + str);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
						}
					});
					write((str + ",too").getBytes());
					cancel();
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) { }
		}
	}

	private void manageConnectedSocket(BluetoothSocket socket) {
		Log.i(TAG, "server manageConnect");
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
//		connectedThread.write(mEditSend.getText().toString().getBytes());
	}
}
