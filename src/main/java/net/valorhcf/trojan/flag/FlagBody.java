package net.valorhcf.trojan.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.valorhcf.trojan.Trojan;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class FlagBody {

    private UUID player;
    private String username;
    private String displayName;
    private List<Flag> flags;

    public String toJson() {
        return Trojan.GSON.toJson(this);
    }

}
