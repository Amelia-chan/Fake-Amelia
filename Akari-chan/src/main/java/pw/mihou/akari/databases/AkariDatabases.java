package pw.mihou.akari.databases;

import pw.mihou.akari.Akari;
import pw.mihou.alisa.modules.database.types.AlisaFeedDatabase;

public class AkariDatabases {

    public static final AlisaFeedDatabase FEEDS = new AlisaFeedDatabase(Akari.getDatabaseClient().client());

}
