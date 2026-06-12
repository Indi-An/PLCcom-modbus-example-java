package example_app;

import static com.indian.plccom.modbus.UnsignedDatatypes.UBuilder.*;

import java.util.ArrayList;
import java.util.BitSet;

import javax.swing.*;

import com.indian.plccom.modbus.UnsignedDatatypes.*;

/**
 * object stores and uses the current values ​​of the bitbar checkboxes
 * 
 * @author Indi.Systems GmbH
 * @author <a target="_blank" href="http://www.plccom.net">www.plccom.net</a>
 * @author <a href="mailto:support@indi.an.de">support@indi.an.de</a>
 * @since 1.0
 * @version PLCCom (Java) for Modbus V5
 */
class customCheckBoxes {

	// <editor-fold defaultstate="collapsed" desc="private Member">
	private ArrayList<JCheckBox> mCheckBoxes = new ArrayList<JCheckBox>();

	/**
	 * get byte array from unsigned short
	 * 
	 * @param value
	 *            a UShort value
	 * @return the byte array based of value
	 */
	private static byte[] getBytes(UShort value) {
			
		byte[] Bytes = { (byte) (value.intValue()  & 0xff),
		 		 		(byte) (value.intValue()>> 8 & 0xff)};
		return Bytes;
	}

	/**
	 * get byte array from unsigned int
	 * 
	 * @param value
	 *            a UInteger value
	 * @return the byte array based of value
	 */
	private static byte[] getBytes(UInteger value) {
		byte[] Bytes = { (byte) (value.longValue()  & 0xff),
		 		 (byte) (value.longValue()>> 8 & 0xff),
		 		(byte) (value.longValue()>> 16 & 0xff),
		 		(byte) (value.longValue()>> 24 & 0xff)};
		return Bytes;
	}

	/**
	 * get java.util.BitSet from byte
	 * 
	 * @param b
	 *            a byte value
	 * @return the java.util.BitSet based of value
	 */
	static BitSet toBitSet(byte b) {
		BitSet bs = new BitSet(Byte.SIZE);
		for (int i = 0; i < Byte.SIZE; i++) {
			if (((b >> i) & 1) == 1) {
				bs.set(i);
			}
		}
		return bs;
	}

	/**
	 * get java.util.BitSet from byte array
	 * 
	 * @param b
	 *            a byte array value
	 * @return the java.util.BitSet based of value
	 */
	static BitSet toBitSet(byte[] bArray) {
		BitSet bs = new BitSet(Byte.SIZE * bArray.length);
		for (int iArray = 0; iArray < bArray.length; iArray++) {
			for (int i = 0; i < Byte.SIZE; i++) {
				if (((bArray[iArray] >> i) & 1) == 1) {
					bs.set((iArray * 8) + i);
				}
			}
		}
		return bs;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="internal member">

	/**
	 * get actual checkbox instances
	 * 
	 * @return
	 */
	JCheckBox[] getCheckBoxes() {
		return mCheckBoxes.toArray(new JCheckBox[mCheckBoxes.size()]);
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="global member">

	/**
	 * add a JCheckBox to internal collection
	 * 
	 * @param value
	 *            JCheckBox
	 */
	void addCheckBox(JCheckBox value) {
		mCheckBoxes.add(value);
	}

	/**
	 * enabled or disabled check state
	 * 
	 * @param index
	 *            index from desired checkbox
	 * @param value
	 *            true or false
	 * @return true, if the operation successfully, otherwise false
	 */
	boolean setChecked(int index, boolean value) {
		try {
			if (mCheckBoxes.size() < index - 1)
				return false;
			mCheckBoxes.get(index).setSelected(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * set enabled state of checkbox
	 * 
	 * @param index
	 *            index from desired checkbox
	 * @param value
	 *            true or false
	 * @return true, if the operation successfully, otherwise false
	 */
	boolean setEnabled(int index, boolean value) {
		try {
			if (mCheckBoxes.size() < index - 1)
				return false;
			mCheckBoxes.get(index).setEnabled(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * enabled all ckecboxes of bitbar
	 * 
	 * @param Enable16BitBoxes
	 *            enable bitbar 0-15
	 * @param Enable32BitBoxes
	 *            enable bitbar 16-31
	 * @return true, if the operation successfully, otherwise false
	 */
	boolean enableBitBarComplete(boolean Enable16BitBoxes,
			boolean Enable32BitBoxes) {
		try {
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				if (i < 16) {
					mCheckBoxes.get(i).setEnabled(Enable16BitBoxes);
				} else {
					mCheckBoxes.get(i).setEnabled(Enable32BitBoxes);
				}
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * set complete bitbar to true or false
	 * 
	 * @param value
	 *            true or false
	 * @return true, if the operation successfully, otherwise false
	 */
	boolean setBitBarComplete(boolean value) {
		try {
			for (JCheckBox c : mCheckBoxes) {
				c.setSelected(value);
			}

			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * set bitbar depending on the value
	 * 
	 * @param value
	 *            a UInteger value
	 */
	void setCheckBoxesfromValue(UInteger value) {
		byte[] b = getBytes(value);
		BitSet BitAr = toBitSet(b);
		for (int i = 0; i < Byte.SIZE * 4; i++) {
			mCheckBoxes.get(i).setSelected(BitAr.get(i));
		}
	}

	/**
	 * set bitbar depending on the value
	 * 
	 * @param value
	 *            a UShort value
	 */
	void setCheckBoxesfromValue(UShort value) {
		byte[] b = getBytes(value);
		BitSet BitAr = toBitSet(b);
		for (int i = 0; i < Byte.SIZE * 2; i++) {
			mCheckBoxes.get(i).setSelected(BitAr.get(i));
		}
	}

	/**
	 * get value depending on the current bitbar
	 * 
	 * @return a UInteger value
	 */
	UInteger getValue() {
		UInteger Value = createUint(0);
		for (int i = 0; i < mCheckBoxes.size(); i++) {
			if (mCheckBoxes.get(i).isSelected())
				Value = createUint(Value.longValue() + createUint(1 << i).longValue());
		}
		return Value;
	}

	// </editor-fold>
}
