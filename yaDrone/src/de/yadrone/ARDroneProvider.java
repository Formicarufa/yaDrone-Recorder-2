/*
 */

package de.yadrone;

import de.yadrone.base.ARDrone;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Prochazka
 * 11.8.2016
 */
public interface ARDroneProvider {
    ARDrone getARDrone();
    
    public static ARDrone getDrone() {
        ARDroneProvider prov = Lookup.getDefault().lookup(ARDroneProvider.class);
        if (prov!=null) return prov.getARDrone();
        return null;
    }
}
