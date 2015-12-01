/*
 * Int.java
 *
 * @author Johannes Roith <roith@in.tum.de>
 * Created on 08. November 2007, 20:00
 * 
 * To use the command:
 * - pass 0x21 as the only parameter 
 * - set AH to 0xA
 * - set DX to the address where you want the input string to be written
 * - set the first byte at [DX] to the max number of characters that can be entered
 * 
 * - now execute "INT 0x21" and enter the string into the dialog box.
 *
 */

package jasmin.commands;

import jasmin.core.*;

public class Int extends jasmin.core.JasminCommand {

	public String[] getID() {
		return new String[]{"INT", "INTO"};
	}

	public ParseError validate(Parameters p) {
		ParseError e = p.validate(0, Op.I8);
		if (e != null) {
			return e;
		}
		return p.validate(1, Op.NULL);
	}

	public void execute(Parameters p) {
		int id = (int) p.get(0);
		switch (id) {
			case 0x21:
				handleDOSInterrupt(p);
				break;
			case 0x80:
				handleLinuxSyscall(p);
				break;
			default:
				throw new SyscallNotImplementedException("Unimplemented syscall: " + id);
		}
	}

	private void handleLinuxSyscall(Parameters p) {
		int function = (int) p.get(dataspace.EAX);
		System.out.println(function);
		switch (function) {
			case 0:
				Linux.handleRead(p);
				break;
			default:
				throw new SyscallNotImplementedException("Linux syscall: " + function);
		}
	}

	private static class Linux {
		public static void handleRead(Parameters p) {
			String read;
			int fd = (int) p.get(p.dsp.EBX);
			int target = (int) p.get(p.dsp.ECX);
			int maxLen = (int) p.get(p.dsp.EDX);
			switch (fd) {
				case 0:
					read = readLine(maxLen);
					break;
				default:
					throw new SyscallNotImplementedException("Linux read: " + fd);
			}
			char[] chars = read.toCharArray();
			Address argument = new Address(Op.MEM, 1, target);
			// write the input to memory
			for (int i = 0; i < chars.length; i++) {
				if (i >= maxLen) {
					break;
				}
				p.dsp.put(chars[i], argument, null);
				argument.address++;
			}
		}
	}

	private void handleDOSInterrupt(Parameters p) {
		int function = (int) p.get(dataspace.AH);
		// Buffered Input handler
		if (function == 0x0A) {
			// create internal argument for mem access
			int target = (int) p.get(dataspace.DX);
			Address argument = new Address(Op.MEM, 1, target);
			// read max input size from "byte [dx]"
			int maxsize = (int) dataspace.getUpdate(argument, false);
			// show dialog box, read user's input
			char[] chars = readLine(maxsize - 1)
					.concat("\0")// write NULL byte after input string
					.toCharArray();
			// write actual size of input into the byte after the definition of max size
			argument.address++;
			dataspace.put(chars.length, argument, null);
			// write the input to memory, beginning at the next byte after the input's size
			for (int i = 0; i < chars.length; i++) {
				if (i >= (maxsize)) {
					break;
				}
				argument.address++;
				dataspace.put(chars[i], argument, null);
			}
		}
	}

	private static String readLine(int maxLength) {
		String input = javax.swing.JOptionPane.showInputDialog("Input (max. " + maxLength + " characters):");
		if (input != null) {
			if (input.length() > maxLength) {
				return input.substring(0, maxLength);
			} else {
				return input;
			}
		}
		return "";
	}

	public static class SyscallNotImplementedException extends RuntimeException {
		public SyscallNotImplementedException() {
		}

		public SyscallNotImplementedException(String message) {
			super(message);
		}

		public SyscallNotImplementedException(String message, Throwable cause) {
			super(message, cause);
		}

		public SyscallNotImplementedException(Throwable cause) {
			super(cause);
		}
	}

}
