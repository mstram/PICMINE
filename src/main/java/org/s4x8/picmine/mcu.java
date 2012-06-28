/**
 * @author Marcos Vives Del Sol
 * @version 1.0, 28/VI/12
 *
 * mcu.java
 * This is the interface from PICMINE to the RedStoneChips API
 * 
 * Licensed under the CC-BY 3.0 (http://creativecommons.org/licenses/by/3.0/) license
 */

package org.s4x8.picmine;

import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;

public class mcu extends Circuit {
	PICMINE pic;
	
	public int inputData(int addr) {
		int base = addr * 8 + 15; // 15 is the first data input pin, as pin 0 is clock and 1-14 are instruction input pins
		int val = 0;
		int i;
		boolean status;
		
		for (i = 0; i < 8; i++) {
			status = false;
			if ((base + i) < inputBits.size()) {
				status = inputBits.get(base + i);
			};
			val = pic.setBit(val, i, status);
		};
		return val;
	};
	
	public void outputData(int addr, int data) {
		int base = addr * 8 + 11; // 11 is the first data output pin, as 0-10 are address output pins
		int i;
		for (i = 0; i < 8; i++) {
			if ((base + i) < outputs.length) {
				sendOutput(base + i, pic.isBitSet(data, i));
			};
		};
	};

	private void outputAddress() {
		int pc = pic.PC;
		int i;
		//System.out.println("PC: " + Integer.toHexString(pc));
		for (i = 0; i < 11; i++) {
			sendOutput(i, (pc & 1) == 1);
			pc = pc >> 1;
		};
	};
	
	private void readAndDoInst() {
		int inst = 0;
		int i;
		for (i = 13; i >= 0; i--) {
			inst = inst << 1;
			inst |= (inputs[i + 1].getPinValue() ? 1 : 0);
		};
		//System.out.println("I: " + Integer.toHexString(inst));
		pic.step(inst);
	};
			
    public void inputChange(int index, boolean state) {
		if (index != 0) return;

		if (!state) { // On high-to-low clock transition, output PC
			outputAddress();
		} else { // On low-to-high clock transition, read and process instruction
			readAndDoInst();
		};
    };

    public boolean init(CommandSender sender, String[] strings) {
        // This code executes when you right-click on the circuit's sign and again each
        // time the the server is restarted or the RedstoneChips plugin is enabled.
		if (inputs.length < 15) {
			info(sender, "Not enough input pins");
			return false;
		};
		
		if (outputs.length < 11) {
			info(sender, "Not enough output pins");
			return false;
		};
		
		pic = new PICMINE(this);
        return true;
    }

}
