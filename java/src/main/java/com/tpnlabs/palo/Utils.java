package com.tpnlabs.palo;

import com.tpnlabs.palo.vendors.DiscordService;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class Utils {
    private static final String DISCORD_HOOK = System.getenv("DISCORD_HOOK");

    static String formatFileSize(long sizeInBytes) {
        double sizeInSomeUnit = (double) sizeInBytes / 1024 / 1024;
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(sizeInSomeUnit);
    }

    public static void sendDiscordMessage(
            String archiveName,
            int totalS3Backups,
            long fileSize,
            int totalExecutionTime
    ) {
        DiscordService discordService = new DiscordService(DISCORD_HOOK);
        discordService.addEmbed(new DiscordService.EmbedObject()
                .setTitle("✅ Successfully backed up Murlock Empire!")
                .setDescription("Archive name: " + archiveName + " | Total backups: " + totalS3Backups)
                .setColor(Color.GREEN)
                .addField("Size", formatFileSize(fileSize) + " MB", true)
                .addField("Time", totalExecutionTime + "s", true));
        try {
            discordService.execute(); //Handle exception
        } catch (IOException e) {
            throw new PaloException("Error sending Discord message: " + e.getMessage());
        }
    }
}
