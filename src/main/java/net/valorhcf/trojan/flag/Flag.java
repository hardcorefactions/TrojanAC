package net.valorhcf.trojan.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Flag {

    private int count;
    private long ping;
    private String check;
    private String metadata;

    public void increment() {
        this.count++;
    }

}