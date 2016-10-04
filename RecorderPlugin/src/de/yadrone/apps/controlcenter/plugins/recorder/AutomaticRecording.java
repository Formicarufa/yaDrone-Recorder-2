/*
 */

package de.yadrone.apps.controlcenter.plugins.recorder;

import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.NavDataManager;
import de.yadrone.base.navdata.StateListener;

/**
 *
 * @author Tomas Prochazka
 * 12.8.2016
 */
public class AutomaticRecording implements StateListener{
    private NavDataManager nav;
    private RecorderPanel panel;
    private boolean active= false;
    public AutomaticRecording(NavDataManager nav, RecorderPanel panel) {
        this.nav = nav;
        this.panel = panel;
    }

    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        active=true;
        nav.addStateListener(this);
    }
    public void deactivate(){
        active=false;
        nav.removeStateListener(this);
    }

    @Override
    public void stateChanged(DroneState state) {
        //nothing.
    }

    @Override
    public void controlStateChanged(ControlState state) {
        boolean recording = panel.isRecording();
        if (state==ControlState.LANDED) {
            if (recording) {
                stopRecording();
            }
        } else {
            if (!recording) {
                startRecording();
            }
        }
    }

    private void stopRecording() {
        panel.stopRecording();
    }

    private void startRecording() {
        String name = panel.getNameFieldText();
        panel.startRecording(name + "_auto", true);
        
    }
    
}
