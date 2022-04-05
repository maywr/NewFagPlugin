package xyz.maywr.newfagplugin;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewfagPatches implements Listener {

    private boolean thing = false;
    private JavaPlugin plugin;

    NewfagPatches(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdminJoin(PlayerJoinEvent evt) {
        if(evt.getPlayer().isOp()){
            evt.getPlayer().sendMessage(ChatColor.GRAY + "[NewFag]" + ChatColor.GREEN + " Плагин стабильно работает. Версия: " + NewFagPlugin.version +  ChatColor.GRAY + " // by maywr" );
        }
    }

    @EventHandler
    public void onElytraFlight(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.isGliding()) {
            if (Math.abs(event.getFrom().getX() - event.getTo().getX()) > 1.8
                    || Math.abs(event.getFrom().getY() - event.getTo().getY()) > 1.8
                    || Math.abs(event.getFrom().getZ() - event.getTo().getZ()) > 1.8) {

                event.getPlayer().setGliding(false);
                event.getPlayer().teleport(event.getFrom());

            }
        }
        if(player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER){
            if(player.getLocation().getBlockY() >= 128){
                Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getBlockY() - 8, player.getLocation().getZ());
                player.teleport(location);
            }
        }
    }
    @EventHandler
    public void onTryToPlaceMap(PlayerInteractEntityEvent evt){
        if(evt.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            if (evt.getPlayer().getItemInHand().getType() == Material.MAP) {
                List<Entity> entityList = Arrays.asList(evt.getRightClicked().getLocation().getChunk().getEntities());
                int numberOfFrames = 0;
                for (Entity entity : entityList) {
                    if (entity instanceof ItemFrame) {
                        ItemFrame itemFrame = (ItemFrame) entity;
                        ItemStack itemStack = new ItemStack(itemFrame.getItem());
                        if (itemStack.getType() == Material.MAP) {
                            numberOfFrames = numberOfFrames + 1;
                        }
                    }
                }
                if (numberOfFrames >= 5) {
                    evt.getRightClicked().remove();
                }
            }
        }
    }

    @EventHandler
    public void onDonkeyRide(PlayerMoveEvent evt){
        if(evt.getPlayer().getLocation().getChunk().getEntities() != null){
            for(Entity e : evt.getPlayer().getLocation().getChunk().getEntities()){
                if(e instanceof Donkey){
                    if(((Donkey) e).isCarryingChest()){
                        e.remove();
                    }
                }
            }
        }
        if(evt.getPlayer().isInsideVehicle()){
            if(evt.getPlayer().getVehicle() instanceof Donkey){
                Donkey donkey = (Donkey) evt.getPlayer().getVehicle();
                if(donkey.isCarryingChest()){
                    donkey.remove();
                }
            }
        }
    }

    @EventHandler
    public void onButton(PlayerInteractEvent evt){
        if(evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if((evt.getClickedBlock().getType().toString().contains("BUTTON"))) {
                Location buttoncoord = evt.getClickedBlock().getLocation();
                Chunk evtChunk = evt.getClickedBlock().getChunk();
                for (Entity entity : evtChunk.getEntities()) {
                    if (entity.getName().equals("Armor Stand")) {
                        if (entity.getLocation().distance(buttoncoord) == 1.224744871391589 || entity.getLocation().distance(buttoncoord) == 1.8708286933869707 ||entity.getLocation().distance(buttoncoord) <= 3 ) {
                            entity.remove();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent evt){

        ArrayList<Entity> entities = new ArrayList<>();
        entities.addAll(Arrays.asList(evt.getChunk().getEntities()));

        for (int i = 0; i < entities.size(); i++) {
            if(i >= 20)
                entities.get(i).remove();
        }
    }
}
