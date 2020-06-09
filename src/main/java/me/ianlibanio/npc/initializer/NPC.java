package me.ianlibanio.npc.initializer;

import com.mojang.authlib.GameProfile;
import lombok.Data;
import lombok.SneakyThrows;
import me.ianlibanio.npc.SeniorNPC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.UUID;

@Data
public class NPC {

    private String name;
    private UUID uniqueId;
    private Location location;
    private EntityPlayer entityPlayer;

    public NPC(String name) {
        this.name = name;
        this.uniqueId = UUID.randomUUID();
    }

    private byte getNmsValue(double value) {
        return (byte) (value * 32);
    }

    @SneakyThrows
    private void changePrivateField(Class type, Object object, String fieldName, Object value) {
        Field field = type.getDeclaredField(fieldName);

        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    private void move(double var1, double var2, double var3, int id) {
        byte x = getNmsValue(var1);
        byte y = getNmsValue(var2);
        byte z = getNmsValue(var3);

        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook();
        changePrivateField(PacketPlayOutEntity.class, packet1, "a", id);
        changePrivateField(PacketPlayOutEntity.class, packet1, "b", x);
        changePrivateField(PacketPlayOutEntity.class, packet1, "c", y);
        changePrivateField(PacketPlayOutEntity.class, packet1, "d", z);

        PacketPlayOutEntityHeadRotation packet2 = new PacketPlayOutEntityHeadRotation();
        changePrivateField(PacketPlayOutEntityHeadRotation.class, packet2, "a", id);

        Bukkit.getOnlinePlayers().forEach(player -> {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);
        });
    }

    public void spawn(Location location) {
        this.location = location;

        final MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();

        final WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), this.name);

        this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));

        final Player playerNpc = entityPlayer.getBukkitEntity().getPlayer();
        playerNpc.setPlayerListName("");

        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        Bukkit.getOnlinePlayers().forEach(player -> {
            final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

            playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        });
    }

    public void remove() {
        PacketPlayOutEntityDestroy packet1 = new PacketPlayOutEntityDestroy(entityPlayer.getId());

        Bukkit.getOnlinePlayers().forEach(player -> {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
        });
    }

    private void jump() {
        move(0, 1, 0, entityPlayer.getId());

        Bukkit.getScheduler().runTaskLater(SeniorNPC.getInstance(), () -> {
            move(0, -1, 0, entityPlayer.getId());
        }, 10L);
    }

    private void crounch(byte animation) {
        DataWatcher dataWatcher = new DataWatcher(entityPlayer);
        dataWatcher.a(0, animation);

        PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true);

        Bukkit.getOnlinePlayers().forEach(player -> {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
        });
    }

    private void hit() {
        Bukkit.getOnlinePlayers().forEach(player -> {
           ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutAnimation(entityPlayer, 0));
        });
    }

    public void execute() {
        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                switch (step) {
                    case 0:
                    case 1:
                        jump();
                        break;
                    case 2:
                    case 4:
                        crounch((byte) 2);
                        break;
                    case 3:
                    case 6:
                        crounch((byte) 0);
                        break;
                    case 5:
                        hit();
                        break;
                    case 7:
                        remove();
                        break;
                    default:
                        cancel();
                }

                step++;
            }
        }.runTaskTimer(SeniorNPC.getInstance(), 20L, 20L);
    }
}