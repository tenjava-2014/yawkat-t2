package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.TenJava;
import org.bukkit.ChatColor;

/**
 * @author yawkat
 */
public class Commands {
    static final String ERROR_PREFIX = ChatColor.YELLOW.toString() + ChatColor.ITALIC;

    private Commands() {}

    /**
     * Convert a double to a nice string, omitting a trailing .0 on round numbers.
     */
    static String toDisplayString(double rounded) {
        if ((int) rounded == rounded) {
            // if this is a round number cast to int so we don't print the trailing .0
            return String.valueOf((int) rounded);
        } else {
            return String.valueOf(rounded);
        }
    }
}
