package net.gunn.elimination.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Component
class EliminationCodeGenerator {
    private final Random random = new Random();

    private final List<String> words;
    private final int wordsPerCode;

    public EliminationCodeGenerator(@Value("${elimination.words}") Resource wordFile, @Value("${elimination.words-per-code}") int wordsPerCode) throws IOException {
        try (var scanner = new Scanner(wordFile.getInputStream())) {
            this.words = new ArrayList<>();
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine());
            }
        }
        this.wordsPerCode = wordsPerCode;
    }

    public String randomCode() {
        var words = new ArrayList<String>(wordsPerCode);
        for (int i = 0; i < wordsPerCode; i++) {
            words.add(this.words.get(random.nextInt(this.words.size())));
        }

        return String.join("-", words);
    }
}


