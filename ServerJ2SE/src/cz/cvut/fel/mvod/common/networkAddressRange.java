/*
Copyright 2011 Radovan Murin

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package cz.cvut.fel.mvod.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents one network address range entity that has self evaluating capabilities
 * @author Radovan Murin
 */
public class networkAddressRange implements Serializable {
    /**
     * Addresses matching this range will be denied access
     */
    public static String DENY_ACCESS = "DENY_ACCESS";
    /**
     * Addresses matching this range will be allowed access only if using secure connections.
     */
    public static String ALLOW_SSL = "ALLOW_SSL";
    /**
     * Addresses matching this range will be allowed access.
     */
    public static String ALLOW_ANY = "ALLOW_ANY";
    /**
     * An ArrayList of private networks as devined byt RFC 1918 and RFC 4193
     */
    private static final ArrayList<networkAddressRange> locals = new ArrayList<networkAddressRange>();;

    
        
        static{
        try {
            //Private networks RFC 1918 and RFC 4193
            locals.add(new networkAddressRange(new int[]{192, 168, 0, 0}, new int[]{255, 255, 0, 0}, "ALLOW_ANY"));
            locals.add(new networkAddressRange(new int[]{172, 16, 0, 0}, new int[]{255, 240, 0, 0}, "ALLOW_ANY"));
            locals.add(new networkAddressRange(new int[]{10, 0, 0, 0}, new int[]{255, 0, 0, 0}, "ALLOW_ANY"));
        } catch (Exception ex) {
            Logger.getLogger(networkAddressRange.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        }




/**
 * Checks if the parameter address is in a private network as defined by RFC 1918 and RFC 4193
 * @param remote the address that is to be validated, integer of 4 numbers from 0-255
 * @return true, if the address is on the lan,
 */
    public static boolean isOnLAN(int[] remote) {
        Iterator<networkAddressRange> it = locals.iterator();
        while(it.hasNext()){
           int i= it.next().isAllowed(remote, true);
           if(i==1) return true;
        }
        return false;
    }
    BitSet network = new BitSet(32);
    BitSet mask = new BitSet(32);
    String networkForHumans;
    int shortMask = 0;
    private String action;
/**
 * The class constructor. the IPs are to be given as arrays of four elements e.g. {192,168,1,1}
 * @param network the network address of this range, can be a network address, device address, broadcast.
 * @param mask The network mask
 * @param action the default action
 * @throws Exception throws exception when a bad address is detected
 */
    public networkAddressRange(int[] network, int[] mask, String action) throws Exception {
        setNetwork(network, mask);
        this.action = action;
        

    }
/**
 * Checks if the parameter address is on the network that this Object represents
 * @param address remote address to be checked
 * @return true if the address belongs to the network represented by this instance
 */
    private boolean isOnNetwork(int[] address) {
        BitSet remote = getBits(address);
        System.out.println("REMOTE: "+remote.toString());
        System.out.println("LOCAL: "+ network.toString());
        for (int i = 0; i < shortMask; i++) {
            if (remote.get(i) != network.get(i)) {
                return false;
            }

        }
        return true;
    }
/**
 * Checks if the network inputed network is allowed by this Filter
 * @param address the address that is to be checked
 * @param isSecured flag if the connection that the connection is comming from is using SSL/TSL
 * @return returns an integer, 1 means allowed, -1 denyed, 3 means that the given is not included in this Filter
 */
    public int isAllowed(int[] address, boolean isSecured) {
        if (isOnNetwork(address)) {

            if (action.equals(ALLOW_ANY)) {
                return 1;
            }
            if (action.equals(ALLOW_SSL) && isSecured) {
                return 1;
            }
            if (action.equals(ALLOW_SSL) && !isSecured) {
                return -1;
            }
            if (action.equals(DENY_ACCESS)) {
                return -1;
            }
            return 1;

        } else {
            return 3;
        }
    }
/**
 * Sets the network range
 * @param network the IPv4 network address - can also be a device/broadcast address
 * @param mask the network mask
 * @throws Exception throws exception if addresses were not valid
 */
    private void setNetwork(int[] network, int[] mask) throws Exception {
        if (!isValidIPv4(network)) {
            throw new Exception("Not a valid IPv4 address");
        }
        if (!isValidIPv4(mask)) {
            throw new Exception("Not a valid IPv4 mask");
        }

        networkForHumans = "" + network[0] + "." +network[1] + "." +network[2] + "." +network[3];
       
        this.network = getBits(network);
        this.mask = getBits(mask);
        for (int i = 0; i < 32; i++) {
            if (this.mask.get(i)) {
                shortMask++;
            }
            if (!this.mask.get(i)) {
                break;
            }





        }
    }
/**
 * Returns the bit set from the inputted bytes
 * @param bytes
 * @return
 */
    private BitSet getBits(int[] bytes) {
        BitSet bits = new BitSet(32);
        for (int i = 0; i < bytes.length; i++) {

            int subst = 128;
            int pos = 0;
            while (bytes[i] != 0) {
                if (bytes[i] - subst >= 0) {
                    bytes[i] -= subst;
                    //  System.out.println(bytes[i]);
                    bits.set(i * 8 + pos, true);
                }

                pos++;
                subst = subst / 2;
            }
            //  System.out.println(bytes[i]);
        }
        return bits;
    }
/**
 * Returns a normal xxx.xxx.xxx.xxx representation of the network IP
 * @return a Human readable network address
 */
    public String getNetworkForHumans() {
        return networkForHumans;
    }
/**
 * Returns the short mask, basically the number of 'ones' in front. e.g. 111110000 -> 5
 * @return the number of consecutive one's in the subnet mask, starting from the beggining.
 */
    public int getShortMask() {
        return shortMask;
    }
/**
 * Returns the action that should be done with the packet if it mathes this instance's range
 * @return the action as defined in the thesis text
 */
    public String getAction() {
        return action;
    }
/**
 * Validates the given IP if it is a valid IPv4 address
 * @param address the address to be validated
 * @return true if valid
 */
    private static boolean isValidIPv4(int[] address) {
         if (address.length != 4) return false;

          for (int i = 0; i < address.length; i++) {
            int k = address[i];
            if (k > 255 || k < 0) {
                return false;
            }

        }
         return true;
    }
    

    


}
