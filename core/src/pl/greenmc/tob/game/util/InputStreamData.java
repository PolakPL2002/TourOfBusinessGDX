/*
 * Copyright Szymon Kucharski - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package pl.greenmc.tob.game.util;

import java.nio.charset.StandardCharsets;

import static pl.greenmc.tob.game.util.Logger.debug;

/**
 * Data from InputStream
 */
public class InputStreamData {
    private final byte[] rawData;

    /**
     * @param rawData Data as byte array
     */
    public InputStreamData(byte[] rawData) {
        debug(this.getClass().getSimpleName() + " initialization...");
        this.rawData = rawData;
    }

    /**
     * @return Data as byte array
     */
    public byte[] getRawData() {
        return rawData;
    }

    /**
     * @return Data as UTF-8 string
     */
    public String getUTF8() {
        return new String(rawData, StandardCharsets.UTF_8);
    }
}
