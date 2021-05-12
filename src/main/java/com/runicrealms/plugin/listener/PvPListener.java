package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.events.EnemyVerifyEvent;
import com.runicrealms.plugin.events.RunicDeathEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

// TODO: safe zone listener?
public class PvPListener implements Listener {

    /*
    For spell effects like knock-up, blind, etc.
     */
    @EventHandler
    public void onSpellVerify(EnemyVerifyEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        if ((!RunicPvPAPI.isOutlaw(((Player) e.getVictim())) || !RunicPvPAPI.isOutlaw(e.getCaster()))
                && !RunicPvP.getDuelManager().areDueling(e.getCaster(), (Player) e.getVictim()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onSpellDamage(SpellDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (RunicPvPAPI.isPlayerValidTarget(e.getPlayer(), e.getEntity()))
            RunicCoreAPI.tagCombat(e.getPlayer(), e.getEntity());
        else
            e.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (RunicPvPAPI.isPlayerValidTarget(e.getPlayer(), e.getEntity()))
            RunicCoreAPI.tagCombat(e.getPlayer(), e.getEntity());
        else
            e.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRunicDeath(RunicDeathEvent e) {
        if (e.getKiller() == null) return;
        if (!(e.getKiller() instanceof Player)) return;
        Player killer = (Player) e.getKiller();
        if (!RunicPvPAPI.isOutlaw((killer)) || !RunicPvPAPI.isOutlaw(e.getVictim())) return;
        if (RunicPvP.getDuelManager().areDueling(killer, e.getVictim())) return;
        if (e.isCancelled()) return;
        RunicPvP.getOutlawManager().onKill(killer, e.getVictim());
    }
}
