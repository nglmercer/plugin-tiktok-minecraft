package com.example.tiktokliveplugin.service;

import io.github.jwdeveloper.tiktok.TikTokLive;
import io.github.jwdeveloper.tiktok.data.models.users.User;
import io.github.jwdeveloper.tiktok.exceptions.TikTokLiveRequestException;
import io.github.jwdeveloper.tiktok.live.LiveClient; // Correct import for LiveClient
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TikTokService {

    private LiveClient currentClient;
    private String currentTikTokUsername;
    private String currentTikTokDisplayName;
    private final Logger logger;
    private CompletableFuture<LiveClient> connectionFuture;

    public TikTokService(Logger logger) {
        this.logger = logger;
    }

    public void connect(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.warning("El nombre de usuario de TikTok no puede estar vacío.");
            broadcastToPlayers(ChatColor.RED + "[TikTokLive] Error: El nombre de usuario de TikTok no puede estar vacío.");
            return;
        }

        if (connectionFuture != null && !connectionFuture.isDone()) {
            logger.info("Ya hay una conexión en progreso para: " + username);
            broadcastToPlayers(ChatColor.YELLOW + "[TikTokLive] Conexión ya en progreso para: " + username);
            return;
        }
        
        if (isConnected()) {
            if (this.currentTikTokUsername != null && this.currentTikTokUsername.equalsIgnoreCase(username)) {
                logger.info("Ya estás conectado a: " + username);
                broadcastToPlayers(ChatColor.YELLOW + "[TikTokLive] Ya estás conectado a: " + username);
                return;
            }
            logger.info("Había una conexión activa con " + (this.currentTikTokDisplayName != null ? this.currentTikTokDisplayName : this.currentTikTokUsername) + ". Desconectando primero...");
            disconnect();
        }

        this.currentTikTokUsername = username;
        logger.info("Intentando conectar a TikTok Live para el usuario: " + username + "...");
        broadcastToPlayers(ChatColor.YELLOW + "[TikTokLive] Intentando conectar a TikTok Live de: " + username + "...");

        try {
            // TikTokLive.newClient(username) DEVUELVE el builder.
            // Simplemente encadenamos los métodos de configuración del builder.
            this.connectionFuture = TikTokLive.newClient(username) // Esto es el builder
                .onComment((liveClient, event) -> {
                    // ... (código de onComment sin cambios)
                    User commenter = event.getUser();
                    Long commenterUniqueId = commenter.getId();
                    String commenterProfileName = commenter.getProfileName();
                    String commenterNickName = commenter.getName();

                    String commenterDisplayName = commenterProfileName;
                    if (commenterDisplayName == null || commenterDisplayName.trim().isEmpty()) {
                        commenterDisplayName = commenterNickName;
                    }
                    if (commenterDisplayName == null || commenterDisplayName.trim().isEmpty()) {
                        commenterDisplayName = "UsuarioTikTok (" + commenterUniqueId + ")";
                    }
                    String commentText = event.getText();
                    String currentStreamerDisplay = (this.currentTikTokDisplayName != null && !this.currentTikTokDisplayName.isEmpty())
                                                    ? this.currentTikTokDisplayName
                                                    : (liveClient.getRoomInfo().getHost() != null ? liveClient.getRoomInfo().getHost().getProfileName() : this.currentTikTokUsername);
                    String messageToLog = "[TikTok - " + currentStreamerDisplay + "] " + commenterDisplayName + ": " + commentText;
                    logger.info(messageToLog);

                    TextComponent prefixComponent = new TextComponent("[TikTok] ");
                    prefixComponent.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
                    prefixComponent.setBold(true);
                    TextComponent userComponent = new TextComponent(commenterDisplayName);
                    userComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                    String hoverText = "@" + commenterNickName;
                    if (commenterProfileName != null && !commenterProfileName.equals(commenterNickName)) {
                        hoverText = commenterProfileName + "\n" + ChatColor.GRAY + "@" + commenterNickName;
                    }
                    userComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(hoverText).color(net.md_5.bungee.api.ChatColor.AQUA).create()));
                    TextComponent separatorComponent = new TextComponent(": ");
                    separatorComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                    TextComponent messageContentComponent = new TextComponent(commentText);
                    messageContentComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                    prefixComponent.addExtra(userComponent);
                    prefixComponent.addExtra(separatorComponent);
                    prefixComponent.addExtra(messageContentComponent);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(prefixComponent);
                    }
                })
                .onConnected((liveClient, event) -> {
                    if (liveClient.getRoomInfo() != null && liveClient.getRoomInfo().getHost() != null) {
                        this.currentTikTokDisplayName = liveClient.getRoomInfo().getHost().getProfileName();
                    }
                    if (this.currentTikTokDisplayName == null || this.currentTikTokDisplayName.isEmpty()) {
                        this.currentTikTokDisplayName = this.currentTikTokUsername;
                    }
                    logger.info(ChatColor.GREEN + "Conectado exitosamente a TikTok Live: " + this.currentTikTokDisplayName + " (@" + this.currentTikTokUsername + ")");
                    broadcastToPlayers(ChatColor.GREEN + "[TikTokLive] Conectado al live de " + this.currentTikTokDisplayName);
                })
                .onError((liveClient, event) -> {
                    String targetUser = (this.currentTikTokDisplayName != null ? this.currentTikTokDisplayName : this.currentTikTokUsername);
                    logger.log(Level.SEVERE, "Error DURANTE la conexión con TikTok Live (" + targetUser + "): " + event.getException().getMessage(), event.getException());
                    broadcastToPlayers(ChatColor.RED + "[TikTokLive] Error en la conexión con " + targetUser + ": " + event.getException().getLocalizedMessage());
                    cleanupConnectionState();
                })
                .onDisconnected((liveClient, event) -> {
                    String disconnectedUser = (this.currentTikTokDisplayName != null ? this.currentTikTokDisplayName : this.currentTikTokUsername);
                    logger.info("Desconectado de TikTok Live: " + disconnectedUser);
                    broadcastToPlayers(ChatColor.YELLOW + "[TikTokLive] Desconectado del live de " + disconnectedUser);
                    cleanupConnectionState();
                })
                .buildAndConnectAsync(); // Finalmente, construimos y conectamos asíncronamente

            // Manejo del CompletableFuture
            this.connectionFuture.thenAccept(connectedClient -> {
                this.currentClient = connectedClient;
                logger.info("CompletableFuture: Conexión asíncrona completada para " + (this.currentTikTokDisplayName != null ? this.currentTikTokDisplayName : this.currentTikTokUsername) );
            }).exceptionally(ex -> {
                String targetUser = this.currentTikTokUsername;
                logger.log(Level.SEVERE, "CompletableFuture: Falló la conexión asíncrona con TikTok Live (" + targetUser + "): " + ex.getMessage(), ex);
                
                String errorMessage = ex.getLocalizedMessage();
                Throwable cause = ex.getCause(); // TikTokLiveJava a menudo envuelve la excepción real
                if (cause instanceof TikTokLiveRequestException) {
                    TikTokLiveRequestException reqEx = (TikTokLiveRequestException) cause;
                     if (reqEx.getMessage().toLowerCase().contains("user not found")) {
                         errorMessage = "Usuario " + targetUser + " no encontrado o no está en live.";
                    } else {
                        errorMessage = reqEx.getLocalizedMessage();
                    }
                } else if (ex instanceof TikTokLiveRequestException) { // Si la excepción principal es la que buscamos
                     TikTokLiveRequestException reqEx = (TikTokLiveRequestException) ex;
                     if (reqEx.getMessage().toLowerCase().contains("user not found")) {
                         errorMessage = "Usuario " + targetUser + " no encontrado o no está en live.";
                    }
                }

                broadcastToPlayers(ChatColor.RED + "[TikTokLive] Error conectando a " + targetUser + ": " + errorMessage);
                cleanupConnectionState();
                return null;
            });

        } catch (IllegalStateException e) {
            logger.log(Level.SEVERE, "Error al inicializar el cliente de TikTok para " + username + ": " + e.getMessage(), e);
            broadcastToPlayers(ChatColor.RED + "[TikTokLive] Error crítico inicializando conexión para " + username + ".");
            cleanupConnectionState();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al configurar la conexión con " + username + ": " + e.getMessage(), e);
            broadcastToPlayers(ChatColor.RED + "[TikTokLive] Error configurando conexión para " + username + ".");
            cleanupConnectionState();
        }
    }

    // ... resto de los métodos (disconnect, cleanupConnectionState, isConnected, etc.) sin cambios ...
    public void disconnect() {
        if (connectionFuture != null && !connectionFuture.isDone()) {
            logger.info("Cancelando intento de conexión en progreso...");
            connectionFuture.cancel(true);
            connectionFuture = null;
        }

        if (currentClient != null) {
            String userToDisconnect = (this.currentTikTokDisplayName != null ? this.currentTikTokDisplayName : this.currentTikTokUsername);
            logger.info("Iniciando desconexión de TikTok Live: " + userToDisconnect + "...");
            try {
                currentClient.disconnect();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error durante el intento de desconexión de TikTok Live: " + e.getMessage(), e);
                cleanupConnectionState();
            }
        } else {
            cleanupConnectionState();
        }
    }

    private void cleanupConnectionState() {
        this.currentClient = null;
        this.currentTikTokUsername = null;
        this.currentTikTokDisplayName = null;
        if (connectionFuture != null && !connectionFuture.isDone() && !connectionFuture.isCancelled()){
            connectionFuture.cancel(true);
        }
        this.connectionFuture = null;
    }

    public boolean isConnected() {
        return this.currentClient != null && this.currentClient.getRoomInfo() != null && this.currentClient.getRoomInfo().getHost() != null;
    }

    public String getCurrentUsername() {
        if (this.currentTikTokDisplayName != null && !this.currentTikTokDisplayName.isEmpty()) {
            return this.currentTikTokDisplayName;
        }
        return this.currentTikTokUsername;
    }

    public String getCurrentTechnicalUsername() {
        return this.currentTikTokUsername;
    }

    private void broadcastToPlayers(String message) {
        // Asegurarse de que se ejecuta en el hilo principal si se llama desde un hilo asíncrono
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("TikTokLivePlugin"), () -> { // Reemplaza "TikTokLivePlugin" con el nombre de tu plugin
                Bukkit.getServer().broadcastMessage(message);
            });
        } else {
            Bukkit.getServer().broadcastMessage(message);
        }
    }

    // Sobrecarga para componentes de chat, también asegurando hilo principal
    private void broadcastToPlayers(TextComponent component) {
         if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("TikTokLivePlugin"), () -> { // Reemplaza "TikTokLivePlugin" con el nombre de tu plugin
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(component);
                }
            });
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(component);
            }
        }
    }
     // Actualizar el onComment para usar el helper de broadcastToPlayers para componentes
     // Esto ya está hecho implícitamente en el código de onComment, pero si tuvieras otro lugar:
     // ...
     // TextComponent finalMessage = ...;
     // broadcastToPlayers(finalMessage);
     // ...
}