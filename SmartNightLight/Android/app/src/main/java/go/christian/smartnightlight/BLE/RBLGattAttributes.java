package go.christian.smartnightlight.BLE;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 *
 * Starter code, which contained the complete bluetooth connectivity provided by Liang He
 * https://github.com/jonfroehlich/CSE590Sp2018/tree/master/A03-BLEBasic
 */
public class RBLGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String BLE_SHIELD_TX = "713d0003-503e-4c75-ba94-3148f18d941e";
    public static String BLE_SHIELD_SERVICE = "713d0000-503e-4c75-ba94-3148f18d941e";

    static {
        // RBL Services.
        attributes.put("713d0000-503e-4c75-ba94-3148f18d941e",
                "BLE Shield Service");
        // RBL Characteristics.
        attributes.put(BLE_SHIELD_TX, "BLE Shield TX");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
