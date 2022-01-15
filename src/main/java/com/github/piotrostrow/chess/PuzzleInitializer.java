package com.github.piotrostrow.chess;

import com.github.piotrostrow.chess.rest.dto.PuzzleDto;
import com.github.piotrostrow.chess.rest.serivce.PuzzleService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile("!test")
@Component
public class PuzzleInitializer implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(PuzzleInitializer.class);

	private final PuzzleService puzzleService;

	public PuzzleInitializer(PuzzleService puzzleService) {
		this.puzzleService = puzzleService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (puzzleService.getPuzzleCount() == 0) {
			LOGGER.info("No puzzles found in database - populating with data...");

			List<PuzzleDto> puzzles = getData().stream()
					.map(e -> new PuzzleDto(e[0], Arrays.asList(e[1].split(" ")), Integer.parseInt(e[2]), Arrays.asList(e[3].split(" "))))
					.collect(Collectors.toList());

			puzzleService.createPuzzles(puzzles);

			LOGGER.info("Populated database with {} puzzles", puzzleService.getPuzzleCount());
		}
	}

	private List<String[]> getData() throws IOException, CsvException {
		InputStream resource = PuzzleInitializer.class.getClassLoader().getResourceAsStream("puzzles.csv");
		if (resource == null) {
			throw new IllegalStateException("Could not load puzzles.csv");
		}

		try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource))) {
			return csvReader.readAll();
		}
	}
}
