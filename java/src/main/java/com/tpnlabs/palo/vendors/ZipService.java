package com.tpnlabs.palo.vendors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {
    public static void create(String folder, String archiveName) throws Exception {
        Path folderPath = Paths.get(folder);
        Path zipPath = Paths.get(archiveName);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(folderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }
}
