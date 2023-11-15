package codes.domino.pokeballmc.item;

import codes.domino.pokeballmc.PokeballMC;
import me.hex.entityserializer.core.exceptions.EntityNotFoundException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {

    public static final NamespacedKey ITEM_ORIGIN_KEY = new NamespacedKey(PokeballMC.getInstance(), "itemOrigin");
    private final static String UNOCCUPIED_STRING = ChatColor.LIGHT_PURPLE + "Pokeball [0/1]";
    private final static String OCCUPIED_STRING = ChatColor.BLUE + "Pokeball [1/1]";
    private final static NamespacedKey ENTITY_UUID_KEY = new NamespacedKey(PokeballMC.getInstance(), "entity");
    private static ItemManager INSTANCE;
    private final ItemStack pokeBallItem;
    private final Set<UUID> temporarilyDisallowedUsers = new HashSet<>();

    private ItemManager() {
        pokeBallItem = new ItemStack(Material.CLOCK);
        pokeBallItem.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta meta = pokeBallItem.getItemMeta();

        meta.setDisplayName(UNOCCUPIED_STRING);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> itemLore = List.of(ChatColor.GRAY + "Entity stored:", ChatColor.DARK_GREEN + "N/A");
        meta.setCustomModelData(411);
        meta.setLore(itemLore);
        meta.getPersistentDataContainer().set(ITEM_ORIGIN_KEY, PersistentDataType.STRING, "PokeballMC");
        pokeBallItem.setItemMeta(meta);
    }

    public static ItemManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemManager();
        }
        return INSTANCE;
    }

    public Set<UUID> getTemporarilyDisallowedUsers() {
        return temporarilyDisallowedUsers;
    }

    public void giveItem(Player player) {
        player.getInventory().addItem(pokeBallItem);
    }

    public boolean captureEntity(ItemStack itemStack, Entity entity) {
        if (this.isItemOccupied(itemStack)) {
            return false;
        }
        NamespacedKey namespacedkey = new NamespacedKey(PokeballMC.getInstance(), entity.getUniqueId().toString());
        PokeballMC.getHandler().serialize(entity, namespacedkey, true);
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(ENTITY_UUID_KEY, PersistentDataType.STRING, entity.getUniqueId().toString());
        meta.setDisplayName(OCCUPIED_STRING);
        List<String> itemLore = List.of(ChatColor.GRAY + "Entity stored:", ChatColor.DARK_GREEN + entity.getName());
        meta.setLore(itemLore);
        itemStack.setItemMeta(meta);
        return true;
    }

    private boolean isItemOccupied(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(ENTITY_UUID_KEY, PersistentDataType.STRING) != null;
    }

    public Entity unCaptureEntity(ItemStack itemStack, Location location) {
        if (!isItemOccupied(itemStack)) {
            return null;
        }
        String id = itemStack.getItemMeta().getPersistentDataContainer().get(ENTITY_UUID_KEY, PersistentDataType.STRING);
        if (id == null || id.isEmpty()) {
            return null;
        }
        Entity deserializedEntity;
        try {
            NamespacedKey tempKey = new NamespacedKey(PokeballMC.getInstance(), id);
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            int randXOffset = rand.nextInt(500, 1000);
            int randZOffset = rand.nextInt(500, 1100);

            deserializedEntity = PokeballMC.getHandler().deserialize(tempKey).spawnAndGet(location.clone().add(randXOffset, 1000, randZOffset));
            PokeballMC.getHandler().deserialize(tempKey);
            ItemMeta meta = itemStack.getItemMeta();
            meta.getPersistentDataContainer().remove(ENTITY_UUID_KEY);
            meta.setDisplayName(UNOCCUPIED_STRING);
            List<String> itemLore = List.of(ChatColor.GRAY + "Entity stored:", ChatColor.DARK_GREEN + "N/A");
            meta.setLore(itemLore);
            itemStack.setItemMeta(meta);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return deserializedEntity;
    }
}
