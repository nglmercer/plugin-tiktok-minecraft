package com.example.tiktokliveplugin.commands;

import com.example.tiktokliveplugin.TikTokLivePlugin;
import com.example.tiktokliveplugin.service.TikTokService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TikTokCommandExecutor implements CommandExecutor {
    private final TikTokService tikTokService;
    private final TikTokLivePlugin plugin; // Útil para acceder al logger del plugin o config

    public TikTokCommandExecutor(TikTokLivePlugin plugin, TikTokService tikTokService) {
        this.plugin = plugin;
        this.tikTokService = tikTokService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permitir a la consola usar 'connect' si especifica usuario, y 'status' o 'disconnect'.
        // Otros comandos podrían requerir ser jugador.
        boolean isPlayer = sender instanceof Player;

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "connect":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /tiktok connect <usuario_tiktok>");
                    return true;
                }
                String usernameToConnect = args[1];
                if (tikTokService.isConnected() && usernameToConnect.equalsIgnoreCase(tikTokService.getCurrentUsername())) {
                    sender.sendMessage(ChatColor.YELLOW + "Ya estás conectado a TikTok Live de: " + ChatColor.WHITE + usernameToConnect);
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Intentando conectar a TikTok Live de: " + ChatColor.WHITE + usernameToConnect + "...");
                    // El servicio ahora maneja la desconexión previa si es necesario
                    tikTokService.connect(usernameToConnect);
                    // El feedback de éxito/error vendrá de los logs del TikTokService y sus eventos
                }
                return true;

            case "disconnect":
                if (tikTokService.isConnected()) {
                    sender.sendMessage(ChatColor.YELLOW + "Desconectando de TikTok Live (" + ChatColor.WHITE + tikTokService.getCurrentUsername() + ChatColor.YELLOW + ")...");
                    tikTokService.disconnect();
                } else {
                    sender.sendMessage(ChatColor.RED + "No estás conectado a ningún TikTok Live.");
                }
                return true;

            case "status":
                if (tikTokService.isConnected()) {
                    sender.sendMessage(ChatColor.GREEN + "Conectado a TikTok Live de: " + ChatColor.WHITE + tikTokService.getCurrentUsername());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "No estás conectado a TikTok Live.");
                }
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Acción no válida.");
                sendUsage(sender);
                return true;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- Uso de TikTokLivePlugin ---");
        sender.sendMessage(ChatColor.AQUA + "/tiktok connect <usuario>" + ChatColor.GRAY + " - Conecta al live de un usuario.");
        sender.sendMessage(ChatColor.AQUA + "/tiktok disconnect" + ChatColor.GRAY + " - Desconecta del live actual.");
        sender.sendMessage(ChatColor.AQUA + "/tiktok status" + ChatColor.GRAY + " - Muestra el estado de la conexión.");
    }
}