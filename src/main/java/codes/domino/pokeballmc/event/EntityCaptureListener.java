package codes.domino.pokeballmc.event;

import codes.domino.pokeballmc.PokeballMC;
import codes.domino.pokeballmc.item.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class EntityCaptureListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getRightClicked() instanceof Player) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(ItemManager.ITEM_ORIGIN_KEY, PersistentDataType.STRING))
            return;
        if (ItemManager.getInstance().getTemporarilyDisallowedUsers().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        if (!ItemManager.getInstance().captureEntity(event.getPlayer().getItemInHand(), event.getRightClicked())) {
            event.getPlayer().sendMessage(ChatColor.RED + "This Pokeball is already occupied.");
            return;
        }
        event.getPlayer().sendMessage(ChatColor.GREEN + "Entity captured.");
        ItemManager.getInstance().getTemporarilyDisallowedUsers().add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(PokeballMC.getInstance(), () -> ItemManager.getInstance().getTemporarilyDisallowedUsers().remove(event.getPlayer().getUniqueId()), 20);

    }


}
