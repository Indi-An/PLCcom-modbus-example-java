package example_app;

import java.util.ArrayList;

import com.indian.plccom.modbus.Enums.eDataType;
import com.indian.plccom.modbus.UnsignedDatatypes.*;

class Utilities {

	/**
	 * extract writeable values from rawdata string
	 * 
	 * @param ValueString
	 *            rawdata in string format
	 * @param ValueType
	 *            desired data type
	 * @return a sValues_to_Write Object
	 */
	static sValues_to_Write CheckValues(String ValueString, eDataType ValueType) {
		sValues_to_Write Result = new sValues_to_Write();

		try {

			if (ValueString.equals("") || ValueType == null) {
				Result.ParseError = true;
				return Result;
			}

			String Separator = "\n";
			ValueString = ValueString.replace("\r\n", "\n").trim();
			String[] rawValues = ValueString.split(Separator);

			if (ValueType == eDataType.STRING) {
				StringBuilder sb = new StringBuilder();
				for (String ValuePart : rawValues) {
					sb.append(ValuePart);
				}
				Result.values.add(sb.toString());
			} else {
				for (String ValuePart : rawValues) {
					try {

						switch (ValueType) {
						case BOOLEAN:
							Result.values.add(Boolean.valueOf(ValuePart));
							break;
						case BYTE:
							Result.values.add(Byte.valueOf(ValuePart));
						case UBYTE:
							Result.values.add(Short.valueOf(ValuePart));
							break;
						case SHORT:
							Result.values.add(Short.valueOf(ValuePart));
							break;
						case USHORT:
							Result.values.add(Integer.valueOf(ValuePart));
							break;
						case INTEGER:
							Result.values.add(Integer.valueOf(ValuePart));
							break;
						case UINTEGER:
							Result.values.add(Long.valueOf(ValuePart));
							break;
						case LONG:
							Result.values.add(Long.valueOf(ValuePart));
							break;
						case ULONG:
							Result.values.add(ULong.valueOf(ValuePart));
							break;
						case FLOAT:
							Result.values.add(Float.valueOf(ValuePart));
							break;
						case DOUBLE:
							Result.values.add(Double.valueOf(ValuePart));
							break;
						case STRING:
							Result.values.add(String.valueOf(ValuePart));
							break;
						case PLC_INT:
							Result.values.add(Short.valueOf(ValuePart));
							break;
						case PLC_DINT:
							Result.values.add(Integer.valueOf(ValuePart));
							break;
						case PLC_LINT:
							Result.values.add(Long.valueOf(ValuePart));
							break;
						case PLC_WORD:
							Result.values.add(Integer.valueOf(ValuePart));
							break;
						case PLC_DWORD:
							Result.values.add(Long.valueOf(ValuePart));
							break;
						case PLC_LWORD:
							Result.values.add(ULong.valueOf(ValuePart));
							break;
						case PLC_BCD16:
							Result.values.add(Short.valueOf(ValuePart));
							break;
						case PLC_BCD32:
							Result.values.add(Integer.valueOf(ValuePart));
							break;
						default:
							break;

						}

					} catch (Exception ex) {
						Result.ParseError = true;
						return Result;
					}
				}
			}
			return Result;
		} catch (Exception ex) {
			Result.ParseError = true;
			return Result;

		}
	}

	static class sValues_to_Write {

		// List<object> values = new List<object>();
		ArrayList<Object> values = new ArrayList<Object>();
		boolean ParseError;
	}

}
