package com.runicrealms.plugin.pvp.listener;

import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.party.event.PartyJoinEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PartyListener implements Listener {

    @EventHandler(priority = EventPriority.LOW) // early
    public void onPartyJoin(PartyJoinEvent event) {

        int slotLeader = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getParty().getLeader().getUniqueId());
        int slotMember = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getJoining().getUniqueId());
        boolean isLeaderOutlaw = RunicCommon.getPvPAPI().isOutlaw(event.getParty().getLeader(), slotLeader);
        boolean isMemberOutlaw = RunicCommon.getPvPAPI().isOutlaw(event.getJoining(), slotMember);
        boolean sameStatus = isLeaderOutlaw == isMemberOutlaw;

        if (!sameStatus) {
            event.setCancelled(true);
            event.getParty().getLeader().sendMessage
                    (
                            ChatColor.GREEN + "[Party] " + ChatColor.WHITE + event.getJoining().getName() +
                                    ChatColor.RED + " tried to join your party, but their outlaw status does not match yours!"
                    );
            event.getJoining().sendMessage
                    (
                            ChatColor.GREEN + "[Party] " + ChatColor.RED + "You tried to join " +
                                    ChatColor.WHITE + event.getParty().getLeader().getName() + ChatColor.RED + "'s party, " +
                                    "but your outlaw status does not match theirs!"
                    );
        }
    }
}
