package io.github.defective4.utils.srchash;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class SourceHasherTest {

    private final File dir;
    private final File sample;

    public SourceHasherTest() throws IOException {
        dir = Files.createTempDirectory("source-hasher-tests").toFile();
        File target = new File(dir, "src");
        sample = new File(target, "Sample.java");
        try (InputStream in = SourceHasherTest.class.getResourceAsStream("/Sample.java")) {
            target.mkdirs();
            Files.copy(in, sample.toPath());
        }
    }

    @Test
    public void generateSHA256() throws NoSuchAlgorithmException, IOException {
        byte[] hash = SourceHasher.generateSHA256(Collections.singleton(sample), "sha256");
        byte[] sample = new byte[32];
        try (DataInputStream in = new DataInputStream(SourceHasherTest.class.getResourceAsStream("/Sample.hash"))) {
            in.readFully(sample);
        }

        assertArrayEquals(hash, sample);
    }

    @Test
    public void recursiveFindSourceFilesTest() {
        List<File> files = SourceHasher.recursiveFindSourceFiles(dir);
        assertTrue(files.size() == 1 && files.get(0).getName().equals("Sample.java"));
    }
}
