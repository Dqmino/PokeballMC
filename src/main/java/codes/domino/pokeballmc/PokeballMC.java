package codes.domino.pokeballmc;

import codes.domino.pokeballmc.command.GiveBallCommand;
import codes.domino.pokeballmc.event.EntityCaptureListener;
import codes.domino.pokeballmc.event.EntityUnCaptureListener;
import me.hex.entityserializer.api.EntityHandler;
import me.hex.entityserializer.api.EntitySerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PokeballMC extends JavaPlugin {

    private static EntityHandler handler;
    private static PokeballMC INSTANCE;

    public static EntityHandler getHandler() {
        return handler;
    }

    public static PokeballMC getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        EntitySerializer entitySerializer = EntitySerializer.create(this);
        handler = (EntityHandler) entitySerializer.getEntityHandler();

        registerCommands();
        registerEvents();

    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("giveball")).setExecutor(new GiveBallCommand());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new EntityCaptureListener(), this);
        getServer().getPluginManager().registerEvents(new EntityUnCaptureListener(), this);
    }

}
