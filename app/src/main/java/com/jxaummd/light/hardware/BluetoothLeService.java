/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxaummd.light.hardware;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jxaummd.light.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {


    //获取简单的类名
	private final static String TAG = "---------------------";

	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	private BluetoothManager mBluetoothManager; //蓝牙管理器
	private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
	private String mBluetoothDeviceAddress; //蓝牙设备地址
	private BluetoothGatt mBluetoothGatt;//设备扫描
	private BluetoothLeScanner blescanner;//扫描回调接口
	private ScanCallback scanCallback;//蓝牙设备
	private BluetoothDevice bledevice;
	private String   myUuid  = null;    //我的UUID
	private String   defaultUuid  = "0000ffe1-0000-1000-8000-00805f9b34fb";  //默认UUID
	private int mConnectionState = STATE_DISCONNECTED;
	public static final int STATE_DISCONNECTED = 11;
	public static final int STATE_CONNECTING = 12;
	public static final int STATE_CONNECTED = 13;
    public static final int STATE_RECIVERDATA = 14;
    public static final  int STAETE_HAVEGETMYCHARACTER = 15;
    public  static  final  int  STATE_RESULT_SUCCESS = 16;
	public  static  final  int  STATE_RESULT_FAILED = 17;
	public  static  final  int  STATE_RESULT_FINDDEVICE = 18;

    private BluetoothGattCharacteristic bluetoothGattCharacteristic = null;

	//连接回调方法
    private  BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
			    //发送已连接广播
				mConnectionState = STATE_CONNECTED;
				EventBus.getDefault().post(new BleOperator(BluetoothLeService.STATE_CONNECTED,"已连接"));
                mBluetoothGatt.discoverServices();
			    }
			    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mConnectionState = STATE_DISCONNECTED;
                    EventBus.getDefault().post(new BleOperator(BluetoothLeService.STATE_DISCONNECTED,"失去连接"));
                    mBluetoothGatt.discoverServices();
			    }
		}


		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d( "-----------------------","获取服务成功了！" );
                if (myUuid==null) {
					Log.w("提示：", "你的uuid为null，使用系统默认的uuid！");
					getMycharacteristic(gatt.getServices(),defaultUuid);
				}else
					getMycharacteristic(gatt.getServices(),myUuid);

			} else {
			    Log.d( "-----------------------","获取服务失败！");
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("onCharacteristicRead");
			if (status == BluetoothGatt.GATT_SUCCESS) {
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
		}

		//特性改变回调接口
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			if (characteristic.getValue() != null) {
				EventBus.getDefault().post(new BleOperator(BluetoothLeService.STATE_RECIVERDATA,characteristic.getStringValue(0)));
                Log.d("-------","接收到了数据"+characteristic.getStringValue(0));
            }
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			System.out.println("rssi = " + rssi);
		}

		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d( "-----------------------","--------write success----- status:" + status);
		}
	};



    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initialize(); //创建服务就初始化蓝牙
        Log.d("BleServiceLog","成功启动服务");
    }

     @Override
     public void onDestroy() {
        super.onDestroy();
        //解除EventBus
        EventBus.getDefault().unregister(this);
        Log.d("BleServiceLog","服务已经销毁");
    }


     @Nullable
     @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //订阅事件
	@Subscribe(threadMode = ThreadMode.MAIN)
	public  void  onReciverEvent(BleOperator operator){
	    switch (operator.getOperatorMode()){
	        //根据地址连接蓝牙
            case BleOperator.OPERATOR_CONNECT:
                connect(operator.getData());
                myUuid=operator.getMyUuid();
                break;
            case BleOperator.OPERATOR_DISCONNECT:
                disconnect(); //断开连接
                break;

            case BleOperator.OPERATOR_SENDDATA:
                //发送数据
                if(bluetoothGattCharacteristic!=null)
                  wirteCharacteristic(bluetoothGattCharacteristic,operator.getData());
                else
                    EventBus.getDefault().post(new BleOperator(BluetoothLeService.STATE_RESULT_FAILED,"空特性！"));
                break;

            case BleOperator.OPERATOR_ENABLE_NPTIFY:
                if(bluetoothGattCharacteristic!=null)
                    setCharacteristicNotification(bluetoothGattCharacteristic,true);
                else
                    EventBus.getDefault().post(new BleOperator(BluetoothLeService.STATE_RESULT_FAILED,"空特性！"));
                break;

			case BleOperator.OPERATOR_INITSCANER:
				if (initScaner())
					EventBus.getDefault().post(new BleOperator(BluetoothLeService.STATE_RESULT_SUCCESS,"初始化扫描成功！"));
				break;

			case BleOperator.OPERATOR_STARTSCANER:
				startScan(operator.getData());
				break;

			case BleOperator.OPERATOR_STOPSCANER:
				stopScan(0);
				break;
        }

    }

    //初始化扫描
    public  boolean initScaner(){
		blescanner = mBluetoothAdapter.getBluetoothLeScanner();
		if (blescanner==null)
			return false;
		return  true;
	}

	//开始扫描
	public  boolean startScan(final String devicrname){
    	if (blescanner == null)
    		return  false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			scanCallback= new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    //将获取的设备，通过EventBus发送出去
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						if(devicrname.equals(result.getDevice().getName())) {
                            BleOperator bleOperator = new BleOperator(BluetoothLeService.STATE_RESULT_FINDDEVICE, "找到了"+devicrname);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bleOperator.setmDevice(result.getDevice());
                            }
                            EventBus.getDefault().post(bleOperator);
                            stopScan(0);      //停止扫描
                        }
					}
				}
            };
		}
		blescanner.startScan(scanCallback);
		Log.d("","真的开始扫描");
    	return true;
	}

	//结束扫描
	public  boolean stopScan(final int time) {
		if (blescanner == null)
			return false;
			if (time == 0) {
				blescanner.stopScan(scanCallback);
			}else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(time * 1000);
								EventBus.getDefault().post(new BleOperator(BleOperator.OPERATOR_STOPSCANER,"定时停止扫描"));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}).start();
			}
			return true;


	}



	private void getMycharacteristic(List<BluetoothGattService> services,String uuid) {
		//用一个StringBuddider类
		final StringBuilder builder = new StringBuilder();
		for (BluetoothGattService bleserver : services) {
			//追加服务
			builder.append("服务：" + bleserver.getUuid() + "\n");
			//获取特性
			List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = bleserver.getCharacteristics();
			//追加特性
			for (BluetoothGattCharacteristic bluetoothgatt :
					bluetoothGattCharacteristics) {
				builder.append("特性:" + bluetoothgatt.getUuid() + "\n");
				if (bluetoothgatt.getUuid().toString().equals(uuid)) {
					bluetoothGattCharacteristic = bluetoothgatt;
					//已经得到特性
					mConnectionState=STAETE_HAVEGETMYCHARACTER;
					EventBus.getDefault().post(new BleOperator(BluetoothLeService.STAETE_HAVEGETMYCHARACTER,"真的连接上了！"));
				}
			}

		}
		Log.d("获取到服务",builder.toString());
	}


	//初始化连接
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		Log.d("BleServiceLog","成功初始化蓝牙！");
		return true;
	}


	//开始连接蓝牙
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
            Log.d("BleServiceLog","地址初始化错误！");
			return false;
		}
		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null&& address.equals(mBluetoothDeviceAddress)&& mBluetoothGatt != null) {
			Log.d("BleServiceLog","此设备已经连接！");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		//根据地址获取远程设备
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.d("BleServiceLog","Device not found.  Unable to connect.");
			return false;
		}
        //进行连接
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d("BleServiceLog","Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}


	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.d("BleServiceLog", "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
		close();
        Log.d("BleServiceLog", "已经断开连接！");
	}

	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	//写特性
	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic,String data) {

		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d( "-----------------------","BluetoothAdapter not initialized");
			return;
		}

		MyApplication.MyToast(data);
        characteristic.setValue(data.getBytes());
		mBluetoothGatt.writeCharacteristic(characteristic);
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	//读特性
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}



	/**
	 *
	 * 打开或者关闭一个特性的通知
	 *
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(CLIENT_CHARACTERISTIC_CONFIG));
		if (descriptor != null) {
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;
		return mBluetoothGatt.getServices();
	}

	public boolean getRssiVal() {
		if (mBluetoothGatt == null)
			return false;
		return mBluetoothGatt.readRemoteRssi();
	}


}
