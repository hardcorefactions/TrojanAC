package net.valorhcf.trojan.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FlagAlertType {

    NONE("None"),
    SERVER("Server"),
    GLOBAL("Global");

    private final String name;

    public FlagAlertType next() {

        if (this.ordinal() >= (types.length - 1)) {
            return types[0];
        }

        return types[this.ordinal() + 1];
    }

    private static final FlagAlertType[] types = new FlagAlertType[values().length];
    private static final Map<String, FlagAlertType> typesByName = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(it -> {
            types[it.ordinal()] = it;
            typesByName.put(it.name().toLowerCase(),it);
        });
    }

    public static FlagAlertType[] getAllTypes() {
        return types;
    }

    public static FlagAlertType get(int ordinal) {
        return types[ordinal];
    }

    public static FlagAlertType get(String name) {
        return typesByName.get(name.toLowerCase());
    }

}
