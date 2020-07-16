package org.asamk.signal;
import org.asamk.signal.manager.Manager;

public class DbusConfig {
    
    final Manager m;
    public static final String SIGNAL_BUSNAME = "org.asamk.Signal." + m.getUsername().replace("+", "");
    public static final String SIGNAL_OBJECTPATH = "/org/asamk/Signal";
}
