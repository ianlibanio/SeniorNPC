package me.ianlibanio.npc.helpers;

import lombok.Getter;
import me.ianlibanio.npc.SeniorNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public abstract class HelperPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.enable();
        super.onEnable();

        this.initialize();
    }

    @Override
    public void onDisable() {
        this.disable();
        super.onDisable();
    }

    @Override
    public void onLoad() {
        this.load();
        super.onLoad();
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void load();

    private void initialize() {
        if (getConfig().get("location") == null) {
            SeniorNPC.setNpcLocation(null);
            return;
        }

        double x = getConfig().getDouble("location.x");
        double y = getConfig().getDouble("location.y");
        double z = getConfig().getDouble("location.z");
        float yaw =getConfig().getFloat("location.yaw");
        float pitch = getConfig().getFloat("location.pitch");
        World world = Bukkit.getWorld(getConfig().getString("location.world"));

        Location location = new Location(world, x, y, z, yaw, pitch);
        SeniorNPC.setNpcLocation(location);
    }

}
