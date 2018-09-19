package com.etwge.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_ENABLE_BT = 0x1001;
	private static final UUID MY_UUID = UUID.fromString("466bf7fe-3fd7-4274-81f7-8638e2fc1203");

	BluetoothAdapter mBluetoothAdapter;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a ListView
				//				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				Log.i(TAG, "find devices:" + device.getName() + "\n" + device.getAddress());
				mAdapter.appendData(device);
			}
		}
	};
	private BluetoothDevicesAdapter mAdapter;
	private EditText mEditSendContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initBluetooth();
		registerBroadcast();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private void initView() {
		mEditSendContent = findViewById(R.id.edit_send_content);
		findViewById(R.id.button_scan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAdapter != null) {
					mAdapter.setDataList(null);
					scanDevices();
				}
			}
		});

		RecyclerView recyclerView = findViewById(R.id.recycler_bluetooth_devices);
		recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
		final DividerItemDecoration itemDecoration = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
		final Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.shape_horizontal_divider);
		if (drawable != null) {
			itemDecoration.setDrawable(drawable);
		}
		recyclerView.addItemDecoration(itemDecoration);
		recyclerView.setAdapter(mAdapter = new BluetoothDevicesAdapter(MainActivity.this));
		mAdapter.setCallback(new BluetoothDevicesAdapter.Callback() {
			@Override
			public void onItemClick(final BluetoothDevice bean) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Thread thread = new ConnectThread(bean);
						thread.start();
					}
				}).start();

			}
		});
	}

	private void registerBroadcast() {
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				if (resultCode == RESULT_OK) {
					Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_LONG).show();
				} else if (resultCode == RESULT_CANCELED) {
					Toast.makeText(MainActivity.this, "蓝牙未打开", Toast.LENGTH_LONG).show();
				}
				break;
		}
	}

	private void scanDevices() {
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.startDiscovery();
		}
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


	private class ConnectThread extends Thread {
		private  BluetoothSocket mmSocket;
		private  BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();
			BluetoothSocket tmp = null;
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				connectException.printStackTrace();
				try {
					mmSocket.close();
				} catch (IOException closeException) {
					closeException.printStackTrace();
				}
				return;
				// Do work to manage the connection (in a separate thread)
			}
			manageConnectedSocket(mmSocket);
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
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

				final String data = mEditSendContent.getText().toString();
				Log.i(TAG, "write data:" + data);
				write(data.getBytes());
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					// Send the obtained bytes to the UI activity
					//					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
					//							.sendToTarget();

					String str = new String(buffer,0,bytes, "utf-8");
					Log.i(TAG, "client receive: " + str);
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

	private void manageConnectedSocket(BluetoothSocket mmSocket) {
		Log.i(TAG, "manageConnect");
		final ConnectedThread connectedThread = new ConnectedThread(mmSocket);
		connectedThread.start();

	}
}
