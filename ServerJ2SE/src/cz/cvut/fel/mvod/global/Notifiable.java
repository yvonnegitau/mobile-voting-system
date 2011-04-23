/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.global;

/**
 *
 * @author Murko
 */
public interface Notifiable {
    /**
     * Notifies this object that a settings change has occured
     */
    public void notifyOfChange();

}
