/**
 * @author Marcos Vives Del Sol
 * @version 1.0, 28/VI/12
 *
 * PIC14.java
 * A mid-range Microchip(R) PIC 14bits CPU emulator
 * This base should be extended to implement program memory and data memory (files)
 * Based on PIC12F683 datasheet
 * 
 * Licensed under the CC-BY 3.0 (http://creativecommons.org/licenses/by/3.0/) license
 */

package org.s4x8.picmine;

public class PIC14 {
	// Main registers
	public int PC = 0;
	public int W = 0;
	
	// Stack stuff
	public int[] stack;
	public int stackPointer = 0;
	
	// Status bits
	public int C = 0;
	public int DC = 0;
	public int Z = 0;
	
	public int readF(int file) {
		// INHERIT ME
		return 0;
	};
	
	public void writeF(int file, int data) {
		// INHERIT ME
	};
	
	public void clearWatchdog() {
		// Unused unless you want to implement a Watchdog timer
	};
	
	public void sleep() {
		// Unused unless you want the PIC to sleep
	};
	
	public int addition(int addend1, int addend2) {
		int result = addend1 + addend2;
		DC = ((addend1 & 0xF) + (addend2 & 0xF) >= 0x10) ? 1 : 0;
		C = (result >= 0x100) ? 1 : 0;
		return result;
	};
	
	public int substraction(int minuend, int substrahend) {
		int result = minuend - substrahend;
		C = (result >= 0) ? 1 : 0;
		DC = ((minuend & 0xF) - (substrahend & 0xF) >= 0) ? 1 : 0;
		return result;
	};
	
	public int setBit(int data, int bit, boolean status) {
		return (data & ~(1 << bit)) | ((status ? 1 : 0) << bit);
	};
	
	public boolean isBitSet(int data, int bit) {
		return (data & (1 << bit)) > 0;
	};
	
	public void push() {
		stack[stackPointer] = PC;
		stackPointer = mod(stackPointer + 1, stack.length);
	};
	
	public int pop() {
		stackPointer = mod(stackPointer - 1, stack.length);
		return stack[stackPointer];
	};
	
	public int mod(int num, int mod) {
		num %= mod;
		if (num < 0) num = num + mod;
		return num;
	};
	
	public boolean mask(int inst, int base, int mask) {
		return (inst & mask) == base;
	};
	
