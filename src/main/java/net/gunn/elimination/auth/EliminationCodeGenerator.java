package net.gunn.elimination.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
 class EliminationCodeGenerator {
   private final Random random = new Random();

   private final List<String> words;
   private final int wordsPerCode;

   public EliminationCodeGenerator(@Value("${elimination.words}") Path wordFile, @Value("${elimination.words-per-code}") int wordsPerCode) throws IOException {
	  this.words = Files.lines(wordFile).toList();
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


