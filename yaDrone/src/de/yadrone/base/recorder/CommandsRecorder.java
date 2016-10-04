/**
 * 
 */
package de.yadrone.base.recorder;

import java.io.PrintStream;

import de.yadrone.base.command.ATCommand;
import de.yadrone.base.command.PCMDCommand;
import de.yadrone.base.command.PCMDMagCommand;
import de.yadrone.base.command.event.CommandSentListener;

/**
 * @author Formicarufa (Tomas Prochazka)
 *12. 3. 2016
 */
class CommandsRecorder implements CommandSentListener{

	PrintStream stream;
	private char separator;

	/**
	 * @param stream
	 */
	public CommandsRecorder(PrintStream stream,char separator) {
		super();
		this.stream = stream;
		this.separator = separator;
	}

	/* (non-Javadoc)
	 * @see de.yadrone.base.command.event.CommandSentListener#commandSent(de.yadrone.base.command.ATCommand)
	 */
	@Override
	public void commandSent(ATCommand command) {
		if (command instanceof PCMDCommand) {
			if (command instanceof PCMDMagCommand) {
				throw new UnsupportedOperationException("Recording of absolute-control commands is not supported.");
			}
			PCMDCommand pcmdCommand = (PCMDCommand) command;
			stream.println(pcmdCommand.toString(separator));
		}
	}
	
	
}
