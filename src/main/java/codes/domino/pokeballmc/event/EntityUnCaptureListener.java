package codes.domino.pokeballmc.event;

import codes.domino.pokeballmc.item.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class EntityUnCaptureListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null || event.getClickedBlock().isEmpty()) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(ItemManager.ITEM_ORIGIN_KEY, PersistentDataType.STRING))
            return;

        if (ItemManager.getInstance().getTemporarilyDisallowedUsers().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        Entity storedEntity = ItemManager.getInstance().unCaptureEntity(event.getPlayer().getItemInHand(), event.getClickedBlock().getLocation().add(0, 1, 0));
        if (storedEntity == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "This Pokeball is already empty.");
            return;
        }

        event.getPlayer().sendMessage(ChatColor.GREEN + "Entity spawned.");
        storedEntity.teleport(event.getClickedBlock().getLocation().add(0, 1, 0));

    }


}
