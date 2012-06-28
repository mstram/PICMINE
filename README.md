S4X8 PICMINE 1.0 (28/VI/2012)
=============================

What is this?
-------------

PICMINE is [RedstoneChips] plugin which allows you to create an interactive and functional microcontroller inside a multiplayer [Minecraft] server based on [Bukkit]. Altough it is somewhat far from being a finished software (see the TODO section), it works, so I tought it was a good idea to release it now and allow people to play and experiment with it.

Installation
------------

To install the chip on your Bukkit server, you may either download the pre-built version, or build it yourself.

The pre-built versions can be found at `/binary`.

To compile it yourself, you need a working [Maven] enviroment. First, open a new shell window and download the latest version of etCommon:

	git clone http://github.com/eisental/etCommon
	cd etCommon

Without closing the shell window, open your favourite text editor, edit `/etCommon/pom.xml` file and add this inside the `<project>` tag:

	<repositories>
		<repository>
			<id>bukkit-repo</id>
			<url>http://repo.bukkit.org/content/groups/public/</url>
		</repository>
	</repositories>

Save it, go back to the shell and run the following:

	mvn install
	cd ..
	
Installing etCommon should be done automatically by Maven when installing RedstoneChips, but as the author of etCommon is not providing a binary for Maven, it has to be done manually.

Now download and compile RedstoneChips:
	
	git clone http://github.com/eisental/RedstoneChips
	cd RedstoneChips
	mvn clean install
	cd ..
	
Then download and compile PICMINE:

	git clone http://github.com/socram8888/PICMINE
	cd PICMINE
	mvn clean package
	
The resulting compiled Java Archive file (`.jar`) ready to be used will be placed at `/target`. Just copy it to Bukkit's plugin folder. If you don't have RedstoneChips installed on your server, download the pre-compiled version and copy it to the plugins folder as well.

How can I build the chip?
-------------------------

After having the chip installed, you need to build a chip with "`mcu`" on the sign, and with at least 15 inputs and 11 outputs. If you add more I/O pins, these will be used as GPIO pins (see memory map "input pins" and "output pins")

The first input is used as the clock pin. On the rising edge (low-to-high transition) of the clock, the chip will output the PC on the first 12 output pins. On the falling edge (high-to-low transition), the chip will process the instruction given on input pins 2~15 (being the 2 the LSB, and 15 the MSB)

How can I develop anything for it?
----------------------------------

It is based on [Microchip]'s [PIC12F683 microcontroller]. It has the same binary code for each instruction and the is capable of driving the same amount of program memory, altough not the same memory map. Thus, you may either use Microchip's official development tools, like [MPLAB], or use open source GPL-licensed software like [GPUTILS] (an assembler) or [SDCC] (a C compiler)

Memory map
----------

### Reading:
 * `$00`: indirect addressing
 * `$02`: `PCL` (*PC* *l*ow)
 * `$03`: `STATUS`
 * `$04`: `FSR`
 * `$0A`: `PCLATH` (*PC* *lat*ched *h*igh)
 * `$10`~`$1F`: input pins
 * `$20`~`$7F`: RAM

### Writting:
 * `$00`: indirect addressing
 * `$02`: `PCL`
 * `$03`: `STATUS`
 * `$04`: `FSR`
 * `$0A`: `PCLATH`
 * `$10`~`$1F`: output pins
 * `$20`~`$7F`: RAM

When reading or writting to the indirect addressing register, you will access the memory position written to `FSR`. This is useful for arrays.

When writting to `PCL`, the `PC` low byte will be set to `PCL`, and the higher byte will be set to `PCLATH`. This is: `PC = PCLATH * 0x100 + PCL`. Reading `PCL` returns the current low byte of the `PC`, but reading `PCLATH` returns the last value written to it.

`STATUS` is composed of the following flags:

	MSB                        LSB
	0 | 0 | 0 | 0 | 0 | Z | DC | C

Being `Z` the zero flag (set to 1 when the result of an operation is zero), `DC` the Digit Carry (the carry from doing the operation only on the first 4-bits), and `C` being the Carry flag.

As RedstoneChips is not compatible with bidirectional I/O pins, it uses unidirectional general-purpose data pins, and as there is 16 bytes for it, there can be up to 128 input and 128 output pins.

Things to do
------------

 * Add debugging!
 * Add interrupts
 * Add a Watchdog Timer
 * Re-do part of the code so the processor does not loses its status (PC, files, RAM...) at server reset/shutdown
 * Check if it feasible to add an internal clock so it is not limited to RedstoneChips "`clock`" chip 10hz limit.
 * Re-do part of the code to allow usage of internal 

License
-----

PICMINE is licensed under a [CC-BY 3.0 license]. This is: you can use, modify, compile and redistribute it, either in source code or binary form, as long as my name is visible, both in the final software (in the credits, for example) and in the source code (in the comments, as it is now). Also, I'm not responsible in any way for any damage done to your computer (I tried not to, but it may set your computer on fire :P), the server, the game map (altought the chip never access the blocks), the user (real or in-game) or anything else. For a longer and boring version of the license, see [the full version of the license CC-BY 3.0 license].

Changelog
---------
 * 28/VI/2012 1.0: First public release
 * 26/VI/2012 beta: It works! Demonstrated to some people at [ElTrolado]
 * 23/VI/2012 alpha: Start of the development

About the author
----------------

My name is Marcos Vives Del Sol, aka "socram8888". I'm a 17-year-old Spanish guy who wants to became a programmer. I know HTML, PHP, JS, 6502 assembly and a bit of C, but I'm not specialized in any of them (I just choose the one where I see the thing I want to do is easier to program). This is the first thing I've ever done in Java, but as it is quite simmilar to JavaScript, it wasn't too hard to do.

If you want to report a bug, ask for a new feature, or just say hello, you can contact me in my e-mail account [socram8888@gmail.com].

Notes
-----

Minecraft is © Mojang.  
Microchip, PIC and PIC12F683 are © Microchip Technology Inc. If you think it is fun to do this inside Minecraft, then try doing it in real life: it's even funnier!

  [Bukkit]: http://bukkit.org/
  [CC-BY 3.0 license]: http://creativecommons.org/licenses/by-sa/3.0/
  [ElTrolado]: http://www.eltrolado.com/
  [GPUTILS]: http://gputils.sourceforge.net/
  [Maven]: http://maven.apache.org/
  [Microchip]: http://www.microchip.com/
  [Minecraft]: http://www.minecraft.net/
  [MPLAB]: http://www.microchip.com/stellent/idcplg?IdcService=SS_GET_PAGE&nodeId=1406&dDocName=en019469
  [PIC12F683 microcontroller]: http://www.microchip.com/wwwproducts/Devices.aspx?dDocName=en010115
  [RedstoneChips]: http://eisental.github.com/RedstoneChips/
  [SDCC]: http://sdcc.sourceforge.net/
  [socram8888@gmail.com]: mailto:socram8888@gmail.com
  [the full version of the license CC-BY 3.0 license]: http://creativecommons.org/licenses/by-sa/3.0/legalcode
