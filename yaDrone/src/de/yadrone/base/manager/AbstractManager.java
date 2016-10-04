/**
 * 
 */
package de.yadrone.base.manager;

import java.net.InetAddress;

import de.yadrone.base.connection.ConnectionStateEvent;
import de.yadrone.base.connection.ConnectionStateListener;

/**
 * Common ancestor for both UDPManager and TCPManager.
 * @author Formicarufa (Tomas Prochazka)
 *19. 3. 2016
 */
public abstract class AbstractManager implements Runnable{

	protected InetAddress inetaddr = null;
	protected Thread thread = null;
	protected boolean connected = false;
	protected ConnectionStateEvent connectionStateEvent = new ConnectionStateEvent();
	
	public AbstractManager(InetAddress inetaddr) {
		this.inetaddr = inetaddr;
	}
	
	public boolean isConnected() {
		return connected;
	}
	public void addConnectionStateListener(ConnectionStateListener l) {
		connectionStateEvent.addListener(l);
	}
	public void removeConnectionStateListeners(ConnectionStateListener l) {
		connectionStateEvent.removeListener(l);
	}

}
