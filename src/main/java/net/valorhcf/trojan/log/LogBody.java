package net.valorhcf.trojan.log;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class LogBody {

    public UUID uuid;
    public Long time;
    public String username;
    public String usernameHex;

    public String prefix;
    public String prefixHex;

    public List<LogEntry> entries;
}
