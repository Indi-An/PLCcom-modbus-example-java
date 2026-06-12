// MIT License
// Copyright (c) Indi.An GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import static com.indian.plccom.modbus.UnsignedDatatypes.UBuilder.createUshort;

import com.indian.plccom.modbus.IncomingLogEntryEventHandler;
import com.indian.plccom.modbus.LogEntry;
import com.indian.plccom.modbus.ModbusSlave;
import com.indian.plccom.modbus.PortStateEventHandler;
import com.indian.plccom.modbus.iConnectionStateChangeEvent;
import com.indian.plccom.modbus.iIncomingLogEntryEvent;
import com.indian.plccom.modbus.Enums.eByteOrder;
import com.indian.plccom.modbus.Enums.eConnectionState;
import com.indian.plccom.modbus.Enums.eModbusRegion;
import com.indian.plccom.modbus.Enums.eRegisterMode;

/**
 * Workshop 12 - Simple Modbus TCP Slave.
 *
 * This workshop starts a small local Modbus TCP slave and exposes an in-memory
 * data store. Workshop 11 or any other Modbus master can connect to it and read
 * or write the prepared holding registers.
 */
public final class _12_Simple_Slave_TCP implements iConnectionStateChangeEvent, iIncomingLogEntryEvent {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String LISTENER_ID = "workshop-tcp";
	private static final int LISTEN_PORT = 502;
	private static final int SLAVE_ID = 1;

	private final IncomingLogEntryEventHandler incomingLogEntryEventHandler = new IncomingLogEntryEventHandler(this);
	private final PortStateEventHandler portStateEventHandler = new PortStateEventHandler(this);

	private _12_Simple_Slave_TCP() {
	}

	/**
	 * Starts the workshop.
	 *
	 * @param args
	 *            command-line arguments; not used by this workshop
	 * @throws Exception
	 *             when console input fails unexpectedly
	 */
	public static void main(String[] args) throws Exception {
		new _12_Simple_Slave_TCP().run();
	}

	private void run() throws Exception {
		PLCcomConsole.open("PLCcom for Modbus - Workshop 12", 1000);
		System.out.println("╔════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 12: Simple Modbus TCP Slave           ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Starts a local Modbus TCP slave and publishes a small in-memory    ║");
		System.out.println("║ holding-register data store.                                       ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ What you will learn:                                               ║");
		System.out.println("║   * Create a ModbusSlave with a clear slave id                     ║");
		System.out.println("║   * Preload holding-register values                                ║");
		System.out.println("║   * Open and close a TCP listener safely                           ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Used by Workshop 11 and any local Modbus TCP master                ║");
		System.out.println("╚════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusSlave slave = null;
		try {
			slave = new ModbusSlave(LICENSE_USER_NAME, LICENSE_SERIAL, SLAVE_ID, eRegisterMode._16Bit,
					eByteOrder.AB_CD);

			// Step 1: Register diagnostic events before the listener starts.
			// Port-state changes are useful during commissioning, and incoming
			// log entries show the request flow created by a master.
			slave.addIncomingLogEntryEventHandler(incomingLogEntryEventHandler);
			slave.addPortStateEventHandler(portStateEventHandler);

			// Step 2: Preload visible sample data. These are the values that
			// Workshop 11 reads first. The unsigned helper type belongs to the
			// SDK's existing UnsignedDatatypes package.
			slave.setValue(createUshort(0), eModbusRegion.HoldingRegister, createUshort(100));
			slave.setValue(createUshort(1), eModbusRegion.HoldingRegister, createUshort(101));
			slave.setValue(createUshort(2), eModbusRegion.HoldingRegister, createUshort(102));
			slave.setValue(createUshort(3), eModbusRegion.HoldingRegister, createUshort(103));

			// Step 3: Open the TCP listener. The listener id is the application
			// handle used later to replace or remove the listener.
			slave.addOrReplaceListener_TCP(LISTENER_ID, LISTEN_PORT);

			System.out.println("The Modbus TCP slave is listening on port " + LISTEN_PORT + ".");
			System.out.println("Start Workshop 11 in a second console window to read the values.");
			System.out.println();
			System.out.println("Press ENTER to stop the listener.");
			PLCcomConsole.readLine("  Press ENTER to stop the listener...");
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			PLCcomConsole.waitForEnter();
		} catch (Throwable ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			PLCcomConsole.waitForEnter();
		} finally {
			if (slave != null) {
				slave.removeListener(LISTENER_ID);
				slave.removeIncomingLogEntryEventHandler(incomingLogEntryEventHandler);
				slave.removePortStateEventHandler(portStateEventHandler);
				slave.unload();
			}
		}
		PLCcomConsole.close();
	}

	/**
	 * Reports a listener state change.
	 *
	 * @param arg
	 *            the new listener or connector state supplied by PLCcom
	 */
	@Override
	public void On_ConnectionStateChange(eConnectionState arg) {
		System.out.println("Listener state: " + arg);
	}

	/**
	 * Reports diagnostic log entries emitted by the slave.
	 *
	 * @param arg
	 *            log entries raised by the slave for the current request flow
	 */
	@Override
	public void OnIncomingLogEntry(LogEntry[] arg) {
		for (LogEntry entry : arg) {
			System.out.println(entry);
		}
	}

}
