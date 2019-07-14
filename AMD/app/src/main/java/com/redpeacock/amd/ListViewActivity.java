package com.redpeacock.amd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ListViewActivity extends AppCompatActivity
{
    private String medData;
    private TextView nameTextView;
    private TextView qtyTextView;
    private TextView typeTextView;
    private TextView medTextView;
    private String nameBuffer;
    private String typeBuffer;
    private String qtyBuffer;
    private String medicamentBuffer;
    private String medCodeBuffer;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;
    private String bluetoothDevice = "HC-05";
    private String bluetoothMAC;
    private int bluetoothFOUND;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private int counter;
    volatile boolean stopWorker;
    private String dataString;
    BluetoothSocket mmSocket;
    private Button orderButton;
    private Button helpButton;
    //private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        medData=getIntent().getStringExtra("Medication Data");
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        qtyTextView = (TextView) findViewById(R.id.qty_textview);
        typeTextView = (TextView) findViewById(R.id.type_textview);
        medTextView = (TextView) findViewById(R.id.med_textview);
        orderButton = (Button) findViewById(R.id.orderButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        qrCodeParser(medData);
        nameTextView.setText(nameBuffer);
        qtyTextView.setInputType(qtyTextView.getInputType()| InputType.TYPE_TEXT_FLAG_MULTI_LINE );
        qtyTextView.setSingleLine(false);
        qtyTextView.setText(qtyBuffer);
        typeTextView.setInputType(typeTextView.getInputType()| InputType.TYPE_TEXT_FLAG_MULTI_LINE );
        typeTextView.setSingleLine(false);
        typeTextView.setText(typeBuffer);
        medTextView.setInputType(medTextView.getInputType()| InputType.TYPE_TEXT_FLAG_MULTI_LINE );
        medTextView.setSingleLine(false);
        medTextView.setText(medicamentBuffer);

        mmOutputStream = new OutputStream()
        {
            @Override
            public void write(int i) throws IOException
            {

            }
        };

        bluetoothON();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        try
        {
            bluetoothFindDevices();

        }//end try
        catch(IOException e)
        {
            Toast.makeText(this,"Cant find devices or device not working",Toast.LENGTH_SHORT).show();
        }//end catch
        try
        {
            bluetoothSendData(medCodeBuffer);
        }
        catch(IOException e)
        {
            Toast.makeText(this,"Cant send message",Toast.LENGTH_SHORT).show();
        }
        final Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("EXIT", true);


        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    bluetoothDisconnect();
                }
                catch (IOException e)
                {

                }

                startActivity(intent);
            }
        });

    }/*end onCreate method*/

    public void qrCodeParser(String rawData)
    {

        int i;
        char j=0;
        int k=0;
        int l=0;
        int m=0;

        int nameBufferStCounter;
        int nameBufferEndCounter;
        int medBufferStCounter;
        int medBufferEndCounter;
        int typeBufferStCounter;
        int typeBufferEndCounter;
        int qtyBufferStCounter;
        int qtyBufferEndCounter;
        int medCodeBufferCounter;


        for(i=0; i< rawData.length();i++)
        {
            switch (rawData.charAt(i))
            {
                case '@':
                {
                    nameBufferStCounter = i;
                    nameBufferEndCounter = i;
                    while (j != '$')
                    {
                        j=rawData.charAt(nameBufferEndCounter);
                        nameBufferEndCounter++;
                    }/*end while*/
                    nameBuffer= rawData.substring(nameBufferStCounter+1,nameBufferEndCounter-1);
                    //nameBuffer.replace(nameBuffer.charAt(nameBufferStCounter),' ');
                    //nameBuffer = nameBuffer.replace(nameBuffer.charAt(nameBufferEndCounter),' ');
                }/*end '@' case */
                break;
                case '$':
                {
                    medBufferStCounter = i;
                    medBufferEndCounter=i;
                    while(k != '&')
                    {
                        k=rawData.charAt(medBufferEndCounter);
                        medBufferEndCounter++;
                    }/*end while*/
                    medicamentBuffer= rawData.substring(medBufferStCounter+1,medBufferEndCounter-1);
                }/*end case '$'*/
                break;
                case '&':
                {
                    typeBufferStCounter = i;
                    typeBufferEndCounter=i;
                    while(l != '!')
                    {
                        l=rawData.charAt(typeBufferEndCounter);
                        typeBufferEndCounter++;
                    }/*end while*/
                    typeBuffer = rawData.substring(typeBufferStCounter+1,typeBufferEndCounter-1);
                }/*end case*/
                break;
                case '!':
                {
                    qtyBufferStCounter = i;
                    qtyBufferEndCounter = i;
                    while(m != '*')
                    {
                        m = rawData.charAt(qtyBufferEndCounter);
                        qtyBufferEndCounter++;
                    }
                    qtyBuffer = rawData.substring(qtyBufferStCounter+1,qtyBufferEndCounter-1);
                }/*end case*/
                break;
                case '*':
                {
                    medCodeBufferCounter = i;
                    medCodeBuffer = rawData.substring(medCodeBufferCounter+1,rawData.length());
                }
                default:
                {
                   if(rawData.charAt(0) != '@') Toast.makeText(getApplicationContext(),"Codigo QR no es valido",Toast.LENGTH_SHORT).show();
                }/*end default case*/
            }/*end switch*/
        }/*end for*/
    }/*end qrCodeParser*/

 private  void bluetoothON()
 {
     mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //obtiene el driver de Bluetooth
     if (mBluetoothAdapter == null) {
         Toast.makeText(this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show(); //Telefono no posee bluetooth
     }

     if (!mBluetoothAdapter.isEnabled()) {
         Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Se  pide permiso para prender Bluetooth
         startActivityForResult(enableBluetooth, 0);
     }
 }//end of bluetooth module turn on method

 private void bluetoothFindDevices() throws IOException
 {
     Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

     if (pairedDevices.size() > 0)
     {
         // There are paired devices. Get the name and address of each paired device.
         for (BluetoothDevice device : pairedDevices)
         {
             String deviceName = device.getName();
             //String deviceHardwareAddress = device.getAddress(); // MAC address
             if(deviceName.equals(bluetoothDevice))
             {
                 mmDevice = device;
                 UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
                 //"00001101-0000-1000-8000-00805F9B34FB"
                 mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                 mmSocket.connect();
                 mmOutputStream = mmSocket.getOutputStream();
                 mmInputStream = mmSocket.getInputStream();
                 beginListenForData();

                 break;
             }//end if
         }//end search for pairedDevices
     }//end if
 }//end bluetoothFindDevices

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 35; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),data.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }//end while
            }
        });//end workerThread

        workerThread.start();
    }//end beginListenForData method

    void bluetoothDisconnect() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();

    }//end bluetoothDisconnect method

    void bluetoothSendData(String msg) throws IOException
    {
        // String msg = myTextbox.getText().toString();
        msg += "\r";
        mmOutputStream.write(msg.getBytes());
    }//end bluetoothSendData

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevice= device.getName();
                bluetoothMAC= device.getAddress(); // MAC address
            }
        }
    };

}/*end ListViewActivity*/
