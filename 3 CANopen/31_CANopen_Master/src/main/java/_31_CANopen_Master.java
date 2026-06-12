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

import com.indian.plccom.modbus.CANopenProtocolControl;
import com.indian.plccom.modbus.CANopenTransportRequest;
import com.indian.plccom.modbus.CANopenTransportResult;
import com.indian.plccom.modbus.ModbusMaster;
import com.indian.plccom.modbus.OperationResult;
import com.indian.plccom.modbus.PortStateEventHandler;
import com.indian.plccom.modbus.RequestBuilder;
import com.indian.plccom.modbus.iConnectionStateChangeEvent;
import com.indian.plccom.modbus.Enums.eBaudrate;
import com.indian.plccom.modbus.Enums.eCANopenAccessFlag;
import com.indian.plccom.modbus.Enums.eCANopenDataTypes;
import com.indian.plccom.modbus.Enums.eConnectionState;
import com.indian.plccom.modbus.Enums.eDataBits;
import com.indian.plccom.modbus.Enums.eFlowControl;
import com.indian.plccom.modbus.Enums.eParity;
import com.indian.plccom.modbus.Enums.eRegisterMode;
import com.indian.plccom.modbus.Enums.eStopBits;

/**
 * Workshop 31 - Encapsulated CANopen Master.
 *
 * PLCcom for Modbus can send CANopen-style requests through Modbus function 43
 * by using the CANopen transport request builder. This workshop keeps the
 * connector setup, read request, write request, and diagnostics in one file so
 * the whole flow is easy to follow.
 */
public final class _31_CANopen_Master implements iConnectionStateChangeEvent {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String COM_PORT = "COM3";
	private static final int SLAVE_ID = 1;

	private final PortStateEventHandler portStateEventHandler = new PortStateEventHandler(this);

	private _31_CANopen_Master() {
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
		new _31_CANopen_Master().run();
	}

	private void run() throws Exception {
		PLCcomConsole.open("PLCcom for Modbus - Workshop 31", 1000);
		System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 31: Encapsulated CANopen Master       ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Uses Modbus function 43 to send CANopen-style read and write       ║");
		System.out.println("║ requests through a Modbus/CANopen gateway.                         ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ What you will learn:                                               ║");
		System.out.println("║   * Configure the physical connector before talking to hardware    ║");
		System.out.println("║   * Create encapsulated CANopen read and write requests            ║");
		System.out.println("║   * Inspect CANopen transport results before automating further    ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Requires a target that supports encapsulated CANopen transport     ║");
		System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusMaster device = null;
		try {
			// Step 1: Create and configure the PLCcom master. In this workshop the
			// master talks to a Modbus/CANopen gateway. The gateway receives normal
			// Modbus frames and forwards the encapsulated payload into the CANopen
			// network behind it.
			device = createAndInitDevice();

			// Step 2: Read an object dictionary entry through Modbus function 43.
			// Keep the read and write samples separate so each request can be
			// inspected and adapted to a real device independently.
			runCanopenRead(device);

			// Step 3: Send a write request through the same transport. A real
			// commissioning workflow should normally read back the affected object
			// or check a device-specific status value afterwards.
			runCanopenWrite(device);
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			System.err.println("Check the serial port and make sure the target supports Modbus function 43.");
		} finally {
			if (device != null) {
				device.unload();
			}
		}
		System.out.println();
		PLCcomConsole.waitForEnter();
		PLCcomConsole.close();
	}

