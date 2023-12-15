package com.runicrealms.plugin.pvp;

import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.common.api.PvPData;
import com.runicrealms.plugin.rdb.RunicDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPvPData implements PvPData {

    private final UUID owner;
    private final Map<Integer, Boolean> outlawEnabled = new HashMap<>();

    public PlayerPvPData(UUID owner, Runnable onComplete) { // runs on completion of loading data
        this.owner = owner;
        for (int i = 1; i <= RunicDatabase.getAPI().getDataAPI().getMaxCharacterSlot(); i++) {
            outlawEnabled.put(i, false);
        }
        RunicCommon.getLuckPermsAPI().retrieveData(owner).then(data -> {
            if (data.containsKey("runic.outlaw")) {
                String outlawString = data.getString("runic.outlaw");
                if (!outlawString.isBlank()) {
                    Arrays.stream(outlawString.split(","))
                            .map(Integer::parseInt).forEach(slot -> outlawEnabled.put(slot, true));
                }
            }
            onComplete.run();
        });
    }

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public boolean isOutlawEnabled(int slot) {
        return outlawEnabled.get(slot);
    }

    @Override
    public boolean isOutlawEnabled() {
        return isOutlawEnabled(RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(this.owner));
    }

    @Override
    public void setOutlawEnabled(boolean enabled) {
        setOutlawEnabled(RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(this.owner), enabled);
    }

    @Override
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
