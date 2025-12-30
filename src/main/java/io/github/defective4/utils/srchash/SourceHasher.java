package io.github.defective4.utils.srchash;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SourceHasher {
    public static byte[] generateSHA256(Collection<File> files, String algo)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance(algo);
        for (File file : files) sha256.update(Files.readAllBytes(file.toPath()));
        return sha256.digest();
    }

    public static void main(String[] args) {
        List<File> files = recursiveFindSourceFiles(new File("src"));
        try {
            byte[] hash = generateSHA256(files, args.length > 1 ? args[1] : "SHA256");
            Path target = new File(new File("target/classes"), args.length > 0 ? args[0] : "objects.hash").toPath();
            Files.write(target, hash, StandardOpenOption.CREATE);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static List<File> recursiveFindSourceFiles(File dir) {
        return Collections.unmodifiableList(recursiveFindSourceFiles0(dir));
    }

    private static List<File> recursiveFindSourceFiles0(File dir) {
        List<File> files = new ArrayList<>();
        for (File file : dir.listFiles()) if (file.isDirectory())
            files.addAll(recursiveFindSourceFiles0(file));
        else if (file.getName().toLowerCase().endsWith(".java")) files.add(file);
        return files;
    }
}
