package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.events.RunicDeathEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// TODO: safe zone listener?
public class OutlawListener implements Listener {

    @EventHandler
    public void onSpellDamage(SpellDamageEvent e) {
        if (e.getEntity() instanceof Player && (!RunicPvPAPI.isOutlaw(((Player) e.getEntity()))
                || !RunicPvPAPI.isOutlaw(e.getPlayer()))
                && !RunicPvP.getDuelManager().areDueling(e.getPlayer(), (Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (e.getEntity() instanceof Player && (!RunicPvPAPI.isOutlaw(((Player) e.getEntity()))
                || !RunicPvPAPI.isOutlaw(e.getPlayer()))
                && !RunicPvP.getDuelManager().areDueling(e.getPlayer(), (Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRunicDeath(RunicDeathEvent e) {
        if (e.getKiller() == null) return;
        if (!(e.getKiller() instanceof Player)) return;
        if (!RunicPvPAPI.isOutlaw(((Player) e.getKiller())) || !RunicPvPAPI.isOutlaw(e.getVictim())) return;
        RunicPvP.getOutlawManager().onKill((Player) e.getKiller(), e.getVictim());
    }
}
