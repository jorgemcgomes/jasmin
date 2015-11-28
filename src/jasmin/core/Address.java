package jasmin.core;

/**
 * @author Jakob Kummerow
 */

public class Address {
	
	public int type = Op.NULL;
	public int datatype = Fpu.NOFPUDATA;
	public int size = -1;
	public int address = -1;
	public long value = 0;
	public boolean dynamic = false;
	
	// special stuff for registers
	public LongWrapper shortcut;
	public int rshift;
	public long mask;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Address address1 = (Address) o;

		if (type != address1.type) return false;
		if (size != address1.size) return false;
		if (address != address1.address) return false;
		return mask == address1.mask;

	}

	@Override
	public int hashCode() {
		int result = type;
		result = 31 * result + size;
		result = 31 * result + address;
		result = 31 * result + (int) (mask ^ (mask >>> 32));
		return result;
	}

	public Address(int aType, int aSize, int aAddress) {
		type = aType;
		size = aSize;
		address = aAddress;
		if (Op.matches(aType, Op.MEM | Op.REG | Op.FPUREG)) {
			dynamic = true;
		}
	}
	
	public Address(int aType, int aSize, long aValue) {
		type = aType;
		size = aSize;
		value = aValue;
	}
	
	public Address clone() {
		Address a = new Address(type, size, address);
		a.datatype = datatype;
		a.value = value;
		a.dynamic = dynamic;
		return a;
	}
	
	public boolean containsAddress(int address) {
		return ((address >= this.address) && (address < this.address + this.size));
	}
	
	public long getShortcut() {
		return (shortcut.value & mask) >> rshift;
	}
	
}
