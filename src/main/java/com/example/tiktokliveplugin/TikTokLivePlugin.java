package com.example.tiktokliveplugin;

import com.example.tiktokliveplugin.service.TikTokService; // Importa el nuevo servicio
import org.bukkit.plugin.java.JavaPlugin;

public class TikTokLivePlugin extends JavaPlugin {

    private TikTokService tikTokService;
    // Es buena práctica mover esto a un archivo de configuración (config.yml)
    private final String TIKTOK_USERNAME = "bangbetmenygy";

    @Override
    public void onEnable() {
        getLogger().info("TikTokLivePlugin habilitado!");

        // Opcional: Cargar configuración desde config.yml
        // saveDefaultConfig(); // Crea config.yml si no existe
        // String usernameFromConfig = getConfig().getString("tiktok-username", TIKTOK_USERNAME);
        // if (usernameFromConfig.equals("tu_usuario_de_tiktok_aqui")) {
        //    getLogger().warning("Por favor, configura tu nombre de usuario de TikTok en config.yml!");
        // }

        // Crear e inicializar el servicio de TikTok
        tikTokService = new TikTokService(TIKTOK_USERNAME, getLogger());
        tikTokService.connect();
    }

    @Override
    public void onDisable() {
        getLogger().info("TikTokLivePlugin deshabilitado!");
        if (tikTokService != null) {
            tikTokService.disconnect();
        }
    }
}