package com.runicrealms.plugin.pvp;

import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.rdb.RunicDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PvPData {

    private final UUID owner;
    private final Map<Integer, Boolean> outlawEnabled = new HashMap<>();

    public PvPData(UUID owner) {
        this.owner = owner;
        for (int i = 1; i <= RunicDatabase.getAPI().getDataAPI().getMaxCharacterSlot(); i++) {
            outlawEnabled.put(i, false);
        }
        RunicCommon.getLuckPermsAPI().retrieveData(owner).thenAcceptAsync(data -> {
            if (!data.containsKey("runic.outlaw")) return;
            Arrays.stream(data.getString("runic.outlaw").split(","))
                    .map(Integer::parseInt).forEach(slot -> outlawEnabled.put(slot, true));
        });
    }

    public boolean isOutlawEnabled(int slot) {
        return outlawEnabled.get(slot);
    }

    public boolean isOutlawEnabled() {
        return isOutlawEnabled(RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(this.owner));
    }

    public void setOutlawEnabled(boolean enabled) {
        setOutlawEnabled(RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(this.owner), enabled);
    }

    public void setOutlawEnabled(int slot, boolean enabled) {
        outlawEnabled.put(slot, enabled);
        RunicCommon.getLuckPermsAPI().savePayload(RunicCommon.getLuckPermsAPI().createPayload(this.owner, data ->
                data.set("runic.outlaw", outlawEnabled.keySet().stream()
                        .filter(outlawEnabled::get)
                        .map(Object::toString)
                        .collect(Collectors.joining(","))))
        );
    }

}
