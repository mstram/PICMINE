/**
 * @author Marcos Vives Del Sol
 * @version 1.0, 28/VI/12
 *
 * PICMINE
 * This class controls the device files
 * Based on PIC12F683 datasheet
 * 
 * Licensed under the CC-BY 3.0 (http://creativecommons.org/licenses/by/3.0/) license
 */
 
package org.s4x8.picmine;

public class PICMINE extends PIC14 {
	public int[] RAM = new int[96];
	public int[] stack = new int[8];
	public int FSR = 0;
	public int PCLATH = 0;
	public mcu ic;
	
	PICMINE(mcu theIc) {
		ic = theIc;
	};
	
	public int readF(int addr) {
		int i, bits;
		int val = 0;
		//System.out.println("READ: 0x" + Integer.toHexString(addr));
		
		if (addr == 0x00) { // 00: Indirect file addressing
			if (FSR == 0) return 0;
			return readF(FSR & 0x7F);
		};
		 
		if (addr == 0x02) { // 02: PCL
			return PC & 0xFF;
		};
		
		if (addr == 0x03) { // 03: STATUS
			return (Z << 2) | (DC << 1) | C;
		};
		
		if (addr == 0x04) { // 04: FSR
			return FSR;
		};
		
		if (addr == 0x0A) { // 0A: PCLATH
			return PCLATH;
		};
		
		if (addr >= 0x10 && addr < 0x20) { // 10-1F: Input pins
			return ic.inputData(addr - 0x10);
		};
		
		if (addr >= 0x20) { // 13-7F: RAM
			return RAM[addr - 0x20];
		};
		
		return 0;
	};
	
	public void writeF(int addr, int data) {
		int i, bits;
		//System.out.println("WRITE: 0x" + Integer.toHexString(addr) + " 0x" + Integer.toHexString(data));
		
		if (addr == 0x00) { // 00: Indirect file addressing
			if (FSR == 0) return;
			writeF(FSR & 0x7F, data);
		};
		
		if (addr == 0x02) { // 02: PCL
			PC = PCLATH << 8 + data;
		};
		
		if (addr == 0x04) { // 04: FSR
			FSR = data;
		};
		
		if (addr == 0x0A) { // 0A: PCLATH
			PCLATH = data;
		};
		
		if ((addr >= 0x10) && (addr < 0x20)) { // 10-1F: Output pins
			ic.outputData(addr - 0x10, data);
		};
		
		if (addr >= 0x20) { // 20-7F: RAM
			RAM[addr - 0x20] = data;
		};
	};
};
