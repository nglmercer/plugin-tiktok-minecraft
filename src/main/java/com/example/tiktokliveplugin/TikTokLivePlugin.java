package com.example.tiktokliveplugin;

import com.example.tiktokliveplugin.commands.TikTokCommandExecutor;
import com.example.tiktokliveplugin.service.TikTokService;
import org.bukkit.plugin.java.JavaPlugin;

public class TikTokLivePlugin extends JavaPlugin {

    private TikTokService tikTokService;

    @Override
    public void onEnable() {
        getLogger().info("TikTokLivePlugin habilitándose...");

        // 1. Opcional: Cargar configuración (si la necesitas más adelante)
        // saveDefaultConfig();
        // String defaultUser = getConfig().getString("tiktok.default-username", "");

        // 2. Crear e inicializar el servicio de TikTok
        // Ahora el servicio no toma un username en el constructor, se le pasa al conectar
        tikTokService = new TikTokService(getLogger());

        // 3. Registrar comandos
        TikTokCommandExecutor commandExecutor = new TikTokCommandExecutor(this, tikTokService);
        this.getCommand("tiktok").setExecutor(commandExecutor);
        // No es necesario setear TabCompleter si no tienes uno complejo,
        // pero podrías añadirlo para sugerir subcomandos o nombres de usuario.

        getLogger().info("TikTokLivePlugin habilitado!");

        // 4. Opcional: Conectar automáticamente a un usuario por defecto si está configurado
        // if (getConfig().getBoolean("tiktok.auto-connect-on-startup", false) && defaultUser != null && !defaultUser.isEmpty()) {
        //    getLogger().info("Intentando conexión automática con el usuario por defecto: " + defaultUser);
        //    tikTokService.connect(defaultUser);
        // }
    }

    @Override
    public void onDisable() {
        getLogger().info("TikTokLivePlugin deshabilitándose...");
        if (tikTokService != null) {
            tikTokService.disconnect(); // Asegura desconectar cualquier sesión activa
        }
        getLogger().info("TikTokLivePlugin deshabilitado!");
    }

    // Getter opcional si necesitas acceder al servicio desde otras clases (ej. listeners)
    public TikTokService getTikTokService() {
        return tikTokService;
    }
}