package com.example.tiktokliveplugin.service;

import io.github.jwdeveloper.tiktok.TikTokLive;
import io.github.jwdeveloper.tiktok.live.LiveClient;
import java.util.logging.Logger; // Usar java.util.logging.Logger

public class TikTokService {

    private LiveClient tikTokLiveClient;
    private final String tiktokUsername;
    private final Logger logger; // Para registrar mensajes usando el logger del plugin

    public TikTokService(String tiktokUsername, Logger logger) {
        this.tiktokUsername = tiktokUsername;
        this.logger = logger;
    }

    public void connect() {
        try {
            logger.info("Conectando a TikTok Live para el usuario: " + tiktokUsername + "...");
            tikTokLiveClient = TikTokLive.newClient(tiktokUsername)
                .onComment((liveClient, event) -> {
                    String userName = event.getUser().getProfileName();
                    String comment = event.getText();
                    // Enviar el mensaje a la consola de Minecraft
                    logger.info("[TikTok] " + userName + ": " + comment);
                    logger.info(event.toJson());
                })
                .onConnected((liveClient, event) -> {
                    logger.info("Conectado exitosamente a TikTok Live: " + tiktokUsername);
                })
                .onError((liveClient, event) -> {
                    logger.severe("Error en la conexión con TikTok Live: " + event.getException().getMessage());
                    event.getException().printStackTrace(); // Es útil para el debug
                })
                .onDisconnected((liveClient, event) -> {
                    logger.info("Desconectado de TikTok Live: " + tiktokUsername);
                })
                .buildAndConnect(); // Usar conexión síncrona

        } catch (Exception e) {
            logger.severe("No se pudo inicializar el cliente de TikTok Live: " + e.getMessage());
            e.printStackTrace(); // Es útil para el debug
        }
    }

    public void disconnect() {
        if (tikTokLiveClient != null && tiktokUsername != null) {
            try {
                logger.info("Desconectando de TikTok Live...");
                tikTokLiveClient.disconnect();
            } catch (Exception e) {
                logger.severe("Error al desconectar de TikTok Live: " + e.getMessage());
            }
        } else if (tikTokLiveClient != null) {
            logger.info("El cliente de TikTok Live no estaba conectado o ya fue desconectado.");
        }
    }

    public boolean isConnected() {
        return tikTokLiveClient != null && tiktokUsername != null;
    }
}