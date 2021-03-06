package com.serial.serialmod;



import binaryCommunication.BinaryByte;
import interpretation.SerialMessageInterpreter;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class SerialInterface implements SerialPortEventListener
{
	private SerialPort serialPort = null; //holds the serial port object once one connects to one

	
    public static String[] searchForPorts()
    {
    	 return SerialPortList.getPortNames();
    }

    public SerialPort getConnectedPort() {return this.serialPort;}
    
    public void connect(String selectedPort) throws SerialPortException
    {
    	if(serialPort!=null) {throw new SerialPortException("", "", "Already connected to a port");}
    	
    	this.serialPort = new SerialPort(selectedPort);
        serialPort.openPort(); //Open serial port
        serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        serialPort.addEventListener(this);
    }

    public void sendMessage(byte[] bytes) throws SerialPortException {
		//serialPort.writeBytes(bytes);
	}
	
	public void sendMessage(String str) throws SerialPortException {
		serialPort.writeString(str);
	}
	
	public void disconnect() throws SerialPortException {
			if(serialPort.isOpened()) {serialPort.closePort();}
			serialPort=null;
	}
	
	public static void repeat(String value) throws SerialPortException {
		SerialMessageInterpreter.interpret(value.getBytes());
	}
	
	public void echoPackage(binaryCommunication.Package package1) {
		SerialMessageInterpreter.interpret(package1.getRawData());
	}
	
	public static void repeat(byte[] bytes) throws SerialPortException {
		SerialMessageInterpreter.interpret(bytes);
	}
	

	public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR()){
            try {
            	int value = event.getEventValue();
            	byte[] receivedData=serialPort.readBytes(value);
            	byte [] fourByteArray= new byte[4];
            	int i=0;
            	
            	
            	//divide received data into 4 byte packages
            		//assuming all data is sent in 4 byte packages
            	while(i<receivedData.length){
            		if(i%4==0) {fourByteArray= new byte[4];}
            		fourByteArray[i%4]=receivedData[i];
            		i++;
            		if(i%4==0) {SerialMessageInterpreter.interpret(fourByteArray);}   
            	}
            	//if the recived data length isn't a multiple of 4, interpret the incomplete array
            	if(receivedData.length%4!=0) {SerialMessageInterpreter.interpret(fourByteArray);}
                 
            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
            
        }
    }
	


}


