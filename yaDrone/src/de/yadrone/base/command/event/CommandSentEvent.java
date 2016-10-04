/**
 * 
 */
package de.yadrone.base.command.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.yadrone.base.command.ATCommand;

/**
 * @author Formicarufa (Tomas Prochazka)
 *12. 3. 2016
 */
public class CommandSentEvent {
	List<CommandSentListener> listeners = Collections.synchronizedList(new ArrayList<CommandSentListener>());
	public void addListener(CommandSentListener l) {
		listeners.add(l);
	}
	public boolean removeListener(CommandSentListener l) {
		return listeners.remove(l);
	}
	public boolean isUnused(){
		return listeners.isEmpty();
	}
	public void invoke(ATCommand command) {
		if (listeners.isEmpty()) return;
		List<CommandSentListener> copy;
		synchronized (listeners) {
			copy = new ArrayList<CommandSentListener>(listeners);			
		}
		for (CommandSentListener listener : copy) {
			listener.commandSent(command);
		}	
		
	}
	
}
