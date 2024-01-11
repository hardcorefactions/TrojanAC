package net.valorhcf.trojan.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogEntry {

    public Long time;
    public Long ping;
    public String server;
    public String check;
    public String metadata;
    public String banned;

}
