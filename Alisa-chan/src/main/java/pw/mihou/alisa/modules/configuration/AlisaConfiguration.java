package pw.mihou.alisa.modules.configuration;

import pw.mihou.dotenv.Dotenv;

public class AlisaConfiguration {

    public static String SIGNATURE;

    static {
        Dotenv.asReflective().reflectTo(AlisaConfiguration.class);
    }

}