	public void step(int inst) {
		boolean toFile = mask(inst, 0x0080, 0x0080);
		int file = inst & 0x7F;
		int bit = (inst >> 7) & 7;
		int literal = inst & 0xFF;
		int address = inst & 0x7FF;
		int result = 0x7FFFFFFF;
		boolean updateZeroFlag = true;
		PC++;
		
		// BYTE-ORIENTED FILE REGISTER OPERATIONS
		if (mask(inst, 0x0700, 0x3F00)) {	// ADDWF
			result = addition(readF(file), W);
		} else
		
		if (mask(inst, 0x0500, 0x3F00)) {	// ANDWF
			result = readF(file) & W;
		} else
		
		if (mask(inst, 0x0100, 0x3F00)) {	// CLRF / CLRW
			result = 0;
		} else
		
		if (mask(inst, 0x0900, 0x3F00)) {	// COMF
			result = ~readF(file);
		} else
		
		if (mask(inst, 0x0300, 0x3F00)) {	// DECF
			result = readF(file) - 1;
		} else
		
		if (mask(inst, 0x0B00, 0x3F00)) {	// DECFSZ
			result = readF(file) - 1;
			if (result == 0) PC++;
			updateZeroFlag = false;
		} else
		
		if (mask(inst, 0x0A00, 0x3F00)) {	// INCF
			result = readF(file) + 1;
		} else
		
		if (mask(inst, 0x0F00, 0x3F00)) {	// INCFSZ
			result = readF(file) + 1;
			if (result == 0) PC++;
			updateZeroFlag = false;
		} else
		
		if (mask(inst, 0x0400, 0x3F00)) {	// IORWF
			result = readF(file) | W;
		} else
		
		if (mask(inst, 0x0800, 0x3F00)) {	// MOVF
			result = readF(file);
		} else
		
		if (mask(inst, 0x0080, 0x3F80)) {	// MOVWF
			result = W;
			updateZeroFlag = false;
		} else
		
		if (mask(inst, 0x0000, 0x3F9F)) {	// NOP
			// No Operation
		} else
		
		if (mask(inst, 0x0D00, 0x3F00)) {	// RLF
			result = (readF(file) << 1) | C;
			C = result >> 8;
			updateZeroFlag = false;
		} else
		
		if (mask(inst, 0x0C00, 0x3F00)) {	// RRF
			result = readF(file) | (C << 8);
			C = result & 1;
			result = result >> 1;
			updateZeroFlag = false;
		} else
		
		if (mask(inst, 0x0200, 0x3F00)) {	// SUBWF
			result = substraction(readF(file), W);
		} else
		
		if (mask(inst, 0x0E00, 0x3F00)) {	// SWAPF
			result = readF(file);
			result = (result << 4) | (result >> 4);
			updateZeroFlag = false;
		} else
		
		if (mask(inst, 0x0600, 0x3F00)) {	// XORWF
			result = readF(file) ^ W;
		} else
		
		{	// LITERAL AND CONTROL
			toFile = false;
		
			if (mask(inst, 0x3E00, 0x3E00)) {	// ADDLW
				result = addition(literal, W);
			} else
			
			if (mask(inst, 0x3900, 0x3F00)) {	// ANDLW
				result = literal & W;
			} else
			
			if (mask(inst, 0x2000, 0x3800)) {	// CALL
				push();
				PC = address;
			} else
			
			if (inst == 0x0064) {	// CLRWDT
				clearWatchdog();
			} else
				
			if (mask(inst, 0x2800, 0x3800)) {	// GOTO
				PC = address;
			} else
			
			if (mask(inst, 0x3800, 0x3F00)) {	// IORLW
				result = literal | W;
			} else
			
			if (mask(inst, 0x3000, 0x3C00)) {	// MOVLW
				result = literal;
				updateZeroFlag = false;
			} else
			
			if (inst == 0x0008) {	// RETFIE
				// TODO!
				PC = pop();
			} else
			
			if (mask(inst, 0x3400, 0x3C00)) {	// RETLW
				PC = pop();
				W = literal;
				updateZeroFlag = false;
			} else
			
			if (inst == 0x0008) {	// RETURN
				PC = pop();
			} else
			
			if (inst == 0x0063) {	// SLEEP
				sleep();
			} else
			
			if (mask(inst, 0x3C00, 0x3E00)) {	// SUBLW
				result = substraction(literal, W);
			} else
			
			if (mask(inst, 0x3A00, 0x3F00)) {	// XORLW
				result = literal ^ W;
			} else
			
			{	// BIT-ORIENTED FILE REGISTER OPERATIONS
				toFile = true;
				updateZeroFlag = false;
			
				if (mask(inst, 0x1000, 0x3C00)) {	// BCF
					result = setBit(readF(file), bit, false);
				} else
				
				if (mask(inst, 0x1400, 0x3C00)) {	// BSF
					result = setBit(readF(file), bit, true);
				} else
				
				if (mask(inst, 0x1800, 0x3C00)) {	// BTFSC
					if (!isBitSet(readF(file), bit)) PC++;
				} else
				
				if (mask(inst, 0x1C00, 0x3C00)) {	// BTFSS
					if (isBitSet(readF(file), bit)) PC++;
				};
			};
			
		};
		if (result != 0x7FFFFFFF) {
			if (toFile) {
				writeF(file, result & 0xFF);
			} else {
				W = result & 0xFF;
			};
			if (updateZeroFlag) {
				if (result == 0) {
					Z = 1;
				} else {
					Z = 0;
				};
			};
		};
	};
};
