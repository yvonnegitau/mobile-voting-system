/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.common;

import java.util.BitSet;

/**
 *
 * @author Murko
 */
public class networkAddressRange {

    BitSet network = new BitSet(32);
    BitSet mask = new BitSet(32);
    int shortMask = 0;

    public networkAddressRange(int[] network, int[] mask) throws Exception {
        setNetwork(network, mask);

    }

    public boolean isOnNetwork(int[] address) {
        BitSet remote = getBits(address);
        for (int i = 0; i < shortMask; i++) {
            if (remote.get(i) != network.get(i)) {
                return false;
            }

        }
        return true;
    }

    private void setNetwork(int[] network, int[] mask) throws Exception {
        if (network.length != 4) {
            throw new Exception("Not a valid IPv4 address");
        }
        if (mask.length != 4) {
            throw new Exception("Not a valid IPv4 mask");
        }
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
}

