/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Murko
 */
public class networkAddressRange implements Serializable {

    public static String DENY_ACCESS = "DENY_ACCESS";
    public static String ALLOW_SSL = "ALLOW_SSL";
    public static String ALLOW_ANY = "ALLOW_ANY";

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
 * @param remote
 * @return
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

    public networkAddressRange(int[] network, int[] mask, String action) throws Exception {
        setNetwork(network, mask);
        this.action = action;
        

    }
/**
 * Checks if the parameter address is on the network that this Object represents
 * @param address
 * @return
 */
    private boolean isOnNetwork(int[] address) {
        BitSet remote = getBits(address);
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
                return 1;
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
 * returns the bit set from the inputted bytes
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
 * returns a normal xxx.xxx.xxx.xxx representation of the network IP
 * @return
 */
    public String getNetworkForHumans() {
        return networkForHumans;
    }
/**
 * returns the short mask, basically the number of 'ones' in front. e.g. 111110000 -> 5
 * @return
 */
    public int getShortMask() {
        return shortMask;
    }
/**
 * Returns the action
 * @return
 */
    public String getAction() {
        return action;
    }

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
