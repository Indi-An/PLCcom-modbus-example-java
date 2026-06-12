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

import com.indian.plccom.modbus.ModbusMaster;
import com.indian.plccom.modbus.OperationResult;
import com.indian.plccom.modbus.ReadRequest;
import com.indian.plccom.modbus.ReadResult;
import com.indian.plccom.modbus.ReadValue;
import com.indian.plccom.modbus.RequestBuilder;
import com.indian.plccom.modbus.WriteRequest;
import com.indian.plccom.modbus.WriteResult;
import com.indian.plccom.modbus.Enums.eByteOrder;
import com.indian.plccom.modbus.Enums.eDataType;
import com.indian.plccom.modbus.Enums.eReadFunction;
import com.indian.plccom.modbus.Enums.eRegisterMode;
import com.indian.plccom.modbus.Enums.eWriteFunction;

/**
 * Workshop 11 - Simple Modbus TCP Master.
 *
 * This workshop is the first small master-side building block. It connects to a
 * Modbus TCP slave, reads a few holding registers, writes two values, and reads
 * the same range again so the change is visible immediately.
 *
 * Start Workshop 12 in a second console first. It provides the local slave on
 * {@code 127.0.0.1:502} and preloads the holding-register values used here.
 */
public final class _11_Simple_Master_TCP {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String SLAVE_HOST = "127.0.0.1";
	private static final int SLAVE_PORT = 502;
	private static final int SLAVE_ID = 1;

	private _11_Simple_Master_TCP() {
	}

	/**
	 * Starts the workshop.
	 *
	 * @param args
	 *            command-line arguments; not used by this workshop
	 * @throws Exception
	 *             when console input or a Modbus operation fails unexpectedly
	 */
	public static void main(String[] args) throws Exception {
		PLCcomConsole.open("PLCcom for Modbus - Workshop 11", 1000);
		System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 11: Simple Modbus TCP Master            ║");
		System.out.println("║                                                                      ║");
		System.out.println("║ Connects to a local slave, reads holding registers, writes two       ║");
		System.out.println("║ values, and verifies the result with a second read.                  ║");
		System.out.println("║                                                                      ║");
		System.out.println("║ What you will learn:                                                 ║");
		System.out.println("║   * Create a licensed ModbusMaster instance                          ║");
		System.out.println("║   * Configure the standard Modbus TCP connector                      ║");
		System.out.println("║   * Read, write, and verify holding-register values                  ║");
		System.out.println("║                                                                      ║");
		System.out.println("║ Required peer: Workshop 12 on 127.0.0.1:502                          ║");
		System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusMaster master = null;
		try {
			master = new ModbusMaster(LICENSE_USER_NAME, LICENSE_SERIAL);

			// Step 1: Select the plain Modbus TCP connector. The master is the
			// active peer: it opens a client connection to the slave endpoint.
			master.setConnector_TCP(SLAVE_HOST, SLAVE_PORT);
			master.setRegisterMode(eRegisterMode._16Bit);
			master.getConnector().setMaxIdleTime(5000);

			// Step 2: Build a normal function-code 03 request. Addresses are
			// zero-based, so address 0 means holding register 40001 in classic
			// Modbus notation.
			ReadRequest readRequest = RequestBuilder.ReadRequestBuilder.create(SLAVE_ID,
					eReadFunction.F03_Read_Holding_Registers, 0, eDataType.USHORT, 4);
			readRequest.setByteOrder(eByteOrder.AB_CD);
			printReadResult("Initial read", master.read(readRequest));

			// Step 3: Function-code 16 writes multiple holding registers. The
			// datatype is selected by the add method; here we write unsigned
			// 16-bit values that match the slave's 16-bit register mode.
			WriteRequest writeRequest = RequestBuilder.WriteRequestBuilder.create(SLAVE_ID,
					eWriteFunction.F16_Write_Multiple_Registers, 0);
			writeRequest.setByteOrder(eByteOrder.AB_CD);
			writeRequest.addUShortRange(new int[] { 2100, 2101 });
			printWriteResult("Write holding registers 0..1", master.write(writeRequest));

			// Step 4: Read the same range again. Reusing the original request
			// keeps the before/after comparison honest: only the slave values
			// should have changed.
			printReadResult("Read back after write", master.read(readRequest));
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			System.err.println("Make sure Workshop 12 is running on " + SLAVE_HOST + ":" + SLAVE_PORT + ".");
		} finally {
			if (master != null) {
				master.unload();
			}
		}
		System.out.println();
		PLCcomConsole.waitForEnter();
		PLCcomConsole.close();
	}


	private static void printReadResult(String title, ReadResult result) {
		System.out.println(title + ": " + result.getMessage());

		if (result.getQuality() != OperationResult.eQuality.GOOD) {
			System.out.println();
			return;
		}

		for (ReadValue value : result.fetchValues()) {
			System.out.println("  Holding register " + value.getAddress() + " = " + value.getValue());
		}
		System.out.println();
	}

	private static void printWriteResult(String title, WriteResult result) {
		System.out.println(title + ": " + result.getMessage());
		System.out.println();
	}
}
