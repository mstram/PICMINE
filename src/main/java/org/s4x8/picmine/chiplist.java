/**
 * @author Marcos Vives Del Sol
 * @version 1.0, 28/VI/12
 *
 * chiplist.java
 * Just a list of the available chips
 * 
 * Licensed under the CC-BY 3.0 (http://creativecommons.org/licenses/by/3.0/) license
 */

package org.s4x8.picmine;

import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.CircuitLibrary;

public class chiplist extends CircuitLibrary {
    public Class[] getCircuitClasses() {
        return new Class[] { mcu.class };
    };
};
