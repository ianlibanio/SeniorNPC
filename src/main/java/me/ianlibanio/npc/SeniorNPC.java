package me.ianlibanio.npc;

import lombok.Getter;
import lombok.Setter;
import me.ianlibanio.npc.commands.NpcCommand;
import me.ianlibanio.npc.helpers.HelperPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public final class SeniorNPC extends HelperPlugin {

    @Getter private static SeniorNPC instance;
    @Getter private static FileConfiguration configuration;
    @Getter @Setter private static Location npcLocation;

    public SeniorNPC() {
        instance = this;
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        configuration = getConfig();

        getCommand("npc").setExecutor(new NpcCommand());
    }

    @Override
    public void disable() {

    }

    @Override
    public void load() {

    }
}