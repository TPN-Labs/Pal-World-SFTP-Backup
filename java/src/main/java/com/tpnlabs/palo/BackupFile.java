package com.tpnlabs.palo;

import java.nio.ByteBuffer;

public class BackupFile {
    private String name;
    private ByteBuffer content;

    public BackupFile(String name, ByteBuffer content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public ByteBuffer getContent() {
        return content;
    }
}
