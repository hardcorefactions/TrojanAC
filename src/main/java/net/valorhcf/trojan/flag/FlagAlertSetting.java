package net.valorhcf.trojan.flag;

import cc.fyre.core.profile.Profile;
import cc.fyre.core.profile.setting.Setting;
import cc.fyre.core.profile.setting.SettingOption;
import cc.fyre.core.profile.setting.SettingValue;
import net.valorhcf.trojan.Trojan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FlagAlertSetting extends Setting<FlagAlertType> {

    private final List<SettingOption<FlagAlertType>> options = new ArrayList<>();

    public FlagAlertSetting() {

        for (FlagAlertType alert : FlagAlertType.getAllTypes()) {
            this.options.add(new SettingOption<>(alert.getName(),alert));
        }

    }

    @NotNull
    @Override
    public SettingValue<FlagAlertType> fromJson(@NotNull Object o) {

        if (!(o instanceof String)) {
            return SettingValue.Companion.of(FlagAlertType.GLOBAL);
        }

        return SettingValue.Companion.of(FlagAlertType.get((String)o));
    }

    @NotNull
    @Override
    public SettingValue<FlagAlertType> getDefaultValue() {
        return SettingValue.Companion.of(FlagAlertType.GLOBAL);
    }

    @NotNull
    @Override
    public String getDisplayName(FlagAlertType flagAlertType) {
        return flagAlertType.getName();
    }

    @NotNull
    @Override
    public String getKey() {
        return "alerts";
    }

    @NotNull
    @Override
    public String getName() {
        return "Trojan Alerts";
    }

    @Override
    public FlagAlertType getNextValue(@NotNull Profile profile, @Nullable FlagAlertType flagAlertType) {

        if (flagAlertType == null) {
            return FlagAlertType.GLOBAL;
        }

        return flagAlertType.next();
    }

    @NotNull
    @Override
    public List<SettingOption<FlagAlertType>> getOptions() {
        return this.options;
    }

    @Override
    public boolean hasPermission(@NotNull Profile profile) {
        return profile.hasPermission(Trojan.STAFF_PERMISSION);
    }

    @Override
    public boolean hasPermission(@NotNull Profile profile, @Nullable FlagAlertType flagAlertType) {
        return this.hasPermission(profile);
    }

}
