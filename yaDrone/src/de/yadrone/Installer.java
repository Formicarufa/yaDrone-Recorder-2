/*
 */
package de.yadrone;

import de.yadrone.base.ARDrone;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // TODO
    }

    @Override
    public void close() {
        ARDrone drone = ARDroneProvider.getDrone();
        if (drone != null) {
            drone.stop();
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drone.disconnect();
        }
    }

}
