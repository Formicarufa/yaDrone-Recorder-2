/*
 */
package de.yadrone.impl;

import de.yadrone.ARDroneProvider;
import de.yadrone.base.ARDrone;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ARDroneProvider.class)
public class ARDroneProviderImpl implements ARDroneProvider {

    ARDrone drone;

    public ARDroneProviderImpl() {
        try {
            drone = new ARDrone();
            drone.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (drone != null) {
                drone.stop();
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                drone.disconnect();
                //drone = null;
            }
        }
    }

    @Override
    public ARDrone getARDrone() {
        return drone;
    }

}