	private ModbusMaster createAndInitDevice() {
		// The ModbusMaster object is still the central PLCcom entry point. The
		// CANopen-specific part begins later when the request is created.
		ModbusMaster device = new ModbusMaster(LICENSE_USER_NAME, LICENSE_SERIAL);

		// Choose the physical transport used to reach the Modbus/CANopen
		// gateway. The classic hardware setup uses RTU; TCP and UDP are often
		// easier during lab integration and are shown below as alternatives.
		device.setConnector_RTU(COM_PORT, eBaudrate.b9600, eDataBits.DataBits8, eParity.None, eStopBits.One,
				eFlowControl.None);

		// Alternative connector examples:
		// device.setConnector_TCP("192.168.1.21", 502);
		// device.setConnector_UDP("192.168.1.21", 502);

		// A connection-state handler is useful when testing hardware because it
		// shows whether the serial line or network connector opens as expected.
		device.getConnector().setMaxIdleTime(5000);
		device.getConnector().addConnectionStateEventHandler(portStateEventHandler);

		// Encapsulated CANopen transport is carried by Modbus. The register mode
		// therefore still belongs to the Modbus connector side of the setup.
		device.setRegisterMode(eRegisterMode._16Bit);
		return device;
	}

	private static void runCanopenRead(ModbusMaster device) {
		System.out.println("Starting encapsulated CANopen read request.");

		// A CANopen object is addressed by node id, index, and subindex. This
		// request reads two bytes from node id 1, index 0x3032, subindex 1. When
		// using real hardware, take these three values from the target device's
		// object dictionary or EDS documentation.
		CANopenTransportRequest request = RequestBuilder.CANopenTransportRequestBuilder.create(SLAVE_ID,
				new CANopenProtocolControl(false, (byte) 0, false, (byte) 0, false, eCANopenDataTypes.unsigned8,
						eCANopenAccessFlag.read),
				(short) 0x01, 0x3032, (short) 0x01, 0, 2);

		// Printing the request before sending it is deliberate. During workshop
		// work it helps compare the desired CANopen object access with the actual
		// frame PLCcom is going to transmit through the gateway.
		System.out.println(request);
		CANopenTransportResult response = device.CANopenTransport(request);
		printCanopenResult(response);
	}

	private static void runCanopenWrite(ModbusMaster device) {
		System.out.println("Starting encapsulated CANopen write request.");

		// This request writes four bytes to node id 1, index 0x0002, subindex 0.
		// The start address is a Modbus-side transport parameter used by the
		// gateway. The byte array is the CANopen payload that will be written to
		// the selected object.
		CANopenTransportRequest request = RequestBuilder.CANopenTransportRequestBuilder.create(SLAVE_ID,
				new CANopenProtocolControl(false, (byte) 0, false, (byte) 0, false, eCANopenDataTypes.unsigned8,
						eCANopenAccessFlag.write),
				(short) 0x01, 0x0002, (short) 0x00, 10, new byte[] { 0x00, 0x01, 0x02, 0x03 });

		// In production code, follow a successful write with a readback or with a
		// device-specific status check if the target object supports it.
		System.out.println(request);
		CANopenTransportResult response = device.CANopenTransport(request);
		printCanopenResult(response);
	}

	private static void printCanopenResult(CANopenTransportResult response) {
		// The transport result contains both the general PLCcom quality and, when
		// the access succeeds, the CANopen payload bytes returned by the gateway.
		System.out.println(response);
		System.out.println("Quality => " + response.getQuality() + " " + response.getMessage());

		if (response.getQuality() == OperationResult.eQuality.GOOD && response.getReadingData() != null) {
			byte[] data = response.getReadingData();
			for (int i = 0; i < data.length; i++) {
				// Display every returned byte explicitly. That makes byte order and
				// payload length visible before the sample is adapted to typed data.
				System.out.println(String.format("  %04d >> 0x%02X", Integer.valueOf(i), Byte.valueOf(data[i])));
			}
		}
		System.out.println();
	}

	/**
	 * Reports connector state changes while the Modbus/CANopen gateway is used.
	 *
	 * @param arg
	 *            the new connector state supplied by PLCcom
	 */
	@Override
	public void On_ConnectionStateChange(eConnectionState arg) {
		System.out.println("Connection state: " + arg);
	}

}
