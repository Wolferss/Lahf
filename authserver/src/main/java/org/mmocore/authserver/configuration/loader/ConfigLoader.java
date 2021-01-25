package org.mmocore.authserver.configuration.loader;

import org.mmocore.authserver.configuration.parser.ServerNamesConfigParser;

/**
 * Create by Mangol on 09.12.2015.
 */
public class ConfigLoader {
    public static void loading() {
        org.mmocore.commons.configuration.ConfigLoader loader = org.mmocore.commons.configuration.ConfigLoader.createConfigLoader();
        loader.load();
        ServerNamesConfigParser.loadServerNames();
    }
}
