//
// --------------------------------------------------------------------------
//  Gurux Ltd

package org.distiya.protocol.gdlms.enums;

/**
 * Enumerates Access Service types.
 */
public final class AccessServiceCommandType {
    /**
     * Constructor.
     */
    private AccessServiceCommandType() {

    }

    /**
     * Get request or response.
     */
    public static final int GET = 1;
    /**
     * Set request or response.
     */
    public static final int SET = 2;
    /**
     * Action request or response.
     */
    public static final int ACTION = 3;
}
